package idc.storyalbum.matcher.model.image;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class AnnotatedImage {
    private File imageFile;
    private Set<String> characterIds = new HashSet<>();
    private String locationId;
    private Set<String> itemIds = new HashSet<>();
    private DateTime imageDate;
    private ImageQuality imageQuality;
    private Set<Object> facesLocations = new HashSet<>();
}
