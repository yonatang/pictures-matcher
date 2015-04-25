package idc.storyalbum.matcher.model.album;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yonatan on 22/4/2015.
 */
@Data
@JsonPropertyOrder({"date", "baseDir", "score", "pages"})
public class Album {
    private List<AlbumPage> pages = new ArrayList<>();
    private double score;
    private Date date;
    private File baseDir;
}
