package com.print;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.posapplication.R;
import com.pos.retail.BreakActivity;
import com.pos.retail.TransactionActivity;
import com.pos.util.Currency;
import com.pos.util.CurrencyType;
import com.pos.util.PoSDatabase;
import com.pos.util.PosConstants;
import com.pos.util.ScreenReceiver;

public class DenominatationTest extends Activity{

	Button floatCash,okayBtn,cancelBtn,calculateBtn,coin,notes,rolls,bundles,cancel;
	
	LinearLayout coinslayout,noteslayout, rollsLayout, bundleLayout;
	TextView txtGrandTotall,txtTotall,selected;
	EditText editHalf,editOne,editTwo;
	
	EditText txtNotesFive, txtNotesTen, txtNotesTwenty, txtNotesFifty, txtNotesHundred, txtNotesTwoHundred, txtNotesFiveHundred, txtNotesThousand;
	EditText txtRollsFive, txtRollsTen, txtRollsTwenty, txtRollsFifty, txtRollsHundred, txtRollsTwoHundred, txtRollsFiveHundred, txtRollsThousand;
	EditText txtBundlesFive, txtBundlesTen, txtBundlesTwenty, txtBundlesFifty, txtBundlesHundred, txtBundlesTwoHundred, txtBundlesFiveHundred, txtBundlesThousand;
	
	
	LinearLayout llayout;
	
	private ArrayList<Currency> currencyListNaira,currListNairaCoin,currListNairaNote;
	
	private ArrayList<Currency> currencyListEuro,currListEuroCoin,currListEuroNote;
	private ArrayList<Currency> currencyListDollar,currListDollarCoin,currListDollarNote;
	
	
	double coinTotal = 0, notesTotal = 0, rollsTotal = 0, bundleTotal = 0; 
	
	int COIN_FLAG = 0, NOTESFLAG = 1, ROLLSFLAG = 2, BUNDLE_FLAG = 3;
	
	int flag = COIN_FLAG;
	
	int coinId[]={R.drawable.halfrs,R.drawable.oners,R.drawable.twors };
	
	Integer noteId[]={R.drawable.fivers,R.drawable.tenrs,R.drawable.twentyrs,R.drawable.fiftyrs,R.drawable.hundredrs,
			R.drawable.twohundredrs,R.drawable.fivehundredrs,R.drawable.thousandrs};
	SharedPreferences loginPref;
	
	boolean isCoinselected=false,isNoteSelected=false,isRollsSelected=false,isBundleSelected=false;
	
	
	private static final String One_Cent="one_cent";
	private static final String Two_Cent="two_cent";
	private static final String Five_Cent="five_cent";
	private static final String Ten_Cent="ten_cent";
	private static final String Twenty_Cent="twenty_cent";
	private static final String TwentyFive_Cent="twentyfive_cent";
	private static final String Fifty_Cent="fifty_cent";
	
	
	
	private static final String coin_Half="half";
	private static final String coin_One="one";
	private static final String coin_Two="two";
	
	
	
	private static final String note_five="note_five";
	private static final String note_ten="note_ten";
	private static final String note_twenty="note_twenty";
	private static final String note_fifty="note_fifty";
	private static final String note_hundred="note_hundred";
	private static final String note_twohundred="note_twohundred";
	private static final String note_fivehundred="note_fivehundred";
	private static final String note_thousand="note_thousand";
	
	
	HashMap<String, Long> map=new HashMap<String, Long>();
	HashMap<Double, String> mapValue=new HashMap<Double, String>();
	
