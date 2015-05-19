package com.rapidride;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.navdrawer.SimpleSideDrawer;
import com.rapidride.util.BeanDriver;
import com.rapidride.util.Coordinates;
import com.rapidride.util.Driver_Coordinates;
import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Parser_json;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class MapView_Activity extends Activity implements LocationListener {
//https://maps.googleapis.com/maps/api/place/autocomplete/json?location=,&radius=5000&sensor=false&key=AIzaSyAyncD545s2oy_EYfjlB4Re_5_YI69C0Bg&components=country:in&input=
//	https://maps.googleapis.com/maps/api/place/autocomplete/json?input=q&types=establishment&location=30.71093,76.68619&radius=5000&key=AIzaSyAyncD545s2oy_EYfjlB4Re_5_YI69C0Bg
	ProgressDialog progress1;
		private GoogleMap googleMap;
		AutoCompleteTextView autocompletetv_StartPlaces,autocompletetv_destinationPlaces;
		Button btn_editregister,btn_logout,btn_driver,btn_riderqueue,buttonVIP,buttonOneWay,halfDay,fullDay;
		ImageButton btn_searchplaces,btn_searchplaces1,imgbtn_map_menu,btn_maphome,imgbtn_cross,imgbtn_cross1;
		Button btn_requestrider,btn_paymentactivity,btn_viewrides,btn_help;
		TextView tv_titlemapscreen,tv_actualPrice,txtview_vehiclelabel;
		protected ProgressDialog dialog;
		Spinner sp_flaglist;
		LinearLayout menulayout;
		public ProgressDialog pDialog;
		private ImageView iv_profilepic,imgCurrentButton,im_view;
		MySwitch publishToggle, swtch_alertshow;
		RelativeLayout rel_show;
		
		Typeface typeFace;
		SharedPreferences prefs;
		SimpleSideDrawer slide_me;
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		ArrayList<Coordinates> coordinates;
		ArrayList<Driver_Coordinates> al_vehicle_coordinates;
		ArrayList<String> al_drivername=new ArrayList<String>();
		ArrayList<String> al_drivertime=new ArrayList<String>();
		ArrayList<String> al_driverid=new ArrayList<String>();
		ArrayList<Integer> al_vehicleType=new ArrayList<Integer>();
		ArrayList<String> al_stringvehicleType=new ArrayList<String>();
		private ArrayList<String> recentPlaces;
		LocationManager locationManager;
		LatLng currentpos;
		Handler refreshHandler;
		Runnable myRunnable;
		private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);
		private LinearLayout flyoutDrawerRl;
		private DrawerLayout mDrawerLayout;
		private ActionBarDrawerToggle mDrawerToggle;
		Calendar c;
		double lati,longi;
	    double latitude,longitude, start_latitude,start_langitude;
	    boolean oneways=false,vips=false;
	    boolean destination=false;
	    int gohome=0, flagshow=1;
		int vehicleFlag = 0,positionV,selectedVehicleType=0;
		int exeception=0,hour,min,startvalue=0,desvalue=0, windowclick;
		int int_homepop=0;
		private String jsonMessage_fetchvehicle,jsonResult_fetchvehicle;
		String[] strings = {"REGULAR","XL","EXECUTIVE", "SUV", "LUXURY"};
		private String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
		private String TYPE_AUTOCOMPLETE = "/autocomplete",OUT_JSON = "/json",API_KEY,LOG_TAG;
		String address_conveted = "",address_converted1="",strAdd = "",str_vehicletype=null,str_currentdate=null;
		String jsonResult="",DestinationShow="",selectdriver=null,uuid="",homeClick="No",regId="";
		String paymentchecking="",driverfirstname,driverlastname,driverdistance,alert="",notification_message="",drivertime;
		String Address1 = "",Address2="",City="",County="",State="",Country="",PIN="",value="",getdriverid="";
		String activeAnRide="",AM_PM;
		String str_vehiclename,str_countryid="",CountryName;
		float est_des,est_time;
		float reg_price,xl_price,exec_price,lux_price,truck_price,latlong,
		reg_base,xl_base,exec_base,lux_base,truck_base,reg_min,
		xl_min,exec_min,lux_min,truck_min,suv_price,suv_base,suv_min,reg_surge,xl_surge,
		exec_surge,lux_surge,suv_surge,truck_surge,reg_hour,xl_hour,exec_hour,suv_hour,
		lux_hour,reg_hourfull,xl_hourfull,exec_hourfull,suv_hourfull,lux_hourfull,truck_hour,
		truck_hourfull;
		Date vehicleTimeStamp;
	    TimerTask scanTask,timerTask;
	    final Handler handler = new Handler();
		final Handler handler1 = new Handler();
	    Timer t = new Timer();
	    Timer timer;
	    AsyncTask<Void, Void, Void> mRegisterTask;
		JSONObject json;
		
		final Map<String, String> map = new TreeMap<String, String>   (String.CASE_INSENSITIVE_ORDER);
	
		
protected void onCreate(Bundle savedInstanceState) {
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapview_activity);
	        
	    prefs = MapView_Activity.this.getSharedPreferences("RapidRide", MODE_PRIVATE);
	    value =getIntent().getStringExtra("driver");
	    	     
	      str_countryid=GetCountryID();
	      	       
	       if(str_countryid.trim().equals(""))
	       {
	    	   str_countryid="US";
	       		}
	       else
	       {
	    	   str_countryid=GetCountryID();
	       		}
	   
	     
	      //////////////////////////////
	      rel_show=(RelativeLayout)findViewById(R.id.rel_show); 
	      rel_show.setVisibility(View.GONE);
	      ////////////////////////////
	       
	       try {
	            // Loading map
	        	intializeMap();
	 
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
		    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
	        .detectAll()
	        .penaltyLog()
	        .build();
	        StrictMode.setThreadPolicy(policy);
	
	        
 /********start timer task*******/
    stopMytimertask();
    startMyTimer();
	        
	    
	        
	        activeAnRide=prefs.getString("activetripid", "");
	      //  activeAnRide="1942";
	        if(activeAnRide.equals(""))
	        {
	        	Log.i("tag", "yes");
	        	}
	        else
	        {
	        	Intent i=new Intent(MapView_Activity.this,ShowDialog.class);
	        	i.putExtra("Active", "ride");
	        	i.putExtra("TripID", activeAnRide);
	        	stopMytimertask();
	        	finish();
	        	startActivity(i);
	        }
	 
	    imgCurrentButton=(ImageView)findViewById(R.id.btnMapCurrentLocation);
	    imgCurrentButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        String provider = locationManager.getBestProvider(criteria, true);
		        // Getting Current Location
		        Location location = locationManager.getLastKnownLocation(provider);	
		        
		        googleMap.setMyLocationEnabled(true);
		        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
			    googleMap.getUiSettings().setMapToolbarEnabled(false);
		        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()), 15));
		     
		        String currentAddress=getAddress2(location.getLatitude(), location.getLongitude());
		        MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title("Current location").snippet(currentAddress);
		        sta_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
    			googleMap.addMarker(sta_marker);
    	
    			
    			
			}
		});
	    
	    vehicleTimeStamp = new Date();
	
		Bundle extras = getIntent().getExtras();
		try{
		if (extras != null) {
			DestinationShow = extras.getString("DestinationShow");
			Log.i("tag", "Destination Show:"+DestinationShow);
			}
		}
		catch(Exception e)
		{
				
		}
		 Log.d("tag", "Show:"+DestinationShow);
		
		if(DestinationShow==null || DestinationShow==""){
		    Editor ed=prefs.edit();//putting destination value into share preference value 
	        ed.putFloat("des_lati", (float) 0.0); 
	        ed.putFloat("des_logi", (float) 0.0);
	        ed.putString("des_address",  "");
	        ed.commit();
	        Log.d("tag", "Show:"+DestinationShow);
	        if(Utility.isConnectingToInternet(MapView_Activity.this))
	        {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		        Criteria criteria = new Criteria();
		        String provider = locationManager.getBestProvider(criteria, true);
		        Log.i("tag", provider);
		             // Getting Current Location
		        Location location = locationManager.getLastKnownLocation(provider);
		            //on location change method set condition 
		        onLocationChanged(location);
		        }
	        else
	        {
	        	Utility.alertMessage(MapView_Activity.this, "No internet connection");
	        }
		 }
		else if(DestinationShow.equals("no"))
			{
//				 Editor ed=prefs.edit();
//				 ed.putString("cur_address",  "");
//			       // ed.putInt("edit_deswindow", 1);
//			     ed.commit();
//			     Log.d("tag", "Show:"+DestinationShow);
			}
		else if(DestinationShow.equals("yes"))
			{
//				 Log.d("tag", "Show:"+DestinationShow);
//				 Editor ed=prefs.edit();
//				 ed.putString("des_address",  "");
//			       // ed.putInt("edit_deswindow", 1);
//			     ed.commit();
			}
	  
        //get current date
	        Calendar c = Calendar.getInstance();
			str_currentdate = df.format(c.getTime());
			System.err.println("str_c"+str_currentdate);
	        
	    //animation layout
	        flyoutDrawerRl=(LinearLayout)findViewById(R.id.left_drawer);
			//this is parent layout
			mDrawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
			mDrawerLayout.setScrimColor(getResources().getColor(android.R.color.transparent));
			//this method contains code for setting open and close listeners on drawer layout/flyout menu
			setListenerOnDrawer();
	       
	    //font set //
	        typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	        tv_titlemapscreen=(TextView)findViewById(R.id.textView_rapid);
	        tv_titlemapscreen.setTypeface(typeFace);
	      
	       
	        String Pic=prefs.getString("picurl", "");
	        Log.d("tag", "PIC:"+Pic);
	        coordinates = new ArrayList<Coordinates>();
	        al_vehicle_coordinates = new ArrayList<Driver_Coordinates>();
	        recentPlaces = new ArrayList<String>();
	        /**api key get from  string xml//*/
	        API_KEY = getResources().getString(R.string.places_api_key);
	        //auto complete text code//
	        autocompletetv_StartPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places);
	        autocompletetv_StartPlaces.setThreshold(4);
	        autocompletetv_StartPlaces.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.adapter_layout));
	        
	     
	        autocompletetv_destinationPlaces = (AutoCompleteTextView) findViewById(R.id.atv_places1);
	        autocompletetv_destinationPlaces.setThreshold(4);
	        autocompletetv_destinationPlaces.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.adapter_layout));
	     
	        
	        oneways=true;
	        Editor ed1=prefs.edit();
			ed1.putString("requestType", "one way");
			ed1.commit();
	        buttonVIP=(Button)findViewById(R.id.buttonVIP);
	        buttonOneWay=(Button)findViewById(R.id.oneway);
	        menulayout=(LinearLayout)findViewById(R.id.llayout);
	        sp_flaglist=(Spinner)findViewById(R.id.spinner_flaglist);
	        btn_maphome=(ImageButton)findViewById(R.id.button_maphome_go);
	        halfDay=(Button)findViewById(R.id.btnHalfDay);
	        fullDay=(Button)findViewById(R.id.btnfullDay);
	    	        
	        buttonVIP.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					homeClick="";
					if(menulayout.getVisibility()==View.GONE)
					{
						menulayout.setVisibility(View.VISIBLE);
						
						}
					else if(menulayout.getVisibility()==View.VISIBLE)
					{
						menulayout.setVisibility(View.GONE);
						}
				}
			});
	       
	        halfDay.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					buttonVIP.setBackgroundColor(Color.BLUE);
					btn_maphome.setVisibility(View.GONE);
					homeClick="";
					Editor ed=prefs.edit();//putting destination value into share preference value 
			        ed.putFloat("des_lati", (float) 0.0); 
			        ed.putFloat("des_logi", (float) 0.0);
			        ed.putString("des_address",  "");
			    	ed.putString("requestType", "viphalf");
			       // ed.putInt("edit_deswindow", 1);
			    	//ed.putString("viptype", "halfday");
			        ed.commit();
			
			        buttonVIP.setBackgroundColor(getResources().getColor(R.color.blue));
					vips=true;
					oneways=false;
					buttonOneWay.setBackgroundColor(getResources().getColor(R.color.transparent));
					autocompletetv_destinationPlaces.setText("");
					autocompletetv_destinationPlaces.setEnabled(false);
					if(menulayout.getVisibility()==View.GONE)
					{
						menulayout.setVisibility(View.VISIBLE);
						}
					else if(menulayout.getVisibility()==View.VISIBLE)
					{
						menulayout.setVisibility(View.GONE);
						}
					googleMap.clear();
					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		            Criteria criteria = new Criteria();
		            String provider = locationManager.getBestProvider(criteria, true);
		             // Getting Current Location
		            Location location = locationManager.getLastKnownLocation(provider);
					onStartLocation(location);
				}
			});
	        
	        fullDay.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					btn_maphome.setVisibility(View.GONE);
					homeClick="";
					Editor ed=prefs.edit();//putting destination value into share preference value 
			        ed.putFloat("des_lati", (float) 0.0); 
			        ed.putFloat("des_logi", (float) 0.0);
			        ed.putString("des_address",  "");
			        ed.putString("requestType", "vipfull");
			       // ed.putString("viptype", "fullday");
			       // ed.putInt("edit_deswindow", 1);
			        ed.commit();
			 		vips=true;
					oneways=false;
					buttonVIP.setBackgroundColor(getResources().getColor(R.color.blue));
					buttonOneWay.setBackgroundColor(getResources().getColor(R.color.transparent));
					autocompletetv_destinationPlaces.setText("");
					autocompletetv_destinationPlaces.setEnabled(false);
					if(menulayout.getVisibility()==View.GONE)
					{
						menulayout.setVisibility(View.VISIBLE);
						}
					else if(menulayout.getVisibility()==View.VISIBLE)
					{
						menulayout.setVisibility(View.GONE);
						}
					googleMap.clear();
					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		            Criteria criteria = new Criteria();
		            
		            String provider = locationManager.getBestProvider(criteria, true);
		             // Getting Current Location
		            Location location = locationManager.getLastKnownLocation(provider);
					onStartLocation(location);
				}
			});
	        
	        buttonOneWay.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
