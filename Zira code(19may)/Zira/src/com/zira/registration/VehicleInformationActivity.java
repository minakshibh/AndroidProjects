package com.zira.registration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.Utility;
import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;

import com.zira.login.ForgotPassword;
import com.zira.modal.Country;
import com.zira.modal.MainModal;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.State;
import com.zira.modal.Vehicle;
import com.zira.modal.Vehicle.ModelArray;
import com.zira.profile.GetProfile;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;


@SuppressLint("NewApi")
public class VehicleInformationActivity extends Activity implements AsyncResponseForZira{

	private String fetchCountry = "GetCountryList";
	private String fetchVehicleModals = "GetVehicleModals", fetchStates ="GetStateList";
	String countryid;
	private Button saveAndContinueButton;
	private EditText licensePlateNumberEditText;
	
	private Spinner vehicleMakeNameSpinner;
	private Spinner vehiclemodalNameSpinner;	
	private Spinner vehiclemodalYearSpinner;
	private Spinner countryNameSpinner;
	private Spinner StateNameSpinner;	

	private MainModal mainModal;
	private ArrayList<Country> countryList = new ArrayList<Country>();
	private ArrayList<State> stateList = new ArrayList<State>();
	private ArrayList<Vehicle> vehicleArrayList = new ArrayList<Vehicle>();
	
	private int makePos, modalPos,value=-1;
	private ZiraParser parser;
	private ProfileInfoModel mProfileInfoModel;
	private String gettingCountry;
	private int gettingVehicleMakePosition,gettingVehicleModelPosition,gettingVehicleYearPosition,gettingCountryPosition,gettingStatePosition;
	private boolean firstTime=true;
	int flag_vehicelspinner=0;
	int flag_countryspinner=0;
	int flag_statespinner=0;
	int flag=0;
	int vehicle_flag=0;
	
	static ArrayList<Activity> regActivities;
/*	Editor editor;
	SharedPreferences prefs3;*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vehicle_information);
		
		
		regActivities = new ArrayList<Activity>();
	
		regActivities.add(VehicleInformationActivity.this);
		initialiseVariable();
		DownloadData();
		initialiseListener();
		
		//Selecteddata();
	
		//setApapterForState();
		//setApapterForCountry();
	}
	
/*	private void Selecteddata() {
		//SingleTon.getInstance().setVehicleMake(vehicleMakeNameSpinner.getSelectedItem().toString());
		String countyid=prefs3.getString("countryid","");
		System.err.println("countyid"+countyid);
		String makename=prefs3.getString("makename","");
		System.err.println("makename"+makename);
		String stateid=prefs3.getString("stateid","");
		System.err.println("stateid"+stateid);
		String year=prefs3.getString("year","");
		System.err.println("year"+year);
		String licplateno=prefs3.getString("licplateno","");
		System.err.println("licplateno"+licplateno);
		
		
	}*/

	private void initialiseVariable() {
		parser = new ZiraParser();
		//prefs3 = getSharedPreferences("Ziradata", MODE_PRIVATE);
		//editor=prefs3.edit();
		mProfileInfoModel=SingleTon.getInstance().getProfileInfoModel();
		licensePlateNumberEditText=(EditText)findViewById(R.id.licensePlateNumber);
		saveAndContinueButton=(Button)findViewById(R.id.SaveAndContinue);
		vehicleMakeNameSpinner = (Spinner) findViewById(R.id.vehicleMakeName);
		vehiclemodalNameSpinner=(Spinner)findViewById(R.id.vehiclemodalName);	
		vehiclemodalYearSpinner=(Spinner)findViewById(R.id.vehiclemodalYear);
		countryNameSpinner=(Spinner)findViewById(R.id.countryName);
		StateNameSpinner=(Spinner)findViewById(R.id.StateName);

		
		if(SingleTon.getInstance().isEdited()){
			licensePlateNumberEditText.setText(mProfileInfoModel.getLicenseplatenumber());	
		/*	if(mProfileInfoModel.getLicenseplatenumber().equals(""))
			{
				licensePlateNumberEditText.setText(prefs3.getString("licplateno", ""));
				}
			else
			{
				
				}*/
		}

	}
	
	
	private void DownloadData() {
		if(Util.isNetworkAvailable(VehicleInformationActivity.this))
		{
		AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(VehicleInformationActivity.this, fetchCountry,new ArrayList<NameValuePair>(), true, "Please wait...");
		mWebPageTask.delegate = (AsyncResponseForZira) this;
		mWebPageTask.execute();
		
		
		//Log.e("fetchVehicleModals", mWebPageTask.toString());
		AsyncTaskForZira mFetchVehicleTask = new AsyncTaskForZira(VehicleInformationActivity.this, fetchVehicleModals,new ArrayList<NameValuePair>(), true, "Please wait...");
		mFetchVehicleTask.delegate = (AsyncResponseForZira) this;
		mFetchVehicleTask.execute();
		}
		else
		{
			Util.alertMessage(VehicleInformationActivity.this, "Please check your internet connection");
		}
	}

