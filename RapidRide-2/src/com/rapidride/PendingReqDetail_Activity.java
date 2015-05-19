package com.rapidride;

import java.text.DecimalFormat;
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
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PendingReqDetail_Activity extends Activity implements LocationListener{
	TextView tv_drivername,tv_endaddress,tv_requesttype,tv_pickdate,
	tv_distance,tv_eta,tv_startaddress,tv_actualfare,tv_suggestionfare;
	ImageView imgv_textmessage;
	Button btn_Accept,btn_reject,btn_active,btn_cancel;
	public ProgressDialog pDialog;
	LinearLayout rl_requestview,rl_queueview;
	ImageView iv_rider,iv_callicon,iv_handicap;
	
	String status=null,tripid=null,jsonResult="",pending_jsonMessage,newValue_disatnce="",url_riderimage,provider,str_cancelride;
	String accept_jsonResult;
	double latitude=0 ,longitude=0;
	
	Typeface typeFace;
	SharedPreferences prefs;
	LocationManager location_manager;
	Location location;
	Timer timer;
	TimerTask timerTask;
	final Handler handler = new Handler();
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pendingreqdetail_activity);
		
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
				
		tv_drivername=(TextView)findViewById(R.id.textView_drivername);
		tv_endaddress=(TextView)findViewById(R.id.textView_endingaddress);
		//tv_requesttype=(TextView)findViewById(R.id.textView_requesttype);
		//tv_pickdate	=(TextView)findViewById(R.id.textView_pickdate);
		tv_distance=(TextView)findViewById(R.id.textView_distance);
		tv_startaddress=(TextView)findViewById(R.id.textView_startaddress);
		tv_actualfare=(TextView)findViewById(R.id.textView_actualfare);
		tv_suggestionfare=(TextView)findViewById(R.id.textView_suggestionfare);
		tv_eta=(TextView)findViewById(R.id.textView_eta);
		imgv_textmessage=(ImageView)findViewById(R.id.textView_textmessage);
		
		iv_rider=(ImageView)findViewById(R.id.imageView_rider);
		iv_callicon=(ImageView)findViewById(R.id.imageView_callicon);
		rl_requestview=(LinearLayout)findViewById(R.id.llayout_request);
		rl_queueview=(LinearLayout)findViewById(R.id.llayout_queue);
		tv_drivername.setText(getIntent().getStringExtra("ridername"));
		tv_endaddress.setText(getIntent().getStringExtra("destination"));
		//tv_requesttype.setText("Request Type: "+getIntent().getStringExtra("requesttype"));
	//	tv_pickdate.setText("Pickup Date: "+getIntent().getStringExtra("pickupdate"));
		
		//String distance=getIntent().getStringExtra("distance");
		
		
			try
			{
				float float_dis=Float.parseFloat(getIntent().getStringExtra("distance"));
				newValue_disatnce=new DecimalFormat("##.##").format(float_dis);
				tv_distance.setText("DISTANCE: "+newValue_disatnce+" Miles");
				
				float act_fare=Float.parseFloat(getIntent().getStringExtra("actualfare"));
				String new_Act_fare=new DecimalFormat("##.##").format(act_fare);
				tv_actualfare.setText("ACTUAL FARE :$"+new_Act_fare); 
			}
			catch(Exception e)
			{
				System.err.println("distance Exception"+e);
			}
			
		
		tv_startaddress.setText(getIntent().getStringExtra("start"));
		tv_suggestionfare.setText("SUGGESTED FARE :$"+getIntent().getStringExtra("suggestionfare"));
		tv_eta.setText("ETA :"+getIntent().getStringExtra("eta").replace("mins", "")+" mins");
		
		tripid=getIntent().getStringExtra("tripid");
		url_riderimage=getIntent().getStringExtra("riderimage");
		System.err.println("getintent=="+getIntent().getStringExtra("request"));
		
		if(!(getIntent().getStringExtra("riderimage")==null)){
			url_riderimage=getIntent().getStringExtra("riderimage");
			// for profile images
		//	GetXMLTask task = ;
			// Execute the task
			if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
   	    	{
   				/**call httpAcceptRequest class**/
				new GetXMLTask().execute(new String[] {url_riderimage });
   	    		}
   	    	else{
   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
   	    		}
			
			}
		
		/** show rider handicap*/
		iv_handicap=(ImageView)findViewById(R.id.imageView_handicap);
		String handicap=getIntent().getStringExtra("riderhandicap");
		System.err.println("handicap "+handicap);
		if(!(handicap==null))
		{	/** if rider handicap*/
			if(handicap.equals("1"))
			{
				iv_handicap.setVisibility(View.VISIBLE);
				}
			/** if rider not handicap*/
			else
			{
				iv_handicap.setVisibility(View.INVISIBLE);
				}
		}
	
		/** show if request view */
		if(!(getIntent().getStringExtra("request1")==null))
		{
			rl_requestview.setVisibility(View.VISIBLE);
			rl_queueview.setVisibility(View.GONE);
			}
		/**show if queue view*/
		else if(!(getIntent().getStringExtra("queue2")==null))
		{
			rl_requestview.setVisibility(View.GONE);
			rl_queueview.setVisibility(View.VISIBLE);
			imgv_textmessage.setVisibility(View.INVISIBLE);
			iv_callicon.setVisibility(View.INVISIBLE);
			}
		
		location_manager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
		   Criteria c=new Criteria();
		   provider=location_manager.getBestProvider(c, false);
		   //now you have best provider
		   //get location
		   location=location_manager.getLastKnownLocation(provider);
		   if(location!=null)
		   {
		     //get latitude and longitude of the location
			   onLocationChanged(location);
		  //   System.err.println("latitude  "+latitude+"  longitude  "+longitude);
		       }
		   else
		   {
		    System.err.println("No Provider");
		       }
		  
		
		btn_Accept=(Button)findViewById(R.id.button_accept);
		btn_Accept.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					status="Accepted";
					if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
		   	    	{
		   				/**call httpAcceptRequest class**/
						new httpAcceptRequest().execute();
		   	    		}
		   	    	else{
		   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
		   	    	}
				
			}
		});
		btn_reject=(Button)findViewById(R.id.button_reject);
		btn_reject.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 status="Rejected";
          	  
				 if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
		   	    	{
		   				/**call httpAcceptRequest class**/
						new httpAcceptRequest().execute();
		   	    		}
		   	    	else{
		   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
		   	    	}
				
			}
		});
		btn_active=(Button)findViewById(R.id.button_activate);
		btn_active.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
	   	    	{
					
					/**call httpAcceptRequest class**/
					new httpUpdateDriverLocation().execute();
	   	    		}
	   	    	else{
	   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
	   	    	}
          	 		
			}
		});
		btn_cancel=(Button)findViewById(R.id.button_cancel);
		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				  final Dialog dialog = new Dialog(PendingReqDetail_Activity.this);
	                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	                dialog.setContentView(R.layout.cancel_ride_layout);
	              //  dialog.setTitle("Cancel Ride");
	                dialog.setCancelable(false);
					Button Ridercancelleddonotcharge=(Button)dialog.findViewById(R.id.button_Ridercancelleddonotcharge);
					Ridercancelleddonotcharge.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
						if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
				   	    	{
				   				/**call http cancel class**/
							str_cancelride="rider cancelled donot charge";
								new httpCancelRide().execute();
				   	    		}
				   	    	else{
				   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
				   	    	}}
							
						});
						Button Sendtoanotherdriver=(Button)dialog.findViewById(R.id.button_Sendtoanotherdriver);
						Sendtoanotherdriver.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();
								if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
					   	    	{
					   				/**call cancel class**/
									str_cancelride="send to another driver";
									new httpCancelRide().execute();
					   	    		}
					   	    	else{
					   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
					   	    	}
							
							
							}
						});
						Button Cancelride=(Button)dialog.findViewById(R.id.button_Cancelride);
						Cancelride.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								dialog.dismiss();	
							
								if(Utility.isConnectingToInternet(PendingReqDetail_Activity.this))
					   	    	{
					   				/**call cancel class**/
									str_cancelride="cancel ride";
									new httpCancelRide().execute();
					   	    		}
					   	    	else{
					   	    		Utility.alertMessage(PendingReqDetail_Activity.this,"error in internet connection");
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
}
	/**accept or reject web services */
	private class httpAcceptRequest extends AsyncTask<Void, Void, Void> { 
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(PendingReqDetail_Activity.this);
				//pDialog.setTitle("Loading");
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.show();
				}
			
			@Override
			protected Void doInBackground(Void... arg0) {
			
				try {
				 Acceptparsing();
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
				System.err.println("go out");
				System.err.println("accept_jsonResult"+accept_jsonResult);
				if(accept_jsonResult.equals("0"))
				{
					if(getIntent().getStringExtra("requesttype")!=null)
					{
						if(getIntent().getStringExtra("requesttype").equalsIgnoreCase("Now"))
						{
							new  httpUpdateDriverLocation().execute();
							System.err.println("go to not");
							}
						
					else
					{
						Intent i=new Intent(PendingReqDetail_Activity.this,PendingRequests_Activity.class);
						finish();
						startActivity(i);
						System.err.println("go to");
						}
					}
				}
				}
				/**pending request code parsing function **/
			public void Acceptparsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/AcceptOrRejectRequest");//Url_Address.url_promocode);
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "AcceptOrRejectRequest");
				System.err.println("tripid="+tripid);
				json.put("TripId", tripid);
			
			if(status.equals("Accepted"))
			{
				json.put("Status", "Accepted");
				System.err.println("status="+status);
				}
			else
			{
				System.err.println("status="+status);
				json.put("Status", "Rejected");
			}
			
			      //	      
				httpost.setEntity(new StringEntity(json.toString()));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				
				HttpResponse response = client.execute(httpost);
				HttpEntity resEntityGet = response.getEntity();
				String jsonstr=EntityUtils.toString(resEntityGet);
				if(jsonstr!=null)
				{
				 Log.e("tag","AcceptOrRejectRequests result-->>>>>    "+ jsonstr);
				 
					}
					JSONObject obj=new JSONObject(jsonstr);
					accept_jsonResult=obj.getString("result");
					pending_jsonMessage=obj.getString("message");
			
					Log.i("tag:", "Result: "+accept_jsonResult);
					Log.i("tag:", "Message :"+pending_jsonMessage);
		
				}
				  catch(Exception e){
				   System.out.println(e);
				   Log.d("tag", "Error :"+e); }  
					}
				}
	//images download 
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
		    	iv_rider.setImageBitmap(Bitmap.createScaledBitmap(result, 80, 80, false));
		    	}
		    	   }
		 
		}
		/**Update Driver Location web services */
		private class httpUpdateDriverLocation extends AsyncTask<Void, Void, Void> { 
				@Override
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					pDialog = new ProgressDialog(PendingReqDetail_Activity.this);
					//pDialog.setTitle("Loading");
					pDialog.setMessage("Please wait...");
					pDialog.setCancelable(false);
					pDialog.show();
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
					  pDialog.dismiss();
					  
					  Intent i=new Intent(PendingReqDetail_Activity.this,RideInfoView_Activity.class);
					  i.putExtra("value", "driver");
		          	  i.putExtra("ridername", getIntent().getStringExtra("ridername"));
		          	  i.putExtra("riderid", getIntent().getStringExtra("riderid"));
		          	  i.putExtra("destination", getIntent().getStringExtra("destination"));
		          	  i.putExtra("start", getIntent().getStringExtra("start"));
		          	  i.putExtra("pickup",getIntent().getStringExtra("pickupdate"));
		          	  i.putExtra("riderimage",getIntent().getStringExtra("riderimage"));
		          	  i.putExtra("suggestionfare",getIntent().getStringExtra("suggestionfare"));
		          	  i.putExtra("eta", getIntent().getStringExtra("eta"));
		          	  i.putExtra("riderrating", getIntent().getStringExtra("riderrating"));
		          	  i.putExtra("tripid", getIntent().getStringExtra("tripid"));
		          	  i.putExtra("endlati", getIntent().getStringExtra("endlati"));
		          	  i.putExtra("endlong", getIntent().getStringExtra("endlong"));
		          	  i.putExtra("distance", newValue_disatnce);
		          	  i.putExtra("startlat", getIntent().getStringExtra("startlat"));
		          	  i.putExtra("startlong", getIntent().getStringExtra("startlong"));
		          	  i.putExtra("vehicletype", getIntent().getStringExtra("vehicletype"));
		          	  i.putExtra("riderhandicap",  getIntent().getStringExtra("riderhandicap")); 
		          	  i.putExtra("requesttype",  getIntent().getStringExtra("requesttype"));
		          	  
		          	startActivity(i);
			
		          	
					
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
					
					System.err.println("trigger:"+tripid);
					json.put("trigger", tripid);
									      
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
						jsonResult=obj.getString("result");
						pending_jsonMessage=obj.getString("message");
				
						Log.i("tag:", "Result: "+jsonResult);
						Log.i("tag:", "Message :"+pending_jsonMessage);
			
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
					pDialog = new ProgressDialog(PendingReqDetail_Activity.this);
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
					  Intent i=new Intent(PendingReqDetail_Activity.this,DriverRating_Activity.class);
					  i.putExtra("cancel", "cancel");
					  i.putExtra("value", "driver");
					  i.putExtra("ridername", getIntent().getStringExtra("ridername"));
		          	  i.putExtra("riderid", getIntent().getStringExtra("riderid"));
		          	  i.putExtra("destination", getIntent().getStringExtra("destination"));
		          	  i.putExtra("start", getIntent().getStringExtra("start"));
		          	  i.putExtra("pickup",getIntent().getStringExtra("pickupdate"));
		          	  i.putExtra("riderimage",getIntent().getStringExtra("riderimage"));
		          	  i.putExtra("suggestionfare",getIntent().getStringExtra("suggestionfare"));
		          	  i.putExtra("eta", getIntent().getStringExtra("eta"));
		          	  i.putExtra("riderrating", getIntent().getStringExtra("riderrating"));
		          	  i.putExtra("tripid", getIntent().getStringExtra("tripid"));
		          	  i.putExtra("endlati", getIntent().getStringExtra("endlati"));
		          	  i.putExtra("endlong", getIntent().getStringExtra("endlong"));
		          	  i.putExtra("vehicletype", getIntent().getStringExtra("vehicletype"));
		          	  i.putExtra("riderhandicap",  getIntent().getStringExtra("riderhandicap")); 
		          	  i.putExtra("requesttype",  getIntent().getStringExtra("requesttype"));
		          	  finish();
					  startActivity(i);
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
					
//					json.put("Trigger", "CancelRide");
									
					System.err.println("tripId:"+tripid);
					json.put("TripId", tripid);
					//json.put("TripId", "1689");
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
						jsonResult=obj.getString("result");
						pending_jsonMessage=obj.getString("message");
				
						Log.i("tag:", "Result: "+jsonResult);
						Log.i("tag:", "Message :"+pending_jsonMessage);
			
					}
					  catch(Exception e){
					   System.out.println(e);
					   Log.d("tag", "Error :"+e); }  
						}
					}
		@Override
		public void onLocationChanged(Location location) {
			longitude=location.getLongitude();
			latitude=location.getLatitude();
			
			}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		public void onProviderEnabled(String provider) {
		
			}
		public void onProviderDisabled(String provider) {
			}
		   public void stoptimertask() {
		        /**stop the timer, if it's not already null */
		        if (timer != null) {
		            timer.cancel();
		            timer = null;
		        }}
		   @Override
		public void onBackPressed() {
			/**********nothing***********/
			super.onBackPressed();
		}
}