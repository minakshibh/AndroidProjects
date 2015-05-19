package com.rapidride;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;

public class Payment_Activity extends Activity {
	
	EditText ed_promocode;
	Button btn_apply,btn_addcard,btn_ccrefresh;
	public ProgressDialog pDialog;
	ListView lv_creditcard,lv_promocode;
	TextView tv_lbl_title;
	public ProgressDialog progressbar;
	LinearLayout Ll_listView_ccard,Ll_textviewepmty1,Ll_textview_promocode,Ll_listView_promocode;
	public Object jsonResult,jsonResult2;
	public String jsonMessage,jsonMessage2;
	TextView tv_empty1;
	
	String rec_id;
	String str_ed_promocode=null,str_currentdate=null;
	int arr_images[] = { R.drawable.ic_launcher};
    String[] strings={"hgjhgj"};
    ArrayList<String> arraylist_items,al_newitems,al_last3no;
    String promocode;
	
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
	SharedPreferences prefs;
	Typeface typeFace;
	
	String ccard_url = "http://appba.riderapid.com/avail_cards/?riderid=";
//	String payment = "http://appba.riderapid.com/process/?tripid= %@&rec_id= %@&riderid= %@&tip_amount= %@&suggested_fare= %@",tripid,rec_id,userIdStr,tip_amount,suggested_fare;
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.payment_activity);

		arraylist_items=new ArrayList<String>();
		al_newitems=new ArrayList<String>();
		al_last3no=new ArrayList<String>();
	
	//	arraylist_promo=new ArrayList<String>();
		Utility.hideKeyboard(Payment_Activity.this);
		prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
		typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf"); 
		
		ed_promocode=(EditText)findViewById(R.id.editText_promocode);
		Ll_listView_ccard=(LinearLayout)findViewById(R.id.LinearLayout_listView_ccard);
		Ll_textviewepmty1=(LinearLayout)findViewById(R.id.LinearLayout_textviewepmty1);
		Ll_textview_promocode=(LinearLayout)findViewById(R.id.LinearLayout_textview_promocode);
		Ll_listView_promocode=(LinearLayout)findViewById(R.id.LinearLayout_listView_promocode);
		
		lv_creditcard=(ListView)findViewById(R.id.listView_ccard);
		lv_promocode=(ListView)findViewById(R.id.listView_promocode);
		
		tv_lbl_title=(TextView)findViewById(R.id.textView_payment_rapid);
	//	btn_paymentnow=(Button)findViewById(R.id.button_paymentnow);
		tv_lbl_title.setTypeface(typeFace);
		btn_apply=(Button)findViewById(R.id.button_applypromo);
		btn_apply.setTypeface(typeFace);
		
		btn_apply.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			str_ed_promocode=ed_promocode.getText().toString();
			
			Calendar c = Calendar.getInstance();
			str_currentdate = df.format(c.getTime());
			System.err.println("str_c"+str_currentdate);
		
				if(ed_promocode.getText().toString().equals(""))
				{
					Utility.alertMessage(Payment_Activity.this,"Please enter data");
					}
				else{
					if(Utility.isConnectingToInternet(Payment_Activity.this))
					{
						new httpPromoCode().execute();
						Utility.hideKeyboard(Payment_Activity.this);
						}
					else{
						Utility.alertMessage(Payment_Activity.this,"error in internet connection");
					}
										
				}
			}
		});

		
		btn_addcard=(Button)findViewById(R.id.button_add_card);
		btn_addcard.setTypeface(typeFace);
		btn_addcard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i=new Intent(Payment_Activity.this,CreditCard_Details.class);
				startActivity(i);
				finish();
			}
		});
		btn_ccrefresh=(Button)findViewById(R.id.button_ccrefresh1);
		btn_ccrefresh.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				al_newitems.clear();
				if(Utility.isConnectingToInternet(Payment_Activity.this))
				{
					new CreditcardParsing().execute(); 
				}
				else{
					Utility.alertMessage(Payment_Activity.this,"error in internet connection");
				}
		
