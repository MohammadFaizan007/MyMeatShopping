package com.mymeatshop.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ShippingAddress {
    @SerializedName("region")
    @Expose
    public String region;
    @SerializedName("region_id")
    @Expose
    public Integer regionId;
    @SerializedName("country_id")
    @Expose
    public String countryId;
    @SerializedName("street")
    @Expose
    public List<String> street = null;
    @SerializedName("company")
    @Expose
    public String company;
    @SerializedName("telephone")
    @Expose
    public String telephone;
    @SerializedName("postcode")
    @Expose
    public String postcode;
    @SerializedName("city")
    @Expose
    public String city;
    @SerializedName("firstname")
    @Expose
    public String firstname;
    @SerializedName("lastname")
    @Expose
    public String lastname;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("prefix")
    @Expose
    public String prefix;
    @SerializedName("region_code")
    @Expose
    public String regionCode;
    @SerializedName("sameAsBilling")
    @Expose
    public Integer sameAsBilling;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public List<String> getStreet() {
        return street;
    }

    public void setStreet(List<String> street) {
        this.street = street;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public Integer getSameAsBilling() {
        return sameAsBilling;
    }

    public void setSameAsBilling(Integer sameAsBilling) {
        this.sameAsBilling = sameAsBilling;
    }
}
