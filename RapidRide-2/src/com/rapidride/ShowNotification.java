package com.rapidride;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowNotification extends Activity implements LocationListener{
	String status="",SpiltImage="";
	JSONObject json=new JSONObject();
	String TripId="";
	ProgressDialog pDialog;
	ImageView ImageDriver,img_handicap;
	String messages="";
	String sendbyreq="",currentLoc="",destination="",eta="",tripID="",handicap="",ridername="",
			getRating="",vehicle="",offered_fare="",pickup="",distance="",imageUrl="",requestType="";
	String	jsonResult="",jsonMessage="",pending_jsonMessage="",pending_jsonResult="";
	SharedPreferences prefs;
	TextView timeLeft,sendby,current_Loc,Destination1,rating,pickUpDateTime,
	prefferedVechileType,offered,textViewDistance,txtbackground,txtTimeLeft_LeftSide,
	text_Rapid,text_Message;
	int countdownNumber = 15;
	Button btnAccept,btnReject,btnOk;
	double latitude,longitude;
	MediaPlayer mp;
	Boolean bol_mp=false,bol_reject=false;
	LocationManager locationManager;
	  String json_driverName="",jsonDestination="",jsonDistance="",
	    		jsonRequestType="",jsonDriverImage="",jsonTripID="",jsonDriverId="",jsonStart="",
	    		jsonSuggesstionFare="",jsonETA="",jsonActaulFare="",
	    		jsonRiderID="",jsonDriverRating="",jsonPickUpDate="",jsonRiderName="",
	    		json_riderRating="",json_RiderImage="",
	    		jsonHandicap="",jsonVehicleType="",jsonEndLatitude="",jsonEndLongitude="",jsonDriverID=""
	    		,jsonDriverName="",jsondriveRating="", jsonDestnationLatitude="",jsonDestinationLongitude="",jsonDestinationAddress="",
	    		jsonStartingLongitude="",jsonStartingLatitude="",jsonStartingAddress="",jsonstartLat="",jsonstartlon="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.request_view);
		
		setFinishOnTouchOutside(false);
		
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
             // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);
            //on location change method set condition 
        onLocationChanged(location);
		
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
		sendby=(TextView)findViewById(R.id.textViewWhoSendRequest);
		current_Loc=(TextView)findViewById(R.id.textViewPickUp);
		Destination1=(TextView)findViewById(R.id.txtDistance);
		rating=(TextView)findViewById(R.id.textViewRating);
		pickUpDateTime=(TextView)findViewById(R.id.textViewPickUp2);
		prefferedVechileType=(TextView)findViewById(R.id.textViewVehicle);
		offered=(TextView)findViewById(R.id.textViewOffered);
		textViewDistance=(TextView)findViewById(R.id.textViewDistance);
		img_handicap=(ImageView)findViewById(R.id.imageViewHandicap);
		ImageDriver=(ImageView)findViewById(R.id.imageViewDriverImage);
		txtbackground=(TextView)findViewById(R.id.textViewBackground);
		txtTimeLeft_LeftSide=(TextView)findViewById(R.id.textViewTimeLeft_leftside);
		btnAccept=(Button)findViewById(R.id.buttonAccept);
		btnReject=(Button)findViewById(R.id.buttonReject);
		timeLeft=(TextView)findViewById(R.id.textViewTimeLeft);
		text_Rapid=(TextView)findViewById(R.id.textView_Rapid);
		text_Message=(TextView)findViewById(R.id.textViewShowDriverMessage);		
		btnOk=(Button)findViewById(R.id.buttonOkDriverSide);
		
		Bundle extras = getIntent().getExtras();
		try{
		if (extras != null) {
			
			  try{
	       		  
        		  mp = new MediaPlayer();
        		  mp = MediaPlayer.create(this, R.raw.request);
        		  mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        		  mp.start();
				bol_mp=true;
        	  }
        	  catch(Exception e){
        	  }
			setFinishOnTouchOutside(true);
			sendbyreq=extras.getString("RequestSendBy");
			currentLoc=extras.getString("CurrentLocation");
			destination=extras.getString("Destination");
			eta=extras.getString("ETA");
			tripID=extras.getString("TripId");
			getRating=extras.getString("Rating");
			handicap=extras.getString("Handicap");
			vehicle=extras.getString("PrefferedVechileType");
			offered_fare=extras.getString("OfferedPrice");
			pickup=extras.getString("PickUpDateTime");
			distance=extras.getString("Distance");
			imageUrl=extras.getString("Image");
			requestType=extras.getString("RequestType");
			
			System.err.println("disssss="+distance);
		//play song
			
		}
		else
		{
			String cancelMessage=prefs.getString("CancelMessage", "");
//			if(cancelMessage==null)
//			{
//				
//				}
//			else
//			{
//				if(!prefs.getString("mode", "").equals("driver"))
//				{
					
					System.err.println("yessssss");
					sendby.setVisibility(View.GONE);
					current_Loc.setVisibility(View.GONE);
					Destination1.setVisibility(View.GONE);
					rating.setVisibility(View.GONE);
					pickUpDateTime.setVisibility(View.GONE);
					prefferedVechileType.setVisibility(View.GONE);
					offered.setVisibility(View.GONE);
					textViewDistance.setVisibility(View.GONE);
					img_handicap.setVisibility(View.GONE);
					ImageDriver.setVisibility(View.GONE);
					txtbackground.setVisibility(View.GONE);
					txtTimeLeft_LeftSide.setVisibility(View.GONE);
					btnAccept.setVisibility(View.GONE);
					btnReject.setVisibility(View.GONE);
					timeLeft.setVisibility(View.GONE);
					
					text_Rapid.setVisibility(View.VISIBLE);
					text_Message.setVisibility(View.VISIBLE);
					btnOk.setVisibility(View.VISIBLE);
					text_Message.setText(cancelMessage);
					btnOk.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
					/*********Change code*********/
					Intent i=new Intent(ShowNotification.this,DriverView_Activity.class);
					finish();
					startActivity(i);
					/*********Change code*********/
						}
					});
				}
