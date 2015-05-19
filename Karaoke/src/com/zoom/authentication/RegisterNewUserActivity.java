package com.zoom.authentication;


import com.karaoke.util.ConnectionDetector;
import com.zoom.authentication.LoginActivity.AsyncServices;
import com.zoom.karaoke.R;
import com.zoom.xmlparser.XmlGetParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class RegisterNewUserActivity extends Activity implements OnClickListener{

	private EditText firstName,lastName,emailAddress,password,confirmPswd;
	private Button btnRegister,btnLogin;
	private String uuid;
	private ImageButton back;
	private ConnectionDetector detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.register);

		TelephonyManager tManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		uuid = tManager.getDeviceId();

		initialiseVariable();
		initialiseListener();
	}

	private void initialiseVariable() {
		
		back=(ImageButton)findViewById(R.id.back);
		firstName=(EditText)findViewById(R.id.firstName);
		lastName=(EditText)findViewById(R.id.lastName);
		emailAddress=(EditText)findViewById(R.id.emailAddress);
		password=(EditText)findViewById(R.id.password);
		confirmPswd=(EditText)findViewById(R.id.confirmPswd);
		btnRegister=(Button)findViewById(R.id.btnRegister);
		btnLogin=(Button)findViewById(R.id.btnLogin);


	}

	private void initialiseListener() {

		detector=new ConnectionDetector(RegisterNewUserActivity.this);
		btnLogin.setOnClickListener(this);
		btnRegister.setOnClickListener(this);
		back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btnLogin:

			finish();

			break;
			
		case R.id.back:

			finish();
			break;

		case R.id.btnRegister:		


			if(firstName.getText().toString().equals("")){

				showToast("Please Enter the First Name");
				return;
			}

			if(lastName.getText().toString().equals("")){

				showToast("Please Enter the Last Name");
				return;
			}
			if(emailAddress.getText().toString().equals("")){

				showToast("Please Enter the Email Address");
				return;
			}
			if(password.getText().toString().equals("")){

				showToast("Please Enter password");
				return;
			}
			if(confirmPswd.getText().toString().equals("")){

				showToast("Please Enter confirm password");
				return;
			}

			if(!(password.getText().toString().equals(confirmPswd.getText().toString()))){

				showToast("Two Password are not identical");
				return;
			}
			
			if(detector.isConnectingToInternet())
			{
				new MyAsync().execute();		
			}
			else
			{
				showConnectionDialog();
			}
			

			break;

		case R.id.forgotPswd:

			break;

		default:

			break;
		}
	}

	private void showConnectionDialog() {
		
		final AlertDialog alertdialog=new AlertDialog.Builder(RegisterNewUserActivity.this).create();
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

	public class MyAsync extends AsyncTask<String, Void, String> {

		private String result = "";	
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {		
			super.onPreExecute();
			pDialog = new ProgressDialog(RegisterNewUserActivity.this);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {		

			result = XmlGetParser.makeHttpRequest("http://www.zoomkaraokeapp.co.uk/service.asmx/RegisterUser" 
					+"?Username="+firstName.getText().toString().trim()+"%20"+lastName.getText().toString().trim()
					+"&Password="+password.getText().toString().trim()
					+"&User_UDID="+uuid
					+"&Email="+emailAddress.getText().toString().trim());
			return result;
		}

		@Override
		protected void onPostExecute(String result) {			
			super.onPostExecute(result);
			pDialog.dismiss();
			if(result.equals("Success")){

				Toast.makeText(RegisterNewUserActivity.this, "User Registred Succesfully", Toast.LENGTH_LONG).show();
			}
			else{

				Toast.makeText(RegisterNewUserActivity.this, "Sorry we did something worng!!!.Please try again", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void showToast(String string) {

		Toast.makeText(RegisterNewUserActivity.this, string, Toast.LENGTH_LONG).show();

	}

}
