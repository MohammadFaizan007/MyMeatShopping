package com.mymeatshop.model.response.track_order;


import com.google.gson.annotations.SerializedName;


public class Data{

	@SerializedName("image")
	private String image;

	public String getLatitude() {
		return latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	@SerializedName("latitude")
	private String latitude;
	@SerializedName("longitude")
	private String longitude;

	public String getAdd() {
		return add;
	}

	@SerializedName("add")
	private String add;

	@SerializedName("delivery_mobile")
	private String deliveryMobile;

	@SerializedName("delivery_name")
	private String deliveryName;

	@SerializedName("c_lati")
	private String cLati;

	@SerializedName("v_lat")
	private String vLat;

	@SerializedName("status")
	private String status;

	public String getD_id() {
		return d_id;
	}

	@SerializedName("d_id")
	private String d_id;

	public void setImage(String image){
		this.image = image;
	}

	public String getImage(){
		return image;
	}

	public void setDeliveryMobile(String deliveryMobile){
		this.deliveryMobile = deliveryMobile;
	}

	public String getDeliveryMobile(){
		return deliveryMobile;
	}

	public void setDeliveryName(String deliveryName){
		this.deliveryName = deliveryName;
	}

	public String getDeliveryName(){
		return deliveryName;
	}

	public void setCLati(String cLati){
		this.cLati = cLati;
	}

	public String getCLati(){
		return cLati;
	}

	public void setVLat(String vLat){
		this.vLat = vLat;
	}

	public String getVLat(){
		return vLat;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}
}