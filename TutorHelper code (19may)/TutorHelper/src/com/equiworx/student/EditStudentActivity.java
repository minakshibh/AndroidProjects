package com.equiworx.student;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.tutor.ParentDetailActivity;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.Util;

public class EditStudentActivity extends Activity implements AsyncResponseForTutorHelper{
	
	private SharedPreferences tutorPrefs;
	private EditText notes,email,contactInfo,fees,address;
	private RadioButton male, female;
	private ImageView back;
	private Button add;
	private TextView name,title,lblnotes;
	private LinearLayout lay_address,lay_fees;

	private String parentId = "0", tutorID, parentGender, studentGender = "male",trigger;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_editstudent);
		
		initializeLayout();
		setClickListeners();
	}
	private void initializeLayout() {
		
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		back=(ImageView)findViewById(R.id.back);
		notes= (EditText)findViewById(R.id.notes);
		lblnotes=(TextView)findViewById(R.id.lblnotes);
		address=(EditText)findViewById(R.id.address);
		address.setText(getIntent().getStringExtra("address"));
		lay_address=(LinearLayout)findViewById(R.id.lay_address);
		lay_fees=(LinearLayout)findViewById(R.id.lay_fees);
		if(tutorPrefs.getString("mode", "").equals("parent"))
		{
			trigger="Parent";
			parentId=tutorPrefs.getString("parentId", "");
			notes.setVisibility(View.GONE);
			lblnotes.setVisibility(View.GONE);
			lay_fees.setVisibility(View.GONE);
			}
		else
		{
			trigger="Tutor";
			tutorID=tutorPrefs.getString("tutorID", "");
			lay_address.setVisibility(View.GONE);
		}
		studentGender=getIntent().getStringExtra("gender");
		male = (RadioButton)findViewById(R.id.male);
		female = (RadioButton)findViewById(R.id.female);
		if(studentGender.equalsIgnoreCase("male"))
		{
			male.isChecked();
			studentGender="male";
			}
		else
		{
			female.isChecked();
			studentGender="female";
			}
		
		
		notes.setText(getIntent().getStringExtra("notes"));
		title= (TextView)findViewById(R.id.title);
		title.setText("Add a Student");
		name = (TextView)findViewById(R.id.name);
		name.setText(getIntent().getStringExtra("name"));
		
		email = (EditText)findViewById(R.id.email);
		email.setText(getIntent().getStringExtra("email"));
		
		contactInfo = (EditText)findViewById(R.id.contactInfo);
		contactInfo.setText(getIntent().getStringExtra("contactno"));
		
		fees = (EditText)findViewById(R.id.fees);
		fees.setText(getIntent().getStringExtra("fees"));
		
		parentId=getIntent().getStringExtra("parentid");
		add = (Button)findViewById(R.id.add);
		//existingParentId = (EditText)findViewById(R.id.existingParentID);
		
	}
	private void setClickListeners() {
		// TODO Auto-generated method stub
		male.setOnCheckedChangeListener(checkListener);
		female.setOnCheckedChangeListener(checkListener);
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
				
			}
		});
		
		
		
		add.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			String check = emptyFieldCheck();
			if(check.equals("success"))
			{
				if (Util.isNetworkAvailable(EditStudentActivity.this)){
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString().trim()));
					nameValuePairs.add(new BasicNameValuePair("email", email.getText().toString().trim()));
					nameValuePairs.add(new BasicNameValuePair("parent_id", parentId));
					nameValuePairs.add(new BasicNameValuePair("phone", contactInfo.getText().toString().trim()));
					
					
					nameValuePairs.add(new BasicNameValuePair("parent_name", ""));
					nameValuePairs.add(new BasicNameValuePair("parent_email",""));
					nameValuePairs.add(new BasicNameValuePair("parent_contact", ""));
					nameValuePairs.add(new BasicNameValuePair("parent_address", ""));
					nameValuePairs.add(new BasicNameValuePair("parent_gender", ""));
					nameValuePairs.add(new BasicNameValuePair("address", address.getText().toString()));
					if(trigger.equalsIgnoreCase("Tutor")){
					/*	nameValuePairs.add(new BasicNameValuePair("parent_name", parentName.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("parent_email", parentEmail.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("parent_contact", parentContact.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("parent_address", parentAddress.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("parent_gender", parentGender));
						nameValuePairs.add(new BasicNameValuePair("address", parentAddress.getText().toString().trim()));*///balwinder.07.89@gmail.com
						nameValuePairs.add(new BasicNameValuePair("creator", "Tutor"));
						nameValuePairs.add(new BasicNameValuePair("creator_id", tutorID));
						
					}else{
						
						nameValuePairs.add(new BasicNameValuePair("creator", "Parent"));
						nameValuePairs.add(new BasicNameValuePair("creator_id", parentId));
						
						
					}
					nameValuePairs.add(new BasicNameValuePair("alternate_phone", ""));
					nameValuePairs.add(new BasicNameValuePair("trigger", "Edit"));
					nameValuePairs.add(new BasicNameValuePair("gender", studentGender));
					nameValuePairs.add(new BasicNameValuePair("fee", fees.getText().toString().trim()));
					nameValuePairs.add(new BasicNameValuePair("notes", notes.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("student_id", getIntent().getStringExtra("studentid")));
					
					Log.e("add stu", nameValuePairs.toString());
					AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(EditStudentActivity.this, "student-register", nameValuePairs, true, "Please wait...");
					mLogin.delegate = (AsyncResponseForTutorHelper) EditStudentActivity.this;
					mLogin.execute();
			}
				else
				{
					Util.alertMessage(EditStudentActivity.this,
							"Please check your internet connection");
					}
				}
			else
			{
				Util.alertMessage(EditStudentActivity.this, check);
			}
				
			}
		});
	}
	private CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(buttonView == male){
				if(isChecked){
					studentGender = "male";
				}else
					studentGender = "female";
			}else if(buttonView == female){
				if(isChecked){
					studentGender = "female";
				}else
					studentGender = "male";
			}
		}
	};
	
	
	protected String emptyFieldCheck() {
	String 	gettingEmail=email.getText().toString().trim();
		if(trigger.equalsIgnoreCase("Parent"))
		{
		
			 if(email.getText().toString().trim().equals("")){
				return "Please enter student email";
			}else if(contactInfo.getText().toString().trim().equals("")){
				return "Please enter student's contact info";
			}
			else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(gettingEmail).matches() && !TextUtils.isEmpty(gettingEmail)) {
				return "Please enter a valid email.";
			}
			else if(address.getText().toString().trim().equals("")){
				return "Please enter address details";
			}
			else if(contactInfo.getText().toString().trim().length()<10){
				return "Please enter valid contact info";
			}
			else{
				return "success";
			}
		}
		else//tutor
		{
			 if(email.getText().toString().trim().equals("")){
					return "Please enter student email";
				}else if(contactInfo.getText().toString().trim().equals("")){
					return "Please enter student's contact info";
				}else if(fees.getText().toString().trim().equals("")){
					return "Please enter fee details";
					}
				else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(gettingEmail).matches() && !TextUtils.isEmpty(gettingEmail)) {
					return "Please enter a valid email.";
				}
				else if(notes.getText().toString().trim().equals("")){
					return "Please enter notes details";
				}
				else if(contactInfo.getText().toString().trim().length()<10){
					return "Please enter valid contact info";
				}
				else{
					return "success";
				}
			
		}
	}
	public void processFinish(String output, String methodName) {
		if(methodName.equalsIgnoreCase("student-register"))
		{
			
				if(trigger.equalsIgnoreCase("Parent"))
				{
					Intent i=new Intent(EditStudentActivity.this,MyStudentList.class);
					startActivity(i);
					finish();
				}
				else
				{
				
				Intent i=new Intent(EditStudentActivity.this,ParentDetailActivity.class);
				startActivity(i);
				finish();
			}
			/*}
			else{
				try{
				if((getIntent().getStringExtra("check")).equalsIgnoreCase("parent"))
				{
					Intent i=new Intent(EditStudentActivity.this,ParentDetailActivity.class);
					startActivity(i);
					finish();
					}
				}
			catch(Exception e)
			{
				e.printStackTrace();
			}*/
	//	}
		}
		
	}
}
