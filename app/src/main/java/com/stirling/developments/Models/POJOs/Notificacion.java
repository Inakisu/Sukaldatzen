package com.stirling.developments.Models.POJOs;

public class Notificacion {

    private String idMac;
    private String medicionFechaInicio;
    private String correousu;
    private int tipo;
    private String mensaje;
    private float tempInt;
    private float tempTapa;

    public Notificacion(){

    }

    public String toString(){
        return "Notificacion{" +
                "idMac='"+idMac+'\''+
                ", medicionFechaInicio='" + medicionFechaInicio + '\'' +
                ", correousu='" + correousu + '\''+
                ", tipo='" + tipo + '\''+
                ", mensaje='" + mensaje + '\''+
                ", tempInt='" + tempInt + '\''+
                ", tempTapa='" + tempTapa + '\''+
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

    public String getCorreousu() {
        return correousu;
    }

    public void setCorreousu(String correousu) {
        this.correousu = correousu;
    }

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public float getTempInt() {
        return tempInt;
    }

    public void setTempInt(float tempInt) {
        this.tempInt = tempInt;
    }

    public float getTempTapa() {
        return tempTapa;
    }

    public void setTempTapa(float tempTapa) {
        this.tempTapa = tempTapa;
    }
}
