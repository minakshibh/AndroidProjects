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
import com.rapidride.imageloader.DriverAdapter;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class RiderQueue_Activity extends Activity
{
	ListView lv_riderqueue;
	public ProgressDialog pDialog;
	Typeface typeFace;
	SharedPreferences prefs;
	
	ArrayList<String> arraylist_drivername=new ArrayList<String>();
	ArrayList<String> arraylist_destination=new ArrayList<String>();
	ArrayList<String> arraylist_tripid=new ArrayList<String>();
	ArrayList<String> arraylist_requesttype=new ArrayList<String>();
	ArrayList<String> arraylist_distance=new ArrayList<String>();
	ArrayList<String> arraylist_pickuptime=new ArrayList<String>();
	ArrayList<String> arraylist_driver_image=new ArrayList<String>();
	ArrayList<String> arraylist_rider_image=new ArrayList<String>();
	ArrayList<String> arraylist_driverid=new ArrayList<String>();
	ArrayList<String> arraylist_riderid=new ArrayList<String>();
	ArrayList<String> arraylist_start=new ArrayList<String>();
	ArrayList<String> arraylist_actualfare=new ArrayList<String>();
	ArrayList<String> arraylist_suggestion=new ArrayList<String>();
	ArrayList<String> arraylist_eta=new ArrayList<String>();
	ArrayList<String> arraylist_driverrating=new ArrayList<String>();
	ArrayList<String> arraylist_status=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_color=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_type=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_name=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_img=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_year=new ArrayList<String>();
		
	
	Timer timer;
	TimerTask timerTask;
	final Handler handler = new Handler();
	
	int progess=0;
	
protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.riderqueue_activity);
	 
	stoptimertask();
	startTimer();/****Start Timer Task***/
	
	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	
	lv_riderqueue=(ListView)findViewById(R.id.listView_rider_queue);
	
	
	lv_riderqueue.setOnItemClickListener(new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			
			 stoptimertask();/****Stop Timer Task***/
			 
			Intent i=new Intent(RiderQueue_Activity.this,RiderQueueDetails_Activity.class);
			i.putExtra("drivername", arraylist_drivername.get(position));
			i.putExtra("destination", arraylist_destination.get(position));
			i.putExtra("distance", arraylist_distance.get(position));
			i.putExtra("requesttype", arraylist_requesttype.get(position));
			//i.putExtra("riderimage", arraylist_rider_image.get(position));
			i.putExtra("driverimage", arraylist_driver_image.get(position));
			i.putExtra("tripid", arraylist_tripid.get(position));
			i.putExtra("driverid", arraylist_driverid.get(position));
			i.putExtra("start", arraylist_start.get(position));
			i.putExtra("suggestion", arraylist_suggestion.get(position));
			i.putExtra("eta", arraylist_eta.get(position));
			i.putExtra("actual", arraylist_actualfare.get(position));
			i.putExtra("riderid", arraylist_riderid.get(position));
			i.putExtra("driverrating", arraylist_driverrating.get(position));
			i.putExtra("pickupdate", arraylist_pickuptime.get(position));

			
			i.putExtra("status", arraylist_status.get(position));
			i.putExtra("vehiclecolor", arraylist_vehicle_color.get(position));
			i.putExtra("vehicletype", arraylist_vehicle_type.get(position));
			i.putExtra("vehiclename", arraylist_vehicle_name.get(position));
			i.putExtra("vehicleimage", arraylist_vehicle_img.get(position));
			i.putExtra("vehicleyear", arraylist_vehicle_year.get(position));
			
			System.err.println("tripid="+arraylist_tripid.get(position));
			//finish();
			startActivity(i);
		    }
		
	});
	
}
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
       		
			arraylist_destination.clear();
			arraylist_driverid.clear();
			arraylist_riderid.clear();
			arraylist_drivername.clear();
			arraylist_pickuptime.clear();
			arraylist_tripid.clear();
			arraylist_driver_image.clear();
			arraylist_rider_image.clear();
			arraylist_distance.clear();
			arraylist_requesttype.clear();
			arraylist_start.clear();
			arraylist_actualfare.clear();
			arraylist_suggestion.clear();
			arraylist_eta.clear();
			arraylist_driverrating.clear();
			arraylist_status.clear();
			arraylist_vehicle_color.clear();
			arraylist_vehicle_type.clear();
			arraylist_vehicle_name.clear();
			arraylist_vehicle_img.clear();
			arraylist_vehicle_year.clear();
			
			
			if(Utility.isConnectingToInternet(RiderQueue_Activity.this))
   	    	{
   				/**call pending request for rider queue class**/
				new httpPendingRequest_riderqueue().execute(); 
   	    		}
   	    	else{
   	    		Utility.alertMessage(RiderQueue_Activity.this,"error in internet connection");
   	    	}
		                     
           }
            });
        }
    };
    }

    
    
    
    /**********************End main Function***********************************/
    
    
    
    
    
    
/**Async_task class for PendingRequest */
private class httpPendingRequest_riderqueue extends AsyncTask<Void, Void, Void> { 
	String tag="RiderQueue:PendingRequest_riderqueue";
	protected void onPreExecute() {
		super.onPreExecute();
//	if(progess==0)
//		{
			pDialog = new ProgressDialog(RiderQueue_Activity.this);
		//	pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			progess=1;
		//	}
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
		
	//	System.err.println("rider queue postttttttt");
		DriverAdapter adapter=new DriverAdapter(RiderQueue_Activity.this, arraylist_driver_image,arraylist_drivername,
		arraylist_destination,arraylist_pickuptime,arraylist_distance,arraylist_requesttype);
		lv_riderqueue.setAdapter(adapter);
		
		
		}
//pending request code parsing function
		public void parsing() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 30000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 31000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/FetchPendingRideRequests");//Url_Address.url_promocode);
		JSONObject json = new JSONObject();
		
