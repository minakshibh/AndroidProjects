package com.autometer.system;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.autometer.twitter.TwitterApp;
import com.autometer.twitter.TwitterApp.TwDialogListener;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;

public class ReportVoilence extends Activity {

	private ImageView fb_Icon, twitter_Icon, email_Icon, msg_Icon;
	private boolean fbClick, twitterClick, e_mailClick, msgClick; 
	private ImageView btnBack, fbImg, twitterImg, e_mailImg, msgImg;
	private TextView report, vehicle_details_02, vehicle_details_03,
			vehicle_details_04, dialog_header_text;
	private RadioButton cpoliceRadiobtn, cTOIRadiobtn;
	private String twitterMessage, twitterLink;
	
	private RelativeLayout vehicle_details_04_arrow,
			vehicle_details_02_down_arrow, vehicle_details_03_down_arrow;
	
	private ProgressDialog progress;
	private String description;
	private EditText txtDesc;
	private ArrayList<String> alpha;
	private ArrayList<String> numeric;
	
	private Dialog settingsDialog;
	private TextView cancelButton, setValuebtn, txtFirst, txtSecond, txtThird, txtFourth;
	private Context ctx;
	private LinearLayout dialog_03, dialog_04;
	private ImageView arrowupfirst, arrowdownfirst, arrowupsecond,
			arrowdownsecond, arrowupthird, arrowdownthird, arrowupfourth, arrowdownfourth;
	private ArrayList<String> valueList_one, valueList_second;
	
	private int index_01, index_02, index_03, index_04;
	private String autoNumber;
	
	private String chennaiPoliceTwitterLink, toiTwitterLink, chennaiPoliceFbLink, toiFbLink, chennaiPoliceEmail, toiEmail, chennaiPoliceNumber, toiNumber; 
	private String preText, postText;
	
	private TextView txtHeaderDesc, txtVehicles, txtNotify, txtChennaiPolice, txtTOI, txtShare, txtFacebook, txtTwitter, txtEmail, txtSMS;
	/*
	 * static int i=0; static int j=0; static int i1=0; static int j1=0;
	 */

	// vicky 235404913284838
	// rahul sir 554439251295770
	// Your Facebook APP ID
//	private static String APP_ID = "554439251295770"; // Replace your App ID
														// here
	private static String APP_ID = "647070925344456";
	
	private static final String[] PERMISSIONS = new String[] { "publish_stream" };

	private static final String TOKEN = "access_token";
	private static final String EXPIRES = "expires_in";
	private static final String KEY = "facebook-credentials";
	private char[] tempArr;
	private TwitterApp mTwitter;
	private Facebook facebook;
	AsyncFacebookRunner mAsyncRunner = null;
	SharedPreferences userPreference;

	Spinner spinnerChar, spinnerNum;
	ArrayList<String> charList = new ArrayList<String>();
	ArrayList<String> numList = new ArrayList<String>();
	ArrayAdapter<String> arrayAdapter;
	ProgressDialog dialog;

	// ///////////////////////////////
//	static String TWITTER_CONSUMER_KEY = "Q6OcKUcyB9vBV4semykkw"; // place your
																	// cosumer
																	// key here
//	static String TWITTER_CONSUMER_SECRET = "Jiff8VwYYF490U9IVPmv1Xs7zvfrp21kYQ3045CuU"; // place
																							// your
																							// consumer
																							// secret
																							// here
	static String TWITTER_CONSUMER_KEY = "vqNiKSf29zQKKMl8oO4Q"; // place your
	static String TWITTER_CONSUMER_SECRET = "qu6Z08rpVSPfUQh4RK7ijTwDpeZ4vqiAFEmDViSY"; // place

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	ProgressDialog pDialog;

	// Twitter
	private static Twitter twitter1;
	private static RequestToken requestToken;

	// Shared Preferences
	private static SharedPreferences mSharedPreferences;

	// Internet Connection detector
	// private ConnectionDetector cd;

	// Alert Dialog Manager
	// AlertDialogManager alert = new AlertDialogManager();
	// ///////////////////////////////

	int sdk = android.os.Build.VERSION.SDK_INT;

	public boolean saveCredentials(Facebook facebook) {
		Editor editor = getApplicationContext().getSharedPreferences(KEY,
				Context.MODE_PRIVATE).edit();
		editor.putString(TOKEN, facebook.getAccessToken());
		editor.putLong(EXPIRES, facebook.getAccessExpires());
		return editor.commit();
	}

