package com.twentyfourseven.zira;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Support extends Activity implements OnClickListener {
	TextView txt_policy, txt_help, txt_terms, txt_job;
	TextView txtEmailSupport, txtInfoSupport, btnText, btnCall;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_support_screen);
		intailizeVariable();

	onlistenser();
	
	}

	private void onlistenser() {
		txt_policy.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://zira247.com/Home/Privacy"));
				startActivity(browser);
				
			}
		});
		txt_help.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://zira247.com/Home/Help"));
				startActivity(browser);
				
			}
		});
		txt_terms.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://zira247.com/Home/Terms"));
				startActivity(browser);
				
			}
		});;
		txt_job.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent browser = new Intent(Intent.ACTION_VIEW, Uri.parse("http://zira247.com/Home/Jobs"));
				startActivity(browser);
				
			}
		});
		
	}

	// here intailize variables of controls
	public void intailizeVariable() {
		txtEmailSupport = (TextView) findViewById(R.id.txt_emailZiraSupport);
		txtInfoSupport = (TextView) findViewById(R.id.txt_emailZiraInfo);
		btnText = (TextView) findViewById(R.id.btn_textUS);
		btnCall = (TextView) findViewById(R.id.btn_CallUS);

		
		txt_policy = (TextView) findViewById(R.id.textView_PrivacyPolicy);
		txt_help = (TextView) findViewById(R.id.textView_help);
		txt_terms = (TextView) findViewById(R.id.textView_Conditions);
		txt_job = (TextView) findViewById(R.id.textView_jobs);
		
		txtEmailSupport.setOnClickListener(this);
		txtInfoSupport.setOnClickListener(this);
		btnText.setOnClickListener(this);
		btnCall.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// here button for sending email
		case R.id.txt_emailZiraSupport:
			Intent intent = new Intent(Intent.ACTION_SENDTO);
			intent.setData(Uri.parse("mailto:"));
			intent.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "support@zira247.com" });
			intent.putExtra(Intent.EXTRA_SUBJECT, "Zira 24/7");
			startActivity(Intent.createChooser(intent, "Send Email"));
			break;
		// here button for sending email
		case R.id.txt_emailZiraInfo:
			Intent intent1 = new Intent(Intent.ACTION_SENDTO);
			intent1.setData(Uri.parse("mailto:"));
			intent1.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "info@zira247.com" });
			intent1.putExtra(Intent.EXTRA_SUBJECT, "Zira 24/7");
			startActivity(Intent.createChooser(intent1, "Send Email"));
			break;
		// here button for sending message by set default number
		case R.id.btn_textUS:
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"
					+ "18559472247")));
			break;
		// here button for calling by set default number
		case R.id.btn_CallUS:
			Intent callIntent = new Intent(Intent.ACTION_CALL);
			callIntent.setData(Uri.parse("tel:18559472247"));
			startActivity(callIntent);
			break;
		default:
			break;
		}
	}
}
