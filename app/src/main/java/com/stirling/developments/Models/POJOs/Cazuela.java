package com.stirling.developments.Models.POJOs;

public class Cazuela {

    private String idMac;
    private String nombreCazuela;
    private String correousu;
    private boolean dueno;

    public Cazuela(){

    }

    public String toString(){
        return "Cazuela{" +
                "idMac='"+idMac+'\''+
                ", nombreCazuela='" + nombreCazuela + '\'' +
                ", correousu='" + correousu + '\''+
                ", dueno='" + dueno + '\''+       //OJO aquí, que esto es un booleano
                '}';
    }

    public String getIdMac() {
        return idMac;
    }

    public void setIdMac(String idMac) {
        this.idMac = idMac;
    }

    public String getNombreCazuela() {
        return nombreCazuela;
    }

    public void setNombreCazuela(String nombreCazuela) {
        this.nombreCazuela = nombreCazuela;
    }

    public String getCorreousu() {
        return correousu;
    }

    public void setCorreousu(String correousu) {
        this.correousu = correousu;
    }

    public boolean isDueno() {
        return dueno;
    }

    public void setDueno(boolean dueno) {
        this.dueno = dueno;
    }
}
