package com.stirling.developments.Models.Sources;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.POJOs.Cazuela;

@IgnoreExtraProperties
public class CazuelaSource {

    @SerializedName("_source")
    @Expose
    private Cazuela cazuela;

    public Cazuela getCazuela(){ return cazuela;}

    public void setCazuela(Cazuela cazuela){ this.cazuela = cazuela;}

}
