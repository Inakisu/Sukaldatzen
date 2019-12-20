package com.stirling.developments.Models.POJOs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Retries {

    @SerializedName("bulk")
    @Expose
    private Integer bulk;
    @SerializedName("search")
    @Expose
    private Integer search;

    public Integer getBulk() {
        return bulk;
    }

    public void setBulk(Integer bulk) {
        this.bulk = bulk;
    }

    public Integer getSearch() {
        return search;
    }

    public void setSearch(Integer search) {
        this.search = search;
    }

}