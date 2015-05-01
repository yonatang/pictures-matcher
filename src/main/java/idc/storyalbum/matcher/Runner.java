package idc.storyalbum.matcher;

import idc.storyalbum.matcher.exception.NoMatchException;
import idc.storyalbum.matcher.exception.TemplateErrorException;
import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.pipeline.DataIOService;
import idc.storyalbum.matcher.pipeline.MandatoryImageMatcher;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import idc.storyalbum.matcher.pipeline.StoryTextResolver;
import idc.storyalbum.matcher.pipeline.albumsearch.AlbumSearch;
import idc.storyalbum.matcher.pipeline.albumsearch.AlbumSearchFactory;
import idc.storyalbum.matcher.tools.html_album.ConvertToHtml;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.SortedSet;

/**
 * Created by yonatan on 18/4/2015.
 */
@Component
@Slf4j
public class Runner implements CommandLineRunner {

    @Autowired
    private AlbumSearchFactory albumSearchFactory;

    @Value("${story-album.search.strategy}")
    private String searchStrategy;

    @Value("${story-album.debug-album:false}")
    private boolean writeDebugAlbum;

    @Value("${story-album.search.strategy}")
    private String strategyName;

    private AlbumSearch albumSearch;

    @PostConstruct
    void init() {
        albumSearch = albumSearchFactory.getAlbumSearch(searchStrategy);
    }

    @Autowired
    private DataIOService dataIOService;

    @Autowired
    private MandatoryImageMatcher mandatoryImageMatcher;

//    @Autowired
//    private AlbumSearchRandomPriorityQueue albumSearcher;

    @Autowired
    private StoryTextResolver storyTextResolver;

    @Override
    public void run(String... args) throws Exception {
        try {
            File annotatedSetFile = new File("/Users/yonatan/StoryAlbumData/Riddle/Set1/annotatedSet.json"); //new File("/tmp/annotatedSet.json");
            File storyGraphFile = new File("/Users/yonatan/StoryAlbumData/Riddle/story.json");
            PipelineContext ctx = dataIOService.readData(storyGraphFile, annotatedSetFile);
            mandatoryImageMatcher.match(ctx);
            SortedSet<Album> bestAlbums = albumSearch.findAlbums(ctx);
            Album bestAlbum = bestAlbums.first();
            storyTextResolver.resolveText(bestAlbum, ctx.getStoryGraph().getProfile());
            File albumFile = new File("/Users/yonatan/StoryAlbumData/Riddle/Set1/album.json");
            dataIOService.writeAlbum(bestAlbum, albumFile);
            if (writeDebugAlbum) {
                String albumPath = FilenameUtils.getFullPath(albumFile.getAbsolutePath());
                File debugHtmlFile = new File(albumPath, "album-" + strategyName + ".html");
                log.info("Producing a debug album {}", debugHtmlFile);
                ConvertToHtml.write(albumFile, debugHtmlFile);
            }
        } catch (NoMatchException e) {
            log.error("Error! Cannot satisfy story constraints: {}", e.getMessage());
        } catch (TemplateErrorException e) {
            log.error("Error! Cannot process template: {}", e.getMessage());
        }
    }
}
