package com.mymeatshop.model.response.dashboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DayDeal {

    @SerializedName("day_deal_item")
    private List<DayDealItemItem> dayDealItem;

    public List<DayDealItemItem> getDayDealItem() {
        return dayDealItem;
    }

    public void setDayDealItem(List<DayDealItemItem> dayDealItem) {
        this.dayDealItem = dayDealItem;
    }

    @Override
    public String toString() {
        return
                "DayDeal{" +
                        "day_deal_item = '" + dayDealItem + '\'' +
                        "}";
    }
}