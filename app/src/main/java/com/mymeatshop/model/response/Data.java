package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("firstname")
    private String firstname;

    public String getOrder_id() {
        return order_id;
    }

    @SerializedName("order_id")
    private String order_id;

    @SerializedName("quote_id")
    private int quoteId;

    @SerializedName("mobile")
    private String mobile;

    @SerializedName("id")
    private int id;

    @SerializedName("email")
    private String email;

    @SerializedName("token")
    private String token;

    @SerializedName("lastname")
    private String lastname;

    @SerializedName("confirmation")
    private String confirmation;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(String lastname) {
        this.confirmation = lastname;
    }

    @Override
    public String toString() {
        return
                "Data{" +
                        "firstname = '" + firstname + '\'' +
                        ",quote_id = '" + quoteId + '\'' +
                        ",mobile = '" + mobile + '\'' +
                        ",id = '" + id + '\'' +
                        ",email = '" + email + '\'' +
                        ",token = '" + token + '\'' +
                        ",lastname = '" + lastname + '\'' +
                        "}";
    }
}