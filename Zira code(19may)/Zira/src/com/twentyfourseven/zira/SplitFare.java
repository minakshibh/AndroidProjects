package com.twentyfourseven.zira;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.*;
import com.zira.util.SingleTon;
import com.zira.util.Util;

public class SplitFare extends Activity implements OnClickListener,
AsyncResponseForZira {
	/////////////
	Button  btnDone;
	ImageView btnspilt_Fare,btnPay;
	/////////////
	ListView showContact;
	ContactAdapter dataAdapter;
	private Cursor mCursor;
	private ContactBean[] contact_read;
	private ListView listView;
	final String SETTING_TODOLIST = "todolist";
	String data = "";
	String nameContact = "No Contact";
	String cNumber = "No Number";
	int j = 0;
	String countryCode = "";
	String responseText = "", jsonResult = "", jsonMessage = "";
	ProgressDialog pDialog;
	String url = "";
	ArrayList<ContactBean> contactList = new ArrayList<ContactBean>();
	User userModel;
	TextView pay,sf;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spilt_fare);
		// getting country code
		countryCode = "+" + GetCountryZipCode();
		intailizeVariable();
	}

	public void intailizeVariable() {
		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		userModel=SingleTon.getInstance().getUser();
		///////////////
		btnspilt_Fare = (ImageView) findViewById(R.id.btn_spiltFare);
		btnPay=(ImageView)findViewById(R.id.btn_Pay);
		///////////////
		btnDone = (Button) findViewById(R.id.btn_Done);
		pay=(TextView)findViewById(R.id.textView_pay);
		sf=(TextView)findViewById(R.id.textView_sf);
		listView = (ListView) findViewById(R.id.listViewShowContact);
		btnspilt_Fare.setOnClickListener(this);
		btnDone.setOnClickListener(this);
		btnPay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_spiltFare:
			j = 0;
			responseText = "";
			contactList.clear();
			displayListView();
			btnspilt_Fare.setVisibility(View.GONE);
			btnPay.setVisibility(View.GONE);
			sf.setVisibility(View.GONE);
			pay.setVisibility(View.GONE);
			
			btnDone.setVisibility(View.VISIBLE);
			listView.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_Pay:
			if(Util.isNetworkAvailable(SplitFare.this)){
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("UserId",prefs.getString("riderid", "")));
			params.add(new BasicNameValuePair("TripId", SingleTon.getInstance().getDriverTripId()));
			params.add(new BasicNameValuePair("TipAmount", SingleTon.getInstance().getTip()));

			AsyncTaskForZira mWebPageTask1 = new AsyncTaskForZira(SplitFare.this, "DeductPaymentEndRide",params, true, "Please wait...");
			mWebPageTask1.delegate = (AsyncResponseForZira) SplitFare.this;
			mWebPageTask1.execute();
			}
			else
			{
				Util.alertMessage(SplitFare.this, "Please check your internet connection");
			}
		default:
			break;
		case R.id.btn_Done:
			String btnText = btnDone.getText().toString();
			if (btnText.equalsIgnoreCase("Cancel")) {
				btnDone.setVisibility(View.GONE);
				btnspilt_Fare.setVisibility(View.VISIBLE);
				btnPay.setVisibility(View.VISIBLE);
				pay.setVisibility(View.VISIBLE);
				sf.setVisibility(View.VISIBLE);
				listView.setVisibility(View.GONE);
			} else {
				String selectedText = "";
				int l = 0;
				ArrayList<ContactBean> contactList = dataAdapter.contactList;
				for (int i = 0; i < contactList.size(); i++) {
					ContactBean contact = contactList.get(i);
					if (contact.isSelected()) {
						if (!contact.getNum().contains(countryCode)) {
							selectedText = countryCode + "" + contact.getNum();
						} else {
							selectedText = contact.getNum();
						}
						if (l == 0) {
							responseText += selectedText;
							l++;
						} else {
							responseText += "," + selectedText;
						}
						Log.d("tag", "Selected Contact::" + responseText);
					}
				}
				if (responseText.length() == 0) {
					btnspilt_Fare.setVisibility(View.VISIBLE);
					btnPay.setVisibility(View.VISIBLE);
					pay.setVisibility(View.VISIBLE);
					sf.setVisibility(View.VISIBLE);
					btnDone.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
				} else {
					//					new SendMessageParsing().execute();

					String userid = userModel.getUserid();
					String phonenumber = responseText;

					String tripid =SingleTon.getInstance().getDriverTripId();
					Log.i("tag", "userid:" + userid);
					Log.i("tag", "phonenumber:" + phonenumber);
					Log.i("tag", "tripid:" + tripid);
					// Building Parameters
					if(Util.isNetworkAvailable(SplitFare.this)){
					ArrayList<NameValuePair> params1 = new ArrayList<NameValuePair>();
					params1.add(new BasicNameValuePair("userid", userid));
					params1.add(new BasicNameValuePair("phonenumber", phonenumber));
					params1.add(new BasicNameValuePair("tripid", tripid));

					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(SplitFare.this, "SplitFare",params1, true,"Please wait...");
					mWebPageTask.delegate = (AsyncResponseForZira) SplitFare.this;
					mWebPageTask.execute();
					}
					else
					{
						Util.alertMessage(SplitFare.this, "Please check your internet connection");
						}
				}
			}
			break;
		}
	}

	// contact adapter for showing custom list of contact
	public class ContactAdapter extends ArrayAdapter<ContactBean> {
		ArrayList<ContactBean> contactList;

		public ContactAdapter(Context context, int textViewResourceId,
				ArrayList<ContactBean> contactList) {
			super(context, textViewResourceId, contactList);
			this.contactList = new ArrayList<ContactBean>();
			this.contactList.addAll(contactList);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Log.v("Contact", String.valueOf(position));
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.select_number_fair, null);
				holder = new ViewHolder();
				holder.textName = (TextView) convertView
						.findViewById(R.id.txt_showname);
				// holder.textNumber=(TextView)
				// convertView.findViewById(R.id.txt_shownumber);
				holder.chkBox = (CheckBox) convertView
						.findViewById(R.id.chk_number);
				convertView.setTag(holder);

				holder.chkBox.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						CheckBox cb = (CheckBox) v;
						ContactBean contact = (ContactBean) cb.getTag();
						contact.setSelected(cb.isChecked());

						boolean checkSelected = false;
						ArrayList<ContactBean> contactList1 = dataAdapter.contactList;
						for (int i = 0; i < contactList1.size(); i++) {
							ContactBean checkcontact = contactList1.get(i);
							if (checkcontact.isSelected()) {
								checkSelected = true;
								if (checkSelected == true) {
									btnDone.setText("Done");
									Log.d("tag", "Selected");
								}
							} else {
								if (checkSelected == false) {
									btnDone.setText("Cancel");
								}
								Log.d("tag", "No Selected");
							}
						}
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			ContactBean contact = contactList.get(position);
			holder.textName.setText(" " + contact.getName() + "");
			// holder.textNumber.setText(" " + contact.getNum() + "");
			holder.chkBox.setChecked(contact.isSelected());
			holder.chkBox.setTag(contact);
			return convertView;

		}
	}

	class ViewHolder {
		TextView textNumber, textName;
		CheckBox chkBox;
	}

	private void displayListView() {
		mCursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		// encode the phone number and build the filter URI
		if (mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			contact_read = new ContactBean[mCursor.getCount()];
			// Add Contacts to the Array
			do {
				String id = mCursor.getString(mCursor
						.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
				String hasPhone = mCursor
						.getString(mCursor
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
				if (hasPhone.equalsIgnoreCase("1")) {
					try {
						Cursor phones = getContentResolver()
								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
										null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
										+ " = " + id, null, null);
						phones.moveToFirst();
						cNumber = "No Number";
						cNumber = phones
								.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						Log.v("tag", "*********" + cNumber);
						nameContact = "No Contact Number";
						nameContact = mCursor
								.getString(mCursor
										.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
						Log.v("tag", "*********" + nameContact);

					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						Log.v("GPS", e.getMessage());
						e.printStackTrace();
					}
				}
				contact_read[j] = new ContactBean(nameContact, cNumber);
				j++;
				contactList = new ArrayList<ContactBean>();
				contactList.addAll(Arrays.asList(contact_read));
				// create an ArrayAdaptar from the String Array
				dataAdapter = new ContactAdapter(this,
						R.layout.select_number_fair, contactList);
				// Assign adapter to ListView
				listView.setAdapter(dataAdapter);
			} while (mCursor.moveToNext());
		} else {
			System.out.println("Cursor is NULL");

		}
	}

	// get country code
	public String GetCountryZipCode() {
		String CountryID = "";
		String CountryZipCode = "";
		TelephonyManager manager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getNetworkCountryIso().toUpperCase();
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				break;
			}
		}
		return CountryZipCode;
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		
		String result = null;
		String message = null;
		String result1 = null;
		String message1 = null;
	
		if (methodName.equals("SplitFare")) {
			Log.e("Split Response", output);
			try {
				JSONObject jsonObj = new JSONObject(output);
				result = jsonObj.getString("result");
				message = jsonObj.getString("message");
				
				if (result.equals("0"))
				{
					Util.alertMessage(SplitFare.this, message);
					btnspilt_Fare.setVisibility(View.VISIBLE);
					btnPay.setVisibility(View.VISIBLE);
					sf.setVisibility(View.VISIBLE);
					pay.setVisibility(View.VISIBLE);
					
					btnDone.setVisibility(View.GONE);
					listView.setVisibility(View.GONE);
					
				}
				else
				{
					Util.alertMessage(SplitFare.this, message);
				}

			}catch (Exception e) {
				e.printStackTrace();
				Util.alertMessage(SplitFare.this, "Something went wrong. Please try again later.");	//	
			}
		
		}

		if (methodName.equals("DeductPaymentEndRide")) {
			Log.e("Deduct PaymentEndRide", output);
			try {
		/*		JSONObject jsonObj = new JSONObject(output);
				result = jsonObj.getString("result");
				message = jsonObj.getString("message");*/
				JSONObject jsonObj1 = new JSONObject(output);
				result1 = jsonObj1.getString("result");
				message1 = jsonObj1.getString("message");
				
				if (result1.equals("0")){

					AlertDialog.Builder alert = new AlertDialog.Builder(SplitFare.this);
					alert.setTitle("Zira24/7");
					alert.setMessage("Payment successful");
					alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
							
						//Util.alertMessage(SplitFare.this, message1);
						Intent mIntent =new Intent(SplitFare.this,VehicleSearchActivity.class);
						mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mIntent);	
						finish();
						}
					});
					alert.show();
				}
				else
				{
					Util.alertMessage(SplitFare.this, message1);
				}
			}catch (Exception e) {
				e.printStackTrace();
			Util.alertMessage(SplitFare.this, "Something went wrong. Please try again later.");	//
			}
	
		}
	}
	
	@Override
	public void onBackPressed() {
		
	}
}
