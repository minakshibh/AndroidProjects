package com.zira.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.twentyfourseven.zira.R;


public class Util {

	static 	Context context;
	
	static	public void alertMessage(Context ctx,String str)
	{
		context=ctx;
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Zira 24/7");
		alert.setMessage(str);
		alert.setPositiveButton("OK", null);
		alert.show();
	}
	
	static public boolean isNetworkAvailable(Context mCtx) {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	public static String showImage(String path, ImageView imageView){
		int targetW = 400;
		 int targetH = 400;

		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(path, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;

		   Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
		    
//	        bitmap = (Bitmap) data.getExtras().get("data"); 
		   imageView.setImageBitmap(bitmap);
	   	
	        ByteArrayOutputStream bao = new ByteArrayOutputStream(); //convert images into base64
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bao);
	        byte[] ba = bao.toByteArray();
	        String encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
	        
	        return encodedImage;
	}
	
	static public  void hideKeyboard(Context cxt) {
		context=cxt;
	    InputMethodManager inputManager = (InputMethodManager) cxt.getSystemService(Context.INPUT_METHOD_SERVICE);

	    // check if no view has focus:
	    View view = ((Activity) cxt).getCurrentFocus();
	    if (view != null) {
	        inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }
	
	public static String createImageFile() {
		try{
		    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		    String imageFileName = "Zira_" + timeStamp;
	
		    String mCurrentPhotoPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+imageFileName+".jpg";
		    return mCurrentPhotoPath;
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	
	public static String getResponseFromUrl(String functionName, List<NameValuePair> param, Context context){
		String responseString = "";
		
		try {
			/*DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost request = new HttpPost(context.getResources().getString(R.string.baseUrl)+"/"+functionName);
	        request.setEntity(new UrlEncodedFormEntity(param));
	        HttpResponse response = httpClient.execute(request);
	        
	        HttpEntity httpEntity = response.getEntity();
	        responseString = EntityUtils.toString(httpEntity);*/
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
	         HttpPost request = new HttpPost(context.getResources().getString(R.string.baseUrl)+"/"+functionName);
	         request.setHeader("Accept", "application/json");
	            request.setHeader("Content-type", "application/json");
	            
	         JSONObject JSONObjectData = new JSONObject();

	         for (NameValuePair nameValuePair : param) {
	             try {
	                 JSONObjectData.put(nameValuePair.getName(), nameValuePair.getValue());
	             } catch (JSONException e) {

	             }
	         }
	         
	         String st = JSONObjectData.toString();
	         Log.e("tag", st);
	         StringEntity st_entity =  new StringEntity(st);
	         request.setEntity(st_entity);
	         HttpResponse response = httpClient.execute(request);
	         
	         HttpEntity httpEntity = response.getEntity();
	         responseString = EntityUtils.toString(httpEntity);
			
//	        Log.e(functionName, responseString);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("sockettttttt"+e);
		}
		
		return responseString;
	}
	 private static ArrayList<String> arraylist_country;
	    public static ArrayList<String> arraylist_country() 
	    {
		     if(arraylist_country== null)
		     {
		    	 arraylist_country = new ArrayList<String>();
		     	}
		      return arraylist_country;
	     	}
	    private static ArrayList<String> arraylist_countryid;
	    public static ArrayList<String> arraylist_countryid() 
	    {
		     if(arraylist_countryid== null)
		     {
		    	 arraylist_countryid = new ArrayList<String>();
		     	}
		      return arraylist_countryid;
	     	}
	    
	    static ArrayList<String> arraylist_state;
	    public static ArrayList<String> arraylist_state() 
	    {
		     if(arraylist_state== null)
		     {
		    	 arraylist_state = new ArrayList<String>();
		     	}
		      return arraylist_state;
	     	}
	    static ArrayList<String> arraylist_stateid;
	    public static ArrayList<String> arraylist_stateid() 
	    {
		     if(arraylist_stateid== null)
		     {
		    	 arraylist_stateid = new ArrayList<String>();
		     	}
		      return arraylist_stateid;
	     	}
	    static ArrayList<String> arraylist_modal;
	    public static ArrayList<String> arraylist_modal() 
	    {
		     if(arraylist_modal== null)
		     {
		    	 arraylist_modal = new ArrayList<String>();
		     	}
		      return arraylist_modal;
	     	}
	   
	    static ArrayList<String> arraylist_modelid;
	    public static ArrayList<String> arraylist_modelid() 
	    {
		     if(arraylist_modelid== null)
		     {
		    	 arraylist_modelid = new ArrayList<String>();
		     	}
		      return arraylist_modelid;
	     	}
}
