package com.zira.util;

import java.util.ArrayList;


import com.zira.modal.AdresssOfLocation;
import com.zira.modal.GetTripDetails;
import com.zira.modal.MainVehicleModel;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.State;
import com.zira.modal.User;

public class SingleTon {
	
	private static SingleTon instance = null;
	private double latitute,longitude;
	private ProfileInfoModel profileInfoModel;
	private ArrayList<State> selectedStates=new ArrayList<State>();
	private String vehicleMake,vehicleModel,VehicleYear,vehicleLicencePlateNumber,vehicleCountryName,vehicleStateName;
	private String bg_address1, bg_address2, bg_zipcode, bg_city, bg_state, bg_drivingLicenseNumber, bg_drivingLicenseState, bg_LicExoDate,
	bgDOB, bg_socialSecNumber; 
	private User user;
	private boolean isEdited;
	private String from;
	private AdresssOfLocation mLocation;
	private MainVehicleModel mainVehicleModel;
	
	private String receivednotificationSentMessage;
	private String receivednotificationImage;
	private String receivednotificationTripID;
	private String receivednotificationName;
	String receiveValue;
	
	private GetTripDetails getTripDetail; 
	private String mode;
	
	private String tip,totalFare;
	
	
	public String getReceiveValue() {
		return receiveValue;
	}

	public void setReceiveValue(String receiveValue) {
		this.receiveValue = receiveValue;
	}

	String DriverName,driverMessage,driverImage,driverTripId,driverVehicleImage,driverLastData;	
	String sentValue;
	public String getSentValue() {
		return sentValue;
	}

	public void setSentValue(String sentValue) {
		this.sentValue = sentValue;
	}

	public String getDriverName() {
		return DriverName;
	}

	public void setDriverName(String driverName) {
		DriverName = driverName;
	}

	public String getDriverMessage() {
		return driverMessage;
	}

	public void setDriverMessage(String driverMessage) {
		this.driverMessage = driverMessage;
	}

	public String getDriverImage() {
		return driverImage;
	}

	public void setDriverImage(String driverImage) {
		this.driverImage = driverImage;
	}

	public String getDriverTripId() {
		return driverTripId;
	}

	public void setDriverTripId(String driverTripId) {
		this.driverTripId = driverTripId;
	}

	public String getDriverVehicleImage() {
		return driverVehicleImage;
	}

	public void setDriverVehicleImage(String driverVehicleImage) {
		this.driverVehicleImage = driverVehicleImage;
	}

	public String getDriverLastData() {
		return driverLastData;
	}

	public void setDriverLastData(String driverLastData) {
		this.driverLastData = driverLastData;
	}

	public String getReceivednotificationName() {
		return receivednotificationName;
	}

	public void setReceivednotificationName(String receivednotificationName) {
		this.receivednotificationName = receivednotificationName;
	}

	public String getReceivednotificationSentMessage() {
		return receivednotificationSentMessage;
	}

	public void setReceivednotificationSentMessage(
			String receivednotificationSentMessage) {
		this.receivednotificationSentMessage = receivednotificationSentMessage;
	}

	public String getReceivednotificationImage() {
		return receivednotificationImage;
	}

	public void setReceivednotificationImage(String receivednotificationImage) {
		this.receivednotificationImage = receivednotificationImage;
	}

	public String getReceivednotificationTripID() {
		return receivednotificationTripID;
	}

	public void setReceivednotificationTripID(String receivednotificationTripID) {
		this.receivednotificationTripID = receivednotificationTripID;
	}

	
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	protected SingleTon() {
		// Exists only to defeat instantiation.
	}
	
	public static SingleTon getInstance() {
		if(instance == null) {
			instance = new SingleTon();
		}
		return instance;
	}
	
	public static void setInstance(SingleTon ins) {
			instance = ins;
	}
	
	public String getVehicleMake() {
		return vehicleMake;
	}

	public void setVehicleMake(String vehicleMake) {
		this.vehicleMake = vehicleMake;
	}

	public String getVehicleModel() {
		return vehicleModel;
	}

	public void setVehicleModel(String vehicleModel) {
		this.vehicleModel = vehicleModel;
	}

	public String getVehicleYear() {
		return VehicleYear;
	}

