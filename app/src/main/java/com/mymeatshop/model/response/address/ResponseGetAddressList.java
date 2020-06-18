package com.mymeatshop.model.response.address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseGetAddressList {

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

    @Override
    public String toString() {
        return
                "ResponseGetAddressList{" +
                        "data = '" + data + '\'' +
                        ",message = '" + message + '\'' +
                        "}";
    }
}