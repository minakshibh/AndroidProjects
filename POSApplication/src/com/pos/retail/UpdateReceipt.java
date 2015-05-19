package com.pos.retail;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.posapplication.R;
import com.pos.util.ProductReal;
//import com.pos.util.TestProduct;

public class UpdateReceipt extends Activity{

	Button btnLogin;
	ArrayList<ProductReal> productList;
	SharedPreferences loginPref;
	@SuppressLint("NewApi")
	public void onCreate(Bundle b)
	{
		
		super.onCreate(b);
		setContentView(R.layout.login);
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		btnLogin = (Button)findViewById(R.id.login);
		
		Intent in = getIntent();
		
		btnLogin .setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				WebServiceCallExample();
				
			}
		});
		
		
		
		  
	}
	@SuppressLint("SimpleDateFormat")
	public void WebServiceCallExample()
    {
		
        String NAMESPACE = "http://tempuri.org/";
        String METHOD_NAME = "UpdateReturns";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        String URL = getResources().getString(R.string.liveurl);
        
        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        
        SoapObject users = new SoapObject(NAMESPACE, "objTestReturnProducts");
        
        
        Random r=new Random();
		int number=(r.nextInt(8000) +65);
			int transactionId = number;
        
			String posId = "1";
			
        
        	int userId = loginPref.getInt("UserId", -1);
        	
        	SoapObject product1 = new SoapObject(NAMESPACE, "TestProduct");
        	product1.addProperty("RecordId", 0); 
            product1.addProperty("ProductId", 0);
            product1.addProperty("ProductName",null);
            product1.addProperty("Type", 1);
            product1.addProperty("Quantity", 0);
            product1.addProperty("Barcode",null);
            product1.addProperty("ItemId",null);
            product1.addProperty("Price",null);
            product1.addProperty("CurrencyPrice",null);
            product1.addProperty("Discount",null);
            product1.addProperty("Tax",null);
            product1.addProperty("retrurnquantity", 1);
            product1.addProperty("RetrunReason_ID", 1);
        	product1.addProperty("InvoiceDetailID", 0);
        	
        	PropertyInfo pi = new PropertyInfo();
            pi.setName("TestProduct");
            pi.setValue(product1);
            pi.setType(product1.getClass());
        	
            users.addProperty(pi);
       
        
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            String currentDate = df.format(new Date());
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("objTestReturnProducts");
        pi3.setValue(users);
        pi3.setType(users.getClass());
        Request.addProperty(pi3);
        
        
    	SoapObject objReturnVoice = new SoapObject(NAMESPACE, "WebInvoice");
        
       
        objReturnVoice.addProperty("InvoiceId",0);
        objReturnVoice.addProperty("InvoiceNumber",null);
        objReturnVoice.addProperty("InvoiceDate",null);
        objReturnVoice.addProperty("CustomerId",0);
        objReturnVoice.addProperty("TotalAmount",null);
        objReturnVoice.addProperty("Tax",null);
        objReturnVoice.addProperty("Discount",null);
        
        objReturnVoice.addProperty("GrandTotal",null);
       
        objReturnVoice.addProperty("Retrun_DateTime",currentDate);
        objReturnVoice.addProperty("ReturnStoreID",1);
        objReturnVoice.addProperty("RetrunPosID","1");
        objReturnVoice.addProperty("ReturnCashMedia","Cash");
       
     
        objReturnVoice.addProperty("RetrunReason_ID",1);
        
        objReturnVoice.addProperty("RetrunTotal","3");
        objReturnVoice.addProperty("Transaction_ID","1403701704");
        objReturnVoice.addProperty("ReturnTotal","23");
        
      
        objReturnVoice.addProperty("ReturnType","1");
        
        objReturnVoice.addProperty("InvoiceStatus",false);
        objReturnVoice.addProperty("PaymentMethodGrid","Cash");
       
        objReturnVoice.addProperty("ReturnDate",null);
        objReturnVoice.addProperty("ReturnTime",null);
        
        objReturnVoice.addProperty("Paid",null);
     
        objReturnVoice.addProperty("InvoiceDateTime",null);
     
        
        PropertyInfo p111 = new PropertyInfo();
        p111.setName("objWebReturnInvoice");
        p111.setValue(objReturnVoice);
        p111.setType(objReturnVoice.getClass());
        Request.addProperty(p111);
        
       
        
        
        PropertyInfo p24 = new PropertyInfo();
        p24.setName("UserId");
        p24.setValue(userId);
        p24.setType(Integer.class);
        Request.addProperty(p24);
        
        PropertyInfo p25 = new PropertyInfo();
        p25.setName("PosId");
        p25.setValue("1");
        p25.setType(String.class);
        Request.addProperty(p25);
        
        
        
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);
        
        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
        androidHttpTransport.debug = true;
        /*
         * Call the web service and retrieve result ... how luvly <3
         * 
         * */
        try
        {
        	
        	
            androidHttpTransport.call(SOAP_ACTION, envelope);
           
			SoapObject response = (SoapObject)envelope.getResponse();
          
			Log.e("response "," "+ response);
            
           
           
            System.out.println(response.getProperty("Status"));
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
