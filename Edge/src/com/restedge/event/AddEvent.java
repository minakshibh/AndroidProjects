package com.restedge.event;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.restedge.utelite.R;
import com.restedge.util.AddEventBean;
import com.restedge.util.City;
import com.restedge.util.CitySpinAdapter;
import com.restedge.util.Place;
import com.restedge.util.PlacesSpinAdapter;
import com.restedge.util.RestaurantDatabase;
import com.restedge.util.SAXXMLParser;


public class AddEvent extends Activity{

	ImageView timepickerstart,datepickerstart,timepickerend,datepickerend;
	TextView timestart,datestart,timeend,dateend;
	EditText eventName,eventDescription,otherCity,otherVenue;
	private TimePicker timePicker1;
	Spinner spinner,spinnerRestaurant;
	private int hour;
	private int minute;
	private int year;
	private int month;
	private int day;
	ProgressDialog dialog;
	HashMap<String,ArrayList<String>> map=new HashMap<String,ArrayList<String>>();
	ArrayList<HashMap<String, ArrayList<String>>> hashmap=new ArrayList<HashMap<String, ArrayList<String>>>();
	
	ArrayList<City> citylist=new ArrayList<City>();
	
	SharedPreferences pref,userPreference;
	Map<String, String> mapotherdata=null;
	String encoded=null;
	String VenueName=null;
	ArrayList<String> cityname=new ArrayList<String>();
	ArrayList<Integer> cityid=new ArrayList<Integer>();
	 ArrayList<AddEventBean> addeventsplaces=new ArrayList<AddEventBean>();
	int vanueId=-1;
	
	
	
	boolean flag=false;
	int cityId;
	InputStream is;
	String method_name="getAllPlaces";
	String selectedcity="",venue="";
	Button uploadImage;
	ImageButton btnSave,btnBack;
	 int mGalleryCount=1;
	 int mGalleryInitializedCount=0;
	String city[]={"Please select","Bathinda","Mansa","Patiala"};
	String restaurant[]={"Please select","Fun city","Gopal","Sindhi","Domino","Other city"};
	int REQUEST_CAMERA=0,SELECT_GALLERY=1;
	String SELECT_FILE="Select File";
	ScrollView scrollview;
	static ContentResolver cr;
	static final int TIME_DIALOG_ID_START=0,TIME_DIALOG_ID_END=1,DATE_DIALOG_ID_START=3,DATE_DIALOG_ID_END=4;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.addevent);
		
		
		btnSave=(ImageButton)findViewById(R.id.save);
		timepickerstart=(ImageView)findViewById(R.id.timepicker);
		timepickerend=(ImageView)findViewById(R.id.timepickerend);
		datepickerstart=(ImageView)findViewById(R.id.datepicker);
		datepickerend=(ImageView)findViewById(R.id.datepickerend);
		eventName=(EditText)findViewById(R.id.eventname);
		eventDescription=(EditText)findViewById(R.id.eventDescrition);
		spinner=(Spinner)findViewById(R.id.selectcity);
		spinnerRestaurant=(Spinner)findViewById(R.id.selectvenue);
		otherCity=(EditText)findViewById(R.id.otherCity);
		otherVenue=(EditText)findViewById(R.id.otherVenue);
		btnBack=(ImageButton)findViewById(R.id.back);
		
		scrollview=(ScrollView)findViewById(R.id.scrollview);
		
		userPreference = this.getSharedPreferences("UserPreference", MODE_PRIVATE);
		
		otherCity.setFocusableInTouchMode(false);
		otherCity.setFocusable(false);
		otherCity.setBackgroundResource(R.drawable.non_editable);
		
		otherVenue.setFocusableInTouchMode(false);
		otherVenue.setFocusable(false);
		otherVenue.setBackgroundResource(R.drawable.non_editable);
		
		
		timestart=(TextView)findViewById(R.id.timestart);
		timeend=(TextView)findViewById(R.id.timeend);
		datestart=(TextView)findViewById(R.id.datestat);
		dateend=(TextView)findViewById(R.id.datend);
		
		uploadImage=(Button)findViewById(R.id.uploadImage);
		
		uploadImage.setOnClickListener(listener);
		
		cr=getContentResolver();
		
		 	 
		
		 final Calendar c = Calendar.getInstance();
	        year  = c.get(Calendar.YEAR);
	        month = c.get(Calendar.MONTH);
	        day   = c.get(Calendar.DAY_OF_MONTH);
		
	       hour =c.get(Calendar.HOUR_OF_DAY);
	        minute=c.get(Calendar.MINUTE);

		timepickerstart.setOnClickListener(listener);
		timepickerend.setOnClickListener(listener);
		datepickerstart.setOnClickListener(listener);
		datepickerend.setOnClickListener(listener);
		btnSave.setOnClickListener(listener);
		btnBack.setOnClickListener(listener);
		
		
		RestaurantDatabase db=new RestaurantDatabase(this);
		
		citylist=db.getAllCities();
		
		
		
		City city=new City();
		city.setCityId(-1);
		city.setCityName("Other City");
		
		
		citylist.add(city);
		for(int i=0;i<citylist.size();i++)
		{
			City city1=citylist.get(i);
			cityname.add(city1.getCityName());
			cityid.add(city1.getCityId());
			
		}
		
		City ci=new City();
		ci.setCityId(-2);
		ci.setCityName("Please select");
		citylist.add(0,ci);
		cityid.add(0, -2);
		
