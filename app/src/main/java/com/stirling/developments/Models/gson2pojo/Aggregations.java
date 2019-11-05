package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Aggregations {

    @SerializedName("myAgg")
    @Expose
    private MyAgg myAgg;

    /**
     * No args constructor for use in serialization
     *
     */
    public Aggregations() {
    }

    /**
     *
     * @param myAgg
     */
    public Aggregations(MyAgg myAgg) {
        super();
        this.myAgg = myAgg;
    }

    public MyAgg getMyAgg() {
        return myAgg;
    }

    public void setMyAgg(MyAgg myAgg) {
        this.myAgg = myAgg;
    }

    public Aggregations withMyAgg(MyAgg myAgg) {
        this.myAgg = myAgg;
        return this;
    }

}
