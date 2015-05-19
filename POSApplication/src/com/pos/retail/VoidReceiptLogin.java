package com.pos.retail;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.posapplication.R;
import com.pos.util.Cashier;
import com.pos.util.PoSDatabase;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class VoidReceiptLogin extends Activity{

	Button btnLogin,btnCancel;
	TextView txtPassword,formTitle,txtOperatorName,txtUserName;
	EditText editOperatorName,editName,editPassword;
	Context mcontext;
	SharedPreferences.Editor edit ;
	SharedPreferences itempref;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		setContentView(R.layout.login);
		
		btnLogin = (Button)findViewById(R.id.login);
		btnCancel = (Button)findViewById(R.id.cancel);
		
		 itempref = getSharedPreferences("itempref", MODE_PRIVATE);
		edit = itempref.edit();
		
		
		mcontext = this;
		txtUserName=(TextView)findViewById(R.id.txtUserName);
		txtPassword=(TextView)findViewById(R.id.txtPassword);
		formTitle=(TextView)findViewById(R.id.formtitle);
		
		txtOperatorName=(TextView)findViewById(R.id.txtOperatorName);
		editOperatorName=(EditText)findViewById(R.id.operatorName);
		
		editName = (EditText) findViewById(R.id.userName);
		editPassword = (EditText) findViewById(R.id.password);
		
		
		btnLogin.setOnClickListener(Listener);
		btnCancel.setOnClickListener(Listener);
	}
	OnClickListener Listener = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
			case R.id.login:
				String userName = editName.getText().toString();
				String userPassword = editPassword.getText().toString();
				
						if(userName.equalsIgnoreCase("") || userPassword.equalsIgnoreCase(""))
						{
							editName.setText("");
							editPassword.setText("");
							editName.requestFocus();
							showDialog("Please enter username or password");
						}
						else
						{
							PoSDatabase db = new PoSDatabase(mcontext);
									
									Cashier detail = db.checkUserExist(userName);
									
									Log.e("Detail ", ""+detail);
									
									if(detail!=null)
										System.out.println(detail.getUsername()+" "+detail.getPassword()+" "+detail.getRollId());
										
									if(detail == null)
									{
										editName.setText("");
										editPassword.setText("");
										editName.requestFocus();
										showDialog("Please enter valid username");
									}
									else if(detail.getPassword().equals(userPassword) && detail.getRollId() ==2)
									{
										String str = itempref.getString("ItemReturnType", "");
										if(str.equals("Get Receipt"))
										{
											
											edit.putString("key", "Void done");
											edit.commit();
											finish();
											
										}
										else
										{
										Intent invoid = new Intent(mcontext,ItemReturnsActivity.class);
										 edit.putString("ItemReturnType", "Void Receipt");
										 edit.putString("ItemReturnCode", "VoidReceipt");
										 edit.commit();
										 finish();
										 startActivity(invoid);
										
										}
									}
									else
									{
										editName.setText("");
										editPassword.setText("");
										editName.requestFocus();
										showDialog("Please enter valid username or password");
									}
						}
				break;
			case R.id.cancel:
				finish();
				break;
		
			
			
			}
			
		}
	};
	public void showDialog(final String str)
	{
		final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
		
		String title=getResources().getString(R.string.pos);
		alertdialog.setTitle(title);
		alertdialog.setMessage(str);
		alertdialog.setPositiveButton("OK", null);
		alertdialog.show();
	}
}
