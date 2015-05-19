package com.equiworx.tutorhelper;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import com.equiworx.model.Parent;
import com.equiworx.model.StudentList;
import com.equiworx.model.Tutor;
import com.equiworx.parent.ParentDashBoard;
import com.equiworx.student.MyStudentList;
import com.equiworx.tutor.TutorDashboard;
import com.equiworx.util.TutorHelperDatabaseHandler;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;
import com.google.android.gcm.GCMRegistrar;


public class LoginActivity extends Activity implements AsyncResponseForTutorHelper{
	
	private Button logIn, signUp;
	private EditText userId, password;
	private TextView forgotpassword, regText;
	private Tutor tutor;
	private String trigger, methodName;
	private Parent parent;
	private ImageView imgview_back;
	private TextView tv_title;
	private TutorHelperDatabaseHandler dbHandler;
	private ArrayList<StudentList>arraylist_student=new ArrayList<StudentList>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_login);
	
		initializeLayout();
		setClickListeners();
		
		notificationGCM();
	}

	private void notificationGCM() {

		/******************************************************/
		checkNotNull(Notification_Util.SERVER_URL, "SERVER_URL");
		checkNotNull(Notification_Util.SENDER_ID, "SENDER_ID");
		// ------------------------------------
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(LoginActivity.this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		/******************************************************/
		/******************************************************/
	}

	private void initializeLayout() {
		// TODO Auto-generated method stub
		dbHandler = new TutorHelperDatabaseHandler(LoginActivity.this);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Tutor Helper");
		imgview_back=(ImageView)findViewById(R.id.back);
		logIn = (Button)findViewById(R.id.logIn);
		signUp = (Button)findViewById(R.id.signUp);
		userId = (EditText)findViewById(R.id.userId);
		password = (EditText)findViewById(R.id.password);
		forgotpassword = (TextView)findViewById(R.id.forgotPassword);
		regText = (TextView)findViewById(R.id.regText);
		
		tutor = new Tutor();
		trigger = getIntent().getStringExtra("trigger");
		if(trigger.equals("tutor")){
			methodName = "login";
			userId.setHint("Tutor Email/Tutor Id");
		}else{
			methodName = "parent-login";
			regText.setVisibility(View.GONE);
			signUp.setVisibility(View.GONE);
			userId.setHint("Parent Email/Parent Id");
		}
	}

	private void setClickListeners() {
		// TODO Auto-generated method stub
		logIn.setOnClickListener(listener);
		signUp.setOnClickListener(listener);
		forgotpassword.setOnClickListener(listener);
		imgview_back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
					
				}
			});
	}

	private Boolean emptyFieldCheck(){
		if(userId.getText().toString().trim().equals("") || password.getText().toString().trim().equals("")){
			return false;
		}else
			return true;
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == logIn){
				if(emptyFieldCheck()){
					if (Util.isNetworkAvailable(LoginActivity.this)){
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						if(trigger.equals("tutor"))
							nameValuePairs.add(new BasicNameValuePair("tutor_pin", userId.getText().toString().trim()));
						else{
							nameValuePairs.add(new BasicNameValuePair("username", userId.getText().toString().trim()));
						}
						
						nameValuePairs.add(new BasicNameValuePair("password",password.getText().toString().trim()));

						AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(LoginActivity.this, methodName, nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForTutorHelper) LoginActivity.this;
						mLogin.execute();
					}else {
						Util.alertMessage(LoginActivity.this,
								"Please check your internet connection");
					}
				}else
					Util.alertMessage(LoginActivity.this, "Please enter the required fields");
			}else if(v == signUp){
				Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
				intent.putExtra("trigger", "add");
				startActivity(intent);
			}else if(v == forgotpassword){
				Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
				intent.putExtra("trigger", trigger);
				startActivity(intent);
			}
		}
	};

	@Override
	public void processFinish(String output, String methodName1) {
		TutorHelperParser parser = new TutorHelperParser(LoginActivity.this);
		if(methodName1.equals(methodName))
		{
		if(trigger.equals("tutor")){
			tutor = parser.parseTutorDetails(output);
			
			if(tutor != null){
//				Util.alertMessage(LoginActivity.this, "Welcome "+tutor.getName());
				
				SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
				Editor editor = tutorPrefs.edit();
				editor.putString("tutorID", tutor.getTutorId());
				editor.putString("tutorpass", password.getText().toString().trim());
				editor.putString("mode", "tutor");
				editor.commit();
				
				for(int i = 0; i<HomeAcitivity.cacheActivities.size(); i++){
					HomeAcitivity.cacheActivities.get(i).finish();
				}
				
				ServerUtilities sUtil = new ServerUtilities();
				sUtil.deviceRegister(LoginActivity.this);
				
				Intent intent = new Intent(LoginActivity.this, TutorDashboard.class);
				startActivity(intent);
				finish();
			}
		}else{
			parent = parser.parseParentDetails(output);
			
			if(parent != null){
//				Util.alertMessage(LoginActivity.this, "Welcome "+parent.getName());
				
				for(int i = 0; i<HomeAcitivity.cacheActivities.size(); i++){
					HomeAcitivity.cacheActivities.get(i).finish();
				}
				
				SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
				Editor editor = tutorPrefs.edit();
				editor.putString("parentID", parent.getParentId());
				editor.putString("parentpass", password.getText().toString().trim());
				editor.putString("mode", "parent");
				editor.commit();
				
				ServerUtilities sUtil = new ServerUtilities();
				sUtil.deviceRegister(LoginActivity.this);
			
				fetchStudentList();
			
			}
		}
		}
		else if(methodName1.equals("fetch-student"))
				{
			arraylist_student = parser.getStudentlistwithoutNote(output);
			dbHandler.deleteStudentDetails();
			dbHandler.updateStudentDetails(arraylist_student);
		
			Intent intent = new Intent(LoginActivity.this, ParentDashBoard.class);
			startActivity(intent);
			finish();
				}
	}
	private void fetchStudentList() {
		if (Util.isNetworkAvailable(LoginActivity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			nameValuePairs.add(new BasicNameValuePair("parent_id",  parent.getParentId()));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", "-1"));
			Log.e("student list", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(LoginActivity.this, "fetch-student", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) LoginActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(LoginActivity.this,
					"Please check your internet connection");
		}
	}
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}
}