	Context mcontext;
	String type;
	 long curentTime=0;
	  long elapTime = 0;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.denominatationtest);
		
		mcontext = this;
		
		
		coinslayout = (LinearLayout)findViewById(R.id.coinlayout);
		noteslayout = (LinearLayout)findViewById(R.id.noteslayout);
		rollsLayout = (LinearLayout)findViewById(R.id.rollslayout);
		bundleLayout = (LinearLayout)findViewById(R.id.bundleslayout);
		
		currencyListNaira = new ArrayList<Currency>();
		currListNairaCoin = new ArrayList<Currency>();
		currListNairaNote = new ArrayList<Currency>();
		
		currencyListDollar =new ArrayList<Currency>();
		currListDollarCoin = new ArrayList<Currency>();
		currListDollarNote = new ArrayList<Currency>();
		
		currencyListEuro =new ArrayList<Currency>();
		currListEuroCoin =  new ArrayList<Currency>();
		currListEuroNote = new ArrayList<Currency>();
		
		
		
		Intent in= getIntent();
		
		type=  in.getStringExtra("Type");
		
		init(type);
		if(type.equals("Naira"))
		{
			
			addCurrencyToCoinLayout(1, currListNairaCoin);
			
			Log.e("count ",""+currListNairaCoin.size()+""+currListNairaNote.size());
			addCurrencyToNoteLayout(1, currListNairaNote, noteslayout);
			addCurrencyToNoteLayout(100, currListNairaNote, rollsLayout);
			addCurrencyToNoteLayout(1000, currListNairaNote, bundleLayout);
			
		}
		else if(type.equals("Dollar"))
		{
			addCurrencyToCoinLayout(1, currListDollarCoin);
			addCurrencyToNoteLayout(1, currListDollarNote, noteslayout);
			addCurrencyToNoteLayout(100, currListDollarNote, rollsLayout);
			addCurrencyToNoteLayout(1000, currListDollarNote, bundleLayout);
			
		}
		else
		{
			addCurrencyToCoinLayout(1, currListEuroCoin);
			addCurrencyToNoteLayout(1, currListEuroNote, noteslayout);
			addCurrencyToNoteLayout(100, currListEuroNote, rollsLayout);
			addCurrencyToNoteLayout(1000, currListEuroNote, bundleLayout);
			
		}
		loginPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
		
	
		
		isCoinselected = true;
		calculateBtn = (Button)findViewById(R.id.calculate);
		
		calculateBtn.setOnClickListener(Listener);
		coin = (Button)findViewById(R.id.coin);
		notes = (Button)findViewById(R.id.notes);
		rolls = (Button)findViewById(R.id.rolls);
		bundles = (Button)findViewById(R.id.bundles);
		cancel = (Button)findViewById(R.id.cancel);
		okayBtn = (Button)findViewById(R.id.okay);
				
		txtGrandTotall = (TextView)findViewById(R.id.grandtotall);
		txtTotall = (TextView)findViewById(R.id.totall);
		
		
		coin.setOnClickListener(Listener);
		notes.setOnClickListener(Listener);
		rolls.setOnClickListener(Listener);
		bundles.setOnClickListener(Listener);
		
		cancel.setOnClickListener(Listener);
		okayBtn.setOnClickListener(Listener);
		
		selected = (TextView)findViewById(R.id.selectedItem);
		
		
	}
	public void addCurrencyToCoinLayout( int j,ArrayList<Currency> currList){
		
		coinslayout.removeAllViews();
		 noteslayout.removeAllViews();
    	 rollsLayout.removeAllViews();
    	 bundleLayout.removeAllViews();
		LayoutInflater inflator = getLayoutInflater();
		LinearLayout layout=null;
		j=3;
		
		for(int i = 0; i<currList.size(); i++){
			final int x =i;
			LinearLayout currencyLayout = (LinearLayout) inflator.inflate(R.layout.currency_node, null);
			final EditText txtQuantity = (EditText)currencyLayout.findViewById(R.id.txtQuantity);
			final ImageView imgCurrency = (ImageView)currencyLayout.findViewById(R.id.imgCurrency);
			final TextView txtId = (TextView) currencyLayout.findViewById(R.id.id);
			
			txtQuantity.addTextChangedListener(changeText);
			txtQuantity.setVisibility(View.VISIBLE);
			imgCurrency.setBackgroundResource(currList.get(i).imageId);	
			imgCurrency.setTag(String.valueOf(currList.get(i).value));
			
			imgCurrency.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					
					long a= StringToInt(txtQuantity.getText().toString());
					a++;
					txtQuantity.requestFocus();
					
					txtQuantity.setText(""+(a));
					setCalculation();
					
				}
			});
			
			
			
			txtId.setText(String.valueOf(currList.get(i).currencyId));
			
			Log.e("Tag ",""+ getTagValue(currList.get(i).value));
			
			if(i%j==0)
			{
			 layout =new LinearLayout(mcontext);
			
			 layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
             layout.setOrientation(LinearLayout.HORIZONTAL);
        
            
            	 coinslayout.addView(layout);
            
             
			}
			//To increas size of notes not coins
