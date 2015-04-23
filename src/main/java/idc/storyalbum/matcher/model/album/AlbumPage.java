package idc.storyalbum.matcher.model.album;

import idc.storyalbum.matcher.model.graph.StoryEvent;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yonatan on 22/4/2015.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlbumPage {
    private AnnotatedImage image;
    private StoryEvent storyEvent;
}
