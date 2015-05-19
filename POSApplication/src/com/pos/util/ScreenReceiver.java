package com.pos.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class ScreenReceiver extends BroadcastReceiver {
    
  // THANKS JASON
  public static boolean wasScreenOn = false;

  @Override
  public void onReceive(Context context, Intent intent) {
	  PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
          // DO WHATEVER YOU NEED TO DO HERE
    	   
    	 System.out.println("Off screen");
          wasScreenOn =  true;
      } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
          // AND DO WHATEVER YOU NEED TO DO HERE
          wasScreenOn = false;
          System.out.println("On screen");
      }
  }

}