package idc.storyalbum.matcher.pipeline;

import com.fasterxml.jackson.databind.ObjectMapper;
import idc.storyalbum.matcher.model.album.Album;
import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import lombok.extern.slf4j.Slf4j;
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
public class DataIOService {
    @Autowired
    private ObjectMapper objectMapper;

    public PipelineContext readData(File graphFile, File setFile) throws IOException {
        return new PipelineContext(readStoryGraph(graphFile), readAnnotatedSet(setFile));
    }

    private StoryGraph readStoryGraph(File file) throws IOException {
        log.info("Reading file {}", file);
        StoryGraph storyGraph = objectMapper.readValue(file, StoryGraph.class);
        log.info("Read graph with {} nodes and {} edges", storyGraph.getEvents().size(), storyGraph.getDependencies().size());
        if (log.isDebugEnabled()) {
            for (StoryEvent storyEvent : storyGraph.getEvents()) {
                log.debug("Event {}:{}", storyEvent.getId(), storyEvent.getName());
                for (Constraint constraint : storyEvent.getConstraints()) {
                    log.debug("  Constraint {}", constraint);
                }
            }
            for (StoryDependency storyDependency : storyGraph.getDependencies()) {
                log.debug("Dependency {} {}->{}: {} {}", storyDependency.getType(), storyDependency.getFromEventId(), storyDependency.getToEventId(), storyDependency.getName(), storyDependency.getOperator());
            }
        }
        return storyGraph;
    }

    private AnnotatedSet readAnnotatedSet(File file) throws IOException {
        log.info("Reading set data {}", file);
        AnnotatedSet annotatedSet = objectMapper.readValue(file, AnnotatedSet.class);
        log.info("Read set with {} images", annotatedSet.getImages().size());
        annotatedSet.setBaseDir(new File(FilenameUtils.getFullPath(file.getAbsolutePath())));
        return annotatedSet;
    }

    public void writeAlbum(Album album, File file) throws IOException {
        log.info("Writing down an album with score {} to {}", album.getScore(), file);
        objectMapper.writeValue(file, album);
        log.info("File {} was written. File size {}", file, file.length());
    }

}
