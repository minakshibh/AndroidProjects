package com.restedge.event;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.MadsAdView.MadsInlineAdView;
import com.restedge.utelite.R;
import com.restedge.utelite.CityListing;
import com.restedge.utelite.HelperActivity;
import com.restedge.utelite.LoginRegisterScreen;
import com.restedge.utelite.RestaurantDetail;
import com.restedge.util.ConnectionDetector;
import com.restedge.util.CustomAdapterEvent;
import com.restedge.util.Event;
import com.restedge.util.MadsDemoUtil;
import com.restedge.util.Place;
import com.restedge.util.RestaurantDatabase;
import com.restedge.util.SAXXMLParser;

public class EventListing extends Activity implements LocationListener {

	Button btnNearest,btnUpcoming,btnPast;
	ImageButton btnAddEvent,btnBack;
	ListView eventListing;
	ArrayList<Place> eventplaces=new ArrayList<Place>();
	LocationManager locationManager;
	TextView okButton,dialogMessage,cancelButton;
	public boolean isGPSEnabled = false;
	Dialog settingsDialog;
	InputStream is;
	ProgressDialog dialog;
    // flag for network status
    boolean isNetworkEnabled = false;
	
    static ArrayList<Event> events =null;
	 Location location;
	boolean canGetLocation = false;
	Map<String, String> map=new HashMap<String, String>();
	int cityId,placeId;
	double curLatitude,curLongitude;
	double latitude; // latitude
    double longitude; // longitude
    String className;
    String currentDateandTime;
    String method_name="getEventListing";
    
    ArrayList<Event> pastList=new ArrayList<Event>();
    ArrayList<Event> upcomingList=new ArrayList<Event>();
    ArrayList<Event> nearList=new ArrayList<Event>();
    
    String receiptId;
    
    String globalpast=null;
    String globalnear=null;
    String globalupcoming=null;
    String globalString=null;
  
    SharedPreferences pref,userPreference;
    SharedPreferences mypref;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    
   @SuppressLint("UseSparseArrays")
   HashMap<Integer, Integer> hmap=new HashMap<Integer, Integer>();
    String str=null;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1; 
	@SuppressWarnings("deprecation")
	@SuppressLint("SimpleDateFormat")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.eventlisting);
		
		btnNearest=(Button)findViewById(R.id.nearestEvent);
		btnUpcoming=(Button)findViewById(R.id.upcomingEvent);
		btnPast=(Button)findViewById(R.id.pastEvent);
		eventListing=(ListView)findViewById(R.id.eventlisting);
		btnAddEvent=(ImageButton)findViewById(R.id.addEvent);
		btnBack=(ImageButton)findViewById(R.id.back);
		RestaurantDatabase db=new RestaurantDatabase(EventListing.this);
		
		location=getLocation();
		Log.e("Location ", ""+location);
	
		
		/*final MadsInlineAdView adView = (MadsInlineAdView)findViewById(R.id.mads_inline_adview);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		MadsDemoUtil.setupUI(adView, this);
		
		adView.useCloseButton(true);*/		
		
		settingsDialog= new Dialog(this); 
		 settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
		
		LayoutInflater DialogInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup DialogParent=(ViewGroup) DialogInflater.inflate(R.layout.dialoglayout, null);
		 View dialoglayout = DialogInflater.inflate(R.layout.dialoglayout, DialogParent, false);
		 settingsDialog.setContentView(dialoglayout); 
		 okButton=(TextView)dialoglayout.findViewById(R.id.oklbtn);
		 dialogMessage=(TextView)dialoglayout.findViewById(R.id.dialogmessage);
		 cancelButton=(TextView)dialoglayout.findViewById(R.id.cancelbtn);	 
		
		
		
		if(location==null)
		{
			showToast("Gps Location is not enabled");
			return;
		}
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		
		
		
		 userPreference = this.getSharedPreferences("UserPreference", MODE_PRIVATE);
		
		
		
		
		mypref=EventListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
		receiptId=mypref.getString("SupportEmail", "");
		
		
		final Calendar c = Calendar.getInstance();
	   int yy = c.get(Calendar.YEAR);
	   int  mm = c.get(Calendar.MONTH);
	  int   dd = c.get(Calendar.DAY_OF_MONTH);
		int hour=c.get(Calendar.HOUR_OF_DAY);
		int minute=c.get(Calendar.MINUTE);
		int second=c.get(Calendar.SECOND);
	  
	  btnBack.setOnClickListener(eventListener);
	  currentDateandTime=yy+"-"+pad((mm+1))+"-"+pad(dd)+" "+pad(hour)+":"+pad(minute)+":"+pad(second);
