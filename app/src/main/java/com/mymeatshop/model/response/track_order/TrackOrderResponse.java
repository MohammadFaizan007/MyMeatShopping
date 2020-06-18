package com.mymeatshop.model.response.track_order;


import com.google.gson.annotations.SerializedName;

public class TrackOrderResponse{

	@SerializedName("data")
	private Data data;

	@SerializedName("response")
	private String response;

	public void setData(Data data){
		this.data = data;
	}

	public Data getData(){
		return data;
	}

	public void setResponse(String response){
		this.response = response;
	}

	public String getResponse(){
		return response;
	}
}