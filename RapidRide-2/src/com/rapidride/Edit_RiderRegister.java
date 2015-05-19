package com.rapidride;

import java.io.ByteArrayOutputStream;
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
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.rapidride.util.ImageDownloader;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class Edit_RiderRegister extends Activity {

	
	static SharedPreferences prefs;
	Typeface typeFace;
	
	EditText ed_userfistname,ed_userlastname,ed_password,ed_confirm_password,ed_contactno,ed_specialnotes,
	ed_ccnumber,ed_promo,ed_address,ed_city,ed_state,ed_zip,ed_cc_zipcode,ed_cc_expdate,ed_countrye,ed_flightno;
	Button btn_submit,btn_datepicker,btn_editregister,btn_editdisable;
	TextView tv_registrationtitle,tv_apptitle,tv_address,tv_getuseremail,tv_lbl_my_edit_profile;
	ImageView imageview_uploadpic;
	ProgressDialog progress;
	Spinner ed_country;
	TextView tv_edit_my_profile;
	RadioButton btn_handiyes,btn_handino,rbtn_animalyes,rbtn_animalno;
	RadioGroup radioGrouphandicap,radiogroupanimal ;
	Bitmap btmp_camera,btmp_gallery;
	int TAKE_PHOTO_CODE = 0;
	static String str_base64="";
	int count=0,exception=0;
	//private final int PICTURE_TAKEN_FROM_GALLERY = 2;
	public String jsonResult;
	public String jsonMessage;
	String str_handi="";
	String str_animal="";
	String item_country="",zipcode;
	String[] countries_array;
	int int_country ;
	//String[] countrieCodes = r.obtainTypedArray(R.array.countries_array);
	
	 protected void onCreate(Bundle savedInstanceState) {
		 requestWindowFeature(Window.FEATURE_NO_TITLE);
	      super.onCreate(savedInstanceState);
	        setContentView(R.layout.edit_register_rider);
	      
	        
	      countries_array=Edit_RiderRegister.this.getResources().getStringArray(R.array.countries_array);
	   	  zipcode="+"+GetCountryZipCode();
	   	  Log.d("tag", "zipcode:"+zipcode);
	     
	   	  prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE); 
	      typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
	      
	      /******edit text********////
	      ed_userfistname=(EditText)findViewById(R.id.editText_userfirstname);
	      ed_userlastname=(EditText)findViewById(R.id.editText_userlastname);
	      ed_password=(EditText)findViewById(R.id.editText_password);
	      ed_confirm_password=(EditText)findViewById(R.id.editText_confirmpassword);
	      ed_contactno=(EditText)findViewById(R.id.editText_contactnumber);
	      ed_specialnotes=(EditText)findViewById(R.id.editText_specialneednotes);
	      /*****address edit text*****////
	      ed_address=(EditText)findViewById(R.id.editText_address);
	      ed_city=(EditText)findViewById(R.id.editText_city);
	      ed_state=(EditText)findViewById(R.id.editText_state);
	      ed_country=(Spinner)findViewById(R.id.editText_country);
	      ed_countrye=(EditText)findViewById(R.id.editText_countrye);
	      ed_zip=(EditText)findViewById(R.id.editText_zip);
	      ed_flightno=(EditText)findViewById(R.id.editText_flateno);
	     
	      /*********button*********////
	      btn_submit=(Button)findViewById(R.id.button_submit);
	      imageview_uploadpic=(ImageView)findViewById(R.id.imageView_profilepic);
	     
	      /******text view************////
	      tv_apptitle=(TextView)findViewById(R.id.textView_editpro_title);
	      tv_address=(TextView)findViewById(R.id.textView_lebelAddress);
	      tv_getuseremail=(TextView)findViewById(R.id.textView_get_useremail);
	     
	      btn_handiyes=(RadioButton)findViewById(R.id.radioButton_yes);
	      btn_handino=(RadioButton)findViewById(R.id.radioButton_no);
	      radioGrouphandicap = (RadioGroup) findViewById(R.id.radio_handicap);
	      radiogroupanimal = (RadioGroup) findViewById(R.id.radio_animal);
	      rbtn_animalyes=(RadioButton)findViewById(R.id.radioButton_animalyes);
	      rbtn_animalno=(RadioButton)findViewById(R.id.radioButton_animalno);
	      
	      /*********** set value to textview *************////
	      tv_getuseremail.setText("Welcome: "+prefs.getString("userfirstname", null));//+" "+prefs.getString("userlastname", null));
	      ed_userfistname.setText(prefs.getString("userfirstname", null));
	      ed_userlastname.setText(prefs.getString("userlastname", null));
	     
	      String ph=prefs.getString("phone", null);
	      if(ph.length()>10){
	      
	      String newph=ph.substring(ph.length()-10,ph.length());
	      System.err.println("phone=="+newph);
	      ed_contactno.setText(newph);
	      }
	      else
	      {
	    	  ed_contactno.setText(ph); 
	      	}
	      
	      
	      ed_password.setText(prefs.getString("password", null));
	      ed_confirm_password.setText(prefs.getString("password", null));
	      ed_flightno.setText(prefs.getString("flightno", null));
	  //    ed_country.setText(prefs.getString("country", null));
	      ed_state.setText(prefs.getString("state", null));
	      ed_city.setText(prefs.getString("city", null));
	      ed_zip.setText(prefs.getString("zip", null));
	      ed_address.setText(prefs.getString("address", null));
	      
	      System.err.println("neddddddddddd="+prefs.getString("specialneed", null));
	      ed_specialnotes.setText(prefs.getString("specialneed", null));
	      String imageURL= prefs.getString("picurl", null);
	      System.err.println("imageurl   "+imageURL);
	      
	   	InputFilter letterOrDigit = new InputFilter() { 
	        public CharSequence filter(CharSequence source, int start, int end, 
	        		Spanned dest, int dstart, int dend) { 
	                for (int i = start; i < end; i++) { 
	                        if (!Character.isLetterOrDigit(source.charAt(i))) { 
	                                return ""; 
	                        } 
	                } 
	                return null; 
	        } 
		};
		ed_userfistname.setFilters(new InputFilter[]{letterOrDigit});
		ed_userlastname.setFilters(new InputFilter[]{letterOrDigit});
		ed_city.setFilters(new InputFilter[]{letterOrDigit});
	//	ed_country.setFilters(new InputFilter[]{letterOrDigit});
	   
	      
	 
	     
		/***Get image*****/
		if(Utility.isConnectingToInternet(Edit_RiderRegister.this))
    	{
		
			 new GetXMLTask().execute(new String[] {imageURL});
    		}
    	else{
    			Utility.alertMessage(Edit_RiderRegister.this,"error in internet connection");
    			}
	     
	     
	      imageview_uploadpic.setClickable(false);
	          
	      
	      //set font 
	      
	     // tv_registrationtitle.setTypeface(typeFace);
	      tv_apptitle.setTypeface(typeFace);
	      tv_address.setTypeface(typeFace);
	      ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(Edit_RiderRegister.this, android.R.layout.simple_spinner_item,countries_array);
	      ed_country.setAdapter(dataAdapter);
	      dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	   
	      
	      
	      if(prefs.getString("country", "").equals(""))
	     {
//	    	// ed_country.setSelection(227);
	    	 item_country="United States";
	    	 ed_countrye.setVisibility(View.VISIBLE);
	    	 ed_country.setVisibility(View.GONE);
	    	 ed_countrye.setText("United States");
	    	  
	    	//  String country=prefs.getString("country", null);
	      
//	      for(int i=0;i<countries_array.length;)
//	      {
//	    	 country.equals(countries_array[i]); 
//	    	 Editor ed=prefs.edit();
//             ed.putInt("intposition", i);
//             ed.commit();
//             i++;
//	    	 break;
//	      }
	      
     		}
	     else
	     {
	    	 ed_countrye.setVisibility(View.VISIBLE);
	    	 ed_country.setVisibility(View.GONE);
	    	// ed_country.setSelection(prefs.getInt("intposition", 227));  
	    	ed_countrye.setText(prefs.getString("country", null)); 
	    	 
	     }
	     ed_countrye.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 ed_countrye.setVisibility(View.GONE);
			     ed_country.setVisibility(View.VISIBLE);
			     Utility.hideKeyboard(Edit_RiderRegister.this);

			}
		});
	    
	     
	  
	      ed_country.setOnItemSelectedListener(new OnItemSelectedListener() {
	        public void onItemSelected(AdapterView<?> adapter, View v,
	                    int position, long id) {
	                // On selecting a spinner item
	                item_country = adapter.getItemAtPosition(position).toString();
	                int_country =position;
	                
	                Editor ed=prefs.edit();
	                ed.putInt("intposition", position);
	                ed.commit();
	                //ed_countrye.setText(item_country);
	               // ed_countrye.setVisibility(View.VISIBLE);
				  //  ed_country.setVisibility(View.GONE);
	              //  Toast.makeText(Edit_RiderRegister.this,"Selected Country : " + item_country, Toast.LENGTH_LONG).show();
	              
	            }
	         public void onNothingSelected(AdapterView<?> arg0) {
	                // TODO Auto-generated method stub
	 
	            }
	        });
	      tv_edit_my_profile=(TextView)findViewById(R.id.textView_lbl_my_edit_profile);
	      tv_edit_my_profile.setTypeface(typeFace);
	      btn_editdisable=(Button)findViewById(R.id.button_editdisable);
	      
	     
