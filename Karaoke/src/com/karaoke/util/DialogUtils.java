package com.karaoke.util;

import android.app.Activity;
import android.app.AlertDialog;

public class DialogUtils {	

	public static void alert(Activity mcontext,String message) {
		AlertDialog.Builder alertdialog = new AlertDialog.Builder(mcontext);	
		alertdialog.setTitle("Zoom-Karaoke");
		alertdialog.setMessage(message);
		alertdialog.setNeutralButton("OK", null);		
		alertdialog.create().show();
	}

}
