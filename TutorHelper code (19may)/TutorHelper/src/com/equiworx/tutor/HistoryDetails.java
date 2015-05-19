package com.equiworx.tutor;

import com.equiworx.tutorhelper.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryDetails extends Activity{
	private ImageView back;
	private TextView tv_feescollect,tv_feedue,tv_outbal;
	private ListView listview;
	private LinearLayout lay_invoice,lay_addcredit,lay_payment,lay_statement;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_details);
		
		intializelayout();
		fetchHistoryDetails();
		setOnClickListners();
}
	
	private void intializelayout() {
	back=(ImageView)findViewById(R.id.back);
	listview=(ListView)findViewById(R.id.listview);
	tv_feescollect=(TextView)findViewById(R.id.fee_collected);
	tv_feedue=(TextView)findViewById(R.id.fee_due);
	tv_outbal=(TextView)findViewById(R.id.outstng_bal);
	lay_invoice=(LinearLayout)findViewById(R.id.invoice);
	lay_payment=(LinearLayout)findViewById(R.id.payment);
	lay_addcredit=(LinearLayout)findViewById(R.id.addcredit);
	lay_statement=(LinearLayout)findViewById(R.id.statement);
	}
	
	private void setOnClickListners() {
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				finish();
				
			}
		});
		lay_invoice.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				}
			});
		lay_addcredit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
					
				}
			});
		lay_payment.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
					
				}
			});
		lay_statement.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					
					
				}
			});
	}
	private void fetchHistoryDetails() {
	
		
	}
}