//	     if( btn_handiyes.isChecked())
//	    	 {
//	    	 str_handi="1";
//	    	 
//	    	 		}
//	     if( btn_handino.isChecked())
//	     {
//	    	 str_handi="0";
//	     		}
//			
//	     if(rbtn_animalyes.isChecked())
//    	 {
//    	 str_animal="1";
//    	 
//    	 		}
//	     if(rbtn_animalno.isChecked())
//	     {
//	    	 str_animal="0";
//	     		}
		
	 
	      radioGrouphandicap.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                switch(checkedId){
	                    case R.id.radioButton_yes:
	                        // do operations specific to this selection
	                    	str_handi="1";
	                    break;

	                    case R.id.radioButton_no:
	                    	str_handi="0";
	                    break;

	                }

	                }});
	   
	      radiogroupanimal.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                switch(checkedId){
	                    case R.id.radioButton_animalyes:
	                        // do operations specific to this selection
	                    	str_animal="1";
	                    break;

	                    case R.id.radioButton_animalno:
	                    	str_animal="0";
	                    break;

	                }

	                }});

	      if(prefs.getString("handicap", null).equals("0"))
	    	{
	    	  btn_handino.setChecked(true);
	    		  }
	      else if(prefs.getString("handicap", null).equals("1"))
		      {
	    	  btn_handiyes.setChecked(true);
		      		}
	      else
	      {
	    	  btn_handino.setChecked(true);
	      }
	      
	      if(prefs.getString("animal", null).equals("0"))
	    	{
	    	  rbtn_animalno.setChecked(true);
	    		  }
	      else  if(prefs.getString("animal", null).equals("1"))
	      {
	    	  rbtn_animalyes.setChecked(true);
	      	}
	      else
		      {
	    	  rbtn_animalno.setChecked(true);
		      		}
	    /*****disable value*/
	     disableData();
	      
	      btn_editdisable.setOnClickListener(new OnClickListener() {
	    	  public void onClick(View v) {
				count++;
				if(count==1)
				{
					enableData(); 
						}
				if(count==2)
				{
					count=0;
					disableData(); 
				}
			}
		});
	      
	     
	      
	      btn_submit.setTypeface(typeFace);
	      btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {//checking empty fields
					
				 if(ed_userfistname.getText().toString().equals("")||ed_userlastname.getText().toString().equals("")
					||ed_password.getText().toString().equals("")||ed_confirm_password.getText().toString().equals("")
					||ed_contactno.getText().toString().equals("")||ed_address.getText().toString().equals("")
					||ed_city.getText().toString().equals("")||ed_state.getText().toString().equals("")
					||ed_zip.getText().toString().equals("")||ed_specialnotes.getText().toString().equals(""))
				//	||ed_country.getText().toString().equals("")) editText_flateno
				   	{
					 	Utility.alertMessage(Edit_RiderRegister.this,"Please fill Details");//||ed_cc_expdate.getText().toString().equals("")
			      		}
			      
			      else if(!(ed_confirm_password.getText().toString().equals(ed_password.getText().toString())))//match password and confirm password
			      	{
			    	  Utility.alertMessage(Edit_RiderRegister.this,"Password and Confirm_password are not same");
			      		}
			      else if(Utility.isConnectingToInternet(Edit_RiderRegister.this))
						{
			    		  	new Edit_RegistrationParsing().execute();// web services class
							}
			      else if(!(ed_contactno.getText().toString().length()==10))//match password and confirm password
			      	{
			    	  Utility.alertMessage(Edit_RiderRegister.this,"Invalid phone number");
			      		}
			      else{
							Utility.alertMessage(Edit_RiderRegister.this,"error in internet connection");
							}
			    	 
					
				}
	      });

	      imageview_uploadpic.setOnClickListener(new OnClickListener() {   // picture upload by camera
	    	  public void onClick(View v) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(Edit_RiderRegister.this);
				alertDialog.setTitle("Open Camera");
			 // alertDialog.setMessage(");
			     alertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
			               public void onClick(DialogInterface dialog,int which) {
			                 
			                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
				                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE); 
				            	 }});
			     alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
		               public void onClick(DialogInterface dialog,int which) {
		               
		           
			            	 }});
			   			   alertDialog.show();
			   			 //  alertDialog.setNegativeButton(text, listener)
					}});
	    
	      
	      
	  }
	 
	 
	 
	 /*********************************main function end*******************************/
	  
	  
	  public void disableData()
	  {
		  
		  ed_userfistname.setEnabled(false);
	      ed_userlastname.setEnabled(false);
	      ed_contactno.setEnabled(false);
	      ed_password.setEnabled(false);
	      ed_confirm_password.setEnabled(false);
	      ed_flightno.setEnabled(false);
	      ed_country.setEnabled(false);
	      ed_state.setEnabled(false);
	      ed_city.setEnabled(false);
	      ed_zip.setEnabled(false);
	      ed_address.setEnabled(false); 
	      btn_submit.setEnabled(false);
	      btn_handiyes.setEnabled(false);
	      btn_handino.setEnabled(false);
	      rbtn_animalyes.setEnabled(false);
	      rbtn_animalno.setEnabled(false);
	      ed_specialnotes.setEnabled(false);
	      ed_countrye.setEnabled(false);
	      imageview_uploadpic.setClickable(false);
	      btn_submit.setVisibility(View.GONE);
	      btn_editdisable.setText("EDIT");
	      tv_edit_my_profile.setText("MY PROFILE");
//	  	 ed_countrye.setVisibility(View.VISIBLE);
//	     ed_country.setVisibility(View.GONE);
	  }
	  public void enableData()
	  {
		  ed_userfistname.setEnabled(true);
	      ed_userlastname.setEnabled(true);
	      ed_contactno.setEnabled(true);
	      ed_password.setEnabled(true);
	      ed_confirm_password.setEnabled(true);
	      ed_flightno.setEnabled(true);
	      ed_specialnotes.setEnabled(true);
	      ed_country.setEnabled(true);
	      ed_state.setEnabled(true);
	      ed_city.setEnabled(true);
	      ed_zip.setEnabled(true);
	      ed_address.setEnabled(true);
	      imageview_uploadpic.setClickable(true);
	      btn_submit.setEnabled(true);
	      btn_handiyes.setEnabled(true);
		  btn_handino.setEnabled(true);
	      rbtn_animalyes.setEnabled(true);
	      rbtn_animalno.setEnabled(true);
	      btn_submit.setVisibility(View.VISIBLE);
	      tv_edit_my_profile.setText("EDIT PROFILE");
	      btn_editdisable.setText("CANCEL"); 
	      ed_countrye.setEnabled(true);
//	  	 ed_countrye.setVisibility(View.GONE);
//	     ed_country.setVisibility(View.VISIBLE);
	  }
	  
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		    super.onActivityResult(requestCode, resultCode, data);

		    if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
		        Log.d("CameraDemo", "Pic saved");
		        btmp_camera = (Bitmap) data.getExtras().get("data"); 
		        imageview_uploadpic.setImageBitmap(btmp_camera);
		   	
		        ByteArrayOutputStream bao = new ByteArrayOutputStream(); //convert images into base64
		        btmp_camera.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		        byte[] ba = bao.toByteArray();
		        str_base64 = Base64.encodeToString(ba, Base64.DEFAULT);
		        Log.e("base64:", str_base64);     
		    }}
