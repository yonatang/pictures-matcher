package idc.storyalbum.matcher.pipeline.albumsearch;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.model.album.AlbumPage;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yonatan on 27/4/2015.
 */
@Service("exhaustive")
@Slf4j
public class AlbumSearchExhaustive extends AlbumSearch {

    private Set<AnnotatedImage> usedImages;
    private SortedSet<Album> matches;
    private LinkedList<AlbumPage> albumPages;
    private PipelineContext ctx;


    private void search(List<StoryEvent> events, int i) {
        StoryEvent thisEvent = events.get(i);
        Set<AnnotatedImage> possibleMatches = ctx.getPossibleMatches(thisEvent);
        if (possibleMatches.isEmpty()) {
            return;
        }
        i++;
        for (AnnotatedImage possibleMatch : possibleMatches) {
            if (usedImages.contains(possibleMatch)) {
//                log.debug("Dead end!");
                continue;
            }
            usedImages.add(possibleMatch);
            AlbumPage albumPage = new AlbumPage(possibleMatch, thisEvent);
            albumPages.add(albumPage);
            if (i == events.size()) {
                //an album was found
                Album album = new Album();
                album.setPages(sortPages(albumPages));
                album.setBaseDir(ctx.getAnnotatedSet().getBaseDir());
                album.setScore(evaluateFitness(ctx, albumPages));
//                log.debug("A valid album was found with score {}", album.getScore());

                matches.add(album);
            } else {
                search(events, i);
            }
            albumPages.removeLast();
            usedImages.remove(possibleMatch);
        }

    }

    @Override
    public SortedSet<Album> findAlbums(PipelineContext ctx) {
        log.info("Searching for albums, Exhaustive Search strategy,  ");
        usedImages = new HashSet<>();
        albumPages = new LinkedList<>();
        matches =
                //sort largest first
                new TreeSet<>((o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));
        this.ctx = ctx;
        List<StoryEvent> events = new ArrayList<>(ctx.getEventIdMap().values());
        StopWatch s3 = new StopWatch();
        s3.start();
        search(events, 0);
        s3.stop();

        log.info("Found {} albums", matches.size());
        log.info("Took {}", s3.toString());

        return matches;
    }
}
