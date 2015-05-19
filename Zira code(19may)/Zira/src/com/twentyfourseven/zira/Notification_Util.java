package com.twentyfourseven.zira;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;

public class Notification_Util {

/*public static final String SERVER_URL = "http://websquare-001-site1.smarterasp.net/zira247/zira247.asmx/RegisterDevice";*/
	public static final String SERVER_URL = "RegisterDevice";
//"http://yexno.com/yexnoservice/service1.asmx/sendpushmessage";

/**
* Google API project id  is your SENDER_ID and it is use to register with GCM.226673393217
*/
//public static final String SENDER_ID = "394559605773";
//public static final String SENDER_ID = "900511462515";
public static final String SENDER_ID = "377277580383";
//public static final String SENDER_ID = "1040712214255";//"app id";
public static final String DISPLAY_MESSAGE_ACTION = "com.twentyfourseven.zira.DISPLAY_MESSAGE";

/**
* Intent's extra that contains the message to be displayed.
*/
public static final String EXTRA_MESSAGE = "custom message";
public static void displayMessage(Context context, String message) {
Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
intent.putExtra(EXTRA_MESSAGE, message);
context.sendBroadcast(intent);
}
private static ArrayList<String> arraylist_notification;
public static ArrayList<String> arraylist_notification() 
{
     if(arraylist_notification== null)
     {
    	 arraylist_notification = new ArrayList<String>();
     	}
      return arraylist_notification;
     }
}
