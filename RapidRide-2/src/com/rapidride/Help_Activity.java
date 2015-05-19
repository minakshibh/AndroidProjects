package com.rapidride;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Help_Activity extends Activity{
	
	RelativeLayout rl_riderview;
	Button btn_call,btn_email,btn_term,btn_policy,btn_driverbecome;
	int value=0;
	SharedPreferences prefs;
	
	 protected void onCreate(Bundle savedInstanceState) {
	    	requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.help_activity);
	        
	    rl_riderview=(RelativeLayout)findViewById(R.id.rl_riderview);
	    btn_call=(Button)findViewById(R.id.button_callus);
	    btn_email=(Button)findViewById(R.id.button_email);
	    btn_term=(Button)findViewById(R.id.button_terms);
	    btn_policy=(Button)findViewById(R.id.button_privacypolicy);
	    btn_driverbecome=(Button)findViewById(R.id.button_becomedriver);
	   
	    prefs = Help_Activity.this.getSharedPreferences("RapidRide", MODE_PRIVATE);
	    if(!(getIntent().getStringExtra("rider")==null))
	    	{
	    	rl_riderview.setVisibility(View.VISIBLE);
	    	value=1;
	    		}
	    if(!(getIntent().getStringExtra("driver")==null))
	    {
	    	rl_riderview.setVisibility(View.GONE);
	    	value=2;
	    	}
	    
	    btn_call.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(value==1)
				{	/***rider call us***/
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:8666526420"));
					startActivity(callIntent);
					}
				if(value==2)
				{	/***driver call us***/
					Intent callIntent = new Intent(Intent.ACTION_CALL);
					callIntent.setData(Uri.parse("tel:7608886421"));
					startActivity(callIntent);	
					}
				
			}
		});
	    btn_email.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/***rider email sending**/
				if(value==1)
				{
					sendEmail("help@riderapid.com");
					}
				/***driver email sending***/
				if(value==2)
				{
					sendEmail("driverhelp@riderapid.com");
					
					}
				
			}
		});	
	    /***rider and driver same privacy***/
	    btn_policy.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			
				Intent i=new Intent(Help_Activity.this,Help_WebView.class);
				i.putExtra("url", "http://www.riderapid.com/app/privacy/");
				
				startActivity(i);
					
							
			}
		});
	    btn_term.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				/***rider  term services***/
				if(value==1)
				{
					System.err.println("rider term");
					Intent i=new Intent(Help_Activity.this,Help_WebView.class);
					i.putExtra("url", "http://www.riderapid.com/app/terms/");
					
					startActivity(i);
					}
				/***driver term services***/
				if(value==2)
				{	System.err.println("diver term");
					Intent i=new Intent(Help_Activity.this,Help_WebView.class);
					i.putExtra("url", "http://www.riderapid.com/app/terms-driver/");
					
					startActivity(i);
					}
				
			}
		});/***driver become a***/
	    btn_driverbecome.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				Intent i=new Intent(Help_Activity.this,Help_WebView.class);
				i.putExtra("url", "http://appba.riderapid.com/mds/?riderid="+prefs.getString("userid", null));
				
				startActivity(i);
			}
		});
}
	 
	 /**********************End main function*********************************/
	 
	 
	   protected void sendEmail(String str) {
		      Log.i("Send email", "");

		      String[] TO = {str};
		      String[] CC = {""};
		      Intent emailIntent = new Intent(Intent.ACTION_SEND);
		      emailIntent.setData(Uri.parse("mailto:"));
		      emailIntent.setType("text/plain");
		      emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		      emailIntent.putExtra(Intent.EXTRA_CC, CC);
		      emailIntent.putExtra(Intent.EXTRA_SUBJECT, "test");
		      emailIntent.putExtra(Intent.EXTRA_TEXT, "");

		      try {
		         //startActivity(Intent.createChooser(emailIntent, "Send mail..."));
		    	 startActivity(emailIntent); 
		         finish();
		         Log.i("Finished sending email...", "");
		      } catch (android.content.ActivityNotFoundException ex) {
		         Toast.makeText(Help_Activity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
		      }
		   }
	   
	   /**keyboard back button*///
	public void onBackPressed() {

		 if(value==1)
			{
			Intent i=new Intent(Help_Activity.this,MapView_Activity.class);
			i.putExtra("help", "value");
			finish();
			startActivity(i);
				}
			if(value==2)
			{
				Intent i=new Intent(Help_Activity.this,DriverView_Activity.class);
				finish();
				startActivity(i);
				}
			
		super.onBackPressed();
	}
}