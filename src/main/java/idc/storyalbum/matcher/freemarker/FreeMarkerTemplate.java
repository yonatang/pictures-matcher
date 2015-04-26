package idc.storyalbum.matcher.freemarker;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import idc.storyalbum.matcher.freemarker.tpl.GetCharactersByGroupMethod;
import idc.storyalbum.matcher.freemarker.tpl.GetHeSheItMethod;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by yonatan on 26/4/2015.
 */
@Service
public class FreeMarkerTemplate {
    private Configuration cfg;

    @PostConstruct
    void initFreeMarker() {
        cfg = new Configuration(new Version(2, 3, 22));
        cfg.setSharedVariable("charactersByGroup",
                new GetCharactersByGroupMethod(cfg));
        cfg.setSharedVariable("heSheIt", new GetHeSheItMethod(cfg));
    }

    @SneakyThrows(IOException.class)
    public String process(String template, Object model) throws TemplateException {
        try (StringWriter writer = new StringWriter()) {
            Template t = new Template("temporaryTemplate", template, cfg);
            t.process(model, writer);
            return writer.toString();
        }
    }
}
