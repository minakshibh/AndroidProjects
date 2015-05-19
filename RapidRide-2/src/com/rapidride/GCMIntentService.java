package com.rapidride;

import java.util.List;
import java.util.Random;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.rapidride.util.ServerUtilities;
import com.rapidride.util.Utility;


public class GCMIntentService extends GCMBaseIntentService{
	static SharedPreferences prefs;
	 MediaPlayer mp;
private static final String TAG = "GCMIntentService";
	  public GCMIntentService() {
	         super(Utility.SENDER_ID);
	     }

	     @Override
	     protected void onRegistered(Context context, String registrationId) {
	         Log.e(TAG, "Device registered: regId = " + registrationId);
	         Utility.displayMessage(context, getString(R.string.gcm_registered));
	         TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	         String uuid = tManager.getDeviceId();
	    	 prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	    	 String driverid=prefs.getString("driverid", null);
	    	 String riderid=prefs.getString("userid", null);
	    	 
	         ServerUtilities.register(context, "rider",driverid,riderid,uuid, registrationId);
	     }

	      @Override
	     protected void onUnregistered(Context context, String registrationId) {
	         Log.e(TAG, "Device unregistered");
	         Utility.displayMessage(context, getString(R.string.gcm_unregistered));
	         if (GCMRegistrar.isRegisteredOnServer(context)) {
//	             ServerUtilities.unregister(context, registrationId);
	         } else {
	             // This callback results from the call to unregister made on
	             // ServerUtilities when the registration to the server failed.
	             Log.i(TAG, "Ignoring unregister callback");
	         }
	     }

