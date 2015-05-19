package com.zira.util;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.twentyfourseven.zira.SplashScreen;
import com.zira.modal.City;
import com.zira.modal.Country;
import com.zira.modal.CreditCard;
import com.zira.modal.GetTripDetails;
import com.zira.modal.MainModal;
import com.zira.modal.MainVehicleModel;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.PromoCode;
import com.zira.modal.State;
import com.zira.modal.User;
import com.zira.modal.Vehicle;
import com.zira.modal.VehicleCategory;
import com.zira.modal.Vehicle.ModelArray;
import com.zira.modal.VehicleInfo;
import com.zira.modal.ZoneInfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class ZiraParser {

	public Context mContext;
public void ZiraParser(Context ctx)
{ctx=mContext;
	}
	public User parseLoginResponse(String response){
		User user = new User();

		try{
			JSONObject jsonObject = new JSONObject(response);
			user.setResult(jsonObject.getString("result"));
			user.setMessage(jsonObject.getString("message"));
			String userid=jsonObject.getString("userid");
			user.setUserid(userid);
			String isdrivermodeactivated=jsonObject.getString("isdrivermodeactivated");
			user.setIsdrivermodeactivated(Boolean.valueOf(isdrivermodeactivated));

			JSONArray promoCodeArray = jsonObject.getJSONArray("listPromoCodes");
			ArrayList<PromoCode> promoArrayList = new ArrayList<PromoCode>();

			for(int i = 0; i<promoCodeArray.length(); i++){
				JSONObject pObject = promoCodeArray.getJSONObject(i);

				PromoCode promoCode = new PromoCode();
				promoCode.setValue(pObject.getString("value"));
				promoCode.setPromoCode(pObject.getString("promocode"));
				promoCode.setCreationDate(pObject.getString("datecreated"));
				promoCode.setUsedDate(pObject.getString("dateused"));

				promoArrayList.add(promoCode);
			}
			user.setPromoCodeList(promoArrayList);

			JSONArray creditCardArray = jsonObject.getJSONArray("listCreditCards");
			ArrayList<CreditCard> cardArrayList = new ArrayList<CreditCard>();

			for(int i = 0; i<creditCardArray.length(); i++){
				JSONObject cObject = creditCardArray.getJSONObject(i);

				CreditCard creditCard = new CreditCard();
				creditCard.setCardId(cObject.getString("cardid"));
				creditCard.setCardNumber(cObject.getString("creditcardnumber"));
				creditCard.setExpiry(cObject.getString("creditcardexpiry"));
				creditCard.setCvv(cObject.getString("creditcardcvv"));
				String isdefault=cObject.getString("isdefault");
				if(isdefault.equalsIgnoreCase("True"))
				{
					creditCard.setIsDefault(true);
					}
				else
				{
					try{
					int isDefault = Integer.parseInt(isdefault);
					if(isDefault == 1)
						creditCard.setIsDefault(true);
					else
						creditCard.setIsDefault(false);
					}
					catch(Exception e)
					{}
				}
				cardArrayList.add(creditCard);
			}
			user.setCardList(cardArrayList);
			
			Editor e=SplashScreen.prefs.edit();
			e.putString("isdrivermodeactivated", isdrivermodeactivated);
			e.putString("riderid", userid);
			e.commit();
			System.err.println("USERIFDDDDDDDDDDDDDDD="+userid);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return user;
	}

	public MainModal parseCountries(String response){
		MainModal modalInstance = new MainModal();
		ArrayList<Country> countryArrayList = new ArrayList<Country>();

		//ArrayList<String> countryid_ArrayList = new ArrayList<String>();
		Country dummyCountry = new Country();
		dummyCountry.setCountryName("Licence Plate Country");
		countryArrayList.add(dummyCountry);

		try{
			
			Util.arraylist_country().clear();
			Util.arraylist_countryid().clear();
			
			JSONObject jsonObject = new JSONObject(response);
			modalInstance.setResult(jsonObject.getString("result"));
			modalInstance.setMessage(jsonObject.getString("message"));
			//			
			JSONArray countryArray = jsonObject.getJSONArray("ListCountry");

			for(int i = 0; i<countryArray.length(); i++){
				JSONObject pObject = countryArray.getJSONObject(i);

				Country country = new Country();
				String countryname=pObject.getString("countryName");
				country.setCountryName(countryname);
				String countryid=pObject.getString("countryId");
				country.setCountryID(countryid);
//	
				Util.arraylist_country().add(countryname);
				Util.arraylist_countryid().add(countryid);
				
				countryArrayList.add(country);
			}

			modalInstance.setCountryList(countryArrayList);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return modalInstance;
	}

	public MainModal parseStates(String response){


		MainModal modalInstance = new MainModal();
		ArrayList<State> stateArrayList = new ArrayList<State>();

		State dummyState = new State();
		dummyState.setStateName("Licence Plate State");

		stateArrayList.add(dummyState);

		try{
			//Util.arraylist_state().clear();
			//Util.arraylist_stateid().clear();
			
			JSONObject jsonObject = new JSONObject(response);
			modalInstance.setResult(jsonObject.getString("result"));
			modalInstance.setMessage(jsonObject.getString("message"));

			JSONArray stateArray = jsonObject.getJSONArray("ListState");
			
			for(int i = 0; i<stateArray.length(); i++){
				JSONObject pObject = stateArray.getJSONObject(i);

				State state = new State();
				String statename=pObject.getString("StateName");
				state.setStateName(statename);
				String stateid=pObject.getString("StateId");
				state.setStateId(stateid);
				
				//Util.arraylist_state().add(statename);
				//Util.arraylist_stateid().add(stateid);
//				
				stateArrayList.add(state);
			}
			modalInstance.setStateList(stateArrayList);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return modalInstance;
	}
	
	
	public MainModal parseCity(String response){


		MainModal modalInstance = new MainModal();
		ArrayList<City> cityArrayList = new ArrayList<City>();

		City dummyCity = new City();
		dummyCity.setCityname("City");

		cityArrayList.add(dummyCity);

		try{
			
			
			JSONObject jsonObject = new JSONObject(response);
			modalInstance.setResult(jsonObject.getString("result"));
			modalInstance.setMessage(jsonObject.getString("message"));

			JSONArray cityArray = jsonObject.getJSONArray("ListCity");
			
			for(int i = 0; i<cityArray.length(); i++){
				JSONObject pObject = cityArray.getJSONObject(i);

				City city = new City();
				String cityname=pObject.getString("CityName");
				city.setCityname(cityname);
				String cityid=pObject.getString("CityId");
				city.setCityId(cityid);
				
				cityArrayList.add(city);
			}
			modalInstance.setCityList(cityArrayList);
		}catch (Exception e) {
			// TODO: handle exception
		}
		return modalInstance;
	}

	public MainModal parseVehicles(String response){
		MainModal modalInstance = new MainModal();

		ArrayList<Vehicle> vehicleList = new ArrayList<Vehicle>();


		Vehicle dummyVehicle = new Vehicle();
		dummyVehicle.setVehicleMakeName("Choose Vehicle Make");
		ArrayList<ModelArray> modalData = new ArrayList<ModelArray>();

		ModelArray modalObj = new Vehicle().new ModelArray();
		modalObj.setVehiclemodalName("Choose Vehicle Model");
		modalObj.setVehiclemodalYear("Choose Vehicle Year");

		dummyVehicle.setListModalData(modalData);
		vehicleList.add(dummyVehicle);

		try{
			
			Util.arraylist_modal().clear();
			Util.arraylist_modelid().clear();
			
			JSONObject jsonObject = new JSONObject(response);
			modalInstance.setResult(jsonObject.getString("result"));
			modalInstance.setMessage(jsonObject.getString("message"));

			JSONArray vehicleArray = jsonObject.getJSONArray("ListVehicleMakeName");

			for(int i = 0; i<vehicleArray.length(); i++){
				Vehicle vehicle = new Vehicle();

				JSONObject vObject = vehicleArray.getJSONObject(i);
				vehicle.setVehicleMakeName(vObject.getString("vehicleMakeName"));

				JSONArray modalArray = vObject.getJSONArray("listModalData");

				ArrayList<ModelArray> listModalData = new ArrayList<ModelArray>();

				for(int j = 0; j<modalArray.length(); j++){
					JSONObject mObject = modalArray.getJSONObject(j);

					ModelArray modal = new Vehicle().new ModelArray();
					String modelname=mObject.getString("vehiclemodalName");
					modal.setVehiclemodalName(modelname);
					
					modal.setVehiclemodalYear(mObject.getString("vehiclemodalYear"));
					
					String modelid=mObject.getString("vechilemodelID");
					//System.err.println(""+modelid);
					modal.setVehiclemodelID(modelid);
//					
					Util.arraylist_modal().add(modelname);
					Util.arraylist_modelid().add(modelid);
					
					listModalData.add(modal);
				}

				vehicle.setListModalData(listModalData);
				vehicleList.add(vehicle);
			}

			modalInstance.setVehicleList(vehicleList);

		}catch (Exception e) {
			// TODO: handle exception
		}
		return modalInstance;
	}

	public ProfileInfoModel profileInfo(String response) {
		//SharedPreferences prefs=this.geta.getSharedPreferences("Zira", MODE_PRIVATE);
		ProfileInfoModel mInfoModel =new ProfileInfoModel();

		try{
			JSONObject jsonObject = new JSONObject(response);			
			mInfoModel.setResult(jsonObject.getString("result"));
			mInfoModel.setMessage(jsonObject.getString("message"));

			/********Getting Information**********/

			mInfoModel.setUserid(jsonObject.getString("userid"));
			
			String userimage=jsonObject.getString("image");
			mInfoModel.setImage(userimage);
			
			mInfoModel.setEmail(jsonObject.getString("email"));
			
			String firstname=jsonObject.getString("firstname");
			mInfoModel.setFirstname(firstname);
			
			String lastname=jsonObject.getString("lastname");
			mInfoModel.setLastname(lastname);
			
			String usermobile=jsonObject.getString("mobile");
			mInfoModel.setMobile(usermobile);
			
			mInfoModel.setPassword(jsonObject.getString("password"));
			mInfoModel.setCreditcardnumber(jsonObject.getString("creditcardnumber"));
			mInfoModel.setCreditcardexpiry(jsonObject.getString("creditcardexpiry"));
			mInfoModel.setCreditcardcvv(jsonObject.getString("creditcardcvv"));
			mInfoModel.setAddress(jsonObject.getString("address"));
			mInfoModel.setAddress1(jsonObject.getString("address1"));
			mInfoModel.setAddress2(jsonObject.getString("address2"));
			mInfoModel.setCity(jsonObject.getString("city"));
			mInfoModel.setState(jsonObject.getString("state"));


			mInfoModel.setVechile_img_location(jsonObject.getString("vechile_img_location"));
			mInfoModel.setVechile_make(jsonObject.getString("vechile_make"));
			mInfoModel.setVechile_model(jsonObject.getString("vechile_model"));
			mInfoModel.setLicenseplatenumber(jsonObject.getString("licenseplatenumber"));

			mInfoModel.setVechile_year(jsonObject.getString("vechile_year"));
			mInfoModel.setLicenseplatecountry(jsonObject.getString("licenseplatecountry"));
			mInfoModel.setLicenseplatestate(jsonObject.getString("licenseplatestate"));

			mInfoModel.setZipcode(jsonObject.getString("zipcode"));

			mInfoModel.setDrivinglicensestate(jsonObject.getString("drivinglicensestate"));
			mInfoModel.setDrivinglicensenumber(jsonObject.getString("drivinglicensenumber"));
			mInfoModel.setDrivinglicenseexpirationdate(jsonObject.getString("drivinglicenseexpirationdate"));
			mInfoModel.setDateofbirth(jsonObject.getString("dateofbirth"));
			mInfoModel.setSocialsecuritynumber(jsonObject.getString("socialsecuritynumber"));
			mInfoModel.setLicense_img_location(jsonObject.getString("license_img_location"));
			mInfoModel.setRc_img_location(jsonObject.getString("rc_img_location"));
			mInfoModel.setMedicalcertificate_img_location(jsonObject.getString("medicalcertificate_img_location"));
			mInfoModel.setRider_rating(jsonObject.getString("rider_rating"));
			mInfoModel.setDriver_rating(jsonObject.getString("driver_rating"));
			
			//edit code
			mInfoModel.setStateID(jsonObject.getString("stateID"));
			mInfoModel.setVechile_modelID(jsonObject.getString("vechile_modelID"));
			mInfoModel.setLicenseplatestateID(jsonObject.getString("licenseplatestateID"));
			mInfoModel.setDrivinglicensestateID(jsonObject.getString("drivinglicensestateID"));
			mInfoModel.setLicenseplatecountryID(jsonObject.getString("licenseplatecountryID"));
			mInfoModel.setCityID(jsonObject.getString("cityId"));
			String PreferedVehicleType=jsonObject.getString("PreferedVehicleType");
			mInfoModel.setPreferedVehicleType(PreferedVehicleType);
			//edit code
			
			String vehicle_status=jsonObject.getString("vehicle_status");
			mInfoModel.setVehicle_status(vehicle_status);
			String vehiceltype=jsonObject.getString("vechiletypeName");
			mInfoModel.setVechiletype(vehiceltype);
			 
			 
			Editor e=SplashScreen.prefs.edit();
			e.putString("userimage", userimage);
			e.putString("userfname", firstname);
			e.putString("userlname", lastname);
			e.putString("usermobile", usermobile);
			e.putString("vehiceltype", vehiceltype);
			e.putString("vehiclestatus", vehicle_status);
			e.putString("preferedvehicletype", PreferedVehicleType);
			e.commit();
			Log.e("getprofile", firstname+lastname+userimage+usermobile+"  v=="+vehiceltype);
			
		}catch (Exception e) {


		}
		return mInfoModel;
	}



	public MainVehicleModel parseVehicleInZone(String response){

		MainVehicleModel modalInstance = new MainVehicleModel();

		try{
			JSONObject jsonObject = new JSONObject(response);
			modalInstance.setResult(jsonObject.getString("result"));
			modalInstance.setMessage(jsonObject.getString("message"));
			modalInstance.setPreffervehicletype(jsonObject.getString("preffervehicletype"));


			modalInstance.setSafetyFee(jsonObject.getString("SafetyFee"));				
			modalInstance.setSplitfees(jsonObject.getString("splitfees"));
			//modalInstance.setCancellationFee(jsonObject.getString("CancellationFee"));
			modalInstance.setUseragreement(jsonObject.getString("useragreement"));
			
			
			/*JSONArray vehicleArray = jsonObject.getJSONArray("ListVehicleCategory");	
			ArrayList<VehicleCategory> listModalData = new ArrayList<VehicleCategory>();

			for(int i = 0; i<vehicleArray.length(); i++){

				VehicleCategory vehicle = new VehicleCategory();
				JSONObject vObject = vehicleArray.getJSONObject(i);
				vehicle.setVehicleType(vObject.getString("VehicleType"));
				vehicle.setVehicleCapacity(vObject.getString("VehicleCapacity"));
				vehicle.setMinFare(vObject.getString("MinFare"));
				listModalData.add(vehicle);
			}*/
		//	modalInstance.setVehicleCategryList(listModalData);	

			JSONArray vehicleInfoArray = jsonObject.getJSONArray("ListVehicleInfo");	
			ArrayList<VehicleInfo> vehicleInfoModalData = new ArrayList<VehicleInfo>();

			for(int i = 0; i<vehicleInfoArray.length(); i++){					
				VehicleInfo vehicle = new VehicleInfo();
				JSONObject vObject = vehicleInfoArray.getJSONObject(i);
				vehicle.setDriverid(vObject.getString("driverid"));
				vehicle.setVehicleimage(vObject.getString("vehicleimage"));
				vehicle.setLongitude(vObject.getString("longitude"));				
				vehicle.setLatitude(vObject.getString("latitude"));
				vehicle.setDistance(vObject.getString("distance"));
				vehicle.setTime(vObject.getString("Time"));				
				vehicle.setVehicleModal(vObject.getString("vehicleModal"));
				vehicle.setVehicleYear(vObject.getString("vehicleYear"));
				vehicle.setVehicleMake(vObject.getString("vehicleMake"));
				vehicleInfoModalData.add(vehicle);
			}
			modalInstance.setVehicleInfoList(vehicleInfoModalData);
			
			JSONArray vehicleZone = jsonObject.getJSONArray("ListZoneInfo");	
			ArrayList<ZoneInfo> zoneData = new ArrayList<ZoneInfo>();

			for(int i = 0; i<vehicleZone.length(); i++){

				ZoneInfo zoneInfo = new ZoneInfo();
				JSONObject vObject = vehicleZone.getJSONObject(i);

/*				zoneInfo.setZirax_hourfull(vObject.getString("ZoneID"));
				zoneInfo.setZiraplus_base(vObject.getString("ZoneName"));
				//zoneInfo.setZiraplus_min(vObject.getString("zip_code"));

				zoneInfo.setZirax_base(vObject.getString("zirax_base"));
				zoneInfo.setZirax_min(vObject.getString("zirax_min"));				
				zoneInfo.setZirax_price(vObject.getString("zirax_price"));
				zoneInfo.setZirax_surge(vObject.getString("zirax_surge"));
				//zoneInfo.setZirax_hour(vObject.getString("zirax_hour"));				
				//zoneInfo.setZirax_hourfull(vObject.getString("zirax_hourfull"));

				zoneInfo.setZiraplus_base(vObject.getString("ziraplus_base"));
				zoneInfo.setZiraplus_min(vObject.getString("ziraplus_min"));				
				zoneInfo.setZiraplus_price(vObject.getString("ziraplus_price"));
				zoneInfo.setZiraplus_surge(vObject.getString("ziraplus_surge"));
				//zoneInfo.setZiraplus_hour(vObject.getString("ziraplus_hour"));			
				//zoneInfo.setZiraplus_hourfull(vObject.getString("ziraplus_hourfull"));

				zoneInfo.setZirasuv_base(vObject.getString("zirasuv_base"));
				zoneInfo.setZirsuv_min(vObject.getString("zirsuv_min"));				
				zoneInfo.setZirasuv_price(vObject.getString("zirasuv_price"));
				zoneInfo.setZirasuv_surge(vObject.getString("zirasuv_surge"));
				//zoneInfo.setZirasuv_hour(vObject.getString("zirasuv_hour"));								
				//zoneInfo.setZirasuv_hourfull(vObject.getString("zirasuv_hourfull"));

				zoneInfo.setSafetycharges(vObject.getString("safetycharges"));				
				zoneInfo.setSplitfees(vObject.getString("splitfees"));
				zoneInfo.setCancellationcharges(vObject.getString("cancellationcharges"));
				zoneInfo.setUseragreement(vObject.getString("useragreement"));*/
				
				
				/////edit/////////
				String vehicelname=vObject.getString("VechileName");
				zoneInfo.setVechileName(vehicelname);
				String vehicelid=vObject.getString("VechileID");
				zoneInfo.setVehicleID(vehicelid);
				
				if(vehicelname.equalsIgnoreCase("ZiraE"))
				{
					Editor ed=SplashScreen.prefs.edit();
					ed.putString("zirae", vehicelid);
					ed.commit();
					
					System.err.println("ZiraE="+vehicelid);
					
					}
				else if(vehicelname.equalsIgnoreCase("ZiraPlus"))
				{
					Editor ed=SplashScreen.prefs.edit();
					ed.putString("ziraplus", vehicelid);
					ed.commit();
					System.err.println("ZiraPlus="+vehicelid);
					}
				else if(vehicelname.equalsIgnoreCase("ZiraTaxi"))
				{	
					Editor ed=SplashScreen.prefs.edit();
					ed.putString("zirataxi", vehicelid);
					ed.commit();
					System.err.println("ZiraTaxi="+vehicelid);
					
					}
				else if(vehicelname.equalsIgnoreCase("ZiraLux"))
				{
					Editor ed=SplashScreen.prefs.edit();
					ed.putString("ziralux", vehicelid);
					ed.commit();
					System.err.println("ZiraLux="+vehicelid);
					
					}
				
			
				
				zoneInfo.setVehicleCapacity(vObject.getString("VehicleCapacity"));
				zoneInfo.setBasePrice(vObject.getString("BasePrice"));
				zoneInfo.setCancellationcharges(vObject.getString("CancellationFee"));
				zoneInfo.setMilesPrice(vObject.getString("MilesPrice"));
				zoneInfo.setMinprice(vObject.getString("Minprice"));				
				zoneInfo.setSurgeprice(vObject.getString("Surgeprice"));
					

				zoneData.add(zoneInfo);
			
			}
			modalInstance.setZoneInfoList(zoneData);	


			



		}catch (Exception e) {
			System.err.println("zoneinfo======="+e);
		}
		return modalInstance;
	}

	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public GetTripDetails parsegetTripDetails(String response) {

		GetTripDetails getTripDetails =new GetTripDetails();
		ArrayList<GetTripDetails> arraylist_getTripDetails = new ArrayList<GetTripDetails>();
		try{

			JSONObject jsonObject = new JSONObject(response);			
			getTripDetails.setGetTrip_result(jsonObject.getString("result"));
			getTripDetails.setGetTrip_message(jsonObject.getString("message"));
			
			getTripDetails.setGetTrip_Distance(jsonObject.getString("trip_miles_est"));
			getTripDetails.setGetTrip_ETA(jsonObject.getString("trip_time_est"));
			getTripDetails.setGetTrip_SuggesstionFare(jsonObject.getString("offered_fare"));
			
			String starting_loc=jsonObject.getString("starting_loc");
			getTripDetails.setGetTrip_StartingAddress(starting_loc);
			String ending_loc=jsonObject.getString("ending_loc");
			getTripDetails.setGetTrip_DestinationAddress(ending_loc);
			getTripDetails.setGetTrip_riderRating(jsonObject.getString("rider_rating"));

			getTripDetails.setGetTrip_driveRating(jsonObject.getString("driver_rating"));
			getTripDetails.setGetTrip_PickUpDate(jsonObject.getString("trip_request_pickup_date"));
			getTripDetails.setGetTrip_RiderImage(jsonObject.getString("rider_img"));
			getTripDetails.setGetTrip_DriverImage(jsonObject.getString("driver_img"));
			getTripDetails.setGetTrip_RiderName(jsonObject.getString("rider_name"));
			getTripDetails.setGetTrip_DriverName(jsonObject.getString("driver_name"));

			getTripDetails.setGetTrip_Vehicelmake(jsonObject.getString("vehicle_make"));
			getTripDetails.setGetTrip_Vehicle_model(jsonObject.getString("vehicle_model"));
			getTripDetails.setGetTrip_Vehicle_year(jsonObject.getString("vehicle_year"));
			getTripDetails.setGetTrip_Trip_Trip_Accept_date(jsonObject.getString("trip_accept_date"));
			getTripDetails.setGetTrip_Trip_Arrive_date(jsonObject.getString("trip_arrive_date"));
			getTripDetails.setGetTrip_Trip_Begin_date(jsonObject.getString("trip_begin_date"));
			
			String driverid=jsonObject.getString("driverid");
			String riderid=jsonObject.getString("riderid");
			getTripDetails.setGetTrip_DriverId(driverid);
			getTripDetails.setGetTrip_RiderID(riderid);
			
			String startlat=jsonObject.getString("start_lat");
			getTripDetails.setGetTrip_StartingLatitude(startlat);
			
			String startlon=jsonObject.getString("start_lon");
			getTripDetails.setGetTrip_StartingLongitude(startlon);
			String endlat=jsonObject.getString("end_lat");
			getTripDetails.setGetTrip_EndLatitude(endlat);
			String endlong=jsonObject.getString("end_lon");
			getTripDetails.setGetTrip_EndLongitude(endlong);

			getTripDetails.setGetTrip_Trip_Total_amount(jsonObject.getString("trip_total_amount"));


			arraylist_getTripDetails.add(getTripDetails);
			
			Editor e=SplashScreen.prefs.edit();
			e.putString("tripstarting_loc", starting_loc);
			e.putString("tripdestinationadd", ending_loc);
			
			e.putString("tripriderid", riderid);
			e.putString("tripdriverid", driverid);
			
			e.putString("tripstarttlat", startlat);
			e.putString("tripstartlong", startlon);
			e.putString("tripdestlat", endlat);
			e.putString("tripdestlong", endlong);
			e.commit();
			

		}
		catch(Exception e){
			System.out.println(e);
			Log.d("tag", "Error :"+e); }  


		getTripDetails.setArraylist_getTripDetails(arraylist_getTripDetails);
		System.out.println(getTripDetails.toString());
		return getTripDetails;
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////
	
	public GetTripDetails getLastActiveDetail(String response) {

		GetTripDetails getTripDetails =new GetTripDetails();
		ArrayList<GetTripDetails> arraylist_getTripDetails = new ArrayList<GetTripDetails>();
		try{

			JSONObject jsonObject = new JSONObject(response);	
			String result=jsonObject.getString("result");
			getTripDetails.setGetTrip_result(result);
			getTripDetails.setGetTrip_message(jsonObject.getString("message"));
			getTripDetails.setTrip_status(jsonObject.getString("tripstatus"));
			if(result.equals("0"))
			{
				String tripid=jsonObject.getString("tripid");
				Editor e=SplashScreen.prefs.edit();
				e.putString("activetripid", tripid);
				e.commit();
			getTripDetails.setTripid(tripid);
			getTripDetails.setGetTrip_Distance(jsonObject.getString("trip_miles_est"));
			getTripDetails.setGetTrip_ETA(jsonObject.getString("trip_time_est"));
			getTripDetails.setGetTrip_SuggesstionFare(jsonObject.getString("offered_fare"));
			
			String starting_loc=jsonObject.getString("starting_loc");
			getTripDetails.setGetTrip_StartingAddress(starting_loc);
			String ending_loc=jsonObject.getString("ending_loc");
			getTripDetails.setGetTrip_DestinationAddress(ending_loc);
			getTripDetails.setGetTrip_riderRating(jsonObject.getString("rider_rating"));

			getTripDetails.setGetTrip_driveRating(jsonObject.getString("driver_rating"));
			getTripDetails.setGetTrip_PickUpDate(jsonObject.getString("trip_request_pickup_date"));
			getTripDetails.setGetTrip_RiderImage(jsonObject.getString("rider_img"));
			getTripDetails.setGetTrip_DriverImage(jsonObject.getString("driver_img"));
			getTripDetails.setGetTrip_RiderName(jsonObject.getString("rider_name"));
			getTripDetails.setGetTrip_DriverName(jsonObject.getString("driver_name"));

			getTripDetails.setGetTrip_Vehicelmake(jsonObject.getString("vehicle_make"));
			getTripDetails.setGetTrip_Vehicle_model(jsonObject.getString("vehicle_model"));
			getTripDetails.setGetTrip_Vehicle_year(jsonObject.getString("vehicle_year"));
			getTripDetails.setGetTrip_Trip_Trip_Accept_date(jsonObject.getString("trip_accept_date"));
			getTripDetails.setGetTrip_Trip_Arrive_date(jsonObject.getString("trip_arrive_date"));
			getTripDetails.setGetTrip_Trip_Begin_date(jsonObject.getString("trip_begin_date"));

			String driverid=jsonObject.getString("driverid");
			String riderid=jsonObject.getString("riderid");
			getTripDetails.setGetTrip_DriverId(driverid);
			getTripDetails.setGetTrip_RiderID(riderid);
			
			String startlat=jsonObject.getString("start_lat");
			getTripDetails.setGetTrip_StartingLatitude(startlat);
			
			String startlon=jsonObject.getString("start_lon");
			getTripDetails.setGetTrip_StartingLongitude(startlon);
			String endlat=jsonObject.getString("end_lat");
			getTripDetails.setGetTrip_EndLatitude(endlat);
			String endlong=jsonObject.getString("end_lon");
			getTripDetails.setGetTrip_EndLongitude(endlong);

			getTripDetails.setGetTrip_Trip_Total_amount(jsonObject.getString("trip_total_amount"));


			arraylist_getTripDetails.add(getTripDetails);
			
			Editor e1=SplashScreen.prefs.edit();
			e1.putString("", starting_loc);
			e.putString("tripstarting_loc", starting_loc);
			e.putString("tripdestinationadd", ending_loc);
			
			e.putString("tripriderid", riderid);
			e.putString("tripdriverid", driverid);
			
			e.putString("tripstarttlat", startlat);
			e.putString("tripstartlong", startlon);
			e.putString("tripdestlat", endlat);
			e.putString("tripdestlong", endlong);
			e1.commit();
			}

		}
		catch(Exception e){
			System.out.println(e);
			Log.d("tag", "Error :"+e); }  


		getTripDetails.setArraylist_getTripDetails(arraylist_getTripDetails);
		System.out.println(getTripDetails.toString());
		return getTripDetails;
	}
}

