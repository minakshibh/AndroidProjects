package com.rapidride.util;

import java.util.ArrayList;

import com.rapidride.RideInfoView_Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;


public class Utility  {
	static 	Context context;
	public static final String SERVER_URL = "http://mapp.riderapid.com/UserRegisteration.asmx/RegisterDevice";
		//	"http://112.196.24.205:502/Service.asmx/RegisterDeviceId";
	
//	"http://yexno.com/yexnoservice/service1.asmx/sendpushmessage";

/**
* Google API project id  is your SENDER_ID and it is use to register with GCM.
*/
//static final String SENDER_ID = "864084821056";
public static final String SENDER_ID = "1040712214255";//"app id";
public static final String DISPLAY_MESSAGE_ACTION = "com.rapidride.DISPLAY_MESSAGE";

/**
* Intent's extra that contains the message to be displayed.
*/
public static final String EXTRA_MESSAGE = "custom message";
public static void displayMessage(Context context, String message) {
	Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
	intent.putExtra(EXTRA_MESSAGE, message);
	context.sendBroadcast(intent);
}
static	public void alertMessage(Context ctx,String str)
	{
		context=ctx;
		AlertDialog.Builder alert = new AlertDialog.Builder(Utility.context);
		alert.setTitle("Rapid");
		alert.setMessage(str);
		alert.setPositiveButton("Ok", null);
		if(!((Activity) Utility.context).isFinishing())
		{
			alert.show();
			}
		
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
	  private static ArrayList<String> arraylist_promo;
	    public static ArrayList<String> arrayListPromo() 
	    {
		     if(arraylist_promo== null)
		     {
		    	 arraylist_promo = new ArrayList<String>();
		     	}
		      return arraylist_promo;
	     	}
	    
	   // array_list_latlng
	    private static ArrayList<Float> array_list_latlng;
	    public static ArrayList<Float> array_list_latlng() 
	    {
		     if(array_list_latlng== null)
		     {
		    	 array_list_latlng = new ArrayList<Float>();
		     	}
		     return array_list_latlng;
	     	}
	    
//	    //arraylist_longitude
//	    private static ArrayList<String> arraylist_longitude;
//	    public static ArrayList<String> arraylist_longitude() 
//	    {
//		     if(arraylist_longitude== null)
//		     {
//		    	 arraylist_longitude = new ArrayList<String>();
//		     	}
//		      return arraylist_longitude;
//		     }
	    //arraylist_drivername
	    private static ArrayList<String> arraylist_drivername;
	    public static ArrayList<String> arraylist_drivername() 
	    {
		     if(arraylist_drivername== null)
		     {
		    	 arraylist_drivername = new ArrayList<String>();
		     	}
		      return arraylist_drivername;
		     }

	    //arraylist_BeanDriver
	    private static ArrayList<BeanDriver> arraylist_driverInfo;
	    public static ArrayList<BeanDriver> arraylist_driverInfo() 
	    {
		     if(arraylist_driverInfo== null)
		     {
		    	 arraylist_driverInfo = new ArrayList<BeanDriver>();
		     	}
		      return arraylist_driverInfo;
		     }
	    
	    //arraylist_NotificationMessage
	    private static ArrayList<String> arraylist_notification;
	    public static ArrayList<String> arraylist_notification() 
	    {
		     if(arraylist_notification== null)
		     {
		    	 arraylist_notification = new ArrayList<String>();
		     	}
		      return arraylist_notification;
		     }
	    
		  public static boolean isConnectingToInternet(Context context){
		        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		       
		        if (connectivity != null) 
		          {
		              NetworkInfo[] info = connectivity.getAllNetworkInfo();
		              if (info != null) 
		                  for (int i = 0; i < info.length; i++) 
		                  {
		                	
		                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
		                      {
		                    	  
		                          return true;
		                      }
		                  }
		          }
		          return false;
		    } 
		  
		  

}
