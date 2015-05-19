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
import org.json.JSONException;
import org.json.JSONObject;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class GetDriverLocation extends Activity implements LocationListener {

	private GoogleMap googleMap;
	public ProgressDialog pDialog,pDialog2;
	TextView selectColor,tv_titlemapscreen;
	
	LatLng driverpos;
	Typeface typeFace;
	SharedPreferences prefs;
	LocationManager  locationManager ;
	Timer timer;
	TimerTask timerTask;
	final Handler handler = new Handler();
	JSONObject json = new JSONObject();
	Location location;String provider;
	 Dialog dialog;
	ArrayList<String> arraylist_pendingrequest=new ArrayList<String>();
	int exception=0,flag=0;
	double latitude,longitude;
	String str_tripid="",getslatitude="",getslongitude="";
	String dissmiss="",jsonResult="",jsonMessage="";
	String selectColors="",vehicle="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getdriver_location);
		
		stoptimertask();
		startTimer();
		
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
        tv_titlemapscreen=(TextView)findViewById(R.id.driver_tv_title);
        tv_titlemapscreen.setTypeface(typeFace);
		
		  try {
	            // Loading map
	        	intializeMap();
		  	  } catch (Exception e) {
	            e.printStackTrace();
	        }
		  
	        //get string from requestride 
			//str_tripid=getIntent().getStringExtra("tripid");
			getslatitude=getIntent().getStringExtra("jsonlatitude");
			getslongitude=getIntent().getStringExtra("jsonlongitude");
			
			Log.d("tag", "str_tripid"+str_tripid);
			Log.d("tag", "getslatitude"+getslatitude);
			Log.d("tag", "getslongitude"+getslongitude);
			
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	        Criteria criteria = new Criteria();
	        provider = locationManager.getBestProvider(criteria, true);
	        location = locationManager.getLastKnownLocation(provider);
	        onLocationChanged(location);
	        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
	            int requestCode = 10;
	            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
	            dialog.show();
	 
	        }else { // Google Play Services are available 
	 
	        try{
  			boolean enabledGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
  			boolean enabledWiFi = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  		
  			// Check if enabled and if not send user to the GSP settings
  			if (!enabledGPS && !enabledWiFi) {
  			      Toast.makeText(GetDriverLocation.this, "GPS signal not found", Toast.LENGTH_LONG).show();
  			      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
  			      startActivity(intent);
  			   }
  			}catch(Exception e){
  				e.printStackTrace();
  			}

	    }

		selectColor=(TextView)findViewById(R.id.btnColorSelect);
		selectColor.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				
//				Editor ed=prefs.edit();
//				ed.putString("vehicletype2", getIntent().getStringExtra("vehicletype"));
//				ed.putString("tripid2",str_tripid);
//				ed.putString("driverid2",getIntent().getStringExtra("driverid"));
//				ed.commit();
                 dialog = new Dialog(GetDriverLocation.this);
                // Include dialog.xml file
