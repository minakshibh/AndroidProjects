package com.twentyfourseven.zira;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.twentyfourseven.zira.R;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.ProfileInfoModel;
import com.zira.modal.User;
import com.zira.registration.ValidateEmailPhoneNumber;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;

public class LoginActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener, AsyncResponseForZira {

	private String loginWith = "LoginWithFacebook";
	private EditText txtEmail, txtPassword;
	private TextView register, forgotPassword;
	// private Button btnLogin;
	private ImageView btnLogin, facebookLogin, googleLogin;
	private String baseUrl;
	private User user;
	private ZiraParser ziraParser;
	private ProgressDialog progress;
	private String getUserVehicleProfile = "GetProfiles";
	private ProfileInfoModel mProfileInfoModel;

	// ************* Your Facebook APP ID****************************//
	private static String APP_ID ;
	@SuppressWarnings("deprecation")
	private static Facebook facebook;
	@SuppressWarnings("deprecation")
	private AsyncFacebookRunner mAsyncRunner;
	SharedPreferences prefs;
	Editor e;
	String fbFirstName = "", fbLastName = "", fbEmail = "",fbmobilePhone="";
	String encoded = "";

	// ************* Your Gmail APP ID****************************//
	public static final int RC_SIGN_IN = 0;
	public static final String TAG = "MainActivity";
	public static final int PROFILE_PIC_SIZE = 400;
	public static GoogleApiClient mGoogleApiClient;
	public static boolean mIntentInProgress;
	public static boolean mSignInClicked;
	public static ConnectionResult mConnectionResult;
	public static SharedPreferences prefs2;
	boolean getGmailInformation = false;
	private String email;
	String value = "";
	public static Context loginContext;

