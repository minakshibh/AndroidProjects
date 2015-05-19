package com.rapidride.util;



import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.rapidride.R;

public class ServerUtilities extends Activity {
	 private static final int MAX_ATTEMPTS = 5;
	       private static final int BACKOFF_MILLI_SECONDS = 1000;
	       private static final Random random = new Random();
	       private static String TAG = "** pushAndroidActivity **";
	       static SharedPreferences prefs;
	    /**
	     * Register this account/device pair within the server.
	     *
	     * @return whether the registration succeeded or not.
	     */
	    public static boolean register(final Context context, final String role, final String driverId,final String riderId,final String deviceId,final String regId) {
	        Log.e(TAG, "registering device (regId = " + regId + ")");
	        //String serverUrl = SERVER_URL + "/register";
	        prefs = context.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	        Editor ed=prefs.edit();
	        ed.putString("regid", regId);
	        ed.commit();
	        String serverUrl = Utility.SERVER_URL;
	//        HttpParams httpParameters = new BasicHttpParams();
//	        HttpClient client = new DefaultHttpClient(httpParameters);
//			HttpPost httpost = new HttpPost(serverUrl);//Url_Address.url_Login);
//			JSONObject json = new JSONObject();
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("Role", role);
	        params.put("DriverId", driverId);
	        params.put("RiderId", riderId);
	        params.put("DeviceUDId", deviceId);
	        params.put("TokenID", regId);
	        params.put("Trigger", "android");
	        
	        
	        
	        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
	        // Once GCM returns a registration id, we need to register it in the
	        // demo server. As the server might be down, we will retry it a couple
	        // times.
	        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
	            Log.d(TAG, "Attempt #" + i + " to register");
	            try {
	            	Utility.displayMessage(context, context.getString(
	                        R.string.server_registering, i, MAX_ATTEMPTS));
	                post(serverUrl, params);
	                Log.e("post called","series 1");
	                GCMRegistrar.setRegisteredOnServer(context, true);
	                String message = context.getString(R.string.server_registered);
	                Utility.displayMessage(context, message);
	                
	                Log.e("post called","series 2");
	                return true;
	            } catch (IOException e) {
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
	        String message = context.getString(R.string.server_register_error,
	                MAX_ATTEMPTS);
	        Utility.displayMessage(context, message);
	        return false;
	    }

	    /**
	     * Unregister this account/device pair within the server.
	     */
	    static void _unregister(final Context context, final String regId) {
	        Log.i(TAG, "unregistering device (regId = " + regId + ")");
	        String serverUrl = Utility.SERVER_URL + "/unregister";
	        Map<String, String> params = new HashMap<String, String>();
	        params.put("regId", regId);
	        try {
	            post(serverUrl, params);
	            GCMRegistrar.setRegisteredOnServer(context, false);
	            String message = context.getString(R.string.server_unregistered);
	            Utility.displayMessage(context, message);
	        } catch (IOException e) {
	            // At this point the device is unregistered from GCM, but still
	            // registered in the server.
	            // We could try to unregister again, but it is not necessary:
	            // if the server tries to send a message to the device, it will get
	            // a "NotRegistered" error message and should unregister the device.
	            String message = context.getString(R.string.server_unregister_error,
	                    e.getMessage());
	            Utility.displayMessage(context, message);
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
	            conn.setRequestProperty("Content-Type",
	                    "application/x-www-form-urlencoded;charset=UTF-8");
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
	}