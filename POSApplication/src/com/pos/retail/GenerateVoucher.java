package com.pos.retail;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
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

import com.example.posapplication.R;
import com.pos.util.ProductReal;
import com.pos.util.SaveReturnItemOnServer;

public class GenerateVoucher extends Activity{

	private TextView txtVoucherAmount, txtVoucherType;
	private EditText validityDate;
	private ImageView imgCalender;
	private int year;
	private int month;
	private int day;
	private String voucherAmount, voucherType;
	Button back,cancel,btnOk;
	SharedPreferences userPref;
	int userId,voucherId;
	String saveVoucherUrl,Method_Name="SaveVoucherDetails";
	ProgressDialog dialog;
	ArrayList<ProductReal> returnProductList, remainingList;
	Context mcontext;
	String TransId, itemReturnType;
	int quantity =0,reasonId;
	boolean isPayWithDiscount;
	String totalVat,naira, returnSubTotal, returnMDiscount, returnDisc, newTransactionId, rem_totalTax, rem_totalDiscount, rem_totalMDiscount, rem_finalTotal;
	String pending_total, pending_vat, pending_discount, pending_MDiscount;
	String ret_subTotal, ret_total, ret_Tax, ret_disc, ret_MDisc;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.generate_voucher);
		
		naira =getResources().getString(R.string.naira);
		
		rem_totalTax = getIntent().getStringExtra("rem_totalTax");
		rem_totalMDiscount = getIntent().getStringExtra("rem_totalMDiscount");
		rem_totalDiscount = getIntent().getStringExtra("rem_totalDiscount");
		rem_finalTotal = getIntent().getStringExtra("rem_finalTotal");
		remainingList = (ArrayList<ProductReal>)getIntent().getSerializableExtra("rem_listofproduct");
		
		pending_total = getIntent().getStringExtra("pending_finalTotal");
		pending_vat = getIntent().getStringExtra("pending_totalTax");
		pending_discount = getIntent().getStringExtra("pending_totalDiscount");
		pending_MDiscount = getIntent().getStringExtra("pending_manualdiscount");
		
		ret_subTotal = getIntent().getStringExtra("ret_subTotal");
		ret_total = getIntent().getStringExtra("ret_total");
		ret_Tax = getIntent().getStringExtra("ret_Tax");
		ret_disc = getIntent().getStringExtra("ret_disc");
		ret_MDisc = getIntent().getStringExtra("ret_MDisc");
		
		voucherAmount = getIntent().getStringExtra("voucherAmount");
		voucherType = getIntent().getStringExtra("voucherType");
		TransId =  getIntent().getStringExtra("TransId");
		voucherId = getIntent().getIntExtra("voucherId", -1);
		reasonId = getIntent().getIntExtra("reasonId", 1);
		newTransactionId = getIntent().getStringExtra("newTransactionId");
				
		isPayWithDiscount = getIntent().getBooleanExtra("isPayWithDiscount",false);
		returnSubTotal = getIntent().getStringExtra("originalAmount");
		returnMDiscount = getIntent().getStringExtra("mDiscount");
		returnDisc = getIntent().getStringExtra("discount");
		totalVat = getIntent().getStringExtra("totalVat");
		returnProductList =(ArrayList<ProductReal>)getIntent().getSerializableExtra("returnProductList");
		
		mcontext = this;
		
		SharedPreferences itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		
		itemReturnType = itempref.getString("ItemReturnCode", "");
		
		userPref =getSharedPreferences("LoginPref", MODE_PRIVATE);
		userId = userPref.getInt("UserId",-1);
		
		for(int i=0;i<returnProductList.size();i++)
			quantity +=returnProductList.get(i).getSelectedQuatity();
		
		saveVoucherUrl = getResources().getString(R.string.liveurl)+"/"+Method_Name;
		
		Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);
		
		back = (Button) findViewById(R.id.back);
		cancel = (Button) findViewById(R.id.btnCancel);
		btnOk = (Button) findViewById(R.id.btnOk);
		back.setOnClickListener(Listener);
		
		cancel.setOnClickListener(Listener);
		btnOk.setOnClickListener(Listener);
		
		txtVoucherAmount = (TextView)findViewById(R.id.txtVoucherAmount);
		txtVoucherType = (TextView)findViewById(R.id.txtVoucherType);
		validityDate = (EditText)findViewById(R.id.txtDate);
		imgCalender = (ImageView)findViewById(R.id.imgCalender);
		
		
		if(isPayWithDiscount){
			voucherAmount = returnSubTotal;
		}
		
		txtVoucherAmount.setText(naira +" "+voucherAmount);
		txtVoucherType.setText(voucherType);
		
		imgCalender.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				showDialog();
			}
		});
	}
	
	public void showDialog()
	{
		DatePickerDialog date =   new DatePickerDialog(this, datePickerListener, year, month, day);
		date.show();
	}
		
	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			Calendar Day = Calendar.getInstance();
			
			Day.set(Calendar.DAY_OF_MONTH,day);
			Day.set(Calendar.MONTH,month); 
			Day.set(Calendar.YEAR, year);

			Calendar today = Calendar.getInstance();
			  
			if(today.compareTo(Day) <=0){
				validityDate.setText((pad(month+1))+"/"+ pad(day)+"/"+year);
			
			}else{
				AlertDialog.Builder alert = new AlertDialog.Builder(GenerateVoucher.this);
				alert.setTitle("Invalid date");
				alert.setMessage("Please select a valid date");
				alert.setPositiveButton("OK", null);
				alert.show();
			}
		}
	};
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private DatePickerDialog customDatePickerStart() {
		
		
        DatePickerDialog dpd = new DatePickerDialog(this, datePickerListener,
                year, month, day);
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    
                    datePicker.setMinDate(Calendar.getInstance().getTimeInMillis());
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                        .getName())) {
                            datePickerField.setAccessible(true);
                            
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
       
        return dpd;
    }
	OnClickListener Listener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.btnCancel:
					finish();
					break;
				case R.id.back :
					finish();
					break;
				case R.id.btnOk:
					if( validityDate.getText().toString().equals(""))
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
						builder.setTitle("Notification");
						builder.setMessage("Valid date should not empty");
						builder.setPositiveButton("OK", null);
						builder.show();
					}
					else
						new saveVoucherDetail().execute();
					
					
					break;
			}
			
			
		}
	};
	class saveVoucherDetail extends AsyncTask<String, String, String>
	{
		String result = "1";
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Please  wait "+"\n"+"Save voucher");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			try
			{
			 Log.e("Id  ", ""+TransId)	;
			 Log.e("Id  ", ""+validityDate.getText().toString())	;
			 Log.e("Id  ", String.valueOf(userId))	;
			 Log.e("Id  ", voucherAmount)	;
			 Log.e("Id  ", voucherType+" "+voucherId)	;
			
			 DefaultHttpClient httpClient = new DefaultHttpClient();
	         HttpPost requestLogin = new HttpPost(saveVoucherUrl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("TransactionId", TransId));
	         nameValuePairs.add(new BasicNameValuePair("validity", validityDate.getText().toString()));
	         nameValuePairs.add(new BasicNameValuePair("UserId", String.valueOf(userId)));
	         nameValuePairs.add(new BasicNameValuePair("amount", voucherAmount));
	         nameValuePairs.add(new BasicNameValuePair("VoucherType", String.valueOf(voucherId)));
	         
	         
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		     InputStream  is= entity.getContent();
			
		     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		     DocumentBuilder db = dbf.newDocumentBuilder();
		     
		     Document doc = db.parse(is);
		     Log.e("Doc",""+ doc);
		    
	         Element root = doc.getDocumentElement();
	         NodeList resultItem = root.getElementsByTagName("VoucherID");
	         result = resultItem.item(0).getFirstChild().getNodeValue();
		    
		     Log.e("Result ", ""+result);
		     
			 }
			 catch(Exception e)
			 {
				e.printStackTrace();
			 }
			
			return null;
		}
		@SuppressWarnings("unchecked")
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	//newly code
	    	if(!result.equals("0"))
	    	{
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    		String currentDateandTime = sdf.format(new Date());
	    		SaveReturnItemOnServer transaction = new SaveReturnItemOnServer(mcontext,ret_subTotal, ret_total, ret_Tax, ret_disc, ret_MDisc, pending_total, pending_vat, pending_discount, pending_MDiscount,TransId,newTransactionId, "Voucher",returnProductList,reasonId,voucherAmount.toString(), itemReturnType, 0, remainingList, rem_finalTotal, rem_totalTax, rem_totalDiscount, rem_totalMDiscount, BigDecimal.valueOf(0),result,voucherType,voucherAmount,currentDateandTime,validityDate.getText().toString()+"  12:00:00 AM");
				transaction.new SaveTransaction().execute(returnProductList);
	    	}
	    	else
	    	{
	    		AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
	    		builder.setTitle("Try Again");
	    		builder.setMessage("Voucher is not saved");
	    		builder.setPositiveButton("OK", null);
	    	}
	    	
		}
	}
	 private static String pad(int c) {
	        if (c >= 10)
	            return String.valueOf(c);
	        else
	            return "0" + String.valueOf(c);
	    }
	 
	 
}
