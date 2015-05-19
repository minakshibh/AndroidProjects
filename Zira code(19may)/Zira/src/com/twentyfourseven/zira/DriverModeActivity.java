package com.twentyfourseven.zira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.Coordinates;
import com.zira.modal.GetTripDetails;
import com.zira.modal.MainVehicleModel;
import com.zira.modal.ProfileInfoModel;
import com.zira.notification.DriverNotifications;
import com.zira.profile.GetProfile;
import com.zira.registration.VehicleInformationActivity;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class DriverModeActivity extends FragmentActivity implements LocationListener,
AsyncResponseForZira {

	
	public static int forCheking=0;
	public RelativeLayout flyoutDrawerRl;
	public ImageView sliderIcon;
	public DrawerLayout mDrawerLayout;
	public AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
	private GoogleMap googleMap;
	private LocationManager locationManager;
	private int flag = 0;
	private String provider;
	private static double myLat=0, myLon=0;
	private Criteria criteria;
	int active_trip=0;
//private Handler timerHandler = new Handler();
//private TextView txtDestination, txtPickupLocation, setPickUp;
//private TextView textViewWhoSendRequest;
//private ImageView imageViewDriverImage;
//private Button acceptButton, rejectButton;
//private String acceptRejectPendingFareRequest = "AcceptRejectPendingFareRequest";
	private LocationFinder locFinder;
	private Coordinates pickUpCoordinates, destinationCoordinates;
//	private Location center;
	private ProfileInfoModel mProfileInfoModel;
	private LinearLayout driverMode;
	private ImageLoader imageLoader;
	private ImageView userImage;
	private TextView username;
	private SharedPreferences prefs;
	private Editor editor;

	private String registerDevice = "RegisterDevice";
	private String switchBetweenMode = "SwitchBetweenMode";
	private String UpdateDriverLocation="UpdateDriverLocation";
	private String NotifyRegardingArrival="NotifyRegardingArrival";
	private String GetProfile="GetProfiles";
	
	private RelativeLayout riderInfoRelativeLayout;
	private LinearLayout bottomLinearLayout;
	private Button endRideButton;
	public static int fromActivtyDriverNotification = 0;
	double curLongitude, curLatitude, destLongitude, destLatitude;
	double longitude=0, latitude=0;

	private GetTripDetails mTripDetailsModel;
	private String selectedDate;
	
	private int endRide=0;
	private int driverModeValue=0;

//////////////////
	float totalKms =00,totalKmInMiles=00;
	private int distanceValue,timeValue,approxMinutes;
	private MainVehicleModel vehicleModel;
	private String distance,time,myAddress;
	double tripStartLat=0,tripStartLong=0,tripdestinationLat=0,tripdestinationLong=0;
	float actualfare;
	int flagdis=0;
	private String newactualf="0";
	private ZiraParser parser;
	private  AsyncTaskForGettingVehicle mAsyncTask;
	private RelativeLayout profileLayout;
	private Button button_getdirection;
	private GetTripDetails tripDetailsModal;

	String jsonMessage = null;
	String result = null;
	String end_tripid="";
//////////////////

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_driver_mode);

		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		DriverNotifications.tripstart=false;
		
		GetLastActiveTrip();
		registerDeviceForDriver();
		initialisevariable();
		initialiseListener();
		getProfileInfo();
		initialiseMap();

	}

	private void GetLastActiveTrip() {
		if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
			nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
			AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(DriverModeActivity.this, "GetLastActiveTripswithStatus",nameValuePair, false, "");
			mWebPageTask.delegate = (AsyncResponseForZira) DriverModeActivity.this;mWebPageTask.execute();
		}else
		{
			Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
			}
		}

	private void getProfileInfo() {
		if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
		AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(DriverModeActivity.this, GetProfile,nameValuePair, false, "");
		mWebPageTask.delegate = (AsyncResponseForZira) DriverModeActivity.this;mWebPageTask.execute();
		
		}else
		{
			Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
			}
		}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (fromActivtyDriverNotification == 1) {

			mTripDetailsModel = SingleTon.getInstance().getGetTripDetail();
		/*	curLatitude = Double.parseDouble(mTripDetailsModel.getGetTrip_StartingLatitude());
			curLongitude = Double.parseDouble(mTripDetailsModel.getGetTrip_StartingLongitude());
			destLongitude = Double.parseDouble(mTripDetailsModel.getGetTrip_EndLongitude());
			destLatitude = Double.parseDouble(mTripDetailsModel.getGetTrip_EndLatitude());*/
			
			curLatitude = Double.parseDouble(prefs.getString("tripstarttlat", ""));
			curLongitude = Double.parseDouble(prefs.getString("tripstartlong", ""));
			destLongitude = Double.parseDouble(prefs.getString("tripdestlong", ""));
			destLatitude = Double.parseDouble(prefs.getString("tripdestlat", ""));
			if(DriverNotifications.tripcancel==false)
			{
				endRideButton.setVisibility(View.VISIBLE);
				button_getdirection.setVisibility(View.VISIBLE);
				}
			else
			{
				endRideButton.setVisibility(View.GONE);
				button_getdirection.setVisibility(View.GONE);
				}
			initialiseMap();
		}
		notificationDriver();
	}
	
	private void updateDriverLocation() {
		if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("DriverId", prefs.getString("riderid", null)));
		nameValuePairs.add(new BasicNameValuePair("longitude",""+longitude));
		nameValuePairs.add(new BasicNameValuePair("latitude", ""+latitude));
		Log.e("UpdateDriverLocation", nameValuePairs.toString());
		AsyncTaskForZira mFetchStates = new AsyncTaskForZira(DriverModeActivity.this, UpdateDriverLocation, nameValuePairs, false,"");
		mFetchStates.delegate = (AsyncResponseForZira) DriverModeActivity.this;
		mFetchStates.execute();
	}else
	{
		Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
		}
		
	}

	private void registerDeviceForDriver() {
		if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("Role", "Driver"));
		nameValuePairs.add(new BasicNameValuePair("DriverId", prefs.getString("riderid", null)));
		nameValuePairs.add(new BasicNameValuePair("RiderId", prefs.getString("riderid", null)));
		nameValuePairs.add(new BasicNameValuePair("DeviceUDId", prefs.getString("udid", null)));
		nameValuePairs.add(new BasicNameValuePair("TokenID", prefs.getString("regid", null)));
		nameValuePairs.add(new BasicNameValuePair("Trigger", "android"));
		AsyncTaskForZira mFetchStates = new AsyncTaskForZira(DriverModeActivity.this, registerDevice, nameValuePairs, false,"");
		mFetchStates.delegate = (AsyncResponseForZira) DriverModeActivity.this;
		mFetchStates.execute();
	}else
	{
		Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
		}
	}

	private void initialiseMap() {

		if (googleMap != null) {

			googleMap.setMyLocationEnabled(true); 
			googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			//criteria = new Criteria();

			// Getting the name of the best provider
			  //provider = locationManager.getBestProvider(criteria, true);
		      Location location =getLastKnownLocation();
		      
		      if(location!=null){
	              onLocationChanged(location);
	          }
			try{
				boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				// Check if enabled and if not send user to the GSP settings
				if (!enabledGPS && !enabledWiFi) {
					Toast.makeText(DriverModeActivity.this, "GPS signal not found", Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

			

			if (fromActivtyDriverNotification == 1) {
				if(DriverNotifications.tripcancel==true)
				{}
				else{
					CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(curLatitude, curLongitude)).zoom(15).build();
	
					googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
					googleMap.clear();
					placeMarker();
	
					LatLng curLatLng = new LatLng(curLatitude, curLongitude);
					LatLng destLatLng = new LatLng(destLatitude, destLongitude);
					String url = getDirectionsUrl(curLatLng, destLatLng);
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(url);
	
					Log.i("tag", "Longitude:" + curLongitude);
					Log.i("tag", "Latitude:" + curLatitude);
					Log.i("tag", "destLongitude:" + destLongitude);
					Log.i("tag", "destLatitude:" + destLatitude);
			}
				
				
			}
			//locationManager.requestLocationUpdates(provider, 10000, 0, this);
		}

		
		try {
			boolean enabledGPS = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean enabledWiFi = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// Check if enabled and if not send user to the GSP settings
			if (!enabledGPS && !enabledWiFi) {
				Toast.makeText(DriverModeActivity.this, "GPS signal not found",
						Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		locationManager.requestLocationUpdates(provider, 1000, 0, this);
	}

	private void initialisevariable() {

		Editor e=prefs.edit();
		e.putString("mode", "driver");
		e.commit();
		System.err.println("modeeeeeeeeeeeee"+"driverrrrrrrrrrrr");
		parser=new ZiraParser();
		VehicleSearchActivity.driverModeActivated = 1;
		mProfileInfoModel = SingleTon.getInstance().getProfileInfoModel();
		username = (TextView) findViewById(R.id.username);
		driverMode = (LinearLayout) findViewById(R.id.driverMode);
		//setPickUp = (TextView) findViewById(R.id.setPickUp);
		locFinder = new LocationFinder(DriverModeActivity.this);
		flyoutDrawerRl = (RelativeLayout) findViewById(R.id.left_drawer_driver);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		userImage = (ImageView) findViewById(R.id.userImage);
		sliderIcon = (ImageView) findViewById(R.id.sliderIcon);

		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		//FragmentManager myFragmentManager = getSupportFragmentManager();
		//SupportMapFragment mySupportMapFragment = (SupportMapFragment)myFragmentManager.findFragmentById(R.id.map);
		//googleMap = mySupportMapFragment.getMap();
		googleMap.setTrafficEnabled(true);
			
		username.setText(prefs.getString("userfname", "test").trim() + " "+ prefs.getString("userlname", "test").trim());
		imageLoader = new ImageLoader(DriverModeActivity.this);
		imageLoader.DisplayImage(prefs.getString("userimage", ""), userImage);

		riderInfoRelativeLayout = (RelativeLayout) findViewById(R.id.riderInfoRelativeLayout);
		riderInfoRelativeLayout.setVisibility(View.GONE);
		//textViewWhoSendRequest = (TextView) findViewById(R.id.textViewWhoSendRequest);
		//imageViewDriverImage = (ImageView) findViewById(R.id.imageViewDriverImage);
		/*	acceptButton = (Button) findViewById(R.id.acceptButton);
		rejectButton = (Button) findViewById(R.id.rejectButton);*/
		bottomLinearLayout = (LinearLayout) findViewById(R.id.bottomLinearLayout);
		bottomLinearLayout.setVisibility(View.GONE);
		endRideButton = (Button) findViewById(R.id.endRideButton);
		endRideButton.setVisibility(View.GONE);
	
		profileLayout = (RelativeLayout)findViewById(R.id.profileLayout);
		button_getdirection = (Button) findViewById(R.id.button_getdrection);
		button_getdirection.setVisibility(View.GONE);
		
		

	}

	private void initialiseListener() {

		endRideButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
			
				AlertDialog.Builder alert = new AlertDialog.Builder(DriverModeActivity.this);
				alert.setTitle("Please confirm");
				alert.setMessage("Are you sure you want to Drop Off?");
				alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						locationManager.removeUpdates(DriverModeActivity.this);
						
						if(newactualf.equals("0"))
						{
							Toast.makeText(DriverModeActivity.this, "Calculating Fare...", Toast.LENGTH_LONG).show();
							new	distanceCalculatorAsyncTask().execute("1");
						}
						else
						{
							endRide();
							}
						
						/*
						if(newactualf.equals("0"))
						{
							Toast.makeText(DriverModeActivity.this, "Please wait..", 1).show();
							
							}
						else
						{
						endRide=1;
						
						getCurrentTime();
						if(prefs.getString("activetripid", "").equals(""))
						{
							end_tripid=SingleTon.getInstance().getReceivednotificationTripID();
							
							}
							else
							{
								end_tripid=prefs.getString("activetripid", "");
										
								}
					if(Util.isNetworkAvailable(DriverModeActivity.this))
						{
						ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("TripId",end_tripid));
						params.add(new BasicNameValuePair("Status", "end"));
						params.add(new BasicNameValuePair("Timestamp", ""));
						params.add(new BasicNameValuePair("Latitude",String.valueOf(myLat)));
						params.add(new BasicNameValuePair("Longitude",String.valueOf(myLon)));
//						params.add(new BasicNameValuePair("TripMilesActual", mTripDetailsModel.getGetTrip_ActaulFare()));
				
						params.add(new BasicNameValuePair("TripMilesActual", distance));
						params.add(new BasicNameValuePair("TripAmountActual", String.valueOf(newactualf)));
						///////////
						params.add(new BasicNameValuePair("TripTimeActual", ""));
//						params.add(new BasicNameValuePair("TripAmountActual", mTripDetailsModel.getGetTrip_Trip_Total_amount()));				 
						params.add(new BasicNameValuePair("Trigger", ""));
						params.add(new BasicNameValuePair("PaymentStatus", ""));
						params.add(new BasicNameValuePair("CancellationCharges", ""));
						//params.add(new BasicNameValuePair("deviceType", "android"));

						Log.e("tag", "end ride :"+params.toString());
						AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(DriverModeActivity.this, NotifyRegardingArrival, params, true, "Ending ride...");
						mWebPageTask1.delegate = (AsyncResponseForZira) DriverModeActivity.this;
						mWebPageTask1.execute();
						}else
						{
							Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
							}
						}*/
						
					}
				});
				alert.setNegativeButton("Cancel", null);
				alert.show();
				
				
		
			}
		});

		driverMode.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				driverModeValue=1;
				if(Util.isNetworkAvailable(DriverModeActivity.this))
				{
				
				try{
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("Riderid",prefs.getString("riderid", null)));
				nameValuePairs.add(new BasicNameValuePair("Longitude", String.valueOf(myLon)));
				nameValuePairs.add(new BasicNameValuePair("Latitude", String.valueOf(myLat)));
				nameValuePairs.add(new BasicNameValuePair("Trigger", "busy"));
				Log.d("tag", "switchBetweenMode :"+nameValuePairs.toString());
				AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(DriverModeActivity.this, switchBetweenMode,nameValuePairs, true,"Please wait...");
				mWebPageTask.delegate = (AsyncResponseForZira) DriverModeActivity.this;
				mWebPageTask.execute();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
		}else
		{
			Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
			}
				mDrawerLayout.closeDrawers();
			}
		});

		sliderIcon.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				v.startAnimation(buttonClick);
				if (mDrawerLayout.isDrawerOpen(flyoutDrawerRl)) {
					mDrawerLayout.closeDrawers();
				} else {
					mDrawerLayout.openDrawer(flyoutDrawerRl);
				}

			}
		});
	
		profileLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
				Intent intent = new Intent(DriverModeActivity.this, GetProfile.class);
				startActivity(intent);
				mDrawerLayout.closeDrawers();
			}
		});
		
		button_getdirection.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
									
				String DestinationAddress=prefs.getString("tripdestinationadd", "").replace(" ", "+");
	
				if(isGoogleMapsInstalled())
				{	
					//Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+tripDetailsModal.getGetTrip_DestnationLatitude()+","+tripDetailsModal.getGetTrip_DestinationLongitude()));
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+prefs.getString("tripdestlat","")+","+prefs.getString("tripdestlong","")));
					startActivity(intent);
					}
				else
				{
					/*url=Uri.parse("http://maps.google.com/maps?daddr="+getGetTrip_StartingAddress+"&x-success=sourceapp://?resume=true&x-source=AirApp");
					}*/
				
				String	url="http://maps.google.com/maps?daddr="+DestinationAddress+"&x-success=sourceapp://?resume=true&x-source=AirApp";
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(url));
				startActivity(intent);
				
				}
			}
		});
	}

	protected void getCurrentTime() {

		Calendar mCalendar=Calendar.getInstance();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		selectedDate=df.format(mCalendar.getTime());
	}

	@Override
	public void onLocationChanged(Location location) {

		Log.e("in", "onlocationChanged");
		
		try {
			if (flag == 0) {
				flag = 1;
				//edit 
				criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);
			/*	if(location==null)
				{
					location = locationManager.getLastKnownLocation(provider);	
					}*/
				//edit 
				
				myLat = location.getLatitude();

				// Getting longitude of the current location
				myLon = location.getLongitude();

				//getAddress(location, "start");
				Log.e("values", "lat : " + myLat + " , lon : " + myLon);

				// Creating a LatLng object for the current location
				LatLng latLng = new LatLng(myLat, myLon);

				// Showing the current location in Google Map
				googleMap.clear();
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));

				// Zoom in the Google Map
				googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
				googleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));

				latitude = myLat;
				longitude =myLon;
				updateDriverLocation();
				
			}

			else
			{
				//googleMap.clear();
				//edit 
				criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);
				if(location==null)
				{
					location = locationManager.getLastKnownLocation(provider);	
					}
				//edit 
				latitude = location.getLatitude();
				longitude = location.getLongitude();
				System.err.println("location update driver");
				LatLng latLng1 = new LatLng(latitude, longitude);
				googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15));
				googleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLon)).icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon)));
				updateDriverLocation();
				}
			///////////////////////
			if(DriverNotifications.tripstart==true)
			{
					if(flagdis==0)
					{
						flagdis=1;
						tripStartLat=latitude;
						tripStartLong=longitude;
						//googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker()));
						}
					else
					{
						tripdestinationLat=latitude;
						tripdestinationLong=longitude;
						}
				getVehicleData();////vehicle fetch web services call
				///////////////////////
				}
			/*else if(active_trip==1)
			{
				
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	

	@Override
	public void onProviderDisabled(String provider) {
	}
	public void onProviderEnabled(String provider) {
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public void getAddress(final Location location, final String trigger) {
		final Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(location.getLatitude());
		coordinates.setLongitude(location.getLongitude());

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Coordinates> coordinateList = locFinder
						.getAddressFromLoc(location.getLatitude(),
								location.getLongitude());
				if (coordinateList.size() > 0) {
					coordinates.setAddress(coordinateList.get(0).getAddress());
				} else {
					coordinates.setAddress("Unknown Rd");
				}

				Message msg = new Message();
				msg.obj = coordinates;

				if (trigger.equals("start"))
					msg.what = 0;
				else
					msg.what = 1;

				handler.sendMessage(msg);
			}
		});
		thread.start();

	}

	private final Handler handler = new Handler() {

		public void handleMessage(Message msg) {

			Coordinates obj = (Coordinates) msg.obj;
			if (msg.what == 0) {
				pickUpCoordinates = obj;

			} else {
				destinationCoordinates = obj;
			}

		}
	};

	@Override
	public void processFinish(String output, String methodName) {


		if (methodName.equals(registerDevice)) {
			Log.e("device register", output);
//			Toast.makeText(DriverModeActivity.this, jsonMessage, 1).show();
		
		}

		else if (methodName.equals("GetLastActiveTripswithStatus")) {
			Log.e("GetLastActiveTripswithStatus", output);
			//SingleTon.getInstance().setMode("rider");
			mTripDetailsModel = parser.getLastActiveDetail(output);
			SingleTon.getInstance().setGetTripDetail(mTripDetailsModel);
			//SingleTon.getInstance().setReceivednotificationTripID(receivednotificationTripID)
			Log.e("mTripDetailsModel", output);
			
			if(mTripDetailsModel.getTrip_status().equalsIgnoreCase("Accepted"))//accept
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
			}
			
			else if(mTripDetailsModel.getTrip_status().equalsIgnoreCase("begintrip"))//
			{
				DriverModeActivity.fromActivtyDriverNotification=1;
				active_trip=1;
				DriverNotifications.tripcancel=false;
				DriverNotifications.tripstart=true;
				endRideButton.setVisibility(View.VISIBLE);
				button_getdirection.setVisibility(View.VISIBLE);
				onResume();
			/*	Intent i=new Intent(DriverModeActivity.this,DriverNotifications.class);
				i.putExtra("begun", "yes");
				startActivity(i);*/
			}
			/*if(mTripDetailsModel.getTrip_status().equals("bugin"))
			{
				Intent i=new Intent(DriverModeActivity.this,DriverNotifications.class);
				i.putExtra("begun", "yes");
				startActivity(i);
			}*/
			
		}
		
		else if (methodName.equals(switchBetweenMode)) {
			Log.e("switchBetweenMode", output);
			//SingleTon.getInstance().setMode("rider");
			
			Intent i=new Intent(DriverModeActivity.this,VehicleSearchActivity.class);
			startActivity(i);
			finish();
		}
		else if (methodName.equals(UpdateDriverLocation)) {
			Log.e("UpdateDriverLocation", output);
//			Toast.makeText(DriverModeActivity.this, jsonMessage, 1).show();
			System.err.println("update location of driver");
		}
		else if (methodName.equals(GetProfile)) {
			//Toast.makeText(DriverModeActivity.this, jsonMessage, 1).show();
			Log.e("GetProfile", output);
			mProfileInfoModel = parser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
		}

		else if (methodName.equals(NotifyRegardingArrival)) {
			Log.e("NotifyRegardingArrival", output);
			try {
				JSONObject 	obj = new JSONObject(output);
				result = obj.getString("result");
				jsonMessage = obj.getString("message");
				Log.e("tag", jsonMessage+result);
				
				if(result.equals("0")){
					DriverNotifications.tripstart=false;//end check of calculation fare
					
					Intent i=new Intent(DriverModeActivity.this,RatingSetScreen.class);
					i.putExtra("newfare", newactualf);
					finish();
					startActivity(i);
				}
				else{
					Util.alertMessage(DriverModeActivity.this, jsonMessage);
				}
				
			} catch (JSONException e) {

				e.printStackTrace();
				}
					
			//	}
			//}
		}
		
