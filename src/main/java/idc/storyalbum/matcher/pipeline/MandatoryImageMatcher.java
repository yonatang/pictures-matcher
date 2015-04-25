package idc.storyalbum.matcher.pipeline;

import com.google.common.collect.Iterables;
import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
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
            if (log.isDebugEnabled()){
                if (context.getPossibleMatches(storyEvent).isEmpty()){
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
        if (log.isDebugEnabled()) {
            log.debug("Filtered potential matches:");
            for (Map.Entry<StoryEvent, Set<AnnotatedImage>> storyEventSetEntry : context.getEventToPossibleImages().entrySet()) {
                StoryEvent event = storyEventSetEntry.getKey();
                Set<AnnotatedImage> possibleImages = storyEventSetEntry.getValue();
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
    }
}
