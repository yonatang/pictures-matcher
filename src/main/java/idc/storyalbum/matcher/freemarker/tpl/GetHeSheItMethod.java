package idc.storyalbum.matcher.freemarker.tpl;

import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import idc.storyalbum.matcher.freemarker.context.Character;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Created by yonatan on 26/4/2015.
 */
public class GetHeSheItMethod implements TemplateMethodModelEx {
    private final ObjectWrapper objectWrapper;

    public GetHeSheItMethod(Configuration configuration) {
        this.objectWrapper = configuration.getObjectWrapper();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 1) {
            throw new TemplateModelException("getCharactersByGroup needs single arguments - character");
        }
        try {
            Character character = (Character) DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
            return objectWrapper.wrap(getHeSheIt(character));
        } catch (ClassCastException e) {
            throw new TemplateModelException("Error: Expecting Page and String arguments");
        }
    }

    private String getHeSheIt(Character character) {
        if (StringUtils.equalsIgnoreCase(character.getGender(), "male")) {
            return "he";
        }
        if (StringUtils.equalsIgnoreCase(character.getGender(), "female")) {
            return "she";
        }
        return "it";
    }
}
