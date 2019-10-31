package com.stirling.developments.Models.HitsLists;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.Sources.NotificacionSource;

import java.util.List;

@IgnoreExtraProperties
public class HitsListN {

    @SerializedName("_source")
    @Expose
    private List<NotificacionSource> notificacionIndex;

    public List<NotificacionSource> getNotificacionIndex() { return notificacionIndex;}

    public void setNotificacionIndex (List<NotificacionSource> notificacionIndex){
        this.notificacionIndex = notificacionIndex;
    }
}
