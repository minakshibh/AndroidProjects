package com.rapidride;
import java.util.HashSet;
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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class Splash_Activity extends Activity {
	
	SharedPreferences prefs;
	private ProgressBar pBar;
	private String splash_jsonMessage;
	private String splash_jsonResult;
	int login_Exception=0;
	public int int_value=0;
	LocationManager manager;
	LocationManager locationManager;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		pBar=(ProgressBar)findViewById(R.id.progressBar_splash);
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 

//		if(Utility.isConnectingToInternet(Splash_Activity.this))
//		{
//			new httpLogin().execute();
//			}
//		else{
//			Utility.alertMessage(Splash_Activity.this,"Error in internet connection");
//			}
	
	
	}




/**login web service call class*/
	
	
private class httpLogin extends AsyncTask<Void, Void, Void> { // Async_task class
	protected void onPreExecute() {
		super.onPreExecute();
		pBar.setVisibility(View.VISIBLE);
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
		pBar.setVisibility(View.GONE);
		if(login_Exception==1)
			{
				Utility.alertMessage(Splash_Activity.this, "error in internet connection");	
				}
		else if(splash_jsonResult.equals("0"))
			{
				if(prefs.getString("paymentcheck", null)!=null)
				{
					Intent i=new Intent(getBaseContext(),RideFinish.class);
					i.putExtra("paymentfinish", "value");
					System.err.println("payment");
		            startActivity(i);
		            finish();
					
					}
				else
				{
					  System.err.println("MODEEEEEEE="+prefs.getString("mode", ""));
					  if(prefs.getString("mode", "").equals("driver"))
					  {
						  Intent i=new Intent(getBaseContext(),DriverView_Activity.class);
						  System.err.println("driver");
			              startActivity(i);
			              finish();  
					  	}
					  else
					  {
						  Intent i=new Intent(getBaseContext(),MapView_Activity.class);
						  System.err.println("rider");
			              startActivity(i);
			              finish();
					  	}
					}
			}
				
			else
			{
				Utility.alertMessage(Splash_Activity.this, splash_jsonMessage);
				}
		
	}
		
	
/**  login parsing function */
	public void parsing() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 60000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 61000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/Login");//Url_Address.url_Login);
		JSONObject json = new JSONObject();
		
		json.put("Trigger", "Login");
		json.put("Email", prefs.getString("key_username", null));
		json.put("Password", prefs.getString("key_password", null));
		//json.put("devicetrigger", "android");
	      //	      
		httpost.setEntity(new StringEntity(json.toString()));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(httpost);
		HttpEntity resEntityGet = response.getEntity();
		String jsonstr=EntityUtils.toString(resEntityGet);
		if(jsonstr!=null)
		{
		 Log.i("tag","login result>>>>>>>    "+ jsonstr);
		 
			}
			JSONObject obj=new JSONObject(jsonstr);
			splash_jsonResult=obj.getString("result");
			splash_jsonMessage=obj.getString("message");
			Log.i("tag:", "Result: "+splash_jsonResult);
			Log.i("tag:", "Message :"+splash_jsonMessage);
			
			String ListPromoCodeInfo=	obj.getString("ListPromoCodeInfo");
			Log.i("tag:", "ListPromoCodeInfo: "+ListPromoCodeInfo);
			
			JSONArray jsonarray=obj.getJSONArray("ListPromoCodeInfo");
		
			for(int i=0;i<jsonarray.length();i++){
				
			JSONObject obj2=jsonarray.getJSONObject(i);
			
			
			String	promocode=	obj2.getString("promocode");
			Log.i("tag:", "promocode: "+promocode);
			
			String dateused=	obj2.getString("dateused");
			Log.i("tag:", "dateused: "+dateused);
			
			String datecreated=	obj2.getString("datecreated");
			Log.i("tag:", "datecreated: "+datecreated);
			
			String value=	obj2.getString("value");
			Log.i("tag:", "value: "+value);
			
			Utility.arrayListPromo().add(promocode+"    ("+"$"+value+")");
			
			 }
		
			HashSet hs = new HashSet();
			hs.addAll(Utility.arrayListPromo());
			Utility.arrayListPromo().clear();
			Utility.arrayListPromo().addAll(hs);
			
		 
			
			String userid=	obj.getString("userid");
			Log.i("tag:", "fname: "+userid);
			
			String prefvehicletype=	obj.getString("prefvehicletype");
			Log.i("tag:", "prefvehicletype: "+prefvehicletype);
			
			String fname=	obj.getString("firstname");
			Log.i("tag:", "fname: "+fname);
			
			String lname=	obj.getString("lastname");
			Log.i("tag:", "lname: "+lname);
			
			String pass=obj.getString("password");
			Log.i("tag:", "password: "+pass);
			
			String phone=	obj.getString("phone");
			Log.i("tag:", "phone: "+phone);
			

			String country=	obj.getString("country");
			Log.i("tag:", "country: "+country);
			String apt=obj.getString("apt");
			Log.i("tag:", "apt: "+apt);
			String state=obj.getString("state");
			Log.i("tag:", "state: "+state);
			String city=obj.getString("city");
			Log.i("tag:", "city: "+city);
			String zipcode=obj.getString("zip");
			Log.i("tag:", "zipcode: "+zipcode);
			
			String address=obj.getString("address");
			Log.i("tag:", "address: "+address);
			
