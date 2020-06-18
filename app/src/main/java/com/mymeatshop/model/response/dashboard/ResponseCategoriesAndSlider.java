package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

public class ResponseCategoriesAndSlider {

    @SerializedName("p_cat")
    private PCat pCat;

    @SerializedName("sliderlist")
    private Sliderlist sliderlist;

    public PCat getPCat() {
        return pCat;
    }

    public void setPCat(PCat pCat) {
        this.pCat = pCat;
    }

    public Sliderlist getSliderlist() {
        return sliderlist;
    }

    public void setSliderlist(Sliderlist sliderlist) {
        this.sliderlist = sliderlist;
    }

    @Override
    public String toString() {
        return
                "ResponseCategoriesAndSlider{" +
                        "p_cat = '" + pCat + '\'' +
                        ",sliderlist = '" + sliderlist + '\'' +
                        "}";
    }
}