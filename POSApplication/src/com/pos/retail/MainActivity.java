package com.pos.retail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.posapplication.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener{

	private Button scanBtn;
	private TextView formatTxt, contentTxt;
	Context mcontext;
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
	    
	    mcontext=this;
	    scanBtn = (Button)findViewById(R.id.scan_button);
	    formatTxt = (TextView)findViewById(R.id.scan_format);
	    contentTxt = (TextView)findViewById(R.id.scan_content);
	    
	    scanBtn.setOnClickListener(this);
	}
	public void onClick(View v){
		
		IntentIntegrator scanIntegrator = new IntentIntegrator((Activity)mcontext);
		
		scanIntegrator.initiateScan();
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		
		if (scanningResult != null) {
			//we have a result
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			
			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
			}
		else{
		    Toast toast = Toast.makeText(getApplicationContext(), 
		        "No scan data received!", Toast.LENGTH_SHORT);
		    toast.show();
		}
		}
}
