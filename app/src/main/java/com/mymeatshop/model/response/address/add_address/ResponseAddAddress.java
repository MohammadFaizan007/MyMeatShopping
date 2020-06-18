package com.mymeatshop.model.response.address.add_address;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseAddAddress {

    @SerializedName("data")
    private List<DataItem> data;

    public List<DataItem> getData() {
        return data;
    }

    public void setData(List<DataItem> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return
                "ResponseAddAddress{" +
                        "data = '" + data + '\'' +
                        "}";
    }
}