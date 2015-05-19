package com.zira.registration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.twentyfourseven.zira.R;
import com.twentyfourseven.zira.VehicleSearchActivity;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.creditcards.AddCreditCardDetailActivity;
import com.zira.util.Util;


@SuppressLint("NewApi") public class AddCreditInfo extends Activity implements AsyncResponseForZira{
EditText editCreditCard,editCVV,editExpiry;
Button btnDone,dialogDone,dialogCancel;//btnCancel
SharedPreferences reg_prefs;
DatePickerDialog dpd1;
RelativeLayout rel_month_year;
LinearLayout center_Layout;
NumberPicker monthPicker,yearPicker;
boolean check=false;
String selectedMonth="",selectYears="";
JSONObject jsonObject=new JSONObject();
ProgressDialog pDialog;
String jsonResult="",jsonMessage="",jsonmessage2="";
Dialog alertDialog1;
String validateCreditCardMethodName="ValidateCreditCard",registerMethodName="RegisterRider";
ImageView back;
long selectdateintoInt=0;
Boolean bol_value,datechecker=false;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	super.onCreate(savedInstanceState);
	setContentView(R.layout.rider_registration_three);
	intializeVariables();
	clickListner();
	
	reg_prefs=getSharedPreferences("reg_Zira", MODE_PRIVATE);
	
	}
private void intializeVariables()
{
	editCreditCard=(EditText)findViewById(R.id.editText_rider_creditCardNumber);
	editCVV=(EditText)findViewById(R.id.editText_riderCVV);
	editExpiry=(EditText)findViewById(R.id.editText_riderExpiryDate);
	btnDone=(Button)findViewById(R.id.btn_newDone);
	//btnCancel=(Button)findViewById(R.id.btn_newCancel);
	rel_month_year=(RelativeLayout)findViewById(R.id.llayout);
	monthPicker=(NumberPicker)findViewById(R.id.numberpickerMonth);
	yearPicker=(NumberPicker)findViewById(R.id.numberpickerYear);
	dialogDone=(Button)findViewById(R.id.Done_btn);
	dialogCancel=(Button)findViewById(R.id.Cancel_btn);
	back=(ImageView)findViewById(R.id.button_rider_back_registhree);
	center_Layout=(LinearLayout)findViewById(R.id.layout_center);
	//dialogDone.setVisibility(View.GONE);
	//dialogCancel.setVisibility(View.GONE);
	
}
private void clickListner()
{
	center_Layout.setOnClickListener(new OnClickListener() {
	public void onClick(View v) {
		
		/*rel_month_year.setVisibility(View.GONE);
		btnDone.setVisibility(View.VISIBLE);
		//ageChecker();
		if(bol_value==false)
		{
			
		}
		else
		{
			Util.alertMessage(AddCreditInfo.this, "Select date less than current date");
			//btn_Done.setVisibility(View.VISIBLE);
			rel_month_year.setVisibility(View.VISIBLE);	
		}*/
		}
	});
	dialogDone.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// restricted number to minimum value i.e 1
				rel_month_year.setVisibility(View.GONE);
				if(selectYears.length()==4)
				{
					//String substring = selectYears.substring(Math.max(selectYears.length() - 2, 0));
					editExpiry.setText(selectedMonth+selectYears);
					//btnDone.setVisibility(View.VISIBLE);
					//btnCancel.setVisibility(View.VISIBLE);
					
					
					ageChecker();
					if(bol_value==false)
					{
						rel_month_year.setVisibility(View.GONE);
						btnDone.setVisibility(View.VISIBLE);
					}
					else
					{
						Util.alertMessage(AddCreditInfo.this, "Select date less than current date");
						//btn_Done.setVisibility(View.VISIBLE);
						rel_month_year.setVisibility(View.VISIBLE);	
					}
				}
			}
	});
		dialogCancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				rel_month_year.setVisibility(View.GONE);	
				btnDone.setVisibility(View.VISIBLE);
				editExpiry.setText("");
				//btnCancel.setVisibility(View.VISIBLE);
			}
		});
		
	btnDone.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
		Util.hideKeyboard(AddCreditInfo.this);
		String gettingCreditCard=editCreditCard.getText().toString();
		String gettingCVV=editCVV.getText().toString();
		String gettingExpiry=editExpiry.getText().toString();
		
			if(gettingCreditCard.equals("") )
			{
				Util.alertMessage(AddCreditInfo.this, "Please enter credit card number");
			}
			else if (gettingCVV.equals("") )
			{
				Util.alertMessage(AddCreditInfo.this, "Please enter CVV");
			}
			else if (gettingExpiry.equals(""))
			{
				Util.alertMessage(AddCreditInfo.this, "Please enter expiry date");
			}
			
			else
			{
				if(Util.isNetworkAvailable(AddCreditInfo.this))
				{
//					new httpCheckCreditCard().execute();
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		            nameValuePairs.add(new BasicNameValuePair("creditcardnumber", editCreditCard.getText().toString()));
		            nameValuePairs.add(new BasicNameValuePair("creditcardexpiry",selectedMonth+selectYears));//"052017"
		            nameValuePairs.add(new BasicNameValuePair("cvv", editCVV.getText().toString()));
		        
					
					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(AddCreditInfo.this,validateCreditCardMethodName,nameValuePairs,true,"Please wait...");
					mWebPageTask.delegate = (AsyncResponseForZira) AddCreditInfo.this;
					mWebPageTask.execute();	
					
				}else
				{
					Util.alertMessage(AddCreditInfo.this, "Please check your internet connection");
				}
				
			}
		}
	});
	
