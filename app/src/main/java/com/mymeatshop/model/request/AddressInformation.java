package com.mymeatshop.model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressInformation {
    @SerializedName("shippingAddress")
    @Expose
    public ShippingAddress shippingAddress;
    @SerializedName("billingAddress")
    @Expose
    public ShippingAddress billingAddress;
    @SerializedName("shipping_method_code")
    @Expose
    public String shippingMethodCode;
    @SerializedName("shipping_carrier_code")
    @Expose
    public String shippingCarrierCode;

    public ShippingAddress getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(ShippingAddress shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public ShippingAddress getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(ShippingAddress billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getShippingMethodCode() {
        return shippingMethodCode;
    }

    public void setShippingMethodCode(String shippingMethodCode) {
        this.shippingMethodCode = shippingMethodCode;
    }

    public String getShippingCarrierCode() {
        return shippingCarrierCode;
    }

    public void setShippingCarrierCode(String shippingCarrierCode) {
        this.shippingCarrierCode = shippingCarrierCode;
    }
}
