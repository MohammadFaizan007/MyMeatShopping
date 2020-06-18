package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseProductListByCategory {

    @SerializedName("product_list")
    private List<ProductListItem> productList;

    public List<ProductListItem> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductListItem> productList) {
        this.productList = productList;
    }

    @Override
    public String toString() {
        return
                "ResponseProductListByCategory{" +
                        "product_list = '" + productList + '\'' +
                        "}";
    }
}