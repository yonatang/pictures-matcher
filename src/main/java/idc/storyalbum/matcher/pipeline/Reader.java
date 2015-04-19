package idc.storyalbum.matcher.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * Created by yonatan on 18/4/2015.
 */
@Service
@Slf4j
public class Reader {
    @Autowired
    private ObjectMapper objectMapper;

    public PipelineContext readData(File graphFile, File setFile) throws IOException {
        return new PipelineContext(readStoryGraph(graphFile), readAnnotatedSet(setFile));
    }

    private StoryGraph readStoryGraph(File file) throws IOException {
        log.info("Reading file {}", file);
        StoryGraph storyGraph = objectMapper.readValue(file, StoryGraph.class);
        log.info("Read graph with {} nodes and {} edges", storyGraph.getEvents().size(), storyGraph.getDependencies().size());
        return storyGraph;
    }

    private AnnotatedSet readAnnotatedSet(File file) throws IOException {
        log.info("Reading set data {}", file);
        AnnotatedSet annotatedSet = objectMapper.readValue(file, AnnotatedSet.class);
        log.info("Read set with {} images", annotatedSet.getImages().size());
        annotatedSet.setBaseDir(new File(FilenameUtils.getFullPath(file.getAbsolutePath())));
        return annotatedSet;
    }


}