//		eventDescription.setOnTouchListener(tlistener);
//		eventName.setOnTouchListener(tlistener);
		
		Intent in=getIntent();
		
		if(in.hasExtra("cityId"))
			cityId=in.getIntExtra("cityId", 0);
		String url=getResources().getString(R.string.app_url)+"/"+method_name;
		
		ArrayList<Place> placeNightClub=db.getTypePlaceList(("NightClub"),""+cityId);
		ArrayList<Place> placeRestaurant=db.getTypePlaceList(("Restaurant"),""+cityId);
		
		
		
		placeRestaurant.addAll(placeNightClub);
		
		Log.e(" Add Places ", cityId+"  "+placeRestaurant.size());
		if(placeRestaurant.size()==0)
		{
//			http://112.196.24.205:501/UtiLiveWebAccess.asmx/getAllPlaces
			 new GetAllPlaces().execute(url);
			flag=false;
		}
		else
		{
			new GetAllPlaces().execute(url);
			flag=true;
		}
//		for(int i=0;i<city.length;i++)
//		{
//			
//			ArrayList<String> list=new ArrayList<String>();
//			for(int j=0;j<restaurant.length;j++)
//			{
//				list.add(restaurant[j]);
//				if(i==4)
//					break;
//				if(i==2)
//					break;
//			}
//			map.put(city[i], list);
//		}
//		
		
		CitySpinAdapter adapter=new CitySpinAdapter(this, android.R.layout.simple_spinner_item,citylist);
		
		
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,int arg2, long arg3) {
						
				if (mGalleryInitializedCount < mGalleryCount)
		        {
		            mGalleryInitializedCount++;
		            
		        }
		        else
		        {
		        	
		        	RestaurantDatabase db=new RestaurantDatabase(AddEvent.this);
		        	int cityId=cityid.get(arg2);
		        	Log.e("city ", ""+cityId);
		        	ArrayList<Place> places=new ArrayList<Place>();
		        	if(flag)
		        	{
			        	ArrayList<Place> placeRestaurant=db.getTypePlaceList(("Restaurant"),""+cityId);
			        	ArrayList<Place> placeNightClub=db.getTypePlaceList(("NightClub"),""+cityId);
			        	
			        	
			        	places.addAll(placeRestaurant);
			        	places.addAll(placeRestaurant.size(), placeNightClub);
		        	}
		        	else
		        	{
		        		System.out.println("addeventsplaces  "+addeventsplaces.size());
		        		for(int i=0;i<addeventsplaces.size();i++)
		        		{
		        			AddEventBean bean=addeventsplaces.get(i);
		        			if(cityId==bean.getCityId())
		        			{
		        				
		        				Place place=new Place();
		        				place.setId(bean.getPlaceId());
		        				place.setName(bean.getPlaceName());
		        				places.add(place);
		        			}
		        			
		        		}
		        		
		        	}
		        	Place place=new Place();
		        	place.setId(-1);
		        	place.setName("Other venue");
		        	places.add(place);
		        	
//		            //only detect selection events that are not done whilst initializing
//		        	ArrayList<String> list=map.get(city[arg2]);
//		        	Log.e("Data",""+ list.size());
		        	
		        	Log.e("cityId ",""+ cityId);
		        	if(cityId==-1)
		        	{
		        		otherCity.setFocusableInTouchMode(true);
		        		otherCity.setFocusable(true);
		        		otherCity.setBackgroundResource(R.drawable.text_bg);
		        	}
		        	else
		        	{
		        		otherCity.setFocusableInTouchMode(false);
		        		otherCity.setFocusable(false);
		        		otherCity.setText("");
		        		otherCity.setBackgroundResource(R.drawable.non_editable);
		        		
		        	}
					PlacesSpinAdapter adapter1=new PlacesSpinAdapter(AddEvent.this, android.R.layout.simple_spinner_item,places);
					
					spinnerRestaurant.setAdapter(adapter1);
					
					spinnerRestaurant.setOnItemSelectedListener(placeselected);
					
		        }
				
				
				
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			
			}
		});
		
		
	}
	class GetAllPlaces extends AsyncTask<String, Void, String>
	{

		@Override
	    protected void onPreExecute() {
	    	
	    	super.onPreExecute();
	    	dialog=new ProgressDialog(AddEvent.this);
	    	
	   
	    	dialog.setTitle("Loading places");
	    	dialog.setMessage("Please wait");
	    	dialog.setCanceledOnTouchOutside(false);
	    	dialog.show();
	    	
	    }
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			try
			{
				System.out.println(params[0]);
			DefaultHttpClient httpClient = new DefaultHttpClient();
	        HttpPost requestLogin = new HttpPost(params[0]);
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	         
	         
	   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     HttpResponse response = httpClient.execute(requestLogin);
		     HttpEntity entity = response.getEntity();
		     is= entity.getContent();
			
		     addeventsplaces = SAXXMLParser.getAddEventPlaces(is);
		     mapotherdata= SAXXMLParser.getEventsOtherData(is);
		     
		     System.out.println("addeventsplaces parse"+addeventsplaces.size());
		     
		     
		     System.out.println("city id");
			}
			catch(Exception e)
			{
				e.printStackTrace();
				
				
			}
		     
			
			return null;
		}
		protected void onPostExecute(String str) 
		{
	    	
	    	dialog.dismiss();
		}
	}
	OnItemSelectedListener placeselected=new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			
			LinearLayout linearlayout=(LinearLayout)arg1;
			
			TextView txtId=(TextView)linearlayout.findViewById(R.id.id);
			TextView txtname=(TextView)linearlayout.findViewById(R.id.name);
			int vid=Integer.parseInt(txtId.getText().toString());
			Log.e("vid", ""+vid);
			VenueName=txtname.getText().toString();
			if(Integer.parseInt(txtId.getText().toString())==-1)
			{
				otherVenue.setFocusableInTouchMode(true);
        		otherVenue.setFocusable(true);
        		vanueId=vid;
        		otherVenue.setBackgroundResource(R.drawable.text_bg);
			}
			else
			{
				otherVenue.setFocusableInTouchMode(false);
        		otherVenue.setFocusable(false);
        		otherVenue.setText("");
        		vanueId=-1;
        		otherVenue.setBackgroundResource(R.drawable.non_editable);
			}
			
		}

		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
	};
	OnTouchListener tlistener=new OnTouchListener() {
		
		public boolean onTouch(View v, MotionEvent event) {
			if (v.getId() == R.id.eventname) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                }
            }
			else if(v.getId()==R.id.eventDescrition)
			{
				 v.getParent().requestDisallowInterceptTouchEvent(true);
	                switch (event.getAction() & MotionEvent.ACTION_MASK) {
	                case MotionEvent.ACTION_UP:
	                    v.getParent().requestDisallowInterceptTouchEvent(false);
	                    break;
	                }
			}
			return false;
		}
	};
	OnClickListener listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			
			switch (v.getId())
			{
				case R.id.timepicker:
					showDialog(TIME_DIALOG_ID_START);
					break;
				case R.id.timepickerend:
					showDialog(TIME_DIALOG_ID_END);
					break;
				case R.id.datepicker:
					showDialog(DATE_DIALOG_ID_START);
					break;
				case R.id.datepickerend:
					showDialog(DATE_DIALOG_ID_END);
					break;
				case R.id.uploadImage:
					selectImage();
					
					break;
				case R.id.save:
					
					
					
						Log.e("User Id", userPreference.getString("UserId",""));
					
						String userId=userPreference.getString("UserId","");
					
						boolean check=checkvalue();
						
						if(check==true)
						{
							String url = getResources().getString(R.string.app_url)+"/"+"SaveEvent";
							new RequestTask().execute(url,userId);
							
							
						}
					
					
					break;
				case R.id.back:
					

					int eventnamelength=eventName.length();
					int eventdescription=eventDescription.getText().toString().length();
					int eventstartDate=datestart.getText().toString().length();
					int eventendDate=dateend.getText().toString().length();
					int eventstartTime=timestart.getText().toString().length();
					int eventendTime=timeend.getText().toString().length();
						City city=(City)spinner.getSelectedItem();
						Place place=(Place)spinnerRestaurant.getSelectedItem();
						
						if(eventnamelength>0 || eventdescription>0 || eventendTime>0 ||eventstartTime>0|| eventstartDate>0 ||eventendDate>0|| city.getCityId()>0  )
						{
					
							final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
				    		
				    		alertdialog.setTitle("Edge");
				    		alertdialog.setMessage("Do you want to save event");
				    		alertdialog.setButton("Yes", new DialogInterface.OnClickListener() {
				    			
				    			public void onClick(DialogInterface dialog, int which) {
				    				// TODO Auto-generated method stub
				    				
				    				alertdialog.dismiss();
				    				
				    			}
				    		});
				    		alertdialog.setButton2("No", new DialogInterface.OnClickListener() {
				    			
				    			public void onClick(DialogInterface dialog, int which) {
				    				// TODO Auto-generated method stub
				    				
				    				alertdialog.dismiss();
				    				AddEvent.this.finish();
				    			}
				    		});
				    		alertdialog.show();
					
						}
						else
							AddEvent.this.finish();
					
					break;
			}
			
		}
	};
	
	private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
 
        AlertDialog.Builder builder = new AlertDialog.Builder(AddEvent.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                	Intent cameraIntent = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                     startActivityForResult(cameraIntent,REQUEST_CAMERA);
                   
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECT_GALLERY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        
        
        	
        	
            if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            	 Bitmap photo = (Bitmap) data.getExtras().get("data");
                try {
                    Bitmap bm,bitmap;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
 
                    
 
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
                    photo.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    InputStream is1 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray()); 
                    InputStream is2 = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    
                    
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(is1,null,o);

                   
                    
                    
                    int sampleSize = calculateInSampleSize(o, 200, 200);
                    
                    o.inJustDecodeBounds = false;
                    o.inSampleSize=sampleSize;
                    
           	      System.out.println("city service scale factor  "+sampleSize);
                    
           	       bitmap=BitmapFactory.decodeStream(is2,null,o);
           	       System.out.println("city service "+ bitmap);
           	       is1.close();
           	  		is2.close();
           	  		is1.close();
                    
                    
                    
                    
                    
                
                  
                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();  
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream1);
                    byte[] byteArray = byteArrayOutputStream1 .toByteArray();
                    
                     encoded= Base64.encodeToString(byteArray, Base64.DEFAULT);
                    
                    System.out.println(encoded);
                    
                    
                   
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
              if (requestCode == SELECT_GALLERY && resultCode == RESULT_OK  && null != data)
              {
                Uri selectedImageUri = data.getData();
 
                try
                {
                 ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); 
                Bitmap bitmap=getThumbnail(selectedImageUri);
                
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                
                encoded= Base64.encodeToString(byteArray, Base64.DEFAULT);
                
                System.out.println(encoded);
                
                }
                catch(Exception e)
                {
                	
                	e.printStackTrace();
                }
