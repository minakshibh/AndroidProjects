package com.rapidride;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.R.integer;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class RideInfoView_Activity extends Activity implements LocationListener {
	
	
	Button btn_starttrip,btn_endtrip,btn_arrive,btn_call,btn_txt,btn_cancel;
	TextView tv_ridername,tv_destination,tv_pickup,tv_eta,tv_suggFare,tv_time,tv_rating,tv_reqesttype;
	ImageView iv_riderimage,iv_menu,iv_gps;
	LinearLayout menulayout;
	ProgressDialog   progress,progress1,progress2,pDialog,pDialog1;
	LocationManager  locationManager ;
	Location location;
	GoogleMap googleMap;
	

	LatLng origin ,dest;
	Typeface typeFace;
	SharedPreferences prefs;

	Timer timer,timerRider,timeractual;
	TimerTask timerTask,timerTaskRider,timertaskactual;
	
	final Handler handler = new Handler();
	final Handler handlerRider = new Handler();
	final Handler handlerActual = new Handler();
	
	ArrayList<LatLng> points;
	
	String provider,value="",getslongitude="",getslatitude="";
	String	end_jsonResult="",start_jsonResult="",cancel_jsonResult="",arrive_jsonResult="";
	String end_jsonMessage="",start_jsonMessage="",cancel_jsonMessage="",arrive_jsonMessage="";
	String str_cancelride,str_red="",time,distance,selectedVehicleType="";
	String str_imageurl="", tripid="",str_value=null;
	public int timeValue,distanceValue,check=0,intvalueActualFare=0,approxMinutes;
	private double destLat, destLon, curLong,curLat,latitude,longitude;
	int flag=0; int int_min=0;
	float actualfare, totalKms =00, des_mile=0,basic_fare,flt_suggestionfare,flt_eta;

	
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rideinfoview);
		
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
				
  
	  	googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.trip_map)).getMap();
	    googleMap.getUiSettings().setMapToolbarEnabled(false);
		tv_ridername=(TextView)findViewById(R.id.textView_drivername);
		tv_destination=(TextView)findViewById(R.id.textView_destination);
		tv_pickup=(TextView)findViewById(R.id.textView_pkdate);
		tv_time=(TextView)findViewById(R.id.textView_pickuptime);
		tv_eta=(TextView)findViewById(R.id.textView_eta);
		tv_suggFare=(TextView)findViewById(R.id.textView_fare);
		tv_rating=(TextView)findViewById(R.id.textView_rating);
		iv_gps=(ImageView)findViewById(R.id.imageView_gps);
		//tv_reqesttype=(TextView)findViewById(R.id.textView_requesttype);
		
		iv_riderimage=(ImageView)findViewById(R.id.imageView_riderpic);
		iv_menu=(ImageView)findViewById(R.id.imageView_menu);
		
		menulayout=(LinearLayout)findViewById(R.id.llayout);
//		btn_test=(Button)findViewById(R.id.button_test);
		btn_starttrip=(Button)findViewById(R.id.button_trip_start);
		btn_endtrip=(Button)findViewById(R.id.button_trip_end);
		btn_arrive=(Button)findViewById(R.id.button_trip_arrive);
		btn_call=(Button)findViewById(R.id.btncall);
		btn_txt=(Button)findViewById(R.id.btntxt);
		btn_cancel=(Button)findViewById(R.id.btncancel);
		tripid=getIntent().getStringExtra("tripid");
		
				
		if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
	    	{
				new httpUpdateDriverLocation().execute(); /**update driver location**/
	    		}
	    else{
	    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
	    	}
		
		points=new ArrayList<LatLng>();
		points.clear();

		tv_suggFare.setText("FARE: $"+getIntent().getStringExtra("suggestionfare"));
		tv_eta.setText(" "+getIntent().getStringExtra("eta").replace("mins", "")+"mins");
			
		String dateString=getIntent().getStringExtra("pickup");
		
		  try 
		    {
		    	DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		    	Date date1 = (Date)formatter.parse(dateString);
		    	formatter = new SimpleDateFormat("yy/MM/dd");
		    	  String  s = formatter.format(date1);
		    	//  System.err.println(s);
		    	  tv_pickup.setText(s);
			      formatter = new SimpleDateFormat("HH:mm");
			      String  t = formatter.format(date1);
			      tv_time.setText(t);
			     // System.err.println(t);
		    }
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    	}
		
		
		  
		  /***********Set rider and driver validation***************/
		  
		  
		 value=getIntent().getStringExtra("value");
		 if((value==null)) {
		 }
		 else{
			  if(value.equals("rider"))
			  {
				 Log.d("tag","Rider");
				 str_value="start";
				 stoptimertaskRider();
				 startTimerRider();
				 btn_cancel.setVisibility(View.GONE);
				 btn_arrive.setVisibility(View.GONE);
				 btn_starttrip.setVisibility(View.GONE);
				 btn_endtrip.setVisibility(View.GONE);
				 iv_gps.setVisibility(View.GONE);
				 tv_ridername.setText(getIntent().getStringExtra("drivername"));
				 tv_rating.setText("Rating :"+getIntent().getStringExtra("driverrating"));
				 tv_destination.setText(getIntent().getStringExtra("destination"));
				 
				 str_imageurl=getIntent().getStringExtra("driverimage");
					
					if(!(str_imageurl==null)){
						if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
				    	{
							
							new GetXMLTask().execute(new String[] { str_imageurl });	/**call get image class */
				    		}
				    	else{
				    			Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
				    			}
						
						}
				
			  }
			  else if(value.equals("driver"))
			  {
				 Log.d("tag","Driver");
				 btn_starttrip.setVisibility(View.GONE);
				 btn_endtrip.setVisibility(View.GONE);
				 btn_arrive.setVisibility(View.VISIBLE);
				 iv_gps.setVisibility(View.VISIBLE);
				  
				 stoptimertask();
				 startTimer();
				  
			/////////////////////////////
				  stoptimerActual();  
				  startActual();
			////////////////////////////////
				
				Log.e("driver", "driver");
				 //btn_endtrip.setBackgroundColor(R.color.transparent);
				 //btn_starttrip.setBackgroundColor(R.color.transparent);
				 //btn_arrive.setBackgroundColor(R.color.yellow);
				 
				
				 tv_ridername.setText(getIntent().getStringExtra("ridername"));
				 tv_rating.setText("Rating :"+getIntent().getStringExtra("riderrating"));
				 tv_destination.setText(getIntent().getStringExtra("start"));
				  
				  str_imageurl=getIntent().getStringExtra("riderimage");
					
					if(!(str_imageurl==null)){
						if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
				    	{
							
							new GetXMLTask().execute(new String[] { str_imageurl });	/**call get image class */
				    		}
				    	else{
				    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
				    	}
						
						}
///////////////////////////////////////////////////////		
/*				Log.e("tag","origin="+origin);
				Log.e("tag","dest="+dest);
				String url = getDirectionsUrl(origin, dest);
				if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
					
					new DownloadTask().execute(url);	*//**call get image class *//*
		    		}
		    	else{
		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
		    	}*/
				
///////////////////////////////////////////////////////////		
			
			  }
		  
		 
		 }
		  
		 iv_gps.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