	public boolean restoreCredentials(Facebook facebook) {
		SharedPreferences sharedPreferences = getApplicationContext()
				.getSharedPreferences(KEY, Context.MODE_PRIVATE);
		// facebook.setA
		facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
		facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
		return facebook.isSessionValid();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		mTwitter = new TwitterApp(this, TWITTER_CONSUMER_KEY,TWITTER_CONSUMER_SECRET);
		mTwitter.setListener(mTwLoginDialogListener);
		
		setContentView(R.layout.report_voilence);

		txtHeaderDesc = (TextView)findViewById(R.id.txtHeaderDesc);
		txtVehicles = (TextView)findViewById(R.id.txtVehicles);
		txtNotify = (TextView)findViewById(R.id.txtNotify);
		txtChennaiPolice = (TextView)findViewById(R.id.cpolice);
		txtTOI = (TextView)findViewById(R.id.toi);
		txtShare = (TextView)findViewById(R.id.txtShare);
		txtFacebook = (TextView)findViewById(R.id.txtFacebook);
		txtTwitter = (TextView)findViewById(R.id.txtTwitter);
		txtEmail = (TextView)findViewById(R.id.txtEmail);
		txtSMS = (TextView)findViewById(R.id.txtSMS);
		
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Roboto-Regular.ttf");
		
		txtHeaderDesc.setTypeface(typeFace);
		txtVehicles.setTypeface(typeFace);
		txtNotify.setTypeface(typeFace);
		txtChennaiPolice.setTypeface(typeFace);
		txtTOI.setTypeface(typeFace);
		txtShare.setTypeface(typeFace);
		txtFacebook.setTypeface(typeFace);
		txtTwitter.setTypeface(typeFace);
		txtEmail.setTypeface(typeFace);
		txtSMS.setTypeface(typeFace);
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		chennaiPoliceEmail = getResources().getString(R.string.chennaiPoliceEmail);
		chennaiPoliceNumber = getResources().getString(R.string.chennaiPoliceNumber);
		chennaiPoliceFbLink = getResources().getString(R.string.chennaiPoliceFbLink);
		chennaiPoliceTwitterLink = getResources().getString(R.string.chennaiPoliceTwitterLink);
		
		toiEmail = getResources().getString(R.string.toiEmail);
		toiFbLink = getResources().getString(R.string.toiFbLink);
		toiNumber = getResources().getString(R.string.toiNumber);
		toiTwitterLink = getResources().getString(R.string.toiTwitterLink);
		
		alpha = new ArrayList<String>();
		numeric = new ArrayList<String>();

		for (int i = 0; i < 10; i++)
			numeric.add(String.valueOf(i));

//		alpha.add("-");
		for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++)
			alpha.add(String.valueOf(alphabet));

		btnBack = (ImageView) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		fbClick = false;
		twitterClick = false;
		e_mailClick = false;
		msgClick = false;

		// Getting the SharedPreferences............
		userPreference = this.getSharedPreferences("UserPreference",
				MODE_PRIVATE);
		// mypref=getSharedPreferences("mypref", MODE_PRIVATE);

		// ///////////////////////////////////////////
		// Check if twitter keys are set
		
		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		
		// ///////////////////////////////////////////

		vehicle_details_02_down_arrow = (RelativeLayout) findViewById(R.id.vehicle_details_02_arrow);
		vehicle_details_03_down_arrow = (RelativeLayout) findViewById(R.id.vehicle_details_03_arrow);
		vehicle_details_04_arrow = (RelativeLayout) findViewById(R.id.vehicle_details_04_arrow);

		txtDesc = (EditText) findViewById(R.id.txtDesc);

		/*InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(txtDesc.getWindowToken(), 0);	*/
		
		vehicle_details_02 = (TextView) findViewById(R.id.vehicle_details_02);
		vehicle_details_03 = (TextView) findViewById(R.id.vehicle_details_03);
		vehicle_details_04 = (TextView) findViewById(R.id.vehicle_details_04);

		// RadioGroup group =new RadioGroup(this);
		cpoliceRadiobtn = (RadioButton) findViewById(R.id.cpolicebtn);
		cTOIRadiobtn = (RadioButton) findViewById(R.id.cTOI);

		fb_Icon = (ImageView) findViewById(R.id.fbimg);
		twitter_Icon = (ImageView) findViewById(R.id.twitterimg);
		email_Icon = (ImageView) findViewById(R.id.emailImg);
		msg_Icon = (ImageView) findViewById(R.id.smsImg);

		/*
		 * fbImg=(ImageView) findViewById(R.id.fbimg); twitterImg=(ImageView)
		 * findViewById(R.id.twitterimg); e_mailImg=(ImageView)
		 * findViewById(R.id.emailImg); msgImg=(ImageView)
		 * findViewById(R.id.msgimg);
		 */

		report = (Button) findViewById(R.id.btnReport);

		/*
		 * cpoliceToggleBtn=(Switch) findViewById(R.id.cpolicebtn);
		 * newsToggleBtn=(Switch) findViewById(R.id.newsbtn);
		 */

		// Created the Facebook api object.
		facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner = new AsyncFacebookRunner(facebook);

		/*
		 * spinnerNum = (Spinner) findViewById(R.id.spinnernum); spinnerChar =
		 * (Spinner) findViewById(R.id.spinnerchar);
		 */

