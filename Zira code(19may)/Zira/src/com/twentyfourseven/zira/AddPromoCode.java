package com.twentyfourseven.zira;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.LoginActivity1;
import com.zira.modal.PromoCode;
import com.zira.modal.User;
import com.zira.util.SingleTon;
import com.zira.util.Util;

public class AddPromoCode extends Activity implements AsyncResponseForZira {
	EditText edt_promocode;
	ListView listview;
	String value = "";
	Button btnDone;
	ImageView btnCancel;
	String result = null;
	String message = null;
	private User mUserModel;
	private SharedPreferences prefs;
	ArrayList<String> promoCodeArrayList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.promocode);
		
		intailizeVariables();
		//addPromoCodeInList();
		setAdapterToList();
		onClickListner();
	}

	public void intailizeVariables() {
		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		mUserModel = SingleTon.getInstance().getUser();
		edt_promocode = (EditText) findViewById(R.id.editText_promocode);
		listview = (ListView) findViewById(R.id.listView_showPromocode);
		btnDone = (Button) findViewById(R.id.btnDone);
		btnCancel=(ImageView)findViewById(R.id.btnCancel);
	}

	private void addPromoCodeInList() {
		for(PromoCode u:mUserModel.getPromoCodeList()){
			promoCodeArrayList.add(u.getPromoCode());
		}
		if (promoCodeArrayList.size() > 0) {
			edt_promocode.setVisibility(View.VISIBLE);
			listview.setVisibility(View.VISIBLE);
			edt_promocode.setVisibility(View.VISIBLE);
			btnDone.setVisibility(View.VISIBLE);
		}
	}

	// assign Adapter to List
	private void setAdapterToList() {
		if(promoCodeArrayList.size()>0)
		{
		ArrayAdapter<String> adtPromoCode = new ArrayAdapter<String>(
				AddPromoCode.this, android.R.layout.simple_list_item_1,	promoCodeArrayList);
		listview.setAdapter(adtPromoCode);
		}
		else
		{
		//	Util.alertMessage(AddPromoCode.this, "No Promo Code is available.");
			/*AlertDialog.Builder alert = new AlertDialog.Builder(AddPromoCode.this);
			alert.setTitle("Zira24/7");
			alert.setMessage("No Promo Code is available.");
			alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
					
					finish();
				}
			});
			alert.show();*/
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("AddPromoCode", output);
		    if(methodName.equals("AddPromoCode")){	
			JSONObject obj;
			try {
				obj = new JSONObject(output);
				result=obj.getString("result");
				message=obj.getString("message");
				if(result.equals("0"))
				{
				
				}
				else
				{
					Util.alertMessage(AddPromoCode.this, message);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    }
	}

	private void onClickListner() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				value = (String)listview.getItemAtPosition(position);
				edt_promocode.setText(value);
				Log.d("tag", "Value:::" + value);
			}
		});
		btnDone.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				String promoCode=edt_promocode.getText().toString();
				//String riderId="4";
				if(promoCode.equals(""))
				{
					Util.alertMessage(AddPromoCode.this, "Please enter promo code.");
					}
				else
				{
				if(Util.isNetworkAvailable(AddPromoCode.this)){
					
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("PromoCode",promoCode ));
				nameValuePairs.add(new BasicNameValuePair("riderId", prefs.getString("riderid", "")));

				AsyncTaskForZira mFetchStates = new AsyncTaskForZira(AddPromoCode.this,"AddPromoCode",nameValuePairs, true,"Please wait...");
				mFetchStates.delegate = (AsyncResponseForZira) AddPromoCode.this;
				mFetchStates.execute();
				}
				else
				{
					Util.alertMessage(AddPromoCode.this, "Please check your internet connection");
					}
				
				}
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}}
	