//				   latitude = location.getLatitude();
//			        longitude = location.getLongitude();
//			      // System.err.println("ride info view change location");
//					str_red="red";
//			      //  System.err.println("driver map call 20 seconds");
//			        LatLng latLng = new LatLng(latitude, longitude);
//			     googleMap.clear();
//				googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
				if(str_value!=null)
				{
					//  Double tempLat=Double.parseDouble(getIntent().getStringExtra("endlati"));
			    	//  Double temoLon=Double.parseDouble(getIntent().getStringExtra("endlong"));
					Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
					Uri.parse("http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+getIntent().getStringExtra("endlati")+","+getIntent().getStringExtra("endlong")));
					startActivity(intent);
					System.err.println("iffffffff");
					}
				else
					{
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
						 Uri.parse("http://maps.google.com/maps?saddr="+latitude+","+longitude+"&daddr="+getIntent().getStringExtra("startlat")+","+getIntent().getStringExtra("startlong")));
						startActivity(intent);	
						System.err.println("elseeeeeeeee");
					}
			}
		 });
		 
		 try
		 {
			float tempslat= Float.parseFloat(getIntent().getStringExtra("startlat"));
			float tempslong= Float.parseFloat(getIntent().getStringExtra("startlong"));
			float tempdlati= Float.parseFloat(getIntent().getStringExtra("endlati"));
			float tempdlong= Float.parseFloat(getIntent().getStringExtra("endlong"));
			origin = new LatLng(tempslat,tempslong);
			googleMap.addMarker(new MarkerOptions().position(origin).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
			dest =new LatLng(tempdlati,tempdlong);
			googleMap.addMarker(new MarkerOptions().position(dest));
		 }
		 catch(Exception e)
		 {
			 
		 }
		 
		
     
	       
	       
		  /***********Set date and time validation***************/
		  String requesttype=getIntent().getStringExtra("requesttype");
			if(!(requesttype==null))
			{	
				requesttype.replace(" ", "");
				//System.err.println("if in");
				if(requesttype.equals("Future"))
				{
					//System.err.println("if if in");
					tv_pickup.setVisibility(View.VISIBLE);
					tv_time.setVisibility(View.GONE);	
					}
				else
				{
					///System.err.println("if else in");
					tv_time.setVisibility(View.VISIBLE);
					tv_pickup.setVisibility(View.GONE);
					}
				}
	
		
	
		/**Arrive button gone in rider view*/
		
//		if(!(getIntent().getStringExtra("mode")==null))
//		{
//			btn_arrive.setVisibility(View.GONE);
//		//	btn_starttrip.setVisibility(View.VISIBLE);	
//			btn_cancel.setVisibility(View.GONE);
//			}
//		else
//		{
//			btn_starttrip.setVisibility(View.VISIBLE);	
//			}
//		
//		btn_test.setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				stoptimertask();
//				Intent i=new Intent(RideInfoView_Activity.this,DriverRating_Activity.class);
//				i.putExtra("riderid", getIntent().getStringExtra("riderid"));
//				 i.putExtra("fare", getIntent().getStringExtra("suggestionfare"));
//				 i.putExtra("tripid", getIntent().getStringExtra("tripid"));
//				i.putExtra("value", "rider");
//				startActivity(i);
//				
//			}
//		});
		
		iv_menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if(menulayout.getVisibility()==View.GONE)
				{
					//System.err.println("if");
					menulayout.setVisibility(View.VISIBLE);
				}
				else if(menulayout.getVisibility()==View.VISIBLE)
				{
					//System.err.println("else");
					menulayout.setVisibility(View.GONE);
				}
				
			}
		});
		btn_arrive.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			
				if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
					 if(menulayout.getVisibility()==View.VISIBLE)
					{	menulayout.setVisibility(View.GONE);
							}
					new httpArrivetRide().execute();	/**call Arrive ride class */
		    		}
		    	else{
		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
		    	}
			}
		});
		btn_starttrip.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				str_value="start";
				tv_destination.setText(getIntent().getStringExtra("destination"));
						
				if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
					if(menulayout.getVisibility()==View.VISIBLE)
					{	menulayout.setVisibility(View.GONE);
							}	
					new httpStartRide().execute(); 	/**call start ride class */
		    		}
		    	else{
		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
		    	}
			
			}
		});
	
		
		btn_endtrip.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
			
				if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
					stoptimertask();
					stoptimerActual();
					stoptimertaskRider();
					if(menulayout.getVisibility()==View.VISIBLE)
					{	menulayout.setVisibility(View.GONE);
							}
					
					AlertDialog.Builder alert = new AlertDialog.Builder(RideInfoView_Activity.this);
					alert.setCancelable(true);
					alert.setTitle("Rapid");
					alert.setMessage("Are you sure to end a ride ?");
					
					alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
		                public void onClick(DialogInterface dialog, int id) {
		                    dialog.cancel();
		                }
		            });
				
					alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			               
							public void onClick(DialogInterface dialog, int id) {
			                    dialog.cancel();
			                //    new httpEndRide().execute();	/**call End ride class */
			                    stoptimertaskRider();
								 stoptimerActual();
								 stoptimertask();
								Intent i=new Intent(RideInfoView_Activity.this,DriverRating_Activity.class);
								i.putExtra("riderid", getIntent().getStringExtra("riderid"));
								i.putExtra("fare", getIntent().getStringExtra("suggestionfare"));
								i.putExtra("tripid", getIntent().getStringExtra("tripid"));
								i.putExtra("vehicletype", getIntent().getStringExtra("vehicletype")); 
								String str_tempeta=getIntent().getStringExtra("eta");
								String  str_tempdistance=getIntent().getStringExtra("distance");
								try
									{	//2hours 20mins
									    int int_tempdistance=Integer.parseInt(str_tempdistance);
									   							
										   if(str_tempeta.contains("hour"))
										    {
										    	   try{
											   		String[] values = str_tempeta.split(" ");
													String hour=values[0].split("hours")[0];
													String min=values[1].split("mins")[0];
													System.err.println("hour="+hour+"min="+min);
													int int_hourmin=60*Integer.parseInt(hour);
													int_min=Integer.parseInt(min)+int_hourmin;
													
											    	}
										    
										    	   catch(Exception e){
																	 }
										    	}
										    else if(str_tempeta.contains("mins"))
										    {
										    	str_tempeta.replace("mins", "");
										    	
										    	try{
										    		int_min=Integer.parseInt(str_tempeta);
											    	}
											    
										    	catch(Exception e){
												 
											 }}
								
									  //  int	int_eta=Integer.parseInt(str_tempeta);
										int newvalue=approxMinutes-int_min;
										int newdis=distanceValue-int_tempdistance;
										Log.e("tag", newvalue+"=newvalue");
										Log.e("tag",newdis+"=newdis" );
										Log.e("tag", approxMinutes+"=approxMinutes");
										Log.e("tag", int_min+"int_eta");
										Log.e("tag", int_tempdistance+"int_suggestionfare");
										if(newvalue>=7)
										{
											i.putExtra("again","yes");
											i.putExtra("newdistance", distanceValue);
											i.putExtra("newmin", approxMinutes);
											}
										else if(newdis>=1)
										{
											i.putExtra("again","yes");
											i.putExtra("newdistance", distanceValue);
											i.putExtra("newmin", approxMinutes);
											}

										}
									catch(Exception e){
										
									}
								
									 if(!(getIntent().getStringExtra("value")==null)) {
										  if(getIntent().getStringExtra("value").equals("rider"))
										  {
											  i.putExtra("value", "rider"); 
										  	}
										  else if(getIntent().getStringExtra("value").equals("driver"))
										  {
											  i.putExtra("value", "driver");  
										  	}
										 }
									 finish();	
									 startActivity(i);	
			    			 
			                }
			            });
					AlertDialog alert11 = alert.create();
					alert11.show();
					
					
		    		}
		    	else{
		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
		    	}
			}
		});
	
		
		btn_call.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
							
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:619-600-5080"));/**call mobile number**/
			startActivity(callIntent);
			menulayout.setVisibility(View.GONE);
		
				
			}
		});
		btn_txt.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				
				Intent smsIntent = new Intent(Intent.ACTION_VIEW);
				smsIntent.setType("vnd.android-dir/mms-sms");
				smsIntent.putExtra("address", "619-600-5080");/**sms mobile number**/
				smsIntent.putExtra("sms_body","");
				startActivity(smsIntent);
				menulayout.setVisibility(View.GONE);
				
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
						
