package com.rapidride.imageloader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.rapidride.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DriverAdapter extends BaseAdapter {
    
   private Activity activity;
   private ArrayList<String> imag;
   private ArrayList<String> name;
   private ArrayList<String> destination;
   private ArrayList<String> picktime;
   private ArrayList<String> distance;
   private ArrayList<String> requesttype;
   private static LayoutInflater inflater=null;
   public ImageLoader imageLoader; 
    
   public DriverAdapter(Activity a, ArrayList<String> i,ArrayList<String> n,ArrayList<String> d,ArrayList<String> p,ArrayList<String> d2,ArrayList<String> r) {
       activity = a;
       imag=i;
       name=n;
       destination=d;
       picktime=p;
       distance=d2;
       requesttype=r;
       inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       imageLoader=new ImageLoader(activity.getApplicationContext());
   }

   public int getCount() {
       return name.size();
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
           view = inflater.inflate(R.layout.riderqueue_adapter, null);

       TextView tv_name=(TextView)view.findViewById(R.id.textView_drivername1);
       tv_name.setText(""+name.get(position));
       TextView tv_desti=(TextView)view.findViewById(R.id.textView_destination1);
       tv_desti.setText(destination.get(position));
		
       String datestring=picktime.get(position);
		TextView tv_pickupdate=(TextView)view.findViewById(R.id.textView_pickupdate);
		TextView tv_time=(TextView)view.findViewById(R.id.textView_pickuptime);
		 try 
		    {
		    	DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		    	
		    	Date date1 = (Date)formatter.parse(datestring);
		    	
		    	 formatter = new SimpleDateFormat("yy/MM/dd");
		    	  String  s = formatter.format(date1);
		    	  
		    	System.err.println(s);
		    	tv_pickupdate.setText(s);
		    	formatter = new SimpleDateFormat("HH:mm");
		    	  String  t = formatter.format(date1);
		    	  tv_time.setText(t);
		    	  System.err.println(t);
		    }
		    catch (Exception e) 
		    {
		        e.printStackTrace();
		    }
		 String requesttype1=requesttype.get(position).replace(" ","");
		// System.err.println("requesttype="+requesttype1);
			if(requesttype!=null)
			{
				if(requesttype1.equals("Future"))
				{
					tv_time.setVisibility(View.GONE);
					tv_pickupdate.setVisibility(View.VISIBLE);
					 System.err.println("requesttype="+requesttype1);
					}
				else
				{
					tv_time.setVisibility(View.VISIBLE);
					tv_pickupdate.setVisibility(View.GONE);
					}
				}
	//	TextView tv_tripid=(TextView)view.findViewById(R.id.textView_pickuptime);
		
       ImageView image=(ImageView)view.findViewById(R.id.imageView_driverpic);
	      
       if(imag.get(position)!=null)
       {
	       imageLoader.DisplayImage(imag.get(position), image);
	       }
       else
       {
    	  image.setImageResource(R.drawable.profilepic);  
       		}
       return view;
   }
}