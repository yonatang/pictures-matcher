package idc.storyalbum.matcher.model.graph;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yonatan on 18/4/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class StoryGraph {
    @Getter(lazy = true)
    final private Set<StoryEvent> events = calcEvents();

    @JsonProperty("edges")
    @Getter(lazy = true)
    final private Set<StoryDependency> dependencies = calcDeps();

    @JsonProperty("profile")
    @Getter
    private Object profile;

    @JsonProperty("graph")
    private Graph internalGraph;

    private Set<StoryEvent> calcEvents() {
        return internalGraph != null ? internalGraph.nodes : new HashSet<>();
    }

    private Set<StoryDependency> calcDeps() {
        return internalGraph != null ? internalGraph.edges : new HashSet<>();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Graph {
        @JsonProperty("nodes")
        private Set<StoryEvent> nodes = new HashSet<>();

        @JsonProperty("edges")
        private Set<StoryDependency> edges = new HashSet<>();
    }


}
