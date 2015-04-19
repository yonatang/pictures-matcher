package idc.storyalbum.matcher.pipeline;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@Service
public class MandatoryImageMatcher {

    private boolean possibleMatch(StoryEvent event, AnnotatedImage image) {
        for (Constraint constraint : event.getConstraints()) {
            if (!constraint.isSoft()){

            }
        }
        return true;
    }

    private void findAllPossibleMatches(PipelineContext context) {
        for (StoryEvent storyEvent : context.getEventIdMap().values()) {
            for (AnnotatedImage annotatedImage : context.getImageNameMap().values()) {
                if (possibleMatch(storyEvent, annotatedImage)) {
                    context.addPossibleMatch(storyEvent, annotatedImage);
                }

            }
        }
    }

    /**
     * iterate through all options and try to find if there is an event with single image matching.
     * If so, match it and remove it from all other events. Repeat until stabilize.
     *
     * @param context
     * @throws NoMatchException
     */
    private void filterMandatoryMatches(PipelineContext context) throws NoMatchException {
        boolean stable;
        do {
            stable = true;

            for (Map.Entry<StoryEvent, Set<AnnotatedImage>> storyEventSetEntry : context.getEventToPossibleImages().entrySet()) {
                StoryEvent event = storyEventSetEntry.getKey();
                Set<AnnotatedImage> possibleImages = storyEventSetEntry.getValue();
                if (possibleImages.size() == 1) {
                    stable = false;
                    //remove the mandatory match from all other events
                    AnnotatedImage theImage = Iterables.getOnlyElement(possibleImages);

                    //remove it
                    Set<StoryEvent> possibleEvents = new HashSet<>(context.getImagesToPossibleEvents().get(theImage));
                    possibleEvents.stream()
                            .filter(possibleStoryEvent -> !possibleStoryEvent.equals(event))
                            .forEach(possibleStoryEvent -> {
                                context.removePossibleMatch(possibleStoryEvent, theImage);
                            });

                }
                if (possibleImages.size() == 0) {
                    throw new NoMatchException("Couldn't find match for event " + event.getId() + ":" + event.getName());
                }
            }
        } while (!stable);
    }

    public void match(PipelineContext context) throws NoMatchException {
        findAllPossibleMatches(context);
        filterMandatoryMatches(context);
    }
}