package com.twentyfourseven.zira;



import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.twentyfourseven.zira.R;
import com.zira.modal.Coordinates;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

public class LocationFinder {

	private Context context;
	
	public LocationFinder(Context ctx) {
		
		context = ctx;
	}
	
	public String getAddressFromLocation(Location location){
		Geocoder geocoder =
	            new Geocoder(context, Locale.getDefault());
	    // Get the current location from the input parameter list
	    Location loc = location;
	    // Create a list to contain the result address
	    List<Address> addresses = null;
	    try {
	        /*
	         * Return 1 address.
	         */
	        addresses = geocoder.getFromLocation(loc.getLatitude(),
	                loc.getLongitude(), 1);
	    } catch (IOException e1) {
	    Log.e("LocationSampleActivity",
	            "IO Exception in getFromLocation()");
	    e1.printStackTrace();
	    return ("IO Exception trying to get address");
	    } catch (IllegalArgumentException e2) {
	    // Error message to post in the log
	    String errorString = "Illegal arguments " +
	            Double.toString(loc.getLatitude()) +
	            " , " +
	            Double.toString(loc.getLongitude()) +
	            " passed to address service";
	    Log.e("LocationSampleActivity", errorString);
	    e2.printStackTrace();
	    return errorString;
	    }
	    // If the reverse geocode returned an address
	    if (addresses != null && addresses.size() > 0) {
	        // Get the first address
	        Address address = addresses.get(0);
	        StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(address.getAddressLine(i)).append("\n");
            }
            
            String addressText = strReturnedAddress.toString();
            
	        // Return the text
	        return addressText;
	    } else {
	        return "";
	    }
   }
	
	public ArrayList<Coordinates> getLatLongFromAddress(double myLat, double myLon, String youraddress) {
		
			ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
			try{
				
				String address = URLEncoder.encode(youraddress,"utf-8").replace("+", "%20");
				
				String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="+myLat+","+myLon+"&radius="+context.getResources().getString(R.string.near_by_radius)+"&query="+address+"&sensor=false&key="+context.getResources().getString(R.string.places_api_key);
//				String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+myLat+","+myLon+"&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
//				String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=30.711104,76.686153&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
				
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
		        
		        Log.e("result",stringBuilder.toString());
		        
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
		        	coordinates.add(cdn);
		        }
		        
		        return coordinates;
			}catch(Exception e){
				e.printStackTrace();
				return coordinates;
			}
	    }
	

	public ArrayList<Coordinates> getAddressFromLoc(double myLat, double myLon) {
		
		ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();
		try{
			
//			String address = URLEncoder.encode(youraddress,"utf-8").replace("+", "%20");
			String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+myLat+","+myLon+"&key="+context.getResources().getString(R.string.places_api_key);
//			String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="+myLat+","+myLon+"&radius="+context.getResources().getString(R.string.near_by_radius)+"&query="+address+"&sensor=false&key="+context.getResources().getString(R.string.places_api_key);
//			String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+myLat+","+myLon+"&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
//			String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=30.711104,76.686153&radius="+getResources().getString(R.string.near_by_radius)+"&name="+address+"&sensor=false&key="+getResources().getString(R.string.places_api_key);
			
//	        Log.e("url",url);
	        
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
	        
	        Log.e("result",stringBuilder.toString());
	        
	        JSONObject jsonObject = new JSONObject();
	        jsonObject = new JSONObject(stringBuilder.toString());
	      
	        JSONArray searchResults =  ((JSONArray)jsonObject.get("results"));
//	        Log.e("total results found","number :: "+searchResults.length());
	        
	        for(int i =0; i<searchResults.length(); i++){
	        	Coordinates cdn = new Coordinates();        	
	        	cdn.latitude = searchResults.getJSONObject(i)
	                    .getJSONObject("geometry").getJSONObject("location")
	                    .getDouble("lat");
	        	
	        	cdn.longitude = searchResults.getJSONObject(i)
	                    .getJSONObject("geometry").getJSONObject("location")
	                    .getDouble("lng");
	        	
	        	cdn.address = searchResults.getJSONObject(i).getString("formatted_address");
//	        	cdn.name = searchResults.getJSONObject(i).getString("name");
	        	coordinates.add(cdn);
	        }
	        
	        return coordinates;
		}catch(Exception e){
			e.printStackTrace();
			return coordinates;
		}
    }
}
