package com.twentyfourseven.zira;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer.InitiateMatchResult;
import com.twentyfourseven.zira.R;
import com.zira.login.LoginActivity1;
import com.zira.modal.AdresssOfLocation;
import com.zira.modal.Coordinates;
import com.zira.util.SingleTon;
import com.zira.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;

public class LocationSettingActivity extends Activity {

	//private ListView mListView;
	private AutoCompleteTextView editText_enter_location;	
	private ArrayList<String> recentPlaces=new ArrayList<String>();
	private LocationFinder finder;
	private ArrayList<Coordinates> coordinates = new ArrayList<Coordinates>();	
	private String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private String TYPE_AUTOCOMPLETE = "/autocomplete";
	private String OUT_JSON = "/json";
	//private String API_KEY;
	//private String LOG_TAG;
	private String desAddress="",cur_Address="";
	private double destLat, destLon, curLat, curLong;
	private AdresssOfLocation adresssOfLocation;
	private ImageView cancelButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_location_set);

		InitialiseVariable();
		initialiseListener();

	}

	private void InitialiseVariable() {
		//Coordinates cooridnates=new co
		adresssOfLocation=SingleTon.getInstance().getmLocation();
		editText_enter_location=(AutoCompleteTextView)findViewById(R.id.editText_enter_location);
		editText_enter_location.setTextSize(12);
		editText_enter_location.setThreshold(4);
		editText_enter_location.setAdapter(new PlacesAutoAdapter(this, R.layout.adapter_layout));
		cancelButton=(ImageView)findViewById(R.id.cancelButton);		
	}

	private void initialiseListener() {
		
		cancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		editText_enter_location.setOnItemClickListener(new OnItemClickListener() { 
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {


				if(editText_enter_location.getText().toString().length()!=0)
				{

					String addressStr = editText_enter_location.getText().toString();
					//desAddress=addressStr;
					new SearchTask_Destination().execute(addressStr);
					//edit
					/*Geocoder geoCoder = new Geocoder(LocationSettingActivity.this, Locale.getDefault());
					//Geocoder geoCoder = new Geocoder(LocationSettingActivity.this);
					try {
						List<Address> addresses =geoCoder.getFromLocationName(addressStr, 1); 
						if (addresses.size() >  0) {
							destLat = addresses.get(0).getLatitude();//30.165465;//
							destLon = addresses.get(0).getLongitude(); //76.4654654;//
							Log.d("tag", "Latitude:"+destLat);
							Log.d("tag", "Longitude:"+destLon);

							if(SingleTon.getInstance().getFrom().equals("imgAdd")){
								
								VehicleSearchActivity.gettingdestinationLat=destLat;
								VehicleSearchActivity.gettingDestinationLong=destLon;
								VehicleSearchActivity.destinationAddres=desAddress;
								
							}
							else {
								
								VehicleSearchActivity.gettingSourceLat=destLat;
								VehicleSearchActivity.gettingSourceLong=destLon;		
								VehicleSearchActivity.sourceAddress=desAddress;
								SingleTon.getInstance().setFrom("LocationSettingActivity");
							}
							Util.hideKeyboard(LocationSettingActivity.this);
							finish();
						}

					} catch (IOException e) { 
						e.printStackTrace(); }
						
						
				*/}

			}
		});
	}


	/*** auto complete find  places function **/////

	private class PlacesAutoAdapter extends ArrayAdapter<String> implements Filterable {
		private ArrayList<String> resultList;
		public PlacesAutoAdapter(Context context, int textViewResourceId) {
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
	/*** auto complete search location function **/////
	private ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?location="+SingleTon.getInstance().getLatitute()+","+SingleTon.getInstance().getLongitude()+"&radius="+getResources().getString(R.string.near_by_radius)+"&sensor=false&key="+getResources().getString(R.string.places_api_key));//search auto text box location			
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
			Log.e("LOG_TAG", "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e("LOG_TAG", "Error connecting to Places API", e);
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

			resultList = new ArrayList<String>();
			for (int i = 0; i < predsJsonArray.length(); i++) {
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e("LOG_TAG", "Cannot process JSON results", e);
		}

		return resultList;
	}
	/**end of auto complete search result **///

    class SearchTask_Destination extends AsyncTask<String, String, String>{ 
	    
	    protected String doInBackground(String... str) {
	    com.twentyfourseven.zira.LocationFinder locFinder = new   com.twentyfourseven.zira.LocationFinder(LocationSettingActivity.this);
	  	coordinates = locFinder.getLatLongFromAddress(VehicleSearchActivity.myLat, VehicleSearchActivity.myLon, str[0]);
//	    	if(trigger.equals("none")) //loadRecentPlaceArray();
	    	return "success";
	    	   }
		  @Override
		    protected void onPreExecute() {
	
		    }
		    
		    @Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);

		        if (coordinates.size() >  0) {
		        	for(int i =0; i<1; i++)
		        	{
					destLat = coordinates.get(i).getLatitude();//30.165465;//
					destLon = coordinates.get(i).getLongitude(); //76.4654654;//
					desAddress= coordinates.get(i).getAddress();
					Log.d("tag", "Latitude:"+destLat);
					Log.d("tag", "Longitude:"+destLon);
		        		}
					if(SingleTon.getInstance().getFrom().equals("imgAdd")){
						
						VehicleSearchActivity.gettingdestinationLat=destLat;
						VehicleSearchActivity.gettingDestinationLong=destLon;
						VehicleSearchActivity.destinationAddres=desAddress;
						
					}
					else {
						
						VehicleSearchActivity.gettingSourceLat=destLat;
						VehicleSearchActivity.gettingSourceLong=destLon;		
						VehicleSearchActivity.sourceAddress=desAddress;
						SingleTon.getInstance().setFrom("LocationSettingActivity");
					}
					Util.hideKeyboard(LocationSettingActivity.this);
					finish();
		    
		    }
	
		    }}
}
