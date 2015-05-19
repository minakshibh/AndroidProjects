package com.pos.retail;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.Reason;
import com.pos.util.ScreenReceiver;
import com.pos.util.ServiceConnector;
import com.pos.util.VoucherType;
import com.print.Utility;

public class ReturnDetailActivity extends Activity{
	
	private String date, time, branch, pos, employee, transId, subTotal, discount, vat, total;
	private String Mdiscount,MdiscountType,grandTotal;
	private TextView txtDate, txtTime, txtBranch, txtPOS, txtEmployee, 
			txtTransId, txtSubTotal, txtDisc, txtVAT, txtTotal, 
			txtReceiptHeaderTotal, txtReturnSubTotal, txtReturnDisc, txtReturnVat, 
			txtReturnTotal, txtReturnHeaderTotal,txtMDiscount,txtHeader, txtReceiptMDiscount, labelReturn;
	public boolean isPayWithDiscount;
	private LinearLayout receiptListLayout, returnListLayout,mdiscountLayout;
	private ArrayList<ProductReal> receiptList, returnList, remainingList;
	private Button selectReceipt, btnCashReturn, btnCardReturn, btnVoucherReturn,btnBack;
	private final int RETURN_CASH = 0, RETURN_CARD = 1, RETURN_VOUCHER = 2;
	LinearLayout itemLayout;
	int setReceiptSelection = -1, setReturnSelection = -1;
	private ProgressDialog progress;
	private RelativeLayout layoutReturn;
	BigDecimal returnTotal = BigDecimal.valueOf(0), returnSubTotal = BigDecimal.valueOf(0), returnVAT = BigDecimal.valueOf(0), returnDisc = BigDecimal.valueOf(0);
	ArrayList<Reason> reasonList;
	ArrayList<String> productNames;
	ArrayList<VoucherType> voucherTypes;
	int selection = -1;
	ArrayList<ProductReal> productReceiptList;
	HashMap<String, String> map ;
	SharedPreferences itempref;
	SharedPreferences pref;
	String itemReturnType, itemReturnCode;
	Context mcontext;
	int reasonId =1;
	String naira, newTransactionId;
	SharedPreferences loginPref;
	long curentTime=0;
	long elapTime = 0;
	BigDecimal BdMdiscount ;
	BigDecimal bd100;
	BigDecimal bdgrandTotal, returnMDiscount, rem_subTotal, rem_vat, rem_total, rem_mDiscount, rem_discount;
	BigDecimal pending_totalTax,pending_totalDiscount,pending_manualdiscount,pending_finalTotal;
	String strText ="";
	
	SharedPreferences checkRetrun;
	 Editor editing;
	 
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.return_detail_activity);
		
		
		checkRetrun=getSharedPreferences(Utility.MyPREFERENCES, Context.MODE_PRIVATE);
		editing=checkRetrun.edit();
		  
		  
		newTransactionId = String.valueOf(System.currentTimeMillis()/1000);
		reasonList = getIntent().getParcelableArrayListExtra("reasonList");
		
		receiptList = new ArrayList<ProductReal>();
		returnList = new ArrayList<ProductReal>();
		remainingList = new ArrayList<ProductReal>();
