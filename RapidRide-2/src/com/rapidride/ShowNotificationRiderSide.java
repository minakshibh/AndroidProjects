package com.rapidride;

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

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class ShowNotificationRiderSide extends Activity{
	ImageView imgdriver;
	TextView txtdriverName,txtmessage;
	String drivername="",driverImg="",messages="",tripID="";
	SharedPreferences prefs;
	Button btn_ok;
	RelativeLayout rel;
	TextView txtmsg;
	Button ok;
	int countdownNumber=7;
    ProgressDialog pDialog;
    String pending_jsonResult="",pending_jsonMessage="";
    String json_driverName="",jsonDestination="",jsonDistance="",
    		jsonRequestType="",jsonDriverImage="",jsonTripID="",jsonDriverId="",jsonStart="",
    		jsonSuggesstionFare="",jsonETA="",jsonActaulFare="",
    		jsonRiderID="",jsonDriverRating="",jsonPickUpDate="",jsonRiderName="",
    		json_riderRating="",json_RiderImage="",
    		jsonHandicap="",jsonVehicleType="",jsonEndLatitude="",jsonEndLongitude="",jsonDriverID=""
    		,jsonDriverName="",jsondriveRating="", jsonDestnationLatitude="",jsonDestinationLongitude="",jsonDestinationAddress="",
    		jsonStartingLongitude="",jsonStartingLatitude="",jsonStartingAddress="";
    		;
    String spicalTripID="";
    MediaPlayer mp;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.riderside_notification);
	
	setFinishOnTouchOutside(false);
	
	prefs=getSharedPreferences("RapidRide", MODE_PRIVATE);
	imgdriver=(ImageView)findViewById(R.id.imageViewRiderSide);
	txtdriverName=(TextView)findViewById(R.id.textViewBackground);
	txtmessage=(TextView)findViewById(R.id.textViewTimeLeft_leftside);
	rel=(RelativeLayout)findViewById(R.id.showMessage);
	txtmsg=(TextView)findViewById(R.id.textViewMessages);
	ok=(Button)findViewById(R.id.buttonOks);
	
	Bundle extras = getIntent().getExtras();
	try{
	if (extras != null) {
		drivername=extras.getString("DriverName");
		driverImg=extras.getString("DriverImage");
		messages=extras.getString("messages");
		tripID=extras.getString("tripID");
		Editor e=prefs.edit();
		e.putString("LastTimeUseTripId", tripID);
		e.commit();
		txtdriverName.setText(drivername);
		txtmessage.setText(messages);
	
        TimerTask countdownTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(countdownNumber == 0) {
                            cancel();
                            finish();
                        }
                         countdownNumber--;
                    }
                });
            }
        };

        Timer countdown = new Timer();
        countdown.schedule(countdownTask, 0, 1000);
		 
		
		if(!(driverImg==null))
	    {
		    GetXMLTask2 task = new GetXMLTask2();
			// Execute the task
			task.execute(new String[] {driverImg});
	    }
		
	
	}
	else{
		final String value=prefs.getString("OnlyMessage", "");
		
		if(value!="no")
		{
			
			txtdriverName.setVisibility(View.GONE);
			txtmessage.setVisibility(View.GONE);
			imgdriver.setVisibility(View.GONE);
			rel.setVisibility(View.VISIBLE);
			txtmsg.setText(value);
			Log.i("tag","check Arrived");
			boolean checkArrived=value.contains("arrived");
			boolean checkStart=value.contains("started");
			boolean checkWillReach=value.contains("will reach");
			if(checkArrived==true)
			{
				Log.i("tag","check Arrived yes");
				
				ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						new GetDetailByTripID().execute();
						rel.setVisibility(View.GONE);
						finish();
						}
				});
			}
			else if(checkStart==true){
				Log.i("tag","check Started yes");
				
				ok.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
						new GetDetailByTripID1().execute();
						rel.setVisibility(View.GONE);
						finish();
						}
				});
			}
			else if(checkWillReach==true)
			{
					ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						new GetDetailByTripID().execute();
						rel.setVisibility(View.GONE);
						finish();
						}
				});
			}
			else if(value.contains("cancelled"))
			{
				ok.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						rel.setVisibility(View.GONE);
						Intent i=new Intent(ShowNotificationRiderSide.this,MapView_Activity.class);
						finish();
						startActivity(i);
						
						}
				});
				
				}
			else{
				if(value.contains("Please rate"))
				{
					  mp = new MediaPlayer();
	        		  mp = MediaPlayer.create(this, R.raw.endride);
	        		  mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
	        		  mp.start();
					}
				ok.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
				boolean check=value.contains("Please rate");
					if(check==true)
						{
							new GetDetailByTripID2().execute();
//							Toast.makeText(getApplicationContext(), "Driver Rating", Toast.LENGTH_SHORT).show();
							}
					else
						{
							
							rel.setVisibility(View.GONE);
							finish();
							}
						}
				});
			}
				
