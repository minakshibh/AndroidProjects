package com.rapidride.imageloader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rapidride.R;

public class RiderAdapter extends BaseAdapter {
    
   private Activity activity;
   private ArrayList<String> al_image;
   private ArrayList<String> al_namerider;
   private ArrayList<String> al_destination;
   private ArrayList<String> al_picktime;
   private ArrayList<String> al_requesttype;
   private ArrayList<String> al_riderrating;
   private static LayoutInflater inflater=null;
   public ImageLoader imageLoader; 
	 SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
   public RiderAdapter(Activity a, ArrayList<String> i,ArrayList<String> n,ArrayList<String> d,ArrayList<String> p,ArrayList<String> r,ArrayList<String> rat) {
       activity = a;
       al_image=i;
       al_namerider=n;
       al_destination=d;
       al_picktime=p;
       al_requesttype=r;
       al_riderrating=rat;
       inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       imageLoader=new ImageLoader(activity.getApplicationContext());
   }

   public int getCount() {
       return al_image.size();
   }

   public Object getItem(int position) {
       return position;
   }

   public long getItemId(int position) {
       return position;
   }
    
   public View getView(int position, View convertView, ViewGroup parent) {
       View view=convertView;
       if(convertView==null)
           view = inflater.inflate(R.layout.pendingrequest_adapter, null);

       TextView tv_name=(TextView)view.findViewById(R.id.textView_drivername1);
		tv_name.setText(al_namerider.get(position));
		
		TextView tv_riderrating=(TextView)view.findViewById(R.id.textView_rating);
		tv_riderrating.setText("Rating :"+al_riderrating.get(position));
		
		TextView tv_desti=(TextView)view.findViewById(R.id.textView_destination1);
		tv_desti.setText(al_destination.get(position));
		
		TextView tv_pickupdate=(TextView)view.findViewById(R.id.textView_pickuptime1);
		String dateString =al_picktime.get(position);
		
		TextView tv_picktime=(TextView)view.findViewById(R.id.textView_pickuptime);
		
		// String dateString = "03/26/2012 11:49:00 AM";
		
		    try 
		    {
		    	DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		    	
		    	Date date1 = (Date)formatter.parse(dateString);
		    	
		    	 formatter = new SimpleDateFormat("yy/MM/dd");
		    	  String  s = formatter.format(date1);
		    	  
		    	System.err.println(s);
		    	tv_pickupdate.setText(s);
		    	formatter = new SimpleDateFormat("HH:mm");
		    	  String  t = formatter.format(date1);
		    	  tv_picktime.setText(t);
		    	  System.err.println(t);
		    }
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
			
		//tv_pickuptime.setText("Pickup: "+ newDateStr);
		
		TextView tv_tripid=(TextView)view.findViewById(R.id.textView_tripid1);
		tv_tripid.setText(al_requesttype.get(position));
		String requesttype=al_requesttype.get(position);
		if(!requesttype.equals(""))
		{
			if(requesttype.equals("Future"))
			{
				tv_picktime.setVisibility(View.GONE);
				tv_pickupdate.setVisibility(View.VISIBLE);
				}
			else
			{
				tv_picktime.setVisibility(View.VISIBLE);
				tv_pickupdate.setVisibility(View.GONE);
				}
			}
		ImageView iv_riderpic=(ImageView)view.findViewById(R.id.imageView_riderpic);
     
		
       if(!(al_image.get(position)==null))
       {
          imageLoader.DisplayImage(al_image.get(position), iv_riderpic);
       		}
       else
       {
    	  iv_riderpic.setImageResource(R.drawable.profilepic);  
       		}
       return view;
   }
  
}