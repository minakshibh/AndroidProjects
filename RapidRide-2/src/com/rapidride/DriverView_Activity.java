package com.rapidride;


import java.util.ArrayList;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class DriverView_Activity extends Activity implements LocationListener {
	
	private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
	private GoogleMap googleMap;
	private Button btn_pendingrequest,btn_query,btn_viewrides,btn_help,btn_payment;
	private LinearLayout flyoutDrawerRl;
	private DrawerLayout mDrawerLayout;
	private ImageView iv_menu,imgbtn_driverpic;
	private ActionBarDrawerToggle mDrawerToggle;
//	RelativeLayout tbtn_ridermode;
//	private ToggleButton tbtn_ridermode;
	private TextView tv_requestcount;
	public ProgressDialog pDialog,pDialog2,progressbar;
	ImageView ImageDriver,im_view;
	TextView timeLeft;
	MySwitch publishToggle;
	
	Typeface typeFace;
	SharedPreferences prefs;
	LocationManager  locationManager ;
	JSONObject json;
	ArrayList<String> arraylist_pendingrequest=new ArrayList<String>();
	
	
	Timer timer,timer_SwithModeActive;
	TimerTask timerTask,timerTask_SwitchModeActive;
	final Handler handler = new Handler();
	final Handler handler_SwitchModeActive = new Handler();
	
	double latitude,longitude;
	int flag=0,countdownNumber = 15,exception=0;;
	String regId,resp,TripId="",SpiltImage="",status="", alert="",message="",checkAlert="";
	
	


	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driverview_activity);
		
		
		regId = GCMRegistrar.getRegistrationId(this);
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
		
		
		
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		flyoutDrawerRl=(LinearLayout)findViewById(R.id.driver_left_drawer);
		
		//this is parent layout
		mDrawerLayout=(DrawerLayout)findViewById(R.id.driver_drawer_layout);
		mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
		
		/***Set Timer**/
		
		
		stoptimertask();
		stoptimertask1();
		startTimer();
		startTimer1();
		
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
         
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
 
         }
         else { // Google Play Services are available 

    		try{		
    			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    				boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    			    boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    		
    			    // Check if enabled and if not send user to the GSP settings
    			    if (!enabledGPS && !enabledWiFi) {
    			       // Toast.makeText(DriverView_Activity.this, "GPS signal not found", Toast.LENGTH_LONG).show();
    			        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    			        startActivity(intent);
    			    }
    		
    			    
	        	googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.driver_map)).getMap();
	        	googleMap.setTrafficEnabled(true);
	        	googleMap.setMyLocationEnabled(true);
	 	       	googleMap.getUiSettings().setMapToolbarEnabled(false);
	            Criteria criteria = new Criteria();
	            String provider = locationManager.getBestProvider(criteria, true);
	            Location location = locationManager.getLastKnownLocation(provider);
	           
	            if(location!=null){
	               	onLocationChanged(location);
	            		}
	            locationManager.requestLocationUpdates(provider, 5000, 0,  this);
			     }
	        catch(Exception e){
				e.printStackTrace();
				}
    	}
/***top left button on screen, on which click, we will open drawer ***/
         iv_menu=(ImageView)findViewById(R.id.driver_iv_menu);
         iv_menu.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				v.startAnimation(buttonClick);
				if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
					mDrawerLayout.closeDrawers();
				}
				else{
					mDrawerLayout.openDrawer(flyoutDrawerRl);
				}
			}
		});
         //set picture of driver
	        String picurl=prefs.getString("picurl",null);

	        Log.i("tag", "Pic Url"+picurl);
	        imgbtn_driverpic=(ImageView)findViewById(R.id.imageView_driverpic);
	        if(!(picurl==null))
	        {
	        	GetXMLTask task = new GetXMLTask();
	    		// Execute the task
	    		task.execute(new String[] {picurl});
	        }
	        //set driver name
	        String lastname=prefs.getString("userlastname","Guest");
	       // lastname.substring(1,1);
	      //  lastname.toUpperCase();
	        System.err.println("lastname="+lastname);
	        
	        TextView tv_name=(TextView)findViewById(R.id.textView_drivername);
	        tv_name.setText(prefs.getString("userfirstname","Guest")+"  "+lastname);
		
		//imgbtn_driverpic.set
		
		btn_pendingrequest=(Button)findViewById(R.id.button_requsts);
		btn_pendingrequest.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stoptimertask() ;
				stoptimertask1();
				
				Intent i=new Intent(DriverView_Activity.this,PendingRequests_Activity.class);
				i.putExtra("requests", "requests");
				i.putExtra("request1", "1");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
			}
		});
		btn_query=(Button)findViewById(R.id.bt_query);
		btn_query.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			stoptimertask() ;
			stoptimertask1();
				Intent i=new Intent(DriverView_Activity.this,PendingRequests_Activity.class);
				i.putExtra("queue", "queue");
				i.putExtra("queue2", "2");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
			}
		});
		//view Rides
		btn_viewrides=(Button)findViewById(R.id.button_viewrides);
		btn_viewrides.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			stoptimertask() ;
			stoptimertask1();
			
				Intent i=new Intent(DriverView_Activity.this,ViewRides_Activity.class);
				i.putExtra("url", "http://appba.riderapid.com/driver_report/?driverid="+prefs.getString("driverid", null));
				i.putExtra("drivervr", "value");
				
				startActivity(i);
				
				stoptimertask() ;
				stoptimertask1();
				mDrawerLayout.closeDrawers();
			}
		});
		/**payment**/
		btn_payment=(Button)findViewById(R.id.button_payment);
		btn_payment.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			
			stoptimertask();
			stoptimertask1();
			
				Intent i=new Intent(DriverView_Activity.this,ViewRides_Activity.class);
				i.putExtra("url", " http://appba.riderapid.com/dpayment/?driverid="+prefs.getString("driverid", null));
				i.putExtra("drivervr", "value");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
			}
		});

		/**help ***/
		btn_help=(Button)findViewById(R.id.button_help);
		btn_help.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			
			stoptimertask();
			stoptimertask1();
			
				Intent i=new Intent(DriverView_Activity.this,Help_Activity.class);
				i.putExtra("driver","driver");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
			}
		});
		//toggle button
