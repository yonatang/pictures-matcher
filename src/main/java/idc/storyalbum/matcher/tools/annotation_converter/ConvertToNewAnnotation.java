package idc.storyalbum.matcher.tools.annotation_converter;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileMetadataDirectory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import idc.storyalbum.matcher.model.image.AnnotatedImage;
import idc.storyalbum.matcher.model.image.AnnotatedSet;
import idc.storyalbum.matcher.model.image.ImageQuality;
import idc.storyalbum.matcher.model.image.Rectangle;
import idc.storyalbum.matcher.tools.annotation_converter.old.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.joda.time.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.apache.commons.lang3.StringUtils.substringAfter;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;

/**
 * Created by yonatan on 24/4/2015.
 */
public class ConvertToNewAnnotation {
    private static Date tryGetDate(File image) throws ImageProcessingException, IOException {
        Metadata metadata = ImageMetadataReader.readMetadata(image);
        Directory directory;
        directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (directory != null) {
            Date date = directory.getDate(ExifDirectoryBase.TAG_DATETIME);
            if (date != null) {
                return date;
            }
        }
        directory = metadata.getFirstDirectoryOfType(FileMetadataDirectory.class);
        if (directory != null) {
            Date date = directory.getDate(FileMetadataDirectory.TAG_FILE_MODIFIED_DATE);
            if (date != null) {
                return date;
            }
        }
        return null;
    }

    public static void main(String... args) throws Exception {
        XStream x = new XStream(new DomDriver());
        x.ignoreUnknownElements();
        x.alias("dataTypes.ImageData", ImageData.class);
        x.alias("dataTypes.DisneyCharacterData", DisneyCharacterData.class);
        x.alias("dataTypes.FamilyMemberData", FamilyMemberData.class);

        String dirName = "/Users/yonatan/StoryAlbumData/OldSet1";
        File dir = new File(dirName);
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"txt"}, false);
        AnnotatedSet set = new AnnotatedSet();
        set.setBaseDir(dir);
        for (File file : files) {
            String metadataName = FilenameUtils.getName(file.getName());
            String imageFilename = substringAfter(substringBeforeLast(metadataName, ".txt"), "image_");


            AnnotatedImage image = new AnnotatedImage();
            set.getImages().add(image);

            ImageData oldImage = (ImageData) x.fromXML(file);

            image.setImageFilename(imageFilename);
            Date date = tryGetDate(new File(dir, imageFilename));
            image.setImageDate(new DateTime(date));

            image.setLocationId(oldImage.getAttractionName().name());

            for (FamilyMemberData familyMemberData : oldImage.getFamilyMembersData()) {
                if (familyMemberData.getAgeRangeEnd() <= 18) {
                    if (familyMemberData.getGender() == EGender.female) {
                        image.getCharacterIds().add("girl");
                    } else if (familyMemberData.getGender() == EGender.male) {
                        image.getCharacterIds().add("boy");
                    }
                } else {
                    if (familyMemberData.getGender() == EGender.female) {
                        image.getCharacterIds().add("mother");
                    } else if (familyMemberData.getGender() == EGender.male) {
                        image.getCharacterIds().add("father");
                    }
                }
            }
            for (DisneyCharacterData disneyCharacterData : oldImage.getDisneyCharacters()) {
                image.getCharacterIds().add(disneyCharacterData.getDisneyCharacter().name());
            }

            for (Integer[] vals : oldImage.getFaces()) {
                Rectangle rect = new Rectangle();
                rect.setX(vals[0]);
                rect.setY(vals[1]);
                rect.setWidth(vals[2]);
                rect.setHeight(vals[3]);
            }
            if (oldImage.getArtifacts() != null && oldImage.getArtifacts().length > 0) {
                image.getItemIds().addAll(Arrays.asList(oldImage.getArtifacts()));
            }

            AutomaticImageQuality automaticQuality = oldImage.getAutomaticQuality();
            ImageQuality iq = new ImageQuality();
            iq.setBlurinessLevelPenalty(automaticQuality.getBlurinessLevelPenalty());
            iq.setOverExposedPenalty(automaticQuality.getOverExposedPenalty());
            iq.setUnderExposedPenalty(automaticQuality.getUnderExposedPenalty());
            image.setImageQuality(iq);

        }
        ObjectMapper objectMapper = new ObjectMapper();
        JodaModule jodaModule = new JodaModule();
        objectMapper.registerModule(new JodaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.writeValue(new File(dir, "annotatedSet.json"), set);
//        File file=new File("/Users/yonatan/StoryAlbumData/OldSet1/image_735184.74481_0160.jpg.txt");
////file=new File("/Users/yonatan/PrivateGithub/StoryAlbum/PictureMatch/src/main/java/idc/storyalbum/matcher/tools/t.xml");
//        ImageData imageData = (ImageData) x.fromXML(file);
//        System.out.println(imageData.getArtifacts());
//        System.out.println(imageData.getAutomaticQuality());
//        System.out.println(imageData);
//        Object o = x.createObjectInputStream(new FileInputStream(file)).readObject();
//        System.out.println(o);
    }
}
