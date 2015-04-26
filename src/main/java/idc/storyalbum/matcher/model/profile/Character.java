package idc.storyalbum.matcher.model.profile;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 25/4/2015.
 */
@Data
public class Character {
    private String id;
    private String name;
    private String gender;
    private Set<String> groups = new HashSet<>();
}
