package idc.storyalbum.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Splitter;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import idc.storyalbum.matcher.model.image.ImageQuality;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 4/5/2015.
 */
public class ZooAnnotator {

    @Test
    public void t() throws Exception {
        Set<String> allAnimals = Utils.allAnimals("./all-animals-non-latin.txt");
        String setImageFolder = "/Users/yonatan/Dropbox/Studies/Story Albums/Sets/Zoo/72157600312588222/";
        String setDataFolder = setImageFolder + "items/";
        File setFolderFile = new File(setDataFolder);
        Collection<File> files = FileUtils.listFiles(setFolderFile, new String[]{"txt"}, false);
        AnnotatedSet annotatedSet = new AnnotatedSet();
        annotatedSet.setBaseDir(new File(setImageFolder));
        for (File file : files) {
            AnnotatedImage ai = new AnnotatedImage();
            annotatedSet.getImages().add(ai);
            ai.setImageQuality(new ImageQuality());
            ai.setImageFilename(file.getName());
            ai.setLocationId("zoo");
            if (file.getName().startsWith("set")) {
                continue;
            }
            System.out.println(file.getName());
            List<String> lines = FileUtils.readLines(file);
            String line = StringUtils.substringBeforeLast(lines.get(0), ":");
            List<String> someItems = Splitter.on(",").trimResults().splitToList(line);
            someItems = someItems.stream().filter((x) -> allAnimals.contains(x.toLowerCase())).collect(Collectors.toList());
            if (!someItems.isEmpty()) {
                int idx = RandomUtils.nextInt(0, someItems.size());
                String charId=someItems.get(idx);
                charId=charId.toLowerCase().replace(' ','-');
                ai.getCharacterIds().add(charId);
            }
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        System.out.println(objectMapper.writeValueAsString(annotatedSet));
    }
}
