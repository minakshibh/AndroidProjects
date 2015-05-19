package com.equiworx.tutorhelper;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.Tutor;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class ForgotPasswordActivity extends Activity implements AsyncResponseForTutorHelper{
	
	private Button recoverPassowrd;
	private EditText userId;
	private String trigger;
	private ImageView imgview_back;
	private TextView tv_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_forgotpassword);
	
		initializeLayout();
		setClickListeners();
	}

	private void initializeLayout() {
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Recover Password");
		imgview_back=(ImageView)findViewById(R.id.back);
		recoverPassowrd = (Button)findViewById(R.id.recover);
		userId = (EditText)findViewById(R.id.userId);
		
		trigger = getIntent().getStringExtra("trigger");
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		recoverPassowrd.setOnClickListener(listener);
		imgview_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
					
				}
			});
	}

	private Boolean emptyFieldCheck(){
		if(userId.getText().toString().trim().equals("")){
			return false;
		}else
			return true;
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == recoverPassowrd){
				if(emptyFieldCheck()){
					if (Util.isNetworkAvailable(ForgotPasswordActivity.this)){
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("fp_e_u", userId.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("trigger", trigger));
						
						AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ForgotPasswordActivity.this, "forgot-pass", nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForTutorHelper) ForgotPasswordActivity.this;
						mLogin.execute();
						
						/*ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("trigger", "android"));
						nameValuePairs.add(new BasicNameValuePair("role", "tutor"));
						nameValuePairs.add(new BasicNameValuePair("reg_id", "APA91bFFXY1QZNjtzPSiO3hK2U55LZVSNidvwR1SWHGNEY_Nd6kNs4FYpq6SVIG_bC_z7AhsXZFJSXTBGGniE-D78_ZkdviBnCH5eZclOreYJ64Mp3rOSrNLoOfhgRpEHPRfyIB4IIrlOkWhhoser2CATZsIa7ogJw"));
						nameValuePairs.add(new BasicNameValuePair("user_id", "tnav37"));
						nameValuePairs.add(new BasicNameValuePair("ud_id", "352985055963959"));*/
						
						
						/*AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ForgotPasswordActivity.this, "insert-device", nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForTutorHelper) ForgotPasswordActivity.this;
						mLogin.execute();*/
						
						
					}else {
						Util.alertMessage(ForgotPasswordActivity.this,
								"Please check your internet connection");
					}
				}else
					Util.alertMessage(ForgotPasswordActivity.this, "Please enter the required fields");
			}
		}
	};

	@Override
	public void processFinish(String output, String methodName) {
//		Util.alertMessage(ForgotPasswordActivity.this, output);
		TutorHelperParser parser = new TutorHelperParser(ForgotPasswordActivity.this);
		String message = parser.parseResponse(output);
		if(!message.equals("failure"))
		{
			AlertDialog.Builder alert = new AlertDialog.Builder(ForgotPasswordActivity.this);
			alert.setTitle("Congrats!!!");
			alert.setMessage("Your password has been sent to you via email.");
			alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			alert.show();
		}
		
		
		/*if(tutor != null)
			Util.alertMessage(ForgotPasswordActivity.this, "Welcome "+tutor.getName());*/
	}
}
