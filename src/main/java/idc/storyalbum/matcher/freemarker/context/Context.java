package idc.storyalbum.matcher.freemarker.context;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonatan on 26/4/2015.
 */
@Data
public class Context {
    private List<Page> pages = new ArrayList<>();
    private Page page;
}
