package com.twentyfourseven.zira;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.GetTripDetails;
import com.zira.modal.MainVehicleModel;
import com.zira.notification.RiderNotification;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class RiderRideView extends Activity implements LocationListener,AsyncResponseForZira {
	GoogleMap googleMap;
	//using current latlog and destination latlong of tripid 635 
	double curLongitude,curLatitude,destLongitude,destLatitude;
	private GetTripDetails mTripDetailsModel;
	
	LocationManager locationManager;
	Criteria criteria;
	String provider;
	SharedPreferences prefs;
	float totalKms =00,totalKmInMiles=00;
	private int distanceValue,timeValue,approxMinutes;
	private MainVehicleModel vehicleModel;
	String distance,time,myAddress;
	double tripStartLat,tripStartLong,tripdestinationLat,tripdestinationLong;
	float actualfare;
	int flagdis=0;
	String newactualf="";
	private ZiraParser parser;
	private  AsyncTaskForGettingVehicle mAsyncTask;
	private RelativeLayout profileLayout;
	private Button button_getdirection;
	String selectedDate;
	int flag=0;
	double longitude, latitude,myLat,myLon;
	String GetDetailsByTripId="GetDetailsByTripId";
	private GetTripDetails tripDetailsModal;
@Override
protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.view_rider_ride);
	
	mTripDetailsModel=SingleTon.getInstance().getGetTripDetail();
	prefs = getSharedPreferences("Zira", MODE_PRIVATE);
	parser =new ZiraParser();
	tripDetailsModal=new GetTripDetails();
	//getDriverDetailByTripID();
	
	button_getdirection=(Button)findViewById(R.id.button_getdirection);
	
	button_getdirection.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			
			String DestinationAddress=prefs.getString("tripdestinationadd", "mohali").replace(" ", "+");
			System.err.println("destination="+tripDetailsModal.getGetTrip_DestnationLatitude());
			System.err.println("destination="+tripDetailsModal.getGetTrip_DestinationLongitude());
			if(isGoogleMapsInstalled())
			{	
			
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse("google.navigation:q="+prefs.getString("tripdestlat", "")+","+prefs.getString("tripdestlong", "")));
				startActivity(intent);
				}
			else
			{
			String	url="http://maps.google.com/maps?daddr="+DestinationAddress+"&x-success=sourceapp://?resume=true&x-source=AirApp";
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,Uri.parse(url));
			startActivity(intent);
			
			}
		}
	});

	try {
		// Loading map
		initilizeMap();

	} catch (Exception e) {
		e.printStackTrace();
	}

}

/**
 * function to load map. If map is not created it will create it for you
 * */
