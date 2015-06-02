package idc.storyalbum.matcher.tools.annotation_converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import idc.storyalbum.model.image.AnnotatedImage;
import idc.storyalbum.model.image.AnnotatedSet;
import idc.storyalbum.model.image.ImageQuality;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 30/5/2015.
 */
public class AddImages {
    public static void main(String... args) throws Exception {
        String orgAnnoFilename="/Users/yonatan/Dropbox/Studies/Story Albums/Sets/Riddle/Set5_full/annotatedSet.json";
        String newAnnoFilename="/Users/yonatan/Dropbox/Studies/Story Albums/Sets/Riddle/Set5_full/annotatedSet.new.json";
        String dirNameToScan="/Users/yonatan/Dropbox/Studies/Story Albums/Sets/Riddle/Set5_full/images";

        File orgAnnotations=new File(orgAnnoFilename);
        ObjectMapper objectMapper=new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        AnnotatedSet annotatedSet = objectMapper.readValue(orgAnnotations, AnnotatedSet.class);
        Set<String> existingFiles=new HashSet<>();
        for (AnnotatedImage annotatedImage : annotatedSet.getImages()) {
            existingFiles.add(annotatedImage.getImageFilename());
        }
        File dirToScan=new File(dirNameToScan);
        Collection<File> files = FileUtils.listFiles(dirToScan, new String[]{"jpg"}, false);
        for (File file : files) {
            if (existingFiles.contains(file.getName())){
                System.out.println("Skipping "+file.getName());
                continue;
            }
            AnnotatedImage annotatedImage=new AnnotatedImage();
            annotatedImage.setImageFilename(file.getName());
            annotatedImage.setImageQuality(new ImageQuality());
            annotatedImage.setImageDate(new DateTime(ConvertToNewAnnotation.tryGetDate(file)));
            annotatedSet.getImages().add(annotatedImage);
        }
        System.out.println("Total of "+files.size()+" scanned");
        System.out.println(annotatedSet.getImages().size()+" images annotated");
        objectMapper.writeValue(new File(newAnnoFilename),annotatedSet);

        AnnotatedSet ras = objectMapper.readValue(new File(newAnnoFilename), AnnotatedSet.class);
        System.out.println(ras.getImages().size());

    }
}