//					buttonOneWay.setBackgroundColor(Color.BLUE);
					btn_maphome.setVisibility(View.VISIBLE);
					homeClick="";
					if(menulayout.getVisibility()==View.VISIBLE)
					{
						menulayout.setVisibility(View.GONE);
						}
					Editor ed=prefs.edit();
					ed.putString("requestType", "one way");
					ed.commit();
					oneways=true;
					vips=false;
					buttonOneWay.setBackgroundColor(getResources().getColor(R.color.blue));
					buttonVIP.setBackgroundColor(Color.TRANSPARENT);
					autocompletetv_destinationPlaces.setEnabled(true);
				}
			});
	        
	       
	     
			Editor ed=prefs.edit();
			ed.putString("mode", "rider");
			ed.commit();
	        
	        imgbtn_cross=(ImageButton)findViewById(R.id.imageView_cross);
	        imgbtn_cross1=(ImageButton)findViewById(R.id.imageView_cross1);
	     
	        //current location display//
	        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
	          // Showing status
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
    			      Toast.makeText(MapView_Activity.this, "GPS signal not found", Toast.LENGTH_LONG).show();
    			      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
    			 
    			      startActivity(intent);
    			   }
    			}catch(Exception e){
    				e.printStackTrace();
    			}
//	        	googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
//	        	googleMap.setTrafficEnabled(true);
//	            
//	        	 if (googleMap != null) {
//	                 // The Map is verified. It is now safe to manipulate the map.
//	        		 googleMap.setMyLocationEnabled(true);
//	        		 
//	             }
	            // Getting LocationManager object from System Service LOCATION_SERVICE
	            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	            Criteria criteria = new Criteria();
	            
	            String provider = locationManager.getBestProvider(criteria, true);
	             // Getting Current Location
	            Location location = locationManager.getLastKnownLocation(provider);
	           
	            if(location!=null){
	            	 if(!(getIntent().getStringExtra("editbutton_start")==null))
	            	 	{
	            		 homeClick="";
	            		 onStartLocation(location);
	            		 System.err.println("start");
	            		 int vtype=prefs.getInt("vehicletype",0);
	           	        Log.d("tag", "Vechile Type:"+vtype);
	           	        
	           	        autocompletetv_destinationPlaces.setVisibility(View.GONE);
	           	        imgbtn_cross1.setVisibility(View.GONE);
	           	        btn_maphome.setVisibility(View.GONE);
	           	        buttonVIP.setVisibility(View.VISIBLE);
	           	        sp_flaglist.setVisibility(View.VISIBLE);
	           	        autocompletetv_StartPlaces.setVisibility(View.VISIBLE);
	           	        imgbtn_cross.setVisibility(View.VISIBLE);
	           	        
	           	        sp_flaglist.setAdapter(new flagAdapter(MapView_Activity.this,R.layout.flag_adapter, strings));
	         	        int newtype=vtype-1;
	         	        sp_flaglist.setSelection(newtype);
	         	        
	         	        sp_flaglist.setOnItemSelectedListener(new OnItemSelectedListener() {
	         			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
	         			positionV=position+1;
	         			Log.d("tag", "Position :"+positionV);
	         			str_vehiclename=(String) txtview_vehiclelabel.getText();
	         			if(flagshow==1)
		         					{
//		         					sp_flaglist.setSelection(prefs.getInt("vehicletype",0));
		         					flagshow=0;
//		         					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		         		            Criteria criteria = new Criteria();
//		         		            String provider = locationManager.getBestProvider(criteria, true);
//		         		             // Getting Current Location
//		         		            Location location = locationManager.getLastKnownLocation(provider);
//		         		            //on location change method set condition 
//		         		            onLocationChanged(location);
		         			}else{
		         					new AddPrefrenceVechileTypeParsing().execute();
//		         					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//		         		            Criteria criteria = new Criteria();
//		         		            String provider = locationManager.getBestProvider(criteria, true);
//		         		             // Getting Current Location
//		         		            Location location = locationManager.getLastKnownLocation(provider);
//		         		            //on location change method set condition 
//		         		            onLocationChanged(location);
		         			} 
		         					}
		         			public void onNothingSelected(AdapterView<?> parent) { }
		         					});
	            	 	}
	            	 else if(!(getIntent().getStringExtra("editbutton_des")==null))
	            	 	{
	            		 homeClick="";
	            		 buttonVIP.setVisibility(View.GONE);
	            		 sp_flaglist.setVisibility(View.GONE);
	            		 autocompletetv_StartPlaces.setVisibility(View.GONE);
	            		 imgbtn_cross.setVisibility(View.GONE);
	            		 autocompletetv_destinationPlaces.setVisibility(View.VISIBLE);
	            		 imgbtn_cross1.setVisibility(View.VISIBLE);
	            		 btn_maphome.setVisibility(View.VISIBLE);
	            		 System.err.println("dest");
	            		 onDesLocation(location);
	            	 	}
            	 else
	            	 { 
            		    //flags list with spinner widget
            		    homeClick="";
            		    int vtype=prefs.getInt("vehicletype",0);
	           	        Log.d("tag", "Vechile Type:"+vtype);
	           	        sp_flaglist.setAdapter(new flagAdapter(MapView_Activity.this,R.layout.flag_adapter, strings));
	         	        int newtype=vtype-1;
	         	        sp_flaglist.setSelection(newtype);
	         	        
	         	        sp_flaglist.setOnItemSelectedListener(new OnItemSelectedListener() {
	         			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
	         			positionV=position+1;
	         			Log.d("tag", "Position :"+positionV);
	         			Log.d("tag", "Name :"+txtview_vehiclelabel.getText());
	         			str_vehiclename=(String) txtview_vehiclelabel.getText();
	         			Editor ed=prefs.edit();
	         			ed.putInt("vehicletype", positionV);
	         			ed.commit();
	         			
	         			
	         			if(flagshow==1)
	         					{
//	         					sp_flaglist.setSelection(prefs.getInt("vehicletype",0));
	         					flagshow=0;
	         					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	         		            Criteria criteria = new Criteria();
	         		            String provider = locationManager.getBestProvider(criteria, true);
	         		             // Getting Current Location
	         		            Location location = locationManager.getLastKnownLocation(provider);
	         		            //on location change method set condition 
	         		            onLocationChanged(location);
	         			}else{
	         					new AddPrefrenceVechileTypeParsing().execute();
//	         					locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//	         		            Criteria criteria = new Criteria();
//	         		            String provider = locationManager.getBestProvider(criteria, true);
//	         		             // Getting Current Location
//	         		            Location location = locationManager.getLastKnownLocation(provider);
//	         		            //on location change method set condition 
//	         		            onLocationChanged(location);
	         			} 
	         					}
	         			public void onNothingSelected(AdapterView<?> parent) { }
	         					});
	            	 	}
	            	 
	            	 }
	        }
	        imgbtn_cross.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					homeClick="";
					autocompletetv_StartPlaces.setText("");
					autocompletetv_StartPlaces.requestFocus();
					autocompletetv_StartPlaces.setEnabled(true);
				}
			});
	        imgbtn_cross1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// TODO Auto-generated method stub
					homeClick="";
					autocompletetv_destinationPlaces.setText("");
					autocompletetv_destinationPlaces.requestFocus();
					autocompletetv_destinationPlaces.setEnabled(true);
				}
			});
	        autocompletetv_StartPlaces.setTextSize(12);
	        autocompletetv_destinationPlaces.setTextSize(12);
	        autocompletetv_StartPlaces.setOnItemClickListener(new OnItemClickListener() { //auto complete select item action 
				public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					homeClick="";
					Utility.hideKeyboard(MapView_Activity.this);
					startvalue=1;
//					flag=1;
					String startlocation = autocompletetv_StartPlaces.getText().toString().trim();
					if(Utility.isConnectingToInternet(MapView_Activity.this))
					{
						new SearchTask_StartLocation().execute(startlocation);
						}
					else
					{
						Utility.alertMessage(MapView_Activity.this, "No internet connection");
						}
					}
				});
	        
	        autocompletetv_StartPlaces.setOnEditorActionListener(new EditText.OnEditorActionListener() {//auto complete search location action   	
				public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
					
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						homeClick="";
						Utility.hideKeyboard(MapView_Activity.this);
						startvalue=1; //validation destination save value
						String startlocation = autocompletetv_StartPlaces.getText().toString().trim();
						if(Utility.isConnectingToInternet(MapView_Activity.this))
						{
							new SearchTask_StartLocation().execute(startlocation);
							}
						else
						{
							Utility.alertMessage(MapView_Activity.this, "No Internet Connection");
							}
					
						return true;
			        }
						return false;
				}});
	       	
	        autocompletetv_destinationPlaces.setOnItemClickListener(new OnItemClickListener() { //auto complete select item action 
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						homeClick="";
						Utility.hideKeyboard(MapView_Activity.this);
						desvalue=1;
						String destination = autocompletetv_destinationPlaces.getText().toString().trim();
						System.err.println(destination);
						if(Utility.isConnectingToInternet(MapView_Activity.this))
						{
							new SearchTask_Destination().execute(destination);
							}
						else
						{
							Utility.alertMessage(MapView_Activity.this, "No internet connection");
							}
						
						}
					});
		        
	        autocompletetv_destinationPlaces.setOnEditorActionListener(new EditText.OnEditorActionListener() {//auto complete search location action   	
					public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
						
						if (actionId == EditorInfo.IME_ACTION_SEARCH) {
							homeClick="";
							Utility.hideKeyboard(MapView_Activity.this);
							desvalue=1; //validation destination save value
							String destination = autocompletetv_destinationPlaces.getText().toString().trim();
							//showOverlayInstructions(destination);
							
							if(Utility.isConnectingToInternet(MapView_Activity.this))
							{
								new SearchTask_Destination().execute(destination);
								}
							else
							{
								Utility.alertMessage(MapView_Activity.this, "No internet connection");
								}
				        	return true;
							}
						return false;
					}});
	       	
	        btn_requestrider=(Button)findViewById(R.id.button_locationprice);
	       	btn_requestrider.setOnClickListener(new OnClickListener() { //call distance calculation
			public void onClick(View v) {
			if(Utility.isConnectingToInternet(MapView_Activity.this))
				{
					homeClick="";
					if(oneways)
					{
						Log.d("tag", "start_latitude"+prefs.getFloat("cur_lati", (float) 00.00 ));
						String value=autocompletetv_StartPlaces.getText().toString();
						Log.i("tag","autocompletetv_StartPlaces:"+value);
						String value2=autocompletetv_destinationPlaces.getText().toString();
						Log.i("tag","autocompletetv_destinationPlaces:"+value2);
						if(DestinationShow==null || DestinationShow=="")
						{
							if(value.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select start location");
								}
					
							else if(value2.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select destination location");
								}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
									{
		//								Log.i("tag","Selected Type:)"+selectedVehicleType);
										stopMytimertask();	
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2",String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
									else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
								}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
							}
						}
						else if(DestinationShow.equals("no"))
						{
							if(value.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select start location");
								}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
									{
										Log.i("tag","Selected Type:)"+selectedVehicleType);	
										stopMytimertask();
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2",String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
									else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
								}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
							}
						}
						else if(DestinationShow.equals("yes"))
						{
							if(value2.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select destination location");
							}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
									{
	//								Log.i("tag","Selected Type:)"+selectedVehicleType);	
										stopMytimertask();	
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2", String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
								}
								else
								{
									Utility.alertMessage(MapView_Activity.this,"No "+str_vehiclename+" vehicle available in this area");
									}
							}
						}
						
//						if(prefs.getFloat("cur_lati", (float) 00.00)==0.0)
//						{
//							Utility.alertMessage(MapView_Activity.this,"Select Start location");
//						}
//						else if(prefs.getFloat("des_lati", (float) 00.00)==0.0)
//						{
//							Utility.alertMessage(MapView_Activity.this,"Select destination location");
//						}
//						else if(prefs.getString("des_address", null).equals("") ||prefs.getString("des_address", null).equals(null))
//						{
//							Utility.alertMessage(MapView_Activity.this,"Select destination location");
//						}
//						else if(prefs.getString("cur_address", null).equals("") ||prefs.getString("cur_address", null).equals(null))
//						{
//							Utility.alertMessage(MapView_Activity.this,"Select start location");
//						}
				
					}
					else if(vips)
					{
						Log.d("tag", "start_latitude"+prefs.getFloat("cur_lati", (float) 00.00 ));
						String value=autocompletetv_StartPlaces.getText().toString();
						Log.i("tag","autocompletetv_StartPlaces:"+value);
						if(value.equals(""))
						{
							Utility.alertMessage(MapView_Activity.this,"Select start location");
							}
						else if(DestinationShow.equals("no"))
						{
							if(value.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select start location");
								}
							}
						
						else{
							if(al_vehicleType.size()>0)
								{
								    if(selectedVehicleType!=0)
								    {
	//							    Log.i("tag","Selected Type:"+selectedVehicleType);
									    stopMytimertask();	
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "yes");
										i.putExtra("VIP2", String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
									    }
								    else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
								}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
					    	}
					}
				
//						if(al_driverid.size()>0){
//							Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
//							startActivity(i);
//							finish();
				    	}
				
				else
					{
					Utility.alertMessage(MapView_Activity.this, "No internet connection");
					}
				}
	       
			});

	        //go to home button
	        btn_maphome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				stopMytimertask();
				desvalue=1;
				if(prefs.getString("completeaddress", null).equals(",,,"))
				{
					Utility.alertMessage(MapView_Activity.this,"Please enter address first in Edit Profile.");}
				else{
						gohome=1;
						Editor ed=prefs.edit();
						ed.putInt("homeclick", 0);
						ed.commit();
						System.err.println(prefs.getString("completeaddress", null));
						Log.i("tag", "Map View Get Address: "+prefs.getString("completeaddress", null));
						homeClick="yes";
						if(Utility.isConnectingToInternet(MapView_Activity.this))
						{
							new SearchTask_Destination().execute(prefs.getString("completeaddress", null));
							}
						else
						{
							Utility.alertMessage(MapView_Activity.this, "No internet connection");
							}
						}
					}
			
			});
			
	        /** Left Side menu bar button */
	        imgbtn_map_menu=(ImageButton)findViewById(R.id.imageButton_mapview_menu); //side menu bar
	        imgbtn_map_menu.setOnClickListener(new OnClickListener() {
	        	public void onClick(View v) {
					// TODO Auto-generated method stub
					v.startAnimation(buttonClick);
					if(mDrawerLayout.isDrawerOpen(flyoutDrawerRl)){
						mDrawerLayout.closeDrawers();
					}
					else{
						mDrawerLayout.openDrawer(flyoutDrawerRl);
						   // Detect touched area 
			           
					}
				}
			});
	        
	        /** profile picture of rider */
	        String picurl=prefs.getString("picurl",null);
	        Log.i("tag", "Pic Url"+picurl);
	        iv_profilepic=(ImageView)findViewById(R.id.imageView_profilepic);
	        if(picurl!="" || picurl!=null)
	        {
	        	GetXMLTask task = new GetXMLTask();
	    		// Execute the task
	        	if(Utility.isConnectingToInternet(MapView_Activity.this))
	        	{
		    		task.execute(new String[] {picurl});
		        	}
	        	else
	        	{
	        		Utility.alertMessage(MapView_Activity.this, "No internet connection");
	        		}
	        }
	     
	        String lastname=prefs.getString("userlastname","Guest");
	        TextView tv_name=(TextView)findViewById(R.id.tv_ridername);
	        tv_name.setText(prefs.getString("userfirstname","Guest")+"  "+lastname);
	      
	        /**Go to Edit_RiderRegister class*/
	        btn_editregister=(Button)findViewById(R.id.button_editregister); 
	        btn_editregister.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					stopMytimertask();
					Intent i=new Intent(MapView_Activity.this,Edit_RiderRegister.class);
					startActivity(i);
				
					mDrawerLayout.closeDrawers();
				}
	        });	
	        /** Logout button */
	        btn_logout=(Button)findViewById(R.id.button_logout); 
	        btn_logout.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
			
				stopMytimertask();
				Editor ed=prefs.edit();
				ed.clear();
				ed.commit();
				Utility.arrayListPromo().clear();
				
				Intent i=new Intent(MapView_Activity.this,Login_Activity.class);
				Intent intent = new Intent(Intent.ACTION_MAIN);
  			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  			    intent.addCategory(Intent.CATEGORY_HOME);
  			    finish();
				startActivity(i);
				
				mDrawerLayout.closeDrawers();
				}
			});
	        /** payment button function */
	        btn_paymentactivity=(Button)findViewById(R.id.button_payment);
			btn_paymentactivity.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				stopMytimertask();
				Intent i=new Intent(MapView_Activity.this,Payment_Activity.class);
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
				}
			}) ;
			
			 /** view rides button function */
	        btn_viewrides=(Button)findViewById(R.id.button_viewrides);
	        btn_viewrides.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				stopMytimertask();
				Intent i=new Intent(MapView_Activity.this,ViewRides_Activity.class);
				i.putExtra("url", "http://appba.riderapid.com/rider_report/?rideid="+prefs.getString("userid", null));
				i.putExtra("ridervr", "value");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
				}
			}) ;
	        /**help button function*/
	        btn_help=(Button)findViewById(R.id.button_help);
	        btn_help.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopMytimertask();
				
				Intent i=new Intent(MapView_Activity.this,Help_Activity.class);
				i.putExtra("rider","rider");
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
				}
			}) ;
	       
	        /** driver button function*/
			btn_driver=(Button)findViewById(R.id.button_driver);
			btn_driver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				}
			}) ;
			/**driver login button function*/
			btn_riderqueue=(Button)findViewById(R.id.button_riderqueue);
			btn_riderqueue.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stopMytimertask();
				
				Intent i=new Intent(MapView_Activity.this,RiderQueue_Activity.class);
				
				startActivity(i);
				mDrawerLayout.closeDrawers();
				}
			}) ;
			//toggle button driver mode on off
			/**changes*****/
			//toggle button driver mode on off