//		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//		 currentDateandTime = sdf.format(new Date());
		 
		 System.out.println(currentDateandTime);
		Intent in=getIntent();
		if(in.hasExtra("ClassName"))
		{
			if(in.hasExtra("ClassName"))
				className=in.getStringExtra("ClassName");
			if(in.hasExtra("cityId"))
				cityId=in.getIntExtra("cityId", 0);
			if(in.hasExtra("placeId"))
				placeId=in.getIntExtra("placeId", 0);
			
			pref=getSharedPreferences("mypref", MODE_APPEND);
			SharedPreferences.Editor edit=pref.edit();
			edit.putString("ClassName", className);
			edit.putInt("cityId",cityId );
			edit.putInt("placeId", placeId);
			edit.commit();
			
		}
		else
		{
			pref=getSharedPreferences("mypref", MODE_APPEND);
			
			className=pref.getString("ClassName", null);
			cityId=	pref.getInt("cityId",cityId );
			placeId=pref.getInt("placeId", placeId);
			
		}
		
		Log.e("city Id",""+ cityId);
		
		globalupcoming="upcoming";
		globalString="upcoming";
		
		curLatitude=location.getLatitude();
		curLongitude=location.getLongitude();
		
		
		
		
		str=getResources().getString(R.string.app_url)+"/"+method_name;
		
		
		if(isInternetPresent)
		{
			//Getting city from server
			new GetEventListing().execute(str,"upcoming",currentDateandTime); 
			
		}
		else
		{
			final AlertDialog alertdialog=new AlertDialog.Builder(EventListing.this).create();
			System.out.println("check connection");
			alertdialog.setTitle("Edge");
			alertdialog.setMessage("Internet connection not available");
			alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
				}
			});
			alertdialog.show();
			
		}
		
		btnNearest.setOnClickListener(eventListener);
		btnUpcoming.setOnClickListener(eventListener);
		btnPast.setOnClickListener(eventListener);
		
		btnAddEvent.setOnClickListener(eventListener);
		
		
		
		eventListing.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(globalString.equalsIgnoreCase("upcoming"))
				{
				
					Event event=events.get(arg2);
					
					Intent in=new Intent(EventListing.this,EventDetail.class);
					
					in.putExtra("event", event);
					startActivity(in);
				}
				else if(globalString.equalsIgnoreCase("past"))
				{
					Event event=pastList.get(arg2);
					
					Intent in=new Intent(EventListing.this,EventDetail.class);
					in.putExtra("eventType", "past");
					in.putExtra("event", event);
					startActivity(in);
					
				}
				else if(globalString.equalsIgnoreCase("near"))
				{
					Event event=nearList.get(arg2);
					
					Intent in=new Intent(EventListing.this,EventDetail.class);
					
					in.putExtra("event", event);
					startActivity(in);
					
				}
			}
		});
		
		
	}
	public void showToast(String str)
	{
		Toast.makeText(EventListing.this, str, 1000).show();
	}
	OnClickListener eventListener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.nearestEvent:
					
					
					if(globalString.equalsIgnoreCase("near"))
						return;
					btnUpcoming.setBackgroundResource(R.drawable.tab_button);
					btnNearest.setBackgroundResource(R.drawable.tab_button_hover);
					btnPast.setBackgroundResource(R.drawable.tab_button);
					
					
					if(globalnear==null)
					{
						nearList.addAll(events);
						if(location!=null)
						{
							float lat1=(float)(location.getLatitude());
							 float lng1=(float)(location.getLongitude());
							for(int i=0;i<nearList.size();i++)
							{
								System.out.println(nearList.get(i).getLatitude());
								System.out.println(nearList.get(i).getLongitude());
								float lat2=Float.parseFloat(nearList.get(i).getLatitude());
								 float lng2=Float.parseFloat(nearList.get(i).getLongitude());
								
								int distance=distFrom(lat1, lng1, lat2, lng2);
								
								nearList.get(i).setDistance(distance);
								hmap.put(i, distance);
								
							}
							Event tempevent;
							for(int i=0;i<nearList.size();i++)
							{
								
								
								for(int j=0;j<nearList.size()-i-1;j++)
								{
									 tempevent=new Event();
									if(nearList.get(j).getDistance()>=nearList.get(j+1).getDistance())
									{
										
										tempevent=nearList.get(j);
										nearList.set(j, nearList.get(j+1));
										nearList.set(j+1, tempevent);
										
									}
									
								}
								
								
							}
							
							CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, nearList,location);
							
							eventListing.setAdapter(adapterevent);
						
							
						}
						globalnear="near";	
						globalString="near";
						
					}
					else
					{
						Log.e("size ",""+ nearList.size());
						CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, nearList,location);
						globalString="near";
						eventListing.setAdapter(adapterevent);
					}
					break;
				case R.id.upcomingEvent:
					
					if(globalString.equalsIgnoreCase("upcoming"))
						return;
					btnUpcoming.setBackgroundResource(R.drawable.tab_button_hover);
					btnNearest.setBackgroundResource(R.drawable.tab_button);
					btnPast.setBackgroundResource(R.drawable.tab_button);
					if(globalupcoming==null)
					{
						
						new GetEventListing().execute(str,"upcoming",currentDateandTime); 
						globalupcoming="upcoming";
						
						globalString="upcoming";
					}
					else
					{
						CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, events,location);
						
						eventListing.setAdapter(adapterevent);
						globalString="upcoming";
					}
					
					break;
				case R.id.pastEvent:
					
					if(globalString.equalsIgnoreCase("past"))
						return;
					btnUpcoming.setBackgroundResource(R.drawable.tab_button);
					btnNearest.setBackgroundResource(R.drawable.tab_button);
					btnPast.setBackgroundResource(R.drawable.tab_button_hover);
					
					if(globalpast==null)
					{
						new PastListing().execute(str,"past",currentDateandTime); 
						globalpast="past";
						globalString="past";
					}
					else
					{
						CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, pastList,location);
						globalString="past";
						eventListing.setAdapter(adapterevent);
					}
					
					break;
				case R.id.addEvent:
					
					if(userPreference.getString("LoginResult","").equals("Login Successful"))
					{
					
					Intent in=new Intent(EventListing.this,AddEvent.class);
					in.putExtra("cityId", cityId);
					startActivity(in);
					}
					else
					{
							dialogMessage.setText("Please Login first");
							okButton.setVisibility(View.VISIBLE);
							cancelButton.setVisibility(View.VISIBLE);
							okButton.setText("Continue");
							cancelButton.setText("Cancel");
							settingsDialog.show();
					}
					break;
				case R.id.back:
					
					if(className!=null)
					{
						if(className.equalsIgnoreCase("City"))
						{
							Intent intent=new Intent(EventListing.this,CityListing.class);
							EventListing.this.finish();
							startActivity(intent);
						}
						else
						{
							Intent intent=new Intent(EventListing.this,RestaurantDetail.class);
							intent.putExtra("cityId", cityId);
							intent.putExtra("PlaceId", placeId);
							EventListing.this.finish();
							startActivity(intent);
						}
					}
					else
					{
						Intent intent=new Intent(EventListing.this,CityListing.class);
						EventListing.this.finish();
						startActivity(intent);
					}
					
					break;
			}
			
			
		}
	};
	public Location getLocation() {
        try {
            locationManager = (LocationManager)EventListing.this
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            Log.v("isGPSEnabled", "=" + isGPSEnabled);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            Log.v("isNetworkEnabled", "=" + isNetworkEnabled);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    location=null;
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    location=null;
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		this.location=location;
	}
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	class GetEventListing extends AsyncTask<String, Void, String>
	{
		int result=1;
		int eventsize=0;
		 protected void onPreExecute() {
		    	
		    	super.onPreExecute();
		    	dialog=new ProgressDialog(EventListing.this);
		    	
		   
		    	dialog.setTitle("Edge");
		    	dialog.setMessage("Please wait");
		    	dialog.setCanceledOnTouchOutside(false);
		    	dialog.show();
		    	
		    }
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String cityid,placeid;
			
			try
			{
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost requestLogin = new HttpPost(params[0]);
		         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		         
		         if(cityId==0)
		        	 cityid="";
		         else
		        	 cityid=""+cityId;
		         if(placeId==0)
		        	 placeid="";
		         else
		        	 placeid=""+placeId;
		         System.out.println(" "+params[0]+"  "+params[1]+"  "+params[2]+" "+curLatitude+" "+curLongitude+" "+cityid+" "+placeid);
		        
		         
		         nameValuePairs.add(new BasicNameValuePair("trigger",params[1]));//passing 
		         nameValuePairs.add(new BasicNameValuePair("curDateTime", params[2]));
		         nameValuePairs.add(new BasicNameValuePair("curLatitude",""+ curLatitude));
		         nameValuePairs.add(new BasicNameValuePair("curLongitude",""+ curLongitude));
		         nameValuePairs.add(new BasicNameValuePair("cityId",cityid));
		         nameValuePairs.add(new BasicNameValuePair("placeId",placeid));
		         
		         for(int i=0;i<nameValuePairs.size();i++)
		        	 System.out.println("Data "+ nameValuePairs.get(i));
		       
		   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     HttpResponse response = httpClient.execute(requestLogin);
			     HttpEntity entity = response.getEntity();
			     is= entity.getContent();
			     
			     
			      events = SAXXMLParser.getEvents(is);
			      map= SAXXMLParser.map;
			     
			      eventsize=events.size();
			     System.out.println("events size"+events.size());
			     
			    result=Integer.parseInt( map.get("Result"));
			     
			     
			     
			     Log.e("is ", ""+is);
			     
			     
			     
			     
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			return null;
		}
		protected void onPostExecute(String str) 
		{
	    	
	    	dialog.dismiss();
	    	
	    	
	    		View footerView=getLayoutInflater().inflate(R.layout.footerhelp, null);
				//eventListing.addFooterView(footerView);	
	    		
				
				if(events.size()>0)
				{
			    	CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, events,location);
					
					eventListing.setAdapter(adapterevent);
				}
			
	    	if(eventsize==0)
	    		Toast.makeText(EventListing.this, "No event is available", 1000).show();
	    		
	    	
	    	
		}
	}
	class PastListing extends AsyncTask<String, Void, String>
	{
		int result=1,pasteventSize=0;
		 protected void onPreExecute() {
		    	
		    	super.onPreExecute();
		    	dialog=new ProgressDialog(EventListing.this);
		    	
		   
		    	dialog.setTitle("Edge");
		    	dialog.setMessage("Please wait");
		    	dialog.setCanceledOnTouchOutside(false);
		    	dialog.show();
		    	
		    }
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			String cityid,placeid;
			try
			{
				
				
				
				
				
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost requestLogin = new HttpPost(params[0]);
		         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		         
		         if(cityId==0)
		        	 cityid="";
		         else
		        	 cityid=""+cityId;
		         if(placeId==0)
		        	 placeid="";
		         else
		        	 placeid=""+placeId;
		         System.out.println(" "+params[0]+"  "+params[1]+"  "+params[2]+" "+curLatitude+" "+curLongitude+" "+cityid+" "+placeid);
		        
		         
		         nameValuePairs.add(new BasicNameValuePair("trigger",params[1]));//passing 
		         nameValuePairs.add(new BasicNameValuePair("curDateTime", params[2]));
		         nameValuePairs.add(new BasicNameValuePair("curLatitude",""+ curLatitude));
		         nameValuePairs.add(new BasicNameValuePair("curLongitude",""+ curLongitude));
		         nameValuePairs.add(new BasicNameValuePair("cityId",cityid));
		         nameValuePairs.add(new BasicNameValuePair("placeId",placeid));
		         
		         for(int i=0;i<nameValuePairs.size();i++)
		        	 System.out.println("Data "+ nameValuePairs.get(i));
		       
		   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     HttpResponse response = httpClient.execute(requestLogin);
			     HttpEntity entity = response.getEntity();
			     is= entity.getContent();
			     
			    
			     pastList= SAXXMLParser.getEvents(is);
			      map= SAXXMLParser.map;
			     
			     pasteventSize=pastList.size();
			     
			    result=Integer.parseInt( map.get("Result"));
			     
			     
			     
			     Log.e("is ", ""+is);
			     
			     
			     
			     
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
			
			return null;
		}
		protected void onPostExecute(String str) 
		{
	    	
	    	dialog.dismiss();
	    	
	    	if(pasteventSize!=0)
	    	{
	    	CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, pastList,location);
			
			eventListing.setAdapter(adapterevent);
	    	
	    	}
	    	else
	    	{
	    		CustomAdapterEvent adapterevent=new CustomAdapterEvent(EventListing.this, android.R.layout.simple_list_item_1, pastList,location);
				
				eventListing.setAdapter(adapterevent);
	    		Toast.makeText(EventListing.this, "No  event is available", 1000).show();
	    		
	    	}
	    	
		}
	}
	 public static int distFrom(float lat1, float lng1, float lat2, float lng2) {
		    double earthRadius = 3958.75;
		    double dLat = Math.toRadians(lat2-lat1);
		    double dLng = Math.toRadians(lng2-lng1);
		    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
		               Math.sin(dLng/2) * Math.sin(dLng/2);
		    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		    double dist = earthRadius * c;

		    return (int)dist;
//		    int meterConversion = 1609;
//
//		    return (float) (dist * meterConversion);
	    }
	 public void onBackPressed()
	 {
		 	if(className!=null)
			{
				 if(className.equalsIgnoreCase("City"))
					{
						Intent intent=new Intent(EventListing.this,CityListing.class);
						EventListing.this.finish();
						startActivity(intent);
					}
					else
					{
						Intent intent=new Intent(EventListing.this,RestaurantDetail.class);
						intent.putExtra("cityId", cityId);
						intent.putExtra("PlaceId", placeId);
						EventListing.this.finish();
						startActivity(intent);
					}
				}
		 	else
			{
				Intent intent=new Intent(EventListing.this,CityListing.class);
				EventListing.this.finish();
				startActivity(intent);
			}
	 }
	 private static String pad(int c) {
			if (c >= 10)
			   return String.valueOf(c);
			else
			   return "0" + String.valueOf(c);
		}
	 public void onFooterClick(View v)
		{
		 	String receiptId=mypref.getString("SupportEmail", "");
			Intent in=new Intent(EventListing.this,HelperActivity.class);
	    	in.putExtra("receiptId", receiptId);
	    	
	    	startActivity(in);
			
		}
	 public void cancelListener(View v){
			settingsDialog.dismiss();		
		}
		
	    public void OkListener(View v){		
	    	settingsDialog.dismiss();
	    	Intent loginIntent=new Intent(EventListing.this, LoginRegisterScreen.class);
	    	loginIntent.putExtra("Name", "Name");
			startActivity(loginIntent);
		}
}