	TelephonyManager tManager;
	AsyncTask<Void, Void, Void> mRegisterTask;
	String regId = "", udid = "", riderid = "", driverid = "";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_new_login);

		loginContext = LoginActivity.this;
		SplashScreen.check=false;
		APP_ID = getResources().getString(R.string.facebook_app_id);
		facebook = new Facebook(APP_ID);
		ziraParser = new ZiraParser();
		baseUrl = getResources().getString(R.string.baseUrl);

		txtEmail = (EditText) findViewById(R.id.txtEmail);
		txtPassword = (EditText) findViewById(R.id.txtPassword);
		register = (TextView) findViewById(R.id.register);
		forgotPassword = (TextView) findViewById(R.id.forgotPswd);
		btnLogin = (ImageView) findViewById(R.id.buttonLogin);

		facebookLogin = (ImageView) findViewById(R.id.facebookLogin);
		// googleLogin = (ImageView)findViewById(R.id.googleLogin);

		btnLogin.setOnClickListener(listener);
		register.setOnClickListener(listener);
		forgotPassword.setOnClickListener(listener);
		facebookLogin.setOnClickListener(listener);
		// googleLogin.setOnClickListener(listener);

		/**************************push notification****************************/
		checkNotNull(Notification_Util.SERVER_URL, "SERVER_URL");
		checkNotNull(Notification_Util.SENDER_ID, "SENDER_ID");
		// ------------------------------------
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(LoginActivity.this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
	
		/********************************push notification**********************/
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		prefs2 = getSharedPreferences("policy", MODE_PRIVATE); 
		e = prefs.edit();
		e.putString("Login", "");
		e.commit();
		String gettingLogin = prefs.getString("Login", "");
		Log.d("tag", "gettingLogin:" + gettingLogin);
		if (gettingLogin.equals("")) {
			mGoogleApiClient = new GoogleApiClient.Builder(this)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).addApi(Plus.API)
					.addScope(Plus.SCOPE_PLUS_LOGIN).build();
			mGoogleApiClient.connect();
			Log.d("tag", "Conntect:");
		} else {
			String gettingLogout = prefs.getString("Logout", "");
			if (gettingLogout.equals("yes")) {
				signOutFromGplus();
				e.putString("Logout", "");
				e.commit();
				mGoogleApiClient = new GoogleApiClient.Builder(this)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this).addApi(Plus.API)
						.addScope(Plus.SCOPE_PLUS_LOGIN).build();
				mGoogleApiClient.connect();
				Log.d("tag", "No Connect:");
			}
		}
	}

	// ************************FaceBook Login*************************//
	/**
	 * Function to login into facebook
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {

		prefs = getPreferences(MODE_PRIVATE);
		String access_token = prefs.getString("FBAccessToken", "");
		long expires = prefs.getLong("FBAccessExpires", 0);

		if (access_token != null) {
			facebook.setAccessToken(access_token);
			Log.d("FB Sessions", "" + facebook.isSessionValid());
			getProfileInformation();
			/*final Editor e=prefs.edit();
			e.putString("mode", "rider");
			e.putString("Userid", "yes");
			e.commit();
			if(Util.isNetworkAvailable(LoginActivity.this)){
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
			nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("fbuserid", "")));
			AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(LoginActivity.this, getUserVehicleProfile, nameValuePair, true, "Please wait...");
			mWebPageTask.delegate = (AsyncResponseForZira) LoginActivity.this;
			mWebPageTask.execute();
			}
			else
			{
				Util.alertMessage(LoginActivity.this, "no internet connection");
			}*/
		}

		if (expires != 0) {
			facebook.setAccessExpires(expires);
			Log.i("tag", "End Process of Facebook expires..");
		}

		if (!facebook.isSessionValid()) {
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
							Log.i("tag", "End Process of Facebook Cancel..");
						}

						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token

							e.putString("FBAccessToken",
									facebook.getAccessToken());
							e.putLong("FBAccessExpires",
									facebook.getAccessExpires());
							e.commit();
							Log.i("tag", "Value: " + facebook.getAccessToken());
							Log.i("tag", "End Process of Facebook..");

							getProfileInformation();

						}

						@Override
						public void onError(DialogError error) {
						}
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);

		/********* Gmail **********/

		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}
			mIntentInProgress = false;
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	/**
	 * Get Profile information by making request to Facebook Graph API
	 * */
	@SuppressWarnings("deprecation")
	public void getProfileInformation() {
		mAsyncRunner.request("me", new RequestListener() {
			@Override
			public void onComplete(String response, Object state) {
				Log.d("Profile", response);
				String json = response;
				try {
					// Facebook Profile JSON data

					JSONObject profile = new JSONObject(json);
					// getting first name of the user
					fbFirstName = profile.getString("first_name");
					// getting last name of the user
					fbLastName = profile.getString("last_name");
					// getting email of the user
					fbEmail = profile.getString("email");
					// fbmobilePhone=profile.getString("mobile_phone");
					// String gender=profile.getString("gender");
					String id = profile.getString("id");
					Log.i("tag", "ID:" + id);

					String image_url = "https://graph.facebook.com/" + id
							+ "/picture?type=square";

					new LoadProfileImageAndConvertingToBase64()
							.execute(image_url);

					Log.i("tag", "FB FirstName:" + fbFirstName);
					Log.i("tag", "FB fbLastName:" + fbLastName);
					Log.i("tag", "FB fbEmail:" + fbEmail);
					Log.i( "mobile_phone",fbmobilePhone);
					Log.i("tag", "FB image_url:" + image_url);

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							e.putString("FBFirstName", fbFirstName);
							e.putString("FBLastName", fbLastName);
							e.putString("FBEmail", fbEmail);
							e.putString("FBmobile", fbmobilePhone);
							e.commit();
						}
					});
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onIOException(IOException e, Object state) {
			}

			@Override
			public void onFileNotFoundException(FileNotFoundException e,Object state) {
			}

			@Override
			public void onMalformedURLException(MalformedURLException e,Object state) {
			}

			@Override
			public void onFacebookError(FacebookError e, Object state) {
			}
		});
	}

	/*class GetData extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... str) {
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						1);
				nameValuePairs.add(new BasicNameValuePair("useremail", txtEmail
						.getText().toString().trim()));
				nameValuePairs.add(new BasicNameValuePair("password",
						txtPassword.getText().toString().trim()));

				String responseString = Util.getResponseFromUrl("Login",
						nameValuePairs, LoginActivity.this);
				Log.e("response", responseString);
				user = ziraParser.parseLoginResponse(responseString);

			} catch (Exception e) {
				Log.e("failure", "bdjkfhksdhfkjh");
				e.printStackTrace();
				return "failure";
			}
			return "success";
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(LoginActivity.this, "Logging In",
					"Please wait...");
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			 progress.dismiss();
			try {
				if (user.getResult().equals("0")) {
					SingleTon.getInstance().setUser(user);
					try {
						ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(
								1);
						nameValuePair.add(new BasicNameValuePair("UserId", user
								.getUserid()));
						AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(
								LoginActivity.this, getUserVehicleProfile,
								nameValuePair);
						mWebPageTask.delegate = (AsyncResponseForZira) LoginActivity.this;
						mWebPageTask.execute();
						Editor e=prefs.edit();
						e.putString("Userid", "yes");
						
						e.putString("_UserEmail", txtEmail.getText().toString().trim());
						e.putString("_Password", txtPassword.getText().toString().trim());
						e.putString("_Login", "true");
						e.commit();
					} catch (Exception e) {
						e.printStackTrace();
						progress.dismiss();
					}
				} else {
					progress.dismiss();
					Util.alertMessage(LoginActivity.this, user.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
				progress.dismiss();
				Util.alertMessage(LoginActivity.this,
						"Something went wrong. Please try again later.");
			}
		}
	}*/

	private View.OnClickListener listener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == btnLogin) {
				Util.hideKeyboard(LoginActivity.this);
				if (txtPassword.getText().toString().trim().equals("")
						|| txtEmail.getText().toString().trim().equals("")) {
					AlertDialog.Builder alert = new AlertDialog.Builder(
							LoginActivity.this);
					alert.setTitle("Zira");
					alert.setMessage("Please enter required fields.");
					alert.setPositiveButton("Ok", null);
					alert.show();
				} else {
					if (Util.isNetworkAvailable(LoginActivity.this)){
//						new GetData().execute();
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
								1);
						nameValuePairs.add(new BasicNameValuePair("useremail", txtEmail
								.getText().toString().trim()));
						nameValuePairs.add(new BasicNameValuePair("password",
								txtPassword.getText().toString().trim()));
						
						AsyncTaskForZira mLogin = new AsyncTaskForZira(
								LoginActivity.this, "Login", nameValuePairs, true, "Please wait...");
						mLogin.delegate = (AsyncResponseForZira) LoginActivity.this;
						mLogin.execute();
					}
					else {
						Util.alertMessage(LoginActivity.this,
								"Please check your internet connection");
					}
				}
			} else if (v == register) {
				Intent intent = new Intent(LoginActivity.this,
						ValidateEmailPhoneNumber.class);
				startActivity(intent);
			} else if (v == forgotPassword) {
				Intent intent = new Intent(LoginActivity.this,
						ForgotPassword.class);
				startActivity(intent);
			} else if (v == facebookLogin) {

				if (Util.isNetworkAvailable(LoginActivity.this)) {
					loginToFacebook();
					value = "fb";
				} else {
					Util.alertMessage(LoginActivity.this,
							"Please check your internet connection");
				}

			} else if (v == googleLogin) {

				if (Util.isNetworkAvailable(LoginActivity.this)) {

					signInWithGplus();
					value = "gmail";
				} else {
					Util.alertMessage(LoginActivity.this,
							"Please check your internet connection");
				}

			}

		}
	};

	// ******************************Gmail
	// Code******************************************//
	/**
	 * Method to resolve any signin errors
	 * */
	private void resolveSignInError() {
		Log.i("tag", "Connection Result::" + mConnectionResult);

		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.d("tag", "Connection Failed Result::" + result);
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}

	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