/*		try {
			
			if (SingleTon.getInstance().getSentValue().equals("1")) {				
				SingleTon.getInstance().setSentValue("0");
				if(endRide==0 && driverModeValue==0){
					
					Log.e("drivermode", "driver mode activity ");
				//startActivity(new Intent(DriverModeActivity.this,DriverNotifications.class));
				}
				else{
					
				}
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			locationManager.removeUpdates(DriverModeActivity.this);
		} catch (Exception e) {

		}
	}

	@Override
	public void onBackPressed() {

	}

	// placing marker
	public void placeMarker() {
	
		googleMap.addMarker(new MarkerOptions().position(new LatLng(curLatitude, curLongitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

		MarkerOptions destLocationmarker = new MarkerOptions()
		.position(new LatLng(destLatitude, destLongitude));
		// adding marker
		googleMap.addMarker(destLocationmarker);
	}

	/********** Path Draw ***********/
	private String getDirectionsUrl(LatLng origin, LatLng dest) {

		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		// Sensor enabled
		String sensor = "sensor=false";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;

		Log.e("", url);

		return url;
	}

	/** A method to download json data from url */
	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
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
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String> {

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try {
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
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
	private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;

			try {
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e) {
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
			
			try{
			
			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++) {
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();

				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);

				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++) {
					HashMap<String, String> point = path.get(j);

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
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	/****************Actual fare calculations***********************************/
	private String getCurrentDateTime() {		
		Calendar mCalendar=Calendar.getInstance();
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		selectedDate=df.format(mCalendar.getTime());	
		return selectedDate;
	}
	protected void getVehicleData() {	
		if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("longitude",String.valueOf(longitude)));
		nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
		nameValuePairs.add(new BasicNameValuePair("currenttime",getCurrentDateTime()));
		nameValuePairs.add(new BasicNameValuePair("distance","10"));
		nameValuePairs.add(new BasicNameValuePair("VechileType",prefs.getString("preferedvehicletype", "")));
		nameValuePairs.add(new BasicNameValuePair("riderid",prefs.getString("riderid", null)));	

		mAsyncTask=new AsyncTaskForGettingVehicle("hello", nameValuePairs, DriverModeActivity.this);
		mAsyncTask.execute();
		}
		else
		{
			Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
		}
	}

	public class AsyncTaskForGettingVehicle extends AsyncTask<String, Void, String> {

		private Activity activity;		
		private String result = "";			
		private String methodName;		
		private ArrayList<NameValuePair> nameValuePairs;

		public AsyncTaskForGettingVehicle(String methodName,ArrayList<NameValuePair> nameValuePairs,Activity activity) {
			this.activity = activity;
			this.methodName = methodName;
			this.nameValuePairs = nameValuePairs;
		}

		@Override
		protected void onPreExecute() {
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
			
			SingleTon.getInstance().setMainVehicleModel(vehicleModel);
			if(Util.isNetworkAvailable(DriverModeActivity.this))
			{
			new	distanceCalculatorAsyncTask().execute("0");
			}
			else
			{
				Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
			}
		}
	}
	class distanceCalculatorAsyncTask extends AsyncTask<String, String, String>{
		
		String tempStr ="";
		
		protected void onPreExecute() {
			
		}		    

		@Override
		protected String doInBackground(String... str) {
//			Log.i("tag", "Current Lati:"+gettingSourceLat+"  Current Longitude:"+gettingSourceLong+"  Destination_Lati:"+gettingdestinationLat+" Destination Longitude:"+gettingDestinationLong);
			String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+tripStartLat+","+tripStartLong+"&destinations="+tripdestinationLat+","+tripdestinationLong+"&mode=driving&language=en-EN&sensor=false";
			tempStr =  str[0];
			
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
				Log.e("distance","done");

				if(str[0].equals("1")){}
			}catch(Exception e){
				Log.e("error","error");
				e.printStackTrace();
			}

			return "success";
		}


		@Override
		protected void onPostExecute(String result) {
			if(result.equals("success")){	
			GetTripDetails getTripDtails=new GetTripDetails();
			Log.e("vehicle type=",	prefs.getString("vehiceltype", "1").trim());
			//calculateFare(getTripDtails.getGetTrip_VehicleType());
			calculateFare(prefs.getString("vehiceltype", "ZiraE"));
			String actualf1=new DecimalFormat("##.##").format(actualfare);
			newactualf=	actualf1;
				Log.e("newactualf=", ""+actualf1);
			/*	try{
					Float	float_newactualf=	Float.parseFloat(actualf1) + Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getSafetycharges());
					newactualf=new DecimalFormat("##.##").format(float_newactualf);
				}
				catch(Exception e)
				{
					
				}
			*/
				Log.e("newactualf + safety charging=", "" +newactualf);
				
				if(tempStr.equals("1")){
					endRide();
				}
			}
		}
	}
	
	public void endRide(){
		endRide=1;
		
		getCurrentTime();
		if(prefs.getString("activetripid", "").equals(""))
		{
			end_tripid=SingleTon.getInstance().getReceivednotificationTripID();
			
			}
			else
			{
				end_tripid=prefs.getString("activetripid", "");
						
				}
	if(Util.isNetworkAvailable(DriverModeActivity.this))
		{
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("TripId",end_tripid));
		params.add(new BasicNameValuePair("Status", "end"));
		params.add(new BasicNameValuePair("Timestamp", ""));
		params.add(new BasicNameValuePair("Latitude",String.valueOf(myLat)));
		params.add(new BasicNameValuePair("Longitude",String.valueOf(myLon)));
//		params.add(new BasicNameValuePair("TripMilesActual", mTripDetailsModel.getGetTrip_ActaulFare()));

		params.add(new BasicNameValuePair("TripMilesActual", distance));
		params.add(new BasicNameValuePair("TripAmountActual", String.valueOf(newactualf)));
		///////////
		params.add(new BasicNameValuePair("TripTimeActual", ""));
//		params.add(new BasicNameValuePair("TripAmountActual", mTripDetailsModel.getGetTrip_Trip_Total_amount()));				 
		params.add(new BasicNameValuePair("Trigger", ""));
		params.add(new BasicNameValuePair("PaymentStatus", ""));
		params.add(new BasicNameValuePair("CancellationCharges", ""));
		//params.add(new BasicNameValuePair("deviceType", "android"));

		Log.e("tag", "end ride :"+params.toString());
		AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(DriverModeActivity.this, NotifyRegardingArrival, params, true, "Ending ride...");
		mWebPageTask1.delegate = (AsyncResponseForZira) DriverModeActivity.this;
		mWebPageTask1.execute();
		}else
		{
			Util.alertMessage(DriverModeActivity.this, "Please check your internet connection");
			}
	}
	/**Method Calculation fare**/
	
	public void calculateFare(String vehicleType) {

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
					System.err.println("ZiraEEEEEEEEEE=");
					System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getBasePrice()));
					System.err.println("approxMinutes="+approxMinutes);
					System.err.println("totalKmInMiles="+totalKmInMiles);
					System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice()));
					System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice()));
					System.err.println("sirf actualfare="+actualfare);
					
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
					System.err.println("actualfaer+safty="+actualfare);
			}catch(Exception e){
				System.err.println("actual zira e="+e);
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
				//actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				
				System.err.println("ZiraPlus pppppp=");
				System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getBasePrice()));
				System.err.println("approxMinutes="+approxMinutes);
				System.err.println("totalKmInMiles="+totalKmInMiles);
				System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice()));
				System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice()));
				System.err.println("sirf actualfare="+actualfare);
				
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				System.err.println("actualfaer+safty="+actualfare);
				
			}catch(Exception e){
				System.err.println("actual zira plus="+e);
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
				//actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				
				System.err.println("ZiraTaxi ttttt=");
				System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getBasePrice()));
				System.err.println("approxMinutes="+approxMinutes);
				System.err.println("totalKmInMiles="+totalKmInMiles);
				System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice()));
				System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice()));
				System.err.println("sirf actualfare="+actualfare);
				
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				System.err.println("actualfaer+safty="+actualfare);
				
				
			}catch(Exception e){
				System.err.println("actual taxi="+e);
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
				//actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				
				System.err.println("ZiraLux xxxxxxx=");
				System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getBasePrice()));
				System.err.println("approxMinutes="+approxMinutes);
				System.err.println("totalKmInMiles="+totalKmInMiles);
				System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice()));
				System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice()));
				System.err.println("sirf actualfare="+actualfare);
				
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				System.err.println("actualfaer+safty="+actualfare);
				
			}catch(Exception e){
				System.err.println("actual lux="+e);
			}
		}
	}
	
	/****************Driver notification code***********************/
	public void notificationDriver()
	{		
		if(prefs.getString("message", "").equals(""))
		{}
		else
		{
		String riderName = null;
		String sentMessage= null;
		String message=	prefs.getString("message", "");
		if(message.contains("sent")){
			
				prefs = this.getSharedPreferences("Zira", MODE_PRIVATE);
				flag = prefs.getInt("not_flag", 0);
				if(flag == 0){
					Editor editor = prefs.edit();
					editor.putInt("not_flag", 1);
					editor.commit();
					
					DriverModeActivity.forCheking++;
					
					String[] values = message.split("@");
					riderName=values[0];
					sentMessage=values[1];
					String image=values[2];
					String tripID=values[3];
					SingleTon.getInstance().setSentValue(String.valueOf(DriverModeActivity.forCheking));
					SingleTon.getInstance().setReceivednotificationName(riderName);
					SingleTon.getInstance().setReceivednotificationSentMessage(sentMessage);
					SingleTon.getInstance().setReceivednotificationImage(image);
					SingleTon.getInstance().setReceivednotificationTripID(tripID);

					
					Intent launchDialog = new Intent(this.getApplicationContext(), DriverNotifications.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(launchDialog);
				}
			}
		
		Editor e=prefs.edit();
		e.putString("message", "");
		e.commit();
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
	        if (bestLocation == null|| l.getAccuracy() < bestLocation.getAccuracy()) {
	           // ALog.d("found best last known location: %s", l);
	            bestLocation = l;
	        }
	    }
	    if (bestLocation == null) {
	        return null;
	    }
	    return bestLocation;
	} 	
	public boolean isGoogleMapsInstalled()
	{
	    try
	    {
	        ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
	        return true;
	    } 
	    catch(PackageManager.NameNotFoundException e)
	    {
	        return false;
	    }
	}	
}