//				ccardAdapter adapter=new ccardAdapter();
//				lv_creditcard.setAdapter(adapter);
				
			}
		});
//		lv_creditcard.setOnItemClickListener(new OnItemClickListener() {
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				str_rec=al_newitems.get(position);
//				
//			}
//		});
/***************** credit card asyn task	*************/	
		if(Utility.isConnectingToInternet(Payment_Activity.this))
		{
			new CreditcardParsing().execute(); 
				}
		else{
			Utility.alertMessage(Payment_Activity.this,"error in internet connection");
		}
	
		
		PromoAdapter promoadapter=new PromoAdapter();
		lv_promocode.setAdapter(promoadapter);
		
		if(Utility.arrayListPromo().isEmpty())
			{
			System.err.println("if");
			Ll_textview_promocode.setVisibility(View.VISIBLE);
			Ll_listView_promocode.setVisibility(View.GONE);
			}
		else
			{
			Ll_listView_promocode.setVisibility(View.VISIBLE);
			Ll_textview_promocode.setVisibility(View.GONE);
			}
//		if(!(getIntent().getIntExtra("tip",0)==0))
//		{
//			btn_paymentnow.setVisibility(View.VISIBLE);
//			}
//		else
//		{
//			btn_paymentnow.setVisibility(View.GONE);
//			}
		
//		btn_paymentnow.setOnClickListener(new View.OnClickListener() {
//		public void onClick(View v) {
//			
//				str_tripid=getIntent().getStringExtra("tripid");
//				str_fare=getIntent().getStringExtra("fare");
//				str_riderid=getIntent().getStringExtra("riderid");
//				str_tip=getIntent().getStringExtra("tip");
//				//str_rec="";
//				if(Utility.isConnectingToInternet(Payment_Activity.this))
//				{
//					new PaymentTask().execute();
//						}
//				else{
//					Utility.alertMessage(Payment_Activity.this,"error in internet connection");
//				}
//				
//				
//			}
//		});

	}
	/************************Main function end**********************************/
	

	
	
	
	/************************ Async_task class for promo code **********************************/
private class httpPromoCode extends AsyncTask<Void, Void, Void> { 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(Payment_Activity.this);
//			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
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
			
			if(jsonResult.equals("0"))
			{
				Utility.alertMessage(Payment_Activity.this,jsonMessage);
				}
			else
				{
					Utility.alertMessage(Payment_Activity.this,jsonMessage);
					jsonResult="";
					}
			pDialog.dismiss();
			
			}
		
			/************************ Function promo code **********************************/
			public void parsing() throws JSONException {
			try {
			HttpParams httpParameters = new BasicHttpParams();
			int timeoutConnection = 60000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			int timeoutSocket = 61000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost httpost = new HttpPost(Url_Address.url_Home+"/ValidatePromoCode");//Url_Address.url_promocode);
			JSONObject json = new JSONObject();
			
			json.put("Trigger", "ValidatePromoCode");
			
			json.put("PromoCode", str_ed_promocode);
			System.err.println("1"+str_ed_promocode);
		
			json.put("CurrentDate", str_currentdate);
			System.err.println("currentdate"+str_currentdate);
			
			json.put("riderId", prefs.getString("userid", null));
			System.err.println("riderid"+prefs.getString("userid", null));
		      //	      
			httpost.setEntity(new StringEntity(json.toString()));
			httpost.setHeader("Accept", "application/json");
			httpost.setHeader("Content-type", "application/json");
			
			HttpResponse response = client.execute(httpost);
			HttpEntity resEntityGet = response.getEntity();
			String jsonstr=EntityUtils.toString(resEntityGet);
			if(jsonstr!=null)
			{
			 Log.e("tag","promo code result-->>>>>    "+ jsonstr);
			 
				}
				JSONObject obj=new JSONObject(jsonstr);
				jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
				
				String ListPromoCodeInfo=	obj.getString("ListPromoCodeInfo");
				Log.e("tag:", "ListPromoCodeInfo: "+ListPromoCodeInfo);
				
				JSONArray jsonarray=obj.getJSONArray("ListPromoCodeInfo");
			
				for(int i=0;i<jsonarray.length();i++){
					
				JSONObject obj2=jsonarray.getJSONObject(i);
				
				String value=obj2.getString("value");
				Log.e("tag:", "value: "+value);
				
					promocode=	obj2.getString("promocode");
				Log.e("tag:", "promocode: "+promocode);
				
				String useone=	obj2.getString("useonce");
				Log.e("tag:", "useone: "+useone);
				
				String valuetype=	obj2.getString("valuetype");
				Log.e("tag:", "valuetype: "+valuetype);
				
				String validationstatus=	obj2.getString("validationstatus");
				Log.e("tag:", "validationstatus: "+validationstatus);
				
				String promoname=	obj2.getString("promoname");
				Log.e("tag:", "promoname: "+promoname);
				
				Log.i("tag:", "Result: "+jsonResult);
				Log.i("tag:", "Message :"+jsonMessage);
				if(validationstatus.equals("valid"))
				{
					Utility.arrayListPromo().add(promocode);
					}
			
				}
				HashSet hs = new HashSet();
				hs.addAll(Utility.arrayListPromo());
				Utility.arrayListPromo().clear();
				Utility.arrayListPromo().addAll(hs);
				}
			  catch(Exception e){
			   System.out.println(e);
			   Log.d("tag", "Error :"+e); }  
				}
			}
	
	public void onBackPressed() {
	// TODO Auto-generated method stub
		finish();
	super.onBackPressed();
	}

	// credit card adapter
