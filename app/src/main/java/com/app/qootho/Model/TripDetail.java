package com.app.qootho.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TripDetail {

    @SerializedName("proposedPickUpLat")
    @Expose
    private Double proposedPickUpLat;
    @SerializedName("proposedPickUpLong")
    @Expose
    private Double proposedPickUpLong;
    @SerializedName("proposedDestinationLat")
    @Expose
    private Double proposedDestinationLat;
    @SerializedName("proposedDestinationLong")
    @Expose
    private Double proposedDestinationLong;
    @SerializedName("pickUpAddress")
    @Expose
    private String pickUpAddress;
    @SerializedName("dropOffAddress")
    @Expose
    private String dropOffAddress;
    @SerializedName("rideTypeID")
    @Expose
    private Integer rideTypeID;
    @SerializedName("scheduledPickUpTime")
    @Expose
    private String scheduledPickUpTime;
    @SerializedName("expenseCode")
    @Expose
    private String expenseCode;
    @SerializedName("paymentTypeID")
    @Expose
    private Integer paymentTypeID;
    @SerializedName("fareEstimateLB")
    @Expose
    private Double fareEstimateLB;
    @SerializedName("fareEstimateUB")
    @Expose
    private Double fareEstimateUB;
    @SerializedName("payerID")
    @Expose
    private Integer payerID;
    @SerializedName("memo")
    @Expose
    private String memo;

    public Double getProposedPickUpLat() {
        return proposedPickUpLat;
    }

    public void setProposedPickUpLat(Double proposedPickUpLat) {
        this.proposedPickUpLat = proposedPickUpLat;
    }

    public Double getProposedPickUpLong() {
        return proposedPickUpLong;
    }

    public void setProposedPickUpLong(Double proposedPickUpLong) {
        this.proposedPickUpLong = proposedPickUpLong;
    }

    public Double getProposedDestinationLat() {
        return proposedDestinationLat;
    }

    public void setProposedDestinationLat(Double proposedDestinationLat) {
        this.proposedDestinationLat = proposedDestinationLat;
    }

    public Double getProposedDestinationLong() {
        return proposedDestinationLong;
    }

    public void setProposedDestinationLong(Double proposedDestinationLong) {
        this.proposedDestinationLong = proposedDestinationLong;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public Integer getRideTypeID() {
        return rideTypeID;
    }

    public void setRideTypeID(Integer rideTypeID) {
        this.rideTypeID = rideTypeID;
    }

    public String getScheduledPickUpTime() {
        return scheduledPickUpTime;
    }

    public void setScheduledPickUpTime(String scheduledPickUpTime) {
        this.scheduledPickUpTime = scheduledPickUpTime;
    }

    public String getExpenseCode() {
        return expenseCode;
    }

    public void setExpenseCode(String expenseCode) {
        this.expenseCode = expenseCode;
    }

    public Integer getPaymentTypeID() {
        return paymentTypeID;
    }

    public void setPaymentTypeID(Integer paymentTypeID) {
        this.paymentTypeID = paymentTypeID;
    }

    public Double getFareEstimateLB() {
        return fareEstimateLB;
    }

    public void setFareEstimateLB(Double fareEstimateLB) {
        this.fareEstimateLB = fareEstimateLB;
    }

    public Double getFareEstimateUB() {
        return fareEstimateUB;
    }

    public void setFareEstimateUB(Double fareEstimateUB) {
        this.fareEstimateUB = fareEstimateUB;
    }

    public Integer getPayerID() {
        return payerID;
    }

    public void setPayerID(Integer payerID) {
        this.payerID = payerID;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

}

