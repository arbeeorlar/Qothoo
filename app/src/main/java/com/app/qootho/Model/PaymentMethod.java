package com.app.qootho.Model;

/**
 * Created by macbookpro on 04/08/2017.
 */

public class PaymentMethod {

    int id;
    String paymentName;
    String paymentType;

    public int getId() {
        return id;
    }

    public void setId(int Id) {
        this.id = Id;

    }

    public String getPaymentName() {
        return paymentName;
    }

    public void setPaymentName(String paymentName) {
        this.paymentName = paymentName;

    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;

    }
}
