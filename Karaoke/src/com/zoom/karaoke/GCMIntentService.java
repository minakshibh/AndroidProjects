package com.zoom.karaoke;

import java.util.Random;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.zoom.karaoke.R;

public class GCMIntentService extends GCMBaseIntentService{

	  private static final String TAG = "GCMIntentService";

	      public GCMIntentService() {
	         super(CommonUtilities.SENDER_ID);
	     }

	      @Override
	     protected void onRegistered(Context context, String registrationId) {
	         Log.e(TAG, "Device registered: regId = " + registrationId);
	         CommonUtilities.displayMessage(context, getString(R.string.gcm_registered));
	         TelephonyManager tManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	         String uuid = tManager.getDeviceId();
	         
	         ServerUtilities.register(context, uuid, registrationId);
	     }

	      @Override
	     protected void onUnregistered(Context context, String registrationId) {
	         Log.i(TAG, "Device unregistered");
	         CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
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
	         Log.i(TAG, "Received message");
	         //String message = getString(R.string.gcm_message);
	         String message = intent.getStringExtra("message");
	         CommonUtilities.displayMessage(context, message);
	         // notifies user
	         generateNotification(context, message);
	     }

	      @Override
	     protected void onDeletedMessages(Context context, int total) {
	         Log.i(TAG, "Received deleted messages notification");
	         String message = getString(R.string.gcm_deleted, total);
	         CommonUtilities.displayMessage(context, message);
	         // notifies user
	         generateNotification(context, message);
	     }

	      @Override
	     public void onError(Context context, String errorId) {
	         Log.i(TAG, "Received error: " + errorId);
	         CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
	     }

	      @Override
	     protected boolean onRecoverableError(Context context, String errorId) {
	         // log message
	         Log.i(TAG, "Received recoverable error: " + errorId);
	         CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
	                 errorId));
	         return super.onRecoverableError(context, errorId);
	     }

	      /**
	      * Issues a notification to inform the user that server has sent a message.
	      */
	     private static void generateNotification(Context context, String message) {
	         int icon = R.drawable.ic_launcher;
	         
	         long when = System.currentTimeMillis();
	         NotificationManager notificationManager = (NotificationManager)
	                 context.getSystemService(Context.NOTIFICATION_SERVICE);
	         Notification notification = new Notification(icon, message, when);
	         String title = context.getString(R.string.app_name);
	         Intent notificationIntent = new Intent(context, SplashScreen.class);
	         // set intent so it does not start a new activity
	         notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	                 Intent.FLAG_ACTIVITY_SINGLE_TOP);
	         PendingIntent intent =
	                 PendingIntent.getActivity(context, 0, notificationIntent, 0);
	         notification.setLatestEventInfo(context, title, message, intent);
	         notification.flags |= Notification.FLAG_AUTO_CANCEL;
	         notification.icon = R.drawable.ic_launcher;
	         
	         Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	         notification.sound = notificationSound;
	         
//	         notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification);
	         notification.defaults = Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE;
	         
	         Random r = new Random();
	         int random = r.nextInt(100);
	         
	         notificationManager.notify(message.length() + random, notification);	         
	     }
}