//			if(i>=3)
//            {
//           	 	LayoutParams lp = (LayoutParams) imgCurrency.getLayoutParams();
//			       lp.width =180;
//			       imgCurrency.setLayoutParams(lp);
//            }
			
			layout.addView(currencyLayout);
			
			
		}
		
		
		
	}
	TextWatcher changeText = new TextWatcher() {
		
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
			setCalculation();
			
		}
	};
	public void addCurrencyToNoteLayout( int multiple,ArrayList<Currency> currList,LinearLayout addlayout){
		
		
		LayoutInflater inflator = getLayoutInflater();
		LinearLayout layout=null;
		int j=3;
		
		for(int i = 0; i<currList.size(); i++){
			
			LinearLayout currencyLayout = (LinearLayout) inflator.inflate(R.layout.currency_node, null);
			final EditText txtQuantity = (EditText)currencyLayout.findViewById(R.id.txtQuantity);
			final ImageView imgCurrency = (ImageView)currencyLayout.findViewById(R.id.imgCurrency);
			final TextView txtId = (TextView) currencyLayout.findViewById(R.id.id);
			
			
			
			txtQuantity.addTextChangedListener(changeText);
			txtQuantity.setVisibility(View.VISIBLE);
			imgCurrency.setBackgroundResource(currList.get(i).imageId);	
			imgCurrency.setTag(String.valueOf((currList.get(i).value)* multiple ));
			
			
			
			imgCurrency.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					long a= StringToInt(txtQuantity.getText().toString());
					a++;
					txtQuantity.requestFocus();
					txtQuantity.setText(""+(a));
					setCalculation();
					
				}
			});
			
			
			
			txtId.setText(String.valueOf(currList.get(i).currencyId));
			
			Log.e("Tag ",""+ getTagValue(currList.get(i).value));
			if(i%j==0)
			{
			 layout =new LinearLayout(mcontext);
			
			 layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
             layout.setOrientation(LinearLayout.HORIZONTAL);
        
            
            	 addlayout.addView(layout);
            
             
			}
			//To increas size of notes not coins
			
           	 	LayoutParams lp = (LayoutParams) imgCurrency.getLayoutParams();
			       lp.width =180;
			       imgCurrency.setLayoutParams(lp);
           
			
			layout.addView(currencyLayout);
			
		}
		
		
	}
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				
				case R.id.calculate:
					
					setCalculation();
					
					break;
				case R.id.okay:
					
					setCalculation();
					
					String total = txtGrandTotall.getText().toString();
					
					BigDecimal bd= new BigDecimal(total);
					if(!(bd.compareTo(BigDecimal.valueOf(1000))==0))
					{
						showDialog("Grand total must be 1000");
						
//						txtGrandTotall.setText(""+0.0);
//						txtTotall.setText(""+0.0);
//						
					
					}
					else
					{
						
						addToDataBase(coinslayout,1);
						addToDataBase(noteslayout,1);
						addToDataBase(rollsLayout,100);
						addToDataBase(bundleLayout, 1000);
						
						 
						finish();
						
						Intent intent = new Intent(getApplicationContext(), TransactionActivity.class);
				    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				    	startActivity(intent);
						
						
					}
					break;
				case R.id.cancel:
					finish();
					break;
				case R.id.coin:
					coin.setBackgroundColor(getResources().getColor(R.color.app_base));
					rolls.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					bundles.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					notes.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					
					coinslayout.setVisibility(View.VISIBLE);
					noteslayout.setVisibility(View.GONE);
					rollsLayout.setVisibility(View.GONE);
					bundleLayout.setVisibility(View.GONE);
					
					
					txtTotall.setText(""+coinTotal);
					selected.setText("Coins");
					
					
					isCoinselected=true;
					
					break;
				case R.id.rolls:
					coin.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					rolls.setBackgroundColor(getResources().getColor(R.color.app_base));
					bundles.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					notes.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					
					coinslayout.setVisibility(View.GONE);
					noteslayout.setVisibility(View.GONE);
					rollsLayout.setVisibility(View.VISIBLE);
					bundleLayout.setVisibility(View.GONE);
					
					selected.setText("Rolls");
					
					
					txtTotall.setText(""+rollsTotal);
					
					isRollsSelected=true;
										
					break;
				case R.id.bundles:
					coin.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					rolls.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					bundles.setBackgroundColor(getResources().getColor(R.color.app_base));
					notes.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					
					coinslayout.setVisibility(View.GONE);
					noteslayout.setVisibility(View.GONE);
					rollsLayout.setVisibility(View.GONE);
					bundleLayout.setVisibility(View.VISIBLE);
					
					selected.setText("Bundles");
					
					txtTotall.setText(""+bundleTotal);
					
					isBundleSelected=true;
					break;
				case R.id.notes:
					coin.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					rolls.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					bundles.setBackgroundColor(getResources().getColor(R.color.translucent_app_base));
					notes.setBackgroundColor(getResources().getColor(R.color.app_base));
					
					coinslayout.setVisibility(View.GONE);
					noteslayout.setVisibility(View.VISIBLE);
					rollsLayout.setVisibility(View.GONE);
					bundleLayout.setVisibility(View.GONE);
					
					
					txtTotall.setText(""+notesTotal);
					
					selected.setText("Notes");
					
					isNoteSelected=true;
					break;
				
			}
			
		}
	};
	@SuppressLint("DefaultLocale")
	public void setCalculation()
	{
		
		String selectedItem=selected.getText().toString();
		double calculate =0;
		
		
		if(selectedItem.equals("Coins"))
		{
			
			calculate = calculation(coinslayout);
			
		}else if(selectedItem.equals("Notes"))
		{
			calculate = calculation(noteslayout);
		
			
		}else if(selectedItem.equals("Rolls"))
		{
			calculate = calculation(rollsLayout);
			
			
		}else
		{
			calculate = calculation(bundleLayout);
			
		}
		String strCalculate =String.format("%.2f", calculate);
		txtTotall.setText(strCalculate);
		
		coinTotal = calculation(coinslayout);
		notesTotal = calculation(noteslayout);
		rollsTotal = calculation(rollsLayout);
		bundleTotal = calculation(bundleLayout);
		
		double Gtotall=coinTotal + notesTotal + rollsTotal + bundleTotal;
		
		String str =String.format("%.2f", Gtotall);
		
		txtGrandTotall.setText(str);
	}
	
	public void addToDataBase (LinearLayout layout ,int multiple)
	{
		int userId = loginPref.getInt("UserId", 0);
		for(int i=0; i<layout.getChildCount();i++)
		{
			
			LinearLayout l =(LinearLayout) layout.getChildAt(i);
			
			for(int j=0; j<l.getChildCount();j++)
			{
				
				View view = l.getChildAt(j);
				TextView txtId = (TextView)view.findViewById(R.id.id);
				EditText editText = (EditText)view.findViewById(R.id.txtQuantity);
				
				String quantity = editText.getText().toString();
				    long quatity =  StringToInt(quantity);
				    long CalculateQuatity = quatity * multiple;
				    
				int currId = Integer.parseInt(txtId.getText().toString());
				if(CalculateQuatity >0)
				{
					PoSDatabase db = new PoSDatabase(mcontext);
							
					db.addDenominatationDetail(userId, currId, String.valueOf(CalculateQuatity));
				}
				
				
			}
			
		}
		
		
	}
	public double calculation(LinearLayout layout)
	{
		
		double count=0;
		
		
		
		for(int i=0;i<layout.getChildCount();i++)
		{
			
			
			LinearLayout firstChildLayout =(LinearLayout) layout.getChildAt(i);
			
			for(int j=0;j<firstChildLayout.getChildCount();j++)
			{
				
				View view = firstChildLayout.getChildAt(j);
				ImageView image = (ImageView)view.findViewById(R.id.imgCurrency);
				EditText editTxt = (EditText) view.findViewById(R.id.txtQuantity);
				
				
				long a= StringToInt(editTxt.getText().toString());
				
				double tempValue = Double.parseDouble(image.getTag().toString());
				count += a * tempValue;
			}
			
		}
		Log.e("value ", ""+count);
		return count;
		
	}
	public long  StringToInt(String str)
	{
		long a=0;
		if(!str.equals(""))
			 a= Long.parseLong(str);
			
		return a;
	}
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		
	final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
			
			String title=getResources().getString(R.string.pos);
			alertdialog.setTitle(title);
			alertdialog.setMessage(str);
			alertdialog.setButton("OK", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
				}
			});
			alertdialog.show();
	}
	public void init(String str)
	{
		
		if(str.equals("Naira"))
		{
			currencyListNaira.add(new Currency(0.5d, R.drawable.halfrs, CurrencyType.Coin,1,0,"Naira"));
			currencyListNaira.add(new Currency(1, R.drawable.oners, CurrencyType.Coin,2,0,"Naira"));
			currencyListNaira.add(new Currency(2, R.drawable.twors, CurrencyType.Coin,3,0,"Naira"));
			currencyListNaira.add(new Currency(5, R.drawable.fivers, CurrencyType.Notes,4,1,"Naira"));
			currencyListNaira.add(new Currency(10, R.drawable.tenrs, CurrencyType.Notes,5,1,"Naira"));
			currencyListNaira.add(new Currency(50, R.drawable.fiftyrs, CurrencyType.Notes,6,1,"Naira"));
			currencyListNaira.add(new Currency(100, R.drawable.hundredrs, CurrencyType.Notes,7,1,"Naira"));
			currencyListNaira.add(new Currency(200, R.drawable.twohundredrs, CurrencyType.Notes,8,1,"Naira"));
			currencyListNaira.add(new Currency(500, R.drawable.fivehundredrs, CurrencyType.Notes,9,1,"Naira"));
			currencyListNaira.add(new Currency(1000, R.drawable.thousandrs, CurrencyType.Notes,10,1,"Naira"));
			
			
			for(int i=0;i<currencyListNaira.size();i++)
			{
				Currency currency= currencyListNaira.get(i);
				if(currency.type == CurrencyType.Coin)
				{
					currListNairaCoin.add(currency);
					PoSDatabase db = new PoSDatabase(mcontext);
					db.addFloatCash(currency);
					
				}
				else
				{
					currListNairaNote.add(currency);
					PoSDatabase db = new PoSDatabase(mcontext);
					db.addFloatCash(currency);
				}
			}
		}
		else if(str.equals("Dollar"))
		{
		currencyListDollar.add(new Currency(0.01d, R.drawable.one_cent, CurrencyType.Coin,11,0,"Dollar"));
		currencyListDollar.add(new Currency(0.05d, R.drawable.five_cent, CurrencyType.Coin,12,0,"Dollar"));
		currencyListDollar.add(new Currency(0.10d, R.drawable.ten_cent, CurrencyType.Coin,13,0,"Dollar"));
		currencyListDollar.add(new Currency(0.25d, R.drawable.twentyfive_cent, CurrencyType.Coin,14,0,"Dollar"));
		currencyListDollar.add(new Currency(0.50d, R.drawable.fifty_cent, CurrencyType.Coin,15,0,"Dollar"));
		currencyListDollar.add(new Currency(5, R.drawable.five_dollar, CurrencyType.Notes,16,1,"Dollar"));
		currencyListDollar.add(new Currency(10, R.drawable.ten_dollar, CurrencyType.Notes,17,1,"Dollar"));
		currencyListDollar.add(new Currency(20, R.drawable.twenty_dollar, CurrencyType.Notes,18,1,"Dollar"));
		currencyListDollar.add(new Currency(50, R.drawable.fifty_dollar, CurrencyType.Notes,19,1,"Dollar"));
		currencyListDollar.add(new Currency(100, R.drawable.hundred_dollar, CurrencyType.Notes,20,1,"Dollar"));
		
		
		for(int i=0;i<currencyListDollar.size();i++)
		{
			Currency currency= currencyListDollar.get(i);
			if(currency.type == CurrencyType.Coin)
			{
				currListDollarCoin.add(currency);
				PoSDatabase db = new PoSDatabase(mcontext);
				db.addFloatCash(currency);
			}
			else
			{
				currListDollarNote.add(currency);
				PoSDatabase db = new PoSDatabase(mcontext);
				db.addFloatCash(currency);
			}
		}
		
		}
		else
		{
		
			currencyListEuro.add(new Currency(0.01d, R.drawable.one_cent_euro, CurrencyType.Coin,13,0,"Euro"));
			currencyListEuro.add(new Currency(0.02d, R.drawable.two_cent_euro, CurrencyType.Coin,14,0,"Euro"));
			currencyListEuro.add(new Currency(0.05d, R.drawable.five_cent_euro, CurrencyType.Coin,15,0,"Euro"));
			currencyListEuro.add(new Currency(0.10d, R.drawable.ten_cent_euro, CurrencyType.Coin,16,0,"Euro"));
			currencyListEuro.add(new Currency(0.20d, R.drawable.twenty_cent_euro, CurrencyType.Coin,17,0,"Euro"));
			currencyListEuro.add(new Currency(0.50d, R.drawable.fifty_cent_euro, CurrencyType.Coin,18,0,"Euro"));
			currencyListEuro.add(new Currency(1, R.drawable.one_euro, CurrencyType.Notes,19,1,"Euro"));
			currencyListEuro.add(new Currency(2, R.drawable.two_euro, CurrencyType.Notes,20,1,"Euro"));
			currencyListEuro.add(new Currency(5, R.drawable.five_euro, CurrencyType.Notes,21,1,"Euro"));
			currencyListEuro.add(new Currency(10, R.drawable.ten_euro, CurrencyType.Notes,22,1,"Euro"));
			currencyListEuro.add(new Currency(20, R.drawable.twenty_euro, CurrencyType.Notes,23,1,"Euro"));
			currencyListEuro.add(new Currency(50, R.drawable.fifty_euro, CurrencyType.Notes,24,1,"Euro"));
			currencyListEuro.add(new Currency(100, R.drawable.hundred_euro, CurrencyType.Notes,25,1,"Euro"));
			currencyListEuro.add(new Currency(200, R.drawable.twohundred_euro, CurrencyType.Notes,26,1,"Euro"));
			currencyListEuro.add(new Currency(500, R.drawable.fivehundred_euro, CurrencyType.Notes,27,1,"Euro"));
			
			
			for(int i=0;i<currencyListEuro.size();i++)
			{
				Currency currency= currencyListEuro.get(i);
				if(currency.type == CurrencyType.Coin)
				{
					currListEuroCoin.add(currency);
					PoSDatabase db = new PoSDatabase(mcontext);
					db.addFloatCash(currency);
				}
				else
				{
					currListEuroNote.add(currency);
					PoSDatabase db = new PoSDatabase(mcontext);
					db.addFloatCash(currency);
				}
			}
		}
		
	}
	public String getTagValue(double value)
	{
		System.out.println(value);
		String str= mapValue.get((Double)value);
		
		return str;
		
		
	}
	public void initialize()
	{
		long a=0;
		map.put(One_Cent, a);
		map.put(Two_Cent, a);
		map.put(Five_Cent, a);
		
		map.put(Ten_Cent, a);
		map.put(Twenty_Cent, a);
		map.put(TwentyFive_Cent, a);
		map.put(Fifty_Cent, a);
	
		map.put(coin_One, a);
		map.put(coin_Two, a);
		
		
		map.put(note_five, a);
		map.put(note_ten, a);
		map.put(note_twenty, a);
		
		map.put(note_fifty, a);
		map.put(note_hundred, a);
		map.put(note_twohundred, a);
		map.put(note_fivehundred, a);
		
		map.put(note_thousand, a);
		
		
		mapValue.put(0.01d,One_Cent);
		mapValue.put(0.02d, Two_Cent);
		mapValue.put(0.05d, Five_Cent);
		mapValue.put(0.10d, Ten_Cent);
		mapValue.put(0.20d, Twenty_Cent);
		mapValue.put(0.25d, TwentyFive_Cent);
		mapValue.put(0.50d, Fifty_Cent);
		
		mapValue.put((double) 1.0, coin_One);
		mapValue.put((double) 2.0, coin_Two);
		
		mapValue.put((double)5, note_five);
		mapValue.put((double)10, note_ten);
		
		mapValue.put((double)20, note_twenty);
		mapValue.put((double)50, note_fifty);
		mapValue.put((double)100, note_hundred);
		mapValue.put((double)500, note_fivehundred);
		mapValue.put((double)1000, note_fivehundred);
		
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

