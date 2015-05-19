package com.rapidride;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

@SuppressLint("ResourceAsColor")
public class DriverRating_Activity  extends Activity{
	
	ImageView iv_one,iv_two,iv_three,iv_four,iv_five;
	EditText et_comment;
	Button btn_done,btn_favoritedriver;
	
	String zone_id,zip_code,reg_price,xl_price,exec_price,lux_price,truck_price,
	latlong,reg_base,xl_base,exec_base,lux_base,truck_base,reg_min,xl_min,exec_min,
	lux_min,truck_min,suv_price,suv_base,suv_min,reg_surge,xl_surge,exec_surge,lux_surge,
	suv_surge,truck_surge,reg_hour,xl_hour,exec_hour,suv_hour,lux_hour,reg_hourfull,
	xl_hourfull,exec_hourfull,suv_hourfull,lux_hourfull,truck_hour,truck_hourfull,
	lat_a,long_a,lat_b,long_b,lat_c,long_c,lat_d,long_d,
	reg_minbase,xl_minbase,exec_minbase,suv_minbase,lux_minbase,truck_minbase;
	String selectedVehicleType="", str_addfavorite=null,str_comments=null,str_currentdate;
	int count1=0,count2=0,count3=0,count4=0,count5=0,flag_count=0,add_count=0,exception=0;
	float new_actualfare;
	
	
	SharedPreferences prefs;
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	

	
	
	
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driverrating_activity);
		
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	    Calendar c = Calendar.getInstance();
		str_currentdate = df.format(c.getTime());
		System.err.println("str_c"+str_currentdate); 
				
		btn_favoritedriver=(Button)findViewById(R.id.button_favoritedriver);
		
		if(!(getIntent().getStringExtra("value")==null))
		{
			if(getIntent().getStringExtra("value").equals("driver"))
			{
				btn_favoritedriver.setVisibility(View.GONE);
				}
			}
		
		btn_favoritedriver.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				add_count++;
				if(add_count==1){
					str_addfavorite="str";
					btn_favoritedriver.setBackgroundResource(R.color.gray);
					btn_favoritedriver.setTextColor(Color.WHITE);
				}
				else if(add_count==2)
				{
					str_addfavorite="";
					btn_favoritedriver.setBackgroundResource(R.color.white);
					btn_favoritedriver.setTextColor(Color.BLACK);
					add_count=0;
				}
						
			}
		});
		et_comment=(EditText)findViewById(R.id.editText_comments);
		iv_one=(ImageView)findViewById(R.id.imageViewIconInfoWindow);
		iv_two=(ImageView)findViewById(R.id.imageView2);
		iv_three=(ImageView)findViewById(R.id.imageView3);
		iv_four=(ImageView)findViewById(R.id.imageView4);
		iv_five=(ImageView)findViewById(R.id.imageView5);
		
		str_comments=et_comment.getText().toString();
		