/*			im_view=(ImageView)findViewById(R.id.toggleButton_driver_on_off);
			tbtn_driver=(RelativeLayout)findViewById(R.id.toggleButton_driver_on_off1);
			tbtn_driver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stoptimertask();
				stopMytimertask();
			
					if(prefs.getString("driverid", null).equals(""))
					{
						Utility.alertMessage(MapView_Activity.this, "You are not activated to drive");//"You are not activated to drive"
					
						}
					else
					{
						//new httpSwitchBetweenMode().execute(); //httpSwitchBetweenMode task
						new Deviceregister().execute();
					//	Intent i=new Intent(MapView_Activity.this,DriverView_Activity.class);
						im_view.setBackgroundResource(R.drawable.green_circle);
					//	startActivity(i);
						}
				}
				
			});*/
			//toggle button driver mode on off
/*			tbtn_driver=(ToggleButton)findViewById(R.id.toggleButton_driver_on_off);
			tbtn_driver.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_off));
			tbtn_driver.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
//					 final int X = (int) event.getX();
//					 final int Y = (int) event.getY();
					if(event.getAction() == MotionEvent.ACTION_MOVE){
						  // Do what you want
						
//						    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
//					        layoutParams.rightMargin = X - 10;
//					        layoutParams.leftMargin = X - 10;           
//					        layoutParams.topMargin = 1;
//					        layoutParams.bottomMargin = 1;
//					        v.setLayoutParams(layoutParams);         
					        if (mStartingX != -1000 && X >= mCurrentX + MOVE_SIZE) {
					            doActionRight(); 
					        } else if (mStartingtX != -1000 && X < mStartingX - MOVE_SIZE) {
					            doActionLeft();
					        }
						
						
						    stoptimertask();
							stopMytimertask();
							if(tbtn_driver.isEnabled()==true)
							{
								if(prefs.getString("driverid", null).equals(""))
								{
									Utility.alertMessage(MapView_Activity.this, "Driver register first");
									tbtn_driver.setChecked(false);
									}
								else
								{
									//new httpSwitchBetweenMode().execute(); //httpSwitchBetweenMode task
									tbtn_driver.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on));
									new Deviceregister().execute();
								//	Intent i=new Intent(MapView_Activity.this,DriverView_Activity.class);
									Editor ed=prefs.edit();
									ed.putString("mode", "driver");
									ed.commit();
								//	startActivity(i);
									}
							}
							else
							{
								
							}
				      }
				return true;
				}
			});*/
			
			
/*			tbtn_driver.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				stoptimertask();
				stopMytimertask();
				if(tbtn_driver.isEnabled()==true)
				{
					if(prefs.getString("driverid", null).equals(""))
					{
						Utility.alertMessage(MapView_Activity.this, "Driver register first");
						tbtn_driver.setChecked(false);
						}
					else
					{
						//new httpSwitchBetweenMode().execute(); //httpSwitchBetweenMode task
						new Deviceregister().execute();
					//	Intent i=new Intent(MapView_Activity.this,DriverView_Activity.class);
						Editor ed=prefs.edit();
						ed.putString("mode", "driver");
						ed.commit();
					//	startActivity(i);
						}
				}
				else
				{
					
				}
			}
			});*/
			
			if(!(getIntent().getStringExtra("driver")==null))
			{
				new Deviceregister().execute();
				}

			
			
			
			
			
			
			
			
/**********************************************End of left side menu bar *************************************/
	
			
			/** map window on click save address of start and end location*/
	        googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				public void onInfoWindowClick(Marker marker) {
					 //validation destination save value
					if(marker.getTitle().equalsIgnoreCase("Set pickup location"))
					{
						Editor ed=prefs.edit();//putting destination value into share preference value 
				        ed.putFloat("cur_lati", (float) marker.getPosition().latitude); 
				        //ed.putInt("edit_startwindow", 1);
				        ed.putFloat("cur_logi", (float) marker.getPosition().longitude);
				        ed.putString("cur_address",  marker.getSnippet());
				        ed.commit();
				        autocompletetv_StartPlaces.setText("");
				        autocompletetv_StartPlaces.setText(prefs.getString("cur_address", ""));
				    	autocompletetv_StartPlaces.setEnabled(false);
				    	autocompletetv_StartPlaces.dismissDropDown();
						marker.hideInfoWindow();
					}
					else if(marker.getTitle().equalsIgnoreCase("Select Vehicle"))
					{
						for(int i=0;i<al_drivername.size();i++)
						{
							if(marker.getSnippet().equalsIgnoreCase(al_drivername.get(i)+"("+al_drivertime.get(i)+")"))
							{
								selectdriver= al_driverid.get(i);
								Editor ed=prefs.edit();//putting destination value into share preference value 
								ed.putInt("Getvehicletype",al_vehicleType.get(i));
								ed.putString("selectdriver", selectdriver);
								ed.commit();
								System.err.println("selected driverrrrr="+selectdriver);
								
								}
							
						}
						marker.hideInfoWindow();
					}
					
					else if(marker.getTitle().equalsIgnoreCase("Set Destination location"))
					{
						Editor ed=prefs.edit();//putting destination value into share preference value 
						ed.putFloat("des_lati", (float) marker.getPosition().latitude); 
						ed.putFloat("des_logi", (float) marker.getPosition().longitude);
						ed.putString("des_address",  marker.getSnippet());
		        		// ed.putInt("edit_deswindow", 1);
						ed.commit();
						autocompletetv_destinationPlaces.setText("");
						autocompletetv_destinationPlaces.setText(prefs.getString("des_address", ""));
						autocompletetv_destinationPlaces.setEnabled(false);
						autocompletetv_destinationPlaces.dismissDropDown();
						marker.hideInfoWindow();
						
									
						Log.d("tag", "start_latitude"+prefs.getFloat("cur_lati", (float) 00.00 ));
						String value=autocompletetv_StartPlaces.getText().toString();
						Log.i("tag","autocompletetv_StartPlaces:"+value);
						String value2=autocompletetv_destinationPlaces.getText().toString();
						Log.i("tag","autocompletetv_destinationPlaces:"+value2);
						if(DestinationShow==null || DestinationShow=="")
						{
							if(value.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select start location");
								}
					
							else if(value2.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select destination location");
								}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
									{
		//								Log.i("tag","Selected Type:)"+selectedVehicleType);
										stopMytimertask();
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2",String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
									else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
									}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
							}
						}
						else if(DestinationShow.equals("no"))
						{
							if(value.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select start location");
								}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
										{
										stopMytimertask();
										Log.i("tag","Selected Type:)"+selectedVehicleType);	
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2",String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
									else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
									}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
								}
						}
						else if(DestinationShow.equals("yes"))
						{
							if(value2.equals(""))
							{
								Utility.alertMessage(MapView_Activity.this,"Select destination location");
								}
							else
							{
								if(al_vehicleType.size()>0)
								{
									if(selectedVehicleType!=0)
									{
		//								
										stopMytimertask();
										Log.i("tag","Selected Type:)"+selectedVehicleType);	
										Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
										i.putExtra("VIP", "no");
										i.putExtra("VIP2", String.valueOf(selectedVehicleType));
										//i.putExtra("selectdriver", selectdriver);
										startActivity(i);
										finish();
										}
									else
									{
										Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
									}
								else
								{
									Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
									}
								}
							}

				
					}
					
			
					else if(marker.getTitle().equalsIgnoreCase("Current location"))
					{
						Editor ed=prefs.edit();//putting destination value into share preference value 
						ed.putFloat("cur_lati", (float) marker.getPosition().latitude); 
						ed.putFloat("cur_logi", (float) marker.getPosition().longitude);
						ed.putString("cur_address",  marker.getSnippet());
		        		// ed.putInt("edit_deswindow", 1);
						ed.commit();
						autocompletetv_StartPlaces.setText("");
						autocompletetv_StartPlaces.setText(prefs.getString("cur_address", ""));
						autocompletetv_StartPlaces.setEnabled(false);
						autocompletetv_StartPlaces.dismissDropDown();
						marker.hideInfoWindow();
						}
					
					if(prefs.getInt("edit_deswindow", 1)==0)
					{
						Editor ed=prefs.edit();//putting destination value into share preference value 
						ed.putFloat("des_lati", (float) marker.getPosition().latitude); 
						ed.putFloat("des_logi", (float) marker.getPosition().longitude);
						ed.putString("des_address",  marker.getSnippet());
		        		// ed.putInt("edit_deswindow", 1);
						ed.commit();
						autocompletetv_destinationPlaces.setText("");
						autocompletetv_destinationPlaces.setText(prefs.getString("des_address", ""));
						marker.hideInfoWindow();
						
						}
					else if(prefs.getInt("edit_startwindow", 1)==0)
					{
						Editor ed=prefs.edit();//putting destination value into share preference value 
				        ed.putFloat("cur_lati", (float) marker.getPosition().latitude); 
				        //ed.putInt("edit_startwindow", 1);
				        ed.putFloat("cur_logi", (float) marker.getPosition().longitude);
				        ed.putString("cur_address",  marker.getSnippet());
				        ed.commit();
				        autocompletetv_StartPlaces.setText("");
				        autocompletetv_StartPlaces.setText(prefs.getString("cur_address", ""));
						marker.hideInfoWindow();
				       
						}

				}
			}); 
	        
	        publishToggle = (MySwitch)findViewById(R.id.toggleButton_driver_on_off);
	        publishToggle.setChecked(true);
	        publishToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
