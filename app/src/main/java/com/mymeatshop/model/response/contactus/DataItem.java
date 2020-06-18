package com.mymeatshop.model.response.contactus;

import com.google.gson.annotations.SerializedName;


public class DataItem{

	@SerializedName("mobile")
	private String mobile;

	@SerializedName("action")
	private String action;

	@SerializedName("id")
	private String id;

	@SerializedName("email")
	private String email;

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return mobile;
	}

	public void setAction(String action){
		this.action = action;
	}

	public String getAction(){
		return action;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return email;
	}
}