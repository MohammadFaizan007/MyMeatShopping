package com.mymeatshop.model.response.my_order;

import com.google.gson.annotations.SerializedName;

public class DataItem {

    @SerializedName("MONTH(s.created_at)")
    private String mONTHSCreatedAt;

    @SerializedName("subtotal")
    private String subtotal;

    @SerializedName("name")
    private String name;

    @SerializedName("store_name")
    private String storeName;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("sku")
    private String sku;

    @SerializedName("value")
    private String value;

    @SerializedName("status")
    private String status;

    @SerializedName("entity_id")
    private String entity_id;

    public String getMONTHSCreatedAt() {
        return mONTHSCreatedAt;
    }

    public void setMONTHSCreatedAt(String mONTHSCreatedAt) {
        this.mONTHSCreatedAt = mONTHSCreatedAt;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(String entity_id) {
        this.entity_id = entity_id;
    }
}