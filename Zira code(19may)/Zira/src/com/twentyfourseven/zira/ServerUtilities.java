package com.twentyfourseven.zira;



import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.zira.login.LegalPolicyDailog;
import com.zira.modal.ProfileInfoModel;
import com.zira.util.Util;


public class ServerUtilities extends Activity {
	 private static final int MAX_ATTEMPTS = 5;
	       private static final int BACKOFF_MILLI_SECONDS = 1000;
	       private static final Random random = new Random();
	       private static String TAG = "** ServerUtilities Activity **";
	       static SharedPreferences prefs;
	       private static Context mContext;
	       TelephonyManager tManager;
	       private String riderid, udid, regId;
	       static String checkpolicy;
	    /**
	     * Register this account/device pair within the server.
	     *
	     * @return whether the registration succeeded or not.
	     */
	    public static boolean register(final Context context, final String role, final String driverId,final String riderId,final String deviceId,final String regId) {
	        Log.e(TAG, "registering device (regId = " + regId + ")");
	        //String serverUrl = SERVER_URL + "/register";
	        prefs = context.getSharedPreferences("Zira", MODE_PRIVATE); 
	        Editor ed=prefs.edit();
	        ed.putString("regid", regId);
	        ed.commit();
	        String serverUrl = Notification_Util.SERVER_URL;
	        
	        mContext = context;
	//        HttpParams httpParameters = new BasicHttpParams();
//	        HttpClient client = new DefaultHttpClient(httpParameters);
//			HttpPost httpost = new HttpPost(serverUrl);//Url_Address.url_Login);
//			JSONObject json = new JSONObject();
	        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	        
	        nameValuePair.add(new BasicNameValuePair("Role", role));
	        nameValuePair.add(new BasicNameValuePair("DriverId", riderId));
	        nameValuePair.add(new BasicNameValuePair("RiderId", riderId));
	        nameValuePair.add(new BasicNameValuePair("DeviceUDId", deviceId));
	        nameValuePair.add(new BasicNameValuePair("TokenID", regId));
	        nameValuePair.add(new BasicNameValuePair("Trigger", "android"));
	        
	        
	        
	        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
	        // Once GCM returns a registration id, we need to register it in the
	        // demo server. As the server might be down, we will retry it a couple
	        // times.
	        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
	            Log.d(TAG, "Attempt #" + i + " to register");
	            try {
	            	Notification_Util.displayMessage(context, context.getString(
	                        R.string.server_registering, i, MAX_ATTEMPTS));
	                String result = Util.getResponseFromUrl(serverUrl, nameValuePair, mContext);
	                
	                Log.e("post called","series 1 "+result);
	            	//goToNext();
	            	
	            	
	                GCMRegistrar.setRegisteredOnServer(context, true);
	                String message = context.getString(R.string.server_registered);
	                Notification_Util.displayMessage(context, message);
	                
	                Log.e("post called","series 2");
	                
	                /*try{
		                Intent intent = new Intent(mContext, VehicleSearchActivity.class);
		    			((Activity)LoginActivity.loginContext).finish();
		    			
		    			((Activity)LoginActivity.loginContext).startActivity(intent);
	                }catch(Exception e){
	                	 Intent intent = new Intent(mContext, VehicleSearchActivity.class);
	 	    			((Activity)SplashScreen.splashContext).finish();
	 	    			
	 	    			((Activity)SplashScreen.splashContext).startActivity(intent);
	                }*/
	    			
	                return true;
	            } catch (Exception e) {
	                // Here we are simplifying and retrying on any error; in a real
	                // application, it should retry only on unrecoverable errors
	            	e.printStackTrace();
	                Log.e(TAG, "Failed to register on attempt " + i, e);
	                if (i == MAX_ATTEMPTS) {
	                    break;
	                }
	                try {
	                    Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
	                    Thread.sleep(backoff);
	                } catch (InterruptedException e1) {
	                    // Activity finished before we complete - exit.
	                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
	                    Thread.currentThread().interrupt();
	                    return false;
	                }
	                // increase backoff exponentially
	                backoff *= 2;
	            }
	        }
	        String message = context.getString(R.string.server_register_error, MAX_ATTEMPTS);
	        Notification_Util.displayMessage(context, message);
	        return false;
	    }

	    /**
	     * Unregister this account/device pair within the server.
	     */
	    static void _unregister(final Context context, final String regId) {
	        Log.i(TAG, "unregistering device (regId = " + regId + ")");
	        String serverUrl = mContext.getResources().getString(R.string.baseUrl) + "/unregister";
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("regId", regId);
	        try {
	            post(serverUrl, params);
	            GCMRegistrar.setRegisteredOnServer(context, false);
	            String message = context.getString(R.string.server_unregistered);
	            Notification_Util.displayMessage(context, message);
	        } catch (IOException e) {
	            // At this point the device is unregistered from GCM, but still
	            // registered in the server.
	            // We could try to unregister again, but it is not necessary:
	            // if the server tries to send a message to the device, it will get
	            // a "NotRegistered" error message and should unregister the device.
	            String message = context.getString(R.string.server_unregister_error,e.getMessage());
	            Notification_Util.displayMessage(context, message);
	        }
	    }

	    /**
	     * Issue a POST request to the server.
	     *
	     * @param endpoint POST address.
	     * @param params request parameters.
	     *
	     * @throws IOException propagated from POST.
	     */
	    private static void post(String endpoint, Map<String, String> params)
	            throws IOException {
	        URL url;
	        try {
	            url = new URL(endpoint);
	        } catch (MalformedURLException e) {
	            throw new IllegalArgumentException("invalid url: " + endpoint);
	        }
	        StringBuilder bodyBuilder = new StringBuilder();
	        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
	        // constructs the POST body using the parameters
	        while (iterator.hasNext()) {
	            Entry<String, String> param = iterator.next();
	            bodyBuilder.append(param.getKey()).append('=')
	                    .append(param.getValue());
	            if (iterator.hasNext()) {
	                bodyBuilder.append('&');
	            }
	        }
	        String body = bodyBuilder.toString();
	        Log.v(TAG, "Posting '" + body + "' to " + url);
	        byte[] bytes = body.getBytes();
	        HttpURLConnection conn = null;
	        try {
	            conn = (HttpURLConnection) url.openConnection();
	            conn.setDoOutput(true);
	            conn.setUseCaches(false);
	            conn.setFixedLengthStreamingMode(bytes.length);
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	            // post the request
	            OutputStream out = conn.getOutputStream();
	            out.write(bytes);
	            out.close();
	            // handle the response
	            int status = conn.getResponseCode();
	            if (status != 200) {
	              throw new IOException("Post failed with error code " + status);
	            }
	            
	            Log.e("called","complete");
	        } finally {
	            if (conn != null) {
	                conn.disconnect();
	            }
	        }
	      }
	    
	    public void deviceRegister(Context _ctx, ProfileInfoModel mProfileInfoModel) {

	    	mContext = _ctx;
			tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			udid = tManager.getDeviceId();
			System.err.println("udid=" + udid);
			prefs = mContext.getSharedPreferences("Zira", MODE_PRIVATE); 
			
			riderid = mProfileInfoModel.getUserid();
			Editor e = prefs.edit();
		//	e.putString("riderid", riderid);
			e.putString("udid", udid);
			//e.putString("mode", "rider");
			e.commit();

			try {
				PackageInfo info = mContext.getPackageManager().getPackageInfo("com.twentyfourseven.zira_temp", PackageManager.GET_SIGNATURES);
				for (Signature signature : info.signatures) {
					MessageDigest md = MessageDigest.getInstance("SHA");
					md.update(signature.toByteArray());
					Log.d("Your Tag",Base64.encodeToString(md.digest(), Base64.DEFAULT));
				}
			} catch (NameNotFoundException ex) {
				ex.printStackTrace();
			} catch (NoSuchAlgorithmException ex) {
				ex.printStackTrace();

			}

			mContext.registerReceiver(mHandleMessageReceiver, new IntentFilter(Notification_Util.DISPLAY_MESSAGE_ACTION));
			regId = GCMRegistrar.getRegistrationId(mContext);
			e = prefs.edit();
			e.putString("regid", regId);
			e.commit();

			GCMRegistrar.register(mContext, Notification_Util.SENDER_ID);// //
																					// by
																					// sender
																					// id
			/*
			 * Intent i1=new
			 * Intent(LoginActivity1.this,VehicleSearchActivity.class);
			 * startActivity(i1);
			 */
			// new RegisterTask().execute();
//			if (!regId.trim().equals("")) {
				
				new RegisterTask().execute(); // by server
				
				/*// Automatically registers application on startup.
				// GCMRegistrar.register(this, Utility.SENDER_ID);
				// GCMRegistrar.register(LoginActivity1.this,
				// Notification_Util.SENDER_ID);
				// regId = GCMRegistrar.getRegistrationId(LoginActivity1.this);
				Intent i1 = new Intent(LoginActivity.this,
						VehicleSearchActivity.class);
				finish();
				startActivity(i1);
				System.err.println("enter in if" + regId);
			} else {
				System.err.println("enter in else");
				// Device is already registered on GCM, check server.
				if (GCMRegistrar.isRegisteredOnServer(this)) {
					// Skips registration.
					Toast.makeText(LoginActivity.this, regId, 1).show();
					System.out.println("Resister id=" + regId);
					Log.e("already registered", "already reg on server");
					Intent i = new Intent(LoginActivity.this, VehicleSearchActivity.class);
					finish();
					startActivity(i);
				} else {
					
				}*/
//			}
		}

		
		class RegisterTask extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {
			}

			@Override
			protected Void doInBackground(Void... params) {

				boolean registered = ServerUtilities.register(mContext,
						"rider", riderid, riderid, udid, regId);
				// At this point all attempts to register with the app
				// server failed, so we need to unregister the device
				// from GCM - the app will try to register again when
				// it is restarted. Note that GCM will send an
				// unregistered callback upon completion, but
				// GCMIntentService.onUnregistered() will ignore it.
				
				Log.e("registration complete","complete");
				if (!registered) {
					GCMRegistrar.unregister(mContext);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.e("server utilty registration complete","on post");
//				progress.dismiss();
				if(LoginActivity.loginContext != null)
				{
					mContext= LoginActivity.loginContext;
					}
				else
				{
					mContext = SplashScreen.splashContext;
					}
				
			///////////////////
				goToNext();
				}
		}
		
		 private final static BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					Log.e("on receive", "broad cast receiver");
					// String newMessage =
					// intent.getExtras().getString("my custom message");
					// mDisplay.append(newMessage + "\n");

				}
			};
			
		public static void	goToNext()
			{
				try{
				String mode = prefs.getString("mode", "");
				System.err.println("modeeeeeeeeeeeeeeeeeeeee========"+mode);
				if(mode.equals(""))
				{
					Intent i = new Intent(mContext, LoginActivity.class);
					((Activity) mContext).finish();
					((Activity) mContext).startActivity(i);
					
					if(SplashScreen.check==false)
					{
													
					    checkpolicy=LoginActivity.prefs2.getString("policy", "");
						if(checkpolicy.equals(""))//lpft=legal policy first time show check
						{
							Editor e=LoginActivity.prefs2.edit();
							e.putString("policy", "fullfhg");//lpft=legal policy first time show check
							e.commit();
							String check1=	LoginActivity.prefs2.getString("policy", "");
							System.err.println("check1check1check1"+check1);
							Intent intent = new Intent(mContext, LegalPolicyDailog.class);
							((Activity) mContext).finish();
							((Activity) mContext).startActivity(intent);
							
							}
						else
						{
							Intent i2 = new Intent(mContext, VehicleSearchActivity.class);
							//i.putExtra("splitfare", "yes");
							((Activity) mContext).finish();
							((Activity) mContext).startActivity(i2);
							}
						}
					}
				else if (mode.equals("driver")) {
					Intent i = new Intent(mContext, DriverModeActivity.class);
					((Activity) mContext).finish();
					((Activity) mContext).startActivity(i);
					}
				else if(mode.equals("rider")) {
					
					/*	String getUserid = prefs.getString("Userid", "");
						if (getUserid.equals("")) {
							Intent i = new Intent(mContext, LoginActivity.class);
							((Activity) mContext).finish();
							((Activity) mContext).startActivity(i);
						}
						else {*/
							
							
							
								Intent i = new Intent(mContext, VehicleSearchActivity.class);
								/*i.putExtra("legalpolicy", "yes");*/
								((Activity) mContext).finish();
								((Activity) mContext).startActivity(i);
								
							//}
				}
			
			}
		catch(Exception e)
		{
			System.err.println("server utility class="+e);
		}
			}
	}