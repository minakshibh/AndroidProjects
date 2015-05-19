package com.pos.retail;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.posapplication.R;
import com.pos.retail.LoginScreen.CurrencySetting;
import com.pos.util.ConnectionDetector;
import com.pos.util.PerformBackgroundTask;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.ScreenReceiver;
import com.pos.util.TransactionSaveOnWeb;
import com.pos.util.XMLParser;
import com.print.Utility;

public class TransactionActivity extends Activity {

	TextView subTotal,txtTotalDiscount,txtTotalVat,txtTotalDue,txtVoucherAmount,txtVoucherId;
	TextView txtDiscount,txtDiscountTypes,txtMDiscount;
	ImageButton btnCancelVoucher;
	Context mcontext;
	ImageButton menu;
	Button addItem,payPalBtn;
	LinearLayout menuLayout;
	Button btnTransaction, btnReturnItem,btnCancelItem,btnExchangeItem, btnSignOff, btnSettings, btnPayCash, btnFloatCash, btnOperatorDeclaration;
	Button payByVoucher , paybyCard,btnVoidReceipt,btnReceipt;
	SharedPreferences loginPref;
	ListView productListView;
	ArrayList<ProductReal> selectedProducts=new ArrayList<ProductReal>();
	ProductListAdapter adapter;
	Intent in;
	int quantity,counter=0;
	SharedPreferences itempref,userPref;
	SharedPreferences.Editor edit;
	String Voucher_Check;
	String KEY_ITEM="IssuedVoucher",KEY_VALIDITY = "Validity",KEY_NAME="Active",KEY_VOUCHERVALUE="Amount",KEY_VOUCHERID="VoucherNumber",KEY_STATUS="Status",KEY_VOUCHERNAME="VoucherName";
	LinearLayout voucherLayout;
	BigDecimal subTotalBD,mbd ;
	ConnectionDetector detector;
	LinearLayout bottomLayout;
	SharedPreferences pref;
	String producturl,Method_Name="GetProductList",naira, CheckVoucher_Method = "ValidateVouchers";
	ProgressDialog dialog;
	String paymentMethod =null;
	String voucherId = null,voucherName =null,validDate = null;
	BigDecimal totalTax, totalDiscount,voucherAmount;
	Timer timer = new Timer();
	Button discountBtn,btnBreak;
	String strDiscount="0";
	BigDecimal transactionDiscount =BigDecimal.valueOf(0),tempBd;
	BroadcastReceiver mReceiver = new ScreenReceiver();
	long curentTime=0;
	long elapTime = 0;
	  
	BigDecimal totalBd,totalMDiscount,totalTransMdiscount;
	int MDType = PosConstants.noDiscount;
	CheckBox checkBox;
	String signOffUrl,Method_SignOff="LogOffRecord";
	boolean isDisTypePerc = false;
	long startTime,elapseTime;
	boolean isMDscAppliedOnTrans = false;
	int totalMDiscType;
	SharedPreferences preference;
	Editor editor;
	String cashType="";
	 SharedPreferences posPref;
		Editor posEditor;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.transaction_activity);
		mcontext=this;
		
		preference=getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		editor = preference.edit();
		
		editor.putString(Utility.CashType,"");
		editor.putString(Utility.ItemReturnBy,"");
		editor.commit(); 
		
		posPref = getSharedPreferences("pos", MODE_PRIVATE);
		posEditor = posPref.edit();	
		
		naira=getResources().getString(R.string.naira);
		
		signOffUrl = getResources().getString(R.string.liveurl)+"/"+Method_SignOff;
		PosConstants.time_out = false;
		
		pref = getSharedPreferences("mypref", MODE_PRIVATE);
		
		Log.e("dlkdkj", ""+pref.getString("ReceiptGenerate", ""));
		
		
		discountBtn = (Button) findViewById(R.id.discountbtn);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
      
        registerReceiver(mReceiver, filter);
		
        PosConstants.activity_code =2;
        
		itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		
		edit = itempref.edit();
		
		edit.clear();
		
		voucherAmount = BigDecimal.valueOf(0);
		
		voucherLayout = (LinearLayout)findViewById(R.id.voucherLayout);
		
		txtVoucherId = (TextView) findViewById(R.id.txtVoucherId);
		txtDiscount = (TextView) findViewById(R.id.txtDiscountValue);
		txtDiscountTypes = (TextView) findViewById(R.id.discountType);
		txtMDiscount = (TextView) findViewById(R.id.mDiscount);
		
		paybyCard = (Button )findViewById(R.id.paybycard);
		btnCancelVoucher = (ImageButton) findViewById(R.id.cancelVoucher);
 		btnCancelVoucher.setOnClickListener(Listener);
		
		Voucher_Check = getResources().getString(R.string.liveurl)+"/"+CheckVoucher_Method;
		menu=(ImageButton)findViewById(R.id.menu);
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		
		detector=new ConnectionDetector(mcontext);
		
		producturl=getResources().getString(R.string.liveurl)+"/"+Method_Name;
		
		txtVoucherAmount = (TextView) findViewById(R.id.txtVoucherAmount);
		 
		productListView=(ListView)findViewById(R.id.listItem);
		
		 adapter=new ProductListAdapter(mcontext, selectedProducts);
		
		 productListView.setAdapter(adapter);
		 if(selectedProducts.size()!=0)
			 productListView.setSelection(selectedProducts.size()-1);
		 
		 bottomLayout=(LinearLayout)findViewById(R.id.bottomLayout);
		 
		 productListView.setOnItemClickListener(productClickListener);
		 
		addItem = (Button)findViewById(R.id.addItem);
		btnTransaction=(Button)findViewById(R.id.Trasaction);
//		btnFindItem=(Button)findViewById(R.id.findItem);
		btnReturnItem =(Button)findViewById(R.id.ReturnItem);
		btnSignOff=(Button)findViewById(R.id.signOff);
		btnPayCash=(Button)findViewById(R.id.paycash);
		btnFloatCash=(Button)findViewById(R.id.floatcashbtn);
		btnOperatorDeclaration=(Button)findViewById(R.id.operatorDeclaration);
		btnExchangeItem = (Button) findViewById(R.id.itemExchange);
		btnCancelItem = (Button) findViewById(R.id.itemCancel);
		btnVoidReceipt = (Button) findViewById(R.id.voidReceipt);
		btnSettings = (Button) findViewById(R.id.settings);
		
		payByVoucher=(Button)findViewById(R.id.paybyvoucher);
		payPalBtn = (Button) findViewById(R.id.paypal);
		btnBreak= (Button) findViewById(R.id.btnbreak);
		btnReceipt = (Button) findViewById(R.id.getReceipt);
		
		menu.setOnClickListener(Listener);
		btnTransaction.setOnClickListener(Listener);