//						System.err.println("yes");
//						Toast.makeText(MapView_Activity.this, "yes", Toast.LENGTH_SHORT).show();
					   
						 }
					else {
//						System.err.println("else");
//						Toast.makeText(MapView_Activity.this, "no", Toast.LENGTH_SHORT).show();
						
							stopMytimertask();
							
								if(prefs.getString("driverid", "").equals(""))
								{
									Utility.alertMessage(MapView_Activity.this, "You are not activated to drive yet.");
									publishToggle.setChecked(true);
									
									}
								else
								{
									//new httpSwitchBetweenMode().execute(); //httpSwitchBetweenMode task
//									tbtn_driver.setBackgroundDrawable(getResources().getDrawable(R.drawable.toggle_on));
									new Deviceregister().execute();
								//	Intent i=new Intent(MapView_Activity.this,DriverView_Activity.class);
									Editor ed=prefs.edit();
									ed.putString("mode", "driver");
									ed.commit();
								//	startActivity(i);
									}
								mDrawerLayout.closeDrawers();
								}
					
				}
			});
	       swtch_alertshow = (MySwitch)findViewById(R.id.toggleButton_showalert);
	      
		     if(prefs.getString("alert", "").equals("yes")) 
		     {
		    	  c= Calendar.getInstance();
	                hour = c.get(Calendar.HOUR_OF_DAY);
	                min = c.get(Calendar.MINUTE);
	                int ds = c.get(Calendar.AM_PM);
	                if(ds==0)
	                {
		                AM_PM="am";
		                System.err.println("am");
		                }
	                else
	                {
		                AM_PM="pm";
		                System.err.println("pm");
		                }
	              //  Toast.makeText(MapView_Activity.this, ""+hour+":"+min+AM_PM, Toast.LENGTH_SHORT).show();
	               if(AM_PM.matches("pm"))
	               {
	                if(hour>=11)
	                		{
	                			if(min>=00)
	                			{
	                				  if(value==null  || int_homepop==0 ||	getIntent().getStringExtra("help").equals("") ||getIntent().getStringExtra("DestinationShow").equals(""))
	                						 {
	                						// AlertBox(); 
	                						 }
	                				   	}
	                			}
	               }
	               else if(AM_PM.matches("am"))
	               {
	            	   if(hour<3)
               			{
	            		   if(value==null  || int_homepop==0 ||	getIntent().getStringExtra("help").equals("") ||getIntent().getStringExtra("DestinationShow").equals(""))
	            		   {
		      						// AlertBox(); 
		      						 }
        				   	
               			}
	            	   else if(hour==3)
	               			{
		            		   if(min<30)
		            		   {
		            			   if(value==null  || int_homepop==0 ||	getIntent().getStringExtra("help").equals("") ||getIntent().getStringExtra("DestinationShow").equals(""))
	              						 {
		              						// AlertBox(); 
		              						 }
		            		      }
	               			}
	               }
	     }
		else
		     {
		    	//  System.err.println("if no");
		    	 swtch_alertshow.setChecked(true);
			    // swtch_alertshow.setBackgroundResource(R.drawable.whitess); 
		     	}
	   
	   
	     swtch_alertshow.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		    @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked){
						//System.err.println("elseshow");
						//swtch_alertshow.setBackgroundResource(R.drawable.whitess);
						Editor ed=prefs.edit();
						ed.putString("alert", "no");
						ed.commit();
						 }
					else {
						//swtch_alertshow.setBackgroundResource(R.drawable.greenss);
						//System.err.println("ifshow");
						Editor ed=prefs.edit();
						ed.putString("alert", "yes");
						ed.commit();	
					}
					mDrawerLayout.closeDrawers();
				}
			});

	  
} 	
	  
	    
	    
	    
	    
	    
/************************************main function end  *******************************************/
	    
	  
 
	    
	    
	    //function of side bar menu
		public void onConfigurationChanged(Configuration newConfig) {
			super.onConfigurationChanged(newConfig);
			mDrawerToggle.onConfigurationChanged(newConfig);
		}
		public boolean onOptionsItemSelected(MenuItem item) {
		
			if (mDrawerToggle.onOptionsItemSelected(item)) {
				return true;
			}
		
			return super.onOptionsItemSelected(item);
		}
		//this methods sets the listeners on drawer layout
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

class SearchTask_StartLocation extends AsyncTask<String, String, String>{ 
	    
		    protected String doInBackground(String... str) {
		    com.rapidride.util.LocationFinder locFinder = new com.rapidride.util.LocationFinder(MapView_Activity.this);
		  	coordinates = locFinder.getLatLongFromAddress(latitude, longitude, str[0]);
//		    	if(trigger.equals("none")) //loadRecentPlaceArray();
		    	return "success";
		    	   }
			protected void onPreExecute() {
				  
				  progress1 = new ProgressDialog(MapView_Activity.this);
				  //progress1.setTitle("Rapid Ride");
				  progress1.setMessage("Please wait...");
				  progress1.setCancelable(false);
				  progress1.show();
			    }
			protected void onPostExecute(String result) {
			        super.onPostExecute(result);

			        progress1.dismiss();
			       
			        try{
				       // googleMap.clear();
			        	
				    	for(int i =0; i<1; i++)
				    	{
				    				    		
				    		Editor editor=prefs.edit();
							//editor.putInt("edit_startgreen", 1);
							editor.putFloat("cur_lati",  (float) coordinates.get(i).latitude);
					        editor.putFloat("cur_logi",  (float) coordinates.get(i).longitude);
					        editor.putString("cur_address",  coordinates.get(i).address);
					        Log.i("tag","Current Address:"+coordinates.get(i).address);
							editor.commit();
				 
						googleMap.clear();
						googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinates.get(i).latitude, coordinates.get(i).longitude), 15));
						MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(coordinates.get(i).latitude, coordinates.get(i).longitude)).title("Set pickup location").snippet(coordinates.get(i).address);
			    		sta_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
			    		googleMap.addMarker(sta_marker);
				    	}//for loop end
				    	latitude = prefs.getFloat("des_lati", (float) 0.0);
				        longitude = prefs.getFloat("des_logi", (float) 0.0);
				        Log.i("tag", "Get Address latitude:"+latitude);
				        Log.i("tag", "Get Address longitude:"+longitude);
					    LatLng latLng = new LatLng(latitude, longitude);
				      //  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latLng),15));
				        String address=prefs.getString("des_address", null);
				        Log.i("tag", "Get Address latitude:"+latitude);
				        Log.i("tag", "Get Address longitude:"+longitude);
//				        address_conveted= getAddress(getBaseContext(), latitude, longitude);
				        address_converted1=getAddress2(latitude, longitude);
				        
				        MarkerOptions destmarker = new MarkerOptions().position(latLng).title("Set Destination location").snippet(address);
				        destmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_flag_icon));
				        googleMap.addMarker(destmarker);
			        }catch(Exception e){
			        	
			        	e.printStackTrace();
			        }
			    }
		
	    }
class SearchTask_Destination extends AsyncTask<String, String, String>{ 
		    
		 protected String doInBackground(String... str) {
		    com.rapidride.util.LocationFinder locFinder = new com.rapidride.util.LocationFinder(MapView_Activity.this);
		  	coordinates = locFinder.getLatLongFromAddress(latitude, longitude, str[0]);
//		    	if(trigger.equals("none")) //loadRecentPlaceArray();
		    	return "success";
		    	   }
	
		 protected void onPreExecute() {
				  
				  progress1 = new ProgressDialog(MapView_Activity.this);
				//  progress1.setTitle("Rapid Ride");
				  progress1.setMessage("Please wait...");
				  progress1.setCancelable(false);
				  progress1.show();
			    }
		 protected void onPostExecute(String result) {
			        super.onPostExecute(result);

		        progress1.dismiss();
			        try{
				        googleMap.clear();
			        	for(int i =0; i<1; i++){
				    		Editor editor=prefs.edit();
							//editor.putInt("edit_startgreen", 1);
							editor.putFloat("des_lati",  (float) coordinates.get(i).latitude);
					        editor.putFloat("des_logi",  (float) coordinates.get(i).longitude);
					        editor.putString("des_address",  coordinates.get(i).address);
					        Log.i("tag","des_address :"+coordinates.get(i).address);
							editor.commit();
				    		
							googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(coordinates.get(i).latitude, coordinates.get(i).longitude), 15));
							MarkerOptions des_marker = new MarkerOptions().position(new  LatLng(coordinates.get(i).latitude,coordinates.get(i).longitude)).title("Set Destination location").snippet(coordinates.get(i).address);
							des_marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_flag_icon));
							System.err.println("destination address" +  des_marker.getSnippet());
							googleMap.addMarker(des_marker);
				    	}//for loop end
				      	latitude = prefs.getFloat("cur_lati", (float) 0.0);
				        longitude = prefs.getFloat("cur_logi", (float) 0.0);
				    
					    LatLng latLng = new LatLng(latitude, longitude);
				      //  googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latLng),15));
				        String address=prefs.getString("des_address", null);
				       // address_conveted= getAddress(getBaseContext(), latitude, longitude);
				        MarkerOptions destmarker = new MarkerOptions().position(latLng).title("Set pickup location").snippet(address);
				        destmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
				        googleMap.addMarker(destmarker);
				        
				        if(homeClick.equals("yes"))
				        {
				        	autocompletetv_destinationPlaces.setText(address);
				        	if(oneways)
							{
								Log.d("tag", "start_latitude"+prefs.getFloat("cur_lati", (float) 00.00 ));
								String value=autocompletetv_StartPlaces.getText().toString();
								Log.i("tag","autocompletetv_StartPlaces:"+value);
								String value2=autocompletetv_destinationPlaces.getText().toString();
								Log.i("tag","autocompletetv_destinationPlaces:"+value2);
								if(DestinationShow==null || DestinationShow=="")
								{
									if(value.equals(""))
									{
										Utility.alertMessage(MapView_Activity.this,"Select start location");
										}
							
									else if(value2.equals(""))
									{
										Utility.alertMessage(MapView_Activity.this,"Select destination location");
										}
									else
									{
										if(al_vehicleType.size()>0)
											{
												if(selectedVehicleType!=0)
													{
				//										Log.i("tag","Selected Type:)"+selectedVehicleType);
													stopMytimertask();
														Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
														i.putExtra("VIP", "no");
														i.putExtra("VIP2",String.valueOf(selectedVehicleType));
														//i.putExtra("selectdriver", selectdriver);
														startActivity(i);
														finish();
														}
													else
													{
														Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
														}
											}
										else
										{
											Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
										}
									}
								}
								else if(DestinationShow.equals("no"))
								{
									if(value.equals(""))
									{
										Utility.alertMessage(MapView_Activity.this,"Select start location");
										}
									else
									{
										if(al_vehicleType.size()>0)
										{
											if(selectedVehicleType!=0)
											{
												Log.i("tag","Selected Type:)"+selectedVehicleType);	
												
												stopMytimertask();
												Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
												i.putExtra("VIP", "no");
												i.putExtra("VIP2",String.valueOf(selectedVehicleType));
												//i.putExtra("selectdriver", selectdriver);
												startActivity(i);
												finish();
												}
											else
											{
												Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
												}
											}
										else
											{
												Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
												}
										}
									}
								else if(DestinationShow.equals("yes"))
								{
									if(value2.equals(""))
									{
										Utility.alertMessage(MapView_Activity.this,"Select destination location");
										}
									else
									{
										if(al_vehicleType.size()>0)
										{
											if(selectedVehicleType!=0)
											{
		//										Log.i("tag","Selected Type:)"+selectedVehicleType);	
												stopMytimertask();
												Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
												i.putExtra("VIP", "no");
												i.putExtra("VIP2", String.valueOf(selectedVehicleType));
												//i.putExtra("selectdriver", selectdriver);
												startActivity(i);
												finish();
												}
										else
										{
											Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
											}
										}
										else
										{
											Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
											}
									}
								}

						
							}
				        	else if(vips)
							{
								Log.d("tag", "start_latitude"+prefs.getFloat("cur_lati", (float) 00.00 ));
								String value=autocompletetv_StartPlaces.getText().toString();
								Log.i("tag","autocompletetv_StartPlaces:"+value);
								if(value.equals(""))
								{
									Utility.alertMessage(MapView_Activity.this,"Select start location");
									}
								else if(DestinationShow.equals("no"))
								{
									if(value.equals(""))
									{
										Utility.alertMessage(MapView_Activity.this,"Select start location");
										}
									}
								
								else{
										if(al_vehicleType.size()>0)
										{
										    if(selectedVehicleType!=0)
										    {
		//									    Log.i("tag","Selected Type:"+selectedVehicleType);
												
										    	stopMytimertask();
										    	Intent i=new Intent(MapView_Activity.this,LocationandPriceDetail.class);
												i.putExtra("VIP", "yes");
												i.putExtra("VIP2", String.valueOf(selectedVehicleType));
												//i.putExtra("selectdriver", selectdriver);
												startActivity(i);
												finish();
											    }
											else
											{
												Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
												}
											}
											else
											{
												Utility.alertMessage(MapView_Activity.this, "No "+str_vehiclename+" vehicle available in this area");
												}
								}}}
			        }catch(Exception e){
			        	
			        	e.printStackTrace();
			        }
			    }
		
	    }
		public void savePlace(String place)
			{
			    Editor editor = prefs.edit();
			    int curSize = prefs.getInt("size", 0);
			    editor.putString("Place_" + curSize, place);
			    editor.putInt("size", curSize + 1);
			    editor.commit();
				}
		public void loadRecentPlaceArray()
			{
				recentPlaces.clear();
			    int size = prefs.getInt("size", 0);
	
			    for(int i=0; i<size; i++) 
			    {
			        recentPlaces.add(prefs.getString("Place_" + i, ""));
			    }
			}

