package com.restedge.utelite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.restedge.utelite.R;

public class LoginRegisterScreen extends Activity{
	
	// Declaration of layout views.........
	SharedPreferences userPreference;
	TextView  dialogMessage, okButton, cancelButton, placeName;
	EditText UserName, Password, ConfirmPassword;
	Button Login, Register;
	ImageButton backButton;
	
	Boolean isRegister=false;
	private String loginRegisterUrl;
	
	Dialog settingsDialog;
	String[] loginList;
	String emailStr, methodName;
	ProgressDialog dialog;
	
	int sdk = android.os.Build.VERSION.SDK_INT;
	
	@TargetApi(Build.VERSION_CODES.FROYO)
	public void onCreate(Bundle b){
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Intent intent=getIntent();
		String Name=intent.getStringExtra("Name");
		
		// Restration of layout views.........
		setContentView(R.layout.loginregisterscreen);	
		UserName=(EditText)findViewById(R.id.username);
		Password=(EditText)findViewById(R.id.password);
		ConfirmPassword=(EditText)findViewById(R.id.confirmpassword);
		
		placeName=(TextView)findViewById(R.id.headername);
		
		
		Login=(Button)findViewById(R.id.login);
		Register=(Button)findViewById(R.id.register);
		backButton=(ImageButton)findViewById(R.id.backbtn);
		methodName="RegisterLogin";
		loginRegisterUrl = getResources().getString(R.string.app_url)+"/"+methodName;		
		userPreference = this.getSharedPreferences("UserPreference", MODE_PRIVATE); 	
		
		 settingsDialog= new Dialog(this); 
		 settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
		 LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 ViewGroup parent=(ViewGroup) inflater.inflate(R.layout.dialoglayout, null);
		 View dialoglayout = inflater.inflate(R.layout.dialoglayout, parent, false);
		 settingsDialog.setContentView(dialoglayout); 
		 placeName.setText("Edge");
		 
		okButton=(TextView)dialoglayout.findViewById(R.id.oklbtn);		
		dialogMessage=(TextView)dialoglayout.findViewById(R.id.dialogmessage);
		cancelButton=(TextView)dialoglayout.findViewById(R.id.cancelbtn);	
		
		 // Click listner for the views (For Buttons and clickable images).....		
		 View.OnClickListener listener = new View.OnClickListener() {
	            @SuppressWarnings("deprecation")
				@SuppressLint("NewApi")
				public void onClick(View v) {
	              // it was the 1st button
	            	switch (v.getId()) {
	            	
					case  R.id.login:
						
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							Login.setBackgroundDrawable(getResources().getDrawable(R.drawable.outer));
						} else {
							Login.setBackground(getResources().getDrawable(R.drawable.outer));
						}
						
						String usernameStr=UserName.getText().toString();
						String passwordStr=Password.getText().toString();
						//if(isEmailValid(usernameStr) && !passwordStr.trim().equals("")){	
						if(!usernameStr.trim().equals("") && !passwordStr.trim().equals("")){	
							
						new RequestTask().execute(loginRegisterUrl, "Login", usernameStr, passwordStr, usernameStr);	
						
						}else{
							dialogMessage.setText("Please enter valid Credantials");
							cancelButton.setVisibility(View.GONE);
							settingsDialog.show();
						}
						
						break;						
					
					case  R.id.register:
						
						
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							Register.setBackgroundDrawable(getResources().getDrawable(R.drawable.outer));	
						} else {
							Register.setBackground(getResources().getDrawable(R.drawable.outer));
						}
						
						if(!isRegister){
							UserName.setHint("User Email");
							Password.setHint("Password");
							ConfirmPassword.setVisibility(View.VISIBLE);
							Login.setVisibility(View.GONE);
							isRegister=true;
							//Toast.makeText(getApplicationContext(), "UsernameStr : "+usernameStr+" , passwordStr :"+passwordStr, Toast.LENGTH_LONG).show();
							}else{
								emailStr=UserName.getText().toString();
								
								String rPasswordStr=Password.getText().toString();							
								String ConfirmpasswordStr=ConfirmPassword.getText().toString();
								
								if(!emailStr.trim().equals("") && !rPasswordStr.trim().equals("") ){
									
								if( emailStr.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))
								{
									if(!emailStr.trim().equals("")){
									
							         if(!rPasswordStr.trim().equals("")){
							        	 
							        	 if(rPasswordStr.equals(ConfirmpasswordStr)){
							        	   // Toast.makeText(getApplicationContext(), "Registration Okay", Toast.LENGTH_LONG).show();
							        	   
							        	    UserName.setHint("User Name");
											Password.setHint("Password");
											ConfirmPassword.setVisibility(View.GONE);
											Login.setVisibility(View.VISIBLE);
											UserName.setText("");
											Password.setText("");											
											new RequestTask().execute(loginRegisterUrl, "Register", emailStr, rPasswordStr, emailStr); 
											isRegister=false;
												
							        	   }else{
							        		   //Toast.makeText(getApplicationContext(), "Password not Match", Toast.LENGTH_LONG).show();	
							        		    dialogMessage.setText("Password not Match");
							        		    cancelButton.setVisibility(View.GONE);
												settingsDialog.show();												
							        	   }
							        	 
							            }else{
							            	    dialogMessage.setText("Password should not be empty");
							            	    cancelButton.setVisibility(View.GONE);
												settingsDialog.show();
							            	 //Toast.makeText(getApplicationContext(), "Password should not be empty", Toast.LENGTH_LONG).show();											
							            }						         
							         
									}else{		
										    dialogMessage.setText("Please Enter User Name");
										    cancelButton.setVisibility(View.GONE);
											settingsDialog.show();
										// Toast.makeText(getApplicationContext(), "Please Enter valid email", Toast.LENGTH_LONG).show();										
									}
								}
								else
								{
									dialogMessage.setText("Please Enter Valid Email");
								    cancelButton.setVisibility(View.GONE);
									settingsDialog.show();
								}
								}else{
									 dialogMessage.setText("Please Enter  credentials");
									 cancelButton.setVisibility(View.GONE);
										settingsDialog.show();
									 //Toast.makeText(getApplicationContext(), "Please Enter credantials", Toast.LENGTH_LONG).show();									
								}
							}
						break;
						
						
					case  R.id.oklbtn:
						okListener();
						break;	
						
					case  R.id.cancelbtn:
						//cancelListener();	
						break;	
						
					case  R.id.backbtn:
						if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
							backButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_button_selector));	
						} else {
							backButton.setBackground(getResources().getDrawable(R.drawable.back_button_selector));	
						}
										
						finish();	
						break;	
						
						
						}				
					
					}	           
	          };
	          
	         Login.setOnClickListener(listener);          	   
	         Register.setOnClickListener(listener); 
	         okButton.setOnClickListener(listener);          	   
	         cancelButton.setOnClickListener(listener);	
	        backButton.setOnClickListener(listener);	
	}
	
	public void cancelListener(){
		settingsDialog.dismiss();
		
	}
	
    public void okListener(){		
    	settingsDialog.dismiss();
	}
	
	
