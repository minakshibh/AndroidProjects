package com.rapidride;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Rider_ColorView extends Activity {
	RelativeLayout showLayout;
	String colors="",getslatitude="",getslongitude="";
	Button close;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.show_background_color);
	
	close=(Button)findViewById(R.id.buttonClose);
	showLayout=(RelativeLayout)findViewById(R.id.linearBackground);
	Bundle extras = getIntent().getExtras();
	try{
	if (extras != null) {
		colors = extras.getString("Color");
		getslatitude=getIntent().getStringExtra("jsonlatitude");
		getslongitude=getIntent().getStringExtra("jsonlongitude");
		Log.i("tag", "Destination Show:"+colors);
	}}
	catch(Exception e)
	{}
	
	if(colors.equals("pink"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_Pink));
	}
	else if(colors.equals("red"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_Orange));
	} 
	else if(colors.equals("green"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_Green));
	}
	else if(colors.equals("yellow"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_yellow));
	}
	else if(colors.equals("orange"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_lightBrown));
	}
	else if(colors.equals("white"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_white));
	}
	else if(colors.equals("blue"))
	{
		showLayout.setBackgroundColor(getResources().getColor(R.color.background_Blue));
	}
	
	close.setOnClickListener(new OnClickListener() {
	public void onClick(View v) {
		Intent i=new Intent(Rider_ColorView.this,GetDriverLocation.class);
		i.putExtra("jsonlatitude", getslatitude);
		i.putExtra("jsonlongitude", getslongitude);
		i.putExtra("check", "value");
		startActivity(i);
		finish();
		}
	});
	
}
}
