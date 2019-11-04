package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Source {

    @SerializedName("idMac")
    @Expose
    private String idMac;

    @SerializedName("medicionFechaInicio")
    @Expose
    private String medicionFechaInicio;

    @SerializedName("medicionFechaFin")
    @Expose
    private String medicionFechaFin;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("tempsInt")
    @Expose
    private int tempsInt;

    @SerializedName("tempsTapa")
    @Expose
    private int tempsTapa;

    public String getIdMac() {
        return idMac;
    }

    public void setIdMac(String idMac) {
        this.idMac = idMac;
    }

    public String getMedicionFechaInicio() {
        return medicionFechaInicio;
    }

    public void setMedicionFechaInicio(String medicionFechaInicio) {
        this.medicionFechaInicio = medicionFechaInicio;
    }

    public String getMedicionFechaFin() {
        return medicionFechaFin;
    }

    public void setMedicionFechaFin(String medicionFechaFin) {
        this.medicionFechaFin = medicionFechaFin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getTempsInt() {
        return tempsInt;
    }

    public void setTempsInt(int tempsInt) {
        this.tempsInt = tempsInt;
    }

    public int getTempsTapa() {
        return tempsTapa;
    }

    public void setTempsTapa(int tempsTapa) {
        this.tempsTapa = tempsTapa;
    }

}