	public void setVehicleYear(String vehicleYear) {
		VehicleYear = vehicleYear;
	}

	public String getVehicleLicencePlateNumber() {
		return vehicleLicencePlateNumber;
	}

	public void setVehicleLicencePlateNumber(String vehicleLicencePlateNumber) {
		this.vehicleLicencePlateNumber = vehicleLicencePlateNumber;
	}

	public String getVehicleCountryName() {
		return vehicleCountryName;
	}

	public void setVehicleCountryName(String vehicleCountryName) {
		this.vehicleCountryName = vehicleCountryName;
	}

	public String getVehicleStateName() {
		return vehicleStateName;
	}

	public void setVehicleStateName(String vehicleStateName) {
		this.vehicleStateName = vehicleStateName;
	}

	public ArrayList<State> getSelectedStates() {
		return selectedStates;
	}

	public void setSelectedStates(ArrayList<State> selectedStates) {
		this.selectedStates = selectedStates;
	}

	public String getBg_address1() {
		return bg_address1;
	}

	public void setBg_address1(String bg_address1) {
		this.bg_address1 = bg_address1;
	}

	public String getBg_address2() {
		return bg_address2;
	}

	public void setBg_address2(String bg_address2) {
		this.bg_address2 = bg_address2;
	}

	public String getBg_zipcode() {
		return bg_zipcode;
	}

	public void setBg_zipcode(String bg_zipcode) {
		this.bg_zipcode = bg_zipcode;
	}

	public String getBg_city() {
		return bg_city;
	}

	public void setBg_city(String bg_city) {
		this.bg_city = bg_city;
	}

	public String getBg_state() {
		return bg_state;
	}

	public void setBg_state(String bg_state) {
		this.bg_state = bg_state;
	}

	public String getBg_drivingLicenseNumber() {
		return bg_drivingLicenseNumber;
	}

	public void setBg_drivingLicenseNumber(String bg_drivingLicenseNumber) {
		this.bg_drivingLicenseNumber = bg_drivingLicenseNumber;
	}

	public String getBg_drivingLicenseState() {
		return bg_drivingLicenseState;
	}

	public void setBg_drivingLicenseState(String bg_drivingLicenseState) {
		this.bg_drivingLicenseState = bg_drivingLicenseState;
	}

	public String getBg_LicExoDate() {
		return bg_LicExoDate;
	}

	public void setBg_LicExoDate(String bg_LicExoDate) {
		this.bg_LicExoDate = bg_LicExoDate;
	}

	public String getBgDOB() {
		return bgDOB;
	}

	public void setBgDOB(String bgDOB) {
		this.bgDOB = bgDOB;
	}

	public String getBg_socialSecNumber() {
		return bg_socialSecNumber;
	}

	public void setBg_socialSecNumber(String bg_socialSecNumber) {
		this.bg_socialSecNumber = bg_socialSecNumber;
	}

	public ProfileInfoModel getProfileInfoModel() {
		return profileInfoModel;
	}

	public void setProfileInfoModel(ProfileInfoModel profileInfoModel) {
		this.profileInfoModel = profileInfoModel;
	}

	public boolean isEdited() {
		// TODO Auto-generated method stub
		return isEdited;
	}

	public void setEdited(boolean b) {
		isEdited = b;
	}

	public double getLatitute() {
		return latitute;
	}

	public void setLatitute(double latitute) {
		this.latitute = latitute;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public AdresssOfLocation getmLocation() {
		return mLocation;
	}

	public void setmLocation(AdresssOfLocation mLocation) {
		this.mLocation = mLocation;
	}

	public MainVehicleModel getMainVehicleModel() {
		return mainVehicleModel;
	}

	public void setMainVehicleModel(MainVehicleModel mainVehicleModel) {
		this.mainVehicleModel = mainVehicleModel;
	}

	public GetTripDetails getGetTripDetail() {
		return getTripDetail;
	}

	public void setGetTripDetail(GetTripDetails getTripDetail) {
		this.getTripDetail = getTripDetail;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public String getTotalFare() {
		return totalFare;
	}

	public void setTotalFare(String totalFare) {
		this.totalFare = totalFare;
	}
	
}
