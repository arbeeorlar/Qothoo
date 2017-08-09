package com.app.qootho.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RiderType {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("rideTypeName")
    @Expose
    private String rideTypeName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRideTypeName() {
        return rideTypeName;
    }

    public void setRideTypeName(String rideTypeName) {
        this.rideTypeName = rideTypeName;
    }

}
