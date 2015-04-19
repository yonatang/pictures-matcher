package idc.storyalbum.matcher;

import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import idc.storyalbum.matcher.pipeline.PipelineContext;
import idc.storyalbum.matcher.pipeline.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.io.File;

/**
 * Created by yonatan on 18/4/2015.
 */
public class Runner implements CommandLineRunner {

    @Autowired
    private Reader reader;

    @Override
    public void run(String... args) throws Exception {
        File annotatedSetFile = new File("/tmp/s1.json");
        File storyGraphFile = new File("/tmp/story.json");
        PipelineContext ctx = reader.readData(storyGraphFile, annotatedSetFile);

    }
}
