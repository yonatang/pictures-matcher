package idc.storyalbum.matcher.model.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class AnnotatedSet {
    @Setter(AccessLevel.NONE)
    private Set<AnnotatedImage> images = new HashSet<>();
    private File baseDir;
}
