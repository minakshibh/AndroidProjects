package com.pos.retail;


import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.StarMicronics.StarIOSDK.PrinterTypeActivity;
import com.StarMicronics.StarIOSDK.StarIOSDKPOSPrinterRasterModeActivity;
import com.example.posapplication.R;
import com.pos.util.PoSDatabase;
import com.pos.util.ProductReal;
import com.print.Utility;
public class ReceiptGenerate extends Activity
{
	 private static final String LOG_TAG = "GeneratePDF";
	 private EditText preparedBy;
	 private File pdfFile;
	 private String filename = "Sample.pdf";
	 private String filepath = "POS";
	 String selectedOption="";
	 ProgressDialog progress_dialog;
//	 private BaseFont bfBold;
	 Context mcontext;
	 BigDecimal totalDiscount,totalPayAble,subTotalBD;
	 ArrayList<ProductReal> productList,remainingList;
	 private Button pdfbtn;
	 ProgressDialog dialog;
	 public static String TransactionId,newTransactionId;
	 String naira,givenAmount,mDiscount,finalAmount,finalTotal,totalDiscounts,totalTax, changeValue, amountPaid;
	 String voucherName= null,VoucherId = null;
	 String retvoucherAmount= null,retVoucherValidDate = null,retVoucherID=null,retVocuherType=null,retVocuherCreationDate=null;
	 String query="";
	 SharedPreferences salePref;
	 SharedPreferences.Editor saleEditor;
	 WebView webView;
	 String html="",returnHtml="";
	 int SELECT_PICTURE=1;
     Uri  selectedImageUri;
     String selectedImagePath;
     SharedPreferences imagePathPref;
     String currentDateandTime;
     String response="";
     String htmlDocument="";
     String cashType="";
     String voucherData="";
     String ApplyVocher="";
     String checkRetrunBy="";
     String cashReturnHtml="";
     String returnAmount="";
     String retrunAmounts="";
     boolean againReceipt=false;
     String saveOldTransactionID="";
     String returnVoucherData="";
     boolean checkreturnVoucherData=false;
     String folderPath="POS-Receipts";
     ArrayList<String> pathOfImages=new ArrayList<String>();
     boolean voucherReceipt=false;
     boolean isEditing = false;
     boolean firstTime=true;
     /*jaswinder**/
     String remTax="",remDiscount="",remMDiscount="",remTotal="";
     String remainsHtml="";
     /**newly code*/
     String payBy="";
     String remainingExeComplete="",returningExeComplete="";
 	 String formattedDate;
 	 String headerLine1="",headerLine2="",headerLine3="",headerLine4="",headerLine5="",itemText="";
 	 String footerLine1="",footerLine2="",footerLine3="",footerLine4="",imageBase64="";
     
	@SuppressWarnings("unchecked")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.receiptgenerate);
		
		Calendar c = Calendar.getInstance();
		System.out.println("Current time => " + c.getTime());

		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		formattedDate = df.format(c.getTime());
		
		folderPath=folderPath+"/"+formattedDate;
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		currentDateandTime = sdf.format(new Date());
		totalDiscount=new BigDecimal(0);
		subTotalBD=new BigDecimal(0);
		mcontext = this;
		pdfbtn = (Button) findViewById(R.id.pdf);
		pdfbtn.setVisibility(View.GONE);
		/*******written by Jaswinder**********/
		webView=(WebView)findViewById(R.id.webViewDisplayReceipt);
		webView.setVisibility(View.INVISIBLE);
		
		imagePathPref=getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		cashType=imagePathPref.getString(Utility.CashType, "");
		checkRetrunBy=imagePathPref.getString(Utility.ItemReturnBy, "");
		Utility.pathList().clear();
	
		Log.i("tag", "Cash Type:"+cashType);
		Log.i("tag", "checkRetrunBy:"+checkRetrunBy);
//		if(checkRetrunBy.equals("Voucher"))
//		{
//			checkreturnVoucherData=true;
//		}
		/************************************/