//                String tempPath = getPath(selectedImageUri, AddEvent.this);
//                Bitmap bm;
//                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
//                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
//                
//                
//                
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();  
//                bm.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                byte[] byteArray = byteArrayOutputStream .toByteArray();
                
//                encoded= Base64.encodeToString(byteArray, Base64.DEFAULT);
//                System.out.println(encoded);
                
                
                
           }
        
       
    }
	 public static Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException{
	        InputStream input = cr.openInputStream(uri);

	        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
	        onlyBoundsOptions.inJustDecodeBounds = true;
	        onlyBoundsOptions.inDither=true;//optional
	        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
	        input.close();
	       
	        int originalSize = calculateInSampleSize(onlyBoundsOptions, 200, 200);

	       

	        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	        bitmapOptions.inSampleSize = originalSize;
	        bitmapOptions.inDither=true;//optional
	        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
	        bitmapOptions.inJustDecodeBounds=false;
	        input = cr.openInputStream(uri);
	        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
	        input.close();
	        return bitmap;
	    }
	public static int calculateInSampleSize(
			 BitmapFactory.Options options, int reqWidth, int reqHeight) {		
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {

		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;

		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }

		
		    return inSampleSize;
		}
	public String getPath(Uri uri, Activity activity) {
        String[] projection = { MediaColumns.DATA };
        @SuppressWarnings("deprecation")
		Cursor cursor = activity
                .managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
		{
			case TIME_DIALOG_ID_START:
				
				return new TimePickerDialog(this,timePickerListenerStart, hour, minute,false);
			case TIME_DIALOG_ID_END:
				
				return new TimePickerDialog(this,timePickerListenerEnd, hour, minute,false);
			case DATE_DIALOG_ID_START:
				
				return new DatePickerDialog(this, datePickerListenerStart,year, month,day);
			case DATE_DIALOG_ID_END:
				
				return new DatePickerDialog(this, datePickerListenerEnd,year, month,day);
		}
		return null;
	}
	private TimePickerDialog.OnTimeSetListener timePickerListenerStart = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			
			
			hour = selectedHour;
			minute = selectedMinute;
 
			// set current time into textview
			timestart.setText(new StringBuilder().append(pad(selectedHour))
					.append(":").append(pad(selectedMinute)).append(":").append("00"));
 
		
 
		}
	};
	private TimePickerDialog.OnTimeSetListener timePickerListenerEnd = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			
			
			hour = selectedHour;
			minute = selectedMinute;
 
		
			timeend.setText(new StringBuilder().append(pad(selectedHour))
					.append(":").append(pad(selectedMinute)).append(":").append("00"));
 
 
		}
	};
	private DatePickerDialog.OnDateSetListener datePickerListenerStart 
    = new DatePickerDialog.OnDateSetListener()
	{

			// when dialog box is closed, below method will be called.
			public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			// set selected date into textview
			datestart.setText(new StringBuilder().append(year).append("-").append(pad(month + 1))
			   .append("-").append(pad(day))
			   );
			
		}
	};
	private DatePickerDialog.OnDateSetListener datePickerListenerEnd 
    = new DatePickerDialog.OnDateSetListener()
	{

			// when dialog box is closed, below method will be called.
			public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			
			// set selected date into textview
			dateend.setText(new StringBuilder().append(year).append("-").append(pad(month + 1))
			   .append("-").append(pad(day))
			   );
			
		}
	};
	@SuppressWarnings("deprecation")
	public boolean checkvalue()
	{
		selectedcity="";
		venue="";
		boolean flag=false;
		
		
		System.out.println(datestart.getText().toString().length()); 
		System.out.println(dateend.getText().toString().length()); 
		
		City txt=(City)spinner.getSelectedItem();
		Log.e("Event name", eventName.getText().toString());
		 Log.e("Description ",eventDescription.getText().toString());
		 Log.e("Spinner", txt.getCityName());
		 Log.e("city ", ""+spinnerRestaurant.getSelectedItem());
		 try
		 {
			 
			 if(!(eventName.getText().toString().length()>0))
			 {
				 showDialog("Please enter event name");
				 return false;
			 }
			 if(!(eventDescription.getText().toString().length()>0))
			 {
				 showDialog("Please enter description");
				 return false;
			 }
			 if(datestart.getText().toString().length()==0 && dateend.getText().toString().length()==0)
			 {
				 showDialog("Please enter dates");
				 return false;
			 }
			 
			 String startTime=timestart.getText().toString();
			 String endTime=timeend.getText().toString();
			 
			 
			 String startsplitTime[]=startTime.split(":");
			 String endsplitTime[]=endTime.split(":");
			 
			 int startHour=Integer.parseInt(startsplitTime[0]);
			 int startMinute=Integer.parseInt(startsplitTime[1]);
			 
			 int endHour=Integer.parseInt(endsplitTime[0]);
			 int endminute=Integer.parseInt(endsplitTime[1]);
			 
			
			 
			
			 
			 
			 
			 	DateFormat df = new SimpleDateFormat("dd-MM-yyyy"); 
			 
			 
			 
				Date d= df.parse(datestart.getText().toString());
				
				Date d1=df.parse(dateend.getText().toString());
				
				
				 Calendar startc=Calendar.getInstance();
				 Calendar endc=Calendar.getInstance();
				 
				 startc.set(Calendar.HOUR_OF_DAY, startHour);
				 startc.set(Calendar.MINUTE, startMinute);
				 
				 endc.set(Calendar.HOUR_OF_DAY, endHour);
				 endc.set(Calendar.MINUTE, endminute);
				
				 
				 Log.e("start time", "  "+d.getDate()+"  "+d1.getDate());
				
				startc.set(d.getYear()+1900, d.getMonth(), d.getDate());
				endc.set(d1.getYear()+1900, d1.getMonth(), d1.getDate());
				
				
				Calendar currentc=Calendar.getInstance();
				
				
				Log.e("Start time",""+ startc.getTime());
				Log.e("End time", ""+endc.getTime());
				Log.e("Curent", ""+currentc.getTime());
				if(endc.compareTo(startc)<=0 && startc.compareTo(currentc)<=0)
				{
				
				
				
				
				
					
					final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
		    		
		    		alertdialog.setTitle("Edge");
		    		alertdialog.setMessage("Please enter valid dates");
		    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    				
		    				alertdialog.dismiss();
		    				
		    			}
		    		});
		    		alertdialog.show();
					
		    		return false;
					
					
					
				}
				
				System.out.println("Date "+d);
				
				City city=(City)spinner.getSelectedItem();
				Place place=(Place)spinnerRestaurant.getSelectedItem();
				if(city.getCityName().equalsIgnoreCase("Please select"))
				{
					showDialog("Please select city");
					return false;
				}
				if(city.getCityName().equalsIgnoreCase("Other City"))
				{
					selectedcity=otherCity.getText().toString();
				}
				else
				{
					selectedcity=city.getCityName();
				}
				
				if(place!=null)
				{
					if(place.getName().equalsIgnoreCase("Other venue"))
					{
						venue=otherVenue.getText().toString();
					}
					else
						venue=place.getName();
				}
				
				Log.e("Venue ",""+ venue);
				
				if(selectedcity.length()==0 )
				{
					showDialog("Please select the city");
					return false;
				}
				if(venue.length()==0)
				{
					showDialog("Please select the venue");
					return false;
				}
				if( timestart.getText().toString().length()>0 && timeend.getText().toString().length()>0)
				{
					flag=true;
					
				}
				else
				{
					showDialog("Please enter time");
					return false;
				}
				
				return flag;
				
		 }
		 catch(Exception e)
		 {
			 e.printStackTrace();
			
			 return false;
		 }
		
		
		
		
	}
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
		
		alertdialog.setTitle("Edge");
		alertdialog.setMessage(str);
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				alertdialog.dismiss();
				
			}
		});
		alertdialog.show();
	}
	class RequestTask extends AsyncTask<String, String, String>
	{
	    	
		int result=0;
	    	@Override
		    protected void onPreExecute()
	    	{	    	
		    	super.onPreExecute();	 
		    	
		    	 // Progress bar..........
				 dialog=new ProgressDialog(AddEvent.this);
				 dialog.setTitle("Saving event");
			     dialog.setMessage("Please wait");
			     dialog.setCanceledOnTouchOutside(false);	
		    	 dialog.show();
		    	
		    }
	    	
	    	@Override
			protected String doInBackground(String... uri)
			{  
			    
	    		String content = null;
	    		int  cityId = 0;
	    		String CityName = null;
	    		
	    		Place place=(Place)spinnerRestaurant.getSelectedItem();
	    		if(place!=null)
				{
					if(place.getId()==-1)
					{
						venue=otherVenue.getText().toString();
						vanueId=-1;
					}
					else
					{
						venue=place.getName();
						vanueId=place.getId();
					}
				}
	    		
	    		City city=(City)spinner.getSelectedItem();
	    		if(city!=null)
	    		{
	    			if(city.getCityId()==-1)
					{
						CityName=otherCity.getText().toString();
						cityId=-1;
					}
					else
					{
						CityName=city.getCityName();
						cityId=city.getCityId();
						
					}
	    		}
	    		System.out.println(uri[0]);
	    		
	    		
	    		System.out.println(cityId+" "+CityName);
	    		String startDate=datestart.getText().toString()+" "+timestart.getText().toString();
	    		String endDate=dateend.getText().toString()+" "+timeend.getText().toString();
	    		
	    		
	    		DefaultHttpClient httpClient = new DefaultHttpClient();	            
	            HttpPost request = new HttpPost(uri[0]);
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();				           
	          System.out.println("uri[1] "+uri[1]);
	            
	            
            	    nameValuePairs.add(new BasicNameValuePair("EventName", eventName.getText().toString()));
		            nameValuePairs.add(new BasicNameValuePair("EventDesc",eventDescription.getText().toString()));
		            nameValuePairs.add(new BasicNameValuePair("VenueId",""+ vanueId));		
		            nameValuePairs.add(new BasicNameValuePair("VenueName", venue));
		            nameValuePairs.add(new BasicNameValuePair("StartDate",startDate));
		            nameValuePairs.add(new BasicNameValuePair("EndDate",endDate));
		            nameValuePairs.add(new BasicNameValuePair("EventImage", encoded));
		            nameValuePairs.add(new BasicNameValuePair("cityid", ""+cityId));
		            nameValuePairs.add(new BasicNameValuePair("cityName", CityName));
		            nameValuePairs.add(new BasicNameValuePair("UserID",uri[1]));
		            try
		            {
			            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));				            
			            HttpResponse response = httpClient.execute(request);				            
			            HttpEntity entity = response.getEntity();
			            content = EntityUtils.toString(entity);
		    		
			            System.out.println("content "+content);
			            
			            InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
		    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    	        DocumentBuilder db = dbf.newDocumentBuilder();
		    	     
		    	        Document doc = db.parse(is);
		    	        
		    	        System.out.println(doc);
		    	        
		    	        NodeList root = doc.getElementsByTagName("result");
		    	        Log.e("root", ""+root);
		    	        Log.e("", ""+root.item(0).getFirstChild().getNodeValue());
		    	     
		    	        result=Integer.parseInt(root.item(0).getFirstChild().getNodeValue());
		            }
		            catch(Exception e)
		            {
		            	e.printStackTrace();
		            }
		            
		           return null; 
			}
	    	@SuppressWarnings("deprecation")
			@SuppressLint("InlinedApi")
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        //Do anything with response..
		        dialog.dismiss();
		        
		        if(this.result==0)
		    	{
		    		final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
		    		
		    		alertdialog.setTitle("Congratulations");
		    		alertdialog.setMessage("Your event has been submitted for approval");
		    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    				
		    				alertdialog.dismiss();
		    				
				        	AddEvent.this.finish();
				        	
		    			}
		    		});
		    		alertdialog.show();
		    	}
		        else
		        {
		        	final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
		    		
		    		alertdialog.setTitle("Edge");
		    		alertdialog.setMessage("Your event has been not submitted, Try again");
		    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				// TODO Auto-generated method stub
		    				
		    				alertdialog.dismiss();
		    			}
		    		});
		    		alertdialog.show();
		        	
		        	
		        }
		        
		        
		        
	    	}
	}
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	public void showToast(String str)
	{
		Toast.makeText(AddEvent.this, str, 1000).show();
	}
	@SuppressWarnings("deprecation")
	public void onBackPressed()
	{
		int eventnamelength=eventName.length();
		int eventdescription=eventDescription.getText().toString().length();
		int eventstartDate=datestart.getText().toString().length();
		int eventendDate=dateend.getText().toString().length();
		int eventstartTime=timestart.getText().toString().length();
		int eventendTime=timeend.getText().toString().length();
			City city=(City)spinner.getSelectedItem();
			Place place=(Place)spinnerRestaurant.getSelectedItem();
			
			if(eventnamelength>0 || eventdescription>0 || eventstartTime>0|| eventendTime>0 || eventstartDate>0 ||eventendDate>0|| city.getCityId()>0  )
			{
		
				final AlertDialog alertdialog=new AlertDialog.Builder(AddEvent.this).create();
	    		
	    		alertdialog.setTitle("Edge");
	    		alertdialog.setMessage("Do you want to save event");
	    		alertdialog.setButton("Yes", new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				// TODO Auto-generated method stub
	    				
	    				alertdialog.dismiss();
	    				
	    			}
	    		});
	    		alertdialog.setButton2("No", new DialogInterface.OnClickListener() {
	    			
	    			public void onClick(DialogInterface dialog, int which) {
	    				// TODO Auto-generated method stub
	    				
	    				alertdialog.dismiss();
	    				AddEvent.this.finish();
	    			}
	    		});
	    		alertdialog.show();
		
			}
			else
				AddEvent.this.finish();

		
		
	}
	
}
