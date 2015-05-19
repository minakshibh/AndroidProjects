package com.autometer.system;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ChooseDestination extends FragmentActivity implements LocationListener{
	
	private GoogleMap googleMap;
	private ImageView btnReport;
	private AutoCompleteTextView txtDestination;
	private ImageView search;
	private ProgressDialog progress;
	private ArrayList<Coordinates> coordinates;
	private SharedPreferences prefs;
	private ArrayList<String> recentPlaces;
	public static double myLat, myLon;
	private LocationManager locationManager;
	private Criteria criteria;
	private  String provider;
	private int flag = 0;
	
	private String LOG_TAG = "AutoMeter";
    
	private String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private String TYPE_AUTOCOMPLETE = "/autocomplete";
	private String OUT_JSON = "/json";

	private String API_KEY;
	private String trigger;
	private RelativeLayout footer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_destination);
		
		API_KEY = getResources().getString(R.string.places_api_key);
		
		trigger = getIntent().getStringExtra("trigger");
		
		prefs = this.getSharedPreferences("PlacePreference", MODE_PRIVATE); 						
		coordinates = new ArrayList<Coordinates>();
		
		SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapView);
		googleMap = fm.getMap();
		
		btnReport = (ImageView)findViewById(R.id.report);
		btnReport.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(ChooseDestination.this, ReportVoilence.class);
				startActivity(i);
			}
		});
		
		footer=(RelativeLayout)findViewById(R.id.footer);
		
		try{
			googleMap.setMyLocationEnabled(true);
			
			/* UiSettings uiSettings = googleMap.getUiSettings();
			 uiSettings.setMyLocationButtonEnabled(true);
			    */
			googleMap.setTrafficEnabled(true);
			
			googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
//					Log.e("Marker clicked :: ","details : "+marker.getId()+" ,, "+marker.getTitle()+",, latitude: "+marker.getPosition().latitude+" ,, longitude : "+marker.getPosition().longitude);
					if(trigger.equals("none")){
						Editor editor = prefs.edit();
						editor.putString("trigger", "none");
						editor.commit();
						
						Intent intent = new Intent(ChooseDestination.this, FareCalculator.class);
						intent.putExtra("latitude", marker.getPosition().latitude);
						intent.putExtra("longitude", marker.getPosition().longitude);
						/*
						intent.putExtra("curLatitude", myLat);
						intent.putExtra("curLongitude", myLon);
						*/
						intent.putExtra("name", marker.getTitle());
						intent.putExtra("address", marker.getSnippet());					
						startActivity(intent);		
					}else{
						/*myLat = marker.getPosition().latitude;
						myLon = marker.getPosition().longitude;
						*/
						Editor editor = prefs.edit();
						editor.putString("markerLat",String.valueOf(marker.getPosition().latitude));
						editor.putString("markerLon", String.valueOf(marker.getPosition().longitude));
						editor.putString("markerTitle", marker.getTitle());
						editor.putString("trigger", "customLoc");
						editor.commit();
						
						finish();
					}
				}
			});
			
			if(trigger.equals("none")){
//				Log.e("trigger","none");
				googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
					@Override
					public void onMyLocationChange(Location arg0) {
//						Log.e("trigger","onlocationChanged");
						onLocationChanged(arg0);
					}
				});
			}			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		if(trigger.equals("none")){
		
		try{		
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
	
			try{
				boolean enabledGPS = locationManager
			            .isProviderEnabled(LocationManager.GPS_PROVIDER);
			    boolean enabledWiFi = locationManager
			            .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
			    // Check if enabled and if not send user to the GSP settings
			    if (!enabledGPS && !enabledWiFi) {
			        Toast.makeText(ChooseDestination.this, "GPS signal not found", Toast.LENGTH_LONG).show();
			        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			        startActivity(intent);
			    }
			}catch(Exception e){
				e.printStackTrace();
			}
			
			/*
	        // Creating a criteria object to retrieve provider
	        criteria = new Criteria();
	
	        // Getting the name of the best provider
	        provider = locationManager.getBestProvider(criteria, true);*/
	        
			recentPlaces = new ArrayList<String>();		
			
	//		txtHeader = (TextView)findViewById(R.id.headerText);
			txtDestination = (AutoCompleteTextView)findViewById(R.id.destination);
			search = (ImageView)findViewById(R.id.search);
			
			Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
			/*txtHeader.setTypeface(typeFace);*/
			
			txtDestination.setTypeface(typeFace);
			txtDestination.setThreshold(1);
			txtDestination.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.adapter_layout));
	//		.setAdapter(new ArrayAdapter<String>(this, R.layout.adapter_layout, recentPlaces));
			
			/*Location myLocation = googleMap.getMyLocation();
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), 16));
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(13), 2000, null);*/
			
	       
	        txtDestination.setOnEditorActionListener(new EditText.OnEditorActionListener() {        	
				@Override
				public boolean onEditorAction(TextView v, int actionId,
						KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//						hideKeyboard();
						String destination = txtDestination.getText().toString().trim();
						showOverlayInstructions(destination);
			        	 return true;
			        }
					return false;
				} });
	        
	        loadRecentPlaceArray();
	        Log.e("recent places","count :: "+recentPlaces.size());
	        search.setOnClickListener(searchDestinations);
	        
	        /*Handler handler = new Handler();
		    handler.postDelayed(new Runnable(){
		           public void run() {
		        	   
		        	   if(flag ==0){
			        	// Getting Last known Location
			            Location location = locationManager.getLastKnownLocation(provider);
		
			                if(location!=null){
			                    onLocationChanged(location);
			                }
			           }
		           	}
		    }, 15000);*/     
		    
		    locationManager.requestLocationUpdates(provider, 2000, 100, this);                        
			
		}catch(Exception ex){
			ex.printStackTrace();
			
			/*StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			
			File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/log.txt");
			   if (!logFile.exists())
			   {
			      try
			      {
			         logFile.createNewFile();
			      } 
			      catch (IOException e)
			      {
			         // TODO Auto-generated catch block
			         e.printStackTrace();
			      }
			   }
			   try
			   {
			      //BufferedWriter for performance, true to set append to file flag
			      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
			      buf.append(sw.toString());
			      buf.newLine();
			      buf.close();
			   }catch (IOException e){
			      // TODO Auto-generated catch block
			      e.printStackTrace();
			   }*/
		}
		
		}else{
			footer.setVisibility(View.GONE);
			
			String destination = getIntent().getStringExtra("destination");
			new searchtask().execute(destination);
//			showOverlayInstructions(destination);
		}
	}

	private View.OnClickListener searchDestinations = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