	private void initialiseListener() {
		saveAndContinueButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				emptyFieldCheck();
			}
		});

		vehicleMakeNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {				
											
				makePos = position;		
				vehicle_flag++;
			
				System.err.println("vehicle_flag"+vehicle_flag);
				SingleTon.getInstance().setVehicleMake(vehicleMakeNameSpinner.getSelectedItem().toString());
//				editor.putString("makename", ""+vehicleMakeNameSpinner.getSelectedItem().toString());
//				editor.commit();
				if(makePos>0){					
					setApapterForVehicleModel(vehicleArrayList.get(makePos).getListModalData());
				}
				else{
					ArrayList<ModelArray> modelArray = new ArrayList<Vehicle.ModelArray>();
					ModelArray mObj = new Vehicle().new ModelArray();
					mObj.setVehiclemodalName("Choose Vehicle Modal");
					mObj.setVehiclemodalYear("Choose Vehicle Year");
					mObj.setVehiclemodelID("0");
					modelArray.add(mObj);
					
					setApapterForVehicleModel(modelArray);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		//vehiclemodalNameSpinner.setSelection(Integer.parseInt(mProfileInfoModel.getVechile_modelID()));
	
		vehiclemodalNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				
				modalPos = position;				
				ModelArray modalData;
				if(makePos>0)
					modalData= (ModelArray)vehicleArrayList.get(makePos).getListModalData().get(modalPos);
				else{
					modalData = new Vehicle().new ModelArray();
					modalData.setVehiclemodalName("Choose Vehicle Modal");
					modalData.setVehiclemodalYear("Choose Vehicle Year");
					modalData.setVehiclemodelID("0");
				}
			//	int position1=position+1;
				//SingleTon.getInstance().setVehicleModel(""+position1);
				//vehiclemodalNameSpinner.getSelectedItem().toString());
				
				String modelname=modalData.getVehiclemodalName();
				String modelid=modalData.getVehiclemodelID();
			//	System.err.println("index modelid="+modelid);
				SingleTon.getInstance().setVehicleModel(""+modelid);
//				editor.putString("modelid", ""+modelid);
//				editor.commit();

				String year = modalData.getVehiclemodalYear();	
				setApapterForVehicleYear(year);
				
				//edit code
/*			if(flag_vehicelspinner==0)	
			{
				flag_vehicelspinner=1;
				for(int i=0;i<Util.arraylist_modelid().size();i++){
					if(mProfileInfoModel.getVechile_modelID().equalsIgnoreCase(Util.arraylist_modelid().get(i)))
					{
						//vehiclemodalNameSpinner.setSelection(i);
						System.err.println("vehiclemodalNameSpinner=="+i);
						}
					}
				}*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});		
		
		SingleTon.getInstance().setVehicleCountryName(""+Integer.parseInt(mProfileInfoModel.getLicenseplatecountryID()));
		
		countryNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				
				flag++;
				//if(position==0)
					//((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
				
				/*SingleTon.getInstance().setVehicleCountryName(countryNameSpinner.getSelectedItem().toString());*/

			Country country = (Country)countryNameSpinner.getSelectedItem();
			
			countryid =  country.getCountryID();
			SingleTon.getInstance().setVehicleCountryName(""+countryid);
//			editor.putString("countryid", ""+countryid);
//			editor.commit();
			//System.err.println("index county="+countryid);
	
				if(position==0){
					stateList = new ArrayList<State>();
					State dummyState = new State();
					stateList.add(dummyState);
				}
				else{
					//try{
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
						if(value==1){						
							nameValuePairs.add(new BasicNameValuePair("countryname", countryid));						
						  }
						else{
			            nameValuePairs.add(new BasicNameValuePair("countryname",""+ countryid));
						}
					//	if(!(gettingCountry.equals("Licence Plate Country"))){
							Log.e("fetchStates", nameValuePairs.toString());
							
							if(Util.isNetworkAvailable(VehicleInformationActivity.this))
							{	
									AsyncTaskForZira mFetchStates = new AsyncTaskForZira(VehicleInformationActivity.this, fetchStates,nameValuePairs, true, "Please wait...");
									mFetchStates.delegate = (AsyncResponseForZira) VehicleInformationActivity.this;
									mFetchStates.execute();
									}
							else
							{
								Util.alertMessage(VehicleInformationActivity.this, "Please check your internet connection");
								}
							
				  }
				//	}
				/*	catch(Exception e)
					{
						
					}*/
			//	}
				
				//edit code
				//countryNameSpinner.setSelection(Integer.parseInt(mProfileInfoModel.getLicenseplatecountryID()));
				//System.err.println("country=="+Integer.parseInt(mProfileInfoModel.getLicenseplatecountryID()));
	/*		if(flag_countryspinner==0)
			{
				flag_countryspinner=1;
				String getcountryid="2";//mProfileInfoModel.getLicenseplatecountryID();
				for(int i=0;i<Util.arraylist_countryid().size();i++){
					if(getcountryid.equalsIgnoreCase(Util.arraylist_countryid().get(i)))
					{
						countryNameSpinner.setSelection(i);
						System.err.println("countryNameSpinner=="+i);
						}
					}
				}*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		SingleTon.getInstance().setVehicleStateName(""+mProfileInfoModel.getLicenseplatestateID());
		
		StateNameSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
	
				//if(position==0)
					//((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
				//SingleTon.getInstance().setVehicleStateName(""+position);
				
				State state = (State)StateNameSpinner.getSelectedItem();
				
				String stateid = state.getStateId();
				//System.err.println("index="+stateid);
				SingleTon.getInstance().setVehicleStateName(""+stateid);
//				editor.putString("stateid", ""+stateid);
//				editor.commit();
				//edit code
/*			if(flag_statespinner==0)	
			{
				flag_statespinner=1;
				for(int i=0;i<Util.arraylist_stateid().size();i++){
					String getstateid="1";//mProfileInfoModel.getLicenseplatestateID();
					if(getstateid.equalsIgnoreCase(Util.arraylist_stateid().get(i)))
					{
						StateNameSpinner.setSelection(i);
						System.err.println("StateNameSpinner=="+i);
						}
					}
			}*/
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		vehiclemodalYearSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				if(!(value==1)){ 
//					if(position==0)
//					((TextView) arg0.getChildAt(0)).setTextColor(Color.GRAY);
				}
				SingleTon.getInstance().setVehicleYear(vehiclemodalYearSpinner.getSelectedItem().toString());
//				editor.putString("year", ""+vehiclemodalYearSpinner.getSelectedItem().toString());
//				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		
		
	}
	
	//*******************************Adapter to set vehicle state********************//
	protected void setApapterForState() {

			ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(this,
					R.layout.spinner_list, stateList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			StateNameSpinner.setAdapter(dataAdapter);	
			if(value==1){
				//gettingStatePosition=0;
				//StateNameSpinner.setSelection(gettingStatePosition);
				if(stateList.size()>gettingStatePosition)
				{
					StateNameSpinner.setSelection(gettingStatePosition);
					}
				if(flag>1)
				{
					StateNameSpinner.setSelection(gettingStatePosition);
					}
				//if((mProfileInfoModel.getLicenseplatecountryID()).equalsIgnoreCase())
		}
	}
	
	
	//*******************************Adapter to set vehicle year********************//
			protected void setApapterForVehicleYear(String year) {

				int yearIntValue;
				
				try{
					yearIntValue = Integer.parseInt(year);
				}catch(Exception e){
					yearIntValue = 0;
				}

				ArrayList<String> years = new ArrayList<String>();
				years.add("Choose Vehicle Year");
				
				if(yearIntValue>0){
					for(int i=yearIntValue;i<=Calendar.getInstance().get(Calendar.YEAR);i++){
						years.add(String.valueOf(i));
					}
				}
				
				/******************************/			
				
				if(years.size()>0){
				String gettingVehicleYear=mProfileInfoModel.getVechile_year();
//				if(gettingVehicleYear.equals(""))
//				{
//					gettingVehicleYear=prefs3.getString("year", "");
//				}
				int i=0;
				for(String s:years)
				{
					String vehicleyear=s;
					if(gettingVehicleYear.equals(vehicleyear))
					{
						gettingVehicleYearPosition=i;
					}
					i++;
				}
				}
				/******************************/
				
				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,R.layout.spinner_list, years);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				vehiclemodalYearSpinner.setAdapter(dataAdapter);
				if(value==1)
					if(vehicle_flag>1)
					{
						vehiclemodalYearSpinner.setSelection(0);
					}
					else
					{
						if(years.size()>gettingVehicleYearPosition)
						{
							vehiclemodalYearSpinner.setSelection(gettingVehicleYearPosition);
							}
						
					}
			}
	

		//*******************************Adapter to set vehicle model********************//
		protected void setApapterForVehicleModel(ArrayList<ModelArray> vehicleModals) {
			/******************************/			
						
			if(vehicleArrayList.size()>0){
				//edit
		//	String gettingVehicleModel=mProfileInfoModel.getVechile_model();
				String gettingVehicleModel=mProfileInfoModel.getVechile_modelID();
//				if(gettingVehicleModel.equals("0"))
//				{
//					gettingVehicleModel=prefs3.getString("modelid", "");
//				}
				
			int i=0;
			for(ModelArray vehicle:vehicleArrayList.get(gettingVehicleMakePosition).getListModalData())
			{
				//String vehicleMode=vehicle.getVehiclemodalName();
				String vehicleMode=vehicle.getVehiclemodelID();
				if(gettingVehicleModel.equals(vehicleMode))
				{
					gettingVehicleModelPosition=i;
					System.err.println("vehicelmodel="+i);
				}
				i++;
			}
			}
			/******************************/
			ArrayAdapter<ModelArray> dataAdapter = new ArrayAdapter<ModelArray>(this,
					R.layout.spinner_list, vehicleModals);
			
		
			
			
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			vehiclemodalNameSpinner.setAdapter(dataAdapter);
			if(value==1)
				if(vehicle_flag>1)
				{
					gettingVehicleModelPosition=0;
					vehiclemodalNameSpinner.setSelection(gettingVehicleModelPosition);
					
					String year;
					if(vehicleModals.size()>0)
						year = vehicleModals.get(gettingVehicleModelPosition).getVehiclemodalYear();
					else
						year = "0";
					
					setApapterForVehicleYear(year);
					}
				else
				{
					if(vehicleModals.size()>gettingVehicleModelPosition)
					{
						vehiclemodalNameSpinner.setSelection(gettingVehicleModelPosition);
						
						String year;
						if(vehicleModals.size()>0)
							year = vehicleModals.get(gettingVehicleModelPosition).getVehiclemodalYear();
						else
							year = "0";
						
						setApapterForVehicleYear(year);
						}
					
				}
		//	vehiclemodalNameSpinner.setSelection(Integer.parseInt(mProfileInfoModel.getVechile_modelID()));
			
			
		}
		//*******************************Adapter to set vehicle country********************//
		protected void setApapterForCountry() {
			
			ArrayAdapter<Country> dataAdapter = new ArrayAdapter<Country>(this,R.layout.spinner_list, countryList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			countryNameSpinner.setAdapter(dataAdapter);
			if(SingleTon.getInstance().isEdited()){
			countryNameSpinner.setSelection(gettingCountryPosition);
			}
		
		}

	@Override
	public void processFinish(String output, String methodName) {
		
		/*try {						
			new Timer().schedule(new TimerTask() {          
			    @Override
			    public void run() {
			        // this code will be executed after 4 seconds    
			    	Log.e("Inside","timer");
			        value=0;
			    }
			}, 10000);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	
		if(methodName.equals(fetchCountry)){
			Log.e("fetchCountry", output);
			mainModal = parser.parseCountries(output);
			countryList = mainModal.getCountryList();
			
			if(SingleTon.getInstance().isEdited()){
				value=1;
				if(countryList.size()>0){
					//edit
					//gettingCountry=mProfileInfoModel.getLicenseplatecountry();//getLicenseplatecountry();
					gettingCountry=mProfileInfoModel.getLicenseplatecountryID();
					
//					if(gettingCountry.equals("0"))
//					{
//						gettingCountry=prefs3.getString("countryid", "");
//						}
					int i=0;
					for(Country country:countryList)
					{
						//edit
						//String countryName=country.getCountryName();
						String countryName=country.getCountryID();
						//Log.e("", countryName);
						if(gettingCountry.equals(countryName))
						{
							gettingCountryPosition=i;
							System.err.println("CountryIndex="+i);
						}
						i++;
					}
				}
			}
			
			setApapterForCountry();
			
		}else if(methodName.equals(fetchVehicleModals)){
			Log.e("fetchVehicleModals", output);
			mainModal = parser.parseVehicles(output);
			vehicleArrayList = mainModal.getVehicleList();
			Log.e("fetchVehicleModals", output);
			/******************************/			
			if (vehicleArrayList.size() > 0) {
				if (SingleTon.getInstance().isEdited()) {
					String gettingVehicleMake = mProfileInfoModel.getVechile_make();//"Tata";//
//					if(gettingVehicleMake.equals(""))
//					{
//						gettingVehicleMake=prefs3.getString("makename", "");
//					}
					int i = 0;
					for (Vehicle vehicle : vehicleArrayList) {
						String vehilmake = vehicle.getVehicleMakeName();
						if (gettingVehicleMake.equals(vehilmake)) {
							gettingVehicleMakePosition = i;
						}
						i++;
					}
				}
			}
			//*******************************Adapter to set make vehicle********************//
			ArrayAdapter<Vehicle> dataAdapter = new ArrayAdapter<Vehicle>(this,R.layout.spinner_list, vehicleArrayList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			//TextView tv=(TextView)findViewById(R.id.text1);
			vehicleMakeNameSpinner.setAdapter(dataAdapter);
			vehicleMakeNameSpinner.setSelection(gettingVehicleMakePosition);
			
			setApapterForVehicleModel(vehicleArrayList.get(gettingVehicleMakePosition).getListModalData());
			
			State dummyState = new State();
			stateList.clear();
			dummyState.setStateName("Licence Plate State");
			stateList.add(dummyState);
			setApapterForState();
			
			
		}else if(methodName.equals(fetchStates)){
			Log.e("fetchStates", output);
			stateList.clear();
			mainModal = parser.parseStates(output);
			stateList = mainModal.getStateList();
			/*********************************************************/
			if (stateList.size() > 0) {
				if (value==1) {
					//edit
					//String gettingLicenceState = mProfileInfoModel.getLicenseplatestate();
					String gettingLicenceState = mProfileInfoModel.getLicenseplatestateID();
//					if(gettingLicenceState.equals("0"))
//					{
//						gettingLicenceState=prefs3.getString("stateid","");
//						}
//					
					int i = 0;
					for (State state : stateList) {
						//edit
						//String licenseState = state.getStateName();
						String licenseState = state.getStateId();
						
						if (gettingLicenceState.equals(licenseState)) {
							gettingStatePosition = i;
							System.err.println("Stateindex="+i);
						}
						i++;
					}
				}
			}
			
			/***********************************************************/
			SingleTon.getInstance().setSelectedStates(stateList);
			setApapterForState(); 
		}
		
	}

	private void forwardToNextScreen() {
	
		SingleTon.getInstance().setVehicleLicencePlateNumber(licensePlateNumberEditText.getText().toString());
//		editor.putString("licplateno", licensePlateNumberEditText.getText().toString());
//		editor.commit();
		//finish();
		
		Intent intent = new Intent(VehicleInformationActivity.this,BackgroundCheckActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		
	}

	private void emptyFieldCheck() {
		
		if (vehicleMakeNameSpinner.getSelectedItem().toString().trim().equals("Choose Vehicle Make")) {
		    Toast.makeText(VehicleInformationActivity.this, "Select Vehicle Make", Toast.LENGTH_SHORT).show();
		    return;
		}
		if (vehiclemodalNameSpinner.getSelectedItem().toString().trim().equals("Choose Vehicle Model")) {
		    Toast.makeText(VehicleInformationActivity.this, "Select Vehicle Model", Toast.LENGTH_SHORT).show();
		    return;
		}		
		if (vehiclemodalYearSpinner.getSelectedItem().toString().trim().equals("Choose Vehicle Year")) {
		    Toast.makeText(VehicleInformationActivity.this, "Select Vehicle Year", Toast.LENGTH_SHORT).show();
		    return;
		}
		if(licensePlateNumberEditText.getText().toString().equals("")){
			
			Toast.makeText(VehicleInformationActivity.this, "Enter Licence Plate Number",Toast.LENGTH_SHORT).show();
			return;
		}
		if (countryNameSpinner.getSelectedItem().toString().trim().equals("Licence Plate Country")) {
		    Toast.makeText(VehicleInformationActivity.this, "Select Country", Toast.LENGTH_SHORT).show();
		    return;
		}		
		
		if (StateNameSpinner.getSelectedItem().toString().trim().equals("Licence Plate State")) {
		    Toast.makeText(VehicleInformationActivity.this, "Select State", Toast.LENGTH_SHORT).show();
		    return;
		}	
		
		forwardToNextScreen();	
		
	}
	public void onBackPressed() {
		
		AlertDialog.Builder alert = new AlertDialog.Builder(VehicleInformationActivity.this);
		alert.setTitle("Please confirm");
		alert.setMessage("Are you sure you don't want to save any changes?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				for(int i = 0;i<regActivities.size(); i++){
					regActivities.get(i).finish();
				}
			}
		});
		alert.setNegativeButton("Dismiss", null);
		alert.show();
		/*Intent intent = new Intent(VehicleInformationActivity.this,GetProfile.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);*/
		
	}

}
