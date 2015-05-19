package com.zira.registration;


import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.twentyfourseven.zira.DriverModeActivity;
import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.City;
import com.zira.modal.Country;
import com.zira.modal.MainModal;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.State;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;


@SuppressLint("SimpleDateFormat")
public class BackgroundCheckActivity extends Activity implements OnClickListener, AsyncResponseForZira{

	
	
	//Editor editor;
	//SharedPreferences prefs3;
	private ProfileInfoModel mProfileInfoModel;
	private Spinner stateSpinner,licenceStateSpinner;
	private Spinner citySpinner;
	private Button saveAndCuntinueButton;
	private String DOB="",licenceExpirationDate="";
	private int hours,minute,second;
	long selectdateintoInt,selectExpirydate;
	private ArrayList<City> arraylist_city=new ArrayList<City>();
	private ArrayList<State> statesArrayList=new ArrayList<State>();	
	private ArrayList<State> licenceStateArrayList=new ArrayList<State>();	
	private ArrayList<String> licenceStateArrayList1=new ArrayList<String>();	
	String HH="";
	private SimpleDateFormat mDateFormat,dateFormatToSend,getDateFormat;	
	private EditText licenceExpirationTextView,dobTextView;
	private EditText  addressOneEditText,addressTwoEditText,zipCodeEditText,
						cityEditText,licenceNumberEditText,securityNumberEditText;
	private String gettingLicenceState;
	private int gettingLicenceStatePosition, gettingStatePosition,gettingCityPosition;
	private String gettingState; 
	private String getcity="GetCityList";
	private ZiraParser parser;
	MainModal mainmodel;
	int value=-1;
	Boolean bol_value,bol_expirydate;
	 long	 MILLISECONDS_IN_YEAR_18;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_background_check);		
		
		VehicleInformationActivity.regActivities.add(BackgroundCheckActivity.this);
		getCurrentTime();
		
		//try {			
			initialiseVariable();
		//} catch (Exception e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}
		initialiseListener();
		getCity();
	}
	
	@SuppressWarnings("deprecation")
	private void getCurrentTime() {
		mDateFormat=new SimpleDateFormat("dd-MMM-yyyy");
		dateFormatToSend=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		getDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		hours = new Time(System.currentTimeMillis()).getHours();
		minute= new Time(System.currentTimeMillis()).getMinutes();
		second= new Time(System.currentTimeMillis()).getSeconds();		
	
	}

	
	public void getCity(){
		if(Util.isNetworkAvailable(BackgroundCheckActivity.this))
		{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		    nameValuePairs.add(new BasicNameValuePair("Statename",SingleTon.getInstance().getVehicleStateName()));
			AsyncTaskForZira mFetchStates = new AsyncTaskForZira(BackgroundCheckActivity.this, getcity,nameValuePairs, true, "Please wait...");
			mFetchStates.delegate = (AsyncResponseForZira) BackgroundCheckActivity.this;
			mFetchStates.execute();
		}
		else
		{
			Util.alertMessage(BackgroundCheckActivity.this, "Please check your internet connection");
			}
		}
	private void setApapterForCity() {
		
		ArrayAdapter<City> dataAdapter = new ArrayAdapter<City>(this,R.layout.spinner_list, arraylist_city);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		citySpinner.setAdapter(dataAdapter);	
		
		citySpinner.setSelection(gettingCityPosition);
		
}
	private void setAdapterForLicenceState() {
		statesArrayList=SingleTon.getInstance().getSelectedStates();
		
		if(statesArrayList.size()>0){
			
			//gettingLicenceState=mProfileInfoModel.getDrivinglicensestate();
			gettingLicenceState=mProfileInfoModel.getDrivinglicensestateID();
			/*if(gettingLicenceState.equals("0"))
			{
				//gettingLicenceState=prefs3.getString("DrivinglicensestateID", "");
				//System.err.println("DrivinglicensestateID="+prefs3.getString("DrivinglicensestateID", ""));
				}*/
			}
			int i=0;
			for(State state:statesArrayList)
			{
				//String stateName=state.getStateName();
				String stateName=state.getStateId();
				//Log.e("stateID=", stateName);
				if(gettingLicenceState.equals(stateName))
				{
					gettingLicenceStatePosition=i;
				}
				i++;
			}
		
		ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(this,
		R.layout.spinner_list, statesArrayList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		licenceStateSpinner.setAdapter(dataAdapter);
		licenceStateSpinner.setSelection(gettingLicenceStatePosition);
	}
	
	private void setAdapterForState() {
		statesArrayList=SingleTon.getInstance().getSelectedStates();
		
		if(statesArrayList.size()>0){
			//gettingLicenceState=mProfileInfoModel.getDrivinglicensestate();
			gettingState=mProfileInfoModel.getStateID();
			int i=0;
			for(State state:statesArrayList)
			{
				//String stateName=state.getStateName();
				String stateName=state.getStateId();
				
				//Log.e("stateID=", stateName);
				if(gettingState.equals(stateName))
				{
					gettingStatePosition=i;
				}
				i++;
			}
		}
		ArrayAdapter<State> dataAdapter = new ArrayAdapter<State>(this,
		R.layout.spinner_list, statesArrayList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		stateSpinner.setAdapter(dataAdapter);
		stateSpinner.setSelection(gettingStatePosition);
	
	}

	
	private void initialiseListener() {
		
		SingleTon.getInstance().setBg_state(""+mProfileInfoModel.getStateID());
		 stateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				
				if(statesArrayList.size()>0)
				{
				String statename=stateSpinner.getSelectedItem().toString();
				for(int i=0;i<statesArrayList.size();i++)
				{
					if(statename.equalsIgnoreCase(statesArrayList.get(i).getStateName()))
					{
						String stateid=""+statesArrayList.get(i).getStateId();
						System.err.println("index="+stateid);
						SingleTon.getInstance().setBg_state(""+stateid);
						/*editor.putString("stateid", stateid);
						editor.commit();*/
						//SingleTon.getInstance().setVehicleStateName(""+position);
						}
				}
				}
				
				
//				int position1=1+position;
//				SingleTon.getInstance().setBg_state(""+position1);//stateSpinner.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		 
		 SingleTon.getInstance().setBg_drivingLicenseState(""+mProfileInfoModel.getDrivinglicensestateID());
		 licenceStateSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
					
				if(statesArrayList.size()>0)
				{
					String statename=licenceStateSpinner.getSelectedItem().toString();
					for(int i=0;i<statesArrayList.size();i++)
					{
						if(statename.equalsIgnoreCase(statesArrayList.get(i).getStateName()))
						{
							String stateid=""+statesArrayList.get(i).getStateId();
							System.err.println("index="+stateid);
							SingleTon.getInstance().setBg_drivingLicenseState(""+stateid);
							//SingleTon.getInstance().setVehicleStateName(""+position);
							/*editor.putString("DrivinglicensestateID", stateid);
							editor.commit();*/
							}
					}
					}
					
//					int position1=1+position;
//					SingleTon.getInstance().setBg_drivingLicenseState(""+position1);//licenceStateSpinner.getSelectedItem().toString());
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		
		 SingleTon.getInstance().setBg_city(""+mProfileInfoModel.getCityID());
		 citySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
				
				if(arraylist_city.size()>0)
				{
				String statename=citySpinner.getSelectedItem().toString();
				for(int i=0;i<arraylist_city.size();i++)
				{
					if(statename.equalsIgnoreCase(arraylist_city.get(i).getCityname()))
					{
						String cityid=""+arraylist_city.get(i).getCityId();
						System.err.println("city index="+cityid);
						SingleTon.getInstance().setBg_city(""+cityid);
					/*	editor.putString("cityid", cityid);
						editor.commit();*/
						//SingleTon.getInstance().setVehicleStateName(""+position);
						}
				}
				}
				
				
//				int position1=1+position;
//				SingleTon.getInstance().setBg_state(""+position1);//stateSpinner.getSelectedItem().toString());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		dobTextView.setOnClickListener(this);
		saveAndCuntinueButton.setOnClickListener(this);
		licenceExpirationTextView.setOnClickListener(this);
		setAdapterForLicenceState();
		setAdapterForState();
		
	}

	private void initialiseVariable() {
		parser = new ZiraParser();
		
		//prefs3 = getSharedPreferences("Ziradata", MODE_PRIVATE);
		//editor=prefs3.edit();
		statesArrayList = SingleTon.getInstance().getSelectedStates();
		mProfileInfoModel=SingleTon.getInstance().getProfileInfoModel();
		saveAndCuntinueButton=(Button)findViewById(R.id.SaveAndCuntinue);
		stateSpinner=(Spinner)findViewById(R.id.state);
		licenceStateSpinner	=(Spinner)findViewById(R.id.licenceState);	
		citySpinner	=(Spinner)findViewById(R.id.city);	
		addressOneEditText=(EditText)findViewById(R.id.addressOne);
		addressTwoEditText=(EditText)findViewById(R.id.addressTwo);
		zipCodeEditText=(EditText)findViewById(R.id.zipCode);
		//cityEditText=(EditText)findViewById(R.id.city);
		licenceNumberEditText=(EditText)findViewById(R.id.licenceNumber);
		licenceExpirationTextView=(EditText)findViewById(R.id.licenceExpiration);		
		dobTextView=(EditText)findViewById(R.id.dob);
		securityNumberEditText =(EditText)findViewById(R.id.securityNumber);

		if(SingleTon.getInstance().isEdited()){

			/*if(mProfileInfoModel.getAddress1().equals(""))
			{
				addressOneEditText.setText(prefs3.getString("addressOne", ""));
				System.err.println("address="+prefs3.getString("addressOne", ""));
				}*/
			/*else
			{*/
			addressOneEditText.setText(mProfileInfoModel.getAddress1());
				//}
			//addressTwoEditText.setText(mProfileInfoModel.getAddress2());
			addressTwoEditText.setVisibility(View.GONE);
		/*	if(mProfileInfoModel.getZipcode().equals(""))
			{
				zipCodeEditText.setText(prefs3.getString("zipCode", ""));
				System.err.println("zipCode="+prefs3.getString("zipCode", ""));
				}
			else
			{*/
				zipCodeEditText.setText(mProfileInfoModel.getZipcode());
				//}
			
			/*if(mProfileInfoModel.getDrivinglicensenumber().equals(""))
			{
				licenceNumberEditText.setText(prefs3.getString("licenceNumber", ""));
				System.err.println("licenceNumber="+prefs3.getString("licenceNumber", ""));
				}
			else
			{*/
				licenceNumberEditText.setText(mProfileInfoModel.getDrivinglicensenumber());
			//	}
			/*if(mProfileInfoModel.getSocialsecuritynumber().equals(""))
			{
				securityNumberEditText.setText(prefs3.getString("securityNumber", ""));
				System.err.println("securityNumber="+prefs3.getString("securityNumber", ""));
				}
			else
			{*/
				securityNumberEditText.setText(mProfileInfoModel.getSocialsecuritynumber());
				//}
			
			String date=mProfileInfoModel.getDrivinglicenseexpirationdate();
		/*	if(date.equals(""))
			{
				licenceExpirationTextView.setText(prefs3.getString("licenceExpirationDate", ""));
				System.err.println("licenceExpirationDate="+prefs3.getString("licenceExpirationDate", ""));
				}
			else*/
			//{
				Calendar mCalendar=Calendar.getInstance();
				mCalendar.set(Integer.valueOf(date.substring(0,4)),Integer.valueOf(date.substring(4,6))-1,Integer.valueOf(date.substring(6,8)));
			
				String selectedDate=mDateFormat.format(mCalendar.getTime());
				licenceExpirationTextView.setText(selectedDate);
			//}
			String dateOfBirth=mProfileInfoModel.getDateofbirth();	
			/*if(dateOfBirth.equals(""))
			{
				dobTextView.setText(prefs3.getString("dob", ""));
				System.err.println("dob="+prefs3.getString("dob", ""));
				}
			else
			{*/
				
				mCalendar.set(Integer.valueOf(dateOfBirth.substring(0,4)),Integer.valueOf(dateOfBirth.substring(4,6))-1,Integer.valueOf(dateOfBirth.substring(6,8)));
				String selectedDobDate=mDateFormat.format(mCalendar.getTime());
				dobTextView.setText(selectedDobDate);	
		//	}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {		

		case R.id.dob:
			int mYear, mMonth, mDay;
			// Process to get Current Date
			final Calendar c = Calendar.getInstance();
			mYear = c.get(Calendar.YEAR);
			mMonth = c.get(Calendar.MONTH);
			mDay = c.get(Calendar.DAY_OF_MONTH);

			// Launch Date Picker Dialog
			DatePickerDialog dpd = new DatePickerDialog(BackgroundCheckActivity.this,
					new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {

					c.set(year,monthOfYear,dayOfMonth);					
					String selectedDate=mDateFormat.format(c.getTime());
					String dateTosend=dateFormatToSend.format(c.getTime());
					System.err.println("selectdate"+dateTosend);
					DOB=dateTosend;//+String.valueOf(hours)+String.valueOf(minute)+String.valueOf(00);
					
					dobTextView.setText(selectedDate);
				/*	editor.putString("dob", selectedDate);
					editor.commit();*/
				}
			}, mYear, mMonth, mDay);
			dpd.show();

			break;
		case R.id.licenceExpiration:
			int Year, Month, Day;
			// Process to get Current Date
			final Calendar c1 = Calendar.getInstance();
			Year = c1.get(Calendar.YEAR);
			Month = c1.get(Calendar.MONTH);
			Day = c1.get(Calendar.DAY_OF_MONTH);

			// Launch Date Picker Dialog
			DatePickerDialog dpd1 = new DatePickerDialog(BackgroundCheckActivity.this,
					new DatePickerDialog.OnDateSetListener() {

				@Override
				public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
					
					c1.set(year,monthOfYear,dayOfMonth);					
					String selectedDate=mDateFormat.format(c1.getTime());
					String dateTosend=dateFormatToSend.format(c1.getTime());
					System.err.println("selectdate"+dateTosend);
					licenceExpirationDate=dateTosend;//+String.valueOf(hours)+String.valueOf(minute)+String.valueOf(00);
					
					licenceExpirationTextView.setText(selectedDate);
					/*editor.putString("licenceExpirationDate", selectedDate);
					editor.commit();*/

				}
			}, Year, Month, Day);
			dpd1.show();
			
			break;
			
		case R.id.SaveAndCuntinue:
			
			emptyFieldCheck();			
			//forwardToNextScreen();		
			break;
			
		default:

			break;
		}
	}

	private void forwardToNextScreen() {
		
		SingleTon.getInstance().setBg_address1(addressOneEditText.getText().toString());
	//	SingleTon.getInstance().setBg_address2(addressTwoEditText.getText().toString());
		SingleTon.getInstance().setBg_zipcode(zipCodeEditText.getText().toString());
		SingleTon.getInstance().setBg_drivingLicenseNumber(licenceNumberEditText.getText().toString());
	//	editor.putString("zipCode", zipCodeEditText.getText().toString());
	//	editor.putString("addressOne", addressOneEditText.getText().toString());
	//	editor.putString("licenceNumber", licenceNumberEditText.getText().toString());
	//	editor.commit();
	
		//SingleTon.getInstance().setBg_city(""+gettingCityPosition);
	
		if(licenceExpirationDate.equals("")){
			String getdate=mProfileInfoModel.getDrivinglicenseexpirationdate();
			 Date date = null;
			try {
				date = getDateFormat.parse(getdate);
				 String formattedDate = dateFormatToSend.format(date); 
				SingleTon.getInstance().setBg_LicExoDate(formattedDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		/* String formattedDate = dateFormatToSend.format(date); 
		SingleTon.getInstance().setBg_LicExoDate(formattedDate);*/
		}else{
			SingleTon.getInstance().setBg_LicExoDate(licenceExpirationDate);
			
		}
		if(DOB.equals("")){
			
			String getdatedob=mProfileInfoModel.getDateofbirth();
			 Date date = null;
				try {
					date = getDateFormat.parse(getdatedob);
					String formattedDate = dateFormatToSend.format(date); 
					SingleTon.getInstance().setBgDOB(formattedDate);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}else{
		SingleTon.getInstance().setBgDOB(DOB);
	
		
		}		
		SingleTon.getInstance().setBg_socialSecNumber(securityNumberEditText.getText().toString());
	//	editor.putString("securityNumber", securityNumberEditText.getText().toString());
	//	editor.commit();
		//finish();
		
		Intent intent = new Intent(BackgroundCheckActivity.this,DocumentUploadActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		
//		startActivity(new Intent(BackgroundCheckActivity.this,DocumentUploadActivity.class));
	}

	private void emptyFieldCheck() {
	//String input = "Sat Feb 17 2012";
		//String givenDateString = "Tue Apr 23 16:08:28 GMT+05:30 2013"; 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
		    Date mDate = sdf.parse(DOB);
		    selectdateintoInt = mDate.getTime();
		    System.out.println("Date in milli :: " + selectdateintoInt);
		} catch (ParseException e) {
		            e.printStackTrace();
		}
		  
		ageChecker();
	
		if(bol_value==false)
		{
			Toast.makeText(BackgroundCheckActivity.this, "Your Age Less than 18",Toast.LENGTH_SHORT).show();
			return;
		}
		//expiryDate();
		/*if(bol_expirydate==false)
		{
			Toast.makeText(BackgroundCheckActivity.this, "Expiry date less then current date",Toast.LENGTH_SHORT).show();
			return;
		}*/
		if(addressOneEditText.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Adress ",Toast.LENGTH_SHORT).show();
			return;
		}
		
		/*if(addressTwoEditText.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter AdressLine Two",Toast.LENGTH_SHORT).show();
			return;
		}*/
		
		if(zipCodeEditText.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Zip Code",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(citySpinner.getSelectedItem().toString().trim().equals("City")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Select City",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (stateSpinner.getSelectedItem().toString().trim().equals("Licence Plate State")) {
		    Toast.makeText(BackgroundCheckActivity.this, "Select State", Toast.LENGTH_SHORT).show();
		    return;
		}
		
		if(licenceNumberEditText.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Driving Licence Number",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if (licenceStateSpinner.getSelectedItem().toString().trim().equals("Licence Plate State")) {
		    Toast.makeText(BackgroundCheckActivity.this, "Select Driving Licence State", Toast.LENGTH_SHORT).show();
		    return;
		}
		
		if(licenceExpirationTextView.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Licence Expiration Date",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(dobTextView.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Date of Birth",Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(securityNumberEditText.getText().toString().equals("")){
			
			Toast.makeText(BackgroundCheckActivity.this, "Enter Social Security Number",Toast.LENGTH_SHORT).show();
			return;
		}
		
		forwardToNextScreen();
	}

	@Override
	public void processFinish(String output, String methodName) {
		if(methodName.equals(getcity))
		{
			Log.e("fetchCity", output);
			arraylist_city.clear();
			mainmodel = parser.parseCity(output);
			arraylist_city = mainmodel.getCityList();
			/*********************************************************/
			if (arraylist_city.size() > 0) {
				value=1;
			/*	if (value==1) {*/
					//edit
					//String gettingLicenceState = mProfileInfoModel.getLicenseplatestate();
					String gettingCityID = mProfileInfoModel.getCityID();
					/*if(gettingCityID.equals("0"))
					{
						gettingCityID=prefs3.getString("cityid", "");
						}*/
					
					int i = 0;
					for (City city : arraylist_city) {
						//edit
						//String licenseState = state.getStateName();
						String cityid = city.getCityId();
						
						if (gettingCityID.equals(cityid)) {
							gettingCityPosition = i;
							System.err.println("Cityindex="+i);
						}
						i++;
					}
				}
			//}
		}
		setApapterForCity();
			}
		
	
	public void ageChecker()
	{
		long currentdateintoInt= System.currentTimeMillis();
			
		System.err.println("selectdate="+selectdateintoInt);
		System.err.println("currentdateintoInt="+currentdateintoInt);
		getAge();
		if(currentdateintoInt-selectdateintoInt>=MILLISECONDS_IN_YEAR_18)
			 {
				Log.d("yessssssss", "Please select greater date from current date");
			//	Util.alertMessage(BackgroundCheckActivity.this, "Please select current or greater date from current date");
				bol_value=true;
			 }
		else
		{
			bol_value=false;
			}
		
	}
	
	public void expiryDate()
	{
		long currentdateintoInt= System.currentTimeMillis();
			
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date mDate=null;
			String getdate=mProfileInfoModel.getDrivinglicenseexpirationdate();
			if(getdate.equals(""))
			{
			 mDate = sdf.parse(licenceExpirationDate);
			}
			else
			{
				mDate = sdf.parse(getdate);
			}
		    selectExpirydate = mDate.getTime();
		    System.out.println("ex Date in milli :: " + selectExpirydate);
		} catch (ParseException e) {
		            e.printStackTrace();
		}
		  
		System.err.println("currentdateintoInt="+currentdateintoInt);
		getAge();
		if(selectExpirydate>=currentdateintoInt)
			 {
				Log.d("expiry dateyessssssss", "Please select date less from current date");
			//	Util.alertMessage(BackgroundCheckActivity.this, "Please select current or greater date from current date");
				bol_expirydate=true;
			 }
		else
		{
			bol_expirydate=false;
			}
		
	}
	
	private void getAge() {
		
		
		 int MILLIS_IN_SECOND = 1000;
		 int SECONDS_IN_MINUTE = 60;
		 int MINUTES_IN_HOUR = 60;
		 int HOURS_IN_DAY = 24;
		 int DAYS_IN_YEAR = 365; //I know this value is more like 365.24...
		 long MILLISECONDS_IN_YEAR = (long) MILLIS_IN_SECOND * SECONDS_IN_MINUTE * MINUTES_IN_HOUR * HOURS_IN_DAY * DAYS_IN_YEAR;
		 MILLISECONDS_IN_YEAR_18=MILLISECONDS_IN_YEAR*18;
		 System.err.println("MILLISECONDS_IN_YEAR_18="+MILLISECONDS_IN_YEAR_18);
		/*
	    Date dateOfBirth = new Date(selectedMilli);
	    Calendar dob = Calendar.getInstance();
	    dob.setTime(dateOfBirth);
	    Calendar today = Calendar.getInstance();
	    int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
	    if (today.get(Calendar.MONTH) < dob.get(Calendar.MONTH)) {
	        age--;
	    } else if (today.get(Calendar.MONTH) == dob.get(Calendar.MONTH)
	            && today.get(Calendar.DAY_OF_MONTH) < dob
	                    .get(Calendar.DAY_OF_MONTH)) {
	        age--;
	    }

	    if (age < 18) {
	    	Log.e("nooooooooo", ""+ age);
	    	
	    } else {

	    	Log.e("yessssssssssss", ""+ age);
	    }

	 String   str_age = age + "";
	    Log.d("", getClass().getSimpleName() + ": Age in year= " + age);
	*/}
	
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(BackgroundCheckActivity.this,VehicleInformationActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
	
	}