//				if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
//		    	{
//					stoptimertask();
//					new httpCancelRide().execute();	/**call cancel ride class */
//		    		}
//		    	else{
//		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
//		    	}
						 
				menulayout.setVisibility(View.GONE);
				final Dialog dialog = new Dialog(RideInfoView_Activity.this);
	                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	                dialog.setContentView(R.layout.cancel_ride_layout);
	                dialog.setCancelable(false);
	                //dialog.setTitle("Cancel Ride");
						Button Ridercancelleddonotcharge=(Button)dialog.findViewById(R.id.button_Ridercancelleddonotcharge);
						Ridercancelleddonotcharge.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
								if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
						   	    	{
						   				/**call http cancel class**/
									str_cancelride="rider cancelled donot charge";
										new httpCancelRide().execute();
						   	    		}
						   	    	else{
						   	    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
						   	    	}
							  	}
							
						});
						Button Sendtoanotherdriver=(Button)dialog.findViewById(R.id.button_Sendtoanotherdriver);
						Sendtoanotherdriver.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
								if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
					   	    	{
					   				/**call cancel class**/
									str_cancelride="send to another driver";
									new httpCancelRide().execute();
					   	    		}
					   	    	else{
					   	    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
					   	    	}
							}
						});
						Button Cancelride=(Button)dialog.findViewById(R.id.button_Cancelride);
						Cancelride.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();	
							
								if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
					   	    	{
					   				/**call cancel class**/
									str_cancelride="cancel ride";
									new httpCancelRide().execute();
					   	    		}
					   	    	else{
					   	    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
					   	    	}
							}
						});
						
						Button Cancel=(Button)dialog.findViewById(R.id.button_dcancel);
						Cancel.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
							
								
							dialog.dismiss();	
							}
						});

			        dialog.show();
			
				
			}
		});
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
           int requestCode = 10;
           Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
           dialog.show();

        }
        else { /** Google Play Services are available */

   		try{		
   			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
   				boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
   			    boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
   		
   			    // Check if enabled and if not send user to the GSP settings
   			    if (!enabledGPS && !enabledWiFi) {
   			      //  Toast.makeText(TripMapView_Activity.this, "GPS signal not found", Toast.LENGTH_LONG).show();
   			        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
   			        startActivity(intent);
   			    }
   		   			
       
     
       	googleMap.setTrafficEnabled(true);
       	googleMap.setMyLocationEnabled(true);
      
       	
           Criteria criteria = new Criteria();
           provider = locationManager.getBestProvider(criteria, true);
           location = locationManager.getLastKnownLocation(provider);
          
           if(location!=null){
              	onLocationChanged(location);
           		}
//           locationManager.requestLocationUpdates(provider, 2000, 0,  this);
		     }
       catch(Exception e){
			e.printStackTrace();
		}
   		}
}
	
	

	
	
	
	
	
	
	/******************************end main function*****************/
	

	public void onLocationChanged(Location location) {
		try{
	         /**Getting latitude and longitude of the current location */
		//	googleMap.clear();
			if(value.equals("rider"))
			{
			  try{
			        double double_lat=Double.valueOf(getslatitude);
			        latitude = double_lat;
			        double double_long=Double.valueOf(getslongitude);
			        longitude=double_long;
			       
			       }
		       catch(Exception e)
		       {	 }
		     
//			  		LatLng latLng = new LatLng(latitude, longitude);
//			      // Instantiating the class PolylineOptions to plot polyline in the map
//		            PolylineOptions polylineOptions = new PolylineOptions();
//		           // Setting the color of the polyline
//		            polylineOptions.color(Color.BLUE);
//		            // Setting the width of the polyline
//		            polylineOptions.width(10);
//		           // Adding the taped point to the ArrayList
//		            points.add(latLng);
//		           // Setting points of polyline
//		            polylineOptions.addAll(points);
//		           // Adding the polyline to the map
//		            googleMap.addPolyline(polylineOptions);
		         
			      }
			
			
			else
			{
		//	googleMap.clear();
	        latitude = location.getLatitude();
	        longitude = location.getLongitude();
	        
//	        LatLng latLng = new LatLng(latitude, longitude);
//		      // Instantiating the class PolylineOptions to plot polyline in the map
//	            PolylineOptions polylineOptions = new PolylineOptions();
//	           // Setting the color of the polyline
//	            polylineOptions.color(Color.BLUE);
//	            // Setting the width of the polyline
//	            polylineOptions.width(10);
//	           // Adding the taped point to the ArrayList
//	            points.add(latLng);
//	           // Setting points of polyline
//	            polylineOptions.addAll(points);
//	           // Adding the polyline to the map
//	            googleMap.addPolyline(polylineOptions);
	    
			}
			 if(check==0)
		        {
		        	curLat=latitude;
		        	curLong=longitude;
		        	check=1;
		        	}
		        else
		        {
		        	destLat=latitude;
		        	destLon=longitude;
		        	}
	      //  System.err.println("driver map call 20 seconds");
	        LatLng latLng = new LatLng(latitude, longitude);
			
	        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
//	      if(str_value.equals("start"))
//	      {
	    	
	    	  
	    	  if(!(getIntent().getStringExtra("riderhandicap")==null))
		    	{
			    	if(getIntent().getStringExtra("riderhandicap").equals("1"))
			    	{
			    		googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_carblue)));
				    	}
					    else 
					    {
				    	    if(!(getIntent().getStringExtra("vehicletype")==null))
							    {
								    	System.err.println("vehicle1="+ getIntent().getStringExtra("vehicletype"));
								    	String vehicle=getIntent().getStringExtra("vehicletype").replace(" ", "");
								    	
								    	if(vehicle.equals("1"))
								    		  {
								    			googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car)));
								    		  } 
									    else  if(vehicle.equals("2"))
								    		  {
									    		googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.xl_car)));
								    		  } 
									    else if(vehicle.equals("3"))
								    		  {
									    		googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.exec_car)));
								    		  } 
									     else  if(vehicle.equals("4"))
								    		  {
									    	 	googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.suv_car)));
								    		  } 
									    else  if(vehicle.equals("5"))
								    		  {
									    		googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.lux_car)));
								    		  } 
									    else
									    {
									    	googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car)));
									    	}
							    	}
				    		}
			    
		    			}
