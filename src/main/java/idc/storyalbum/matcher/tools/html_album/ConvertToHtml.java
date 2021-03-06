package idc.storyalbum.matcher.tools.html_album;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import idc.storyalbum.model.album.Album;
import idc.storyalbum.model.album.AlbumPage;
import idc.storyalbum.model.image.AnnotatedImage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yonatan on 25/4/2015.
 */
public class ConvertToHtml {
    public static void write(File albumFile, File htmlFile) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        Album album = objectMapper.readValue(albumFile, Album.class);
        String baseDir = album.getBaseDir().getAbsolutePath();
        List<String> lines = new ArrayList<>();
        lines.add("<!DOCTYPE html>");
        lines.add("<html>");
        lines.add("<head><link href='http://fonts.googleapis.com/css?family=Indie+Flower' rel='stylesheet' type='text/css'></head>");
        lines.add("<body>");
        lines.add("<div>");
        lines.add("<h1>Album date: " + album.getDate() + ", score: " + album.getScore() + "</h1>");
        int idx = 0;
        for (AlbumPage albumPage : album.getPages()) {
            idx++;
            AnnotatedImage image = albumPage.getImage();
            String img = "file://" + baseDir + File.separatorChar + image.getImageFilename();
            String style = "max-height:300px; max-width:300px";
            lines.add("  <h2>Page " + idx + "</h2>");
            lines.add("<div style=\"font-family: 'Indie Flower', cursive;font-size: 30px;width:100%; text-align:center\">");
            lines.add("  <img src='" + img + "' style='" + style + "'>");
            lines.add("  <div style='width:100%'>");
            lines.add("</div>");
            String text = albumPage.getText();
            String[] textLines = StringUtils.split(text, "\n\r");
            for (String textLine : textLines) {
                lines.add(StringEscapeUtils.escapeHtml4(textLine) + "<br/>");
            }
            lines.add("  </div>");
        }
        lines.add("</div>");
        lines.add("</body>");
        lines.add("</html>");
        FileUtils.writeLines(htmlFile, lines, false);
    }

    public static void main(String... args) throws Exception {
        File input = new File("/Users/yonatan/StoryAlbumData/Riddle/Set1/album.json");
        File output = new File("/Users/yonatan/StoryAlbumData/Riddle/Set1/out.html");
        write(input,output);
    }
}
