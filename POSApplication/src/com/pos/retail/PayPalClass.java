package com.pos.retail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.posapplication.R;
import com.pos.util.PoSDatabase;
import com.pos.util.ProductReal;



public class PayPalClass  extends Activity {
	private final static String		TAG						= "PPHere";

	private EditText				amountText;
//	private EditText				nameText;
	private Button					currencyButton;
	private final int				CURRENCY_DIALOG			= 1;
	private final int				INVALID_AMOUNT_DIALOG	= 2;
	private final CharSequence[]	CURRENCIES				= {
			"NGN","EUR",  "USD"};

	 private String filename = "Logger.text";
	 private String filepath = "POS",naira;
	
	 static ArrayList<ProductReal> productList;
	 static String TotalTax="",TotalDiscount="",TotalAmount="",paymentMethod="",voucherName="",voucherId="";
	 static String strDiscount="",TransactionType="", finalAmount="", subTotalAmount="", pending_total="", pending_vat="", pending_discount="", pending_MDiscount="";
	 static BigDecimal payableMoney;
	 
	 Intent in;
	 File pdfFile;
	 static int paymenttype;
	
	 private Button btnBack;
	 
	 CustomAdapter adapter;
	 ListView listview;
	 Context mcontext;
	 ArrayList<com.pos.util.CurrencySetting> currencysettingList;
	 BigDecimal convertedPrice;
	 int trigger;
	 Button payWithPaypal, payWithEFT, payWithLocal;
	 
	 
//	 BigDecimal bdtotalAmount;
	/**
	 * Called when the activity is first created.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.home);
		amountText = (EditText) findViewById(R.id.amountText);
//		nameText = (EditText) findViewById(R.id.nameText);
		currencyButton = (Button) findViewById(R.id.currencyButton);
		
		payWithPaypal = (Button)findViewById(R.id.continueButton);
		payWithEFT = (Button)findViewById(R.id.continueEFTButton);
		payWithLocal = (Button)findViewById(R.id.continueLocalButton);
		
	 btnBack = (Button)findViewById(R.id.back);
	 
	 btnBack.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			finish();
		}
	});
		mcontext = this;
		naira = getResources().getString(R.string.naira);
		listview = (ListView) findViewById(R.id.listview);
		
		in = getIntent();
		
		try{
		
		productList = (ArrayList<ProductReal>)in.getSerializableExtra("itemSelected");
		
		Log.e("", ""+productList);
		
		TotalTax = in.getStringExtra("TotalTax");
		TotalDiscount = in.getStringExtra("TotalDiscount");
		TotalAmount = in.getStringExtra("TotalAmount");
		paymentMethod = in.getStringExtra("paymentMethod");
		voucherName = in.getStringExtra("voucherName");
		voucherId = in.getStringExtra("voucherId");
		paymenttype = in.getIntExtra("paymenttype",0);
		strDiscount = in.getStringExtra("strDiscount");
		TransactionType = in.getStringExtra("TransactionType");
		finalAmount = in.getStringExtra("finalTotal");
		subTotalAmount = in.getStringExtra("TotalAmount");
		String payableAmt = in.getStringExtra("paycash");
		payableMoney = BigDecimal.valueOf(Double.parseDouble(payableAmt));
		trigger = in.getIntExtra("trigger", -1);
		
		amountText.setText(finalAmount);
		
		if(trigger!=-1){
			payWithPaypal.setText("Return payment with PayPal Here");
			payWithEFT.setText("Return payment with EFT");
			payWithLocal.setText("Return payment with Local Payment Gateway");
		}
		
		pending_total = in.getStringExtra("pending_finalTotal");
		pending_vat = in.getStringExtra("pending_totalTax");
		pending_discount = in.getStringExtra("pending_totalDiscount");
		pending_MDiscount = in.getStringExtra("pending_manualdiscount");

		adapter =new CustomAdapter(this, android.R.layout.simple_list_item_1, productList);
		listview.setAdapter(adapter);
		
	
		currencyButton.setText(CURRENCIES[2]);
		PoSDatabase db = new PoSDatabase(mcontext);
		currencysettingList = db.getAllCurrencySetting();
		
		convertedPrice = BigDecimal.valueOf(0);
		for(int i = 0; i<productList.size(); i++){
			BigDecimal tempPrice = getUSDvalue(productList.get(i).getDiscountedValue().toString());
			productList.get(i).setConvertedPrice(tempPrice);
			
			convertedPrice = convertedPrice.add(tempPrice);
		}
		convertedPrice = convertedPrice.setScale(2);
		amountText.setText(convertedPrice.toString());
		}catch(Exception e){
			Log.e("error.....","error...");
			e.printStackTrace();
		}
	}

	public BigDecimal getUSDvalue(String amount){
		BigDecimal tempbd = new BigDecimal(amount);
		String  saleValue1;
		
		System.out.println("value "+currencysettingList.get(1).getCurrSaleValue());
		if(TransactionType.equals(""))
			 saleValue1 =currencysettingList.get(1).getCurrSaleValue();
		else
			saleValue1 =currencysettingList.get(1).getCurrReturnValue();
		
		BigDecimal bd = BigDecimal.valueOf(Double.parseDouble(saleValue1));
			
		tempbd = tempbd.divide(bd ,2,RoundingMode.HALF_UP);
		
		return tempbd;
	}
	@SuppressWarnings("deprecation")
	public void changeCurrency(View view) {
		showDialog(CURRENCY_DIALOG);
	}

	@SuppressWarnings("deprecation")
	public void continuePayment(View view) {
		

		
		if(view.getId() == R.id.continueButton){
		
		String url = "paypalhere://takePayment/?returnUrl={{returnUrl}}&invoice=%7B%22merchantEmail%22%3A%22%22,%22payerEmail%22%3A%22spotireddy-biz%40paypal.com%22,%22itemList%22%3A%7B%22item%22%3A%5B";

		for(int i = 0; i<productList.size(); i++){
			String tempStr ="{"+
	                "\"taxRate\": \"{{TaxRate}}\","+
	                "\"name\": \"{{Name}}\","+
	                "\"description\": \"\","+
	                "\"unitPrice\": \"{{UnitPrice}}\","+
	                "\"taxName\": \"Tax\","+
	                "\"quantity\": \"1\""+
	            "}";
			
			tempStr = tempStr.replace("{{TaxRate}}", productList.get(i).getSingleItemTax().toString());
			tempStr = tempStr.replace("{{Name}}", productList.get(i).getProductName());
			tempStr = tempStr.replace("{{UnitPrice}}", productList.get(i).getConvertedPrice().toString());
//			tempStr = tempStr.replace("{{Quantity}}", String.valueOf(productList.get(i).getSelectedQuatity()));
			
			url = url+tempStr;
			if(i != productList.size()-1)
				url = url+",";
		}
		
		url = url+"%5D%7D,%22currencyCode%22%3A%22{{currency}}%22,%22paymentTerms%22%3A%22DueOnReceipt%22,%22discountPercent%22%3A%220.0%22%7D";
			
		String returnUrl = "hailocabtest://paymentResult/?{result}?Type={Type}&InvoiceId={InvoiceId}&Tip={Tip}&Email={Email}&TxId={TxId}";
	    String priceString = amountText.getText().toString();
//		String nameString = nameText.getText().toString();
		String currency = currencyButton.getText().toString();

		Log.e("currency::", currency);
		
		try {
			Float.parseFloat(priceString);
		} catch (Exception e) {
			showDialog(INVALID_AMOUNT_DIALOG);
			return;
		}

		returnUrl = URLEncoder.encode(returnUrl);

		url = url.replace("{{returnUrl}}", returnUrl);
//		url = url.replace("{{price}}", priceString);
//		url = url.replace("{{name}}", "POS");
//		url = url.replace("{{description}}", "POS");
		url = url.replace("{{currency}}", currency);

		Log.v(TAG, "URL: " + url);
		Log.e("Url",""+ Uri.parse(url));
		
			try
			{
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				/*final String urlStr = url;
				AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);
				alert.setMessage("Url : "+urlStr);
				alert.setTitle("Url for paypal");
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Intent i = new Intent(Intent.ACTION_VIEW);
						i.setData(Uri.parse(urlStr));
						startActivity(i);
					}
				});
				alert.show();*/
			}
			catch(Exception e)
			{
				File myFile = new File("/sdcard/mysdfile.txt");
				try
				{
					FileOutputStream fOut = new FileOutputStream(myFile, true);
					OutputStreamWriter myOutWriter = 
											new OutputStreamWriter(fOut);
					myOutWriter.append(File.separator);
					myOutWriter.append("//////////////// New Data ///////////"+File.separator+File.separator);
					
					String stackTrace = Log.getStackTraceString(e);
					myOutWriter.append(stackTrace);
					myOutWriter.close();
					fOut.close();
				}
				catch(Exception e1)
				{
					e1.printStackTrace();  
				}
			}
		}else{
			AlertDialog.Builder alert = new AlertDialog.Builder(PayPalClass.this);
			alert.setMessage("Work on this module is still under developement.");
			alert.setTitle("POS");
			alert.setPositiveButton("Ok", null);
			alert.show();
		}
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		AlertDialog.Builder builder = null;
		switch (id) {
			case CURRENCY_DIALOG:
				builder = new AlertDialog.Builder(this);
				builder.setTitle(R.string.label_select_currency);
				builder.setSingleChoiceItems(CURRENCIES, 2,
						new DialogInterface.OnClickListener() {
							@SuppressWarnings("deprecation")
							public void onClick(DialogInterface dialog, int item) {
								currencyButton.setText(CURRENCIES[item]);
								
								
								System.out.println("Item "+item);
								PoSDatabase db = new PoSDatabase(mcontext);
								
								ArrayList<com.pos.util.CurrencySetting> list =db.getAllCurrencySetting();
								switch (item) 
								{
								case 0:
									
									BigDecimal tempbd=new BigDecimal(finalAmount);
									amountText .setText(tempbd.toString());
									
									break;
								case 1:
								
									 tempbd=new BigDecimal(finalAmount);
									 System.out.println("value "+list.get(0).getCurrSaleValue());
									String  saleValue;
									if(TransactionType.equals(""))
										 saleValue =list.get(0).getCurrSaleValue();
									else
										saleValue =list.get(0).getCurrReturnValue();
									BigDecimal bd = new BigDecimal(saleValue);
									
									
									
									tempbd = tempbd.divide(bd ,2,RoundingMode.HALF_UP);
									
									System.out.println(tempbd);
									amountText.setText(tempbd.toString());
									
									break;
								case 2:
									
									tempbd = new BigDecimal(finalAmount);
									
									String  saleValue1;
									
									System.out.println("value "+list.get(1).getCurrSaleValue());
									if(TransactionType.equals(""))
										 saleValue1 =list.get(1).getCurrSaleValue();
									else
										saleValue1 =list.get(1).getCurrReturnValue();
									
									  bd = BigDecimal.valueOf(Double.parseDouble(saleValue1));
										
		
									  tempbd = tempbd.divide(bd ,2,RoundingMode.HALF_UP);
										
									  System.out.println(tempbd);
										amountText.setText(tempbd.toString());
									  
									  
									break;
								}
								
								
								dismissDialog(CURRENCY_DIALOG);
							}
						});
				dialog = builder.create();
				break;
			case INVALID_AMOUNT_DIALOG:
				builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.invalid_amount_message)
						.setCancelable(false)
						.setPositiveButton(R.string.label_ok,
								new DialogInterface.OnClickListener() {
									@SuppressWarnings("deprecation")
									public void onClick(DialogInterface dialog,
											int id) {
										dismissDialog(INVALID_AMOUNT_DIALOG);
									}
								});
				dialog = builder.create();
				break;
		}

		return dialog;
	}
	class CustomAdapter extends ArrayAdapter<ProductReal>
	{
		Context mcontext;
		ArrayList<ProductReal> listOfProduct;
		public CustomAdapter(Context context, int resource,
				ArrayList<ProductReal> listofProduct) {
			super(context, resource, listofProduct);
			
			mcontext = context;
			listOfProduct = listofProduct;
		}
		public View getView(int position,View convertView,ViewGroup parent)
		{
			
			if(convertView == null)
			{
				convertView = ((Activity)mcontext).getLayoutInflater().inflate(R.layout.itemlayout, null);
			}
			
			TextView pid = (TextView)convertView.findViewById(R.id.pid);
			TextView itemId = (TextView)convertView.findViewById(R.id.pItemId);
			TextView pname = (TextView)convertView.findViewById(R.id.pname);
			TextView pprice=(TextView)convertView.findViewById(R.id.pprice);
			TextView tax=(TextView)convertView.findViewById(R.id.tax);
			
			TextView txtDelete = (TextView)convertView.findViewById(R.id.delete);
			TextView txtEdit = (TextView) convertView.findViewById(R.id.edit);
			TextView txt = (TextView) convertView.findViewById(R.id.labelQuntity);
			txtDelete.setVisibility(View.GONE);
			txtEdit.setVisibility(View.GONE);
			
			TextView discount=(TextView)convertView.findViewById(R.id.discount);
			TextView qunatity=(TextView)convertView.findViewById(R.id.quantity);
			
			ProductReal product=productList.get(position);
			
			pid.setText(""+product.getPid());
			pname.setText(""+product.getProductName());
			pprice.setText(naira +" "+product.getPrice().toString());
			tax.setText(naira +" "+ product.getTax().toString());
			
			itemId.setText(""+product.getItemId());
			
			discount.setText(naira +" "+product.getDiscount().toString());
			qunatity.setText(""+product.getSelectedQuatity());
			
			txt.setText("item");
			
			return convertView;
			
			
		}
	}
}
