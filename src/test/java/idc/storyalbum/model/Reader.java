package idc.storyalbum.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import idc.storyalbum.model.graph.StoryDependency;
import idc.storyalbum.model.graph.StoryEvent;
import idc.storyalbum.model.graph.StoryGraph;
import idc.storyalbum.model.image.AnnotatedImage;
import org.testng.annotations.Test;

/**
 * Created by yonatan on 18/4/2015.
 */
@Test
public class Reader {

    ObjectMapper objectMapper = new ObjectMapper();

    public void shouldReadEvent() throws Exception {
        StoryEvent storyEvent = objectMapper.readValue(Resources.getResource("event.json"), StoryEvent.class);
        System.out.println(storyEvent);
    }

    public void shouldReadDependency() throws Exception {
        StoryDependency storyDependency = objectMapper.readValue(Resources.getResource("dependency.json"), StoryDependency.class);
        System.out.println(storyDependency);
    }

    public void shouldReadStory() throws Exception {
        StoryGraph storyGraph = objectMapper.readValue(Resources.getResource("story.json"), StoryGraph.class);
        System.out.println(storyGraph);
    }

    public void shouldReadAnnotatedImage() throws Exception {
        AnnotatedImage annotatedImage = objectMapper.readValue(Resources.getResource("annotator/image.json"), AnnotatedImage.class);
        System.out.println(annotatedImage);


    }
}
