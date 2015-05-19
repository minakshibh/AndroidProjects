package com.rapidride;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.rapidride.util.BeanDriver;
import com.rapidride.util.Coordinates;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class LocationandPriceDetail extends Activity{
	
	ImageButton imgbtn_homelocation;
	private ProgressDialog progress;
	private TextView lblDestAddress, tv_Distance,tv_time,tv_myprice;
	TextView lbl_startingaddess,lbl_destinationaddress,lbl_distance,lbl_rapidtitle,lblstatingAddress,textView_lblsp,
	lbl_outputstatingAddress,hDestLocation,hDistance,hTime,hMyPrice,hNameofPrice,textViewActualPrice;
	Button btn_edit_startpos,btn_edit_despos,btn_requestpickup, btn_distance,btn_editddatetime;
	DatePicker datepicker;
	TimePicker timepicker;
	public ProgressDialog pDialog;
	NumberPicker numberpicker;
	TextView tv_textView_nowtimedate;
	Typeface typeFace;
	private SharedPreferences prefs;
	Spinner sp_vehiclelist;
	
	StringBuilder datepicker_date;
	private float destLat, destLon, curLat, curLong,des_mile=0;
	float totalKms =00;
	Double latitude,longitude;
	DecimalFormat twoDForm;
	private int distanceValue,timeValue,approxMinutes;
	SimpleDateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");
	String datetimepick=null,myAddress,str_currentdate=null,time,requestType,distance,numberpicker_no,tripid;
	float minvalue=0,suggestion=100,per_minvalue=0,per_maxvalue=0,maxvalue=0,basic_fare;
	public String RequestRide_Result=null,RequestRide_Message,VIPCheck="",str_timepicker="";
	String zone_id,zip_code,reg_price,xl_price,exec_price,lux_price,truck_price,
	latlong,reg_base,xl_base,exec_base,lux_base,truck_base,reg_min,xl_min,exec_min,
	lux_min,truck_min,suv_price,suv_base,suv_min,reg_surge,xl_surge,exec_surge,lux_surge,
	suv_surge,truck_surge,reg_hour,xl_hour,exec_hour,suv_hour,lux_hour,reg_hourfull,
	xl_hourfull,exec_hourfull,suv_hourfull,lux_hourfull,truck_hour,truck_hourfull,
	lat_a,long_a,lat_b,long_b,lat_c,long_c,lat_d,long_d,
	reg_minbase,xl_minbase,exec_minbase,suv_minbase,lux_minbase,truck_minbase;
	String drivierID,vehicleType,driverLatitude,driverLongitude,driverDistance;
	String selectedVehicleType="", HH="",getTime="",selectedTime="",favtDriver="";
	
	ArrayList<String> driverlist=new ArrayList<String>();
	ArrayList<Double> driverDistances=new ArrayList<Double>();
	ArrayList<String> al_vehicletype=new ArrayList<String>();
	ArrayList<Coordinates> coordinates;
	
	float actualfare = 0;
	long currentdateintoInt,selectdateintoInt;
	int intvalueActualFare,exception=0,flag=0;

	boolean checkfavt=false;
	
	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location_price);
	
	    prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 	
	    favtDriver=prefs.getString("favoritedriver", "");
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		coordinates = new ArrayList<Coordinates>();
		driverlist.clear();
		
		/************Get Information of vehicles**************/
		if(Utility.arraylist_driverInfo().size()>0)
		{
			
			for(BeanDriver driverObj:Utility.arraylist_driverInfo())
				{
				// drivierID=driverObj.getDriverID();
				 vehicleType=driverObj.getVehicleType();
	//			 driverLatitude=driverObj.getDriverLatitude();
	//			 driverLongitude=driverObj.getDriverLongitude();
	//			 driverDistance=driverObj.getDriverDistance();
				 if(vehicleType.equals("1"))
				 {
					 al_vehicletype.add("REGULAR");
					 }
				 else if(vehicleType.equals("2"))
				 {
					 al_vehicletype.add("XL");
					 }
				 else if(vehicleType.equals("3"))
				 {
					 al_vehicletype.add("EXECUTIVE");
					 }
				 else if(vehicleType.equals("4"))
				 {
					 al_vehicletype.add("SUV");
					 }
				 else if(vehicleType.equals("5"))
				 {
					 al_vehicletype.add("LUXURY");
					 }
				 }
			HashSet<String> hs = new HashSet<String>();
			hs.addAll(al_vehicletype);
			al_vehicletype.clear();
			al_vehicletype.addAll(hs);
			}
		
