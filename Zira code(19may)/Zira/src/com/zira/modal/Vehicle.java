package com.zira.modal;

import java.util.ArrayList;


public class Vehicle {
	
	private String vehicleMakeName;
	private ArrayList<ModelArray> listModalData;
	
	
    public String getVehicleMakeName() {
		return vehicleMakeName;
	}
	public void setVehicleMakeName(String vehicleMakeName) {
		this.vehicleMakeName = vehicleMakeName;
	}
	
	public ArrayList<ModelArray> getListModalData() {
		return listModalData;
	}
	public void setListModalData(ArrayList<ModelArray> listModalData) {
		this.listModalData = listModalData;
	}

	@Override
	public String toString(){
		return vehicleMakeName;
	}
	
	public class ModelArray {


		private String vehiclemodalName;
		private String vehiclemodalYear;
		private String vehiclemodelID;
		
		
		public String getVehiclemodelID() {
			return vehiclemodelID;
		}
		public void setVehiclemodelID(String vehiclemodelID) {
			this.vehiclemodelID = vehiclemodelID;
		}
		public String getVehiclemodalName() {
			return vehiclemodalName;
		}
		public void setVehiclemodalName(String vehiclemodalName) {
			this.vehiclemodalName = vehiclemodalName;
		}
		public String getVehiclemodalYear() {
			return vehiclemodalYear;
		}
		public void setVehiclemodalYear(String vehiclemodalYear) {
			this.vehiclemodalYear = vehiclemodalYear;
		}
		
		@Override
		public String toString(){
			return vehiclemodalName;
		}
	}

}
