package com.rapidride;

import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.json.JSONException;
import org.json.JSONObject;
import com.rapidride.SimpleGestureFilter.SimpleGestureListener;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ResourceAsColor")
public class RideFinish extends Activity implements LocationListener{

	TextView tv_fare,tv_tip,tv_riderfare,tv_tipper,tv_totalfare;
	//TextView tv_data,tv_p1,tv_p2,tv_n1,tv_n2;
	TextView tv_promocode,tv_codename,tv_firstcard;
	Button btn_finishdriver,btn_finishrider,btn_addnewcard,btn_change;
	ListView lv_ccard;
	SharedPreferences prefs;
	RelativeLayout rl_driver;
	LinearLayout rl_rider,ll_promocode, linearlayout_ccard,linearLayout_list;
	ProgressDialog progressbar;
	private SimpleGestureFilter detector;
	CCardAdapter adapter;
	ArrayList<String> arraylist_items,al_newitems,al_last3no;
	float maxtip=200,defauttip1,defauttip,var_value,mintip=1;
	int  pre1=0,pre2=0,nxt1=0,nxt2=0,maindata=0,int_tip=0,int_position=0;
	String	promocode=null,promoname=null,payment_jsonMessage=null,payment_jsonResult=null,fare=null;
	String str_tripid=null,str_fare="",str_tip=null,str_riderid=null,str_last3digit=null,getriderid="",end_jsonResult=null,end_jsonMessage=null;
	int int_fare,int_var_value,int_defauttip,flag=0;
	NumberPickerHorizontal numberPicker;
	ProgressDialog pDialog;
	LocationManager location_manager;
	String  provider;
	Location location;
	double latitude=0 ,longitude=0;
//	String ccard_url = "http://appba.riderapid.com/avail_cards/?riderid=";
	
	
	  protected void onCreate(Bundle savedInstanceState) {
		  requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.ride_finish);
	        
	        arraylist_items=new ArrayList<String>();
			al_newitems=new ArrayList<String>();
			al_last3no=new ArrayList<String>();
			adapter=new CCardAdapter();
			 prefs = RideFinish.this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
		
			
			Log.e("tag","tripid Ride Finish="+getIntent().getStringExtra("tripid"));
			Log.e("tag","riderid Ride Finish="+getIntent().getStringExtra("riderid"));
			Log.e("tag","fare Ride Finish="+getIntent().getStringExtra("fare"));
			Log.e("tag","driverid Ride Finish="+getIntent().getStringExtra("driverid"));
			Log.e("tag","vehicleType Ride Finish="+getIntent().getStringExtra("vehicleType"));
			
			
	    	if(Utility.isConnectingToInternet(RideFinish.this))
			{
	    		 getriderid=getIntent().getStringExtra("riderid");
		    	 System.err.println("Riderid="+getriderid);
	    		
		    	 new CreditcardParsing().execute(); 
				
					}
			else{
				Utility.alertMessage(RideFinish.this,"error in internet connection");
			}
	    	
	    	
	    	numberPicker =(NumberPickerHorizontal)findViewById(R.id.number);
	    	location_manager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
			   Criteria c=new Criteria();
			   provider=location_manager.getBestProvider(c, false);
			   //now you have best provider
			   //get location
			   location=location_manager.getLastKnownLocation(provider);
			   if(location!=null)
			   {
			     //get latitude and longitude of the location
				   onLocationChanged(location);
			  //   System.err.println("latitude  "+latitude+"  longitude  "+longitude);
			       }
			   else
			   {
			    System.err.println("No Provider");
			       }
	    	
	        // Detect touched area 
           // detector = new SimpleGestureFilter(this,this);
            
            lv_ccard=(ListView)findViewById(R.id.listView_ccard);
            tv_promocode=(TextView)findViewById(R.id.textView_promocode);
            tv_codename=(TextView)findViewById(R.id.textView_promocodename);
            btn_change=(Button)findViewById(R.id.button_change);
            
