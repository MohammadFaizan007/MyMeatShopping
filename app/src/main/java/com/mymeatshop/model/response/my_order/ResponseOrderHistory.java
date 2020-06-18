package com.mymeatshop.model.response.my_order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseOrderHistory {

    @SerializedName("data")
    private List<DataItem> data;

    @SerializedName("message")
    private String message;

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}