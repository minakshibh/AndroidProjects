package com.pos.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.spec.PSource;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.example.posapplication.R;
import com.pos.util.TransactionSaveOnWeb.SaveTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class PerformBackgroundTask extends AsyncTask<String, String, String>{

	Context mcontext;
	String producturl,Method_Name="GetProductList";
	public PerformBackgroundTask(Context context)
	{
		mcontext = context;
		producturl = mcontext.getResources().getString(R.string.liveurl)+"/"+Method_Name;
	}
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		ConnectionDetector detector = new ConnectionDetector(mcontext);
		
		if(detector.isConnectingToInternet())
		{
//			updateInventory();
			
			PoSDatabase db = new PoSDatabase(mcontext);
			ArrayList<Transaction> list = db.getTransactions();
			
			
			for(int i=0;i<list.size();i++)
			{
				
				Transaction transaction = list.get(i);
				
				if(transaction.getQueryType().equals("Transaction"))
				{
					///commented by Minakshi////
			    		/*String NAMESPACE = "http://tempuri.org/";
				        String METHOD_NAME = "UpdateTransaction";
				        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
//				        String URL = "http://112.196.24.205:1123/LogIn.asmx";
				        
				        String URL = mcontext.getResources().getString(R.string.liveurl);
				        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
				        
				        SoapObject users = new SoapObject(NAMESPACE, "objTestProductList");*/
				        
				       String transactionId = transaction.getTransaction_Id();
				       
				       db =new PoSDatabase(mcontext);
				       
				       ArrayList<ProductReal> listofProductbuy = db.getBuyProducts(transactionId);
				       
				       TransactionSaveOnWeb transactionObj = new TransactionSaveOnWeb(mcontext,transaction.getTax().toString(),transaction.getDiscount().toString(),BigDecimal.valueOf(Double.valueOf(transaction.getPayableAmt())),transaction.getPaymentMethod(),transaction.getVoucherId()
								,transaction.getVoucherId(),transaction.getCurrencyId(),Integer.parseInt(transaction.getmDiscountType()),transaction.getmDiscountAmount().toString(), transaction.getTotal().toString(), transaction.getSubTotal().toString(), 1, transaction.getTransaction_Id(), true, "0", transaction.getSubTotal().toString());
				       transactionObj.new SaveTransaction().execute(listofProductbuy);
				}
			}
		}
		
		return null;
	}
	
	public void updateInventory()
	{
		ArrayList<ProductReal> productList =null;
		int result=1;
		SharedPreferences posPref = mcontext.getSharedPreferences("pos",Context.MODE_PRIVATE);
		SharedPreferences.Editor posEditor = posPref.edit();
		int timestamp=posPref.getInt("producttimestamp", -1);	
		InputStream is = null;
		try
		{
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(producturl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("timestamp",Integer.toString(timestamp)));
	        
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
		if(result==0 && productList !=null)
	     {
	    	 
   		
	    	 for(int i=0;i<productList.size();i++)
	    	 {
	    		 
	    		 ProductReal product=productList.get(i);
	    		 PoSDatabase db=new PoSDatabase(mcontext);
	    		 
	    		 if(product.isDeleted())
	    		 {
	    			 db.deleteProduct(product.getPid());
//	    			 productList.remove(i);
	    		 }
	    		 else
	    		 {
	    			 db.addProduct(product);
	    		 }
	    		 
	    	 }
	    	 
	     }
		
	}
}
