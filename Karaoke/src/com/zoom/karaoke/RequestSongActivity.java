package com.zoom.karaoke;



import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.ResponseXmlHandler;
import com.zoom.xmlResponseHandler.ResponseXmlHandlerForRequestedSong;
import com.zoom.xmlparser.XmlGetParser;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RequestSongActivity extends Activity implements OnClickListener{

	private EditText songName;
	private Button btnSendRequest;
	private SharedPreferences mSharedPreferences;
	private String userEmail;
	private ImageButton back;
    private String URL;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.request_song_activity);
		getUserName();
		initialiseVariable();
		initialiseListener();
	}

	private void getUserName() {

		mSharedPreferences=RequestSongActivity.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		userEmail=mSharedPreferences.getString("user_email", null);

	}

	private void initialiseVariable() {

		back=(ImageButton)findViewById(R.id.backRequestSong);
		songName=(EditText)findViewById(R.id.songName);
		btnSendRequest=(Button)findViewById(R.id.btnSendRequest);
	}

	private void initialiseListener() {

		btnSendRequest.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {


		case R.id.btnSendRequest:
			URL="http://www.zoomkaraokeapp.co.uk/service.asmx/RequestSong" 
					+"?SongName="+songName.getText().toString()
					+"&UserName=&User_Email="+userEmail;
			URL=URL.replace(" ", "%20");
			new AsyncRequestSong().execute();

			break;

		case R.id.backRequestSong:

			Intent intent=new Intent(RequestSongActivity.this,AvailableSong.class);
			startActivity(intent);
			finish();
			break;

		default:

			break;
		}

	}

	public class AsyncRequestSong extends AsyncTask<String, Void, String> {

		private String result = "";	
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = new ProgressDialog(RequestSongActivity.this);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {		

			InputStream inputStream = null;
			String result = "";
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpResponse httpResponse = httpclient.execute(new HttpGet(URL));
				inputStream = httpResponse.getEntity().getContent();	
				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result.equals("Success")){

				showToast("Your Request has been sent");
			}
			else{


			}
		}
	}

	public void showToast(String string) {

		Toast.makeText(RequestSongActivity.this, string, Toast.LENGTH_LONG).show();

	}

	public String convertInputStreamToString(InputStream inputStream) {	

		String Result="";

		try {

			// create a XMLReader from SAXParser
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			// create a SAXXMLHandler
			ResponseXmlHandlerForRequestedSong saxHandler = new ResponseXmlHandlerForRequestedSong();        	
			// store handler in XMLReader
			xmlReader.setContentHandler(saxHandler);
			// the process starts
			InputSource ins= new InputSource(inputStream);
			ins.setEncoding("UTF-8");
			xmlReader.parse(ins);
			// get the `Employee list`
			Result = saxHandler.getOtherData(); 

		} catch (Exception ex) {
			Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
		}

		// return Employee list
		return Result;
	}
	
public void onBackPressed() {
		
		Intent intent=new Intent(RequestSongActivity.this,AvailableSong.class);
		startActivity(intent);
		finish();
			
		
	}
}