package com.zira.async_tasks;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import com.zira.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class AsyncTaskForZira extends AsyncTask<String, Void, String> {

	private Activity activity;
	public AsyncResponseForZira delegate = null;
	private String result = "";	
	private  ProgressDialog pDialog;
	private String methodName, message;
	private ArrayList<NameValuePair> nameValuePairs;
	private boolean displayProgress;
	
	public AsyncTaskForZira(Activity activity, String methodName,ArrayList<NameValuePair> nameValuePairs, boolean displayDialog, String message) {
		this.activity = activity;
		this.methodName = methodName;
		this.nameValuePairs = nameValuePairs;
		this.displayProgress = displayDialog;
		this.message = message;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		if(displayProgress){
			pDialog = new ProgressDialog(activity);
			pDialog.setTitle("Zira 24/7");
			pDialog.setMessage(message);
			pDialog.setCancelable(false);
			pDialog.show();
		}
	}

	@Override
	protected String doInBackground(String... urls) {
		result = Util.getResponseFromUrl(methodName, nameValuePairs, activity);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		
		try{
		if(displayProgress)
	
		 pDialog.dismiss();
		
		
		delegate.processFinish(result, methodName);
		}
		catch (Exception e) {
			System.err.println("dailog exception="+e);
		}
		
		
	}
	
}