            tv_firstcard=(TextView)findViewById(R.id.textView_firstcardname);
            ll_promocode=(LinearLayout)findViewById(R.id.ll_promocode);
            linearlayout_ccard=(LinearLayout)findViewById(R.id.linearlayout_ccard);
            linearLayout_list=(LinearLayout)findViewById(R.id.LinearLayout_listviewccard);
            btn_addnewcard=(Button)findViewById(R.id.button_addnewccard);
	        tv_riderfare=(TextView)findViewById(R.id.textView_fareoutput);
//	        tv_data=(TextView)findViewById(R.id.textView_dtext);
//	        tv_p1=(TextView)findViewById(R.id.textView_ptext1);
//	        tv_p2=(TextView)findViewById(R.id.textView_ptext2);
//	        tv_n1=(TextView)findViewById(R.id.textView_ntext1); 
//	        tv_n2=(TextView)findViewById(R.id.textView_ntext2);
	        tv_tipper=(TextView)findViewById(R.id.textView_ridertip_output);
	        tv_totalfare=(TextView)findViewById(R.id.textView_totalfare_rider);
	        
	        rl_driver=(RelativeLayout)findViewById(R.id.relativelayout_driver);
	        rl_rider=(LinearLayout)findViewById(R.id.LinearLayout1);
	       
	        btn_finishdriver=(Button)findViewById(R.id.button_driverfinish);
	        btn_finishrider=(Button)findViewById(R.id.button_riderfinish);  
	        
	        tv_fare=(TextView)findViewById(R.id.textView_driverfare);
	        
	        if(getIntent().getStringExtra("finish")!=null)
	        {
	        	fare=prefs.getString("finish_fare", null);
	        	}
	        else if(getIntent().getStringExtra("paymentfinish")!=null)
	        {
	        	fare=prefs.getString("fare3", null);
	        	}
	        else
	        {
	        	fare=getIntent().getStringExtra("fare");
	        	}
	        
	       
    
    System.err.println("fare="+fare);
    try{
    	int_fare=Integer.parseInt(fare);
    	}
    catch(Exception e)
    {
    	System.err.println("Exception fare"+e);
    	}
	       
    
    numberPicker.setMinValue(1);
	numberPicker.setMaxValue(200);
	
	numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
	numberPicker.setOnValueChangedListener(new NumberPickerHorizontal.OnValueChangeListener() 
	  {
			public void onValueChange(NumberPickerHorizontal picker, int oldVal,int newVal) {
			int newtip=newVal;
			System.err.println("tip="+newtip);
			
			float maintip = (float)newtip;
			float maintip1= maintip*int_fare;
			
			float maindata3=maintip1/100;
			int_tip=(int)maindata3;
			
			tv_tipper.setText("$"+int_tip);
			int total=int_tip+int_fare;
			tv_totalfare.setText("$"+total);
			
			}
    });
	numberPicker.setValue(10);
	
   // var_value=defauttip;
  //  int_var_value = (int)var_value;
   // int_defauttip = (int)defauttip;
    
  
	        
	 //      System.err.println("maptip="+maxtip);	
	    //   Toast.makeText(this, ""+maxtip, Toast.LENGTH_SHORT).show(); 
	       
	       if(getIntent().getStringExtra("finish")!=null)
	        {
	       	   tv_fare.setText("$"+prefs.getString("finish_fare", null));
	        	tv_riderfare.setText("$"+prefs.getString("finish_fare", null));
	        	System.err.println("add card");
	            }
	       else if(getIntent().getStringExtra("paymentfinish")!=null)
	       {
	    	   tv_fare.setText("$"+prefs.getString("fare3",null));
		       tv_riderfare.setText("$"+prefs.getString("fare3",null));
		   	System.err.println("payemnt");
	       		}
	        else
	        { 	
	        	tv_fare.setText("$"+getIntent().getStringExtra("fare"));
			    tv_riderfare.setText("$"+getIntent().getStringExtra("fare"));
			    System.err.println("normal");
	        	}

//	        int int_p1=int_defauttip-1;
//	        int int_p2=int_defauttip-2;
//	       
//	        int int_n1=int_defauttip+1;
//	        int int_n2=int_defauttip+2;
	           
	       	int_defauttip= 10;
			float maindata2= 10*int_fare;
			float maindata3=maindata2/100;
			int_tip=(int)maindata3;
			
