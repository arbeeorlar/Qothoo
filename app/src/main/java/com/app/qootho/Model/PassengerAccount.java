package com.app.qootho.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PassengerAccount {

    @SerializedName("institutionID")
    @Expose
    private Integer institutionID;
    @SerializedName("institutionName")
    @Expose
    private String institutionName;

    private boolean isChecked = false;

    public Integer getInstitutionID() {
        return institutionID;
    }

    public void setInstitutionID(Integer institutionID) {
        this.institutionID = institutionID;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

}