//		int minDistance = driverDistances.indexOf(Collections.min(driverDistances));
//		for(int i=minDistance;i<minDistance;i++)
//		{
//		 BeanDriver driverObj=new BeanDriver();
//		 driverObj=Utility.arraylist_driverInfo().get(minDistance);
//		 drivierID=driverObj.getDriverID();
//		}
		/************************/
		zone_id=prefs.getString("zone_id","");
		zip_code=prefs.getString("zip_code","");
		reg_price=prefs.getString("reg_price","");
		xl_price=prefs.getString("xl_price","");
		exec_price=prefs.getString("exec_price","");
		lux_price=prefs.getString("lux_price","");
		truck_price=prefs.getString("truck_price","");
		reg_base=prefs.getString("reg_base","");
		xl_base=prefs.getString("xl_base","");
		exec_base=prefs.getString("exec_base","");
		lux_base=prefs.getString("lux_base","");
		truck_base=prefs.getString("truck_base","");
		reg_min=prefs.getString("reg_min","");
		xl_min=prefs.getString("xl_min","");
		lux_min=prefs.getString("lux_min","");
		truck_min=prefs.getString("truck_min","");
		suv_price=prefs.getString("suv_price","");
		suv_base=prefs.getString("suv_base","");
		suv_min=prefs.getString("suv_min","");
		reg_surge=prefs.getString("reg_surge","");
		xl_surge=prefs.getString("xl_surge","");
		exec_surge=prefs.getString("exec_surge","");
		lux_surge=prefs.getString("lux_surge","");
		suv_surge=prefs.getString("suv_surge","");
		truck_surge=prefs.getString("truck_surge","");
		reg_hour=prefs.getString("reg_hour","");
		xl_hour=prefs.getString("xl_hour","");
		exec_surge=prefs.getString("exec_surge","");
		exec_hour=prefs.getString("exec_hour","");
		exec_surge=prefs.getString("exec_surge","");
		xl_surge=prefs.getString("xl_surge","");
		suv_hour=prefs.getString("suv_hour","");
		lux_hour=prefs.getString("lux_hour","");
		reg_hourfull=prefs.getString("reg_hourfull","");
		xl_hourfull=prefs.getString("xl_hourfull","");
		exec_hourfull=prefs.getString("exec_hourfull","");
		suv_hourfull=prefs.getString("suv_hourfull","");
		lux_hourfull=prefs.getString("lux_hourfull","");
		truck_hour=prefs.getString("truck_hour","");
		truck_hourfull=prefs.getString("truck_hourfull","");
		lat_a=prefs.getString("lat_a","");
		long_a=prefs.getString("long_a","");
		lat_b=prefs.getString("lat_b","");
		long_b=prefs.getString("long_b","");
		lat_c=prefs.getString("lat_c","");
		long_c=prefs.getString("long_c","");
		lat_d=prefs.getString("lat_c","");
		long_d=prefs.getString("long_c","");
		
		reg_minbase=prefs.getString("reg_minbase","");
		xl_minbase=prefs.getString("xl_minbase","");
		exec_minbase=prefs.getString("exec_minbase","");
		suv_minbase=prefs.getString("suv_minbase","");
		lux_minbase=prefs.getString("lux_minbase","");
		truck_minbase=prefs.getString("truck_minbase","");
		
		requestType=prefs.getString("requestType", "");

		Calendar c = Calendar.getInstance();
    
	    int hh = c.get(Calendar.HOUR_OF_DAY);
	    Log.d("tag","HH::"+hh);
	    
	    if(hh<=9)
	    {
	    	HH="0"+String.valueOf(hh);
	    	Log.d("tag","After Condition HH::"+hh);
	    	}
	    else
	    {
	    	HH=String.valueOf(hh);
	    	}
	    
	    Date date = new Date();
	    SimpleDateFormat simpDate;
	    simpDate = new SimpleDateFormat("yyyyMMdd"+HH+"mmss");
	    str_currentdate=simpDate.format(date);
	    Log.i("tag", "format date:"+str_currentdate);
	    
	    currentdateintoInt=Long.parseLong(str_currentdate);
	    Log.d("tag", "dateintoInt:"+currentdateintoInt);
	    curLat=	 prefs.getFloat("cur_lati",0);
	    
		lbl_outputstatingAddress = (TextView)findViewById(R.id.textView_startingaddress);
		lbl_rapidtitle=(TextView)findViewById(R.id.textView_location_rapid);
		lbl_rapidtitle.setTypeface(typeFace);
		tv_textView_nowtimedate=(TextView)findViewById(R.id.textView_nowtimedate);
		btn_edit_startpos=(Button)findViewById(R.id.button_strating_postion);
		btn_editddatetime=(Button)findViewById(R.id.button_edit_datetime);
		btn_requestpickup=(Button)findViewById(R.id.button_request_pickup);
		hDestLocation=(TextView)findViewById(R.id.textView_lbl_destinationAddress);
		lblDestAddress=(TextView)findViewById(R.id.textView_destination);
		btn_edit_despos=(Button)findViewById(R.id.button_end_positionchange);
		hDistance=(TextView)findViewById(R.id.textView_lbldistance);
		tv_Distance = (TextView)findViewById(R.id.textView_distance);
		hTime=(TextView)findViewById(R.id.textView_lbl_time);
		tv_time=(TextView)findViewById(R.id.textView_time);
		hMyPrice=(TextView)findViewById(R.id.textView_lbl_myprice11);
		tv_myprice=(TextView)findViewById(R.id.textView_myprice);
		hNameofPrice=(TextView)findViewById(R.id.textViewTimeLeft_leftside);
		textView_lblsp=(TextView)findViewById(R.id.textView_lblsp);
		numberpicker= (NumberPicker)findViewById(R.id.numberpicker);
		sp_vehiclelist=(Spinner)findViewById(R.id.spinner_vehicle);
		
		//////////////////////
		
		sp_vehiclelist.setVisibility(View.GONE);
		textView_lblsp.setVisibility(View.GONE);
		
		///////////////////
		ArrayAdapter<String> adapter_vehicle = new ArrayAdapter<String>(this,  android.R.layout.simple_spinner_item, al_vehicletype);
		sp_vehiclelist.setAdapter(adapter_vehicle);
		sp_vehiclelist.setOnItemSelectedListener(new OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				int pos=position+1;
				if(flag==1)
				{
					selectedVehicleType=""+pos;
					oneWay_fare();
					}
				flag=1;
				System.err.println("selectedVehicleType="+selectedVehicleType);
			}
		public void onNothingSelected(AdapterView<?> parent) {
								
			}
		});
		
		/*********getting time from another Ride*******/
		
		getTime=prefs.getString("SelectedTimeIS", "");
		Log.e("tag", "gettine"+getTime);
		if(getTime==null || getTime.equals(""))
		{}
		else
		{
			if(!getTime.equals("now"))
			{
				
  				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
  				String ss=""; 
  				ss=getTime;
		    	Date date1 = null;
				try {
					date1 = (Date)formatter.parse(ss);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		    	
		    	 formatter = new SimpleDateFormat("yyyy-MM-dd");
		    	 String  s = formatter.format(date1);
		    	  
		    	System.err.println(s);
		    	formatter = new SimpleDateFormat("HH:mm");
		    	String  t = formatter.format(date1);
		    	 
		    	System.err.println(t);
		    	tv_textView_nowtimedate.setText(" "+s+"   "+t);
		    	str_currentdate=getTime;
			}
		}
		
			
		/************for another ride***********************/
		
		if(getIntent().getStringExtra("tripscreen")!=null)
		{
			btn_edit_startpos.setVisibility(View.GONE);
			btn_edit_despos.setVisibility(View.GONE);
			sp_vehiclelist.setEnabled(false);
			}
		
		else
		{
			btn_edit_startpos.setVisibility(View.VISIBLE);
			btn_edit_despos.setVisibility(View.VISIBLE);
			sp_vehiclelist.setEnabled(true);
			}
		/************for another ride***********************/
		
		
		curLong	=prefs.getFloat("cur_logi",0);
		System.err.println("curLong=="+curLong);	
		System.err.println(curLong);
		//selectedVehicleType="1";	
		Bundle extras = getIntent().getExtras();
		try{
		if (extras == null) {
		}
		else
		{
			VIPCheck = extras.getString("VIP");
//			selectedVehicleType="1";
			if(extras.getString("VIP2")!=null)
			{
			selectedVehicleType = extras.getString("VIP2");
			}
		
		}}
		catch(Exception e)
		{
				
		}
			
		if(!VIPCheck.equals("yes"))
		{
			System.err.println("No VIP");
			destLat=prefs.getFloat("des_lati",0);
			//System.err.println(destLat);
			destLon=prefs.getFloat("des_logi", 0);
			//System.err.println(destLon);
			if(Utility.isConnectingToInternet(LocationandPriceDetail.this))
			{
				new distanceCalculator().execute(); //distance calculations
				}
			else
			{
				Utility.alertMessage(LocationandPriceDetail.this, "No Internet Connection");
				}
		}
		else
		{
			Editor editor=prefs.edit();
		    editor.putString("des_address","");
		    editor.commit();
		    hDestLocation.setVisibility(View.GONE);
		    lblDestAddress.setVisibility(View.GONE);
		    btn_edit_despos.setVisibility(View.GONE);
		    hDistance.setVisibility(View.GONE);
		    tv_Distance.setVisibility(View.GONE);
		    hTime.setVisibility(View.GONE);
		    tv_time.setVisibility(View.GONE);
		    hMyPrice.setVisibility(View.GONE);
		    tv_myprice.setVisibility(View.GONE);
		    hNameofPrice.setVisibility(View.GONE);
		    numberpicker.setVisibility(View.GONE);
		}
			System.err.println("Destination Address"+prefs.getString("des_address", null));
			System.err.println("Starting Address : "+prefs.getString("cur_address", null));
			textViewActualPrice=(TextView)findViewById(R.id.textViewActualPrice);
			
			lbl_outputstatingAddress.setText(prefs.getString("cur_address", null));
			lbl_outputstatingAddress.setTextSize(15);
			if(prefs.getInt("homeclick", 1)==0)
			{
				lblDestAddress.setText(prefs.getString("completeaddress", null));
				Editor ed=prefs.edit();
				ed.putInt("homeclick", 1);
				ed.commit();
				}
			else
			{
				lblDestAddress.setText(prefs.getString("des_address", null));
				}
				
		lblDestAddress.setTextSize(15);
		btn_edit_startpos.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent i=new Intent(LocationandPriceDetail.this,MapView_Activity.class);
			i.putExtra("edit_startgreen", "value");
			i.putExtra("edit_startwindow", "value");
			i.putExtra("editbutton_start", "value");
			i.putExtra("DestinationShow", "no");
			startActivity(i);
			finish();
				
			}
		});
		
		btn_edit_despos.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				Intent i=new Intent(LocationandPriceDetail.this,MapView_Activity.class);
				i.putExtra("editbutton_des", "value");
				i.putExtra("edit_desred", "value");
				i.putExtra("edit_deswindow", "value");
				i.putExtra("DestinationShow", "yes");
				startActivity(i);
				finish();

			}
		});
		
