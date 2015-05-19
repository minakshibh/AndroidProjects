package com.rapidride;


import java.util.Calendar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.rapidride.util.Utility;
public class CreditCard_Details extends Activity{
	
	private ProgressBar pBar;
	public ProgressDialog progress,progressbar;
	Button btn_submit,btn_cancel;
	EditText ed_cc_number,ed_zipcode,ed_cc_expiredate,ed_securitycode;
	
	Typeface typeFace;
	SharedPreferences prefs;
	
	public String jsonResult,jsonMessage;
	private int mYear = 2013, mMonth = 5, mDay = 30,exception=0;
	static final int DATE_DIALOG_ID = 1;
	
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crditcard_details);
		
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);  
		//pBar=(ProgressBar)findViewById(R.id.progressBar1);
		
		TextView rapidtitle=(TextView)findViewById(R.id.textView_rapid);
		rapidtitle.setTypeface(typeFace);
		ed_cc_number=(EditText)findViewById(R.id.editText_creditcardno);
		ed_cc_expiredate=(EditText)findViewById(R.id.editText_cc_expiredate);
		ed_securitycode=(EditText)findViewById(R.id.editText_securitycode);
		ed_zipcode=(EditText)findViewById(R.id.editText_cc_zipcode);
		
		btn_submit=(Button)findViewById(R.id.button_ccsubmit);
		btn_submit.setTypeface(typeFace);
		btn_submit.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				
				if(ed_cc_number.getText().toString().equals("")||ed_cc_expiredate.getText().toString().equals("")
					||ed_securitycode.getText().toString().equals("")||ed_zipcode.getText().toString().equals(""))
					{
						Utility.alertMessage(CreditCard_Details.this,"Please enter details");
						}
				else{
										
					   if(Utility.isConnectingToInternet(CreditCard_Details.this))
						{
							new creditcardParsing().execute(); /**asyn task for c card */
							Utility.hideKeyboard(CreditCard_Details.this);
							}
						else{
								Utility.alertMessage(CreditCard_Details.this,"Error in internet connection");
								}
						}
			}
		});
		ed_cc_expiredate.setOnTouchListener(new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
				Utility.hideKeyboard(CreditCard_Details.this);
				showDialog(DATE_DIALOG_ID);
				return false;
				
			}
		});
		
	
		btn_cancel=(Button)findViewById(R.id.button_cccancel2);
		btn_cancel.setTypeface(typeFace);
		btn_cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			
				finish();
			}
		});
		final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
	}
	
	
	
	
	
	
	
	
	/***************************End main function********************************************/

	
	
	
	
	
	
	
	
	
	//date picker 
	 DatePickerDialog.OnDateSetListener mDateSetListner = new OnDateSetListener() {
      public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

	            mYear = year;
	            mMonth = monthOfYear;
	            mDay = dayOfMonth;
	            updateDate();
	        }
	    };

	    protected Dialog onCreateDialog(int id) { switch (id) {
	        case DATE_DIALOG_ID:
	        DatePickerDialog datePickerDialog = this.customDatePicker();
	        return datePickerDialog;
	        }
	        return null;
	    }

	    @SuppressWarnings("deprecation")
	    protected void updateDate() {
	        int localMonth = (mMonth + 1);
	        String monthString = localMonth < 10 ? "0" + localMonth : Integer
	                .toString(localMonth);
	        String localYear = Integer.toString(mYear).substring(2);
	        ed_cc_expiredate.setText(new StringBuilder()
	        // Month is 0 based so add 1
	                .append(monthString).append("").append(localYear).append(" "));
	        showDialog(DATE_DIALOG_ID);
	    }

	    private DatePickerDialog customDatePicker() {
	        DatePickerDialog dpd = new DatePickerDialog(this, mDateSetListner,
	                mYear, mMonth, mDay);
	        try {
	            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
	            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
	                if (datePickerDialogField.getName().equals("mDatePicker")) {
	                    datePickerDialogField.setAccessible(true);
	                    DatePicker datePicker = (DatePicker) datePickerDialogField
	                            .get(dpd);
	                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType()
	                            .getDeclaredFields();
	                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
	                        if ("mDayPicker".equals(datePickerField.getName())
	                                || "mDaySpinner".equals(datePickerField
	                                        .getName())) {
	                            datePickerField.setAccessible(true);
	                            Object dayPicker = new Object();
	                            dayPicker = datePickerField.get(datePicker);
	                            ((View) dayPicker).setVisibility(View.GONE);
	                        }
	                    }
	                }
	            }
	        } catch (Exception ex) {
	        }
	        return dpd;
	    }
	    
 /**** c card parsing class **************************/
	    
	  	class creditcardParsing extends AsyncTask<Void, Void, Void> {
	  		
	  	String url = "http://appba.riderapid.com/c_verify/?c_info="+ed_cc_number.getText().toString().trim()+"&e_info="+ed_cc_expiredate.getText().toString().trim()+"&riderid="+prefs.getString("userid",null)+"&z_info="+ed_zipcode.getText().toString().trim()+"&s_info="+ed_securitycode.getText().toString().trim();
	  	
	  		protected void onPreExecute() {
	  				super.onPreExecute();
	  				pBar = new ProgressBar(CreditCard_Details.this);
	  				exception=0;
	  				pBar.setClickable(false);
	  				pBar.setVisibility(View.VISIBLE);
	  		}
	  		protected Void doInBackground(Void... arg0) {
	  		
	  				try {
	  					parsing();
	  				 } catch (Exception e) {
	  				 // TODO Auto-generated catch block
	  					e.printStackTrace();
	  				 }
	  				return null;
	  		}
	  		protected void onPostExecute(Void result) {
	  			super.onPostExecute(result);
	  			//progressbar.dismiss();
	  			pBar.setVisibility(View.GONE);
	  			if(exception==1)
	  			{
	  				Utility.alertMessage(CreditCard_Details.this,"Internet connection failed. Please Try again later.");
	  				}
	  			else
	  			{
		  			if(jsonResult.equals("0")){
		  				
		  				if(getIntent().getStringExtra("finish")!=null)
		  				{
		  					Intent i=new Intent(CreditCard_Details.this,RideFinish.class);
		  					i.putExtra("finish", "value");
			  				startActivity(i);	
		  					}
		  				else
		  				{
			  				//Toast.makeText(CreditCard_Details.this, jsonMessage, Toast.LENGTH_LONG).show();
			  				Intent i=new Intent(CreditCard_Details.this,Payment_Activity.class);
			  				startActivity(i);
			  				}
		  				}
		  			else
		  			{
			  			 Utility.alertMessage(CreditCard_Details.this, jsonMessage);
			  			 }
		  			ed_cc_number.setText("");
		  			ed_cc_expiredate.setText("");
		  			ed_zipcode.setText("");
		  			ed_securitycode.setText("");
	  			}
	  		}
	  		
/****c card parsing function ***/
	  	public void parsing() throws JSONException {
	  		try {
	  			HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	  				HttpGet httpGet = new HttpGet(url);
					
					HttpClient client = new DefaultHttpClient();
			        HttpResponse response;
			      //  StringBuilder stringBuilder = new StringBuilder();
			        response = client.execute(httpGet);
			            
			        HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					if(jsonstr!=null)
					{
					 Log.e("tag","result>>>>>>>    "+ jsonstr);
					 
						}
						JSONObject obj=new JSONObject(jsonstr);
						jsonResult=obj.getString("result");
						jsonMessage=obj.getString("message");
						
			        System.err.println("result"+jsonResult);
			        System.err.println("result"+jsonMessage);
			        
					}catch(Exception e){
						exception=1;
					e.printStackTrace();
					
				}
	  		}
	  	}

	  	public void onBackPressed() {
		}
}
