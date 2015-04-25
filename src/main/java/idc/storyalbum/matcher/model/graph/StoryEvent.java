package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryEvent {

    @JsonProperty("value")
    @Delegate
    private Value value=new Value();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Value {
        @JsonProperty("id")
        @Getter
        private int id;

        @JsonProperty("name")
        @Getter
        private String name;

        @JsonProperty("constraints")
        @Getter
        private Set<Constraint> constraints = new HashSet<>();
    }

    private static final MessageFormat TO_STRING_FORMAT = new MessageFormat("Event {0}:{1}");

    @Override
    public String toString() {
        if (value != null) {
            return TO_STRING_FORMAT.format(new Object[]{
                    value.id, value.name
            });
        }
        return TO_STRING_FORMAT.format(new Object[]{
                null, null
        });
    }

}