// open date time picker view		
	
	btn_editddatetime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			  /* final Dialog dialog = new Dialog(LocationandPriceDetail.this);
	                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	                dialog.setContentView(R.layout.datetimepicker_dailog);
					datepicker=(DatePicker)dialog.findViewById(R.id.datePicker1);
					timepicker=(TimePicker)dialog.findViewById(R.id.timePicker1);
					timepicker.setIs24HourView(true);
					timepicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
					Button done=(Button)dialog.findViewById(R.id.buttonDone);
					done.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
					datepicker_date=new StringBuilder();
					datepicker_date.setLength(0);
					datepicker_date.append(datepicker.getYear()).append(datepicker.getMonth()+1).append(datepicker.getDayOfMonth());
					String year=String.valueOf(datepicker.getYear());
					String month=String.valueOf(datepicker.getMonth()+1);
						         if(month.length()==1)
							         {
							          month="0"+month;
							         }
							         
							         String day=String.valueOf(datepicker.getDayOfMonth());
							         if(day.length()==1)
							         {
							          day="0"+day;
							         }
							         String curhour="",curmin="";
							         curhour=String.valueOf(timepicker.getCurrentHour());
							                 if(curhour.length()== 1)
							                          {
							                              curhour="0"+curhour; 
							                          }
							                          
							                 curmin=String.valueOf(timepicker.getCurrentMinute());
							                 if(curmin.length()== 1)
							                          {
							                  curmin="0"+curmin; 
							                   }
							           
							                 str_timepicker=new String();
							                 
							                 str_timepicker="";
							                 
							                 str_timepicker=curhour+""+curmin+""+"01";
							                 System.err.println("time"+str_timepicker);
							                 
							                 Log.d("tag","str_timepicker"+str_timepicker);
							                 datetimepick="edit";
							                 
							                 selectedTime=year+""+month+""+day+""+curhour+""+curmin+"01";
							                 selectdateintoInt=Long.parseLong(selectedTime);
							                 Log.i("tag", "selectdateintoInt:"+selectdateintoInt);
			          			 if(selectdateintoInt<currentdateintoInt)
			          			 {
			          				Log.d("tag", "Please select greater date from current date");
			          				Utility.alertMessage(LocationandPriceDetail.this, "Please select current or greater date from current date");
			          			 }
			          			 else
			          			 {
			          				
			          				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			          				String ss=""; 
			          				ss=selectedTime;
			        		    	Date date1 = null;
									try {
										date1 = (Date)formatter.parse(ss);
									} catch (ParseException e) {
										e.printStackTrace();
									}
			        		    	
			        		    	 formatter = new SimpleDateFormat("yyyy-MM-dd");
			        		    	 String  s = formatter.format(date1);
			        		    	  
			        		    	System.err.println(s);
			        		    	formatter = new SimpleDateFormat("HH:mm");
			        		    	String  t = formatter.format(date1);
			        		    	 
			        		    	System.err.println(t);
			        		    	tv_textView_nowtimedate.setText(" "+s+"   "+t);
			          				
			          				// tv_textView_nowtimedate.setText(datepicker_date.append(str_timepicker));
			          				 dialog.dismiss();
			          			 	}
							}
						});
						Button cancel=(Button)dialog.findViewById(R.id.buttonCancel);
						cancel.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
								// TODO Auto-generated method stub
							dialog.dismiss();	
							}
						});

			        dialog.show();
			        dialog.getWindow().setLayout(500, 650);*/
				
				
				
				
				  final Dialog dialog = new Dialog(LocationandPriceDetail.this);
	              // Include dialog.xml file

	              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
	              dialog.setContentView(R.layout.new_datetime_picker);
				
			    datepicker=(DatePicker)dialog.findViewById(R.id.datePicker1);
				timepicker=(TimePicker)dialog.findViewById(R.id.timePicker1);
				timepicker.setIs24HourView(true);
				timepicker.setCurrentHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
				Button done=(Button)dialog.findViewById(R.id.buttonDone);
		        done.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					 datepicker_date=new StringBuilder();
					 datepicker_date.setLength(0);
					 
					 datepicker_date.append(datepicker.getYear()).append(datepicker.getMonth()+1).append(datepicker.getDayOfMonth());
					 
					  String year=String.valueOf(datepicker.getYear());
				         String month=String.valueOf(datepicker.getMonth()+1);
				         if(month.length()==1)
				         {
				          month="0"+month;
				         }
				         
				         String day=String.valueOf(datepicker.getDayOfMonth());
				         if(day.length()==1)
				         {
				          day="0"+day;
				         }
				         
				   	       
				         
				         String curhour="",curmin="";
				         curhour=String.valueOf(timepicker.getCurrentHour());
				         if(curhour.length()== 1)
	                       {
	                          curhour="0"+curhour; 
	                          }       
		                 curmin=String.valueOf(timepicker.getCurrentMinute());
		                 if(curmin.length()== 1)
		                     {
		                		curmin="0"+curmin; 
		                          }
		                 str_timepicker=new String();
		                 str_timepicker="";
		                 
		                 str_timepicker=curhour+""+curmin+""+"43";
		                 System.err.println("time"+str_timepicker);
		                 
		                 Log.d("tag","str_timepicker"+str_timepicker);
		                 datetimepick="edit";
		                 
		                 selectedTime=year+""+month+""+day+""+curhour+""+curmin+"43";
		                 selectdateintoInt=Long.parseLong(selectedTime);
		                 Log.i("tag", "selectdateintoInt:"+selectdateintoInt);
	      			 
	      			 if(selectdateintoInt<currentdateintoInt)
	      			 {
	      				Log.d("tag", "Please select greater date from current date");
	      				Utility.alertMessage(LocationandPriceDetail.this, "Please select current or greater date from current date");
	      			 }
	      			 else
	      			 {
	      				
	      				
	      				DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	      				String ss=""; 
	      				ss=selectedTime;
	    		    	Date date1 = null;
						try {
							date1 = (Date)formatter.parse(ss);
						} catch (ParseException e) {
							e.printStackTrace();
						}
	    		    	
	    		    	 formatter = new SimpleDateFormat("yyyy-MM-dd");
	    		    	 String  s = formatter.format(date1);
	    		    	  
	    		    	System.err.println(s);
	    		    	formatter = new SimpleDateFormat("HH:mm");
	    		    	String  t = formatter.format(date1);
	    		    	 
	    		    	System.err.println(t);
	    		    	tv_textView_nowtimedate.setText(" "+s+"   "+t);
	    		    	System.err.println("trip_request_pickup_date "+ datepicker_date.append(str_timepicker));
	      				
	      				// tv_textView_nowtimedate.setText(datepicker_date.append(str_timepicker));
	      				 dialog.dismiss();
	      			 	}
				}
			});
			Button cancel=(Button)dialog.findViewById(R.id.buttonCancel);
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				dialog.dismiss();	
				}
			});
						
			    dialog.show();
			  
		
			}
		});

	
		   if(prefs.getString("requestType", "").equals("vipfull"))
	   {
		   	VIP_fareFull();
		   }
	   else if(prefs.getString("requestType", "").equals("viphalf"))
	   {
			VIP_fareHalf(); 
	   		}
	 
