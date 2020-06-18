package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

public class WishListItem {

    @SerializedName("item_id")
    private int itemId;

    @SerializedName("price")
    private int price;

    @SerializedName("name")
    private String name;

    @SerializedName("wishlist_id")
    private int wishlistId;

    @SerializedName("sku")
    private String sku;

    @SerializedName("gallery")
    private String gallery;

    @SerializedName("storename")
    private String storename;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWishlistId() {
        return wishlistId;
    }

    public void setWishlistId(int wishlistId) {
        this.wishlistId = wishlistId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getStorename() {
        return storename;
    }

    public void setStorename(String gallery) {
        this.storename = storename;
    }

    @Override
    public String toString() {
        return
                "WishListItem{" +
                        "item_id = '" + itemId + '\'' +
                        ",price = '" + price + '\'' +
                        ",name = '" + name + '\'' +
                        ",wishlist_id = '" + wishlistId + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",gallery = '" + gallery + '\'' +
                        ",storename = '" + storename + '\'' +
                        "}";
    }
}