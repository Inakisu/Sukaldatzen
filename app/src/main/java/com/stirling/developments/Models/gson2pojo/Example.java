package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Example {

    @SerializedName("aggregations")
    @Expose
    private Aggregations aggregations;

    /**
     * No args constructor for use in serialization
     *
     */
    public Example() {
    }

    /**
     *
     * @param aggregations
     */
    public Example(Aggregations aggregations) {
        super();
        this.aggregations = aggregations;
    }

    public Aggregations getAggregations() {
        return aggregations;
    }

    public void setAggregations(Aggregations aggregations) {
        this.aggregations = aggregations;
    }

    public Example withAggregations(Aggregations aggregations) {
        this.aggregations = aggregations;
        return this;
    }

}