			int total=int_tip+int_fare;
	        tv_tipper.setText("$"+int_tip);
			tv_totalfare.setText("$"+total);
	        
//	        tv_data.setText(""+int_defauttip+"%");
//	        tv_p1.setText(""+int_p1+"%");
//			tv_p2.setText(""+int_p2+"%");
//			tv_n1.setText(""+int_n1+"%");
//			tv_n2.setText(""+int_n2+"%");
	        
			
			  /************SHow  view condition**********/
			
		    linearLayout_list.setVisibility(View.GONE);
			linearlayout_ccard.setVisibility(View.VISIBLE);
			btn_addnewcard.setVisibility(View.GONE);
			System.err.println("promocode="+promocode);
			
		
			
	        String value=getIntent().getStringExtra("value");
			 if(!(value==null)) {
				 /***Driver mode******/
				  if(value.equals("rider"))
				  {
					  rl_driver.setVisibility(View.GONE);
					  rl_rider.setVisibility(View.VISIBLE);
						
					  if(flag==0)
						{
							if(getIntent().getStringExtra("paymentfinish")==null)
							{
								Editor ed=prefs.edit();
								ed.putString("tripid3", getIntent().getStringExtra("tripid"));
								ed.putString("riderid3", getIntent().getStringExtra("riderid"));
								ed.putString("fare3", getIntent().getStringExtra("fare"));
								ed.putString("driverid3", getIntent().getStringExtra("driverid"));
								ed.putString("vehicleType3", getIntent().getStringExtra("vehicleType"));
								ed.putString("paymentcheck", "no");
								ed.commit();
								flag=1;
								}
							}
					 
				  	}
				   /***Rider mode******/
				  else if(value.equals("driver"))
				  {
					  	rl_driver.setVisibility(View.VISIBLE);
				    	rl_rider.setVisibility(View.GONE); 
				  	}
				 }
			 System.err.println("size="+	 al_newitems.size());
		
			
			 btn_change.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
					
						lv_ccard.setSelection(int_position); 
						lv_ccard.setAdapter(adapter);
					
						linearLayout_list.setVisibility(View.VISIBLE);
						linearlayout_ccard.setVisibility(View.GONE); 
						btn_addnewcard.setVisibility(View.VISIBLE);
					
						
					
				}
			});
//	        if(prefs.getString("mode", null).equals("driver"))
//		    {
//		    	rl_driver.setVisibility(View.VISIBLE);
//		    	rl_rider.setVisibility(View.GONE);
//		    	
//		    	}
//	        /***Rider mode******/
//		    else if(prefs.getString("mode", null).equals("rider"))
//		    {
//		    	rl_driver.setVisibility(View.GONE);
//		    	rl_rider.setVisibility(View.VISIBLE);
//		    	}
		    
	        
		    btn_finishdriver.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
				new httpEndRide().execute();
//					Intent i=new Intent(RideFinish.this,DriverView_Activity.class);
//					finish();
//					startActivity(i);
					
				}
			});
		    btn_finishrider.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				
					str_tripid=getIntent().getStringExtra("tripid");
					str_riderid= getIntent().getStringExtra("riderid");
					str_fare=getIntent().getStringExtra("fare");
				try{
						str_tip=String.valueOf(int_tip);
					}
					catch(Exception e)
					{
					System.err.println(e);	
					}
				
					if(Utility.isConnectingToInternet(RideFinish.this))
					{
						
//						if(str_last3digit!=null)
//						{
						new PaymentTask().execute();
							
						Log.e("tag","str_tripid="+str_tripid);
						Log.e("tag","str_riderid="+str_riderid);
						Log.e("tag","str_tip="+str_tip);
						Log.e("tag","str_last3digit="+str_last3digit);
						Log.e("tag","str_fare="+str_fare);
					//	}
//						else
//						{
//							Utility.alertMessage(RideFinish.this,"no credit card ");
//						}
					}
							
					else{
						Utility.alertMessage(RideFinish.this,"error in internet connection");
					}
					
					
				}
			});
		    btn_addnewcard.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
				
					Editor ed=prefs.edit();
			    	ed.putString("finish_tripid", getIntent().getStringExtra("tripid"));
			    	ed.putString("finish_riderid", getIntent().getStringExtra("riderid"));
			    	ed.putString("finish_fare", getIntent().getStringExtra("fare"));
			    //ed.putString(key, value);
			    	ed.commit();
					Intent i=new Intent(RideFinish.this,CreditCard_Details.class);
					i.putExtra("finish", "value");
					startActivity(i);
					
					
				}
			});
		    
		  
			
			lv_ccard.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
					
				
				str_last3digit=al_last3no.get(position);
				int_position=position;
				tv_firstcard.setText("VISA    ****-"+al_newitems.get(position));
				
				 linearLayout_list.setVisibility(View.GONE);
				linearlayout_ccard.setVisibility(View.VISIBLE);
				btn_addnewcard.setVisibility(View.GONE);
				
				System.err.println("3last="+str_last3digit);
					
				}
			});
			
	  }
	  
	  
	  
	  
	  /***************************End main function********************************************/
	  
	  
	  
	  
	  
	  
	  
	
