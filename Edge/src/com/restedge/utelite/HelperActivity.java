package com.restedge.utelite;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.restedge.utelite.R;

public class HelperActivity extends Activity{

	
	Button btnContinue,btnBack;
	String EmailSubject,receiptId;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.helpmenu);
		btnContinue=(Button)findViewById(R.id.sendemail);
		btnBack=(Button)findViewById(R.id.btnBack);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				finish();
			}
		});
		
		
		EmailSubject=getResources().getString(R.string.EmailSubject);
		Intent in=getIntent();
		receiptId=in.getStringExtra("receiptId");
		
		
		
		
		btnContinue.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				
				Intent intent=new Intent(Intent.ACTION_SEND);
            	String[] recipients={receiptId};
            	intent.putExtra(Intent.EXTRA_EMAIL, recipients);
            	intent.putExtra(Intent.EXTRA_SUBJECT,EmailSubject);
            	intent.putExtra(Intent.EXTRA_TEXT,"");
            	
            	intent.setType("plain/text");
            	startActivity(Intent.createChooser(intent, "Contact Edge via"));
            	
            	
			}
		});
		
		
		
	}
}