private void initilizeMap() {
	if (googleMap == null) {
		try{
			prefs = getSharedPreferences("Zira", MODE_PRIVATE);
			curLatitude = Double.parseDouble(prefs.getString("tripstarttlat", ""));
			curLongitude = Double.parseDouble(prefs.getString("tripstartlong", ""));
			destLongitude = Double.parseDouble(prefs.getString("tripdestlong", ""));
			destLatitude = Double.parseDouble(prefs.getString("tripdestlat", ""));
			
			
	//	curLatitude=Double.parseDouble(mTripDetailsModel.getGetTrip_StartingLatitude());
	//	curLongitude=Double.parseDouble(mTripDetailsModel.getGetTrip_StartingLongitude());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.rider_ride_map)).getMap();
		googleMap.setMyLocationEnabled(true);		
		googleMap.getUiSettings().setRotateGesturesEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(false);
		CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(curLatitude, curLongitude)).zoom(15).build();

		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

		//destLongitude=Double.parseDouble(mTripDetailsModel.getGetTrip_EndLongitude());
		//destLatitude=Double.parseDouble(mTripDetailsModel.getGetTrip_EndLatitude());
		
		placeMarker();
		
		
	
		LatLng curLatLng=new  LatLng(curLatitude, curLongitude);
		LatLng destLatLng=new  LatLng(destLatitude, destLongitude);
		
		Log.i("tag", "Longitude:"+curLongitude);
		Log.i("tag", "Latitude:"+curLongitude);
		Log.i("tag", "destLongitude:"+destLongitude);
		Log.i("tag", "destLatitude:"+destLatitude);
		
		String url = getDirectionsUrl(curLatLng, destLatLng);				 
		DownloadTask downloadTask = new DownloadTask();               
		downloadTask.execute(url);
		

		// check if map is created successfully or not
		if (googleMap == null) {
			Toast.makeText(getApplicationContext(),
					"Sorry! unable to create maps", Toast.LENGTH_SHORT)
					.show();
		}
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		criteria = new Criteria();

		// Getting the name of the best provider
		provider = locationManager.getBestProvider(criteria, true);

		try {
			boolean enabledGPS = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean enabledWiFi = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			// Check if enabled and if not send user to the GSP settings
			if (!enabledGPS && !enabledWiFi) {
				Toast.makeText(RiderRideView.this, "GPS signal not found",Toast.LENGTH_LONG).show();
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivity(intent);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		locationManager.requestLocationUpdates(provider, 15000, 0, this);
	}
}

@Override
protected void onResume() {
	super.onResume();
	initilizeMap();
}

// placing marker
public void placeMarker() {
	// create marker
	//MarkerOptions curLocationmarker = new MarkerOptions().position(new LatLng(curLatitude, curLongitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
	 // adding marker
	googleMap.addMarker(new MarkerOptions().position(new LatLng(curLatitude, curLongitude)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
	
	MarkerOptions destLocationmarker = new MarkerOptions().position(new LatLng(destLatitude, destLongitude));
	 // adding marker
	googleMap.addMarker(destLocationmarker);
}
/**********Path Draw***********/
private String getDirectionsUrl(LatLng origin,LatLng dest){

	// Origin of route
	String str_origin = "origin="+origin.latitude+","+origin.longitude;

	// Destination of route
	String str_dest = "destination="+dest.latitude+","+dest.longitude;

	// Sensor enabled
	String sensor = "sensor=false";

	// Building the parameters to the web service
	String parameters = str_origin+"&"+str_dest+"&"+sensor;

	// Output format
	String output = "json";

	// Building the url to the web service
	String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

	Log.e("", url);

	return url;
}
/** A method to download json data from url */
private String downloadUrl(String strUrl) throws IOException{
	String data = "";
	InputStream iStream = null;
	HttpURLConnection urlConnection = null;
	try{
		URL url = new URL(strUrl);

		// Creating an http connection to communicate with url
		urlConnection = (HttpURLConnection) url.openConnection();

		// Connecting to url
		urlConnection.connect();

		// Reading data from url
		iStream = urlConnection.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

		StringBuffer sb = new StringBuffer();

		String line = "";
		while( ( line = br.readLine()) != null){
			sb.append(line);
		}

		data = sb.toString();

		br.close();

	}catch(Exception e){
		Log.d("Exception while downloading url", e.toString());
	}finally{
		iStream.close();
		urlConnection.disconnect();
	}
	return data;
}

// Fetches data from url passed
private class DownloadTask extends AsyncTask<String, Void, String>{

	// Downloading data in non-ui thread
	@Override
	protected String doInBackground(String... url) {

		// For storing data from web service
		String data = "";

		try{
			// Fetching the data from web service
			data = downloadUrl(url[0]);
		}catch(Exception e){
			Log.d("Background Task",e.toString());
		}
		return data;
	}

	// Executes in UI thread, after the execution of
	// doInBackground()
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);

		ParserTask parserTask = new ParserTask();

		// Invokes the thread for parsing the JSON data
		parserTask.execute(result);
	}
}

/** A class to parse the Google Places in JSON format */
private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

	// Parsing the data in non-ui thread
	@Override
	protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

		JSONObject jObject;
		List<List<HashMap<String, String>>> routes = null;

		try{
			jObject = new JSONObject(jsonData[0]);
			DirectionsJSONParser parser = new DirectionsJSONParser();

			// Starts parsing data
			routes = parser.parse(jObject);
		}catch(Exception e){
			e.printStackTrace();
		}
		return routes;
	}

	// Executes in UI thread, after the parsing process
	@Override
	protected void onPostExecute(List<List<HashMap<String, String>>> result) {
		ArrayList<LatLng> points = null;
		PolylineOptions lineOptions = null;
		MarkerOptions markerOptions = new MarkerOptions();

		// Traversing through all the routes
		for(int i=0;i<result.size();i++){
			points = new ArrayList<LatLng>();
			lineOptions = new PolylineOptions();

			// Fetching i-th route
			List<HashMap<String, String>> path = result.get(i);

			// Fetching all the points in i-th route
			for(int j=0;j<path.size();j++){
				HashMap<String,String> point = path.get(j);

				double lat = Double.parseDouble(point.get("lat"));
				double lng = Double.parseDouble(point.get("lng"));
				LatLng position = new LatLng(lat, lng);

				points.add(position);
			}

			// Adding all the points in the route to LineOptions
			lineOptions.addAll(points);
			lineOptions.width(8);
			lineOptions.color(Color.RED);
		}

		// Drawing polyline in the Google Map for the i-th route
		googleMap.addPolyline(lineOptions);
	}
}
public void onLocationChanged(Location location) {

	Log.e("in", "onlocationChanged");

	try {
		if (flag == 0) {
			flag = 1;
			myLat = location.getLatitude();

			// Getting longitude of the current location
			myLon = location.getLongitude();

			//getAddress(location, "start");
			Log.e("values", "lat : " + myLat + " , lon : " + myLon);

			// Creating a LatLng object for the current location
			LatLng latLng = new LatLng(myLat, myLon);

			// Showing the current location in Google Map
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));

			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
			//googleMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLon)).icon(BitmapDescriptorFactory.defaultMarker()));

			latitude = myLat;
			longitude =myLon;
		
			//getVehicleData();
		}

		else
		{
			//googleMap.clear();
			latitude = location.getLatitude();
			longitude = location.getLongitude();
			System.err.println("location update rider");
			LatLng latLng1 = new LatLng(latitude, longitude);
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1,15));
		
			}
	
				/*if(flagdis==0)
				{
					flagdis=1;
					tripStartLat=latitude;
					tripStartLong=longitude;
					//googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.defaultMarker()));
					}
				else
				{
					tripdestinationLat=latitude;
					tripdestinationLong=longitude;
					}*/
			//getVehicleData();////vehicle fetch web services call
			///////////////////////Double tripStartLat,tripStartLong,tripdestinationLat,tripdestinationLong;
			
	} catch (Exception e) {
		e.printStackTrace();
	}
	
}