//		pdfbtn.setVisibility(View.GONE);
		
		naira = getResources().getString(R.string.naira);
		Log.e("naira", naira+query); 
		Intent in=getIntent();
		TransactionId = in.getStringExtra("TransactionId");
		saveOldTransactionID=TransactionId;
		Log.i("tag", "TID:"+saveOldTransactionID);
		newTransactionId=in.getStringExtra("newTransactionid");
		Log.i("tag", "new TID:"+newTransactionId);
		
		if(newTransactionId!=null || newTransactionId=="")
		{
			isEditing = true;
			TransactionId=newTransactionId;
		}
		
		/**newly code**/
		if(checkRetrunBy=="" || checkRetrunBy==null)
		{
			payBy="Pay by : "+cashType+"";
			Log.i("tag", ""+payBy);
		}
		else
		{
			isEditing = true;
			Log.i("tag", "Return case:"+payBy);
			payBy="";
			
		}
		
//		if(checkRetrunBy!=null || !checkRetrunBy.equals(""))
//		{
//			isEditing = true;
//		}
		
		/**Get Header and Footer Detail***/
		headerLine1=imagePathPref.getString(Utility.HeaderLine1, "");
		headerLine2=imagePathPref.getString(Utility.HeaderLine2, "");
		headerLine3=imagePathPref.getString(Utility.HeaderLine3, "");
		headerLine4=imagePathPref.getString(Utility.HeaderLine4, "");
		headerLine5=imagePathPref.getString(Utility.HeaderLine5, "");
		itemText=imagePathPref.getString(Utility.ITEMText, "");
		
		footerLine1=imagePathPref.getString(Utility.FooterLine1, "");
		footerLine2=imagePathPref.getString(Utility.FooterLine2, "");
		footerLine3=imagePathPref.getString(Utility.FooterLine3, "");
		footerLine4=imagePathPref.getString(Utility.FooterLine4, "");
		
		imageBase64=imagePathPref.getString(Utility.ImageData, "");
	
		productList = (ArrayList<ProductReal>)in.getSerializableExtra("listofproduct");
