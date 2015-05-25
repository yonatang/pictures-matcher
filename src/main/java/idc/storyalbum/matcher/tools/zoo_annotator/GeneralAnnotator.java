package idc.storyalbum.matcher.tools.zoo_annotator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.base.Splitter;
import idc.storyalbum.model.image.AnnotatedImage;
import idc.storyalbum.model.image.AnnotatedSet;
import idc.storyalbum.model.image.ImageQuality;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 5/5/2015.
 */
public class GeneralAnnotator {

    private static Map<String, ImageQuality> readQualities(File file) throws Exception{
        LineIterator lineIterator = FileUtils.lineIterator(file);
        Map<String, ImageQuality> result= new HashMap<>();
        extenal:
        while (lineIterator.hasNext()){
            String entry;
            while (StringUtils.isBlank(entry = lineIterator.next())){
                if (!lineIterator.hasNext()){
                    break extenal;
                }
            }
            String filename = StringUtils.substringAfterLast(entry, "/");
            while (StringUtils.isBlank(entry = lineIterator.next())){
                if (!lineIterator.hasNext()){
                    break extenal;
                }
            }
            List<String> scores = Splitter.on(' ').trimResults().omitEmptyStrings().splitToList(entry);
            ImageQuality imageQuality=new ImageQuality();
            imageQuality.setOverExposedPenalty(Double.parseDouble(scores.get(0)));
            imageQuality.setUnderExposedPenalty(Double.parseDouble(scores.get(1)));
            imageQuality.setBlurinessLevelPenalty(Double.parseDouble(scores.get(2)));
            result.put(filename, imageQuality);

        }
        return result;
    }
    public static void main(String... args) throws Exception {
        String base="/Users/yonatan/Dropbox/Studies/Story Albums/Sets/MP/";
        List<String> folders =
                Arrays.asList(
//                        "72157602995838247",
//                        "72157622641789194"
//                        "72157630418695066"
                        "72157649771797738"
 );
        for (String folder : folders) {
            annotateFolder(base+folder+"/");

        }

    }

    private static void annotateFolder(String setImageFolder) throws Exception {
        File scoresFile = new File(setImageFolder,"scores.txt");
        Map<String, ImageQuality> qualities = readQualities(scoresFile);

        AnnotatedSet annotatedSet = new AnnotatedSet();
        annotatedSet.setBaseDir(new File(setImageFolder));
        for (String imageFilename : qualities.keySet()) {
            AnnotatedImage ai = new AnnotatedImage();
            annotatedSet.getImages().add(ai);
            ai.setImageQuality(qualities.get(imageFilename));
            ai.setImageFilename(imageFilename);
            ai.setImageDate(new DateTime(Utils.tryGetDate(new File(setImageFolder+"/images", imageFilename))));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.registerModule(new JodaModule());
        String json=objectMapper.writeValueAsString(annotatedSet);
        FileUtils.write(new File(setImageFolder,"annotatedSet.json"),json);
        System.out.println(json);
    }
}