public void onProviderDisabled(String provider) {
}
public void onProviderEnabled(String provider) {
}
public void onStatusChanged(String provider, int status, Bundle extras) {
}

/****************Actual fare calculations***********************************/
private String getCurrentDateTime() {		
	Calendar mCalendar=Calendar.getInstance();
	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	selectedDate=df.format(mCalendar.getTime());	
	return selectedDate;
}
protected void getVehicleData() {	
	if(Util.isNetworkAvailable(RiderRideView.this)){

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	nameValuePairs.add(new BasicNameValuePair("longitude",String.valueOf(longitude)));
	nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
	nameValuePairs.add(new BasicNameValuePair("currenttime",getCurrentDateTime()));
	nameValuePairs.add(new BasicNameValuePair("distance","10"));
	nameValuePairs.add(new BasicNameValuePair("VechileType",VehicleSearchActivity.str_vehicleType));
	nameValuePairs.add(new BasicNameValuePair("riderid",prefs.getString("riderid", null)));	

	mAsyncTask=new AsyncTaskForGettingVehicle("hello", nameValuePairs, RiderRideView.this);
	mAsyncTask.execute();
	}
	else
	{
		Util.alertMessage(RiderRideView.this, "Please check your internet connection");
		}

}

public class AsyncTaskForGettingVehicle extends AsyncTask<String, Void, String> {

	private Activity activity;		
	private String result = "";			
	private String methodName;		
	private ArrayList<NameValuePair> nameValuePairs;

