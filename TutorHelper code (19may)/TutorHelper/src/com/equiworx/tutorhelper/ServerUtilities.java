package com.equiworx.tutorhelper;



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

import com.equiworx.model.Tutor;
import com.equiworx.tutor.TutorDashboard;
import com.equiworx.util.Util;
import com.google.android.gcm.GCMRegistrar;



public class ServerUtilities extends Activity {
		   private static final int MAX_ATTEMPTS = 5;
	       private static final int BACKOFF_MILLI_SECONDS = 1000;
	       private static final Random random = new Random();
	       private static String TAG = "** ServerUtilities Activity **";
	       static SharedPreferences prefs;
	       private static Context mContext;
	       static TelephonyManager tManager;
	       private static String udid;
	       private String regId;
	       static String checkpolicy;
	       private String str_tutorid="";
	       private String str_userid="";
	       private String str_role="";
	     public static SharedPreferences tutorPrefs; 
	    /**
	     * Register this account/device pair within the server.
	     *
	     * @return whether the registration succeeded or not.
	     */
	    public static boolean register(final Context context, final String trigger, String role,final String regId,String userid,String udid) {
	        Log.e(TAG, "registering device (regId = " + regId + ")");
	        //String serverUrl = SERVER_URL + "/register";
	        tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			udid = tManager.getDeviceId();
			System.err.println("udid=" + udid);
	        String serverUrl = Notification_Util.SERVER_URL;
	     
	        mContext = context;
	       
	        ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
	        
	        nameValuePair.add(new BasicNameValuePair("trigger", "Android"));
	        nameValuePair.add(new BasicNameValuePair("role", role));
	        nameValuePair.add(new BasicNameValuePair("reg_id", regId));
	        nameValuePair.add(new BasicNameValuePair("user_id",userid));
	        nameValuePair.add(new BasicNameValuePair("ud_id", udid));
	      
	        
	        System.err.println(nameValuePair.toString());
	        
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
	    
	    public void deviceRegister(Context _ctx) {

	    	mContext = _ctx;
			tManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			udid = tManager.getDeviceId();
			System.err.println("udid=" + udid);
			//tutorid=	tutor.getTutorId();
		

			try {
				PackageInfo info = mContext.getPackageManager().getPackageInfo("com.equiworx.tutor", PackageManager.GET_SIGNATURES);
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
		
			GCMRegistrar.register(mContext, Notification_Util.SENDER_ID);// //
																					// by
			new RegisterTask().execute(); // by server
			
		}

		
		class RegisterTask extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {
			}

			@Override
			protected Void doInBackground(Void... params) {
				
				Log.e("taskkkkkk", "taskkkkkk");
				
				tutorPrefs= mContext.getSharedPreferences("tutor_prefs", 0);
				if(tutorPrefs.getString("mode", "").equalsIgnoreCase("tutor"))
		    	{
		    	   str_role="Tutor";
		    	   str_userid=tutorPrefs.getString("tutorID", "");
		    	   
		    		   }
		       else if(tutorPrefs.getString("mode", "").equalsIgnoreCase("parent"))
		    	 {
		    	   str_role="Parent";
		    	   str_userid=tutorPrefs.getString("parentID", "");
		    		   }
				
				boolean registered = ServerUtilities.register(mContext,"Android", str_role, regId, str_userid, udid);
			
				Log.e("registration after complete","complete");
				if (!registered) {
					GCMRegistrar.unregister(mContext);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				Log.e("server utilty registration complete","on post");

				}
		}
		
		 private final static BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					Log.e("on receive", "broad cast receiver");
				
				}
			};
			
	}