package com.zira.modal;

import java.util.ArrayList;

public class MainVehicleModel {

	private String result,message,preffervehicletype,SafetyFee,splitfees,useragreement,CancellationFee;
	
	
	
	private ArrayList<VehicleCategory> vehicleCategryList;
	private ArrayList<VehicleInfo> vehicleInfoList;
	private ArrayList<ZoneInfo> zoneInfoList;

	public String getSafetyFee() {
		return SafetyFee;
	}
	public void setSafetyFee(String safetyFee) {
		SafetyFee = safetyFee;
	}
	public String getSplitfees() {
		return splitfees;
	}
	public void setSplitfees(String splitfees) {
		this.splitfees = splitfees;
	}
	public String getUseragreement() {
		return useragreement;
	}
	public void setUseragreement(String useragreement) {
		this.useragreement = useragreement;
	}
	public String getCancellationFee() {
		return CancellationFee;
	}
	public void setCancellationFee(String cancellationFee) {
		CancellationFee = cancellationFee;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getPreffervehicletype() {
		return preffervehicletype;
	}
	public void setPreffervehicletype(String preffervehicletype) {
		this.preffervehicletype = preffervehicletype;
	}
	public ArrayList<VehicleCategory> getVehicleCategryList() {
		return vehicleCategryList;
	}
	public void setVehicleCategryList(ArrayList<VehicleCategory> vehicleCategryList) {
		this.vehicleCategryList = vehicleCategryList;
	}
	public ArrayList<VehicleInfo> getVehicleInfoList() {
		return vehicleInfoList;
	}
	public void setVehicleInfoList(ArrayList<VehicleInfo> vehicleInfoList) {
		this.vehicleInfoList = vehicleInfoList;
	}
	public ArrayList<ZoneInfo> getZoneInfoList() {
		return zoneInfoList;
	}
	public void setZoneInfoList(ArrayList<ZoneInfo> zoneInfoList) {
		this.zoneInfoList = zoneInfoList;
	}


}