//		btnFindItem.setOnClickListener(Listener);
		btnReturnItem.setOnClickListener(Listener);
		btnSignOff.setOnClickListener(Listener);
		btnPayCash.setOnClickListener(Listener);
		btnFloatCash.setOnClickListener(Listener);
		btnOperatorDeclaration.setOnClickListener(Listener);
		btnVoidReceipt.setOnClickListener(Listener);
		discountBtn.setOnClickListener(Listener);
		btnReceipt.setOnClickListener(Listener);
		btnSettings.setOnClickListener(Listener);
		
		if(loginPref.getInt("UserRollId", 1)>1)
			btnOperatorDeclaration.setVisibility(View.VISIBLE);
		else
			btnOperatorDeclaration.setVisibility(View.GONE);
		
		addItem.setOnClickListener(Listener);
		payByVoucher.setOnClickListener(Listener);
		
		btnExchangeItem.setOnClickListener(Listener);
		btnCancelItem .setOnClickListener(Listener);
		
		menuLayout=(LinearLayout)findViewById(R.id.llayout);
		
		subTotal = (TextView)findViewById(R.id.subTotal);
		txtTotalDiscount = (TextView)findViewById(R.id.discountTotal);
		txtTotalVat = (TextView)findViewById(R.id.vatTotal);
		txtTotalDue = (TextView)findViewById(R.id.dueTotal);
		
		paybyCard.setOnClickListener(Listener);
		
		payPalBtn.setOnClickListener(Listener);
		btnBreak.setOnClickListener(Listener);
		btnTransaction.setVisibility(View.GONE);
		
		callAsynchronousTask();
		new SyncProductListing().execute();
		
	}
	class SyncProductListing extends AsyncTask<String, String, String>
	{
		int result=1;
		ArrayList<ProductReal> productList =null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Synchronizing products");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
			
			int timestamp=posPref.getInt("producttimestamp", -1);	
			InputStream is = null;
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(producturl);
	         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("timestamp",Integer.toString(-1)));
	        
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
			
			return null;
		}
		
		
		@Override
	    protected void onPostExecute(String str) {
	    	
			if(result==0 && productList !=null)
		     {
				PoSDatabase db =new PoSDatabase(mcontext);
	    		
	    		db.deleteProducts();
				
		    	 for(int i=0;i<productList.size();i++)
		    	 {
		    		 
		    		 ProductReal product=productList.get(i);
//		    		 PoSDatabase db=new PoSDatabase(mcontext);
		    		 
		    		 if(product.isDeleted())
		    		 {
//		    			 db.deleteProduct(product.getPid());
//		    			 productList.remove(i);
		    		 }
		    		 else
		    		 {
		    			 db.addProduct(product);
		    		 }
		    		 
		    	 }
//	    		PoSDatabase db=new PoSDatabase(mcontext);
				productList=db.getAllProducts();
		     }
	    	
			
			try {
		        if ((dialog != null) && dialog.isShowing()) {
		            dialog.dismiss();
		        }
		    } catch (final IllegalArgumentException e) {
		        // Handle or log or ignore
		    } catch (final Exception e) {
		        // Handle or log or ignore
		    } finally {
		        dialog = null;
		    }  
	    	
	    	
	    	
//	    	new CurrencySetting().execute();
	    	
		}
	}
	public void setManualDiscount(int mDType, int productId, BigDecimal totalValue, BigDecimal discount){
		
			if(productId==-1){
				for(int i = 0; i<selectedProducts.size(); i++){
					ProductReal product = selectedProducts.get(i);
					product.setMDType(mDType);
					
					BigDecimal temp = null;
					
					if(mDType == PosConstants.MDPercentage){
						product.setMdiscount(discount);
						temp = calculateAmtAfterMDiscount(product, BigDecimal.valueOf(0), BigDecimal.valueOf(0), mDType);
					}else{
						
						temp = calculateAmtAfterMDiscount(product, totalValue, discount, mDType);
						BigDecimal tempMDisc = product.getTotalPrice().subtract(temp);
						product.setMdiscount(tempMDisc);
					}
					product.setDiscountedValue(temp);
				}
			}else{
				ProductReal product = selectedProducts.get(productId);
				product.setMdiscount(discount);
				product.setMDType(mDType);

				BigDecimal temp = null;
				
				if(mDType == PosConstants.MDPercentage)
					temp = calculateAmtAfterMDiscount(product, BigDecimal.valueOf(0), BigDecimal.valueOf(0), mDType);
				else
					temp = calculateAmtAfterMDiscount(product, totalValue, discount, mDType);
				
				product.setDiscountedValue(temp);
			}
		
		adapter=new ProductListAdapter(mcontext,selectedProducts);
		productListView.setAdapter(adapter);
		
		if(productId!=0 && productId!=-1)
			 productListView.setSelection(productId);
		
		setDataOnBottomLayout();
	}
	
	public void callAsynchronousTask() {
		final ConnectionDetector detector = new ConnectionDetector(mcontext);
	    final Handler handler = new Handler();
	   
	    TimerTask doAsynchronousTask = new TimerTask() {       
	        @Override
	        public void run() {
	            handler.post(new Runnable() {
	                public void run() {       
	                	if(detector.isConnectingToInternet())
	            		{
		                    try {
		                        PerformBackgroundTask performBackgroundTask = new PerformBackgroundTask(mcontext);
		                        // PerformBackgroundTask this class is the class that extends AsynchTask 
		                        performBackgroundTask.execute();
		                    } catch (Exception e) {
		                        // TODO Auto-generated catch block
		                    }
	            		}else{
	            		}
	                }
	            });
	        }
	    };
//	    timer.schedule(doAsynchronousTask, 0, 3600000);
	    timer.schedule(doAsynchronousTask, 0, 1800000);
	    //execute in every 50000 ms //7200000
	}
	
	
	OnClickListener Listener=new OnClickListener() {
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub	
			Intent intent;
			switch (v.getId()) 
			{
				case R.id.menu:
					if(menuLayout.getVisibility()==View.VISIBLE)
						menuLayout.setVisibility(View.GONE);
					else
					{
						menuLayout.setVisibility(View.VISIBLE);
					}
					break;
				case R.id.btnbreak:
					Intent intent1 =new Intent(mcontext,BreakActivity.class);
					startActivity(intent1);
					finish();
					break;
				case R.id.signOff:
					
					menuLayout.setVisibility(View.GONE);
					intent = new Intent(TransactionActivity.this, LoginScreen.class);
					intent.putExtra("trigger", "operatorDeclaration");
					
					startActivity(intent);
					
					break;
				case R.id.getReceipt:
					
					menuLayout.setVisibility(View.GONE);
					 
					 edit.putString("ItemReturnType", "Get Receipt");
					 edit.putString("ItemReturnCode", "GetReceipt");
					 edit.commit();
					 
					 in = new Intent(mcontext,ItemReturnsActivity.class);
					 startActivity(in);
					break;
				case R.id.floatcashbtn:
					menuLayout.setVisibility(View.GONE);
					
					intent = new Intent(TransactionActivity.this,FloatCashActivity.class);
					startActivity(intent);
					
					break;
				case R.id.voidReceipt:
					Intent invoid = new Intent(mcontext,VoidReceiptLogin.class);
					
					 edit.putString("ItemReturnType", "Void Receipt");
					 edit.putString("ItemReturnCode", "VoidReceipt");
					 edit.commit();
					 
					 startActivity(invoid);
					
					break;
				/*case R.id.findItem:
					menuLayout.setVisibility(View.GONE);
					Intent in=new Intent(mcontext,ProductList.class);
					startActivity(in);
					break;*/
					
				case R.id.ReturnItem:
					 menuLayout.setVisibility(View.GONE);
					 
					 edit.putString("ItemReturnType", "Item Return");
					 edit.putString("ItemReturnCode", "ReturnItem");
					 edit.commit();
					 
					 in = new Intent(mcontext,ItemReturnsActivity.class);
					 startActivity(in);
					
					break;
				case R.id.itemExchange:
					menuLayout.setVisibility(View.GONE);
					edit.putString("ItemReturnType", "Item Exchange");
					 edit.putString("ItemReturnCode", "ItemExchange");
					 edit.commit();
					 in = new Intent(mcontext,ItemReturnsActivity.class);
					startActivity(in);
					
					break;
				case R.id.itemCancel:
					edit.putString("ItemReturnType", "Order Cancel");
					edit.putString("ItemReturnCode", "CancelOrder");
					edit.commit();
					menuLayout.setVisibility(View.GONE);
					in = new Intent(mcontext,ItemReturnsActivity.class);
					startActivity(in);
					
					break;
				case R.id.paycash:
					menuLayout.setVisibility(View.GONE);
					if(selectedProducts.size()>0)
					{
						if (totalBd.compareTo(BigDecimal.ZERO) > 0)
						{
								cashType="Cash";
								editor.putString(Utility.CashType,cashType);
								editor.commit(); 
								menuLayout.setVisibility(View.GONE);
								
								System.out.println("Total discount"+totalDiscount.toString());
								
								in=new Intent(mcontext, PayCashActivity.class);
								in.putExtra("paycash", totalBd.subtract(voucherAmount).toString());
								in.putExtra("totalTax",totalTax.toString() );
								in.putExtra("totalDiscount", totalDiscount.toString());
								in.putExtra("listofproduct", selectedProducts);
								
								if(voucherName == null)
									voucherName = "";
								
								in.putExtra("VoucherName", voucherName);
								in.putExtra("manualdiscount", totalMDiscount.toString());
								in.putExtra("finalTotal", totalBd.toString());
								in.putExtra("TotalAmount", subTotalBD.toString());
								
								if(!(totalMDiscount.compareTo(BigDecimal.valueOf(0))<=0))
								{
									if(totalMDiscType == PosConstants.MDPercentage)
										in.putExtra("discounttype", 1);
									else
										in.putExtra("discounttype", 2);
								}
								else
								{
									in.putExtra("discounttype", 0);
								}
								
								if(voucherAmount.compareTo(BigDecimal.ZERO)>0)
								{
									in.putExtra("PaymentMethodType", "Cash_Voucher");
									in.putExtra("VoucherId", txtVoucherId.getText().toString());
								}
								else
									in.putExtra("PaymentMethodType", "Cash");
								
								startActivity(in);
							}
							else
							{
								
								final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
								builder.setMessage("Your cart is empty");
								builder.setButton("OK", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
										builder.dismiss();
									}
								});
								builder.show();
								
							}
						
					}
					else
					{
						
						final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
						builder.setTitle("Notification");
						builder.setMessage("Your cart is empty");
						builder.setButton2("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
								
							}
						});
						
						builder.show();
					}
					break;
				case R.id.paypal:
					 intent = new Intent(mcontext,PayPalClass.class);
					 
					break;
				case R.id.Trasaction:
					menuLayout.setVisibility(View.GONE);
					
					break;
				case R.id.paybyvoucher:
					if(selectedProducts.size()>0){
						cashType="Voucher";
						editor.putString(Utility.CashType,cashType);
						editor.commit();
						showDialogVoucher();
					}
					else
					{
						final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
						builder.setTitle("Notification");
						builder.setMessage("Your cart is empty");
						builder.setButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
							}
						});
						builder.show();
					}
					break;
					
				case R.id.addItem:
					menuLayout.setVisibility(View.GONE);
					 in=new Intent(mcontext,AddProducts.class);
					 startActivity(in);
					 
					break;
				case R.id.operatorDeclaration:
					menuLayout.setVisibility(View.GONE);
					intent = new Intent(TransactionActivity.this, LoginScreen.class);
					intent.putExtra("trigger", "operatorDeclaration");
					startActivity(intent);
					
					break;
				case R.id.settings:
					menuLayout.setVisibility(View.GONE);
					intent = new Intent(TransactionActivity.this, ChooseTrxOption.class);
					startActivity(intent);
					
					break;
				/*case R.id.masterServerConfig:
					
					menuLayout.setVisibility(View.GONE);
					intent = new Intent(TransactionActivity.this, TrxSettings.class);
					intent.putExtra("trigger", "trx");
					startActivity(intent);
					
					
					menuLayout.setVisibility(View.GONE);
					intent = new Intent(TransactionActivity.this, TrxSettings.class);
					intent.putExtra("trigger", "master");
					startActivity(intent);
					
					break;*/
				case R.id.cancelVoucher:
//					totalBd = totalBd.add(voucherAmount);
					voucherAmount = BigDecimal.valueOf(0);
					
					txtVoucherAmount.setText(naira + " " +voucherAmount.toString());
					txtVoucherId.setText("Voucher Id");

		    		txtVoucherId.setText("");
		    		voucherId = "";
		    		txtTotalDue.setText( naira + totalBd.toString());
		    	
					break;
				case R.id.discountbtn:
					
					if(selectedProducts.size() > 0)
					{
						
						final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
						builder.setMessage("All the previous discounts on the selected  items will removed ..continue ?");
						builder.setButton("YES", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
								
								final AlertDialog dialogDiscount = new AlertDialog.Builder(mcontext).create();
								dialogDiscount.setTitle("Select Discount");
								
								LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
								View discountview = inflater.inflate( R.layout.discountlayout, null );
								
								Button btnTen = (Button)discountview.findViewById(R.id.ten);
								Button btnTwenty = (Button)discountview.findViewById(R.id.twenty);
								Button btnFifty = (Button)discountview.findViewById(R.id.fifty);
								Button btnSeven = (Button)discountview.findViewById(R.id.seventy);
								Button manualSelect = (Button) discountview.findViewById(R.id.selectManual);
								
								dialogDiscount.setView(discountview);
								
								btnTen.setOnClickListener(new View.OnClickListener() {
								    public void onClick(View v) {
								    	dialogDiscount.dismiss();
								    	chkDiscOnTotalTrans(PosConstants.MDPercentage, -1, BigDecimal.valueOf(0), "10");
								    	

								    }
								});
								btnTwenty.setOnClickListener(new View.OnClickListener() {
								    public void onClick(View v) {
								    	dialogDiscount.dismiss();
								    	
								    	chkDiscOnTotalTrans(PosConstants.MDPercentage, -1, BigDecimal.valueOf(0), "20");
								    }
								});
								btnFifty.setOnClickListener(new View.OnClickListener() {
								    public void onClick(View v) {
								    	dialogDiscount.dismiss();
								    	chkDiscOnTotalTrans(PosConstants.MDPercentage, -1, BigDecimal.valueOf(0), "50");
								    	
								    }
								});
								btnSeven.setOnClickListener(new View.OnClickListener() {
								    public void onClick(View v) {
								    	dialogDiscount.dismiss();
								    	chkDiscOnTotalTrans(PosConstants.MDPercentage, -1, BigDecimal.valueOf(0), "70");
								    }
								});
								manualSelect.setOnClickListener(new View.OnClickListener() {
								    public void onClick(View v) {
								    	
								    	dialogDiscount.dismiss();
								    	
								    	showManualDiscount(-1, subTotalBD, subTotalBD.subtract(totalMDiscount));
								    	
								    }
								});
								dialogDiscount.show();
								
							}
						});
						builder.setButton2("NO", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
							}
						});
						builder.show();
						
						
					}
					else
					{
						final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
						builder.setMessage("Your cart is empty");
						builder.setButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
							}
						});
						builder.show();
					}
					break;
				case R.id.paybycard:
					
					if(selectedProducts.size()>0)
					{
						if (totalBd.compareTo(BigDecimal.ZERO) > 0)
						{
							
						
						
						/*final AlertDialog dialog = new AlertDialog.Builder(mcontext).create();
						dialog.setTitle("Pay by Card");
						
						*/
						
						if(voucherAmount.compareTo(BigDecimal.ZERO)>0)
						{
							paymentMethod = "Card_Voucher";
							voucherId= txtVoucherId.getText().toString();
						}
						else
							paymentMethod = "Card";
						
						int userId = loginPref.getInt("UserId", 0);
		                PoSDatabase db =new PoSDatabase(mcontext);
						db.addTransaction(userId, 3, totalBd.toString());
						
						int paymenttype=0;
						
						if(!(totalMDiscount.compareTo(BigDecimal.valueOf(0))<=0))
						{
							if(totalMDiscType == PosConstants.MDPercentage)
								paymenttype=1;
							else
								paymenttype=2;
						}
						else
						{
							paymenttype = 0;
						}
						
						Intent _in =new Intent(mcontext,PayPalClass.class);
						_in.putExtra("paycash", totalBd.subtract(voucherAmount).toString());
						_in.putExtra("TotalTax",totalTax.toString());
						_in.putExtra("TotalDiscount",totalDiscount.toString());
						_in.putExtra("TotalAmount",totalBd.toString());
						_in.putExtra("paymentMethod",paymentMethod);
						_in.putExtra("voucherName",voucherName);
						_in.putExtra("voucherId",voucherId);
						_in.putExtra("paymenttype",paymenttype);
						_in.putExtra("strDiscount",totalMDiscount);
						_in.putExtra("itemSelected",selectedProducts);
						_in.putExtra("finalTotal", totalBd.toString());
						_in.putExtra("TotalAmount", subTotalBD.toString());
						_in.putExtra("TransactionType","");
						startActivity(_in);
						
						/*View view = ((Activity) mcontext).getLayoutInflater().inflate(R.layout.paybycard,null);
						
						dialog.setView(view);
						
						TextView txtTotal = (TextView)view.findViewById(R.id.total);
						Button btnOk = (Button) view.findViewById(R.id.ok);
						Button btnCancel = (Button) view.findViewById(R.id.cancel);
						
						
						
						btnOk.setOnClickListener(new OnClickListener() {
							    @SuppressWarnings("unchecked")
								public void onClick(View view) {
							
							                dialog.dismiss();
							                int userId = loginPref.getInt("UserId", 0);
							                PoSDatabase db =new PoSDatabase(mcontext);
											db.addTransaction(userId, 3, totalBd.toString());
											
											int paymenttype=0;
											
											if(!(totalMDiscount.compareTo(BigDecimal.valueOf(0))<=0))
											{
												if(totalMDiscType == PosConstants.MDPercentage)
													paymenttype=1;
												else
													paymenttype=2;
											}
											else
											{
												paymenttype = 0;
											}
											
											Intent in =new Intent(mcontext,PayPalClass.class);
											in.putExtra("TotalTax",totalTax.toString());
											in.putExtra("TotalDiscount",totalDiscount.toString());
											in.putExtra("TotalAmount",totalBd.toString());
											in.putExtra("paymentMethod",paymentMethod);
											in.putExtra("voucherName",voucherName);
											in.putExtra("voucherId",voucherId);
											in.putExtra("paymenttype",paymenttype);
											in.putExtra("strDiscount",totalMDiscount);
											in.putExtra("itemSelected",selectedProducts);
											in.putExtra("finalTotal", totalBd.toString());
											in.putExtra("TotalAmount", subTotalBD.toString());
											in.putExtra("TransactionType","");
											startActivity(in);
							            }
							
							        });
						btnCancel.setOnClickListener(new OnClickListener() {
						    public void onClick(View view) {
						
						    	dialog.dismiss();
						
						            }
						
						        });
					
					
						
						
						dialog.show();*/
						
						}
						else
						{
							final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
							builder.setTitle("Notification");
							builder.setMessage("Total amount is not valid ");
							builder.setButton2("OK", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
									builder.dismiss();
									
								}
							});
							
							
							builder.show();
							
						}
					}
					else
					{
						final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
						builder.setTitle("Notification");
						builder.setMessage("Your cart is empty");
						builder.setButton("OK", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								builder.dismiss();
							}
						});
						builder.show();
						
						
					}
					
				
					
				}
		}
	};
	OnItemClickListener productClickListener= new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			final int pos = arg2;
			final ProductReal product = selectedProducts.get(arg2);
			final TextView txtMDiscount = (TextView)arg1.findViewById(R.id.mdiscount);
			final TextView txtDiscount = (TextView) arg1.findViewById(R.id.discount);
			final TextView txtTotal = (TextView) arg1.findViewById(R.id.total);
			

			
			LinearLayout mdlayout = (LinearLayout) arg1.findViewById(R.id.mdlayout);
			
			mdlayout.setVisibility(View.VISIBLE);
			final AlertDialog dialogDiscount = new AlertDialog.Builder(mcontext).create();
			dialogDiscount.setTitle("Select Discount");
			
			LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
			View discountview = inflater.inflate( R.layout.discountlayout, null );
			
			Button btnTen = (Button)discountview.findViewById(R.id.ten);
			Button btnTwenty = (Button)discountview.findViewById(R.id.twenty);
			Button btnFifty = (Button)discountview.findViewById(R.id.fifty);
			Button btnSeven = (Button)discountview.findViewById(R.id.seventy);
			Button manualSelect = (Button) discountview.findViewById(R.id.selectManual);
			Button removeMDiscount = (Button) discountview.findViewById(R.id.removeMDiscount);
			
			dialogDiscount.setView(discountview);
			
			btnTen.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			    	dialogDiscount.dismiss();
			    	chkDiscOnTotalTrans(PosConstants.MDPercentage,pos, BigDecimal.valueOf(0), "10");
			    }
			});
			btnTwenty.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			    	
			    	dialogDiscount.dismiss();
			    	
			    	chkDiscOnTotalTrans(PosConstants.MDPercentage, pos, BigDecimal.valueOf(0), "20");
			    	
			    }
			});
			btnFifty.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			    	dialogDiscount.dismiss();
			    	chkDiscOnTotalTrans(PosConstants.MDPercentage, pos, BigDecimal.valueOf(0), "50");
			    }
			});
			btnSeven.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			    	dialogDiscount.dismiss();
			    	chkDiscOnTotalTrans(PosConstants.MDPercentage, pos, BigDecimal.valueOf(0), "70");
			    }
			});
			manualSelect.setOnClickListener(new View.OnClickListener() {
			    public void onClick(View v) {
			    	dialogDiscount.dismiss();
			    	String temp = subTotal.getText().toString();
			    	temp = temp.replace(naira+" ", "");
			    	showManualDiscount(pos, product.getTotalPrice(), BigDecimal.valueOf(Double.parseDouble(temp)));
			    }
			});
			removeMDiscount.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					dialogDiscount.dismiss();
					
				}
			});
			
			dialogDiscount.show();
			
			
			
			
			
		}
	};
	
	public void onPause()
	{
		super.onPause();
		
		Log.e("lklkdfkf",""+ ScreenReceiver.wasScreenOn);
		
		if(!ScreenReceiver.wasScreenOn)
			curentTime =System.currentTimeMillis();
		
		
	}
	public void onResume()
	{
		super.onResume();
		
		Log.e("MDType ", ""+MDType);
		Log.e("lklkdfkf",""+ ScreenReceiver.wasScreenOn);
		
		if(ScreenReceiver.wasScreenOn || PosConstants.time_out)
		{
			elapTime = System.currentTimeMillis();
			
			elapTime = (elapTime-curentTime);
			if(elapTime>PosConstants.TIMEOUT_IN_MS && elapTime<System.currentTimeMillis())
			{
				PosConstants.time_out = true;
				Intent in =new Intent(mcontext,BreakActivity.class);
				startActivity(in);
				finish();
			}
			Log.e("", ""+(elapTime));
		}
		
		if(pref.getString("ReceiptGenerate", null) !=null)
		{
			System.out.println("Transsactio action" +pref.getString("ReceiptGenerate", null));
			TransactionActivity.this.finish();
			
			
			Log.e("value ", "kdjd");
			pref.edit().clear().commit();
			
			startActivity(getIntent());
			
		}else{
			try{
			if(pref.getString("ProductId", null)!=null)
			{			
				String id = pref.getString("ProductId", null);
				
				ProductReal product = new ProductReal();
				product.setPid(Integer.valueOf(id));
				product.setSelectedQuatity(Integer.valueOf(pref.getString("Quantity","0")));
				product.setProductName(pref.getString("desc", ""));
				product.setTax(BigDecimal.valueOf(Double.valueOf(pref.getString("vat", "0"))));
				product.setDiscount(BigDecimal.valueOf(Double.valueOf(pref.getString("discount","0"))));
				product.setPrice(BigDecimal.valueOf(Double.valueOf(pref.getString("price", "0"))));
				product.setTotalPrice(BigDecimal.valueOf(Double.valueOf(pref.getString("total", "0"))));
				product.setItemId(pref.getString("itemId", product.getItemId()));
				product.setBarCode(pref.getString("barcode", product.getBarCode()))	;
				product.setTotalQuantity(pref.getInt("totalQuantity", product.getTotalQuantity()));
				
				product.setSingleItemDiscount(BigDecimal.valueOf(Double.valueOf(pref.getString("singleItemDiscount","0"))));
				product.setSingleItemTax(BigDecimal.valueOf(Double.valueOf(pref.getString("singleItemTax","0"))));
				
				product.setMdiscount( BigDecimal.valueOf(Double.parseDouble(pref.getString("Mdiscount", "0"))));
				product.setMDType(pref.getInt("MdType", PosConstants.noDiscount));
				
				int itemPosition= pref.getInt("ItemSelected", -1);
				
				bottomLayout.setVisibility(View.VISIBLE);
							
				SharedPreferences.Editor edit=pref.edit();
				edit.clear();
				edit.commit();
				
				
					
				if(itemPosition==-1){
					if(isMDscAppliedOnTrans){
						if(totalMDiscType == PosConstants.MDPercentage)
						{
							BigDecimal percentage = selectedProducts.get(0).getMdiscount();
							product.setMdiscount(percentage);
							product.setMDType(PosConstants.MDPercentage);
							
							BigDecimal temp = calculateAmtAfterMDiscount(product, BigDecimal.valueOf(0), percentage, PosConstants.MDPercentage);
							product.setDiscountedValue(temp);
							selectedProducts.add(product);
							
							adapter =new ProductListAdapter(mcontext, selectedProducts);
							productListView.setAdapter(adapter);
							
							 if(selectedProducts.size()!=0)
								 productListView.setSelection(selectedProducts.size()-1);
							 
							setDataOnBottomLayout();
						}else{
							subTotalBD = subTotalBD.add(product.getTotalPrice());
							
							selectedProducts.add(product);
							chkDiscOnTotalTrans(PosConstants.MDAmount, -1, subTotalBD, totalTransMdiscount.toString());
						}
					}else{
						product.setDiscountedValue(product.getTotalPrice().subtract(product.getDiscount()));
						selectedProducts.add(product);
						
						adapter =new ProductListAdapter(mcontext, selectedProducts);
						productListView.setAdapter(adapter);
						
						 if(selectedProducts.size()!=0)
							 productListView.setSelection(selectedProducts.size()-1);
						 
						setDataOnBottomLayout();
					}
				}
				else{
					
				
					
					if(isMDscAppliedOnTrans){
						if(totalMDiscType == PosConstants.MDPercentage)
						{
							BigDecimal percentage = selectedProducts.get(0).getMdiscount();
							product.setMdiscount(percentage);
							product.setMDType(PosConstants.MDPercentage);
							
							BigDecimal temp = calculateAmtAfterMDiscount(product, BigDecimal.valueOf(0), percentage, PosConstants.MDPercentage);
							product.setDiscountedValue(temp);
							selectedProducts.set(itemPosition, product);
							
							adapter =new ProductListAdapter(mcontext, selectedProducts);
							productListView.setAdapter(adapter);
							setDataOnBottomLayout();
						}else{
							subTotalBD = subTotalBD.subtract(selectedProducts.get(itemPosition).getTotalPrice());
							subTotalBD = subTotalBD.add(product.getTotalPrice());
							
							selectedProducts.set(itemPosition, product);
							chkDiscOnTotalTrans(PosConstants.MDAmount, -1, subTotalBD, totalTransMdiscount.toString());
						}
					}else{
						if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
							selectedProducts.set(itemPosition, product);
							
							if(product.getMDType() == PosConstants.MDPercentage)
								setManualDiscount(product.getMDType(), itemPosition, BigDecimal.valueOf(0), product.getMdiscount());
							else
								setManualDiscount(product.getMDType(), itemPosition, product.getTotalPrice(), product.getMdiscount());
						}
						else{
							product.setDiscountedValue(product.getTotalPrice().subtract(product.getDiscount()));
							selectedProducts.set(itemPosition, product);
							
							adapter =new ProductListAdapter(mcontext, selectedProducts);
							productListView.setAdapter(adapter);
							
							productListView.setSelection(itemPosition);
							 
							setDataOnBottomLayout();
						}
					}
				}
			}
			
			}catch(Exception e){
				e.printStackTrace();
				Editor editor = pref.edit();
				editor.clear();
				editor.commit();
			}
		}
	}
	
	public BigDecimal calculateAmtAfterMDiscount(ProductReal product, BigDecimal totalPrice, BigDecimal mDiscount, int mDType){
		BigDecimal temp = null;
		
		if(mDType == PosConstants.MDPercentage){
			if(totalPrice.compareTo(BigDecimal.valueOf(0)) == 0){
				totalPrice = product.getTotalPrice();
				temp = totalPrice.multiply(product.getMdiscount());
			}else
				temp = totalPrice.multiply(mDiscount);
			
			temp = temp.divide(BigDecimal.valueOf(100), 2);
			temp = totalPrice.subtract(temp);
		}else{
			if(totalPrice.compareTo(BigDecimal.valueOf(0)) == 0){
				temp = product.getTotalPrice().subtract(product.getMdiscount());
			}else{
				
				BigDecimal tempPercentage = mDiscount.multiply(BigDecimal.valueOf(100));
				tempPercentage = tempPercentage.divide(totalPrice, 4);
				
				temp = product.getTotalPrice().multiply(tempPercentage);
				temp = temp.divide(BigDecimal.valueOf(100), 2);
				temp = product.getTotalPrice().subtract(temp);
			}
		}
		return temp;
	}
	
	class ProductListAdapter extends ArrayAdapter<ProductReal>
	{
		
		ArrayList<ProductReal> list;
		public ProductListAdapter(Context context, ArrayList<ProductReal> objects) {
			super(context,android.R.layout.simple_list_item_1,objects);
			
			list=objects;
			
		}
		public View getView(final int position,View convertView,ViewGroup parent)
		{
			if(convertView==null)
				convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.itemlayout, null);
			
			
			TextView pid = (TextView)convertView.findViewById(R.id.pid);
			TextView itemId = (TextView)convertView.findViewById(R.id.pItemId);
			TextView pname = (TextView)convertView.findViewById(R.id.pname);
			TextView pprice=(TextView)convertView.findViewById(R.id.pprice);
			TextView tax=(TextView)convertView.findViewById(R.id.tax);
			
			TextView discount=(TextView)convertView.findViewById(R.id.discount);
			TextView qunatity=(TextView)convertView.findViewById(R.id.quantity);
			TextView total=(TextView)convertView.findViewById(R.id.total);
			
			TextView mdiscount=(TextView)convertView.findViewById(R.id.mdiscount);
			LinearLayout layout =(LinearLayout) convertView.findViewById(R.id.mdlayout);
			TextView delete=(TextView)convertView.findViewById(R.id.delete);
			TextView edit = (TextView)convertView.findViewById(R.id.edit);
			
			
			RelativeLayout rlayout=(RelativeLayout)convertView.findViewById(R.id.totalLayout);
			
			TextView txt=(TextView)convertView.findViewById(R.id.labelQuntity);
			
			if(quantity==1)
				txt.setText(" "+"item");
			else
				txt.setText(" "+"items");
			
			rlayout.setVisibility(View.VISIBLE);
			final ProductReal product=list.get(position);
			
		
			pid.setText(""+product.getPid());
			pname.setText(""+product.getProductName());
			itemId.setText(""+product.getItemId());
			
			pprice.setText(naira +" "+product.getPrice().toString());
			qunatity.setText(""+product.getSelectedQuatity());
			tax.setText(naira+" "+product.getTax().toString());
			
			
			if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))==1)
			{
				discount.setText(naira +" 0");

				if(product.getMDType() == PosConstants.MDPercentage){
					mdiscount.setText(product.getMdiscount().toString()+" %");
				}else{
					
					mdiscount.setText(naira +" "+ product.getMdiscount().toString());
				}
				total.setText(naira +" "+product.getDiscountedValue().toString());

			}else{
				discount.setText(naira+" "+product.getDiscount().toString());	
				mdiscount.setText("0 %");
				
				total.setText(naira+" "+ product.getDiscountedValue().toString());
			}
			
			layout.setVisibility(View.VISIBLE);

			
			delete.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
					builder.setMessage("Do you want delete item ?");
					builder.setButton2("Yes", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
							subTotalBD = subTotalBD.subtract(product.getTotalPrice());
							
							selectedProducts.remove(product);
							
							if(isMDscAppliedOnTrans){
								if(totalMDiscType == PosConstants.MDAmount){
									chkDiscOnTotalTrans(PosConstants.MDAmount, -1, subTotalBD, totalTransMdiscount.toString());
								}else{
									adapter =new ProductListAdapter(mcontext, selectedProducts);
									productListView.setAdapter(adapter);
									 
									setDataOnBottomLayout();
								}
							}else{
								adapter =new ProductListAdapter(mcontext, selectedProducts);
								productListView.setAdapter(adapter);

								setDataOnBottomLayout();
							}
						}
					});
					builder.setButton("No", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						builder.dismiss();	
						}
					});
					
					builder.show();
					
				}
			});
			edit.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
							ProductReal selectedproduct= product;
							Intent in=new Intent(TransactionActivity.this,AddProducts.class);
							in.putExtra("product", selectedproduct);
							in.putExtra("ItemPosition", position);
							in.putExtra("isMDiscAppliedOnTrans", isMDscAppliedOnTrans);
							startActivity(in);
						
				}
			});
			
			return convertView;
		}
	}
	

	public void setDataOnBottomLayout()
	{
		subTotalBD = BigDecimal.valueOf(0);
		totalBd = BigDecimal.valueOf(0);
		totalDiscount = BigDecimal.valueOf(0);
		totalTax = BigDecimal.valueOf(0);
		totalMDiscount = BigDecimal.valueOf(0);
		
		for(int i =0; i<selectedProducts.size(); i++)
		{
			ProductReal product = selectedProducts.get(i);
			subTotalBD = subTotalBD.add(product.getTotalPrice());

			if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
				if(product.getMDType() == PosConstants.MDPercentage){
					BigDecimal temp = product.getMdiscount().multiply(product.getTotalPrice());
					temp = temp.divide(BigDecimal.valueOf(100), 2);
					
					totalMDiscount = totalMDiscount.add(temp);
				}else
					totalMDiscount = totalMDiscount.add(product.getMdiscount());
			}else{
				totalDiscount = totalDiscount.add(product.getDiscount());
			}
			
			totalTax = totalTax.add(product.getTax());
		}
		
		subTotal.setText(naira +" "+subTotalBD.toString());
		txtTotalDiscount.setText(naira+" "+totalDiscount.toString());
		txtTotalVat.setText(naira +" "+totalTax.toString());
		
		totalBd = subTotalBD.subtract(totalDiscount);
		
		if(isMDscAppliedOnTrans && totalMDiscType == PosConstants.MDAmount){
			totalMDiscount = totalTransMdiscount;
			txtMDiscount.setText(naira+" "+totalTransMdiscount.toString());
			totalBd = totalBd.subtract(totalTransMdiscount);
		}else{
			txtMDiscount.setText(naira+" "+totalMDiscount.toString());
			totalBd = totalBd.subtract(totalMDiscount);
		}
		
		
		txtTotalDue.setText(naira +" "+totalBd.toString());
		
		
		if(totalBd.compareTo(BigDecimal.valueOf(0))<=0){
			isMDscAppliedOnTrans = false;
			totalTransMdiscount = BigDecimal.valueOf(0);
		}
		
		if(totalBd.compareTo(BigDecimal.valueOf(0))<0 && totalMDiscount.compareTo(BigDecimal.valueOf(0))>0){
			for(int i = 0; i<selectedProducts.size(); i++){
				ProductReal product = selectedProducts.get(i);
				product.setMDType(PosConstants.MDnoapplied);
				product.setMdiscount(BigDecimal.valueOf(0));
				product.setDiscountedValue(product.getTotalPrice().subtract(product.getDiscount()));
			}
			
			adapter=new ProductListAdapter(mcontext,selectedProducts);
			productListView.setAdapter(adapter);
			setDataOnBottomLayout();
			
			
			AlertDialog.Builder alert = new AlertDialog.Builder(TransactionActivity.this);
			alert.setTitle("Invalid M-Discount");
			alert.setMessage("M-Discount cannot be greater than bill amount. Resetting M-Discount to 0 %");
			alert.setPositiveButton("OK", null);
			alert.show();
		}
		
		if(voucherAmount.compareTo(BigDecimal.valueOf(0))>0){
			if(totalBd.compareTo(voucherAmount)<0){
				AlertDialog.Builder alert = new AlertDialog.Builder(TransactionActivity.this);
				alert.setTitle("Invalid Bill Amount");
				alert.setMessage("Total Bill Amount cannot be less than 0. Resetting applied voucher.");
				alert.setPositiveButton("OK", null);
				alert.show();
				
				voucherAmount = BigDecimal.valueOf(0);
				voucherId = "";
				txtVoucherAmount.setText("");
				txtVoucherId.setText("");
			}else
				txtTotalDue.setText(naira +" "+totalBd.subtract(voucherAmount).toString());
		}

	}
	
	class checkValidVoucherNumber extends AsyncTask<String, String, String>
	{

		int result=1;
		boolean active = false;
		  String Id ="";
		java.io.InputStream in = null;
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Checking Valid "+"\n"+ "Voucher Number");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			try
			{
				
				
				DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost requestLogin = new HttpPost(Voucher_Check);
		         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		         
		         Log.e("fff  ", params[0]);
		         nameValuePairs.add(new BasicNameValuePair("VoucherNumber",params[0]));
		        
		   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     HttpResponse response = httpClient.execute(requestLogin);
			     HttpEntity entity = response.getEntity();
			     
			    String xml = EntityUtils.toString(entity);
			    
			    Log.e(" ", xml);
			    XMLParser xmlparser=new XMLParser();
			    Document document = xmlparser.getDomElement(xml);
			    
			    
			    
			    NodeList nl = document.getElementsByTagName(KEY_ITEM);
			    
			    for (int i = 0; i < nl.getLength(); i++) {
			    	Element e=(Element)nl.item(i);
			        String name = xmlparser.getValue(e, KEY_NAME); 
			        
			        validDate = xmlparser.getValue(e, KEY_VALIDITY);
			        String amount = xmlparser.getValue(e, KEY_VOUCHERVALUE); 
			         Id = xmlparser.getValue(e, KEY_VOUCHERID); 
			        active = Boolean.parseBoolean(name);
			        voucherName = xmlparser.getValue(e, KEY_VOUCHERNAME);
			        
			        voucherAmount = new BigDecimal(amount);
			    }
			    System.out.println(active);
			    Log.i("tag", "Active:"+active);
			   
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
	    	if(!active)
	    	{
	    		voucherAmount = BigDecimal.valueOf(0);
	    		showDialog("Voucher number is invalid");
	    	}
	    	else
	    	{
	    		System.out.println(txtVoucherId.getText()+" "+Id);
	    		if(!txtVoucherId.getText().equals(Id))
	    		{
	    			if(voucherAmount.compareTo(totalBd)<0)
	    			{
			    		voucherLayout.setVisibility(View.VISIBLE);
			    		txtVoucherAmount.setText(naira +" "+voucherAmount.toString());
			    		txtVoucherId.setText(Id);
			    		voucherId = Id;
			    		txtTotalDue.setText( naira + totalBd.subtract(voucherAmount).toString());
	    			}
	    			else if(voucherAmount.compareTo(totalBd)==0)
	    			{
	    				
			    		txtVoucherId.setText(Id);
			    		txtTotalDue.setText( naira + totalBd.toString());
			    		int userId = loginPref.getInt("UserId", 0);
			    		
			    		/*PoSDatabase db =new PoSDatabase(mcontext);
			    		db.addTransaction(userId, 3, voucherAmount.toString());
			    		
			    		Intent in =new Intent(mcontext,VoucherReceiptGenerate.class);
			    		in.putExtra("VoucherType", voucherName);
			    		in.putExtra("Amount", voucherAmount.toString());
			    		in.putExtra("VoucherDate", validDate);
			    		in.putExtra("VoucherId", Id);
			    		in.putExtra("itemList", selectedProducts);
			    		in.putExtra("Method", "Voucher");
			    		in.putExtra("finalTotal", totalBd.toString());
						in.putExtra("TotalAmount", subTotalBD.toString());
			    		startActivity(in);*/
			    		/*PoSDatabase db =new PoSDatabase(mcontext);
						db.addTransaction(userId, 0, voucherAmount.toString());
						*/
						
					TransactionSaveOnWeb transaction = new TransactionSaveOnWeb(mcontext,totalTax.toString(),totalDiscount.toString(),totalBd,"Voucher",voucherName
							,Id,3,totalMDiscType,totalMDiscount.toString(), voucherAmount.toString(), subTotalBD.toString(), 0, String.valueOf(System.currentTimeMillis()/1000), false, "0", voucherAmount.toString());
//				
					transaction.new SaveTransaction().execute(selectedProducts);
	    			}
	    			else
	    			{
	    				voucherAmount = BigDecimal.valueOf(0);
	    				showDialog("Voucher amount is greater than bill amount. This voucher cannot be applied.");
	    			}
	    			
	    		}
	    	}
	    	
	    	
		}
		
	}
	public void showDialogVoucher()
	{
		
		EditText editPassword;
		final EditText editUsername;
		TextView txtPassword,txtUsername,formTitle;
		Button btnCheck;
		final AlertDialog dialog = new AlertDialog.Builder(mcontext).create();
		dialog.setTitle("Check valid voucher");
		
		
		View v = getLayoutInflater().inflate(R.layout.login, null);
		
		dialog.setView(v);
		
		editPassword = (EditText) v.findViewById(R.id.password);
		editUsername = (EditText) v.findViewById(R.id.userName);
		
		txtPassword = (TextView) v.findViewById(R.id.txtPassword) ;
		txtUsername = (TextView) v.findViewById(R.id.txtUserName) ;
		formTitle = (TextView) v.findViewById(R.id.formtitle);
 		btnCheck = (Button) v.findViewById(R.id.login);
		
 		editPassword.setVisibility(View.GONE);
 		txtPassword.setVisibility(View.GONE);
 		formTitle .setVisibility(View.GONE);
 		
 		txtUsername.setText("Voucher Id");
 		
 		btnCheck.setText("Check Voucher Id");
		
 		btnCheck.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				String str = editUsername.getText().toString();
				if(str.equals(""))
				{
					showDialog("Please enter voucher number");
				}
				else
				{
					new checkValidVoucherNumber().execute(str);
				}
				
				
			}
		});
		dialog.show();
	}
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		final AlertDialog dialog = new AlertDialog.Builder(mcontext).create();
		dialog.setTitle("Check valid voucher");
		
		
		dialog.setMessage(str);
		dialog.setButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
	}
	

	public void onDestroy()
	{
		super.onDestroy();
		timer.cancel();
		unregisterReceiver(mReceiver);
		
	}
	@SuppressWarnings("deprecation")
	public void onBackPressed()
	{
		
		final AlertDialog dialog = new AlertDialog.Builder(mcontext).create();
		dialog.setTitle("Notification");
		
		dialog.setMessage("Do you want close application");
		dialog.setButton2("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
				
				Intent intent = new Intent(mcontext, LoginScreen.class);
				intent.putExtra("trigger", "operatorDeclaration");
				startActivity(intent);
				
			}
		});
		dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		dialog.show();
		
	}
	public void showManualDiscount(final int productId, final BigDecimal totalPrice, BigDecimal discountedPrice)
	{
		
		final AlertDialog dialogDiscount = new AlertDialog.Builder(mcontext).create();
		dialogDiscount.setTitle("Manual Discount");
		
		
		LayoutInflater inflater = (LayoutInflater) mcontext.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View discountview = inflater.inflate( R.layout.manualdiscount, null );
		
		final EditText totalAmount = (EditText) discountview.findViewById(R.id.totalAmount);
		final EditText discountedAmount = (EditText) discountview.findViewById(R.id.discountableAmount);
		final EditText txtValue = (EditText) discountview.findViewById(R.id.txtValue);
		RadioGroup rg = (RadioGroup) discountview.findViewById(R.id.radiotype);
		 final String value = ((RadioButton)discountview.findViewById(rg.getCheckedRadioButtonId() )).getText().toString();
	    Button btnDone = (Button) discountview.findViewById(R.id.discountdone);
	    Button btnCancel = (Button) discountview.findViewById(R.id.discountcancel);
	    
	    totalAmount.setText(totalPrice.toString());
	    discountedAmount.setText("0");
	    
	    isDisTypePerc = true;
	    
	
		
	    txtValue.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
				if(s.toString().equals(""))
					txtValue.setText("0");
				else
				{
					String strValue =txtValue.getText().toString();
		        	if(!isDisTypePerc)
		        	{
		        		Log.e("Amount", "Amount");
		        		
		        		BigDecimal temp;
		        		temp = totalPrice.subtract(BigDecimal.valueOf(Double.parseDouble(strValue)));
		    	    	discountedAmount.setText(temp.toString());
		    	    	
			        	}
		        	else
		        	{
		        		Log.e("Rate", "Rate");
		        		
		        		BigDecimal temp;
		        		
		        		if(productId == -1){
		        			temp = calculateAmtAfterMDiscount(null, totalPrice, BigDecimal.valueOf(Double.parseDouble(strValue)), PosConstants.MDPercentage);
		        		}else{
		        			temp = calculateAmtAfterMDiscount(selectedProducts.get(productId), totalPrice, BigDecimal.valueOf(Double.parseDouble(strValue)), PosConstants.MDPercentage);
		        		}
		        		
		        		discountedAmount.setText(temp.toString());
		    	    	

		        	}
				}
			}
		});
	    
	    rg.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        public void onCheckedChanged(RadioGroup group, int checkedId) {

	        	String strValue =txtValue.getText().toString();
        		
        		
        		 String value = ((RadioButton)group.findViewById(group.getCheckedRadioButtonId() )).getText().toString();
        		
        		 if(!strValue.toString().equals("")){
		        	if(value.equals("Amount"))
		        	{
		        		isDisTypePerc = false;
		        		
		        		BigDecimal temp = totalPrice.subtract(BigDecimal.valueOf(Double.parseDouble(strValue)));
		    	    	discountedAmount.setText(temp.toString());
		    	    	
		        	
		        	}
		        	else
		        	{
		        		isDisTypePerc = true;
		        		BigDecimal temp;
		        		
		        		if(productId == -1){
		        			temp = calculateAmtAfterMDiscount(null, totalPrice, BigDecimal.valueOf(Double.parseDouble(strValue)), PosConstants.MDPercentage);
		        		}else{
		        			temp = calculateAmtAfterMDiscount(selectedProducts.get(productId), totalPrice, BigDecimal.valueOf(Double.parseDouble(strValue)), PosConstants.MDPercentage);
		        		}
		        		
		        		discountedAmount.setText(temp.toString());
		        	}
        		 }
	        }
	    });
	    
	    btnDone.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialogDiscount.dismiss();
				final String str = txtValue.getText().toString();
				
				if(!str.equals("") || !str.equals("0"))
				{
					BigDecimal tempbd=new BigDecimal(discountedAmount.getText().toString());
					if(tempbd.compareTo(BigDecimal.valueOf(0))>0)
					{
						if(isDisTypePerc)							
							chkDiscOnTotalTrans(PosConstants.MDPercentage, productId, totalPrice, str);
						else
							chkDiscOnTotalTrans(PosConstants.MDAmount, productId, totalPrice, str);
					}else{
						final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
		    			
		    			String title=getResources().getString(R.string.pos);
		    			alertdialog.setTitle(title);
		    			alertdialog.setMessage("Discount amount should not be less then invoice amount");
		    			alertdialog.setPositiveButton("OK", null);
		    			alertdialog.show();
					}
				}
				
			
			}
		});
	    
		btnCancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialogDiscount.dismiss();
				
			}
		});
		dialogDiscount.setView(discountview);
		dialogDiscount.show();
	}
	
	public void chkDiscOnTotalTrans(final int mDType, final int productId, final BigDecimal totalPrice, final String str){
		if(isMDscAppliedOnTrans){
			if(productId == -1){
				applyManualDiscount(mDType, productId, totalPrice, str, 1);
			}else{
				AlertDialog.Builder alertdialog=new AlertDialog.Builder(TransactionActivity.this);
				
				String title=getResources().getString(R.string.pos);
				alertdialog.setTitle(title);
				alertdialog.setMessage("Previous discount is applied on total transaction. Applying this discount will reset M-Discount on all products.");
				alertdialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						applyManualDiscount(mDType, productId, totalPrice, str, 1);
					}
				});
				
				alertdialog.setNegativeButton("Cancel", null);
				alertdialog.show();
			}
		}else{
			applyManualDiscount(mDType, productId, totalPrice, str, 0);
		}
	}
	
	public void applyManualDiscount(int mDType, int productId, BigDecimal totalPrice, String str, int flag){
		
		if(flag == 1){
			for(int i = 0; i<selectedProducts.size(); i++){
				ProductReal product = selectedProducts.get(i);
				product.setMDType(mDType);
				product.setMdiscount(BigDecimal.valueOf(0));
				product.setDiscountedValue(product.getTotalPrice().subtract(product.getDiscount()));
			}
		}
		
		totalMDiscType = mDType;
		
		if(mDType == PosConstants.MDPercentage){
			if(productId == -1){
				isMDscAppliedOnTrans = true;
				totalTransMdiscount = BigDecimal.valueOf(Double.parseDouble(str));
			}else
				isMDscAppliedOnTrans = false;
			
			
			setManualDiscount(PosConstants.MDPercentage, productId, BigDecimal.valueOf(0), BigDecimal.valueOf(Double.parseDouble(str)));
		}else{
			if(productId == -1){
				isMDscAppliedOnTrans = true;
				totalTransMdiscount = BigDecimal.valueOf(Double.parseDouble(str));
			}else
				isMDscAppliedOnTrans = false;
			
			setManualDiscount(PosConstants.MDAmount, productId, totalPrice, BigDecimal.valueOf(Double.parseDouble(str)));
		}
	}
	
	/*public class SignOff extends AsyncTask<String, String, String>
	{
		String status="";
		@Override
	    protected void onPreExecute() {
	    
			super.onPreExecute();
	    	dialog=new ProgressDialog(mcontext);
	    	
	    	dialog.setMessage("Please wait for Sign out ");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(signOffUrl);
	       
	        try
	        {
	        
	        int userId =loginPref.getInt("UserId", -1);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         nameValuePairs.add(new BasicNameValuePair("UserId",Integer.toString(userId)));
	        
	         nameValuePairs.add(new BasicNameValuePair("status","Sign Off"));
	         nameValuePairs.add(new BasicNameValuePair("Pos_ID",Integer.toString(1)));
	         
	         
	         requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		    
		     String xml = EntityUtils.toString(entity);
		     
		     Log.e("Sign off", xml);
		     XMLParser parser = new XMLParser();
		     Document doc =parser.getDomElement(xml);
		     
		     NodeList nl = doc.getElementsByTagName("LoginDetails");
		     
			     for(int i=0;i<nl.getLength();i++)
			     {
			    	 Element e =(Element)nl.item(i);
			    	 
			    	status = parser.getValue(e, "UserStatus");
			     }
	        }
	        catch(Exception e)
	        {
	        	
	        	
	        }
			
			return null;
		}
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	
	    	if(status.equals("0"))
	    	{
	    		
	    		SharedPreferences.Editor edit1 = loginPref.edit();
				edit1.putString("UserName", null);
				edit1.putString("Password", null);
				edit1.putString("UserId", null);
				edit1.putString("UserRollId", null);
				
				edit1.commit();
	    		
				new SignOffUser(mcontext).new SignOff().execute();
	    		
	    	}
	    	else
	    	{
	    		
	    			final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
	    			
	    			String title=getResources().getString(R.string.pos);
	    			alertdialog.setTitle(title);
	    			alertdialog.setMessage("You are not sign off. Please try again ");
	    			alertdialog.setPositiveButton("OK", null);
	    			alertdialog.show();
	    		
	    		
	    	}
	    	
		}
	}*/
	
}
