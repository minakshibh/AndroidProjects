package com.rapidride;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import com.rapidride.util.Utility;


public class TripScreen extends Activity implements LocationListener {
	AutoCompleteTextView enterDestination;
	Button goToPickUp,goHome,requestRide,cancel;
	private ArrayList<String> recentPlaces;
	private String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private String TYPE_AUTOCOMPLETE = "/autocomplete";
	private String OUT_JSON = "/json";
	private String API_KEY;
	private String LOG_TAG;
	double latitude,longitude;
	LocationManager locationManager;
	Geocoder geocoder;
	TextView textViewNow;
	String str_currentdate=null;
	String HH="";
	long currentdateintoInt,selectdateintoInt;
	StringBuilder datepicker_date;
	DatePicker datepicker;
	TimePicker timepicker;
	String str_timepicker="";
	String datetimepick=null;
	String selectedTime="";
	SharedPreferences prefs;
	private float destLat, destLon, curLat, curLong;
	String desAddress="",cur_Address="",str_countryid;
	double currentLongitude,currentLatitude;
	ImageView iv_now;
	boolean checking_EditingDate=false;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.tripview);
	
	setFinishOnTouchOutside(false);
	prefs =getSharedPreferences("RapidRide", MODE_PRIVATE);
	
	textViewNow=(TextView)findViewById(R.id.textViewNow);
	goToPickUp=(Button)findViewById(R.id.button_BackToPickUpLocation);
	goHome=(Button)findViewById(R.id.Button_backToHome);
	requestRide=(Button)findViewById(R.id.buttonRequestRide_Trip);
	iv_now=(ImageView)findViewById(R.id.imageView_now);
	cancel=(Button)findViewById(R.id.buttonRequestRide_cancel);
	
	 str_countryid=GetCountryID();
     
     if(str_countryid.trim().equals(""))
     {
  	   str_countryid="US";
     		}
     else
     {
  	   str_countryid=GetCountryID();
     		}
    Log.e("tag", "getCountry="+GetCountryID());
	
	Calendar c = Calendar.getInstance();