/**start position on map*//////
public void onStartLocation(Location location) {
	     try{
	        desvalue=1;
	       
	         // Getting latitude and longitude of the current location
	        start_latitude = prefs.getFloat("cur_lati", (float) 00.00 );
	        start_langitude = prefs.getFloat("cur_logi", (float) 00.00);
			  
		    LatLng latLng = new LatLng(start_latitude, start_langitude);
	        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latLng),15)); 
	      //  googleMap.setOnCameraChangeListener(mOnCameraChangeListener);
		    String address=  prefs.getString("cur_address", null);
		     //address_conveted= getAddress(getBaseContext(), start_latitude, start_langitude);
		    MarkerOptions startmarker = new MarkerOptions().position(new LatLng( start_latitude, start_langitude)).title("Set pickup location").snippet(address);
		    startmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
		    googleMap.addMarker(startmarker);
		    googleMap.getUiSettings().setZoomGesturesEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(false);
			autocompletetv_StartPlaces.setText(address);
		    googleMap.getUiSettings().setMapToolbarEnabled(false);
			 stopMytimertask();
		     startMyTimer();
		     longitude=start_langitude;
			 latitude=start_latitude;
//			 if(Utility.isConnectingToInternet(MapView_Activity.this))
//		        {
//		        	new FetchVehicleList().execute();
//			        }
//		        else
//		        {
//		        	Utility.alertMessage(, "No Internet Connection");
//		        	}
			Utility.array_list_latlng().clear();
		//	Utility.arraylist_longitude().clear();
		//	setVehicleonMap();

	      }catch(Exception e){
				e.printStackTrace();
	      }
	}
/**destination position on map */////
		public void onDesLocation(Location location) {
			   try{
//				    googleMap.clear();
				    desvalue=1;
			         // Getting latitude and longitude of the current location
			        latitude = prefs.getFloat("des_lati", (float) 0.0);
			        longitude = prefs.getFloat("des_logi", (float) 0.0);
			    
				    LatLng latLng = new LatLng(latitude, longitude);
			        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latLng),15));
			        String address=prefs.getString("des_address", null);
			        
			        // address_conveted= getAddress(getBaseContext(), latitude, longitude);
			        MarkerOptions destmarker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Set Destination location").snippet(address);
				    // Changing marker icon
			        destmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_flag_icon));
			        // adding marker
				    googleMap.addMarker(destmarker);
				    googleMap.getUiSettings().setZoomGesturesEnabled(true);
					googleMap.getUiSettings().setCompassEnabled(false);
					autocompletetv_destinationPlaces.setText(address);
				   googleMap.getUiSettings().setMapToolbarEnabled(false);
					Editor editor=prefs.edit();
					editor.putInt("editbutton_des", 1);
					editor.commit();
			       
			      }catch(Exception e){
					e.printStackTrace();
			      }
	}
		
/***first time location show on map view*///
	     public void onLocationChanged(Location location) {
	    	  try{
	     /**  Getting latitude and longitude of the current location*///
	    	googleMap.clear();
	    	latitude = location.getLatitude();
	        longitude = location.getLongitude();
	        currentpos = new LatLng(latitude, longitude);
	         
		/**  // Showing the current location in Google Map*////
	        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentpos,15));
	        
	        Log.i("tag", "Get Address latitude:"+latitude);
	        Log.i("tag", "Get Address longitude:"+longitude);
	        
//	        address_conveted= getAddress(getBaseContext(), latitude, longitude);
//	        Log.i("tag", "Address:"+address_conveted);
	        address_converted1=getAddress2(latitude,longitude);
	        MarkerOptions startmarker = new MarkerOptions().position(currentpos).title("Set pickup location").snippet(address_converted1);
		    /***Changing marker icon***///
	        startmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
	        googleMap.addMarker(startmarker);
	     	
	/***FetchVehicleList task **///        
	        if(Utility.isConnectingToInternet(MapView_Activity.this))
	        {
	        	new FetchVehicleList().execute();
		        }
	        else
	        {
	        	Utility.alertMessage(MapView_Activity.this, "No internet connection");
	        	}
	/***function of set vehicle on map   ***//////     
	        setVehicleonMap();

	    	Editor editor=prefs.edit();// store the starting value of 
			editor.putFloat("cur_lati", (float) latitude);
	        editor.putFloat("cur_logi", (float) longitude);
	        editor.putString("cur_address",  address_converted1);
	        editor.commit();
	        autocompletetv_StartPlaces.setText(address_converted1);	   
		    googleMap.getUiSettings().setZoomGesturesEnabled(true);
			googleMap.getUiSettings().setCompassEnabled(false);
		    googleMap.getUiSettings().setMapToolbarEnabled(false);	  
	      }catch(Exception e){
				e.printStackTrace();
	      }
		}
	 
	    public void onProviderDisabled(String provider) {
	    }
	    public void onProviderEnabled(String provider) {
	    }
	    public void onStatusChanged(String provider, int status, Bundle extras) {
	    }
/*** auto complete Adapter find  places  **/////
	  	    
	    private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		    private ArrayList<String> resultList;
		    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
		        super(context, textViewResourceId);
		    }
		    @Override
		    public int getCount() {
		        return resultList.size();
		    }
		    @Override
		    public String getItem(int index) {
		        return resultList.get(index);
		    }
		    @Override
		    public Filter getFilter() {
		        Filter filter = new Filter() {
		            @Override
		            protected FilterResults performFiltering(CharSequence constraint) {
		                FilterResults filterResults = new FilterResults();
		                if (constraint != null) {
		                    // Retrieve the autocomplete results.
		                    resultList = autocomplete(constraint.toString());
		                    
		                    for(int i =0; i<recentPlaces.size(); i++){
		                    	if(recentPlaces.get(i).startsWith(constraint.toString()))
		                    		resultList.add(recentPlaces.get(i));
		                    }
		                    
		                    // Assign the data to the FilterResults
		                    filterResults.values = resultList;
		                    filterResults.count = resultList.size();
		                }
		                return filterResults;
		            }

		            @Override
		            protected void publishResults(CharSequence constraint, FilterResults results) {
		                if (results != null && results.count > 0) {
		                    notifyDataSetChanged();
		                }
		                else {
		                    notifyDataSetInvalidated();
		                }
		            }};
		        return filter;
		    }  
	    }
/*** auto complete search location function with url name **/////
		private ArrayList<String> autocomplete(String input) {
		    ArrayList<String> resultList = null;
		    
		    HttpURLConnection conn = null;
		    StringBuilder jsonResults = new StringBuilder();
		    try {
		        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
		        sb.append("?location="+latitude+","+longitude+"&radius="+getResources().getString(R.string.near_by_radius)+"&sensor=false&key=" + API_KEY); //search auto text box location
		        sb.append("&components=country:"+str_countryid);
		        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
		        
		        URL url = new URL(sb.toString());
		        conn = (HttpURLConnection) url.openConnection();
		        InputStreamReader in = new InputStreamReader(conn.getInputStream());
		        
		        // Load the results into a StringBuilder
		        int read;
		        char[] buff = new char[1024];
		        while ((read = in.read(buff)) != -1) {
		            jsonResults.append(buff, 0, read);
		        }
		    } catch (MalformedURLException e) {
		        Log.e(LOG_TAG, "Error processing Places API URL", e);
		        return resultList;
		    } catch (IOException e) {
		        Log.e(LOG_TAG, "Error connecting to Places API", e);
		        return resultList;
		    } finally {
		        if (conn != null) {
		            conn.disconnect();
		        }
		    }
		    
		    Log.e("prediction result", jsonResults.toString());
		    
		    try {
		        /** Create a JSON object hierarchy from the results8*/
		        JSONObject jsonObj = new JSONObject(jsonResults.toString());
		        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
		       
		        resultList = new ArrayList<String>();
		        for (int i = 0; i < predsJsonArray.length(); i++) {
		            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
		        }
		    } catch (JSONException e) {
		        Log.e(LOG_TAG, "Cannot process JSON results", e);
		    }
		    
		    return resultList;
		}
/**end of auto complete search result **///

/**find address name from latitude or longitude**///
		public  String getAddress(Context ctx, double latitude, double longitude) {
		        StringBuilder result = new StringBuilder();
		        try {
		            Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
		            List<Address>
		            addresses = geocoder.getFromLocation(latitude, longitude, 1);
		            if (addresses.size() > 0) {
		                Address address = addresses.get(0);
		     
				    String locality=address.getLocality();
				    String city=address.getCountryName();
				    String region_code=address.getCountryCode();
				    String zipcode=address.getPostalCode();
				  
				    result.append(locality+" ");
				    result.append(city+" "+ region_code+" ");
				    result.append(zipcode);
		     
		            }
		        } catch (IOException e) {
		            Log.e("tag", e.getMessage());
		        }
		 
		        return result.toString();
		    }
/***showOverlayInstructions	**///
		public void showOverlayInstructions(final String destination){
		
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(autocompletetv_StartPlaces.getWindowToken(), 0);		
		}
			
/*** adapter for vehicles name***///
		public class flagAdapter extends ArrayAdapter<String>{
			 
		        public flagAdapter(Context context, int textViewResourceId,   String[] objects) {
		            super(context, textViewResourceId, objects);
		        }
		 
		        @Override
		        public View getDropDownView(int position, View convertView,ViewGroup parent) {
		            return getCustomView(position, convertView, parent);
		        }
		 
		        @Override
		        public View getView(int position, View convertView, ViewGroup parent) {
		            return getCustomView(position, convertView, parent);
		        }
		 
		        public View getCustomView(int position, View convertView, ViewGroup parent) {
		 
		           LayoutInflater inflater=getLayoutInflater();
		           View row=inflater.inflate(R.layout.flag_adapter, parent, false);
		           txtview_vehiclelabel=(TextView)row.findViewById(R.id.company);
		           txtview_vehiclelabel.setText(strings[position]);
		            return row;
		            }
		        }
		 
		/**images download **///
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
		    		iv_profilepic.setImageBitmap(Bitmap.createScaledBitmap(result, 90, 80, false));
		    		//iv_profilepic.setImageBitmap(result);	
		    	}
		    }
		    }
