package com.twentyfourseven.zira;

import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;
import com.twentyfourseven.zira.R;
import com.twentyfourseven.zira.VehicleSearchActivity.distanceCalculatorAsyncTask;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.MainVehicleModel;
import com.zira.util.SingleTon;
import com.zira.util.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CalculateFareActivity extends Activity {

	private TextView textView_ShowFare,txtDestination,txtPickUpLoc;
	private ImageView cancelImageView;
	private MainVehicleModel vehicleModel;
	private int distanceValue,timeValue,approxMinutes;
	private float minvalue=0,suggestion=100,per_minvalue=0,per_maxvalue=0,maxvalue=0,basic_fare;
	private float totalKms =00,totalKmInMiles=00;
	private float actualfare;
	String datetimepick=null,myAddress,str_currentdate=null,time,requestType,distance,numberpicker_no,tripid;
	private LinearLayout sourceRelativeLayout,destinationRelativeLayout;
	public static int flag;
	public static int from=0;
	protected static double gettingSourceLat;
	protected static double gettingSourceLong;
	protected static String sourceAddress;
	protected static double gettingdestinationLat;
	protected static double gettingDestinationLong;
	protected static String destinationAddres;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fare_estimate);

		initialiseVariable();
		initialiseListener();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(from==1){
			if(Util.isNetworkAvailable(CalculateFareActivity.this)){
			new distanceCalculatorAsyncTask().execute();
			}
			else
			{
				Util.alertMessage(CalculateFareActivity.this, "Please check your internet connection");
				}
		}
	}
	
	private void initialiseVariable() {
		
		vehicleModel=SingleTon.getInstance().getMainVehicleModel();
		textView_ShowFare=(TextView)findViewById(R.id.textView_ShowFare);
		txtDestination=(TextView)findViewById(R.id.txtDestination);
		txtPickUpLoc=(TextView)findViewById(R.id.txtPickUpLoc);
		cancelImageView=(ImageView)findViewById(R.id.cancelImageView);
		sourceRelativeLayout=(LinearLayout)findViewById(R.id.sourceRelativeLayout);
		destinationRelativeLayout=(LinearLayout)findViewById(R.id.destinationRelativeLayout);
		
		gettingSourceLat=VehicleSearchActivity.gettingSourceLat;
	    gettingSourceLong=VehicleSearchActivity.gettingSourceLong;
		sourceAddress=VehicleSearchActivity.sourceAddress;
		
		gettingdestinationLat=VehicleSearchActivity.gettingdestinationLat;
		gettingDestinationLong=VehicleSearchActivity.gettingDestinationLong;
		destinationAddres=VehicleSearchActivity.destinationAddres;
		
	}

	private void initialiseListener() {
		
		sourceRelativeLayout.setOnClickListener(new OnClickListener() {
	
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag=1;
				startActivity(new Intent(CalculateFareActivity.this,LocationChangeToCheckFareActivity.class));
			}
		});
		
		destinationRelativeLayout.setOnClickListener(new OnClickListener() {
		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag=2;
				startActivity(new Intent(CalculateFareActivity.this,LocationChangeToCheckFareActivity.class));
			}
		});

		cancelImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {			
				finish();
			}
		});
		
		String newFare=new DecimalFormat("##.##").format(VehicleSearchActivity.actualfare);		
		textView_ShowFare.setText("$"+newFare);
		txtDestination.setText(VehicleSearchActivity.destinationAddres);
		txtPickUpLoc.setText(VehicleSearchActivity.sourceAddress);

	}


	//****************************Async to Calculate Distance **********************************//	
		class distanceCalculatorAsyncTask extends AsyncTask<String, String, String>{

			ProgressDialog pDialog;
			
			@Override
			protected void onPreExecute() {
				
				pDialog = new ProgressDialog(CalculateFareActivity.this);
				pDialog.setTitle("Zira 24/7");
				pDialog.setMessage("Loading data...");
				pDialog.setCancelable(false);
				pDialog.show();
			}		    

			@Override
			protected String doInBackground(String... str) {
				Log.i("tag", "Current Lati:"+gettingSourceLat+"  Current Longitude:"+gettingSourceLong+"  Destination_Lati:"+gettingdestinationLat+" Destination Longitude:"+gettingDestinationLong);
				String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+gettingSourceLat+","+gettingSourceLong+"&destinations="+gettingdestinationLat+","+gettingDestinationLong+"&mode=driving&language=en-EN&sensor=false";

				try{
					HttpGet httpGet = new HttpGet(distance_url);
					HttpClient client = new DefaultHttpClient();
					HttpResponse response;
					StringBuilder stringBuilder = new StringBuilder();
					response = client.execute(httpGet);
					HttpEntity entity = response.getEntity();
					InputStream stream = entity.getContent();
					int b;

					while ((b = stream.read()) != -1) {
						stringBuilder.append((char) b);
					}

					Log.e("result",stringBuilder.toString());
					JSONObject jsonObject = new JSONObject();
					jsonObject = new JSONObject(stringBuilder.toString());
					myAddress = jsonObject.getJSONArray("origin_addresses").getString(0);
					JSONArray searchResults =  jsonObject.getJSONArray(("rows"));

					for(int i = 0; i<searchResults.length(); i++){
						JSONArray elementArray = searchResults.getJSONObject(i).getJSONArray("elements");
						for(int j =0; j<elementArray.length(); j++){
							distance = elementArray.getJSONObject(j).getJSONObject("distance").getString("text");
							distanceValue = elementArray.getJSONObject(j).getJSONObject("distance").getInt("value");

							totalKms = (float)(distanceValue*0.001);
							totalKmInMiles=(float) (totalKms*0.62137);
							Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);
												
							time    = elementArray.getJSONObject(j).getJSONObject("duration").getString("text");
							timeValue = elementArray.getJSONObject(j).getJSONObject("duration").getInt("value");

							float temp= (float)timeValue/60;
							approxMinutes = Math.round(temp);
							Log.e("result","Option "+" :: "+distance);
							System.err.println("time"+ time);
						}
					}
					Log.e("done","done");

				}catch(Exception e){
					Log.e("error","error");
					e.printStackTrace();
				}

				return "success";
			}


			@Override
			protected void onPostExecute(String result) {
				pDialog.dismiss();
				if(result.equals("success")){	
					
					calculateFare(VehicleSearchActivity.str_vehicleType);
					
					String newFare=new DecimalFormat("##.##").format(actualfare);		
					textView_ShowFare.setText(""+newFare);
					txtDestination.setText(destinationAddres);
					txtPickUpLoc.setText(sourceAddress);				
					
					
				}
			}
		}
		
		//******************************Method to calculate Fare***********************************//
		public void calculateFare(String vehicleType) {

			if(vehicleType.equalsIgnoreCase("ZiraE")){
				try{
					actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getBasePrice())+
						(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice())
								+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice())));
			

					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getSurgeprice());
					if(surgePrice>0.0)
					{
						actualfare=actualfare*surgePrice;
						}
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				}catch(Exception e){
					System.err.println("actual="+e);
				}
			} 
			else if(vehicleType.equalsIgnoreCase("ZiraPlus")){
				try{
					actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getBasePrice())+
						(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice())
								+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice())));
				

					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getSurgeprice());
					if(surgePrice>0.0)
					{
						actualfare=actualfare*surgePrice;
						}
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				}catch(Exception e){
					System.err.println("actual="+e);
				}
			}
			else if(vehicleType.equalsIgnoreCase("ZiraTaxi")){
				try{
					actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getBasePrice())+
						(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice())
								+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice())));
				

					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getSurgeprice());
					if(surgePrice>0.0)
					{
						actualfare=actualfare*surgePrice;
						}
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				}catch(Exception e){
					System.err.println("actual="+e);
				}
			}
			else if(vehicleType.equalsIgnoreCase("ZiraLux")){
				try{
					actualfare=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getBasePrice())+
						(approxMinutes*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice())
								+(totalKmInMiles*Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice())));
			

					float surgePrice=Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getSurgeprice());
					if(surgePrice>0.0)
					{
						actualfare=actualfare*surgePrice;
						}
					actualfare=actualfare+Float.parseFloat(vehicleModel.getSafetyFee());
				}catch(Exception e){
					System.err.println("actual="+e);
				}
			}
		}}
