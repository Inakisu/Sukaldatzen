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
    private Integer tempsInt;
    @SerializedName("tempsTapa")
    @Expose
    private Integer tempsTapa;

    /**
     * No args constructor for use in serialization
     *
     */
    public Source() {
    }

    /**
     *
     * @param medicionFechaInicio
     * @param idMac
     * @param tempsTapa
     * @param medicionFechaFin
     * @param tempsInt
     * @param timestamp
     */
    public Source(String idMac, String medicionFechaInicio, String medicionFechaFin, String timestamp, Integer tempsInt, Integer tempsTapa) {
        super();
        this.idMac = idMac;
        this.medicionFechaInicio = medicionFechaInicio;
        this.medicionFechaFin = medicionFechaFin;
        this.timestamp = timestamp;
        this.tempsInt = tempsInt;
        this.tempsTapa = tempsTapa;
    }
    public String toString(){
        return "Medicion{" +
                "idMac='"+idMac+'\''+
                ", medicionFechaInicio='" + medicionFechaInicio + '\'' +
                ", medicionFechaFin='" + medicionFechaFin + '\''+
                ", timestamp='" + timestamp + '\''+
                ", tempsInt='" + tempsInt + '\''+
                ", tempsTapa='" + tempsTapa + '\''+
                '}';
    }

    public String getIdMac() {
        return idMac;
    }

    public void setIdMac(String idMac) {
        this.idMac = idMac;
    }

    public Source withIdMac(String idMac) {
        this.idMac = idMac;
        return this;
    }

    public String getMedicionFechaInicio() {
        return medicionFechaInicio;
    }

    public void setMedicionFechaInicio(String medicionFechaInicio) {
        this.medicionFechaInicio = medicionFechaInicio;
    }

    public Source withMedicionFechaInicio(String medicionFechaInicio) {
        this.medicionFechaInicio = medicionFechaInicio;
        return this;
    }

    public String getMedicionFechaFin() {
        return medicionFechaFin;
    }

    public void setMedicionFechaFin(String medicionFechaFin) {
        this.medicionFechaFin = medicionFechaFin;
    }

    public Source withMedicionFechaFin(String medicionFechaFin) {
        this.medicionFechaFin = medicionFechaFin;
        return this;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Source withTimestamp(String timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Integer getTempsInt() {
        return tempsInt;
    }

    public void setTempsInt(Integer tempsInt) {
        this.tempsInt = tempsInt;
    }

    public Source withTempsInt(Integer tempsInt) {
        this.tempsInt = tempsInt;
        return this;
    }

    public Integer getTempsTapa() {
        return tempsTapa;
    }

    public void setTempsTapa(Integer tempsTapa) {
        this.tempsTapa = tempsTapa;
    }

    public Source withTempsTapa(Integer tempsTapa) {
        this.tempsTapa = tempsTapa;
        return this;
    }

}