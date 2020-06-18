package com.mymeatshop.model.request;

import com.google.gson.annotations.SerializedName;

public class CartItem {

    @SerializedName("qty")
    private int qty;

    @SerializedName("quote_id")
    private String quoteId;

    @SerializedName("sku")
    private String sku;

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }


}