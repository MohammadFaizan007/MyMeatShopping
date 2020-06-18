package com.mymeatshop.model.response.address.add_address;

import com.google.gson.annotations.SerializedName;

public class DataItem {

    @SerializedName("code")
    private String code;

    @SerializedName("title")
    private String title;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
                "DataItem{" +
                        "code = '" + code + '\'' +
                        ",title = '" + title + '\'' +
                        "}";
    }
}