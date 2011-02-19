package com.gemokodero.zk.core;

public class Result {

	private String mName;
	private String mAddress;
	private String mPhoneNumber;
	private String mLocation;
	private String mWebsite;
	
	public Result() {
	}

	public String getName() {
		return mName;
	}
	
	public String getAddress() {
		return mAddress;
	}
	
	public String getLocation() {
		return mLocation;
	}
	
	public String getPhoneNumber() {
		return mPhoneNumber;
	}
	
	public String getWebsite() {
		return mWebsite;
	}
	
	public void setName(String name) {
		mName = name;
	}
	
	public void setAddress(String address) {
		mAddress = address;
	}
	
	public void setLocation(String location) {
		mLocation = location;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		mPhoneNumber = phoneNumber;
	}
	
	public void setWebsite(String website) {
		mWebsite = website;
	}
	
}