//	      }
//	      else  {
//	    	  googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
//	      	}
//	      if(!(getIntent().getStringExtra("endlati")==null))
//	        {
//	  	      //System.err.println("destination location");
//	    	  Double tempLat=Double.parseDouble(getIntent().getStringExtra("endlati"));
//	    	  Double temoLon=Double.parseDouble(getIntent().getStringExtra("endlong"));
//	    	  if(tempLat.equals("") || tempLat.equals(null))
//	    	  {
//	    		  tempLat=0.0;
//	    	  }
//	    	  else if(temoLon.equals("")|| temoLon.equals(null))
//	    	  {
//	    		  temoLon=0.0; 
//	    	  }
//	    	  
//	  	      Double  lat = tempLat;
//	  	      Double lon=temoLon;
//	  	      System.err.println(lat+"  "+lon);
//	  	      LatLng latLng1 = new LatLng(lat, lon);
//	  	      googleMap.addMarker(new MarkerOptions().position(latLng1));
//	  	      }
	        
//	      
//	      if(!(str_value==null))
//	      {
	      // Instantiating the class PolylineOptions to plot polyline in the map
            PolylineOptions polylineOptions = new PolylineOptions();

            // Setting the color of the polyline
            polylineOptions.color(Color.BLUE);

            // Setting the width of the polyline
            polylineOptions.width(15);

            // Adding the taped point to the ArrayList
            points.add(latLng);

            // Setting points of polyline
            polylineOptions.addAll(points);

            // Adding the polyline to the map
            googleMap.addPolyline(polylineOptions);
           // Toast.makeText(TripMapView_Activity.this,"location change", Toast.LENGTH_SHORT).show();
	    //  }
          
		     }catch(Exception e){
					e.printStackTrace();
		      }
		  	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	public void onProviderEnabled(String provider) {
		}
	public void onProviderDisabled(String provider) {
		}
	
	/** images download */
	private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
	        @Override
	    protected Bitmap doInBackground(String... urls) {
	            Bitmap map = null;
	            for (String url : urls) {
	                map = ImageDownloader.downloadImage(url);
	            }
	            return map;
	        }

	protected void onPostExecute(Bitmap result) {
		if(!(result==null))
		{
	    	iv_riderimage.setImageBitmap(Bitmap.createScaledBitmap(result, 70, 70, false));
		}
	    	//iv_riderimage.setImageBitmap(result);
	    			}
			}
	/** Timer **/
	 public void startTimer() {
	        timer = new Timer();
	        initializeTimerTask();
	      /**schedule the timer, after the first 20s the TimerTask will run every 20s */
	        timer.schedule(timerTask, 2000, 20000); //
		    }
	 public void stoptimertask() {
	        /**stop the timer, if it's not already null */
	        if (timer != null) {
	            timer.cancel();
	            timer = null;
	        }}
	 public void initializeTimerTask() {
	        timerTask = new TimerTask() {
	         public void run() {
	        /**use a handler to run a toast that shows the current time stamp */
	         handler.post(new Runnable() {
	         public void run() {
	         if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
			   	{
				 new  httpUpdateDriverLocation().execute();	/**call get image class */
			   		}
			  else{
			    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
			    		}
	        	
	               		}
	                });
	            }
	        };
		 }
		     
		     /** Timer **/
	 public void startTimerRider() {
		  timerRider = new Timer();
		     initializeTimerTaskRider();
		     /**schedule the timer, after the first 20s the TimerTask will run every 20s */
		      timerRider.schedule(timerTaskRider, 2000, 20000); //
			    }
		public void stoptimertaskRider() {
			        /**stop the timer, if it's not already null */
	        if (timerRider != null) {
	        	timerRider.cancel();
	        	timerRider = null;
	        }}
		public void initializeTimerTaskRider() {
			timerTaskRider = new TimerTask() {
			 public void run() {
			  /**use a handler to run a toast that shows the current time stamp */
			  handlerRider.post(new Runnable() {
			  public void run() {
        	   if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
        		   new  httpGetDriverLocation().execute();	/**call get image class */
		    		}
		    	else{
			    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
			    		}
			   		}
			  });
			 }
			};
		}
		     
		     /** Timer for actual **/
		public void startActual() {
			    timeractual = new Timer();
			    initializeTimerActual();
			   /**schedule the timer, after the first 20s the TimerTask will run every 20s */
			    timeractual.schedule(timertaskactual, 1000, 20000); //
				 }
		public void stoptimerActual() {
			        /**stop the timer, if it's not already null */
			   if (timeractual != null) {
			   	timeractual.cancel();
			  	timeractual = null;
			      }}
		public void initializeTimerActual() {
			timertaskactual = new TimerTask() {
			public void run() {
		 /**use a handler to run a toast that shows the current time stamp */
			handlerActual.post(new Runnable() {
			public void run() {
	    	 if(Utility.isConnectingToInternet(RideInfoView_Activity.this))
		    	{
					
	    		   new  distanceCalculator().execute();
		    		}
		    	else{
		    		Utility.alertMessage(RideInfoView_Activity.this,"error in internet connection");
		    	}
			  
			      }
			    });
			       }
			        };
				 }     
