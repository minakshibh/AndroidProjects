
package com.pos.retail;


import java.math.BigDecimal;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.posapplication.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

public class KsoapLibrarySample extends Activity{

	private final String SOAP_NAMESPACE = "http://tempuri.org/";
	private String SOAP_URL ; //= "http://10.52.0.114:8082/Service1.asmx";
	 
	private final String SOAP_ACTION = "http://tempuri.org/TestingProduct";
	 
	private final String SOAP_METHOD_NAME = "TestingProduct";
	 private SoapObject request;
	 private PropertyInfo pi1;
	 
	private PropertyInfo pi2;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		
		setContentView(R.layout.login);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		SOAP_URL=getResources().getString(R.string.liveurl);
		
		request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_NAME);
		 
		
		SoapObject product2 = new SoapObject(SOAP_NAMESPACE, "objProductList");
    	product2.addProperty("RecordId", 4); 
    	product2.addProperty("ProductId", 4);
    	product2.addProperty("ProductName","TestProduct");
    	product2.addProperty("Type", 1);
    	product2.addProperty("Quantity", 1);
    	product2.addProperty("Barcode","TestBarcode");
    	product2.addProperty("ItemId","Test2");
    	product2.addProperty("Price","12");
    	product2.addProperty("CurrencyPrice","$89");
    	product2.addProperty("Discount","12");
    	product2.addProperty("Tax","12");
    	product2.addProperty("retrurnquantity", 1);
    	product2.addProperty("RetrunReason_ID", 1);
    	product2.addProperty("InvoiceDetailID", 1);
    	
        PropertyInfo pi2 = new PropertyInfo();
        pi2.setName("objProductList");
        pi2.setValue(product2);
        pi2.setType(product2.getClass());
        request.addProperty(pi2);
		
		
		
		 
		 SoapSerializationEnvelope envp = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		 envp.dotNet = true;
		 envp.setOutputSoapObject(request);
		 HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
		 try {
		 androidHttpTransport.call(SOAP_ACTION, envp);
		 SoapObject response = (SoapObject)envp.getResponse();
         
		 Log.e("Response ", ""+response);
		 } catch (Exception e) {
		Log.i("WS Error->",e.toString());
		 }
		
		
		
	}
}
