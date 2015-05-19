package com.zira.registration;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;

import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.util.Util;


public class ValidateEmailPhoneNumber extends Activity implements AsyncResponseForZira{
	private EditText edit_email,edit_password,edit_mobileNumber;
	private ImageView btn_next,btn_back;
	private SharedPreferences reg_prefs;
	private String jsonEmailExistance,jsonphoneNumberExistence;
	private String jsonstr;
	private String regMethodName = "RegisterationValidate";
	private String countryCode = "";
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.rider_register_one);
	initialiseVariable();
	setclickListners();
	countryCode = "+" + GetCountryZipCode();
	System.err.println("countryCode="+countryCode);
	reg_prefs=getSharedPreferences("reg_Zira", MODE_PRIVATE);
}
private void initialiseVariable() {

	btn_next=(ImageView)findViewById(R.id.button_rider_Next_regisone);
	btn_back=(ImageView)findViewById(R.id.button_rider_back_regisone);
	edit_email=(EditText)findViewById(R.id.edit_rider_email);
	edit_password=(EditText)findViewById(R.id.edit_rider_password);
	edit_mobileNumber=(EditText)findViewById(R.id.editText_rider_mobileNumber);
}
private void setclickListners()
{
	btn_next.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Util.hideKeyboard(ValidateEmailPhoneNumber.this);
			String gettingEmail=edit_email.getText().toString();
			String gettingPassword=edit_password.getText().toString();
			String gettingMobileNumber=edit_mobileNumber.getText().toString();
			
			if(gettingEmail.equals("") )
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "Please enter email address");
			}//here check email address is valid or not
			else if(gettingPassword.equals(""))
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "Please enter password");
			}
			else if(gettingMobileNumber.equals(""))
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "Please enter mobile number");
			}
			else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(gettingEmail).matches() && !TextUtils.isEmpty(gettingEmail)) {
				Util.alertMessage(ValidateEmailPhoneNumber.this, "Please enter a valid email.");
			}
			else
			{
				if(Util.isNetworkAvailable(ValidateEmailPhoneNumber.this)){
//					new checkUserAvailability().execute();
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				    
					nameValuePairs.add(new BasicNameValuePair("useremail", edit_email.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("password", edit_password.getText().toString()));
					nameValuePairs.add(new BasicNameValuePair("mobile", countryCode+edit_mobileNumber.getText().toString()));
				    
					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(ValidateEmailPhoneNumber.this,regMethodName,nameValuePairs, true, "Please wait...");
					mWebPageTask.delegate = (AsyncResponseForZira) ValidateEmailPhoneNumber.this;
					mWebPageTask.execute();	
				}
				else
				{
					Util.alertMessage(ValidateEmailPhoneNumber.this, "Please check your internet connection");
				}
				
			}
		}
	});
	btn_back.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
		finish();
			
		}
	});
}
/*
public class checkUserAvailability extends AsyncTask<String, Void, String> {

	private ProgressDialog pDialog;

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		pDialog = new ProgressDialog(ValidateEmailPhoneNumber.this);
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... obj) {		
	
		try {
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
		    
		    params.add(new BasicNameValuePair("useremail", edit_email.getText().toString()));
		    params.add(new BasicNameValuePair("password", edit_password.getText().toString()));
		    params.add(new BasicNameValuePair("mobile", edit_mobileNumber.getText().toString()));
		    
		    jsonstr = Util.getResponseFromUrl(regMethodName, params, ValidateEmailPhoneNumber.this);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "success";
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pDialog.dismiss();
		
		JSONObject obj;
		try {
			obj = new JSONObject(jsonstr);
			String jsonMessage=obj.getString("message");
			String jsonResult=obj.getString("result");
			jsonEmailExistance=obj.getString("emailExistence");
			jsonphoneNumberExistence=obj.getString("phoneNumberExistence");
//			Toast.makeText(ValidateEmailPhoneNumber.this, jsonMessage, Toast.LENGTH_SHORT).show();
			if(jsonResult.equals("0"))
			{
				Editor e=reg_prefs.edit();
				e.putString("userid", "-1");
				e.putString("email", edit_email.getText().toString());
				e.putString("password", edit_password.getText().toString());
				e.putString("phonenumber", edit_mobileNumber.getText().toString());
				e.commit();
				
				Intent i=new Intent(ValidateEmailPhoneNumber.this,UploadBasicInfo.class);
				startActivity(i);
			}
			else
			{
				if(jsonEmailExistance.equals("1") || jsonphoneNumberExistence.equals("1"))
				{
					Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified email and mobile number already exists.");
				}
				else if(jsonEmailExistance.equals("1"))
				{
					Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified email already exists.");
				}
				else if(jsonphoneNumberExistence.equals("1"))
				{
					Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified mobile number already exists.");
				}else{
					Util.alertMessage(ValidateEmailPhoneNumber.this, jsonMessage);
				}
					
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
*/
@Override
public void processFinish(String output, String methodName) {
	JSONObject obj;
	try {
		Log.e("email ph", output);
		obj = new JSONObject(output);
		
		String jsonMessage=obj.getString("message");
		String jsonResult=obj.getString("result");
		jsonEmailExistance=obj.getString("emailExistence");
		jsonphoneNumberExistence=obj.getString("phoneNumberExistence");
//		Toast.makeText(ValidateEmailPhoneNumber.this, jsonMessage, Toast.LENGTH_SHORT).show();
		if(jsonResult.equals("0"))
		{
			Editor e=reg_prefs.edit();
			e.putString("userid", "-1");
			e.putString("email", edit_email.getText().toString());
			e.putString("password", edit_password.getText().toString());
			e.putString("phonenumber", edit_mobileNumber.getText().toString());
			e.commit();
			
			Intent i=new Intent(ValidateEmailPhoneNumber.this,UploadBasicInfo.class);
			startActivity(i);
		}
		else
		{
			if(jsonEmailExistance.equals("1") || jsonphoneNumberExistence.equals("1"))
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified email and mobile number already exists.");
			}
			else if(jsonEmailExistance.equals("1"))
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified email already exists.");
			}
			else if(jsonphoneNumberExistence.equals("1"))
			{
				Util.alertMessage(ValidateEmailPhoneNumber.this, "An account with specified mobile number already exists.");
			}else{
				Util.alertMessage(ValidateEmailPhoneNumber.this, jsonMessage);
			}
				
		}
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
public String GetCountryZipCode() {
	String CountryID = "";
	String CountryZipCode = "";
	TelephonyManager manager = (TelephonyManager) this
			.getSystemService(Context.TELEPHONY_SERVICE);
	// getNetworkCountryIso
	CountryID = manager.getNetworkCountryIso().toUpperCase();
	String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
	for (int i = 0; i < rl.length; i++) {
		String[] g = rl[i].split(",");
		if (g[1].trim().equals(CountryID.trim())) {
			CountryZipCode = g[0];
			break;
		}
	}
	return CountryZipCode;
}
}