/**Arrive ride Async task class*/
	private class httpArrivetRide extends AsyncTask<Void, Void, Void> { 
	protected void onPreExecute() {
		super.onPreExecute();
			pDialog = new ProgressDialog(RideInfoView_Activity.this);
			//pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			if(!((Activity) RideInfoView_Activity.this).isFinishing())
			{
				pDialog.show();
				}
		}
	protected Void doInBackground(Void... arg0) {
		try {
			 parsing();
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
			
			return null;
			}
	protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			if(!(arrive_jsonResult==null))
			{
				if(arrive_jsonResult.equals("0"))
				{
					//Utility.alertMessage(RideInfoView_Activity.this, arrive_jsonMessage);
					btn_starttrip.setVisibility(View.VISIBLE);
					btn_endtrip.setVisibility(View.GONE);
					btn_arrive.setVisibility(View.GONE);
					}
				else
				{
					Utility.alertMessage(RideInfoView_Activity.this, arrive_jsonMessage);
					}
				}
			}
			//pending request code parsing function
	public void parsing() throws JSONException {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/ArriveRide");//Url_Address.url_promocode);
			JSONObject json = new JSONObject();
			
			json.put("Trigger", "ArriveRide");
			System.err.println("ArriveRide web service");
			
			System.err.println("tripid="+tripid);
			json.put("TripId", tripid);
			
			json.put("Latitude", latitude);
			json.put("Longitude", longitude);
			      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("arriveRide"," result-->>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
			arrive_jsonResult=obj.getString("result");
			arrive_jsonMessage=obj.getString("message");
		
				Log.i("arriveRide:", "Result: "+arrive_jsonResult);
				Log.i("arriveRide:", "Message :"+arrive_jsonMessage);
	
			}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
				}
			}
