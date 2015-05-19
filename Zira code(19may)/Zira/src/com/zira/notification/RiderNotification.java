package com.zira.notification;

import java.text.DecimalFormat;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.twentyfourseven.zira.DriverProfileInformation;
import com.twentyfourseven.zira.LocationFinder;
import com.twentyfourseven.zira.R;
import com.twentyfourseven.zira.RatingSetScreen;
import com.twentyfourseven.zira.RiderRideView;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.modal.Coordinates;
import com.zira.modal.GetTripDetails;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;
import com.zira.util.ZiraParser;

public class RiderNotification extends Activity implements AsyncResponseForZira{
	ImageView imgdriver;
	TextView txtdriverName,txtmessage,setPickUp;
	String drivername="",driverImg="",messages="",tripID="";
	SharedPreferences prefs;
	Button btn_ok;
	RelativeLayout rel,headerLayout,Relativelayout_map;
	TextView txtmsg;
	Button ok;
	int countdownNumber=7;
	ProgressDialog pDialog;
	private TextView textViewWhoSendRequest,textView_text;
	private ImageView imageViewDriverImage;
	private LinearLayout rel_imageandtext;
	private ImageLoader imageLoader;
	String GetDetailsByTripId="GetDetailsByTripId";
	private ZiraParser parser;
	private GetTripDetails mTripDetailsModel;
	GoogleMap googleMap;
	private LocationManager locationManager;
	private String provider;
	private static double myLat=0, myLon=0;
	private Criteria criteria;
	private LocationFinder locFinder;
	String jsonResult1 = null,actualfare="";
	private Coordinates pickUpCoordinates;
	int flag=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rider_notification);
		
	
		initialiseMap();

	
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		initialiseVariable();
		initialiseMap();
		
		getDriverDetailByTripID();
	
		
		rel_imageandtext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(textView_text.getText().toString().contains("arriving")){

					startActivity(new Intent(RiderNotification.this,DriverProfileInformation.class));
				}
				else if(textView_text.getText().toString().contains("has begun the trip")){

					startActivity(new Intent(RiderNotification.this,RiderRideView.class));
					finish();
				}

				else if(textView_text.getText().toString().contains(" has ended the ride")){
					flag=1;
					
					getDriverDetailByTripIDend();
					
//					Intent i=new Intent(RiderNotification.this,RatingSetScreen.class);
//					i.putExtra("newfare", prefs.getString("newfare", ""));
//					startActivity(i);
//					finish();
				}
				else if(textView_text.getText().toString().contains(" has cancelled the ride")){

					finish();
				}
				else if(textView_text.getText().toString().contains("arrived")){
					
				/*	AlertDialog.Builder alert = new AlertDialog.Builder(RiderNotification.this);
					alert.setTitle("Zira24/7");
					alert.setMessage("Do you want to exit?");
					alert.setPositiveButton("yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
							
							finish();
							
						}
					});
					alert.setNegativeButton("no", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
								
								//finish();
							}
					});
					alert.show();*/
//					

				}

			}
		});
	}

	private void initialiseVariable() {
		parser = new ZiraParser();
		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		textViewWhoSendRequest=(TextView)findViewById(R.id.textViewWhoSendRequest);
		imageViewDriverImage=(ImageView)findViewById(R.id.imageViewDriverImage);
		textView_text=(TextView)findViewById(R.id.textView_text);
		rel_imageandtext=(LinearLayout)findViewById(R.id.linear_imageandtext);
		headerLayout=(RelativeLayout)findViewById(R.id.headerLayout);
		Relativelayout_map=(RelativeLayout)findViewById(R.id.Relativelayout_map);
		
		imageLoader = new ImageLoader(RiderNotification.this);
		imageLoader.DisplayImage(SingleTon.getInstance().getDriverImage(),imageViewDriverImage);
		textViewWhoSendRequest.setText(SingleTon.getInstance().getDriverName());
		textView_text.setText(SingleTon.getInstance().getDriverMessage());
		
		if(textView_text.getText().toString().contains("arrived"))
		{
			Relativelayout_map.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
			}
		
		if(textView_text.getText().toString().contains("has begun the trip"))
		{
			Relativelayout_map.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
			}
		if(textView_text.getText().toString().contains(" has ended the ride"))
		{
			Relativelayout_map.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
			
			}
		setPickUp=(TextView)findViewById(R.id.setPickUp);
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		googleMap.setTrafficEnabled(true);
		SingleTon.getInstance().setSentValue("0");
		
	}

	private void getDriverDetailByTripID() {
		Editor ed=prefs.edit();
		ed.putString("tripid",SingleTon.getInstance().getDriverTripId());
		ed.commit();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("TripId",SingleTon.getInstance().getDriverTripId()));
		Log.e("GetDetailsByTripId", nameValuePairs.toString());
		AsyncTaskForZira mFetchStates = new AsyncTaskForZira(RiderNotification.this, GetDetailsByTripId,nameValuePairs, false, "");
		mFetchStates.delegate = (AsyncResponseForZira) RiderNotification.this;
		mFetchStates.execute();		
	}
	private void getDriverDetailByTripIDend() {
		Editor ed=prefs.edit();
		ed.putString("tripid",SingleTon.getInstance().getDriverTripId());
		ed.commit();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("TripId",SingleTon.getInstance().getDriverTripId()));
		Log.e("GetDetailsByTripId", nameValuePairs.toString());
		AsyncTaskForZira mFetchStates = new AsyncTaskForZira(RiderNotification.this, GetDetailsByTripId,nameValuePairs, true, "Please wait");
		mFetchStates.delegate = (AsyncResponseForZira) RiderNotification.this;
		mFetchStates.execute();		
	}

	@Override
	public void processFinish(String output, String methodName) {

	
		JSONObject obj;
		try {
			obj = new JSONObject(output);
			String jsonMessage=obj.getString("message");
			jsonResult1=obj.getString("result");
//			Toast.makeText(RiderNotification.this, jsonMessage, 0).show();
			
			if(methodName.equals(GetDetailsByTripId)){
				if(jsonResult1.equals("0")){
					mTripDetailsModel = parser.parsegetTripDetails(output);
					SingleTon.getInstance().setGetTripDetail(mTripDetailsModel);
					Log.e("mTripDetailsModel", output);
					
					if(flag==1)
					{
						 String newactualf=mTripDetailsModel.getGetTrip_Trip_Total_amount();
						 try{
						
							 actualfare=new DecimalFormat("##.##").format(Float.parseFloat(newactualf));
						 }
						 catch(Exception e)
						 {
							 
						 }
						 System.err.println("newactualf"+actualfare);
						Intent i=new Intent(RiderNotification.this,RatingSetScreen.class);
						i.putExtra("newfare", actualfare);
						startActivity(i);
						finish();
					}
				}
			}
		}catch(JSONException exception){

		}

	
	}
	private void initialiseMap() {
		locFinder = new LocationFinder(RiderNotification.this);
		
		if (googleMap != null) {

			googleMap.setMyLocationEnabled(false);
			googleMap.getUiSettings().setMapToolbarEnabled(false);
		

			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        criteria = new Criteria();
	        provider = locationManager.getBestProvider(criteria, true);
	        Log.i("tag", provider);
	             // Getting Current Location
	        Location location = locationManager.getLastKnownLocation(provider);
	            //on location change method set condition 
	        myLat=location.getLatitude();
	        myLon=location.getLongitude();
	        LatLng latLng = new LatLng(myLat, myLon);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
	       
			pickUpCoordinates = new Coordinates();
			pickUpCoordinates.setLatitude(myLat);
			pickUpCoordinates.setLongitude(myLon);
		//	getAddress(pickUpCoordinates,"asd");
	       
			//googleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLon)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			
		}

		
		
	}
	public void getAddress(final Coordinates location, final String trigger){
		/*final Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(location.latitude);
		coordinates.setLongitude(location.longitude);*/

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				ArrayList<Coordinates> coordinateList = locFinder.getAddressFromLoc(location.latitude, location.longitude);
				if(coordinateList.size()>0){
					location.setAddress(coordinateList.get(0).getAddress());
				}else{
					location.setAddress("Unknown Road");
				}
				setPickUp.setText(pickUpCoordinates.getAddress());
				
			}
		});
		thread.start();

	}
	

	
		
	@Override
	public void onBackPressed() {
		
	}
}
