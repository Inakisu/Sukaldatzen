package com.stirling.developments.Models.gson2pojo;

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
        "_index",
        "_type",
        "_id",
        "_score",
        "_source",
        "sort"
})
public class Hit_ {

    @JsonProperty("_index")
    private String index;
    @JsonProperty("_type")
    private String type;
    @JsonProperty("_id")
    private String id;
    @JsonProperty("_score")
    private Object score;
    @JsonProperty("_source")
    private Source_ source;
    @JsonProperty("sort")
    private List<Integer> sort = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("_index")
    public String getIndex() {
        return index;
    }

    @JsonProperty("_index")
    public void setIndex(String index) {
        this.index = index;
    }

    @JsonProperty("_type")
    public String getType() {
        return type;
    }

    @JsonProperty("_type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("_score")
    public Object getScore() {
        return score;
    }

    @JsonProperty("_score")
    public void setScore(Object score) {
        this.score = score;
    }

    @JsonProperty("_source")
    public Source_ getSource() {
        return source;
    }

    @JsonProperty("_source")
    public void setSource(Source_ source) {
        this.source = source;
    }

    @JsonProperty("sort")
    public List<Integer> getSort() {
        return sort;
    }

    @JsonProperty("sort")
    public void setSort(List<Integer> sort) {
        this.sort = sort;
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
