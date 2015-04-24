package idc.storyalbum.matcher.tools.annotation_converter.old;

import lombok.Data;

/**
 * This data structure holds everything needed to be known on Disney character,
 * including its type, location in the image and face direction
 *
 * @author ozrad
 */
@Data
public class DisneyCharacterData {

    @Override
    public String toString() {
        return String.format("%s placed at %s,  face directing %s", disneyCharacter, locationAtImage, faceDirection);
    }

    private EFaceDirection faceDirection;
    private ELocationAtImage locationAtImage;
    private EDisneyCharacter disneyCharacter;

}