/***  flags image buttons on click	*/
		iv_one.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flag_count=1;
				count1++;
				if(count1==1){
					iv_one.setImageResource(R.drawable.red_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					
					}
				else if(count1==2)
				{
					count1=0;count2=0;count3=0;count4=0;count5=0;
					iv_one.setImageResource(R.drawable.black_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					flag_count=0;
						}
			}
		});
		iv_two.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flag_count=2;
				count2++;
				if(count2==1){
					iv_one.setImageResource(R.drawable.red_flag_white);
					iv_two.setImageResource(R.drawable.red_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					
					}
				else if(count2==2)
				{
					count1=0;count2=0;count3=0;count4=0;count5=0;
					iv_one.setImageResource(R.drawable.black_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					flag_count=0;
						}
			}
		});
		iv_three.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				flag_count=3;
				count3++;
				if(count3==1){
					iv_one.setImageResource(R.drawable.red_flag_white);
					iv_two.setImageResource(R.drawable.red_flag_white);
					iv_three.setImageResource(R.drawable.red_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					
					}
				else if(count3==2)
				{
					count1=0;count2=0;count3=0;count4=0;count5=0;
					iv_one.setImageResource(R.drawable.black_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					flag_count=0;
						}
			}
		});
		iv_four.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flag_count=4;
				count4++;
				if(count4==1){
					iv_one.setImageResource(R.drawable.green_flag_white);
					iv_two.setImageResource(R.drawable.green_flag_white);
					iv_three.setImageResource(R.drawable.green_flag_white);
					iv_four.setImageResource(R.drawable.green_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					
					}
				else if(count4==2)
				{
					count1=0;count2=0;count3=0;count4=0;count5=0;
					iv_one.setImageResource(R.drawable.black_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					flag_count=0;
						}
				
			}
		});
		iv_five.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				flag_count=5;
					count5++;
				if(count5==1){
					iv_one.setImageResource(R.drawable.green_flag_white);
					iv_two.setImageResource(R.drawable.green_flag_white);
					iv_three.setImageResource(R.drawable.green_flag_white);
					iv_four.setImageResource(R.drawable.green_flag_white);
					iv_five.setImageResource(R.drawable.green_flag_white);
				
					}
				else if(count5==2)
				{
					count1=0;count2=0;count3=0;count4=0;count5=0;
					iv_one.setImageResource(R.drawable.black_flag_white);
					iv_two.setImageResource(R.drawable.black_flag_white);
					iv_three.setImageResource(R.drawable.black_flag_white);
					iv_four.setImageResource(R.drawable.black_flag_white);
					iv_five.setImageResource(R.drawable.black_flag_white);
					flag_count=0;
						}
			}
		});
		if(getIntent().getStringExtra("again")!=null)
		{
			new FetchVehicleList().execute();
			}
		btn_done=(Button)findViewById(R.id.button_done);
		btn_done.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				str_comments=et_comment.getText().toString();
				
				 if(flag_count==5)
				{
					 if(Utility.isConnectingToInternet(DriverRating_Activity.this))
		            	{
		            	 	new RiderToDriverRating().execute(); /**asyn task for Rating */
		            		}
		            	else{
		            		Utility.alertMessage(DriverRating_Activity.this,"error in internet connection");
		            	}
		          	}
				
			 else
					{
						
				 
					  if(flag_count==0)
						{
							Utility.alertMessage(DriverRating_Activity.this,"Please rate first");
							
								}
					  else if(str_comments.equals(""))
						{
							Utility.alertMessage(DriverRating_Activity.this,"Please comment first");
													
							System.err.println("iffff");
							}
						
						else
						{
							if(Utility.isConnectingToInternet(DriverRating_Activity.this))
			            	{
			            	 	new RiderToDriverRating().execute(); /**asyn task for Rating */
			            		}
			            	else{
			            		Utility.alertMessage(DriverRating_Activity.this,"error in internet connection");
			            	}
						}
						
						 }

								
				}
		});
}
	
	
	
	
	
	
	
	
	
	
/***************************End main function********************************************/
	