		json.put("Trigger", "FetchPendingRideRequests");
		
		json.put("Role", "Rider");
		System.err.println("Rider");
	
		json.put("Id", prefs.getString("userid", null));
		System.err.println(prefs.getString("userid", null));
		
		json.put("Trigger", "queue");
		System.err.println("rider queue");
	      //	      
		httpost.setEntity(new StringEntity(json.toString()));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(httpost);
		HttpEntity resEntityGet = response.getEntity();
		String jsonstr=EntityUtils.toString(resEntityGet);
		if(jsonstr!=null)
		{
		 Log.i(tag," result-->>>>>    "+ jsonstr);
		}
		JSONObject obj=new JSONObject(jsonstr);
		String	jsonResult=obj.getString("result");
		String	pending_jsonMessage=obj.getString("message");
			
			String PendingRequestList=	obj.getString("PendingRequestList");
			Log.i(tag, "PendingRequestList queue: "+PendingRequestList);
			
			JSONArray jsonarray=obj.getJSONArray("PendingRequestList");
	
			
		for(int i=0;i<jsonarray.length();i++){
				
			JSONObject obj2=jsonarray.getJSONObject(i);
			
			String tripId=obj2.getString("tripId");
			Log.i(tag, "tripId: "+tripId);
			
			String	riderId=	obj2.getString("riderId");
			Log.i(tag, "riderId: "+riderId);
			
			String driverId=	obj2.getString("driverId");
			Log.i(tag, "driverId: "+driverId);
			
			String driver_first=	obj2.getString("driver_first");
			Log.i(tag, "driver_first: "+driver_first);
			
			String driver_last=	obj2.getString("driver_last");
			Log.i(tag, "driver_last: "+driver_last);
			
			String trip_miles_est=	obj2.getString("trip_miles_est");
			Log.i(tag, "trip_miles_est: "+trip_miles_est);
			
			String trip_time_est=	obj2.getString("trip_time_est");
			Log.i(tag, "trip_time_est: "+trip_time_est);
			
			String start_loc=	obj2.getString("start_loc");
			Log.i(tag, "start_loc: "+start_loc);
			
			String destination_loc=	obj2.getString("destination_loc");
			Log.i(tag, "destination_loc: "+destination_loc);
			
			String request_type=	obj2.getString("request_type");
			Log.i(tag, "request_type: "+request_type);
			
			String trip_request_date=	obj2.getString("trip_request_date");
			Log.i(tag, "request_type: "+trip_request_date);
			
			String driver_image=	obj2.getString("driver_image");
			Log.i(tag, "driver_image: "+driver_image);
			
			String rider_image=	obj2.getString("rider_image");
			Log.i(tag, "rider_image: "+rider_image);
			
			
			String setfare=	obj2.getString("setfare");
			Log.i(tag, "setfare: "+setfare);
			
			String offered_fare=	obj2.getString("offered_fare");
			Log.i(tag, "offered_fare: "+offered_fare);
			
			String driver_rating=	obj2.getString("driver_rating");
			Log.i("tag:", "riderRating: "+driver_rating);
			

			
			String status=	obj2.getString("status");
			Log.i(tag, "status: "+status);
			
			String vehicle_color=	obj2.getString("vehicle_color");
			Log.i(tag, "vehicle_color: "+vehicle_color);
			
			String vehicle_type=	obj2.getString("vehicle_type");
			Log.i("tag:", "vehicle_type: "+vehicle_type);
			
			String vehicle_name=	obj2.getString("vehicle_name");
			Log.i("tag:", "vehicle_name: "+vehicle_name);

				
			String vehicle_img=	obj2.getString("vehicle_img");
			Log.i("tag:", "vehicle_img: "+vehicle_img);
		
			String vehicle_year=	obj2.getString("vehicle_year");
			Log.i("tag:", "vehicle_year: "+vehicle_year);
	
			
			
			Log.i(tag, "Result: "+jsonResult);
			Log.i(tag, "Message :"+pending_jsonMessage);
			
			arraylist_destination.add(destination_loc);
			arraylist_driverid.add(driverId);
			arraylist_riderid.add(riderId);
			arraylist_drivername.add(driver_first);
			arraylist_pickuptime.add(trip_request_date);
			arraylist_tripid.add(tripId);
			arraylist_driver_image.add(driver_image);
			arraylist_rider_image.add(rider_image);
			arraylist_distance.add(trip_miles_est);
			arraylist_requesttype.add(request_type);
			arraylist_start.add(start_loc);
			arraylist_actualfare.add(setfare);
			arraylist_suggestion.add(offered_fare);
			arraylist_eta.add(trip_time_est);
			arraylist_driverrating.add(driver_rating);
			arraylist_status.add(status);
			arraylist_vehicle_color.add(vehicle_color);
			arraylist_vehicle_type.add(vehicle_type);
			arraylist_vehicle_name.add(vehicle_name);
			arraylist_vehicle_img.add(vehicle_img);
			
			
			arraylist_vehicle_year.add(vehicle_year);
	
			
			}
		}
		  catch(Exception e){
		   System.out.println(e);
		   Log.d(tag, "Error :"+e); }  
			}
		}
@Override
public void onBackPressed() {
	 stoptimertask();/****Stop Timer Task***/
	 finish();
	super.onBackPressed();
}

}