/**edit Registration parsing class */
class Edit_RegistrationParsing extends AsyncTask<Void, Void, Void> {
		protected void onPreExecute() {
				super.onPreExecute();
				// Showing progress dialog
				progress = new ProgressDialog(Edit_RiderRegister.this);
				//progress.setTitle("Loading");
				progress.setMessage("Please wait...");
				progress.setCancelable(false);
				progress.show();
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
			progress.dismiss();
			if(!(jsonResult==null))
			{
			
				if(jsonResult.equals("0")){
				//Toast.makeText(Edit_RiderRegister.this, jsonMessage, Toast.LENGTH_LONG).show();
				
				
				Editor editor =prefs.edit();
				//editor.putString("useremail", edittext_username.getText().toString());
				//editor.putString("userid", userid);
				editor.putString("userfirstname", ed_userfistname.getText().toString());
				editor.putString("userlastname", ed_userlastname.getText().toString());
//				 String ph=prefs.getString("phone", null);
//			      String newph=ph.substring(10, ph.length());
//			      ed_contactno.setText(newph);
				editor.putString("flightno",ed_flightno.getText().toString());
				editor.putString("phone",ed_contactno.getText().toString());
				editor.putString("password",ed_password.getText().toString());
				editor.putString("apt", "apt");
				editor.putString("specialneed", ed_specialnotes.getText().toString());
				//editor.putString("flightno", ed_flightno.getText().toString());
				//editor.putString("country",ed_country.getText().toString());
				editor.putString("country",item_country);
				editor.putString("state",ed_state.getText().toString());
				editor.putString("city", ed_city.getText().toString());
				editor.putString("zip", ed_zip.getText().toString());
				editor.putString("address", ed_address.getText().toString());
				editor.putString("completeaddress", ed_address.getText().toString()+","+ed_city.getText().toString()+","+ed_state.getText().toString()+","+item_country);
				editor.putString("profilepic", str_base64);
				editor.commit();
				
				
				disableData(); /**Disable value***/
				}
			
			else
			{
				Toast.makeText(Edit_RiderRegister.this, jsonMessage, Toast.LENGTH_LONG).show();
				}
			}
		}
		
