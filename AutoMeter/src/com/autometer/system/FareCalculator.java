package com.autometer.system;

import java.io.InputStream;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FareCalculator extends Activity implements AnimationListener{	
	
	private double destLat, destLon, curLat, curLong;
	private String destName, destAddress, myAddress, time, distance;
	private int distanceValue, approxMinutes, timeValue;
	private float total_fare, basic_fare, extra_charges = 0;
	private ImageView btnBack;
//	private TextView txtHeader;
	private ProgressDialog progress;
	private TextView lblDestName, lblDestAddress, lblCurAddress, lblDistance, txtFare, btnNext;
	private ImageView btnRefresh;
//	private LocationManager locationManager;
//	private Criteria criteria;
//	private  String provider;
	private LinearLayout lightTraffic, mediumTraffic, heavyTraffic;
	private TextView layoutLightTraffic, lblCurrentLoc, layoutMediumTraffic, layoutHeavyTraffic, lblPrescribedFare, lblTraffic;
//	private int flag =0;
	private SharedPreferences prefs;
	private Animation enter_anim, leave_anim, enter_left_anim, leave_left_anim;
	private int anim_flag = 0;
	private View currentView;
	DecimalFormat twoDForm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fare_calculator);

        twoDForm = new DecimalFormat("#.##");
		prefs = this.getSharedPreferences("PlacePreference", MODE_PRIVATE); 		

		enter_anim = AnimationUtils.loadAnimation(FareCalculator.this, R.anim.animation_enter);		
		enter_anim.setAnimationListener(this);
		leave_anim = AnimationUtils.loadAnimation(FareCalculator.this, R.anim.animation_leave);		
		leave_anim.setAnimationListener(this);
		enter_left_anim = AnimationUtils.loadAnimation(FareCalculator.this, R.anim.animation_opp_leave);		
		enter_left_anim.setAnimationListener(this);
		leave_left_anim = AnimationUtils.loadAnimation(FareCalculator.this, R.anim.animation_opp_enter);		
		leave_left_anim.setAnimationListener(this);
		
//		flag = 1;
//		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
//        criteria = new Criteria();

        // Getting the name of the best provider
//        provider = locationManager.getBestProvider(criteria, true);
        
		Bundle info = getIntent().getExtras();
		
		destLat = info.getDouble("latitude");
		destLon = info.getDouble("longitude");
		destName = info.getString("name");
		destAddress = info.getString("address");
		
//		txtHeader = (TextView)findViewById(R.id.headerText);
		lightTraffic = (LinearLayout)findViewById(R.id.lightTraffic);
		mediumTraffic = (LinearLayout)findViewById(R.id.mediumTraffic);
		heavyTraffic = (LinearLayout)findViewById(R.id.heavyTraffic);
		
		layoutLightTraffic = (TextView)findViewById(R.id.layoutLight);
		layoutMediumTraffic = (TextView)findViewById(R.id.layoutMedium);
		layoutHeavyTraffic = (TextView)findViewById(R.id.layoutHeavy);
		lblPrescribedFare = (TextView)findViewById(R.id.lblFare);
		lblTraffic = (TextView)findViewById(R.id.lblTraffic);
		
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
//		txtHeader.setTypeface(typeFace);
		
		lblPrescribedFare.setTypeface(typeFace);
		lblTraffic.setTypeface(typeFace);
		
