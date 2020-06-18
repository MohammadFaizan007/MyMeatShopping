package com.mymeatshop.model.response.product_detail;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ResponseProductDetail {

    @SerializedName("pieces")
    private String pieces;

    @SerializedName("short_description")
    private String shortDescription;

    @SerializedName("stock_item")
    private StockItem stockItem;

    @SerializedName("gross")
    private String gross;

    @SerializedName("price")
    private int price;

    @SerializedName("type_id")
    private String typeId;

    @SerializedName("name")
    private String name;

    @SerializedName("media_gallery")
    private List<MediaGalleryItem> mediaGallery;

    @SerializedName("description")
    private String description;

    @SerializedName("id")
    private int id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("net")
    private String net;

    @SerializedName("offerprice")
    private String offerprice;

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public void setStockItem(StockItem stockItem) {
        this.stockItem = stockItem;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MediaGalleryItem> getMediaGallery() {
        return mediaGallery;
    }

    public void setMediaGallery(List<MediaGalleryItem> mediaGallery) {
        this.mediaGallery = mediaGallery;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getNet() {
        return net;
    }

    public void setNet(String net) {
        this.net = net;
    }

    public String getOfferprice() {
        return offerprice;
    }

    public void setOfferprice(String net) {
        this.offerprice = net;
    }

    @Override
    public String toString() {
        return
                "ResponseProductDetail{" +
                        "pieces = '" + pieces + '\'' +
                        ",short_description = '" + shortDescription + '\'' +
                        ",stock_item = '" + stockItem + '\'' +
                        ",gross = '" + gross + '\'' +
                        ",price = '" + price + '\'' +
                        ",type_id = '" + typeId + '\'' +
                        ",name = '" + name + '\'' +
                        ",media_gallery = '" + mediaGallery + '\'' +
                        ",description = '" + description + '\'' +
                        ",id = '" + id + '\'' +
                        ",sku = '" + sku + '\'' +
                        ",net = '" + net + '\'' +
                        "}";
    }
}