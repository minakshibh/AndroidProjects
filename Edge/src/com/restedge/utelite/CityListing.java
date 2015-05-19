package com.restedge.utelite;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.MadsAdView.MadsAdView;
import com.MadsAdView.MadsInlineAdView;
import com.adgoji.mraid.adview.AdViewCore;
import com.adgoji.mraid.adview.AdViewCore.OnAdDownload;
import com.restedge.utelite.R;
import com.restedge.event.EventListing;
import com.restedge.util.City;
import com.restedge.util.ConnectionDetector;
import com.restedge.util.RestaurantDatabase;
import com.restedge.util.SAXXMLParser;

//
public class CityListing extends Activity {

	ListView cityListView;
	ArrayList<City> cityList=new ArrayList<City>();
	RestaurantDatabase db;
	ProgressDialog dialog;
	String message;
	public static int deviceWidth, deviceHeight;
	String cityListUrl;
	/*String cityListUrl="http://112.196.24.205:111/utilivewebaccess.asmx/CityListing";
	String cityurl="http://112.196.24.205:111/utilivewebaccess.asmx";*/
	String method_name="CityListing";
	InputStream is;
	int LastUpdatedCities;
	Map<String, String> map=null;
	SharedPreferences pref;
	String receiptId,EmailSubject;
	 private Menu menu;
	 String supportEmail;
	 
	 TextView txtcityEvent;
	 ImageButton eventListing;
	@SuppressWarnings("deprecation")
	@SuppressLint({ "CommitPrefEdits", "NewApi" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.city_listing);
		
		FrameLayout fl=(FrameLayout)findViewById(R.id.flayout);
		pref = CityListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
		
		eventListing=(ImageButton)findViewById(R.id.btneventListing);
		
		
		android.webkit.CookieManager cookiesmanager=android.webkit.CookieManager.getInstance();
		System.out.println(cookiesmanager.hasCookies());
		if(cookiesmanager.hasCookies())
			cookiesmanager.removeAllCookie();
		
		
		MadsInlineAdView adView = (MadsInlineAdView)findViewById(R.id.mads_inline_adview);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		MadsAdView adview=new MadsAdView(this, "5430679986");
		adview.setAdtype("inline");
		adview.update();
		fl.addView(adview);
		
		adView.setVisibility(View.GONE);
		adView.setOnAdDownload(new OnAdDownload() {
			
			@Override
			public void noad(AdViewCore arg0) {
				// TODO Auto-generated method stub
				Log.e("MAID","no ads ");
			}
			
			@Override
			public void error(AdViewCore arg0, String arg1) {
				// TODO Auto-generated method stub
				Log.e("MAID","Error in download ads");
			}
			
			@Override
			public void end(AdViewCore arg0) {
				// TODO Auto-generated method stub
				Log.e("MAID","End process of download");
			}
			
			@Override
			public void begin(AdViewCore arg0) {
				// TODO Auto-generated method stub
				Log.e("MAID","Begining and download");
			}
		});
		
		
		
		eventListing.setOnClickListener(new OnClickListener() {
	    public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Intent in=new Intent(CityListing.this,EventListing.class);
				in.putExtra("ClassName", "City");
				CityListing.this.finish();
				startActivity(in);
				
				
			}
		});
		
		
		
		
		//Getting url
		String app_url=getResources().getString(R.string.app_url);
		receiptId=pref.getString("SupportEmail", null);
		EmailSubject=getResources().getString(R.string.EmailSubject);
		cityListUrl=app_url+"/"+method_name;
		
		
		
		
		//Gat height width of screen
		Display display = getWindowManager().getDefaultDisplay();
		if (android.os.Build.VERSION.SDK_INT >= 13){			
			Point size = new Point();
			display.getSize(size);
			deviceWidth = size.x;
			deviceHeight = size.y;
		}else{
			deviceWidth = display.getWidth();  // deprecated
			deviceHeight = display.getHeight();  
		}
		