//				else
//				{
//					System.err.println("noooooooo");
//				}
			}
	//	}
	//	}
		catch(Exception e)
		{
				
		}
		

		if(vehicle.equals(" 0") ||vehicle.equals(" 1"))
		{
			vehicle="Vehicle:"+"REGULAR";
		}
		else if(vehicle.equals(" 2"))
		{
			vehicle="Vehicle:"+"XL";
		}
		else if(vehicle.equals(" 3"))
		{
			vehicle="Vehicle:"+"EXECUTIVE";
		}
		else if(vehicle.equals(" 4"))
		{
			vehicle="Vehicle:"+"SUV";
		}
		else if(vehicle.equals(" 5"))
		{
			vehicle="Vehicle:"+"LUXURY";
		}
		
		
        sendby.setText(sendbyreq);
        current_Loc.setText("Pick up at:"+currentLoc);
        Destination1.setText("Destination:"+destination);
        rating.setText("Rating:"+getRating);
        
        //background Color
        if(requestType.equalsIgnoreCase("Now"))
        {
        	txtbackground.setBackgroundColor(getResources().getColor(R.color.yellow));
        }
        else if(requestType.equalsIgnoreCase("Future"))
        {
        	txtbackground.setBackgroundColor(getResources().getColor(R.color.red));
        }
        else if(requestType.equalsIgnoreCase("VIP"))
        {
        	txtbackground.setBackgroundColor(getResources().getColor(R.color.blue));
        	}
        
        
        if(requestType.equalsIgnoreCase("Now"))
        {
	        pickUpDateTime.setText("Pickup Time:"+"Now");
	        }
        else
        {
  		  try 
		    {
		    	
  			    DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		    	Date date1 = (Date)formatter.parse(pickup);
		    	formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
		    	String  s = formatter.format(date1);
		    	//  System.err.println(s);
		    	pickUpDateTime.setText("Pickup Time:"+s);
//			      formatter = new SimpleDateFormat("HH:mm");
//			      String  t = formatter.format(date1);
//			      tv_time.setText(t);
			     // System.err.println(t);
		    }
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    	}
//  		pickUpDateTime.setText("Pickup Time:"+pickup); 	
        }
       
        prefferedVechileType.setText(vehicle);
        offered.setText("Offered Fare:"+offered_fare);
        System.err.println("disssssss="+distance);
      
        if(extras!=null)
        {
	        if(extras.getString("Distance").equals(""))
		        {}
		        else
		        {
			       try{
			       Float ff=Float.parseFloat(distance);
			       String  newValue_disatnce=new DecimalFormat("##.#").format(ff);
			       textViewDistance.setText("Distance:"+newValue_disatnce);
			        }catch(Exception e)
			        {
			        System.err.println(e);
			        }
		        }
        	}
        
        img_handicap.setVisibility(View.GONE);
        if(handicap.equals("1"))
        {
        	img_handicap.setVisibility(View.VISIBLE);
        	}
       
        
        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(countdownNumber == 0) {
                            cancel();
                            if(bol_reject==false)
                            {
                            	
                            	finish();
                            	//new httpRejectParsing().execute();
                            	}
                           
                        }
                        timeLeft.setText(String.valueOf(countdownNumber));
                        countdownNumber--;
                    }
                });
            }
        };

        Timer countdown = new Timer();
        countdown.schedule(countdownTask, 0, 1000);
        
        if(imageUrl==null || imageUrl=="")
        {
        	
        }else
	        {
      	        GetXMLTask2 task = new GetXMLTask2();
	    		// Execute the task
	    		task.execute(new String[] {imageUrl});
	        }
       
        btnAccept.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			status="Accepted";
			new httpAcceptParsing().execute();
				
				}
		});
        
       
        btnReject.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			//status="Rejected";
			//new httpRejectParsing().execute();
				finish();
			bol_reject=true;
			}
        });
	}
	public class GetXMLTask2 extends AsyncTask<String, Void, Bitmap> {
        @Override
    protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map =ImageDownloader.downloadImage(url);
            }
            return map;
        }

    protected void onPostExecute(Bitmap result) {
    	if(result!=null)
    	{
	    	ImageDriver.setImageBitmap(result);
	    	}
    
        }	
}
	private class httpAcceptParsing extends AsyncTask<Void, Void, Void> { 
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowNotification.this);
			//pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
		
		@Override
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
				
				if ((pDialog != null) && pDialog.isShowing())
				{
					pDialog.dismiss();
					}
				if(jsonResult.equals("0"))
				{
					 if(requestType.equalsIgnoreCase("Now"))
					  {
						new httpUpdateDriverLocation().execute();
					  	}
					 else
					 {
						 new GetDetailByTripID2().execute();
					 	}
					}
				else
				{
					Utility.alertMessage(ShowNotification.this, jsonMessage);
					}
			
				}
			
			}
	public void parsing()
	{
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection =60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/AcceptOrRejectRequest");
			json = new JSONObject();
			json.put("TripId",tripID);
			json.put("Status", "Accepted");
			
		      //	      
			Log.i("tag", "Sending Data:"+json.toString());
			
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","Response Request:>>>>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
			jsonResult=obj.getString("result");
			jsonMessage=obj.getString("message");
				
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+jsonMessage);
				}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e);
			   }  
		}
	
	/******Reject Class**************/
	private class httpRejectParsing extends AsyncTask<Void, Void, Void> { 
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ShowNotification.this);
			//pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
		
		@Override
		protected Void doInBackground(Void... arg0) {
		
			try {
				parsingReject();
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
			if(jsonResult.equals("0"))
			{
				finish();
				}
			else
			{
				Utility.alertMessage(ShowNotification.this, jsonMessage);
				}
			
			}
			
		}
	public void parsingReject()
	{
		try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection =60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/AcceptOrRejectRequest");
			json = new JSONObject();
			json.put("TripId",tripID);
			json.put("Status", "Rejected");
			
		      //	      
			Log.i("tag", "Sending Data:"+json.toString());
			
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","Response Request:>>>>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
			jsonResult=obj.getString("result");
			jsonMessage=obj.getString("message");
				
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+jsonMessage);
				}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e);
			   }  
	}

	/**Update Driver Location web services class */
	private class httpUpdateDriverLocation extends AsyncTask<Void, Void, Void> { 
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
//				pDialog = new ProgressDialog(RideInfoView_Activity.this);
//				pDialog.setTitle("Loading");
//				pDialog.setMessage("Please wait...");
//				pDialog.setCancelable(false);
//				pDialog.show();
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

					new GetDetailByTripID().execute();
	
				}
			
