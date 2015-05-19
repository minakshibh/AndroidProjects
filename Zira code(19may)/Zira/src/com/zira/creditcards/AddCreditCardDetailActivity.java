package com.zira.creditcards;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.modal.User;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class AddCreditCardDetailActivity extends Activity implements OnClickListener,AsyncResponseForZira{

	private EditText editText_creditCardNumber,editText_CVV,editText_ExpiryDate;
	private Button btn_Done,btn_Cancel;
	private RelativeLayout rel_month_year;
	private JSONObject jsonObject;
	private NumberPicker monthPicker,yearPicker;
	boolean check=false;
	String selectedMonth="",selectYears="";
	private Button dialogDone,dialogCancel;
	private String addCreditcard = "AddCreditCard";
	private User mUserModel;
	private SharedPreferences prefs;
	long selectdateintoInt;
	Boolean bol_value;
	private User user;
	private ZiraParser ziraParser;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_creditcard_detail);

		initialiseVariable();
		
		initialiseListener();
	}

	private void initialiseVariable() {

		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		mUserModel=SingleTon.getInstance().getUser();
		ziraParser=new ZiraParser();
		rel_month_year=(RelativeLayout)findViewById(R.id.llayout);
		editText_creditCardNumber=(EditText)findViewById(R.id.editText_creditCardNumber);
		editText_CVV=(EditText)findViewById(R.id.editText_CVV);
		editText_ExpiryDate=(EditText)findViewById(R.id.editText_ExpiryDate);

		dialogDone=(Button)findViewById(R.id.Done_btn);
		dialogCancel=(Button)findViewById(R.id.Cancel_btn);
		btn_Done=(Button)findViewById(R.id.btn_DoneCredit);
		btn_Cancel=(Button)findViewById(R.id.btn_CancelCredit);
		monthPicker=(NumberPicker)findViewById(R.id.numberpickerMonth);
		yearPicker=(NumberPicker)findViewById(R.id.numberpickerYear);
	}

	private void initialiseListener() {

		dialogDone.setOnClickListener(this);
		dialogCancel.setOnClickListener(this);		
		btn_Done.setOnClickListener(this);
		btn_Cancel.setOnClickListener(this);
		editText_ExpiryDate.setOnClickListener(this);
		monthPicker.setMinValue((int)1);
		monthPicker.setMaxValue((int)12);
		monthPicker.setValue(1);// restricked number to maximum value i.e. 31
		monthPicker.setWrapSelectorWheel(true); 
		monthPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		selectedMonth="01";
		monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
		{
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
			{

				selectedMonth=""+newVal;
				if(selectedMonth.length()==1)
				{
					selectedMonth="0"+newVal;
				}
			}
		});

		// restricted number to minimum value i.e 1
		int year = Calendar.getInstance().get(Calendar.YEAR);
		yearPicker.setMinValue((int)year);
		yearPicker.setMaxValue((int)year+20);
		yearPicker.setValue(year);// restricked number to maximum value i.e. 31
		yearPicker.setWrapSelectorWheel(true); 
		yearPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		selectYears="2015";
		yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
		{
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
			{

				selectYears=""+newVal;

			}
		});
	}


	@Override
	public void onClick(View v) {


		switch (v.getId()) {

		case R.id.btn_DoneCredit:
			if(editText_creditCardNumber.getText().toString().equals(""))
			{
				Util.alertMessage(AddCreditCardDetailActivity.this, "Please enter credit card number");
				}
			else if(editText_CVV.getText().toString().equals(""))
			{
				Util.alertMessage(AddCreditCardDetailActivity.this, "Please enter CVV");
			}
			else if(editText_ExpiryDate.getText().toString().equals(""))
			{
				Util.alertMessage(AddCreditCardDetailActivity.this, "Please enter expiry date");
			}
				
			else
			{
				if(Util.isNetworkAvailable(AddCreditCardDetailActivity.this)){
					Util.hideKeyboard(AddCreditCardDetailActivity.this);
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("riderid",prefs.getString("riderid", "")));
			nameValuePairs.add(new BasicNameValuePair("creditcardnumber", editText_creditCardNumber.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("creditcardexpiry",selectedMonth+selectYears));//editText_ExpiryDate.getText().toString()));//"052017"));
			nameValuePairs.add(new BasicNameValuePair("cvv",editText_CVV.getText().toString()));
			nameValuePairs.add(new BasicNameValuePair("cardid","-1"));

			AsyncTaskForZira mFetchStates = new AsyncTaskForZira(AddCreditCardDetailActivity.this, addCreditcard,nameValuePairs, true,"Please wait...");
			mFetchStates.delegate = (AsyncResponseForZira) AddCreditCardDetailActivity.this;
			mFetchStates.execute();	
				}
				else
				{
					Util.alertMessage(AddCreditCardDetailActivity.this, "Please check your internet connection");
					}
			}
			break;

		case R.id.editText_ExpiryDate:

			Util.hideKeyboard(AddCreditCardDetailActivity.this);
			rel_month_year.setVisibility(View.VISIBLE);
			btn_Done.setVisibility(View.GONE);
			btn_Cancel.setVisibility(View.GONE);

			break;

		case R.id.Done_btn:

			rel_month_year.setVisibility(View.GONE);
			ageChecker();
			if(bol_value==false)
			{
			if(selectYears.length()==4)
			{
				//String substring = selectYears;//.substring(Math.max(selectYears.length() - 2, 0));
				editText_ExpiryDate.setText(selectedMonth+selectYears);
				btn_Done.setVisibility(View.VISIBLE);
				btn_Cancel.setVisibility(View.GONE);
			}
			}
			else
			{
				Util.alertMessage(AddCreditCardDetailActivity.this, "Select date less than current date");
				//btn_Done.setVisibility(View.VISIBLE);
				rel_month_year.setVisibility(View.VISIBLE);
			}

			break;

		case R.id.Cancel_btn:

			rel_month_year.setVisibility(View.GONE);	
			btn_Done.setVisibility(View.VISIBLE);
			btn_Cancel.setVisibility(View.GONE);
			editText_ExpiryDate.setText("");
			break;

	
		default:

			break;
		}



	}	

	@Override
	public void processFinish(String output, String methodName) {

		JSONObject obj;
		Log.e("credit card", output);
		try {
			obj = new JSONObject(output);
			String jsonresult=obj.getString("result");
			String jsonMessage=obj.getString("message");
			if(methodName.equals(addCreditcard))
			{
			if(jsonresult.equals("0"))
			{
				//finish();
				if(Util.isNetworkAvailable(AddCreditCardDetailActivity.this)){

					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
					nameValuePairs.add(new BasicNameValuePair("useremail",prefs.getString("_UserEmail","")));
					nameValuePairs.add(new BasicNameValuePair("password",prefs.getString("_Password","")));
					
					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(AddCreditCardDetailActivity.this, "Login",nameValuePairs, false,"");
					mWebPageTask.delegate = (AsyncResponseForZira) AddCreditCardDetailActivity.this;
					mWebPageTask.execute();
					}
					else
					{
						Util.alertMessage(AddCreditCardDetailActivity.this, "Please check your internet connection");
					}
			}
			else
			{
				Util.alertMessage(AddCreditCardDetailActivity.this, jsonMessage);
			}}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			obj = new JSONObject(output);
			String jsonresult=obj.getString("result");
			String jsonMessage=obj.getString("message");
			if(methodName.equals("Login"))
			{
				user = ziraParser.parseLoginResponse(output);
				if(jsonresult.equals("0"))
				{
					SingleTon.getInstance().setUser(user);
					Intent i=new Intent(AddCreditCardDetailActivity.this,ListOfCreditCardsActivity.class);
					startActivity(i);
					finish();
				}
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
