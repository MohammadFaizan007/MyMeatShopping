package com.mymeatshop.model.request;

import com.google.gson.annotations.SerializedName;

public class RequestUpdateCart {

    @SerializedName("item_id")
    private int itemId;

    @SerializedName("cartItem")
    private CartItem cartItem;

    @SerializedName("token")
    private String token;

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return
                "RequestUpdateCart{" +
                        "item_id = '" + itemId + '\'' +
                        ",cartItem = '" + cartItem + '\'' +
                        ",token = '" + token + '\'' +
                        "}";
    }
}