//	public boolean isEmailValid(String email){
//        String regExpn =
//            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
//                +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//                  +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
//                  +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
//                  +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
//                  +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
//
//    CharSequence inputStr = email;
//    Pattern pattern = Pattern.compile(regExpn,Pattern.CASE_INSENSITIVE);
//    Matcher matcher = pattern.matcher(inputStr);
//
//    if(matcher.matches())
//       return true;
//    else
//       return false;
//	}
	
	
	class RequestTask extends AsyncTask<String, String, String>{
		
		@Override
	    protected void onPreExecute() {	    	
	    	super.onPreExecute();	 
	    	
	    	 // Progress bar..........
			 dialog=new ProgressDialog(LoginRegisterScreen.this);
			 dialog.setTitle("Edge");
		     dialog.setMessage("Please wait");
		     dialog.setCanceledOnTouchOutside(false);	
	    	 dialog.show();
	    	
	    }

	    @Override
	    protected String doInBackground(String... uri) {     
	        try {	
	        	
	            DefaultHttpClient httpClient = new DefaultHttpClient();	            
	            HttpPost requestLogin = new HttpPost(uri[0]);
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("Register", uri[1]));
	            nameValuePairs.add(new BasicNameValuePair("Username", uri[2]));
	            nameValuePairs.add(new BasicNameValuePair("Password", uri[3]));
	            nameValuePairs.add(new BasicNameValuePair("EmailAddress", uri[4]));
	           // nameValuePairs.add(new BasicNameValuePair("password", password));
	            
	            requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            HttpResponse response = httpClient.execute(requestLogin);
	            
	            HttpEntity entity = response.getEntity();
	            String content = EntityUtils.toString(entity);
	          //  Log.e("Get Comment", "Comment : "+content);        
	            loginList =parseLoginResponse(content);
	         //  Looper.prepare();                 
	            return content;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return  null;
	        }       
	    }

	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        
	        dialog.dismiss();
	        //Do anything with response..
	        if(loginList[0].equals("0")){
	        	 Editor editor = userPreference.edit();
	 			 editor.putString("LoginResult", loginList[1]);
	 			 editor.putString("RegisterResult", loginList[1]);	
	 			 editor.putString("UserId", loginList[2]);
	 			 editor.commit();				
				// Log.e("Get Comment", "loginList[0] : "+loginList[0]+", loginList[0] : "+loginList[1] +"UserId"+ loginList[2]);
				 //Toast.makeText(getApplicationContext(), loginList[1]+"UserId "+ loginList[2], Toast.LENGTH_LONG).show();
				
				 if(loginList[1].equals("Login Successful")){
				     finish();
				  }else if(loginList[1].equals("Registration Successful")){
					 Editor editor1 = userPreference.edit();
					 editor1.putString("LoginResult", "Login Successful");
		 			 editor1.putString("RegisterResult", loginList[1]);	
		 			 editor1.putString("UserId", loginList[2]);	
		 			 editor1.commit();
		 			 finish();
				  }else if(loginList[1].equals("User exist,please try other username")){
					  dialogMessage.setText("User exist,please try other username");
						 cancelButton.setVisibility(View.GONE);
							settingsDialog.show();
						//Toast.makeText(getApplicationContext(), "User exist, please try other username", Toast.LENGTH_LONG).show();
				}else if(loginList[1].equals("Login failed for user "+emailStr)){
					 dialogMessage.setText("Login failed for user "+emailStr);
					 cancelButton.setVisibility(View.GONE);
						settingsDialog.show();
					//Toast.makeText(getApplicationContext(), "Login failed for user "+emailStr, Toast.LENGTH_LONG).show();
			   }
			}else{				
				 dialogMessage.setText(loginList[1]);
				 cancelButton.setVisibility(View.GONE);
					settingsDialog.show();
				///Log.e("Get Comment", "loginList[0] : "+loginList[0]+", loginList[0] : "+loginList[1]);
				//Toast.makeText(getApplicationContext(), loginList[1], Toast.LENGTH_LONG).show();
			}
	       
	    }
	}
	
	
	public String[] parseLoginResponse(String response)
	{
		String[] strArray = new String[3];
		try{
			InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(is);
	        Element root = doc.getDocumentElement();
	        NodeList resultItem = root.getElementsByTagName("result");
	        String result = resultItem.item(0).getFirstChild().getNodeValue();
	        
	        strArray[0] = result;
	        
	        if(result.equals("0"))
	        {
	        	Log.e("Login","successful");
	        	NodeList messageItem = root.getElementsByTagName("message");
	        	NodeList UserIdItem = root.getElementsByTagName("UserId");
		        String message = messageItem.item(0).getFirstChild().getNodeValue();
		        String UserId = UserIdItem.item(0).getFirstChild().getNodeValue();
		        strArray[1] = message;	
		        strArray[2]=   UserId;
		        Log.e("Login User Id","successful , "+message +" User Id: "+UserId);
	        }else{
	        	Log.e("Login","failed");
	        	NodeList messageItem = root.getElementsByTagName("message");
		        String message = messageItem.item(0).getFirstChild().getNodeValue();
		        strArray[1] = message;		       
		        Log.e("Error message",message);
	        }	
		}catch(Exception e)
		{
			e.printStackTrace();
		}		
		return strArray;
	   }

	}