		dialog = new ProgressDialog(ReportVoilence.this);
		dialog.setCanceledOnTouchOutside(false);

		fb_Icon.setOnClickListener(listener);
		twitter_Icon.setOnClickListener(listener);
		email_Icon.setOnClickListener(listener);
		msg_Icon.setOnClickListener(listener);

		report.setOnClickListener(listener);

		vehicle_details_02_down_arrow.setOnClickListener(listener);
		vehicle_details_03_down_arrow.setOnClickListener(listener);
		vehicle_details_04_arrow.setOnClickListener(listener);
		vehicle_details_02.setOnClickListener(listener);
		vehicle_details_03.setOnClickListener(listener);
		vehicle_details_04.setOnClickListener(listener);
		txtFacebook.setOnClickListener(listener);
		txtTwitter.setOnClickListener(listener);
		txtEmail.setOnClickListener(listener);
		txtSMS.setOnClickListener(listener);
		
		cpoliceRadiobtn.setOnClickListener(listener);
		cTOIRadiobtn.setOnClickListener(listener);

	}

	// Click listner for the views (For Buttons and clickable images)
	private View.OnClickListener listener = new View.OnClickListener() {
				@SuppressLint("NewApi")
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				public void onClick(View v) {
					
					autoNumber = "TN-"+vehicle_details_02.getText().toString()+" "+vehicle_details_03.getText().toString()+" "+vehicle_details_04.getText().toString();
					
					switch (v.getId()) {
					case R.id.vehicle_details_02:
						// dialog_03.setVisibility(View.GONE);
						// dialog_04.setVisibility(View.GONE);
						//
						vehicle_2_listener();
						break;
						
					case R.id.vehicle_details_02_arrow:
						// dialog_03.setVisibility(View.GONE);
						// dialog_04.setVisibility(View.GONE);
						//
						vehicle_2_listener();
						break;

					case R.id.vehicle_details_03:

						// dialog_03.setVisibility(View.GONE);
						// dialog_04.setVisibility(View.GONE);
						//
						vehicle_3_listener();
						break;

					case R.id.vehicle_details_03_arrow:

						// dialog_03.setVisibility(View.GONE);
						// dialog_04.setVisibility(View.GONE);
						//
						vehicle_3_listener();
						break;

					case R.id.vehicle_details_04:

						// dialog_03.setVisibility(View.VISIBLE);
						// dialog_04.setVisibility(View.VISIBLE);
						//
						vehicle_4_listener();
						break;
						
					case R.id.vehicle_details_04_arrow:

						// dialog_03.setVisibility(View.VISIBLE);
						// dialog_04.setVisibility(View.VISIBLE);
						//
						vehicle_4_listener();
						break;

					case R.id.fbimg:
						facebookSelector();

						break;

					case R.id.twitterimg:
						twitterSelector();
						
						break;

					case R.id.emailImg: // mailsection
						emailSelector();
						
						break;

					case R.id.smsImg:

						smsSelector();
						break;

					case R.id.txtFacebook:

						facebookSelector();
						break;

					case R.id.txtTwitter:
						twitterSelector();
						break;

					case R.id.txtEmail: // mailsection
						emailSelector();
						
						break;

					case R.id.txtSMS:

						smsSelector();
						
						break;
					case R.id.btnReport:// reporttxt

						if(txtDesc.getText().toString().trim().equals("")){
							AlertDialog.Builder alert = new AlertDialog.Builder(ReportVoilence.this);
							alert.setMessage("Please enter report description.");
							alert.setTitle("AutoMeter");
							alert.setPositiveButton("Ok", null);
							alert.show();
						}else{
							if(fbClick==false && twitterClick == false && msgClick == false && e_mailClick == false){
								AlertDialog.Builder alert = new AlertDialog.Builder(ReportVoilence.this);
								alert.setMessage("Please select medium of Sharing report.");
								alert.setTitle("AutoMeter");
								alert.setPositiveButton("Ok", null);
								alert.show();
							}else{
								if (fbClick) {
									
									if (!facebook.isSessionValid()) {
										loginAndPostToWall();
//										getProfileInformation();
									} else {
//										getProfileInformation();
										
										if (cpoliceRadiobtn.isChecked()) {
											postToFrendsWall("Complaint for auto number: "+autoNumber+" , "+txtDesc.getText().toString(), chennaiPoliceFbLink);
										} else{
											postToFrendsWall("Report voilence against Auto no. "+autoNumber, toiFbLink);
										}
									}

								} else if (twitterClick) {
									twitterMessage = "Complaint for auto number: "+autoNumber+" , "+txtDesc.getText().toString()+" - Sent via AutoMeter App";
									
									if (cpoliceRadiobtn.isChecked()) {										
										twitterLink = chennaiPoliceTwitterLink;										
									} else{
										twitterLink = toiTwitterLink;
									}
									
									loginToTwitter(twitterMessage, twitterLink);

								} else if (e_mailClick) {
									Intent i = new Intent(Intent.ACTION_SEND);
									i.setType("message/rfc822");
									i.putExtra(Intent.EXTRA_SUBJECT, "Auto Complaint");
									
									if (cpoliceRadiobtn.isChecked()) {
										i.putExtra(Intent.EXTRA_EMAIL,
												new String[] { chennaiPoliceEmail });
									} else if (cTOIRadiobtn.isChecked()) {
										i.putExtra(Intent.EXTRA_EMAIL,
												new String[] { toiEmail });
									}
									
									i.putExtra(Intent.EXTRA_TEXT, "Complaint for auto number: "+autoNumber+" , "+txtDesc.getText().toString());
									startActivity(Intent.createChooser(i,
											"Send mail via..."));
								} else if (msgClick) {
									
									final Dialog alert = new Dialog(ReportVoilence.this);
									alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
									alert.setContentView(R.layout.custom_alert);
									
									Button btnOk = (Button)alert.findViewById(R.id.btnContinue);
									Button btnCancel = (Button)alert.findViewById(R.id.useExisting);
									TextView txtMessage = (TextView)alert.findViewById(R.id.txtMessage);
									
									btnOk.setText("Ok");
									btnCancel.setText("Cancel");
									txtMessage.setText("You will be charged for sending out SMS messages as per your mobile tariff plan.");
									
									WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
								    lp.copyFrom(alert.getWindow().getAttributes());
								    lp.width = WindowManager.LayoutParams.FILL_PARENT;
								    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
								    
									btnOk.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											alert.dismiss();
											Intent intentsms = new Intent(
													Intent.ACTION_VIEW);
//											, Uri.parse("sms:" + ""))
											intentsms.setType("vnd.android-dir/mms-sms");
											
											intentsms.putExtra("sms_body", "Complaint for auto number: "+autoNumber+" , "+txtDesc.getText().toString());
											
											if (cpoliceRadiobtn.isChecked()) {
												intentsms.putExtra("address", chennaiPoliceNumber);
											} else if (cTOIRadiobtn.isChecked()) {
												intentsms.putExtra("address", toiNumber);
											}
											
											startActivity(intentsms);
										}
									});
									
									btnCancel.setOnClickListener(new View.OnClickListener() {
										
										@Override
										public void onClick(View v) {
											alert.dismiss();											
										}
									});
								
									alert.show();
									alert.getWindow().setAttributes(lp);
								}
							}
						}
						
						/*
						 * Intent intent=new Intent(getApplicationContext(),
						 * MainActivity.class); startActivity(intent);
						 */
						break;

					case R.id.cpolicebtn:
						
						cTOIRadiobtn.setChecked(false);
						cpoliceRadiobtn.setChecked(true);
						
						if(e_mailClick == true){
							e_mailClick = false;
							email_Icon.setImageResource(R.drawable.email_icon);
						}
						
						break;

					case R.id.cTOI:
						
						cTOIRadiobtn.setChecked(true);
						cpoliceRadiobtn.setChecked(false);
						
						break;
					}
				}
			};

	public void vehicle_2_listener(){
		tempArr = vehicle_details_02.getText().toString()
				.toCharArray();

		preText = "TN - ";
		postText = vehicle_details_03.getText().toString()+" "+vehicle_details_04.getText().toString();
		
		showDialog(numeric, 1,
				R.id.vehicle_details_02_arrow,
				String.valueOf(tempArr[0]),
				String.valueOf(tempArr[1]), String.valueOf(0),
				String.valueOf(0));
	}
	public void vehicle_3_listener(){
		tempArr = vehicle_details_03.getText().toString()
				.toCharArray();

		preText = "TN - "+vehicle_details_02.getText().toString();
		postText = vehicle_details_04.getText().toString();
		
		showDialog(alpha, 2,
				R.id.vehicle_details_03_arrow,
				String.valueOf(tempArr[0]),
				String.valueOf(tempArr[1]), String.valueOf(0),
				String.valueOf(0));
	}
	public void vehicle_4_listener(){
		String tempText = vehicle_details_04.getText().toString();
		
		preText = "TN - "+vehicle_details_02.getText().toString()+" "+vehicle_details_03.getText().toString();
		postText = "";
		
		final Dialog alert = new Dialog(ReportVoilence.this);
		alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alert.setContentView(R.layout.dialoglayout);
		
		Button btnOk = (Button)alert.findViewById(R.id.setvaluelbtn);
		Button btnCancel = (Button)alert.findViewById(R.id.cancelbtn);
		LinearLayout settings = (LinearLayout)alert.findViewById(R.id.setting);
		final TextView hint = (TextView)alert.findViewById(R.id.hint);
		
		final EditText txtDetails = (EditText)alert.findViewById(R.id.txtDetail);
		dialog_header_text = (TextView)alert.findViewById(R.id.dialogHeaderText);
		
		dialog_header_text.setText(preText+" "+tempText);
		
		txtDetails.setText(tempText);
		txtDetails.setSelection(tempText.length());
		
//		txtDetails.requestFocus();
	/*	InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(txtDetails, InputMethodManager.SHOW_FORCED);
		*/
		hint.setVisibility(View.GONE);
		txtDetails.setVisibility(View.VISIBLE);
		
		txtDetails.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			dialog_header_text.setText(preText + " "+s.toString());
			}
		});
		
		settings.setVisibility(View.GONE);
		
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(alert.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(txtDetails.getText().toString().length()<4)
					hint.setVisibility(View.VISIBLE);
				else{
					alert.dismiss();
					hint.setVisibility(View.GONE);
					vehicle_details_04.setText(txtDetails.getText().toString());
				}
			}
		});
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alert.dismiss();											
			}
		});
	
		
	    
		alert.show();
		alert.getWindow().setAttributes(lp);
		txtDetails.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
		/*showDialog(numeric, 4,
				R.id.vehicle_details_04_arrow,
				String.valueOf(tempArr[0]),
				String.valueOf(tempArr[1]),
				String.valueOf(tempArr[2]),
				String.valueOf(tempArr[3]));*/
		
	}
	
	public void facebookSelector(){

		fbClick = true;
		twitterClick = false;
		e_mailClick = false;
		msgClick = false;
		
		fb_Icon.setImageResource(R.drawable.fb_icon_hover);
		twitter_Icon.setImageResource(R.drawable.twitter_icon);
		email_Icon.setImageResource(R.drawable.email_icon);
		msg_Icon.setImageResource(R.drawable.sms_icon);
	}
	public void twitterSelector(){
		fbClick = false;
		twitterClick = true;
		e_mailClick = false;
		msgClick = false;
		
		fb_Icon.setImageResource(R.drawable.fb_icon);
		twitter_Icon.setImageResource(R.drawable.twitter_icon_hover);
		email_Icon.setImageResource(R.drawable.email_icon);
		msg_Icon.setImageResource(R.drawable.sms_icon);
		
	}
	public void emailSelector(){

		if(!cpoliceRadiobtn.isChecked()){
			fbClick = false;
			twitterClick = false;
			e_mailClick = true;
			msgClick = false;
			
			fb_Icon.setImageResource(R.drawable.fb_icon);
			twitter_Icon.setImageResource(R.drawable.twitter_icon);
			email_Icon.setImageResource(R.drawable.email_icon_hover);
			msg_Icon.setImageResource(R.drawable.sms_icon);
		}
	}
	public void smsSelector(){
		fbClick = false;
		twitterClick = false;
		e_mailClick = false;
		msgClick = true;
		
		fb_Icon.setImageResource(R.drawable.fb_icon);
		twitter_Icon.setImageResource(R.drawable.twitter_icon);
		email_Icon.setImageResource(R.drawable.email_icon);
		msg_Icon.setImageResource(R.drawable.sms_icon_hover);
	}
	
	
	public void loginAndPostToWall() {
		facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH,
				new LoginDialogListener());
	}

	class LoginDialogListener implements DialogListener {
		public void onComplete(Bundle values) {
			saveCredentials(facebook);
			
			if (cpoliceRadiobtn.isChecked()) {
				postToFrendsWall("Complaint for auto number: "+autoNumber+" , "+txtDesc.getText().toString(), chennaiPoliceFbLink);
			} else{
				postToFrendsWall("Report voilence against Auto no. "+autoNumber, toiFbLink);
			}
			
			// if (messageToPost != null){
			// postToWall(messageToPost);
			// postToFrendsWall(userPreference.getString("FbUser",""));
			// postToUsersWall(fb, message, linkurl, caption, des, restName,
			// imageurl)
			// postToUsersWall(facebook, userPreference.getString("FbUser",""),
			// place.getImageUrl(), "", place.getDescription(), place.getName(),
			// place.getImageUrl());

			// }
		}

		public void onFacebookError(FacebookError error) {
			// showToast("Authentication with Facebook failed!");
			// finish();
		}

		public void onError(DialogError error) {
			// showToast("Authentication with Facebook failed!");
			// finish();
		}

		public void onCancel() {
			// showToast("Authentication with Facebook cancelled!");
			// finish();
		}
	}

	/*
	 * private void showToast(String message){
	 * //Toast.makeText(getApplicationContext(), message,
	 * Toast.LENGTH_SHORT).show(); dialogMessage.setText(message);
	 * okButton.setVisibility(View.GONE); cancelButton.setText("Ok");
	 * settingsDialog.show(); }
	 */

	// Get the Profile information of the login user.
	public void getProfileInformation() {

		mAsyncRunner.request("me", new RequestListener() {

			public void onComplete(String response, Object state) {
//				Log.d("Profile", response);
				String json = response;
				try {
					JSONObject profile = new JSONObject(json);
					// getting name of the user
					final String Username = profile.getString("name");

					Editor editor = userPreference.edit();
					editor.putString("FbUser", Username);
					editor.commit();

					runOnUiThread(new Runnable() {

						public void run() {
							// Toast.makeText(getApplicationContext(), "Name: "
							// + name + "\nEmail: " + email +"\nId: " + id,
							// Toast.LENGTH_LONG).show();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			public void onIOException(IOException e, Object state) {
			}

			public void onFileNotFoundException(FileNotFoundException e,
					Object state) {
			}

			public void onMalformedURLException(MalformedURLException e,
					Object state) {
			}

			public void onFacebookError(FacebookError e, Object state) {
			}
		});
		
		// return fbUser;
	}

	class updateFacebookStatus extends AsyncTask<String, String, String>{
		String response;
		
		@Override
	    protected String doInBackground(String... str) {
	    	
	    	try{
		    	Bundle params = new Bundle();
				
		    	params.putString("message", str[0]);
	
				response = facebook.request(str[1]+"/feed",
						params, "POST");
//				Log.e("postToFrendsWall", "response : : : " + response);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    	
	       return "success";
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        progress = ProgressDialog.show(ReportVoilence.this, "Posting Report on Facebook", "Please wait...");
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        
	        progress.dismiss();
	        
	        if (response == null || response.equals("")
					|| response.equals("false")) {
//				Log.v("Error", "Blank response");
				Toast.makeText(ReportVoilence.this, "Some error occurred. Please try again.", Toast.LENGTH_LONG).show();	
			}else{
				Toast.makeText(ReportVoilence.this, "Report has been posted successfully.", Toast.LENGTH_LONG).show();					
			}
	    }
	}
	
	protected void postToFrendsWall( String message, String link) {
		try {

			// if (isSession()) {
			if (restoreCredentials(facebook)) {
				new updateFacebookStatus().execute(message, link);
				
			} else {
				// no logged in, so relogin
				Log.d("Share DAta", "sessionNOTValid, relogin");
//				Log.e("postToFrendsWall",
//						"response : : : sessionNOTValid, relogin");
				facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter(String message, String link) {
		if (mTwitter.hasAccessToken()) {
			postToTwitter(link+" "+message);
		} else {
			mTwitter.authorize();
		}
	}

	private void postToTwitter(final String review) {
		progress = ProgressDialog.show(ReportVoilence.this, "Posting Report on Twitter", "Please wait...");
		
		new Thread() {
			 
			@Override
			public void run() {
				int what = 0;

				
				 try {
					mTwitter.updateStatus(review);
				} catch (Exception e) {
					what = 1;
					System.out.println(e.getMessage());
				}
				
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}
	
	private final TwDialogListener mTwLoginDialogListener = new TwDialogListener() {
		@Override
		public void onComplete(String value) {
			/*username 	= mTwitter.getUsername();
			username	= (username.equals("")) ? "No Name" : username;
		
			mTwitterBtn.setText("  Twitter  (" + username + ")");
			mTwitterBtn.setChecked(true);
			
			postToTwitter = true;
			*/
//			Toast.makeText(ReportVoilence.this, "Connected to Twitter", Toast.LENGTH_LONG).show();
//			Log.e("values","message : "+twitterMessage +", link: "+twitterLink);
			
			loginToTwitter(twitterMessage, twitterLink);
		}
		
		@Override
		public void onError(String value) {
			Toast.makeText(ReportVoilence.this, "Twitter connection failed", Toast.LENGTH_LONG).show();
		}
	};
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			progress.dismiss();
			if(msg.what ==0)
				Toast.makeText(ReportVoilence.this, "Report has been posted successfully.", Toast.LENGTH_LONG).show();	
			else
				Toast.makeText(ReportVoilence.this, "Some error occurred. Please try again.", Toast.LENGTH_LONG).show();	
		}
	};
	
	public void showDialog(ArrayList<String> array, int mode, final int trigger, String value_1, String value_2, String value_3, String value_4) {
		valueList_one = new ArrayList<String>();
		valueList_second = new ArrayList<String>();
		
		if(mode==2)
			valueList_one.add("-");
			
		for(int i = 0; i<array.size(); i++){
			valueList_one.add(array.get(i));
			valueList_second.add(array.get(i));
		}
		
		settingsDialog = new Dialog(ReportVoilence.this);
		
		settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		LayoutInflater inflater = (LayoutInflater) ReportVoilence.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.dialoglayout,
				null);
		View dialoglayout = inflater.inflate(R.layout.dialoglayout, parent,
				false);
		settingsDialog.setContentView(dialoglayout);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(settingsDialog.getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.FILL_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    
		setValuebtn = (TextView) dialoglayout.findViewById(R.id.setvaluelbtn);
		// dialogMessage=(TextView)dialoglayout.findViewById(R.id.dialogmessage);
		cancelButton = (TextView) dialoglayout.findViewById(R.id.cancelbtn);

		dialog_03 = (LinearLayout) dialoglayout.findViewById(R.id.dialog_03);
		dialog_04 = (LinearLayout) dialoglayout.findViewById(R.id.dialog_04);
		
		dialog_header_text = (TextView)dialoglayout.findViewById(R.id.dialogHeaderText);
		dialog_header_text.setText(preText+" "+value_1+value_2+" "+postText);
		
		arrowupfirst = (ImageView) dialoglayout
				.findViewById(R.id.arrowupfirstbtn);
		arrowdownfirst = (ImageView) dialoglayout
				.findViewById(R.id.arrowdownfirstbtn);
		txtFirst = (TextView) dialoglayout.findViewById(R.id.txtfirst);
		txtFirst.setText(value_1);
		index_01 = valueList_one.indexOf(value_1);
		
		/*if(index_01==0)
			arrowdownfirst.setBackgroundResource(R.drawable.down_icon_hover);
		else if(index_01==valueList_one.size()-1)
			arrowupfirst.setBackgroundResource(R.drawable.up_icon_hover);*/
		
		arrowupsecond = (ImageView) dialoglayout
				.findViewById(R.id.arrowupsecondtbtn);
		arrowdownsecond = (ImageView) dialoglayout
				.findViewById(R.id.arrowdownsecondbtn);
		txtSecond = (TextView) dialoglayout.findViewById(R.id.txtSecond);
		txtSecond.setText(value_2);
		index_02 = valueList_second.indexOf(value_2);
		
		/*if(index_02==0)
			arrowdownsecond.setBackgroundResource(R.drawable.down_icon_hover);
		else if(index_02==valueList_second.size()-1)
			arrowupsecond.setBackgroundResource(R.drawable.up_icon_hover);*/
		
		
		arrowupthird = (ImageView) dialoglayout
				.findViewById(R.id.arrowup_03tbtn);
		arrowdownthird = (ImageView) dialoglayout
				.findViewById(R.id.arrowdown_03btn);
		txtThird = (TextView) dialoglayout.findViewById(R.id.txt_03);
		txtThird.setText(value_3);
		index_03 = valueList_second.indexOf(value_3);
		
		/*if(index_03==0)
			arrowdownthird.setBackgroundResource(R.drawable.down_icon_hover);
		else if(index_03==valueList_second.size()-1)
			arrowupthird.setBackgroundResource(R.drawable.up_icon_hover);*/
		
		arrowupfourth = (ImageView) dialoglayout
				.findViewById(R.id.arrowup_04tbtn);
		arrowdownfourth = (ImageView) dialoglayout
				.findViewById(R.id.arrowdown_04btn);
		txtFourth = (TextView) dialoglayout.findViewById(R.id.txt_04);
		txtFourth.setText(value_4);
		index_04 = valueList_second.indexOf(value_4);
		
		/*if(index_04==0)
			arrowdownfourth.setBackgroundResource(R.drawable.down_icon_hover);
		else if(index_04==valueList_second.size()-1)
			arrowupfourth.setBackgroundResource(R.drawable.up_icon_hover);
		*/
		
		arrowupfirst.setOnClickListener(arrowListener);
		arrowdownfirst.setOnClickListener(arrowListener);
		arrowupsecond.setOnClickListener(arrowListener);
		arrowdownsecond.setOnClickListener(arrowListener);
		arrowupthird.setOnClickListener(arrowListener);
		arrowdownthird.setOnClickListener(arrowListener);
		arrowupfourth.setOnClickListener(arrowListener);
		arrowdownfourth.setOnClickListener(arrowListener);
		
		if(mode==2 || mode == 1){
			dialog_03.setVisibility(View.GONE);
			dialog_04.setVisibility(View.GONE);
		}
		
		cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				settingsDialog.dismiss();
			}
		});
		setValuebtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				switch(trigger){
				case R.id.vehicle_details_02_arrow:
					vehicle_details_02.setText(txtFirst.getText().toString()+txtSecond.getText().toString());
					settingsDialog.dismiss();
					break;
				case R.id.vehicle_details_03_arrow:
					String detail = txtFirst.getText().toString()+txtSecond.getText().toString();
					
					if(!detail.equals("--")){
						vehicle_details_03.setText(detail);
						settingsDialog.dismiss();	
					}
					break;
				case R.id.vehicle_details_04_arrow:
					vehicle_details_04.setText(txtFirst.getText().toString()+txtSecond.getText().toString()+txtThird.getText().toString()+txtFourth.getText().toString());
					settingsDialog.dismiss();
					break;
				}
				
				
			}
		});
		
		settingsDialog.show();
		settingsDialog.getWindow().setAttributes(lp);
	}

	private View.OnClickListener arrowListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.arrowupfirstbtn:
				if(index_01>=(valueList_one.size()-1)){
					index_01 = -1;
				}
				txtFirst.setText(valueList_one.get(++index_01));
				dialog_header_text.setText(preText+" "+txtFirst.getText().toString()+txtSecond.getText().toString()+" "+postText);
				
				/*if(index_01==valueList_one.size()-1)
					arrowupfirst.setBackgroundResource(R.drawable.up_icon_hover);
				else
					arrowupfirst.setBackgroundResource(R.drawable.up_arrow_hover);*/
				
				break;

			case R.id.arrowdownfirstbtn:
				if(index_01<=0){
					index_01 = valueList_one.size();
				}
				txtFirst.setText(valueList_one.get(--index_01));
				dialog_header_text.setText(preText+" "+txtFirst.getText().toString()+txtSecond.getText().toString()+" "+postText);
				
//				arrowupfirst.setBackgroundResource(R.drawable.up_arrow_hover);
				/*if(index_01==0)
					arrowdownfirst.setBackgroundResource(R.drawable.down_icon_hover);
				else
					arrowdownfirst.setBackgroundResource(R.drawable.down_arrow_hover);
				*/
				break;

			case R.id.arrowupsecondtbtn:
				if(index_02>=(valueList_second.size()-1)){
					index_02 = -1;
				}
				txtSecond.setText(valueList_second.get(++index_02));
				dialog_header_text.setText(preText+" "+txtFirst.getText().toString()+txtSecond.getText().toString()+" "+postText);
				
				/*if(index_02<(valueList_second.size()-1)){
					txtSecond.setText(valueList_second.get(++index_02));
					arrowdownsecond.setBackgroundResource(R.drawable.down_arrow_hover);
				}
				
				if(index_02==valueList_second.size()-1)
					arrowupsecond.setBackgroundResource(R.drawable.up_icon_hover);
				else
					arrowupsecond.setBackgroundResource(R.drawable.up_arrow_hover);*/
				break;

			case R.id.arrowdownsecondbtn:
				if(index_02<=0){
					index_02 = valueList_second.size();
				}
				txtSecond.setText(valueList_second.get(--index_02));
				dialog_header_text.setText(preText+" "+txtFirst.getText().toString()+txtSecond.getText().toString()+" "+postText);
				
				/*if(index_02>0){
					txtSecond.setText(valueList_second.get(--index_02));
					arrowupsecond.setBackgroundResource(R.drawable.up_arrow_hover);
				}
				
				if(index_02==0)
					arrowdownsecond.setBackgroundResource(R.drawable.down_icon_hover);
				else
					arrowdownsecond.setBackgroundResource(R.drawable.down_arrow_hover);*/
				
				break;
				
			case R.id.arrowup_03tbtn:
				if(index_03>=(valueList_second.size()-1)){
					index_03 = -1;
				}
				txtThird.setText(valueList_second.get(++index_03));
				
				/*if(index_03<(valueList_second.size()-1)){
					txtThird.setText(valueList_second.get(++index_03));
					arrowdownthird.setBackgroundResource(R.drawable.down_arrow_hover);
				}
				
				if(index_03==valueList_second.size()-1)
					arrowupthird.setBackgroundResource(R.drawable.up_icon_hover);
				else
					arrowupthird.setBackgroundResource(R.drawable.up_arrow_hover);*/
				
				break;

			case R.id.arrowdown_03btn:
				if(index_03<=0){
					index_03 = valueList_second.size();
				}
				txtThird.setText(valueList_second.get(--index_03));
				
				/*if(index_03>0){
					txtThird.setText(valueList_second.get(--index_03));
					arrowupthird.setBackgroundResource(R.drawable.up_arrow_hover);
				}
				
				if(index_03==0)
					arrowdownthird.setBackgroundResource(R.drawable.down_icon_hover);
				else
					arrowdownthird.setBackgroundResource(R.drawable.down_arrow_hover);*/
				
				break;
				
			case R.id.arrowup_04tbtn:
				if(index_04>=(valueList_second.size()-1)){
					index_04 = -1;
				}
				txtFourth.setText(valueList_second.get(++index_04));
				
				/*if(index_04<(valueList_second.size()-1)){
					txtFourth.setText(valueList_second.get(++index_04));
					arrowdownfourth.setBackgroundResource(R.drawable.down_arrow_hover);
				}
				
				if(index_04==valueList_second.size()-1)
					arrowupfourth.setBackgroundResource(R.drawable.up_icon_hover);
				else
					arrowupfourth.setBackgroundResource(R.drawable.up_arrow_hover);*/
				
				break;

			case R.id.arrowdown_04btn:
				if(index_04<=0){
					index_04 = valueList_second.size();
				}
				txtFourth.setText(valueList_second.get(--index_04));
				
				/*if(index_04>0){
					txtFourth.setText(valueList_second.get(--index_04));
					arrowupfourth.setBackgroundResource(R.drawable.up_arrow_hover);
				}
				
				if(index_04==0)
					arrowdownfourth.setBackgroundResource(R.drawable.down_icon_hover);
				else
					arrowdownfourth.setBackgroundResource(R.drawable.down_arrow_hover);*/
				
				break;
			}
		}
	};
}
