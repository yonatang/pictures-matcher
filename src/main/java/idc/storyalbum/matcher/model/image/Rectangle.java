package idc.storyalbum.matcher.model.image;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yonatan on 24/4/2015.
 */
@Data
@NoArgsConstructor
public class Rectangle {
    private int x;
    private int y;
    private int width;
    private int height;
}
