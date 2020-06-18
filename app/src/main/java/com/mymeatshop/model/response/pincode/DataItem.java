package com.mymeatshop.model.response.pincode;


import com.google.gson.annotations.SerializedName;


public class DataItem{

	@SerializedName("Pincode")
	private String pincode;

	public void setPincode(String pincode){
		this.pincode = pincode;
	}

	public String getPincode(){
		return pincode;
	}
}