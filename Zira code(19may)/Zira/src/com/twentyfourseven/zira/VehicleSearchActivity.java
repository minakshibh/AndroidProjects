package com.twentyfourseven.zira;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.creditcards.ListOfCreditCardsActivity;
import com.zira.login.ForgotPassword;
import com.zira.login.LegalPolicyDailog;
import com.zira.modal.AdresssOfLocation;
import com.zira.modal.Coordinates;
import com.zira.modal.GetTripDetails;
import com.zira.modal.MainVehicleModel;
import com.zira.modal.PendingSplitFare;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.Vehicle;
import com.zira.notification.DriverNotifications;
import com.zira.notification.RiderNotification;
import com.zira.profile.GetProfile;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class VehicleSearchActivity extends Activity implements LocationListener,AsyncResponseForZira{
	public ActionBarDrawerToggle mDrawerToggle;
	public DrawerLayout mDrawerLayout;
	public RelativeLayout flyoutDrawerRl;
	private LinearLayout sourceRelativeLayout,destinationRelativeLayout;
	public AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
	public ImageView slider;
	private ImageView userImage;
	private TextView username;
	private GoogleMap googleMap;
	private  ProgressDialog pDialogn;
	private LocationManager locationManager;
	private int flag = 0;
	private  String provider;
	public static double myLat=0, myLon=0;
	private Criteria criteria;
	private TextView txtDestination, txtPickupLocation, setPickUp;
	private Coordinates pickUpCoordinates, destinationCoordinates;
	private LocationFinder locFinder;
	private ImageView imgAdd, imgDelete,imgPromCode,imgFare;
	private TextView cancelImageView;
	private RelativeLayout destinationLayout;
	private RelativeLayout optionLayout;
	private ImageView btnCreditCard, btnFare, btnPromoCode;
	private RelativeLayout profileLayout;
	private ProfileInfoModel mProfileInfoModel;
	private ImageLoader imageLoader;
//	private LatLng pickuplatLng;
	private ArrayList<Coordinates> Addresss = new ArrayList<Coordinates>();
	private AdresssOfLocation mLocation;
	private boolean isProgressBeingShown=false;
	
	public static String destinationAddres,sourceAddress;
	public static double gettingSourceLat,gettingSourceLong;
	public static double gettingdestinationLat,gettingDestinationLong;
	private ZiraParser parser;
	//private RelativeLayout ;
	private LinearLayout driverMode,payment,promotions,support,getZira,share;
	private ImageView btnRequestPicup;
	private MySwitch publishToggle, swtch_alertshow;
	private RelativeLayout carsRelativeLayout;
	private ImageView regularCarImageView,xuvCarImageView,taxiCarImageView,fourcar_ImageView,imgCurrentButton;
//	private TimerTask timerTask;
//	private Handler timerHandler = new Handler();      
//	private Timer timer;
	private int value=-1;
	private TextView txtEstimatedTime;
	private TimerTask backgroundTimerTask;    
	private Timer Backgroundtimer;
	private  AsyncTaskForGettingVehicle mAsyncTask;
	private String datetimepick=null,myAddress,str_currentdate=null,time,requestType,distance,numberpicker_no,tripid;
	private int progressFlag = 0;
	
	//Method name of web service
	private String requestRide="RequestRide";
	private String switchBetweenMode="SwitchBetweenMode";
	private String registerDevice = "RegisterDevice";
	private String GetPendingSplitFare="GetPendingSplitFare";
	private String AcceptRejectPendingFareRequest="AcceptRejectPendingFareRequest";
	private String GetProfile="GetProfiles";
	
	
	String driverids="";
	private String selectedDate;
	private MainVehicleModel vehicleModel;
	private int distanceValue,timeValue,approxMinutes;
	float minvalue=0,suggestion=100,per_minvalue=0,per_maxvalue=0,maxvalue=0,basic_fare;
	float totalKms =00,totalKmInMiles=00;
	public static float actualfare;
	private Marker customMarker;
	//private String[] vehicleArray = new String[] {"Zirax","ziraplus","zirasuv"};
	private int from=0;
	public static int vehicleType;
	public static String  str_vehicleType;
	private SharedPreferences prefs,prefs2;
	private Editor editor;
	public static int driverModeActivated =0;
	String estimstetime;
	
	String jsonMessage = null,jsonResult=null;
	private int flag_slpitcheck=0;
	private GetTripDetails mTripDetailsModel;
	PendingSplitFare psplitfare;
	int countdownNumber = 60;
	static int ncheck=0;
	@SuppressLint("NewApi") 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_vehicle_search);
		

		initialiseVariable();
		mapInitialise();
		default_VehicleSelect();
		
	//	GetLastActiveTrip();
	
		setListeners();
	
	}
	

	private void mapInitialise() {
		try{
		if(googleMap!=null){
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		//criteria = new Criteria();

		// Getting the name of the best provider
		//provider = locationManager.getBestProvider(criteria, true);
	    Location location = getLastKnownLocation();//locationManager.getLastKnownLocation(provider);	
	      
	      if(location!=null){
	   
	    	  onLocationChanged(location);
          }
			}
		try{
			boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// Check if enabled and if not send user to the GSP settings
			if (!enabledGPS && !enabledWiFi) {
				Toast.makeText(VehicleSearchActivity.this, "GPS signal not found", Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		locationManager.requestLocationUpdates(provider, 10000, 0, this);
		
		}catch(Exception e){
			e.printStackTrace();
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// MAP INITIALIZE ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}
	private Location getLastKnownLocation() {
	    List<String> providers = locationManager.getProviders(true);
	    Location bestLocation = null;
	    for (String provider : providers) {
	        Location l = locationManager.getLastKnownLocation(provider);
	      //  Log.d("last known location, provider: %s, location: %s", provider, l);

	        if (l == null) {
	            continue;
	        }
	        if (bestLocation == null
	                || l.getAccuracy() < bestLocation.getAccuracy()) {
	           // ALog.d("found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    if (bestLocation == null) {
	        return null;
	    }
	    return bestLocation;
	} 

	private void default_VehicleSelect() {
		
		try{
		str_vehicleType=prefs.getString("preferedvehicletype", "");
		System.err.println("str_vehicleType="+str_vehicleType);
		
		if(str_vehicleType.equalsIgnoreCase("ZiraE"))
		{
			switchBetweenImages(true,false,false,false);
			vehicleType=Integer.parseInt(prefs.getString("zirae", "1"));
			}
		else if(str_vehicleType.equalsIgnoreCase("ZiraPlus"))
		{
			switchBetweenImages(false,true,false,false);
			vehicleType=Integer.parseInt(prefs.getString("ziraplus", "2"));
			}
		else if(str_vehicleType.equalsIgnoreCase("ZiraTaxi"))
		{
			switchBetweenImages(false,false,true,false);
			vehicleType=Integer.parseInt(prefs.getString("zirataxi", "3"));
			}
		else if(str_vehicleType.equalsIgnoreCase("ZiraLux"))
		{
			switchBetweenImages(false,false,false,true);
			vehicleType=Integer.parseInt(prefs.getString("ziralux", "7"));
			}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// DEFAULT VEHICLE SELECT ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
		
	}


	public void setListeners(){
		
		try{
		
	    imgCurrentButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        String provider = locationManager.getBestProvider(criteria, true);
		        // Getting Current Location
		        Location location = locationManager.getLastKnownLocation(provider);	
		        try{
		        googleMap.setMyLocationEnabled(true);
		        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
		        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
		        }
		        catch(Exception e)
		        {
		        	e.printStackTrace();
		        }
		      
 			
			}
		});
		
		
		
		profileLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				getProfileInfo();
				/*Intent intent = new Intent(VehicleSearchActivity.this, GetProfile.class);
				startActivity(intent);*/
				mDrawerLayout.closeDrawers();
			}
		});

		regularCarImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {	
				googleMap.clear();
				stopBackgroundTimer();
				startBackgroundTimer(1);
				switchBetweenImages(true,false,false,false);				
				str_vehicleType="ZiraE";
				vehicleType=Integer.parseInt(prefs.getString("zirae", "1"));
				
			
				startActivity(new Intent(VehicleSearchActivity.this,VehicleFareInfoDialog.class));
				
			}
		});

		xuvCarImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {		
				googleMap.clear();
				stopBackgroundTimer();
				startBackgroundTimer(1);
			
				
				switchBetweenImages(false,true,false,false);
				str_vehicleType="ZiraPlus";
				vehicleType=Integer.parseInt(prefs.getString("ziraplus", "2"));
			
				startActivity(new Intent(VehicleSearchActivity.this,VehicleFareInfoDialog.class));
			
			}
		});

		taxiCarImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				googleMap.clear();
				stopBackgroundTimer();
				startBackgroundTimer(1);
			
				
				switchBetweenImages(false,false,true,false);
				str_vehicleType="ZiraTaxi";
				vehicleType=Integer.parseInt(prefs.getString("zirataxi", "3"));
				
			
				startActivity(new Intent(VehicleSearchActivity.this,VehicleFareInfoDialog.class));
				
			}
		});
		
		fourcar_ImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {				
				
				googleMap.clear();
				stopBackgroundTimer();
				startBackgroundTimer(1);
				
				switchBetweenImages(false,false,false,true);
				str_vehicleType="ZiraLux";
				vehicleType=Integer.parseInt(prefs.getString("ziralux", "7"));
			
				startActivity(new Intent(VehicleSearchActivity.this,VehicleFareInfoDialog.class));
				
			}
		});
	
		support.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VehicleSearchActivity.this,Support.class));
				mDrawerLayout.closeDrawers();
			}
		});
		payment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VehicleSearchActivity.this,ListOfCreditCardsActivity.class));
				mDrawerLayout.closeDrawers();
			}
		});
		promotions.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VehicleSearchActivity.this,Promotions.class));
				mDrawerLayout.closeDrawers();
			}
		});
		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VehicleSearchActivity.this,ShareScreen.class));
				mDrawerLayout.closeDrawers();	
			}
		});
		getZira.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mDrawerLayout.closeDrawers();	
			}
		});
		btnFare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if(txtDestination.getText().toString().equals("")){

				//	Toast.makeText(VehicleSearchActivity.this, "Please Choose Destination", 0).show();
					Util.alertMessage(VehicleSearchActivity.this, "Please Choose Destination");
					return;
				}			
				if(Util.isNetworkAvailable(VehicleSearchActivity.this)){
					new distanceCalculatorAsyncTask().execute();
				}
				else
				{
					Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
				}
			}
		});
		btnPromoCode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(VehicleSearchActivity.this,AddPromoCode.class));	
			}
		});
		btnRequestPicup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				if(txtDestination.getText().toString().equals("")){

					//Toast.makeText(VehicleSearchActivity.this, "Please Choose Destination", 0).show();
					Util.alertMessage(VehicleSearchActivity.this, "Please Choose Destination");
					return;
				}
				try{
					if(vehicleModel.getVehicleInfoList().size()==0){
	
						//Toast.makeText(VehicleSearchActivity.this, "Vehicle not available", 0).show();
						Util.alertMessage(VehicleSearchActivity.this, "Vehicle not available");
						return;
					}
				}
				catch(Exception e)
				{}
					
			
				from=1;
				if(Util.isNetworkAvailable(VehicleSearchActivity.this)){
					new distanceCalculatorAsyncTask().execute();
				}
				else
				{
					Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
				}

			}
		});
		driverMode.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(prefs.getString("isdrivermodeactivated", "").equalsIgnoreCase("true"))
				{
				
					if(prefs.getString("vehiclestatus", "").equalsIgnoreCase("1"))
					{
						//Toast.makeText(VehicleSearchActivity.class, "Your Vehicle is not confirmed", Toast.LENGTH_SHORT).show();
						Util.alertMessage(VehicleSearchActivity.this, "your Vehicle is not confirmed")	;
						}
					else
					{
	
						stopBackgroundTimer();
						if(Util.isNetworkAvailable(VehicleSearchActivity.this))
						{
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
						nameValuePairs.add(new BasicNameValuePair("Riderid",prefs.getString("riderid", "")));
						nameValuePairs.add(new BasicNameValuePair("Longitude",String.valueOf(myLat)));
						nameValuePairs.add(new BasicNameValuePair("Latitude", String.valueOf(myLon)));
						nameValuePairs.add(new BasicNameValuePair("Trigger","activate"));	
						AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(VehicleSearchActivity.this, switchBetweenMode,nameValuePairs, true,"Please wait...");
						mWebPageTask.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
						mWebPageTask.execute();	
						
						mDrawerLayout.closeDrawers();
						}
						else
						{
							Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
						}
						}
					}
				else
				{
					Intent i=new Intent (VehicleSearchActivity.this,com.zira.registration.VehicleInformationActivity.class);
					startActivity(i);
					//finish();
					}
			}
		});

		btnCreditCard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(VehicleSearchActivity.this,ListOfCreditCardsActivity.class));
			
			}
		});

		sourceRelativeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				SingleTon.getInstance().setFrom("");
				startActivity(new Intent(VehicleSearchActivity.this,LocationSettingActivity.class));

			}
		});

		imgAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				destinationAddres="";
				SingleTon.getInstance().setFrom("imgAdd");
				startActivity(new Intent(VehicleSearchActivity.this,LocationSettingActivity.class));

			}
		});

		imgDelete.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				destinationAddres="";
				txtDestination.setText("");
				destinationLayout.setVisibility(View.INVISIBLE);

			}
		});

		cancelImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				carsRelativeLayout.setVisibility(View.VISIBLE);
				destinationAddres="";
				txtPickupLocation.setText("");
				txtDestination.setText("");
				SingleTon.getInstance().setFrom("");
				destinationLayout.setVisibility(View.INVISIBLE);	
				txtEstimatedTime.setVisibility(View.GONE);
				btnRequestPicup.setVisibility(View.GONE);
				optionLayout.setVisibility(View.GONE);
				imgAdd.setVisibility(View.GONE);
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
				cancelImageView.setVisibility(View.GONE);
				slider.setVisibility(View.VISIBLE);			

			}
		});

		setPickUp.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {

				carsRelativeLayout.setVisibility(View.GONE);
				gettingSourceLat=pickUpCoordinates.latitude;
				gettingSourceLong=pickUpCoordinates.longitude;
				txtEstimatedTime.setVisibility(View.VISIBLE);
				btnRequestPicup.setVisibility(View.VISIBLE);
				optionLayout.setVisibility(View.VISIBLE);
				optionLayout.setVisibility(View.VISIBLE);
				imgAdd.setVisibility(View.VISIBLE);
				txtPickupLocation.setText(setPickUp.getText().toString());
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
				slider.setVisibility(View.GONE);
				cancelImageView.setVisibility(View.VISIBLE);
				sourceAddress=setPickUp.getText().toString();
			}
		});

		if (googleMap != null) {
			// The Map is verified. It is now safe to manipulate the map.
			googleMap.setMyLocationEnabled(true); 
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			/*googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
				@Override
				public void onMyLocationChange(Location arg0) {
					//Log.e("trigger","onlocationChanged");
					//onLocationChanged(arg0);

					center =  new LatLng(arg0.getLatitude(), arg0.getLongitude());	 

					SingleTon.getInstance().setLatitute(Double.valueOf(center.latitude));
					SingleTon.getInstance().setLongitude(Double.valueOf(center.longitude));
					Log.e("onMyLocationChange",""+center.latitude+","+""+center.longitude);
					Geocoder geocoder;
					List<Address> addresses = null;
					geocoder = new Geocoder(VehicleSearchActivity.this, Locale.getDefault());
					try {
						addresses = geocoder.getFromLocation(Double.valueOf(arg0.getLatitude()), Double.valueOf(arg0.getLongitude()), 1);
						String address = addresses.get(0).getAddressLine(0);
						String city = addresses.get(0).getAddressLine(1);
						String country = addresses.get(0).getAddressLine(2);
						setPickUp.setText(address+","+city+","+country);

					} catch (Exception e1) {						
						e1.printStackTrace();


					}
					
				}
			});*/

			slider.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					v.startAnimation(buttonClick);
					if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
						mDrawerLayout.closeDrawers();
					}
					else{
						mDrawerLayout.openDrawer(flyoutDrawerRl);
					}
				}
			});

			googleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
				public void onCameraChange(CameraPosition arg0) {

					try {	
						stopBackgroundTimer();
						if(Backgroundtimer != null) {
							Backgroundtimer.cancel();
							Backgroundtimer = null;
						}
						pickUpCoordinates = new Coordinates();
						pickUpCoordinates.setLatitude(arg0.target.latitude);
						pickUpCoordinates.setLongitude(arg0.target.longitude);
						
						startBackgroundTimer(0);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
		}
		
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// SET LISTENERS ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}

	}
	protected void switchBetweenImages(boolean regularCar, boolean xuvCar, boolean LuxaryCar, boolean lux2) {

		try{
			if(regularCar){
				regularCarImageView.setImageResource(R.drawable.reg_car_green);
			}else{
				regularCarImageView.setImageResource(R.drawable.reg_car);
			}
			if(xuvCar){					
				xuvCarImageView.setImageResource(R.drawable.xuv_green);					
			}else{					
				xuvCarImageView.setImageResource(R.drawable.xuv);					
			}
			if(LuxaryCar){					
				taxiCarImageView.setImageResource(R.drawable.luxery_green);					
			}else{					
				taxiCarImageView.setImageResource(R.drawable.luxery);					
			}
			if(lux2){					
				fourcar_ImageView.setImageResource(R.drawable.lux_twogreen);					
			}else{					
				fourcar_ImageView.setImageResource(R.drawable.luxtwo);					
			}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// SWITCH BETWEEN IMAGES ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}
	private String getCurrentDateTime() {		
		try{
		Calendar mCalendar=Calendar.getInstance();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		selectedDate=df.format(mCalendar.getTime());	
		return selectedDate;
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// GET CURRENT DATE TIME ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
			return selectedDate;
		}
	}

	private void initialiseVariable() {
		
		try{
		imgCurrentButton=(ImageView)findViewById(R.id.btnMapCurrentLocation);
		prefs=getSharedPreferences("Zira", MODE_PRIVATE);
		prefs2 = getSharedPreferences("policy", MODE_PRIVATE); 
		vehicleModel=new MainVehicleModel();
		parser = new ZiraParser();
		psplitfare=new PendingSplitFare();
		locFinder = new LocationFinder(VehicleSearchActivity.this);
		mLocation=SingleTon.getInstance().getmLocation();
		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		//googleMap.setTrafficEnabled(true);

		//*******************Sliding Layout****************************//
		profileLayout = (RelativeLayout)findViewById(R.id.profileLayout);		
		slider = (ImageView)findViewById(R.id.user);
		flyoutDrawerRl=(RelativeLayout)findViewById(R.id.left_drawer);
		mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		userImage = (ImageView)findViewById(R.id.userImage);
		//slider clicked Control
		driverMode=(LinearLayout)findViewById(R.id.driverMode);
		support=(LinearLayout)findViewById(R.id.support);
		payment=(LinearLayout)findViewById(R.id.payment);
		promotions=(LinearLayout)findViewById(R.id.promotions);	
		getZira=(LinearLayout)findViewById(R.id.GetZira);
		share=(LinearLayout)findViewById(R.id.share);	
		username = (TextView)findViewById(R.id.username);

		cancelImageView= (TextView)findViewById(R.id.cancelImageView);
		setPickUp = (TextView)findViewById(R.id.setPickUp);
		txtPickupLocation = (TextView)findViewById(R.id.txtPickUpLoc);
		txtDestination = (TextView)findViewById(R.id.txtDestination);
		imgAdd = (ImageView)findViewById(R.id.imgAdd);
		imgAdd.setVisibility(View.GONE);		
		imgDelete = (ImageView)findViewById(R.id.imgDelete);
		imgDelete.setVisibility(View.GONE);		
		destinationLayout = (RelativeLayout)findViewById(R.id.destinationLayout);	
		optionLayout = (RelativeLayout)findViewById(R.id.buttonLayout);
		btnCreditCard = (ImageView)findViewById(R.id.primaryCreditCard);
		btnFare = (ImageView)findViewById(R.id.fareEstimate);
		btnPromoCode = (ImageView)findViewById(R.id.promoCode);
		destinationRelativeLayout= (LinearLayout)findViewById(R.id.destinationRelativeLayout);
		destinationRelativeLayout.setVisibility(View.GONE);		
		sourceRelativeLayout= (LinearLayout)findViewById(R.id.sourceRelativeLayout);
		btnRequestPicup= (ImageView)findViewById(R.id.btnRequest);
		txtEstimatedTime= (TextView)findViewById(R.id.txtEstimatedTime);
		txtEstimatedTime.setVisibility(View.GONE);
		btnRequestPicup.setVisibility(View.GONE);
		optionLayout.setVisibility(View.GONE);
		carsRelativeLayout= (RelativeLayout)findViewById(R.id.carsRelativeLayout);
		carsRelativeLayout.setVisibility(View.VISIBLE);
		
		regularCarImageView=(ImageView)findViewById(R.id.regularCarImageView);
		xuvCarImageView=(ImageView)findViewById(R.id.xuvCarImageView);
		taxiCarImageView=(ImageView)findViewById(R.id.taxiCarImageView);
		fourcar_ImageView=(ImageView)findViewById(R.id.car4);
		//SingleTon.getInstance().setMode("rider");
		Editor e=prefs.edit();
		e.putString("mode", "rider");
		e.commit();
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// INITIALIZE VARIABLE ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}

	}


	private void initializeBackgroundTimerTask(final int flag) {
		try{
		backgroundTimerTask = new TimerTask() {
			public void run() {
				//Log.e("trigger", "onCameraChange");
				//Log.e("onCameraChange", "" + center.latitude + "," + ""	+ center.longitude);
//				getVehicleData();
				if(flag == 0)
				{
					getAddress(pickUpCoordinates, "start");
					}
				else
				{
					getVehicleData();
					}
			}
		};
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// INITIALIZE BACKGROUND TIMER TASK ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	protected void getVehicleData() {	
		
		try{
		if(Util.isNetworkAvailable(VehicleSearchActivity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("longitude",String.valueOf(pickUpCoordinates.longitude)));// "-118.1161111"));//
			nameValuePairs.add(new BasicNameValuePair("latitude",String.valueOf(pickUpCoordinates.latitude)));//  "33.8816667"));//
			nameValuePairs.add(new BasicNameValuePair("currenttime",getCurrentDateTime()));
			nameValuePairs.add(new BasicNameValuePair("distance","10"));
			nameValuePairs.add(new BasicNameValuePair("VechileType",str_vehicleType));
			nameValuePairs.add(new BasicNameValuePair("riderid",prefs.getString("riderid", null)));	
			//Log.e("fetchvehicel", nameValuePairs.toString());
			if(progressFlag == 0){
				progressFlag=1;
				isProgressBeingShown = true;
				mAsyncTask=new AsyncTaskForGettingVehicle("hello", nameValuePairs, VehicleSearchActivity.this,true);
			}else{
				mAsyncTask=new AsyncTaskForGettingVehicle("hello", nameValuePairs, VehicleSearchActivity.this,false);
				
			}
			mAsyncTask.execute();
		}
		else
		{
			Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
		}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// GET VEHICLE DATA ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		
		try{
			
			
			
		System.err.println("onResume");
		mTripDetailsModel = SingleTon.getInstance().getGetTripDetail();
	
		//mapInitialise();
		stopBackgroundTimer();
		
		startBackgroundTimer(1);
		
		
		//**************************register Device******************************************//
		if(driverModeActivated==1){
			
			if(Util.isNetworkAvailable(VehicleSearchActivity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("Role","Rider"));
			nameValuePairs.add(new BasicNameValuePair("DriverId", prefs.getString("riderid", null)));
			nameValuePairs.add(new BasicNameValuePair("RiderId",prefs.getString("riderid", null)));
			nameValuePairs.add(new BasicNameValuePair("DeviceUDId", prefs.getString("udid", null)));
			nameValuePairs.add(new BasicNameValuePair("TokenID",prefs.getString("regid", null)));
			nameValuePairs.add(new BasicNameValuePair("Trigger", "android"));
			AsyncTaskForZira mFetchStates = new AsyncTaskForZira(VehicleSearchActivity.this, registerDevice,nameValuePairs, false, "");
			mFetchStates.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
			mFetchStates.execute();
			driverModeActivated=0;
		}
			else
			{
				Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
			}
		}
		
	
		
		if(getIntent().getStringExtra("splitfare")!=null)
		{
			if(prefs2.getString("splitfare", "").equals(""))
			{
				Editor e =SplashScreen.prefs2.edit();
				e.putString("splitfare","yes");
				e.commit();
				getPendingSplitFare();	
				
				}
			}
		//*********************************************************************************//
		try {
			
			username.setText(prefs.getString("userfname", "").trim() + " "+ prefs.getString("userlname", "").trim());
			
		
			//mProfileInfoModel = SingleTon.getInstance().getProfileInfoModel();
			//username.setText(mProfileInfoModel.getFirstname().trim()+" "+mProfileInfoModel.getLastname().trim());
			imageLoader = new ImageLoader(VehicleSearchActivity.this);
			imageLoader.DisplayImage(prefs.getString("userimage", ""), userImage);
			//imageLoader.DisplayImage(mProfileInfoModel.getImage(), userImage);
			
			if(SingleTon.getInstance().getFrom().equals("LocationSettingActivity")){	
				LatLng latLng = new LatLng(gettingSourceLat, gettingSourceLong);			
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));				
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));				
				SingleTon.getInstance().setFrom("");
			}

			if(SingleTon.getInstance().getFrom().equals("imgAdd")){	

				if(!(destinationAddres.equals(""))){
					destinationLayout.setVisibility(View.VISIBLE);		
					destinationRelativeLayout.setVisibility(View.VISIBLE);		
					imgDelete.setVisibility(View.VISIBLE);		
					txtDestination.setText(destinationAddres);					
				}
				SingleTon.getInstance().setFrom("");
			}
			
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		notificationRider();
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// ON RESUME ///////////"+File.separator+File.separator+ String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	public void allowUserToChooseDestination(){
		try{
		imgAdd.setVisibility(View.VISIBLE);
		destinationLayout.setVisibility(View.VISIBLE);
		optionLayout.setVisibility(View.VISIBLE);

		LatLng latLng = new LatLng(myLat, myLon);

		// Showing the current location in Google Map
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

		// Zoom in the Google Map
		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// ALLOW USER TO CHOOSE DESTINATION ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}

	}

	@SuppressWarnings("null")
	@Override
	public void onLocationChanged(Location location) {

	//	Log.e("in","onlocationChanged");
		
		try{
			if(flag==0){
				flag = 1;
				//editing code
			
				/*if(location==null)
				{
					location = locationManager.getLastKnownLocation(provider);	
					}*/
				//editing code
			
				myLat = location.getLatitude();

				// Getting longitude of the current location
				myLon = location.getLongitude();

				SingleTon.getInstance().setLatitute(Double.valueOf(myLat));
				SingleTon.getInstance().setLongitude(Double.valueOf(myLon));
				
				pickUpCoordinates =  new Coordinates();
				pickUpCoordinates.setLatitude(myLat);
				pickUpCoordinates.setLongitude(myLon);
				
				getAddress(pickUpCoordinates, "start");
				Log.e("on change location111==","lat : "+myLat+" , lon : "+myLon);


				// Creating a LatLng object for the current location
				LatLng latLng = new LatLng(myLat, myLon);

				// Showing the current location in Google Map
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

				// Zoom in the Google Map
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

				
				
			}
			else
			{
				/*if(location==null)
				{
					location = locationManager.getLastKnownLocation(provider);	
					}*/
				
				try{
						if(myLat==0)
						{
							Log.e("on change location2222==","lat : "+myLat+" , lon : "+myLon);
							LatLng latLng = new LatLng( location.getLatitude(), location.getLongitude());
							// Showing the current location in Google Map
							googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
							// Zoom in the Google Map
							googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
							}
						else
						{
							
						}
					}
					catch(Exception e){
						e.printStackTrace();
					}
			}
			//locationManager.removeUpdates(VehicleSearchActivity.this);
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// ON LOCATION CHANGED ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	
	@Override
	public void onProviderDisabled(String provider) {
	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vehicle_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getAddress(final Coordinates location, final String trigger){
		/*final Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(location.latitude);
		coordinates.setLongitude(location.longitude);*/
		try{
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Coordinates> coordinateList = locFinder.getAddressFromLoc(location.latitude, location.longitude);
				if(coordinateList.size()>0){
					location.setAddress(coordinateList.get(0).getAddress());
				}else{
					location.setAddress("Unknown Rd");
				}

				Message msg = new Message();
				msg.obj = location;

				if(trigger.equals("start"))
					msg.what = 0;
				else
					msg.what = 1;

//				timerHandler.sendMessage(msg);
				handler.sendMessage(msg);
			}
		});
		thread.start();
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// GET ADDRESS ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	private final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			Coordinates obj = (Coordinates) msg.obj;
			if(msg.what == 0){
				pickUpCoordinates = obj;
				setPickUp.setText(pickUpCoordinates.getAddress());
				getVehicleData();
				
			}else{
				destinationCoordinates = obj;
			}

		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try{
			locationManager.removeUpdates(VehicleSearchActivity.this);
			stopBackgroundTimer();
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// ON DESTROY ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	public class AsyncTaskForGettingVehicle extends AsyncTask<String, Void, String> {
		ProgressDialog pDialog;
		private Activity activity;		
		private String result = "";			
		private String methodName;		
		private ArrayList<NameValuePair> nameValuePairs;
		private boolean displayDialog;
		public AsyncTaskForGettingVehicle(String methodName,ArrayList<NameValuePair> nameValuePairs,Activity activity,boolean displayDialog) {
			this.activity = activity;
			this.methodName = methodName;
			this.nameValuePairs = nameValuePairs;
			this.displayDialog = displayDialog;
		}

		@Override
		protected void onPreExecute() {
			estimstetime="";
			if(isProgressBeingShown){
				pDialog = new ProgressDialog(activity);
				pDialog.setTitle("Zira 24/7");
				pDialog.setMessage("Please wait..");
				pDialog.setCancelable(false);
				pDialog.show();
			}
			super.onPreExecute();
		}
		@Override
		protected String doInBackground(String... urls) {

			for(int i=0;i<2;i++){
				if(mAsyncTask.isCancelled()) break;
			}
			result = Util.getResponseFromUrl("FetchVehicleList", nameValuePairs, activity);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			vehicleModel = parser.parseVehicleInZone(result);
			
			
			Log.e("Fetch vehicel list", result);

			googleMap.clear();
			try{
			if(vehicleModel.getMessage().equals("Success")){
				System.err.println("vehicle size="+vehicleModel.getVehicleInfoList().size());
				
				for(int i=0;i<vehicleModel.getVehicleInfoList().size();i++){			
					customMarker=googleMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(vehicleModel.getVehicleInfoList().get(i).getLatitude()), Double.valueOf(vehicleModel.getVehicleInfoList().get(i).getLongitude()))).icon(BitmapDescriptorFactory.fromResource((R.drawable.car_icon))));
					estimstetime=vehicleModel.getVehicleInfoList().get(i).getTime();
					}
				
				txtEstimatedTime.setText("ESTIMATED TIME OF ARRIVAL: "+estimstetime);	
				
				
				}	
			} catch (Exception e) {

				e.printStackTrace();
			}

			SingleTon.getInstance().setMainVehicleModel(vehicleModel);
//			Log.d("tag",vehicleModel.getMessage());
//			Log.e("tag",result);
			//startBackgroundTimer(1);
			if(isProgressBeingShown){
				isProgressBeingShown= false;
				
//				Util.alertMessage(VehicleSearchActivity.this, "vehicle fare response::"+result);
			}
			try{
				pDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

	}

	protected void startBackgroundTimer(int flag) {
		stopBackgroundTimer();
		value=1;		
		Backgroundtimer = new Timer();
		initializeBackgroundTimerTask(flag);
		Backgroundtimer.schedule(backgroundTimerTask, 5000);
		
		Log.e("","backtimerStart");	
	}

	public void stopBackgroundTimer() {
		value=0;
		if (Backgroundtimer != null) {
			Backgroundtimer.cancel();
			Backgroundtimer = null;
//			System.out.println("backtimerStop");
			Log.e("","backtimerStop");	
		}
	}

	/*public void stopTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
			System.out.println("timer");
			Log.e("","timerrStop");	
		}
	}*/

	/* @Override
	public void onBackPressed() {
//    	super.onBackPressed();
    	Log.e("back pressed","back pressed");
    	AlertDialog.Builder alert = new AlertDialog.Builder(VehicleSearchActivity.this);
    	alert.setTitle("Zira 24/7");
    	alert.setMessage("Do you want to exit from app?");
    	alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				finish();
			}
		});

    }*/
	@Override
	protected void onPause() {		
		super.onPause();
		try {
			mAsyncTask.cancel(true);
			//stopTimer();
			stopBackgroundTimer();
			Log.i("tag","onPause");
		} catch (Exception e) {		
			e.printStackTrace();
		}
		try{
			if(ncheck==1)
			{
				pDialogn.dismiss();
				ncheck=0;
				System.err.println("on pauseeeeeeee");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initializeBackgroundTimerTaskdailog() {
		backgroundTimerTask = new TimerTask() {
			public void run() {

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
//						if(countdownNumber == 0) {
							//cancel();
						stopBackgroundTimerDailog();
							try{
								pDialogn.dismiss();
							}
							catch(Exception e)
							{
								e.printStackTrace();
								
							}
//						}
//						//counterTextView.setText(String.valueOf(countdownNumber));
//						countdownNumber--;
//						System.err.println("countdownNumber="+countdownNumber);
					}
				});
			}
		};
	}
	protected void startBackgroundTimerfordialog() {
		stopBackgroundTimerDailog();

		Backgroundtimer = new Timer();
		initializeBackgroundTimerTaskdailog();
		Backgroundtimer.schedule(backgroundTimerTask, 60000, 60000);
		System.out.println("backtimerStart");
		Log.e("","backtimerStart");	

	}

	public void stopBackgroundTimerDailog() {

		if (Backgroundtimer != null) {
			Backgroundtimer.cancel();
			Backgroundtimer = null;
			System.out.println("backtimerStop");
			Log.e("","backtimerStop");	
		}
	}
	@Override
	public void processFinish(String output, String methodName) {

		JSONObject obj;
	
		try {
			obj = new JSONObject(output);
			jsonMessage=obj.getString("message");
			jsonResult=obj.getString("result");
//			Toast.makeText(VehicleSearchActivity.this, jsonMessage, 1).show();
			if(methodName.equals(requestRide)){			
				Log.e("requestRide", output);
				if(jsonResult.equals("0")){
				//	Toast.makeText(VehicleSearchActivity.this,"Request has been sent", 1).show();
					pDialogn = new ProgressDialog(VehicleSearchActivity.this);
					pDialogn.setTitle("Zira 24/7");
					pDialogn.setMessage("Please wait while we are searching for a driver...");
					pDialogn.setCancelable(false);
					pDialogn.show();
					startBackgroundTimerfordialog();
				}
				else
				{
					Toast.makeText(VehicleSearchActivity.this,jsonMessage, 1).show();
					}
			}		
			else if(methodName.equals(switchBetweenMode)){
				Log.e("switchBetweenMode=", output);
				if(jsonResult.equals("0")){
					stopBackgroundTimer();
					startActivity(new Intent(VehicleSearchActivity.this,DriverModeActivity.class));	
				    // SingleTon.getInstance().setMode("driver");
				}
				else
				{
					Toast.makeText(VehicleSearchActivity.this, "Please fill the vehicle detail first", 0).show();	
			}		}
			else if(methodName.equals(registerDevice)){			
//				Toast.makeText(VehicleSearchActivity.this, jsonMessage, 1).show();
				editor=prefs.edit();
				editor.putString("mode", "rider");
				editor.commit();
			}
		

		if(methodName.equals(GetPendingSplitFare))
		{
			Log.e("GetPendingSplitFare", output);
			String jsonResultsplit = "";
			String jsonMessagesplit="";
				
				JSONObject splitObj = new JSONObject(output);
				jsonResultsplit=splitObj.getString("result");
				psplitfare.setJsonResultsplit(jsonResultsplit);
				jsonMessagesplit=splitObj.getString("message");
				psplitfare.setJsonMessage(jsonMessagesplit);
				
				psplitfare.setPendingsplitfare(splitObj.getString("pendingsplitfare"));
				psplitfare.setAmount(splitObj.getString("amount"));
				psplitfare.setTripid(splitObj.getString("tripid"));
				
				if(jsonResultsplit.equals("0"))
				{
					AcceptRejectDailog();	
					}		
				else
				{
					//Util.alertMessage(VehicleSearchActivity.this, jsonMessagesplit);
					}
		}
		else if(methodName.equals(AcceptRejectPendingFareRequest))
		{
			Log.e("AcceptRejectPendingFareRequest", output);
			}
		else if(methodName.equals(GetProfile))
		{
			Log.e("GetProfile", output);
			mProfileInfoModel = parser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			Intent intent = new Intent(VehicleSearchActivity.this, GetProfile.class);
			startActivity(intent);
			
			}
		else if (methodName.equals("GetLastActiveTripswithStatus")) {
			Log.e("GetLastActiveTripswithStatus", output);
			//SingleTon.getInstance().setMode("rider");
			mTripDetailsModel = parser.getLastActiveDetail(output);
			SingleTon.getInstance().setGetTripDetail(mTripDetailsModel);
			//SingleTon.getInstance().setReceivednotificationTripID(receivednotificationTripID)
			Log.e("mTripDetailsModel", output);
			
			/*if(mTripDetailsModel.getTrip_status().equalsIgnoreCase("Accepted"))//accept
			{
				DriverNotifications.routecheck=1;
				DriverNotifications.tripstart=true;
				Intent i=new Intent(DriverModeActivity.this,DriverNotifications.class);
				i.putExtra("accepted", "yes");
				startActivity(i);
			}
			
			else if(mTripDetailsModel.getTrip_status().equals("arrived"))//accept
			{
				DriverNotifications.tripstart=true;
				Intent i=new Intent(DriverModeActivity.this,DriverNotifications.class);
				i.putExtra("arrived", "yes");
				startActivity(i);
			}*/
			
			if(mTripDetailsModel.getTrip_status().equalsIgnoreCase("begintrip"))//
			{
				
				Intent i=new Intent(VehicleSearchActivity.this,RiderRideView.class);
				i.putExtra("begun", "yes");
				startActivity(i);
			}
			/*if(mTripDetailsModel.getTrip_status().equals("bugin"))
			{
				Intent i=new Intent(DriverModeActivity.this,DriverNotifications.class);
				i.putExtra("begun", "yes");
				startActivity(i);
			}*/
			
		}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// PROCESS FINISH ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	//****************************Async to Calculate Distance **********************************//	
	class distanceCalculatorAsyncTask extends AsyncTask<String, String, String>{

		ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			pDialog = new ProgressDialog(VehicleSearchActivity.this);
			pDialog.setTitle("Zira 24/7");
			pDialog.setMessage("Loading data...");
			pDialog.setCancelable(false);
			pDialog.show();
		}		    

		@Override
		protected String doInBackground(String... str) {
//			Log.i("tag", "Current Lati:"+gettingSourceLat+"  Current Longitude:"+gettingSourceLong+"  Destination_Lati:"+gettingdestinationLat+" Destination Longitude:"+gettingDestinationLong);
			String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+gettingSourceLat+","+gettingSourceLong+"&destinations="+gettingdestinationLat+","+gettingDestinationLong+"&mode=driving&language=en-EN&sensor=false";

			try{
				HttpGet httpGet = new HttpGet(distance_url);
				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();
				response = client.execute(httpGet);
				HttpEntity entity = response.getEntity();
				InputStream stream = entity.getContent();
				int b;

				while ((b = stream.read()) != -1) {
					stringBuilder.append((char) b);
				}

				Log.e("result",stringBuilder.toString());
				JSONObject jsonObject = new JSONObject();
				jsonObject = new JSONObject(stringBuilder.toString());
				myAddress = jsonObject.getJSONArray("origin_addresses").getString(0);
				JSONArray searchResults =  jsonObject.getJSONArray(("rows"));

				for(int i = 0; i<searchResults.length(); i++){
					JSONArray elementArray = searchResults.getJSONObject(i).getJSONArray("elements");
					for(int j =0; j<elementArray.length(); j++){
						distance = elementArray.getJSONObject(j).getJSONObject("distance").getString("text");
						distanceValue = elementArray.getJSONObject(j).getJSONObject("distance").getInt("value");

						totalKms = (float)(distanceValue*0.001);
						totalKmInMiles=(float) (totalKms*0.62137);
						Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);

						time    = elementArray.getJSONObject(j).getJSONObject("duration").getString("text");
						timeValue = elementArray.getJSONObject(j).getJSONObject("duration").getInt("value");

						float temp= (float)timeValue/60;
						approxMinutes = Math.round(temp);
						Log.e("result","Option "+" :: "+distance);
						System.err.print("time"+ time);
					}
				}
				Log.e("done","done");

			}catch(Exception e){
				Log.e("error","error");
				e.printStackTrace();
			}

			return "success";
		}


		@Override
		protected void onPostExecute(String result) {
			pDialog.dismiss();
			if(result.equals("success")){	

				calculateFare(str_vehicleType);	
				 getOnline_DriverIds();
				if(from==1){
					if(Util.isNetworkAvailable(VehicleSearchActivity.this)){
					
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("DriverList",driverids));
					nameValuePairs.add(new BasicNameValuePair("riderId",prefs.getString("riderid", "")));
					nameValuePairs.add(new BasicNameValuePair("driverId", vehicleModel.getVehicleInfoList().get(0).getDriverid()));
					nameValuePairs.add(new BasicNameValuePair("requestType",""));
					nameValuePairs.add(new BasicNameValuePair("starting_loc",txtPickupLocation.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("ending_loc",txtDestination.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("start_long", String.valueOf(gettingSourceLong)));
					nameValuePairs.add(new BasicNameValuePair("end_long",String.valueOf(gettingDestinationLong)));
					nameValuePairs.add(new BasicNameValuePair("start_lat",String.valueOf(gettingSourceLat)));
					nameValuePairs.add(new BasicNameValuePair("end_lat",String.valueOf(gettingdestinationLat)));
					nameValuePairs.add(new BasicNameValuePair("trip_request_date",selectedDate));
					nameValuePairs.add(new BasicNameValuePair("trip_request_pickup_date",selectedDate));
					nameValuePairs.add(new BasicNameValuePair("trip_miles_est",""));
					nameValuePairs.add(new BasicNameValuePair("trip_miles_act",""));
					nameValuePairs.add(new BasicNameValuePair("trip_time_est", ""));
					nameValuePairs.add(new BasicNameValuePair("trip_time_act",""));
					nameValuePairs.add(new BasicNameValuePair("offered_fare", String.valueOf(actualfare)));
					nameValuePairs.add(new BasicNameValuePair("setfare",String.valueOf(actualfare)));
					nameValuePairs.add(new BasicNameValuePair("vehicle_type",String.valueOf(vehicleType)));
					//nameValuePairs.add(new BasicNameValuePair("deviceType","android"));
					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(VehicleSearchActivity.this, requestRide,nameValuePairs, true, "Please wait...");
					mWebPageTask.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
					mWebPageTask.execute();
					from=0;
					}
					else
					{
						Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
						}
				}
				else{

					startActivity(new Intent(VehicleSearchActivity.this,CalculateFareActivity.class));
				}
			}
		}
	}

	//******************************Method to calculate Fare***********************************//
	public void getOnline_DriverIds()
	{
		try{
		for(int i=0;i<vehicleModel.getVehicleInfoList().size();i++)
		{
			if(i==0){
				driverids=vehicleModel.getVehicleInfoList().get(i).getDriverid();
				System.err.println("driver id==="+driverids);
			}
			else if(i>0)
			{
				driverids=driverids+","+vehicleModel.getVehicleInfoList().get(i).getDriverid();
				System.err.println("driver idsss==="+driverids);
				}
			
			}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// GET ONLINE DRIVER IDs ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
		}
	
	public void calculateFare(String vehicleType) {

		try{
		if(vehicleType.equalsIgnoreCase("ZiraE")){
			try{
					actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getBasePrice())+
						(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice())
								+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice())));
			
					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getSurgeprice());
					if(surgePrice>0.0)
					{
						actualfare=actualfare*surgePrice;
						}
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			}catch(Exception e){
				System.err.println("actual="+e);
				}
		} 
		else if(vehicleType.equalsIgnoreCase("ZiraPlus")){
			try{
			actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getBasePrice())+
					(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice())
							+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice())));
		
				float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getSurgeprice());
				if(surgePrice>0.0)
				{
					actualfare=actualfare*surgePrice;
					}
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			}catch(Exception e){
				System.err.println("actual="+e);
			}
		}
		else if(vehicleType.equalsIgnoreCase("ZiraTaxi")){
			try{
			actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getBasePrice())+
					(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice())
							+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice())));
		
			float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getSurgeprice());
				if(surgePrice>0.0)
				{
					actualfare=actualfare*surgePrice;
					}
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			}catch(Exception e){
				System.err.println("actual="+e);
			}
		}
		else if(vehicleType.equalsIgnoreCase("ZiraLux")){
			try{
				actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getBasePrice())+
					(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice())
							+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice())));
		
				float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getSurgeprice());
				if(surgePrice>0.0)
				{
					actualfare=actualfare*surgePrice;
					}
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			}catch(Exception e){
				System.err.println("actual="+e);
			}
		}
		}catch(Exception e){
			File myFile = new File(Environment.getExternalStorageDirectory()+"/ziralogs.txt");
//					("/sdcard/ziralogs.txt");
			try
			{
				FileOutputStream fOut = new FileOutputStream(myFile, true);
				OutputStreamWriter myOutWriter = 
										new OutputStreamWriter(fOut);
				myOutWriter.append(File.separator);
				myOutWriter.append("//////////////// CALCULATING FARE ///////////"+File.separator+File.separator + String.valueOf(new Date()));
				
				String stackTrace = Log.getStackTraceString(e);
				myOutWriter.append(stackTrace);
				myOutWriter.close();
				fOut.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();  
			}
		}
	}

	//*****************************Methods for drawing path on map*****************************//
	private String getDirectionsUrl(LatLng origin,LatLng dest){

		// Origin of route
		String str_origin = "origin="+origin.latitude+","+origin.longitude;

		// Destination of route
		String str_dest = "destination="+dest.latitude+","+dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

		Log.e("", url);

		return url;
	}
	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while( ( line = br.readLine()) != null){
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		}catch(Exception e){
			Log.d("Exception while downloading url", e.toString());
		}finally{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder alert = new AlertDialog.Builder(VehicleSearchActivity.this);
		alert.setMessage("Are you sure you want to exit?");
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});
	};
	
	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>{

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			}catch(Exception e){
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result) {
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();

			// Traversing through all the routes
			for(int i=0;i<result.size();i++){
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){
					HashMap<String,String> point = path.get(j);

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);

					points.add(position);
				}

				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(8);
				lineOptions.color(Color.RED);
			}

			// Drawing polyline in the Google Map for the i-th route
			googleMap.addPolyline(lineOptions);
		}
	}
	//*******************************************************************************//
	
	public void getPendingSplitFare(){
	
		if(Util.isNetworkAvailable(VehicleSearchActivity.this))
				{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mobile",prefs.getString("usermobile", "")));
		params.add(new BasicNameValuePair("UserId",prefs.getString("riderid", "")));
	
		AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(VehicleSearchActivity.this, GetPendingSplitFare,params,false,"");
		mWebPageTask1.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
		mWebPageTask1.execute();
				}
		else
		{
			Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");	
		}
	}
	
	public void acceptPendingFareRequest(){
		if(Util.isNetworkAvailable(VehicleSearchActivity.this))
		{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("RiderId",prefs.getString("riderid", "")));
		params.add(new BasicNameValuePair("Trigger", "Accept"));
		params.add(new BasicNameValuePair("Amount", psplitfare.getAmount()));
		params.add(new BasicNameValuePair("TripId", psplitfare.getTripid()));
		
		AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(VehicleSearchActivity.this, AcceptRejectPendingFareRequest,params,true,"Please wait...");
		mWebPageTask1.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
		mWebPageTask1.execute();
		}
		else
		{
			Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");	
		}
	}
	public void rejectPendingFareRequest(){
		if(Util.isNetworkAvailable(VehicleSearchActivity.this))
		{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("RiderId",prefs.getString("riderid", "")));
		params.add(new BasicNameValuePair("Trigger", "Reject"));
		params.add(new BasicNameValuePair("Amount", psplitfare.getAmount()));
		params.add(new BasicNameValuePair("TripId", psplitfare.getTripid()));
	
		AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(VehicleSearchActivity.this, AcceptRejectPendingFareRequest,params, true, "Please wait...");
		mWebPageTask1.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;
		mWebPageTask1.execute();
		}
	else
	{
		Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");	
	}
	}
	protected void AcceptRejectDailog() {

		final CharSequence[] options = {"ACCEPT", "REJECT","CANCEL" };

		AlertDialog.Builder builder = new AlertDialog.Builder(VehicleSearchActivity.this);
		builder.setTitle("SPLIT FARE");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("ACCEPT"))
				{
					acceptPendingFareRequest();
					}
				else if (options[item].equals("REJECT"))
				{
					rejectPendingFareRequest();
					}
				else if (options[item].equals("CANCEL")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}
	
	public void notificationRider()
	{
		if(prefs.getString("message", "").equals(""))
		{}
		else
		{
		String DriverName=null,driverMessage=null,driverImage=null,driverTripId=null,driverVehicleImage=null,driverLastData=null;		
		String message=	prefs.getString("message", "");
		if(message.contains("arrived")){

			String[] values = message.split("@");
			DriverName=values[0];
			driverMessage=values[1];
			driverImage=values[2];
			driverTripId=values[3];	
			SingleTon.getInstance().setDriverName(DriverName);
			SingleTon.getInstance().setDriverMessage(driverMessage);
			SingleTon.getInstance().setDriverImage(driverImage);
			SingleTon.getInstance().setDriverTripId(driverTripId);

			Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
			launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(launchDialog);
		}

		else if(message.contains(" has ended the ride")){
		
			String[] values = message.split("@");
			DriverName=values[0];
			driverMessage=values[1];
			driverImage=values[2];
			driverTripId=values[3];	
			SingleTon.getInstance().setDriverName(DriverName);
			SingleTon.getInstance().setDriverMessage(driverMessage);
			SingleTon.getInstance().setDriverImage(driverImage);
			SingleTon.getInstance().setDriverTripId(driverTripId);

			Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
			launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(launchDialog);
		
		}
		else if(message.contains("has begun the trip")){

			String[] values = message.split("@");
			DriverName=values[0];
			driverMessage=values[1];
			driverImage=values[2];
			driverTripId=values[3];	
			SingleTon.getInstance().setDriverName(DriverName);
			SingleTon.getInstance().setDriverMessage(driverMessage);
			SingleTon.getInstance().setDriverImage(driverImage);
			SingleTon.getInstance().setDriverTripId(driverTripId);

			Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
			launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(launchDialog);
		}

		else if(message.contains("Your driver has cancelled the ride and minimum cancellation charges will be deducted from your account")){

			SingleTon.getInstance().setDriverMessage("Your driver has cancelled the ride and minimum cancellation charges will be deducted from your account");

			Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
			launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			startActivity(launchDialog);
		}


		else if(message.contains("arriving")){

			String[] values = message.split("@");
			DriverName=values[0];
			driverMessage=values[1];
			driverImage=values[2];
			driverTripId=values[3];			
			driverVehicleImage=values[4];			
			driverLastData=values[5];	

			SingleTon.getInstance().setDriverName(DriverName);
			SingleTon.getInstance().setDriverMessage(driverMessage);
			SingleTon.getInstance().setDriverImage(driverImage);
			SingleTon.getInstance().setDriverTripId(driverTripId);
			SingleTon.getInstance().setDriverImage(driverImage);
			SingleTon.getInstance().setDriverTripId(driverTripId);

			Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
			launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(launchDialog);
		}
		Editor e=prefs.edit();
		e.putString("message", "");
		e.commit();
	}
	}
	@Override
	protected void onStop() {
	stopBackgroundTimer();
		super.onStop();
	}
	private void getProfileInfo() {
		if(Util.isNetworkAvailable(VehicleSearchActivity.this))
		{
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
		AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(VehicleSearchActivity.this, GetProfile,nameValuePair, true, "Please wait..");
		mWebPageTask.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;mWebPageTask.execute();
		
		}else
		{
			Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
			}
		}
	
	
	private void GetLastActiveTrip() {
		if(Util.isNetworkAvailable(VehicleSearchActivity.this))
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
			nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
			AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(VehicleSearchActivity.this, "GetLastActiveTripswithStatus",nameValuePair, false, "");
			mWebPageTask.delegate = (AsyncResponseForZira) VehicleSearchActivity.this;mWebPageTask.execute();
		}else
		{
			Util.alertMessage(VehicleSearchActivity.this, "Please check your internet connection");
			}
		}
}
