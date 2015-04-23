package idc.storyalbum.matcher.pipeline;

import com.google.common.collect.Sets;
import idc.storyalbum.matcher.Consts.Constraints;
import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
public class ConstraintUtils {
    private final static DateTime date = new DateTime().withDate(2015, 1, 1);
    private final static DateTime fourAm = date.withTime(4, 0, 0, 0);
    private final static DateTime eightAm = date.withTime(8, 0, 0, 0);
    private final static DateTime twelvePm = date.withTime(12, 0, 0, 0);
    private final static DateTime sixPm = date.withTime(18, 0, 0, 0);
    private final static DateTime ninePm = date.withTime(21, 0, 0, 0);
    private final static DateTime midnight = date.withTime(23, 59, 59, 999);

    private final static Interval earlyMorning = new Interval(fourAm, eightAm);
    private final static Interval morning = new Interval(eightAm, twelvePm);
    private final static Interval noon = new Interval(twelvePm, sixPm);
    private final static Interval evening = new Interval(sixPm, ninePm);
    private final static Interval night = new Interval(ninePm, midnight);

    private ConstraintUtils() {
        throw new NotImplementedException("utility class");
    }

    public static boolean isMatch(Constraint constraint, AnnotatedImage image) {
        switch (constraint.getType()) {
            case Constraints.TYPE_WHAT:
                return isWhatMatch(constraint, image);
            case Constraints.TYPE_WHEN:
                return isWhenMatch(constraint, image);
            case Constraints.TYPE_WHERE:
                return isWhereMatch(constraint, image);
            case Constraints.TYPE_WHO:
                return isWhoMatch(constraint, image);
        }
        throw new IllegalStateException("Unknown constraint type " + constraint.getType());
    }

    private static boolean timeMatch(DateTime dateTime, String partOfTheDay) {
        dateTime = dateTime.withDate(2015, 1, 1);
        switch (partOfTheDay) {
            case Constraints.TIME_EARLY_MORNING:
                return earlyMorning.contains(dateTime);
            case Constraints.TIME_MORNING:
                return morning.contains(dateTime);
            case Constraints.TIME_AFTERNOON:
                return noon.contains(dateTime);
            case Constraints.TIME_EVENING:
                return evening.contains(dateTime);
            case Constraints.TIME_NIGHT:
                return night.contains(dateTime);
            case Constraints.TIME_LATE_NIGHT:
                return night.isBefore(dateTime) || earlyMorning.isAfter(dateTime);
        }
        throw new IllegalStateException("Unkown part of the day " + partOfTheDay);
    }

    static boolean isWhenMatch(Constraint constraint, AnnotatedImage image) {
        boolean isMatchReturn;
        if (StringUtils.equals(constraint.getOperator(), Constraints.OP_ONE_OF)) {
            isMatchReturn = true;
        } else {
            isMatchReturn = false;
        }

        for (String partOfTheDay : constraint.getValues()) {
            if (timeMatch(image.getImageDate(), partOfTheDay)) {
                return isMatchReturn;
            }
        }
        return !isMatchReturn;
    }

    static boolean isWhereMatch(Constraint constraint, AnnotatedImage image) {
        //ops:
        //* oneOf
        //* notOneOf
        switch (constraint.getOperator()) {
            case Constraints.OP_ONE_OF:
                return oneOf(constraint.getValues(), image.getLocationId());
            case Constraints.OP_NOT_ONE_OF:
                return notOneOf(constraint.getValues(), image.getLocationId());
        }
        throw new IllegalStateException("Unknown operator type " + constraint.getOperator());
    }

    static boolean isWhatMatch(Constraint constraint, AnnotatedImage image) {
        Set<String> itemIds = image.getItemIds();
        return isMultivalMatch(constraint, itemIds);
    }

    static boolean isWhoMatch(Constraint constraint, AnnotatedImage image) {
        //ops:
        //* includeAll
        //* includeN
        //* excludeAll
        Set<String> characterIds = image.getCharacterIds();
        return isMultivalMatch(constraint, characterIds);
    }

    private static boolean isMultivalMatch(Constraint constraint, Set<String> imageData) {
        switch (constraint.getOperator()) {
            case Constraints.OP_INCLUDE_N:
                return includeN(constraint.getExtraN(), constraint.getValues(), imageData);
            case Constraints.OP_INCLUDE_ALL:
                return includeAll(constraint.getValues(), imageData);
            case Constraints.OP_EXCLUDE_ALL:
                return excludeAll(constraint.getValues(), imageData);

        }
        throw new IllegalStateException("Unknown operator type " + constraint.getOperator());
    }

    private static boolean oneOf(Set<String> constraintData, String imageData) {
        return constraintData.contains(imageData);
    }

    private static boolean notOneOf(Set<String> constraintData, String imageData) {
        return !oneOf(constraintData, imageData);
    }

    private static boolean includeN(int n, Set<String> constraintData, Set<String> imageData) {
        Sets.SetView<String> intersection = Sets.intersection(constraintData, imageData);
        return intersection.size() >= n;
    }

    private static boolean includeAll(Set<String> constraintData, Set<String> imageData) {
        Sets.SetView<String> intersection = Sets.intersection(constraintData, imageData);
        return intersection.size() == constraintData.size();
    }

    private static boolean excludeAll(Set<String> constraintData, Set<String> imageData) {
        Sets.SetView<String> intersection = Sets.intersection(constraintData, imageData);
        return intersection.size() == 0;
    }
}