//			hideKeyboard();
			String destination = txtDestination.getText().toString().trim();
			showOverlayInstructions(destination);
		}
	};
	
	/*public String ConvertPointToLocation(LatLng point) {   
        String address = "";
        Geocoder geoCoder = new Geocoder(
                getBaseContext(), Locale.getDefault());
        try {
            List<Address> addresses = geoCoder.getFromLocation(
                point.latitude, 
                point.longitude, 1);

            if (addresses.size() > 0) {
                for (int index = 0; index < addresses.get(0).getMaxAddressLineIndex(); index++)
                    address += addresses.get(0).getAddressLine(index) + " ";
                
//                address += addresses.get(0).getLocality()+", "+addresses.get(0).getSubAdminArea()+", "+addresses.get(0).getAdminArea();
            }
        }
        catch (IOException e) {                
            e.printStackTrace();
        }   

        return address;
    } */

	private class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	    private ArrayList<String> resultList;
	    
	    public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
	        super(context, textViewResourceId);
	    }
	    
	    @Override
	    public int getCount() {
	        return resultList.size();
	    }

	    @Override
	    public String getItem(int index) {
	        return resultList.get(index);
	    }

	    @Override
	    public Filter getFilter() {
	        Filter filter = new Filter() {
	            @Override
	            protected FilterResults performFiltering(CharSequence constraint) {
	                FilterResults filterResults = new FilterResults();
	                if (constraint != null) {
	                    // Retrieve the autocomplete results.
	                    resultList = autocomplete(constraint.toString());
	                    
	                    for(int i =0; i<recentPlaces.size(); i++){
	                    	if(recentPlaces.get(i).startsWith(constraint.toString()))
	                    		resultList.add(recentPlaces.get(i));
	                    }
	                    
	                    // Assign the data to the FilterResults
	                    filterResults.values = resultList;
	                    filterResults.count = resultList.size();
	                }
	                return filterResults;
	            }

	            @Override
	            protected void publishResults(CharSequence constraint, FilterResults results) {
	                if (results != null && results.count > 0) {
	                    notifyDataSetChanged();
	                }
	                else {
	                    notifyDataSetInvalidated();
	                }
	            }};
	        return filter;
	    }
	}
	private ArrayList<String> autocomplete(String input) {
	    ArrayList<String> resultList = null;
	    
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?location="+myLat+","+myLon+"&radius="+getResources().getString(R.string.near_by_radius)+"&sensor=false&key=" + API_KEY);
	        sb.append("&components=country:in");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));
	        
	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());
	        
	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }
	    
	    Log.e("prediction result", jsonResults.toString());
	    
	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
	        
	        // Extract the Place descriptions from the results
