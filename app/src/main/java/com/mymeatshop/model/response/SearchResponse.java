package com.mymeatshop.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SearchResponse {
    @SerializedName("product_list")
    @Expose
    public List<ProductList> productList = null;
    @SerializedName("message")
    @Expose
    public String message;

    public List<ProductList> getProductList() {
        return productList;
    }

    public String getMessage() {
        return message;
    }
}
