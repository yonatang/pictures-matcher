package idc.storyalbum.matcher.model.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@JsonPropertyOrder({"baseDir", "images"})
public class AnnotatedSet {
    @Setter(AccessLevel.NONE)
    private Set<AnnotatedImage> images = new HashSet<>();
    private File baseDir;
}