//	        resultList = new ArrayList<String>(predsJsonArray.length());
	        resultList = new ArrayList<String>();
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
	        }
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    
	    return resultList;
	}
	
	public void showOverlayInstructions(final String destination){

		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtDestination.getWindowToken(), 0);		
		
		
		final Dialog alert = new Dialog(ChooseDestination.this);
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(R.layout.overlay_layout);
		alert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(alert.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    
		Button gotIt = (Button)alert.findViewById(R.id.btnGotIt);
		
		gotIt.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				alert.dismiss();
			}
		});
		
		alert.setOnDismissListener(new DialogInterface.OnDismissListener() {			
			@Override
			public void onDismiss(DialogInterface dialog) {
				searchLocation(destination);
			}
		});
		
		alert.setCancelable(false);
		
		/*alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				searchLocation(destination);
			}
		});
		*/
		alert.show();
		alert.getWindow().setAttributes(lp);
	}
	
	public void searchLocation(String destination){
		coordinates.clear();
		
		if(destination.equals("")){
			AlertDialog.Builder alert = new AlertDialog.Builder(ChooseDestination.this);
			alert.setTitle("Auto Meter");
			alert.setMessage("Please enter a destination location");
			alert.setPositiveButton("Ok", null);
			alert.show();
		}else{
			if(!recentPlaces.contains(destination))
				savePlace(destination);
			new searchtask().execute(destination);
		}
	}
	
	class searchtask extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... str) {
	    	LocationFinder locFinder = new LocationFinder(ChooseDestination.this);
	    	coordinates = locFinder.getLatLongFromAddress(myLat, myLon, str[0]);
	    	if(trigger.equals("none"))
	    		loadRecentPlaceArray();
	    	return "success";
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        progress = ProgressDialog.show(ChooseDestination.this, "Loading", "Please wait...");
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
//	        txtDestination.setAdapter(new ArrayAdapter<String>(ChooseDestination.this, android.R.layout.simple_dropdown_item_1line, recentPlaces));
	        progress.dismiss();
        	
	        try{
		        googleMap.clear();
		        
		    	for(int i =0; i<coordinates.size(); i++){
		    		MarkerOptions marker = new MarkerOptions().position(new LatLng(coordinates.get(i).latitude, coordinates.get(i).longitude)).title(coordinates.get(i).name).snippet(coordinates.get(i).address);
		    		googleMap.addMarker(marker);
		    		
//		    		Log.e("coordinates","lat :: "+coordinates.get(i).latitude+",,lon ::"+coordinates.get(i).longitude+",, address :: "+coordinates.get(i).name);
	    	}
		    	
		    	progress.dismiss();
		    	if(coordinates.size()>0){
					Coordinates firstCoordinate = coordinates.get(0);
					
//					Log.e("first coordinate","first loc");
					
					googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(firstCoordinate.latitude, firstCoordinate.longitude), 16));
					googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
					
				/*	Intent intent = new Intent(ChooseDestination.this, FareCalculator.class);
					intent.putExtra("latitude", coordinates.get(0).latitude);
					intent.putExtra("longitude", coordinates.get(0).longitude);
					
					intent.putExtra("curLatitude", myLat);
					intent.putExtra("curLongitude", myLon);
					
					intent.putExtra("name", coordinates.get(0).name);
					intent.putExtra("address", coordinates.get(0).address);					
					startActivity(intent);					
					*/
				}else
		    	{
		    		AlertDialog.Builder alert = new AlertDialog.Builder(ChooseDestination.this);
		    		alert.setTitle("Auto Meter");
		    		alert.setMessage("No Results found. Please try again.");
		    		alert.setPositiveButton("Ok", null);
		    		alert.show();
		    	}
	        }catch(Exception e){
	        	
	        	e.printStackTrace();
	        }
	    }
	}
	/*
	@Override
	public void onResume(){
		
	}*/
	
	@Override
	public void onLocationChanged(Location location) {
		Log.e("in","onlocationChanged");
		try{
			myLat = location.getLatitude();
			
	        // Getting longitude of the current location
	        myLon = location.getLongitude();
	        Log.e("values","lat : "+myLat+" , lon : "+myLon);
	        
	        if(flag==0){
	        	flag = 1;
	        	
		        // Creating a LatLng object for the current location
		        LatLng latLng = new LatLng(myLat, myLon);
		
		        // Showing the current location in Google Map
		        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
		
		        // Zoom in the Google Map
		        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	        }
//	        locationManager.removeUpdates(ChooseDestination.this);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
	
	
	
	public void savePlace(String place)
	{
	    Editor editor = prefs.edit();
	    int curSize = prefs.getInt("size", 0);
	    
	    editor.putString("Place_" + curSize, place);
	    editor.putInt("size", curSize + 1);
	    editor.commit();
	}
	
	public void loadRecentPlaceArray()
	{
		recentPlaces.clear();
	    int size = prefs.getInt("size", 0);

	    for(int i=0; i<size; i++) 
	    {
	        recentPlaces.add(prefs.getString("Place_" + i, ""));
	    }
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
//		locationManager.removeUpdates(ChooseDestination.this);
	}
}