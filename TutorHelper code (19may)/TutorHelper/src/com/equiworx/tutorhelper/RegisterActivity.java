package com.equiworx.tutorhelper;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.Tutor;
import com.equiworx.tutor.TutorDashboard;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class RegisterActivity extends Activity implements AsyncResponseForTutorHelper{

	private EditText name, email, password, confirmPassword, contactNumber, alternateContactNumber, address;
	private Button register;
	private RadioButton male, female;
	private String trigger, gender="Male", tutor_id="";
	private Tutor tutor;
	private ImageView imgview_back;
	private TextView tv_title;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_register);
		
		initializeLayout();
		setClickListeners();
	}
	
	private void initializeLayout(){
		
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Tutor Helper");
		imgview_back=(ImageView)findViewById(R.id.back);
		
		name = (EditText)findViewById(R.id.name);
		email = (EditText)findViewById(R.id.email);
		//userName = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		confirmPassword = (EditText)findViewById(R.id.confirmPassword);
		contactNumber = (EditText)findViewById(R.id.contactInfo);
		alternateContactNumber = (EditText)findViewById(R.id.altContactInfo);
		address = (EditText)findViewById(R.id.address);
		male = (RadioButton)findViewById(R.id.male);
		female = (RadioButton)findViewById(R.id.female);
		
		register = (Button)findViewById(R.id.register);
		
		trigger = getIntent().getStringExtra("trigger");
		
		male.setChecked(true);
		female.setChecked(false);
		
		if(trigger.equals("edit")){
			tutor = getIntent().getParcelableExtra("Tutor");
			
			name.setText(tutor.getName());
			email.setText(tutor.getEmail());
			//userName.setText(tutor.getTutorId());
			contactNumber.setText(tutor.getContactNumber());
			alternateContactNumber.setText(tutor.getAltContactNumber());
			address.setText(tutor.getAddress());
			tutor_id = tutor.getTutorId();
		}
	}

	private void setClickListeners(){
		register.setOnClickListener(listener);
		male.setOnClickListener(listener);
		female.setOnClickListener(listener);
		imgview_back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
				
			}
		});
	}
	
	private String emptyFieldCheck(){
		String gettingEmail=email.getText().toString().trim();
		if(name.getText().toString().trim().equals("") || email.getText().toString().trim().equals("")
			|| password.getText().toString().trim().equals("") || confirmPassword.getText().toString().trim().equals("")
			|| contactNumber.getText().toString().trim().equals("") || address.getText().toString().trim().equals("")){
				return "Please fill in required fields.";
			}else if(!password.getText().toString().trim().equals(confirmPassword.getText().toString().trim())){
				return "Passwords do not match";
			}
			else if((contactNumber.getText().toString().trim().length()<10)){
				return "Please enter valid contact no";
			}
			else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(gettingEmail).matches() && !TextUtils.isEmpty(gettingEmail)) {
				return "Please enter a valid email.";
				}
				
			else
				return "success";
	}
	private View.OnClickListener listener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == register){
				String result = emptyFieldCheck();
				if(result.equals("success")){
					if (Util.isNetworkAvailable(RegisterActivity.this)){
						
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("email",email.getText().toString().trim()));
						//nameValuePairs.add(new BasicNameValuePair("username", userName.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("password",password.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("phone", contactNumber.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("alternate_phone",alternateContactNumber.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("address", address.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("trigger",trigger));
						nameValuePairs.add(new BasicNameValuePair("gender",gender));
						nameValuePairs.add(new BasicNameValuePair("tutor_id",tutor_id));
						
						AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(RegisterActivity.this, "register", nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForTutorHelper) RegisterActivity.this;
						mLogin.execute();
					}else {
						Util.alertMessage(RegisterActivity.this,
								"Please check your internet connection");
					}
				}else
					Util.alertMessage(RegisterActivity.this, result);
			}
			else if(v == male)
			{
				male.setChecked(true);
				female.setChecked(false);
				gender = "Male";
			}else if(v == female){
				female.setChecked(true);
				male.setChecked(false);
				gender = "Female";
			}
		}
	};
	
	@Override
	public void processFinish(String output, String methodName) {
		//Util.alertMessage(RegisterActivity.this, output);
		TutorHelperParser parser = new TutorHelperParser(RegisterActivity.this);
		tutor = parser.parseTutorDetails(output);
		
		if(tutor != null){
			AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
			alert.setTitle("Tutor Helper");
			alert.setMessage("Congrats!!! Your account has been registered with us.");
			alert.setPositiveButton("Get Started!", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					
					for(int i = 0; i<HomeAcitivity.cacheActivities.size(); i++){
						HomeAcitivity.cacheActivities.get(i).finish();
					}
					
					SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
					Editor editor = tutorPrefs.edit();
					editor.putString("tutorID", tutor.getTutorId());
					editor.putString("mode", "tutor");
					editor.putString("tutorpass", password.getText().toString());
					editor.commit();
					
					Intent intent = new Intent(RegisterActivity.this, TutorDashboard.class);
					startActivity(intent);
					finish();
				}
			});	
			alert.show();
		}
	}
}
