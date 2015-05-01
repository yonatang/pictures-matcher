package idc.storyalbum.matcher.pipeline;

import com.google.common.collect.Sets;
import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.joda.time.DateTime;
import org.joda.time.Interval;

/**
 * Created by yonatan on 22/4/2015.
 */
public class DependencyUtils {
    static public boolean isMatch(StoryDependency dependency, AnnotatedImage i1, AnnotatedImage i2) {
        switch (dependency.getType()) {
            case "when":
                return isWhenMatch(dependency, i1, i2);
            case "where":
                return isWhereMatch(dependency, i1, i2);
            case "who":
                return isWhoMatch(dependency, i1, i2);
            case "what":
                return isWhatMatch(dependency, i1, i2);
        }
        throw new IllegalStateException("Unknown constraint type " + dependency.getType());
    }

    static boolean isWhoMatch(StoryDependency dependency, AnnotatedImage i1, AnnotatedImage i2) {
        switch (dependency.getOperator()) {
            case "include":
                return Sets.intersection(i1.getCharacterIds(), i2.getCharacterIds()).size() == i1.getCharacterIds().size();
            case "exclude":
                return Sets.intersection(i1.getCharacterIds(), i2.getCharacterIds()).size() == 0;
            case "includeN":
                return Sets.intersection(i1.getCharacterIds(), i2.getCharacterIds()).size() >= dependency.getExtraN();
        }
        throw new IllegalStateException("Unknown who operator " + dependency.getOperator());
    }

    static boolean isWhenMatch(StoryDependency dependency, AnnotatedImage i1, AnnotatedImage i2) {
        switch (dependency.getOperator()) {
            case "increase":
                return i1.getImageDate().isBefore(i2.getImageDate());
            case "decrease":
                return i1.getImageDate().isAfter(i2.getImageDate());
            case "same":
                DateTime dt1 = i1.getImageDate();
                DateTime dt2 = i2.getImageDate();
                if (dt1.isEqual(dt2)) {
                    return true;
                }
                Interval interval = dt1.isBefore(dt2) ?
                        new Interval(i1.getImageDate(), i2.getImageDate()) :
                        new Interval(i2.getImageDate(), i1.getImageDate());

                return interval.toPeriod().getMinutes() < 5;
        }
        throw new IllegalStateException("Unknown when operator " + dependency.getOperator());
    }

    static boolean isWhereMatch(StoryDependency dependency, AnnotatedImage i1, AnnotatedImage i2) {
        switch (dependency.getOperator()) {
            case "same":
                return i1.getLocationId().equals(i2.getLocationId());
            case "different":
                return !i1.getLocationId().equals(i2.getLocationId());
        }
        throw new IllegalStateException("Unknown where operator " + dependency.getOperator());
    }

    /**
     * All items in i1 included in i2
     *
     * @param dependency
     * @param i1
     * @param i2
     * @return
     */
    static boolean isWhatMatch(StoryDependency dependency, AnnotatedImage i1, AnnotatedImage i2) {
        switch (dependency.getOperator()) {
            case "include":
                return Sets.intersection(i1.getItemIds(), i2.getItemIds()).size() == i1.getItemIds().size();
            case "exclude":
                return Sets.intersection(i1.getItemIds(), i2.getItemIds()).size() == 0;
        }
        throw new IllegalStateException("Unknown what operator " + dependency.getOperator());
    }

}
