package com.zira.creditcards;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.LoginActivity1;
import com.zira.modal.CreditCard;
import com.zira.modal.User;
import com.zira.registration.DocumentUploadActivity;
import com.zira.util.SingleTon;
import com.zira.util.Util;

public class ListOfCreditCardsActivity extends Activity implements
		AsyncResponseForZira {

	private ListView listView1;
	private AddCreditCardCustomAdapter adapter;
	private User mUserModel;
	private ImageView addPaymentImageView;
	ArrayList<String> cardDetails = new ArrayList<String>();
	String creditCardId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creditcard_list);

		initialiseVariable();
		initialiseListener();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	
		mUserModel = SingleTon.getInstance().getUser();
		if(mUserModel.getCardList().size()>0){
			setAdapterListview();
		}
		else
		{
			Util.alertMessage(ListOfCreditCardsActivity.this, "No Credit cards is available.");
		}
	
			
		listView1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CreditCard user = mUserModel.getCardList().get(position);
				creditCardId = user.getCardId();
				Log.i("creditCardId : ", ":" + creditCardId);

				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
				nameValuePair.add(new BasicNameValuePair("riderid",mUserModel.getUserid()));
				nameValuePair.add(new BasicNameValuePair("cardid", creditCardId));
				AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(ListOfCreditCardsActivity.this, "SetSomeCardsAsDefault",nameValuePair, true, "Please wait...");
				mWebPageTask.delegate = (AsyncResponseForZira) ListOfCreditCardsActivity.this;
				mWebPageTask.execute();
			}
		});

	}

	private void initialiseVariable() {

		listView1 = (ListView) findViewById(R.id.listView_Promotions);
		addPaymentImageView = (ImageView) findViewById(R.id.addPaymentImageView);
	}

	private void setAdapterListview() {
		adapter = new AddCreditCardCustomAdapter(
				ListOfCreditCardsActivity.this, 0, mUserModel.getCardList());
		listView1.setAdapter(adapter);
	}

	private void initialiseListener() {

		addPaymentImageView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				startActivity(new Intent(ListOfCreditCardsActivity.this,
						AddCreditCardDetailActivity.class));
			}
		});

	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
	Log.e("set  default=", output);
		try {
			JSONObject obj = new JSONObject(output);
			String jsonResult = obj.getString("result");
			String message = obj.getString("message");
			
			if (methodName.equals("SetSomeCardsAsDefault")) {
				
				if (jsonResult.equals("0")) {
					
					for (int i = 0; i < mUserModel.getCardList().size(); i++) {

						if (creditCardId.equals(mUserModel.getCardList().get(i)
								.getCardId())) {

							mUserModel.getCardList().get(i).setIsDefault(true);
						} else {

							mUserModel.getCardList().get(i).setIsDefault(false);
						}
					}

					SingleTon.getInstance().setUser(mUserModel);
					setAdapterListview();
					AlertDialog.Builder alert = new AlertDialog.Builder(ListOfCreditCardsActivity.this);
					alert.setTitle("Zira24/7");
					alert.setMessage("select card set as default card");
					alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
							
							finish();
						}
					});
					alert.show();
										
				} 
				}
				else {
					Util.alertMessage(ListOfCreditCardsActivity.this, message);
				}
			

		} catch (Exception e) {

		}

	}
}
