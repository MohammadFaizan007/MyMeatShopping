package com.mymeatshop.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CouponResponse {
    @SerializedName("response")
    @Expose
    public String response;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("data")
    @Expose
    public String data;

    public String getData() {
        return data;
    }

    public String getResponse() {
        return response;
    }

    public String getMessage() {
        return message;
    }
}
