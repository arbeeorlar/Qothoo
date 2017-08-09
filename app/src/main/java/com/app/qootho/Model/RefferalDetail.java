package com.app.qootho.Model;

/**
 * Created by macbookpro on 16/07/2017.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RefferalDetail {

    @SerializedName("refferalTypeName")
    @Expose
    private String refferalTypeName;
    @SerializedName("refferalCount")
    @Expose
    private Integer refferalCount;
    @SerializedName("accumulatedCash")
    @Expose
    private Double accumulatedCash;
    @SerializedName("refferalTypeID")
    @Expose
    private Integer refferalTypeID;
    @SerializedName("id")
    @Expose
    private Integer id;

    public String getRefferalTypeName() {
        return refferalTypeName;
    }

    public void setRefferalTypeName(String refferalTypeName) {
        this.refferalTypeName = refferalTypeName;
    }

    public Integer getRefferalCount() {
        return refferalCount;
    }

    public void setRefferalCount(Integer refferalCount) {
        this.refferalCount = refferalCount;
    }

    public Double getAccumulatedCash() {
        return accumulatedCash;
    }

    public void setAccumulatedCash(Double accumulatedCash) {
        this.accumulatedCash = accumulatedCash;
    }

    public Integer getRefferalTypeID() {
        return refferalTypeID;
    }

    public void setRefferalTypeID(Integer refferalTypeID) {
        this.refferalTypeID = refferalTypeID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

}
