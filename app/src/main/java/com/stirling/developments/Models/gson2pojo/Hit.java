package com.stirling.developments.Models.gson2pojo;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Hit {

/*    @SerializedName("_index")
    @Expose
    private String index;

    @SerializedName("_type")
    @Expose
    private String type;

    @SerializedName("_id")
    @Expose
    private String id;

    @SerializedName("_score")
    @Expose
    private double score;*/

    @SerializedName("_source")
    @Expose
    private Source source;

    /*@SerializedName("sort")
    @Expose //esto sobraría, se ignoranlos campos que no están declarados en la clase
    private List<Integer> sort;*/

    /**
     * No args constructor for use in serialization
     *
     */
    public Hit() {
    }

    /**
     *
     * @param source
     */
    public Hit(Source source) {
        super();
        this.source = source;
    }

/*    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }*/

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

/*    public List<Integer> getSort() {return sort;}

    public void setSort(List<Integer> sort) { this.sort = sort; }*/

}
