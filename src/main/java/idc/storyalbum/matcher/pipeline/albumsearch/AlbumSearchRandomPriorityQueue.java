package idc.storyalbum.matcher.pipeline.albumsearch;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.model.album.AlbumPage;
import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.pipeline.DependencyUtils;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import idc.storyalbum.matcher.pipeline.ScoreService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Created by yonatan on 19/4/2015.
 * Heuristically searches for the best matching albums it found according to
 * a partially random search
 */
@Service("priorityQueue")
@Slf4j
public class AlbumSearchRandomPriorityQueue extends AlbumSearch {

    @Value("${story-album.search.priority.num-of-repetitions}")
    int M;

    @Value("${story-album.search.num-of-results}")
    int NUM_OF_BEST_RESULTS;



    private class ImageMatchPriorityQueue extends PriorityQueue<AnnotatedImage> {
        public ImageMatchPriorityQueue(ScoreService scoreService, StoryEvent event) {
            super((o1, o2) -> {
                double o1Score = scoreService.getImageFitScore(o1, event);
                double o2Score = scoreService.getImageFitScore(o2, event);
                //sort largest first
                return Double.compare(o2Score, o1Score);
            });
        }
    }

    class EventPriorityQueue extends PriorityQueue<StoryEvent> {
        public EventPriorityQueue(PipelineContext ctx, ScoreService scoreService, double nonFuzziness) {
            super((o1, o2) -> {
                double o1Scope = scoreService.getEventScore(ctx, o1, nonFuzziness);
                double o2Scope = scoreService.getEventScore(ctx, o2, nonFuzziness);
                //sort largest first
                return Double.compare(o2Scope, o1Scope);
            });
        }
    }

    Set<AlbumPage> findAssignment(PipelineContext ctx, int t) {
        StoryGraph storyGraph = ctx.getStoryGraph();
        double nonFuzziness = (double) t / (double) M;
        EventPriorityQueue eventQueue = new EventPriorityQueue(ctx, scoreService, nonFuzziness);
        Map<StoryEvent, ImageMatchPriorityQueue> queues = new HashMap<>();
        for (StoryEvent storyEvent : storyGraph.getEvents()) {
            ImageMatchPriorityQueue queue = new ImageMatchPriorityQueue(scoreService, storyEvent);
            queues.put(storyEvent, queue);
            Set<AnnotatedImage> possibleMatches = ctx.getPossibleMatches(storyEvent);
            log.trace("Adding for {}:{} possible images {}", storyEvent.getId(), storyEvent.getName(), possibleMatches);
            queue.addAll(possibleMatches);
            eventQueue.add(storyEvent);
        }

        Set<AlbumPage> assignment = new HashSet<>();
        while (!eventQueue.isEmpty()) {
            StoryEvent event = eventQueue.poll();
            ImageMatchPriorityQueue images = queues.get(event);
            if (images.isEmpty()) {
                //break and continue to next solution
                return null;
            }
            AnnotatedImage bestImage = images.poll();

            for (ImageMatchPriorityQueue queue : queues.values()) {
                queue.remove(bestImage);
            }
            assignment.add(new AlbumPage(bestImage, event));
        }
        return assignment;
    }





    public SortedSet<Album> findAlbums(PipelineContext ctx) {
        log.info("Searching for {} best albums, Priority Queue strategy, using {} iterations", NUM_OF_BEST_RESULTS, M);
        SortedSet<Album> bestAlbums =
                //sort largest first
                new TreeSet<>((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

        StopWatch s3 = new StopWatch();
        s3.start();
        for (int i = 0; i < M; i++) {
            Set<AlbumPage> assignment = findAssignment(ctx, i);
            if (assignment == null) {
                continue;
            }
            double score = evaluateFitness(ctx, assignment);
            Album album = new Album();
            album.setPages(sortPages(assignment));
            album.setScore(score);
            album.setBaseDir(ctx.getAnnotatedSet().getBaseDir());
            bestAlbums.add(album);
            while (bestAlbums.size() > NUM_OF_BEST_RESULTS) {
                bestAlbums.remove(bestAlbums.last());
            }
        }
        s3.stop();
        log.info("Found {} albums with scores {}", bestAlbums.size(), bestAlbums.stream().map(Album::getScore).collect(toList()));
        log.info("Took {}", s3.toString());
        log.debug("In average, {}ms per iteration", s3.getNanoTime() / M / 1000);
        return bestAlbums;
    }


}
