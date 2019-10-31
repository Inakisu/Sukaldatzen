package com.stirling.developments.Models.gson2pojo;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Hits_ {

    @SerializedName("total")
    @Expose
    private Total_ total;
    @SerializedName("max_score")
    @Expose
    private Object maxScore;
    @SerializedName("hits")
    @Expose
    private List<Hit_> hits = null;

    public Total_ getTotal() {
        return total;
    }

    public void setTotal(Total_ total) {
        this.total = total;
    }

    public Object getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(Object maxScore) {
        this.maxScore = maxScore;
    }

    public List<Hit_> getHits() {
        return hits;
    }

    public void setHits(List<Hit_> hits) {
        this.hits = hits;
    }

}