//		untouchedProductList = new ArrayList<ProductReal>();
		
		productNames = new ArrayList<String>();
		voucherTypes = new ArrayList<VoucherType>();
		
		naira =getResources().getString(R.string.naira);
		
		Intent intent = getIntent();
		isPayWithDiscount = intent.getBooleanExtra("isPayWithDiscount", false);
		productReceiptList = (ArrayList<ProductReal>)intent.getSerializableExtra("productlist");
		map = (HashMap<String, String>)intent.getSerializableExtra("map");
		
		loginPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
		mcontext = this;
		
		String str = map.get("InvoiceDate");
		
		String st[] = str.split("T");
		
		date =st[0];
		time = st[1];
		branch = map.get("ReturnStoreID");
		pos =  map.get("RetrunPosID");
		employee = map.get("CustomerId");
		transId =  map.get("Transaction_ID");
		grandTotal =  map.get("GrandTotal");
		discount =  map.get("Discount");
		vat =  map.get("Tax");
		total =  map.get("TotalAmount");
		Mdiscount = map.get("Mdiscount");
		MdiscountType = map.get("MdiscountType");
		
		Log.e("map",":::"+map.toString());
		if(branch == null || branch.equals("")){
			branch = "";
		} if (pos ==null || pos.equals("")){
			pos = "";
		} if(employee == null || employee.equals("")){
			employee ="";
		} if(transId == null || transId.equals("")){
			transId ="";
		} if(grandTotal ==null || grandTotal.equals("")){
			grandTotal = "0";
		} if(discount ==null || discount.equals("")){
			discount = "0";
		} if(vat ==null || vat.equals("")){
			vat = "0";
		} if(total == null || total.equals("")) {
			total = "0";
		} if(Mdiscount == null || Mdiscount.equals("")){
			Mdiscount = "0";
		} if(MdiscountType == null || MdiscountType.equals("")){
			MdiscountType = "-1";
		}
		
		
		Log.e(Mdiscount+""+Mdiscount, ""+MdiscountType+" "+total+" "+subTotal);
		employee = loginPref.getString("UserName", "");
		
		txtDate = (TextView)findViewById(R.id.txtDate);
		txtTime = (TextView)findViewById(R.id.txtTime);
		txtBranch = (TextView)findViewById(R.id.txtBranch);
		txtPOS = (TextView)findViewById(R.id.txtPOS);
		txtEmployee = (TextView)findViewById(R.id.txtEmployee);
		txtTransId = (TextView)findViewById(R.id.txtTransId);
		txtSubTotal = (TextView)findViewById(R.id.txtReceiptSubTotal);
		txtDisc = (TextView)findViewById(R.id.txtReceiptDisc);
		txtVAT = (TextView)findViewById(R.id.txtReceiptVAT);
		txtTotal = (TextView)findViewById(R.id.txtReceiptTotal);
		txtReceiptHeaderTotal = (TextView)findViewById(R.id.txtReceiptHeaderTotal);
		txtReturnSubTotal = (TextView)findViewById(R.id.txtReturnSubTotal);
		txtReturnDisc = (TextView)findViewById(R.id.txtReturnDisc);
		txtReturnVat = (TextView)findViewById(R.id.txtReturnVAT);
		txtReturnTotal = (TextView)findViewById(R.id.txtReturnTotal);
		txtReturnHeaderTotal = (TextView)findViewById(R.id.txtReturnHeaderTotal);
		txtMDiscount = (TextView) findViewById(R.id.txtMDiscount);
		txtReceiptMDiscount = (TextView) findViewById(R.id.txtReceiptMDiscount);
		txtHeader = (TextView) findViewById(R.id.txtHeader);
		labelReturn = (TextView) findViewById(R.id.labelReturn);
		
		receiptListLayout = (LinearLayout)findViewById(R.id.receiptList);
		returnListLayout = (LinearLayout)findViewById(R.id.returnList);
		selectReceipt = (Button)findViewById(R.id.btnSelectReceipt);
		btnCashReturn = (Button)findViewById(R.id.btnCashReturn);
		btnCardReturn = (Button)findViewById(R.id.btnCardReturn);
		btnVoucherReturn = (Button)findViewById(R.id.btnVoucherReturn);
		btnBack = (Button) findViewById(R.id.back);
		
		mdiscountLayout = (LinearLayout) findViewById(R.id.mdiscountLayout);
		layoutReturn = (RelativeLayout)findViewById(R.id.layoutReturn);
		
		btnCashReturn.setOnClickListener(returnMoneyListener);
		btnCardReturn.setOnClickListener(returnMoneyListener);
		btnVoucherReturn.setOnClickListener(returnMoneyListener);
		btnBack .setOnClickListener(Listener);
		
		txtDate.setText(date);
		txtTime.setText(time);
		txtBranch.setText(branch);
		txtPOS.setText(pos);
		txtEmployee.setText(employee);
		txtTransId.setText(newTransactionId);
		
		txtSubTotal.setText(naira +" "+total);
		txtDisc.setText(naira +" "+discount);
		txtVAT.setText(naira +" "+vat);
		txtTotal.setText(naira +" "+grandTotal);
		
		
		itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		
		itemReturnType = itempref.getString("ItemReturnType", "");
		itemReturnCode = itempref.getString("ItemReturnCode", "");
		
		
		txtHeader.setText(itemReturnType);
		if(itemReturnType.equals("Get Receipt") )
			selectReceipt.setText("Void Receipt");
		txtReceiptHeaderTotal.setText(subTotal);		
		
		if(MdiscountType.equals("0"))
		{
			txtReceiptMDiscount.setText(naira + " 0");
		}
		else {
			BigDecimal tempbd = new BigDecimal(Mdiscount);
			tempbd = tempbd.setScale(2);
			Mdiscount = tempbd.toString();
			txtReceiptMDiscount.setText(naira +" "+tempbd.toString());
		}
		
		bd100=BigDecimal.valueOf(100);
		Log.e("Type ", itemReturnType);
		if(itemReturnType.equals("Get Receipt") || itemReturnType.equals("Item Exchange") || itemReturnType.equals("Order Cancel") || itemReturnType.equals("Void Receipt"))
		{
			btnCardReturn.setVisibility(View.GONE);
			btnCashReturn.setVisibility(View.GONE);
		}
		
		for(int i=0;i<productReceiptList.size();i++)
		{
			ProductReal product = productReceiptList.get(i);
			receiptList.add(new ProductReal(product));
		
		}
		if(itemReturnType.equals("Get Receipt")){
			labelReturn.setVisibility(View.GONE);
			layoutReturn.setVisibility(View.GONE);
			returnListLayout.setVisibility(View.GONE);
			txtReturnTotal.setVisibility(View.GONE);
			txtReturnDisc.setVisibility(View.GONE);
			txtReturnVat.setVisibility(View.GONE);
			txtReturnSubTotal.setVisibility(View.GONE);
			txtMDiscount.setVisibility(View.GONE);
		}
		setLayout(true);
		selectReceipt.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				String str = selectReceipt.getText().toString();
				if(str.equals("Select Receipt"))
				{
					for(int i = 0; i<receiptList.size(); i++){
						ProductReal p = receiptList.get(i);
						
						p.setInitialDiscount(p.getDiscount());
						p.setInitialMDiscount(p.getMdiscount());
						p.setInitialTax(p.getTax());
						p.setInitialTotal(p.getDiscountedValue());
						
						if(productNames.contains(p.getProductName())){
							int index = productNames.indexOf(p.getProductName());
							returnList.remove(index);
							productNames.remove(index);						
						}
						productNames.add(p.getProductName());
						ProductReal tempProd = new ProductReal(p);
						tempProd.setInitialQuantity(p.getInitialQuantity());
						returnList.add(tempProd);
					}
					setLayout(false);		
					showReasonDialog(true, -1);
				}
				else
				{
					
					SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			    	String formattedDate = df.format(new Date());
					String invoiceDate = map.get("InvoiceDate");
			    	Log.e("invoice ", ""+invoiceDate);
			    	try
			    	{
			    	Date date1 = df.parse(invoiceDate);
			    	Date date2 = df.parse(formattedDate);
			    	
			    	Log.e("two date "+date1.toString(), date2.toString());
			    	Log.e("Compare ", ""+date1.compareTo(date2));
			    	
				    	if(date1.compareTo(date2)==0)
				    	{
							Intent in =new Intent(mcontext,VoidReceiptLogin.class);
							in.putExtra("data", "Get Receipt");
							startActivity(in);
				    	}
				    	else
				    	{
				    		
				    		AlertDialog.Builder builder =new AlertDialog.Builder(mcontext);
				    		builder.setTitle("Notification");
				    		builder.setMessage("The transaction is not of current date so you cannot void this receipt");
				    		builder.setPositiveButton("OK", null);
				    		builder.show();
				    	}
			    	}
			    	catch(Exception e)
			    	{
			    		e.printStackTrace();
			    	}
				}
			}
		});
		
	}
	
	View.OnClickListener returnMoneyListener = new View.OnClickListener() {	
		public void onClick(View v) {
			
			
			if(returnList.size()>0){
				if(v == btnCardReturn)
					returnMoney(RETURN_CARD);
				else if(v == btnCashReturn)
					returnMoney(RETURN_CASH);
				else
					returnMoney(RETURN_VOUCHER);
			}else{
				AlertDialog.Builder alert = new AlertDialog.Builder(ReturnDetailActivity.this);
				alert.setTitle("Return Cart Empty");
				alert.setMessage("Please select some items to be returned");
				alert.setPositiveButton("OK", null);
				alert.show();
			}
		}
	};
	public void onPause()
	{
		super.onPause();
		
		if(!ScreenReceiver.wasScreenOn)
			curentTime =System.currentTimeMillis();
		
	}
	@Override
	public void onResume(){
		super.onResume();
		
		if(ScreenReceiver.wasScreenOn || PosConstants.time_out)
		{
			
			elapTime = System.currentTimeMillis();
			
			elapTime = (elapTime-curentTime);
			if(elapTime>PosConstants.TIMEOUT_IN_MS && elapTime<System.currentTimeMillis() || PosConstants.time_out )
			{
				
				PosConstants.time_out = true;
				Intent in = new Intent(mcontext,BreakActivity.class);
				startActivity(in);
			}
			Log.e("", ""+(elapTime));
		}
		pref = getSharedPreferences("mypref", MODE_PRIVATE);
		if(pref.getString("ReceiptReturned", null) !=null)
		{
			
			pref.edit().clear().commit();
			
			Intent intent = new Intent(ReturnDetailActivity.this,TransactionActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			
		}else{
		
			strText =itempref.getString("key", "");
			
			if(!strText.equals(""))
			{
				
				selectReceipt.setText("Select Receipt");
				
				
				itempref.edit().putString("key","").commit();
			}
			if(selectReceipt.getText().toString().equals("Select Receipt"))
			{
				btnVoucherReturn.setVisibility(View.VISIBLE);
			}
			else
			{
				btnVoucherReturn.setVisibility(View.GONE);
			}
		}
	}
	public void returnMoney(int medium){
		
		
		
		
		remainingList.clear();
//		untouchedProductList.clear();
		
		rem_subTotal = BigDecimal.valueOf(0);
		rem_vat = BigDecimal.valueOf(0);
		rem_total = BigDecimal.valueOf(0);
		rem_mDiscount = BigDecimal.valueOf(0);
		rem_discount = BigDecimal.valueOf(0);
		
		pending_totalTax = BigDecimal.valueOf(0);
		pending_totalDiscount = BigDecimal.valueOf(0);
		pending_manualdiscount = BigDecimal.valueOf(0);
		pending_finalTotal = BigDecimal.valueOf(0);
		
		for (int y = 0; y<receiptList.size(); y++){
			ProductReal product = new ProductReal(receiptList.get(y));
			product.setInitialDiscount(product.getDiscount());
			product.setInitialMDiscount(product.getMdiscount());
			product.setInitialTax(product.getTax());
			product.setInitialTotal(product.getDiscountedValue());
			
			if(!productNames.contains(product.getProductName())){
				/*	if(product.getSelectedQuatity() != product.getInitialQuantity()){
					product.setReturnQunatity(product.getInitialReturnQuantity()+product.getSelectedQuatity());
//					product.setSelectedQuatity(product.getInitialQuantity()-product.getSelectedQuatity());
					
					remainingList.add(product);
					
					
					
					for(int x = 0; x<remainingList.size(); x++){
						
						rem_subTotal = rem_subTotal.add(remainingList.get(x).getPrice().multiply(BigDecimal.valueOf(remainingList.get(x).getSelectedQuatity())));
						
						rem_vat = rem_vat.add(remainingList.get(x).getSingleItemTax().multiply(BigDecimal.valueOf(remainingList.get(x).getSelectedQuatity())));
						rem_total = rem_total.add(remainingList.get(x).getDiscountedValue());
						
						if(remainingList.get(x).getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
							if(remainingList.get(x).getMDType() == PosConstants.MDAmount)
								rem_mDiscount = rem_mDiscount.add(remainingList.get(x).getMdiscount());
							else{
								BigDecimal temp = remainingList.get(x).getMdiscount().multiply(remainingList.get(x).getTotalPrice());
								temp = temp.divide(BigDecimal.valueOf(100), 2);
								rem_mDiscount = rem_mDiscount.add(temp);
							}
						}else{
							rem_discount = rem_discount.add(remainingList.get(x).getDiscount());
						}
					}
				}
			}else{*/
				remainingList.add(product);
				
				rem_subTotal = rem_subTotal.add(product.getPrice().multiply(BigDecimal.valueOf(product.getInitialQuantity())));
				
				rem_vat = rem_vat.add(product.getSingleItemTax().multiply(BigDecimal.valueOf(product.getInitialQuantity())));
				rem_total = rem_total.add(product.getDiscountedValue());
				
				if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
					if(product.getMDType() == PosConstants.MDAmount)
						rem_mDiscount = rem_mDiscount.add(product.getMdiscount());
					else{
						BigDecimal temp = product.getMdiscount().multiply(product.getTotalPrice());
						temp = temp.divide(BigDecimal.valueOf(100), 2);
						rem_mDiscount = rem_mDiscount.add(temp);
					}
				}else{
					rem_discount = rem_discount.add(product.getDiscount());
				}
			}
		}
		
		
		for(int i = 0; i<returnList.size(); i++){
			ProductReal product =  new ProductReal(returnList.get(i));
				pending_finalTotal = pending_finalTotal.add(product.getInitialTotal().subtract(product.getDiscountedValue()));
				pending_totalTax = pending_totalTax.add(product.getSingleItemTax().multiply(BigDecimal.valueOf(product.getInitialQuantity() - product.getSelectedQuatity())));
				
//				rem_total = rem_total.add(product.getDiscountedValue());
				
				if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
					if(product.getMDType() == PosConstants.MDAmount)
						pending_manualdiscount = pending_manualdiscount.add(product.getInitialMDiscount().subtract(product.getMdiscount()));
					else{
						BigDecimal totalPrice = product.getPrice().multiply(BigDecimal.valueOf(product.getInitialQuantity()-product.getSelectedQuatity()));
						
						BigDecimal temp = product.getMdiscount().multiply(totalPrice);
						temp = temp.divide(BigDecimal.valueOf(100), 2);
						pending_manualdiscount = pending_manualdiscount.add(temp);
					}
				}else{
					pending_totalDiscount = pending_totalDiscount.add(product.getInitialDiscount().subtract(product.getSingleItemDiscount().multiply(BigDecimal.valueOf(product.getSelectedQuatity()))));
				}
		}
		
		if(medium == RETURN_CASH)
		{
			String paymentMethod ="Cash";
			
			System.out.println(returnList.get(0).getSelectedQuatity()+" "+returnList.get(0).getInitialQuantity());
			
			for(int i=0;i<returnList.size();i++)
			{
				
				System.out.println(returnList.get(i).getPrice()+" "+returnList.get(i).getSingleItemDiscount()+" "+returnList.get(i).getSingleItemTax());
			}
			
			Log.i("tag", "Return Details");
			Intent in=new Intent(mcontext, PayCashActivity.class);
			in.putExtra("paycash", returnTotal.toString());
			in.putExtra("PaymentMethodType", paymentMethod);
			in.putExtra("returnTransactionId", transId);
			in.putExtra("newTransactionId", newTransactionId);
			in.putExtra("reasonId", reasonId);
			
			in.putExtra("totalTax", returnVAT.toString());
			in.putExtra("totalDiscount", returnDisc.toString());
			in.putExtra("manualdiscount", returnMDiscount.toString());
			in.putExtra("finalTotal", returnTotal.toString());
			
			in.putExtra("pending_totalTax", pending_totalTax.toString());
			in.putExtra("pending_totalDiscount", pending_totalDiscount.toString());
			in.putExtra("pending_manualdiscount", pending_manualdiscount.toString());
			in.putExtra("pending_finalTotal", pending_finalTotal.toString());
			
			in.putExtra("ret_subTotal", returnSubTotal.toString());
			in.putExtra("ret_total", returnTotal.toString());
			in.putExtra("ret_Tax", returnVAT.toString());
			in.putExtra("ret_disc", returnDisc.toString());
			in.putExtra("ret_MDisc", returnMDiscount.toString());
		
			in.putExtra("listofproduct", returnList);
			in.putExtra("rem_totalTax",rem_vat.toString() );
			in.putExtra("rem_totalDiscount", rem_discount.toString());
			in.putExtra("rem_totalMDiscount", rem_mDiscount.toString());
			in.putExtra("rem_listofproduct", remainingList);
			in.putExtra("rem_finalTotal", rem_total.toString());
			
			editing.putString(Utility.CashType,"Cash");
			editing.putString(Utility.ItemReturnBy, "Cash");
			editing.commit();
			   
			startActivity(in);
		}
		else if(medium == RETURN_CARD)
		{
			
			if(returnList.size()>0)
			{
				if (returnTotal.compareTo(BigDecimal.ZERO) > 0)
				{
					String paymentMethod = "Card";
					int paymenttype=0;
					
					Intent _in =new Intent(mcontext,PayPalClass.class);
					_in.putExtra("paycash", returnTotal.toString());
					_in.putExtra("TotalTax",returnVAT.toString());
					_in.putExtra("TotalDiscount",returnDisc.toString());
					_in.putExtra("TotalAmount",returnTotal.toString());
					_in.putExtra("paymentMethod",paymentMethod);
					_in.putExtra("paymenttype",paymenttype);
					_in.putExtra("strDiscount",returnMDiscount);
					_in.putExtra("itemSelected",returnList);
					_in.putExtra("finalTotal", returnTotal.toString());
					_in.putExtra("TotalAmount", total.toString());
					_in.putExtra("TransactionType","");
					_in.putExtra("trigger",0);
					_in.putExtra("ret_subTotal", returnSubTotal.toString());
					_in.putExtra("ret_total", returnTotal.toString());
					_in.putExtra("ret_Tax", returnVAT.toString());
					_in.putExtra("ret_disc", returnDisc.toString());
					_in.putExtra("ret_MDisc", returnMDiscount.toString());
					
					_in.putExtra("pending_totalTax", pending_totalTax.toString());
					_in.putExtra("pending_totalDiscount", pending_totalDiscount.toString());
					_in.putExtra("pending_manualdiscount", pending_manualdiscount.toString());
					_in.putExtra("pending_finalTotal", pending_finalTotal.toString());
					
					editing.putString(Utility.CashType,"Card");
					   editing.putString(Utility.ItemReturnBy, "Card");
					   editing.commit();
					   
					   
					startActivity(_in);
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
		else{
			   editing.putString(Utility.ItemReturnBy, "Voucher");
			   editing.commit();
			   
			new fetchVoucherTypes().execute();
			
		}
	}
	
	public void showDetail(final int pos, int quantity, final boolean isReceipt){
		final TextView txtTotal, txtItemId, txtDescription, txtDiscount, txtVAT, txtPrice, txtMDiscount;
		final EditText txtQuantity;
		Button btnSave, btnCancel;
		final ArrayList<ProductReal> templist;
		
		final Dialog alert = new Dialog(ReturnDetailActivity.this);
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(R.layout.item_detail_layout);
		
		LayoutParams params = alert.getWindow().getAttributes();
	    params.height = LayoutParams.WRAP_CONTENT;
	    params.width = LayoutParams.MATCH_PARENT;
	    alert.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
	        
	    txtTotal = (TextView)alert.findViewById(R.id.txtTotal);
	    txtItemId = (TextView)alert.findViewById(R.id.txtItemId);
	    txtDescription = (TextView)alert.findViewById(R.id.txtDiscription);
	    txtDiscount = (TextView)alert.findViewById(R.id.txtDiscount);
	    txtMDiscount = (TextView)alert.findViewById(R.id.txtMDiscount);
	    txtVAT = (TextView)alert.findViewById(R.id.txtVAT);
	    txtPrice = (TextView)alert.findViewById(R.id.txtPrice);
	    txtQuantity = (EditText)alert.findViewById(R.id.txtQuantity);
	    btnSave = (Button)alert.findViewById(R.id.btnSave);
	    btnCancel = (Button)alert.findViewById(R.id.btnCancel);
	    
	    if(isReceipt){
			templist = receiptList;
		}else{
			templist = returnList;
		}
	    
	    
	    txtQuantity.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				int quantity;
				if(!s.toString().trim().equals(""))
					quantity = Integer.parseInt(s.toString());
				else{
					quantity = 0;
				}
				
				 BigDecimal subTotal = templist.get(pos).getPrice().multiply(BigDecimal.valueOf(quantity));
				    BigDecimal totalDiscount = templist.get(pos).getSingleItemDiscount().multiply(BigDecimal.valueOf(quantity));
				    BigDecimal totalVAT = templist.get(pos).getSingleItemTax().multiply(BigDecimal.valueOf(quantity));
				    
				    txtItemId.setText(templist.get(pos).getItemId());
				    txtDescription.setText(templist.get(pos).getProductName());
				    txtVAT.setText(totalVAT.toString());
				    txtPrice.setText(templist.get(pos).getPrice().toString());
				    
				    BigDecimal mDiscount = templist.get(pos).getMdiscount();
				    if(mDiscount.compareTo(BigDecimal.valueOf(0))>0){
				    	totalDiscount = BigDecimal.valueOf(0);
				    	txtDiscount.setText(totalDiscount.toString());
				    	
				    	if(templist.get(pos).getMDType() == PosConstants.MDPercentage){
				    		mDiscount = mDiscount.multiply(subTotal);
				    		mDiscount = mDiscount.divide(BigDecimal.valueOf(100), 2);
				    	}else{
					    	mDiscount = mDiscount.multiply(BigDecimal.valueOf(quantity));
				    		mDiscount = mDiscount.divide(BigDecimal.valueOf(templist.get(pos).getSelectedQuatity()), 2);
				    	}
				    	
				    	txtMDiscount.setText(mDiscount.toString());
			    		txtTotal.setText(subTotal.subtract(mDiscount).toString());
			    		
				    }else{
				    	txtMDiscount.setText(mDiscount.toString());
					    txtDiscount.setText(totalDiscount.toString());
				    	txtTotal.setText(subTotal.subtract(totalDiscount).toString());
				    }
				    
			}
		});
	   
	   
	    txtQuantity.setText(String.valueOf(quantity));
	    
	    btnSave.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				
				Log.e("qunatity", txtQuantity.getText().toString());
				if(!txtQuantity.getText().toString().trim().equals("")){		
					int quantity = Integer.parseInt(txtQuantity.getText().toString());
					if(quantity<=0){
						Toast.makeText(ReturnDetailActivity.this, "Please enter valid quantity", Toast.LENGTH_LONG).show();
					}else{
						ProductReal p;				
						if(isReceipt){
							p =  new ProductReal(receiptList.get(pos));
							p.setInitialDiscount(p.getDiscount());
							p.setInitialMDiscount(p.getMdiscount());
							p.setInitialTax(p.getTax());
							p.setInitialTotal(p.getDiscountedValue());
							p.setInitialQuantity(p.getSelectedQuatity());
						}else{
							p = new ProductReal(returnList.get(pos));
						}
						
						if(quantity>p.getInitialQuantity()){
							Toast.makeText(ReturnDetailActivity.this, "Quantity cannot be greater than that mentioned in receipt", Toast.LENGTH_LONG).show();
						}else{
							p.setSelectedQuatity(quantity);
							p.setDiscountedValue(BigDecimal.valueOf(Double.parseDouble(txtTotal.getText().toString())));
							
							if(p.getMdiscount().compareTo(BigDecimal.valueOf(0))>0 && p.getMDType() == PosConstants.MDAmount){
								p.setMdiscount(BigDecimal.valueOf(Double.parseDouble(txtMDiscount.getText().toString())));
							}

							if(productNames.contains(p.getProductName())){
								int index = productNames.indexOf(p.getProductName());
								returnList.remove(index);
								productNames.remove(index);						
							}
							
							returnList.add(p);						
							productNames.add(p.getProductName());
								
							setLayout(false);
							alert.dismiss();
							if(isReceipt)
								showReasonDialog(false, returnList.size() -1);
						}
					}
				}
			}
		});
	    
	    btnCancel.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				alert.dismiss();	
			}
		});
	    
		alert.show();
	}
	
	
	public void setLayout(final Boolean isReceipt){
		LayoutInflater inflator = getLayoutInflater();
		final ArrayList<ProductReal> templist;
		TextView itemName, itemPrice;
		BigDecimal total = BigDecimal.valueOf(0);
		BigDecimal discount = BigDecimal.valueOf(0);
		BigDecimal vat = BigDecimal.valueOf(0);
		BigDecimal subTotal = BigDecimal.valueOf(0);
		BigDecimal mDiscount = BigDecimal.valueOf(0);
		
		if(isReceipt){
			receiptListLayout.removeAllViews();
			templist = receiptList;
		}else{
			returnListLayout.removeAllViews();
			templist = returnList;
		}
		
		System.out.println("Size "+templist.size());
		for(int i = 0; i<templist.size(); i++){
			final int x = i;
			itemLayout = (LinearLayout)inflator.inflate(R.layout.return_item_layout, null);
			itemName = (TextView)itemLayout.findViewById(R.id.txtName);
			itemPrice = (TextView)itemLayout.findViewById(R.id.txtPrice);
			
			itemName.setText(templist.get(i).getProductName());		
			
			subTotal = subTotal.add(templist.get(i).getPrice().multiply(BigDecimal.valueOf(templist.get(i).getSelectedQuatity())));
			
			vat = vat.add(templist.get(i).getSingleItemTax().multiply(BigDecimal.valueOf(templist.get(i).getSelectedQuatity())));
			total = total.add(templist.get(i).getDiscountedValue());
			
			if(templist.get(i).getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
				if(templist.get(i).getMDType() == PosConstants.MDAmount)
					mDiscount = mDiscount.add(templist.get(i).getMdiscount());
				else{
					BigDecimal temp = templist.get(i).getMdiscount().multiply(templist.get(i).getPrice().multiply(BigDecimal.valueOf(templist.get(i).getSelectedQuatity())));
					temp = temp.divide(BigDecimal.valueOf(100), 2);
					mDiscount = mDiscount.add(temp);
				}
			}else{
				discount = discount.add(templist.get(i).getSingleItemDiscount().multiply(BigDecimal.valueOf(templist.get(i).getSelectedQuatity())));
			}
			
			
			itemPrice.setText(naira +" "+templist.get(i).getDiscountedValue().toString());
			
			if(isReceipt){
				receiptListLayout.addView(itemLayout);
			}else{
				returnListLayout.addView(itemLayout);
			}
			if((setReceiptSelection == x && isReceipt) || (setReturnSelection == x && !isReceipt))
				itemLayout.setBackgroundColor(Color.parseColor("#E6E6E6"));				
			
			itemLayout.setOnClickListener(new View.OnClickListener() {				
				public void onClick(View v) {	
				
					if( itemReturnType.equals("Get Receipt") || itemReturnType.equals("Order Cancel") || itemReturnType.equals("Void Receipt"))
					{
						
					}
					else
						showDetail(x, templist.get(x).getSelectedQuatity(), isReceipt);
					setLayout(isReceipt);		
				}
			});
			
			returnTotal = total;
		}
		
		
		
		
		if(isReceipt){
			
			
			
			txtReceiptHeaderTotal.setText(naira +" "+grandTotal);
		}else{
			
			
			
			txtReturnHeaderTotal.setText(naira +" "+returnTotal.toString());			
			returnDisc = discount; 
			returnVAT = vat;
			returnSubTotal = subTotal;
			returnMDiscount = mDiscount;
			
			txtReturnTotal.setText(naira +" "+returnTotal.toString());
			txtReturnDisc.setText(naira +" "+returnDisc.toString());
			txtReturnVat.setText(naira +" "+returnVAT.toString());
			txtReturnSubTotal.setText(naira +" "+returnSubTotal.toString());
			txtMDiscount.setText(naira+" "+returnMDiscount.toString());
		}
	}
	
	public void showReasonDialog(final boolean isReceipt , final int pos){
		final Dialog alert = new Dialog(ReturnDetailActivity.this);
		
		
		final ListView listView = new ListView(ReturnDetailActivity.this);
		listView.setAdapter(new ReasonAdapter(ReturnDetailActivity.this, reasonList));
		
		alert.setContentView(listView);
		alert.setTitle("Select Reason...");
		alert.setCancelable(false);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				alert.dismiss();
				/*if(pos == -1){
					for(int i = 0; i<returnList.size(); i++){
						returnList.get(i).setRetunResonId(arg2+1);
					}
				}else
					returnList.get(pos).setRetunResonId(arg2+1);*/
				int reasonId = Integer.parseInt(reasonList.get(arg2).getId());
				returnList.get(pos).setRetunResonId(reasonId);
				
				Log.e("selected reasonId","::: "+reasonId);
			}
		});
		
		alert.show();
	}
	
	class fetchVoucherTypes extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... str) {
			if(voucherTypes.size()<1){
				ServiceConnector connector = new ServiceConnector(ReturnDetailActivity.this);
				voucherTypes = connector.getVoucherTypes();
			}
			return "success";
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(ReturnDetailActivity.this, "Loading Voucher types", "Please wait...");
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			progress.dismiss();
			
			if(voucherTypes.size()>0){
				Button btnOk, btnCancel;
				final Dialog alert = new Dialog(ReturnDetailActivity.this);
				alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				alert.setContentView(R.layout.list_dialog_layout);
				
				final ListView listView = (ListView)alert.findViewById(R.id.voucherList);
				btnOk = (Button)alert.findViewById(R.id.btnOk);
				btnCancel = (Button)alert.findViewById(R.id.btnCancel);
				
				final VoucherTypeAdapter adapter = new VoucherTypeAdapter(ReturnDetailActivity.this, voucherTypes);
				listView.setAdapter(adapter);				
				
				LayoutParams params = alert.getWindow().getAttributes();
			    params.height = LayoutParams.WRAP_CONTENT;
			    params.width = LayoutParams.MATCH_PARENT;
			    alert.getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
			    
				listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
							long arg3) {
							selection = arg2;
							listView.setAdapter(adapter);
	    			    }
				});
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
						if(selection>=0){
//							Toast.makeText(ReturnDetailActivity.this, "VoucherType: "+voucherTypes.get(selection).toString(), Toast.LENGTH_LONG).show();
							alert.dismiss();
							
							Intent intent = new Intent(ReturnDetailActivity.this, GenerateVoucher.class);
							intent.putExtra("voucherAmount", returnTotal.toString());
							intent.putExtra("voucherType", voucherTypes.get(selection).toString());
							intent.putExtra("voucherId", voucherTypes.get(selection).getId());
							intent.putExtra("TransId", transId);
							intent.putExtra("returnProductList", returnList);
							intent.putExtra("reasonId", reasonId);
							intent.putExtra("originalAmount", returnSubTotal.toString());
							intent.putExtra("totalVat", returnVAT.toString());
							intent.putExtra("mDiscount", returnMDiscount.toString());
							intent.putExtra("discount", returnDisc.toString());
							intent.putExtra("isPayWithDiscount",isPayWithDiscount);
							intent.putExtra("newTransactionId",newTransactionId);
							
							intent.putExtra("pending_totalTax", pending_totalTax.toString());
							intent.putExtra("pending_totalDiscount", pending_totalDiscount.toString());
							intent.putExtra("pending_manualdiscount", pending_manualdiscount.toString());
							intent.putExtra("pending_finalTotal", pending_finalTotal.toString());
							
							intent.putExtra("ret_subTotal", returnSubTotal.toString());
							intent.putExtra("ret_total", returnTotal.toString());
							intent.putExtra("ret_Tax", returnVAT.toString());
							intent.putExtra("ret_disc", returnDisc.toString());
							intent.putExtra("ret_MDisc", returnMDiscount.toString());
							
							intent.putExtra("rem_totalTax",rem_vat.toString() );
							intent.putExtra("rem_totalDiscount", rem_discount.toString());
							intent.putExtra("rem_totalMDiscount", rem_mDiscount.toString());
							intent.putExtra("rem_listofproduct", remainingList);
							intent.putExtra("rem_finalTotal", rem_total.toString());
							
							Log.e("isPaywithDiscount", ":::: "+isPayWithDiscount);
							startActivity(intent);							
						}
					}
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {					
					public void onClick(View v) {
						alert.dismiss();
					}
				});
				
				alert.show();
			}else{
				AlertDialog.Builder alert = new AlertDialog.Builder(ReturnDetailActivity.this);
				alert.setTitle("No voucher types available");
				alert.setMessage("Please try again later.");
				alert.setPositiveButton("OK", null);
				alert.show();
			}
		}
	}
	
