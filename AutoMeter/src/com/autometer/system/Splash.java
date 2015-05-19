package com.autometer.system;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class Splash extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);

		Handler handler = new Handler();
	    handler.postDelayed(new Runnable(){
	           public void run() {
	                Intent myIntent = new Intent(getApplicationContext(), ChooseDestination.class);
	                myIntent.putExtra("trigger", "none");
	                startActivity(myIntent);     
	                finish();
	           }
	    }, 3000);
	}
}