class ccardAdapter extends BaseAdapter{
		public int getCount() {
			// TODO Auto-generated method stub
			 
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
		public View getView(final int position, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			LayoutInflater infla=getLayoutInflater();
			View v1=infla.inflate(R.layout.ccard_adapter, null);
			TextView label=(TextView)v1.findViewById(R.id.ccardname);
			ImageView iv=(ImageView)v1.findViewById(R.id.ccardimage);
			iv.setImageResource(R.drawable.visacard_icon);
			label.setText(al_newitems.get(position));
			ImageView iv_del=(ImageView)v1.findViewById(R.id.imageView_del);
			
///////////////////////////////////////////			
			iv_del.setVisibility(View.GONE);
//////////////////////////////////////////			
			
			iv_del.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
					rec_id=al_last3no.get(position);
					System.err.println("card=="+rec_id);
					if(Utility.isConnectingToInternet(Payment_Activity.this))
					{
						new	DeleteCCardParsing().execute();
						}
					else{
						Utility.alertMessage(Payment_Activity.this,"error in internet connection");
						}
				
				}
			});
           
            return v1;
			}
		}
	
	//promo code adapter
class PromoAdapter extends BaseAdapter{

		public int getCount() {
		return Utility.arrayListPromo().size();
				
		}

		@Override
		public Object getItem(int arg0) 
		{
			// TODO Auto-generated method stub
			return arg0;
			}

		@Override
		public long getItemId(int arg0)
		{
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View arg1, ViewGroup arg2) {
			// TODO Auto-generated method stub
			
			LayoutInflater infla=getLayoutInflater();
			View v1=infla.inflate(R.layout.promoadapter, null);
			TextView tv_code=(TextView)v1.findViewById(R.id.promo_name);
			if(Utility.arrayListPromo().isEmpty())
			{
				tv_code.setText("None");}
         	else
			{
         		tv_code.setText(Utility.arrayListPromo().get(position));
				}
          return v1;
		}
		}



/*******************c card parsing class ***********************/
class CreditcardParsing extends AsyncTask<Void, Void, Void> {

			 
				@Override
				protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				progressbar = new ProgressDialog(Payment_Activity.this);
//				progressbar.setTitle("Loading");
				progressbar.setMessage("Please wait...");
				progressbar.setCancelable(false);
				progressbar.show();
				}
		@Override
		protected Void doInBackground(Void... arg0) {
		
				try {
				 parsing3();
				 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
				}
		
				
		return null;
		}
			protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressbar.dismiss();
			ccardAdapter adapter=new ccardAdapter();
			
			if(al_newitems.isEmpty())
			{
				System.err.println("if");
				Ll_textviewepmty1.setVisibility(View.VISIBLE);
				Ll_listView_ccard.setVisibility(View.GONE);
				}
			else
			{
				Ll_listView_ccard.setVisibility(View.VISIBLE);
				Ll_textviewepmty1.setVisibility(View.GONE);
				System.err.println("elseeeeeee");
				
				lv_creditcard.setAdapter(adapter);
			}
			if(al_newitems.size()>0)
			{
				Editor e=prefs.edit();
				e.putString("paymentstatus", "yes");
				e.commit();
			}

		}
		
