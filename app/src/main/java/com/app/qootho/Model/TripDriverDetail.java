package com.app.qootho.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripDriverDetail {

    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("modelName")
    @Expose
    private String modelName;
    @SerializedName("plateNumber")
    @Expose
    private String plateNumber;
    @SerializedName("firstName")
    @Expose
    private String firstName;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("vehiclePhoto")
    @Expose
    private String vehiclePhoto;
    @SerializedName("raiderPhoto")
    @Expose
    private String raiderPhoto;

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVehiclePhoto() {
        return vehiclePhoto;
    }

    public void setVehiclePhoto(String vehiclePhoto) {
        this.vehiclePhoto = vehiclePhoto;
    }

    public String getRaiderPhoto() {
        return raiderPhoto;
    }

    public void setRaiderPhoto(String raiderPhoto) {
        this.raiderPhoto = raiderPhoto;
    }

}


