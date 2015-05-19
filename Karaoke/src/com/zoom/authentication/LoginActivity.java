package com.zoom.authentication;



import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.karaoke.util.ConnectionDetector;
import com.karaoke.util.DialogUtils;
import com.zoom.dataController.SingleTon;
import com.zoom.karaoke.AvailableSong;
import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import com.zoom.xmlparser.XmlGetParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{
	
	private ConnectionDetector detector;
	private EditText firstName,password;
	private Button btnLogin,btnRegister;
	private TextView forgotPswd;
	private SharedPreferences mSharedPreferences;	
	private Editor mEditor;
	private String CREDIT_URL="http://www.zoomkaraokeapp.co.uk/service.asmx/AddCredits";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		initialiseVariable();
		initialiseListener();
	}

	private void initialiseVariable() {
	
		firstName=(EditText)findViewById(R.id.firstName);
		password=(EditText)findViewById(R.id.password);
		btnLogin=(Button)findViewById(R.id.btnLogin);
		btnRegister=(Button)findViewById(R.id.btnRegister);
		forgotPswd=(TextView)findViewById(R.id.forgotPswd);
		
	}

	private void initialiseListener() {
	
		detector=new ConnectionDetector(LoginActivity.this);
		firstName.setOnClickListener(this);
		password.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		forgotPswd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.btnLogin:
			
			if(firstName.getText().toString().equals("")){
				
				showToast("Please Enter the Email");
				return;
			}
			if(password.getText().toString().equals("")){
				
				showToast("Please Enter the Password");
				return;
			}
			
			if(detector.isConnectingToInternet())
			{
				new AsyncServices().execute();		
			}
			else
			{
				showConnectionDialog();
			}
			
				
			break;
			
		case R.id.btnRegister:
			
			startActivity(new Intent(LoginActivity.this,RegisterNewUserActivity.class));	
		
			break;
			
		case R.id.forgotPswd:
			
			Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
			startActivity(intent);
			break;

		default:

			break;
		}
	}
	
	private void showConnectionDialog() {
		
		final AlertDialog alertdialog=new AlertDialog.Builder(LoginActivity.this).create();
		System.out.println("check connection");
		alertdialog.setTitle("Zoom Karaoke");
		alertdialog.setMessage("Internet connection not available");
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
			}
		});
		alertdialog.show();

	}

	public class AsyncServices extends AsyncTask<String, Void, String> {
		
		private String result = "";	
		private ProgressDialog pDialog;
	
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {	
		
			result = XmlGetParser.makeHttpRequest("http://www.zoomkaraokeapp.co.uk/service.asmx/LoginUser" 
									+"?email="+firstName.getText().toString().trim()
									+"&Password="+password.getText().toString().trim());
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {					
					
					try {
						String result;
						InputStream Stream = null; 					
						
						try {
							HttpClient httpclient = new DefaultHttpClient();
							String url=CREDIT_URL+"?user_email="+firstName.getText().toString().trim()+"&Credit=0";
							HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
							Stream = httpResponse.getEntity().getContent();
							
						} catch (ClientProtocolException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalStateException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
						if(Stream != null){
							result = convertInputStreamToString(Stream);
							SingleTon.getInstance().setUserCredit(result);
						}
						else
							result = "Did not work!";
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
			
			if(result.equals("Success")){
				
				SingleTon.getInstance().setUserEmailID(firstName.getText().toString().trim());
				saveLoginCredential();
				Intent mIntent=new Intent(LoginActivity.this,AvailableSong.class);
				startActivity(mIntent);
				finish();
			}
			else{
				
				password.setText("");
				DialogUtils.alert(LoginActivity.this,"Wrong username or password");
				
			}
			
			
		}

		protected String convertInputStreamToString(InputStream stream) {	
			String Result="";
			try {		
				XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();		
				CreditXmlHandler saxHandler = new CreditXmlHandler(); 
				xmlReader.setContentHandler(saxHandler);
				InputSource ins= new InputSource(stream);
				ins.setEncoding("UTF-8");
				xmlReader.parse(ins);
				Result = saxHandler.getTotalCredits();

			} catch (Exception ex) {
				Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
			}
			return Result;
		}
	}

	public void showToast(String string) {
		
		Toast.makeText(LoginActivity.this, string, Toast.LENGTH_LONG).show();
		
	}

	public void saveLoginCredential() {
		mSharedPreferences=LoginActivity.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		mEditor=mSharedPreferences.edit();
		mEditor.putString("user_email", firstName.getText().toString());
		mEditor.commit();
	}
	
	

}
