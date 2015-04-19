package idc.storyalbum.matcher;

/**
 * Created by yonatan on 18/4/2015.
 */
public interface Consts {
    interface Constraints {
        String TYPE_WHO = "who";
        String TYPE_WHERE = "where";
        String TYPE_WHEN = "when";
        String TYPE_WHAT = "what";

        String OP_INCLUDE_ALL = "includeAll";
        String OP_INCLUDE_N = "includeN";
        String OP_EXCLUDE_ALL = "excludeAll";

        String OP_ONE_OF = "oneOf";
        String OP_NOT_ONE_OF = "notOneOf";

        String TIME_EARLY_MORNING = "early-morning";
        String TIME_MORNING = "morning";
        String TIME_AFTERNOON = "afternoon";
        String TIME_EVENING = "evening";
        String TIME_NIGHT = "night";
        String TIME_LATE_NIGHT = "late-night";
    }
}
