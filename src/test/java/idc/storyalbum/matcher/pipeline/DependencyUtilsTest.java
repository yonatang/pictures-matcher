package idc.storyalbum.matcher.pipeline;

import idc.storyalbum.matcher.model.graph.StoryDependency;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import org.joda.time.DateTime;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by yonatan on 22/4/2015.
 */
@Test
public class DependencyUtilsTest {
    public void shouldMatchCloseTimeDates() {
        AnnotatedImage ai1 = new AnnotatedImage();
        ai1.setImageDate(new DateTime());
        AnnotatedImage ai2 = new AnnotatedImage();
        ai1.setImageDate(ai1.getImageDate().plusSeconds(240));
        StoryDependency storyDependency = new StoryDependency("when", "same", 1, 2);
        assertThat(DependencyUtils.isWhenMatch(storyDependency, ai1, ai2), is(true));
    }
    public void shouldNotMatchNonCloseTimeDates() {
        AnnotatedImage ai1 = new AnnotatedImage();
        ai1.setImageDate(new DateTime());
        AnnotatedImage ai2 = new AnnotatedImage();
        ai1.setImageDate(ai1.getImageDate().plusMinutes(5).plusSeconds(1));
        StoryDependency storyDependency = new StoryDependency("when", "same", 1, 2);
        assertThat(DependencyUtils.isWhenMatch(storyDependency, ai1, ai2), is(false));
    }
}