//		im_view=(ImageView)findViewById(R.id.toggleButton_rp_driver_on_off);
//		tbtn_ridermode=(RelativeLayout)findViewById(R.id.toggleButton_rp_driver_on_off1);
//		
//		//tbtn_ridermode.setChecked(true);
//		
//		tbtn_ridermode.setOnClickListener(new OnClickListener() {
//		public void onClick(View v) {
//				
//				stoptimertask() ;
//				stoptimertask1();
//				mDrawerLayout.closeDrawers();
//				if(Utility.isConnectingToInternet(DriverView_Activity.this))
//				{
//					new httpSwitchBetweenModeBusy().execute();/**busy mode */
//					im_view.setImageResource(R.drawable.green_circle);
//					}
//				else{
//					Utility.alertMessage(DriverView_Activity.this,"error in internet connection");
//				}
//				
//				//}
//				
//				//System.err.println("if");
//				
//				}
//			
//		});
		
		
		//toggle button
//				tbtn_ridermode=(ToggleButton)findViewById(R.id.toggleButton_rp_driver_on_off);
//				tbtn_ridermode.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on));
//				tbtn_ridermode.setOnTouchListener(new OnTouchListener() {
//					
//					@Override
//					public boolean onTouch(View v, MotionEvent event) {
//						// TODO Auto-generated method stub
//						if(event.getAction() == MotionEvent.ACTION_MOVE){
//							tbtn_ridermode.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off));
//									stoptimertask() ;
//									stoptimertask1();
//									mDrawerLayout.closeDrawers();
//								
//									new httpSwitchBetweenModeBusy().execute();
//								
//									
//							
//						}
//						return true;
//					}
//				});
		
//		//toggle button
//		tbtn_ridermode=(ToggleButton)findViewById(R.id.toggleButton_rp_driver_on_off);
//		tbtn_ridermode.setChecked(true);
//		tbtn_ridermode.setOnClickListener(new OnClickListener() {
//		public void onClick(View v) {
//				
//			if(tbtn_ridermode.isChecked()==false)
//			{
//				stoptimertask() ;
//				stoptimertask1();
//				mDrawerLayout.closeDrawers();
//			
//				new httpSwitchBetweenModeBusy().execute();
//				//}
////				Intent i=new Intent(DriverView_Activity.this,MapView_Activity.class);
////				i.putExtra("driver", "value");
////				startActivity(i);
//				//System.err.println("if");
//				
//				}
//			else
//			{
//				System.err.println("else");
//				}
//			}
//		});
	
		
	setListenerOnDrawer();
	
	if(Utility.isConnectingToInternet(DriverView_Activity.this))
	{
		new httpPendingRequest().execute(); /**asyn task for padding request */
		}
	else{
		Utility.alertMessage(DriverView_Activity.this,"error in internet connection");
	}
	//new httpSwitchBetweenModeActive().execute(); // asyn task Active mode
	
	 
	
	
	
	
	 publishToggle = (MySwitch)findViewById(R.id.toggleButton_rp_driver_on_off);
     publishToggle.setChecked(false);
     publishToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
	 public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked){
//					System.err.println("yes");
//					Toast.makeText(MapView_Activity.this, "yes", Toast.LENGTH_SHORT).show();
					stoptimertask() ;
					stoptimertask1();
					mDrawerLayout.closeDrawers();
				
					new httpSwitchBetweenModeBusy().execute();
					 }
				else {
				}
				
			}
		});
	
}
	
	
	
	
	/***************************End main function********************************************/
	/**Timer***/   
