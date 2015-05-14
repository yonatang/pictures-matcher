package idc.storyalbum.model;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by yonatan on 4/5/2015.
 */
public class Utils {
    public static Set<String> allAnimals(String file){
         InputStream allIS = Utils.class.getResourceAsStream(file);
        List<String> allLines = null;
        try {
            allLines = IOUtils.readLines(allIS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allLines.stream()
                .map((x) -> StringUtils.substringBefore(x, " : "))
                .collect(toSet());
    }
}
