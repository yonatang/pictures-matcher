package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Delegate;

import java.text.MessageFormat;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StoryDependency {
    /*
     "name": "who",
      "value": {
        "fromEventId": 1,
        "toEventId": 2,
        "type": "who",
        "operator": {
          "id": "includeN",
          "name": "Exclude"
        },
        extraN: 2
     */

    public StoryDependency() {

    }

    public StoryDependency(String type, String operator, int fromEventId, int toEventId) {
        this(type, operator, fromEventId, toEventId, null);
    }

    public StoryDependency(String type, String operator, int fromEventId, int toEventId, Integer extraN) {
        this.value = new Value();
        value.type = type;
        value.fromEventId = fromEventId;
        value.toEventId = toEventId;
        value.internalOperator = new Value.Operator();
        value.internalOperator.id = operator;
        value.extraN = extraN;
    }

    @JsonProperty("name")
    @Getter
    private String name;

    @JsonProperty("value")
    @Delegate
    private Value value;

    private static final MessageFormat TO_STRING_FORMAT = new MessageFormat("Dependency {0} {1}->{2}: {3} {4}");
    private static final MessageFormat TO_STRING_EX_N_FORMAT = new MessageFormat("Dependency {0} {1}->{2}: {3} {4}({5})");

    @Override
    public String toString() {
        if (value != null) {
            if (value.extraN!=null) {
                return TO_STRING_EX_N_FORMAT.format(new Object[]{
                        getName(),
                        getFromEventId(),
                        getToEventId(),
                        getType(),
                        getOperator(),
                        getExtraN()
                });
            }
            return TO_STRING_FORMAT.format(new Object[]{
                    getName(),
                    getFromEventId(),
                    getToEventId(),
                    getType(),
                    getOperator()
            });
        }
        return TO_STRING_FORMAT.format(new Object[]{
                getName(),
                null,
                null,
                null,
                null
        });
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Value {
        @JsonProperty("fromEventId")
        @Getter
        private int fromEventId;
        @JsonProperty("toEventId")
        @Getter
        private int toEventId;
        @JsonProperty("type")
        @Getter
        private String type;

        @JsonProperty("extraN")
        @Getter
        private Integer extraN;

        public String getOperator() {
            if (internalOperator == null) {
                return null;
            }
            return internalOperator.id;
        }

        @JsonProperty("operator")
        private Operator internalOperator;

        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Operator {
            @JsonProperty("id")
            private String id;
        }

    }

}