//	protected void onResume() {
//	         super.onResume();
//	         startTimer();
//	     }
		  public void startTimer() {
	         timer = new Timer();
	         initializeTimerTask();
	      
	         //schedule the timer, after the first 5000ms the TimerTask will run every 15s
	         timer.schedule(timerTask, 1000, 15000); //
	
	     }
	     public void stoptimertask() {

	         //stop the timer, if it's not already null
	         if (timer != null) {
	             timer.cancel();
	             timer = null;
	         }}
		     public void initializeTimerTask() {
	          timerTask = new TimerTask() {
	             public void run() {
	              //use a handler to run a toast that shows the current timestamp
	                handler.post(new Runnable() {
	                public void run() {
	              //  Toast.makeText(getApplicationContext(), "driver view refresh", Toast.LENGTH_SHORT).show();
	                arraylist_pendingrequest.clear();
	                
	            	if(Utility.isConnectingToInternet(DriverView_Activity.this))
	            	{
	            		new httpPendingRequest().execute(); 
	 	                //new httpSwitchBetweenModeActive().execute();/**asyn task for padding request */
	            		}
	            	else{
	            		Utility.alertMessage(DriverView_Activity.this,"error in internet connection");
	            	}
	               
	                     
	                }
	                 });
	             }
	         };
		     }
		     
		     
		     public void startTimer1() {
		         timer_SwithModeActive = new Timer();
		         initializeTimerTask1();
		      
		         //schedule the timer, after the first 5000ms the TimerTask will run every 15s
		         timer_SwithModeActive.schedule(timerTask_SwitchModeActive, 1000, 15000); //
		
		     }
		     public void stoptimertask1() {

		         //stop the timer, if it's not already null
		         if (timer_SwithModeActive != null) {
		             timer_SwithModeActive.cancel();
		             timer_SwithModeActive = null;
		         }}
			     public void initializeTimerTask1() {
		          timerTask_SwitchModeActive = new TimerTask() {
		             public void run() {
		              //use a handler to run a toast that shows the current timestamp
		                handler_SwitchModeActive.post(new Runnable() {
		                public void run() {
		              //  Toast.makeText(getApplicationContext(), "driver view refresh", Toast.LENGTH_SHORT).show();
		               
		            	if(Utility.isConnectingToInternet(DriverView_Activity.this))
		            	{
		            		System.err.println("drive swtich betactiveeeeeeeeeeee");
		 	                new httpSwitchBetweenModeActive().execute();/**asyn task for padding request */
		            		}
		            	else{
		            		Utility.alertMessage(DriverView_Activity.this,"error in internet connection");
		            	}
		               
		                     
		                }
		                 });
		             }
		         };
			     }
		     
/**following method for drawer layout */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

/**this methods sets the listeners on drawer layout */
	private void setListenerOnDrawer(){
		mDrawerToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_launcher,
				R.string.app_name, R.string.app_name){
			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};
		//		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	/**images download */ 
	public class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
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
	    		imgbtn_driverpic.setImageBitmap(Bitmap.createScaledBitmap(result, 90, 80, false));
	    	}
	   // imgbtn_driverpic.setImageBitmap(result);
	    
	        }	
	}
/**Async_task class for PendingRequest count */
	private class httpPendingRequest extends AsyncTask<Void, Void, Void> { 
			@Override
		protected void onPreExecute() {
				super.onPreExecute();

				exception=0;
			}
		protected Void doInBackground(Void... arg0) {
			
			try {
				PendingRequest_parsing();
				 } catch (Exception e) {
				 e.printStackTrace();
				}
			return null;
		}
		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				//pDialog2.dismiss();
				if(exception==1)
				{
					Utility.alertMessage(DriverView_Activity.this,"Internet connection failed. Please Try again later.");
				}
				else{
				System.err.println("driver view size="+arraylist_pendingrequest.size());
				tv_requestcount=(TextView)findViewById(R.id.textView_reuestarray);
				tv_requestcount.setText(String.valueOf(arraylist_pendingrequest.size()));
				}
			}
		}
