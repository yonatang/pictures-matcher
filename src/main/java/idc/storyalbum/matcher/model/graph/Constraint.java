package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Constraint {

    public Constraint() {
    }

    private static final MessageFormat TO_STRING_FORMAT = new MessageFormat("{0} constraint: {1} {2} {3}: {4}");

    public String toString() {
        return TO_STRING_FORMAT.format(new Object[]{
                isSoft() ? "Soft" : "Hard",
                getType(),
                getOperator(),
                getExtraN() != null ? getExtraN() : "",
                getValues()});
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

    public void setOperator(Object operator) {
        if (operator instanceof String) {
            this.operator = new Operator();
            this.operator.id = (String) operator;
        } else if (operator instanceof Map) {
            this.operator = new Operator();
            this.operator.id = (String) ((Map) operator).get("id");
        } else {
            this.operator = (Operator) operator;
        }
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
