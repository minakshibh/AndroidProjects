package com.pos.util;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.posapplication.R;
import com.pos.retail.ItemReturnsActivity;
import com.pos.retail.ReceiptGenerate;
import com.pos.retail.RetunReceipt;

public class SaveReturnItemOnServer {

	Context mcontext;
	ConnectionDetector detector;
	int employee_id;
	SharedPreferences loginPref;
	ProgressDialog dialog;
	String paymentType="Cash";
	String transactionId;
	ArrayList<ProductReal> returnProductList ;
	int reasonId;
	String returnTotal , returnType;
	String totalTax,totalDiscount,totalMDiscount, voucherId, newTransactionId,voucherName,voucherAmount,voucherCreationData,voucherValidDate;
	SharedPreferences pref ;
	int currencyId;
	ArrayList<ProductReal> remList;
	String remTotal;
	String remTax;
	String remDiscount;
	String remMDiscount;
	String pendingTotal;
	BigDecimal payableMoney;
	String ret_subTotal, ret_total, ret_Tax, ret_disc, ret_MDisc;
	SharedPreferences posPref;
	Editor posEditor;
	String producturl,Method_Name="GetProductList";
	
	public SaveReturnItemOnServer( Context context, String ret_subTotal, String ret_total, String ret_Tax, String ret_disc, String ret_MDisc, String pending_total, String totalTax, String totalDiscount, String totalMDiscount, String transactionId, String newTransactionId,String paymentMethod,ArrayList<ProductReal> list,int reasonId,String returnTotal, String returnType, int currencyId, ArrayList<ProductReal> remList, String remTotal, String remTax, String remDiscount, String remMDiscount, BigDecimal payableMoney,String voucherId,String voucherName,String voucherAmount,String voucherCreationDate,String voucherValidDate) {
		// TODO Auto-generated constructor stub
		mcontext = context;
		detector = new ConnectionDetector(mcontext);
		
		loginPref = mcontext.getSharedPreferences("LoginPref",Context.MODE_PRIVATE);
		
		employee_id = loginPref.getInt("UserId", 0);
		
		pref = mcontext.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		posPref = mcontext.getSharedPreferences("pos", Context.MODE_PRIVATE);
		
		posEditor = posPref.edit();
		this.totalMDiscount = totalMDiscount;
		this.transactionId = transactionId;
		this.voucherId = voucherId;
		this.voucherName=voucherName;
		this.voucherCreationData=voucherCreationDate;
		this.voucherValidDate=voucherValidDate;
		this.voucherAmount=voucherAmount;
		this.paymentType = paymentMethod;
		this.totalTax = totalTax;
		this.totalDiscount = totalDiscount;
		this.reasonId = reasonId;
		returnProductList = list;
		this.returnTotal = returnTotal;
		this.returnType = returnType;
		this.newTransactionId = newTransactionId;
		
		this.currencyId = currencyId;
		this.remList = remList;
		this.remTotal = remTotal;
		this.remTax = remTax;
		this.remDiscount = remDiscount;
		this.remMDiscount = remMDiscount;
		this.payableMoney = payableMoney;
		this.pendingTotal = pending_total;
		
		this.ret_disc = ret_disc;
		this.ret_MDisc = ret_MDisc;
		this.ret_subTotal = ret_subTotal;
		this.ret_Tax = ret_Tax;
		this.ret_total = ret_total;
		
		producturl=mcontext.getResources().getString(R.string.liveurl)+"/"+Method_Name;
		
		Log.e("Hello","hello");
	}
	
	public class SaveTransaction  extends AsyncTask<ArrayList<ProductReal>, String, String>
	{
		ArrayList<ProductReal> productList;
		ArrayList<ProductReal> productsInInventory = null;
		int result=1;
		
