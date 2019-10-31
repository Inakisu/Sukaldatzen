package com.stirling.developments.Models.HitsLists;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.Sources.CazuelaSource;

import java.util.List;

@IgnoreExtraProperties
public class HitsListC {

    @SerializedName("hits")
    @Expose

    private List<CazuelaSource> cazuelaIndex;

    public List<CazuelaSource> getCazuelaIndex(){ return cazuelaIndex;}

    public void setCazuelaIndex (List<CazuelaSource> cazuelaIndex) {
        this.cazuelaIndex = cazuelaIndex;
    }
}
