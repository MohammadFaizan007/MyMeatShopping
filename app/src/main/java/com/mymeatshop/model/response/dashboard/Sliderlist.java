package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sliderlist {

    @SerializedName("sliderlistdata")
    private List<SliderlistdataItem> sliderlistdata;

    public List<SliderlistdataItem> getSliderlistdata() {
        return sliderlistdata;
    }

    public void setSliderlistdata(List<SliderlistdataItem> sliderlistdata) {
        this.sliderlistdata = sliderlistdata;
    }

    @Override
    public String toString() {
        return
                "Sliderlist{" +
                        "sliderlistdata = '" + sliderlistdata + '\'' +
                        "}";
    }
}