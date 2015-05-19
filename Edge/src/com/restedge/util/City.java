package com.restedge.util;

public class City {
	
	private int cityId,visibility_flag=1;
	
	private String cityName;
	private String cityUrl;
	private byte[] cityimage;
	
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCityUrl() {
		return cityUrl;
	}
	public void setCityUrl(String cityUrl) {
		this.cityUrl = cityUrl;
	}
	public byte[] getCityimage() {
		return cityimage;
	}
	public void setCityimage(byte[] cityimage) {
		this.cityimage = cityimage;
	}
	public int getVisibility_flag() {
		return visibility_flag;
	}
	public void setVisibility_flag(int visibility_flag) {
		this.visibility_flag = visibility_flag;
	}
}