//	int mYear = c.get(Calendar.YEAR);
//  int mMonth = c.get(Calendar.MONTH);
//  int mDay = c.get(Calendar.DAY_OF_MONTH);
//    
    int hh = c.get(Calendar.HOUR_OF_DAY);
    Log.d("tag","HH::"+hh);
    
    if(hh<=9)
    {
    	HH="0"+String.valueOf(hh);
    	Log.d("tag","After Condition HH::"+hh);
    }
    else
    {
    	HH=String.valueOf(hh);
    }

    Date date = new Date();
    SimpleDateFormat simpDate;
    simpDate = new SimpleDateFormat("yyyyMMdd"+HH+"mmss");
    str_currentdate=simpDate.format(date);
    Log.i("tag", "format date:"+str_currentdate);
    currentdateintoInt=Long.parseLong(str_currentdate);
    if(checking_EditingDate==false)
    {
    Editor e=prefs.edit();
    e.putString("SelectedTimeIS", "now");
    e.commit();
    }
    Log.d("tag", "dateintoInt:"+currentdateintoInt);
	
	/**********************/
    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
    .detectAll()
    .penaltyLog()
    .build();
    StrictMode.setThreadPolicy(policy);
    
	locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    Criteria criteria = new Criteria();
    String provider = locationManager.getBestProvider(criteria, true);
    Log.i("tag", provider);
         // Getting Current Location
    Location location = locationManager.getLastKnownLocation(provider);
        //on location change method set condition 
    onLocationChanged(location);
    recentPlaces = new ArrayList<String>();
	 //api key get from  string xml//
    API_KEY = getResources().getString(R.string.places_api_key);
	enterDestination=(AutoCompleteTextView)findViewById(R.id.editText_enter_Destination);
	enterDestination.setThreshold(4);
	enterDestination.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.adapter_layout));
	
	enterDestination.setOnItemClickListener(new OnItemClickListener() { //auto complete select item action 
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				homeClick="";
				Utility.hideKeyboard(TripScreen.this);
				if(enterDestination.getText().toString().length()!=0)
				{
//					getLatLongFromGivenAddress(enterDestination.getText().toString());
					String addressStr = enterDestination.getText().toString();
					desAddress=addressStr;
					Geocoder geoCoder = new Geocoder(TripScreen.this, Locale.getDefault());

					try {
					    List<Address> addresses =
					    geoCoder.getFromLocationName(addressStr, 1); 
					    if (addresses.size() >  0) {
					    destLat = (float) addresses.get(0).getLatitude();
					    destLon =(float) addresses.get(0).getLongitude(); 
					    Log.d("tag", "Latitude:"+destLat);
					    Log.d("tag", "Longitude:"+destLon);
					    
					    }

					} catch (IOException e) { // TODO Auto-generated catch block
					e.printStackTrace(); }
				}

				}
			});
	cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		Intent i=new Intent (TripScreen.this,MapView_Activity.class);
		finish();
		startActivity(i);
			
		}
	});
	enterDestination.setOnEditorActionListener(new EditText.OnEditorActionListener() {//auto complete search location action   	
			public boolean onEditorAction(TextView v, int actionId,KeyEvent event) {
				
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					homeClick="";
					Utility.hideKeyboard(TripScreen.this);
					String addressStr = enterDestination.getText().toString();
					desAddress=addressStr;
					Geocoder geoCoder = new Geocoder(TripScreen.this, Locale.getDefault());

					try {
					    List<Address> addresses =
					    geoCoder.getFromLocationName(addressStr, 1); 
					    if (addresses.size() >  0) {
					    destLat = (float) addresses.get(0).getLatitude();
					    destLon =(float) addresses.get(0).getLongitude(); 
					    Log.d("tag", "Latitude:"+destLat);
					    Log.d("tag", "Longitude:"+destLon);
					    }

					} catch (IOException e) { // TODO Auto-generated catch block
					e.printStackTrace(); }
		        }
					return false;
			}});
	
	goHome.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Editor ed=prefs.edit();
			ed.putInt("homeclick", 0);
			ed.commit();
			if(prefs.getString("completeaddress", null).equals(",,,"))
			{
				Utility.alertMessage(TripScreen.this,"Please enter address first in Edit Profile.");}
			else{
				enterDestination.setText(prefs.getString("completeaddress", null));
			  
				Utility.hideKeyboard(TripScreen.this);
				String addressStr = enterDestination.getText().toString();
				Geocoder geoCoder = new Geocoder(TripScreen.this, Locale.getDefault());

				try {
				    List<Address> addresses =
				    geoCoder.getFromLocationName(addressStr, 1); 
				    if (addresses.size() >  0) {
				    destLat = (float) addresses.get(0).getLatitude();
				    destLon =(float) addresses.get(0).getLongitude(); 
				    Log.d("tag", "Latitude:"+destLat);
				    Log.d("tag", "Longitude:"+destLat);
				    desAddress=enterDestination.getText().toString();
				    }

				} catch (IOException e) { // TODO Auto-generated catch block
				e.printStackTrace(); }
			}	
		}
	});
	
	goToPickUp.setOnClickListener(new OnClickListener() {
	public void onClick(View v) {
			// TODO Auto-generated method stub
			destLat=Float.parseFloat(prefs.getString("cur_lati",""));
			System.err.println(destLat);
			destLon=Float.parseFloat(prefs.getString("cur_logi", ""));
			System.err.println(destLon);
			desAddress=prefs.getString("cur_address", null);
			enterDestination.setText(desAddress);
		}
	});
	
	
	iv_now.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			  final Dialog dialog = new Dialog(TripScreen.this);
              // Include dialog.xml file

              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
              dialog.setContentView(R.layout.new_datetime_picker);
			
		    datepicker=(DatePicker)dialog.findViewById(R.id.datePicker1);
			timepicker=(TimePicker)dialog.findViewById(R.id.timePicker1);
			timepicker.setIs24HourView(true);
			timepicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
			Button done=(Button)dialog.findViewById(R.id.buttonDone);
	        done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 datepicker_date=new StringBuilder();
				 datepicker_date.setLength(0);
				 
				 datepicker_date.append(datepicker.getYear()).append(datepicker.getMonth()+1).append(datepicker.getDayOfMonth());
				 
				  String year=String.valueOf(datepicker.getYear());
			         String month=String.valueOf(datepicker.getMonth()+1);
			         if(month.length()==1)
			         {
			          month="0"+month;
			         }
			         
			         String day=String.valueOf(datepicker.getDayOfMonth());
			         if(day.length()==1)
			         {
			          day="0"+day;
			         }
			         
			   	       
			         
			         String curhour="",curmin="";
			         curhour=String.valueOf(timepicker.getCurrentHour());
			         if(curhour.length()== 1)
                       {
                          curhour="0"+curhour; 
                          }       
	                 curmin=String.valueOf(timepicker.getCurrentMinute());
	                 if(curmin.length()== 1)
	                     {
	                		curmin="0"+curmin; 
	                          }
	                 str_timepicker=new String();
	                 str_timepicker="";
	                 
	                 str_timepicker=curhour+""+curmin+""+"43";
	                 System.err.println("time"+str_timepicker);
	                 
	                 Log.d("tag","str_timepicker"+str_timepicker);
	                 datetimepick="edit";
	                 
	                 selectedTime=year+""+month+""+day+""+curhour+""+curmin+"43";
	                 selectdateintoInt=Long.parseLong(selectedTime);
	                 Log.i("tag", "selectdateintoInt:"+selectdateintoInt);
      			 
      			 if(selectdateintoInt<currentdateintoInt)
      			 {
      				Log.d("tag", "Please select greater date from current date");
      				Utility.alertMessage(TripScreen.this, "Please select current or greater date from current date");
      			 }
      			 else
      			 {
      				
      				checking_EditingDate=true;
      				if(checking_EditingDate==true)
      				{
      				Editor e1=prefs.edit();
      				e1.putString("SelectedTimeIS", String.valueOf(selectdateintoInt));
      				e1.commit();
      				}
      				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
      				String ss=""; 
      				ss=selectedTime;
    		    	Date date1 = null;
					try {
						date1 = (Date)formatter.parse(ss);
					} catch (ParseException e) {
						e.printStackTrace();
					}
    		    	
    		    	 formatter = new SimpleDateFormat("yyyy-MM-dd");
    		    	 String  s = formatter.format(date1);
    		    	  
    		    	System.err.println(s);
    		    	formatter = new SimpleDateFormat("HH:mm");
    		    	String  t = formatter.format(date1);
    		    	 
    		    	System.err.println(t);
    		    	textViewNow.setText(" "+s+"   "+t);
    		    	System.err.println("trip_request_pickup_date "+ datepicker_date.append(str_timepicker));
      				
      				// tv_textView_nowtimedate.setText(datepicker_date.append(str_timepicker));
      				 dialog.dismiss();
      			 	}
			}
		});
		Button cancel=(Button)dialog.findViewById(R.id.buttonCancel);
		cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			dialog.dismiss();	
			}
		});
					
		    dialog.show();
		  }
	});
	
	requestRide.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			if(enterDestination.getText().toString().equals(""))
			{
				Utility.alertMessage(TripScreen.this, "Fill destination address");
			}
			else
			{

//			curLat=prefs.getFloat("des_lati",0);
//			System.err.println("curLat=="+curLat);
//			curLong=prefs.getFloat("des_logi", 0);
//			System.err.println("curLong=="+curLong);
//			cur_Address=prefs.getString("des_address", null);
//			enterDestination.setText(desAddress);
//		
//			
//			/********************/
//			Intent i=new Intent(TripScreen.this,LocationandPriceDetail.class);
//			i.putExtra("VIP", "no");
//			i.putExtra("VIP2","1");
//			i.putExtra("tripscreen", "value");
//			i.putExtra("driverid",getIntent().getStringExtra("driverid"));
//			Editor e=prefs.edit();
//			e.putFloat("cur_lati", prefs.getFloat("des_lati",0));
//			e.putFloat("cur_logi", prefs.getFloat("des_logi", 0));
//			e.putString("cur_address", prefs.getString("des_address", null));
//			
//			e.putFloat("des_lati", destLat);
//			e.putFloat("des_logi", destLon);
//			e.putString("des_address", desAddress);
//			e.commit();
//			
//			startActivity(i);
//			finish();
//			/*********************/
//			curLat=Float.parseFloat(prefs.getString("des_lati",""));
//			System.err.println(curLat);
//			curLong=Float.parseFloat(prefs.getString("des_logi", ""));
//			System.err.println(curLong);
//			cur_Address=prefs.getString("des_address", null);
//			enterDestination.setText(desAddress);
			
			/********************/
				
			curLat=Float.parseFloat(prefs.getString("des_lati",""));
				System.err.println(curLat);
				curLong=Float.parseFloat(prefs.getString("des_logi", ""));
				System.err.println(curLong);
				cur_Address=prefs.getString("des_address", null);
				
			Intent i=new Intent(TripScreen.this,LocationandPriceDetail.class);
			i.putExtra("VIP", "no");
			i.putExtra("VIP2","1");
			i.putExtra("tripscreen", "gghhg");
			
			Editor e=prefs.edit();
			e.putFloat("cur_lati", curLat);
			e.putFloat("cur_logi", curLong);
			e.putString("cur_address", cur_Address);
			
			e.putFloat("des_lati", destLat);
			e.putFloat("des_logi", destLon);
			e.putString("des_address", desAddress);
			e.commit();
			
			startActivity(i);
			finish();
			
			
			/**********************/
		}}
	});
}
/*** auto complete find  places function **/////
  
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
/*** auto complete search location function **/////
private ArrayList<String> autocomplete(String input) {
    ArrayList<String> resultList = null;
    
    HttpURLConnection conn = null;
    StringBuilder jsonResults = new StringBuilder();
    try {
        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?location="+latitude+","+longitude+"&radius="+getResources().getString(R.string.near_by_radius)+"&sensor=false&key=" + API_KEY); //search auto text box location
       sb.append("&components=country:"+str_countryid);
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
       
        resultList = new ArrayList<String>();
        for (int i = 0; i < predsJsonArray.length(); i++) {
            resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
        }
    } catch (JSONException e) {
        Log.e(LOG_TAG, "Cannot process JSON results", e);
    }
    
    return resultList;
}
/**end of auto complete search result **///

