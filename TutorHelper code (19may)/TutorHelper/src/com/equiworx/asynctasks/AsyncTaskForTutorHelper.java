package com.equiworx.asynctasks;


import java.util.ArrayList;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.equiworx.util.Util;

public class AsyncTaskForTutorHelper extends AsyncTask<String, Void, String> {

	private Activity activity;
	public AsyncResponseForTutorHelper delegate = null;
	private String result = "";	
	private ProgressDialog pDialog;
	private String methodName, message;
	private ArrayList<NameValuePair> nameValuePairs;
	private boolean displayProgress;
	
	public AsyncTaskForTutorHelper(Activity activity, String methodName,ArrayList<NameValuePair> nameValuePairs, boolean displayDialog, String message) {
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
			pDialog.setTitle("Tutor Helper");
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
		if(displayProgress)
			pDialog.dismiss();
		
		Log.e(methodName,result);
		delegate.processFinish(result, methodName);
	}
	
}
