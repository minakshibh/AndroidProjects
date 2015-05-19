package com.pos.retail;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.pos.util.Cashier;
import com.pos.util.ConnectionDetector;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.XMLParser;
import com.print.Utility;

public class LoginScreen extends Activity {

	TextView txtUserName,txtPassword,formTitle,txtOperatorName;
	EditText editName,editPassword,editOperatorName;
	Button btnLogin,btnCancel;
	Context mcontext;
	ProgressDialog pDialog;
	String currencySettingUrl,Method_Currency="GetCurrencySetting";
	boolean isLoginActivity=true;
	String producturl,Method_Name="GetProductList", loginurl ,checkLoginStatusUrl,Method_NAMELOGIN="WebServiceLogin";
	ConnectionDetector detector;
	String userName,userPassword;
	SharedPreferences loginPref;
	SharedPreferences posPref;
	Editor posEditor;
	String method_name="CashierList";
	String trigger;
	ProgressDialog dialog;
	SharedPreferences pref;
	String headerLine1="",headerLine2="",headerLine3="",headerLine4="",headerLine5="",itemText="";
	String footerLine1="",footerLine2="",footerLine3="",footerLine4="",logo="";
	Editor e;
	String errormsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		
		pref=getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		e=pref.edit();
	
		
		trigger = getIntent().getStringExtra("trigger");
		if(trigger == null)
			trigger = "";
				
		mcontext = this;
		producturl=getResources().getString(R.string.liveurl)+"/"+Method_Name;
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String uuid =telephonyManager.getDeviceId();
		
		 System.out.println("Uudi "+uuid );
		 PosConstants.activity_code =1;
		
		 PosConstants.time_out = false;
		 long millis = System.currentTimeMillis();
		 
		 posPref = getSharedPreferences("pos", MODE_PRIVATE);
		 posEditor = posPref.edit();
		 
		 checkLoginStatusUrl = getResources().getString(R.string.liveurl)+"/"+Method_NAMELOGIN;
		 Log.i("tag", "checkLoginStatusUrl: "+checkLoginStatusUrl);
		 
		 
		 currencySettingUrl = getResources().getString(R.string.liveurl)+"/"+Method_Currency;
		 Log.i("tag", "currencySettingUrl: "+currencySettingUrl);
		 Log.e("Long ", ""+millis);
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		isLoginActivity=true;
		detector = new ConnectionDetector(mcontext);
		editName = (EditText) findViewById(R.id.userName);
		editPassword = (EditText) findViewById(R.id.password);
		
		btnLogin = (Button)findViewById(R.id.login);
		btnCancel = (Button)findViewById(R.id.cancel);
		
		txtUserName=(TextView)findViewById(R.id.txtUserName);
		txtPassword=(TextView)findViewById(R.id.txtPassword);
		formTitle=(TextView)findViewById(R.id.formtitle);
		
		txtOperatorName=(TextView)findViewById(R.id.txtOperatorName);
		editOperatorName=(EditText)findViewById(R.id.operatorName);

		if(trigger.equalsIgnoreCase("")){
			
			String strcheck = loginPref.getString("UserName", "");
			
			
			txtOperatorName.setVisibility(View.GONE);
			editOperatorName.setVisibility(View.GONE);
			isLoginActivity = true;
		}else{
			
			loginPref.edit().putString("OperatorDecelaration", null).commit();
			
			String name =loginPref.getString("UserName", "");
			
			editName.setText(name);
			editName.setEnabled(false);
			
			editPassword.setText("");
			
			editPassword.requestFocus();
			formTitle.setText("Operator Sign-On");
			txtUserName.setText("Sign-On Name :");
			txtPassword.setText("Pin :");
			btnCancel.setVisibility(View.VISIBLE);
			btnCancel.setText("Cancel");
			btnLogin.setText("Sign On");
			isLoginActivity = false;
		}
		
		btnLogin.setOnClickListener(Listener);
		btnCancel.setOnClickListener(Listener);
		
		loginurl = getResources().getString(R.string.liveurl)+"/"+method_name;
		 Log.i("tag", "loginurl: "+loginurl);

			//Check internet connection
			if(detector.isConnectingToInternet())
			{
				//Loading the new cashier list
				new synchronizeCashierList().execute();
			}
			else
			{
				
				PoSDatabase db = new PoSDatabase(mcontext);
				ArrayList<Cashier> listCahier = db.getAllUsers();
				
				if(listCahier.size()==0)
				{
					final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
					alertdialog.setMessage("Internet connection is not available");
					alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
		
							finish();
						}
					});
					alertdialog.show();
				}
			}
			
	}
