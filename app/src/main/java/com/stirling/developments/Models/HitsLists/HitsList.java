package com.stirling.developments.Models.HitsLists;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.Sources.UsuarioSource;

import java.util.List;

/**
 * Created by User on 10/31/2017.
 */

@IgnoreExtraProperties
public class HitsList {

    @SerializedName("hits")
    @Expose
    private List<UsuarioSource> usuarioIndex;


    public List<UsuarioSource> getUsuarioIndex() {
        return usuarioIndex;
    }

    public void setUsuarioIndex(List<UsuarioSource> usuarioIndex) {
        this.usuarioIndex = usuarioIndex;
    }
}