	public AsyncTaskForGettingVehicle(String methodName,ArrayList<NameValuePair> nameValuePairs,Activity activity) {
		this.activity = activity;
		this.methodName = methodName;
		this.nameValuePairs = nameValuePairs;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	@Override
	protected String doInBackground(String... urls) {

		for(int i=0;i<2;i++){
			if(mAsyncTask.isCancelled()) break;
		}
		result = Util.getResponseFromUrl("FetchVehicleList", nameValuePairs, activity);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {			
		super.onPostExecute(result);
		
		vehicleModel = parser.parseVehicleInZone(result);
		
		SingleTon.getInstance().setMainVehicleModel(vehicleModel);
		
		new	distanceCalculatorAsyncTask().execute();
	}
}
class distanceCalculatorAsyncTask extends AsyncTask<String, String, String>{
	protected void onPreExecute() {


	}		    

	@Override
	protected String doInBackground(String... str) {
//		Log.i("tag", "Current Lati:"+gettingSourceLat+"  Current Longitude:"+gettingSourceLong+"  Destination_Lati:"+gettingdestinationLat+" Destination Longitude:"+gettingDestinationLong);
		String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+tripStartLat+","+tripStartLong+"&destinations="+tripdestinationLat+","+tripdestinationLong+"&mode=driving&language=en-EN&sensor=false";

		try{
			HttpGet httpGet = new HttpGet(distance_url);
			HttpClient client = new DefaultHttpClient();
			HttpResponse response;
			StringBuilder stringBuilder = new StringBuilder();
			response = client.execute(httpGet);
			HttpEntity entity = response.getEntity();
			InputStream stream = entity.getContent();
			int b;

			while ((b = stream.read()) != -1) {
				stringBuilder.append((char) b);
			}

			Log.e("result",stringBuilder.toString());
			JSONObject jsonObject = new JSONObject();
			jsonObject = new JSONObject(stringBuilder.toString());
			myAddress = jsonObject.getJSONArray("origin_addresses").getString(0);
			JSONArray searchResults =  jsonObject.getJSONArray(("rows"));

			for(int i = 0; i<searchResults.length(); i++){
				JSONArray elementArray = searchResults.getJSONObject(i).getJSONArray("elements");
				for(int j =0; j<elementArray.length(); j++){
					distance = elementArray.getJSONObject(j).getJSONObject("distance").getString("text");
					distanceValue = elementArray.getJSONObject(j).getJSONObject("distance").getInt("value");

					totalKms = (float)(distanceValue*0.001);
					totalKmInMiles=(float) (totalKms*0.62137);
					Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);

					time    = elementArray.getJSONObject(j).getJSONObject("duration").getString("text");
					timeValue = elementArray.getJSONObject(j).getJSONObject("duration").getInt("value");

					float temp= (float)timeValue/60;
					approxMinutes = Math.round(temp);
					Log.e("result","Option "+" :: "+distance);
					System.err.print("time"+ time);
				}
			}
			Log.e("done","done");

		}catch(Exception e){
			Log.e("error","error");
			e.printStackTrace();
		}

		return "success";
	}


	@Override
	protected void onPostExecute(String result) {
		if(result.equals("success")){	
	//	GetTripDetails getTripDtails=new GetTripDetails();
		Log.e("vehicle type=",	VehicleSearchActivity.str_vehicleType);
		//calculateFare(getTripDtails.getGetTrip_VehicleType());
		calculateFare(VehicleSearchActivity.str_vehicleType);
		
		String actualf1=new DecimalFormat("##.##").format(actualfare);
		
		Log.e("newactualf=", ""+actualf1);
		/*try{
			Float	float_newactualf=	Float.parseFloat(actualf1) + Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getSafetycharges());
			newactualf=new DecimalFormat("##.##").format(float_newactualf);
			
		}
		catch(Exception e)
		{
			
		}*/
	/*	newactualf=actualf1;
			Log.e("newactualf", ""+newactualf);
			Editor e=prefs.edit();
			e.putString("newfare", newactualf);
			e.commit();*/
			
		}
	}
}

/**Method Calculation fare**/

public void calculateFare(String vehicleType) {

	if(vehicleType.equalsIgnoreCase("ZiraE")){
		try{
				actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getBasePrice())+
					(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice())
							+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice())));
			
					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getSurgeprice());
				if(surgePrice>0.0)
				{
					actualfare=actualfare*surgePrice;
					}
				System.err.println("ZiraEEEEEEEEEE=");
				System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getBasePrice()));
				System.err.println("approxMinutes="+approxMinutes);
				System.err.println("totalKmInMiles="+totalKmInMiles);
				System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice()));
				System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice()));
				System.err.println("sirf actualfare="+actualfare);
				actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				System.err.println("actualfaer+safty="+actualfare);
		}catch(Exception e){
			System.err.println("actual zira e="+e);
			}
	} 
	else if(vehicleType.equalsIgnoreCase("ZiraPlus")){
		try{
		actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getBasePrice())+
				(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice())
						+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice())));
		

			float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getSurgeprice());
			if(surgePrice>0.0)
			{
				actualfare=actualfare*surgePrice;
			}
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			
			System.err.println("ZiraPlus pppppp=");
			System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getBasePrice()));
			System.err.println("approxMinutes="+approxMinutes);
			System.err.println("totalKmInMiles="+totalKmInMiles);
			System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice()));
			System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice()));
			System.err.println("sirf actualfare="+actualfare);
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			System.err.println("actualfaer+safty="+actualfare);
			
		}catch(Exception e){
			System.err.println("actual zira plus="+e);
		}
	}
	else if(vehicleType.equalsIgnoreCase("ZiraTaxi")){
		try{
		actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getBasePrice())+
				(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice())
						+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice())));
	
		float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getSurgeprice());
			if(surgePrice>0.0)
			{
				actualfare=actualfare*surgePrice;
			}
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			
			System.err.println("ZiraTaxi ttttt=");
			System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getBasePrice()));
			System.err.println("approxMinutes="+approxMinutes);
			System.err.println("totalKmInMiles="+totalKmInMiles);
			System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice()));
			System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice()));
			System.err.println("sirf actualfare="+actualfare);
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			System.err.println("actualfaer+safty="+actualfare);
			
			
		}catch(Exception e){
			System.err.println("actual taxi="+e);
		}
	}
	else if(vehicleType.equalsIgnoreCase("ZiraLux")){
		try{
			actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getBasePrice())+
				(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice())
						+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice())));
		

			float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getSurgeprice());
			if(surgePrice>0.0)
			{
				actualfare=actualfare*surgePrice;
			}
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			
			System.err.println("ZiraLux xxxxxxx=");
			System.err.println("baseprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getBasePrice()));
			System.err.println("approxMinutes="+approxMinutes);
			System.err.println("totalKmInMiles="+totalKmInMiles);
			System.err.println("minprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice()));
			System.err.println("mileprice="+Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice()));
			System.err.println("sirf actualfare="+actualfare);
			actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
			System.err.println("actualfaer+safty="+actualfare);
			
		}catch(Exception e){
			System.err.println("actual lux="+e);
		}
	}
}

