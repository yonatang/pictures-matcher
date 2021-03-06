package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.model.graph.StoryDependency;
import idc.storyalbum.model.graph.StoryEvent;
import idc.storyalbum.model.graph.StoryGraph;
import idc.storyalbum.model.image.AnnotatedImage;
import idc.storyalbum.model.image.AnnotatedSet;
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
        this.eventToPossibleImages = storyGraph.getEvents().stream()
                .collect(Collectors.toMap(identity(), (x) -> new HashSet<>()));

        this.imageNameMap = annotatedSet.getImages()
                .stream()
                .collect(toMap(annotatedImage -> annotatedImage.getImageFilename(), identity()));
        this.imagesToPossibleEvents = set.getImages().stream()
                .collect(toMap(identity(), (x) -> new HashSet<>()));
    }

    private AnnotatedSet annotatedSet;
    private StoryGraph storyGraph;

    private Map<Integer, StoryEvent> eventIdMap = new HashMap<>();
    private Map<String, AnnotatedImage> imageNameMap = new HashMap<>();
    private Set<String> assignedImages = new HashSet<>();
    private Set<Integer> assignedEvents = new HashSet<>();
    private Map<StoryEvent, Set<AnnotatedImage>> eventToPossibleImages;
    private Map<AnnotatedImage, Set<StoryEvent>> imagesToPossibleEvents;

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
    public boolean removePossibleMatch(StoryEvent event, AnnotatedImage image) {
        boolean removed = eventToPossibleImages.get(event).remove(image);
        imagesToPossibleEvents.get(image).remove(event);
        return removed;
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
