package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.Delegate;

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
          "id": "exclude",
          "name": "Exclude"
        },
     */

    public StoryDependency(){

    }
    public StoryDependency(String type, String operator, int fromEventId, int toEventId){
        this.value=new Value();
        value.type=type;
        value.fromEventId=fromEventId;
        value.toEventId=toEventId;
        value.internalOperator=new Value.Operator();
        value.internalOperator.id=operator;
    }
    @JsonProperty("name")
    @Getter
    private String name;

    @JsonProperty("value")
    @Delegate
    private Value value;

    @Override
    public String toString() {
        if (value != null) {
            String operator = value.internalOperator != null ? value.internalOperator.id : null;
            return "StoryDependency(" +
                    "name=" + name + ", " +
                    "fromEventId=" + value.fromEventId +
                    ", toEventId=" + value.toEventId +
                    ", type=" + value.type +
                    ", operator=" + operator +
                    ")";
        }
        return "StoryDependency(" +
                "name=" + name + ", " +
                "fromEventId=null" +
                ", toEventId=null" +
                ", type=null" +
                ", operator=null" +
                ")";
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
