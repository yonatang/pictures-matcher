package idc.storyalbum.matcher.model.album;

import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yonatan on 22/4/2015.
 */
@Data
@NoArgsConstructor
public class AlbumPage {
    public AlbumPage(AnnotatedImage image, StoryEvent storyEvent) {
        this.image = image;
        this.storyEvent = storyEvent;
    }

    private AnnotatedImage image;
    private StoryEvent storyEvent;
    private String text;
}