//		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
		getGmailInformation = true;
		e.putString("Login", "Successfully");
		e.commit();
	}

	/**
	 * Fetching user's information name, email, profile pic
	 * */
	private void getProfileInformation1() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi
						.getCurrentPerson(mGoogleApiClient);
				/*******************************/
				// our project requirement
				email = Plus.AccountApi.getAccountName(mGoogleApiClient);
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				/******************************/
				e.putString("GMemail", email);
				e.putString("GMpersonName", personName);
				e.putString("GMpersonPhotoUrl", personPhotoUrl);
				e.commit();

				new LoadProfileImageAndConvertingToBase64()
						.execute(personPhotoUrl);

				String personGooglePlusProfile = currentPerson.getUrl();
				Log.e(TAG, "Name: " + personName + ", plusProfile: "
						+ personGooglePlusProfile + ", email: " + email
						+ ", Image: " + personPhotoUrl);
				// Toast.makeText(getApplicationContext(),"Email:"+email
				// +"\n"+" Name:"+personName +" \n"+" PhotoUrl:"+personPhotoUrl
				// , Toast.LENGTH_SHORT).show();

			} else {

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		Log.d("tag", "onConnectionSuspended:" + arg0);
		mGoogleApiClient.connect();

	}

	private void signInWithGplus() {
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			// resolveSignInError();
			// Get user's information
			getProfileInformation1();
		}
	}

	/**
	 * Sign-out from google
	 * */
	private void signOutFromGplus() {

		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
//			Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
			e.putString("Login", "");
			e.commit();
		}
	}

	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.connect();

						}

					});
		}
	}

	// ******************Class Converting Bitmap To base
	// 64******************************//
	private class LoadProfileImageAndConvertingToBase64 extends
			AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			result.compress(Bitmap.CompressFormat.PNG, 100,
					byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
			Log.d("tag", "Encoded String::" + encoded);
			e.putString("Base64", encoded);
			e.commit();

			if (value.equals("fb")) {
				if(Util.isNetworkAvailable(LoginActivity.this)){
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("UserEmail", fbEmail));
				nameValuePairs.add(new BasicNameValuePair("Trigger", "facebook"));
		
				nameValuePairs.add(new BasicNameValuePair("FirstName",fbFirstName));//prefs.getString("FBFirstName", "")));
				nameValuePairs.add(new BasicNameValuePair("LastName", fbLastName));//prefs.getString("FBLastName", "")));
				nameValuePairs.add(new BasicNameValuePair("MobileNumber","1234567891"));//fbmobilePhone));//prefs.getString("FBmobile", "")));
				
				AsyncTaskForZira mFetchStates = new AsyncTaskForZira(LoginActivity.this, loginWith, nameValuePairs,true, "Please wait...");
				mFetchStates.delegate = (AsyncResponseForZira) LoginActivity.this;
				mFetchStates.execute();
				}
				else
				{
					Util.alertMessage(LoginActivity.this, "Please check your internet connection");
					}

			} else {

				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("UserEmail", email));
				nameValuePairs.add(new BasicNameValuePair("Trigger", "gmail"));

		AsyncTaskForZira mFetchStates = new AsyncTaskForZira(LoginActivity.this, loginWith, nameValuePairs, true, "Please wait...");
		mFetchStates.delegate = (AsyncResponseForZira) LoginActivity.this;
		mFetchStates.execute();

			}

		}
	}

	@Override
	public void processFinish(String output, String methodName) {

		
		if(methodName.equals("Login")){
			Log.e("loginResponse", output);
			user = ziraParser.parseLoginResponse(output);
			
			try {
				if (user.getResult().equals("0")) {
					SingleTon.getInstance().setUser(user);
					try {
						if(Util.isNetworkAvailable(LoginActivity.this)){
						ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
						nameValuePair.add(new BasicNameValuePair("UserId", user.getUserid()));
						AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(LoginActivity.this, getUserVehicleProfile,nameValuePair, true, "Please wait...");
						mWebPageTask.delegate = (AsyncResponseForZira) LoginActivity.this;
						mWebPageTask.execute();
						}
						else
						{
							Util.alertMessage(LoginActivity.this, "Please check your internet connection");
							}
						Editor e=prefs.edit();
						e.putString("Userid", "yes");
						
						e.putString("_UserEmail", txtEmail.getText().toString().trim());
						e.putString("_Password", txtPassword.getText().toString().trim());
						e.putString("_Login", "true");
						e.commit();
					} catch (Exception e) {
						e.printStackTrace();
//						progress.dismiss();
					}
				} else {
//					progress.dismiss();
					Util.alertMessage(LoginActivity.this, user.getMessage());
				}
			} catch (Exception e) {
				e.printStackTrace();
//				progress.dismiss();
				Util.alertMessage(LoginActivity.this,"Please try again. ");
			}
			
		}else if (methodName.equals(getUserVehicleProfile)) {
		
			Log.e("getprofile", output);
			mProfileInfoModel = ziraParser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			// progress.dismiss();		
			ServerUtilities sUtil = new ServerUtilities();
			sUtil.deviceRegister(LoginActivity.this, mProfileInfoModel);

			// Intent intent = new Intent(LoginActivity1.this,
			// VehicleSearchActivity.class);
			// startActivity(intent);
			// finish();
		} else if (methodName.equals(loginWith)) {
			Log.e("loginWithfacebook", output);
			user = ziraParser.parseLoginResponse(output);
			JSONObject obj;
			
			String userID = null;
			try {
				obj = new JSONObject(output);
			
				SingleTon.getInstance().setUser(user);
				
				String Result = obj.getString("result");
				String messge = obj.getString("message");
				
			/*	Editor e1=prefs.edit();
				e1.putString("fbuserid", userID);
				e1.commit();*/
				if(Result.equals("0"))
				{
					userID = obj.getString("userid");
					//Util.alertMessage(LoginActivity.this, messge);
					//Editor ed=prefs.edit();
					Editor e=prefs.edit();
					e.putString("mode", "rider");
					e.putString("Userid", "yes");
					e.commit();
					if(Util.isNetworkAvailable(LoginActivity.this)){
					ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
					nameValuePair.add(new BasicNameValuePair("UserId", userID));
					AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(LoginActivity.this, getUserVehicleProfile, nameValuePair, true, "Please wait...");
					mWebPageTask.delegate = (AsyncResponseForZira) LoginActivity.this;
					mWebPageTask.execute();
					}
					else
					{
						Util.alertMessage(LoginActivity.this, "Please check your internet connection");
						}
					}
				else
				{
					Util.alertMessage(LoginActivity.this, messge);
					}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}

	}

	@Override
	public void onBackPressed() {
	
	}
	

	
	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(getString(R.string.error_config, name));
		}
	}

	
}