//			b.show();
		}
	}
	}
	catch(Exception e)
	{
		}

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
		imgdriver.setImageBitmap(Bitmap.createScaledBitmap(result, 130, 120, false));
		}
	}	
}



private class GetDetailByTripID extends AsyncTask<Void, Void, Void> { 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
//		pDialog = new ProgressDialog(ShowNotificationRiderSide.this);
//		pDialog.setTitle("Loading");
//		pDialog.setMessage("Please wait...");
//		pDialog.setCancelable(false);
//		pDialog.show();
		}
	

	protected Void doInBackground(Void... arg0) {
		try {
			DetailParsing();
		
		} catch (Exception e) {
		 e.printStackTrace();
		}
		
		return null;
		}

	protected void onPostExecute(Void result) {
	super.onPostExecute(result);
//		  pDialog.dismiss();
		  if(pending_jsonResult.equals("0"))
		  {
		  Intent i=new Intent(ShowNotificationRiderSide.this,RiderQueueDetails_Activity.class);
		  i.putExtra("drivername", json_driverName);
		  i.putExtra("destination", jsonDestination);
		  i.putExtra("distance", jsonDistance);
		  i.putExtra("requesttype", jsonRequestType);
		  i.putExtra("driverimage", jsonDriverImage);
		  i.putExtra("tripid", jsonTripID);
		  i.putExtra("driverid", jsonDriverId);
		  i.putExtra("start", jsonStart);
		  i.putExtra("suggestion", jsonSuggesstionFare);
		  i.putExtra("eta", jsonETA);
		  i.putExtra("actual", jsonActaulFare);
		  i.putExtra("riderid", jsonRiderID);
		  i.putExtra("driverrating", jsonDriverRating);
		  i.putExtra("pickupdate", jsonPickUpDate);
		  startActivity(i);
		  finish();
		  }
		  else
		  {
			  Utility.alertMessage(ShowNotificationRiderSide.this, pending_jsonMessage);
		  		}
		}
	
/** Ride Detail function*/
public void DetailParsing() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 31000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDetailsByTripId");//Url_Address.url_UpdateDriverLocation;
		JSONObject json = new JSONObject();
		
//		json.put("Trigger", "CancelRide");
						
//		System.err.println("tripId:"+tripID);
		json.put("TripId", prefs.getString("LastTimeUseTripId", ""));
		
