package com.mymeatshop.model.response;

import com.google.gson.annotations.SerializedName;

public class ProductListItem {

    @SerializedName("pieces")
    private String pieces;
    @SerializedName("wislist")
    private String wislist;
    @SerializedName("wishlist_item_id")
    private String wislist_item_id;
    @SerializedName("wishlist_id")
    private String wislist_id;
    @SerializedName("gross")
    private String gross;
    @SerializedName("price")
    private String price;
    @SerializedName("type_id")
    private String typeId;
    @SerializedName("name")
    private String name;
    @SerializedName("id")
    private int id;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @SerializedName("status")
    private int status;
    @SerializedName("sku")
    private String sku;
    @SerializedName("net")
    private String net;
    @SerializedName("gallery")
    private String gallery;
    @SerializedName("offerprice")
    private String offerprice;

    public String getWislist() {
        return wislist;
    }

    public void setWislist(String wislist) {
        this.wislist = wislist;
    }

    public String getWislist_item_id() {
        return wislist_item_id;
    }

    public void setWislist_item_id(String wislist_item_id) {
        this.wislist_item_id = wislist_item_id;
    }

    public String getWislist_id() {
        return wislist_id;
    }

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getGross() {
        return gross;
    }

    public void setGross(String gross) {
        this.gross = gross;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
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

    public String getGallery() {
        return gallery;
    }

    public void setGallery(String gallery) {
        this.gallery = gallery;
    }

    public String getOfferprice() {
        return offerprice;
    }

    public void setOfferprice(String gallery) {
        this.offerprice = gallery;
    }

	/*@Override
 	public String toString(){
		return 
			"ProductListItem{" + 
			"pieces = '" + pieces + '\'' + 
			",gross = '" + gross + '\'' + 
			",price = '" + price + '\'' + 
			",type_id = '" + typeId + '\'' + 
			",name = '" + name + '\'' + 
			",id = '" + id + '\'' + 
			",sku = '" + sku + '\'' + 
			",net = '" + net + '\'' + 
			",gallery = '" + gallery + '\'' + 
			"}";
		}*/
}