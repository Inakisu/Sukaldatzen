package com.stirling.developments.Models.jackson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "total",
        "max_score",
        "hits"
})
public class Hits_ {

    @JsonProperty("total")
    private Total_ total;
    @JsonProperty("max_score")
    private Object maxScore;
    @JsonProperty("hits")
    private List<Hit_> hits = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("total")
    public Total_ getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Total_ total) {
        this.total = total;
    }

    @JsonProperty("max_score")
    public Object getMaxScore() {
        return maxScore;
    }

    @JsonProperty("max_score")
    public void setMaxScore(Object maxScore) {
        this.maxScore = maxScore;
    }

    @JsonProperty("hits")
    public List<Hit_> getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(List<Hit_> hits) {
        this.hits = hits;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