/***AsyncTask class for fetch vehicle list**////
public class FetchVehicleList extends AsyncTask<Void, Void, Void> {
		 protected void onPreExecute() {
			 al_vehicle_coordinates.clear();
			 al_drivertime.clear();
			 al_drivername.clear();
			 al_driverid.clear();
			 al_stringvehicleType.clear();
			 super.onPreExecute();
				}
		protected Void doInBackground(Void... arg0) {
			 		try {
						HttpParams httpParameters = new BasicHttpParams();
						int timeoutConnection = 60000;
						HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
						int timeoutSocket = 61000;
						HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
						HttpClient client = new DefaultHttpClient(httpParameters);
						HttpPost httpost = new HttpPost(Url_Address.url_Home+"/FetchVehicleList");
						JSONObject json = new JSONObject();
								
						json.put("Trigger", "FetchVehicleList");
//						System.err.println("FetchVehicleList");
						
						json.put("longitude", longitude);
//						System.err.println("longitude="+longitude);
						 	
					    json.put("latitude", latitude);
//					    System.err.println("latitude="+latitude);
					    
				        json.put("currenttime",str_currentdate);
//				        System.err.println("str_currentdate-->"+str_currentdate);
				      
				        json.put("distance",10);
				        
				        json.put("riderid",prefs.getString("userid",null));
				        System.err.println("riderid-"+prefs.getString("userid",null));
					     
			
				        httpost.setEntity(new StringEntity(json.toString()));
						httpost.setHeader("Accept", "application/json");
						httpost.setHeader("Content-type", "application/json");
						
						HttpResponse response = client.execute(httpost);
						HttpEntity resEntityGet = response.getEntity();
						String jsonstr=EntityUtils.toString(resEntityGet);
						
						if(jsonstr!=null)
						{
						// Log.i("FetchVehicleList","result vehicle-->   "+ jsonstr);
						}
						JSONObject obj=new JSONObject(jsonstr);
						jsonResult_fetchvehicle=obj.getString("result");
						jsonMessage_fetchvehicle=obj.getString("message");
						
//						Log.i("FetchVehicleList:", "Result: "+jsonResult_fetchvehicle);
//						Log.i("FetchVehicleList:", "Message :"+jsonMessage_fetchvehicle);
						
						String vehiclelist=obj.getString("ListVehicleInfo");
//						System.err.println("list "+vehiclelist);
						Log.i("tag","FetchVehicle List:"+vehiclelist);
						
						
						JSONArray jsonarray=obj.getJSONArray("ListVehicleInfo");
						Utility.arraylist_driverInfo().clear();
						
						for(int i=0;i<jsonarray.length();i++)
						{
							
							JSONObject obj2=jsonarray.getJSONObject(i);
							BeanDriver driverObj=new BeanDriver();
							 getdriverid=obj2.getString("driverid");
//							Log.i("ListVehicleInfo:", "driverid: "+driverid);
							driverObj.setDriverID(getdriverid);
							
							driverfirstname=	obj2.getString("firstname");
//							Log.i("ListVehicleInfo:", "firstname: "+firstname);
							
							 driverlastname=	obj2.getString("lastname");
//							Log.i("ListVehicleInfo:", "lastname: "+lastname);
							
						//	String driverimage=	obj2.getString("driverimage");
//							Log.i("ListVehicleInfo:", "driverimage: "+driverimage);
							
				//			String vehicleimage=obj2.getString("vehicleimage");
//							Log.i("ListVehicleInfo:", "validationstatus: "+vehicleimage);
							
							str_vehicletype=obj2.getString("vehicletype");
//							Log.i("ListVehicleInfo:", "vehicletype: "+str_vehicletype);
							driverObj.setVehicleType(str_vehicletype);
							
							String	longitude=	obj2.getString("longitude");
//							Log.i("ListVehicleInfo:", "longitude: "+longitude);
							driverObj.setDriverLongitude(longitude);
							
							String latitude=obj2.getString("latitude");
//							Log.i("ListVehicleInfo:", "latitude: "+latitude);
							driverObj.setDriverLatitude(latitude);
							
							 driverdistance=	obj2.getString("distance");
//							Log.i("ListVehicleInfo:", "distance: "+distance);
							driverObj.setDriverDistance(driverdistance);
							
							 drivertime=	obj2.getString("Time");
//								Log.i("ListVehicleInfo:", "distance: "+distance);
								driverObj.setDriverDistance(drivertime);
							if(!(longitude==null))
							{
								try {
					                  longi = Double.parseDouble(longitude);
					                  lati= Double.parseDouble(latitude);
					              } catch (NumberFormatException nfe) {
//					                 System.out.println("NumberFormatException: " + nfe.getMessage());
					              }
							}
							
							Driver_Coordinates d_cdn = new Driver_Coordinates();
							d_cdn.latitude=lati;
							d_cdn.longitude=longi;
							al_vehicle_coordinates.add(d_cdn);
							al_drivername.add(driverfirstname+driverlastname);
							al_drivertime.add(drivertime);
							al_driverid.add(getdriverid);
							al_stringvehicleType.add(str_vehicletype);
							if((str_vehicletype!=null))
							{
								int int_value=Integer.parseInt(str_vehicletype);
								Editor ed=prefs.edit();
								ed.putInt("Getvehicletype",int_value);
								ed.commit();
								al_vehicleType.add(int_value);
							}
							
							
							//Utility.arraylist_longitude().add(longitude);
						    Utility.arraylist_drivername().add(driverfirstname+driverlastname);
						    
							Utility.arraylist_driverInfo().add(driverObj);
							
							Log.d("tag","Size Of driver info:"+Utility.arraylist_driverInfo().size());
						}
						
						JSONArray jsonarray_ListVehicleInfo=obj.getJSONArray("ListZoneInfo");
						
						for(int i=0;i<jsonarray_ListVehicleInfo.length();i++)
						{
							
							JSONObject obj3=jsonarray_ListVehicleInfo.getJSONObject(i);
							Editor ed=prefs.edit();
							String zone_id=obj3.getString("zone_id");
//							Log.i("ListZoneInfo:", "zone_id: "+zone_id);
							ed.putString("zone_id",zone_id);
							
							String	zip_code=	obj3.getString("zip_code");
//							Log.i("ListZoneInfo:", "zip_code: "+zip_code);
							ed.putString("zip_code",zip_code);
							
							String reg_price=	obj3.getString("reg_price");
//							Log.i("ListZoneInfo:", "reg_price: "+reg_price);
							ed.putString("reg_price",reg_price);
							
							String xl_price=	obj3.getString("xl_price");
//							Log.i("ListZoneInfo:", "xl_price: "+xl_price);
							ed.putString("xl_price",xl_price);
							
							String exec_price=	obj3.getString("exec_price");
//							Log.i("ListZoneInfo:", "exec_price: "+exec_price);
							ed.putString("exec_price",exec_price);
							
							String lux_price=obj3.getString("lux_price");
//							Log.i("ListZoneInfo:", "lux_price: "+lux_price);
							ed.putString("lux_price",lux_price);
							
							String	truck_price=	obj3.getString("truck_price");
//							Log.i("ListZoneInfo:", "truck_price: "+truck_price);
							ed.putString("truck_price",truck_price);
							
							String latlong=	obj3.getString("latlong");
//							Log.i("ListZoneInfo:", "latitude: "+latlong);
							ed.putString("latlong",latlong);
							
							String reg_base=obj3.getString("reg_base");
//							Log.i("ListZoneInfo:", "reg_base: "+reg_base);
							ed.putString("reg_base",reg_base);
							
							String	xl_base=	obj3.getString("xl_base");
//							Log.i("ListZoneInfo:", "xl_base: "+xl_base);
							ed.putString("xl_base",xl_base);
							
							String exec_base=	obj3.getString("exec_base");
//							Log.i("ListZoneInfo:", "exec_base: "+exec_base);
							ed.putString("exec_base",exec_base);
							
							String lux_base=	obj3.getString("lux_base");
//							Log.i("ListZoneInfo:", "lux_base: "+lux_base);
							ed.putString("lux_base",lux_base);
							
							String truck_base=	obj3.getString("truck_base");
//							Log.i("ListZoneInfo:", "truck_base: "+truck_base);
							ed.putString("truck_base",truck_base);
							
							String reg_min=obj3.getString("reg_min");
//							Log.i("ListZoneInfo:", "reg_min: "+reg_min);
							ed.putString("reg_min",reg_min);
							
							String	xl_min=	obj3.getString("xl_min");
//							Log.i("ListZoneInfo:", "xl_min: "+xl_min);
							ed.putString("xl_min",xl_min);
							
							String exec_min=	obj3.getString("exec_min");
//							Log.i("ListZoneInfo:", "exec_min: "+exec_min);
							ed.putString("exec_min",exec_min);
							
							String lux_min=obj3.getString("lux_min");
//							Log.i("ListZoneInfo:", "lux_min: "+lux_min);
							ed.putString("lux_min",lux_min);
							
							String	truck_min=	obj3.getString("truck_min");
//							Log.i("ListZoneInfo:", "truck_min: "+truck_min);
							ed.putString("truck_min",truck_min);
							
							String suv_price=	obj3.getString("suv_price");
//							Log.i("ListZoneInfo:", "suv_price: "+suv_price);
							ed.putString("suv_price",suv_price);
							
							String suv_base=	obj3.getString("suv_base");
//							Log.i("ListZoneInfo:", "suv_base: "+suv_base);
							ed.putString("suv_base",suv_base);
							
							String suv_min=	obj3.getString("suv_min");
//							Log.i("ListZoneInfo:", "suv_min: "+suv_min);
							ed.putString("suv_min",suv_min);
							
							String reg_surge=obj3.getString("reg_surge");
//							Log.i("ListZoneInfo:", "reg_surge: "+reg_surge);
							ed.putString("reg_surge",reg_surge);
							
							String	xl_surge=	obj3.getString("xl_surge");
//							Log.i("ListZoneInfo:", "xl_surge: "+xl_surge);
							ed.putString("xl_surge",xl_surge);
							
							String exec_surge=	obj3.getString("exec_surge");
//							Log.i("ListZoneInfo:", "exec_surge"+exec_surge);
							ed.putString("exec_surge",exec_surge);
							
							String lux_surge=obj3.getString("lux_surge");
//							Log.i("ListZoneInfo:", "lux_surge: "+lux_surge);
							ed.putString("lux_surge",lux_surge);
							
							String	suv_surge=	obj3.getString("suv_surge");
//							Log.i("ListZoneInfo:", "suv_surge: "+suv_surge);
							ed.putString("suv_surge",suv_surge);
							
							String truck_surge=	obj3.getString("truck_surge");
//							Log.i("ListZoneInfo:", "truck_surge: "+truck_surge);
							ed.putString("truck_surge",truck_surge);
							
							String reg_hour=obj3.getString("reg_hour");
//							Log.i("ListZoneInfo:", "reg_hour: "+reg_hour);
							ed.putString("reg_hour",reg_hour);
							
							String xl_hour=	obj3.getString("xl_hour");
//							Log.i("ListZoneInfo:", "xl_hour: "+xl_hour);
							ed.putString("xl_hour",xl_hour);
							
							String exec_hour=obj3.getString("exec_hour");
//							Log.i("ListZoneInfo:", "exec_hour: "+exec_hour);
							ed.putString("exec_hour",exec_hour);
							
							String	suv_hour=	obj3.getString("suv_hour");
//							Log.i("ListZoneInfo:", "suv_hour: "+suv_hour);
							ed.putString("suv_hour",suv_hour);
							
							String lux_hour=	obj3.getString("lux_hour");
//							Log.i("ListZoneInfo:", "lux_hour: "+lux_hour);
							ed.putString("lux_hour",lux_hour);
							
							String reg_hourfull=	obj3.getString("reg_hourfull");
//							Log.i("ListZoneInfo:", "reg_hourfull: "+reg_hourfull);
							ed.putString("reg_hourfull",reg_hourfull);
							
							String xl_hourfull=obj3.getString("xl_hourfull");
//							Log.i("ListZoneInfo:", "xl_hourfull: "+xl_hourfull);
							ed.putString("xl_hourfull",xl_hourfull);
							
							String	exec_hourfull=	obj3.getString("exec_hourfull");
//							Log.i("ListZoneInfo:", "exec_hourfull: "+exec_hourfull);
							ed.putString("exec_hourfull",exec_hourfull);
							
							String suv_hourfull=	obj3.getString("suv_hourfull");
//							Log.i("ListZoneInfo:", "suv_hourfull"+suv_hourfull);
							ed.putString("suv_hourfull",suv_hourfull);
							
							String lux_hourfull=obj3.getString("lux_hourfull");
//							Log.i("ListZoneInfo:", "lux_hourfull: "+lux_hourfull);
							ed.putString("lux_hourfull",lux_hourfull);
							
							String	truck_hour=	obj3.getString("truck_hour");
//							Log.i("ListZoneInfo:", "truck_hour: "+truck_hour);
							ed.putString("truck_hour",truck_hour);
							
							String truck_hourfull=	obj3.getString("truck_hourfull");
//							Log.i("ListZoneInfo:", "truck_hourfull: "+truck_hourfull);
							ed.putString("truck_hourfull",truck_hourfull);
							
							String lat_a=obj3.getString("lat_a");
//							Log.i("ListZoneInfo:", "lat_a: "+lat_a);
							ed.putString("lat_a",lat_a);
							
							String long_a=obj3.getString("long_a");
//							Log.i("ListZoneInfo:", "long_a: "+long_a);
							ed.putString("long_a",long_a);
							
							String lat_b=obj3.getString("lat_b");
//							Log.i("ListZoneInfo:", "lat_b: "+lat_b);
							ed.putString("lat_b",lat_b);
							
							String	long_b=	obj3.getString("long_b");
//							Log.i("ListZoneInfo:", "long_b: "+long_b);
							ed.putString("long_b",long_b);
							
							String lat_c= obj3.getString("lat_c");
//							Log.i("ListZoneInfo:", "lat_c: "+lat_c);
							ed.putString("lat_c",lat_c);
						
							String	long_c=	obj3.getString("long_c");
//							Log.i("ListZoneInfo:", "long_c: "+long_c);
							ed.putString("long_c",long_c);
							
							String lat_d=	obj3.getString("lat_d");
//							Log.i("ListZoneInfo:", "lat_d: "+lat_d);
							ed.putString("lat_d",lat_d);
							
							String long_d=	obj3.getString("long_d");
//							Log.i("ListZoneInfo:", "long_d: "+long_d);
							ed.putString("long_d",long_d);
							
							
							/**change**/						
							String reg_minbase=obj3.getString("reg_minbase");
							Log.i("reg_minbase:", "reg_minbase: "+reg_minbase);
							ed.putString("reg_minbase",reg_minbase);
							
							String	xl_minbase=	obj3.getString("xl_minbase");
							Log.i("xl_minbase:",  xl_minbase);
							ed.putString("xl_minbase",xl_minbase);
							
							String exec_minbase= obj3.getString("exec_minbase");
							Log.i("exec_minbase:",  exec_minbase);
							
							ed.putString("exec_minbase",exec_minbase);
						
							String	suv_minbase=	obj3.getString("suv_minbase");
							Log.i("suv_minbase:",  suv_minbase);
							ed.putString("suv_minbase",suv_minbase);
							
							String lux_minbase=	obj3.getString("lux_minbase");
							Log.i("lux_minbase:",  lux_minbase);
							ed.putString("lux_minbase",lux_minbase);
							
							String truck_minbase=	obj3.getString("truck_minbase");
							Log.i("truck_minbase:",  truck_minbase);
							
							ed.putString("truck_minbase",truck_minbase);
							/**change**/	
											
							ed.commit();				
						}
				}
						  catch(Exception e){
						   System.out.println(e);
//						   exeception=1;
						   Log.d("tag", "Error :"+e);
						   }
						
				
				return null;
			}

		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
//				if(exeception==1)
//				{
//					Utility.alertMessage(MapView_Activity.this,"Internet connection failed. Please Try again later.");
//				}
//				else {
				setVehicleonMap();
//				}
		}
}

		 public class AddPrefrenceVechileTypeParsing extends AsyncTask<Void, Void, Void> {
			 protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
//				pDialog = new ProgressDialog(MapView_Activity.this);
//				pDialog.setMessage("Please wait...");
//				pDialog.setCancelable(false);
//				pDialog.show();
				}
		
		protected Void doInBackground(Void... arg0) {

					try {
//						HttpParams httpParameters = new BasicHttpParams();
//						int timeoutConnection = 30000;
//						HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
//						int timeoutSocket = 31000;
//						HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
//						HttpClient client = new DefaultHttpClient(httpParameters);
//						HttpPost httpost = new HttpPost(Url_Address.AddPrefferedVehicleType);
//						JSONObject json = new JSONObject();
//						
//						String userID=prefs.getString("userid", "");
//						json.put("RiderId", prefs.getString("userid", ""));
//						Log.i("tag","RiderId: "+userID);
//						
//						json.put("Pref_Vehicle_Type", positionV);
//						Log.i("tag","Pref_Vehicle_Type: "+positionV);
//						
//						System.err.println("longitude="+longitude);
//				
//						httpost.setEntity(new StringEntity(json.toString()));
//						httpost.setHeader("Accept", "application/json");
//						httpost.setHeader("Content-type", "application/json");
//						
//						HttpResponse response = client.execute(httpost);
//						HttpEntity resEntityGet = response.getEntity();
//						String jsonstr=EntityUtils.toString(resEntityGet);
//						
//						Log.i("tag", "Result:"+jsonstr);
//						JSONObject obj=new JSONObject(jsonstr);
//						jsonResult=obj.getString("result");
//						Log.d("tag", "jsonResult:"+jsonResult);
					}
					catch(Exception e)
					{
						
					}
				return null;
			}

		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
