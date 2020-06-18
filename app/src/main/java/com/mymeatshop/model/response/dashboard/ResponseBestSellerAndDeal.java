package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

public class ResponseBestSellerAndDeal {

    @SerializedName("best_seller")
    private BestSeller bestSeller;

    @SerializedName("day_deal")
    private DayDeal dayDeal;

    public BestSeller getBestSeller() {
        return bestSeller;
    }

    public void setBestSeller(BestSeller bestSeller) {
        this.bestSeller = bestSeller;
    }

    public DayDeal getDayDeal() {
        return dayDeal;
    }

    public void setDayDeal(DayDeal dayDeal) {
        this.dayDeal = dayDeal;
    }

    @Override
    public String toString() {
        return
                "ResponseBestSellerAndDeal{" +
                        "best_seller = '" + bestSeller + '\'' +
                        ",day_deal = '" + dayDeal + '\'' +
                        "}";
    }
}