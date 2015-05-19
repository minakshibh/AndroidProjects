package com.zira.modal;

public class City {

	private String  Cityname,cityId;

	public String getCityname() {
		return Cityname;
	}

	public void setCityname(String cityname) {
		Cityname = cityname;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return Cityname;
	}
}
