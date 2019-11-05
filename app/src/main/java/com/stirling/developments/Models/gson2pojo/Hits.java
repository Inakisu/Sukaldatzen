package com.stirling.developments.Models.gson2pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hits {

/*    @SerializedName("total")
    @Expose
    private Total total;

    @SerializedName("max_score")
    @Expose
    private double maxScore;*/

    @SerializedName("hits")
    @Expose
    private List<Hit> hits;

    public Hits() {
    }

    /**
     *
     * @param hits
     */
    public Hits(List<Hit> hits) {
        super();
        this.hits = hits;
    }

/*    public Total getTotal() {
        return total;
    }

    public void setTotal(Total total) {
        this.total = total;
    }

    public double getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Double maxScore) {
        this.maxScore = maxScore;
    }*/

    public List<Hit> getHits() {
        return hits;
    }

    public void setHits(List<Hit> hits) {
        this.hits = hits;
    }

}

