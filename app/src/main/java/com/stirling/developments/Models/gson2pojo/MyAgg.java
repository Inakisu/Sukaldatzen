package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyAgg {

    @SerializedName("hits")
    @Expose
    private Hits hits;

    /**
     * No args constructor for use in serialization
     *
     */
    public MyAgg() {
    }

    /**
     *
     * @param hits
     */
    public MyAgg(Hits hits) {
        super();
        this.hits = hits;
    }

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

    public MyAgg withHits(Hits hits) {
        this.hits = hits;
        return this;
    }

}