//number picker view

	
	 btn_requestpickup.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			 int value=	numberpicker.getValue();
 			 numberpicker_no=String.valueOf(value);
 			 
			System.err.println("request pickup");
			 //call Async Task
			if(Utility.isConnectingToInternet(LocationandPriceDetail.this))
			{
//				if(Utility.arraylist_driverInfo().size()>0)
//				{
				  	  /******PayMent checking**********/
			  	    String paymentchecking=prefs.getString("paymentstatus", "");
			  	    if(paymentchecking=="" || paymentchecking==null)
			  	    	{
			  	    		Intent i=new Intent(LocationandPriceDetail.this,ShowDialog.class);
			  	    		i.putExtra("Active", "payment");
			  	    		startActivity(i);
			  	    	}
			  	   
			  	    /*********************/
			  	    else
			  	    {
			  	    	new httpReqeustPickup().execute();
			  	    }
//				}
//				else
//				{
//					Utility.alertMessage(LocationandPriceDetail.this, "No Vehicle in this area");
//				}
			}
			else
			{
				Utility.alertMessage(LocationandPriceDetail.this, "No Internet Connection");
			}
		}
	});
}
// calculate distance value	
	class distanceCalculator extends AsyncTask<String, String, String>{
	    @Override
	    protected String doInBackground(String... str) {
	    	Log.i("tag", "Current Lati:"+curLat+"  Current Longitude:"+curLong+"  Destination_Lati:"+destLat+" Destination Longitude:"+destLon);
	    	String distance_url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+curLat+","+curLong+"&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";

//	    	String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins=30.711104,76.6861538&destinations="+destLat+","+destLon+"&mode=driving&language=en-EN&sensor=false";
//	    	Log.e("url", url);
			
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
			        	distance = elementArray.getJSONObject(j).getJSONObject("distance")
			                    .getString("text");
			        	distanceValue = elementArray.getJSONObject(j).getJSONObject("distance")
			                    .getInt("value");
			        	
			        	 totalKms = (float)(distanceValue*0.001);
			        	Log.e("values","total kms: "+totalKms+" , rem kms: "+(totalKms-2)+",,,"+distanceValue);
			        	if(totalKms>2){
			        		basic_fare = ((totalKms-2)*12) + 25;
			        	}else
			        		{
			        			basic_fare = 25;
			        			}
			          //  basic_fare = Float.valueOf(twoDForm.format(basic_fare));
			            
//			        	fare = ((distanceValue/1000))
			        	time    = elementArray.getJSONObject(j).getJSONObject("duration")
			                    .getString("text");
			        	timeValue = elementArray.getJSONObject(j).getJSONObject("duration")
			                    .getInt("value");
			        	
			        	float temp= (float)timeValue/60;
			        	
			        	approxMinutes = Math.round(temp);
			        	
			        	Log.e("result","Option "+" :: "+distance);//+" | "+approxMinutes+",, "+temp);
			        	
			        	System.err.print("time"+ time);
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
	    protected void onPreExecute() {
	        progress = ProgressDialog.show(LocationandPriceDetail.this, "", "Please wait...");
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        
	        progress.dismiss();
	      
	      //  reg_minbase,xl_minbase,exec_minbase,suv_minbase,lux_minbase;
	        
	        
	        des_mile=totalKms/(float) 1.609344;
//		    tv_Distance.setText(des_mile+" Miles");
		    
	        String newValue=new DecimalFormat("##.##").format(des_mile);
			tv_Distance.setText(String.valueOf(newValue)+" Miles");
		    
		    tv_time.setText(time);
		    
		    if(!prefs.getString("MainVehicleType", "").equals(""))
			{
				selectedVehicleType=prefs.getString("MainVehicleType","1").replace(" ","");
				}
		    
		   if(prefs.getString("requestType", "").equals("vipfull"))
		   {
			   	VIP_fareFull();
			   }
		   else if(prefs.getString("requestType", "").equals("viphalf"))
		   {
				VIP_fareHalf(); 
		   		}
		   else
		   {
			   oneWay_fare();
			   }
		    
//		    if(selectedVehicleType.equals("1"))
//			{
//				actualfare=Float.parseFloat(reg_base)+(approxMinutes*Float.parseFloat(reg_min)+(des_mile*Float.parseFloat(reg_price)));
//				try{
//					
//				float freg_minbase=Float.parseFloat(reg_minbase);
//				if(actualfare<freg_minbase)
//					{
//						actualfare=freg_minbase;
//						}
//				}catch(Exception e)
//				{
//					}
//				}
//			else if(selectedVehicleType.equals("2"))
//			{
//				actualfare=Float.parseFloat(xl_base)+(approxMinutes*Float.parseFloat(xl_min)+(des_mile*Float.parseFloat(xl_price)));
//				try{
//					float fxl_minbase=Float.parseFloat(xl_minbase);
//					if(actualfare<fxl_minbase)
//						{
//							actualfare=fxl_minbase;
//							}
//					}catch(Exception e)
//					{
//						}
//					}
//			else if(selectedVehicleType.equals("3"))
//			{
//				actualfare=Float.parseFloat(exec_base)+(approxMinutes*Float.parseFloat(exec_min)+(des_mile*Float.parseFloat(exec_price)));
//				try{
//					float fexec_minbase=Float.parseFloat(exec_minbase);
//					if(actualfare<fexec_minbase)
//						{
//							actualfare=fexec_minbase;
//							}
//					}catch(Exception e)
//					{
//						}
//					}
//			else if(selectedVehicleType.equals("4"))
//			{
//				actualfare=Float.parseFloat(suv_base)+(approxMinutes*Float.parseFloat(suv_min)+(des_mile*Float.parseFloat(suv_price)));
//			try{
//				float fsuv_minbase=Float.parseFloat(suv_minbase);
//				if(actualfare<fsuv_minbase)
//					{
//						actualfare=fsuv_minbase;
//						}
//				}catch(Exception e)
//				{}
//				}
//			else if(selectedVehicleType.equals("5"))
//			{
//				actualfare=Float.parseFloat(lux_base)+(approxMinutes*Float.parseFloat(lux_min)+(des_mile*Float.parseFloat(lux_price)));
//				try{
//					float flux_minbase=Float.parseFloat(lux_minbase);
//					if(actualfare<flux_minbase)
//						{
//							actualfare=flux_minbase;
//							}
//					}catch(Exception e)
//					{}
//					}
			Log.i("tag","actualfare"+actualfare);
			float ac   =actualfare;
			intvalueActualFare=(int)actualfare;
			System.err.println("reg_minbase="+reg_minbase+ac);
			System.err.println("reg_minbase="+xl_minbase);
//			String newValue1=new DecimalFormat("##.##").format(actualfare);
			textViewActualPrice.setText("$"+String.valueOf(intvalueActualFare));
			tv_myprice.setText("$"+String.valueOf(intvalueActualFare));
			

			float intvalueActualFare1=(float)intvalueActualFare;
			System.err.println("intvalueActualFare="+intvalueActualFare);
			System.err.println("intvalueActualFare1="+intvalueActualFare1);
			
			per_minvalue=20*intvalueActualFare;
			float per_minvalue1=per_minvalue/100;
			System.err.println(per_minvalue1);
			minvalue=intvalueActualFare-per_minvalue1;
			
			System.err.println("min="+minvalue);
			
			per_maxvalue=500*intvalueActualFare;
					
			float per_maxvalue1=per_maxvalue/100;
			System.err.println(per_maxvalue1);
			maxvalue=per_maxvalue1-intvalueActualFare;
			System.err.println("max="+maxvalue);
			
			try{
			// restricted number to minimum value i.e 1
			numberpicker.setMinValue((int)minvalue);
			numberpicker.setMaxValue((int) maxvalue);
			numberpicker.setValue(intvalueActualFare);// restricked number to maximum value i.e. 31
			numberpicker.setWrapSelectorWheel(true); 
			}
		   catch(Exception e1)
		    {
			System.err.println("numberpicker="+e1);	   
		    }
			numberpicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		    numberpicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() 
		    {
		      public void onValueChange(NumberPicker picker, int oldVal, int newVal) 
		      {
		       String picker_no = ""+newVal;
		       tv_myprice.setText("$"+picker_no);

		      }
		    });

	        }
	}

	public void oneWay_fare()
	{
		
		if(selectedVehicleType.equals("1"))
		{
			actualfare=Float.parseFloat(reg_base)+(approxMinutes*Float.parseFloat(reg_min)+(des_mile*Float.parseFloat(reg_price)));
			try{
				if(Float.parseFloat(reg_surge)>0)
				actualfare=actualfare*Float.parseFloat(reg_surge);
				
			float freg_minbase=Float.parseFloat(reg_minbase);
			if(actualfare<freg_minbase)
				{
					actualfare=freg_minbase;
					}
			}catch(Exception e)
			{
				}
			}
		 
		else if(selectedVehicleType.equals("2"))
			{
				actualfare=Float.parseFloat(xl_base)+(approxMinutes*Float.parseFloat(xl_min)+(des_mile*Float.parseFloat(xl_price)));
				try{
					
					if(Float.parseFloat(xl_surge)>0)
						actualfare=actualfare*Float.parseFloat(xl_surge);
					
					float fxl_minbase=Float.parseFloat(xl_minbase);
					if(actualfare<fxl_minbase)
						{
							actualfare=fxl_minbase;
							}
					}catch(Exception e)
					{
						}
					}
			else if(selectedVehicleType.equals("3"))
			{
				actualfare=Float.parseFloat(exec_base)+(approxMinutes*Float.parseFloat(exec_min)+(des_mile*Float.parseFloat(exec_price)));
				try{
					
					if(Float.parseFloat(exec_surge)>0)
						actualfare=actualfare*Float.parseFloat(exec_surge);
					
					float fexec_minbase=Float.parseFloat(exec_minbase);
					if(actualfare<fexec_minbase)
						{
							actualfare=fexec_minbase;
							}
					}catch(Exception e)
					{
						}
					}
			else if(selectedVehicleType.equals("4"))
			{
				actualfare=Float.parseFloat(suv_base)+(approxMinutes*Float.parseFloat(suv_min)+(des_mile*Float.parseFloat(suv_price)));
			try{
				if(Float.parseFloat(suv_surge)>0)
					actualfare=actualfare*Float.parseFloat(suv_surge);
				
				float fsuv_minbase=Float.parseFloat(suv_minbase);
				if(actualfare<fsuv_minbase)
					{
						actualfare=fsuv_minbase;
						}
				}catch(Exception e)
				{}
				}
			else if(selectedVehicleType.equals("5"))
			{
				actualfare=Float.parseFloat(lux_base)+(approxMinutes*Float.parseFloat(lux_min)+(des_mile*Float.parseFloat(lux_price)));
				if(Float.parseFloat(lux_surge)>0)
					actualfare=actualfare*Float.parseFloat(lux_surge);
				
				try{
					float flux_minbase=Float.parseFloat(lux_minbase);
					if(actualfare<flux_minbase)
						{
							actualfare=flux_minbase;
							}
					}catch(Exception e)
					{}
					}
			else//selectedVehicleType.equals("1"))
				{
					actualfare=Float.parseFloat(reg_base)+(approxMinutes*Float.parseFloat(reg_min)+(des_mile*Float.parseFloat(reg_price)));
					
					try{
						
					if(Float.parseFloat(reg_surge)>0)
					actualfare=actualfare*Float.parseFloat(reg_surge);
					float freg_minbase=Float.parseFloat(reg_minbase);
					if(actualfare<freg_minbase)
						{
							actualfare=freg_minbase;
							}
					}catch(Exception e)
					{
						}
					}
	System.err.println("actual="+actualfare);
	textViewActualPrice.setText("$"+String.valueOf(actualfare)); 
	numberpicker.setValue((int) actualfare);
	tv_myprice.setText("$"+actualfare);
	}
	
	
	public void VIP_fareHalf()
	{
		  if(selectedVehicleType.equals("1"))
			{
				actualfare=Float.parseFloat(reg_hour);
				}
			else if(selectedVehicleType.equals("2"))
			{
				actualfare=Float.parseFloat(xl_hour);
				}
			else if(selectedVehicleType.equals("3"))
			{
				actualfare=Float.parseFloat(exec_hour);
				}
			else if(selectedVehicleType.equals("4"))
			{
				actualfare=Float.parseFloat(suv_hour);
				}
			else if(selectedVehicleType.equals("5"))
			{
				actualfare=Float.parseFloat(lux_hour);
				}
			else
			{
				actualfare=Float.parseFloat(reg_hour);
				}
		  System.err.println("actualhalf="+actualfare);
		  textViewActualPrice.setText("$"+String.valueOf(actualfare));
		  numberpicker.setValue((int) actualfare);
		  tv_myprice.setText("$"+actualfare);
	}
	public void VIP_fareFull()
	{
		  if(selectedVehicleType.equals("1"))
			{
				actualfare=Float.parseFloat(reg_hourfull);
				}
			else if(selectedVehicleType.equals("2"))
			{
				actualfare=Float.parseFloat(xl_hourfull);
					}
			else if(selectedVehicleType.equals("3"))
			{
				actualfare=Float.parseFloat(exec_hourfull);
					}
			else if(selectedVehicleType.equals("4"))
			{
				actualfare=Float.parseFloat(suv_hourfull);
				}
			else if(selectedVehicleType.equals("5"))
			{
				actualfare=Float.parseFloat(lux_hourfull);
					}
			else
			{
				actualfare=Float.parseFloat(reg_hourfull);
				}
		  System.err.println("actualfull="+actualfare);
		  textViewActualPrice.setText("$"+String.valueOf(actualfare));
		  numberpicker.setValue((int) actualfare);
		  tv_myprice.setText("$"+actualfare);
	}
	
	
	
