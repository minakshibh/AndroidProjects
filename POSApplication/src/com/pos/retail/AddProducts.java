package com.pos.retail;


import java.math.BigDecimal;
import java.math.RoundingMode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ProductReal;
import com.pos.util.ScreenReceiver;

public class AddProducts extends Activity
{
	Button findItem,done,back,scanItem;
	Context mcontext;
	EditText scanTxt,editQuntity;
	String naira;
	boolean isDialogCancel = true;
	static ProductReal product;
	TextView totalTxt,descriptionTxt,itemCountTxt,discountTxt,vatTxt,priceTxt,productId;
	int flag = 0;
	int position;
	int itemSelected=-1;
	BigDecimal vat, discount, pricePerUnit, totalPrice;	
	int Quantity;
	  BroadcastReceiver mReceiver = new ScreenReceiver();
	  long curentTime=0;
	  long elapTime = 0;
	  SharedPreferences loginPref;
	  
	  TextView dividerMDisc, txtMDisc;
	  LinearLayout layoutMDisc;
	  boolean isEditing, isMDiscAppliedOnTrans;
	 BigDecimal totalValue;
	 
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addproduct);
		
		PosConstants.activity_code =3;
		 mcontext = this;
		findItem=(Button)findViewById(R.id.findItem);
		done=(Button)findViewById(R.id.done);
		scanTxt=(EditText)findViewById(R.id.barcodeTxt);
		findItem.setOnClickListener(Listener);
		done.setOnClickListener(Listener);
		naira=getResources().getString(R.string.naira);
		
		loginPref=getSharedPreferences("LoginPref", MODE_PRIVATE);
		
		dividerMDisc = (TextView)findViewById(R.id.dividerMDiscount);
		txtMDisc = (TextView)findViewById(R.id.m_discount);
		layoutMDisc = (LinearLayout)findViewById(R.id.layoutMDiscount);
		
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
      
        registerReceiver(mReceiver, filter);
		
		 isDialogCancel = true;
		scanItem=(Button)findViewById(R.id.scan);
		back=(Button)findViewById(R.id.back);
		
		productId=(TextView)findViewById(R.id.productId);
		
		totalTxt=(TextView)findViewById(R.id.total);
		descriptionTxt=(TextView)findViewById(R.id.description);
		discountTxt=(TextView)findViewById(R.id.discount);
		vatTxt=(TextView)findViewById(R.id.vat);
		priceTxt=(TextView)findViewById(R.id.price);
		
		
		editQuntity=(EditText)findViewById(R.id.Qunatity);
		editQuntity.setImeOptions(EditorInfo.IME_ACTION_DONE);
		scanTxt.addTextChangedListener(scanTextChange);
		editQuntity.addTextChangedListener(quantityChangeListener);
		
		scanItem.setOnClickListener(Listener);
		back.setOnClickListener(Listener);
		
		Intent in=getIntent();
		product = (ProductReal)in.getSerializableExtra("product");
		itemSelected=in.getIntExtra("ItemPosition", -1);
		isMDiscAppliedOnTrans = in.getBooleanExtra("isMDiscAppliedOnTrans", false);
		
		if(itemSelected!=-1){
			isEditing = true;
		}else{
			isEditing = false;
		}
		
	}
	
	public void refreshView(){
		Log.e("refresh 2","2222222...."+product.getSelectedQuatity());
		
		if(editQuntity.getText().toString().trim().equals("")){
			flag = 1;
			editQuntity.setText(""+product.getSelectedQuatity());
		}
		
		if(scanTxt.getText().toString().trim().equals("")){
			flag = 1;
			scanTxt.setText(product.getBarCode());
		}
		
		int quantity = product.getSelectedQuatity();
		
		vat=product.getSingleItemTax().multiply(new BigDecimal(quantity));
		discount=product.getSingleItemDiscount().multiply(new BigDecimal(quantity));
		totalPrice=product.getPrice().multiply(new BigDecimal(quantity));
		product.setTotalPrice(totalPrice);
		
		vat=vat.setScale(2, RoundingMode.HALF_DOWN);
		discount=discount.setScale(2, RoundingMode.HALF_DOWN);
		totalPrice=totalPrice.setScale(2, RoundingMode.HALF_DOWN);
		pricePerUnit = product.getPrice();

		discountTxt.setText(naira+" "+discount.toString());
		vatTxt.setText(naira+" "+vat.toString());
		
		totalValue = totalPrice.subtract(discount);
		totalTxt.setText(naira+" "+totalValue.toString());
		
		descriptionTxt.setText(product.getProductName());
		productId.setText(""+product.getPid());
		priceTxt.setText(""+product.getPrice());
		
		if(isEditing){
			layoutMDisc.setVisibility(View.VISIBLE);
			dividerMDisc.setVisibility(View.VISIBLE);
			
		}
		
		if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))==1){
			if(product.getMDType()==PosConstants.MDPercentage){
				txtMDisc.setText(""+product.getMdiscount()+" %");
				
				discountTxt.setText(naira +" 0");
				totalValue = product.getTotalPrice().multiply(product.getMdiscount());
				totalValue = totalValue.divide(BigDecimal.valueOf(100), 2);
				totalValue = product.getTotalPrice().subtract(totalValue);
				
				totalTxt.setText(naira +" "+totalValue);
			}else{
				if(isMDiscAppliedOnTrans){
					txtMDisc.setText(naira + " 0");
				}else{
					txtMDisc.setText(naira +" "+product.getMdiscount());
					totalValue = product.getTotalPrice().subtract(product.getMdiscount());
					totalTxt.setText(totalValue.toString());
				}
			}
		}else{
			txtMDisc.setText(""+product.getMdiscount()+" %");
		}
		isDialogCancel=false;
	}
	
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId())
			{
				case R.id.findItem:
					Intent in=new Intent(mcontext,ProductList.class);
					
					startActivity(in);
					break;
				case R.id.scan:
					IntentIntegrator scanIntegrator = new IntentIntegrator((Activity)mcontext);
					scanIntegrator.initiateScan();
					break;
				case R.id.done:
					
					
					if(descriptionTxt.getText().equals("") )
					{
						Toast.makeText(mcontext, "Please select the item", 1000).show();
					}
					
					else if(editQuntity.getText().toString().trim().equals("") || Integer.parseInt(editQuntity.getText().toString().trim())<=0)
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(AddProducts.this);
						alert.setTitle("Invalid Product");
						alert.setMessage("Quantity should always be greater than 0");
						alert.setPositiveButton("Ok", null);
						alert.show();
						
					}else if(totalValue.compareTo(BigDecimal.valueOf(0))<0){
						AlertDialog.Builder alert = new AlertDialog.Builder(AddProducts.this);
						alert.setTitle("Invalid M-Discount");
						alert.setMessage("M-Discount cannot be greater than total value. Continue to reset M-Discount.");
						alert.setNegativeButton("Cancel", null);
						alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								product.setMdiscount(BigDecimal.valueOf(0));
								refreshView();
							}
						});
						alert.show();
					}
					else
					{
						String totalCalculation=totalTxt.getText().toString();
						String st[] = totalCalculation.split(" ");
						
						SharedPreferences pref=getSharedPreferences("mypref", MODE_PRIVATE);
						SharedPreferences.Editor edit=pref.edit();
						edit.putString("ProductId", productId.getText().toString());
						edit.putString("Quantity", editQuntity.getText().toString());
						edit.putString("desc", descriptionTxt.getText().toString());
						edit.putString("vat", vat.toString());
						edit.putString("discount", discount.toString());
						edit.putString("price",pricePerUnit.toString());
						edit.putString("total", totalPrice.toPlainString());
						edit.putString("itemId", product.getItemId());
						edit.putString("barcode", product.getBarCode());
						edit.putInt("totalQuantity", product.getTotalQuantity());
						edit.putInt("ItemSelected", itemSelected);
						edit.putString("singleItemDiscount", product.getSingleItemDiscount().toString());
						edit.putString("singleItemTax", product.getSingleItemTax().toString());
						edit.putString("Mdiscount",product.getMdiscount().toString());
						edit.putInt("MdType", product.getMDType());
						edit.commit();
						
						product = null;
						 finish();
					}
					break;
				case R.id.back:
						finish();
						break;
			}
		}
	};
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null){
			if(scanningResult.getContents() != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			
			scanTxt.setText(scanContent);
			
			}
		}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
	}
	
	TextWatcher scanTextChange=new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(flag == 0){
				String str=s.toString();
				
				
				if(!str.equals(""))
				{
					PoSDatabase db=new PoSDatabase(mcontext);
					
					product=db.getProductByBarCode(str);
					
					if(product!=null)
					{
						
						if( editQuntity.getText().toString().equals(""))
						{
							editQuntity.setText("1");
							product.setSelectedQuatity(1);
						}
						else
						{
							
							String quantity = editQuntity.getText().toString();
							product.setSelectedQuatity(Integer.parseInt(quantity));
							Log.e("refresh 3","333333");
							
								refreshView();
							
						}
					}
					else
					{
						totalTxt.setText("");
						descriptionTxt.setText("");
						discountTxt.setText("");
						vatTxt.setText("");
						priceTxt.setText("");
						productId.setText("");
					}
				}
			}else
				flag =0;
		}
	};
	
	TextWatcher quantityChangeListener=new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(flag == 0){
				String str=s.toString();
				
				if(!str.equals("") )
				{
					int quantity = Integer.parseInt(editQuntity.getText().toString());
					
					System.out.println(""+quantity);
					if(quantity>product.getTotalQuantity() && isDialogCancel)
					{
						AlertDialog.Builder alert = new AlertDialog.Builder(AddProducts.this);
						alert.setTitle("Quantity Exceeded");
					
						alert.setMessage("Only "+product.getTotalQuantity()+" "+product.getProductName()+" available.");
						alert.setPositiveButton("OK", new DialogInterface.OnClickListener(
								) {
							
							public void onClick(DialogInterface dialog, int which) {
								editQuntity.setText(String.valueOf(product.getTotalQuantity()));
								
								isDialogCancel=true;
								
							}
						});
						alert.show();
						
						isDialogCancel=false;
					}else{
						String strQuntity=editQuntity.getText().toString().trim();
						product.setSelectedQuatity(Integer.parseInt(strQuntity));
						
						
						Log.e("refresh 4","444444");
						if(flag == 0)
							refreshView();
						else
							flag = 0;
					}
				}
				else
				{
					if(product!=null)
					{
						totalTxt.setText("0");
						discountTxt.setText(naira +" "+product.getDiscount().toString());
						vatTxt.setText(naira + " "+product.getTax().toString());
					}				
				}
				
			}else
				flag = 0;
		}
	};

	public void resetFields()
	{
		
		descriptionTxt.setText("");
		vatTxt.setText("");
		productId.setText("");
		priceTxt.setText("");
		discountTxt.setText("");
		scanTxt.setText("");
	
		totalTxt.setText("");
		
		
		
	}
	@Override
	  public void onConfigurationChanged (Configuration newConfig)
	{
	    super.onConfigurationChanged(newConfig);
	}
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
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
		
		if(ScreenReceiver.wasScreenOn || PosConstants.time_out)
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
		try{
		if(product!= null & !descriptionTxt.getText().equals(product.getProductName())){
			editQuntity.setText("");
			Log.e("refresh 1","11111111");
			refreshView();
		}}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		product = null;
		unregisterReceiver(mReceiver);
	}
}
