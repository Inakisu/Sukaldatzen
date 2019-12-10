package com.stirling.developments.Models.POJOs;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stirling.developments.Models.gson2pojo.Shards;

public class RespuestaU {

    @SerializedName("_index")
    @Expose
    private String index;
    @SerializedName("_type")
    @Expose
    private String type;
    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("_version")
    @Expose
    private Integer version;
    @SerializedName("result")
    @Expose
    private String result;
    @SerializedName("_shards")
    @Expose
    private Shards shards;
    @SerializedName("_seq_no")
    @Expose
    private Integer seqNo;
    @SerializedName("_primary_term")
    @Expose
    private Integer primaryTerm;

    /**
     * No args constructor for use in serialization
     *
     */
    public RespuestaU() {
    }

    /**
     *
     * @param result
     * @param shards
     * @param seqNo
     * @param primaryTerm
     * @param index
     * @param id
     * @param type
     * @param version
     */
    public RespuestaU(String index, String type, String id, Integer version, String result, Shards shards, Integer seqNo, Integer primaryTerm) {
        super();
        this.index = index;
        this.type = type;
        this.id = id;
        this.version = version;
        this.result = result;
        this.shards = shards;
        this.seqNo = seqNo;
        this.primaryTerm = primaryTerm;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public RespuestaU withIndex(String index) {
        this.index = index;
        return this;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public RespuestaU withType(String type) {
        this.type = type;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RespuestaU withId(String id) {
        this.id = id;
        return this;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public RespuestaU withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public RespuestaU withResult(String result) {
        this.result = result;
        return this;
    }

    public Shards getShards() {
        return shards;
    }

    public void setShards(Shards shards) {
        this.shards = shards;
    }

    public RespuestaU withShards(Shards shards) {
        this.shards = shards;
        return this;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public RespuestaU withSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
        return this;
    }

    public Integer getPrimaryTerm() {
        return primaryTerm;
    }

    public void setPrimaryTerm(Integer primaryTerm) {
        this.primaryTerm = primaryTerm;
    }

    public RespuestaU withPrimaryTerm(Integer primaryTerm) {
        this.primaryTerm = primaryTerm;
        return this;
    }

    @Override
    public String toString() {
        return "RespuestaU{" +
                "index='"+index+'\''+
                ", type='" + type + '\'' +
                ", id='" + id + '\''+
                ", version='" + version + '\''+
                ", result='" + result + '\''+
                ", shards='" + shards + '\''+
                ", seqNo='" + seqNo + '\''+
                ", primaryTerm='" + primaryTerm + '\''+
                '}';
    }

}