/** ReqeustPickup class	*/
	private class httpReqeustPickup extends AsyncTask<Void, Void, Void> { // Async_task class
			protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(LocationandPriceDetail.this);
			exception=0;
		//	pDialog.setTitle("Loading");
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
			
			@Override
	protected void onPostExecute(Void result) {
			super.onPostExecute(result);
						
			pDialog.dismiss();
			if(RequestRide_Result!=null )
			{
			if(exception==1)
			{
				Utility.alertMessage(LocationandPriceDetail.this,"Internet connection failed. Please Try again later.");
			}
			else if(RequestRide_Result.equals("0"))
			{
				Intent i=new Intent(LocationandPriceDetail.this,MapView_Activity.class);
				Editor e=prefs.edit();
				e.putString("MainDriverID", "");
				e.putString("SelectedTimeIS", "");
				e.putString("requestType", "");
				e.putString("MainVehicleType", "");
				e.putString("selectdriver", "");
				e.commit();
				startActivity(i);
				finish();
			}
			else
			{
				Utility.alertMessage(LocationandPriceDetail.this,RequestRide_Message);
			}
			}
		}
		
//request parsing function
		public void parsing() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/RequestRide");//Url_Address.request ride);
			JSONObject json = new JSONObject();
			
			json.put("Trigger", "RequestRide");
			json.put("TripId","");
			json.put("driver","");
		
				
			String mainDriverID=prefs.getString("MainDriverID", "");
			
//			json.put("DriverList","8,9");
//			json.put("driverId", 9);
			
			//DriverList
			if (!mainDriverID.equals(""))
			{
				driverlist.add(mainDriverID);
				json.put("DriverList",mainDriverID);
				json.put("driverId", mainDriverID);
				
				Log.i("tag","Main Driver ID not null:");
				
				}
			else 
			{
				/*if(!prefs.getString("selectdriver","").equals(""))
				{
						for(BeanDriver driverObj:Utility.arraylist_driverInfo())
						{
							 drivierID=driverObj.getDriverID();
							 driverlist.add(drivierID);
							}
						String driverlistitem="";
						Log.i("tag", "DRIVER LIST::::::::::" +driverlist.size());
						for(int i=0;i<driverlist.size();i++)
							{
								if(i==0){
									driverlistitem=driverlist.get(i);
									}
								else if(i>0)
								{
									driverlistitem=driverlistitem+","+driverlist.get(i);
									}
								}
						json.put("DriverList",driverlistitem);
						json.put("driverId", prefs.getString("selectdriver",""));
					}
					else 
					{*/
						Log.i("tag","Main Driver ID not null:");
						for(BeanDriver driverObj:Utility.arraylist_driverInfo())
							{
							 drivierID=driverObj.getDriverID();
							 vehicleType=driverObj.getVehicleType();
							 driverLatitude=driverObj.getDriverLatitude();
							 driverLongitude=driverObj.getDriverLongitude();
							 driverDistance=driverObj.getDriverDistance();
							 driverlist.add(drivierID);
							}
//							}
						String driverIDs="";
						Log.i("tag", "DRIVER LIST::::::::::" +driverlist.size());
						for(int i=0;i<driverlist.size();i++)
							{
								if(i==0){
									driverIDs=driverlist.get(i);
									}
								else if(i>0)
								{
									driverIDs=driverIDs+","+driverlist.get(i);
									}
								
								else if(favtDriver.equals(driverlist.get(i)))
								{
									checkfavt=true;
									}
							}
						System.err.println("driverIDs"+driverIDs);	
						json.put("DriverList",driverIDs);
							
							favtDriver=prefs.getString("favoritedriver", "");
							if(checkfavt==true)
							{
								json.put("driverId", favtDriver);
								//default	
							 	}
							else
							{
								json.put("driverId", driverlist.get(0)); 
								}
						
						
						
						
						}
		//	}
//			else
//			{
//				Utility.alertMessage(LocationandPriceDetail.this, "No Driver In This Area.");
//			}
//			
			
			System.err.println("userid "+prefs.getString("userid",null));
			json.put("riderId",prefs.getString("userid", null));
				
			json.put("requestType",prefs.getString("requestType", null));
			json.put("starting_loc",prefs.getString("cur_address", null));
			if(prefs.getInt("homeclick", 1)==0)
			{
				json.put("ending_loc",prefs.getString("completeaddress", null));}
			else
				{
				json.put("ending_loc",prefs.getString("des_address", null));
				System.err.println(prefs.getString("des_address", null));}
				
				if(requestType.equals("vipfull"))
				{
					json.put("trip_time_est", "0");
					System.err.println("time"+time);
					
					json.put("trip_time_act","0");
					System.err.println("time"+time);
				}
				else if(requestType.equals("viphalf")) 
				{
					json.put("trip_time_est", "0");
					System.err.println("time"+time);
					
					json.put("trip_time_act","0");
					System.err.println("time"+time);
				}
					
				
				else
				{
					json.put("trip_time_est", time);
					System.err.println("est time"+"8.15");
					
					json.put("trip_time_act","0");
					System.err.println("time"+time);
				}
				
				
				json.put("start_long", curLong);
				System.err.println("curLong"+curLong);
				
				json.put("end_long", destLon);
				System.err.println("destLon"+destLon);
				
				json.put("start_lat", curLat);
				System.err.println("curLat"+curLat);
				
				json.put("end_lat", destLat);
				System.err.println("destLat"+destLat);
												
				json.put("trip_request_date",str_currentdate);
				System.err.println(str_currentdate);
				
				if(!(datetimepick==null))
				{	json.put("trip_request_pickup_date", selectedTime);
					System.err.println("trip_request_pickup_date "+ datepicker_date.append(str_timepicker));
					}
				else
				{
					json.put("trip_request_pickup_date", str_currentdate);
					System.err.println("trip_request_pickup_date "+str_currentdate);
					}
					
				json.put("trip_miles_est",des_mile);
				System.err.println("des_mile"+des_mile);
				
				json.put("trip_miles_act", des_mile);
				System.err.println("des_mile"+des_mile);
				
				
				if(requestType.equals("vipfull"))
				{
					json.put("offered_fare", actualfare);
					System.err.println("numberpicker_no="+actualfare);
				}
				else if(requestType.equals("viphalf")) 
				{
					json.put("offered_fare", actualfare);
					System.err.println("numberpicker_no="+actualfare);
					}
					
				
				else
				{
					json.put("offered_fare", numberpicker_no);
					System.err.println("numberpicker_no="+numberpicker_no);
					}
			
				String newactual=new DecimalFormat("##.##").format(actualfare);
				
				json.put("setfare",newactual);
				System.err.println("actualfare="+newactual);
				
				json.put("vehicle_type", prefs.getInt("Getvehicletype", 0));
				System.err.println("vehicle_type="+prefs.getInt("Getvehicletype", 0));
				
				
				
			Log.i("tag", "SendData in Location Price:"+json.toString());	
						      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","result>>>>>>>    "+ jsonstr);
			}
				JSONObject obj=new JSONObject(jsonstr);
				RequestRide_Result=obj.getString("result");
				RequestRide_Message=obj.getString("message");
				
				tripid=obj.getString("tripid");
				System.err.println("tripid  "+tripid);
			}
				catch(Exception e){
					System.out.println(e);
					Log.d("tag", "Error :"+e); }  
		   }
}

		
	public void onBackPressed() {
     
		// TODO Auto-generated method stub
		if(getIntent().getStringExtra("tripscreen")!=null)
		{

			Intent i=new Intent(LocationandPriceDetail.this,TripScreen.class);
			startActivity(i);
			finish();
		}
		else
		{
		
		Intent i=new Intent(LocationandPriceDetail.this,MapView_Activity.class);
		startActivity(i);
		finish();
		}
	}
	
	
}


