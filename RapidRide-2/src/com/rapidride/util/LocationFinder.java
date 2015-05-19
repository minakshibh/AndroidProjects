package com.rapidride.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import com.rapidride.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class LocationFinder {

	private Context context;
SharedPreferences prefs;
	
	public LocationFinder(Context ctx) {
		context = ctx;
	}
	
	public ArrayList<Coordinates> getLatLongFromAddress(double myLat, double myLon, String youraddress) {
		
			ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
			try{
				
				String address = URLEncoder.encode(youraddress,"utf-8").replace("+", "%20");
				
				String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="+myLat+","+myLon+"&radius="+context.getResources().getString(R.string.near_by_radius)+"&query="+address+"&sensor=false&key="+context.getResources().getString(R.string.places_api_key);
//				String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+myLat+","+myLon+"&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
//				String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=30.711104,76.686153&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
		//	String url=	"https://maps.googleapis.com/maps/api/geocode/json?key="+context.getResources().getString(R.string.places_api_key)+"&address="+address+"&sensor=false";
//		        Log.e("url",url);
		        
				HttpGet httpGet = new HttpGet(url);
				
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
		        
		        Log.e("location search result",stringBuilder.toString());
		        
		        JSONObject jsonObject = new JSONObject();
		        jsonObject = new JSONObject(stringBuilder.toString());
		      
		        JSONArray searchResults =  ((JSONArray)jsonObject.get("results"));
//		        Log.e("total results found","number :: "+searchResults.length());
		        
		        for(int i =0; i<searchResults.length(); i++){
		        	Coordinates cdn = new Coordinates();        	
		        	cdn.latitude = searchResults.getJSONObject(i)
		                    .getJSONObject("geometry").getJSONObject("location")
		                    .getDouble("lat");
		        	
		        	cdn.longitude = searchResults.getJSONObject(i)
		                    .getJSONObject("geometry").getJSONObject("location")
		                    .getDouble("lng");
		        	
		        	cdn.address = searchResults.getJSONObject(i).getString("formatted_address");
		        	cdn.name = searchResults.getJSONObject(i).getString("name");
		        
		        	String location_address=cdn.address;
		        	
		        	System.err.println("search lat"+cdn.latitude);
		        	System.err.println("search logi"+cdn.longitude);
		        	System.err.println("search address===" +location_address);
		        	
//		        	 
		        	coordinates.add(cdn);
		        }
		        
		        return coordinates;
			}catch(Exception e){
				e.printStackTrace();
				return coordinates;
			}
	    }
}