/**Update Driver Location function */
		public void updateDriverLocationParsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 31000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/UpdateDriverLocation");//Url_Address.url_UpdateDriverLocation;
				JSONObject json = new JSONObject();
			
				System.err.println("DriverId:"+prefs.getString("driverid", null));
				json.put("DriverId", prefs.getString("driverid", null));
				
				System.err.println("longitude:"+longitude);
				json.put("longitude", longitude);
				System.err.println("latitude:"+latitude);
				json.put("latitude", latitude);
				
				System.err.println("trigger:"+tripID);
				json.put("trigger", tripID);
			
				Log.i("tag", "Sending Data in Update Driver:"+json.toString());      
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
	
	
	private class GetDetailByTripID extends AsyncTask<Void, Void, Void> { 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
//			pDialog = new ProgressDialog(ShowNotificationRiderSide.this);
//			pDialog.setTitle("Loading");
//			pDialog.setMessage("Please wait...");
//			pDialog.setCancelable(false);
//			pDialog.show();
			}
		

		protected Void doInBackground(Void... arg0) {
			try {
				DetailParsing1();
			
			} catch (Exception e) {
			 e.printStackTrace();
			}
			
			return null;
			}

		protected void onPostExecute(Void result) {
		super.onPostExecute(result);
//			  pDialog.dismiss();
			  if(pending_jsonResult.equals("0"))
			  {
					//Intent i=new Intent(ShowNotification.this,RideInfoView_Activity.class);
//					i.putExtra("value", "rider");
//					i.putExtra("riderid",jsonRiderID );
//					i.putExtra("tripid",jsonTripID );
//					i.putExtra("suggestionfare",jsonSuggesstionFare);
//					i.putExtra("eta", jsonETA);
//					i.putExtra("ridername",jsonRiderName);
//					i.putExtra("destination",jsonDestination);
//					i.putExtra("riderrating",json_riderRating);
//					i.putExtra("pickup",jsonPickUpDate);
//					i.putExtra("riderimage",json_RiderImage);
//					
//					i.putExtra("riderhandicap",jsonHandicap);
//					i.putExtra("vehicletype",jsonVehicleType);
//					i.putExtra("requesttype",jsonRequestType);
//					
//					i.putExtra("endlati",jsonEndLatitude);
//					i.putExtra("endlong",jsonEndLongitude);
//					
//					i.putExtra("driverid",jsonDriverID);
//					i.putExtra("drivername",jsonDriverName);
//					i.putExtra("driverrating",jsondriveRating);
//					i.putExtra("driverimage",jsonDriverImage);
					
					Intent i=new Intent(ShowNotification.this,RideInfoView_Activity.class);
					  i.putExtra("value", "driver");
		          	  i.putExtra("ridername",jsonRiderName);
		          	  i.putExtra("riderid", jsonRiderID);
		          	  i.putExtra("destination",jsonDestination);
		          	  i.putExtra("start", currentLoc);
		          	  i.putExtra("pickup",jsonPickUpDate);
		          	  i.putExtra("riderimage",json_RiderImage);
		          	  i.putExtra("suggestionfare",jsonSuggesstionFare);
		          	  i.putExtra("eta", jsonETA);
		          	  i.putExtra("riderrating", json_riderRating);
		          	  i.putExtra("tripid", tripID);
		          	  i.putExtra("endlati", jsonEndLatitude);
		          	  i.putExtra("endlong", jsonEndLongitude);
		          	  i.putExtra("vehicletype", jsonVehicleType);
		          	  i.putExtra("riderhandicap", jsonHandicap); 
		          	  i.putExtra("requesttype", jsonRequestType);
		          	  i.putExtra("startlat", jsonstartLat); 
		          	  i.putExtra("startlong", jsonstartlon);
		          	  i.putExtra("tripid", jsonTripID);
		          	  
		          	  finish();
					  startActivity(i);
					
					
			  }
			  else
			  {
				  Utility.alertMessage(ShowNotification.this, pending_jsonMessage);
			  }
			}
		
	/** Ride Detail function*/
	public void DetailParsing1() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDetailsByTripId");//Url_Address.url_UpdateDriverLocation;
			JSONObject json = new JSONObject();
			