/**pending request code parsing function */
		public void PendingRequest_parsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/FetchPendingRideRequests");//Url_Address.url_promocode);
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "FetchPendingRideRequests");
				System.err.println("Fetch PendingRide Requests web services");
				
				json.put("Role", "Driver");
				System.err.println("Role="+"Driver");
			
				json.put("Id", prefs.getString("driverid", null));
				System.err.println("Id ="+prefs.getString("driverid", null));
				json.put("Trigger", "requests");
				System.err.println("requests");
				
				httpost.setEntity(new StringEntity(json.toString()));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				
				HttpResponse response = client.execute(httpost);
				HttpEntity resEntityGet = response.getEntity();
				String jsonstr=EntityUtils.toString(resEntityGet);
				if(jsonstr!=null)
				{
				 Log.i("FetchPendingRideRequests","result-->>>>>    "+ jsonstr);
				 
					}
					JSONObject obj=new JSONObject(jsonstr);
					String	jsonResult=obj.getString("result");
					String	pending_jsonMessage=obj.getString("message");
					
					String ListPromoCodeInfo=	obj.getString("PendingRequestList");
					Log.i("FetchPendingRideRequests:", "PendingRequestList: "+ListPromoCodeInfo);
					
					JSONArray jsonarray=obj.getJSONArray("PendingRequestList");
				
					for(int i=0;i<jsonarray.length();i++){
						
					JSONObject obj2=jsonarray.getJSONObject(i);
					
					String tripId=obj2.getString("tripId");
					Log.i("FetchPendingRideRequests:", "tripId: "+tripId);
					
					String	riderId=	obj2.getString("riderId");
					Log.i("FetchPendingRideRequests:", "riderId: "+riderId);
					
					String driverId=	obj2.getString("driverId");
					Log.i("FetchPendingRideRequests:", "driverId: "+driverId);
					
					String start_loc=	obj2.getString("start_loc");
					Log.i("FetchPendingRideRequests:", "start_loc: "+start_loc);
					
					String destination_loc=	obj2.getString("destination_loc");
					Log.i("FetchPendingRideRequests:", "destination_loc: "+destination_loc);
					
					Log.i("FetchPendingRideRequests:", "Result: "+jsonResult);
					Log.i("FetchPendingRideRequests:", "Message :"+pending_jsonMessage);
					
					arraylist_pendingrequest.add(driverId);
					
					}
				}
				  catch(Exception e){
				   System.out.println(e);
				   exception=1;
				   Log.d("tag", "Error :"+e); }  
					}
				
/**location change google map */
	  public void onLocationChanged(Location location) {
		  	
		
    	  		try{
    	  		googleMap.clear();
    	        /**Getting latitude and longitude of the current location**/
		        latitude = location.getLatitude();
		        longitude = location.getLongitude();
		      
		        LatLng latLng = new LatLng(latitude, longitude);
		        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
		        googleMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
		        }catch(Exception e){
						e.printStackTrace();
			      }
			  	}
		public void onStatusChanged(String provider, int status, Bundle extras) {
			}
		public void onProviderEnabled(String provider) {
			}
		public void onProviderDisabled(String provider) {
			}
// AsyncTask class for SwitchBetweenMode Active
		public class httpSwitchBetweenModeActive extends AsyncTask<Void, Void, Void> {
				ProgressDialog pDialog;
			protected void onPreExecute() {
				super.onPreExecute();

				}
			protected Void doInBackground(Void... arg0) {
				try {
					SwitchBetweenModeActive();
					} catch (Exception e) {
					 e.printStackTrace();
					}
				return null;
				}
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);


			}
/**function SwitchBetweenModeActive	*/	
 public void SwitchBetweenModeActive() throws JSONException {
			
			try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 30000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 31000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/SwitchBetweenMode");
					JSONObject json = new JSONObject();
					 
					 json.put("Trigger", "SwitchBetweenMode");
					 System.err.println("SwitchBetweenModeActive");
				
					 	json.put("DriverId", prefs.getString("driverid",null));
					 	System.err.println("DriverId-"+prefs.getString("driverid",null));
					 	
					    json.put("RiderId", prefs.getString("userid",null));
					    System.err.println("RiderId-"+prefs.getString("userid",null));
					    
					    json.put("Longitude",longitude);
					    System.err.println("Longitude-"+longitude);
					    
					    json.put("Latitude",latitude);
					    System.err.println("Latitude-"+latitude);
					    
				        json.put("Trigger","activate");
				        System.err.println("Trigger-->"+"activate");
				       
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					
					if(jsonstr!=null)
					{
					 Log.e("SwitchBetweenModeActive","SwitchBetweenModeActive result-->   "+ jsonstr);
						}
					JSONObject obj=new JSONObject(jsonstr);
				String	jsonResult_switchmode=obj.getString("result");
				String	jsonMessage_switchmode=obj.getString("message");
					
					Log.e("SwitchBetweenModeActive:", " Result: "+jsonResult_switchmode);
					Log.e("SwitchBetweenModeActive:", "Message :"+jsonMessage_switchmode);
					}
					  catch(Exception e){
					   System.out.println("SwitchBetweenModeActive"+e);
					   Log.d("SwitchBetweenModeActive", "SwitchBetweenModeActive Error :"+e);
					   }
					}
				
		 }		
// AsyncTask class for SwitchBetweenMode busy
	 public class httpSwitchBetweenModeBusy extends AsyncTask<Void, Void, Void> {
			
	
	protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			progressbar = new ProgressDialog(DriverView_Activity.this);
			//progressbar.setMessage("Loading");
			progressbar.setMessage("Please wait...");
			progressbar.setCancelable(false);
			progressbar.show();
			}
	@Override
	protected Void doInBackground(Void... arg0) {

			try {
				SwitchBetweenModeBusy();
			 
			} catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}

	return null;
	}

		@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		progressbar.dismiss();
		Editor ed=prefs.edit();
		ed.putString("mode", "rider");
		ed.commit();
		Intent i=new Intent(DriverView_Activity.this,MapView_Activity.class);
		i.putExtra("driver", "value");
		finish();
		startActivity(i);
		
		}
	 }

