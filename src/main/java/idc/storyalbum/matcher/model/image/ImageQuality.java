package idc.storyalbum.matcher.model.image;

import lombok.Data;

/**
 * Created by yonatan on 18/4/2015.
 */
@Data
public class ImageQuality {
    private double overExposedPenalty;
    private double underExposedPenalty;
    private double blurinessLevelPenalty;
}
