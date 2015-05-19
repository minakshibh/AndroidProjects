package com.pos.retail;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.posapplication.R;
import com.pos.util.CurrencySetting;
import com.pos.util.PerformBackgroundTask;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ScreenReceiver;
import com.pos.util.SignOffUser;
import com.pos.util.Transaction;
import com.pos.util.SignOffUser.SignOff;
import com.print.DenominatationTest;

public class FloatCashActivity extends Activity{

	Button btnTransaction,btnReturnItem,btnSignOff,btnPayCash,btnFloatCash,btnOperatorDeclaration, btnDeclare, btnGetReceipt;
	ImageButton menu;
	TextView txtGrandTotall,txtTotall;
	LinearLayout menuLayout,transactionForm,floatbtnLayout;
	Context mcontext;
	SharedPreferences loginPref;
	Button floatCashNaira,floatCashDollar,floatCashEuro,btnVoidReceipt;
	String trigger=null;
	EditText editFloatAmount,editTotallSale,editExpectedAmount,editCashValue,editdifferncevalue;
	TextView title;
	
	SharedPreferences itempref;
	SharedPreferences.Editor edit;
	String floatCashStr = "floatCash", naira;
	String operatorDeclarationStr = "operatorDeclaration";
	BigDecimal expectedAmount, cashValue, totallDifference,totalSale;
	Button btnExchangeItem,btnCancelItem,btnBreak;
	BigDecimal floatAmount;
	 long curentTime=0;
	  long elapTime = 0;
	  SharedPreferences transactionPrefs;
	  
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.floatcash_activity);
		
		mcontext=this;
		
		loginPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
		menuLayout=(LinearLayout)findViewById(R.id.llayout);
		transactionForm=(LinearLayout)findViewById(R.id.transactionForm);
		title = (TextView)findViewById(R.id.title);
		
		transactionForm.setOnClickListener(Listener);
		floatCashNaira = (Button)findViewById(R.id.floatbtnNaira);
		floatCashNaira.setOnClickListener(Listener);
		
		floatbtnLayout = (LinearLayout) findViewById(R.id.floatbtnLayout);
		
		floatCashDollar = (Button)findViewById(R.id.floatbtnDollar);
		floatCashDollar.setOnClickListener(Listener);
		
		floatCashEuro = (Button)findViewById(R.id.floatbtnEuro);
		btnGetReceipt = (Button) findViewById(R.id.getReceipt);
		
		floatCashEuro.setOnClickListener(Listener);
		
		trigger = getIntent().getStringExtra("trigger");
		
		
		
		
		
		if(trigger == null)
		{	
			trigger = floatCashStr;	
			transactionForm.setVisibility(View.GONE);		
			title.setText("Float Cash");
			floatbtnLayout.setVisibility(View.VISIBLE);
			
		}else{
			trigger = operatorDeclarationStr;
			transactionForm.setVisibility(View.VISIBLE);
			floatbtnLayout.setVisibility(View.GONE);
			title.setText("Operator Declaration");
		}
		
		menu=(ImageButton)findViewById(R.id.menu);
		menu.setVisibility(View.GONE);
		
		editFloatAmount=(EditText)findViewById(R.id.editFloatAmount);
		editTotallSale=(EditText)findViewById(R.id.editTotallSale);
		btnDeclare = (Button)findViewById(R.id.declare);
		
		 int userId = loginPref.getInt("UserId", 0);
		 
		 ////commented by Minakshi /////
		/* PoSDatabase db = new PoSDatabase(mcontext);
		 ArrayList<CurrencySetting> listCurrency =db.getAllCurrencySetting();
			
		ArrayList<Transaction> transactionList = db.getTransactionByUser(userId);
		
		Log.e("Transaction list", ""+transactionList.size());
		totalSale = BigDecimal.valueOf(0);
		
		for(int i=0;i<transactionList.size();i++)
		{
			
			BigDecimal tempbg=new BigDecimal(transactionList.get(i).getTotal());
			totalSale = totalSale.add(tempbg);
			
		}*/
		transactionPrefs = FloatCashActivity.this.getSharedPreferences("transPref", MODE_PRIVATE);
	  
		String transAmountStr = transactionPrefs.getString("transAmount", "0");
		totalSale = BigDecimal.valueOf(Double.parseDouble(transAmountStr));
		
		floatAmount = BigDecimal.valueOf(1000);
		editFloatAmount.setText(floatAmount.toString());
		editTotallSale.setText(totalSale.toString());
		
		editCashValue=(EditText)findViewById(R.id.editCashValue);
		editExpectedAmount=(EditText)findViewById(R.id.editExpectedAmount);
		
		editdifferncevalue=(EditText)findViewById(R.id.editdifferncevalue);
		
		naira=getResources().getString(R.string.naira);
		
		editExpectedAmount.setText(naira + " "+totalSale.add(floatAmount).toString());
		
		
		
		
		btnTransaction=(Button)findViewById(R.id.Trasaction);
