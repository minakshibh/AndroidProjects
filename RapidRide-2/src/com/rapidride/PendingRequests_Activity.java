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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import com.rapidride.imageloader.RiderAdapter;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class PendingRequests_Activity extends Activity{
	private ListView lv_riderdetails;
//	private ProgressDialog pDialog;
	ImageView iv_riderpic;
	ProgressDialog pDialog1;
	Button btn_prback;
	 
	ArrayList<String> arraylist_ridername=new ArrayList<String>() ;
	ArrayList<String> arraylist_driverid=new ArrayList<String>() ;
	ArrayList<String> arraylist_destination=new ArrayList<String>();
	ArrayList<String> arraylist_start =new ArrayList<String>();
	ArrayList<String> arraylist_tripid =new ArrayList<String>();
	ArrayList<String> arraylist_pickuptime =new ArrayList<String>();
	ArrayList<String> arraylist_requesttype=new ArrayList<String>();
	ArrayList<String> arraylist_riderimage =new ArrayList<String>();
	ArrayList<String> arraylist_riderrating=new ArrayList<String>();
	ArrayList<String> arraylist_riderhandicap=new ArrayList<String>();
	ArrayList<String> arraylist_actualfare=new ArrayList<String>();
	ArrayList<String> arraylist_suggestionfare =new ArrayList<String>();
	ArrayList<String> arraylist_distance=new ArrayList<String>();
	ArrayList<String> arraylist_eta=new ArrayList<String>();
	ArrayList<String> arraylist_endlong=new ArrayList<String>();
	ArrayList<String> arraylist_endlati=new ArrayList<String>();
	ArrayList<String> arraylist_vehicle_type=new ArrayList<String>();
	ArrayList<String> arraylist_riderid=new ArrayList<String>();
	
	ArrayList<String> arraylist_start_long=new ArrayList<String>();
	ArrayList<String> arraylist_start_lat=new ArrayList<String>();
	//DriverDetail_Adapter driverdetails_adpater;
	
	String Trigger=null;
	//ArrayList<String> arraylist_ ;
	String status=null,tripid=null;
	private String jsonResult;
	private String pending_jsonMessage;
	int progess=0;
	
	Typeface typeFace;
	SharedPreferences prefs;
	Timer timer;
	TimerTask timerTask;
	Handler handler = new Handler();
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pending_requests_actvity);
		
		stoptimertask();
		startTimer(); /**Set Timer **/
		
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	
					
		lv_riderdetails=(ListView)findViewById(R.id.listView_pendingreuest_driverdetails);
		lv_riderdetails.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
			if(arraylist_ridername.size()>0)
				 {
				
				stoptimertask();/**stop timer**/

					Intent i=new Intent(PendingRequests_Activity.this,PendingReqDetail_Activity.class);
					i.putExtra("ridername", arraylist_ridername.get(position));
					i.putExtra("destination", arraylist_destination.get(position));
					i.putExtra("pickupdate", arraylist_pickuptime.get(position));
					i.putExtra("requesttype", arraylist_requesttype.get(position));
					i.putExtra("tripid", arraylist_tripid.get(position));
					i.putExtra("riderimage", arraylist_riderimage.get(position));
					i.putExtra("start", arraylist_start.get(position));
					i.putExtra("actualfare", arraylist_actualfare.get(position));
					i.putExtra("suggestionfare", arraylist_suggestionfare.get(position));
					i.putExtra("eta", arraylist_eta.get(position));
					i.putExtra("distance", arraylist_distance.get(position));
					i.putExtra("riderrating", arraylist_riderrating.get(position));
					i.putExtra("riderhandicap", arraylist_riderhandicap.get(position));
					i.putExtra("endlati", arraylist_endlati.get(position));
					i.putExtra("endlong", arraylist_endlong.get(position));
					i.putExtra("vehicletype", arraylist_vehicle_type.get(position));
					i.putExtra("riderid", arraylist_riderid.get(position));
					i.putExtra("startlong", arraylist_start_long.get(position));
					i.putExtra("startlat", arraylist_start_lat.get(position));
					
					Editor ed=prefs.edit();
					ed.putString("currentdriver", arraylist_driverid.get(position));
					ed.commit();
					
					if(!(getIntent().getStringExtra("request1")==null))
					{
						i.putExtra("request1", "1");
						}
					else if(!(getIntent().getStringExtra("queue2")==null))
					{
						i.putExtra("queue2", "2");
						}
				//	finish();
					startActivity(i);
					 }
			}
	});
	
		
	}
	/**Timer**/
    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
      /**schedule the timer, after the first 1000ms the TimerTask will run every 15s */
        timer.schedule(timerTask, 1000, 15000); //
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
              // Toast.makeText(PendingRequests_Activity.this,"requests queue refresh", Toast.LENGTH_SHORT).show();
               /***Clear all array list***/
	   			arraylist_driverid.clear();
	   			arraylist_tripid.clear();
	   			arraylist_ridername.clear();
	   			arraylist_destination.clear();
	   			arraylist_start.clear();
	   			arraylist_pickuptime.clear();
	   			arraylist_requesttype.clear();
	   			arraylist_riderimage.clear();
	   			arraylist_riderrating.clear();
	   			arraylist_actualfare.clear();
	   			arraylist_distance.clear();
	   			arraylist_eta.clear();
	   			arraylist_suggestionfare.clear();
	   			arraylist_riderhandicap.clear();
	   			arraylist_endlati.clear();
	   			arraylist_endlong.clear();
	   			arraylist_vehicle_type.clear();
	   			
	   			arraylist_start_lat.clear();
	   			arraylist_start_long.clear();
	   			
	   			
	   			if(Utility.isConnectingToInternet(PendingRequests_Activity.this))
	   	    	{
	   				/**call pending request class**/
	   				new httpPendingRequest().execute();
	   	    		}
	   	    	else{
	   	    		Utility.alertMessage(PendingRequests_Activity.this,"error in internet connection");
	   	    	}
	   		                                     
               }
                });
            }
        };
	     }
	     
