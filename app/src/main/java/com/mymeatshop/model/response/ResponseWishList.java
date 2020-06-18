package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseWishList {

    @SerializedName("wish_list")
    private List<WishListItem> wishList;

    public List<WishListItem> getWishList() {
        return wishList;
    }

    public void setWishList(List<WishListItem> wishList) {
        this.wishList = wishList;
    }

    @Override
    public String toString() {
        return
                "ResponseWishList{" +
                        "wish_list = '" + wishList + '\'' +
                        "}";
    }
}