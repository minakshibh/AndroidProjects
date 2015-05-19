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

import com.example.posapplication.R;
import com.pos.retail.ReceiptGenerate;
import com.pos.retail.ReturnDetailActivity;
import com.pos.retail.TransactionActivity;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TransactionSaveOnWeb {

	Context mcontext;
	ConnectionDetector detector;
	int employee_id,currencyId;
	SharedPreferences loginPref;
	ProgressDialog dialog;
	String paymentType;
	String transactionId;
	String voucherName ;
	String givenAmount;
	String discountAmount;
	int discountType;
	String totalTax,totalDiscount,voucherId, finalTotal, subTotalAmt, changeValue, amountPaid;
	BigDecimal payableMoney;
	
	int flag = 0;
	SharedPreferences posPref;
	Editor posEditor;
	String producturl,Method_Name="GetProductList";
	private String status = "failure";
	private boolean isBackgroundTask = false;
	
	public TransactionSaveOnWeb( Context context ,String totalTax,String totalDiscount,BigDecimal payableAmount,String paymentMethod,String voucherName,String voucherId,
			int currencyId,int discounttype,String discountAmount, String finalTotal, String subTotalAmt, int flag, String transactionId, boolean isBackground, String changeValue, String amountPaid) {
		// TODO Auto-generated constructor stub
		mcontext = context;
		detector = new ConnectionDetector(mcontext);
		
		loginPref = mcontext.getSharedPreferences("LoginPref",Context.MODE_PRIVATE);
		
		posPref = mcontext.getSharedPreferences("pos", Context.MODE_PRIVATE);
		
		posEditor = posPref.edit();
		employee_id = loginPref.getInt("UserId", 0);
		
		producturl=mcontext.getResources().getString(R.string.liveurl)+"/"+Method_Name;
		
		SharedPreferences pref = mcontext.getSharedPreferences("mypref", Context.MODE_PRIVATE);
		pref.edit().putString("ReceiptGenerate", "ReceiptGenerate").commit();
		
		this.voucherId = voucherId;
		this.paymentType = paymentMethod;
		this.totalTax = totalTax;
		this.totalDiscount = totalDiscount;
		this.payableMoney = payableAmount;
		this.voucherName = voucherName;
		
		discountType = discounttype;
		this.discountAmount =discountAmount;
		this.currencyId = currencyId;
		this.finalTotal = finalTotal;
		this.subTotalAmt = subTotalAmt;
		this.flag = flag;
		this.transactionId = transactionId;
		this.isBackgroundTask = isBackground;
		this.changeValue = changeValue;
		this.amountPaid = amountPaid;
	}
	
	public void saveTransactionInOfflineMode(){
		PoSDatabase database = new PoSDatabase(mcontext);
	}
	
	public class SaveTransaction  extends AsyncTask<ArrayList<ProductReal>, String, String>
	{
		
		ArrayList<ProductReal> productList, productsInInventory = null;
		int result=1;
		
		protected void onPreExecute() {
		    
			super.onPreExecute();
			
			if(flag == 0){
					dialog=new ProgressDialog(mcontext);
		    	
		    	dialog.setMessage("Update Transaction");
		    	dialog.setCanceledOnTouchOutside(false);
		    	dialog.show();
			}
		}
		
		protected String doInBackground(ArrayList<ProductReal>... params) {
			// TODO Auto-generated method stub
			
			productList = params[0]; 
			if(detector.isConnectingToInternet())
			{
				Log.e("saving transaction","saving transac");
					WebServiceCallExample(productList);
			
					
			}else{
				Log.e("saving transaction","net not working");
			}
			return null;
		}
		public void onPostExecute(String res)
		{
			
			if(flag == 0){
				dialog.dismiss();
			}
//			if(status.equalsIgnoreCase("success")){
				try{
					PoSDatabase	db =new PoSDatabase(mcontext);
					db.deleteTransaction(String.valueOf(transactionId));
				}catch(Exception e){
					
				}
//			}
			
			Intent in=new Intent(mcontext,ReceiptGenerate.class);
//			in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//			
			Log.e("compare ", ""+(((Activity)mcontext).getClass()).equals(TransactionActivity.class));
			
			in.putExtra("listofproduct", productList);
			in.putExtra("TransactionId", String.valueOf(transactionId));
			in.putExtra("voucher_name", voucherName);
			in.putExtra("voucherId", voucherId);
			in.putExtra("voucherName", voucherName);
			in.putExtra("given", givenAmount);
			in.putExtra("MDiscount", discountAmount);
			in.putExtra("finaltotal",finalTotal);
			in.putExtra("totalDiscount",totalDiscount);
			in.putExtra("totalTax", totalTax);
			in.putExtra("changeValue", changeValue);
			in.putExtra("amountPaid", amountPaid);
			if(!isBackgroundTask && status.equals("success"))
			{
					if(flag == 0){
						
						mcontext.startActivity(in);
					
						if(!(((Activity)mcontext).getClass()).equals(TransactionActivity.class))
						{
							((Activity)mcontext).finish();
						}
					}
			}
			else if(!status.equals("success"))
			{
				if(flag == 0){
					PoSDatabase db=new PoSDatabase(mcontext);
					Transaction transaction=new Transaction();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String currentDateandTime = sdf.format(new Date());
					
					System.out.println(currentDateandTime);
//					 transactionId = transaction.getTransaction_Id();
					
					
						transaction.setDate(currentDateandTime);
						transaction.setEmployee_Id(String.valueOf(employee_id));
						transaction.setPos_Id("1");
						transaction.setStore_Id("1");
						transaction.setTransaction_Id(String.valueOf(transactionId));
						transaction.setSubTotal(subTotalAmt);
						transaction.setTotal(finalTotal);
						transaction.setDiscount(totalDiscount);
						transaction.setVoucherId(voucherId);
						transaction.setCurrencyId(currencyId);
						transaction.setPaymentMethod(paymentType);
						transaction.setmDiscountAmount(discountAmount);
						transaction.setmDiscountType(String.valueOf(discountType));
						transaction.setDiscount(totalDiscount);
						transaction.setTax(totalTax);
						transaction.setPayableAmt(payableMoney.toPlainString());
						
						db.saveTransaction(transaction);
						
						
						for(int i=0;i<productList.size();i++)
						{
							 db=new PoSDatabase(mcontext);
							
							 db.buyProductDetail(productList.get(i), ""+transactionId);
							
						}
						
						if(!isBackgroundTask){
							
							if(!(((Activity)mcontext).getClass()).equals(TransactionActivity.class))
							{
								((Activity)mcontext).finish();
							}
							mcontext.startActivity(in);
					
						}
						
						/*Intent in=new Intent(mcontext,ReceiptGenerate.class);
						in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						in.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						
						Log.e("compare ", ""+(((Activity)mcontext).getClass()).equals(TransactionActivity.class));
						if(!(((Activity)mcontext).getClass()).equals(TransactionActivity.class))
						{
							((Activity)mcontext).finish();
							
						}
						
						in.putExtra("listofproduct", productList);
						in.putExtra("TransactionId", String.valueOf(transactionId));
						in.putExtra("voucher_name", voucherName);
						in.putExtra("voucherId", voucherId);
						in.putExtra("voucherName", voucherName);*/
						
				}
			}

			/*PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask(mcontext);
	        performBackgroundTask.execute();*/
		}
	}
	
	public void WebServiceCallExample(ArrayList<ProductReal> listOfProducts)
    {
		
        String NAMESPACE = "http://tempuri.org/";
        String METHOD_NAME = "UpdateTransaction";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        
        String URL = mcontext.getResources().getString(R.string.liveurl);
        SoapObject Request = new SoapObject(NAMESPACE, METHOD_NAME);
        
        SoapObject users = new SoapObject(NAMESPACE, "objTestProductList");
        
        
        Random r=new Random();
//         transactionId = String.valueOf(System.currentTimeMillis()/1000);
			
       String posId = "1";
			
        for(int i=0;i<listOfProducts.size();i++)
        {
        	
        	ProductReal product = listOfProducts.get(i);
        	Log.e("transactionId",""+ transactionId);
        	Log.e("product id", ""+product.getPid());
        	
        	Log.e("product.getProductName()", ""+product.getProductName());
        	
        	Log.e("product.getSelectedQuatity()", ""+product.getSelectedQuatity());
        	Log.e("Barcode", ""+product.getBarCode());
        	Log.e("Price", ""+product.getPrice().toString());
        	Log.e("discount", ""+product.getSingleItemDiscount().toString());
        	Log.e("Tax", ""+product.getTax().toString());
        	Log.e("MDiscount", ""+product.getMdiscount());
        	Log.e("MDType", ""+product.getMDType());
        	
        	Log.e("Voucher ", ""+voucherId);
            Log.e("PayM ", ""+paymentType);
             
            Log.e("DiscountAmount ", ""+discountAmount);
            Log.e("discountType ", ""+discountType);
             
             
             
        	SoapObject product1 = new SoapObject(NAMESPACE, "TestProduct");
        	product1.addProperty("RecordId", transactionId); 
        	
        	
            product1.addProperty("ProductId", product.getPid());
            product1.addProperty("ProductName",product.getProductName());
            product1.addProperty("Type", 1);
            product1.addProperty("Quantity", product.getSelectedQuatity());
            product1.addProperty("Barcode",product.getBarCode());
            product1.addProperty("ItemId",product.getItemId());
            product1.addProperty("Price",product.getPrice().toString());
            product1.addProperty("CurrencyPrice","1");
            if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
            	product1.addProperty("Discount",product.getMdiscount().toString());
            }else
            	product1.addProperty("Discount",product.getSingleItemDiscount().toString());
            product1.addProperty("Tax",product.getSingleItemTax().toString());
            product1.addProperty("retrurnquantity", 0);
            product1.addProperty("RetrunReason_ID", 0);
        	product1.addProperty("InvoiceDetailID", 0);
        	
        	System.out.println(String.valueOf(product.getMdiscount())+" "+product.getMDType());
        	
        	product1.addProperty("MDiscount", String.valueOf(product.getMdiscount()));
        	
        	
        	product1.addProperty("DiscountType", product.getMDType());
        	product1.addProperty("ManualDis", null);
        	
        	PropertyInfo pi = new PropertyInfo();
            pi.setName("TestProduct");
            pi.setValue(product1);
            pi.setType(product1.getClass());
        	
            users.addProperty(pi);
        }
        
        
        PropertyInfo pi3 = new PropertyInfo();
        pi3.setName("objTestProductList");
        pi3.setValue(users);
        pi3.setType(users.getClass());
        Request.addProperty(pi3);
        
        PropertyInfo p1 = new PropertyInfo();
        p1.setName("UserId");
        p1.setValue(employee_id);
        p1.setType(Integer.class);
        Request.addProperty(p1);
        
        PropertyInfo p2 = new PropertyInfo();
        p2.setName("transactionId");
        p2.setValue(String.valueOf(transactionId));
        p2.setType(String.class);
        Request.addProperty(p2);
        
        
        PropertyInfo p3 = new PropertyInfo();   ///Problem is here
        p3.setName("Paid");
        p3.setValue(null);
        p3.setType(String.class);
        
        
        PropertyInfo p4 = new PropertyInfo();
        p4.setName("PosId");
        p4.setValue(posId);
        p4.setType(String.class);
        Request.addProperty(p4);
        
        PropertyInfo p5 = new PropertyInfo();
        p5.setName("invoiceStatus");
        p5.setValue("true");
        p5.setType(String.class);
        Request.addProperty(p5);
        
      
       
       
        PropertyInfo p6 = new PropertyInfo();
        p6.setName("VoucherNumber");						//Chnage done here
        p6.setValue(voucherId);
        p6.setType(String.class);
        Request.addProperty(p6);
        
        
        
        PropertyInfo p9 = new PropertyInfo();
        p9.setName("CurrencyId");						//Chnage done here
        p9.setValue(currencyId);
        p9.setType(Integer.class);
        Request.addProperty(p9);
        
        
        PropertyInfo p7 = new PropertyInfo();
        p7.setName("CustomerId");
        p7.setValue(null);
        p7.setType(String.class);
        Request.addProperty(p7);
        
        PropertyInfo p8 = new PropertyInfo();
        p8.setName("PayM");											//Chnages done here
        p8.setValue(paymentType);
        p8.setType(String.class);
        Request.addProperty(p8);
       
        
        PropertyInfo p10 = new PropertyInfo();
        p10.setName("DiscountAmount");											//Chnages done here
        p10.setValue(discountAmount);
        p10.setType(String.class);
        Request.addProperty(p10);
        
        
        PropertyInfo p11 = new PropertyInfo();
        p11.setName("DiscountType");											//Chnages done here
        p11.setValue(discountType);
        p11.setType(Integer.class);
        Request.addProperty(p11);
        
        
        
        PropertyInfo p12 = new PropertyInfo();
        p12.setName("Discount");											//Chnages done here
        p12.setValue(totalDiscount);
        p12.setType(String.class);
        Request.addProperty(p12);
        
        PropertyInfo p13 = new PropertyInfo();
        p13.setName("Tax");											//Chnages done here
        p13.setValue(totalTax);
        p13.setType(String.class);
        Request.addProperty(p13);
        
        PropertyInfo p14 = new PropertyInfo();
        p14.setName("FinalTotal");											//Chnages done here
        p14.setValue(finalTotal);
        p14.setType(String.class);
        Request.addProperty(p14);
        
        PropertyInfo p15 = new PropertyInfo();
        p15.setName("TotalAmount");											//Chnages done here
        p15.setValue(subTotalAmt);
        p15.setType(Integer.class);
        Request.addProperty(p15);
  
    
        System.out.println(Request.getAttributeCount());
        
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
            Log.e("response ",""+ response);
            
            System.out.println(response.getProperty("Status"));
            SharedPreferences transactionPrefs = mcontext.getSharedPreferences("transPref", mcontext.MODE_PRIVATE);
            String transAmountStr = transactionPrefs.getString("transAmount", "0");
			BigDecimal transAmount = BigDecimal.valueOf(Double.parseDouble(transAmountStr));
			transAmount = transAmount.add(payableMoney);
			Editor editor = transactionPrefs.edit();
			editor.putString("transAmount", transAmount.toString());
			editor.commit();
            
			status = "success";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            status = "failure";
        }
    }
}