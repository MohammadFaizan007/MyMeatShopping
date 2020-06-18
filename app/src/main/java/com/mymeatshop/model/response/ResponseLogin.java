package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

public class ResponseLogin {

    @SerializedName("data")
    private Data data;

    @SerializedName("response")
    private String response;
    @SerializedName("message")
    private String message;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return
                "ResponseLogin{" +
                        "data = '" + data + '\'' +
                        ",response = '" + response + '\'' +
                        "}";
    }
}