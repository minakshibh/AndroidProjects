
package com.zoom.karaoke;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.karaoke.util.CardTypeSpinAdapter;
import com.karaoke.util.ConnectionDetector;
import com.karaoke.util.SAXXMLParser;
import com.karaoke.util.Song;
import com.karaoke.util.UnzipUtil;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.karaoke.R;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class PaymentActivity extends Activity{

	Spinner cardTypeSpinner;
	static final int DATE_DIALOG_ID_START=1,DATE_DIALOG_ID_END=2;
	int year,month,day;
	String type,transationUrl,validDateFrom,validDateTo;
	Song song;
	String url,Method_Name="SagePayDirect";
	
	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	int minYear,maxYear;
	ArrayList<String> cardListKey=new ArrayList<String>();
	EditText cardNumber,cardHolder,cvvNumber,userName,userEmail;
	TextView txtDateFrom,txtDateTo,songName,artistName,price;
	ImageButton dateValidFrom,dateValidTo,paynow,back;
	ImageView image;
	Context mcontext;
	Calendar minCalender,maxCalender;
	float priceProduct;
	String albumsDirectory;
	ConnectionDetector detector;
	HashMap<String, String> map=new HashMap<String, String>();
	int selectedValueCardId;
	ArrayList<String> listCardValue=new ArrayList<String>();
	ProgressDialog pDialog;
	SharedPreferences pref;
	Editor edit;
	int position;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.payment);
		
		mcontext=this;
		detector=new ConnectionDetector(mcontext);
		
		
		pref=mcontext.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		
		 edit=pref.edit();
		transationUrl=mcontext.getResources().getString(R.string.appurl)+"/InsertTransaction";
		
		url=getResources().getString(R.string.appurl)+"/"+Method_Name;
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		dateValidFrom=(ImageButton)findViewById(R.id.validFrom);
		dateValidTo=(ImageButton)findViewById(R.id.validTo);
		cardTypeSpinner=(Spinner)findViewById(R.id.cardType);
		txtDateFrom=(TextView)findViewById(R.id.txtDateFrom);
		txtDateTo=(TextView)findViewById(R.id.txtDateTo);
		paynow=(ImageButton)findViewById(R.id.payNow);
		userName=(EditText)findViewById(R.id.name);
		userEmail=(EditText)findViewById(R.id.email);
		cardNumber=(EditText)findViewById(R.id.cardNumber);
		cardHolder=(EditText)findViewById(R.id.cardHolderName);
		cvvNumber=(EditText)findViewById(R.id.cvcNumber);
		image=(ImageView)findViewById(R.id.imageView);
		songName=(TextView)findViewById(R.id.songName);
		artistName=(TextView)findViewById(R.id.artistName);
		price=(TextView)findViewById(R.id.price);
		back=(ImageButton)findViewById(R.id.back);
		
		Intent in=getIntent();
		type=in.getStringExtra("Type");
		if(in.hasExtra("position"))
			position=in.getIntExtra("position", -1);
		
		
			
		if(type.equalsIgnoreCase("Song"))
		{
			song=(Song)in.getSerializableExtra("Ob");
			if(detector.isConnectingToInternet())
			{
				
				Bitmap bitmap=getBitmapFromURL(song.getSongTumbnailUrl());
				image.setImageBitmap(bitmap);
			}
			songName.setText(song.getSongName());
			artistName.setText(song.getArtistName());
			price.setText("£  "+song.getSongPrice());
			priceProduct=song.getSongPrice();
		}
		else
		{
			song=(Song)in.getSerializableExtra("Ob");
			if(detector.isConnectingToInternet())
			{
				
				Bitmap bitmap=getBitmapFromURL(song.getSongTumbnailUrl());
				image.setImageBitmap(bitmap);
			}
			songName.setText(song.getAlbumName());
			artistName.setText(song.getArtistName());
			price.setText("£ "+song.getSongPrice());
			priceProduct=song.getSongPrice();
		}
		init();
		
		
		CardTypeSpinAdapter spinnerAdapter=new CardTypeSpinAdapter(this, android.R.layout.simple_list_item_1,cardListKey);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		
	
		cardTypeSpinner.setAdapter(spinnerAdapter);
		
		
		
		final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);
		
        
         minCalender=Calendar.getInstance();
        minYear=year-10;
        minCalender.set(Calendar.YEAR, minYear);
        maxYear=year+10;
        
        maxCalender=Calendar.getInstance();
        maxCalender.set(Calendar.YEAR, maxYear);
        
        dateValidFrom.setOnClickListener(Listener);
        dateValidTo.setOnClickListener(Listener);
        paynow.setOnClickListener(Listener);
        back.setOnClickListener(Listener);
	}
	public void init()
	{
		cardListKey.add("Select Card Type");
		cardListKey.add("VISA Credit");
		cardListKey.add("VISA Debit");
		cardListKey.add("VISA Electron");
		cardListKey.add("MasterCard Credit");
		cardListKey.add("MasterCard Debit");
		cardListKey.add("Maestro");
		cardListKey.add("American Express");
		cardListKey.add("Diner's Club");
		cardListKey.add("JCB Card");
		cardListKey.add("Laser");
		
		listCardValue.add("Select Card Type");
		listCardValue.add("VISA");
		listCardValue.add("DELTA");
		listCardValue.add("UKE");
		listCardValue.add("MC");
		listCardValue.add("MCDEBIT");
		listCardValue.add("MAESTRO");
		listCardValue.add("AMEX");
		listCardValue.add("DC");
		
		listCardValue.add("JCB");
		listCardValue.add("LASER");
		
		cardNumber.setText("5404000000000001");
		
	}
	OnClickListener Listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.validFrom:
					showDialog(DATE_DIALOG_ID_START);
					break;
				case R.id.validTo:
					showDialog(DATE_DIALOG_ID_END);
					break;
				case R.id.payNow:
					
					boolean flag=checkValue();
					if(flag)
					{
						final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
						
						alertdialog.setTitle("Zoom-Karaoke");
						alertdialog.setMessage("Continue payment ?");
						alertdialog.setButton("Yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								alertdialog.dismiss();
								
									new MakePayMent().execute(url);
								
							}
						});
						alertdialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
								alertdialog.dismiss();
								
							}
						});
						alertdialog.show();
					}
					
					
					break;
				case R.id.back:
					finish();
			}
			
			
		}
	};
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) 
		{
			
			case DATE_DIALOG_ID_START:
				
				return customDatePickerStart();
//				return new DatePickerDialog(this, datePickerListenerStart,year, month,day);
			case DATE_DIALOG_ID_END:
				return customDatePickerLast();
//				return new DatePickerDialog(this, datePickerListenerEnd,year, month,day);
		}
		return null;
	}
	private DatePickerDialog.OnDateSetListener datePickerListenerStart 
    = new DatePickerDialog.OnDateSetListener()
	{

		
			// when dialog box is closed, below method will be called.
			public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			
			Log.e("Id ",""+view.getId());
			
//			view.updateDate(maxYear, selectedMonth, selectedDay);
			
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;
			selectedYear=year%100;
			
			StringBuilder builder=new StringBuilder().append(pad(month + 1))
					   .append(pad(selectedYear));
			// set selected date into textview
			
			validDateFrom=builder.toString();
			txtDateFrom.setText(new StringBuilder().append(pad(month + 1)).append("-")
			   .append(pad(selectedYear))
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
			
			selectedYear=year%100;
			// set selected date into textview
			
			StringBuilder builder=new StringBuilder().append(pad(month + 1))
					   .append(pad(selectedYear));
			validDateTo=builder.toString();
			txtDateTo.setText(new StringBuilder().append(pad(month + 1)).append("-")
			   .append(pad(selectedYear))
			   );
			
		}
			
        	
			
	};
	private static String pad(int c) {
		if (c >= 10)
		   return String.valueOf(c);
		else
		   return "0" + String.valueOf(c);
	}
	public boolean checkValue()
	{
		
		int cardkey=cardTypeSpinner.getSelectedItemPosition();
		selectedValueCardId = cardkey;
		String email = userEmail.getText().toString();
		
		System.out.println(userName.getText().toString()+" "+email);
		
		if(cardkey==0)
		{
			showDialog("Please select card type");
			return false;
		}
		else if(cardNumber.getText().toString().equalsIgnoreCase(""))
		{
			showDialog("Please enter card number");
			return false;
		}
		else if(userName.getText().toString().trim().equalsIgnoreCase(""))
		{
			
			System.out.println(userName.getText().toString());
			showDialog("Please enter your name");
			return false;
		}
		else if(email.equals(""))
		{
			System.out.println(userName.getText().toString());
			showDialog("Please enter your email");
			return false;
		}
		else if(!email.equals("") && !email.matches(emailPattern))
		{
			
				showDialog("Please enter valid email address");
				return false;
		}
		else if(cardHolder.getText().toString().equalsIgnoreCase(""))
		{
			showDialog("Please enter card holder name");
			return false;
		}
		else if(txtDateFrom.getText().toString().equalsIgnoreCase(""))
		{
			showDialog("Please select card valid from date");
			return false;
		}
		else if(txtDateTo.getText().toString().equalsIgnoreCase(""))
		{
			showDialog("Please select card valid to date");
			return false;
		}
		
		else if(cvvNumber.getText().toString().equalsIgnoreCase(""))
		{
			showDialog("Please enter cvv number");
			return false;
		}
		else if(!(txtDateFrom.getText().toString().equalsIgnoreCase("") || txtDateTo.getText().toString().equalsIgnoreCase("")))
		{
			
			
			int dateFrom=Integer.parseInt(validDateFrom);
			int dateTo=Integer.parseInt(validDateTo);
			
			
			int year1=dateFrom%100;
			dateFrom/=100;
			int month1=dateFrom%100;
			
			int year2=dateTo%100;
			dateTo/=100;
			int month2=dateTo%100;
			
			Log.e(""+year2, ""+month2);
			Log.e(""+year1, ""+month1);
			if(year2>=year1)
			{
				
				if(year2==year1 && month2<month1)
				{
					showDialog("Please date to must be greater then date from");
					return false;
				}
			}
			else
			{
				showDialog("Please date to must be greater then date from");
				return false;
			}
			
		
		}
		
		
			return true;
	
		
		
	}
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
		
		alertdialog.setTitle("Zoom-Karaoke");
		alertdialog.setMessage(str);
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
				alertdialog.dismiss();
				
				
			}
		});
		alertdialog.show();
	}
	class MakePayMent extends AsyncTask<String, String, String>
	{

		public void onPreExecute()
		{
			pDialog = new ProgressDialog(mcontext);
			pDialog.setTitle("Processing Payment");
	        pDialog.setMessage("Please wait�");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
					try
					{
						DefaultHttpClient httpClient = new DefaultHttpClient();
				        HttpPost requestLogin = new HttpPost(params[0]);
				         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				         
				         
				         Log.e("amount", ""+priceProduct);
				         Log.e("Description", songName.getText().toString());
				         Log.e("CardType", listCardValue.get(selectedValueCardId));
				         Log.e("CardNumber", cardNumber.getText().toString());
				         Log.e("CardHolderName", cardHolder.getText().toString());
				         Log.e("StartDate", validDateFrom);
				         Log.e("CardExpiryDate", validDateTo);
				         Log.e("CvcNumber", cvvNumber.getText().toString());
				         nameValuePairs.add(new BasicNameValuePair("amount",""+priceProduct));
				         nameValuePairs.add(new BasicNameValuePair("Description", songName.getText().toString()));
				         nameValuePairs.add(new BasicNameValuePair("CardType", listCardValue.get(selectedValueCardId)));
				         nameValuePairs.add(new BasicNameValuePair("CardNumber", cardNumber.getText().toString()));
				         nameValuePairs.add(new BasicNameValuePair("CardHolderName", cardHolder.getText().toString()));
				         nameValuePairs.add(new BasicNameValuePair("StartDate", validDateFrom));
				         nameValuePairs.add(new BasicNameValuePair("CardExpiryDate", validDateTo));
				         nameValuePairs.add(new BasicNameValuePair("CvcNumber", cvvNumber.getText().toString()));
				         nameValuePairs.add(new BasicNameValuePair("Name", userName.getText().toString()));
				         nameValuePairs.add(new BasicNameValuePair("Email",userEmail.getText().toString()));
				   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					     HttpResponse response = httpClient.execute(requestLogin);
					     HttpEntity entity = response.getEntity();
//					     InputStream is= entity.getContent();
					     
					  
					    String xml = EntityUtils.toString(entity);
					    System.out.println(xml);
//					     map=com.karaoke.util.SAXXMLParser.parseCardValidation(is);
			    	        
//			    	       
			    	        
					     Log.e("Map ", ""+map);
					     
					}
					catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
			
			
			
			return null;
		}
		public void onPostExecute(String result)
		{
			pDialog.dismiss();
			if(map.get("Status") !=null)
			{
				if(map.get("Status").equalsIgnoreCase("OK"))
				{
					if(type.equalsIgnoreCase("Song"))
					{
						new DownloadFileFromURL().execute(song.getSongUrl(),songName.getText().toString());
					}
					else
					{
						Log.e(" ",""+ song.getSongUrl()+" /n "+songName.getText().toString());
						new DownloadAlbumFromURL().execute(song.getSongUrl(),songName.getText().toString());
					}
				}
				else
				{
					showDialog("Payment is not done");
				}
			
			}
			else
			{
				showDialog("Payment is not done");
			}
		}
		
	}
	
		
	@SuppressLint("NewApi")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private DatePickerDialog customDatePickerStart() {
		
		
        DatePickerDialog dpd = new DatePickerDialog(this, datePickerListenerStart,
                year, month, day);
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    datePicker.setMaxDate(Calendar.getInstance().getTimeInMillis());
                    datePicker.setMinDate(minCalender.getTimeInMillis());
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                        .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
       
        return dpd;
    }
	 
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	private DatePickerDialog customDatePickerLast() {
        DatePickerDialog dpd = new DatePickerDialog(this, datePickerListenerEnd,
                year, month, day);
        try {
            Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField
                            .get(dpd);
                    
                    datePicker.setMaxDate(maxCalender.getTimeInMillis());
                    
                    datePicker.setMinDate(new Date().getTime()-1000);
                    
                    Field datePickerFields[] = datePickerDialogField.getType()
                            .getDeclaredFields();
                    for (Field datePickerField : datePickerFields) {
                        if ("mDayPicker".equals(datePickerField.getName())
                                || "mDaySpinner".equals(datePickerField
                                        .getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = new Object();
                            dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                    
                }
            }
        } catch (Exception ex) {
        	ex.printStackTrace();
        }
        return dpd;
    }
	public  Bitmap getBitmapFromURL(String src) {
	    try {
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		boolean flag=true;
		 File outputFile;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mcontext);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Downloading file in background thread
		 * */
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("SdCardPath")
		@Override
		protected String doInBackground(String... f_url) {
			int count;
	        try {
	            URL url = new URL(f_url[0]);
	            
	            String filename=f_url[1];
	            
	            String extension = MimeTypeMap.getFileExtensionFromUrl(f_url[0]);
	            filename=filename+"."+extension;
	            System.out.println(extension);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	            int lenghtOfFile = conection.getContentLength();
	            int max=(lenghtOfFile/1000);
	            pDialog.setMax(max);
	            pDialog.setProgressNumberFormat("%1d/%2d kB");
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	            File songDirectory = new File("/sdcard/ZoomKaraoke/Songs/");
	         // have the object build the directory structure, if needed.
	            songDirectory.mkdirs();
	         // create a File object for the output file
	         outputFile = new File(songDirectory, filename);
	         // now attach the OutputStream to the file object, instead of a String representation
//	         FileOutputStream fos = new FileOutputStream(outputFile);
	            
	            
	            // Output stream to write file
	            OutputStream output = new FileOutputStream(outputFile);

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
//	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	                publishProgress(""+(int)(total/1000));
	                // writing data to file
	                output.write(data, 0, count);
	            }

	            // flushing output
	            output.flush();
	            
	            // closing streams
	            output.close();
	            input.close();
	            
	            
	            
	            
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	flag=false;
	        }
	        
	        return null;
		}
		
		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
       }

		/**
		 * After completing background task
		 * Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			
			ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
			String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
			datbase.openDataBase();
			datbase.addSongs(song, outputFile.getAbsolutePath(), currentDateTimeString,"Song");
			song.setSDcardPath(outputFile.getAbsolutePath());
			new CheckTransation(song.getSongId(),"123").execute();
			
			
		}

	}
	class CheckTransation extends AsyncTask<String, String, String> {

		boolean flag=true;
		 File outputFile;
		 int songId;
		 String transationId;
		 
		 int sucess=1;
		 public CheckTransation(int songid,String transation_Id)
		 {
			
			 songId=songid;
			 transationId=transation_Id;
			 
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mcontext);
			pDialog.setMessage("Transation start. Please wait...");
			pDialog.setIndeterminate(false);
			
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Downloading file in background thread
		 * */
		@SuppressLint("SdCardPath")
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			InputStream is;
		
			
	        try {
	        	
	        	
	        	DefaultHttpClient httpClient = new DefaultHttpClient();
		        HttpPost requestLogin = new HttpPost(transationUrl);
		        TelephonyManager tManager = (TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE);
		        String uuid = tManager.getDeviceId();
		         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		         nameValuePairs.add(new BasicNameValuePair("video_ID",""+ songId));
		         nameValuePairs.add(new BasicNameValuePair("user_UDID", uuid));
		         nameValuePairs.add(new BasicNameValuePair("Status", map.get("Status")));
		         nameValuePairs.add(new BasicNameValuePair("StatusDetail", map.get("StatusDetail")));
		         nameValuePairs.add(new BasicNameValuePair("VPSTxId", map.get("VPSTxId")));
		         nameValuePairs.add(new BasicNameValuePair("SecurityKey", map.get("SecurityKey")));
		         nameValuePairs.add(new BasicNameValuePair("VPSTxId", map.get("VPSTxId")));
		         nameValuePairs.add(new BasicNameValuePair("TxAuthNo", map.get("TxAuthNo")));
		         nameValuePairs.add(new BasicNameValuePair("AVSCV2", map.get("AVSCV2")));
		         nameValuePairs.add(new BasicNameValuePair("AddressResult", map.get("AddressResult")));
		         nameValuePairs.add(new BasicNameValuePair("PostCodeResult", map.get("PostCodeResult")));
		         nameValuePairs.add(new BasicNameValuePair("CV2Result", map.get("CV2Result")));
		         nameValuePairs.add(new BasicNameValuePair("SecureStatus", map.get("SecureStatus")));
		         nameValuePairs.add(new BasicNameValuePair("FraudResponse", map.get("FraudResponse")));
		         nameValuePairs.add(new BasicNameValuePair("ExpiryDate", map.get("ExpiryDate")));
		         nameValuePairs.add(new BasicNameValuePair("BankAuthCode", map.get("BankAuthCode")));
		         nameValuePairs.add(new BasicNameValuePair("DeclineCode", map.get("DeclineCode")));
		         nameValuePairs.add(new BasicNameValuePair("transaction_date", "abc"));
		        
		   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     HttpResponse response = httpClient.execute(requestLogin);
			     HttpEntity entity = response.getEntity();
			     
//			     String content = EntityUtils.toString(entity);
//			     
//			     Log.e("Is", ""+content);
			     
			     is= entity.getContent();
	            
			     
			     
			     
			    
			   HashMap<String, String> map1= SAXXMLParser.parseTransation(is);
			   Log.e("Map1",""+map1);
	            sucess=Integer.parseInt(map1.get("Result"));
	            Log.e("Map1",""+map1);
	            
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	flag=false;
	        }
	        
	        return null;
		}
		
		
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			
			System.out.println(AvailableSong.listsong.remove(song));
			
			if(sucess==0)
			{
				
				if(type.equalsIgnoreCase("Song"))
				{
					
					edit.putString("song", "Song");
					edit.putInt("songId", position);
					edit.commit();
					final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
					
					alertdialog.setTitle("Download complete");
					alertdialog.setMessage("Do you want to play this song ?");
					alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.dismiss();
							Intent in=new Intent(mcontext,PlayScreen.class);
							
							Log.e("Path",song.getSDcardPath());
							ArrayList<Song> songlist = new ArrayList<Song>();
							songlist.add(song);
							
							in.putExtra("singlesong", "singlesong");
							in.putExtra("ob", song);
							in.putExtra("count", 1);
							in.putExtra("songlist", songlist);
							((Activity)mcontext).finish();
							mcontext.startActivity(in);
							
						}
					});
					alertdialog.setButton("No", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.dismiss();
							finish();
						}
					});
					alertdialog.show();
				}
				else
				{
					
					edit.putString("song", "Song");
					edit.putInt("albumId", position);
					edit.commit();
					final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
					
					alertdialog.setTitle("Download complete");
					alertdialog.setMessage("Do you want to open this album");
					alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.dismiss();
							
							ZoomKaraokeDatabaseHandler handle=new ZoomKaraokeDatabaseHandler(mcontext);
							int id=handle.getMaxAlbumId();
							
							Intent in=new Intent(mcontext,AlbumListSong.class);
							in.putExtra("AlbumId", id);
							in.putExtra("albumname",songName.getText().toString());
							finish();
							mcontext.startActivity(in);
							
							
							
						}
					});
					alertdialog.setButton("No", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.dismiss();
							finish();
						}
					});
					alertdialog.show();
				}
			}
			else
			{
				Toast.makeText(mcontext, "Your transation is not complete", 1000).show();
			}
		}

	}
	class DownloadAlbumFromURL extends AsyncTask<String, String, String> {

		File songDirectory;
		File albumDir;
		  File outputFile;
		  String result;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mcontext);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Downloading file in background thread
		 * */
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("SdCardPath")
		@Override
		protected String doInBackground(String... f_url) {
			int count = 0;
	        try {
	            URL url = new URL(f_url[0]);
	            
	            String filename=f_url[1];
	            
	            Log.e("url ", f_url[0]);
	            Log.e("name  ", f_url[1]);
	            
	            String extension = MimeTypeMap.getFileExtensionFromUrl(f_url[0]);
	            filename=filename+"."+extension;
	            System.out.println(extension);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	           
	            int lenghtOfFile = conection.getContentLength();
	            int max=(lenghtOfFile/1000);
	        	pDialog.setMax(max);
	        	 pDialog.setProgressNumberFormat("%1d/%2d kB");
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	           songDirectory = new File("/sdcard/ZoomKaraoke/ZipFolder/");
	         // have the object build the directory structure, if needed.
	            songDirectory.mkdirs();
	           albumsDirectory="/sdcard/ZoomKaraoke/Albums/";
	            
	          
	           
	         // create a File object for the output file
	            outputFile = new File(songDirectory, filename);
	         // now attach the OutputStream to the file object, instead of a String representation
//	         FileOutputStream fos = new FileOutputStream(outputFile);
	            
//	         ZipInputStream zin = new ZipInputStream(input);
	            // Output stream to write file
	            OutputStream output = new FileOutputStream(outputFile);

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
//	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	                publishProgress(""+(int)(total/1000));
	                // writing data to file
	                output.write(data, 0, count);
	            }
	            
	           
	            output.flush();
	            // closing streams
	            output.close();
	            input.close();
	           
	            
	           
	        } catch (Exception e) {
	        	Log.e("Error", "Error ");
	        	e.printStackTrace();
	        	result="false";
	        }
	        result="true";
	        return null;
		}
		
		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
           
       }

		/**
		 * After completing background task
		 * Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			
			if(result.equalsIgnoreCase("true"))
			  {
			   try
			   {
				   unzip(outputFile.getAbsolutePath(),albumsDirectory,song);
//				   Decompress d = new Decompress(outputFile.getAbsolutePath(), songDirectory.getAbsolutePath()); 
//		           System.out.println("compressing");
//				   d.unzip(); 
			   } catch (Exception e)
			   {
//			    // TODO Auto-generated catch block
			    e.printStackTrace();
			   }
//			
//			
			}
		}

	}
	public void unzip(String str,String str1,Song album) throws IOException 
	{
	 pDialog = new ProgressDialog(mcontext);
	 pDialog.setMessage("Please Wait...");
	 pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 pDialog.setCancelable(false);
	 pDialog.show();
	 String StorezipFileLocation=str;
	 String DirectoryName=str1;
	 new UnZipTask().execute(StorezipFileLocation, DirectoryName);
	}


	private class UnZipTask extends AsyncTask<String, Void, Boolean> 
	{
	 @SuppressWarnings("rawtypes")
	 @Override
	 protected Boolean doInBackground(String... params) 
	 {
//	  String filePath = params[0];
//	  String destinationPath = params[1];
//
//	  File archive = new File(filePath);
	  try 
	  {
//	   ZipFile zipfile = new ZipFile(archive);
//	   for (Enumeration e = zipfile.entries(); e.hasMoreElements();) 
//	   {
//	    ZipEntry entry = (ZipEntry) e.nextElement();
//	    unzipEntry(zipfile, entry, destinationPath);
//	   }


		  System.out.println("Album url "+params[1]);
		  
		  
		 
	   UnzipUtil d = new UnzipUtil(params[0], params[1],song,mcontext); 
	   d.unzip();

	  } 
	  catch (Exception e) 
	  {
	   return false;
	  }

	  return true;
	 }

	 @SuppressLint("SdCardPath")
	@Override
	 protected void onPostExecute(Boolean result) 
	 {
	  pDialog.dismiss(); 
	  
	  
	  
	  File file=new File("/sdcard/ZoomKaraoke/ZipFolder");
	  if (file.isDirectory()) {
	        String[] children = file.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(file, children[i]).delete();
	        }
	        file.delete();
	    }
	  new CheckTransation(song.getAlbumId(),"123").execute();
	  
	 }}
	
}
