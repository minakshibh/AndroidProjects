package com.pos.retail;

import java.util.HashMap;

import com.example.posapplication.R;
import com.pos.util.PoSDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Denomination extends Activity{

	Button floatCash,okayBtn,cancelBtn,calculateBtn,coin,notes,rolls,bundles,cancel;
	
	LinearLayout coinslayout,noteslayout, rollsLayout, bundleLayout;
	TextView txtGrandTotall,txtTotall,selected;
	EditText editHalf,editOne,editTwo;
	
	EditText txtNotesFive, txtNotesTen, txtNotesTwenty, txtNotesFifty, txtNotesHundred, txtNotesTwoHundred, txtNotesFiveHundred, txtNotesThousand;
	EditText txtRollsFive, txtRollsTen, txtRollsTwenty, txtRollsFifty, txtRollsHundred, txtRollsTwoHundred, txtRollsFiveHundred, txtRollsThousand;
	EditText txtBundlesFive, txtBundlesTen, txtBundlesTwenty, txtBundlesFifty, txtBundlesHundred, txtBundlesTwoHundred, txtBundlesFiveHundred, txtBundlesThousand;
	
	
	LinearLayout llayout;
	
	
	private static final String coin_Half="half";
	private static final String coin_One="one";
	private static final String coin_two="two";
	
	private static final String note_five="note_five";
	private static final String note_ten="note_ten";
	private static final String note_twenty="note_twenty";
	private static final String note_fifty="note_fifty";
	private static final String note_hundred="note_hundred";
	private static final String note_twohundred="note_twohundred";
	private static final String note_fivehundred="note_fivehundred";
	private static final String note_thousand="note_thousand";
	
	private static final String rolls_five="note_five";
	private static final String rolls_ten="note_ten";
	private static final String rolls_twenty="note_twenty";
	private static final String rolls_fifty="note_fifty";
	private static final String rolls_hundred="note_hundred";
	private static final String rolls_twohundred="note_twohundred";
	private static final String rolls_fivehundred="note_fivehundred";
	private static final String rolls_thousand="note_thousand";
	
	private static final String bundle_five="note_five";
	private static final String bundle_ten="note_ten";
	private static final String bundle_twenty="note_twenty";
	private static final String bundle_fifty="note_fifty";
	private static final String bundle_hundred="note_hundred";
	private static final String bundle_twohundred="note_twohundred";
	private static final String bundle_fivehundred="note_fivehundred";
	private static final String bundle_thousand="note_thousand";
	
	double coinTotal = 0, notesTotal = 0, rollsTotal = 0, bundleTotal = 0; 
	
	int COIN_FLAG = 0, NOTESFLAG = 1, ROLLSFLAG = 2, BUNDLE_FLAG = 3;
	
	int flag = COIN_FLAG;
	
	int coinId[]={R.drawable.halfrs,R.drawable.oners,R.drawable.twors };
	
	Integer noteId[]={R.drawable.fivers,R.drawable.tenrs,R.drawable.twentyrs,R.drawable.fiftyrs,R.drawable.hundredrs,
			R.drawable.twohundredrs,R.drawable.fivehundredrs,R.drawable.thousandrs};
	SharedPreferences loginPref;
	
	boolean isCoinselected=false,isNoteSelected=false,isRollsSelected=false,isBundleSelected=false;
	HashMap<String, Long> map=new HashMap<String, Long>();
	Context mcontext;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.denomination);
		
		mcontext = this;
		editHalf = (EditText)findViewById(R.id.half);
		editOne = (EditText)findViewById(R.id.one);
		
		editTwo = (EditText)findViewById(R.id.two);
		
		coinslayout = (LinearLayout)findViewById(R.id.coinlayout);
		noteslayout = (LinearLayout)findViewById(R.id.noteslayout);
		rollsLayout = (LinearLayout)findViewById(R.id.rollslayout);
		bundleLayout = (LinearLayout)findViewById(R.id.bundleslayout);
		
		
		txtNotesFive = (EditText)findViewById(R.id.notesfive);		
		txtNotesTen = (EditText)findViewById(R.id.notes_ten);
		txtNotesTwenty = (EditText)findViewById(R.id.notes_twenty);
		txtNotesFifty = (EditText)findViewById(R.id.notes_fifty);		
		txtNotesHundred = (EditText)findViewById(R.id.notes_hundred);
		txtNotesTwoHundred = (EditText)findViewById(R.id.notes_twohundred);
		txtNotesFiveHundred = (EditText)findViewById(R.id.notes_fivehundred);		
		txtNotesThousand = (EditText)findViewById(R.id.notes_thousand);
		
		txtRollsFive = (EditText)findViewById(R.id.rolls_five);		
		txtRollsTen = (EditText)findViewById(R.id.rolls_ten);
		txtRollsTwenty = (EditText)findViewById(R.id.rolls_twenty);
		txtRollsFifty = (EditText)findViewById(R.id.rolls_fifty);		
		txtRollsHundred = (EditText)findViewById(R.id.rolls_hundred);
		txtRollsTwoHundred = (EditText)findViewById(R.id.rolls_twohundred);
		txtRollsFiveHundred = (EditText)findViewById(R.id.rolls_fivehundred);		
		txtRollsThousand = (EditText)findViewById(R.id.rolls_thousand);
		
		txtBundlesFive = (EditText)findViewById(R.id.bundle_five);		
		txtBundlesTen = (EditText)findViewById(R.id.bundle_ten);
		txtBundlesTwenty = (EditText)findViewById(R.id.bundle_twenty);
		txtBundlesFifty = (EditText)findViewById(R.id.bundle_fifty);		
		txtBundlesHundred = (EditText)findViewById(R.id.bundle_hundred);
		txtBundlesTwoHundred = (EditText)findViewById(R.id.bundle_twohundred);
		txtBundlesFiveHundred = (EditText)findViewById(R.id.bundle_fivehundred);		
		txtBundlesThousand = (EditText)findViewById(R.id.bundle_thousand);
		
		loginPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
		
		init();
		
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
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				
				case R.id.calculate:
					
					
					calculation();
					
					break;
				case R.id.okay:
					
					calculation();
					String total = txtGrandTotall.getText().toString();
					int tvalue=(int)Float.parseFloat(total);
					
					if(tvalue!=1000)
					{
						showDialog("Grand total must be 1000");
						
						txtGrandTotall.setText(""+0.0);
						txtTotall.setText(""+0.0);
						
						init();
					}
					else
					{
						int userId = loginPref.getInt("UserId", 0);
						 
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
					
					coinTotal = CalculateValue("Coins");
					
					
					
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
					
					rollsTotal = CalculateValue("Rolls");
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
					bundleTotal = CalculateValue("Bundles");
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
					
					notesTotal = CalculateValue("Notes");
					txtTotall.setText(""+notesTotal);
					
					selected.setText("Notes");
					
					isNoteSelected=true;
					break;
			}
			
		}
	};
	
	
	
	public void calculation()
	{
		Log.e("lldfl", "lkdkdk");
		String selectedItem=selected.getText().toString();
		double value=CalculateValue(selectedItem);
		
		txtTotall.setText(""+value);
		
		coinTotal = CalculateValue("Coins");
		notesTotal = CalculateValue("Notes");
		rollsTotal = CalculateValue("Rolls");
		bundleTotal = CalculateValue("Bundles");
		
		double Gtotall=coinTotal + notesTotal + rollsTotal + bundleTotal;
		
		txtGrandTotall.setText(""+Gtotall);
		
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
	public double CalculateValue(String selected)
	{
		double count=0;
		if(selected.equalsIgnoreCase("Coins"))
		{
			
			if(!editHalf.getText().toString().equals(""))
			{
				
				long a=StringToInt(editHalf.getText().toString());
				count += (a/2.0);
				
				map.put(coin_Half, a);
				
			}
			if(!editOne.getText().toString().equals(""))
			{
				long a=StringToInt(editOne.getText().toString());
				count += a;
				
				map.put(coin_One, a);
				
			}
			if(!editTwo.getText().toString().equals(""))
			{
				
				long a = StringToInt(editTwo.getText().toString());
				
				count += 2*a;
				
				map.put(coin_two, a);
				
			}
			
			Log.e("fg", ""+count);
		}
		else if(selected.equalsIgnoreCase("Notes"))
		{
			if(!txtNotesFive.getText().toString().equals(""))
			{
				long a=StringToInt(txtNotesFive.getText().toString());
				count += (a)*5;
				
				map.put(note_five, a);
				
			}
			if(!txtNotesTen.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesTen.getText().toString());
				count += a*10;
				
				
				map.put(note_ten, a);
				
			}
			if(!txtNotesTwenty.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesTwenty.getText().toString());
				count += a*20;
				
				map.put(note_twenty, a);
				
			}
			if(!txtNotesFifty.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesFifty.getText().toString());
				
				count += a*50;
				
				map.put(note_fifty, a);
				
			}
			if(!txtNotesHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesHundred.getText().toString());
				
				count += a*100;
				
				map.put(note_hundred, a);
				
			}
			if(!txtNotesTwoHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesTwoHundred.getText().toString());
				count += a * 200;
				
				map.put(note_twohundred, a);
				
			}
			if(!txtNotesFiveHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesFiveHundred.getText().toString());
				count += a*500;
				
				
				map.put(note_fivehundred, a);
				
			}
			if(!txtNotesThousand.getText().toString().equals(""))
			{
				long a = StringToInt(txtNotesThousand.getText().toString());
				count += a*1000;
				
				map.put(note_thousand, a);
				
			}
			
			
		}
		else if(selected.equalsIgnoreCase("Rolls"))
		{
			if(!txtRollsFive.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsFive.getText().toString());
				
				count += (a)*5*100;
				map.put(note_five, ((a * 100)));
				
				
			}
			if(!txtRollsTen.getText().toString().equals(""))
			{
				
				long a = StringToInt(txtRollsTen.getText().toString());
				count += a*10*100;
				
				
				map.put(note_ten, ((a * 100)));
				
			}
			if(!txtRollsTwenty.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsTwenty.getText().toString());
				
				count += a*20*100;
				
				map.put(note_twenty, ((a * 100)));
				
			}
			if(!txtRollsFifty.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsFifty.getText().toString());
				
				count += a*50*100;
				
				map.put(note_fifty, ((a * 100)));
				
				
			}
			if(!txtRollsHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsHundred.getText().toString());
				count += a*100*100;
				
				
				map.put(note_hundred, ((a * 100)));
				
			}
			if(!txtRollsTwoHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsTwoHundred.getText().toString());
				
				count += a*200*100;
				
				
				map.put(note_twohundred, ((a * 100)));
				
			}
			if(!txtRollsFiveHundred.getText().toString().equals(""))
			{
				
				long a = StringToInt(txtRollsFiveHundred.getText().toString());
				count += a*500*100;
				
				
				map.put(note_fivehundred, ((a * 100)));
				
			}
			if(!txtRollsThousand.getText().toString().equals(""))
			{
				long a = StringToInt(txtRollsThousand.getText().toString());
				
				count += a*1000*100;
				
				map.put(note_thousand, ((a * 100)));
			
			}
			
			
		}
		else if(selected.equalsIgnoreCase("Bundles"))
		{
			if(!txtBundlesFive.getText().toString().equals(""))
			{
				
				long a = StringToInt(txtBundlesFive.getText().toString());
				count += (a)*5*1000;
				map.put(note_five, ((a * 1000)));
				
			}
			if(!txtBundlesTen.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesTen.getText().toString());
				count += a*10*1000;
				
				map.put(note_ten, ((a * 1000)));
			}
			if(!txtBundlesTwenty.getText().toString().equals(""))
			{
				
				long a = StringToInt(txtBundlesTwenty.getText().toString());
				count += a*20*1000;
				
				map.put(note_twenty, ((a * 1000)));
				
				
			}
			if(!txtBundlesFifty.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesFifty.getText().toString());
				
				count += a*50*1000;
				map.put(note_fifty, ((a * 1000)));
				
			}
			if(!txtBundlesHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesHundred.getText().toString());
				
				count += a*100*1000;
				map.put(note_hundred, ((a * 1000)));
				
			}
			if(!txtBundlesTwoHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesTwoHundred.getText().toString());
				
				count += a*200*1000;
				map.put(note_twohundred, ((a * 1000)));
				
			}
			if(!txtBundlesFiveHundred.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesFiveHundred.getText().toString());
				
				count += a*500*1000;
				
				map.put(note_fivehundred, ((a * 1000)));
				
			}
			if(!txtBundlesThousand.getText().toString().equals(""))
			{
				long a = StringToInt(txtBundlesThousand.getText().toString());
				
				count += a*1000*1000;
				
				map.put(note_thousand, ((a * 1000)));
				
			}
		}
		return count;
	}
	
	public long  StringToInt(String str)
	{
		
		long a= Long.parseLong(str);
		return a;
	}
	
	
	public void init()
	{
		long a=0;
		map.put(coin_Half, a);
		map.put(coin_One, a);
		map.put(coin_two, a);
		
		map.put(note_five, a);
		map.put(note_ten, a);
		map.put(note_twenty, a);
		
		map.put(note_fifty, a);
		map.put(note_hundred, a);
		map.put(note_twohundred, a);
		map.put(note_fivehundred, a);
		
		map.put(note_thousand, a);
		
	}
	
}
