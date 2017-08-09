package com.app.qootho.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableDriver {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("raiderID")
    @Expose
    private Integer raiderID;
    @SerializedName("pointLat")
    @Expose
    private Double pointLat;
    @SerializedName("pointLong")
    @Expose
    private Double pointLong;
    @SerializedName("logTime")
    @Expose
    private String logTime;
    @SerializedName("location")
    @Expose
    private Object location;
    @SerializedName("raider")
    @Expose
    private Object raider;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRaiderID() {
        return raiderID;
    }

    public void setRaiderID(Integer raiderID) {
        this.raiderID = raiderID;
    }

    public Double getPointLat() {
        return pointLat;
    }

    public void setPointLat(Double pointLat) {
        this.pointLat = pointLat;
    }

    public Double getPointLong() {
        return pointLong;
    }

    public void setPointLong(Double pointLong) {
        this.pointLong = pointLong;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public Object getLocation() {
        return location;
    }

    public void setLocation(Object location) {
        this.location = location;
    }

    public Object getRaider() {
        return raider;
    }

    public void setRaider(Object raider) {
        this.raider = raider;
    }

}

