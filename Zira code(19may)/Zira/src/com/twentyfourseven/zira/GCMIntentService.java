package com.twentyfourseven.zira;

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
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.twentyfourseven.zira.R;
import com.zira.notification.DriverNotifications;
import com.zira.notification.RiderNotification;
import com.zira.util.SingleTon;


public class GCMIntentService extends GCMBaseIntentService{
	static SharedPreferences prefs;
	MediaPlayer mp;
	//private int count=0;
	private static final String TAG = "GCMIntentService";
	public GCMIntentService() {
		super(Notification_Util.SENDER_ID);
	}
	int flag = 0;
	
	protected void onRegistered(Context context, String registrationId) {
		Log.e(TAG, "Device registered: regId = " + registrationId);
		Notification_Util.displayMessage(context, getString(R.string.gcm_registered));
		TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		String udid = tManager.getDeviceId();
		prefs = this.getSharedPreferences("Zira", MODE_PRIVATE); 
		String driverid=prefs.getString("riderid", null);
		String riderid=prefs.getString("riderid", null);

		ServerUtilities.register(context, "rider",driverid,riderid,udid, registrationId);
	}


	protected void onUnregistered(Context context, String registrationId) {
		Log.e(TAG, "Device unregistered");
		Notification_Util.displayMessage(context, getString(R.string.gcm_unregistered));
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			//	             ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(TAG, "Ignoring unregister callback");
		}
	}


	protected void onMessage(final Context context, Intent intent) {
		Log.e("Zira: On message", "Received message");
		prefs = this.getSharedPreferences("Zira", MODE_PRIVATE); 
		String riderName = null;
		String sentMessage= null;
		String DriverName=null,driverMessage=null,driverImage=null,driverTripId=null,driverVehicleImage=null,driverLastData=null;		

		//String message = getString(R.string.gcm_message);
		String message = intent.getStringExtra("message");
		Log.e("message", message);
		
		Notification_Util.displayMessage(context, message);

		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		Log.d("current task :", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClass().getSimpleName());
		ComponentName componentInfo = taskInfo.get(0).topActivity;
		if(componentInfo.getPackageName().equalsIgnoreCase("com.twentyfourseven.zira")){

			Log.e("inside application","app running");
			String Message=message;

			if(prefs.getString("mode", "").equalsIgnoreCase("rider")){

				if(message.contains("arrived")){

					String[] values = Message.split("@");
					DriverName=values[0];
					driverMessage=values[1];
					driverImage=values[2];
					driverTripId=values[3];	
					SingleTon.getInstance().setDriverName(DriverName);
					SingleTon.getInstance().setDriverMessage(driverMessage);
					SingleTon.getInstance().setDriverImage(driverImage);
					SingleTon.getInstance().setDriverTripId(driverTripId);

					Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(launchDialog);
				}

				else if(message.contains(" has ended the ride")){
				
					String[] values = Message.split("@");
					DriverName=values[0];
					driverMessage=values[1];
					driverImage=values[2];
					driverTripId=values[3];	
					SingleTon.getInstance().setDriverName(DriverName);
					SingleTon.getInstance().setDriverMessage(driverMessage);
					SingleTon.getInstance().setDriverImage(driverImage);
					SingleTon.getInstance().setDriverTripId(driverTripId);

					Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(launchDialog);
				}
				else if(message.contains("has begun the trip")){

					String[] values = Message.split("@");
					DriverName=values[0];
					driverMessage=values[1];
					driverImage=values[2];
					driverTripId=values[3];	
					SingleTon.getInstance().setDriverName(DriverName);
					SingleTon.getInstance().setDriverMessage(driverMessage);
					SingleTon.getInstance().setDriverImage(driverImage);
					SingleTon.getInstance().setDriverTripId(driverTripId);

					/*Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(launchDialog);
					*/
					Intent launchDialog = new Intent(this.getApplicationContext(), RiderRideView.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(launchDialog);
				}

				else if(message.contains("has cancelled the ride and minimum cancellation charges will be deducted from your account")){

					SingleTon.getInstance().setDriverMessage("Your driver has cancelled the ride.");// and minimum cancellation charges will be deducted from your account");

					Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(launchDialog);
				}


				else if(message.contains("arriving")){
					VehicleSearchActivity.ncheck=1;
					String[] values = Message.split("@");
					DriverName=values[0];
					driverMessage=values[1];
					driverImage=values[2];
					driverTripId=values[3];			
					driverVehicleImage=values[4];			
					driverLastData=values[5];	

					SingleTon.getInstance().setDriverName(DriverName);
					SingleTon.getInstance().setDriverMessage(driverMessage);
					SingleTon.getInstance().setDriverImage(driverImage);
					SingleTon.getInstance().setDriverTripId(driverTripId);
					SingleTon.getInstance().setDriverVehicleImage(driverVehicleImage);
					SingleTon.getInstance().setDriverLastData(driverLastData);

					Intent launchDialog = new Intent(this.getApplicationContext(), RiderNotification.class);
					launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

					startActivity(launchDialog);
				}
			}

			else if(prefs.getString("mode", "").equalsIgnoreCase("driver")){
				if(message.contains("sent")){
					
						prefs = context.getSharedPreferences("Zira", MODE_PRIVATE);
						flag = prefs.getInt("not_flag", 0);
						if(flag == 0){
							Editor editor = prefs.edit();
							editor.putInt("not_flag", 1);
							editor.commit();
							
							DriverModeActivity.forCheking++;
							
							String[] values = Message.split("@");
							riderName=values[0];
							sentMessage=values[1];
							String image=values[2];
							String tripID=values[3];
							SingleTon.getInstance().setSentValue(String.valueOf(DriverModeActivity.forCheking));
							SingleTon.getInstance().setReceivednotificationName(riderName);
							SingleTon.getInstance().setReceivednotificationSentMessage(sentMessage);
							SingleTon.getInstance().setReceivednotificationImage(image);
							SingleTon.getInstance().setReceivednotificationTripID(tripID);
		
							
							Intent launchDialog = new Intent(this.getApplicationContext(), DriverNotifications.class);
							launchDialog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
							startActivity(launchDialog);
						}
					}
				}
		}else {
			////////////////////////
			Log.e("outside application","app running");
			String changeMessage=message.replace("@", "   ");
			
			//String Notifiymessage=riderName+" "+sentMessage;
			//generateNotification(context, Notifiymessage);
			generateNotification(context, changeMessage);
			////////////////////////
		}
	}

	protected void onDeletedMessages(Context context, int total) {
		Log.e(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		Notification_Util.displayMessage(context, message);
		// notifies user
		generateNotification(context, message);
	}

	public void onError(Context context, String errorId) {
		Log.e(TAG, "Received error: " + errorId);
		Notification_Util.displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.e(TAG, "Received recoverable error: " + errorId);
		Notification_Util.displayMessage(context, getString(R.string.gcm_recoverable_error,  errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**Issues a notification to inform the user that server has sent a message. */
	@SuppressWarnings("deprecation")
	private static void generateNotification(Context context, String message) {

		Intent notificationIntent = null;
		int icon = R.drawable.app_icon;
		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);

		String title = context.getString(R.string.app_name);
		System.err.println("title="+title);
		System.err.println("message="+message);
		System.err.println("message="+message);
		System.err.println("message="+message);
		System.err.println("message="+message);
		System.err.println("message="+message);

		Log.d("","messagemessagemessage"+message);
		Log.d("","messagemessagemessage"+message);
		Log.d("","messagemessagemessage"+message);
		Log.d("","messagemessagemessage"+message);
		///////////////////////////////
		String changeMessage=message.replace("   ", "@");
		
		System.err.println("changeMessage="+changeMessage);
		//         
		prefs = context.getSharedPreferences("Zira", MODE_PRIVATE);
		String Mode=prefs.getString("mode", null);
		Log.i("tag", "Mode:"+Mode);
		//      
		if(Mode!=null)
		{
			if(Mode.equals("driver"))
			{
				notificationIntent = new Intent(context, DriverModeActivity.class);
				Editor e=prefs.edit();
				e.putString("message", changeMessage);////////////////
				e.commit();
				Log.i("tag", "Send Message on driver mode:"+changeMessage);
			}
			else if(Mode.equals("rider"))
			{
				notificationIntent = new Intent(context, VehicleSearchActivity.class);
				Editor e=prefs.edit();
				e.putString("message", changeMessage);///////////////
				e.commit();

				Log.i("tag", "Send Message on vehicle search:"+changeMessage);
			}

		}
		else
		{
			notificationIntent = new Intent(context, LoginActivity.class);
			}
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, changeMessage, intent);/////////////////
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.icon = R.drawable.app_icon;

		Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notification.sound = notificationSound;
	
		if(prefs.getString("mode", "").equals("driver"))
		{	
			if(changeMessage.contains("sent"))///////////////
			{
				notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.newsound);
				notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
			}
		}
		else if(prefs.getString("mode", "").equals("rider")){

			if(changeMessage.contains("has ended the ride"))////////////
			{
				notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.endride);
				notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
			}	
		}


		Random r = new Random();
		int random = r.nextInt(100);
		notificationManager.notify(changeMessage.length() + random, notification);	 ///////////////        
	}
	public void onDestroy() {
		mp = new MediaPlayer();
		mp.stop();
		super.onDestroy();

	}

}
