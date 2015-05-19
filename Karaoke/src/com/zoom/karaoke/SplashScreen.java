package com.zoom.karaoke;


import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import com.zoom.authentication.LoginActivity;
import com.zoom.dataController.SingleTon;

public class SplashScreen extends Activity {

	Handler handler=new Handler();
	public static int deviceWidth,deviceHeight;
	private SharedPreferences mSharedPreferences;	
	private String CREDIT_URL="http://www.zoomkaraokeapp.co.uk/service.asmx/AddCredits";
	private String userEmail;
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		getUserName();
		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13){			
			Point size = new Point();
			display.getSize(size);
			deviceWidth = size.x;
			deviceHeight = size.y;
		}else{
			deviceWidth = display.getWidth();  // deprecated
			deviceHeight = display.getHeight();  
		}
		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		String uuid = tManager.getDeviceId();
		System.out.println(uuid);
		ZoomKaraokeDatabaseHandler dbh=new ZoomKaraokeDatabaseHandler(this);
		try
		{
			dbh.createDataBase();

		}
	
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		new AsyncRequestCredit().execute();
		
		/*handler.postDelayed(new Runnable() {

			public void run() {
				
				mSharedPreferences=SplashScreen.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
				String str=mSharedPreferences.getString("user_email", null);
				if(str==null||str.equals("")){
					
					Intent mIntent=new Intent(SplashScreen.this,LoginActivity.class);
					SplashScreen.this.finish();
					startActivity(mIntent);
					
				}
				else {				
				Intent in=new Intent(SplashScreen.this,AvailableSong.class);
				SplashScreen.this.finish();
				startActivity(in);
				}

			}
		}, 2000);*/




	}
	
	public class AsyncRequestCredit extends AsyncTask<String, Void, String> {

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {	

			InputStream inputStream = null;
			String result = "";
			try {
				HttpClient httpclient = new DefaultHttpClient();
				String url=CREDIT_URL+"?user_email="+userEmail+"&Credit=0";
				HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
				inputStream = httpResponse.getEntity().getContent();	
				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				Thread.sleep(2000);
				
			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
		
			super.onPostExecute(result);			
			SingleTon.getInstance().setUserCredit(result);
			SingleTon.getInstance().setUserEmailID(userEmail);		
			if(userEmail==null||userEmail.equals("")){
				
				Intent mIntent=new Intent(SplashScreen.this,LoginActivity.class);
				SplashScreen.this.finish();
				startActivity(mIntent);
				
			}
			else {				
			Intent in=new Intent(SplashScreen.this,AvailableSong.class);
			SplashScreen.this.finish();
			startActivity(in);
			}
		}
	}

	public String convertInputStreamToString(InputStream inputStream) {	
		String Result="";
		try {		
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();		
			CreditXmlHandler saxHandler = new CreditXmlHandler(); 
			xmlReader.setContentHandler(saxHandler);
			InputSource ins= new InputSource(inputStream);
			ins.setEncoding("UTF-8");
			xmlReader.parse(ins);
			Result = saxHandler.getTotalCredits();

		} catch (Exception ex) {
			Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
		}
		return Result;
	}
	
	private void getUserName() {

		mSharedPreferences=SplashScreen.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		userEmail=mSharedPreferences.getString("user_email", null);

	}
}
