package idc.storyalbum.matcher.freemarker.context;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 26/4/2015.
 */
@Data
public class Character {
    private String id;
    private String name;
    private Set<String> groups = new HashSet<>();
    private String gender;
}