//				pDialog.dismiss();
				//new distanceCalculator().execute(); 
//				if(jsonResult.equalsIgnoreCase("0"))
//				{
				Editor editor=prefs.edit();
			    editor.putInt("vehicletype",positionV);
			    editor.commit();
				int vtype=prefs.getInt("vehicletype",0);
				Log.d("tag","Latest V Type:"+vtype);
				sp_flaglist.setSelection((vtype-1));
				positionV=0;
				setVehicleonMap();
//				}
//				else
//				{
//					
//				}
		}
}		 
		 
 /***fetchVehicleListFunction	**///		
public void setVehicleonMap()
{
			
	if(al_vehicle_coordinates.size()>0)
	{
		for(int j=0;j<al_vehicle_coordinates.size();j++)
		{
			//lati=vehicle_coordinates.get(0).latitude;
			//longi=vehicle_coordinates.get(0).longitude;
		  if(Utility.arraylist_driverInfo().size()>0)
			{
				System.err.println("lati...."+lati);
				System.err.println("longi...."+longi);
		       // MarkerOptions multimarker = new MarkerOptions().position(new LatLng(al_vehicle_coordinates.get(j).latitude, al_vehicle_coordinates.get(j).longitude)).title("Select Vehicle").snippet(al_drivername.get(j)+"("+al_drivertime.get(j)+")");//address_conveted);
				MarkerOptions multimarker = new MarkerOptions().position(new LatLng(al_vehicle_coordinates.get(j).latitude, al_vehicle_coordinates.get(j).longitude)).title("");//address_conveted);
				System.err.println("vehicletype "+prefs.getInt("Getvehicletype", 1));
//			    int getFetchVehicleType=prefs.getInt("Getvehicletype", 0);
			    selectedVehicleType=prefs.getInt("vehicletype", 1);
			    
		    for(int i=0;i<al_vehicleType.size();i++)
		    {
			    if(selectedVehicleType==al_vehicleType.get(i))
			    {
			    	  if(selectedVehicleType==1)
				        {
				        	multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car));
				        	googleMap.addMarker(multimarker);
				        	}
			    	   else if(selectedVehicleType==2)
				        {
				        	multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.xl_car));
				        	googleMap.addMarker(multimarker);
				        	}
				        else if(selectedVehicleType==3)
				        {
				        	multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.exec_car));
				        	googleMap.addMarker(multimarker);
				        	}
				        else if(selectedVehicleType==4)
				        {
				        	multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.suv_car));
				        	googleMap.addMarker(multimarker);
				        	}
				        else if(selectedVehicleType==5)
				        {
				        	multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.lux_car));
				        	googleMap.addMarker(multimarker);
				        	}
				        else {
				        	  multimarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.small_car));
				        	  googleMap.addMarker(multimarker);
				        	}
//			    	 Log.d("tag","Selected Type:"+selectedVehicleType);
					}
			    
			    else
			    {
			    	selectedVehicleType=0;
			    	googleMap.clear();	
			    	address_converted1=getAddress2(latitude,longitude);
			    	MarkerOptions startmarker = new MarkerOptions().position(currentpos).title("Set pickup location").snippet(address_converted1);
				    /***Changing marker icon***///
			        startmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.green_flag_icon));
			        googleMap.addMarker(startmarker);
			        
				    float checkval =prefs.getFloat("des_lati", (float) 0.0);    
//				    Log.i("tag","Check Value of Destination:"+checkval);
				    
				    if(checkval==0.0)
				    {}
				    else
				    {
				    	latitude = prefs.getFloat("des_lati", (float) 0.0);
				        longitude = prefs.getFloat("des_logi", (float) 0.0);
				    
					    LatLng latLng = new LatLng(latitude, longitude);
					    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom((latLng),15));
				        String address=prefs.getString("des_address", null);
				       // address_conveted= getAddress(getBaseContext(), latitude, longitude);
				        MarkerOptions destmarker = new MarkerOptions().position(latLng).title("Set Destination location").snippet(address);
				        destmarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.red_flag_icon));
				        googleMap.addMarker(destmarker);
				    	}
			    	}
		    	}
			}
		}
	}	
}
	
	public void intializeMap()
	{
	   		googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
	        if(Utility.isConnectingToInternet(MapView_Activity.this))
	        {
	        	googleMap.setTrafficEnabled(true);
		   		if (googleMap != null) {
	             // The Map is verified. It is now safe to manipulate the map.
	    		googleMap.setMyLocationEnabled(true);
	    		googleMap.getUiSettings().setMyLocationButtonEnabled(false);
	        }
	        else
	        {
	        	Utility.alertMessage(MapView_Activity.this, "No internet connection");
	        }
         }
	}
	

/******DRIVER Register ************/
	private class Deviceregister extends AsyncTask<Void, Void, Void> { // Async_task class
	private ProgressDialog pDialog;
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		pDialog = new ProgressDialog(MapView_Activity.this);
		exeception=0;
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
		}
	protected Void doInBackground(Void... arg0) {
		try {
		 parsing();
		 } catch (Exception e) {
		 e.printStackTrace();
		}
		
		return null;
		}
	
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		pDialog.dismiss();
	    if(exeception==1)
		    {
		    	Utility.alertMessage(MapView_Activity.this,"Internet connection failed");
		    	}
		    else
			if(!(value==null))
			{
			System.err.println("if");
			value=null;
			}
			else
			{
				Editor ed=prefs.edit();
				ed.putString("mode", "driver");
				ed.commit();
				Intent i=new Intent(MapView_Activity.this,DriverView_Activity.class);
				startActivity(i);
				finish();
				System.err.println("else");
				}

		
		}
	
		/****password parsing function **/
		public void parsing() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 60000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 61000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/RegisterDevice");
		json = new JSONObject();
		
		System.err.println("url="+Url_Address.url_Home+"/RegisterDevice");
//		json.put("Trigger", "RegisterDevice");
		if(!(value==null))
		{
			json.put("Role", "rider");
			System.err.println("ddddddddrrrrriver Role=rider");
			
			}
		else
		{
			json.put("Role", "driver");
			System.err.println("riderrrrrrrrrrrrrr Role=driver");
		}
		json.put("DriverId",prefs.getString("driverid", null));
		System.err.println("ride driverid="+prefs.getString("driverid", null));
		
//		json.put("RiderId", prefs.getString("riderid", null));
//		System.err.println("ridreeeeeeeeeeeeeeeeeeeeeeeeeee riderid="+prefs.getString("userid", null));
		
		json.put("DeviceUDId",prefs.getString("uuid", null));
		System.err.println("rider uuid="+prefs.getString("uuid", null));
		
		json.put("TokenID", prefs.getString("regid", null));
		System.err.println("rider redid="+prefs.getString("regid", null));
		
		json.put("Trigger", "android");
		System.err.println("rider trigger="+"android");
		
		json.put("RiderId",prefs.getString("userid", null));
		System.err.println("rider riderid="+prefs.getString("userid", null));
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
		 Log.e("tag","RegisterDevice>>>>>>>    "+ jsonstr);
		 
			}
			JSONObject obj=new JSONObject(jsonstr);
		String	   jsonResult=obj.getString("result");
		String	jsonMessage=obj.getString("message");
			
			Log.i("tag:", "Result: "+jsonResult);
			Log.i("tag:", "Message :"+jsonMessage);
			}
		  catch(Exception e){
		   System.out.println(e);
		   exeception=1;
		   Log.d("tag", "Error :"+e); }  
			}
		}
	
@Override
protected void onResume() {
	stopMytimertask();
	startMyTimer();
	String Message=prefs.getString("Message", "");
	if(Message==null || Message=="")
	{
		}
	else
	{
		String msg=Message;
		//prefs = getSharedPreferences("RapidRide", MODE_PRIVATE);
	    Editor e1=prefs.edit();
		e1.putString("OnlyMessage", "no");
		e1.commit();
		boolean contain=msg.contains("@");
		if(contain==true){
			
		String value[]=msg.split("@");
		String DriverName=value[0];
		String messages=value[1];
		String DriverImage=value[2];
		String[] temptripID=value[3].trim().split("\\(\\)");
		String tempTripID=temptripID[0].split(":")[1];
		String tripID=tempTripID;
		
		Intent launchDialog = new Intent(MapView_Activity.this, ShowNotificationRiderSide.class);
		launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		launchDialog.putExtra("DriverName", DriverName);
		launchDialog.putExtra("messages", messages);
		launchDialog.putExtra("DriverImage", DriverImage);
		launchDialog.putExtra("tripID", tripID);
		finish();
		startActivity(launchDialog);
		Editor e=prefs.edit();
		e.putString("Message", "");
		e.commit();
		}
	else
	{
	boolean contains=msg.contains(";");
	if(contains==true)
	{
		String getMessage[]=msg.split(";");
		String messages=getMessage[0];
	   	//prefs = getSharedPreferences("RapidRide", MODE_PRIVATE);
    	Editor e=prefs.edit();
    	e.putString("OnlyMessage", messages);
    	e.putString("Message", "");
    	e.commit();
    	Intent launchDialog = new Intent(MapView_Activity.this, ShowNotificationRiderSide.class);
    	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	finish();
    	startActivity(launchDialog);
    	
	}
	else{
	//prefs = getSharedPreferences("RapidRide", MODE_PRIVATE);
		Editor e=prefs.edit();
		e.putString("OnlyMessage", Message);
		e.putString("Message", "");
		e.commit();
		Intent launchDialog = new Intent(MapView_Activity.this, ShowNotificationRiderSide.class);
		launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		finish();
		startActivity(launchDialog);
		}
	}
	
}
super.onResume();
}
public String getAddress2(double longitude,double latitude) {
	
	String formatAddress="";
    try {
    	
        JSONObject jsonObj = Parser_json.getJSONfromURL("http://maps.googleapis.com/maps/api/geocode/json?latlng=" + longitude + ","
                + latitude + "&sensor=true");
        String Status = jsonObj.getString("status");
      
        if (Status.equalsIgnoreCase("OK")) {
            JSONArray Results = jsonObj.getJSONArray("results");
            
            for(int i=0;i<Results.length();i++)
            {
            	JSONObject obj=Results.getJSONObject(0);
            	formatAddress=obj.getString("formatted_address");
            	Log.i("tag", "ForMatted Address:"+formatAddress);
            }
            JSONObject zero = Results.getJSONObject(0);
            JSONArray address_components = zero.getJSONArray("address_components");
            
            for (int i = 0; i < address_components.length(); i++) {
                JSONObject zero2 = address_components.getJSONObject(i);
                String long_name = zero2.getString("long_name");
//                String county_code = zero2.getString("short_name");
//                Log.e("county_code=", county_code);
                JSONArray mtypes = zero2.getJSONArray("types");
                String Type = mtypes.getString(0);

                if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                    if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = long_name + " ";
                    } else if (Type.equalsIgnoreCase("route")) {
                        Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("sublocality")) {
                        Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (Type.equalsIgnoreCase("administrative_area_level_2")) {
                        County = long_name;
                    } else if (Type.equalsIgnoreCase("administrative_area_level_1")) {
                        State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        CountryName = long_name;
                        Log.e("county=", CountryName);
                        
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        PIN = long_name;
                    }
                  //str_countryid=getCode(CountryName);
                }
            }
        }
       
    } catch (Exception e) {
        e.printStackTrace();
    }
	return formatAddress;

}



public void startMyTimer() {
    timer = new Timer();
    initializeMyTimerTask();
   timer.schedule(timerTask, 1000, 12000); //

}
public void stopMytimertask() {
    if (timer != null) {
        timer.cancel();
        timer = null;
    }}
public void initializeMyTimerTask() {
       timerTask = new TimerTask() {
       public void run() {
       handler.post(new Runnable() {
	   public void run() {
	      if(Utility.isConnectingToInternet(MapView_Activity.this))
	        {
	        	new FetchVehicleList().execute();
		        }
	        else
	        {
	        	Utility.alertMessage(MapView_Activity.this, "No internet connection");
	        	}
	         }
	       });
	     }
       };
    }

