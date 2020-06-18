package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

public class CartitemsItem {

    @SerializedName("product_type")
    private String productType;

    @SerializedName("item_id")
    private int itemId;

    @SerializedName("cat_name")
    private String catName;

    @SerializedName("price")
    private int price;

    @SerializedName("quote_id")
    private String quoteId;

    @SerializedName("qty")
    private int qty;

    @SerializedName("name")
    private String name;

    @SerializedName("sku")
    private String sku;

    @SerializedName("product_option")
    private Object productOption;

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Object getProductOption() {
        return productOption;
    }

    public void setProductOption(Object productOption) {
        this.productOption = productOption;
    }

    @Override
    public String toString() {
        return
                "CartitemsItem{" +
                        "product_type = '" + productType + '\'' +
                        ",item_id = '" + itemId + '\'' +
                        ",cat_name = '" + catName + '\'' +
                        ",price = '" + price + '\'' +
                        ",quote_id = '" + quoteId + '\'' +
                        ",qty = '" + qty + '\'' +
                        ",name = '" + name + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",product_option = '" + productOption + '\'' +
                        "}";
    }
}