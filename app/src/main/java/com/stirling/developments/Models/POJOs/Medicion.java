package com.stirling.developments.Models.POJOs;

public class Medicion {

    private String idMac;
    private String medicionFechaInicio;
    private String medicionFechaFin;
    private String timestamp;
    private float tempsInt; //eran float, los pongo en int para probar
    private float tempsTapa; // """"

    public Medicion(){

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

    public float getTempsInt() {
        return tempsInt;
    }

    public void setTempsInt(float tempsInt) {
        this.tempsInt = tempsInt;
    }

    public float getTempsTapa() {
        return tempsTapa;
    }

    public void setTempsTapa(float tempsTapa) {
        this.tempsTapa = tempsTapa;
    }
}