/**RiderToDriver Rating class */
	private class RiderToDriverRating extends AsyncTask<Void, Void, Void> { 
			ProgressDialog pDialog;
		protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				pDialog = new ProgressDialog(DriverRating_Activity.this);
				exception=0;
				//pDialog.setTitle("Loading");
				pDialog.setMessage("Please wait...");
				pDialog.setCancelable(false);
				pDialog.show();
				}
		protected Void doInBackground(Void... arg0) {
			
				try {
				 parsing();
				 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				}
				
				return null;
				}
		protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				
				pDialog.dismiss();
				
				System.err.println("Riderid="+getIntent().getStringExtra("riderid"));
			   Log.e("tag","tripid="+getIntent().getStringExtra("tripid"));
			   if(getIntent().getStringExtra("cancel")!=null)
				{
				   Intent i=new Intent(DriverRating_Activity.this,DriverView_Activity.class);
				   startActivity(i);
					}
			   else
			   {
			   
				Intent i=new Intent(DriverRating_Activity.this,RideFinish.class);
				i.putExtra("riderid", getIntent().getStringExtra("riderid"));
				i.putExtra("tripid", getIntent().getStringExtra("tripid"));
				if(getIntent().getStringExtra("again")!=null)
				{
					i.putExtra("fare", new_actualfare);
				}
				else
				{
					i.putExtra("fare", getIntent().getStringExtra("fare"));
					}
				i.putExtra("driverid", getIntent().getStringExtra("driverid"));
				i.putExtra("vehicleType", getIntent().getStringExtra("vehicleType"));
				
				i.putExtra("DesLong", getIntent().getStringExtra("DesLong"));
				i.putExtra("DesLati", getIntent().getStringExtra("DesLati"));
				i.putExtra("DesAddress", getIntent().getStringExtra("DesAddress"));
				
				i.putExtra("CusLong", getIntent().getStringExtra("CusLong"));
				i.putExtra("CusLati", getIntent().getStringExtra("CusLati"));
				i.putExtra("CusAddress", getIntent().getStringExtra("CusAddress"));
				
				
				Log.d("tag", "Getting RiderId:"+getIntent().getStringExtra("riderid"));
				Log.d("tag", "Getting tripID:"+getIntent().getStringExtra("tripid"));
				Log.d("tag", "Getting Fare:"+getIntent().getStringExtra("fare"));
				Log.d("tag", "Getting driverid:"+getIntent().getStringExtra("driverid"));
				Log.d("tag", "Getting vehicleType:"+getIntent().getStringExtra("vehicleType"));
				
				Log.d("tag", "Getting DesLong:"+getIntent().getStringExtra("DesLong"));
				Log.d("tag", "Getting DesLati:"+getIntent().getStringExtra("DesLati"));
				Log.d("tag", "Getting DesAddress:"+getIntent().getStringExtra("DesAddress"));
				
				if(exception==1)
				{
					Utility.alertMessage(DriverRating_Activity.this,"Internet connection failed. Please Try again later.");
				}
				else if(!(getIntent().getStringExtra("value")==null)) {
					  if(getIntent().getStringExtra("value").equals("rider"))
					  {
						  i.putExtra("value", "rider"); 
					  	}
					  else if(getIntent().getStringExtra("value").equals("driver"))
					  {
						  i.putExtra("value", "driver");  
					  	}
					 }
				finish();
				startActivity(i);
			   }
				
				}
			
		/**pending request code parsing function */
		public void parsing() throws JSONException {
				try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/RiderToDriverRating");//Url_Address.url_promocode);
				JSONObject json = new JSONObject();
				
				json.put("Trigger", "RiderToDriverRating");
				System.err.println("Rider To Driver Rating");
				
				json.put("TripId", getIntent().getStringExtra("tripid"));
				System.err.println("tripid="+getIntent().getStringExtra("tripid"));
				json.put("Rating", flag_count);
				json.put("Comments", str_comments);
				System.err.println("str_comments->"+str_comments);
				if(!(str_addfavorite==null))
				{
					System.err.println("str_addfavorite->"+null);
					json.put("Favorite_driver", getIntent().getStringExtra("driverid"));
					}
				else
				{
					json.put("Favorite_driver", "");
					}
				
				if(getIntent().getStringExtra("value")!=null)
				{
					if(getIntent().getStringExtra("value").equals("rider"))
					{
					json.put("Trigger", "Rider");
						}
					else if(getIntent().getStringExtra("value").equals("driver"))
					{
					json.put("Trigger", "Driver");
						}
					}
			      //	      
				httpost.setEntity(new StringEntity(json.toString()));
				httpost.setHeader("Accept", "application/json");
				httpost.setHeader("Content-type", "application/json");
				
				HttpResponse response = client.execute(httpost);
				HttpEntity resEntityGet = response.getEntity();
				String jsonstr=EntityUtils.toString(resEntityGet);
				if(jsonstr!=null)
				{
				 Log.d("RiderToDriverRating","result-->>>>>    "+ jsonstr);
				 
					}
					JSONObject obj=new JSONObject(jsonstr);
				String	jsonResult=obj.getString("result");
				String	pending_jsonMessage=obj.getString("message");
			
					Log.d("RiderToDriverRating:", "Result: "+jsonResult);
					Log.d("RiderToDriverRating:", "Message :"+pending_jsonMessage);
		
				}
				  catch(Exception e){
				   System.out.println(e);
				   exception=1;
				   Log.d("tag", "Error :"+e); }  
					}
				}
		
	/***AsyncTask class for fetch vehicle list**////
	 public class FetchVehicleList extends AsyncTask<Void, Void, Void> {
	 protected void onPreExecute() {
//		 exeception=0;
	 super.onPreExecute();
			}
		 	protected Void doInBackground(Void... arg0) {
		 		try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
					HttpClient client = new DefaultHttpClient(httpParameters);
					HttpPost httpost = new HttpPost(Url_Address.url_Home+"/FetchVehicleList");
					JSONObject json = new JSONObject();
							
					json.put("Trigger", "FetchVehicleList");
//					System.err.println("FetchVehicleList");
					
					json.put("longitude", "76.465444");
//					System.err.println("longitude="+longitude);
					 	
				    json.put("latitude", "30.64949");
//				    System.err.println("latitude="+latitude);
				    
			        json.put("currenttime",str_currentdate);
//			        System.err.println("str_currentdate-->"+str_currentdate);
			      
			        json.put("distance",10);
			        
			        json.put("riderid",getIntent().getStringExtra("riderid"));
			        System.err.println("riderid-"+prefs.getString("userid",null));
				     
		
			        httpost.setEntity(new StringEntity(json.toString()));
					httpost.setHeader("Accept", "application/json");
					httpost.setHeader("Content-type", "application/json");
					
					HttpResponse response = client.execute(httpost);
					HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					
					if(jsonstr!=null)
					{
					// Log.i("FetchVehicleList","result vehicle-->   "+ jsonstr);
					}
					JSONObject obj=new JSONObject(jsonstr);
			String 		jsonResult_fetchvehicle=obj.getString("result");
			String		jsonMessage_fetchvehicle=obj.getString("message");
					
//					Log.i("FetchVehicleList:", "Result: "+jsonResult_fetchvehicle);
//					Log.i("FetchVehicleList:", "Message :"+jsonMessage_fetchvehicle);
					
//					String vehiclelist=obj.getString("ListVehicleInfo");
////					System.err.println("list "+vehiclelist);
//					Log.i("tag","FetchVehicle List:"+vehiclelist);
//					
//					
//					JSONArray jsonarray=obj.getJSONArray("ListVehicleInfo");
//					Utility.arraylist_driverInfo().clear();
//					
//					for(int i=0;i<jsonarray.length();i++)
//					{
//						
//						JSONObject obj2=jsonarray.getJSONObject(i);
//						BeanDriver driverObj=new BeanDriver();
//						String driverid=obj2.getString("driverid");
////						Log.i("ListVehicleInfo:", "driverid: "+driverid);
//						driverObj.setDriverID(driverid);
//						
//						String	firstname=	obj2.getString("firstname");
////						Log.i("ListVehicleInfo:", "firstname: "+firstname);
//						
//						String lastname=	obj2.getString("lastname");
////						Log.i("ListVehicleInfo:", "lastname: "+lastname);
//						
//						String driverimage=	obj2.getString("driverimage");
////						Log.i("ListVehicleInfo:", "driverimage: "+driverimage);
//						
//						String vehicleimage=obj2.getString("vehicleimage");
////						Log.i("ListVehicleInfo:", "validationstatus: "+vehicleimage);
//						
//						str_vehicletype=obj2.getString("vehicletype");
////						Log.i("ListVehicleInfo:", "vehicletype: "+str_vehicletype);
//						driverObj.setVehicleType(str_vehicletype);
//						
//						String	longitude=	obj2.getString("longitude");
////						Log.i("ListVehicleInfo:", "longitude: "+longitude);
//						driverObj.setDriverLongitude(longitude);
//						
//						String latitude=obj2.getString("latitude");
////						Log.i("ListVehicleInfo:", "latitude: "+latitude);
//						driverObj.setDriverLatitude(latitude);
//						
//						String distance=	obj2.getString("distance");
////						Log.i("ListVehicleInfo:", "distance: "+distance);
//						driverObj.setDriverDistance(distance);
//						
//						if(!(longitude==null))
//						{
//							try {
//				                  longi = Double.parseDouble(longitude);
//				                  lati= Double.parseDouble(latitude);
//				              } catch (NumberFormatException nfe) {
////				                 System.out.println("NumberFormatException: " + nfe.getMessage());
//				              }
//						}
//						
//						Driver_Coordinates d_cdn = new Driver_Coordinates();
//						d_cdn.latitude=lati;
//						d_cdn.longitude=longi;
//						vehicle_coordinates.add(d_cdn);
//						
//						if(!(str_vehicletype==null))
//						{
//							int int_value=Integer.parseInt(str_vehicletype);
////							Editor ed=prefs.edit();
////							ed.putInt("Getvehicletype",int_value);
////							ed.commit();
//							vehicleType.add(int_value);
//						}
//						
//						
//						//Utility.arraylist_longitude().add(longitude);
//					    Utility.arraylist_drivername().add(firstname+lastname);
//					    
//						Utility.arraylist_driverInfo().add(driverObj);
//						
//						Log.d("tag","Size Of driver info:"+Utility.arraylist_driverInfo().size());
//					}
					
					JSONArray jsonarray_ListVehicleInfo=obj.getJSONArray("ListZoneInfo");
					
					for(int i=0;i<jsonarray_ListVehicleInfo.length();i++)
					{
						
						JSONObject obj3=jsonarray_ListVehicleInfo.getJSONObject(i);
					//	Editor ed=prefs.edit();
						String zone_id=obj3.getString("zone_id");
						Log.i("ListZoneInfo:", "zone_id: "+zone_id);
						//ed.putString("zone_id",zone_id);
						
						zip_code=	obj3.getString("zip_code");
						Log.i("ListZoneInfo:", "zip_code: "+zip_code);
						//ed.putString("zip_code",zip_code);
						
						reg_price=	obj3.getString("reg_price");
						Log.i("ListZoneInfo:", "reg_price: "+reg_price);
						//ed.putString("reg_price",reg_price);
						
						 xl_price=	obj3.getString("xl_price");
						Log.i("ListZoneInfo:", "xl_price: "+xl_price);
						//ed.putString("xl_price",xl_price);
						
						 exec_price=	obj3.getString("exec_price");
//						Log.i("ListZoneInfo:", "exec_price: "+exec_price);
						//ed.putString("exec_price",exec_price);
						
						 lux_price=obj3.getString("lux_price");
//						Log.i("ListZoneInfo:", "lux_price: "+lux_price);
						//ed.putString("lux_price",lux_price);
						
						truck_price=	obj3.getString("truck_price");
//						Log.i("ListZoneInfo:", "truck_price: "+truck_price);
						//ed.putString("truck_price",truck_price);
						
						latlong=	obj3.getString("latlong");
//						Log.i("ListZoneInfo:", "latitude: "+latlong);
						//ed.putString("latlong",latlong);
						
						reg_base=obj3.getString("reg_base");
//						Log.i("ListZoneInfo:", "reg_base: "+reg_base);
						//ed.putString("reg_base",reg_base);
						
					 	xl_base=	obj3.getString("xl_base");
//						Log.i("ListZoneInfo:", "xl_base: "+xl_base);
					//	ed.putString("xl_base",xl_base);
						
						 exec_base=	obj3.getString("exec_base");
//						Log.i("ListZoneInfo:", "exec_base: "+exec_base);
					//	ed.putString("exec_base",exec_base);
						
						 lux_base=	obj3.getString("lux_base");
//						Log.i("ListZoneInfo:", "lux_base: "+lux_base);
						//ed.putString("lux_base",lux_base);
						
						 truck_base=	obj3.getString("truck_base");
//						Log.i("ListZoneInfo:", "truck_base: "+truck_base);
					//	ed.putString("truck_base",truck_base);
						
						 reg_min=obj3.getString("reg_min");
//						Log.i("ListZoneInfo:", "reg_min: "+reg_min);
					//	ed.putString("reg_min",reg_min);
						
						 xl_min=	obj3.getString("xl_min");
//						Log.i("ListZoneInfo:", "xl_min: "+xl_min);
					//	ed.putString("xl_min",xl_min);
						
						 exec_min=	obj3.getString("exec_min");
//						Log.i("ListZoneInfo:", "exec_min: "+exec_min);
						//ed.putString("exec_min",exec_min);
						
						 lux_min=obj3.getString("lux_min");
//						Log.i("ListZoneInfo:", "lux_min: "+lux_min);
						//ed.putString("lux_min",lux_min);
						
						truck_min=	obj3.getString("truck_min");
//						Log.i("ListZoneInfo:", "truck_min: "+truck_min);
						//ed.putString("truck_min",truck_min);
						
						suv_price=	obj3.getString("suv_price");
//						Log.i("ListZoneInfo:", "suv_price: "+suv_price);
						//ed.putString("suv_price",suv_price);
						
						suv_base=	obj3.getString("suv_base");
//						Log.i("ListZoneInfo:", "suv_base: "+suv_base);
						//ed.putString("suv_base",suv_base);
						
						 suv_min=	obj3.getString("suv_min");
//						Log.i("ListZoneInfo:", "suv_min: "+suv_min);
						//ed.putString("suv_min",suv_min);
						
						 reg_surge=obj3.getString("reg_surge");
//						Log.i("ListZoneInfo:", "reg_surge: "+reg_surge);
						//ed.putString("reg_surge",reg_surge);
						
						xl_surge=	obj3.getString("xl_surge");
//						Log.i("ListZoneInfo:", "xl_surge: "+xl_surge);
						//ed.putString("xl_surge",xl_surge);
						
						 exec_surge=	obj3.getString("exec_surge");
//						Log.i("ListZoneInfo:", "exec_surge"+exec_surge);
						//ed.putString("exec_surge",exec_surge);
						
						 lux_surge=obj3.getString("lux_surge");
//						Log.i("ListZoneInfo:", "lux_surge: "+lux_surge);
						//ed.putString("lux_surge",lux_surge);
						
						suv_surge=	obj3.getString("suv_surge");
//						Log.i("ListZoneInfo:", "suv_surge: "+suv_surge);
						//ed.putString("suv_surge",suv_surge);
						
						truck_surge=	obj3.getString("truck_surge");
//						Log.i("ListZoneInfo:", "truck_surge: "+truck_surge);
						//ed.putString("truck_surge",truck_surge);
						
						reg_hour=obj3.getString("reg_hour");
//						Log.i("ListZoneInfo:", "reg_hour: "+reg_hour);
						//ed.putString("reg_hour",reg_hour);
						
						xl_hour=obj3.getString("xl_hour");
//						Log.i("ListZoneInfo:", "xl_hour: "+xl_hour);
						//ed.putString("xl_hour",xl_hour);
						
						 exec_hour=obj3.getString("exec_hour");
//						Log.i("ListZoneInfo:", "exec_hour: "+exec_hour);
						//ed.putString("exec_hour",exec_hour);
						
						suv_hour=	obj3.getString("suv_hour");
//						Log.i("ListZoneInfo:", "suv_hour: "+suv_hour);
						//ed.putString("suv_hour",suv_hour);
						
						lux_hour=	obj3.getString("lux_hour");
//						Log.i("ListZoneInfo:", "lux_hour: "+lux_hour);
						//ed.putString("lux_hour",lux_hour);
						
						 reg_hourfull=	obj3.getString("reg_hourfull");
//						Log.i("ListZoneInfo:", "reg_hourfull: "+reg_hourfull);
						//ed.putString("reg_hourfull",reg_hourfull);
						
						 xl_hourfull=obj3.getString("xl_hourfull");
//						Log.i("ListZoneInfo:", "xl_hourfull: "+xl_hourfull);
						//ed.putString("xl_hourfull",xl_hourfull);
						
						exec_hourfull=	obj3.getString("exec_hourfull");
//						Log.i("ListZoneInfo:", "exec_hourfull: "+exec_hourfull);
						//ed.putString("exec_hourfull",exec_hourfull);
						
						suv_hourfull=	obj3.getString("suv_hourfull");
//						Log.i("ListZoneInfo:", "suv_hourfull"+suv_hourfull);
						//ed.putString("suv_hourfull",suv_hourfull);
						
						 lux_hourfull=obj3.getString("lux_hourfull");
//						Log.i("ListZoneInfo:", "lux_hourfull: "+lux_hourfull);
						//ed.putString("lux_hourfull",lux_hourfull);
						
						truck_hour=	obj3.getString("truck_hour");
//						Log.i("ListZoneInfo:", "truck_hour: "+truck_hour);
						//ed.putString("truck_hour",truck_hour);
						
						 truck_hourfull=	obj3.getString("truck_hourfull");
//						Log.i("ListZoneInfo:", "truck_hourfull: "+truck_hourfull);
					//	ed.putString("truck_hourfull",truck_hourfull);
						
						 lat_a=obj3.getString("lat_a");
//						Log.i("ListZoneInfo:", "lat_a: "+lat_a);
						//ed.putString("lat_a",lat_a);
						
						 long_a=obj3.getString("long_a");
//						Log.i("ListZoneInfo:", "long_a: "+long_a);
						//ed.putString("long_a",long_a);
						
						 lat_b=obj3.getString("lat_b");
//						Log.i("ListZoneInfo:", "lat_b: "+lat_b);
						//ed.putString("lat_b",lat_b);
						
							long_b=	obj3.getString("long_b");
//						Log.i("ListZoneInfo:", "long_b: "+long_b);
						//ed.putString("long_b",long_b);
						
							lat_c= obj3.getString("lat_c");
//						Log.i("ListZoneInfo:", "lat_c: "+lat_c);
						//ed.putString("lat_c",lat_c);
					
							long_c=	obj3.getString("long_c");
//						Log.i("ListZoneInfo:", "long_c: "+long_c);
						//ed.putString("long_c",long_c);
						
						lat_d=	obj3.getString("lat_d");
//						Log.i("ListZoneInfo:", "lat_d: "+lat_d);
						//ed.putString("lat_d",lat_d);
						
						 long_d=	obj3.getString("long_d");
//						Log.i("ListZoneInfo:", "long_d: "+long_d);
						//ed.putString("long_d",long_d);
											
					 reg_minbase=obj3.getString("reg_minbase");
						Log.i("reg_minbase:", "reg_minbase: "+reg_minbase);
						//ed.putString("reg_minbase",reg_minbase);
						
							xl_minbase=	obj3.getString("xl_minbase");
						Log.i("xl_minbase:",  xl_minbase);
						//ed.putString("xl_minbase",xl_minbase);
						
						 exec_minbase= obj3.getString("exec_minbase");
						Log.i("exec_minbase:",  exec_minbase);
						
						//ed.putString("exec_minbase",exec_minbase);
					
						suv_minbase=	obj3.getString("suv_minbase");
						Log.i("suv_minbase:",  suv_minbase);
						//ed.putString("suv_minbase",suv_minbase);
						
						 lux_minbase=	obj3.getString("lux_minbase");
						Log.i("lux_minbase:",  lux_minbase);
						//ed.putString("lux_minbase",lux_minbase);
						
					 truck_minbase=	obj3.getString("truck_minbase");
						Log.i("truck_minbase:",  truck_minbase);
						
						//ed.putString("truck_minbase",truck_minbase);
				
						
						
						//ed.commit();				
					}
			}
					  catch(Exception e){
					   System.out.println(e);
//					   exeception=1;
					   Log.d("tag", "Error :"+e);
					   }
					
			
			return null;
		}

	protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			selectedVehicleType= getIntent().getStringExtra("vehicletype").replace(" ", ""); 
			String tempMinutes=getIntent().getStringExtra("newmin").replace(" ", "");
			String tempmile=getIntent().getStringExtra("newdistance").replace(" ", "");
			int approxMinutes=Integer.parseInt(tempMinutes);
			int	des_mile=Integer.parseInt(tempmile);
		    if(selectedVehicleType.equals("1"))
			{
				new_actualfare=Float.parseFloat(reg_base)+(approxMinutes*Float.parseFloat(reg_min)+(des_mile*Float.parseFloat(reg_price)));
				try{
					if(Float.parseFloat(reg_surge)>0)
						new_actualfare=new_actualfare*Float.parseFloat(reg_surge);
				float freg_minbase=Float.parseFloat(reg_minbase);
				if(new_actualfare<freg_minbase)
					{
						new_actualfare=freg_minbase;
						}
				}catch(Exception e)
				{
					}
				}
			else if(selectedVehicleType.equals("2"))
			{
				new_actualfare=Float.parseFloat(xl_base)+(approxMinutes*Float.parseFloat(xl_min)+(des_mile*Float.parseFloat(xl_price)));
				try{
					if(Float.parseFloat(xl_surge)>0)
						new_actualfare=new_actualfare*Float.parseFloat(xl_surge);
					float fxl_minbase=Float.parseFloat(xl_minbase);
					if(new_actualfare<fxl_minbase)
						{
							new_actualfare=fxl_minbase;
							}
					}catch(Exception e)
					{
						}
					}
			else if(selectedVehicleType.equals("3"))
			{
				new_actualfare=Float.parseFloat(exec_base)+(approxMinutes*Float.parseFloat(exec_min)+(des_mile*Float.parseFloat(exec_price)));
				try{
					if(Float.parseFloat(exec_surge)>0)
						new_actualfare=new_actualfare*Float.parseFloat(exec_surge);
					
					float fexec_minbase=Float.parseFloat(exec_minbase);
					if(new_actualfare<fexec_minbase)
						{
							new_actualfare=fexec_minbase;
							}
					}catch(Exception e)
					{
						}
					}
			else if(selectedVehicleType.equals("4"))
			{
				new_actualfare=Float.parseFloat(suv_base)+(approxMinutes*Float.parseFloat(suv_min)+(des_mile*Float.parseFloat(suv_price)));
			try{
				if(Float.parseFloat(suv_surge)>0)
					new_actualfare=new_actualfare*Float.parseFloat(suv_surge);
				
				float fsuv_minbase=Float.parseFloat(suv_minbase);
				if(new_actualfare<fsuv_minbase)
					{
						new_actualfare=fsuv_minbase;
						}
				}catch(Exception e)
				{}
				}
			else if(selectedVehicleType.equals("5"))
			{
				new_actualfare=Float.parseFloat(lux_base)+(approxMinutes*Float.parseFloat(lux_min)+(des_mile*Float.parseFloat(lux_price)));
				try{
					if(Float.parseFloat(lux_surge)>0)
						new_actualfare=new_actualfare*Float.parseFloat(lux_surge);
					float flux_minbase=Float.parseFloat(lux_minbase);
					if(new_actualfare<flux_minbase)
						{
							new_actualfare=flux_minbase;
							}
					}catch(Exception e)
					{}
					}
			Log.i("tag","actualfare"+new_actualfare);
		int	intvalueActualFare=(int)new_actualfare;
//
		Log.i("tag","intvalueActualFare="+intvalueActualFare);
			
	}
}
		public void onBackPressed() {
		
		
		}

}	

