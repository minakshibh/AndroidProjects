package com.rapidride;

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

import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ShowDialog extends Activity{
	TextView message;
	Button Ok;
	String active="",tripID="";
	ProgressDialog pDialog;
	    String pending_jsonResult="",pending_jsonMessage="";
	    String json_driverName="",jsonDestination="",jsonDistance="",
	    		jsonRequestType="",jsonDriverImage="",jsonTripID="",jsonDriverId="",jsonStart="",
	    		jsonSuggesstionFare="",jsonETA="",jsonActaulFare="",
	    		jsonRiderID="",jsonDriverRating="",jsonPickUpDate="",jsonRiderName="",
	    		json_riderRating="",json_RiderImage="",
	    		jsonHandicap="",jsonVehicleType="",jsonEndLatitude="",jsonEndLongitude="",jsonDriverID=""
	    		,jsonDriverName="",jsondriveRating="",jsonstartLat="",jsonstartlon="";
	    SharedPreferences prefs; 
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.showpayment_dialog);
	
	setFinishOnTouchOutside(false);
	prefs =getSharedPreferences("RapidRide", MODE_PRIVATE);

	
	Bundle extras = getIntent().getExtras();
	try{
	if (extras != null) {
		active = extras.getString("Active");
		tripID=extras.getString("TripID");
		Editor e=prefs.edit();
		e.putString("SplTripID", tripID);
		e.commit();
		Log.e("tag", "Active Show:"+active);
		Log.e("tag", "tripID Show in ShowDialog::"+active);
		
		if(active.equals("ride"))
		{
		message=(TextView)findViewById(R.id.textViewMessages);
		Ok=(Button)findViewById(R.id.buttonOks);
		
		message.setText("you have an active ride.");
		Ok.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		
			new GetDetailByTripID1().execute();
			}
		});
		}
	}
	else
	{
		message=(TextView)findViewById(R.id.textViewMessages);
		Ok=(Button)findViewById(R.id.buttonOks);
		
		message.setText("Please add credit card in your profile");
		Ok.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent i=new Intent(ShowDialog.this,Payment_Activity.class);
			startActivity(i);
			}
		});
		}
	
	}
	catch(Exception e)
	{
			
	}
	

	
}
@Override
public void onAttachedToWindow() {
    super.onAttachedToWindow();
    View view = getWindow().getDecorView();
	WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	lp.gravity = Gravity.CENTER;
	getWindowManager().updateViewLayout(view, lp);	
    }

private class GetDetailByTripID1 extends AsyncTask<Void, Void, Void> { 
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		pDialog = new ProgressDialog(ShowDialog.this);
		//pDialog.setTitle("Loading");
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
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
		  pDialog.dismiss();
		  if(pending_jsonResult.equals("0"))
		  {
				Intent i=new Intent(ShowDialog.this,RideInfoView_Activity.class);
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
				i.putExtra("startlat", jsonstartLat); 
	        	i.putExtra("startlong", jsonstartlon);
	        	
	        	Editor ed=prefs.edit();
	        	ed.putString("activetripid", "");
				ed.commit();
				startActivity(i);
				finish();
		  }
		  else
		  {
			  Utility.alertMessage(ShowDialog.this, pending_jsonMessage);
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

		Log.i("tag", "Getting TripID:"+prefs.getString("SplTripID", ""));
		json.put("TripId",prefs.getString("SplTripID", ""));
		
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
}
