package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyAgg {

    @SerializedName("hits")
    @Expose
    private Hits hits;

    public Hits getHits() {
        return hits;
    }

    public void setHits(Hits hits) {
        this.hits = hits;
    }

}
