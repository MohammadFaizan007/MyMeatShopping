package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseCartItem {

    @SerializedName("cartitems")
    private List<CartitemsItem> cartitems;

    public List<CartitemsItem> getCartitems() {
        return cartitems;
    }

    public void setCartitems(List<CartitemsItem> cartitems) {
        this.cartitems = cartitems;
    }

    @Override
    public String toString() {
        return
                "ResponseCartItem{" +
                        "cartitems = '" + cartitems + '\'' +
                        "}";
    }
}