/*public void AlertBox()
	{
		
		int_homepop=1;
    	final AlertDialog.Builder builder = new AlertDialog.Builder(MapView_Activity.this, R.style.Transparent);
		LayoutInflater inflator = ((Activity)MapView_Activity.this).getLayoutInflater();
		
		final View couponView = inflator.inflate(R.layout.alertpopup, null);
		
		builder.setView(couponView);
		builder.setCancelable(false);
		
		final AlertDialog dialog1 =builder.create();
		Button btn_dest = (Button) couponView.findViewById(R.id.button_destination);
		btn_dest.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			autocompletetv_destinationPlaces.requestFocus();
			dialog1.dismiss();
				
			}
		});
		ImageButton imgbtn_home = (ImageButton) couponView.findViewById(R.id.imageButton_home);
		imgbtn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog1.dismiss();
				stopMytimertask();
				desvalue=1;
				if(prefs.getString("completeaddress", null).equals(",,,"))
				{
					Utility.alertMessage(MapView_Activity.this,"Please enter address first in Edit Profile.");}
				else{
					gohome=1;
					Editor ed=prefs.edit();
					ed.putInt("homeclick", 0);
					ed.commit();
					System.err.println(prefs.getString("completeaddress", null));
					Log.i("tag", "Map View Get Address: "+prefs.getString("completeaddress", null));
					homeClick="yes";
					if(Utility.isConnectingToInternet(MapView_Activity.this))
					{
						new SearchTask_Destination().execute(prefs.getString("completeaddress", null));
						}
					else
					{
						Utility.alertMessage(MapView_Activity.this, "No internet connection");
						}
					}
				}
				
			
		});
		
	//	dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.argb(0,200,200,200)));
		dialog1.show();
		
       	dialog1.getWindow().setLayout(450, 500);
	}*/

    /**keyboard back button function*////
public void onBackPressed() {
			AlertDialog.Builder alert = new AlertDialog.Builder(MapView_Activity.this);
			alert.setCancelable(true);
			alert.setTitle("Rapid");
			alert.setMessage("Do you want close application");
			alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });
		alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		                @SuppressWarnings("deprecation")
						public void onClick(DialogInterface dialog, int id) {
		                	 dialog.cancel();
//		                    finish();
		    			    Intent intent = new Intent(Intent.ACTION_MAIN);
		    			    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    			    intent.addCategory(Intent.CATEGORY_HOME);
		    			    finish();
		    			    startActivity(intent);
		                
		                   
		                    //(MapView_Activity.this).finish();
		                }
		            });
				AlertDialog alert11 = alert.create();
				alert11.show();
			}
public String GetCountryID(){
    String CountryID="";
   // String CountryZipCode="";

    TelephonyManager tmanager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    //getNetworkCountryIso
    CountryID= tmanager.getNetworkCountryIso().toUpperCase();
  
    /* String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
    for(int i=0;i<rl.length;i++){
        String[] g=rl[i].split(",");
        if(g[1].trim().equals(CountryID.trim())){
            CountryZipCode=g[0];
            break;  
        }
    }*/
    //CountryID= getResources().getConfiguration().locale;
    //CountryID= Locale.getDefault().getCountry(); 
    System.err.println("countryid="+CountryID);
    return CountryID;
}

/*public MapView_Activity() {
	 
    map.put("Andorra, Principality Of", "AD");
    map.put("United Arab Emirates", "AE");
    map.put("Afghanistan, Islamic State Of", "AF");
    map.put("Antigua And Barbuda", "AG");
    map.put("Anguilla", "AI");
    map.put("Albania", "AL");
    map.put("Armenia", "AM");
    map.put("Netherlands Antilles", "AN");
    map.put("Angola", "AO");
    map.put("Antarctica", "AQ");
    map.put("Argentina", "AR");
    map.put("American Samoa", "AS");
    map.put("Austria", "AT");
    map.put("Australia", "AU");
    map.put("Aruba", "AW");
    map.put("Azerbaidjan", "AZ");
    map.put("Bosnia-Herzegovina", "BA");
    map.put("Barbados", "BB");
    map.put("Bangladesh", "BD");
    map.put("Belgium", "BE");
    map.put("Burkina Faso", "BF");
    map.put("Bulgaria", "BG");
    map.put("Bahrain", "BH");
    map.put("Burundi", "BI");
    map.put("Benin", "BJ");
    map.put("Bermuda", "BM");
    map.put("Brunei Darussalam", "BN");
    map.put("Bolivia", "BO");
    map.put("Brazil", "BR");
    map.put("Bahamas", "BS");
    map.put("Bhutan", "BT");
    map.put("Bouvet Island", "BV");
    map.put("Botswana", "BW");
    map.put("Belarus", "BY");
    map.put("Belize", "BZ");
    map.put("Canada", "CA");
    map.put("Cocos (Keeling) Islands", "CC");
    map.put("Central African Republic", "CF");
    map.put("Congo, The Democratic Republic Of The", "CD");
    map.put("Congo", "CG");
    map.put("Switzerland", "CH");
    map.put("Ivory Coast (Cote D'Ivoire)", "CI");
    map.put("Cook Islands", "CK");
    map.put("Chile", "CL");
    map.put("Cameroon", "CM");
    map.put("China", "CN");
    map.put("Colombia", "CO");
    map.put("Costa Rica", "CR");
    map.put("Former Czechoslovakia", "CS");
    map.put("Cuba", "CU");
    map.put("Cape Verde", "CV");
    map.put("Christmas Island", "CX");
    map.put("Cyprus", "CY");
    map.put("Czech Republic", "CZ");
    map.put("Germany", "DE");
    map.put("Djibouti", "DJ");
    map.put("Denmark", "DK");
    map.put("Dominica", "DM");
    map.put("Dominican Republic", "DO");
    map.put("Algeria", "DZ");
    map.put("Ecuador", "EC");
    map.put("Estonia", "EE");
    map.put("Egypt", "EG");
    map.put("Western Sahara", "EH");
    map.put("Eritrea", "ER");
    map.put("Spain", "ES");
    map.put("Ethiopia", "ET");
    map.put("Finland", "FI");
    map.put("Fiji", "FJ");
    map.put("Falkland Islands", "FK");
    map.put("Micronesia", "FM");
    map.put("Faroe Islands", "FO");
    map.put("France", "FR");
    map.put("France (European Territory)", "FX");
    map.put("Gabon", "GA");
    map.put("Great Britain", "UK");
    map.put("Grenada", "GD");
    map.put("Georgia", "GE");
    map.put("French Guyana", "GF");
    map.put("Ghana", "GH");
    map.put("Gibraltar", "GI");
    map.put("Greenland", "GL");
    map.put("Gambia", "GM");
    map.put("Guinea", "GN");
    map.put("Guadeloupe (French)", "GP");
    map.put("Equatorial Guinea", "GQ");
    map.put("Greece", "GR");
    map.put("S. Georgia & S. Sandwich Isls.", "GS");
    map.put("Guatemala", "GT");
    map.put("Guam (USA)", "GU");
    map.put("Guinea Bissau", "GW");
    map.put("Guyana", "GY");
    map.put("Hong Kong", "HK");
    map.put("Heard And McDonald Islands", "HM");
    map.put("Honduras", "HN");
    map.put("Croatia", "HR");
    map.put("Haiti", "HT");
    map.put("Hungary", "HU");
    map.put("Indonesia", "ID");
    map.put("Ireland", "IE");
    map.put("Israel", "IL");
    map.put("India", "IN");
    map.put("British Indian Ocean Territory", "IO");
    map.put("Iraq", "IQ");
    map.put("Iran", "IR");
    map.put("Iceland", "IS");
    map.put("Italy", "IT");
    map.put("Jamaica", "JM");
    map.put("Jordan", "JO");
    map.put("Japan", "JP");
    map.put("Kenya", "KE");
    map.put("Kyrgyz Republic (Kyrgyzstan)", "KG");
    map.put("Cambodia, Kingdom Of", "KH");
    map.put("Kiribati", "KI");
    map.put("Comoros", "KM");
    map.put("Saint Kitts & Nevis Anguilla", "KN");
    map.put("North Korea", "KP");
    map.put("South Korea", "KR");
    map.put("Kuwait", "KW");
    map.put("Cayman Islands", "KY");
    map.put("Kazakhstan", "KZ");
    map.put("Laos", "LA");
    map.put("Lebanon", "LB");
    map.put("Saint Lucia", "LC");
    map.put("Liechtenstein", "LI");
    map.put("Sri Lanka", "LK");
    map.put("Liberia", "LR");
    map.put("Lesotho", "LS");
    map.put("Lithuania", "LT");
    map.put("Luxembourg", "LU");
    map.put("Latvia", "LV");
    map.put("Libya", "LY");
    map.put("Morocco", "MA");
    map.put("Monaco", "MC");
    map.put("Moldavia", "MD");
    map.put("Madagascar", "MG");
    map.put("Marshall Islands", "MH");
    map.put("Macedonia", "MK");
    map.put("Mali", "ML");
    map.put("Myanmar", "MM");
    map.put("Mongolia", "MN");
    map.put("Macau", "MO");
    map.put("Northern Mariana Islands", "MP");
    map.put("Martinique (French)", "MQ");
    map.put("Mauritania", "MR");
    map.put("Montserrat", "MS");
    map.put("Malta", "MT");
    map.put("Mauritius", "MU");
    map.put("Maldives", "MV");
    map.put("Malawi", "MW");
    map.put("Mexico", "MX");
    map.put("Malaysia", "MY");
    map.put("Mozambique", "MZ");
    map.put("Namibia", "NA");
    map.put("New Caledonia (French)", "NC");
    map.put("Niger", "NE");
    map.put("Norfolk Island", "NF");
    map.put("Nigeria", "NG");
    map.put("Nicaragua", "NI");
    map.put("Netherlands", "NL");
    map.put("Norway", "NO");
    map.put("Nepal", "NP");
    map.put("Nauru", "NR");
    map.put("Neutral Zone", "NT");
    map.put("Niue", "NU");
    map.put("New Zealand", "NZ");
    map.put("Oman", "OM");
    map.put("Panama", "PA");
    map.put("Peru", "PE");
    map.put("Polynesia (French)", "PF");
    map.put("Papua New Guinea", "PG");
    map.put("Philippines", "PH");
    map.put("Pakistan", "PK");
    map.put("Poland", "PL");
    map.put("Saint Pierre And Miquelon", "PM");
    map.put("Pitcairn Island", "PN");
    map.put("Puerto Rico", "PR");
    map.put("Portugal", "PT");
    map.put("Palau", "PW");
    map.put("Paraguay", "PY");
    map.put("Qatar", "QA");
    map.put("Reunion (French)", "RE");
    map.put("Romania", "RO");
    map.put("Russian Federation", "RU");
    map.put("Rwanda", "RW");
    map.put("Saudi Arabia", "SA");
    map.put("Solomon Islands", "SB");
    map.put("Seychelles", "SC");
    map.put("Sudan", "SD");
    map.put("Sweden", "SE");
    map.put("Singapore", "SG");
    map.put("Saint Helena", "SH");
    map.put("Slovenia", "SI");
    map.put("Svalbard And Jan Mayen Islands", "SJ");
    map.put("Slovak Republic", "SK");
    map.put("Sierra Leone", "SL");
    map.put("San Marino", "SM");
    map.put("Senegal", "SN");
    map.put("Somalia", "SO");
    map.put("Suriname", "SR");
    map.put("Saint Tome (Sao Tome) And Principe", "ST");
    map.put("Former USSR", "SU");
    map.put("El Salvador", "SV");
    map.put("Syria", "SY");
    map.put("Swaziland", "SZ");
    map.put("Turks And Caicos Islands", "TC");
    map.put("Chad", "TD");
    map.put("French Southern Territories", "TF");
    map.put("Togo", "TG");
    map.put("Thailand", "TH");
    map.put("Tadjikistan", "TJ");
    map.put("Tokelau", "TK");
    map.put("Turkmenistan", "TM");
    map.put("Tunisia", "TN");
    map.put("Tonga", "TO");
    map.put("East Timor", "TP");
    map.put("Turkey", "TR");
    map.put("Trinidad And Tobago", "TT");
    map.put("Tuvalu", "TV");
    map.put("Taiwan", "TW");
    map.put("Tanzania", "TZ");
    map.put("Ukraine", "UA");
    map.put("Uganda", "UG");
    map.put("United Kingdom", "UK");
    map.put("USA Minor Outlying Islands", "UM");
    map.put("United States", "US");
    map.put("Uruguay", "UY");
    map.put("Uzbekistan", "UZ");
    map.put("Holy See (Vatican City State)", "VA");
    map.put("Saint Vincent & Grenadines", "VC");
    map.put("Venezuela", "VE");
    map.put("Virgin Islands (British)", "VG");
    map.put("Virgin Islands (USA)", "VI");
    map.put("Vietnam", "VN");
    map.put("Vanuatu", "VU");
    map.put("Wallis And Futuna Islands", "WF");
    map.put("Samoa", "WS");
    map.put("Yemen", "YE");
    map.put("Mayotte", "YT");
    map.put("Yugoslavia", "YU");
    map.put("South Africa", "ZA");
    map.put("Zambia", "ZM");
    map.put("Zaire", "ZR");
    map.put("Zimbabwe", "ZW");

   }

    public String getCode(String country){
    String countryFound = map.get(country);
    if(countryFound==null){
            countryFound="US";
    }
    return countryFound;
    }*/

}