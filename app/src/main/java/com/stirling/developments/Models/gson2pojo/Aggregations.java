package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aggregations {

    @SerializedName("my_agg")
    @Expose
    private MyAgg myAgg;

    public MyAgg getMyAgg() {
        return myAgg;
    }

    public void setMyAgg(MyAgg myAgg) {
        this.myAgg = myAgg;
    }

}
