package com.pos.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.example.posapplication.R;
import com.pos.retail.LoginScreen;
import com.pos.retail.TransactionActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.support.v4.content.IntentCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SignOffUser {

	ProgressDialog dialog;
	Context mcontext;
	  String signOffUrl,Method_SignOff="OperatorDeclaration";
	  SharedPreferences loginpref;
	  SharedPreferences posPref;
	  Editor posEditor;
	  String uuid;
	  String expectedAmount;
	  
	public SignOffUser(Context context, String expectedAmount) {
		mcontext = context;

		loginpref=mcontext.getSharedPreferences("LoginPref", Context.MODE_PRIVATE);
		
		 posPref = mcontext.getSharedPreferences("pos", Context.MODE_PRIVATE);
		 posEditor = posPref.edit();
		signOffUrl = mcontext.getResources().getString(R.string.liveurl)+"/"+Method_SignOff;

        this.expectedAmount  = expectedAmount;
        TelephonyManager telephonyManager = (TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE);
        uuid =telephonyManager.getDeviceId();
	}
	
	public class SignOff extends AsyncTask<String, String, String>
	{
		String status="";
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Please wait for Sign out ");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(signOffUrl);
	       
//			edit1.putString("UserName", null);
//			edit1.putString("Password", null);
//			edit1.putString("UserId", null);
//			edit1.putString("UserRollId", null);
	       
	        try
	        {
	        
	        int userId =loginpref.getInt("UserId", -1);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	        String username = loginpref.getString("UserName", "");
	        
	         nameValuePairs.add(new BasicNameValuePair("PosId","1"));
	         nameValuePairs.add(new BasicNameValuePair("UserId",Integer.toString(userId)));
	         nameValuePairs.add(new BasicNameValuePair("UserName",username));
	         nameValuePairs.add(new BasicNameValuePair("ExpectedAmount",expectedAmount));
	         nameValuePairs.add(new BasicNameValuePair("Difference","0"));
	         nameValuePairs.add(new BasicNameValuePair("Confirm","true"));
	         nameValuePairs.add(new BasicNameValuePair("UID",uuid));
	         
	         
	         requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		    
		     String xml = EntityUtils.toString(entity);
		     
		     Log.e("Sign off", xml);
		     XMLParser parser = new XMLParser();
		     Document doc =parser.getDomElement(xml);
		     
		     NodeList nl = doc.getElementsByTagName("document");
		     
			     for(int i=0;i<nl.getLength();i++)
			     {
			    	Element e =(Element)nl.item(i);
			    	status = parser.getValue(e, "Result");
			     }
	        }
	        catch(Exception e)
	        {
	        	e.printStackTrace();
	        }
			
			return null;
		}
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	
	    	if(status.equals("0"))
	    	{
	    		SharedPreferences.Editor edit1 = loginpref.edit();
				edit1.putString("UserName", null);
				edit1.putString("Password", null);
				edit1.putString("UserId", null);
				edit1.putString("UserRollId", null);
				
				edit1.commit();
	    		posEditor.putInt("timestamp",-1 );
	    		posEditor.commit();
				
	    		
		    	
	    		Intent intent = new Intent(mcontext.getApplicationContext(), LoginScreen.class);
				ComponentName cn = intent.getComponent();
				Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
				
				mcontext.startActivity(mainIntent);
				((Activity)mcontext).finish();
				
				
				/*
				PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask(mcontext);
                // PerformBackgroundTask this class is the class that extends AsynchTask 
                performBackgroundTask.execute();*/
	    	}
	    	else
	    	{
	    		
	    			final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
	    			
	    			String title=mcontext.getResources().getString(R.string.pos);
	    			alertdialog.setTitle(title);
	    			alertdialog.setMessage("Connection error. Please try again ");
	    			alertdialog.setPositiveButton("OK", null);
	    			alertdialog.show();
	    		
	    		
	    	}
	    	
		}
	}
}
