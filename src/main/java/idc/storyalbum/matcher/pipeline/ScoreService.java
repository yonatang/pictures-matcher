package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 22/4/2015.
 */
@Service
@CacheConfig(cacheNames = "scores-cache")
public class ScoreService {

    @Value("${story-album.scores.quality-factory}")
    private double qualityFactor;

    /**
     * Calculate the fineness of a specific image to an event
     *
     * @param image
     * @param event
     * @return score
     */
    @Cacheable
    public double getImageFitScore(AnnotatedImage image, StoryEvent event) {
        Set<Constraint> softConstraints = event.getConstraints().stream()
                .filter(Constraint::isSoft)
                .collect(Collectors.toSet());
        long softConstraintsCount = Math.min(softConstraints.size(), 10);
        double factor = (1.0 - qualityFactor) / ((double) softConstraintsCount);

        long matchedConstraints = softConstraints.stream()
                .filter(constraint -> ConstraintUtils.isMatch(constraint, image))
                .count();
        return factor / (double) matchedConstraints;
    }

    @Value("${story-album.scores.event-factor}")
    private double eventScoreFactor = 0.5;

    /**
     * Calculate the relative score of an event, in order to greedy process it
     *
     * @param ctx
     * @param event
     * @param nonFuzziness - a number between 0 to 1 that how deterministic the result is.
     *                     nonFuzziness 1: totally deterministic. nonFuzziness 0: very random
     * @return
     */
    @Cacheable
    public double getEventScore(PipelineContext ctx, StoryEvent event, double nonFuzziness) {
        double largestOptions = ctx.getEventToPossibleImages().values()
                .stream()
                .mapToInt(Set::size)
                .max()
                .getAsInt();
        double largestDegree = ctx.getEventToPossibleImages().keySet()
                .stream()
                .mapToInt((event1) -> ctx.getInDependenciesForEvent(event1).size())
                .max()
                .getAsInt();
        double degree = ctx.getInDependenciesForEvent(event).size();
        double optionsCount = ctx.getEventToPossibleImages().get(event).size();

        double result = eventScoreFactor * (1.0 - (optionsCount / largestOptions));
        result += (1.0 - eventScoreFactor) * (degree / largestDegree);
        result += RandomUtils.nextDouble(0, 1 - Math.pow(nonFuzziness, 2));
        return result;
    }
}
