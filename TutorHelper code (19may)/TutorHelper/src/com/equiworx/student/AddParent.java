package com.equiworx.student;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.Util;

public class AddParent extends Activity implements AsyncResponseForTutorHelper {

	private EditText name, email, contactInfo, address;
	private Button done,cancel;
	private SharedPreferences tutorPrefs;
	private RadioButton male, female;
	private TextView tv_title;
	private ImageView back;
	
	
	private String gender = "male";
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_add_parent);
		
		initializeLayout();
		setClickListeners();
	}

	private void initializeLayout() {
		// TODO Auto-generated method stub
		//tv_title=(TextView)findViewById(R.id.title);
		//back=(ImageView)findViewById(R.id.back);
		//tv_title.setText("Add Parent");
		name = (EditText)findViewById(R.id.name);
		email = (EditText)findViewById(R.id.email);
		contactInfo = (EditText)findViewById(R.id.contactInfo);
		address = (EditText)findViewById(R.id.address);
		done = (Button)findViewById(R.id.done);
		cancel=(Button)findViewById(R.id.cancel);
		male = (RadioButton)findViewById(R.id.male);
		female = (RadioButton)findViewById(R.id.female);
		
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
	}

	private String emptyFieldCheck(){
		String gettingEmail= email.getText().toString().trim();
		if(name.getText().toString().trim().equals("") || email.getText().toString().trim().equals("") ||
				contactInfo.getText().toString().trim().equals("") || address.getText().toString().trim().equals(""))
			return "Please enter required fields";
		else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(gettingEmail).matches() && !TextUtils.isEmpty(gettingEmail)) {
			return "Please enter a valid email.";
		}else if(contactInfo.getText().toString().trim().length()<10){
			return "Please enter correct contact info";
			}
		else
			return "success";
	}
	
	private void setClickListeners(){
		done.setOnClickListener(listener);
		male.setOnCheckedChangeListener(checkListener);
		female.setOnCheckedChangeListener(checkListener);
		cancel.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	private CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			// TODO Auto-generated method stub
			if(buttonView == male){
				if(isChecked){
					gender = "male";
				}else
					gender = "female";
			}else if(buttonView == female){
				if(isChecked){
					gender = "female";
				}else
					gender = "male";
			}
		}
	};
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v == done){
				String result = emptyFieldCheck();
				if(result.equals("success"))
				{
					AddStudent.idcheck=1;
					Editor editor = tutorPrefs.edit();
					editor.putString("name", name.getText().toString().trim());
					editor.putString("email", email.getText().toString().trim());
					editor.putString("contact", contactInfo.getText().toString().trim());
					editor.putString("address", address.getText().toString().trim());
					editor.putString("gender", gender);
					editor.putString("newparentID","-1");
					
					editor.commit();
					finish();
					}
		
			else{
					Util.alertMessage(AddParent.this, result);
					}
		}
	};
	
	};
	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		
	}
}