/**find address name from latitude or longitude**///
public  String getAddress(Context ctx, double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            geocoder = new Geocoder(ctx, Locale.getDefault());
            List<Address>
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
     
		    String locality=address.getLocality();
		    String city=address.getCountryName();
		    String region_code=address.getCountryCode();
		    String zipcode=address.getPostalCode();
		  
		    result.append(locality+" ");
		    result.append(city+" "+ region_code+" ");
		    result.append(zipcode);
     
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
 
        return result.toString();
    }
/***showOverlayInstructions	**///
public void showOverlayInstructions(final String destination){

	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	imm.hideSoftInputFromWindow(enterDestination.getWindowToken(), 0);		
}

@Override
public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	latitude = location.getLatitude();
    longitude = location.getLongitude();
    currentLatitude=latitude;
    currentLongitude=longitude;
 }

@Override
public void onStatusChanged(String provider, int status, Bundle extras) {

}

public void onProviderEnabled(String provider) {

}

public void onProviderDisabled(String provider) {

	
}



//public static void getLatLongFromGivenAddress(String youraddress) {
//    String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
//                  youraddress + "&sensor=false";
//    HttpGet httpGet = new HttpGet(uri);
//    HttpClient client = new DefaultHttpClient();
//    org.apache.http.HttpResponse response;
//    StringBuilder stringBuilder = new StringBuilder();
//
//    try {
//        response = client.execute(httpGet);
//        HttpEntity entity = response.getEntity();
//        InputStream stream = entity.getContent();
//        int b;
//        while ((b = stream.read()) != -1) {
//            stringBuilder.append((char) b);
//        }
//    } catch (ClientProtocolException e) {
//        e.printStackTrace();
//    } catch (IOException e) {
//        e.printStackTrace();
//    }
//
//    JSONObject jsonObject = new JSONObject();
//    try {
//        jsonObject = new JSONObject(stringBuilder.toString());
//
//       double lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
//            .getJSONObject("geometry").getJSONObject("location")
//            .getDouble("lng");
//
//       double lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
//            .getJSONObject("geometry").getJSONObject("location")
//            .getDouble("lat");
//
//        Log.d("tag","latitude:" +lat);
//        Log.d("tag","longitude:"+lng);
//    } catch (JSONException e) {
//        e.printStackTrace();
//    }
//
//}
@Override
public void onAttachedToWindow() {
    super.onAttachedToWindow();
  
    View view = getWindow().getDecorView();
	WindowManager.LayoutParams lp = (WindowManager.LayoutParams) view.getLayoutParams();
	lp.gravity = Gravity.TOP;
	getWindowManager().updateViewLayout(view, lp);	

}
public String GetCountryID(){
    String CountryID="";
     TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    //getNetworkCountryIso
    CountryID= manager.getNetworkCountryIso().toUpperCase();
   // CountryID= Locale.getDefault().getCountry(); 
    return CountryID;
}
}