//			json.put("Trigger", "CancelRide");
							
			Log.i("tag", "Getting TripID:"+tripID);
			
			json.put("TripId",tripID);
//			System.err.println("trigger:"+"Driver");
//			json.put("Trigger", "Driver");
		
			Log.d("tag", "Send Trip Id:"+json.toString());      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","RiderSide Start Arrive result-->>>>>    "+ jsonstr);
			 
			
				JSONObject obj=new JSONObject(jsonstr);
				pending_jsonResult=obj.getString("result");
				pending_jsonMessage=obj.getString("message");
				
				jsonRiderID=obj.getString("riderid");
				jsonSuggesstionFare=obj.getString("offered_fare");
				jsonETA=obj.getString("trip_time_est");
				jsonRiderName=obj.getString("rider_name");
				jsonDestination=obj.getString("ending_loc");
				json_riderRating=obj.getString("rider_rating");
				json_RiderImage=obj.getString("rider_img");
				jsonPickUpDate=obj.getString("trip_request_pickup_date");
				
				jsonHandicap=obj.getString("rider_handicap");
				jsonVehicleType=obj.getString("rider_prefer_vehicle");
				jsonRequestType=obj.getString("requestType");
				jsonEndLatitude=obj.getString("end_lat");
				jsonEndLongitude=obj.getString("end_lon");
				
				jsonDriverID=obj.getString("driverid");
				jsonDriverName=obj.getString("driver_name");
				jsondriveRating=obj.getString("driver_rating");
				jsonDriverImage=obj.getString("driver_img");
				jsonstartLat=obj.getString("start_lat");
				jsonstartlon=obj.getString("start_lon");
				
				jsonTripID=tripID;
				
				
				Log.i("tag:", "Result: "+pending_jsonResult);
				Log.i("tag:", "Message :"+pending_jsonMessage);
				
			}
			}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
			}
	}
	
	private class GetDetailByTripID2 extends AsyncTask<Void, Void, Void> { 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
//			pDialog = new ProgressDialog(ShowNotificationRiderSide.this);
//			pDialog.setTitle("Loading");
//			pDialog.setMessage("Please wait...");
//			pDialog.setCancelable(false);
//			pDialog.show();
			}
		

		protected Void doInBackground(Void... arg0) {
			try {
				DetailParsing1();
			
			} catch (Exception e) {
			 e.printStackTrace();
			}
			
			return null;
			}

		protected void onPostExecute(Void result) {
		super.onPostExecute(result);
//			  pDialog.dismiss();
			  if(pending_jsonResult.equals("0"))
			  {
					
				      Intent i=new Intent(ShowNotification.this,PendingReqDetail_Activity.class);
					  i.putExtra("value", "driver");
		          	  i.putExtra("ridername",jsonRiderName);
		          	  i.putExtra("riderid", jsonRiderID);
		          	  i.putExtra("destination",jsonDestination);
		          	  i.putExtra("start", currentLoc);
		          	  i.putExtra("pickupdate",jsonPickUpDate);
		          	  i.putExtra("riderimage",json_RiderImage);
		          	  i.putExtra("suggestionfare",jsonSuggesstionFare);
		          	  i.putExtra("actualfare",jsonSuggesstionFare);
		          	  i.putExtra("distance",jsonDistance);
		          	  i.putExtra("eta", jsonETA);
		          	  i.putExtra("riderrating", json_riderRating);
		          	  i.putExtra("endlati", jsonEndLatitude);
		          	  i.putExtra("endlong", jsonEndLongitude);
		          	  i.putExtra("vehicletype", jsonVehicleType);
		          	  i.putExtra("riderhandicap", jsonHandicap); 
		          	  i.putExtra("requesttype", jsonRequestType);
		          	  i.putExtra("startlat", jsonstartLat); 
		          	  i.putExtra("startlong", jsonstartlon);
		          	  i.putExtra("tripid", jsonTripID);
		          	  
		          	  
		          	  
		          	  i.putExtra("queue", "queue");
					  i.putExtra("queue2", "2");
					  startActivity(i);
					  finish();
			  }
			  else
			  {
				  Utility.alertMessage(ShowNotification.this, pending_jsonMessage);
			  	}
			}
		
	/** Ride Detail function*/
	public void DetailParsing1() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDetailsByTripId");//Url_Address.url_UpdateDriverLocation;
			JSONObject json = new JSONObject();
			