		//edit Registration parsing function
		public void parsing() throws JSONException {
		try {
				HttpParams httpParameters = new BasicHttpParams();
				int timeoutConnection = 60000;
				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				int timeoutSocket = 61000;
				HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				HttpClient client = new DefaultHttpClient(httpParameters);
				HttpPost httpost = new HttpPost(Url_Address.url_Home+"/EditProfile");
				JSONObject json = new JSONObject();
				
			
				 json.put("Trigger", "EditProfile");
			      		
				 		json.put("UserId", prefs.getString("userid", null));
			            System.err.println("Userid"+prefs.getString("userid", null));
			            
			            json.put("FirstName", ed_userfistname.getText().toString());
			            json.put("LastName",ed_userlastname.getText().toString());
			           // json.put("UserEmail", bean_rider_registration.getEmail());
			            json.put("Password",ed_password.getText().toString());
			            json.put("Phone", zipcode+ed_contactno.getText().toString());
			            json.put("FlightNumber",ed_flightno.getText().toString());
			            System.err.println("PHone with code="+zipcode+ed_contactno.getText().toString());
			            json.put("Apt","apt");
			            json.put("Specialneedsnotes",ed_specialnotes.getText().toString());
			            json.put("Address",ed_address.getText().toString());
			    //        json.put("Country",ed_country.getText().toString());
			            json.put("Country",item_country);
			            json.put("City", ed_city.getText().toString());
			            json.put("State",ed_state.getText().toString());
			            json.put("Zip", ed_zip.getText().toString());
			            json.put("ProfilePhoto", str_base64);
			            json.put("Handicap",str_handi);
			            System.err.println("Handicap="+str_handi);
			            json.put("Animal",str_animal);
			            System.err.println("str_animal="+str_animal);
			          
			            Log.e("tag", str_base64);
			       
				      Log.e("sending data", json.toString());
				      
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
				   jsonResult=obj.getString("result");
				jsonMessage=obj.getString("message");
				
				Log.e("tag:", "Result: "+jsonResult);
				Log.e("tag:", "Message :"+jsonMessage);
				}
				  catch(Exception e){
				   System.out.println(e);
				   Log.d("tag", "Error :"+e);
				   exception=1;
				 
				   }
				}
}
		//images download 
		private class GetXMLTask extends AsyncTask<String, Void, Bitmap> {
		        @Override
		    protected Bitmap doInBackground(String... urls) {
		            Bitmap map = null;
		            for (String url : urls) {
		                map = ImageDownloader.downloadImage(url);
		            }
		            return map;
		        }
	
		    protected void onPostExecute(Bitmap result) {
		    	if(result!=null)
		    	{
		        	imageview_uploadpic.setImageBitmap(Bitmap.createScaledBitmap(result, 80, 80, false));
		        	imageview_uploadpic.setClickable(false);
		    		}
		        }
		}
		
		public String GetCountryZipCode(){
		    String CountryID="";
		    String CountryZipCode="";

		    TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		    //getNetworkCountryIso
		    CountryID= manager.getNetworkCountryIso().toUpperCase();
		    String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
		    for(int i=0;i<rl.length;i++){
		        String[] g=rl[i].split(",");
		        if(g[1].trim().equals(CountryID.trim())){
		            CountryZipCode=g[0];
		            break;  
		        }
		    }
		    return CountryZipCode;
		}
		public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
		}
}