public boolean isGoogleMapsInstalled()
{
    try
    {
        ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
        return true;
    } 
    catch(PackageManager.NameNotFoundException e)
    {
        return false;
    }
}
@Override
public void onBackPressed() {
	
}
private void getDriverDetailByTripID() {

	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	nameValuePairs.add(new BasicNameValuePair("TripId",prefs.getString("tripid", "")));
	Log.e("GetDetailsByTripId", nameValuePairs.toString());
	AsyncTaskForZira mFetchStates = new AsyncTaskForZira(RiderRideView.this, GetDetailsByTripId,nameValuePairs, false, "");
	mFetchStates.delegate = (AsyncResponseForZira) RiderRideView.this;
	mFetchStates.execute();		
}
public void processFinish(String output, String methodName) {

	
	JSONObject obj;
	try {
		obj = new JSONObject(output);
		String jsonMessage=obj.getString("message");
	String	jsonResult1=obj.getString("result");
	if(methodName.equals(GetDetailsByTripId)){
		if(jsonResult1.equals("0")){
			System.err.println("mTripDetailsModel"+output);
			mTripDetailsModel = parser.parsegetTripDetails(output);
			SingleTon.getInstance().setGetTripDetail(mTripDetailsModel);
			//Log.e("mTripDetailsModel", "success");
			newactualf=mTripDetailsModel.getGetTrip_ActaulFare();
			Editor e=prefs.edit();
			e.putString("newfare", newactualf);
			e.commit();
			System.err.println("newactualf="+newactualf);
		}}
//		Toast.makeText(RiderNotification.this, jsonMessage, 0).show();
	}catch(JSONException exception){

	


	
	}
}

}