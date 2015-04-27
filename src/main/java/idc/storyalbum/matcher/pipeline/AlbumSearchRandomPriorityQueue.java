package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.model.album.AlbumPage;
import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
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
@Service
@Slf4j
public class AlbumSearchRandomPriorityQueue {

    @Value("${story-album.search.priority.num-of-repetitions}")
    int M;

    @Value("${story-album.search.num-of-results}")
    int NUM_OF_BEST_RESULTS;

    @Autowired
    ScoreService scoreService;

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

    private double evaluateDependencies(List<StoryDependency> dependencies, AnnotatedImage i1, AnnotatedImage i2) {

        double sum = 0;
        for (StoryDependency dependency : dependencies) {
            int wight = dependency.getWight() != null ? dependency.getWight() : 1;
            sum += (DependencyUtils.isMatch(dependency, i1, i2) ? 1 : -1) * wight;
            sum *= 0.25;
        }
        return sum;
    }

    private double evaluateFitness(PipelineContext ctx, Set<AlbumPage> assignment) {
        //calculate image score for each image
        double imagesScore = assignment.stream()
                .mapToDouble((page) -> scoreService.getImageFitScore(page.getImage(), page.getStoryEvent()))
                .sum();
        imagesScore = imagesScore / (double) assignment.size();


        // map pairs of events to their dependencies list
        Map<ImmutablePair<Integer, Integer>, List<StoryDependency>> pairDependencies =
                ctx.getStoryGraph().getDependencies()
                        .stream()
                        .collect(groupingBy(
                                dependency ->
                                        new ImmutablePair<>(dependency.getFromEventId(), dependency.getToEventId())));

        // map each event to its annotated image
        Map<Integer, AnnotatedImage> eventToImage = assignment.stream()
                .collect(toMap((page) -> page.getStoryEvent().getId(), AlbumPage::getImage));

        // calculate each dependencies' score
        double sum = 0;
        int count = 0;
        for (ImmutablePair<Integer, Integer> pair : pairDependencies.keySet()) {
            AnnotatedImage i1 = eventToImage.get(pair.getLeft());
            AnnotatedImage i2 = eventToImage.get(pair.getRight());
            List<StoryDependency> dependencies = pairDependencies.get(pair);
            count += dependencies.size();
            sum += evaluateDependencies(dependencies, i1, i2);
        }
        double dependenciesScore = 0;
        if (count > 0) {
            dependenciesScore = sum / (double) count;
        }
        return imagesScore + dependenciesScore;
    }


    public SortedSet<Album> findAlbums(PipelineContext ctx) {
        log.info("Searching for {} best albums, using {} iterations", NUM_OF_BEST_RESULTS, M);
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

    List<AlbumPage> sortPages(Set<AlbumPage> pages) {
        List<AlbumPage> result = new ArrayList<>(pages);
        result.sort((o1, o2) -> o1.getStoryEvent().getId() - o2.getStoryEvent().getId());
        return result;
    }
}