/*	btnCancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			  AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCreditInfo.this);
			  builder1.setTitle("Zira 24/7");
			  builder1.setMessage("Do you want to cancel the registration?");
			  builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					Editor editor = reg_prefs.edit();
					editor.clear();
					editor.commit();
					
					Intent intent = new Intent(AddCreditInfo.this, com.twentyfourseven.zira.LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			  });
			  builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertDialog1.dismiss();
				}
			});
			  alertDialog1 = builder1.create();
  			  alertDialog1.setCanceledOnTouchOutside(false);
  			  alertDialog1.show();
		  }
	});*/
	
	editExpiry.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			editExpiry.setFocusable(false);
			Util.hideKeyboard(AddCreditInfo.this);
			rel_month_year.setVisibility(View.VISIBLE);
			btnDone.setVisibility(View.INVISIBLE);
			//btnCancel.setVisibility(View.INVISIBLE);
			datechecker=true;
		}
	});

	editCVV.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			rel_month_year.setVisibility(View.GONE);
		}
	});
	editCreditCard.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			rel_month_year.setVisibility(View.GONE);
			
		}
	});
	
	back.setOnClickListener(new OnClickListener() {
	public void onClick(View v) {
			
			//finish();
			  AlertDialog.Builder builder1 = new AlertDialog.Builder(AddCreditInfo.this);
			  builder1.setTitle("Zira 24/7");
			  builder1.setMessage("Do you want to cancel the registration?");
			  builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					Editor editor = reg_prefs.edit();
					editor.clear();
					editor.commit();
					
					Intent intent = new Intent(AddCreditInfo.this, com.twentyfourseven.zira.LoginActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			  });
			  builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertDialog1.dismiss();
				}
			});
			  alertDialog1 = builder1.create();
			  alertDialog1.setCanceledOnTouchOutside(false);
			  alertDialog1.show();
		}
	});
	
	
	monthPicker.setMinValue((int)1);
	monthPicker.setMaxValue((int)12);
	monthPicker.setValue(1);// restricked number to maximum value i.e. 31
	monthPicker.setWrapSelectorWheel(true); 
	monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	selectedMonth="01";
	
	int month_count = monthPicker.getChildCount();
	    for(int i = 0; i < month_count; i++){
	        View child = monthPicker.getChildAt(i);
	        if(child instanceof EditText){
	            try{
	            	EditText et = ((EditText) monthPicker.getChildAt(i));
	                et.setTextColor(getResources().getColor(R.color.black));
	            }
	            catch(Exception e){
	                Log.w("setNumberPickerTextColor", e);
	            }
	        }
	    }
	    
	monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
    {
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
      {
    	  
    	  selectedMonth=""+newVal;
    	  if(selectedMonth.length()==1)
    	  {
    		  selectedMonth="0"+newVal;
    	  }
    	  
    	  if(selectYears.length()==4)
			{
				//String substring = selectYears.substring(Math.max(selectYears.length() - 2, 0));
    		  //selectedMonth+selectYears;
				//editExpiry.setText();
				//rel_month_year.setVisibility(View.GONE);
				//btnDone.setVisibility(View.VISIBLE);
				//btnCancel.setVisibility(View.VISIBLE);
			}
      }
    });
	
	// restricted number to minimum value i.e 1
	// year is stored as a static member
	int year = Calendar.getInstance().get(Calendar.YEAR);
	yearPicker.setMinValue((int)year);
	yearPicker.setMaxValue((int)year+20);
	yearPicker.setValue(year);// restricked number to maximum value i.e. 31
	yearPicker.setWrapSelectorWheel(true); 
	yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	selectYears="2015";
	
	int year_count = yearPicker.getChildCount();
   	for(int i = 0; i < year_count; i++){
       View child = yearPicker.getChildAt(i);
       if(child instanceof EditText){
           try{
           	EditText et = ((EditText) yearPicker.getChildAt(i));
               et.setTextColor(getResources().getColor(R.color.black));
           }
           catch(Exception e){
               Log.w("setNumberPickerTextColor", e);
           }
       }
    }
   	
	yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
    {
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
      {
    	  
    	  selectYears=""+newVal;
    	  if(selectYears.length()==4)
			{
				//String substring = selectYears.substring(Math.max(selectYears.length() - 2, 0));
				//editExpiry.setText(selectedMonth+selectYears);
				//rel_month_year.setVisibility(View.GONE);
				//btnDone.setVisibility(View.VISIBLE);
				//btnCancel.setVisibility(View.VISIBLE);
				
			}
    	 
      }
    });
}
/*
//Async_task Credit Card class
	private class httpCheckCreditCard extends AsyncTask<Void, Void, Void> { 
	
		private int int_value=0;

		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(AddCreditInfo.this);
		    pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
			}
		
		@Override
		protected Void doInBackground(Void... arg0) {
		
			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	            nameValuePairs.add(new BasicNameValuePair("creditcardnumber", editCreditCard.getText().toString()));
	            nameValuePairs.add(new BasicNameValuePair("creditcardexpiry",selectedMonth+selectYears));
	            nameValuePairs.add(new BasicNameValuePair("cvv", editCVV.getText().toString()));
	        
	            String jsonstr=Util.getResponseFromUrl(validateCreditCardMethodName, nameValuePairs, AddCreditInfo.this);
			
				JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
			
			return null;
			}
			
			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				pDialog.dismiss();
			    if(jsonResult.equals("0"))
				{
					new RegisterUser().execute();
				}else{
					if(jsonMessage.equals("Failure"))
					Util.alertMessage(AddCreditInfo.this, "This is not a valid credit card");	
				}
			}
	}*/
