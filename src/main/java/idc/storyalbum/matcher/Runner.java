package idc.storyalbum.matcher;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.pipeline.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.SortedSet;

/**
 * Created by yonatan on 18/4/2015.
 */
@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Autowired
    private DataIOService dataIOService;

    @Autowired
    private MandatoryImageMatcher mandatoryImageMatcher;

    @Autowired
    private AlbumSearchRandomPriorityQueue albumSearcher;

    @Override
    public void run(String... args) throws Exception {
        try {
            File annotatedSetFile = new File("/Users/yonatan/StoryAlbumData/OldSet1/annotatedSet.json"); //new File("/tmp/annotatedSet.json");
            File storyGraphFile = new File("/tmp/story.json");
            PipelineContext ctx = dataIOService.readData(storyGraphFile, annotatedSetFile);
            mandatoryImageMatcher.match(ctx);
            SortedSet<Album> bestAlbums = albumSearcher.findAlbums(ctx);
            File albumFile = new File("/tmp/album.json");
            dataIOService.writeAlbum(bestAlbums.first(), albumFile);
        } catch (NoMatchException e) {
            log.error("Error! Cannot satisfy story constraints: {}", e.getMessage());
        }
    }
}
