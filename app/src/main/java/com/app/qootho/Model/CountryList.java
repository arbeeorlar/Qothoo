package com.app.qootho.Model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryList {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("countryName")
    @Expose
    private String countryName;
    @SerializedName("phoneCode")
    @Expose
    private String phoneCode;
    @SerializedName("currencyCode")
    @Expose
    private String currencyCode;
    @SerializedName("flag")
    @Expose
    private String flag;
    @SerializedName("contentLength")
    @Expose
    private Integer contentLength;
    @SerializedName("contentType")
    @Expose
    private String contentType;
    @SerializedName("fileExtension")
    @Expose
    private String fileExtension;
    @SerializedName("weekStartDayID")
    @Expose
    private Integer weekStartDayID;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getContentLength() {
        return contentLength;
    }

    public void setContentLength(Integer contentLength) {
        this.contentLength = contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public Integer getWeekStartDayID() {
        return weekStartDayID;
    }

    public void setWeekStartDayID(Integer weekStartDayID) {
        this.weekStartDayID = weekStartDayID;
    }

}
