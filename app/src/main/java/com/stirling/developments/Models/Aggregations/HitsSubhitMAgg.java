package com.stirling.developments.Models.Aggregations;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@IgnoreExtraProperties
public class HitsSubhitMAgg {

    @SerializedName("hits")
    @Expose
    private HitsListMAgg hitsListMAgg;

    public HitsListMAgg getHits(){return hitsListMAgg;}

    public void setHits(HitsListMAgg hitsListMAgg){this.hitsListMAgg = hitsListMAgg; }
}


