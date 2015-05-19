package com.equiworx.tutorhelper;

import com.equiworx.parent.ParentDashBoard;
import com.equiworx.tutor.TutorDashboard;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;


public class Splash extends Activity {

	private Button next, skip;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);	
        setContentView(R.layout.activity_splash);
        
        /*initializeLayout();
        setClickListeners();*/
        
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
			public void run() {
				
				SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
				
				if(tutorPrefs.getString("mode", "none").equals("none")){
					goToHomeScreen();
				}else if(tutorPrefs.getString("mode", "none").equals("tutor")){
					if(tutorPrefs.getString("tutorID", "0").equals("0")){
						goToHomeScreen();
					}else{
						Intent mIntent=new Intent(Splash.this,TutorDashboard.class);
						Splash.this.finish();
						startActivity(mIntent);
					}
				}else if(tutorPrefs.getString("mode", "none").equals("parent")){
					if(tutorPrefs.getString("parentID", "0").equals("0")){
						goToHomeScreen();
					}else{
						Intent mIntent=new Intent(Splash.this,ParentDashBoard.class);
						Splash.this.finish();
						startActivity(mIntent);
					}
				}
				
			}
		}, 2000);
    }

    public void goToHomeScreen(){
    	Intent mIntent=new Intent(Splash.this,HomeAcitivity.class);
		Splash.this.finish();
		startActivity(mIntent);
    }
	/*private void initializeLayout() {
		// TODO Auto-generated method stub
		next = (Button)findViewById(R.id.next);
		skip = (Button)findViewById(R.id.skip);
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		next.setOnClickListener(listener);
		skip.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == next){
				
			}else if(v == skip){
				Intent intent = new Intent(Splash.this, HomeAcitivity.class);
				startActivity(intent);
				finish();
			}
		}
	};
   */
}