package idc.storyalbum.matcher.tools.annotation_converter.old;

import lombok.Data;

@Data
public class AutomaticImageQuality {
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();

        String sep=System.lineSeparator();
        retVal.append("\tover Exposed penalty: " + overExposedPenalty);
        retVal.append(sep);
        retVal.append("\tunder Exposed penalty: " + underExposedPenalty);
        retVal.append(sep);
        retVal.append("\tbluriness level penalty: " + blurinessLevelPenalty);
        retVal.append(sep);

        return retVal.toString();
    }

    private double overExposedPenalty;
    private double underExposedPenalty;
    private double blurinessLevelPenalty;

}
