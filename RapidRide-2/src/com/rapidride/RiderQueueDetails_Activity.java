package com.rapidride;


import java.text.DecimalFormat;
import java.util.Timer;

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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RiderQueueDetails_Activity extends Activity{
	Button btn_cancel,btn_active,btn_test;
	TextView tv_drivername,tv_endaddress,tv_requesttype,tv_pickdate,
	tv_distance,tv_eta,tv_startaddress,tv_actualfare,tv_suggestionfare,tv_start,
	tv_model,tv_year,tv_color;
	Typeface typeFace;
	SharedPreferences prefs;
	ImageView iv_driver,imgv_textmessage,iv_callicon,iv_findlocation;
	LinearLayout ll_vehicleimg;
	Timer timer;
	
	String urldriverimage,str_tripid="",newValue_distance="",str_urlvehicleimg;
	String jsonResult,jsonMessage,jsonDriverID,jsonlongitude,jsonlatitude,
	jsondriver_first,jsondriver_last,jsonlast_updated,jsondriver_image,
	jsonvehicle_class,json,jsontripid,jsonphone_number,jsonstr;
	String distance="";
	String actualFare="",newValue_actual="";
	
	protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.riderqueuedetails_activity);
	
	stopTimertask();
	typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);  
	
	iv_driver=(ImageView)findViewById(R.id.imageView_rider);
	tv_drivername=(TextView)findViewById(R.id.textView_drivername);
	tv_endaddress=(TextView)findViewById(R.id.textView_endingaddress);
	tv_start=(TextView)findViewById(R.id.textView_startaddress);
	tv_actualfare=(TextView)findViewById(R.id.textView_actualfare);
	tv_suggestionfare=(TextView)findViewById(R.id.textView_suggestionfare);
	tv_pickdate=(TextView)findViewById(R.id.textView_distance);
	tv_eta=(TextView)findViewById(R.id.textView_eta);
	
	tv_color=(TextView)findViewById(R.id.textView_vehiclecolor);
	tv_year=(TextView)findViewById(R.id.textView_vehicleyear);
	tv_model=(TextView)findViewById(R.id.textView_vehiclemodel);
	
	imgv_textmessage=(ImageView)findViewById(R.id.textView_textmessage);
	iv_callicon=(ImageView)findViewById(R.id.imageView_callicon);
	iv_findlocation=(ImageView)findViewById(R.id.imageView_findlocation);
	
	ll_vehicleimg=(LinearLayout)findViewById(R.id.layout_vehicleimage);
	
	tv_color.setText(""+getIntent().getStringExtra("vehiclecolor"));
	tv_year.setText(""+getIntent().getStringExtra("vehicleyear"));
	tv_model.setText(""+getIntent().getStringExtra("vehiclename"));
	tv_start.setText(""+getIntent().getStringExtra("start"));
	tv_endaddress.setText(""+getIntent().getStringExtra("destination"));

	tv_drivername.setText(""+getIntent().getStringExtra("drivername"));

	
	distance=getIntent().getStringExtra("distance").replace(" ", "");
	System.err.println("distance="+distance+"eta="+getIntent().getStringExtra("eta").replace("mins", ""));

		
	if(distance==null)
	{}
	else{
		try{ 
		float float_dis=Float.parseFloat(distance);
		  newValue_distance=new DecimalFormat("##.##").format(float_dis);
		  
		
		}
		catch(Exception e)
		{}
		}
	
	tv_pickdate.setText("DISTANCE: "+newValue_distance+" Miles");

	
	  actualFare=getIntent().getStringExtra("actual");
	  float float_dis1=Float.parseFloat(actualFare);
	  newValue_actual=new DecimalFormat("##.##").format(float_dis1);

	
	tv_actualfare.setText("ACTUAL FARE: $"+newValue_actual);
	tv_suggestionfare.setText("SUGGESTED FARE:  $"+getIntent().getStringExtra("suggestion"));
	tv_eta.setText("ETA: "+getIntent().getStringExtra("eta").replace("mins", "")+" mins");
	
	str_tripid=getIntent().getStringExtra("tripid");
	
	urldriverimage=getIntent().getStringExtra("driverimage");
	
	if(urldriverimage!=null){
		urldriverimage=getIntent().getStringExtra("driverimage");
		System.err.println("driverimage "+urldriverimage);
		// for profile images
		if(Utility.isConnectingToInternet(RiderQueueDetails_Activity.this))
	    	{
			/***Get image*****/
			new GetXMLTask().execute(new String[] {urldriverimage });
	    		}
	    	else{
	    		Utility.alertMessage(RiderQueueDetails_Activity.this,"error in internet connection");
	    	}
		
		}

	str_urlvehicleimg=getIntent().getStringExtra("vehicleimage");
	System.err.println("vehicleimage======="+str_urlvehicleimg);
	if(str_urlvehicleimg!=null){
		urldriverimage=getIntent().getStringExtra("vehicleimage");
		System.err.println("vehicleimage="+str_urlvehicleimg);
		// for profile images
		if(Utility.isConnectingToInternet(RiderQueueDetails_Activity.this))
	    	{
			/***Get image*****/
			new GetXMLTask_Vehicle().execute(new String[] {str_urlvehicleimg });
	    		}
	    	else{
	    		Utility.alertMessage(RiderQueueDetails_Activity.this,"error in internet connection");
	    	}
		
		}
	
	/**Code change****/
	//hide image button
	if(getIntent().getStringExtra("status")==null)
	{}
	else{
		if(getIntent().getStringExtra("status").equalsIgnoreCase("Accepted"))
		{
			iv_findlocation.setVisibility(View.VISIBLE);
			}
		else
		{
			iv_findlocation.setVisibility(View.INVISIBLE);	
			}
		}



	btn_cancel=(Button)findViewById(R.id.button_cancel);
	btn_cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(RiderQueueDetails_Activity.this);
		//	alertDialog.setTitle();
		  alertDialog.setMessage("Do You Want to Cancel Ride. Cancelation charges may apply?");
		     alertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog,int which) {
		                   // Write your code here to execute after dialog
		            	   if(Utility.isConnectingToInternet(RiderQueueDetails_Activity.this))
		          	    	{
		       				/***Get cancel ride class*****/
		       				new httpCancelRide().execute();
		          	    		}
		          	    	else{
		          	    		Utility.alertMessage(RiderQueueDetails_Activity.this,"error in internet connection");
		          	    	}
			            	 }});
		     alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
	               public void onClick(DialogInterface dialog,int which) {
	                   // Write your code here to execute after dialog
	           
		            	 }});
		   			   alertDialog.show();	
					}
	});
	imgv_textmessage.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			smsIntent.setType("vnd.android-dir/mms-sms");
			smsIntent.putExtra("address", "619-600-5080");/**sms number**/
			smsIntent.putExtra("sms_body","");
			startActivity(smsIntent);
				
			}
		});
		iv_callicon.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:619-600-5080"));/**call number**/
			startActivity(callIntent);
				
			}
		});
		
		iv_findlocation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			
				if(Utility.isConnectingToInternet(RiderQueueDetails_Activity.this))
	   	    	{
					/***Get driver location class*****/
					new httpGetDriverLocation().execute();
	   	    		}
	   	    	else{
	   	    		Utility.alertMessage(RiderQueueDetails_Activity.this,"error in internet connection");
	   	    	}
				
			}
		});
}
	
	
	/********************************main function end***********************/
	
		
	
	/**image upload***/
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
	    	if(result!=null)
	    	{
		    	iv_driver.setImageBitmap(Bitmap.createScaledBitmap(result, 100, 100, false));
		    	}
	        }
	 
	}
	
	/**image upload***/
	private class GetXMLTask_Vehicle extends AsyncTask<String, Void, Bitmap> {
	        @Override
	    protected Bitmap doInBackground(String... urls) {
	            Bitmap map = null;
	            for (String url : urls) {
	                map = ImageDownloader.downloadImage(url);
	            }
	            return map;
	        }

	    protected void onPostExecute(Bitmap result) {
	    	if(result!=null)
	    	{
	    		//Bitmap newresult =Bitmap.createScaledBitmap(result, 320, 120, false);
	    		BitmapDrawable ob = new BitmapDrawable(getResources(), result);
	    		
	    		ll_vehicleimg.setBackgroundDrawable(ob);
	    	}
	    	
	        }
	 
	}
	/**cancel Ride web services class */
	private class httpCancelRide extends AsyncTask<Void, Void, Void> { 
			ProgressDialog pDialog;
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(RiderQueueDetails_Activity.this);
				//pDialog.setTitle("Loading");
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.show();
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
				  Intent i=new Intent(RiderQueueDetails_Activity.this,MapView_Activity.class);
				  //i.putExtra("value", "rider");
				  finish();
				  startActivity(i);
					 }
			
