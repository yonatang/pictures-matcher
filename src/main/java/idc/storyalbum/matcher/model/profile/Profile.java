package idc.storyalbum.matcher.model.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 25/4/2015.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
    String id;
    String name;
    Set<Location> locations = new HashSet<>();
    Set<Character> characters = new HashSet<>();
    Set<Group> groups=new HashSet<>();

}
