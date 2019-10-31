package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyAgg {

    @SerializedName("hits")
    @Expose
    private Hits_ hits;

    public Hits_ getHits() {
        return hits;
    }

    public void setHits(Hits_ hits) {
        this.hits = hits;
    }

}
