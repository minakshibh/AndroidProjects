package com.pos.retail;



import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.pos.util.ConnectionDetector;
import com.pos.util.Currency;
import com.pos.util.CurrencyType;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.SaveReturnItemOnServer;
import com.pos.util.ScreenReceiver;
import com.pos.util.TransactionSaveOnWeb;
/**
 * 
 * @author Kis_8
 * This class is meant for Pay via Cash functionality and would allow cashier to accept payments via Cash
 */
public class PayCashActivity extends Activity{
	
	private BigDecimal totalValue, givenValue, changeValue, currencyCounter, quantity;
	private TextView txtTotal;
	private EditText txtGiven, txtChange;
	private Button btnBack,btnDone,btnCancel;
	private ArrayList<Currency> currencyListNaira;
	
	private ArrayList<Currency> currencyListEuro;
	private ArrayList<Currency> currencyListDollar;
	
	private LinearLayout denominationLayout, currencyLayout,linearLayout;
	private CheckBox chkDenomination;	
	Context mcontext;
	String producturl;
	 private SoapObject request;
	SharedPreferences loginPref;
	private final String SOAP_ACTION = "http://tempuri.org/TestingProduct";
	private final String SOAP_NAMESPACE = "http://tempuri.org/";
	private final String SOAP_METHOD_NAME = "TestingProduct";
	int employee_id;
	private PropertyInfo pi2;
	String payableMoney,totalTax,totalDiscount;
	ArrayList<ProductReal> productList, remProductList;
	String SOAP_URL;
	int reasonId;
	String returnTransactionId="";
//	List<TestProduct> list;
	ConnectionDetector detector;
	String paymentMethod,voucherId =null,VoucherName =null;
	Spinner spinner;
	String naira;
	TextView origionalValue;
	String remTax="0", remTotal="0", remDiscount="0", remMDiscount="0";
	String pending_total, pending_vat, pending_discount, pending_MDiscount;
	String ret_subTotal, ret_total, ret_Tax, ret_disc, ret_MDisc;
	
