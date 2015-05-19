package com.zoom.authentication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.karaoke.util.ConnectionDetector;
import com.zoom.authentication.LoginActivity.AsyncServices;
import com.zoom.karaoke.R;
import com.zoom.xmlparser.XmlGetParser;

public class ForgotPasswordActivity extends Activity implements OnClickListener{


	private Button btnGetEmail;
	private EditText emailAddress;
	private ImageButton back;
	private ConnectionDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.forgot_password_activity);

		initialiseVariable();
		initialiseListener();
	}

	private void initialiseVariable() {
		
		back=(ImageButton)findViewById(R.id.back);
		btnGetEmail=(Button)findViewById(R.id.btnGetEmail);
		emailAddress=(EditText)findViewById(R.id.emailAddress);
	}

	private void initialiseListener() {

		detector=new ConnectionDetector(ForgotPasswordActivity.this);
		btnGetEmail.setOnClickListener(this);
		back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btnGetEmail:

			if(emailAddress.getText().toString().equals("")){

				showToast("Please Enter the Email");
				return;
			}
			
			if(detector.isConnectingToInternet())
			{
				new AsyncServices().execute();	
			}
			else
			{
				showConnectionDialog();
			}

			break;
			
		case R.id.back:

			finish();
			break;

		default:

			break;
		}

	}

	private void showConnectionDialog() {
		
		final AlertDialog alertdialog=new AlertDialog.Builder(ForgotPasswordActivity.this).create();
		System.out.println("check connection");
		alertdialog.setTitle("Zoom Karaoke");
		alertdialog.setMessage("Internet connection not available");
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
			}
		});
		alertdialog.show();

	}

	public class AsyncServices extends AsyncTask<String, Void, String> {

		private String result = "";	
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {		

			result = XmlGetParser.makeHttpRequest("http://www.zoomkaraokeapp.co.uk/service.asmx/ForgotPassword" 
					+"?Email="+emailAddress.getText().toString());

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result.equals("Success")){

				showToast("An Email has been sent to your email Address containg your password");
				Intent mIntent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
				startActivity(mIntent);
				finish();
			}
			else{

				showToast("Enter Valid Email Adddress");

			}
		}
	}

	public void showToast(String string) {

		Toast.makeText(ForgotPasswordActivity.this, string, Toast.LENGTH_LONG).show();

	}

}