/*	class SyncProductListing extends AsyncTask<String, String, String>
	{
		int result=1;
		ArrayList<ProductReal> productList =null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Loading products");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			
			int timestamp=posPref.getInt("producttimestamp", -1);	
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(producturl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("timestamp",Integer.toString(-1)));
	        
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		     is= entity.getContent();
			
		     productList= com.pos.util.SAXXMLParser.parseProductDetail(is);
		     Map<String, String> map=com.pos.util.SAXXMLParser.map;

		     System.out.println(map);
		     result=Integer.parseInt(map.get("result"));
		     timestamp=Integer.parseInt(map.get("timestamp"));
		     
		     if(result==0)
		     {
		    	 posEditor.putInt("producttimestamp", timestamp).commit();
		     }
		     Log.e("gggg", ""+productList);
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
	    	
	    	
	    	if(result==0 && productList !=null)
		     {
		    	 
	    		
		    	 for(int i=0;i<productList.size();i++)
		    	 {
		    		 
		    		 ProductReal product=productList.get(i);
		    		 PoSDatabase db=new PoSDatabase(mcontext);
		    		 
		    		 if(product.isDeleted())
		    		 {
//		    			 db.deleteProduct(product.getPid());
//		    			 productList.remove(i);
		    		 }
		    		 else
		    		 {
		    			 db.addProduct(product);
		    		 }
		    		 
		    	 }
	    		PoSDatabase db=new PoSDatabase(mcontext);
				productList=db.getAllProducts();
		     }
	    	
	    	new CurrencySetting().execute();
	    	
		}
	}
	*/
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			
			switch (v.getId()) 
			{
				case R.id.login:
					
					
					if(detector.isConnectingToInternet())
					{
					
					 userName = editName.getText().toString();
					 userPassword = editPassword.getText().toString();
					
							if(userName.equalsIgnoreCase("") || userPassword.equalsIgnoreCase(""))
							{
								
								if(isLoginActivity)
								editName.setText("");
								editPassword.setText("");
								
								showDialog("Please enter credentials");
							}
							else
							{
											
											if(isLoginActivity)
												new checkLoinWithServer().execute(userName,userPassword);
											else
											{
//											
												int tempUserId=loginPref.getInt("UserId", -1);
												System.out.println("User id "+tempUserId); 
												
												String userPassword =loginPref.getString("Password", "");
												String password =editPassword.getText().toString();
												
												if(password.equals(userPassword))
												{
													Intent intent=new Intent(mcontext,FloatCashActivity.class);
													intent.putExtra("trigger", "operatorDeclaration");
													startActivity(intent);	
													finish();
												}
												else
												{
													if(isLoginActivity){
														editName.setText("");
													    editPassword.setText("");
													}
													showDialog("Please enter valid password");
												}
											}
										
							}
					}
					else
					{
						showDialog("Internet connection is not available");
						
					}
					break;
				case R.id.cancel:
					finish();
					break;
			}
		}
	};
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	class synchronizeCashierList extends AsyncTask<String, String, String>
	{
		
		Map<String,String> map;
		ArrayList<Cashier> cashierList;
		int result=1;
		String xml;
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			    	int timeStamp=posPref.getInt("timestamp", -1);
					InputStream is;
				
					System.out.println(loginurl);
					DefaultHttpClient httpClient = new DefaultHttpClient();
			        HttpPost requestLogin = new HttpPost(loginurl);
			         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			         nameValuePairs.add(new BasicNameValuePair("timestamp", Integer.toString(-1)));
			        
			   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				     HttpResponse response = httpClient.execute(requestLogin);
				     HttpEntity entity = response.getEntity();
				     is= entity.getContent();
					
				     cashierList= com.pos.util.SAXXMLParser.parseUserDetail(is);
				     Map<String, String> map=com.pos.util.SAXXMLParser.map;
//				     
				    
				     Log.e("Is ", ""+is);
				     Log.e("gggg", ""+cashierList);
				     Log.e("gggg", ""+map);
				     
				     result=Integer.parseInt(map.get("result"));
				     
				     timeStamp=Integer.parseInt(map.get("timestamp"));
				     
				     if(result==0)
				     {
				    	 
				    	 for(int i=0;i<cashierList.size();i++)
				    	 {
				    		 
				    		 Cashier cashier=cashierList.get(i);
				    		 
				    		 if(cashier.isDeleted())
				    		 {
				    			 PoSDatabase db=new PoSDatabase(mcontext);
				    			 cashierList.remove(i);
					    		 db.deleteCashier(cashier.getId());
				    			 
				    		 }
				    		 else
				    		 {
					    		 PoSDatabase db=new PoSDatabase(mcontext);
					    		 Cashier tempCashier =db.checkUserExist(cashier.getUsername());
					    		 
					    		 if(tempCashier ==null)
					    			 db.addUser(cashier);
					    		 else
					    			 db.updateUser(cashier, cashier.getId());
				    		 }
				    	 }
				    	 
				    	 posPref.edit().putInt("timestamp",timeStamp).commit();
				     }
				     
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
	}
	
	//Show alert dialog 
	public void showDialog(final String str)
	{
		final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
		
		String title=getResources().getString(R.string.pos);
		alertdialog.setTitle(title);
		alertdialog.setMessage(str);
		alertdialog.setPositiveButton("OK", null);
		alertdialog.show();
	}
	
	class CurrencySetting extends AsyncTask<String, String, String>
	{
		int result=1;
		ArrayList<com.pos.util.CurrencySetting> currencySettingList =null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Loading Currency Settings");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			
			
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(currencySettingUrl);
	       
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		     is= entity.getContent();
			
		     currencySettingList = com.pos.util.SAXXMLParser.parseCurrencySetting(is);
		     Map<String, String> map=com.pos.util.SAXXMLParser.map;

		     System.out.println(map);
		     result=Integer.parseInt(map.get("result"));
		     
		     
		     
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
	    	
	    	if(result==0 && currencySettingList !=null)
		     {
		    	 
	    		
		    	 for(int i=0;i<currencySettingList.size();i++)
		    	 {
		    		 
		    		com.pos.util.CurrencySetting currSetting = currencySettingList.get(i);
		    		 PoSDatabase db=new PoSDatabase(mcontext);
		    		
		    		 int currId = currSetting.getCurrencyId();
		    		 com.pos.util.CurrencySetting  tempCurrSetting = db.getCurrencySetting(currId);
		    		 
		    		 if(tempCurrSetting ==null)
		    		 {
		    			 
		    			 db.addCurrencySetting(currSetting);
		    			 
		    		 }
		    		 else
		    		 {
		    			 
		    			 db.updateCurrencySetting(currSetting, currId);
		    			 
		    		 }
		    		 
		    	 }
		    	 
		  
		     }
	    	
	    	SharedPreferences transactionPrefs = LoginScreen.this.getSharedPreferences("transPref", MODE_PRIVATE);
	    	Editor transEditor = transactionPrefs.edit();
	    	
	    	transEditor.putString("transAmount", "0");
	    	transEditor.commit();
	    	
	    	Intent intent=new Intent(mcontext,FloatCashActivity.class);											
			startActivity(intent);	
			finish();
		}
		
	}
	public class checkLoinWithServer extends AsyncTask<String, String, String>
	{
		
		int result=1,userId=-1,rollId;
		
		String username,password, status="";
		ArrayList<com.pos.util.CurrencySetting> currencySettingList =null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Please wait for login ");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			
			
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(checkLoginStatusUrl);
	       
	        userName =params[0];
	        password = params[1];
	        
	        
	        Log.e("username", userName+"  "+password);
	        
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("UserName",params[0]));
	         nameValuePairs.add(new BasicNameValuePair("Password",params[1]));
	         nameValuePairs.add(new BasicNameValuePair("status","Sign On"));
	         
	         TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
	         String uuid =telephonyManager.getDeviceId();
	         nameValuePairs.add(new BasicNameValuePair("UID",uuid));
	         nameValuePairs.add(new BasicNameValuePair("posId",String.valueOf(1)));
	         
	         requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		    
		
		     String  xml = EntityUtils.toString(entity);
		     errormsg ="Got Response: "+xml;
		     
		     Log.e("tag", "Response: "+xml);