/**cancel Ride function*/
		public void cancelRideParsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 31000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/CancelRide");//Url_Address.url_UpdateDriverLocation;
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "CancelRide");
								
				System.err.println("tripId:"+str_tripid);
				json.put("TripId", str_tripid);
				
				System.err.println("trigger:"+"Rider");
				json.put("Trigger", "Rider");
				
				System.err.println("Reason:"+"");
				json.put("Reason", "");
														
				System.err.println("Latitude:"+"");
				json.put("Latitude", "");
				
				System.err.println("Longitude:"+"");
				json.put("Longitude", "");
				      
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
				String	jsonResult=obj.getString("result");
				String	jsonMessage=obj.getString("message");
			
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
			//ProgressDialog pDialog1;
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
//				pDialog1 = new ProgressDialog(RiderQueueDetails_Activity.this);
//				pDialog1.setTitle("Loading");
//				pDialog1.setMessage("Please wait...");
//				pDialog1.setCancelable(false);
//				pDialog1.show();
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
				Editor ed=prefs.edit();
				ed.putString("vehicletype2", getIntent().getStringExtra("vehicletype"));
				ed.putString("tripid2",str_tripid);
				ed.putString("driverid2",getIntent().getStringExtra("driverid"));
				ed.commit();
				  
		   Intent i=new Intent(RiderQueueDetails_Activity.this,GetDriverLocation.class);
				  i.putExtra("tripid", str_tripid);
				  i.putExtra("jsonlatitude", jsonlatitude);
				  i.putExtra("jsonlongitude", jsonlongitude);
				  i.putExtra("driverid", getIntent().getStringExtra("driverid"));
				  i.putExtra("vehicletype", getIntent().getStringExtra("vehicletype"));
				  finish();
				  startActivity(i);
				  
			}
			