//function  SwitchBetweenModeActive busy
		public void SwitchBetweenModeBusy() throws JSONException {
			
			try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 30000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 31000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/SwitchBetweenMode");
					JSONObject json = new JSONObject();
							
					 json.put("Trigger", "SwitchBetweenMode");
					 System.err.println("SwitchBetweenMode");
					 	
					 json.put("DriverId", prefs.getString("driverid",null));
					 System.err.println("DriverId-"+prefs.getString("driverid",null));
					 	
					 json.put("Longitude", longitude);
					 System.err.println("Longitude-"+longitude);
						    
					 json.put("Latitude", latitude);
					 System.err.println("Latitude-"+latitude);
					 	
					 json.put("RiderId", prefs.getString("userid",null));
					 System.err.println("RiderId-"+prefs.getString("userid",null));
					    
				     json.put("Trigger","busy");
				     System.err.println("Trigger-->"+"busy");
				        
				      		     
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					
					if(jsonstr!=null)
					{
					 Log.e("SwitchBetweenModeBusy","SwitchBetweenModeBusy result-->   "+ jsonstr);
						}
					JSONObject obj=new JSONObject(jsonstr);
					String	jsonResult_switchmode=obj.getString("result");
					String	jsonMessage_switchmode=obj.getString("message");
					
					Log.e("SwitchBetweenModeBusy:", " Result: "+jsonResult_switchmode);
					Log.e("SwitchBetweenModeBusy:", "Message :"+jsonMessage_switchmode);
					}
			catch(Exception e){
					   System.out.println("SwitchBetweenModeBusy"+e);
					   Log.d("SwitchBetweenModeBusy", "SwitchBetweenModeBusy Error :"+e);
					   }
					}
		public void onBackPressed() {
//				stoptimertask();
//				stoptimertask1();
//				
//			
//			
//			super.onBackPressed();
		}
		 
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			//oneWay
				/*******Working Code******/
			String Message=prefs.getString("Message", "");
			if(Message==null || Message=="")
			{
			}else
			{
				
				boolean checkCancalRide=Message.contains("cancelled the ride");
	        	if(checkCancalRide==true)
	        	{
	        		Intent launchDialog = new Intent(DriverView_Activity.this, ShowNotification.class);
		        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
			    	Editor e=prefs.edit();
			    	e.putString("CancelMessage",Message);
			    	e.putString("Message", "");
			    	e.commit();
			    	startActivity(launchDialog);
			    }
	        	 else if(message.contains("Since no near by driver"))
	 	        {	}
	 	        
	 	        else if(message.contains("@"))
	 	        {	}
	 	        else if(message.contains("will reach"))
	 	        {	}  
	 	        else if(message.contains("been ended"))
	 	        {	}
	 	        else if(message.contains("started"))
	 	        {	}
	 	        else if(message.contains("arrived"))
	 	        {	}
	        	else{
				String message=Message;
				boolean check=message.contains("VIP");
				if(check==true){
				String[] values = message.split(";");
				String sendbyRequest=values[0].split("and offering price")[0];
				String offerPrice=values[0].split("and offering price")[1];
				String currentLoc = values[1].split("Current Location:")[1];
				//String distance = values[2].split("Distance:")[1];
				//String ETA = values[3].split("ETA:")[1];
				String TripId = values[2].split("TripId:")[1];
				String Rating = values[3].split("Rating:")[1];
				String Handicap = values[4].split("Handicap:")[1];
				String PickUpDateTime = values[5].split("PickUpDateTime:")[1];
				String PrefferedVechileType = values[6].split("PrefferedVechileType:")[1];
				String Image[] = values[7].trim().split(" ");
				String SpiltImage=Image[1];
				String requestType=values[7].split(".png")[1];
				
				Editor e=prefs.edit();
				e.putString("Message", "");
				e.commit();
				
				Intent launchDialog = new Intent(DriverView_Activity.this, ShowNotification.class);
	        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	launchDialog.putExtra("RequestSendBy", sendbyRequest);
	        	launchDialog.putExtra("OfferedPrice", offerPrice);
	        	launchDialog.putExtra("CurrentLocation", currentLoc);
	        	launchDialog.putExtra("Destination", "");
	        	launchDialog.putExtra("Distance", "");
	        	launchDialog.putExtra("ETA", "");
	        	launchDialog.putExtra("TripId", TripId);
	        	launchDialog.putExtra("Rating", Rating);
	        	launchDialog.putExtra("Handicap", Handicap);
	        	launchDialog.putExtra("PickUpDateTime", PickUpDateTime);
	        	launchDialog.putExtra("PrefferedVechileType", PrefferedVechileType);
	        	launchDialog.putExtra("Image", SpiltImage);
	        	launchDialog.putExtra("RequestType",requestType);
	        	startActivity(launchDialog);
	        	
				}
				else
				{
					try{
						String[] values = message.split(";");
						String sendbyRequest=values[0].split("to")[0];
						String TempDestination=values[0].split("and offering price")[0];
						String destinations=TempDestination.split("requesting ride to")[1];
						String offeringPrice=values[0].split("and offering price")[1];
						String getOfferPrice=offeringPrice.split("Current Location:")[0];
						String sendBy = values[0].split("Current Location:")[0];
						String currentLoc = values[0].split("Current Location:")[1];
						String distance = values[1].split("Distance:")[1];
						String ETA = values[2].split("ETA:")[1];
						String TripId = values[3].split("TripId:")[1];
						String Rating = values[4].split("Rating:")[1];
						String Handicap = values[5].split("Handicap:")[1];
						String PickUpDateTime = values[6].split("PickUpDateTime:")[1];
						String PrefferedVechileType = values[7].split("PrefferedVechileType:")[1];
						String Image[] = values[8].trim().split(" ");
						String SpiltImage=Image[1];
						String requestType=values[8].split(".png")[1];
		      	 
						Editor e=prefs.edit();
						e.putString("Message", "");
						e.commit();
						
						Intent launchDialog = new Intent(DriverView_Activity.this, ShowNotification.class);
			        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        	launchDialog.putExtra("RequestSendBy", sendbyRequest);
			        	launchDialog.putExtra("OfferedPrice", getOfferPrice);
			        	launchDialog.putExtra("CurrentLocation", currentLoc);
			        	launchDialog.putExtra("Destination", destinations);
			        	launchDialog.putExtra("Distance", distance);
			        	launchDialog.putExtra("ETA", ETA);
			        	launchDialog.putExtra("TripId", TripId);
			        	launchDialog.putExtra("Rating", Rating);
			        	launchDialog.putExtra("Handicap", Handicap);
			        	launchDialog.putExtra("PickUpDateTime", PickUpDateTime);
			        	launchDialog.putExtra("PrefferedVechileType", PrefferedVechileType);
			        	launchDialog.putExtra("Image", SpiltImage);
			        	launchDialog.putExtra("RequestType",requestType);
			        	startActivity(launchDialog);

					} 
					catch(Exception e)
					{
						System.err.println("exception"+e);
						}
				}
		}
	}
			/***************************/