	      @Override
	     protected void onMessage(Context context, Intent intent) {
	         Log.e(TAG, "Received message");
	         //String message = getString(R.string.gcm_message);
	         String message = intent.getStringExtra("message");
	         Utility.displayMessage(context, message);
	         
	         ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	         List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
	         Log.d("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClass().getSimpleName());
	         ComponentName componentInfo = taskInfo.get(0).topActivity;
	         if(componentInfo.getPackageName().equalsIgnoreCase("com.rapidride")){
////	            /*ShowNotificationPopUp popUp = new ShowNotificationPopUp(context);
////	            popUp.showNotification();*/
////	        	 
	        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	        	String mode=prefs.getString("mode", "");
	        	Log.e("tag", mode);
		if(prefs.getString("mode", "").equals("driver"))
		{
	        	String Message=message;
	        	boolean checkCancalRide=Message.contains("cancelled the ride");
	        if(checkCancalRide==true)
	        	{
	        		Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotification.class);
		        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
			    	Editor e=prefs.edit();
			    	e.putString("CancelMessage",Message);
			    	e.commit();
			    	startActivity(launchDialog);
			    	}
	        else if(message.contains("Turn on"))
	        {
	        	String[] values = Message.split(":");
	        	String color=values[1];
	        	
        	
	              Intent i=new Intent(this.getApplicationContext(),Driver_ColorView.class);
	              i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        		  i.putExtra("color", color);
        		  startActivity(i);
	        	
	        	}
	        ///////////////////////////////////////////////// rider side notification////////////////////////
	        else if(message.contains("Since no near by driver"))
	        {	
	        	Log.e("Since no near by driver", "Since no near by driver");
	        	}
	        else if(message.contains("@"))
	        {	
	        	Log.e("@ notification", "@");
	        	}
	        else if(message.contains("will reach"))
	        {	
	        	Log.e("will reach", "will reach");
	        	}  
	        else if(message.contains("been ended"))
	        {	
	        	Log.e("been ended", "been ended");
	        	}
	        else if(message.contains("started"))
	        {	Log.e("started started", "started started");
	        	
	        	}
	        else if(message.contains("arrived"))
	        {	Log.e("arrived arrived", "arrived arrived");
	        	  	}
	        ///////////////////////////////////////////////// rider side notification////////////////////////
	        else{
//	        	  try{
//       		  
//	        		  mp = new MediaPlayer();
//	        		  mp = MediaPlayer.create(this, R.raw.request);
//	        		  mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
//	        		  mp.start();
//	        	  }
//	        	  catch(Exception e){
//	        	  }
	        	 
		        	boolean check=Message.contains("VIP");
					Log.e("tag", Message);
		        	if(check==true){
						String[] values = Message.split(";");
						String sendbyRequest=values[0].split("and offering price")[0];
						String offerPrice=values[0].split("and offering price")[1];
						String currentLoc = values[1].split("Current Location:")[1];
						//String distance = values[2].split("Distance:")[1];
						//String ETA = values[3].split("ETA:")[1];
						String TripId = values[2].split("TripId:")[1];
						String Rating = values[3].split("Rating:")[1];
						String Handicap = values[4].split("Handicap:")[1];
						String PickUpDateTime = values[5].split("PickUpDateTime:")[1];
						String PrefferedVechileType = values[6].split("PrefferedVechileType:")[1];
						String Image[] = values[7].trim().split(" ");
						String SpiltImage=Image[1];
						String temprequestType=values[7].split("\\(")[1];
						String requestType=temprequestType.split("\\)")[0];
						Log.i("tag", "RequestType:"+requestType);
						String[] requestType_Trim=requestType.trim().split(" ");
						String atLast_ReqType=requestType_Trim[0];
						
						Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotification.class);
			        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        	launchDialog.putExtra("RequestSendBy", sendbyRequest);
			        	launchDialog.putExtra("OfferedPrice", offerPrice);
			        	launchDialog.putExtra("CurrentLocation", currentLoc);
			        	launchDialog.putExtra("Destination", "");
			        	launchDialog.putExtra("Distance", "");
			        	launchDialog.putExtra("ETA", "");
			        	launchDialog.putExtra("TripId", TripId);
			        	launchDialog.putExtra("Rating", Rating);
			        	launchDialog.putExtra("Handicap", Handicap);
			        	launchDialog.putExtra("PickUpDateTime", PickUpDateTime);
			        	launchDialog.putExtra("PrefferedVechileType", PrefferedVechileType);
			        	launchDialog.putExtra("Image", SpiltImage);
			        	launchDialog.putExtra("RequestType",atLast_ReqType);
			        	startActivity(launchDialog);
			        	
						}
					else
					{
						try{
							String[] values = Message.split(";");
							String sendbyRequest=values[0].split("to")[0];
							String TempDestination=values[0].split("and offering price")[0];
							String destinations=TempDestination.split("requesting ride to")[1];
							String offeringPrice=values[0].split("and offering price")[1];
							String getOfferPrice=offeringPrice.split("Current Location:")[0];
							String sendBy = values[0].split("Current Location:")[0];
							String currentLoc = values[0].split("Current Location:")[1];
							String distance = values[1].split("Distance:")[1];
							String ETA = values[2].split("ETA:")[1];
							String TripId = values[3].split("TripId:")[1];
							String Rating = values[4].split("Rating:")[1];
							String Handicap = values[5].split("Handicap:")[1];
							String PickUpDateTime = values[6].split("PickUpDateTime:")[1];
							String PrefferedVechileType = values[7].split("PrefferedVechileType:")[1];
							String Image[] = values[8].trim().split(" ");
							String SpiltImage=Image[1];
							String temprequestType=values[8].split("\\(")[1];
							String requestType=temprequestType.split("\\)")[0];
							Log.i("tag", "RequestType:"+requestType);
							String[] requestType_Trim=requestType.trim().split(" ");
							String atLast_ReqType=requestType_Trim[0];
			//	        	 
				        	Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotification.class);
				        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	launchDialog.putExtra("RequestSendBy", sendbyRequest);
				        	launchDialog.putExtra("OfferedPrice", getOfferPrice);
				        	launchDialog.putExtra("CurrentLocation", currentLoc);
				        	launchDialog.putExtra("Destination", destinations);
				        	launchDialog.putExtra("Distance", distance);
				        	launchDialog.putExtra("ETA", ETA);
				        	launchDialog.putExtra("TripId", TripId);
				        	launchDialog.putExtra("Rating", Rating);
				        	launchDialog.putExtra("Handicap", Handicap);
				        	launchDialog.putExtra("PickUpDateTime", PickUpDateTime);
				        	launchDialog.putExtra("PrefferedVechileType", PrefferedVechileType);
				        	launchDialog.putExtra("Image", SpiltImage);
				        	launchDialog.putExtra("RequestType",atLast_ReqType);
				        	startActivity(launchDialog);
			//	        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
			//	        	Editor e=prefs.edit();
			//	        	e.putString("mode", "rider");
			//	        	e.commit();
							} 
					
		        	catch (Exception e) {
						// TODO: handle exception
					Log.e("requestnoification error", "In GCM intent class");
	          		}
			}}}
	       else{
	        	 
	        	String Message=message;
	        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
	        	Editor e1=prefs.edit();
	        	e1.putString("OnlyMessage", "no");
	        	e1.commit();
	        	boolean contain=Message.contains("@");
	        	
		        if(contain==true){
		        		
			     		String value[]=Message.split("@");
						String DriverName=value[0];
						String messages=value[1];
						String DriverImage=value[2];
						String[] temptripID=value[3].trim().split("\\(");
						String tempTripID=temptripID[0].split(":")[1];
						String tripID=tempTripID;
						
						Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotificationRiderSide.class);
			        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        	launchDialog.putExtra("DriverName", DriverName);
			        	launchDialog.putExtra("messages", messages);
			        	launchDialog.putExtra("DriverImage", DriverImage);
			        	launchDialog.putExtra("tripID", tripID);
			        	startActivity(launchDialog);
			        	}
	        	else
	        	{
			        boolean contains=Message.contains(";");
			        if(contains==true)
			        	{
			        		String getMessage[]=Message.split(";");
			        		String messages=getMessage[0];
			        	   	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
				        	Editor e=prefs.edit();
				        	e.putString("OnlyMessage", messages);
				        	e.commit();
				        	Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotificationRiderSide.class);
				        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				        	startActivity(launchDialog);
				        	}
				        else{
				        	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
				        	Editor e=prefs.edit();
				        	e.putString("OnlyMessage", Message);
				        	e.commit();
				        	if(message.contains("cancelled"))
				        	{
				        		Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotificationRiderSide.class);
					        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					        	startActivity(launchDialog);
				        		}
				        	else{
						        	Intent launchDialog = new Intent(this.getApplicationContext(), ShowNotificationRiderSide.class);
						        	launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						        	startActivity(launchDialog);
						        	}
				        	}
				        }
	       		}
	         }
    else
    	{
	        generateNotification(context, message);
	    	}
	      }

	      @Override
	     protected void onDeletedMessages(Context context, int total) {
	         Log.e(TAG, "Received deleted messages notification");
	         String message = getString(R.string.gcm_deleted, total);
	         Utility.displayMessage(context, message);
	         // notifies user
	         generateNotification(context, message);
	     }

	     public void onError(Context context, String errorId) {
	         Log.e(TAG, "Received error: " + errorId);
	         Utility.displayMessage(context, getString(R.string.gcm_error, errorId));
	     }

	     protected boolean onRecoverableError(Context context, String errorId) {
	         // log message
	         Log.e(TAG, "Received recoverable error: " + errorId);
	         Utility.displayMessage(context, getString(R.string.gcm_recoverable_error,  errorId));
	         return super.onRecoverableError(context, errorId);
	     }

	      /**
	      * Issues a notification to inform the user that server has sent a message.
	      */
	     @SuppressWarnings("deprecation")
		 private static void generateNotification(Context context, String message) {
	    	 Utility.arraylist_notification().clear();
	    	 int icon = R.drawable.app_icon;
	    	 Intent notificationIntent = null;
	       
	         long when = System.currentTimeMillis();
	         NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	         Notification notification = new Notification(icon, message, when);
	         String title = context.getString(R.string.app_name);
	         System.err.println("title="+title);
	         System.err.println("message="+message);
//	         
	         SharedPreferences prefs = context.getSharedPreferences("RapidRide", MODE_PRIVATE);
	         String Mode=prefs.getString("mode", null);
	         Log.i("tag", "Mode:"+Mode);
//	      
	         if(Mode!=null)
	         {
		         if(Mode.equals("driver"))
		         {
		        	 notificationIntent = new Intent(context, DriverView_Activity.class);
		         	 Editor e=prefs.edit();
			    	 e.putString("Message", message);
			    	 Log.i("tag", "Send Message on Driver View:"+message);
			    	 e.commit();
		         }
		         else if(Mode.equals("rider"))
		         {
		        	notificationIntent = new Intent(context, MapView_Activity.class);
		        	prefs = context.getSharedPreferences("RapidRide", MODE_PRIVATE); 
			    	Editor e=prefs.edit();
			    	e.putString("Message", message);
			    	e.commit();
		        	Log.i("tag", "Send Message on Map View:"+message);
		         }
	         
	         }
	         notificationIntent = new Intent(context, Splash_Activity.class);
	         notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
	         PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
	         notification.setLatestEventInfo(context, title, message, intent);
	         notification.flags |= Notification.FLAG_AUTO_CANCEL;
	         notification.icon = R.drawable.app_icon;
	         
	         Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	         notification.sound = notificationSound;
	                
	        	
	         if(message.contains("offering price"))
	         {
		         notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.request);
		         notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
		         }
		 		         
	         else if(message.contains("Please rate"))
		         {
		        	 notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.endride);
			         notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
		        	 }	
	        	
	        	
	       
	         Random r = new Random();
	         int random = r.nextInt(100);
	         Utility.arraylist_notification().add(message);
	         for(String val:Utility.arraylist_notification())
	         {
	        	 Log.d("tag", "Message:"+val);
	         }
	         
	         notificationManager.notify(message.length() + random, notification);	         
	     }
	      
}
