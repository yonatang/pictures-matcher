package idc.storyalbum.matcher.freemarker.tpl;

import freemarker.template.*;
import freemarker.template.utility.DeepUnwrap;
import idc.storyalbum.matcher.freemarker.context.Page;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 26/4/2015.
 */
public class GetCharactersByGroupMethod implements TemplateMethodModelEx {
    private final ObjectWrapper objectWrapper;

    public GetCharactersByGroupMethod(Configuration configuration) {
        this.objectWrapper = configuration.getObjectWrapper();
    }

    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments.size() != 2) {
            throw new TemplateModelException("getCharactersByGroup needs two arguments - page and group");
        }
        try {
            Page page = (Page) DeepUnwrap.unwrap((TemplateModel) arguments.get(0));
            TemplateScalarModel groupScalar = (TemplateScalarModel) arguments.get(1);
            return objectWrapper.wrap(getCharactersByGroup(page, groupScalar.getAsString()));
        } catch (ClassCastException e) {
            throw new TemplateModelException("Error: Expecting Page and String arguments");
        }
    }

    private List<idc.storyalbum.matcher.freemarker.context.Character>
    getCharactersByGroup(Page page, String group) {
        return page.getCharacters().stream()
                .filter((x) -> x.getGroups().contains(group))
                .collect(Collectors.toList());
    }

}