		protected void onPreExecute() {
		    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Update Return Transaction");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		
		protected String doInBackground(ArrayList<ProductReal>... params) {
			// TODO Auto-generated method stub
			
			productList = params[0]; 
			if(detector.isConnectingToInternet()){
					WebServiceCallExample(productList);
			
			}
			
			return null;
		}
		public void onPostExecute(String res)
		{
			dialog.dismiss();
			
			if(detector.isConnectingToInternet())
			{
				pref.edit().putString("ReceiptReturned", "ReceiptReturned").commit();
				
				((Activity)mcontext).finish();
				
				Intent in=new Intent(mcontext,ReceiptGenerate.class);
//				in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				in.putExtra("TransactionId", transactionId);
				in.putExtra("listofproduct", productList);
				
				in.putExtra("voucherID", voucherId);
			    in.putExtra("voucherName", voucherName);
			    in.putExtra("voucherAmount", voucherAmount);
			    in.putExtra("voucherCreateDate", voucherCreationData);
			    in.putExtra("voucherValiedDate", voucherValidDate);
			    in.putExtra("changeValue", "0");
			    in.putExtra("amountPaid", ret_subTotal);
			    
			    in.putExtra("totalDiscount", ret_disc);
			    in.putExtra("finaltotal", ret_subTotal);
			    in.putExtra("totalTax", ret_Tax);
			    in.putExtra("returnTotal", ret_total);
			    in.putExtra("MDiscount", ret_MDisc);

			    if(remList!=null & remList.size()>0)
			        in.putExtra("newTransactionid", newTransactionId);
			        
//			        in.putExtra("returnProductList", returnProductList);
			           in.putExtra("remainingProductList", remList);
			           in.putExtra("rem_total", remTotal);
			           in.putExtra("rem_Discount", remDiscount);
			           in.putExtra("rem_MDiscount", remMDiscount);
			           in.putExtra("rem_totalTax", remTax);
//			           in.putExtra("MDiscount", ret_MDisc);
			           
				mcontext.startActivity(in);
				SharedPreferences transactionPrefs = mcontext.getSharedPreferences("transPref", mcontext.MODE_PRIVATE);
				String transAmountStr = transactionPrefs.getString("transAmount", "0");
				
				BigDecimal transAmount = BigDecimal.valueOf(Double.parseDouble(transAmountStr));
				transAmount = transAmount.subtract(payableMoney);
				Editor editor = transactionPrefs.edit();
				editor.putString("transAmount", transAmount.toString());
				editor.commit();
			}
			else
			{
				
				AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);
				alert.setTitle("Internet Problem");
				alert.setMessage("Internet connection is not available");
				alert.setPositiveButton("OK", null);
				alert.show();
				
			}
		}
		
		
	}
	public void WebServiceCallExample(ArrayList<ProductReal> listOfProducts)
    {
		
		String NAMESPACE = "http://tempuri.org/";
        String METHOD_NAME = "UpdateReturns1";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
//        String URL = "http://112.196.24.205:1123/LogIn.asmx";
        
        String URL =mcontext.getResources().getString(R.string.liveurl);
        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        
        SoapObject users = new SoapObject(NAMESPACE, "objTestReturnProducts");
        
        
        	int userId = loginPref.getInt("UserId", -1);
        	
        	for(int i=0;i<returnProductList.size();i++)
        	{ 
	        	SoapObject product1 = new SoapObject(NAMESPACE, "TestProduct");
	        	product1.addProperty("RecordId", 0); 
	        	product1.addProperty("ProductId", returnProductList.get(i).getPid());
                product1.addProperty("ProductName",returnProductList.get(i).getProductName());
                product1.addProperty("Type", 1);
                product1.addProperty("Quantity", returnProductList.get(i).getInitialQuantity());
                product1.addProperty("Barcode",returnProductList.get(i).getBarCode());
                product1.addProperty("ItemId",returnProductList.get(i).getItemId());
                product1.addProperty("Price",returnProductList.get(i).getPrice().toString());
                product1.addProperty("CurrencyPrice","1");
                
                if(returnProductList.get(i).getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
                	product1.addProperty("Discount",returnProductList.get(i).getMdiscount().toString());
                }else
                	 product1.addProperty("Discount",returnProductList.get(i).getSingleItemDiscount().toString());
                
               
                product1.addProperty("Tax",returnProductList.get(i).getSingleItemTax().toString());
	            product1.addProperty("retrurnquantity", returnProductList.get(i).getSelectedQuatity());
	            product1.addProperty("RetrunReason_ID", returnProductList.get(i).getRetunResonId());
	        	product1.addProperty("InvoiceDetailID", returnProductList.get(i).getInvoiceDetailID());
	        	product1.addProperty("DiscountType", returnProductList.get(i).getMDType());
	        	product1.addProperty("MDiscount", returnProductList.get(i).getMdiscount().toString());
	        	
	        	System.out.print("Return ProductList:\n"+product1.toString());
	        	
	        	
	        	PropertyInfo pi = new PropertyInfo();
	            pi.setName("TestProduct");
	            pi.setValue(product1);
	            pi.setType(product1.getClass());
	        	
	            users.addProperty(pi);
        	}
        
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            String currentDate = df.format(new Date());
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("objTestReturnProducts");
        pi3.setValue(users);
        pi3.setType(users.getClass());
        Request.addProperty(pi3);
        
        
    	SoapObject objReturnVoice = new SoapObject(NAMESPACE, "WebInvoice");
    	
    	BigDecimal totalAmt = BigDecimal.valueOf(Double.valueOf(totalDiscount)).add(BigDecimal.valueOf(Double.valueOf(totalMDiscount)));
    	totalAmt = totalAmt.add(BigDecimal.valueOf(Double.valueOf(pendingTotal)));
    	
        objReturnVoice.addProperty("InvoiceId",0);
        objReturnVoice.addProperty("InvoiceNumber",null);
        objReturnVoice.addProperty("InvoiceDate",null);
        objReturnVoice.addProperty("CustomerId",0);
        objReturnVoice.addProperty("TotalAmount",totalAmt.toString());
        objReturnVoice.addProperty("Tax",totalTax);
        
//      BigDecimal discount = BigDecimal.valueOf(Double.valueOf(totalDiscount)).add(BigDecimal.valueOf(Double.valueOf(totalMDiscount)));
        objReturnVoice.addProperty("Discount", totalDiscount.toString());
        
        objReturnVoice.addProperty("GrandTotal",pendingTotal);
        objReturnVoice.addProperty("ReturnStatus", "IsItemReturn");
        objReturnVoice.addProperty("Retrun_DateTime",currentDate);
        objReturnVoice.addProperty("ReturnStoreID",1);
        objReturnVoice.addProperty("RetrunPosID","1");
        objReturnVoice.addProperty("ReturnCashMedia",paymentType);
        objReturnVoice.addProperty("CurrencyID", currencyId);
     
        objReturnVoice.addProperty("RetrunReason_ID",reasonId);
        
        objReturnVoice.addProperty("RetrunTotal","3");
        objReturnVoice.addProperty("Transaction_ID",transactionId);
        objReturnVoice.addProperty("ReturnTotal",returnTotal);
        
        objReturnVoice.addProperty("ReturnType",returnType);
        
        objReturnVoice.addProperty("InvoiceStatus",false);
        
        Log.e("method type ", paymentType);
        objReturnVoice.addProperty("PaymentMethodGrid",paymentType);
       
        objReturnVoice.addProperty("ReturnDate",null);
        objReturnVoice.addProperty("ReturnTime",null);
        
        objReturnVoice.addProperty("Paid","0");
     
        objReturnVoice.addProperty("InvoiceDateTime",null);
        
        objReturnVoice.addProperty("DiscountType",1);
        objReturnVoice.addProperty("DiscountAmount",totalMDiscount.toString());
        
        System.out.println("Return Invoice:\n"+objReturnVoice.toString());
        
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
        Log.e("userid ::: ",",,,, "+userId);
        
        PropertyInfo p25 = new PropertyInfo();
        p25.setName("PosId");
        p25.setValue("1");
        p25.setType(String.class);
        Request.addProperty(p25);
        

        PropertyInfo p26 = new PropertyInfo();
        p26.setName("NewTransactionId");
        p26.setValue(newTransactionId);
        p26.setType(String.class);
        Request.addProperty(p26);
        Log.e("newTransactionId ::: ",",,,, "+newTransactionId);
        
        
//        --------------------------------------------
        
        SoapObject remProducts = new SoapObject(NAMESPACE, "objTestRemainingProducts");

    	for(int i=0;i<remList.size();i++)
    	{
        	SoapObject product1 = new SoapObject(NAMESPACE, "TestProduct");
        	product1.addProperty("RecordId", 0); 
        	product1.addProperty("ProductId", remList.get(i).getPid());
            product1.addProperty("ProductName",remList.get(i).getProductName());
            product1.addProperty("Type", 1);
            product1.addProperty("Quantity", remList.get(i).getSelectedQuatity());
            product1.addProperty("Barcode",remList.get(i).getBarCode());
            product1.addProperty("ItemId",remList.get(i).getItemId());
            product1.addProperty("Price",remList.get(i).getPrice().toString());
            product1.addProperty("CurrencyPrice","1");
            
            if(remList.get(i).getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
            	product1.addProperty("Discount",remList.get(i).getMdiscount().toString());
            }else
            	product1.addProperty("Discount",remList.get(i).getSingleItemDiscount().toString());
            
            
            product1.addProperty("Tax",remList.get(i).getSingleItemTax().toString());
            product1.addProperty("retrurnquantity", remList.get(i).getReturnQunatity());
            product1.addProperty("RetrunReason_ID", remList.get(i).getRetunResonId());
        	product1.addProperty("InvoiceDetailID", remList.get(i).getInvoiceDetailID());
        	product1.addProperty("DiscountType", remList.get(i).getMDType());
        	product1.addProperty("MDiscount", remList.get(i).getMdiscount().toString());
        
        	

             
        	System.out.println("remaining list:\n"+product1.toString());
        	
        	PropertyInfo pInfo = new PropertyInfo();
        	pInfo.setName("TestProduct");
        	pInfo.setValue(product1);
        	pInfo.setType(product1.getClass());
        	
            remProducts.addProperty(pInfo);
    	}
    
    PropertyInfo pRem = new PropertyInfo();
    pRem.setName("objTestRemainingProducts");
    pRem.setValue(remProducts);
    pRem.setType(remProducts.getClass());
    Request.addProperty(pRem);
    
	SoapObject objRemainingVoice = new SoapObject(NAMESPACE, "WebInvoice");
    
	BigDecimal totalRemAmt = BigDecimal.valueOf(Double.valueOf(remDiscount)).add(BigDecimal.valueOf(Double.valueOf(remMDiscount)));
	totalRemAmt = totalRemAmt.add(BigDecimal.valueOf(Double.valueOf(remTotal)));
	
	objRemainingVoice.addProperty("InvoiceId",0);
	objRemainingVoice.addProperty("InvoiceNumber",null);
	objRemainingVoice.addProperty("InvoiceDate",null);
	objRemainingVoice.addProperty("CustomerId",0);
	objRemainingVoice.addProperty("TotalAmount",remTotal);
	objRemainingVoice.addProperty("Tax",remTax);
	
//	BigDecimal remDisc = BigDecimal.valueOf(Double.valueOf(remDiscount)).add(BigDecimal.valueOf(Double.valueOf(remMDiscount)));
	
	objRemainingVoice.addProperty("Discount",remDiscount.toString());
    
	objRemainingVoice.addProperty("GrandTotal",remTotal);
	objRemainingVoice.addProperty("ReturnStatus", "IsItemExchange");
	objRemainingVoice.addProperty("Retrun_DateTime",currentDate);
	objRemainingVoice.addProperty("ReturnStoreID",1);
	objRemainingVoice.addProperty("RetrunPosID","1");
	objRemainingVoice.addProperty("ReturnCashMedia",paymentType);
	objRemainingVoice.addProperty("CurrencyID", currencyId);
 
	objRemainingVoice.addProperty("RetrunReason_ID",reasonId);
    
	objRemainingVoice.addProperty("RetrunTotal","3");
	objRemainingVoice.addProperty("Transaction_ID",transactionId);
	objRemainingVoice.addProperty("ReturnTotal",remTotal);
	
  
	objRemainingVoice.addProperty("ReturnType",returnType);
	objRemainingVoice.addProperty("InvoiceStatus",false);
    
    objRemainingVoice.addProperty("PaymentMethodGrid",paymentType);
   
    objRemainingVoice.addProperty("ReturnDate",null);
    objRemainingVoice.addProperty("ReturnTime",null);
    
    objRemainingVoice.addProperty("Paid","0");
 
    objRemainingVoice.addProperty("InvoiceDateTime",null);
 
    objRemainingVoice.addProperty("DiscountType",1);
    objRemainingVoice.addProperty("DiscountAmount",remMDiscount.toString());
    
    System.out.println("Remaining Voice:\n"+objRemainingVoice.toString());
    
    PropertyInfo p112 = new PropertyInfo();
    p112.setName("objWebRemainingInvoice");
    p112.setValue(objRemainingVoice);
    p112.setType(objRemainingVoice.getClass());
    Request.addProperty(p112);
    
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(Request);
        
//        envelope.addMapping(NAMESPACE, "TestProduct",TestProduct.class);
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
			Log.e("response"," "+ response);
            System.out.println(response.getProperty("Status"));
        }
        catch(Exception e)
        {
        	Log.e("error "," "+ e.getMessage()); 
            e.printStackTrace();
        }
    }
}
