package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.Delegate;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryEvent {

    @JsonProperty("value")
    @Delegate
    private Value value;

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

    @Override
    public String toString() {
        if (value != null) {
            return "StoryDependency(" +
                    "id=" + value.id +
                    ", name=" + value.name +
                    ", constraints=" + value.constraints +
                    ")";
        }
        return "StoryDependency(" +
                "id=null" +
                ", name=null" +
                ", constraints=null" +
                ")";
    }

}
