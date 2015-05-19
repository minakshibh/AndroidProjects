package com.rapidride;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;

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
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gcm.GCMRegistrar;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class Login_Activity extends Activity {
	
	EditText edittext_username,edittext_password;
	Button btn_login,btn_forgotpassword,btn_register,btn_editRegister;
	TextView tv_login;
	ProgressBar pBar;
	private ProgressDialog pDialog;
	
	Typeface typeFace;
	SharedPreferences prefs;
	TelephonyManager tManager;
	AsyncTask<Void, Void, Void> mRegisterTask;
	
	String	promocode="";
	private String jsonResult="";
	private String jsonMessage="";
	String str_ed_username,str_ed_password;
	int login_Exception=0;
	String uuid="";
	String regId="",driverid="",riderid="";
	String zipcode="";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
/*		String locale = this.getResources().getConfiguration().locale.getCountry();
		Log.d("tag", "locale:"+locale);
		
//		TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		TelephonyManager telephonyManager=(TelephonyManager)getBaseContext().
			    getSystemService(Context.TELEPHONY_SERVICE);
			String CountryIso= telephonyManager.getNetworkCountryIso();
			String NetworkIso=telephonyManager.getSimCountryIso();
		
			Log.d("tag", "CountryIso:"+CountryIso);
			Log.d("tag", "NetworkIso:"+NetworkIso);
			
			try{
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expression = "//GeocodeResponse/result/address_component[type=\"postal_code\"]/long_name/text()";
			InputSource inputSource = new InputSource("https://maps.googleapis.com/maps/api/geocode/xml?latlng=37.775,-122.4183333&sensor=true");
			 zipcode = (String) xpath.evaluate(expression, inputSource, XPathConstants.STRING);
			Log.i("tag", "zipcode:"+zipcode);
			}
			catch(Exception e)
			{
				Log.i("tag", "zipcode:"+zipcode);	
			}*/
			
		zipcode="+"+GetCountryZipCode();
		Log.d("tag", "zipcode:"+zipcode);
		
		SharedPreferences.Editor prefsEditor = prefs.edit();
        prefsEditor.putString("Zipcode",zipcode );
        // editor.remove("key_username");
        prefsEditor.commit();
        
		
        checkNotNull(Utility.SERVER_URL, "SERVER_URL");
        checkNotNull(Utility.SENDER_ID, "SENDER_ID");
    	//------------------------------------
        // Make sure the device has the proper dependencies.
        GCMRegistrar.checkDevice(Login_Activity.this);
        // Make sure the manifest was properly set - comment out this line
        // while developing the app, then uncomment it when it's ready.
     //   GCMRegistrar.checkManifest(Splash_Activity.this);
		
		
		tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        uuid = tManager.getDeviceId();
		
	 
		 typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		 tv_login=(TextView)findViewById(R.id.textView_login_title);
		 tv_login.setTypeface(typeFace);
		/*txtHeader.setTypeface(typeFace);*/
		 pBar=(ProgressBar)findViewById(R.id.progressBar_splash);
		
		
		edittext_username=(EditText)findViewById(R.id.editText_username);
		edittext_password=(EditText)findViewById(R.id.editText_password);
		btn_login=(Button)findViewById(R.id.button_login);
		if(getIntent().getStringExtra("username")==null)
		{}
		else
		{
			edittext_username.setText(getIntent().getStringExtra("username"));
			edittext_password.setText(getIntent().getStringExtra("password"));
			}
		//btn_editRegister=(Button)findViewById(R.id.button_editRegister);
		btn_login.setTypeface(typeFace);
		
		btn_login.setOnClickListener(new OnClickListener() { // login button function
			
			@Override
			public void onClick(View v) { //checking the empty fields
				str_ed_username=edittext_username.getText().toString();
				str_ed_password=edittext_password.getText().toString();
				
				if((edittext_username.getText().toString()).equals("") || edittext_password.getText().toString().equals(""))
					{
					
						Utility.alertMessage(Login_Activity.this,"Please enter username and password");
					
						}
				else if(Utility.isConnectingToInternet(Login_Activity.this))
				{
					new httpLogin().execute();
					Utility.hideKeyboard(Login_Activity.this);
					}
				else{
					Utility.alertMessage(Login_Activity.this,"error in internet connection");
				}
				}
		});
		btn_forgotpassword=(Button)findViewById(R.id.button_forgotpassword);
		btn_forgotpassword.setPaintFlags(btn_forgotpassword.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);//under line text
		btn_forgotpassword.setTypeface(typeFace);
		btn_forgotpassword.setOnClickListener(new OnClickListener() {  // forgot password function
	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(Login_Activity.this,ForgotPassword.class);
				startActivity(i);
				}
		});
		
		btn_register=(Button)findViewById(R.id.button_signup);
		btn_register.setPaintFlags(btn_register.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
		btn_register.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(Login_Activity.this,Rider_Register.class);
				startActivity(i);
			}
		});

	}

	private boolean isValidEmail(String email) // email validation 
	{
	    String emailRegex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	    if(email.matches(emailRegex))
	    	{
	        	return true;
	    		}
	    return false;
		}
