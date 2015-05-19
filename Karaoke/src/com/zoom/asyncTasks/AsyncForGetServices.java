package com.zoom.asyncTasks;


import com.karaoke.interfaces.AsyncResponse;
import com.zoom.xmlparser.XmlGetParser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;



public class AsyncForGetServices extends AsyncTask<String, Void, String> {

	private Activity activity;
	public AsyncResponse responseDelegate = null;
	private String result = "";	
	private ProgressDialog pDialog;
	private String url;


	public AsyncForGetServices(Activity activity, String url) {
		this.activity = activity;
		this.url = url;


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

		result = XmlGetParser.makeHttpRequest(url);
		return result;
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		pDialog.dismiss();
		responseDelegate.processFinish(result);

	}
}