//			json.put("Trigger", "CancelRide");
							
			Log.i("tag", "Getting TripID:"+tripID);
			
			json.put("TripId",tripID);
//			System.err.println("trigger:"+"Driver");
//			json.put("Trigger", "Driver");
		
			Log.d("tag", "Send Trip Id:"+json.toString());      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","RiderSide Start Arrive result-->>>>>    "+ jsonstr);
			 
			
				JSONObject obj=new JSONObject(jsonstr);
				pending_jsonResult=obj.getString("result");
				pending_jsonMessage=obj.getString("message");
				
				jsonRiderID=obj.getString("riderid");
				jsonSuggesstionFare=obj.getString("offered_fare");
				jsonETA=obj.getString("trip_time_est");
				
				jsonRiderName=obj.getString("rider_name");
				jsonDestination=obj.getString("ending_loc");
				json_riderRating=obj.getString("rider_rating");
				json_RiderImage=obj.getString("rider_img");
				jsonPickUpDate=obj.getString("trip_request_pickup_date");
				
				jsonHandicap=obj.getString("rider_handicap");
				jsonVehicleType=obj.getString("rider_prefer_vehicle");
				jsonRequestType=obj.getString("requestType");
				jsonEndLatitude=obj.getString("end_lat");
				jsonEndLongitude=obj.getString("end_lon");
				
				jsonDriverID=obj.getString("driverid");
				jsonDriverName=obj.getString("driver_name");
				jsondriveRating=obj.getString("driver_rating");
				jsonDriverImage=obj.getString("driver_img");
				
				jsonstartLat=obj.getString("start_lat");
				jsonstartlon=obj.getString("start_lon");
				jsonDistance=obj.getString("trip_miles_est");
				
				jsonTripID=tripID;
				
				
				Log.i("tag:", "Result: "+pending_jsonResult);
				Log.i("tag:", "Message :"+pending_jsonMessage);
				
			}
			}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
			}
	}
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		latitude = location.getLatitude();
        longitude = location.getLongitude();

	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onProviderDisabled(String provider) {
	}
	@Override
	public void onAttachedToWindow() {
	    super.onAttachedToWindow();
	  
	    View view = getWindow().getDecorView();
		WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
		lp.gravity = Gravity.CENTER;
		getWindowManager().updateViewLayout(view, lp);	
	
	}
	public void onDestroy(){
	    super.onDestroy();
	    if ( pDialog!=null && pDialog.isShowing() ){
	    	pDialog.cancel();
	    }
	   if(bol_mp=true){
		   mp = new MediaPlayer();
   	 		mp.stop();
	   }
	}
}


