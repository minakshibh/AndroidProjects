package com.restedge.event;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.MadsAdView.MadsInlineAdView;
import com.restedge.utelite.R;
import com.restedge.utelite.CityListing;
import com.restedge.utelite.HelperActivity;
import com.restedge.util.Event;
import com.restedge.util.MadsDemoUtil;

public class EventDetail extends Activity implements LocationListener{

	Button readmore,addToCalender,getDirection;
	ImageButton btnBack;
	Intent in;
	Event event;
	TextView eventName,eventInfo,venueName,starttime,endtime;
	ImageView eventImage;
	int venueId;
	boolean canGetLocation = false;
	boolean isNetworkEnabled = false;
	Uri eventUri;
	String receiptId;
	SharedPreferences pref,mypref;
	int counter=0;
	private static final long MIN_TIME_BW_UPDATES = 1; 
	byte[] image;
	 Location location;
	 private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;
	 double curLatitude,curLongitude;
		double latitude; // latitude
	    double longitude; 
	 LocationManager locationManager;
		String eventType=null;
		public boolean isGPSEnabled = false;

	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.eventdetail);
		
		btnBack=(ImageButton)findViewById(R.id.back);
		eventName=(TextView)findViewById(R.id.eventname);
		eventImage=(ImageView)findViewById(R.id.restaurantimage);
		eventInfo=(TextView)findViewById(R.id.restinfo);
		
		in=getIntent();
		event=(Event)in.getSerializableExtra("event");
		
		mypref=getSharedPreferences("pref", MODE_PRIVATE);
		counter=mypref.getInt("AdCounter", 0);