//			String response="minakshi b is requesting ride to Chandigarh" +
//				" and offering price" +
//				"  Current Location: Landran; Distance: ;" +
//				" ETA: ; TripId: 1073;" +
//				" Rating:0 ; Handicap:1 ; PickUpDateTime: 20141215175800;" +
//				" PrefferedVechileType: 0;" +
//				" Image: http://mapp.riderapid.com/RiderImages/Img_71.png (Future)";
////		
////		String isoResponse="minakshi b is requesting ride to Chandigarh" +
////				" Group Of Colleges, Landran Kharar Banur Highway," +
////				" Sector 112, Landran, Sahibzada Ajit Singh Nagar," +
////				" Punjab 140307, India and offering price 21.91 Current" +
////				" Location: Sector 74 Industrial Area Phase 8B ,45 Ft Wide" +
////				" Road,Mohali,Punjab 140301,India; Distance: 3.355398;" +
////				" ETA: 13 mins; TripId: 1282; Rating:0 ; Handicap:1 ;" +
////				" PickUpDateTime: 20141219043444; PrefferedVechileType: 1;" +
////				" Image: http://mapp.riderapid.com/RiderImages/Img_71.png (Now)";
//		
//		String vipResponse="minakshi b sent you VIP request" +
//				" for Full day and offering price ;" +
//				"Current Location: Landran;Distance: ;" +
//				" ETA: ; TripId: 1073; Rating:0 ; Handicap:1 ;" +
//				" PickUpDateTime: 20141215175800;PrefferedVechileType: 0;" +
//				"Image: http://mapp.riderapid.com/RiderImages/Img_71.png" +
//				" (Future)";
//////		
//		boolean check=vipResponse.contains("VIP");
//////		
//		String[] values = vipResponse.split(";");
//		String sendbyRequest=values[0].split("and offering price")[0];
//		String offerPrice=values[0].split("and offering price")[1];
//		String currentLoc = values[1].split("Current Location:")[1];
//		String distance = values[2].split("Distance:")[1];
//		String ETA = values[3].split("ETA:")[1];
//		TripId = values[4].split("TripId:")[1];
//		String Rating = values[5].split("Rating:")[1];
//		String Handicap = values[6].split("Handicap:")[1];
//		String PickUpDateTime = values[7].split("PickUpDateTime:")[1];
//		String PrefferedVechileType = values[8].split("PrefferedVechileType:")[1];
//		
//		String Image[] = values[9].trim().split(" ");
//		SpiltImage=Image[1];
//		String temprequestType=values[9].split("\\(")[1];
//		String requestType=temprequestType.split("\\)")[0];
//		String[] requestType_Trim=requestType.trim().split(" ");
//		String rq=requestType_Trim[0];
//		Log.i("tag", "RequestType:"+requestType);
//		
//		if(PrefferedVechileType.equals(" 0") ||PrefferedVechileType.equals(" 1"))
//		{
//			PrefferedVechileType="Vehicle:"+"REGULAR";
//		}
//		else if(PrefferedVechileType.equals(" 2"))
//		{
//			PrefferedVechileType="Vehicle:"+"XL";
//		}
//		else if(PrefferedVechileType.equals(" 3"))
//		{
//			PrefferedVechileType="Vehicle:"+"EXECUTIVE";
//		}
//		else if(PrefferedVechileType.equals(" 4"))
//		{
//			PrefferedVechileType="Vehicle:"+"SUV";
//		}
//		else if(PrefferedVechileType.equals(" 5"))
//		{
//			PrefferedVechileType="Vehicle:"+"LUXURY";
//		}
//		try{
//
//			
//					  final Dialog dialog = new Dialog(DriverView_Activity.this);
//		                // Include dialog.xml file
////		                View viewTitle=getLayoutInflater().inflate(R.layout.select_colortitle, null);
//					  Window window = dialog.getWindow();
//					  window.setGravity(Gravity.BOTTOM);
//		              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
//		              dialog.setContentView(R.layout.request_view);
//		              dialog.setCancelable(false);
//		              TextView sendby=(TextView)dialog.findViewById(R.id.textViewWhoSendRequest);
//		              sendby.setText(sendbyRequest);
//		              
//		              TextView current_Loc=(TextView)dialog.findViewById(R.id.textViewPickUp);
//		              current_Loc.setText("Pick up at:"+currentLoc);
//		              
//		              TextView Destination1=(TextView)dialog.findViewById(R.id.txtDistance);
////		              Destination1.setText("Destination:"+destinations);
//		              
//		              TextView rating=(TextView)dialog.findViewById(R.id.textViewRating);
//		              rating.setText("Rating:"+Rating);
//		              
//		              TextView pickUpDateTime=(TextView)dialog.findViewById(R.id.textViewPickUp2);
//		              pickUpDateTime.setText("Pickup Time:"+PickUpDateTime);
//		              
//		              TextView prefferedVechileType=(TextView)dialog.findViewById(R.id.textViewVehicle);
//		              prefferedVechileType.setText(PrefferedVechileType);
//		              
//		              TextView offered=(TextView)dialog.findViewById(R.id.textViewOffered);
////		              offered.setText("Offered Fare:"+getOfferPrice);
//		              
//		              TextView textViewDistance=(TextView)dialog.findViewById(R.id.textViewDistance);
//		              textViewDistance.setText("Distance:"+distance);
//		              
//		              timeLeft=(TextView)dialog.findViewById(R.id.textViewTimeLeft);
//		              if(!(SpiltImage==null))
//		      	        {
//		            	    GetXMLTask2 task = new GetXMLTask2();
//		      	    		// Execute the task
//		      	    		task.execute(new String[] {SpiltImage});
//		      	        }
//		              ImageDriver=(ImageView)dialog.findViewById(R.id.imageViewDriverImage);
//		              
//		              Button btnAccept=(Button)dialog.findViewById(R.id.buttonAccept);
//		              btnAccept.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							status="Accepted";
//							new httpAcceptParsing().execute();
//							dialog.dismiss();
////							Utility.arraylist_notification().clear();
//							
//						}
//					});
//		              
//		              Button btnReject=(Button)dialog.findViewById(R.id.buttonReject);
//		              btnReject.setOnClickListener(new OnClickListener() {
//						
//						@Override
//						public void onClick(View v) {
//							// TODO Auto-generated method stub
//							status="Rejected";
//							new httpAcceptParsing().execute();
//							dialog.dismiss();
////							Utility.arraylist_notification().clear();
//							
//						}
//		              });
//		              
//		              dialog.show();
////		              Utility.arraylist_notification().clear();
//		              
//		              TimerTask countdownTask = new TimerTask() {
//		                  @Override
//		                  public void run() {
//		                      runOnUiThread(new Runnable() {
//		                          @Override
//		                          public void run() {
//		                              if(countdownNumber == 0) {
//		                                  cancel();
//		                                  dialog.dismiss();
//		                              }
//		                              timeLeft.setText(String.valueOf(countdownNumber));
//		                              countdownNumber--;
//		                          }
//		                      });
//		                  }
//		              };
//
//		              Timer countdown = new Timer();
//		              countdown.schedule(countdownTask, 0, 1000);
//				
//		
//			
//		}
//			catch(Exception e)
//			{
//					
//			}
//		}
//	public class GetXMLTask2 extends AsyncTask<String, Void, Bitmap> {
//        @Override
//    protected Bitmap doInBackground(String... urls) {
//            Bitmap map = null;
//            for (String url : urls) {
//                map =ImageDownloader.downloadImage(url);
//            }
//            return map;
//        }
//
//    protected void onPostExecute(Bitmap result) {
//    	ImageDriver.setImageBitmap(Bitmap.createScaledBitmap(result, 130, 120, false));
//    
//        }	
//}		
//	private class httpAcceptParsing extends AsyncTask<Void, Void, Void> { 
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// Showing progress dialog
//			pDialog = new ProgressDialog(DriverView_Activity.this);
//			pDialog.setTitle("Loading");
//			pDialog.setMessage("Please wait...");
//			pDialog.setCancelable(false);
//			pDialog.show();
//			}
//		
//		@Override
//		protected Void doInBackground(Void... arg0) {
//		
//			try {
//			 parsing();
//			 } catch (Exception e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			}
//			
//			return null;
//			}
//			
//			@Override
//			protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			pDialog.dismiss();
//			}
//			
//		}
//	public void parsing()
//	{
//		try {
//			HttpParams httpParameters = new BasicHttpParams();
//			int timeoutConnection = 30000;
//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//			int timeoutSocket = 31000;
//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//			HttpClient client = new DefaultHttpClient(httpParameters);
//			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/AcceptOrRejectRequest");
//			json = new JSONObject();
//			json.put("TripId",TripId);
//			json.put("Status", status);
//			
//		      //	      
//			Log.i("tag", "Sending Data:"+json.toString());
//			
//			httpost.setEntity(new StringEntity(json.toString()));
//			httpost.setHeader("Accept", "application/json");
//			httpost.setHeader("Content-type", "application/json");
//			
//			HttpResponse response = client.execute(httpost);
//			HttpEntity resEntityGet = response.getEntity();
//			String jsonstr=EntityUtils.toString(resEntityGet);
//			if(jsonstr!=null)
//			{
//			 Log.e("tag","Response Request:>>>>>>>    "+ jsonstr);
//			 
//				}
//				JSONObject obj=new JSONObject(jsonstr);
//			String	   jsonResult=obj.getString("result");
//			String	jsonMessage=obj.getString("message");
//				
//				Log.i("tag:", "Result: "+jsonResult);
//				Log.i("tag:", "Message :"+jsonMessage);
//				}
//			  catch(Exception e){
//			   System.out.println(e);
//			   Log.d("tag", "Error :"+e);
//			   }  
	

		
//	private class httpAcceptParsing extends AsyncTask<Void, Void, Void> { 
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// Showing progress dialog
//			pDialog = new ProgressDialog(DriverView_Activity.this);
//			pDialog.setTitle("Loading");
//			pDialog.setMessage("Please wait...");
//			pDialog.setCancelable(false);
//			pDialog.show();
//			}
//		
//		@Override
//		protected Void doInBackground(Void... arg0) {
//		
//			try {
//			 parsing();
//			 } catch (Exception e) {
//			 // TODO Auto-generated catch block
//			 e.printStackTrace();
//			}
//			
//			return null;
//			}
//			
//			@Override
//			protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			pDialog.dismiss();
//			}
//			
//		}
//	public void parsing()
//	{
//		try {
//			HttpParams httpParameters = new BasicHttpParams();
//			int timeoutConnection = 30000;
//			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//			int timeoutSocket = 31000;
//			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//			HttpClient client = new DefaultHttpClient(httpParameters);
//			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/AcceptOrRejectRequest");
//			json = new JSONObject();
//			json.put("TripId",TripId);
//			json.put("Status", status);
//			
//		      //	      
//			Log.i("tag", "Sending Data:"+json.toString());
//			
//			httpost.setEntity(new StringEntity(json.toString()));
//			httpost.setHeader("Accept", "application/json");
//			httpost.setHeader("Content-type", "application/json");
//			
//			HttpResponse response = client.execute(httpost);
//			HttpEntity resEntityGet = response.getEntity();
//			String jsonstr=EntityUtils.toString(resEntityGet);
//			if(jsonstr!=null)
//			{
//			 Log.e("tag","Response Request:>>>>>>>    "+ jsonstr);
//			 
//				}
//				JSONObject obj=new JSONObject(jsonstr);
//			String	   jsonResult=obj.getString("result");
//			String	jsonMessage=obj.getString("message");
//				
//				Log.i("tag:", "Result: "+jsonResult);
//				Log.i("tag:", "Message :"+jsonMessage);
//				}
//			  catch(Exception e){
//			   System.out.println(e);
//			   Log.d("tag", "Error :"+e);
//			   }  
//	}
//
//	public class GetXMLTask2 extends AsyncTask<String, Void, Bitmap> {
//        @Override
//    protected Bitmap doInBackground(String... urls) {
//            Bitmap map = null;
//            for (String url : urls) {
//                map =ImageDownloader.downloadImage(url);
//            }
//            return map;
//        }
//
//    protected void onPostExecute(Bitmap result) {
//    	ImageDriver.setImageBitmap(Bitmap.createScaledBitmap(result, 130, 120, false));
//    
//        }	
//}
			
				super.onResume();
		}
		
		@Override
		protected void onPause() {
		//stoptimertask();
		//stoptimertask1();
		super.onPause();
		}
}
