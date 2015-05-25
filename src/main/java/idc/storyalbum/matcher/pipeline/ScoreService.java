package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.model.graph.Constraint;
import idc.storyalbum.model.graph.StoryEvent;
import idc.storyalbum.model.image.AnnotatedImage;
import idc.storyalbum.model.image.ImageQuality;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 22/4/2015.
 */
@Service
public class ScoreService {

    @Value("${story-album.scores.quality-factory}")
    private double qualityFactor;

    @Value("${story-album.scores.underExposedPenalty}")
    private double underExposedPenalty;
    @Value("${story-album.scores.blurinessLevelPenalty}")
    private double blurinessLevelPenalty;
    @Value("${story-album.scores.overExposedPenalty}")
    private double overExposedPenalty;

    /**
     * Calculate the fineness of a specific image to an event with some random jittering
     *
     * @param image
     * @param event
     * @param nonFuziness
     * @return
     */
    public double getImageFitScore(AnnotatedImage image, StoryEvent event, double nonFuziness) {
        return getImageFitScore(image, event) + fuziness(nonFuziness);
    }

    /**
     * Calculate the fineness of a specific image to an event
     *
     * @param image
     * @param event
     * @return score
     */
    public double getImageFitScore(AnnotatedImage image, StoryEvent event) {
        //calculate soft constraints score
        Set<Constraint> softConstraints = event.getConstraints().stream()
                .filter(Constraint::isSoft)
                .collect(Collectors.toSet());

        double softConstraintsScore = 0;
        if (!softConstraints.isEmpty()) {
            long softConstraintsCount = Math.min(softConstraints.size(), 10);
            double factor = (1.0 - qualityFactor) / ((double) softConstraintsCount);
            long matchedConstraints = softConstraints.stream()
                    .filter(constraint -> ConstraintUtils.isMatch(constraint, image))
                    .count();
            softConstraintsScore = factor * Math.min(matchedConstraints, 10.0);
        }

        //calculate image quality score
        ImageQuality imageQuality = image.getImageQuality();
        imageQuality.getBlurinessLevelPenalty();
        imageQuality.getOverExposedPenalty();
        imageQuality.getUnderExposedPenalty();

        double qualityScore = qualityFactor * (
                underExposedPenalty * imageQuality.getUnderExposedPenalty() +
                        overExposedPenalty * imageQuality.getOverExposedPenalty() +
                        blurinessLevelPenalty * imageQuality.getBlurinessLevelPenalty());

        return qualityScore + softConstraintsScore;
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
        result += fuziness(nonFuzziness);
        return result;
    }

    private double fuziness(double nonFuzziness) {
        return RandomUtils.nextDouble(0, 1 - Math.pow(nonFuzziness, 2));
    }
}
