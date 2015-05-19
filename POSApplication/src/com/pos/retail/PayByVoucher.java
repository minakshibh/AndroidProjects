package com.pos.retail;


import java.io.IOException;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.pos.util.PosConstants;
import com.pos.util.ScreenReceiver;


public class PayByVoucher extends Activity {

	EditText editPassword,editUsername;
	TextView txtPassword,txtUsername,formTitle;
	Button btnCheck;
	ProgressDialog dialog;
	String SOAP_URL;
	String KEY_ITEM="IssuedVoucher",KEY_NAME="Active";
	private final String SOAP_NAMESPACE = "http://tempuri.org/";
	private final String SOAP_METHOD_NAME = "ValidateVouchers";
	private final String SOAP_ACTION = "http://tempuri.org/ValidateVouchers";
	private SoapObject request;
	Context mcontext;
	long curentTime=0;
	  long elapTime = 0;
	  SharedPreferences loginPref;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.login);
		
		mcontext=this;
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		editPassword = (EditText) findViewById(R.id.password);
		editUsername = (EditText) findViewById(R.id.userName);
		
		txtPassword = (TextView) findViewById(R.id.txtPassword) ;
		txtUsername = (TextView) findViewById(R.id.txtUserName) ;
		formTitle = (TextView) findViewById(R.id.formtitle);
 		btnCheck = (Button) findViewById(R.id.login);
 		
 		SOAP_URL=getResources().getString(R.string.liveurl)+"/"+SOAP_METHOD_NAME;
 		
 		request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_NAME);
 		 
 		
 		editPassword.setVisibility(View.GONE);
 		txtPassword.setVisibility(View.GONE);
 		formTitle .setVisibility(View.GONE);
 		
 		txtUsername.setText("Voucher Id");
 		
 		btnCheck.setText("Check Voucher Id");
 		
 		btnCheck.setOnClickListener(Listener);
		
	}
	OnClickListener Listener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.login:
					
					String str=editUsername.getText().toString();
					if(!str.equals(""));
					
					break;
	
			}
			
		}
	};
	
	public void checkValidVoucherNumber()
	{
		PropertyInfo pi2 = new PropertyInfo();
		 pi2.setName("VoucherNumber");
		 pi2.setValue("121212");
		 pi2.setType(String.class);
		request.addProperty(pi2);
		 
		SoapSerializationEnvelope envp = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		 envp.dotNet = true;
		 envp.setOutputSoapObject(request);
		 HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
		 try 
		 {
			 androidHttpTransport.call(SOAP_ACTION, envp);
			 SoapObject response = (SoapObject)envp.getResponse();
		     
			 Log.e("Response ", ""+response);
		 } 
		 catch (Exception e) 
		 {
			 Log.i("WS Error->",e.toString());
		 }
	}
	public void onPause()
	{
		super.onPause();
		
		if(!ScreenReceiver.wasScreenOn)
			curentTime =System.currentTimeMillis();
		
	}
	@Override
	public void onResume(){
		super.onResume();
		
		if(ScreenReceiver.wasScreenOn)
		{
			elapTime = System.currentTimeMillis();
			
			elapTime = (elapTime-curentTime);
			if(elapTime>PosConstants.TIMEOUT_IN_MS && elapTime<System.currentTimeMillis() || PosConstants.time_out )
			{
				
				PosConstants.time_out = true;
				Intent in =new Intent(mcontext,BreakActivity.class);
				startActivity(in);
			}
			Log.e("", ""+(elapTime));
		}
	}
	
	
	
	
	
	
}
