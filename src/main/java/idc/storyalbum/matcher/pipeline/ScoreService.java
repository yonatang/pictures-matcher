package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 22/4/2015.
 */
@Service
public class ScoreService {
    private double qualityFactor = 0.5;

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

    private double eventScoreFactor = 0.5;

    public double getEventScore(PipelineContext ctx, StoryEvent event, int t, int maxT) {
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
        result += RandomUtils.nextDouble(0, 1 - Math.pow(t / maxT, 2));
        return result;
    }
}