/**start ride Async task class */
	private class httpStartRide extends AsyncTask<Void, Void, Void> { 
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(RideInfoView_Activity.this);
			//pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			if(!((Activity) RideInfoView_Activity.this).isFinishing())
			{
				pDialog.show();
				}
			}
		protected Void doInBackground(Void... arg0) {
		
			try {
			 parsing();
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
			
			return null;
			}
			
			@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			pDialog.dismiss();
			
			 if(location!=null){
	              	onLocationChanged(location);
	           		}
			if(!(start_jsonResult==null))
			{
//			if(!(getIntent().getStringExtra("mode")==null))
//			{
//				btn_starttrip.setVisibility(View.GONE);
//				btn_endtrip.setVisibility(View.VISIBLE);
//				tv_destination.setText(getIntent().getStringExtra("destination"));
//					
//				}
//			else
//			{
				if(start_jsonResult.equals("0"))
				{
					//Utility.alertMessage(RideInfoView_Activity.this, start_jsonMessage);
					btn_starttrip.setVisibility(View.GONE);
					btn_endtrip.setVisibility(View.VISIBLE);
					btn_arrive.setVisibility(View.GONE);
			
				}
				else
				{
					Utility.alertMessage(RideInfoView_Activity.this, start_jsonMessage);
				}
//					}	
			}
			}
			//pending request code parsing function
		public void parsing() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 30000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 31000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/StartRide");//Url_Address.url_promocode);
			JSONObject json = new JSONObject();
			
			json.put("Trigger", "StartRide");
			System.err.println("tripid"+tripid);
			
			json.put("TripId", tripid);
			json.put("Latitude", latitude);
			json.put("Longitude", longitude);
	      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("StartRide"," result-->>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
				start_jsonResult=obj.getString("result");
				start_jsonMessage=obj.getString("message");
		
				Log.i("StartRide:", "Result: "+start_jsonResult);
				Log.i("StartRide:", "Message :"+start_jsonMessage);
	
			}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
				}
			}
/**end ride Async task class */
		private class httpEndRide extends AsyncTask<Void, Void, Void> { 
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(RideInfoView_Activity.this);
				//pDialog.setTitle("Loading");
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				if(!((Activity) RideInfoView_Activity.this).isFinishing())
				{
					pDialog.show();
					}
				
				}
		protected Void doInBackground(Void... arg0) {
			
				try {
				 endRideparsing();
				 } catch (Exception e) {
				 e.printStackTrace();
				}
				
				return null;
				}
		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				int int_min = 0;
				pDialog.dismiss();
				if(!(end_jsonResult==null))
				{
					if(end_jsonResult.equals("0"))
					{
						 stoptimertaskRider();
						 stoptimerActual();
						 stoptimertask();
						Intent i=new Intent(RideInfoView_Activity.this,DriverRating_Activity.class);
						i.putExtra("riderid", getIntent().getStringExtra("riderid"));
						i.putExtra("fare", getIntent().getStringExtra("suggestionfare"));
						i.putExtra("tripid", getIntent().getStringExtra("tripid"));
						i.putExtra("vehicletype", getIntent().getStringExtra("vehicletype")); 
						String str_tempeta=getIntent().getStringExtra("eta");
						String  str_tempdistance=getIntent().getStringExtra("distance");
						try
							{	//2hours 20mins
							    int int_tempdistance=Integer.parseInt(str_tempdistance);
							   							
								   if(str_tempeta.contains("hour"))
								    {
								    	   try{
									   		String[] values = str_tempeta.split(" ");
											String hour=values[0].split("hours")[0];
											String min=values[1].split("mins")[0];
											System.err.println("hour="+hour+"min="+min);
											int int_hourmin=60*Integer.parseInt(hour);
											int_min=Integer.parseInt(min)+int_hourmin;
											
									    	}
								    
								    	   catch(Exception e){
															 }
								    	}
								    else if(str_tempeta.contains("mins"))
								    {
								    	str_tempeta.replace("mins", "");
								    	
								    	try{
								    		int_min=Integer.parseInt(str_tempeta);
									    	}
									    
								    	catch(Exception e){
										 
									 }}
						
							  //  int	int_eta=Integer.parseInt(str_tempeta);
								int newvalue=approxMinutes-int_min;
								int newdis=distanceValue-int_tempdistance;
								Log.e("tag", newvalue+"=newvalue");
								Log.e("tag",newdis+"=newdis" );
								Log.e("tag", approxMinutes+"=approxMinutes");
								Log.e("tag", int_min+"int_eta");
								Log.e("tag", int_tempdistance+"int_suggestionfare");
								if(newvalue>=7)
								{
									i.putExtra("again","yes");
									i.putExtra("newdistance", distanceValue);
									i.putExtra("newmin", approxMinutes);
									}
								else if(newdis>=1)
								{
									i.putExtra("again","yes");
									i.putExtra("newdistance", distanceValue);
									i.putExtra("newmin", approxMinutes);
									}

								}
							catch(Exception e){
								
							}
						
							 if(!(getIntent().getStringExtra("value")==null)) {
								  if(getIntent().getStringExtra("value").equals("rider"))
								  {
									  i.putExtra("value", "rider"); 
								  	}
								  else if(getIntent().getStringExtra("value").equals("driver"))
								  {
									  i.putExtra("value", "driver");  
								  	}
								 }
							 finish();	
							 startActivity(i);	
					}
					else
					{
						Utility.alertMessage(RideInfoView_Activity.this, end_jsonMessage);
						}
				
					}
				}
			
				/**end ride parsing function **/
			public void endRideparsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/EndRide");//Url_Address.url_promocode);
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "EndRide");
				System.err.println("tripid"+tripid);
				
				json.put("TripId", tripid);
				json.put("Latitude", latitude);
				json.put("Longitude", longitude);
				//json.put("TripFinalFare",getIntent().getStringExtra("suggestionfare"));
			 
				httpost.setEntity(new StringEntity(json.toString()));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				
				HttpResponse response = client.execute(httpost);
				HttpEntity resEntityGet = response.getEntity();
				String jsonstr=EntityUtils.toString(resEntityGet);
				if(jsonstr!=null)
				{
				 Log.e("EndRide"," result-->>>>>    "+ jsonstr);
				 
					}
					JSONObject obj=new JSONObject(jsonstr);
					end_jsonResult=obj.getString("result");
					end_jsonMessage=obj.getString("message");
			
					Log.i("EndRide:", "Result: "+end_jsonResult);
					Log.i("EndRide:", "Message :"+end_jsonMessage);
		
				}
				  catch(Exception e){
				   System.out.println(e);
				   Log.d("tag", "Error :"+e); }  
					}
				}
	
			     
	 	/**cancel Ride web services class */
			private class httpCancelRide extends AsyncTask<Void, Void, Void> { 
					@Override
					protected void onPreExecute() {
						super.onPreExecute();
						// Showing progress dialog
						pDialog = new ProgressDialog(RideInfoView_Activity.this);
						//pDialog.setTitle("Loading");
						pDialog.setMessage("Please wait...");
						pDialog.setCancelable(false);
						if(!((Activity) RideInfoView_Activity.this).isFinishing())
						{
							pDialog.show();
							}
					}
					protected Void doInBackground(Void... arg0) {
					
						try {
							
						cancelRideParsing();
						
						} catch (Exception e) {
						 e.printStackTrace();
						}
						
						return null;
						}
				
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						  pDialog.dismiss();
							
				  if(!(cancel_jsonResult==null))
				  {
						  if(cancel_jsonResult.equals("0"))
						  {
							 // Utility.alertMessage(RideInfoView_Activity.this, cancel_jsonMessage);
							 Intent i=new Intent(RideInfoView_Activity.this,DriverRating_Activity.class);
							 i.putExtra("cancel", "cancel");
							 i.putExtra("riderid", getIntent().getStringExtra("riderid"));
							 i.putExtra("fare", getIntent().getStringExtra("suggestionfare"));
							 i.putExtra("tripid", getIntent().getStringExtra("tripid"));
							
							 if(!(getIntent().getStringExtra("value")==null)) {
								  if(getIntent().getStringExtra("value").equals("rider"))
								  {
									  i.putExtra("value", "rider"); 
								  	}
								  else if(getIntent().getStringExtra("value").equals("driver"))
								  {
									  i.putExtra("value", "driver");  
								  	}
								 }
							 finish();
							 startActivity(i);
							  }
						  else
						  {
							  Utility.alertMessage(RideInfoView_Activity.this, cancel_jsonMessage);  
						  		}
						  }
					}
					
	/**cancel Ride function*/
			public void cancelRideParsing() throws JSONException {
					try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/CancelRide");//Url_Address.url_UpdateDriverLocation;
					JSONObject json = new JSONObject();
					
					System.err.println("tripId:"+tripid);
					//json.put("TripId", tripid);
					json.put("TripId", tripid);
					System.err.println("trigger:"+"Driver");
					json.put("Trigger", "Driver");
					
					System.err.println("Reason:"+str_cancelride);
					json.put("Reason", str_cancelride);
															
					System.err.println("Latitude:"+latitude);
					json.put("Latitude", latitude);
					
					System.err.println("Longitude:"+longitude);
					json.put("Longitude", longitude);
				
					      
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					if(jsonstr!=null)
					{
					 Log.e("tag","cancel result-->>>>>    "+ jsonstr);
					 
						}
						JSONObject obj=new JSONObject(jsonstr);
						cancel_jsonResult=obj.getString("result");
						cancel_jsonMessage=obj.getString("message");
				
						Log.i("tag:", "Result: "+cancel_jsonResult);
						Log.i("tag:", "Message :"+cancel_jsonMessage);
			
					}
					  catch(Exception e){
					   System.out.println(e);
					   Log.d("tag", "Error :"+e); }  
						}
					}
