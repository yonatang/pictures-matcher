package idc.storyalbum.matcher;

import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.pipeline.AlbumSearchRandomPriorityQueue;
import idc.storyalbum.matcher.pipeline.DataIOService;
import idc.storyalbum.matcher.pipeline.MandatoryImageMatcher;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.SortedSet;

/**
 * Created by yonatan on 18/4/2015.
 */
@Component
public class Runner implements CommandLineRunner {

    @Autowired
    private DataIOService dataIOService;

    @Autowired
    private MandatoryImageMatcher mandatoryImageMatcher;

    @Autowired
    private AlbumSearchRandomPriorityQueue albumSearcher;

    @Override
    public void run(String... args) throws Exception {
        File annotatedSetFile = new File("/tmp/s1.json");
        File storyGraphFile = new File("/tmp/story.json");
        PipelineContext ctx = dataIOService.readData(storyGraphFile, annotatedSetFile);
        mandatoryImageMatcher.match(ctx);
        SortedSet<Album> bestAlbums = albumSearcher.findAlbums(ctx);
        File albumFile = new File("/tmp/album.json");
        dataIOService.writeAlbum(bestAlbums.first(), albumFile);
    }
}