//		final MadsInlineAdView adView = (MadsInlineAdView)findViewById(R.id.mads_inline_adview);
//		adView.setAdExpandListener(new AdExpandListener() {
//			
//			@Override
//			public void onExpand() {
//				// For the demo use case we just ad it to the main content view 
//				// which means we overwrite the default behaviour of showing it 
//				// inline 
//				ViewGroup adParent = (ViewGroup)adView.getParent();
//				if ( adParent != null ) {
//					adParent.removeView(adView);
//				} 
//				((ViewGroup)findViewById(Window.ID_ANDROID_CONTENT)).addView(adView);
//				
//			}
//			
//			@Override
//			public void onClose() {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		
		if(counter==5)
		{
			SharedPreferences.Editor edit=mypref.edit();
			edit.putInt("AdCounter", 0);
			edit.commit();
			//adView.setVisibility(View.VISIBLE);
			
		}
		else
		{
			counter++;
			SharedPreferences.Editor edit=mypref.edit();
			edit.putInt("AdCounter", counter);
			edit.commit();
			//adView.setVisibility(View.GONE);
		}
		//MadsDemoUtil.setupUI(adView, this);
		//adView.useCloseButton(true);
		
		
		
		
		
		
		if(in.hasExtra("eventType"))
			eventType=in.getStringExtra("eventType");
		System.out.println(event);
		eventName.setText(event.getEventName());
		venueName=(TextView)findViewById(R.id.venuetext);
		readmore=(Button)findViewById(R.id.readmore);
		starttime=(TextView)findViewById(R.id.starttime);
		endtime=(TextView)findViewById(R.id.endtime);
		addToCalender=(Button)findViewById(R.id.addToCalender);
		getDirection=(Button)findViewById(R.id.getdirection);
		
		location=getLocation();
		
		pref = getSharedPreferences("mypref", MODE_PRIVATE);
		
		receiptId=pref.getString("SupportEmail", null);
		venueId=event.getVenueId();
		if(event.getEventImageUrl()!=null)
		{
			
			image=getDecodeUrl(event.getEventImageUrl());
			
			if(image!=null)
			{
				Bitmap bitmap=BitmapFactory.decodeByteArray(image, 0, image.length);
				eventImage.setImageBitmap(bitmap);
			}
		}
		eventInfo.setText(event.getEventDesc());
		venueName.setText(event.getVenueName()+" ,"+event.getCityName());
		starttime.setText(event.getStartDate());
		endtime.setText(event.getEndDate());
		readmore.setOnClickListener(listener);
		btnBack.setOnClickListener(listener);
		addToCalender.setOnClickListener(listener);
		getDirection.setOnClickListener(listener);
		
		if(eventType!=null)
			addToCalender.setVisibility(View.GONE);
	}
	OnClickListener listener=new OnClickListener() {
		
		@SuppressLint("SimpleDateFormat")
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.back:
					
					EventDetail.this.finish();
					
					break;
				case R.id.readmore:
					 in=new Intent(EventDetail.this,MoreInfoEvent.class);
					
					in.putExtra("eventDesc", event.getEventDesc());
					startActivity(in);
					break;
				case R.id.addToCalender:
					
					 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
					 Date d = null,d1 = null;
					
					 Log.e("starttime", starttime.getText().toString());
					 Log.e("endtime", endtime.getText().toString());
					 try
					 {
						d= df.parse(starttime.getText().toString());
						 d1=df.parse(endtime.getText().toString());
					 }
					 catch(Exception e)
					 {
						 e.printStackTrace();
//						 showToast("Error ");
						 
					 }
					 long startTime=d.getTime();
					 long endTime=d1.getTime();
					 
					 Log.e("Time ", startTime+"  "+endTime);
					ContentValues eventcal = new ContentValues();
				    eventcal.put("calendar_id", 1); // "" for insert
				    eventcal.put("title", event.getEventName());
				    eventcal.put("description", event.getEventDesc());
				    
				    
				    eventcal.put("eventStatus", 1);
				   // event.put("transparency", 0);
				    eventcal.put("dtstart", startTime);
				    eventcal.put("dtend",endTime);
				    eventcal.put("eventTimezone", TimeZone.getDefault().getID());
				    ContentResolver contentResolver = getContentResolver();
				    eventUri = Uri.parse("content://com.android.calendar/events");
				     eventUri = contentResolver.insert(eventUri, eventcal);
				    String ret = eventUri.toString();
					Log.e("Event ", ret);
				    final AlertDialog alertdialog=new AlertDialog.Builder(EventDetail.this).create();
		    		
		    		alertdialog.setTitle("Edge");
		    		alertdialog.setMessage("Event succesfully created");
		    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    				
		    				alertdialog.dismiss();
		    				
		    			}
		    		});
		    		alertdialog.show();
					break;
				case R.id.getdirection :
					if(venueId!=-1)	
					{//					"http://maps.google.com/maps?saddr="+currentLattitude+","+currentLongitude+"&daddr="+targetLat+","+targetLang+"&mode=driving";
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+event.getLatitude()+","+event.getLongitude()));
						startActivity(intent);
					}
					else
					{
						
						final AlertDialog alertdialog1=new AlertDialog.Builder(EventDetail.this).create();
			    		
			    		alertdialog1.setTitle("Sorry");
			    		alertdialog1.setMessage("Direction not available");
			    		alertdialog1.setButton("Ok", new DialogInterface.OnClickListener() {
			    			
			    			public void onClick(DialogInterface dialog, int which) {
			    				// TODO Auto-generated method stub
			    				
			    				alertdialog1.dismiss();
			    				
			    			}
			    		});
			    		alertdialog1.show();
					}
					break;
			}
			
			
			
		}
	};
	public void onBackPressed()
	{
		
		
		EventDetail.this.finish();
		
		
	}
	public byte[] getDecodeUrl(String url)
	{
    	try
		{
		 Bitmap bitmap=null;
         URL imageUrl = new URL(url);
         System.out.println(imageUrl);
         HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
         conn.setConnectTimeout(30000);
         conn.setReadTimeout(30000);
         conn.setInstanceFollowRedirects(true);
         InputStream is=conn.getInputStream();
         System.out.println(is);
//         
         
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];
         int len;
         while ((len = is.read(buffer)) > -1 ) {
             baos.write(buffer, 0, len);
         }
         baos.flush();

         // Open new InputStreams using the recorded bytes
         // Can be repeated as many times as you wish
         InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
         InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
         
         
         
		
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;
       
       BitmapFactory.decodeStream(is1,null,o);
       
         int sampleSize = calculateInSampleSize(o, CityListing.deviceWidth, CityListing.deviceHeight);
         
         System.out.println("scale city"+sampleSize);
         o.inSampleSize = sampleSize;
         o.inJustDecodeBounds = false;
        
	      
         
	       bitmap=BitmapFactory.decodeStream(is2,null,o);
	       System.out.println(bitmap);
	      is1.close();
	  		is2.close();
	      ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		 
		byte imageInByte[] = stream.toByteArray();
		
		
		return imageInByte;
		
		}
		catch(Exception e)
		{
			System.out.println("Error "+e);
			if(e!=null)
				System.out.println(e.getMessage());
			return null;
		}
		
	}
	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {		
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {
		
		        final int heightRatio = Math.round((float) height / (float) reqHeight);
		        final int widthRatio = Math.round((float) width / (float) reqWidth);
		
		        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		    }
		
		    return inSampleSize;
	}
//    public void showToast(String str)
//    {
//    	Toast.makeText(EventDetail.this, str, 1000).show();
//    }
    public Location getLocation() {
        try {
            locationManager = (LocationManager)EventDetail.this
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
	public void onFooterClick(View v)
	{
		
		
		Intent in=new Intent(EventDetail.this,HelperActivity.class);
    	in.putExtra("receiptId", receiptId);
    	
    	startActivity(in);
		
	}
	
}
