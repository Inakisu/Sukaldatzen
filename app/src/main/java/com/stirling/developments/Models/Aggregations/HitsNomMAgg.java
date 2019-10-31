package com.stirling.developments.Models.Aggregations;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@IgnoreExtraProperties
public class HitsNomMAgg {

    @SerializedName("my_agg")
    @Expose
    private HitsSubhitMAgg hitsSubhitMAgg;

    public HitsSubhitMAgg getHits(){ return hitsSubhitMAgg;}

    public void setHits(HitsSubhitMAgg hitsSubhitMAgg){
        this.hitsSubhitMAgg = hitsSubhitMAgg;
    }
}