//                View viewTitle=getLayoutInflater().inflate(R.layout.select_colortitle, null);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
                dialog.setContentView(R.layout.select_color);
                // Set dialog title
                ImageView btnPink=(ImageView)dialog.findViewById(R.id.imageViewPink);
                ImageView btnOrange=(ImageView)dialog.findViewById(R.id.imageViewOrange);
                ImageView btnGreen=(ImageView)dialog.findViewById(R.id.imageViewGreen);
                ImageView btnYellow=(ImageView)dialog.findViewById(R.id.imageViewYellow);
                ImageView btnLightBrown=(ImageView)dialog.findViewById(R.id.imageViewLightBrown);
                ImageView btnLightGray=(ImageView)dialog.findViewById(R.id.imageViewLghtGray);
                ImageView btnBlue=(ImageView)dialog.findViewById(R.id.imageViewBlue);
                
               
                
                btnPink.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					try {
							json.put("Colour","pink");
							selectColors="pink";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Log.i("tag", "Color: Pink");
					}
				});
                btnOrange.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					Log.i("tag", "Color: red");
						try {
							json.put("Colour","red");
							selectColors="red";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				});
                btnGreen.setOnClickListener(new OnClickListener() {
                	public void onClick(View v) {
                   		Log.i("tag", "Color: green");
						try {
							json.put("Colour","green");
							selectColors="green";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                });
                btnYellow.setOnClickListener(new OnClickListener() {
		           	public void onClick(View v) {
                  		Log.i("tag", "Color: yellow");
						try {
							json.put("Colour","yellow");
							selectColors="yellow";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                });
                btnLightBrown.setOnClickListener(new OnClickListener() {
		              	public void onClick(View v) {
                		Log.i("tag", "Color: orange");
						try {
							json.put("Colour","orange");
							selectColors="orange";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                });
                btnLightGray.setOnClickListener(new OnClickListener() {
		               	public void onClick(View v) {
                		Log.i("tag", "Color: white");
						try {
							json.put("Colour","white");
							selectColors="white";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                });
                btnBlue.setOnClickListener(new OnClickListener() {
                     	public void onClick(View v) {
                		Log.i("tag", "Color: blue");
						try {
							json.put("Colour","blue");
							selectColors="blue";
							new ColorMatchParsing().execute();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                	}
                	
                });
              
                	
                	  dialog.show();
				
                
			}
		});
		}
	
	 public class ColorMatchParsing extends AsyncTask<Void, Void, Void> {
		 protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(GetDriverLocation.this);
			exception=0;
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
	
	protected Void doInBackground(Void... arg0) {

				try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/ColorMatch");
					
					
					json.put("TripId",prefs.getString("tripid2", null));
					
					Log.i("tag","Send Json:"+json.toString());
					httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					
					Log.i("tag", "Result:"+jsonstr);
					JSONObject obj=new JSONObject(jsonstr);
					jsonResult=obj.getString("result");
					jsonMessage=obj.getString("message");
					Log.d("tag", "jsonResult:"+jsonResult);
				}
				catch(Exception e)
				{
					
				}
			return null;
		}

	protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			//new distanceCalculator().execute();
			if(exception==1)
			{
				Utility.alertMessage(GetDriverLocation.this,"Internet connection failed. Please Try again later.");
			}
			else if(jsonResult.equalsIgnoreCase("0"))
			{
				stoptimertask();
				Intent i=new Intent(GetDriverLocation.this,Rider_ColorView.class);
				i.putExtra("jsonlatitude", getslatitude);
				i.putExtra("jsonlongitude", getslongitude);
				i.putExtra("Color", selectColors);
				finish();
				
				startActivity(i);
				}
			else
			{
				Utility.alertMessage(GetDriverLocation.this, jsonMessage);
				}
	}
}		 

		public void intializeMap()
		{
		   		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		   		googleMap.setTrafficEnabled(true);
		   		if (googleMap != null) {
	             // The Map is verified. It is now safe to manipulate the map.
	    		googleMap.setMyLocationEnabled(true);
	    		 
	       }
		}

		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			  try{
				     /**  Getting latitude and longitude of the current location*///
				    	googleMap.clear();
				    	Log.d("tag","getlatitude"+getslatitude);
				    	Log.d("tag", "getlongitude"+getslongitude);
				    	latitude = Double.parseDouble(getslatitude);
				        longitude = Double.parseDouble(getslongitude);
				        driverpos = new LatLng(latitude, longitude);
				         
				        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverpos,15));
						    if((getIntent().getStringExtra("check")==null))
						    {
								    	System.err.println("vehicle1="+ getIntent().getStringExtra("vehicletype"));
								    	 vehicle=getIntent().getStringExtra("vehicletype").replace(" ", "");
						    		}
						    else
						    {
						    	System.err.println("vehicle2="+ prefs.getString("vehicletype2", ""));
						    	 vehicle=prefs.getString("vehicletype2", "");
						    		}
							    	if(vehicle.equals("1"))
							    		  {
							    			googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car)));
							    		  } 
								    else  if(vehicle.equals("2"))
							    		  {
								    		googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.xl_car)));
							    		  } 
								    else if(vehicle.equals("3"))
							    		  {
								    		googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.exec_car)));
							    		  } 
								     else  if(vehicle.equals("4"))
							    		  {
								    	 	googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.suv_car)));
							    		  } 
								    else  if(vehicle.equals("5"))
							    		  {
								    		googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.lux_car)));
							    		  } 
								    else
								    {
								    	googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car)));
								    	}
						    
				     //   googleMap.addMarker(new MarkerOptions().position(driverpos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
			
//				        autocompletetv_StartPlaces.setText(address_conveted);	   
					    googleMap.getUiSettings().setZoomGesturesEnabled(true);
						googleMap.getUiSettings().setCompassEnabled(false);
				    		  
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
		
		/** Timer **/
		 public void startTimer() {
		        timer = new Timer();
		        initializeTimerTask();
		      /**schedule the timer, after the first 20s the TimerTask will run every 20s */
		        timer.schedule(timerTask, 1000, 20000); //
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
		              // Toast.makeText(TripMapView_Activity.this,"trip refresh", Toast.LENGTH_SHORT).show();
		            		new  httpGetDriverLocation().execute();
		               }
		                });
		            }
		        };
			 }
		
		/***Get driver location class*****/
		
		private class httpGetDriverLocation extends AsyncTask<Void, Void, Void> { 
				ProgressDialog pDialog1;
				protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
				//	pDialog1 = new ProgressDialog(GetDriverLocation.this);
					///pDialog1.setTitle("Loading");
				//	pDialog1.setMessage("Please wait...");
				//	pDialog1.setCancelable(false);
//					if(!((Activity) GetDriverLocation.this).isFinishing())
//					{
//					pDialog1.show();
//					}
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
//					if(!((Activity) GetDriverLocation.this).isFinishing())
//					{  
//					pDialog1.dismiss();
//					}
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
					
				
					Log.d("tag","DriverId:"+prefs.getString("driverid2", ""));
					json.put("DriverId",prefs.getString("driverid2", ""));
					Log.d("tag","TripId:"+prefs.getString("tripid2", ""));
					json.put("TripId", prefs.getString("tripid2", ""));
				
					 String str_json=json.toString();
					 System.err.println("postdata="+str_json);
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
					jsonResult=obj.getString("result");
					jsonMessage=obj.getString("message");
					
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
		@Override
		public void onBackPressed() {
			stoptimertask();
			finish();
		}
		public void onDestroy(){
		    super.onDestroy();
		    if ( dialog!=null && dialog.isShowing() ){
		    	dialog.cancel();
		    }
		}
}
