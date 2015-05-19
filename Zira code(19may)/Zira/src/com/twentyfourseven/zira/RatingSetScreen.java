package com.twentyfourseven.zira;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.login.LoginActivity1;
import com.zira.modal.GetTripDetails;
import com.zira.notification.RiderNotification;
import com.zira.util.SingleTon;
import com.zira.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class RatingSetScreen extends Activity implements OnClickListener,
AsyncResponseForZira {
	ImageView imageViewStarOne, imageViewStarTwo, imageViewStarThree,
	imageViewStarFour, imageViewStarFive;
	int count1 = 0, count2 = 0, count3 = 0, count4 = 0, count5 = 0,
			flag_count = 0, add_count = 0;
	EditText editComment;
	TextView txtFair;
	ImageView sendFeedBack;
	ProgressDialog pDialog;
	String urlRating = "";
	String jsonResult = "", jsonMessage = "";
	String driverId = "", riderId = "", tripId = "", rating = "",
			feedback = "", sender = "";
	String methodRiderToDriverRating = "RiderToDriverRating";
	private SharedPreferences prefs;
	private Editor editor;
	private GetTripDetails tripDetailsModel;
	String result;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rating_screen);
		intailizeVariable();
	}

	private void intailizeVariable() {
		
		prefs=getSharedPreferences("Zira", MODE_PRIVATE);	
		tripDetailsModel=SingleTon.getInstance().getGetTripDetail();
		imageViewStarOne = (ImageView) findViewById(R.id.image1);
		imageViewStarTwo = (ImageView) findViewById(R.id.image2);
		imageViewStarThree = (ImageView) findViewById(R.id.image3);
		imageViewStarFour = (ImageView) findViewById(R.id.image4);
		imageViewStarFive = (ImageView) findViewById(R.id.image5);
		editComment = (EditText) findViewById(R.id.editText_comment);
		sendFeedBack = (ImageView) findViewById(R.id.ButtonSendFeedBack);
		txtFair = (TextView) findViewById(R.id.textView_ShowFare);
		
		//String newFare=new DecimalFormat("##.##").format(Float.parseFloat(tripDetailsModel.getGetTrip_SuggesstionFare()));		
		//txtFair.setText(newFare);
		///////////////////
		txtFair.setText("$"+	getIntent().getStringExtra("newfare"));
		imageViewStarOne.setOnClickListener(this);
		imageViewStarTwo.setOnClickListener(this);
		imageViewStarThree.setOnClickListener(this);
		imageViewStarFour.setOnClickListener(this);
		imageViewStarFive.setOnClickListener(this);
		sendFeedBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.image1:
			flag_count = 1;
			imageViewStarOne.setImageResource(R.drawable.star_active);
			imageViewStarTwo.setImageResource(R.drawable.star);
			imageViewStarThree.setImageResource(R.drawable.star);
			imageViewStarFour.setImageResource(R.drawable.star);
			imageViewStarFive.setImageResource(R.drawable.star);
			break;
		case R.id.image2:
			flag_count = 2;
			imageViewStarOne.setImageResource(R.drawable.star_active);
			imageViewStarTwo.setImageResource(R.drawable.star_active);
			imageViewStarThree.setImageResource(R.drawable.star);
			imageViewStarFour.setImageResource(R.drawable.star);
			imageViewStarFive.setImageResource(R.drawable.star);
			break;
		case R.id.image3:
			flag_count = 3;
			imageViewStarOne.setImageResource(R.drawable.star_active);
			imageViewStarTwo.setImageResource(R.drawable.star_active);
			imageViewStarThree.setImageResource(R.drawable.star_active);
			imageViewStarFour.setImageResource(R.drawable.star);
			imageViewStarFive.setImageResource(R.drawable.star);
			break;
		case R.id.image4:
			flag_count = 4;
			imageViewStarOne.setImageResource(R.drawable.star_active);
			imageViewStarTwo.setImageResource(R.drawable.star_active);
			imageViewStarThree.setImageResource(R.drawable.star_active);
			imageViewStarFour.setImageResource(R.drawable.star_active);
			imageViewStarFive.setImageResource(R.drawable.star);
			break;
		case R.id.image5:
			flag_count = 5;
			imageViewStarOne.setImageResource(R.drawable.star_active);
			imageViewStarTwo.setImageResource(R.drawable.star_active);
			imageViewStarThree.setImageResource(R.drawable.star_active);
			imageViewStarFour.setImageResource(R.drawable.star_active);
			imageViewStarFive.setImageResource(R.drawable.star_active);
			break;
		case R.id.ButtonSendFeedBack:
			if (flag_count == 0)
				Util.alertMessage(RatingSetScreen.this, "Please Give Rating.");
			else if (editComment.getText().toString().equals(""))
				Util.alertMessage(RatingSetScreen.this, "Please Give Comment.");
			else {

				// Building Parameters
				driverId = prefs.getString("tripdriverid", "");
				riderId = prefs.getString("tripriderid", "");
				sender =prefs.getString("mode", "");
				if(prefs.getString("activetripid", "").equals(""))
				{
					if(prefs.getString("mode", "").equals("rider"))
					{
						tripId = SingleTon.getInstance().getDriverTripId();
						}
					else
					{
						tripId=SingleTon.getInstance().getReceivednotificationTripID();
						}
					
				}
				else
				{
					tripId=prefs.getString("activetripid", "");
				
				}
				rating = String.valueOf(flag_count);
				feedback = editComment.getText().toString();
				Log.e("tag", "Rating:" + rating+" "+driverId+"sender="+sender);
				Log.e("tag", "FeedBack:" + feedback+" "+riderId);
				if(Util.isNetworkAvailable(RatingSetScreen.this)){

				ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
				nameValuePair.add(new BasicNameValuePair("DriverId", driverId));
				nameValuePair.add(new BasicNameValuePair("RiderId", riderId));
				nameValuePair.add(new BasicNameValuePair("Sender", sender));
				nameValuePair.add(new BasicNameValuePair("TripId", tripId));
				nameValuePair.add(new BasicNameValuePair("Rating", rating));
				nameValuePair.add(new BasicNameValuePair("Feedback", feedback));

				AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(RatingSetScreen.this, methodRiderToDriverRating,
						nameValuePair,true,"Please wait...");
				mWebPageTask.delegate = (AsyncResponseForZira) RatingSetScreen.this;
				mWebPageTask.execute();
				}
				else
				{
					Util.alertMessage(RatingSetScreen.this, "Please check your internet connection");
					}
			}
		default:
			break;
		}
	}

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		Log.e("Rating view", output);
		if (methodName.equals(methodRiderToDriverRating)) {
			try {
				JSONObject jsonObj = new JSONObject(output);
				result = jsonObj.getString("result");
				Editor e =prefs.edit();
				e.putString("activetripid", "");
				e.commit();
				
				String message = jsonObj.getString("message");
				if (result.equals("0")){
					if(prefs.getString("mode", "").equals("rider")){
						
						Intent i=new Intent(RatingSetScreen.this,TipView.class);
						i.putExtra("newfare", getIntent().getStringExtra("newfare"));
						startActivity(i);		
						finish();
						
					}
					else{
						DriverModeActivity.fromActivtyDriverNotification =0;
						Intent mIntent =new Intent(RatingSetScreen.this,DriverModeActivity.class);
						mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(mIntent);						
						finish();
					}
				}
				else
					Util.alertMessage(RatingSetScreen.this, message);

			} catch (Exception e) {
				Util.alertMessage(RatingSetScreen.this, "Error :: "+e.getMessage());
			}

		}
	}
	@Override
	public void onBackPressed() {
	
	}
}
