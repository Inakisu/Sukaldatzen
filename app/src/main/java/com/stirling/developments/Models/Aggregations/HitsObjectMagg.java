package com.stirling.developments.Models.Aggregations;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@IgnoreExtraProperties
public class HitsObjectMagg {

    @SerializedName("aggregations")
    @Expose
    private HitsNomMAgg hitsNomMAgg;

    public HitsNomMAgg getHits() { return hitsNomMAgg;}

    public void setHits(HitsNomMAgg hitsNomMAgg) {this.hitsNomMAgg = hitsNomMAgg;}
}
