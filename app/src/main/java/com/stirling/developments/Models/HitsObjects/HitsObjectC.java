package com.stirling.developments.Models.HitsObjects;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.HitsLists.HitsListC;

@IgnoreExtraProperties
public class HitsObjectC {

    @SerializedName("hits")
    @Expose
    private HitsListC hits;

    public HitsListC getHits(){ return hits;}

    public void setHits(HitsListC hits){ this.hits = hits;}
}
