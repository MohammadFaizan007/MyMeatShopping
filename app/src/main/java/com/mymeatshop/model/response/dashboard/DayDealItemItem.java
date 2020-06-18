package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

public class DayDealItemItem {

    @SerializedName("store_id")
    private String storeId;

    @SerializedName("image")
    private String image;

    @SerializedName("type_id")
    private String typeId;

    @SerializedName("price")
    private String price;

    @SerializedName("name")
    private String name;

    @SerializedName("sku")
    private String sku;

    @SerializedName("offerprice")
    private String offerprice;

    public void setWislist(String wislist) {
        this.wislist = wislist;
    }

    public void setWishlist_item_id(String wishlist_item_id) {
        this.wishlist_item_id = wishlist_item_id;
    }

    public void setWishlist_id(String wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    @SerializedName("wislist")
    private String wislist;
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

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public String getOfferprice() {
        return offerprice;
    }

    public void setOfferprice(String offerprice) {
        this.offerprice = offerprice;
    }

    @Override
    public String toString() {
        return
                "DayDealItemItem{" +
                        "store_id = '" + storeId + '\'' +
                        ",image = '" + image + '\'' +
                        ",type_id = '" + typeId + '\'' +
                        ",price = '" + price + '\'' +
                        ",name = '" + name + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",offerprice = '" + offerprice + '\'' +
                        "}";
    }
}