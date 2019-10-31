package com.stirling.developments.Models.POJOs;


public class Usuario {

    private String correousu;
    private int edad;
    private String primUbic;
    private int discapaz;


    public Usuario() {
    }

    public String getCorreousu() {return correousu;  }

    public void setCorreousu(String correousu) {        this.correousu = correousu;    }

    public int getEdad() {        return edad;    }

    public void setEdad(int edad) {        this.edad = edad;    }

    public String getPrimUbic() {        return primUbic;    }

    public void setPrimUbic(String primUbic) {        this.primUbic = primUbic;    }

    public int getDiscapaz() {        return discapaz;    }

    public void setDiscapaz(int discapaz) {        this.discapaz = discapaz;    }

    public Usuario (String correousu, int edad, String primUbic, int discapaz){
        this.correousu = correousu;
        this.discapaz = discapaz;
        this.edad = edad;
        this.primUbic = primUbic;
    }
    @Override
    public String toString(){
        return "Usuario{" +
                "correousu='" + correousu + '\'' +
                ", edad='" + edad + '\'' +
                ", primUbic='" + primUbic + '\'' +
                ", discapaz='" + discapaz + '\'' +
                '}';
    }
}