		cityListView=(ListView)findViewById(R.id.citylist);
		cityListView.setOnItemClickListener(listener);
		
		//check the internet connection
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		
	if(isInternetPresent)
	{
		//Getting city from server
		new GettingCity().execute(); 
		
	}	
	else
	{
		//AlertDialog when connection not found
		AlertDialog alertdialog=new AlertDialog.Builder(CityListing.this).create();
		System.out.println("check connection");
		alertdialog.setTitle("Edge");
		alertdialog.setMessage("Internet connection not available");
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
		alertdialog.show();
		 db=new RestaurantDatabase(CityListing.this);
			ArrayList<City> citie=db.getAllCities();
			cityList=citie;
			// check weather city is avalaibale or not
			if(cityList.size()==0)
				Toast.makeText(CityListing.this, "No data found", 1000).show();
			
//			for(int i=0;i<citie.size();i++)
//			{
//				
//				City ci=citie.get(i);
//				
//			//	System.out.println(ci.getCityId()+"  "+ci.getCityName()+"  "+ci.getCityUrl()+" "+ci.getCityimage());
//				
//			}
			
			CityAdapter adapter=new CityAdapter(this, android.R.layout.simple_list_item_1, citie);
			cityListView.setAdapter(adapter);
			cityListView.setOnItemClickListener(listener);	
	}
	//show data on list
	
	
		
	}
	
	// get byte from server by url for saving in database
	public byte[] getBitmap(String url)
	{
		
		
		try
		{
		 Bitmap bitmap=null;
         URL imageUrl = new URL(url);
         
         HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
         conn.setConnectTimeout(30000);
         conn.setReadTimeout(30000);
         conn.setInstanceFollowRedirects(true);
         InputStream is=conn.getInputStream();
         
         //This method is for scaling
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;
       
         BitmapFactory.decodeStream(is,null,o);
        
         final int REQUIRED_SIZE=200;
         
         int width_tmp=o.outWidth, height_tmp=o.outHeight;
         int scale=1;
         while(true){
             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                 break;
             width_tmp/=2;
             height_tmp/=2;
             scale*=2;
         }
         
         //decode with inSampleSize
         BitmapFactory.Options o2 = new BitmapFactory.Options();
         o2.inSampleSize=scale;
       
        
       
	       bitmap=BitmapFactory.decodeStream(is,null,o);
	      
	      is.close();
	  		
	      //Convert bitmap into byte stream
	      ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		
		byte imageInByte[] = stream.toByteArray();
		
		bitmap.recycle();
		
		return imageInByte;
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	
	//Click listener for citylisting
	OnItemClickListener listener=new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			
			
			View v=arg1;
			/*
			TextView txtid=(TextView)v.findViewById(R.id.cityid);
			TextView txtName=(TextView)v.findViewById(R.id.txtCityName);
			Toast.makeText(CityListing.this, "id "+txtid.getText()+"  "+txtName.getText(), 1000).show();*/
			
			Intent in=new Intent(CityListing.this,RestaurantListing.class);
			System.out.println("City id "+cityList.get(arg2).getCityId() );
			System.out.println(cityList.get(arg2));
			in.putExtra("cityId",""+ cityList.get(arg2).getCityId() );
			in.putExtra("cityName", cityList.get(arg2).getCityName());
			startActivity(in);	
			
		}
	};
	
	//This class is used for setting adapter 
	class CityAdapter extends ArrayAdapter<City>
	{
		Context mcontext;
		ArrayList<City> cityList;
		com.restedge.util.ImageLoader	imageLoader;
		public CityAdapter(Context context, int textViewResourceId, ArrayList<City> objects) {
			super(context, textViewResourceId, objects);
			
			mcontext=context;
			cityList=objects;
			imageLoader=new com.restedge.util.ImageLoader(mcontext.getApplicationContext());
			
		}
		//This method will set the layout on citylisting
		public View getView(int position,View convertView,ViewGroup parent)
		{
			if(convertView==null)
				convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.customcity, null);
			
			final City city=cityList.get(position);
			ImageView cityImage=(ImageView)convertView.findViewById(R.id.imageCity);
			TextView cityName=(TextView)convertView.findViewById(R.id.txtCityName);
			final TextView cityId=(TextView)convertView.findViewById(R.id.cityid);
			TextView  cityevent=(TextView)convertView.findViewById(R.id.cityevent);
			
			
			SpannableString content = new SpannableString(cityevent.getText().toString());
			content.setSpan(new UnderlineSpan(), 0, cityevent.getText().toString().length(), 0);
			content.setSpan(new StyleSpan(Typeface.BOLD), 0,cityevent.getText().toString().length(), 0);
			cityevent.setText(content);
			
			
			cityevent.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Intent in=new Intent(mcontext,EventListing.class);
					in.putExtra("cityId", city.getCityId());
					in.putExtra("ClassName", "City");
					startActivity(in);
					
					((Activity)mcontext).finish();
					
				}
			});
			cityImage.setTag(""+city.getCityId());
			imageLoader.DisplayImage(""+city.getCityId(), CityListing.this, cityImage);
			cityId.setText(""+city.getCityId());
			cityName.setText(city.getCityName());
			
			
			return convertView;
		}
	}
	//Get list of cities from server 
	public class GettingCity extends AsyncTask<String, Void, String> {
	
		int result;
		List<City> cities;
		@Override
	    protected void onPreExecute() {
	    	
	    	super.onPreExecute();
	    	dialog=new ProgressDialog(CityListing.this);
	    	
	   
	    	dialog.setTitle("Edge");
	    	dialog.setMessage("Please wait");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
	    	
	    }
	    @Override
	    protected String doInBackground(String... params) {
	       
			int check= pref.getInt("Update", -1);
	//	int check=-1;
			try
			{
				System.out.println("Check "+check);
				//for create connection with server
				DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost requestLogin = new HttpPost(cityListUrl);
		         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		         nameValuePairs.add(new BasicNameValuePair("lastUpdatedId", ""+check));//passing 
		   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     HttpResponse response = httpClient.execute(requestLogin);
			     HttpEntity entity = response.getEntity();
			     is= entity.getContent();
		     
			     //Get parsed cities
			     List<City> list= SAXXMLParser.parse(is);
				 System.out.println("sizes  "+list.size());
				 cities=list;
				 
				 
				 //  new InputSource( url.openStream());
				 //Get parsed values like result,lastupdates,message
			     map= SAXXMLParser.map;
			     result=Integer.parseInt(map.get("Result"));
			     System.out.println("result  " +result);
			     message=map.get("Message");
			     System.out.println("Message"+message);
			     System.out.println("Last updated "+map.get("LastUpdatedCities"));
			     LastUpdatedCities=Integer.parseInt(map.get("LastUpdatedCities"));
			    supportEmail=map.get("SupportEmail"); 
			    
			    pref = CityListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
			    SharedPreferences.Editor edit=pref.edit();
			    System.out.println("SupportEmail "+supportEmail);
			    edit.putString("SupportEmail", supportEmail);
			    
			    edit.commit();
			    pref = CityListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
			    receiptId=pref.getString("SupportEmail", "");
				System.out.println("receiptId "+receiptId);
			    
			}
			catch(Exception e){
				
				System.out.println("error during parsing");
				e.printStackTrace();
			}
			
			System.out.println("Check "+check);
			
		if(result==0)
		{	
			System.out.println("Last updates  "+LastUpdatedCities);
			if(LastUpdatedCities>check)
			{
				try
				{
					System.out.println("size "+cities.size());
					//Insert  or update cities in database
						for (int i=0;i<cities.size();i++)
						{
							
							System.out.println("hello");
							RestaurantDatabase db=new RestaurantDatabase(CityListing.this);
							
							if(cities.get(i).getVisibility_flag()==0)
							{
								City city= db.getCity(cities.get(i).getCityId());
								if(city==null)
									continue;
								else
								{
									db.deleteCity(cities.get(i).getCityId());
									continue;
								}
							}
							
							//Check city exist or not
							City city= db.getCity(cities.get(i).getCityId());
							if(city==null)
							{
								cities.get(i).setCityimage(null);
								
								db.addCity(cities.get(i));
							}
							//update city
							else
							{
							//	City city= db.getCity(cities.get(i).getCityId());
								System.out.println(city);
								int cityid=cities.get(i).getCityId();
								cities.get(i).setCityimage(null);
								db.updateCity(cityid, cities.get(i));
							}
							
							
						
						}
						//update shared preference
						SharedPreferences.Editor edit1=pref.edit();
						edit1.putInt("Update", LastUpdatedCities);
						edit1.commit();
	//					LastUpdatedCities=1;
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
			
			
		}
		
		return null;
	    }        
	
	    @SuppressWarnings("deprecation")
		@Override
	    protected void onPostExecute(String str) {
	    	
	    	dialog.dismiss();
	    	//Check any problem
	    	if(result!=0)
	    	{
	    		AlertDialog alertdialog=new AlertDialog.Builder(CityListing.this).create();
	    		System.out.println("check connection");
	    		alertdialog.setTitle("Edge");
	    		alertdialog.setMessage(message);
	    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				// TODO Auto-generated method stub
	    				
	    			}
	    		});
	    		alertdialog.show();
	    	}
	    	RestaurantDatabase db=new RestaurantDatabase(CityListing.this);
//	    	ArrayList<City> citie=
	    	cityList=db.getAllCities();
	    	
	    	//check wether cities are available or not
	     	if(cityList.size()==0)
				Toast.makeText(CityListing.this, "no data found", 1000).show();
			
	     	View footerView=CityListing.this.getLayoutInflater().inflate(R.layout.footerhelp, null);
		//	cityListView.addFooterView(footerView);
			
			
	     	//this is adpter on citylisting
	     	CityAdapter adapter=new CityAdapter(CityListing.this, android.R.layout.simple_list_item_1, cityList);
			cityListView.setAdapter(adapter);
			
			
			
			
			//Start backend service to download images
			startService(new Intent(CityListing.this, ImageDownloadServices.class));
			
	    	
	    }
	
	   
	}
