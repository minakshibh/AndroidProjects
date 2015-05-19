package com.twentyfourseven.zira;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.User;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class SplashScreen extends Activity implements AsyncResponseForZira{
	public static SharedPreferences prefs;
	VideoView videoView;
	MediaController mcontroller;
	private User user;
	private ZiraParser ziraParser;
	private String getUserVehicleProfile = "GetProfiles";
	private ProfileInfoModel mProfileInfoModel;
	private boolean isLogin;
	private int flag = 0;
	public static Context splashContext;
	public static boolean check=false;
	public static SharedPreferences prefs2;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);

		splashContext = SplashScreen.this;
		ziraParser = new ZiraParser();
		
			prefs = getSharedPreferences("Zira", MODE_PRIVATE);
			prefs2 = getSharedPreferences("policy", MODE_PRIVATE); 
			String loginStatus = prefs.getString("_Login", "");
			
			videoView=(VideoView)findViewById(R.id.videoView);
//			mcontroller = new MediaController(SplashScreen.this);
//			mcontroller.setEnabled(false);
			Uri videourl = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashvideo);
			videoView.setVideoURI(videourl);
//			mcontroller.setAnchorView(videoView);
//			videoView.setMediaController(mcontroller);
			videoView.start();
			videoView.setOnCompletionListener(new OnCompletionListener() {
				
			public void onCompletion(MediaPlayer mp) {
				
				if(flag == 1){
					String mode = prefs.getString("mode", "");
					if(mode.equals(""))
					{
						Intent i=new Intent(SplashScreen.this,com.twentyfourseven.zira.LoginActivity.class);
						startActivity(i);
						finish();
						}
					else if (mode.equals("driver")) {
						startActivity(new Intent(SplashScreen.this,DriverModeActivity.class));
						finish();
						}
					else if(mode.equals("rider")) {
						
							String getUserid = prefs.getString("Userid", "");
							if (getUserid.equals("")) {
								startActivity(new Intent(SplashScreen.this,com.twentyfourseven.zira.LoginActivity.class));
								finish();
							} else {
								Intent i=new Intent(SplashScreen.this,VehicleSearchActivity.class);
								i.putExtra("legalpolicy", "no");
								startActivity(i);
							}
						}
				}else{
					videoView.start();
				}
				}
				});
			
			if(loginStatus.equals("true")){
				isLogin = true;
				
				if(Util.isNetworkAvailable(SplashScreen.this)){

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("useremail",prefs.getString("_UserEmail","")));
				nameValuePairs.add(new BasicNameValuePair("password",prefs.getString("_Password","")));
				
				AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(SplashScreen.this, "Login",nameValuePairs, false,"");
				mWebPageTask.delegate = (AsyncResponseForZira) SplashScreen.this;
				mWebPageTask.execute();
				}
				else
				{
					Util.alertMessage(SplashScreen.this, "Please check your internet connection");
				}
			}else{
				isLogin = false;
				flag = 1;
			}
			
       }
	
	@Override
	public void processFinish(String output, String methodName) {
		Log.e("login", output);
		if(methodName.equals("Login")){
			
			user = ziraParser.parseLoginResponse(output);
			try {
				if (user.getResult().equals("0")) {
					SingleTon.getInstance().setUser(user);
					try {
						ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
						nameValuePair.add(new BasicNameValuePair("UserId", user.getUserid()));
						AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(SplashScreen.this, getUserVehicleProfile,nameValuePair, false, "");
						mWebPageTask.delegate = (AsyncResponseForZira) SplashScreen.this;
						mWebPageTask.execute();
						Editor e=prefs.edit();
						e.putString("Userid", "yes");
						e.commit();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Util.alertMessage(SplashScreen.this, user.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				AlertDialog.Builder alert = new AlertDialog.Builder(SplashScreen.this);
				alert.setTitle("Zira 24/7");
				alert.setMessage("Something went wrong. Please try again later.");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				});
				alert.show();
						
			}
		}else if(methodName.equals(getUserVehicleProfile)){
			Log.e("getUserVehicleProfile", output);
			mProfileInfoModel = ziraParser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			// progress.dismiss();
			check=true;
			ServerUtilities sUtil = new ServerUtilities();
			sUtil.deviceRegister(SplashScreen.this, mProfileInfoModel);
		}
	}
}
