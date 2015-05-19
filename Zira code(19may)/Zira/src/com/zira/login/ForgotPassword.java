package com.zira.login;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.profile.EditBaseProfile;
import com.zira.util.Util;

public class ForgotPassword extends Activity implements AsyncResponseForZira{
	private EditText txtEmail;
	private Button  btnDone;
	private ProgressDialog pDialog;
	private ImageView back;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_activity);
		
		txtEmail = (EditText)findViewById(R.id.edit_email);
		btnDone = (Button)findViewById(R.id.btnDone);
		back = (ImageView)findViewById(R.id.btn_back);
		
		btnDone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String email = txtEmail.getText().toString().trim();
				if(!email.equals("")){
					if(Util.isNetworkAvailable(ForgotPassword.this)){

						//new recoverPassword().execute(email);
		
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			            nameValuePairs.add(new BasicNameValuePair("UserEmail", email));

						AsyncTaskForZira mFetchVehicleTask = new AsyncTaskForZira(ForgotPassword.this, "RecoverPassword",nameValuePairs,true, "Please wait...");
						mFetchVehicleTask.delegate = (AsyncResponseForZira) ForgotPassword.this;
						mFetchVehicleTask.execute();
						
						
						
			/*			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			            nameValuePairs.add(new BasicNameValuePair("message", "amrik android"));
			            nameValuePairs.add(new BasicNameValuePair("ID", "97"));
			            nameValuePairs.add(new BasicNameValuePair("type", "android"));
			          // nameValuePairs.add(new BasicNameValuePair("trigger", ""));
			            
			           AsyncTaskForZira mFetchVehicleTask = new AsyncTaskForZira(ForgotPassword.this, "TestSendPushMessage",nameValuePairs,true, "Please wait...");
			           mFetchVehicleTask.delegate = (AsyncResponseForZira) ForgotPassword.this;
			           mFetchVehicleTask.execute();*/
						
					}
					else
					{
						Util.alertMessage(ForgotPassword.this, "Please check your internet connection");
						}
					
				}else{
						Util.alertMessage(ForgotPassword.this, "Please enter email address");	
						}
		}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
			finish();	
			}
		});
	}
	
	/*private class recoverPassword extends AsyncTask<String, String, String> { 
		
		private int int_value=0;
		private String jsonResult, jsonMessage;
		
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(ForgotPassword.this);
		    pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
		
		@Override
		protected String doInBackground(String... arg0) {
		
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	            nameValuePairs.add(new BasicNameValuePair("UserEmail", arg0[0]));
	            
	            String jsonstr = Util.getResponseFromUrl("RecoverPassword", nameValuePairs, ForgotPassword.this);
	            JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
				
			} catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
			
			return "";
			}
			
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				pDialog.dismiss();
			    if(jsonResult.equals("0"))
				{
					Util.alertMessage(ForgotPassword.this, "Your password has been sent to you via email. Kindly check.");
				}else{
					Util.alertMessage(ForgotPassword.this, jsonMessage);	
				}
			}
	}*/

	@Override
	public void processFinish(String output, String methodName) {
		Log.e("forgotpass", output);
		String jsonResult=null;
		String jsonMessage=null;
		try{
			JSONObject obj=new JSONObject(output);
			jsonResult=obj.getString("result");
			jsonMessage=obj.getString("message");
			System.err.println(jsonResult+jsonMessage);
			
			 if(jsonResult.equals("0"))
				{
					Util.alertMessage(ForgotPassword.this, "Your password has been sent to you via email. Kindly check.");
				}else{
					Util.alertMessage(ForgotPassword.this, jsonMessage);	
				}
		}catch(Exception e){
			
		}
		
	}
}
