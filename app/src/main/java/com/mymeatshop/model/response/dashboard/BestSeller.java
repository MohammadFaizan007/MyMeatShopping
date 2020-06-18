package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BestSeller {

    @SerializedName("best_seller_item")
    private List<BestSellerItemItem> bestSellerItem;

    public List<BestSellerItemItem> getBestSellerItem() {
        return bestSellerItem;
    }

    public void setBestSellerItem(List<BestSellerItemItem> bestSellerItem) {
        this.bestSellerItem = bestSellerItem;
    }

    @Override
    public String toString() {
        return
                "BestSeller{" +
                        "best_seller_item = '" + bestSellerItem + '\'' +
                        "}";
    }
}