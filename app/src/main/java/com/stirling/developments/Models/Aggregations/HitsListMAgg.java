package com.stirling.developments.Models.Aggregations;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@IgnoreExtraProperties
public class HitsListMAgg {

    @SerializedName("hits")
    @Expose
    private List<MedicionSourceAgg> medicionIndex;

    public List<MedicionSourceAgg> getMedicionIndex(){return medicionIndex;}

    public void setMedicionIndex(List<MedicionSourceAgg> medicionIndex){
        this.medicionIndex = medicionIndex;
    }
}

//NOTA: Cambio de estructura
//¿Debería existir esta clase? ¿No debería ser una lista declarada dentro de la clase HitsSubhits?