package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

public class BestSellerItemItem {

    @SerializedName("year_ordered")
    private String yearOrdered;

    @SerializedName("price")
    private String price;

    @SerializedName("name")
    private String name;

    @SerializedName("sku")
    private String sku;

    @SerializedName("value")
    private String value;
    @SerializedName("wislist")
    private String wislist;

    public void setWislist(String wislist) {
        this.wislist = wislist;
    }

    public void setWishlist_item_id(String wishlist_item_id) {
        this.wishlist_item_id = wishlist_item_id;
    }

    public void setWishlist_id(String wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    @SerializedName("wishlist_item_id")
    private String wishlist_item_id;

    public String getWislist() {
        return wislist;
    }

    public String getWishlist_item_id() {
        return wishlist_item_id;
    }

    public String getWishlist_id() {
        return wishlist_id;
    }

    @SerializedName("wishlist_id")
    private String wishlist_id;

    public String getYearOrdered() {
        return yearOrdered;
    }

    public void setYearOrdered(String yearOrdered) {
        this.yearOrdered = yearOrdered;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return
                "BestSellerItemItem{" +
                        "year_ordered = '" + yearOrdered + '\'' +
                        ",price = '" + price + '\'' +
                        ",name = '" + name + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",value = '" + value + '\'' +
                        "}";
    }
}