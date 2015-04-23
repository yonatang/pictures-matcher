package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 18/4/2015.
 */
@ToString(exclude = {"operator", "internalValues"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Constraint {

    public Constraint() {
    }

    public Constraint(String type, String operator, boolean soft, Integer extraN, String... values) {
        this.type = type;
        this.operator = new Operator();
        this.operator.id = operator;
        this.internalValues = new HashSet<>();
        for (String value : values) {
            Value v = new Value();
            v.id = value;
            internalValues.add(v);
        }
        this.extraN = extraN;
        this.soft = soft;
    }

    public Constraint(String type, String operator, boolean soft, String... values) {
        this(type, operator, soft, null, values);
    }

    @JsonProperty("type")
    @Getter
    private String type;

    @Getter(lazy = true)
    private final Set<String> values = calculateValues();

    @JsonProperty("soft")
    @Getter
    private boolean soft;

    @JsonProperty("extraN")
    @Getter
    /**
     * Extra N value required for some of the operators
     */
    private Integer extraN;

    @JsonProperty("operator")
    private Operator operator;

    @JsonProperty("value")
    private Set<Value> internalValues;

    public String getOperator() {
        if (operator == null) {
            return null;
        }
        return operator.getId();
    }

    private Set<String> calculateValues() {
        if (internalValues == null) {
            return new HashSet<>();
        }
        return internalValues.stream().map(Value::getId).collect(Collectors.toSet());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Operator {
        @Getter
        private String id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Value {
        @Getter
        private String id;
    }
}
