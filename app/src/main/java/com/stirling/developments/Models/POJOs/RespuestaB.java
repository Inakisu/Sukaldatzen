package com.stirling.developments.Models.POJOs;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespuestaB {

    @SerializedName("took")
    @Expose
    private Integer took;
    @SerializedName("timed_out")
    @Expose
    private Boolean timedOut;
    @SerializedName("total")
    @Expose
    private Integer total;
    @SerializedName("deleted")
    @Expose
    private Integer deleted;
    @SerializedName("batches")
    @Expose
    private Integer batches;
    @SerializedName("version_conflicts")
    @Expose
    private Integer versionConflicts;
    @SerializedName("noops")
    @Expose
    private Integer noops;
    @SerializedName("retries")
    @Expose
    private Retries retries;
    @SerializedName("throttled_millis")
    @Expose
    private Integer throttledMillis;
    @SerializedName("requests_per_second")
    @Expose
    private Double requestsPerSecond;
    @SerializedName("throttled_until_millis")
    @Expose
    private Integer throttledUntilMillis;
    @SerializedName("failures")
    @Expose
    private List<Object> failures = null;

    public Integer getTook() {
        return took;
    }

    public void setTook(Integer took) {
        this.took = took;
    }

    public Boolean getTimedOut() {
        return timedOut;
    }

    public void setTimedOut(Boolean timedOut) {
        this.timedOut = timedOut;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }

    public Integer getBatches() {
        return batches;
    }

    public void setBatches(Integer batches) {
        this.batches = batches;
    }

    public Integer getVersionConflicts() {
        return versionConflicts;
    }

    public void setVersionConflicts(Integer versionConflicts) {
        this.versionConflicts = versionConflicts;
    }

    public Integer getNoops() {
        return noops;
    }

    public void setNoops(Integer noops) {
        this.noops = noops;
    }

    public Retries getRetries() {
        return retries;
    }

    public void setRetries(Retries retries) {
        this.retries = retries;
    }

    public Integer getThrottledMillis() {
        return throttledMillis;
    }

    public void setThrottledMillis(Integer throttledMillis) {
        this.throttledMillis = throttledMillis;
    }

    public Double getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public void setRequestsPerSecond(Double requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
    }

    public Integer getThrottledUntilMillis() {
        return throttledUntilMillis;
    }

    public void setThrottledUntilMillis(Integer throttledUntilMillis) {
        this.throttledUntilMillis = throttledUntilMillis;
    }

    public List<Object> getFailures() {
        return failures;
    }

    public void setFailures(List<Object> failures) {
        this.failures = failures;
    }

}