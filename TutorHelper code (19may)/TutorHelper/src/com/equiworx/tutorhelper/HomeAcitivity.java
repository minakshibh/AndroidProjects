package com.equiworx.tutorhelper;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

public class HomeAcitivity extends Activity{

	private ImageView tutorLogin, parentLogin, tutorReg;
	public static ArrayList<Activity> cacheActivities;
	
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_home);
		
		cacheActivities = new ArrayList<Activity>();
		cacheActivities.add(HomeAcitivity.this);
		
		initializeLayout();
		setClickListeners();
		
	}

	private void initializeLayout() {
		// TODO Auto-generated method stub
		tutorLogin = (ImageView)findViewById(R.id.tutorLogin);
		tutorReg = (ImageView)findViewById(R.id.tutorReg);
		parentLogin = (ImageView)findViewById(R.id.parentLogin);
	}

	private void setClickListeners() {
		tutorLogin.setOnClickListener(listener);
		tutorReg.setOnClickListener(listener);
		parentLogin.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == tutorLogin){
				Intent intent = new Intent(HomeAcitivity.this, LoginActivity.class);
				intent.putExtra("trigger", "tutor");
				startActivity(intent);
//				finish();
			}else if(v == tutorReg){
				Intent intent = new Intent(HomeAcitivity.this, RegisterActivity.class);
				intent.putExtra("trigger", "add");
				startActivity(intent);
			}else if(v == parentLogin){
				Intent intent = new Intent(HomeAcitivity.this, LoginActivity.class);
				intent.putExtra("trigger", "parent");
				startActivity(intent);
//				finish();
			}
		}
	};
	
	
}