//			 
		     XMLParser parser= new XMLParser();
		     Document doc =parser.getDomElement(xml);
		     NodeList nl = doc.getElementsByTagName("EmployeesData");
		     
		     for(int i=0;i<nl.getLength();i++)
		     {
		    	 Element e =(Element)nl.item(i);
		    	 status =parser.getValue(e, "UserLoginStatus");
		    	 userId = Integer.parseInt(parser.getValue(e, "UserId"));
		    	 rollId = Integer.parseInt(parser.getValue(e, "UserRoleId"));
		    }
		     NodeList n2 = doc.getElementsByTagName("HeaderDetails");
		     for(int i=0;i<n2.getLength();i++)
		     {
		    	 Element e =(Element)n2.item(i);
		    	 headerLine1 =parser.getValue(e, "Line1");
		    	 headerLine2 =parser.getValue(e, "Line2");
		    	 headerLine3 =parser.getValue(e, "Line3");
		    	 headerLine4 =parser.getValue(e, "Line4");
		    	 headerLine5 =parser.getValue(e, "Line5");
		    	 itemText=parser.getValue(e, "ItemText");
		    	 
		    	 Log.i("tag", "HeaderDetails Line1:"+headerLine1);
		    	 Log.i("tag", "HeaderDetails Line2:"+headerLine2);
		    	 Log.i("tag", "HeaderDetails Line3:"+headerLine3);
		    	 Log.i("tag", "HeaderDetails Line4:"+headerLine4);
		    	 Log.i("tag", "HeaderDetails Line5:"+headerLine5);
		    	 Log.i("tag", "ItemText :"+itemText);
		    }
		     NodeList n3 = doc.getElementsByTagName("FooterDetails");
		     for(int i=0;i<n3.getLength();i++)
		     {
		    	 Element e =(Element)n3.item(i);
		    	 footerLine1=parser.getValue(e, "Line1");
		    	 footerLine2 =parser.getValue(e, "Line2");
		    	 footerLine3 =parser.getValue(e, "Line3");
		    	 footerLine4 =parser.getValue(e, "Line4");
		    	
		    	 
		    	 Log.i("tag", "FooterDetails Line1:"+footerLine1);
		    	 Log.i("tag", "FooterDetails Line2:"+footerLine2);
		    	 Log.i("tag", "FooterDetails Line3:"+footerLine3);
		    	 Log.i("tag", "FooterDetails Line4:"+footerLine4);
		    	
		    		    	 
		    }
		     NodeList n4 = doc.getElementsByTagName("Logo");
		     logo=n4.item(0).getTextContent();
		     Log.i("tag", "logo Details :"+logo);
		     
		     errormsg =errormsg+" ,, Response parsed successfully";
		    }
			catch(Exception e)
			{
				Log.i("tag", "Error :"+e.getMessage());
				userId = -1;
				
				StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				
				errormsg =errormsg+" ,,ERROR :: "+e.getMessage()+",, STACKTRACE:  "+exceptionAsString;
				
			}
			
			return null;
		}
		
		
		@SuppressLint("SimpleDateFormat")
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	
	    	if(userId >0)
	    	{
		    	if(status.equals("") )
		    	{
					SimpleDateFormat sdf1 = new SimpleDateFormat("dd/mm/yyyy");
					String date =sdf1.format(new Date());
					
					SharedPreferences.Editor edit = loginPref.edit();
					edit.putString("UserName",userName );
					edit.putString("Password", password);
					edit.putInt("UserId", userId);
					edit.putInt("UserRollId",rollId);
					
					/******header Detail & footer Detail**********/
					e.putString(Utility.HeaderLine1, headerLine1);
					e.putString(Utility.HeaderLine2, headerLine2);
					e.putString(Utility.HeaderLine3, headerLine3);
					e.putString(Utility.HeaderLine4, headerLine4);
					e.putString(Utility.HeaderLine5, headerLine5);
					e.putString(Utility.ITEMText, itemText);
					
					e.putString(Utility.FooterLine1, footerLine1);
					e.putString(Utility.FooterLine2, footerLine2);
					e.putString(Utility.FooterLine3, footerLine3);
					e.putString(Utility.FooterLine4, footerLine4);
					e.putString(Utility.ImageData, logo);
					e.commit();
					
					/*******************/
					
					edit.commit();	
					if(isLoginActivity){
						
//						if(detector.isConnectingToInternet() )
//							new SyncProductListing().execute();
						new CurrencySetting().execute();
						/*else
						{
							Intent intent=new Intent(mcontext,FloatCashActivity.class);											
							startActivity(intent);	
							finish();
						}*/
					}else{
						Intent intent=new Intent(mcontext,FloatCashActivity.class);
						intent.putExtra("trigger", "operatorDeclaration");
						startActivity(intent);	
						finish();
					}
		    	
		    	}
		    	else
				{
					
		    		editName.setText("");
					editPassword.setText("");
					editName.requestFocus();
					showDialog("This user already login on other system");
		    		
				}
			}
	    	else
	    	{
	    		editName.setText("");
				editPassword.setText("");
				editName.requestFocus();
				if(userId== -1){
					showDialog(errormsg);
//					showDialog("There seems to be some network error. Kindly check your internet connection and please try again later.");
				}else{
					showDialog("Please enter valid credentials.");
				}
	    	}
		}
		
	}
}