//		lightTraffic.setOnClickListener(trafficListener);
//		mediumTraffic.setOnClickListener(trafficListener);
//		heavyTraffic.setOnClickListener(trafficListener);
		
		lightTraffic.setOnTouchListener(touchListener);
		mediumTraffic.setOnTouchListener(touchListener);
		heavyTraffic.setOnTouchListener(touchListener);
		
		/*layoutLightTraffic.setOnTouchListener(swipeListener);
		layoutMediumTraffic.setOnTouchListener(swipeListener);
		layoutHeavyTraffic.setOnTouchListener(swipeListener);
		*/
		
		btnBack = (ImageView)findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		/*myAddress = info.getString("myAddress");*/
		curLat = info.getDouble("curLatitude");
		curLong = info.getDouble("curLongitude");
		
		lblDestName = (TextView)findViewById(R.id.lblDestLoc);
		lblDestAddress = (TextView)findViewById(R.id.lblDestAddress);
		lblCurrentLoc = (TextView)findViewById(R.id.lblCurLoc);
		lblCurAddress = (TextView)findViewById(R.id.lblCurAddress);
		lblDistance = (TextView)findViewById(R.id.lblDistance);
		btnRefresh = (ImageView)findViewById(R.id.btnRefresh);
		txtFare = (TextView)findViewById(R.id.txtFare);
		btnNext = (TextView)findViewById(R.id.footer);
		
		lblCurrentLoc.setTypeface(typeFace, Typeface.BOLD);
		lblDestName.setTypeface(typeFace, Typeface.BOLD);
		
		lblCurAddress.setTypeface(typeFace);
		lblDestAddress.setTypeface(typeFace);
		
		layoutLightTraffic.setTypeface(typeFace, Typeface.BOLD);
		layoutMediumTraffic.setTypeface(typeFace, Typeface.BOLD);
		layoutHeavyTraffic.setTypeface(typeFace, Typeface.BOLD);
		
