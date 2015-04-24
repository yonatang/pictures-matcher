package idc.storyalbum.matcher.tools.annotation_converter.old;

import lombok.Data;

@Data
public class FamilyMemberData {
    private int ageRangeStart;
    private int ageRangeEnd;
    private EGender gender;
    private EFaceDirection faceDirection;
    private ELocationAtImage locationAtImage;

    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        retVal.append("\tGender: " + getGender());
        retVal.append(System.lineSeparator());
        retVal.append("\tAge Range: " + getAgeRangeStart() + " - " + getAgeRangeEnd());
        retVal.append(System.lineSeparator());

        return retVal.toString();
    }
}