/**GetDriverLocation function*/
		public void GetDriverLocation() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 30000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 31000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/GetDriverLocation");//Url_Address.url_UpdateDriverLocation;
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "GetDriverLocation");
								
				Log.d("tag","DriverId:"+getIntent().getStringExtra("driverid"));
				json.put("DriverId", getIntent().getStringExtra("driverid"));
				
				
				Log.d("tag","TripId:"+str_tripid);
				json.put("TripId", str_tripid);
			
				      
				httpost.setEntity(new StringEntity(json.toString()));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				
				HttpResponse response = client.execute(httpost);
				HttpEntity resEntityGet = response.getEntity();
				jsonstr=EntityUtils.toString(resEntityGet);
				
				if(jsonstr!=null)
				{
				 Log.e("tag","GetDriverLocation result-->>>>>    "+ jsonstr);
				 
				
				
				JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
				
				jsonDriverID=obj.getString("driverId");
				jsonlongitude=obj.getString("longitude");
				jsonlatitude=obj.getString("latitude");
				
				Log.i("tag", "Driver ID:"+jsonDriverID +" longitude"+jsonlongitude+" latitude"+jsonlatitude);
				jsondriver_first=obj.getString("driver_first");

				jsondriver_last=obj.getString("driver_last");
				jsonlast_updated=obj.getString("last_updated");
				
				jsondriver_image=obj.getString("driver_image");
				jsonvehicle_class=obj.getString("vehicle_class");
				
				jsontripid=obj.getString("tripid");
				jsonphone_number=obj.getString("phone_number");
				
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+jsonMessage);
		
				}
				}
				  catch(Exception e){
				   System.out.println(e);
				   Log.d("tag", "Error :"+e); }  
					}
				}
	public void stopTimertask() {
	    if (timer != null) {
	        timer.cancel();
	        timer = null;
	    }}
}