public class ReasonAdapter extends BaseAdapter {
		
		private Context mContext;
		private ArrayList<Reason> mItems = new ArrayList<Reason>();
		
		
		public ReasonAdapter(Context context, ArrayList<Reason> items) {
		     mContext = context;
		     mItems = items;
		}
		
		public int getCount() {
		     return mItems.size();
		}
		
		public Object getItem(int position) {
		     return mItems .get(position);
		}
		
		public long getItemId(int position) {
		     return position;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;

	        if (view == null) {
	            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = layoutInflater.inflate(R.layout.listitem_layout, null);
	            
	            TextView txtView = (TextView)view;
	            txtView.setText(mItems.get(position).getDetailText());
	           
	        }
	        return view;
		}
	}

	public class VoucherTypeAdapter extends BaseAdapter {
		
		private Context mContext;
		private ArrayList<VoucherType> mItems = new ArrayList<VoucherType>();
		
		TextView content, author, date;
		ImageView commentType;
		String username;
		
		public VoucherTypeAdapter(Context context, ArrayList<VoucherType> items) {
		     mContext = context;
		     mItems = items;
		}
		
		public int getCount() {
		     return mItems.size();
		}
		
		public Object getItem(int position) {
		     return mItems .get(position);
		}
		
		public long getItemId(int position) {
		     return position;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = convertView;

	        if (view == null) {
	            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = layoutInflater.inflate(R.layout.listitem_layout, null);
	            
	            TextView txtView = (TextView)view;
	            txtView.setText(mItems.get(position).toString());
	            
	            if(selection == position){
	            	view.setBackgroundColor(getResources().getColor(R.color.list_selection_color));
	            }
	      
	            
	        }
	        return view;
		}
	}
	OnClickListener Listener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
			case R.id.back:
				finish();
				break;

			
			}
		}
	};
	@SuppressLint("SimpleDateFormat")
	public void WebServiceCallExample11()
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