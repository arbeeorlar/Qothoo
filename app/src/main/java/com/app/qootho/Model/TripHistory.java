package com.app.qootho.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TripHistory {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("actualDestinationLat")
    @Expose
    private Double actualDestinationLat;
    @SerializedName("actualDestinationLong")
    @Expose
    private Double actualDestinationLong;
    @SerializedName("actualPickUpLat")
    @Expose
    private Double actualPickUpLat;
    @SerializedName("actualPickUpLong")
    @Expose
    private Double actualPickUpLong;
    @SerializedName("dropOffAddress")
    @Expose
    private String dropOffAddress;
    @SerializedName("paymentReference")
    @Expose
    private String paymentReference;
    @SerializedName("pickUpAddress")
    @Expose
    private String pickUpAddress;
    @SerializedName("riderRating")
    @Expose
    private Integer riderRating;
    @SerializedName("rideTypeName")
    @Expose
    private String rideTypeName;
    @SerializedName("tripStartTime")
    @Expose
    private String tripStartTime;
    @SerializedName("timeCost")
    @Expose
    private Double timeCost;
    @SerializedName("distanceCost")
    @Expose
    private Double distanceCost;
    @SerializedName("subTotal")
    @Expose
    private Double subTotal;
    @SerializedName("isCanceled")
    @Expose
    private Boolean isCanceled;
    @SerializedName("baseFare")
    @Expose
    private Double baseFare;
    @SerializedName("roundDownValue")
    @Expose
    private Double roundDownValue;
    @SerializedName("routeMap")
    @Expose
    private String routeMap;
    @SerializedName("modelName")
    @Expose
    private String modelName;
    @SerializedName("brandName")
    @Expose
    private String brandName;
    @SerializedName("totalFare")
    @Expose
    private Double totalFare;
    @SerializedName("plateNumber")
    @Expose
    private String plateNumber;
    @SerializedName("raider")
    @Expose
    private String raider;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getActualDestinationLat() {
        return actualDestinationLat;
    }

    public void setActualDestinationLat(Double actualDestinationLat) {
        this.actualDestinationLat = actualDestinationLat;
    }

    public Double getActualDestinationLong() {
        return actualDestinationLong;
    }

    public void setActualDestinationLong(Double actualDestinationLong) {
        this.actualDestinationLong = actualDestinationLong;
    }

    public Double getActualPickUpLat() {
        return actualPickUpLat;
    }

    public void setActualPickUpLat(Double actualPickUpLat) {
        this.actualPickUpLat = actualPickUpLat;
    }

    public Double getActualPickUpLong() {
        return actualPickUpLong;
    }

    public void setActualPickUpLong(Double actualPickUpLong) {
        this.actualPickUpLong = actualPickUpLong;
    }

    public String getDropOffAddress() {
        return dropOffAddress;
    }

    public void setDropOffAddress(String dropOffAddress) {
        this.dropOffAddress = dropOffAddress;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPickUpAddress() {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress) {
        this.pickUpAddress = pickUpAddress;
    }

    public Integer getRiderRating() {
        return riderRating;
    }

    public void setRiderRating(Integer riderRating) {
        this.riderRating = riderRating;
    }

    public String getRideTypeName() {
        return rideTypeName;
    }

    public void setRideTypeName(String rideTypeName) {
        this.rideTypeName = rideTypeName;
    }

    public String getTripStartTime() {
        return tripStartTime;
    }

    public void setTripStartTime(String tripStartTime) {
        this.tripStartTime = tripStartTime;
    }

    public Double getTimeCost() {
        return timeCost;
    }

    public void setTimeCost(Double timeCost) {
        this.timeCost = timeCost;
    }

    public Double getDistanceCost() {
        return distanceCost;
    }

    public void setDistanceCost(Double distanceCost) {
        this.distanceCost = distanceCost;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(Double subTotal) {
        this.subTotal = subTotal;
    }

    public Boolean getIsCanceled() {
        return isCanceled;
    }

    public void setIsCanceled(Boolean isCanceled) {
        this.isCanceled = isCanceled;
    }

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getRoundDownValue() {
        return roundDownValue;
    }

    public void setRoundDownValue(Double roundDownValue) {
        this.roundDownValue = roundDownValue;
    }

    public String getRouteMap() {
        return routeMap;
    }

    public void setRouteMap(String routeMap) {
        this.routeMap = routeMap;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getRaider() {
        return raider;
    }

    public void setRaider(String raider) {
        this.raider = raider;
    }

}

