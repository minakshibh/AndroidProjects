package com.pos.retail;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.pos.util.ConnectionDetector;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.Reason;
import com.pos.util.ScreenReceiver;
import com.pos.util.ServiceConnector;

public class ItemReturnsActivity extends Activity{
	private int year;
	private int month;
	private int day;
	private ImageView imgCalender;
	private EditText txtRetailStore, txtTransNo, txtPOSNo, txtDate;
	private Button btnOk, btnCancel,btnBack;
	static final int DATE_DIALOG_ID = 999;
	
	private ArrayList<Reason> reasonList;
	
	ProgressDialog dialog;
	Context mcontext;
	 HashMap<String, String> map;
	ArrayList<ProductReal> productList;
	TextView txtHeader;
	ConnectionDetector detector;
	
	SharedPreferences itempref;
	String transactionProductUrl,Method_Name = "GetTransactionProducts";
	
	 long curentTime=0;
	  long elapTime = 0;
	  boolean isPayWithDiscount = false;
	  String returnStatus ="";
	  String message="";
	  SharedPreferences loginPref, pref;
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.item_returns_form);
		
		reasonList = new ArrayList<Reason>();
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
				
		mcontext = this;
		
		detector = new ConnectionDetector(mcontext);
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		imgCalender = (ImageView)findViewById(R.id.imgCalender);
		txtRetailStore = (EditText)findViewById(R.id.txtRetailStore);
		txtTransNo = (EditText)findViewById(R.id.txtTransNo);
		txtPOSNo = (EditText)findViewById(R.id.txtPOSNmbr);
		txtDate = (EditText)findViewById(R.id.txtDate);
		btnOk = (Button)findViewById(R.id.btnOk);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		btnBack = (Button) findViewById(R.id.back);
		
		transactionProductUrl = getResources().getString(R.string.liveurl)+"/"+Method_Name;
		
		txtHeader = (TextView)findViewById(R.id.txtHeader);
		
		
		itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		pref = getSharedPreferences("mypref", MODE_PRIVATE);
		pref.edit().clear().commit();
			
		
		String itemReturnType = itempref.getString("ItemReturnType", "");
		
		Log.e("Type ", itemReturnType);
		if(itemReturnType.equals("Item Return"))
			returnStatus = "return";
		
		txtHeader.setText(itemReturnType);
		btnBack.setOnClickListener(Listener);
		btnCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.e("ItemReturnActivity","cancelled");
				finish();
			}
		});
		
		imgCalender.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				showDialog();
			}
		});
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(!txtRetailStore.getText().toString().trim().equals(""))
				{
					if(!txtPOSNo.getText().toString().trim().equals("")) 
					{
							if(!txtTransNo.getText().toString().trim().equals(""))
							{
								if( !txtDate.getText().toString().trim().equals("") ){				
					
					
									String retailstore = txtRetailStore.getText().toString().trim();
									String TransNo = txtTransNo.getText().toString().trim();
									
									String posNo = txtPOSNo .getText().toString().trim();
									String Transdate = txtDate.getText().toString().trim();
									
									if(detector.isConnectingToInternet())
										new GetTransactionDetail().execute(TransNo,posNo,Transdate);
									else
									{
										
										AlertDialog.Builder alert = new AlertDialog.Builder(ItemReturnsActivity.this);
										alert.setTitle("Internet Problem");
										alert.setMessage("Internet connection is not available");
										alert.setPositiveButton("OK", null);
										alert.show();
										
									}
								}
								else
								{
									shorDialog("Please enter transaction date");
								}
							}
							else
							{
								shorDialog("Please enter transaction id");
								
							}
					
					}
					else
					{
						
						shorDialog("Please enter pos id");
						
						
					}
				}
				else{
					AlertDialog.Builder alert = new AlertDialog.Builder(ItemReturnsActivity.this);
					alert.setTitle("Incomplete Form");
					alert.setMessage("Please enter Retail Store Id");
					alert.setPositiveButton("OK", null);
					alert.show();
				}
			}
		});
	}	
	public void shorDialog(String str)
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(ItemReturnsActivity.this);
		alert.setTitle("Incomplete Form");
		alert.setMessage(str);
		alert.setPositiveButton("OK", null);
		alert.show();
	}
	public void showDialog(){
		DatePickerDialog dialog = new DatePickerDialog(this, datePickerListener, year, month,
				day);
		dialog.show();
	}
		
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			txtDate.setText((year)+"-"+(pad(month+1))+"-"+ pad(day));
		}
	};
	OnClickListener Listener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			switch (v.getId()) 
			{
				case R.id.cancel:
					Log.e("ItemReturnActivity","cancelled...1111");
					finish();
					break;
				case R.id.back:
					Log.e("ItemReturnActivity","back");
					finish();
					break;
			
			}
			
			
		}
	};
	 private static String pad(int c) {
	        if (c >= 10)
	            return String.valueOf(c);
	        else
	            return "0" + String.valueOf(c);
	    }
	class GetTransactionDetail extends AsyncTask<String, String, String>
	{

		String result = "1";
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	result = "1";
	    	dialog.setMessage("Checking the  transaction detail");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.e("getting transaction detail","started");
			try
			{
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(transactionProductUrl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         
	         nameValuePairs.add(new BasicNameValuePair("Trnsid",params[0]));
	         nameValuePairs.add(new BasicNameValuePair("Posid","1"));
	         nameValuePairs.add(new BasicNameValuePair("CreatedDate",params[2]));
	         nameValuePairs.add(new BasicNameValuePair("returnStatus",returnStatus));
	         
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
			 is= entity.getContent();
		     productList= com.pos.util.SAXXMLParser.parseReturnProduct(is);
		     map=(HashMap<String, String>)com.pos.util.SAXXMLParser.map;
//		     
		     isPayWithDiscount = com.pos.util.SAXXMLParser.isPayWithDiscount;
		     
		     Log.e("map ",""+ map);
		     result = map.get("result");
		     message=map.get("message");
		     Log.e("get trx detail Sucess",""+ map);
		     Log.e("get trx detail Sucess11",""+ productList.size());
		     
		     ServiceConnector connector = new ServiceConnector(ItemReturnsActivity.this);
			 reasonList = connector.getReasonList();
				
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
	    	Log.e("getting transaction detail","stopped");
	    	SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
	    	
	    	String formattedDate = df.format(new Date());
	    	
	    	
	    		
	    	if(result.equals("0") && productList.size()>0)
	    	{
	    		Log.e("getting transaction detail","result 0");
	    		
	    		if(txtHeader.getText().toString().equals("Void Receipt"))
	    		{
	    			String invoiceDate = map.get("InvoiceDate");
			    	
			    	Log.e("invoice ", ""+invoiceDate);
			    	
			    	
			    	try
			    	{
			    	Date date1 = df.parse(invoiceDate);
			    	Date date2 = df.parse(formattedDate);
			    	
			    	Log.e("two date "+date1.toString(), date2.toString());
			    	Log.e("Compare ", ""+date1.compareTo(date2));
			    	
				    	if(date1.compareTo(date2)==0)
				    	{
				    		Log.e("getting transaction detail","passed");
				    		Intent in =new Intent(mcontext,ReturnDetailActivity.class);
					    	in.putExtra("productlist", productList);
					    	in.putExtra("isPayWithDiscount",isPayWithDiscount);
					    	in.putExtra("reasonList", reasonList);
					    	in.putExtra("map", map);
					    	
					    	startActivity(in);
				    		
				    	}
				    	else
				    	{
				    		Log.e("getting transaction detail","issue");
				    		AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
				    		builder.setTitle("Notification");
				    		builder.setMessage("Please check your transaction date ");
				    		builder.setPositiveButton("OK", null);
				    		builder.show();
				    		
				    		
				    		txtPOSNo.setText("");
				    		txtTransNo.setText("");
				    		txtDate.setText("");
				    		
				    		txtRetailStore.setText("");
				    		
				    		txtRetailStore.requestFocus();
				    		
				    	}
			    	
			    	}
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    	}
	    		}
	    		else
	    		{
	    			Log.e("getting transaction detail","issue 222");
			    	Intent in =new Intent(mcontext,ReturnDetailActivity.class);
			    	in.putExtra("productlist", productList);
			    	in.putExtra("isPayWithDiscount",isPayWithDiscount);
			    	in.putExtra("reasonList", reasonList);
			    	in.putExtra("map", map);
			    	
			    	startActivity(in);
	    		}
	    	}
	    	else
	    	{
	    		Log.e("getting transaction detail","issue 333");
	    		AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
	    		builder.setTitle("Notification");
	    		builder.setMessage(message);
	    		builder.setPositiveButton("OK", null);
	    		builder.show();
	    		
	    		
	    		
	    		txtPOSNo.setText("");
	    		txtTransNo.setText("");
	    		txtDate.setText("");
	    		
	    		txtRetailStore.setText("");
	    		
	    		txtRetailStore.requestFocus();
	    	}
	    	
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
				Intent in = new Intent(mcontext,BreakActivity.class);
				startActivity(in);
			}
			Log.e("", ""+(elapTime));
		}
	}
}
