package com.mymeatshop.model.response.product_detail;

import com.google.gson.annotations.SerializedName;

public class StockItem {

    @SerializedName("is_in_stock")
    private boolean isInStock;

    @SerializedName("max_sale_qty")
    private int maxSaleQty;

    @SerializedName("min_sale_qty")
    private int minSaleQty;

    @SerializedName("item_id")
    private int itemId;

    @SerializedName("min_qty")
    private int minQty;

    @SerializedName("product_id")
    private int productId;

    @SerializedName("qty")
    private int qty;

    @SerializedName("stock_id")
    private int stockId;

    public boolean isIsInStock() {
        return isInStock;
    }

    public void setIsInStock(boolean isInStock) {
        this.isInStock = isInStock;
    }

    public int getMaxSaleQty() {
        return maxSaleQty;
    }

    public void setMaxSaleQty(int maxSaleQty) {
        this.maxSaleQty = maxSaleQty;
    }

    public int getMinSaleQty() {
        return minSaleQty;
    }

    public void setMinSaleQty(int minSaleQty) {
        this.minSaleQty = minSaleQty;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getMinQty() {
        return minQty;
    }

    public void setMinQty(int minQty) {
        this.minQty = minQty;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Override
    public String toString() {
        return
                "StockItem{" +
                        "is_in_stock = '" + isInStock + '\'' +
                        ",max_sale_qty = '" + maxSaleQty + '\'' +
                        ",min_sale_qty = '" + minSaleQty + '\'' +
                        ",item_id = '" + itemId + '\'' +
                        ",min_qty = '" + minQty + '\'' +
                        ",product_id = '" + productId + '\'' +
                        ",qty = '" + qty + '\'' +
                        ",stock_id = '" + stockId + '\'' +
                        "}";
    }
}