//		returnList = (ArrayList<ProductReal>)in.getSerializableExtra("returnProductList");
		remainingList = (ArrayList<ProductReal>)in.getSerializableExtra("remainingProductList");
		//return voucher data
		retVoucherID=in.getStringExtra("voucherID");
		retVocuherType=in.getStringExtra("voucherName");
		retvoucherAmount=in.getStringExtra("voucherAmount");
		retVoucherValidDate=in.getStringExtra("voucherValiedDate");
		retVocuherCreationDate=in.getStringExtra("voucherCreateDate");
		
		voucherName = in.getStringExtra("voucher_name");
		VoucherId = in.getStringExtra("voucherId");
		Log.i("tag", "Voucher ID: "+VoucherId);
		
		if(VoucherId!=null)
		{
			ApplyVocher="yes";
		}
		
		givenAmount = in.getStringExtra("given");
	    mDiscount= in.getStringExtra("MDiscount");
		totalDiscounts=in.getStringExtra("totalDiscount");
		finalAmount=in.getStringExtra("finaltotal");
		changeValue = in.getStringExtra("changeValue");
		amountPaid = in.getStringExtra("amountPaid");
		
		totalTax=in.getStringExtra("totalTax");
		returnAmount=in.getStringExtra("returnTotal");
		
	    remTax= in.getStringExtra("rem_totalTax");
		remDiscount=in.getStringExtra("rem_Discount");
		remMDiscount=in.getStringExtra("rem_MDiscount");
		remTotal=in.getStringExtra("rem_total");
	
		
		Log.i("tag", "Rec mDiscount:"+mDiscount);
		Log.i("tag", "Rec totalDiscounts:"+totalDiscounts);
		Log.i("tag", "Rec finalAmount:"+finalAmount);
		Log.i("tag", "Rec totalTax:"+totalTax);
		
	    /*PoSDatabase db= new PoSDatabase(mcontext);
		for(int i=0;i<productList.size();i++)
		{
			ProductReal product = productList.get(i);
			db.updateProductQunatity(product.getPid(),( product.getTotalQuantity()-product.getSelectedQuatity()));
		
		}
		Log.i("tag","productList.size() :"+productList.size());*/
	

		new GenerateImage().execute("");
	}
	
	/***********Jaswinder***********/
	public void genrateImage()
	{
		String remainHtml;
		
//		//product list
		for(int i=0;i<productList.size();i++)
		{
			ProductReal product=productList.get(i);
			Log.i("tag", "Product name:"+product.getProductName());
			Log.i("tag", "Quantity:"+product.getSelectedQuatity());
			Log.i("tag", "Discount:"+product.getDiscount());
			Log.i("tag", "Price:"+product.getPrice());
			if(product.getSelectedQuatity()==-1)
			product.setSelectedQuatity(0);
			BigDecimal discountValue=product.getTotalPrice().subtract(product.getDiscountedValue());
			html+="<tr><td style=width:25%>"+product.getProductName()+"</td><td style=width:25%>"+String.valueOf(product.getSelectedQuatity())+"</td> <td style=width:25%>"+naira+""+discountValue.toPlainString()+"</td> <td style=width:25%>"+naira+""+product.getTotalPrice().toPlainString()+"</td><tr>";
//		    subTotalBD=subTotalBD.add(product.getTotalPrice());
		}
	
		if(cashType.equals("Voucher"))
		{
		voucherData="<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>Vouchar Name</td>"+
		            "<td>"+voucherName+"</td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>Voucher Number</td>"+
		            "<td>"+VoucherId+"</td>"+
		            "</tr>"
		            ;
		}		
//		checkReturnBy Cash
		if(isEditing){
		if(againReceipt==false){
			
		if(checkRetrunBy.equals("Cash") || checkRetrunBy.equals("Card") || checkRetrunBy.equals("Voucher"))
				{
		
			remainHtml= "";

			if(productList!=null){
			for(int i=0;i<productList.size();i++)
			{
				ProductReal product=productList.get(i);
				
				if(product.getSelectedQuatity()==-1)
					product.setSelectedQuatity(0);
				BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(product.getSelectedQuatity()));
				BigDecimal discountValue=totalPrice.subtract(product.getDiscountedValue());
				returnHtml+="<tr><td style=width:25%>"+product.getProductName()+"</td><td style=width:25%>"+String.valueOf(product.getSelectedQuatity())+"</td> <td style=width:25%>"+naira+""+discountValue.toPlainString()+"</td> <td style=width:25%>"+naira+""+totalPrice.toPlainString()+"</td><tr>";
				
				if(product.getInitialQuantity()!= product.getSelectedQuatity()){
					int remQuantity = product.getInitialQuantity()-product.getSelectedQuatity();
					
					BigDecimal remPrice = product.getPrice().multiply(BigDecimal.valueOf(remQuantity));
					BigDecimal remDiscountValue=product.getInitialDiscount().add(product.getInitialMDiscount()).subtract(discountValue);
					remainHtml+="<tr><td style=width:25%>"+product.getProductName()+"</td><td style=width:25%>"+String.valueOf(remQuantity)+"</td> <td style=width:25%>"+naira+""+remDiscountValue.toPlainString()+"</td> <td style=width:25%>"+naira+""+remPrice.toPlainString()+"</td><tr>";
					
				}
			}
			}
			
			/**jaswinder**/
			if(!remainHtml.equals("")){
				    cashReturnHtml="<tr>"+		
		            "<tr>"+	
					//after returning pay by cash show remaining and returning items
		            "<th colspan=2>Returned Items </th>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            //remaining List
		            "<tr>"+returnHtml+"</tr>"+
		            "<tr>"+		
		            "<td colspan=4></td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<th colspan=2>Remaining Items </th>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            //returnList
		            "<tr>"+remainHtml+"</tr>";
				    returningExeComplete="yes";
			}else{
				cashReturnHtml="<tr>"+		
			            "<tr>"+	
						//after returning pay by cash show remaining and returning items
			            "<th colspan=2>Returned Items </th>"+
			            "</tr>"+
			            "<tr>"+		
			            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
			            "</tr>"+
			            //remaining List
			            "<tr>"+returnHtml+"</tr>"+
			            "<tr>"+		
			            "<td colspan=4></td>"+
			            "</tr>";
				returningExeComplete="yes";
			}
					html="";
					//show return amount
					retrunAmounts="<tr>"+
							"<td></td>"+
							"<td></td>"+
							"<td>Return Amount</td>"+
							"<td>"+naira+returnAmount+"</td>"+
							"</tr>"+
							"<tr>"+	
							"<td></td>"+
							"<td></td>"+
							"<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
							"</tr>";
					//for voucher receipt
					if(voucherReceipt==true)
					{
					if(checkreturnVoucherData==true){
						cashType="Vocuher";
						folderPath="POS-Voucher"+"/"+formattedDate;
						returnVoucherData="<table style=font-size:55px; width:100%>"+
								"<tr>"+		
								"<td colspan=4 valign=middle align=center><img width=400 height=150 src=\"data:image/jpeg;base64," + imageBase64 + "\"></td>"+
								"</tr>"+
								"<tr>"+		
					            "<th colspan=4>Voucher</th>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center colspan=4>"+headerLine1+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center>"+headerLine2+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center>"+headerLine3+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center>"+headerLine4+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center>"+headerLine5+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4>"+itemText+"</td>"+
					            "</tr>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td  colspan=2 >Voucher Creation Date :</td><td>"+retVocuherCreationDate+"</td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td colspan=2>Voucher Valid Date :</td><td> "+retVoucherValidDate+"</td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td colspan=2>Transaction id :</td><td> "+TransactionId+"</td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td colspan=2>Voucher Name :</td><td>"+retVocuherType+"</td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td colspan=2>Voucher Number :</td><td> "+retVoucherID+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4><div style=height:1px><hr style=border:none;border-top:dashed thin black></div><div><hr style=border:none;border-top:dashed thin black></div></td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td style=width:50%>Total : </td><td colspan=1>"+naira+retvoucherAmount+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4></td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4></td>"+
					            "</tr>"+
					            "<tr>"+
					            "<td colspan=4 align=center colspan=4>"+footerLine1+"</td>"+
								"</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center colspan=4>"+footerLine2+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center colspan=4>"+footerLine3+"</td>"+
					            "</tr>"+
					            "<tr>"+		
					            "<td colspan=4 align=center colspan=4>"+footerLine4+"</td>"+
					            "</tr>"+
					            "</table>"
					            ;
					
					}
					voucherReceipt=false;
					checkRetrunBy="";
					
					}
		}
		else{
			cashReturnHtml="";
		}
		}
		else
		{
//			the shows only remaining item,the no return item 
			//remaininglist
			/**newly*/
			payBy=cashType;
			folderPath="POS-Receipts"+"/"+formattedDate;
			totalTax=remTax;
		    totalDiscounts=remDiscount;
		    mDiscount=remMDiscount;
		    finalAmount=remTotal;
		    cashReturnHtml="";
			
			html="";
			if(remainingList!=null){
				
			for(int i=0;i<remainingList.size();i++)
				{
					ProductReal product=remainingList.get(i);
					Log.i("tag", "Remain Product name:"+product.getProductName());
					Log.i("tag", "Remain Quantity:"+product.getSelectedQuatity());
					Log.i("tag", "Remain Discount:"+product.getDiscount());
					Log.i("tag", "Remain Price:"+product.getPrice());
					BigDecimal discountValue=product.getTotalPrice().subtract(product.getDiscountedValue());
					remainsHtml+="<tr><td style=width:25%>"+product.getProductName()+"</td><td style=width:25%>"+String.valueOf(product.getSelectedQuatity())+"</td> <td style=width:25%>"+naira+""+discountValue.toPlainString()+"</td> <td style=width:25%>"+naira+""+product.getTotalPrice().toPlainString()+"</td><tr>";
					Log.i("tag", "remainsHtml:"+remainsHtml);
//					if(product.getSelectedQuatity()==-1)
//					product.setSelectedQuatity(0);
//					BigDecimal discountValue=product.getTotalPrice().subtract(product.getDiscountedValue());
//					
//				    subTotalBD=subTotalBD.add(product.getTotalPrice());
				    
				}
			}
			Log.i("tag", "In :remainsHtml:"+remainsHtml);
			
			        cashReturnHtml="<tr>"+		
		            "<td colspan=4>Remaining Items </td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4><hr style=border:none;border-top:dashed thin black /></td>"+
		            "</tr>"+
		            //pass here remaining list without return
		            "<tr>"+remainsHtml+"</tr>";
			        
			     
			        retrunAmounts="";
			        
			        againReceipt=false;
					newTransactionId=null;
					remainingExeComplete="yes";
		}
		}