//credit card parsing function
		/*
		private class RegisterUser extends AsyncTask<Void, Void, Void> {
			@Override
						protected void onPreExecute() {
							super.onPreExecute();
							// Showing progress dialog
							pDialog = new ProgressDialog(AddCreditInfo.this);
							pDialog.setMessage("Please wait...");
							pDialog.setCancelable(false);
							pDialog.show();
						}
						@Override
						protected Void doInBackground(Void... arg0) {

							try {
								
								String userid=reg_prefs.getString("userid", "");
								String email=reg_prefs.getString("email", "");
								String userimage=reg_prefs.getString("userimage", "");
								String firstname=reg_prefs.getString("firstname", "");
								String lastname=reg_prefs.getString("lastname", "");
								String password=reg_prefs.getString("password", "");
								String phonenumber=reg_prefs.getString("phonenumber", "");
								String creditcardnumber=editCreditCard.getText().toString();
								String creditcardexpiry=selectedMonth+selectYears;
								String cvv=editCVV.getText().toString();
								
							    List<NameValuePair> params1 = new ArrayList<NameValuePair>();
							    params1.add(new BasicNameValuePair("userid", userid));
							    params1.add(new BasicNameValuePair("email", email));
							    params1.add(new BasicNameValuePair("firstname",firstname));
							    
							    params1.add(new BasicNameValuePair("lastname", lastname));
							    params1.add(new BasicNameValuePair("password", password));
							    params1.add(new BasicNameValuePair("userimage",userimage));

							    params1.add(new BasicNameValuePair("phonenumber", phonenumber));
							    params1.add(new BasicNameValuePair("creditcardnumber", creditcardnumber));
							    params1.add(new BasicNameValuePair("creditcardexpiry",creditcardexpiry));
							    params1.add(new BasicNameValuePair("cvv",cvv));
							    
								String jsonstr=Util.getResponseFromUrl(registerMethodName, params1, AddCreditInfo.this);
								Log.i("tag", "Result:"+jsonstr);
								
								JSONObject obj=new JSONObject(jsonstr);
								jsonResult=obj.getString("result");
								jsonmessage2=obj.getString("message");
								
								} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								}
								return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							super.onPostExecute(result);
							pDialog.dismiss();
							
							Log.i("tag","Result=" +jsonResult);
							
							if(jsonResult.equals("0"))
							{
								AlertDialog.Builder alert = new AlertDialog.Builder(AddCreditInfo.this);
								alert.setTitle("Registration successful");
								alert.setMessage("Congrats!!! You have been registered with Zira. Please continue to Login.");
								alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface arg0, int arg1) {
										Intent intent = new Intent(AddCreditInfo.this, com.twentyfourseven.zira.LoginActivity.class);
										intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
										startActivity(intent);
										finish();
									}
								});
								alert.show();
							}else{
								Util.alertMessage(AddCreditInfo.this, "Something went wrong. Please try again after some time.");	
							}
						}
		}*/

		@Override
		public void processFinish(String output, String methodName) {
			if(methodName.equals(validateCreditCardMethodName)){
				Log.e("validateCreditCardMethodName", output);
				try{
					JSONObject obj=new JSONObject(output);
					jsonResult=obj.getString("result");
					jsonMessage=obj.getString("message");
					if(jsonResult.equals("0"))
					{
//						new RegisterUser().execute();
						String userid=reg_prefs.getString("userid", "");
						String email=reg_prefs.getString("email", "");
						String userimage=reg_prefs.getString("userimage", "");
						String firstname=reg_prefs.getString("firstname", "");
						String lastname=reg_prefs.getString("lastname", "");
						String password=reg_prefs.getString("password", "");
						String phonenumber=reg_prefs.getString("phonenumber", "");
						String creditcardnumber=editCreditCard.getText().toString();
						String creditcardexpiry=selectedMonth+selectYears;
						String cvv=editCVV.getText().toString();
						
					    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					    nameValuePairs.add(new BasicNameValuePair("userid", userid));
					    nameValuePairs.add(new BasicNameValuePair("email", email));
					    nameValuePairs.add(new BasicNameValuePair("firstname",firstname));
					    
					    nameValuePairs.add(new BasicNameValuePair("lastname", lastname));
					    nameValuePairs.add(new BasicNameValuePair("password", password));
					    nameValuePairs.add(new BasicNameValuePair("userimage",userimage));//"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"));

					    nameValuePairs.add(new BasicNameValuePair("phonenumber", phonenumber));
					    nameValuePairs.add(new BasicNameValuePair("creditcardnumber", creditcardnumber));
					    nameValuePairs.add(new BasicNameValuePair("creditcardexpiry",creditcardexpiry));
					    nameValuePairs.add(new BasicNameValuePair("cvv",cvv));
						
						AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(AddCreditInfo.this,registerMethodName,nameValuePairs, true, "Please wait...");
						mWebPageTask.delegate = (AsyncResponseForZira) AddCreditInfo.this;
						mWebPageTask.execute();	
					}else{
						if(jsonMessage.equalsIgnoreCase("Failure"))
						Util.alertMessage(AddCreditInfo.this, "This is not a valid credit card");	
					}
				}catch(Exception e){
					
				}
			}else if(methodName.equals(registerMethodName)){
				Log.e("Rider Registration", output);
				try{
					JSONObject obj=new JSONObject(output);
				
					jsonResult=obj.getString("result");
					Log.e("rider register", jsonResult );
					jsonmessage2=obj.getString("message");
					Log.e("rider register", jsonResult+" "+jsonmessage2);
					}catch(Exception e){
						System.err.println("Exception"+e);
					}
					if(jsonResult.equals("0"))
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(AddCreditInfo.this);
						alert.setTitle("Registration successful");
						alert.setMessage("Congrats!!! You have been registered with Zira. Please continue to Login.");
						alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent = new Intent(AddCreditInfo.this, com.twentyfourseven.zira.LoginActivity.class);
								intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
								finish();
							}
						});
						alert.show();
					}else{
						Util.alertMessage(AddCreditInfo.this, jsonmessage2);	
					}
					
				}
			
		}
		
		public void ageChecker()
		{
			long currentdateintoInt= System.currentTimeMillis();
				
						
			System.err.println("selectdate="+selectedMonth+selectYears);
			System.err.println("currentdateintoInt="+currentdateintoInt);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			try {
				String ss=selectYears+selectedMonth;
				selectdateintoInt=Long.parseLong(ss);
				Date mDate=null;
			
				if(ss.equals(""))
				{}
				else{
				 mDate = sdf.parse(ss);
				}
				/*else
				{
					mDate = sdf.parse(ss);
				}*/
				selectdateintoInt = mDate.getTime();
			    System.out.println("ex Date in milli :: " + selectdateintoInt);
			} catch (Exception e) {
			            e.printStackTrace();
			}
			if(currentdateintoInt>selectdateintoInt)
				 {
					Log.d("yessssssss", "Please select greater date from current date");
				//	Util.alertMessage(BackgroundCheckActivity.this, "Please select current or greater date from current date");
					bol_value=true;
				 }
			else
			{
				bol_value=false;
				}
			
		}
}