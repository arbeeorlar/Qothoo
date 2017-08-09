package com.app.qootho.Model;


import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class HelpModel {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("helpSubTitle")
    @Expose
    private List<HelpSubTitle> helpSubTitle = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<HelpSubTitle> getHelpSubTitle() {
        return helpSubTitle;
    }

    public void setHelpSubTitle(List<HelpSubTitle> helpSubTitle) {
        this.helpSubTitle = helpSubTitle;
    }

    public static class HelpSubTitle implements Parcelable, Comparable<HelpSubTitle> {

        public static final Creator<HelpSubTitle> CREATOR = new Creator<HelpSubTitle>() {
            @Override
            public HelpSubTitle createFromParcel(Parcel in) {
                return new HelpSubTitle(in);
            }

            @Override
            public HelpSubTitle[] newArray(int size) {
                return new HelpSubTitle[size];
            }
        };
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("subTitle")
        @Expose
        private String subTitle;
        @SerializedName("helpContent")
        @Expose
        private List<HelpContent> helpContent = null;
        private boolean isSectionHeader = false;
        private String subTitleContent;

        protected HelpSubTitle(Parcel in) {
            subTitle = in.readString();
        }

        public HelpSubTitle() {

        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getSubTitle() {
            return subTitle;
        }

        public void setSubTitle(String subTitle) {
            this.subTitle = subTitle;
        }

        public List<HelpContent> getHelpContent() {
            return helpContent;
        }

        public void setHelpContent(List<HelpContent> helpContent) {
            this.helpContent = helpContent;
        }

        public void setToSectionHeader() {
            isSectionHeader = true;
        }

        public boolean isSectionHeader() {
            return isSectionHeader;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(subTitle);
        }

        @Override
        public int compareTo(@NonNull HelpSubTitle o) {
            return this.subTitle.compareTo(o.subTitle);
        }

        public String getSubTitleContent() {
            return subTitleContent;
        }

        public void setSubTitleContent(String subTitleContent) {
            this.subTitleContent = subTitleContent;
        }
    }

    public class HelpContent {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("displayContent")
        @Expose
        private String displayContent;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDisplayContent() {
            return displayContent;
        }

        public void setDisplayContent(String displayContent) {
            this.displayContent = displayContent;
        }

    }
}