//		 //paycash
		 htmlDocument = 
		            "<table style=font-size:55px; width:100%>" +
		            "<tr>"+		
				    "<td colspan=4 valign=middle align=center><img width=400 height=150  src=\"data:image/jpeg;base64," + imageBase64 + "\"></td>"+
				    "</tr>"+
		            "<tr>"+		
		            "<th colspan=4>Reciept</th>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center colspan=4>"+headerLine1+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center>"+headerLine2+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center>"+headerLine3+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center>"+headerLine4+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center>"+headerLine5+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4><hr style=border:none;border-top:dashed thin black/></td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4>"+itemText+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4>Transaction Id :"+TransactionId+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4>Date Time : "+currentDateandTime+" </td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4>Pos Id : 1</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<tr>"+		
		            "<td colspan=4>"+payBy+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td colspan=4></td>"+
		            "</tr>"+
		            "<tr><td style=width:25%>item</td> <td style=width:25%>qty</td> <td style=width:25%>Dis</td> <td style=width:25%>Price</td><tr>"+
		            "<tr>"+		
		            "<td colspan=4><div style=height:1px><hr style=border:none;border-top:dashed thin black></div><div><hr style=border:none;border-top:dashed thin black></div></td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td>"+cashReturnHtml+"</td>"+
		            "</tr>"+
		            "</tr>"+
		            "<tr>"+html+"</tr>"+
		             "<tr>"+	
		            "<td colspan=4><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>VAT</td>"+
		            "<td>"+naira+""+totalTax+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td></td>"+
		            "<td></td>"+
		            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>Discount</td>"+
		            "<td>"+naira+""+totalDiscounts+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td></td>"+
		            "<td></td>"+
		            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>MDiscount</td>"+
		            "<td>"+naira+""+mDiscount+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td></td>"+
		            "<td></td>"+
		            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>"+
		            "<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>TOTAL</td>"+
		            "<td>"+naira+""+finalAmount+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td></td>"+
		            "<td></td>"+
		            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>";
		 if(!isEditing){
			 htmlDocument = htmlDocument+"<tr>"+
		            "<td></td>"+
		            "<td></td>"+
		            "<td>Amount Paid</td>"+
		            "<td>"+naira+""+amountPaid+"</td>"+
		            "</tr>"+
		            "<tr>"+	
		            "<td></td>"+
		            "<td></td>"+
		            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
		            "</tr>";
		 
			 BigDecimal change = BigDecimal.valueOf(Double.valueOf(changeValue));
			 int changeInt = change.intValue();
			 
					 if(changeInt>0){
						 htmlDocument = htmlDocument+"<tr>"+
						            "<td></td>"+
						            "<td></td>"+
						            "<td>Change</td>"+
						            "<td>"+naira+""+changeValue+"</td>"+
						            "</tr>"+
						            "<tr>"+	
						            "<td></td>"+
						            "<td></td>"+
						            "<td colspan=2><hr style=border:none;border-top:dashed thin black></td>"+
						            "</tr>";
					 }
		 }      
		 
		 htmlDocument = htmlDocument+  "<td>"+retrunAmounts+"</td>"+
		            "</tr>"+
		            
		            "<tr>"+
					voucherData+""+
			        "<tr>"+	
		            "<td></td>"+
		            "</tr>"+
					"<tr>"+
		            "<td colspan=4 align=center colspan=4>"+footerLine1+"</td>"+
					"</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center colspan=4>"+footerLine2+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center colspan=4>"+footerLine3+"</td>"+
		            "</tr>"+
		            "<tr>"+		
		            "<td colspan=4 align=center colspan=4>"+footerLine4+"</td>"+
		            "</tr>"+
		            "</table>";
	 		
	 	
					if(checkreturnVoucherData==true && isEditing){
						
						ReceiptGenerate.this.runOnUiThread(new Runnable() {
						    public void run() {
						    	webView.loadDataWithBaseURL(null, returnVoucherData, 
							            "text/html", "UTF-8", null);
								checkreturnVoucherData=false;
						     }
						});
						
						
						
					}
					else
					{
						ReceiptGenerate.this.runOnUiThread(new Runnable() {
						    public void run() {
								webView.loadDataWithBaseURL(null, htmlDocument, 
							            "text/html", "UTF-8", null);
										returnVoucherData="";
										
						    }
						});
					}
		 		
	    }
	
	class GenerateImage extends AsyncTask<String, String, String>
	{

		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(ReceiptGenerate.this);
	    	
	    	dialog.setMessage("Creating receipt please wait ..");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
	    	// Initialize the WebView
	    				webView.getSettings().setSupportZoom(true);
	    				webView.getSettings().setBuiltInZoomControls(true);
	    				webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
	    				webView.setScrollbarFadingEnabled(true);
	    				webView.getSettings().setLoadsImagesAutomatically(true);
	    				webView.getSettings().setJavaScriptEnabled(true);
	    				webView.setWebViewClient(new WebViewClient() {

	            public boolean shouldOverrideUrlLoading(WebView view, 
	                           String url) 
	    	   {
	                 return false;     
	           }

	            @Override
	            public void onPageFinished(WebView view, String url) 
	    	   {
	            	
	           }
	           
	     });
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			genrateImage();
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		@SuppressWarnings("deprecation")
		@Override
	    protected void onPostExecute(String str) {
	    	
			dialog.dismiss();
	    	Picture picture = webView.capturePicture();
			Bitmap b = Bitmap.createBitmap(picture.getWidth(),picture.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			picture.draw(c);
			
			FileOutputStream fos = null;
			File sdCard = Environment.getExternalStorageDirectory();
	        File dir = new File (sdCard.getAbsolutePath() + "/"+folderPath+"/");
	        dir.mkdirs();
	        
	        File file = new File(dir, TransactionId+".jpg");
			try {
				fos = new FileOutputStream(file);
				if (fos != null) {
					b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
					fos.close();
					selectedImageUri=Uri.fromFile(file);
					selectedImagePath=getPath(ReceiptGenerate.this, selectedImageUri);
					Utility.pathList().add(selectedImagePath);
					
					  for (String Value : Utility.pathList()) {
						  Log.i("tag","Receieve Path:"+Value);
					  }
//					
					/**new Code**/
					
					Log.i("tag", "Image Path:"+selectedImagePath);
					Editor editor = imagePathPref.edit();
					
					editor.putString(Utility.RecieptPath,selectedImagePath);
					/*newly code*/
					if(checkRetrunBy.equals("")){
					editor.putString(Utility.CashType,"");
					editor.commit(); 
					}
					editor.putString(Utility.ItemReturnBy,"");
					editor.commit(); 
					
					if(checkRetrunBy.equals("Voucher"))
					{
						voucherReceipt=true;
						checkreturnVoucherData=true;
						new GenerateImage().execute("");
					}
					
					else if(newTransactionId!=null)
					{
						
//						folderPath="Receipt";
						againReceipt=true;
						TransactionId=saveOldTransactionID;
						new GenerateImage().execute("");
					}

					else
					{
						final Dialog alert = new Dialog(mcontext);
						alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
						alert.setContentView(R.layout.list_dialog_layout);
						
						final ListView listView = (ListView)alert.findViewById(R.id.voucherList);
						Button btnOk = (Button)alert.findViewById(R.id.btnOk);
						Button btnCancel = (Button)alert.findViewById(R.id.btnCancel);
						TextView txtHeader = (TextView)alert.findViewById(R.id.txtHeader);
						
						txtHeader.setText("Transaction complete");
					
						ArrayList<String> list = new ArrayList<String>();
						list.add("Email Receipt");
						list.add("Print Receipt");
						
						listView.setAdapter(new ArrayAdapter<String>(mcontext, R.layout.listitem_layout, list));
						
						btnOk.setVisibility(View.GONE);
						btnCancel.setVisibility(View.GONE);
						
						listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1,
									int arg2, long arg3) {
								if(arg2 == 0){
									selectedOption = "email";
									    Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
									    emailIntent.setType("plain/text");
									    ArrayList<Uri> uris = new ArrayList<Uri>();
									    for (String file :  Utility.pathList())
									    {
									    	Log.i("tag","Sending Image on Email::"+file);
									    	File fileIn = new File(file);
									        Uri u = Uri.fromFile(fileIn);
									        uris.add(u);
									    }
									    	emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
									    	mcontext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//									    	finish();
										}else{
											selectedOption = "printer";
											Intent goForPrint=new Intent(ReceiptGenerate.this,PrinterTypeActivity.class);
											startActivity(goForPrint);
//											finish();
								}
							}
						});
						alert.setCancelable(false);
						alert.show();
					}
				}
			} catch (Exception e) {
				Log.e("exception","eeeeeeeeeeeeee");
				e.printStackTrace();
			}