//	  public boolean dispatchTouchEvent(MotionEvent me){
//	      // Call onTouchEvent of SimpleGestureFilter class
//	       this.detector.onTouchEvent(me);
//	     return super.dispatchTouchEvent(me);
//	  }
//	@Override
//	public void onSwipe(int direction) {
//		 String str = "";
//	      
//	      switch (direction) {
//	      
//	      case SimpleGestureFilter.SWIPE_RIGHT : 
//	    	  str = "Swipe Right";
//	    	  swipeRight();
//	    	  break;
//	      case SimpleGestureFilter.SWIPE_LEFT :  
//	    	  str = "Swipe Left";
//	    	  swipeLeft();
//	          break;
////	      case SimpleGestureFilter.SWIPE_DOWN : 
////	    	  str = "Swipe Down";
////	    	  
////	    	  break;
////	      case SimpleGestureFilter.SWIPE_UP :  
////	    	  str = "Swipe Up";
//	    	  
//	 //         break;
//	      }
//	     // Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
//	}
//
//	public void onDoubleTap() {
//		// TODO Auto-generated method stub
//		
//	}  
//	public void swipeLeft()
//	{
//		if(int_defauttip<maxtip)
//		{
//			
//			
//			pre2=int_defauttip-3;
//			pre1=int_defauttip-2;
//			maindata=int_defauttip-1;
//			nxt1=int_defauttip+0;
//			nxt2=int_defauttip+1;
//			
//			int_defauttip=nxt2;
//			
////			tv_data.setText(maindata+"%");
////			tv_p1.setText(""+pre1+"%");
////			tv_p2.setText(""+pre2+"%");
////			tv_n1.setText(""+nxt1+"%");
////			tv_n2.setText(""+nxt2+"%");
//			
//
//			float maindata1 = (float)maindata;
//			float maindata2= maindata1*int_fare;
//			
//			float maindata3=maindata2/100;
//			int_tip=(int)maindata3;
//			
//			tv_tipper.setText("$"+int_tip);
//			int total=int_tip+int_fare;
//			tv_totalfare.setText("$"+total);
//		}
//	}

	
//	public void swipeRight()
//	{
//		if(int_defauttip>mintip)
//		{
//			
//			
//			nxt2=int_defauttip+1;
//			nxt1=int_defauttip+0;
//			maindata=int_defauttip-1;
//			pre1=int_defauttip-2;
//			pre2=int_defauttip-3;
//			
//			
//			
//			int_defauttip=maindata;
//			
////			tv_data.setText(""+maindata+"%");
////			tv_p1.setText(""+pre1+"%");
////			tv_p2.setText(""+pre2+"%");
////			tv_n1.setText(""+nxt1+"%");
////			tv_n2.setText(""+nxt2+"%");
//			
//			
//			float maindata1 = (float)maindata;
//			float maindata2= maindata1*int_fare;
//			
//			float maindata3=maindata2/100;
//			
//			int_tip=(int)maindata3;
//			tv_tipper.setText("$"+int_tip);
//			int total=int_tip+int_fare;
//			tv_totalfare.setText("$"+total);
//			
//		}
//	}
	 /*******************c card parsing class ***********************/
	class CreditcardParsing extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {
					super.onPreExecute();
					// Showing progress dialog
					progressbar = new ProgressDialog(RideFinish.this);
					//progressbar.setTitle("Loading");
					progressbar.setMessage("Please wait...");
					progressbar.setCancelable(false);
					progressbar.show();
					}
			@Override
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
				progressbar.dismiss();
				 
				
				if(al_newitems.size()<=1)
				 {
					 btn_change.setVisibility(View.GONE);
					 btn_addnewcard.setVisibility(View.VISIBLE);
				 }
				 else
				 {
					 linearlayout_ccard.setVisibility(View.VISIBLE);
					 btn_addnewcard.setVisibility(View.GONE); 
					 
				 }
				
				tv_promocode.setText(promoname);
				tv_codename.setText("(-$"+promocode+")");
				try{
				tv_firstcard.setText("VISA    ****-"+al_newitems.get(0));
				
				str_last3digit=al_last3no.get(0);
				}
				catch(Exception e)
				{
					
				}
				
				
				if(promocode==null)
				{
					System.err.println("promocode="+promocode);
					ll_promocode.setVisibility(View.GONE);
				}
				else{
					ll_promocode.setVisibility(View.VISIBLE);
				}
				if(al_newitems.isEmpty())
				{
//					System.err.println("if");
//					Ll_textviewepmty1.setVisibility(View.VISIBLE);
//					Ll_listView_ccard.setVisibility(View.GONE);
					}
				else
				{
				
//					Ll_listView_ccard.setVisibility(View.VISIBLE);
//					Ll_textviewepmty1.setVisibility(View.GONE);
//					System.err.println("elseeeeeee");
					lv_ccard.setAdapter(adapter);
				}

			}
			
	//ccard_url parsing function
			public void parsing() throws JSONException {
			try {
					HttpParams httpParameters = new BasicHttpParams();
					int timeoutConnection = 60000;
					HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					int timeoutSocket = 61000;
					HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket); 
				
				
					HttpGet httpGet;
					if(getIntent().getStringExtra("finish")!=null)
					{
						Log.e("tag","trip="+prefs.getString("finish_tripid",null));
						Log.e("tag","finish_riderid="+prefs.getString("finish_riderid",null));
					 httpGet = new HttpGet("http://appba.riderapid.com/prepare_payment/?tripid="+prefs.getString("finish_tripid",null)+"&"+"riderid="+prefs.getString("finish_riderid",null));
						
						}
					else if(getIntent().getStringExtra("paymentfinish")!=null)
					{
						Log.e("tag","payment_tripid="+prefs.getString("tripid3",null));
						Log.e("tag","payment_riderid="+prefs.getString("riderid3",null));
					 httpGet = new HttpGet("http://appba.riderapid.com/prepare_payment/?tripid="+prefs.getString("tripid3",null)+"&"+"riderid="+prefs.getString("riderid3",null));
						
						}
					else
					{
						String getTempTripId=getIntent().getStringExtra("tripid");
						String getTripId=getTempTripId.trim();
						String getriderID=getriderid;
						Log.d("tag","getTripId Ride Finish"+getTripId);
						Log.d("tag","riderid Ride Finish="+ getriderID);
						Log.e("tag","tripelse="+prefs.getString("finish_tripid",null));
					 httpGet = new HttpGet("http://appba.riderapid.com/prepare_payment/?tripid="+getTripId+"&"+"riderid="+getriderID);
					}
//					HttpGet httpGet = new HttpGet("http://appba.riderapid.com/prepare_payment/?tripid=1495&riderid=74");
					HttpClient client = new DefaultHttpClient();
					HttpResponse response;
//					StringBuilder stringBuilder = new StringBuilder();
		        	
					response = client.execute(httpGet);
		            
		        	HttpEntity resEntityGet = response.getEntity();
		        	String jsonstr=EntityUtils.toString(resEntityGet);
		        	if(jsonstr!=null)
		        	{
		        		Log.e("tag","credit card result>>>>>>>    "+ jsonstr);
		        		JSONObject obj=new JSONObject(jsonstr);
						String	jsonResult2=obj.getString("result");
						String	jsonMessage2=obj.getString("card");
							
						System.err.println("result"+jsonResult2);
				        	System.err.println("card="+jsonMessage2);
				        	
				        String	promocode1=obj.getString("promocode");
							System.err.println("promocode="+promocode1);
							
							promocode=obj.getString("value");
							System.err.println("value="+promocode);
							
							promoname=obj.getString("promoname");
							System.err.println("promoname="+promoname);
							
							
				        	String str_firstfour=jsonMessage2.substring(0, 4);
				        	//  strings.add(jsonMessage);
				        	//String csv = idList.substring(1, idList.length() - 1).replace(", ", ",");
				        	arraylist_items =new  ArrayList<String>(Arrays.asList(jsonMessage2.split(",")));
				        
				        	System.err.println("size ="+arraylist_items.size());
				        	System.err.println("elements="+arraylist_items);
				        	
				        	for(int i=0;i<arraylist_items.size();i++)
				        	{
				        	String str_full=arraylist_items.get(i);
				        	String str_half=str_full.substring(0, 4);
				        	al_newitems.add(str_half);
				        	System.err.println("half="+al_newitems);
				        	
				        	
				        	String str_full1=arraylist_items.get(i);
				        	String str_3last=str_full1.substring(str_full.length() - 3, str_full1.length());
				        	al_last3no.add(str_3last);
				        	System.err.println("last 3="+al_last3no);
				        	}
					
		        	}
			
					}catch(Exception e){
						e.printStackTrace();
				
					}
			}}	
	// credit card adapter
	class CCardAdapter extends BaseAdapter{
			public int getCount() {
						 
				return al_newitems.size();}
			
			public Object getItem(int arg0) 
			{
				// TODO Auto-generated method stub
				return arg0;}
			
			public long getItemId(int arg0)
			{
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public View getView(int position, View arg1, ViewGroup arg2) {
				// TODO Auto-generated method stub
				
				LayoutInflater infla=getLayoutInflater();
				View v1=infla.inflate(R.layout.ridefinish_ccard, null);
				TextView label=(TextView)v1.findViewById(R.id.ccardname);
			//	ImageView iv=(ImageView)v1.findViewById(R.id.ccardimage);
			//	iv.setImageResource(R.drawable.visacard_icon);
				label.setText("VISA    ****-"+al_newitems.get(position));
				
//				if(al_newitems.isEmpty())
//				{
//					 label.setText("No promo code");}
//	         	else
//				{
//	         		 label.setText(al_newitems.get(position));
//					}
				//lv_ccard.setBackground(background)
				 if(position == int_position) {
			            // you can define your own color of selected item here
					 label.setBackgroundColor(R.color.blue);
			        } else {
			            // you can define your own default selector here
			        	//label.setBackground(StringUtils.getDrawableFromResources(R.color.blue));
			        }
	            return v1;
				}
			}
		
	
	/******************* Payment class ***********************/
	class PaymentTask extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {
	  				super.onPreExecute();
	  				// Showing progress dialog
	  				progressbar = new ProgressDialog(RideFinish.this);
	  				progressbar.setMessage("Please wait...");
	  				progressbar.setCancelable(false);
	  				progressbar.show();
	  				}
	  		@Override
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
	  			progressbar.dismiss();
	  			if(payment_jsonResult.equals("1"))
		  		{
//	  			Utility.alertMessage(RideFinish.this, payment_jsonMessage);	
//	  			Intent i=new Intent(RideFinish.this,MapView_Activity.class);
//	  			startActivity(i);
	  				Editor ed=prefs.edit();
	  				ed.remove("paymentcheck");
	  				ed.remove("tripid3");
	  				ed.remove("riderid3");
	  				ed.remove("driverid3");
	  				ed.remove("vehicleType3");
	  				ed.remove("fare3");
	  				ed.commit();
	  				
  			  AlertDialog.Builder builder = new AlertDialog.Builder(RideFinish.this);
  			  builder.setTitle("Rapid");
  			  builder.setMessage(payment_jsonMessage);
  			  builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
  			  public void onClick(DialogInterface dialogInterface, int i) {
  			    // Show location settings when the user acknowledges the alert dialog
  				  AlertDialog.Builder builder1 = new AlertDialog.Builder(RideFinish.this);
    			  builder1.setTitle("Rapid");
    			  builder1.setMessage("Do you want to book another ride with same driver ?");
    			  builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i=new Intent(RideFinish.this,TripScreen.class);
//						i.putExtra("driverid",getIntent().getStringExtra("driverid"));
////						i.putExtra("MainVehicleType",getIntent().getStringExtra("vehicleType"));
//						Log.e("tag","DesLati>>>>>>>    "+ getIntent().getStringExtra("DesLati"));
//						Log.e("tag","DesLong>>>>>>>    "+ getIntent().getStringExtra("DesLong"));
//						Log.e("tag","DesAddress>>>>>>>    "+ getIntent().getStringExtra("DesAddress"));
//						
//						
//						Editor e=prefs.edit();
////						e.putString("des_lati", prefs.getFloat("des_lati", 0.0));
////						e.putString("des_logi",  prefs.getString("des_logi", ""));
////						e.putString("des_address", prefs.getString("des_address", ""));
//						
//			  			e.putString("MainDriverID", getIntent().getStringExtra("driverid"));
//			  			e.putString("MainVehicleType", getIntent().getStringExtra("vehicleType"));
//			  			e.putString("activetripid", "");
//			  			e.commit();
//			  			startActivity(i);
//			  			finish();
			  			
			  		
						/*********************************/
			  			Editor e=prefs.edit();
						e.putString("des_lati", getIntent().getStringExtra("DesLati"));
						e.putString("des_logi", getIntent().getStringExtra("DesLong"));
						e.putString("des_address", getIntent().getStringExtra("DesAddress"));
						
						e.putString("cur_lati", getIntent().getStringExtra("DesLati"));
						e.putString("cur_logi", getIntent().getStringExtra("DesLong"));
						e.putString("cur_address", getIntent().getStringExtra("DesAddress"));
					
						
			  			e.putString("MainDriverID", getIntent().getStringExtra("driverid"));
			  			e.putString("MainVehicleType", getIntent().getStringExtra("vehicleType"));
			  			e.putString("activetripid", "");
			  			e.commit();
			  			startActivity(i);
			  			finish();
			  			/****************************************/
					}
    			  });
    			builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
    			public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent i=new Intent(RideFinish.this,MapView_Activity.class);
			  			startActivity(i);
			  		    Editor e=prefs.edit();
		  			    e.putString("activetripid", "");
		  			    e.commit();
					    finish();
					}
				});
    			  Dialog alertDialog1 = builder1.create();
      			  alertDialog1.setCanceledOnTouchOutside(false);
      			  alertDialog1.show();
  			    }
  			  });
  			  
  			  Dialog alertDialog = builder.create();
  			  alertDialog.setCanceledOnTouchOutside(false);
  			  alertDialog.show();	
	
		  	  }
		  		else
		  		{
		  			Utility.alertMessage(RideFinish.this, payment_jsonMessage);
		  		}
	  		}
	  		
	/******************* Payment parsing function ***********************/
	  		public void parsing() throws JSONException {
	  			String paymenturl;
	  			if(getIntent().getStringExtra("finish")!=null)
	  			{			//
	  				 paymenturl = "http://appba.riderapid.com/process/?tripid="+prefs.getString("finish_tripid",null)+"&rec_id=" + str_last3digit+"&riderid=" +prefs.getString("finish_riderid",null)+"&tip_amount="+ str_tip +"&suggested_fare=" +prefs.getString("finish_fare",null);
	  				}
	  			else if(getIntent().getStringExtra("paymentfinish")!=null)
	  			{
	  				 paymenturl = "http://appba.riderapid.com/process/?tripid="+prefs.getString("tripid3",null)+"&rec_id=" + str_last3digit+"&riderid=" +prefs.getString("riderid3",null)+"&tip_amount="+ str_tip +"&suggested_fare=" +prefs.getString("fare3",null);
	  				}
	  			else
	  			{
	  				paymenturl = "http://appba.riderapid.com/process/?tripid="+str_tripid.trim() +"&rec_id=" + str_last3digit+"&riderid=" +str_riderid+"&tip_amount="+ str_tip +"&suggested_fare=" +str_fare;
	  		 
	  				}
	  		try {
	  			HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				 // Set the default socket timeout (SO_TIMEOUT) 
				 // in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	  				HttpGet httpGet = new HttpGet(paymenturl);
	  				
					HttpClient client = new DefaultHttpClient();
			        HttpResponse response;
			        StringBuilder stringBuilder = new StringBuilder();
			        response = client.execute(httpGet);
			            
			        HttpEntity resEntityGet = response.getEntity();
					String jsonstr=EntityUtils.toString(resEntityGet);
					if(jsonstr!=null)
					{
					 Log.e("tag","result>>>>>>>    "+ jsonstr);
					 
						}
						JSONObject obj=new JSONObject(jsonstr);
						payment_jsonResult=obj.getString("result");
						payment_jsonMessage=obj.getString("message");
						
			        System.err.println("payemnt result="+payment_jsonResult);
			        System.err.println("payment message="+payment_jsonMessage);
			        
					}catch(Exception e){
					e.printStackTrace();
					
				}
	  		}}
	/**end ride Async task class */
	private class httpEndRide extends AsyncTask<Void, Void, Void> { 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(RideFinish.this);
			//pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			if(!((Activity) RideFinish.this).isFinishing())
			{
				pDialog.show();
				}
			
			}
	protected Void doInBackground(Void... arg0) {
		
			try {
			 endRideparsing();
			 } catch (Exception e) {
			 e.printStackTrace();
			}
			
			return null;
			}
	protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			
			pDialog.dismiss();
			if(!(end_jsonResult==null))
			{
				if(end_jsonResult.equals("0"))
				{
					Intent i=new Intent(RideFinish.this,DriverView_Activity.class);
					finish();
					startActivity(i);
				}
				else
				{
					Utility.alertMessage(RideFinish.this, end_jsonMessage);
					}
			
				}
			}
		
			/**end ride parsing function **/
		public void endRideparsing() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/EndRide");//Url_Address.url_promocode);
			JSONObject json = new JSONObject();
			
			json.put("Trigger", "EndRide");
			if(getIntent().getStringExtra("finish")!=null)
			{
				Log.e("tag","trip="+prefs.getString("finish_tripid",null));
			
				json.put("TripId", prefs.getString("finish_tripid",null));
				json.put("TripFinalFare",prefs.getString("finish_fare",null));
				}
			else if(getIntent().getStringExtra("paymentfinish")!=null)
			{
				Log.e("tag","payment_tripid="+prefs.getString("tripid3",null));
				Log.e("tag","payment_riderid="+prefs.getString("riderid3",null));
				
				json.put("TripId", prefs.getString("tripid3",null));
				json.put("TripFinalFare",prefs.getString("fare3",null));
				}
			else
			{
				Log.e("tag","payment_tripid="+getIntent().getStringExtra("fare"));
				Log.e("tag","payment_riderid="+getIntent().getStringExtra("tripid"));
				
				json.put("TripId", getIntent().getStringExtra("tripid"));
				json.put("TripFinalFare",getIntent().getStringExtra("fare"));
			}
			//json.put("TripId", tripid);
			json.put("Latitude", latitude);
			json.put("Longitude", longitude);
			Log.e("tag","latitude="+latitude+"longitude="+longitude);
			//json.put("TripFinalFare",getIntent().getStringExtra("suggestionfare"));
			Log.e("tag","Sending data:="+json.toString());
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("EndRide"," result-->>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
				end_jsonResult=obj.getString("result");
				end_jsonMessage=obj.getString("message");
		
				Log.i("EndRide:", "Result: "+end_jsonResult);
				Log.i("EndRide:", "Message :"+end_jsonMessage);
	
			}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
				}
			}
		    public void onBackPressed() {
		    	
			
		}
//		    public boolean onKeyDown(int keyCode, KeyEvent event) {
//		    	
//		    	   
//		        if (keyCode == KeyEvent.KEYCODE_BACK) {
//		       
//		        	Editor ed=prefs.edit();
//			    	ed.putString("finish_tripid", getIntent().getStringExtra("tripid"));
//			    	ed.putString("finish_riderid", getIntent().getStringExtra("riderid"));
//			    	ed.putString("finish_fare", getIntent().getStringExtra("fare"));
//			    //ed.putString(key, value);
//			    	ed.commit();
//			    	Toast.makeText(RideFinish.this, "back", Toast.LENGTH_SHORT);
//		        }
//		        return super.onKeyDown(keyCode, event);
//		    }
			@Override
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				longitude=location.getLongitude();
				latitude=location.getLatitude();
			}
			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
}

