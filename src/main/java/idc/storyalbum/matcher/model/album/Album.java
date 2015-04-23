package idc.storyalbum.matcher.model.album;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonatan on 22/4/2015.
 */
@Data
public class Album {
    private List<AlbumPage> pages = new ArrayList<>();
    private double score;
}