// Async_task login class
	private class httpLogin extends AsyncTask<Void, Void, Void> { 
	
		private int int_value=0;

		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(Login_Activity.this);
			login_Exception=0;
			
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
		
		@Override
		protected Void doInBackground(Void... arg0) {
		
			try {
			 parsing();
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
			
			return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			edittext_password.setText("");	
			if(login_Exception==1)
			{
				Utility.alertMessage(Login_Activity.this,"error in internet connection");
			}
			else if(jsonResult.equals("0"))
			{
				SharedPreferences.Editor prefsEditor = prefs.edit();
		        prefsEditor.putString("key_username",str_ed_username );
		        prefsEditor.putString("key_password",str_ed_password);
		        prefsEditor.commit();
		        deviceRegister();
			}

			else{
					Utility.alertMessage(Login_Activity.this,jsonMessage);
				}
		
			
			}
		
//Login parsing function
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
			
		//	json.put("Trigger", "Login");
			json.put("Email", str_ed_username);
			json.put("Password", str_ed_password);
//			json.put("devicetrigger", "android");
			
			Log.i("tag", "Email:"+str_ed_username);
			Log.i("tag", "Password:"+str_ed_password);
			//Log.i("tag", "andriod:"+"android");
			
		      //	      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.i("tag","login result--->>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
				
				
				String ListPromoCodeInfo=	obj.getString("ListPromoCodeInfo");
				Log.i("tag:", "ListPromoCodeInfo: "+ListPromoCodeInfo);
				
				JSONArray jsonarray=obj.getJSONArray("ListPromoCodeInfo");
			
				for(int i=0;i<jsonarray.length();i++){
					
				JSONObject obj2=jsonarray.getJSONObject(i);
							
				promocode=	obj2.getString("promocode");
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
				
				riderid=obj.getString("userid");
				System.err.println(riderid);
			
				String prefvehicletype=	obj.getString("prefvehicletype");
				Log.i("tag:", "prefvehicletype: "+String.valueOf(prefvehicletype));
				
				/**************/
				
				if(prefvehicletype.equalsIgnoreCase("") || prefvehicletype.equalsIgnoreCase(null))
				{
					prefvehicletype="1";
				}
				/**********************/
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
				
				driverid=obj.getString("driverid");
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
				editor.putString("useremail", edittext_username.getText().toString());
				
				editor.putString("userid", riderid);
				
				if(!(prefvehicletype==null))
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
				//	}
				editor.putString("favoritedriver",favoritedriver );
				editor.putString("paymentstatus",paymentstatus );
				editor.putString("handicap",handicap );
				editor.putString("activetripid",activetripid );
				
				editor.putString("animal",animal);
				editor.putString("specialneed",specialneedsnotes);
				editor.putString("flightno",flightno);
				editor.commit();
				
				Log.i("tag", "Address get: "+prefs.getString("completeaddress", null));
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+jsonMessage);
				}
			   catch(Exception e){
			   System.out.println(e);
			   login_Exception=1;
			   Log.d("tag", "login Error :"+e); }  
				}
		
	}
	private void checkNotNull(Object reference, String name) {
	    if (reference == null) {
	        throw new NullPointerException(
	                getString(R.string.error_config, name));
	    }
	}

	private final BroadcastReceiver mHandleMessageReceiver =
	        new BroadcastReceiver() {
	    @Override
	    public void onReceive(Context context, Intent intent) {
	    	Log.e("on receive","broad cast receiver");
//	        String newMessage = intent.getExtras().getString("my custom message");
//	        mDisplay.append(newMessage + "\n");
	    	
	    
	    	
	    }
	};
	public String isApplicationSentToBackground(final Context context) {
	    ActivityManager am = (ActivityManager) context
	            .getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> tasks = am.getRunningTasks(1);
	    if (!tasks.isEmpty()) {
	        ComponentName topActivity = tasks.get(0).topActivity;
	        if (!topActivity.getPackageName().equals(context.getPackageName())) {
	            return "false";
	        }
	    }

	    return "true";
	}

	@Override
	protected void onDestroy() {
		try{
	        if (mRegisterTask != null) {
	            mRegisterTask.cancel(true);
	        }
	        unregisterReceiver(mHandleMessageReceiver);
	        GCMRegistrar.onDestroy(this);
		}catch(Exception e){
			e.printStackTrace();
		}
	    super.onDestroy();
	}
	

	
	
	
	public void deviceRegister()
	{
		try {
	        PackageInfo info = getPackageManager().getPackageInfo("com.rapidride", PackageManager.GET_SIGNATURES);
	        for (Signature signature : info.signatures) {
	            MessageDigest md = MessageDigest.getInstance("SHA");
	            md.update(signature.toByteArray());
	            Log.d("Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
	            }
	    } catch (NameNotFoundException e) {

	    	e.printStackTrace();
	    } catch (NoSuchAlgorithmException e) {
	    	e.printStackTrace();

	    }
		
		registerReceiver(mHandleMessageReceiver,new IntentFilter(Utility.DISPLAY_MESSAGE_ACTION));
		regId = GCMRegistrar.getRegistrationId(this);
        Editor ed=prefs.edit();
      //  ed.putString("regid", regId);
        ed.putString("uuid", uuid);
        ed.commit();
        System.out.println("Resister id="+regId);
        
        GCMRegistrar.register(this, Utility.SENDER_ID);
        Intent i1=new Intent(Login_Activity.this,MapView_Activity.class);
        finish();
    	startActivity(i1);
        //new RegisterTask().execute();
	    if (regId.equals("")) {
	        // Automatically registers application on startup.
	    	// GCMRegistrar.register(this, Utility.SENDER_ID);
	        System.err.println("enter in if");
	    } else {
	    	  pDialog.dismiss();
	        	System.err.println("enter in else");
            // Device is already registered on GCM, check server.
            if (GCMRegistrar.isRegisteredOnServer(this)) {
                // Skips registration.
               Log.e("already registered", "already reg");
              
//               Intent i=new Intent(Splash_Activity.this,Login_Activity.class);/************go to login class******************/
//               startActivity(i);
             //  pBar.setVisibility(View.GONE);
               Intent i=new Intent(Login_Activity.this,MapView_Activity.class);
               finish();
               startActivity(i);
 			   
            }
            
          
            else {
                // Try to register again, but not in the UI thread.
                // It's also necessary to cancel the thread onDestroy(),
                // hence the use of AsyncTask instead of a raw thread.
              //  final Context context = this;
                
               new RegisterTask().execute();
                		
//                		{
//                	protected void onPreExecute() {
////            			super.onPreExecute();
////            			// Showing progress dialog
//            	//	pBar.setVisibility(View.VISIBLE);
//                	}
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                    	
//                    	System.err.println("doInBackground");
//                        boolean registered =
//                        	//	 String role, final String driverId,final String riderId,final String regId,final String deviceId,final String TokenId)
//                               // com.rapidride.util.ServerUtilities.register(context, uuid, regId,);
//                        com.rapidride.util.ServerUtilities.register(context, "driver", "20", "71",uuid, regId);
//                        // At this point all attempts to register with the app
//                        // server failed, so we need to unregister the device
//                        // from GCM - the app will try to register again when
//                        // it is restarted. Note that GCM will send an
//                        // unregistered callback upon completion, but
//                        // GCMIntentService.onUnregistered() will ignore it.
//                        if (!registered) {
//                            GCMRegistrar.unregister(context);
//                        }
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                    	pBar.setVisibility(View.GONE);
//                        mRegisterTask = null;
//                      Intent i=new Intent(Splash_Activity.this,Login_Activity.class); /************go to class******************/
//          			  System.err.println("splash");
//                        startActivity(i);
//                    }
//
//                };
//                mRegisterTask.execute(null, null, null);
//            }
           
       }
	}
	}
	
// Try to register again, but not in the UI thread.
// It's also necessary to cancel the thread onDestroy(),
// hence the use of AsyncTask instead of a raw thread.

	
	class RegisterTask extends AsyncTask<Void, Void, Void>
	//final Context context = Login_Activity.this;
		{
	protected void onPreExecute() {
//		super.onPreExecute();
//		// Showing progress dialog
//	pBar.setVisibility(View.VISIBLE);
	}
    @Override
    protected Void doInBackground(Void... params) {
    	
    	System.err.println("doIn Background"+"driver"+" driverid="+driverid+" riderid="+riderid+"  uuid="+uuid+"regId="+regId);
        boolean registered =
        	//	 String role, final String driverId,final String riderId,final String regId,final String deviceId,final String TokenId)
               // com.rapidride.util.ServerUtilities.register(context, uuid, regId,);
        		
        com.rapidride.util.ServerUtilities.register(Login_Activity.this, "rider", driverid, riderid,uuid, regId);
        // At this point all attempts to register with the app
        // server failed, so we need to unregister the device
        // from GCM - the app will try to register again when
        // it is restarted. Note that GCM will send an
        // unregistered callback upon completion, but
        // GCMIntentService.onUnregistered() will ignore it.
        if (!registered) {
            GCMRegistrar.unregister(Login_Activity.this);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
    //	pBar.setVisibility(View.GONE);
        mRegisterTask = null;
       // pDialog.dismiss();
        Intent i=new Intent(Login_Activity.this,MapView_Activity.class);
        finish();
		startActivity(i);
    }


}
	public String GetCountryZipCode(){
	    String CountryID="";
	    String CountryZipCode="";

	    TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
	    //getNetworkCountryIso
	    CountryID= manager.getNetworkCountryIso().toUpperCase();
	    String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
	    for(int i=0;i<rl.length;i++){
	        String[] g=rl[i].split(",");
	        if(g[1].trim().equals(CountryID.trim())){
	            CountryZipCode=g[0];
	            break;  
	        }
	    }
	    return CountryZipCode;
	}
	
	public void onBackPressed() {
		
		 moveTaskToBack (true);
	}
	}
