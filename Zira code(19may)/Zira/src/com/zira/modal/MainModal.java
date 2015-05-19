package com.zira.modal;

import java.util.ArrayList;

public class MainModal {

	private String result, message;
	private ArrayList<Country> countryList;
	private ArrayList<State> stateList;
	private ArrayList<Vehicle> vehicleList;
	private ArrayList<City> CityList;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public ArrayList<City> getCityList() {
		return CityList;
	}
	public void setCityList(ArrayList<City> cityList) {
		CityList = cityList;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public ArrayList<Country> getCountryList() {
		return countryList;
	}
	public void setCountryList(ArrayList<Country> countryList) {
		this.countryList = countryList;
	}
	public ArrayList<State> getStateList() {
		return stateList;
	}
	public void setStateList(ArrayList<State> stateList) {
		this.stateList = stateList;
	}
	public ArrayList<Vehicle> getVehicleList() {
		return vehicleList;
	}
	public void setVehicleList(ArrayList<Vehicle> vehicleList) {
		this.vehicleList = vehicleList;
	}
	
	
}
