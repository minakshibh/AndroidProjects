package com.rapidride;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class ForgotPassword extends Activity {
	Typeface typeFace;
	EditText ed_email;
	TextView tv_title;
	Button btn_gone;
	String str_ed_email,str_phoneno;
	SharedPreferences pref;
	String zipCode="";
	int exception=0;
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		 pref = ForgotPassword.this.getSharedPreferences("RapidRide", MODE_PRIVATE);
		 zipCode=pref.getString("Zipcode", null);
		 Log.d("tag", "Receive Zip Code:"+zipCode);		 
		 typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");// set the font 
		 tv_title=(TextView)findViewById(R.id.textView_forgotpassword_title);
		 tv_title.setTypeface(typeFace);
		
		 btn_gone=(Button)findViewById(R.id.button_forgotpassworddone);
		 btn_gone.setTypeface(typeFace);
		 
		 btn_gone.setOnClickListener(new OnClickListener() {
			
				public void onClick(View v) {
				ed_email=(EditText)findViewById(R.id.editText_forgot_email);
			
				str_ed_email=ed_email.getText().toString();
			
				if(ed_email.getText().toString().equals(""))
				{
					Utility.alertMessage(ForgotPassword.this,"Please enter data") ;
					}
			
				else if (isValidEmail(ed_email.getText().toString()) == false) //email validation
			      	{
			    	  	Utility.alertMessage(ForgotPassword.this,"Invalid email address") ;
			      		}
				else if(Utility.isConnectingToInternet(ForgotPassword.this))
				{
					new Forgotpassword().execute(); /*************call Async_task*/
					}
				else{
					Utility.alertMessage(ForgotPassword.this,"error in internet connection");
				}
				
				
			}
		});
	}
	
	
	
/*********************End Main Function *************************/
	
	
	
	
	private class Forgotpassword extends AsyncTask<Void, Void, Void> { // Async_task class
	
	private ProgressDialog pDialog;
	private String jsonResult;
	private String jsonMessage;

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// Showing progress dialog
		pDialog = new ProgressDialog(ForgotPassword.this);
		exception=0;
		//pDialog.setTitle("Loading");
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
		}
	
	@Override
	protected Void doInBackground(Void... arg0) {
	
		try {
		 parsing();
		 } catch (Exception e) {
		 // TODO Auto-generated catch block
		 e.printStackTrace();
		}
		
		return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		pDialog.dismiss();
		if(exception==1)
		{
			Utility.alertMessage(ForgotPassword.this,"Internet connection failed. Please Try again later.");
		}
		else if(jsonResult.equals("0"))
		{
			//Utility.alertMessage(ForgotPassword.this,jsonMessage);
			//alertMessage(jsonMessage);
			ed_email.setText("");
			Intent i=new Intent(ForgotPassword.this,Login_Activity.class);
			finish();
			startActivity(i);
				}
			else
			{
				Utility.alertMessage(ForgotPassword.this,jsonMessage);
				jsonResult="";
				}
		
		}
	
		//password parsing function
		public void parsing() throws JSONException {
		try {
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 60000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 61000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpPost httpost = new HttpPost(Url_Address.url_Home+"/RecoverPassword");
		JSONObject json = new JSONObject();
		
		json.put("Trigger", "RecoverPassword");
		//json.put("PhoneNumber", zipCode);
		json.put("UserEmail", str_ed_email);
		
	      //	      
		httpost.setEntity(new StringEntity(json.toString()));
		httpost.setHeader("Accept", "application/json");
		httpost.setHeader("Content-type", "application/json");
		
		HttpResponse response = client.execute(httpost);
		HttpEntity resEntityGet = response.getEntity();
		String jsonstr=EntityUtils.toString(resEntityGet);
		if(jsonstr!=null)
		{
		 Log.e("tag","result for forgot pass>>>>>>>    "+ jsonstr);
		 
			}
			JSONObject obj=new JSONObject(jsonstr);
			   jsonResult=obj.getString("result");
			jsonMessage=obj.getString("message");
			
			Log.i("tag:", "Result: "+jsonResult);
			Log.i("tag:", "Message :"+jsonMessage);
			}
		  catch(Exception e){
		   System.out.println(e);
		   exception=1;
		   Log.d("tag", "Error :"+e); }  
			}
		}
	 private boolean isValidEmail(String email)// email validation
		{
		    String emailRegex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		    if(email.matches(emailRegex))
		    	{
		        	return true;
		    		}
		    return false;
			}  	
	 
}