//ccard_url parsing function
		public void parsing3() throws JSONException {
		try {
				HttpParams httpParameters = new BasicHttpParams();
				 // Set the timeout in milliseconds until a connection is established.
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				 // Set the default socket timeout (SO_TIMEOUT) 
				 // in milliseconds which is the timeout for waiting for data.
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpGet httpGet = new HttpGet(ccard_url+prefs.getString("userid",null));
			
				
				HttpClient client = new DefaultHttpClient();
				HttpResponse response;
				StringBuilder stringBuilder = new StringBuilder();
	        	response = client.execute(httpGet);
	            
	        	HttpEntity resEntityGet = response.getEntity();
	        	String jsonstr=EntityUtils.toString(resEntityGet);
	        	if(jsonstr!=null)
	        	{
	        		Log.e("tag","credit card result>>>>>>>    "+ jsonstr);
			 
					}
				
	        	JSONObject obj=new JSONObject(jsonstr);
				jsonResult2=obj.getString("result");
				jsonMessage2=obj.getString("message");
				System.err.println("result"+jsonResult2);
	        	System.err.println("result"+jsonMessage2);
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
				}catch(Exception e){
					e.printStackTrace();
			
				}
		}}

/**********Delete ccard ********************/

class DeleteCCardParsing extends AsyncTask<Void, Void, Void> {
			protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			progressbar = new ProgressDialog(Payment_Activity.this);
		//	progressbar.setTitle("Loading");
			progressbar.setMessage("Please wait...");
			progressbar.setCancelable(false);
			progressbar.show();
			}
protected Void doInBackground(Void... arg0) {
		
			try {
			 parsing1();
			 } catch (Exception e) {
			 // TODO Auto-generated catch block
			 e.printStackTrace();
			}
		
			
		return null;
		}


protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		progressbar.dismiss();
	
		if(Utility.isConnectingToInternet(Payment_Activity.this))
		{
			al_newitems.clear();
			al_last3no.clear();
			new CreditcardParsing().execute(); 
			}
		else{
			Utility.alertMessage(Payment_Activity.this,"error in internet connection");
		}
		
		
		}
		
public void parsing1() throws JSONException {
	try {
	HttpParams httpParameters = new BasicHttpParams();
	int timeoutConnection = 60000;
	HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
	int timeoutSocket = 61000;
	HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	HttpClient client = new DefaultHttpClient(httpParameters);
	HttpPost httpost = new HttpPost(Url_Address.url_Home+"/DeleteCreditCard");//Url_Address.url_promocode);
	JSONObject json = new JSONObject();
	
	json.put("Trigger", "DeleteCreditCard");
	
	json.put("rec_id", rec_id);
	System.err.println("rec_id="+rec_id);

      //	      
	httpost.setEntity(new StringEntity(json.toString()));
	httpost.setHeader("Accept", "application/json");
	httpost.setHeader("Content-type", "application/json");
	
	HttpResponse response = client.execute(httpost);
	HttpEntity resEntityGet = response.getEntity();
	String jsonstr=EntityUtils.toString(resEntityGet);
	if(jsonstr!=null)
	{
	 Log.e("tag","promo code result-->>>>>    "+ jsonstr);
	 
		}
		JSONObject obj=new JSONObject(jsonstr);
		jsonResult=obj.getString("result");
		jsonMessage=obj.getString("message");
		
		}
	  catch(Exception e){
	   System.out.println(e);
	   Log.d("tag", "Error :"+e); }  
		}
	}
}