//		System.err.println("trigger:"+"Driver");
//		json.put("Trigger", "Driver");
	
		Log.d("tag", "Send Trip Id:"+json.toString());      
		httpost.setEntity(new StringEntity(json.toString()));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(httpost);
		HttpEntity resEntityGet = response.getEntity();
		String jsonstr=EntityUtils.toString(resEntityGet);
		if(jsonstr!=null)
		{
		 Log.e("tag","Microscope result-->>>>>    "+ jsonstr);
		 
			}
			JSONObject obj=new JSONObject(jsonstr);
			pending_jsonResult=obj.getString("result");
			pending_jsonMessage=obj.getString("message");
			
			json_driverName=obj.getString("driver_name");
			jsonDestination=obj.getString("ending_loc");
			jsonDistance=obj.getString("trip_miles_est");
			
			jsonRequestType=obj.getString("requestType");
			jsonDriverImage=obj.getString("driver_img");
			jsonTripID=tripID;
			
			jsonDriverId=obj.getString("driverid");
			jsonStart=obj.getString("starting_loc");
			jsonSuggesstionFare=obj.getString("offered_fare");
			
			
			jsonETA=obj.getString("trip_time_est");
			jsonActaulFare=obj.getString("offered_fare");
			jsonRiderID=obj.getString("riderid");
			
			
			jsonDriverRating=obj.getString("driver_rating");
			jsonPickUpDate=obj.getString("trip_request_pickup_date");
			
			Log.i("tag:", "Result: "+pending_jsonResult);
			Log.i("tag:", "Message :"+pending_jsonMessage);

		}
		  catch(Exception e){
		   System.out.println(e);
		   Log.d("tag", "Error :"+e); }  
		}
}

private class GetDetailByTripID1 extends AsyncTask<Void, Void, Void> { 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
//		pDialog = new ProgressDialog(ShowNotificationRiderSide.this);
//		pDialog.setTitle("Loading");
//		pDialog.setMessage("Please wait...");
//		pDialog.setCancelable(false);
//		pDialog.show();
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
//		  pDialog.dismiss();
		  if(pending_jsonResult.equals("0"))
		  {
				
			  Editor ed=prefs.edit();
			  ed.putString("riv_tripid", prefs.getString("LastTimeUseTripId", ""));
			  ed.putString("riv_driverid", jsonDriverID);
			  ed.commit();
			  
			  
			  	Intent i=new Intent(ShowNotificationRiderSide.this,RideInfoView_Activity.class);
				i.putExtra("value", "rider");
				i.putExtra("riderid",jsonRiderID );
				i.putExtra("tripid",jsonTripID );
				i.putExtra("suggestionfare",jsonSuggesstionFare);
				i.putExtra("eta", jsonETA);
				i.putExtra("ridername",jsonRiderName);
				i.putExtra("destination",jsonDestination);
				i.putExtra("riderrating",json_riderRating);
				i.putExtra("pickup",jsonPickUpDate);
				i.putExtra("riderimage",json_RiderImage);
				
				i.putExtra("riderhandicap",jsonHandicap);
				i.putExtra("vehicletype",jsonVehicleType);
				i.putExtra("requesttype",jsonRequestType);
				
				i.putExtra("endlati",jsonEndLatitude);
				i.putExtra("endlong",jsonEndLongitude);
				
				i.putExtra("driverid",jsonDriverID);
				i.putExtra("drivername",jsonDriverName);
				i.putExtra("driverrating",jsondriveRating);
				i.putExtra("driverimage",jsonDriverImage);
				
				
				startActivity(i);
				finish();
		  }
		  else
		  {
			  Utility.alertMessage(ShowNotificationRiderSide.this, pending_jsonMessage);
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
		
//		json.put("Trigger", "CancelRide");
						
		Log.i("tag", "Getting TripID:"+prefs.getString("LastTimeUseTripId", ""));
		
		json.put("TripId",prefs.getString("LastTimeUseTripId", ""));
//		System.err.println("trigger:"+"Driver");
//		json.put("Trigger", "Driver");
	
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

		}
	

	protected Void doInBackground(Void... arg0) {
		try {
			DetailParsing2();
		
		} catch (Exception e) {
		 e.printStackTrace();
		}
		
		return null;
		}

