package com.stirling.developments.Models.jackson;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "idMac",
        "medicionFechaInicio",
        "medicionFechaFin",
        "timestamp",
        "tempsInt",
        "tempsTapa"
})
public class Source {

    @JsonProperty("idMac")
    private String idMac;
    @JsonProperty("medicionFechaInicio")
    private String medicionFechaInicio;
    @JsonProperty("medicionFechaFin")
    private String medicionFechaFin;
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("tempsInt")
    private Integer tempsInt;
    @JsonProperty("tempsTapa")
    private Integer tempsTapa;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("idMac")
    public String getIdMac() {
        return idMac;
    }

    @JsonProperty("idMac")
    public void setIdMac(String idMac) {
        this.idMac = idMac;
    }

    @JsonProperty("medicionFechaInicio")
    public String getMedicionFechaInicio() {
        return medicionFechaInicio;
    }

    @JsonProperty("medicionFechaInicio")
    public void setMedicionFechaInicio(String medicionFechaInicio) {
        this.medicionFechaInicio = medicionFechaInicio;
    }

    @JsonProperty("medicionFechaFin")
    public String getMedicionFechaFin() {
        return medicionFechaFin;
    }

    @JsonProperty("medicionFechaFin")
    public void setMedicionFechaFin(String medicionFechaFin) {
        this.medicionFechaFin = medicionFechaFin;
    }

    @JsonProperty("timestamp")
    public String getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("tempsInt")
    public Integer getTempsInt() {
        return tempsInt;
    }

    @JsonProperty("tempsInt")
    public void setTempsInt(Integer tempsInt) {
        this.tempsInt = tempsInt;
    }

    @JsonProperty("tempsTapa")
    public Integer getTempsTapa() {
        return tempsTapa;
    }

    @JsonProperty("tempsTapa")
    public void setTempsTapa(Integer tempsTapa) {
        this.tempsTapa = tempsTapa;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
