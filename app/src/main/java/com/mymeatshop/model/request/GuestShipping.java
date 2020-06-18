package com.mymeatshop.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GuestShipping {
    @SerializedName("addressInformation")
    @Expose
    public AddressInformation addressInformation;

    public AddressInformation getAddressInformation() {
        return addressInformation;
    }

    public void setAddressInformation(AddressInformation addressInformation) {
        this.addressInformation = addressInformation;
    }
}
