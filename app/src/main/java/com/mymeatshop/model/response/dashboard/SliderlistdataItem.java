package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

public class SliderlistdataItem {

    @SerializedName("image")
    private String image;

    @SerializedName("banner_id")
    private String bannerId;

    @SerializedName("name")
    private String name;

    @SerializedName("title")
    private String title;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return
                "SliderlistdataItem{" +
                        "image = '" + image + '\'' +
                        ",banner_id = '" + bannerId + '\'' +
                        ",name = '" + name + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}