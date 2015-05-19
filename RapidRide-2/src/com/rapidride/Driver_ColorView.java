package com.rapidride;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Driver_ColorView extends Activity {
	RelativeLayout showLayout;
	Button close;
	
	String colors="";
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.colorview);

	close=(Button)findViewById(R.id.buttonClose);
	showLayout=(RelativeLayout)findViewById(R.id.linearBackground);
	Bundle extras = getIntent().getExtras();
	try{
		if (extras != null) {
			colors = extras.getString("color");
		
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
	
		finish();
		}
	});
}}