			String profilepicdata=obj.getString("profilepicdata");
			Log.i("tag:", "profilepicdata: "+profilepicdata);
			String profilepiclocation=obj.getString("profilepiclocation");
			Log.i("tag:", "profilepiclocation: "+profilepiclocation);
			String driveractive=obj.getString("driveractive");
			Log.i("tag:", "driveractive: "+driveractive);
			String driverid=obj.getString("driverid");
			Log.i("tag:", "driverid: "+driverid);
			String favoritedriver=obj.getString("favoritedriver");
			Log.i("tag:", "favoritedriver: "+favoritedriver);
			
			String paymentstatus=obj.getString("payment_status");
			Log.i("tag:", "favoritedriver: "+paymentstatus);
			
			String handicap=obj.getString("handicap");
			Log.i("tag:", "handicap: "+handicap);
			
			String activetripid=obj.getString("active_tripid");
			Log.i("tag:", "active_tripid: "+activetripid);
			
			String animal=obj.getString("animal");
			Log.i("tag:", "animal: "+animal);
			
			String specialneedsnotes=obj.getString("specialneedsnotes");
			Log.i("tag:", "specialneedsnotes: "+specialneedsnotes);
			
			String flightno=obj.getString("flightnumber");
			Log.i("tag:", "flightno: "+flightno);
			
			Editor editor =prefs.edit();
			editor.putString("useremail", prefs.getString("usermail", null));
			
			editor.putString("userid", userid);
			
			if(prefvehicletype.equals(""))
			{
			}
			else
			{
				int_value=Integer.parseInt(prefvehicletype);	
				}
			editor.putInt("vehicletype", int_value);
			
			editor.putString("userfirstname", fname);
			editor.putString("userlastname", lname);
			editor.putString("phone", phone);
			editor.putString("password", pass);
			editor.putString("apt", apt);
			editor.putString("country", country);
			editor.putString("state", state);
			editor.putString("city", city);
			editor.putString("zip", zipcode);
			editor.putString("address", address);
			editor.putString("completeaddress", address+","+city+","+state+","+country);
			editor.putString("profilepic", profilepicdata);
			editor.putString("picurl", profilepiclocation);
			
			editor.putString("driveractive",driveractive);
		/*	if(driveractive.trim().equals("false"))
			{
				editor.putString("driverid","");
				}
			else
			{*/
				editor.putString("driverid",driverid);
				//}
			editor.putString("favoritedriver",favoritedriver );
			editor.putString("paymentstatus",paymentstatus );
			editor.putString("handicap",handicap );
			editor.putString("activetripid",activetripid );
			
			editor.putString("animal",animal);
			editor.putString("specialneed",specialneedsnotes);
			
			editor.putString("flightno",flightno);
			editor.commit();
			
			Log.i("tag", "Address get: "+prefs.getString("completeaddress", null));
			
			}
		  catch(Exception e){
		   System.out.println(e);
		   login_Exception=1;
		   Log.d("tag", "Error :"+e); }  
		
		}
	}
@Override
protected void onResume() {

	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	pBar=(ProgressBar)findViewById(R.id.progressBar_splash);
	
	// Get Location Manager and check for GPS & Network location services
	LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
	if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
	      !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
	  // Build the alert dialog
	  AlertDialog.Builder builder = new AlertDialog.Builder(this);
	  builder.setTitle("Location Services Not Active");
	  builder.setMessage("Please enable Location Services and GPS");
	  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
	  public void onClick(DialogInterface dialogInterface, int i) {
	    // Show location settings when the user acknowledges the alert dialog
	    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
	    startActivity(intent);
	    }
	  });
	  Dialog alertDialog = builder.create();
	  alertDialog.setCanceledOnTouchOutside(false);
	  alertDialog.show();
	}
/*	else if(!isGooglePlayServicesAvailable())
	{
		finish();
		 Utility.alertMessage(Splash_Activity.this, "Google Play Services not Available");
	}*/
	else
	{
		if(prefs.getString("key_username", null)==null)
		{
			Thread background = new Thread()
				{
					public void run()
						{
							try {
									 
							 sleep(2*1000); /** Thread will sleep for 2 seconds*/
					              if(Utility.isConnectingToInternet(Splash_Activity.this))
					               {
									 Intent i=new Intent(getBaseContext(),Login_Activity.class);
					                 startActivity(i);
					                 finish();
					                 	}
					                 else
					                 {
					                	 Utility.alertMessage(Splash_Activity.this, "No internet connection");
					                 	}
								}
							catch (Exception e) {}
		  					}	
						};
				
					// start thread
					background.start();
			}
		else{
			  if(Utility.isConnectingToInternet(Splash_Activity.this))
			  {
				new  httpLogin().execute();
//			  String Mode=prefs.getString("mode", null); 
//			  if(Mode.equals("driver"))
//			  {
//				  Intent i=new Intent(getBaseContext(),DriverView_Activity.class);
//				  System.err.println("splash");
//	              startActivity(i);
//	              finish();  
//			  }
//			  else
//			  {
//				  Intent i=new Intent(getBaseContext(),MapView_Activity.class);
//				  System.err.println("splash");
//	              startActivity(i);
//	              finish();
//				  }
			  }
			  else
			  {
				  Utility.alertMessage(Splash_Activity.this, "No internet connection");
			  	}
//			new httpLogin().execute(); // call web service
		}
	}
super.onResume();
}
/*private boolean isGooglePlayServicesAvailable() {
    int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
    if (ConnectionResult.SUCCESS == status) {
        return true;
    } else {
        GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
        return false;
    }
}*/
}
