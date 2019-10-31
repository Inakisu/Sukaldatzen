package com.stirling.developments.Models.HitsObjects;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.HitsLists.HitsListM;

@IgnoreExtraProperties
public class HitsObjectM {

    @SerializedName("hits")
    @Expose
    private HitsListM hits;

    public HitsListM getHits() { return hits;}

    public void setHits(HitsListM hits){ this.hits = hits;}

}
