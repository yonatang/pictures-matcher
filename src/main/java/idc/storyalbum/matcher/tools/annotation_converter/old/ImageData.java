package idc.storyalbum.matcher.tools.annotation_converter.old;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Created by yonatan on 24/4/2015.
 */
@Data
public class ImageData {
    private int imageId = 0;

    // meta data
    private String path;
    private String text;

    /**
     * <p>The faces detected manually / by face detector. </p>
     * <p>
     * <p>format is (x, y, w, h):
     * <li>x, y: top corner</li>
     * <li>w: width of face</li>
     * <li>h:height of face</li>
     * </p>
     */
    private List<Integer[]> faces;

    // who
    private List<DisneyCharacterData> disneyCharacters;
    private List<FamilyMemberData> familyMembersData;

    // where
    private ERegionName regionName;
    private ERegionType regionType;
    private EAttractionName attractionName;

    // when
    private EPartOfDay partOfDay;
    private Date dateTime;

    // quality
    private AutomaticImageQuality automaticQuality;
    private EOrientation orientration;

    // What
    private boolean foodExist;
    private String[] artifacts;

}