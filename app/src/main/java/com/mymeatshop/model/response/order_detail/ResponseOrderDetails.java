package com.mymeatshop.model.response.order_detail;

import java.util.List;

import com.google.gson.annotations.SerializedName;


public class ResponseOrderDetails{

	@SerializedName("data")
	private List<DataItem> data;

	@SerializedName("message")
	private String message;

	public void setData(List<DataItem> data){
		this.data = data;
	}

	public List<DataItem> getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}
}