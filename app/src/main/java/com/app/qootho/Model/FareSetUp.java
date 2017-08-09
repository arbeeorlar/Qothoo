package com.app.qootho.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FareSetUp {

    @SerializedName("baseFare")
    @Expose
    private Double baseFare;
    @SerializedName("farePerKm")
    @Expose
    private Double farePerKm;
    @SerializedName("farePerMinute")
    @Expose
    private Double farePerMinute;
    @SerializedName("cancellationCharge")
    @Expose
    private Double cancellationCharge;
    @SerializedName("chargeGracePeriod")
    @Expose
    private Integer chargeGracePeriod;

    public Double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(Double baseFare) {
        this.baseFare = baseFare;
    }

    public Double getFarePerKm() {
        return farePerKm;
    }

    public void setFarePerKm(Double farePerKm) {
        this.farePerKm = farePerKm;
    }

    public Double getFarePerMinute() {
        return farePerMinute;
    }

    public void setFarePerMinute(Double farePerMinute) {
        this.farePerMinute = farePerMinute;
    }

    public Double getCancellationCharge() {
        return cancellationCharge;
    }

    public void setCancellationCharge(Double cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
    }

    public Integer getChargeGracePeriod() {
        return chargeGracePeriod;
    }

    public void setChargeGracePeriod(Integer chargeGracePeriod) {
        this.chargeGracePeriod = chargeGracePeriod;
    }

}