	protected void onPostExecute(Void result) {
	super.onPostExecute(result);

		  if(pending_jsonResult.equals("0"))
		  {
			Intent i=new Intent(ShowNotificationRiderSide.this,DriverRating_Activity.class);
			i.putExtra("value","rider");
			i.putExtra("fare", jsonSuggesstionFare);
			i.putExtra("riderid", jsonRiderID);
			
			i.putExtra("tripid", jsonTripID);
			i.putExtra("driverid", jsonDriverID);
			i.putExtra("vehicleType", jsonVehicleType);
			i.putExtra("DesLong", jsonDestinationLongitude);
			i.putExtra("DesLati", jsonDestnationLatitude);
			i.putExtra("DesAddress", jsonDestinationAddress);
			
			i.putExtra("CusLong", jsonStartingLongitude);
			i.putExtra("CusLati", jsonStartingLatitude);
			i.putExtra("CusAddress", jsonStartingAddress);
			
			Log.i("tag","Sending Fare:"+jsonSuggesstionFare);
			Log.i("tag","Sending riderid:"+jsonRiderID);
			Log.i("tag","Sending tripid:"+jsonTripID);
			Log.i("tag","Sending jsonDriverID:"+jsonDriverID);
			Log.i("tag","Sending jsonVehicleType:"+jsonVehicleType);
			
			Editor e=prefs.edit();
			e.putString("SplTripID", "");
			e.commit();
			startActivity(i);
			finish();
		  }
		  else
		  {
			  Utility.alertMessage(ShowNotificationRiderSide.this, pending_jsonMessage);
		  	}
		}
	
/** Ride Detail function*/
public void DetailParsing2() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 60000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 61000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDetailsByTripId");//Url_Address.url_UpdateDriverLocation;
		JSONObject json = new JSONObject();
		
		spicalTripID=prefs.getString("SplTripID", "");
		Log.i("tag", "Show Notify: "+spicalTripID);
		if(spicalTripID=="" || spicalTripID==null )
		{
		
		json.put("TripId", prefs.getString("LastTimeUseTripId", ""));
			Log.i("tag", "Show LastTimeUseTripId Notify: "+spicalTripID);
		}
		else{
			json.put("TripId", spicalTripID.trim());
			Log.i("tag", "Show spicalTripID Notify: "+spicalTripID);
		}
		Log.d("tag", "Send Trip Id  In Driver Rating.... :"+json.toString());      
		httpost.setEntity(new StringEntity(json.toString()));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(httpost);
		HttpEntity resEntityGet = response.getEntity();
		String jsonstr=EntityUtils.toString(resEntityGet);
		if(jsonstr!=null)
		{
		 Log.e("tag","Microscope result-->>>>>    "+ jsonstr);
		 
			}
			JSONObject obj=new JSONObject(jsonstr);
			pending_jsonResult=obj.getString("result");
			pending_jsonMessage=obj.getString("message");
			
			jsonSuggesstionFare=obj.getString("offered_fare");
			jsonRiderID=obj.getString("riderid");
			jsonTripID=prefs.getString("LastTimeUseTripId", "");
			jsonDriverID=obj.getString("driverid");
			jsonVehicleType=obj.getString("rider_prefer_vehicle");
			jsonDestinationLongitude=obj.getString("end_lon");
			jsonDestnationLatitude=obj.getString("end_lat");
			jsonDestinationAddress=obj.getString("ending_loc");
			jsonStartingLongitude=obj.getString("start_lon");
			jsonStartingLatitude=obj.getString("start_lat");
			jsonStartingAddress=obj.getString("starting_loc");
			
	
		}
		  catch(Exception e){
		   System.out.println(e);
		   Log.d("tag", "Error :"+e); }  
		}
}


@Override
public void onAttachedToWindow() {
    super.onAttachedToWindow();
    Log.d("tag", "TripID:"+tripID);
    if(tripID=="" || tripID==null)
    {
    View view = getWindow().getDecorView();
	WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	lp.gravity = Gravity.CENTER;
	getWindowManager().updateViewLayout(view, lp);	
    }
    else
    {
    View view = getWindow().getDecorView();
    WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
    lp.gravity = Gravity.BOTTOM;
    getWindowManager().updateViewLayout(view, lp);	
    }
}
public void onDestroy() {
	mp = new MediaPlayer();
    mp.stop();
    super.onDestroy();

}

}