/**Async_task class for httpPendingRequest */
private class httpPendingRequest extends AsyncTask<Void, Void, Void> { 
		
		protected void onPreExecute() {
			super.onPreExecute();
			if(progess==0)
			{
				pDialog1 = new ProgressDialog(PendingRequests_Activity.this);
	//			pDialog1.setTitle("Loading");
				pDialog1.setMessage("Please wait...");
				pDialog1.setCancelable(false);
				if(!((Activity) PendingRequests_Activity.this).isFinishing())
				{
					pDialog1.show();
					}
			progess=1;
			}
		
		}
		
		@Override
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
			pDialog1.dismiss();
			
			RiderAdapter adapter=new RiderAdapter(PendingRequests_Activity.this, arraylist_riderimage,arraylist_ridername,
					arraylist_destination,arraylist_pickuptime,arraylist_requesttype,arraylist_riderrating);
			lv_riderdetails.setAdapter(adapter);
		}
		
			/**pending request code parsing function**/
		public void parsing() throws JSONException {
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
			
			json.put("Role", "Driver");
			System.err.println("Role="+"Driver");
		
			json.put("Id", prefs.getString("driverid", null));
			System.err.println("ID ="+prefs.getString("driverid", null));
			
			if(!(getIntent().getStringExtra("requests")==null))
			{
			System.err.println("requests");
			json.put("Trigger", "requests"); }
			else if(!(getIntent().getStringExtra("queue")==null))
			{
				json.put("Trigger", "queue");
				System.err.println("queue");
			}
		      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.i("tag","pending requests result-->>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				pending_jsonMessage=obj.getString("message");
				
				String ListPromoCodeInfo=	obj.getString("PendingRequestList");
				Log.e("tag:", "PendingRequestList: "+ListPromoCodeInfo);
				
				JSONArray jsonarray=obj.getJSONArray("PendingRequestList");
			
				for(int i=0;i<jsonarray.length();i++){
					
				JSONObject obj2=jsonarray.getJSONObject(i);
				
				String tripId=obj2.getString("tripId");
				Log.i("tag:", "tripId: "+tripId);
				
				String	riderId=	obj2.getString("riderId");
				Log.i("tag:", "riderId: "+riderId);
				
				String driverId=	obj2.getString("driverId");
				Log.i("tag:", "driverId: "+driverId);
				
				String rider_first=	obj2.getString("rider_first");
				Log.i("tag:", "rider_first: "+rider_first);
				
				String start_loc=	obj2.getString("start_loc");
				Log.i("tag:", "start_loc: "+start_loc);
				
				String destination_loc=	obj2.getString("destination_loc");
				Log.i("tag:", "destination_loc: "+destination_loc);
				
				String pickupdate=	obj2.getString("trip_request_pickup_date");
				Log.i("tag:", "pickupdate: "+pickupdate);
				
				String riderimage=	obj2.getString("rider_image");
				Log.i("tag:", "riderimage: "+riderimage);
				
				String driverimage=	obj2.getString("driver_image");
				Log.i("tag:", "driverimage: "+driverimage);
				
				String request_type=	obj2.getString("request_type");
				Log.i("tag:", "request_type: "+request_type);
				
				
				String eta=	obj2.getString("trip_time_est");
				Log.i("tag:", "eta: "+eta);
				
				String actual_fare=	obj2.getString("setfare");
				Log.i("tag:", "actual_fare: "+actual_fare);
				
				String suggestion_fare=	obj2.getString("offered_fare");
				Log.i("tag:", "suggestion_fare: "+suggestion_fare);
				
				String distance=	obj2.getString("trip_miles_est");
				
			
				Log.i("tag:", "distance: "+distance);
				
				String riderRating=	obj2.getString("rider_rating");
				Log.i("tag:", "riderRating: "+riderRating);
				
				String riderHandicap=	obj2.getString("rider_handicap");
				Log.i("tag:", "riderHandicap: "+riderHandicap);
				
				String end_long=	obj2.getString("end_long");
				Log.i("tag:", "end_long: "+end_long);
				
				String end_lat=	obj2.getString("end_lat");
				Log.i("tag:", "end_lat: "+end_lat);
				
				String start_long=	obj2.getString("start_long");
				Log.i("tag:", "start_long: "+start_long);
				
				String start_lat=	obj2.getString("start_lat");
				Log.i("tag:", "start_lat: "+start_lat);
				
				String vehicle_type=	obj2.getString("vehicle_type");
				Log.i("tag:", "vehicle_type: "+vehicle_type);
				
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+pending_jsonMessage);
				
				arraylist_driverid.add(driverId);
				arraylist_tripid.add(tripId);
				arraylist_riderid.add(tripId);
				arraylist_ridername.add(rider_first);
				arraylist_destination.add(destination_loc);
				arraylist_start.add(start_loc);
				arraylist_pickuptime.add(pickupdate);
				arraylist_requesttype.add(request_type);
				arraylist_riderimage.add(riderimage);
				arraylist_riderrating.add(riderRating);
				arraylist_actualfare.add(actual_fare);
				arraylist_distance.add(distance);
				arraylist_eta.add(eta);
				arraylist_suggestionfare.add(suggestion_fare);
				arraylist_riderhandicap.add(riderHandicap);
				arraylist_endlong.add(end_long);
				arraylist_endlati.add(end_lat);
				
				arraylist_start_long.add(start_long);
				arraylist_start_lat.add(start_lat);
				arraylist_vehicle_type.add(vehicle_type);
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
	Intent i=new Intent(PendingRequests_Activity.this,DriverView_Activity.class);
	startActivity(i);
	super.onBackPressed();
	 }

}