//		btnFindItem=(Button)findViewById(R.id.findItem);
		btnReturnItem =(Button)findViewById(R.id.ReturnItem);
		btnSignOff=(Button)findViewById(R.id.signOff);
		btnFloatCash=(Button)findViewById(R.id.floatcashbtn);
		btnOperatorDeclaration=(Button)findViewById(R.id.operatorDeclaration);
		btnExchangeItem = (Button) findViewById(R.id.itemExchange);
		btnCancelItem = (Button) findViewById(R.id.itemCancel);
		btnVoidReceipt = (Button) findViewById(R.id.voidReceipt);
		
		btnBreak = (Button) findViewById(R.id.btnbreak);
		
		if(loginPref.getInt("UserRollId", 1)>1)
			btnOperatorDeclaration.setVisibility(View.VISIBLE);
		else
			btnOperatorDeclaration.setVisibility(View.GONE);
		
		btnFloatCash.setVisibility(View.VISIBLE);
		
		btnTransaction.setOnClickListener(Listener);
//		btnFindItem.setOnClickListener(Listener);
		btnReturnItem.setOnClickListener(Listener);
		btnSignOff.setOnClickListener(Listener);
		btnFloatCash.setOnClickListener(Listener);
		btnOperatorDeclaration.setOnClickListener(Listener);
		btnDeclare.setOnClickListener(Listener);
		btnExchangeItem.setOnClickListener(Listener);
		btnCancelItem.setOnClickListener(Listener);
		btnVoidReceipt.setOnClickListener(Listener);
		btnBreak.setOnClickListener(Listener);
		btnGetReceipt.setOnClickListener(Listener);
		
		itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		
		edit = itempref.edit();
		
		edit.clear();
		
		
			
		
		editCashValue.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub				
			}
			
			public void afterTextChanged(Editable s) {
				if(!s.toString().trim().equals("")){
					expectedAmount = totalSale.add(floatAmount);
					cashValue = BigDecimal.valueOf(Double.valueOf(editCashValue.getText().toString()));
					totallDifference=expectedAmount.subtract(cashValue);
							
					editdifferncevalue.setText(String.valueOf(totallDifference));
				}else{
					editdifferncevalue.setText("");
				}
			}
		});
	}
	
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent;
			switch (v.getId()) 
			{
				
				case R.id.floatbtnNaira:
					menuLayout.setVisibility(View.GONE);
					
					intent=new Intent(mcontext,DenominatationTest.class);
					intent.putExtra("Type", "Naira");
					startActivity(intent);
					
					break;
				case R.id.floatbtnDollar:
					menuLayout.setVisibility(View.GONE);
					
					intent=new Intent(mcontext,DenominatationTest.class);
					intent.putExtra("Type", "Dollar");

					startActivity(intent);
					
					
					break;
				case R.id.floatbtnEuro:
					menuLayout.setVisibility(View.GONE);
					
					intent=new Intent(mcontext,DenominatationTest.class);
					intent.putExtra("Type", "Euro");

					startActivity(intent);
					
					
					break;
				case R.id.declare:
					
					if(editdifferncevalue.getText().toString().equals("")){
						showDialog("Please enter cash value");
					}else{
						BigDecimal difference =  BigDecimal.valueOf(Double.valueOf(editdifferncevalue.getText().toString()));
						
						if(difference.compareTo(BigDecimal.valueOf(0)) == 1){
							showDialog("There is a difference of "+" ₦ "+String.valueOf(difference));
						}else if(difference.compareTo(BigDecimal.valueOf(0)) == -1){
							showDialog("Invalid cash entered by cashier");
						}else{
							showDialog("Perfect! :)");
						}
					}
					
					break;
			}
		}
	};
	
	//Show alert dialog 
		public void showDialog(final String str)
		{
			final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
			
			String title=getResources().getString(R.string.pos);
			alertdialog.setTitle(title);
			alertdialog.setMessage(str);
			alertdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
					if(str.equals("Perfect! :)"))
					{
						/* PoSDatabase db =new PoSDatabase(mcontext);
						 int userId = loginPref.getInt("UserId", 0);
							db.deleteTransactionByUser(userId);*/
//							
							new SignOffUser(mcontext, expectedAmount.toString()).new SignOff().execute();
//							
					}
				}
			});
			
			alertdialog.show();
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
					Intent in = new Intent(mcontext,BreakActivity.class);
					startActivity(in);
				}
				Log.e("", ""+(elapTime));
			} 
		}
		
		public void onBackPressed()
		{
			if(trigger.equalsIgnoreCase(floatCashStr)){
				menuLayout.setVisibility(View.GONE);
				final AlertDialog.Builder dialog = new AlertDialog.Builder(mcontext);
				dialog.setTitle("Notification");
				
				dialog.setMessage("Do you want close application");
				dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
						dialog.dismiss();
						
						Intent intent = new Intent(mcontext, LoginScreen.class);
						intent.putExtra("trigger", "operatorDeclaration");
						startActivity(intent);
						
					}
				});
				dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}
				});
				dialog.show();
			}else
				finish();
		}
}