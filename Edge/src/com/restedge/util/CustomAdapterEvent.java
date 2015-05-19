package com.restedge.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.restedge.utelite.R;

public class CustomAdapterEvent extends ArrayAdapter<Event> {

	ArrayList<Event> events;
	Context mcontext;
	Location location;
	float lat1,lng1;
	com.restedge.util.ImageLoaderEvent	imageLoader;
	public CustomAdapterEvent(Context context, int resource,
			 ArrayList<Event> objects,Location loc) {
		super(context, resource,objects);
		
		location=loc;
		mcontext=context;
		events=objects;
		imageLoader=new ImageLoaderEvent(mcontext);
		lat1=(float)location.getLatitude();
		lng1=(float)location.getLongitude();
	}
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) 
	 {
	      
		 if(convertView==null)
			  convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.eventrow, null);
		 
		 
		 Event event=events.get(position);
		 ImageView imageEvent=(ImageView)convertView.findViewById(R.id.restaurantImage);
		 TextView eventName=(TextView)convertView.findViewById(R.id.restaurantName);
		 
		 
		 
		 float lat2=Float.parseFloat(events.get(position).getLatitude());
		 float lng2=Float.parseFloat(events.get(position).getLongitude());
		 int distance=distFrom(lat1, lng1, lat2, lng2);
		 
		 event.setDistance(distance);
		TextView txttime=(TextView)convertView.findViewById(R.id.eventtime);
		 TextView distText=(TextView)convertView.findViewById(R.id.txtdistance);
		 TextView eventAddress=(TextView)convertView.findViewById(R.id.restaurantAddress);
		 distText.setText(distance+"Km");
		 eventName.setText(event.getEventName());
		 eventAddress.setText(event.getEventDesc());
		 txttime.setText(event.getEndDate());
		 
		 
		 imageEvent.setTag(event.getEventImageUrl());
			imageLoader.DisplayImage(event.getEventImageUrl(), (Activity)mcontext, imageEvent);
		 
		 return convertView;
		 
		 
	  } 
	 public static int distFrom(float lat1, float lng1, float lat2, float lng2) {
		 
		 
		 
		 System.out.println("sLat"+lat1+" slong"+lng1+" dlat "+lat2+" lng2 "+lng2);
			    double earthRadius = 6369;
			    double dLat = Math.toRadians(lat2-lat1);
			    double dLng = Math.toRadians(lng2-lng1);
			    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
			               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
			               Math.sin(dLng/2) * Math.sin(dLng/2);
			    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
			    double dist = earthRadius * c;
	
			    return (int)dist;
//			    int meterConversion = 1609;
//	
//			    return (float) (dist * meterConversion);
		    }
	 
}
