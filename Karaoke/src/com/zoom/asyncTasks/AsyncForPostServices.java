package com.zoom.asyncTasks;


import org.json.JSONObject;

import com.karaoke.interfaces.AsyncResponse;
import com.zoom.xmlparser.XmlPostParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;


public class AsyncForPostServices extends AsyncTask<String, Void, String> {

	private Activity activity;
	public AsyncResponse delegate = null;
	private String result = "";	
	private ProgressDialog pDialog;
	private String url;
	private JSONObject jsonObject;

	public AsyncForPostServices(Activity activity, String url,JSONObject jsonObject) {
		this.activity = activity;
		this.url = url;
		this.jsonObject = jsonObject;

	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();

		pDialog = new ProgressDialog(activity);
		pDialog.setTitle("Loading");
		pDialog.setMessage("Please wait...");
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	protected String doInBackground(String... urls) {		
	
		result = XmlPostParser.makeHttpPostRequest(url, jsonObject);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pDialog.dismiss();
		delegate.processFinish(result);

	}
}
