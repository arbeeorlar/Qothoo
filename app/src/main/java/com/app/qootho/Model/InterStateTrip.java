package com.app.qootho.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InterStateTrip {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("requestDate")
    @Expose
    private String requestDate;
    @SerializedName("tripDate")
    @Expose
    private String tripDate;
    @SerializedName("riderName")
    @Expose
    private String riderName;
    @SerializedName("isTripCanceled")
    @Expose
    private Boolean isTripCanceled;
    @SerializedName("isTripConcluded")
    @Expose
    private Boolean isTripConcluded;
    @SerializedName("sourceState")
    @Expose
    private String sourceState;
    @SerializedName("destinationSate")
    @Expose
    private String destinationSate;
    @SerializedName("totalFare")
    @Expose
    private Double totalFare;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getTripDate() {
        return tripDate;
    }

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
    }

    public String getRiderName() {
        return riderName;
    }

    public void setRiderName(String riderName) {
        this.riderName = riderName;
    }

    public Boolean getIsTripCanceled() {
        return isTripCanceled;
    }

    public void setIsTripCanceled(Boolean isTripCanceled) {
        this.isTripCanceled = isTripCanceled;
    }

    public Boolean getIsTripConcluded() {
        return isTripConcluded;
    }

    public void setIsTripConcluded(Boolean isTripConcluded) {
        this.isTripConcluded = isTripConcluded;
    }

    public String getSourceState() {
        return sourceState;
    }

    public void setSourceState(String sourceState) {
        this.sourceState = sourceState;
    }

    public String getDestinationSate() {
        return destinationSate;
    }

    public void setDestinationSate(String destinationSate) {
        this.destinationSate = destinationSate;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

}
