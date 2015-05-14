package idc.storyalbum.matcher.tools.zoo_annotator;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectoryBase;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.file.FileMetadataDirectory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

/**
 * Created by yonatan on 5/5/2015.
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

    public static Date tryGetDate(File image) throws ImageProcessingException, IOException {
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
}