//		btnNext.setTypeface(typeFace);
		
		lblDestName.setText(destName);
		lblDestAddress.setText(destAddress);
		lblCurAddress.setText(myAddress);
		
		btnRefresh.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				refreshLocation();	
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(curLat>0 && curLong>0){
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
							Uri.parse("http://maps.google.com/maps?saddr="+curLat+","+curLong+"&daddr="+destLat+","+destLon));
					intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
				    startActivity(intent);
				}else{
					AlertDialog.Builder alert = new AlertDialog.Builder(FareCalculator.this);
					alert.setTitle("AutoMeter");
					alert.setMessage("Current Location not available. Please check your GPS settings.");
					alert.setPositiveButton("Ok", null);
					alert.show();
				}
			}
		});
		
		/*Location loc = new Location("My Location");
		loc.setLatitude(ChooseDestination.myLat);
		loc.setLongitude(ChooseDestination.myLon);
		onLocationChanged(loc);*/
	}
	
	/*private OnTouchListener swipeListener = new OnSwipeTouchListener(){
		@Override
	    public boolean onTouch(final View v, final MotionEvent event) {
//			Log.e("swipe","touch");
			currentView = v;
			return super.onTouch(v, event);
		}
		 
		public void onSwipeRight() {
			if(currentView==lightTraffic || currentView==layoutLightTraffic){
				if(anim_flag==0){
					if(layoutLightTraffic.getVisibility()==View.VISIBLE){
						layoutMediumTraffic.startAnimation(enter_anim);			
						layoutLightTraffic.startAnimation(leave_anim);
						layoutLightTraffic.setVisibility(View.INVISIBLE);
						layoutMediumTraffic.setVisibility(View.VISIBLE);
						layoutHeavyTraffic.setVisibility(View.INVISIBLE);
						calculateExtraFare(TrafficConditions.MEDIUM);
					}
				}
			}else if(currentView==mediumTraffic || currentView == layoutMediumTraffic){
				if(anim_flag==0){
					if(layoutMediumTraffic.getVisibility()==View.VISIBLE){
						layoutMediumTraffic.startAnimation(leave_anim);		
						layoutHeavyTraffic.startAnimation(enter_anim);		
						layoutLightTraffic.setVisibility(View.INVISIBLE);
						layoutMediumTraffic.setVisibility(View.INVISIBLE);
						layoutHeavyTraffic.setVisibility(View.VISIBLE);
						calculateExtraFare(TrafficConditions.HEAVY);
					}
				}
			}
		}
		
		public void onSwipeLeft() {
			if(currentView==mediumTraffic || currentView == layoutMediumTraffic){
				if(anim_flag==0){
					if(layoutMediumTraffic.getVisibility()==View.VISIBLE){
						layoutMediumTraffic.startAnimation(leave_left_anim);		
						layoutLightTraffic.startAnimation(enter_left_anim);		
						layoutLightTraffic.setVisibility(View.VISIBLE);
						layoutMediumTraffic.setVisibility(View.INVISIBLE);
						layoutHeavyTraffic.setVisibility(View.INVISIBLE);
						calculateExtraFare(TrafficConditions.LIGHT);
					}
				}
			}else if(currentView==heavyTraffic || currentView == layoutHeavyTraffic){
				if(anim_flag ==0){
					if(layoutHeavyTraffic.getVisibility()==View.VISIBLE){
						layoutHeavyTraffic.startAnimation(leave_left_anim);		
						layoutMediumTraffic.startAnimation(enter_left_anim);	
						layoutLightTraffic.setVisibility(View.INVISIBLE);
						layoutMediumTraffic.setVisibility(View.VISIBLE);
						layoutHeavyTraffic.setVisibility(View.INVISIBLE);
						calculateExtraFare(TrafficConditions.MEDIUM);
					}
				}
			}
		}
	};*/
	
	private OnTouchListener touchListener = new View.OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {

			if(v==lightTraffic){
				layoutLightTraffic.setTextColor(Color.WHITE);
				layoutLightTraffic.setBackgroundResource(R.drawable.black);
				layoutMediumTraffic.setTextColor(Color.BLACK);
				layoutMediumTraffic.setBackgroundResource(R.drawable.white);
				layoutHeavyTraffic.setTextColor(Color.BLACK);
				layoutHeavyTraffic.setBackgroundResource(R.drawable.white);
				
				calculateExtraFare(TrafficConditions.LIGHT);
			}else if(v==mediumTraffic){
				layoutLightTraffic.setTextColor(Color.BLACK);
				layoutLightTraffic.setBackgroundResource(R.drawable.white);
				layoutMediumTraffic.setTextColor(Color.WHITE);
				layoutMediumTraffic.setBackgroundResource(R.drawable.black);
				layoutHeavyTraffic.setTextColor(Color.BLACK);
				layoutHeavyTraffic.setBackgroundResource(R.drawable.white);
				
				calculateExtraFare(TrafficConditions.MEDIUM);
			}else if(v==heavyTraffic){
				layoutLightTraffic.setTextColor(Color.BLACK);
				layoutLightTraffic.setBackgroundResource(R.drawable.white);
				layoutMediumTraffic.setTextColor(Color.BLACK);
				layoutMediumTraffic.setBackgroundResource(R.drawable.white);
				layoutHeavyTraffic.setTextColor(Color.WHITE);
				layoutHeavyTraffic.setBackgroundResource(R.drawable.black);
				
				calculateExtraFare(TrafficConditions.HEAVY);
			}
			
			return false;
		}
	};
	

	@Override
	public void onResume(){
		super.onResume();
		String trigger = prefs.getString("trigger","");
		Location loc = new Location("My Location");
		
		Editor editor = prefs.edit();
		
		if(trigger.equals("none")){		
			editor.remove("trigger");
			editor.commit();
			
			loc.setLatitude(ChooseDestination.myLat);
			loc.setLongitude(ChooseDestination.myLon);

			onLocationChanged(loc);
		}else if(trigger.equals("customLoc")){
			lblCurrentLoc.setText(prefs.getString("markerTitle", "Current Location"));
			editor.remove("trigger");
			editor.commit();
			
			loc.setLatitude(Double.valueOf(prefs.getString("markerLat", "0.0")));
			loc.setLongitude(Double.valueOf(prefs.getString("markerLon", "0.0")));

			onLocationChanged(loc);
		}
		
//		}
	}
	/*
	private View.OnClickListener trafficListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v==lightTraffic){
				if(anim_flag==0){
					layoutLightTraffic.startAnimation(enter_anim);						
					layoutLightTraffic.setVisibility(View.VISIBLE);
					layoutMediumTraffic.setVisibility(View.INVISIBLE);
					layoutHeavyTraffic.setVisibility(View.INVISIBLE);
				}
			}else if(v==mediumTraffic){
				if(anim_flag==0){
					layoutLightTraffic.startAnimation(leave_anim);		
					layoutMediumTraffic.startAnimation(enter_anim);		
					layoutLightTraffic.setVisibility(View.INVISIBLE);
					layoutMediumTraffic.setVisibility(View.VISIBLE);
					layoutHeavyTraffic.setVisibility(View.INVISIBLE);
				}
			}else if(v==heavyTraffic){
				if(anim_flag ==0){
					layoutMediumTraffic.startAnimation(leave_anim);		
					layoutHeavyTraffic.startAnimation(enter_anim);	
					layoutLightTraffic.setVisibility(View.INVISIBLE);
					layoutMediumTraffic.setVisibility(View.INVISIBLE);
					layoutHeavyTraffic.setVisibility(View.VISIBLE);
				}
			}
		}
	};*/
	
	public void calculateExtraFare(TrafficConditions trafficCondition){
		if(trafficCondition.equals(TrafficConditions.LIGHT)){
			extra_charges = 0;
		}else if(trafficCondition.equals(TrafficConditions.MEDIUM)){
			if(approxMinutes<5){
				extra_charges = (float) ((3.5 * 2)/5);
			}else if(approxMinutes>=5 && approxMinutes<=10){
				extra_charges = (float) ((3.5 * 5)/5);
			}else{
				extra_charges = (float) ((3.5 * 8)/5);
			}
		}else if(trafficCondition.equals(TrafficConditions.HEAVY)){
			if(approxMinutes<5){
				extra_charges = (float) ((3.5 * 3)/5);
			}else if(approxMinutes>=5 && approxMinutes<=10){
				extra_charges = (float) ((3.5 * 8)/5);
			}else{
				extra_charges = (float) ((3.5 * 12)/5);
			}
		}
		total_fare = Float.valueOf(twoDForm.format(basic_fare + extra_charges));
	    txtFare.setText(String.valueOf(total_fare));
	}
	public void refreshLocation(){
//        locationManager.requestLocationUpdates(provider, 500000, 0, this);
//		
        // Getting Current Location
       /* Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }*/
		
		/*Location loc = new Location("My Location");
		loc.setLatitude(ChooseDestination.myLat);
		loc.setLongitude(ChooseDestination.myLon);
		onLocationChanged(loc);*/
		
		final Dialog alert = new Dialog(FareCalculator.this);
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(R.layout.custom_alert);
		
		Button btnContinue = (Button)alert.findViewById(R.id.btnContinue);
		Button useExisting = (Button)alert.findViewById(R.id.useExisting);
		
		useExisting.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				alert.dismiss();			
				lblCurrentLoc.setText("Current Location");
				Location loc = new Location("My Location");
				loc.setLatitude(ChooseDestination.myLat);
				loc.setLongitude(ChooseDestination.myLon);
				onLocationChanged(loc);
			}
		});
		
		btnContinue.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				alert.dismiss();
				
				final Dialog destinationAlert = new Dialog(FareCalculator.this);
				destinationAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				destinationAlert.setContentView(R.layout.destination_alert);
				
				final EditText txtSearch = (EditText)destinationAlert.findViewById(R.id.txtLocation);
				
				Button search = (Button)destinationAlert.findViewById(R.id.btnSearch);
				
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			    lp.copyFrom(destinationAlert.getWindow().getAttributes());
			    lp.width = WindowManager.LayoutParams.FILL_PARENT;
			    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
			    
			    search.setOnClickListener(new View.OnClickListener() {					
					@Override
					public void onClick(View v) {
						
						if(txtSearch.getText().toString().trim().equals("")){
							txtSearch.setText("");
							txtSearch.setHintTextColor(Color.RED);
						}else{
							destinationAlert.dismiss();
							
							Intent intent = new Intent(FareCalculator.this, ChooseDestination.class);
							intent.putExtra("trigger","calc");
							intent.putExtra("destination", txtSearch.getText().toString().trim());
							startActivity(intent);
						}
					}
				});
			    
			    txtSearch.setOnEditorActionListener(new EditText.OnEditorActionListener() {        	
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							if(txtSearch.getText().toString().trim().equals("")){
								txtSearch.setText("");
								txtSearch.setHintTextColor(Color.RED);
							}else{
								destinationAlert.dismiss();
								
								Intent intent = new Intent(FareCalculator.this, ChooseDestination.class);
								intent.putExtra("trigger","calc");
								intent.putExtra("destination", txtSearch.getText().toString().trim());
								startActivity(intent);
							}
							
							return true;
				        }
						return false;
					} });
			    
			    destinationAlert.show();
			    destinationAlert.getWindow().setAttributes(lp);			    				
			}
		});
		
		alert.show();
	}
	/*public void calculateDistance(){			
//		LatLng loc1 = new LatLng(curLat, curLong);
//		LatLng loc2 = new LatLng(destLat, destLon);
		
		
		
		Location locationA = new Location("point A");
		locationA.setLatitude(curLat );
		locationA.setLongitude(curLong);
	
		Location locationB = new Location("point B");	
		locationB.setLatitude(destLat);
		locationB.setLongitude(destLon);
	
		float distance     = locationA.distanceTo(locationB)/1000;
		
		lblDistance.setText(String.valueOf(distance));
	}*/
	
	class distanceCalculator extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... str) {
	    	String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+curLat+","+curLong+"&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";

//	    	String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=30.711104,76.6861538&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";
//	    	Log.e("url", url);
			
	    	try{
		    	HttpGet httpGet = new HttpGet(url);
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
			        	distance = elementArray.getJSONObject(j).getJSONObject("distance")
			                    .getString("text");
			        	distanceValue = elementArray.getJSONObject(j).getJSONObject("distance")
			                    .getInt("value");
			        	
			        	float totalKms = (float)(distanceValue*0.001);
			        	Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);
			        	if(totalKms>2){
			        		basic_fare = ((totalKms-2)*12) + 25;
			        	}else
			        		basic_fare = 25;
			        	
			            basic_fare = Float.valueOf(twoDForm.format(basic_fare));
			            
//			        	fare = ((distanceValue/1000))
			        	time    = elementArray.getJSONObject(j).getJSONObject("duration")
			                    .getString("text");
			        	timeValue = elementArray.getJSONObject(j).getJSONObject("duration")
			                    .getInt("value");
			        	
			        	float temp= (float)timeValue/60;
			        	
			        	approxMinutes = Math.round(temp);
			        	
			        	Log.e("result","Option "+" :: "+distance+" | "+approxMinutes+",, "+temp);
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
	    protected void onPreExecute() {
	        progress = ProgressDialog.show(FareCalculator.this, "Loading", "Please wait...");
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        progress.dismiss();
	        lblDistance.setText(distance+" | "+time);
	        lblCurAddress.setText(myAddress);  
            total_fare = Float.valueOf(twoDForm.format(basic_fare+extra_charges));
	        txtFare.setText(String.valueOf(total_fare));
	        
//	        locationManager.removeUpdates(FareCalculator.this);
	    }
	}

//	@Override
	public void onLocationChanged(Location location) {		
		curLat = location.getLatitude();
		curLong = location.getLongitude();
		
		new distanceCalculator().execute();
	}

@Override
public void onAnimationEnd(Animation animation) {
	anim_flag = 0;
}

@Override
public void onAnimationRepeat(Animation animation) {
	// TODO Auto-generated method stub
	
}

@Override
public void onAnimationStart(Animation animation) {
	anim_flag = 1;
}
}