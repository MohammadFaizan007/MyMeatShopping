package com.mymeatshop.model.response.product_detail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseAddToWishList {

    @SerializedName("data")
    @Expose
    public Data data;
    @SerializedName("response")
    private String response;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return
                "ResponseAddToWishList{" +
                        "response = '" + response + '\'' +
                        "}";
    }
}