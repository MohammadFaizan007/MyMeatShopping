package com.mymeatshop.model.response.address;

import com.google.gson.annotations.SerializedName;

public class DataItem {

    @SerializedName("firstname")
    private String firstname;

    @SerializedName("city")
    private String city;

    public String getC_lat() {
        return c_lat;
    }

    @SerializedName("c_lat")
    private String c_lat;

    @SerializedName("street")
    private String street;

    @SerializedName("address_id")
    private String addressId;

    @SerializedName("postcode")
    private String postcode;

    @SerializedName("telephone")
    private String telephone;

    @SerializedName("same_as_billing")
    private String sameAsBilling;

    @SerializedName("region")
    private String region;

    @SerializedName("email")
    private String email;

    @SerializedName("lastname")
    private String lastname;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSameAsBilling() {
        return sameAsBilling;
    }

    public void setSameAsBilling(String sameAsBilling) {
        this.sameAsBilling = sameAsBilling;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return
                "DataItem{" +
                        "firstname = '" + firstname + '\'' +
                        ",city = '" + city + '\'' +
                        ",street = '" + street + '\'' +
                        ",address_id = '" + addressId + '\'' +
                        ",postcode = '" + postcode + '\'' +
                        ",telephone = '" + telephone + '\'' +
                        ",same_as_billing = '" + sameAsBilling + '\'' +
                        ",region = '" + region + '\'' +
                        ",email = '" + email + '\'' +
                        ",lastname = '" + lastname + '\'' +
                        "}";
    }
}