package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.graph.StoryGraph;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Created by yonatan on 18/4/2015.
 */
@Data
public class PipelineContext {
    public PipelineContext(StoryGraph storyGraph, AnnotatedSet set) {
        this.annotatedSet = set;
        this.storyGraph = storyGraph;

        this.eventIdMap = storyGraph.getEvents()
                .stream()
                .collect(toMap(StoryEvent::getId, identity()));
        this.imageNameMap = annotatedSet.getImages()
                .stream()
                .collect(toMap(annotatedImage -> annotatedImage.getImageFile().getName(), identity()));
    }

    private AnnotatedSet annotatedSet;
    private StoryGraph storyGraph;

    private Map<Integer, StoryEvent> eventIdMap = new HashMap<>();
    private Map<String, AnnotatedImage> imageNameMap = new HashMap<>();
    private Set<String> assignedImages = new HashSet<>();
    private Set<Integer> assignedEvents = new HashSet<>();
    private Map<StoryEvent, Set<AnnotatedImage>> eventToPossibleImages = new HashMap<>();
    private Map<AnnotatedImage, Set<StoryEvent>> imagesToPossibleEvents = new HashMap<>();

    /**
     * Add to an event a possible image
     *
     * @param event
     * @param image
     */
    public void addPossibleMatch(StoryEvent event, AnnotatedImage image) {
        if (!eventToPossibleImages.containsKey(event)) {
            eventToPossibleImages.put(event, new HashSet<>());
        }
        eventToPossibleImages.get(event).add(image);
        if (!imagesToPossibleEvents.containsKey(image)) {
            imagesToPossibleEvents.put(image, new HashSet<>());
        }
        imagesToPossibleEvents.get(image).add(event);
    }

    /**
     * Remove from an event a possible match
     *
     * @param event
     * @param image
     */
    public void removePossibleMatch(StoryEvent event, AnnotatedImage image) {
        eventToPossibleImages.get(event).remove(image);
        imagesToPossibleEvents.get(image).remove(event);
    }

    public Set<AnnotatedImage> getPossibleMatches(StoryEvent event) {
        return eventToPossibleImages.get(event);
    }

    public Set<StoryDependency> getInDependenciesForEvent(StoryEvent event) {
        return storyGraph.getDependencies().stream()
                .filter((dep) -> dep.getToEventId() == event.getId())
                .collect(Collectors.toSet());
    }
}