	int currencyId =3;;
	String mDiscountAmount, finalTotal, totalAmount, itemReturnCode, newTransactionId;
	int discountType =0 ;
	 long curentTime=0;
	  long elapTime = 0;
	@SuppressWarnings("unchecked")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paycashactivity);
		
		SharedPreferences itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		
		itemReturnCode = itempref.getString("ItemReturnCode", "");
		
		currencyListNaira = new ArrayList<Currency>();
		
		currencyListDollar =new ArrayList<Currency>();
		
		currencyListEuro =new ArrayList<Currency>();
		mcontext = this;
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		SOAP_URL = getResources().getString(R.string.liveurl);
		// Initializing UI components
		txtTotal = (TextView)findViewById(R.id.txtTotal);
		origionalValue = (TextView) findViewById(R.id.origionalValue);
		
		naira = getResources().getString(R.string.naira);
		txtGiven = (EditText)findViewById(R.id.txtGiven);
		txtChange = (EditText)findViewById(R.id.txtChange);
		btnBack = (Button)findViewById(R.id.back);
		denominationLayout = (LinearLayout)findViewById(R.id.denominationLayout);
		
		spinner = (Spinner) findViewById(R.id.spinner);
		
		spinner.setOnItemSelectedListener(currSelector);
		
		chkDenomination = (CheckBox)findViewById(R.id.chkDenomination);
		
		detector = new ConnectionDetector(mcontext);
		
		btnDone = (Button)findViewById(R.id.btnDone);
		btnCancel = (Button)findViewById(R.id.btnCancel);
		
		btnDone.setOnClickListener(Listener);
		btnCancel.setOnClickListener(Listener);
		
		chkDenomination.setVisibility(View.GONE);
		
		producturl=getResources().getString(R.string.liveurl)+"/"+SOAP_METHOD_NAME;
		
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		
		employee_id = loginPref.getInt("UserId", 0);
		Intent in = getIntent();
		payableMoney = in.getStringExtra("paycash");
		finalTotal = in.getStringExtra("finalTotal");
		totalAmount = in.getStringExtra("TotalAmount");
		mDiscountAmount = in.getStringExtra("manualdiscount");
		discountType = in.getIntExtra("discounttype", 0);
		
		remProductList = (ArrayList<ProductReal>)in.getSerializableExtra("rem_listofproduct");
		
		if(remProductList!=null){
			remTotal = in.getStringExtra("rem_finalTotal");
			remTax = in.getStringExtra("rem_totalTax");
			remDiscount = in.getStringExtra("rem_totalDiscount");
			remMDiscount = in.getStringExtra("rem_totalMDiscount");
		}
		
		paymentMethod = in.getStringExtra("PaymentMethodType");
		in.getStringExtra("");
		
		if(in.hasExtra("returnTransactionId"))
		{
			returnTransactionId = in.getStringExtra("returnTransactionId");
			reasonId = in.getIntExtra("reasonId", 1);
			newTransactionId = in.getStringExtra("newTransactionId");
			
			pending_total = in.getStringExtra("pending_finalTotal");
			pending_vat = in.getStringExtra("pending_totalTax");
			pending_discount = in.getStringExtra("pending_totalDiscount");
			pending_MDiscount = in.getStringExtra("pending_manualdiscount");
			
			ret_subTotal = getIntent().getStringExtra("ret_subTotal");
			ret_total = getIntent().getStringExtra("ret_total");
			ret_Tax = getIntent().getStringExtra("ret_Tax");
			ret_disc = getIntent().getStringExtra("ret_disc");
			ret_MDisc = getIntent().getStringExtra("ret_MDisc");
		}
		if(in.hasExtra("VoucherId"))
			voucherId = in.getStringExtra("VoucherId");
		
		if(in.hasExtra("VoucherName"))
			VoucherName = in.getStringExtra("VoucherName");
		productList = (ArrayList<ProductReal>)in.getSerializableExtra("listofproduct");
		
		totalTax = in.getStringExtra("totalTax");
		totalDiscount = in.getStringExtra("totalDiscount");
		
		origionalValue.setText("Original Value "+naira +" "+payableMoney);
		
		// set Total Bill amount
		txtTotal.setText(payableMoney);
		givenValue = BigDecimal.valueOf(0);
		totalValue = new BigDecimal(payableMoney);
		currencyCounter = new BigDecimal(0);
		
		// back button listener
		btnBack.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				finish();	
			}
		});
		
		//TextChange listener for Given Amount TextBox so as to Calculate Change Amount on text change
		txtGiven.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				if(!txtGiven.getText().toString().trim().equals("")){
					givenValue = BigDecimal.valueOf(Double.valueOf(txtGiven.getText().toString()));
					changeValue = givenValue.subtract(totalValue);
					
					PoSDatabase db = new PoSDatabase(mcontext);
					
					ArrayList<com.pos.util.CurrencySetting> list =db.getAllCurrencySetting();
					if(currencyId == 4)
					{
						
						
						if(returnTransactionId.equals(""))
						{
							String  saleValue =list.get(0).getCurrSaleValue();
							
							BigDecimal bd = new BigDecimal(saleValue);
							
							System.out.println(totalValue +" "+bd);
							
							Log.e("jfkfkkf", totalValue +" "+bd);
							changeValue = changeValue.multiply(bd);
						}
						else
						{
							
							String  returnValue =list.get(0).getCurrReturnValue();
							
							BigDecimal bd = new BigDecimal(returnValue);
							
							System.out.println(totalValue +" "+bd);
							
							Log.e("jfkfkkf", totalValue +" "+bd);
							changeValue = changeValue.multiply(bd);
							
						}
						
						
					}
					else if(currencyId ==5 )
					{
						
						if(returnTransactionId.equals(""))
						{
						String  saleValue =list.get(1).getCurrSaleValue();
						
						BigDecimal bd = new BigDecimal(saleValue);
						
						System.out.println(totalValue +" "+bd);
						
						Log.e("jfkfkkf", totalValue +" "+bd);
						changeValue = changeValue.multiply(bd);
						}
						else
						{
							String  returnValue =list.get(1).getCurrReturnValue();
							
							BigDecimal bd = new BigDecimal(returnValue);
							
							System.out.println(totalValue +" "+bd);
							
							Log.e("jfkfkkf", totalValue +" "+bd);
							changeValue = changeValue.multiply(bd);
							
							
						}
						
					}
					else
					{
						BigDecimal bd = BigDecimal.valueOf(1);
						
						System.out.println(totalValue +" "+bd);
						
						Log.e("jfkfkkf", totalValue +" "+bd);
						changeValue = changeValue.multiply(bd);
					}
					Log.e("kksk", changeValue.toString());
					txtChange.setText(String.valueOf(changeValue));
				}else{
					givenValue = BigDecimal.valueOf(0);
					txtChange.setText("");
				}
			}
		});
		
		
		
		// Add different currencies to currency list
		currencyListNaira.add(new Currency(0.5d, R.drawable.halfrs, CurrencyType.Coin));
		currencyListNaira.add(new Currency(1, R.drawable.oners, CurrencyType.Coin));
		currencyListNaira.add(new Currency(2, R.drawable.twors, CurrencyType.Coin));
		currencyListNaira.add(new Currency(5, R.drawable.fivers, CurrencyType.Notes));
		currencyListNaira.add(new Currency(10, R.drawable.tenrs, CurrencyType.Notes));
		currencyListNaira.add(new Currency(50, R.drawable.fiftyrs, CurrencyType.Notes));
		currencyListNaira.add(new Currency(100, R.drawable.hundredrs, CurrencyType.Notes));
		currencyListNaira.add(new Currency(200, R.drawable.twohundredrs, CurrencyType.Notes));
		currencyListNaira.add(new Currency(500, R.drawable.fivehundredrs, CurrencyType.Notes));
		currencyListNaira.add(new Currency(1000, R.drawable.thousandrs, CurrencyType.Notes));
		
		
		
		currencyListDollar.add(new Currency(0.01d, R.drawable.one_cent, CurrencyType.Coin));
		currencyListDollar.add(new Currency(0.5d, R.drawable.five_cent, CurrencyType.Coin));
		currencyListDollar.add(new Currency(0.10d, R.drawable.ten_cent, CurrencyType.Coin));
		currencyListDollar.add(new Currency(0.25d, R.drawable.twentyfive_cent, CurrencyType.Coin));
		currencyListDollar.add(new Currency(0.50d, R.drawable.fifty_cent, CurrencyType.Coin));
		currencyListDollar.add(new Currency(5, R.drawable.five_dollar, CurrencyType.Notes));
		currencyListDollar.add(new Currency(10, R.drawable.ten_dollar, CurrencyType.Notes));
		currencyListDollar.add(new Currency(20, R.drawable.twenty_dollar, CurrencyType.Notes));
		currencyListDollar.add(new Currency(50, R.drawable.fifty_dollar, CurrencyType.Notes));
		currencyListDollar.add(new Currency(100, R.drawable.hundred_dollar, CurrencyType.Notes));
		
		
		
		currencyListEuro.add(new Currency(0.01d, R.drawable.one_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(0.02d, R.drawable.two_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(0.05d, R.drawable.five_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(0.10d, R.drawable.ten_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(0.20d, R.drawable.twenty_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(0.50d, R.drawable.fifty_cent_euro, CurrencyType.Coin));
		currencyListEuro.add(new Currency(1, R.drawable.one_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(2, R.drawable.two_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(5, R.drawable.five_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(10, R.drawable.ten_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(20, R.drawable.twenty_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(50, R.drawable.fifty_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(100, R.drawable.hundred_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(200, R.drawable.twohundred_euro, CurrencyType.Notes));
		currencyListEuro.add(new Currency(500, R.drawable.fivehundred_euro, CurrencyType.Notes));
		
		
		addCurrencyLayout(3, currencyListNaira);
		
		//set check change listener on checkBox
		chkDenomination.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				 if(isChecked){
					 txtGiven.setText(String.valueOf(currencyCounter));
				 }else{
					 txtGiven.setText("0");
				 }
			}
		});
	}
	
	// Dynamically add currency list
	public void addCurrencyLayout( int j,ArrayList<Currency> currList){
		denominationLayout.removeAllViews();
		LinearLayout layout = null ;
		LayoutInflater inflator = getLayoutInflater();
		
		denominationLayout.setGravity(Gravity.CENTER);
		
		j=3;
		
		for(int i = 0; i<currList.size(); i++){
			final int x =i;
			currencyLayout = (LinearLayout) inflator.inflate(R.layout.currency_node, null);
			final EditText txtQuantity = (EditText)currencyLayout.findViewById(R.id.txtQuantity);
			final ImageView imgCurrency = (ImageView)currencyLayout.findViewById(R.id.imgCurrency);
			
			
			
			imgCurrency.setBackgroundResource(currList.get(i).imageId);	
			imgCurrency.setTag(String.valueOf(currList.get(i).value));
			
			
			
			if(i%j==0)
			{
			 layout =new LinearLayout(mcontext);
			
			 layout.setLayoutParams(new LayoutParams(
                     LayoutParams.FILL_PARENT,
                     LayoutParams.WRAP_CONTENT));
			 
			 
             layout.setOrientation(LinearLayout.HORIZONTAL);
            
             
             denominationLayout.addView(layout);
			}
			//To increas size of notes not coins
			if(i>=3)
            {
           	 	LayoutParams lp = (LayoutParams) imgCurrency.getLayoutParams();
			       lp.width =180;
			       imgCurrency.setLayoutParams(lp);
            }
			imgCurrency.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Double dvalue= Double.parseDouble(imgCurrency.getTag().toString());
					
					givenValue = givenValue.add(BigDecimal.valueOf(dvalue));
					
					txtGiven.setText(givenValue.toString());
					
					
				}
			});
			layout.addView(currencyLayout);
			
			
		}
		
	}
	OnItemSelectedListener currSelector= new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			PoSDatabase db = new PoSDatabase(mcontext);
			
			ArrayList<com.pos.util.CurrencySetting> list =db.getAllCurrencySetting();
			switch (arg2) 
			{
			case 0:
				addCurrencyLayout(3, currencyListNaira);
				givenValue = BigDecimal.valueOf(0);
				txtChange.setText("");
				txtGiven.setText("");
				currencyId = 3;
				totalValue = new BigDecimal(payableMoney);
				txtTotal.setText(payableMoney);
				
				break;
			case 1:
				addCurrencyLayout(3, currencyListEuro);
				givenValue = BigDecimal.valueOf(0);
				txtChange.setText("");
				txtGiven.setText("");
				
				currencyId = 4;
				totalValue = new BigDecimal(payableMoney);
				String  saleValue;
				if(returnTransactionId.equals(""))
					 saleValue =list.get(0).getCurrSaleValue();
				else
					saleValue =list.get(0).getCurrReturnValue();
				BigDecimal bd = new BigDecimal(saleValue);
				
				System.out.println(totalValue +" "+bd);
				
				Log.e("jfkfkkf", totalValue +" "+bd);
				totalValue = totalValue.divide(bd ,2,RoundingMode.HALF_UP);
				
				System.out.println(totalValue);
				txtTotal.setText(totalValue.toString());
				
				break;
			case 2:
				addCurrencyLayout(3, currencyListDollar);
				givenValue = BigDecimal.valueOf(0);
				txtChange.setText("");
				txtGiven.setText("");
				totalValue = new BigDecimal(payableMoney);
				
				String  saleValue1;
				if(returnTransactionId.equals(""))
					 saleValue1 =list.get(0).getCurrSaleValue();
				else
					saleValue1 =list.get(0).getCurrReturnValue();
				
				  bd = BigDecimal.valueOf(Double.parseDouble(saleValue1));
					
				  currencyId = 5;
				  System.out.println(totalValue +" "+bd);
					
					Log.e("jfkfkkf", totalValue +" "+bd);
				  
				  totalValue = totalValue.divide(bd ,2,RoundingMode.HALF_UP);
					
				  System.out.println(totalValue);
					txtTotal.setText(totalValue.toString());
				  
				  
				break;
			}
			
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	OnClickListener Listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
			case R.id.btnDone:
				
			
			
				if(txtChange.getText().toString().equals(""))
						showDialog("Please enter given amount");
				else
				{
					
					Log.e("chnage value", ""+changeValue.doubleValue());
					if(changeValue.doubleValue()< 0)
					{
						changeValue = changeValue.multiply(new BigDecimal(-1));
						showDialog("An amount of ₦ "+ changeValue.toString() +  "  is due. Kindly pay .");
						changeValue = changeValue.multiply(new BigDecimal(-1));
					}
					else if(!returnTransactionId.equals("") && changeValue.compareTo(BigDecimal.valueOf(0))!=0){
						showDialog("Amount being returned cannot be greater than liable value."); 
					}else
					{
						showDialog("Payment is successful");
					}
				}
				
				//An amount of ... N is due. Kindly pay .
				
				 
				break;
			case R.id.btnCancel:
				finish();
				
				break;
			
			}
		}
	};
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	@SuppressWarnings("deprecation")
	public void showDialog(final String str)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
//		builder.setMessage(str);
		builder.setTitle("POS");
		
		TextView myMsg = new TextView(this);
		myMsg.setText(str);
		myMsg.setGravity(Gravity.CENTER);
		builder.setView(myMsg);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			@SuppressWarnings("unchecked")
			public void onClick(DialogInterface dialog, int which) 
			{
				
				if(str.equals("Payment is successful"))
				{
					
					
					if(returnTransactionId.equals(""))
					{
						/*String transAmountStr = transactionPrefs.getString("transAmount", "0");
						
						BigDecimal transAmount = BigDecimal.valueOf(Double.parseDouble(transAmountStr));
						
						transAmount = transAmount.add(totalValue);
						
						Editor editor = transactionPrefs.edit();
						editor.putString("transAmount", transAmount.toString());
						editor.commit();*/
						
						/*
						PoSDatabase db =new PoSDatabase(mcontext);
						db.addTransaction(employee_id, currencyId, finalTotal);
						
						Log.e("chnage", givenValue.toString());*/
						Log.i("tag", "Return transaction null");
						
						
					TransactionSaveOnWeb transaction = new TransactionSaveOnWeb(mcontext,totalTax,totalDiscount,totalValue,paymentMethod,VoucherName
							,voucherId,currencyId,discountType,mDiscountAmount, finalTotal, totalAmount, 0, String.valueOf(System.currentTimeMillis()/1000), false, changeValue.toString(), givenValue.toString());
					transaction.new SaveTransaction().execute(productList);
					}
					else
					{
						SharedPreferences transactionPrefs = PayCashActivity.this.getSharedPreferences("transPref", mcontext.MODE_PRIVATE);
						String transAmountStr = transactionPrefs.getString("transAmount", "0");
						
						BigDecimal transAmount = BigDecimal.valueOf(Double.parseDouble(transAmountStr));
						
						if(transAmount.add(BigDecimal.valueOf(1000)).compareTo(totalValue)>=0){
							/*transAmount = transAmount.subtract(totalValue);
							Editor editor = transactionPrefs.edit();
							editor.putString("transAmount", transAmount.toString());
							editor.commit();*/
							Log.i("tag", "Return transaction");	
							SaveReturnItemOnServer transaction = new SaveReturnItemOnServer(mcontext,ret_subTotal, ret_total, ret_Tax, ret_disc, ret_MDisc, pending_total, pending_vat, pending_discount, pending_MDiscount,returnTransactionId, newTransactionId, paymentMethod,productList,reasonId,finalTotal,itemReturnCode, currencyId, remProductList, remTotal, remTax, remDiscount, remMDiscount, totalValue,null,null,null,null,null);
							transaction.new SaveTransaction().execute(productList);
						}else{
							AlertDialog.Builder alert = new AlertDialog.Builder(PayCashActivity.this);
							alert.setMessage("Don't have enough balance in cash drawer. Please try to return by voucher.");
							alert.setTitle("POS");
							alert.setPositiveButton("OK", null);
							alert.show();
						}
					}
				}
			}
		});
		builder.show();
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
				Intent in =new Intent(mcontext,BreakActivity.class);
				startActivity(in);
				
			}
			Log.e("", ""+(elapTime));
		}
	}
	
	
}