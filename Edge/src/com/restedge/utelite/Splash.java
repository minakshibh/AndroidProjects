package com.restedge.utelite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.restedge.utelite.R;

public class Splash extends Activity {

	TextView headerText, footerText;
	
	Handler handler=new Handler();
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		
		//Initialization of UI controls
		headerText =(TextView)findViewById(R.id.appNameHeader);
		//footerText =(TextView)findViewById(R.id.footertext);
		
		Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/TrajanProRegular.ttf");
		headerText.setTypeface(tf);
		//footerText.setTypeface(tf,Typeface.BOLD);
		//footerText.setText("Hele Norges Edge"+"\n"+"P� Ett Sted");
		//footerText.setVisibility(View.GONE);
		//Sending intent to city list
		handler.postDelayed(new Runnable() {
			
			public void run() {
				
				Intent inCity=new Intent(Splash.this,CityListing.class);
				startActivity(inCity);
				Splash.this.finish();
			}
		}, 3000);
		
	}
}
