package idc.storyalbum.matcher.pipeline;

import com.google.common.collect.Iterables;
import idc.storyalbum.matcher.exception.NoMatchException;
import idc.storyalbum.model.graph.Constraint;
import idc.storyalbum.model.graph.StoryEvent;
import idc.storyalbum.model.image.AnnotatedImage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@Service
@Slf4j
public class MandatoryImageMatcher {

    private boolean possibleMatch(StoryEvent event, AnnotatedImage image) {
        for (Constraint constraint : event.getConstraints()) {
            if (!constraint.isSoft()) {
                if (!ConstraintUtils.isMatch(constraint, image)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void findAllPossibleMatches(PipelineContext context) {
        for (StoryEvent storyEvent : context.getEventIdMap().values()) {
            log.debug("{}", storyEvent);
            for (AnnotatedImage annotatedImage : context.getImageNameMap().values()) {
                if (possibleMatch(storyEvent, annotatedImage)) {
                    log.debug("  Potential match: {}", annotatedImage.getImageFilename());
                    context.addPossibleMatch(storyEvent, annotatedImage);
                }
            }
            if (log.isDebugEnabled()) {
                if (context.getPossibleMatches(storyEvent).isEmpty()) {
                    log.debug("  No potential matches!");
                    //actually, the NoMatchException can be thrown here
                    //but for the sake of logging more information, i let it
                    //continue and fail the the next phase
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
        final MutableBoolean stable = new MutableBoolean();
        Map<StoryEvent, Set<AnnotatedImage>> possibleImageMap = context.getEventToPossibleImages();
        do {
            stable.setTrue();

            for (Map.Entry<StoryEvent, Set<AnnotatedImage>> storyEventSetEntry : possibleImageMap.entrySet()) {
                StoryEvent event = storyEventSetEntry.getKey();
                Set<AnnotatedImage> possibleImages = storyEventSetEntry.getValue();
                if (possibleImages.size() == 1) {
                    //remove the mandatory match from all other events
                    AnnotatedImage theImage = Iterables.getOnlyElement(possibleImages);

                    //remove it
                    Set<StoryEvent> possibleEvents = new HashSet<>(context.getImagesToPossibleEvents().get(theImage));
                    possibleEvents.stream()
                            .filter(possibleStoryEvent -> !possibleStoryEvent.equals(event))
                            .forEach(possibleStoryEvent -> {
                                boolean removed = context.removePossibleMatch(possibleStoryEvent, theImage);
                                if (removed) {
                                    stable.setFalse();
                                }
                            });
                }
                if (possibleImages.size() == 0) {
                    throw new NoMatchException("Couldn't find match for event " + event.getId() + ":" + event.getName());
                }
            }
        } while (stable.isFalse());
        if (log.isDebugEnabled()) {
            log.debug("Filtered potential matches:");
            List<StoryEvent> events = new ArrayList<>(possibleImageMap.keySet());
            events.sort((o1, o2) -> Integer.compare(o1.getId(),o2.getId()));
            for (StoryEvent event : events) {
                Set<AnnotatedImage> possibleImages = possibleImageMap.get(event);
                log.debug("  {}", event);
                for (AnnotatedImage possibleImage : possibleImages) {
                    log.debug("    Potential match: {}", possibleImage.getImageFilename());
                }
            }
        }
    }

    public void match(PipelineContext context) throws NoMatchException {
        log.info("Finding all potential matches");
        findAllPossibleMatches(context);
        log.info("Fixing trivial matches");
        filterMandatoryMatches(context);
        log.info("All potential images are set");
        //calculate average images per node
        int i = 0;
        Map<StoryEvent, Set<AnnotatedImage>> eventToPossibleImages = context.getEventToPossibleImages();
        for (Set<AnnotatedImage> storyEvents : eventToPossibleImages.values()) {
            i += storyEvents.size();
        }
        log.info("Average number of pictures per node: {}", i / (double) eventToPossibleImages.size());
    }
}