/**Update Driver Location web services class */
		private class httpUpdateDriverLocation extends AsyncTask<Void, Void, Void> { 
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
//					pDialog = new ProgressDialog(RideInfoView_Activity.this);
//					pDialog.setTitle("Loading");
//					pDialog.setMessage("Please wait...");
//					pDialog.setCancelable(false);
//					pDialog.show();
					}
				
		
				protected Void doInBackground(Void... arg0) {
				
					try {
						
					updateDriverLocationParsing();
					
					} catch (Exception e) {
					 e.printStackTrace();
					}
					
					return null;
					}
			
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					//  pDialog.dismiss();
					
		
					}
				
	/**Update Driver Location function */
		public void updateDriverLocationParsing() throws JSONException {
			try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/UpdateDriverLocation");//Url_Address.url_UpdateDriverLocation;
					JSONObject json = new JSONObject();
					
					json.put("Trigger", "UpdateDriverLocation");
					System.err.println("DriverId:"+prefs.getString("driverid", null));
					json.put("DriverId", prefs.getString("driverid", null));
					
					System.err.println("longitude:"+longitude);
					json.put("longitude", longitude);
					System.err.println("latitude:"+latitude);
					json.put("latitude", latitude);
					
					System.err.println("trigger:"+"second");
					json.put("trigger", "");
				
					      
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					if(jsonstr!=null)
					{
					 Log.e("tag","Active result-->>>>>    "+ jsonstr);
					 
						}
						JSONObject obj=new JSONObject(jsonstr);
						String jsonResult=obj.getString("result");
						String jsonMessage=obj.getString("message");
						//String jsonMessage=obj.getString("Tripid");
				
						Log.i("tag:", "Result: "+jsonResult);
						Log.i("tag:", "Message :"+jsonMessage);
			
					}
					  catch(Exception e){
					   System.out.println(e);
					   Log.d("tag", "Error :"+e); }  
						}
					}
		
		 /***Get driver location class*****/
		
	private class httpGetDriverLocation extends AsyncTask<Void, Void, Void> { 
		protected void onPreExecute() {
		super.onPreExecute();
					// Showing progress dialog
				if(flag==0)
					{
					googleMap.clear();
					points.clear();
			//		pDialog1 = new ProgressDialog(RideInfoView_Activity.this);
					//pDialog1.setTitle("Loading");
//					pDialog1.setMessage("Please wait...");
//					pDialog1.setCancelable(false);
//					pDialog1.show();
					flag=1;
					}
				}
				
		
				protected Void doInBackground(Void... arg0) {
				
					try {
						
						GetDriverLocation();
					
					} catch (Exception e) {
					 e.printStackTrace();
					}
					
					return null;
					}
			
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
				//	  pDialog1.dismiss();
//					  Intent i=new Intent(GetDriverLocation.this,GetDriverLocation.class);
//					  i.putExtra("tripid", str_tripid);
//					  i.putExtra("jsonlatitude", jsonlatitude);
//					  i.putExtra("jsonlongitude", jsonlongitude);
//					  startActivity(i);
					  onLocationChanged(location);
					  
				}
				
	/**GetDriverLocation function*/
			public void GetDriverLocation() throws JSONException {
					try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDriverLocation");//Url_Address.url_UpdateDriverLocation;
					JSONObject json = new JSONObject();
					
					json.put("Trigger", "GetDriverLocation");
									
					Log.d("tag","DriverId:"+prefs.getString("riv_driverid", ""));
					json.put("DriverId", prefs.getString("riv_driverid", ""));
					
					
					Log.d("tag","TripId:"+prefs.getString("riv_tripid", ""));
					json.put("TripId", prefs.getString("riv_tripid", ""));
				
					      
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					
					if(jsonstr!=null)
					{
					 Log.e("tag","GetDriverLocation result-->>>>>    "+ jsonstr);
					 
					
					
					JSONObject obj=new JSONObject(jsonstr);
					String 	jsonResult=obj.getString("result");
					String	jsonMessage=obj.getString("message");
					
					String jsonDriverID=obj.getString("driverId");
					getslongitude=obj.getString("longitude");
					getslatitude=obj.getString("latitude");
					
					Log.i("tag", "Driver ID:"+jsonDriverID +" longitude"+getslongitude+" latitude"+getslatitude);
					String jsondriver_first=obj.getString("driver_first");

					String jsondriver_last=obj.getString("driver_last");
					String jsonlast_updated=obj.getString("last_updated");
					
					String jsondriver_image=obj.getString("driver_image");
					String jsonvehicle_class=obj.getString("vehicle_class");
					
					String jsontripid=obj.getString("tripid");
					String jsonphone_number=obj.getString("phone_number");
					
					Log.i("tag:", "Result: "+jsonResult);
					Log.i("tag:", "Message :"+jsonMessage);
			
					}
					}
					  catch(Exception e){
					   System.out.println(e);
					   Log.d("tag", "Error :"+e); }  
						}
					}

	/*** calculate distance value	*/
		class distanceCalculator extends AsyncTask<String, String, String>{
		    @Override
		    protected String doInBackground(String... str) {
		    	Log.i("tag", "Current Lati:"+curLat+"  Current Longitude:"+curLong+"  Destination_Lati:"+destLat+" Destination Longitude:"+destLon);
		    	String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+curLat+","+curLong+"&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";

//		    	String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=30.711104,76.6861538&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";
//		    	Log.e("url", url);
				
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
			      
			  String      myAddress = jsonObject.getJSONArray("origin_addresses").getString(0);
			        
			        JSONArray searchResults =  jsonObject.getJSONArray(("rows"));
			        
			        for(int i = 0; i<searchResults.length(); i++){
			        	JSONArray elementArray = searchResults.getJSONObject(i).getJSONArray("elements");
			        	for(int j =0; j<elementArray.length(); j++){
				        	distance = elementArray.getJSONObject(j).getJSONObject("distance")
				                    .getString("text");
				        	distanceValue = elementArray.getJSONObject(j).getJSONObject("distance")
				                    .getInt("value");
				        	
				      float  	 totalKms = (float)(distanceValue*0.001);
				        //	Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);
				        	if(totalKms>2){
				        		basic_fare = ((totalKms-2)*12) + 25;
				        	}else
				        		{
				        			basic_fare = 25;
				        			}
				          //  basic_fare = Float.valueOf(twoDForm.format(basic_fare));
				            
//				        	fare = ((distanceValue/1000))
				        	time    = elementArray.getJSONObject(j).getJSONObject("duration")
				                    .getString("text");
				        	timeValue = elementArray.getJSONObject(j).getJSONObject("duration")
				                    .getInt("value");
				        	
				        	float temp= (float)timeValue/60;
				        	
				        	approxMinutes = Math.round(temp);
				        	
				        	Log.e("result","Option "+" :: "+distance);//+" | "+approxMinutes+",, "+temp);
				        	
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
		    protected void onPreExecute() {
		  // progress = ProgressDialog.show(RideInfoView_Activity.this, "", "Please wait...");
		    }
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		    	    
		        des_mile=totalKms/(float) 1.609344;
	    
		       // String newValue=new DecimalFormat("##.##").format(des_mile);
	
			
		        }
		}
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
		 
	 /** Fetches data from url passed */
    private class DownloadTask extends AsyncTask<String, Void, String>{
		      protected void onPreExecute() {
		    	 progress1 = ProgressDialog.show(RideInfoView_Activity.this, "", "Please wait...");
		    		    }
		      protected String doInBackground(String... url) {
		            String data = "";
		 
		            try{
		                // Fetching the data from web service
		                data = downloadUrl(url[0]);
		            }catch(Exception e){
		                Log.d("Background Task",e.toString());
		            }
		            return data;
		        }
		 
		        protected void onPostExecute(String result) {
		            super.onPostExecute(result);
		            progress1.dismiss();
		           new ParserTask().execute(result);
		        }
		    }
		 
		    /** A class to parse the Google Places in JSON format */
		    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{
		    	protected void onPreExecute() {
//		    		 progress2 = ProgressDialog.show(RideInfoView_Activity.this, "", "Please wait...");
		    		    }
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
		        //	progress2.dismiss();
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
		                lineOptions.color(Color.BLUE);
		            }
		 
		            // Drawing polyline in the Google Map for the i-th route
		            googleMap.addPolyline(lineOptions);
		        }
		    }
		
	@Override
	protected void onPause() {
		
//		if(value.equals("rider"))
//		{
//		stoptimertask();
//		stoptimertaskRider();
//		stoptimerActual();
//		}
	
		super.onPause();
	}
		public void onBackPressed() {
		
		}
}