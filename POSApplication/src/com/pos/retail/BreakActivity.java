package com.pos.retail;

import com.example.posapplication.R;
import com.pos.util.PosConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class BreakActivity extends Activity {

	Button btnSignInOtherUser,btnLogin,btnCancel;
	LinearLayout layout;
	EditText editName,editPassword;
	SharedPreferences loginPref;
	Context mcontext;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.login);
		
		
		mcontext = this;
		layout = (LinearLayout) findViewById(R.id.logoutLayout);
		
		layout.setVisibility(View.VISIBLE);
		btnCancel = (Button) findViewById(R.id.cancel);
		btnLogin = (Button) findViewById(R.id.login);
		
		btnCancel.setVisibility(View.GONE);
		
		btnSignInOtherUser = (Button) findViewById(R.id.loginwithotheruser);
		
		editName = (EditText) findViewById(R.id.userName);
		
		editName.setTypeface(null, Typeface.BOLD);
		editPassword = (EditText) findViewById(R.id.password);
		
		editName.setEnabled(false);
		editName.setCursorVisible(false);
		
		
		loginPref = getSharedPreferences("LoginPref", MODE_PRIVATE);
		Log.e("Pasword ", loginPref.getString("Password", ""));
		Log.e("Username ", loginPref.getString("UserName", ""));
		editName.setText(loginPref.getString("UserName", ""));
		
		btnLogin.setOnClickListener(Listener);
		btnSignInOtherUser.setOnClickListener(Listener);
		
		
		
	}
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
		
			switch (v.getId()) 
			{
				case R.id.login:
					
					String tempPassword= editPassword.getText().toString();
					String password= loginPref.getString("Password", "");
					if(password.equals(tempPassword))
					{
						
						PosConstants.time_out = false;
						Intent intent1 =new Intent(BreakActivity.this,TransactionActivity.class);
						startActivity(intent1);
						finish();
						
					}
					else
					{
						showDialog("Please enter correct password");
					}
					
					break;
				case R.id.loginwithotheruser:
					Intent in = new Intent(mcontext,LoginScreen.class);
					in.putExtra("trigger", "operatorDeclaration");
				
					
					startActivity(in);
					break;
				
			}
			
			
		}
	};
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		final AlertDialog builder=new AlertDialog.Builder(mcontext).create();
		builder.setTitle("Notification");
		builder.setMessage(str);
		builder.setButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				builder.dismiss();
			}
		});
		builder.show();
	}
	public void onBackPressed()
	{
		
	}
}