//	@Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main, menu);
//        return true;
//    }
//	public boolean onOptionsItemSelected(MenuItem item){
//	    try{
//	        switch(item.getItemId()){
//	            case R.id.helpmenu:
//	            	
//	            	Intent in=new Intent(CityListing.this,HelperActivity.class);
//	            	in.putExtra("receiptId", receiptId);
//	            	
//	            	startActivity(in);
//	            	
////	            	Intent intent=new Intent(Intent.ACTION_SEND);
////	            	String[] recipients={receiptId};
////	            	intent.putExtra(Intent.EXTRA_EMAIL, recipients);
////	            	intent.putExtra(Intent.EXTRA_SUBJECT,EmailSubject);
////	            	intent.putExtra(Intent.EXTRA_TEXT,"");
////	            	
////	            	intent.setType("plain/text");
////	            	startActivity(Intent.createChooser(intent, "Contact Uteliv via"));
//	    }
//	    }catch(Exception e){
//	        Log.i("Sleep Recorder", e.toString());
//	    }
//	    return true;
//	}
	public void showToast(String str)
	{
		Toast.makeText(CityListing.this, str, 1000).show();
	}
	public void onFooterClick(View v)
	{
		Intent in=new Intent(CityListing.this,HelperActivity.class);
    	in.putExtra("receiptId", receiptId);
    	
    	startActivity(in);
		
	}
	//stopService(new Intent(this, BackgroundMusicService.class));
}