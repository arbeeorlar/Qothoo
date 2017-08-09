package com.app.qootho.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PendingPayment {

    @SerializedName("totalFare")
    @Expose
    private Double totalFare;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("paymentTypeName")
    @Expose
    private String paymentTypeName;

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

}

