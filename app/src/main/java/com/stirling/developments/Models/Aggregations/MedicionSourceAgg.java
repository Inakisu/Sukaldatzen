package com.stirling.developments.Models.Aggregations;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.POJOs.Medicion;

@IgnoreExtraProperties
public class MedicionSourceAgg {

    @SerializedName("_source")
    @Expose
    private Medicion medicion;

    public Medicion getMedicion() { return medicion;}

    public void setMedicion(Medicion medicion) {this.medicion = medicion;}
}
