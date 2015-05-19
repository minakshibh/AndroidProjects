package com.pos.retail;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.posapplication.R;

public class TrxSettings extends Activity {
   
	private TextView txtHeader;
	private EditText txtUserName, txtDomain, txtPassword, txtLocalServerPath, txtRemoteServerPath;
	private String trigger = "trx";
	private ProgressDialog dialog;
	private String getTrxDetailUrl, updateTrxDetailUrl;
	private String userName ="", domain="", password="", localServerPath="", remoteServerPath="", trxSettingId="", result="", message="";
	private Button done, back;
	
	@Override
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.trx_settings);
		
		trigger = getIntent().getStringExtra("trigger");
		if(trigger == null)
			trigger = "trx";
		
		txtHeader = (TextView)findViewById(R.id.txtHeader);
		txtUserName = (EditText)findViewById(R.id.txtUsername);
		txtDomain = (EditText)findViewById(R.id.txtDomain);
		txtPassword = (EditText)findViewById(R.id.txtPassword);
		txtLocalServerPath = (EditText)findViewById(R.id.localServerPath);
		txtRemoteServerPath = (EditText)findViewById(R.id.remoteServerPath);
		done = (Button)findViewById(R.id.done);
		back = (Button)findViewById(R.id.back);
		
		done.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				new UpdateTrxDetails().execute("");
			}
		});
		
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
		if(trigger.equals("trx")){
			getTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"GetTrxServerData";
			txtHeader.setText("Trx Server Settings");
		}else{
			getTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"GetmasterServerConfigData";
			txtHeader.setText("Master Server Config Settings");
		}
		new GetTrxDetails().execute("");

	}
	
	
	class UpdateTrxDetails extends AsyncTask<String, String, String>
	{
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(TrxSettings.this);
	    	
	    	if(trigger.equals("trx"))
	    		dialog.setMessage("Updating Trx Details");
	    	else
	    		dialog.setMessage("Updating Master Trx Details");
	    	
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try
			{
			InputStream is = null;
			URL url = new URL(getTrxDetailUrl);
            
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(updateTrxDetailUrl);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         
	        if(!trxSettingId.equals("0"))
	        	nameValuePairs.add(new BasicNameValuePair("ID",trxSettingId));
	        
	         nameValuePairs.add(new BasicNameValuePair("UserName",txtUserName.getText().toString().trim()));
	         nameValuePairs.add(new BasicNameValuePair("Domain",txtDomain.getText().toString().trim()));
	         nameValuePairs.add(new BasicNameValuePair("Password",txtPassword.getText().toString().trim()));
	         nameValuePairs.add(new BasicNameValuePair("LocalServerpath",txtLocalServerPath.getText().toString().trim()));
	         nameValuePairs.add(new BasicNameValuePair("RemoteServerpath",txtRemoteServerPath.getText().toString().trim()));
	         
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
			 is= entity.getContent();
			 
			}
			catch(Exception e)
			{
				
				e.printStackTrace();
			}
			
			return null;
		}
		@SuppressLint("SimpleDateFormat")
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	AlertDialog.Builder alert = new AlertDialog.Builder(TrxSettings.this);
	    	alert.setTitle("POS");
	    	alert.setMessage("Settings updated successfully");
	    	alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
	    	alert.show();
		}
	}
	
	class GetTrxDetails extends AsyncTask<String, String, String>
	{
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(TrxSettings.this);
	    	
	    	if(trigger.equals("trx"))
	    		dialog.setMessage("Getting Trx Details from server");
	    	else
	    		dialog.setMessage("Getting Master Trx Details from server");
	    	
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			try
			{
			InputStream is = null;
			URL url = new URL(getTrxDetailUrl);
            
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(getTrxDetailUrl);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         
	        /* nameValuePairs.add(new BasicNameValuePair("Trnsid",params[0]));
	         nameValuePairs.add(new BasicNameValuePair("Posid","1"));
	         nameValuePairs.add(new BasicNameValuePair("CreatedDate",params[2]));
	         nameValuePairs.add(new BasicNameValuePair("returnStatus",returnStatus));*/
	         
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
			 is= entity.getContent();
			 
			 
			    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	            DocumentBuilder db = dbf.newDocumentBuilder();
	            Document doc = db.parse(is);
	            doc.getDocumentElement().normalize();

	            NodeList nodeList;
	            if(trigger.equals("trx"))
	            	nodeList = doc.getElementsByTagName("TrxServerData");
	            else
	            	nodeList = doc.getElementsByTagName("MasteServerSetings");
	            
	            NodeList settingNodeList = doc.getElementsByTagName("TrxServerSetings");
	            
	            for (int i = 0; i < nodeList.getLength(); i++) {
	            	Element e = (Element) nodeList.item(i);
	            	trxSettingId = getValue(e, "ID");
	            	userName = getValue(e, "UserName");
	            	password = getValue(e, "Password");
	            	domain = getValue(e, "Domain");
	            	localServerPath = getValue(e, "LocalServerpath");
	            	remoteServerPath = getValue(e, "RemoteServerpath");
	            }
	            
	            Element resultElement = (Element) settingNodeList.item(0);
	            
	            result = getValue(resultElement, "Result");
	            message = getValue(resultElement, "Message");
			}
			catch(Exception e)
			{
				
				e.printStackTrace();
			}
			
			return null;
		}
		@SuppressLint("SimpleDateFormat")
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	if(result.equals("0")){
		    	txtUserName.setText(userName);
		    	txtDomain.setText(domain);
		    	txtPassword.setText(password);
		    	txtLocalServerPath.setText(localServerPath);
		    	txtRemoteServerPath.setText(remoteServerPath);
		    	
		    	if(trigger.equals("trx")){
		    		if(trxSettingId.equals("0"))
		    			updateTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"InsertTrxServerData";
		    		else
		    			updateTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"UpdateTrxServerData";
				}else{
					if(trxSettingId.equals("0"))
		    			updateTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"InsertmasterServerConfigData";
		    		else
		    			updateTrxDetailUrl = getResources().getString(R.string.liveurl)+"/"+"UpdatemasterServerConfigData";
				}
		    	
	    	}else{
	    		AlertDialog.Builder alert = new AlertDialog.Builder(TrxSettings.this);
	    		alert.setTitle("Something went wrong");
	    		alert.setMessage(message);
	    		alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						finish();
					}
				});
	    		alert.show();
	    	}
		}
	}
	
	public String getValue(Element item, String str) {      
		try{
		    NodeList n = item.getElementsByTagName(str);        
		    return this.getElementValue(n.item(0));
		}catch(Exception e){
			return "0";
		}
	}
	 
	public final String getElementValue( Node elem ) {
	         Node child;
	         if( elem != null){
	             if (elem.hasChildNodes()){
	                 for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
	                     if( child.getNodeType() == Node.TEXT_NODE  ){
	                         return child.getNodeValue();
	                     }
	                 }
	             }
	         }
	         return "";
	  } 
}
