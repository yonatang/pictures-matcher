package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.graph.Constraint;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.joda.time.DateTime;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static idc.storyalbum.matcher.Consts.Constraints.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by yonatan on 19/4/2015.
 */
@Test
public class ConstraintUtilsTest {

    @DataProvider(name = "whenTests")
    public static Object[][] whenTests() {
        String date = "2015-01-01T";
        Object[][] result = new Object[][]{
                new Object[]{OP_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"08:55", true},
                new Object[]{OP_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"19:55", true},
                new Object[]{OP_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"12:55", false},
                new Object[]{OP_NOT_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"08:55", false},
                new Object[]{OP_NOT_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"19:55", false},
                new Object[]{OP_NOT_ONE_OF, new String[]{TIME_MORNING, TIME_EVENING}, date+"12:55", true},
        };
        return result;
    }

    @Test(dataProvider = "whenTests")
    public void testWhen(String op, String[] values, String dateTime, boolean result){
        Constraint constraint = new Constraint(TYPE_WHEN, op, false, values);
        AnnotatedImage ai = new AnnotatedImage();
        ai.setImageDate(new DateTime(dateTime));
        assertThat(ConstraintUtils.isWhenMatch(constraint, ai), is(result));
    }
    @DataProvider(name = "whereTests")
    public static Object[][] whereTests() {
        Object[][] result = new Object[][]{
                new Object[]{OP_ONE_OF, new String[]{"loc1", "loc2"}, "loc1", true},
                new Object[]{OP_ONE_OF, new String[]{"loc1", "loc2"}, "loc3", false},
                new Object[]{OP_NOT_ONE_OF, new String[]{"loc1", "loc2"}, "loc3", true},
                new Object[]{OP_NOT_ONE_OF, new String[]{"loc1", "loc2"}, "loc1", false},
        };
        return result;
    }

    @Test(dataProvider = "whereTests")
    public void testWhere(String op, String[] values, String loc, boolean result) {
        Constraint constraint = new Constraint(TYPE_WHERE, op, false, values);
        AnnotatedImage ai = new AnnotatedImage();
        ai.setLocationId(loc);
        assertThat(ConstraintUtils.isWhereMatch(constraint, ai), is(result));
    }

    @DataProvider(name = "whoTests")
    public static Object[][] whoTests() {
        Object[][] result = new Object[][]{
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2", "c3"}, null, true},
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c3"}, null, false},
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2"}, null, true},

                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2"}, null, false},
                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c3", "c4"}, null, true},
                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, null, false},

                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, 1, true},
                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, 2, false},
                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c1", "c4", "c2"}, 2, true}

        };
        return result;
    }

    @Test(dataProvider = "whoTests")
    public void testWho(String op, String[] values, String[] imageChars, Integer extraN, boolean result) {
        Constraint constraint = new Constraint(TYPE_WHO, op, false, extraN, values);
        AnnotatedImage ai = new AnnotatedImage();
        ai.getCharacterIds().addAll(Arrays.asList(imageChars));
        assertThat(ConstraintUtils.isWhoMatch(constraint, ai), is(result));
    }

    @DataProvider(name = "whatTests")
    public static Object[][] whatTests() {
        Object[][] result = new Object[][]{
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2", "c3"}, null, true},
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c3"}, null, false},
                new Object[]{OP_INCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2"}, null, true},

                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c1", "c2"}, null, false},
                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c3", "c4"}, null, true},
                new Object[]{OP_EXCLUDE_ALL, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, null, false},

                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, 1, true},
                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c3", "c4", "c2"}, 2, false},
                new Object[]{OP_INCLUDE_N, new String[]{"c1", "c2"}, new String[]{"c1", "c4", "c2"}, 2, true}

        };
        return result;
    }

    @Test(dataProvider = "whatTests")
    public void testWhat(String op, String[] values, String[] imageChars, Integer extraN, boolean result) {
        Constraint constraint = new Constraint(TYPE_WHAT, op, false, extraN, values);
        AnnotatedImage ai = new AnnotatedImage();
        ai.getItemIds().addAll(Arrays.asList(imageChars));
        assertThat(ConstraintUtils.isWhatMatch(constraint, ai), is(result));
    }

}