//	    	doPrint();
	    	}
	}
	
public static String getPath(final Context context, final Uri uri) {

	    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

	    // DocumentProvider
	    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
	        // ExternalStorageProvider
	        if (isExternalStorageDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            if ("primary".equalsIgnoreCase(type)) {
	                return Environment.getExternalStorageDirectory() + "/" + split[1];
	            }

	            // TODO handle non-primary volumes
	        }
	        // DownloadsProvider
	        else if (isDownloadsDocument(uri)) {

	            final String id = DocumentsContract.getDocumentId(uri);
	            final Uri contentUri = ContentUris.withAppendedId(
	                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

	            return getDataColumn(context, contentUri, null, null);
	        }
	        // MediaProvider
	        else if (isMediaDocument(uri)) {
	            final String docId = DocumentsContract.getDocumentId(uri);
	            final String[] split = docId.split(":");
	            final String type = split[0];

	            Uri contentUri = null;
	            if ("image".equals(type)) {
	                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
	            } else if ("video".equals(type)) {
	                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	            } else if ("audio".equals(type)) {
	                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
	            }

	            final String selection = "_id=?";
	            final String[] selectionArgs = new String[] {
	                    split[1]
	            };

	            return getDataColumn(context, contentUri, selection, selectionArgs);
	        }
	    }
	    // MediaStore (and general)
	    else if ("content".equalsIgnoreCase(uri.getScheme())) {

	        // Return the remote address
	        if (isGooglePhotosUri(uri))
	            return uri.getLastPathSegment();

	        return getDataColumn(context, uri, null, null);
	    }
	    // File
	    else if ("file".equalsIgnoreCase(uri.getScheme())) {
	        return uri.getPath();
	    }

	    return null;
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri, String selection,
	        String[] selectionArgs) {

	    Cursor cursor = null;
	    final String column = "_data";
	    final String[] projection = {
	            column
	    };

	    try {
	        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,null);
	        if (cursor != null && cursor.moveToFirst()) {
	            final int index = cursor.getColumnIndexOrThrow(column);
	            return cursor.getString(index);
	        }
	    } finally {
	        if (cursor != null)
	            cursor.close();
	    }
	    return null;
	}


	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
	    return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
	    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
	    return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
	    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}
	/*************************/

	
		@SuppressWarnings("deprecation")
		public void showDialog(String str)
		 {
			 
			 final AlertDialog alert=new AlertDialog.Builder(mcontext).create();
			 
			 alert.setMessage(str);
			 
			
			 alert.setButton2("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alert.dismiss();
				}
			});
			 alert.setButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						alert.dismiss();
						
						Intent i = new Intent(android.content.Intent.ACTION_VIEW);
						i.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.infraware.office.link"));
						startActivity(i);
						
						
					}
				});
			 
			 alert.show();
			 }
		public void onBackPressed()
		{
			Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
	    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    	startActivity(intent);
//	    	finish();
		}
		
		@Override
		public void onResume(){
			super.onResume();
			if(selectedOption.equals("email")){
				Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
		    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(intent);
		    	finish();
			}
			Log.e("resumed","selected option "+selectedOption);
		}
}
