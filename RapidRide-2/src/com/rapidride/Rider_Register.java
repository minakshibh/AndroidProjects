package com.rapidride;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Paint;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gcm.GCMRegistrar;
import com.rapidride.util.Bean_RiderRegisteration;
import com.rapidride.util.Url_Address;
import com.rapidride.util.Utility;


public class Rider_Register extends Activity {

	EditText ed_userfistname,ed_userlastname,ed_email,ed_password,ed_confirm_password,ed_contactno;
	Button btn_register;
	ImageView imageview_uploadpic;
	Bitmap btmp_camera,btmp_gallery;
	private ProgressDialog progress;
	int TAKE_PHOTO_CODE = 0;
	Typeface typeFace;
	TextView tv_loginhere,tv_already,tv_title,tv_registertitle;
	int images=0;
	static String jsonResult="";
	static String str_base64="",register_jsonResult="",register_jsonMessage="";;
	static String jsonMessage="";
	Bean_RiderRegisteration bean_rider_registration;
	ProgressDialog progressbar1;
	//String userid="";
	static String riderid=null;
	static String driverid=null;
	static String promocode=null,zipcode;
	static int int_value;
	String  uuid="",regId="";
	static SharedPreferences prefs;
	RegisterTask mRegisterTask;
	
	  protected void onCreate(Bundle savedInstanceState) {
		  requestWindowFeature(Window.FEATURE_NO_TITLE);
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.rider_register);
	        
	       
	        
			checkNotNull(Utility.SERVER_URL, "SERVER_URL");
	        checkNotNull(Utility.SENDER_ID, "SENDER_ID");
	    	//------------------------------------
	        // Make sure the device has the proper dependencies.
	        GCMRegistrar.checkDevice(Rider_Register.this);
	        
	    	prefs = this.getSharedPreferences("RapidRide", MODE_PRIVATE);
	        //edit text//
		      ed_userfistname=(EditText)findViewById(R.id.editText_reg_userfirstname);
		      ed_userlastname=(EditText)findViewById(R.id.editText_reg_userlastname);
		      ed_email=(EditText)findViewById(R.id.editText_reg_email);
		      ed_password=(EditText)findViewById(R.id.editText_reg_password);
		      ed_confirm_password=(EditText)findViewById(R.id.editText_reg_confirmpassword);
		      ed_contactno=(EditText)findViewById(R.id.editText_reg_contactnumber);
		      //button
		      btn_register=(Button)findViewById(R.id.button_reg_register);
		      //set font 
		      imageview_uploadpic=(ImageView)findViewById(R.id.imageView_reg_profilepic);
		      typeFace=Typeface.createFromAsset(getAssets(),"MYRIADPROREGULAR.ttf");
		      tv_already=(TextView)findViewById(R.id.textView_alreay_haveRegis);
		      tv_loginhere=(TextView)findViewById(R.id.textView_loginhere);
		      tv_title=(TextView)findViewById(R.id.textView_register_title);
		      tv_registertitle=(TextView)findViewById(R.id.textView_lebelriderres);
		      tv_loginhere.setPaintFlags(tv_loginhere.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);//under line text
		      tv_loginhere.setTypeface(typeFace);
		      tv_registertitle.setTypeface(typeFace);
		      tv_title.setTypeface(typeFace);
		      tv_already.setTypeface(typeFace);
		      btn_register.setTypeface(typeFace);
		    		      
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
		  
		      btn_register.setOnClickListener(new OnClickListener() {
				
				@Override 
				public void onClick(View v) {//checking empty fields
						
					 if(ed_userfistname.getText().toString().equals("")||ed_userlastname.getText().toString().equals("")
						||ed_email.getText().toString().equals("")||ed_password.getText().toString().equals("")
						||ed_confirm_password.getText().toString().equals("")
						||ed_contactno.getText().toString().equals(""))
					 { 
						
					   	
						 	Utility.alertMessage(Rider_Register.this,"Please enter details");//||ed_cc_expdate.getText().toString().equals("")
				      		}
					 else if (isValidEmail(ed_email.getText().toString()) == false) //email validation
				      	{
						 Utility.alertMessage(Rider_Register.this,"Invalid email address") ;
				      		}
					 else if(!(ed_contactno.getText().toString().length()==10))//match password and confirm password
				      	{
				    	  Utility.alertMessage(Rider_Register.this,"Invalid phone number");
				      		}
				      else if(!(ed_confirm_password.getText().toString().equals(ed_password.getText().toString())))//match password and confirm password
				      	{
				    	  Utility.alertMessage(Rider_Register.this,"Password doesnot match");
				      		}
				      else if(str_base64.equals(""))
				      {
				    	  Utility.alertMessage(Rider_Register.this,"Upload profile picture"); 
				    	  
				      }
					  else
					 {
							zipcode="+"+GetCountryZipCode();
							Log.d("tag", "zipcode:"+zipcode);
						  new HttpAsyncTask().execute(Url_Address.url_Home);// home web services link
				    	 // Toast.makeText(getApplicationContext(), "Submit Successfully", Toast.LENGTH_SHORT).show();
						 Utility.hideKeyboard(Rider_Register.this);	
					 }
					}
		      });
		      tv_loginhere.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i=new Intent(Rider_Register.this,Login_Activity.class);
					finish();
					startActivity(i);
					}
		      });
		      imageview_uploadpic.setOnClickListener(new OnClickListener() {   // picture upload by camera
					public void onClick(View v) {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(Rider_Register.this);
						alertDialog.setTitle("Open Camera");
					 // alertDialog.setMessage(");
					     alertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
					               public void onClick(DialogInterface dialog,int which) {
					                   // Write your code here to execute after dialog
					                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
						                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE); 
						            	 }});
					     alertDialog.setNegativeButton("No",new DialogInterface.OnClickListener() {
				               public void onClick(DialogInterface dialog,int which) {
				                   // Write your code here to execute after dialog
				           
					            	 }});
					   			   alertDialog.show();
					   			 //  alertDialog.setNegativeButton(text, listener)
							}});
//					    alertDialog.setNegativeButton("Gallery",new DialogInterface.OnClickListener() {
//					               public void onClick(DialogInterface dialog,int which) {
//					                   // Write your code here to execute after dialog
//					               	Intent gallerypickerIntent = new Intent(Intent.ACTION_PICK);
//									    gallerypickerIntent.setType("image/*");
//									    startActivityForResult(gallerypickerIntent, PICTURE_TAKEN_FROM_GALLERY);
//					               } });
					   			
			     
	  }
				protected void onActivityResult(int requestCode, int resultCode, Intent data) {
				    super.onActivityResult(requestCode, resultCode, data);

				    if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
				        Log.d("CameraDemo", "Pic saved");
				     
				        
				        
				         btmp_camera = (Bitmap) data.getExtras().get("data"); 
				      //   int width = btmp_camera.getWidth();
				     //    int height = btmp_camera.getHeight();
				         
				     Bitmap  newbtmp_camera = Bitmap.createScaledBitmap(btmp_camera, 120, 120, false);
				         
//				         float bitmapRatio = (float)width / (float) height;
//				         if (bitmapRatio > 0) {
//				             width = 50;
//				             height = (int) (width / bitmapRatio);
//				         } else {
//				             height = 50;
//				             width = (int) (height * bitmapRatio);
//				         }
				         
				      imageview_uploadpic.setImageBitmap(newbtmp_camera);
				    
	               	     //64 bit
	               	  ByteArrayOutputStream bao = new ByteArrayOutputStream();
	           
	               	newbtmp_camera.compress(Bitmap.CompressFormat.JPEG, 80, bao);
	  		        byte[] ba = bao.toByteArray();
	  		    
	  		        str_base64 = Base64.encodeToString(ba, Base64.DEFAULT);
	  		        
	  		        Log.e("base64:", str_base64);
				         
				    }
				    
//				        if (requestCode == PICTURE_TAKEN_FROM_GALLERY && resultCode == RESULT_OK && null != data) {
//				            Uri selectedImage = data.getData();
//				            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//				            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//				            cursor.moveToFirst();
//				            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//				            String picturePath = cursor.getString(columnIndex);
//				            cursor.close();
//				             try
//				            {
//				            	    // Decode image size
//				            	    BitmapFactory.Options o = new BitmapFactory.Options();
//				            	    o.inJustDecodeBounds = true;
//				            	    BitmapFactory.decodeFile(picturePath, o);
//				            	    // The new size we want to scale to
//				            	    final int REQUIRED_SIZE = 512;
//				            	    // Find the correct scale value. It should be the power of 2.
//				            	    int width_tmp = o.outWidth, height_tmp = o.outHeight;
//				            	    int scale = 1;
//				            	    while (true) {
//				            	        if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
//				            	            break;
//				            	        width_tmp /= 2;
//				            	        height_tmp /= 2;
//				            	        scale *= 2;
//				            	    }
//				            	    // Decode with inSampleSize
//				            	    BitmapFactory.Options o2 = new BitmapFactory.Options();
//				            	    o2.inSampleSize = scale;
//				            	    Bitmap b1 = BitmapFactory.decodeFile(picturePath, o2);
//				            	     btmp_camera= ExifUtils.rotateBitmap(picturePath, b1);
//				               	     imageview_uploadpic.setImageBitmap(btmp_camera);

//				                FileOutputStream fOut;
//					            try {
//					                fOut = new FileOutputStream(picturePath);
//					                btmp_camera.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
//					                fOut.flush();
//					                fOut.close();
//					            } catch (FileNotFoundException e1) {
//					                // TODO Auto-generated catch block
//					                e1.printStackTrace();
//					            } catch (IOException e) {
//					                // TODO Auto-generated catch block
//					                e.printStackTrace();
//					            }
//				            } catch (Exception exception)
//				            { Log.d("mistake", "exception is occur"); }
//			    				 }
				}
	 
				 
				  private boolean isValidEmail(String email)// email validation
					{
					    String emailRegex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
					    if(email.matches(emailRegex))
					    	{
					        	return true;
					    		}
					    return false;
						}  
				  public static String POST(String url, Bean_RiderRegisteration bean_rider_registeration){ //post the data on web services
				      //  InputStream inputStream = null;
				        String result = "";
				      try {
				      HttpParams httpParameters = new BasicHttpParams();
				      int timeoutConnection = 30000;
				      HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
				      int timeoutSocket = 31000;
				      HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
				      HttpClient client = new DefaultHttpClient(httpParameters);
				      HttpPost httpost = new HttpPost(Url_Address.url_Home+"/Registeration"); //url registration
				      JSONObject json = new JSONObject();
				      
				      json.put("Trigger", "Registeration");
				            json.put("FirstName", bean_rider_registeration.getUserfirstname());
				            System.err.println("firstname--"+bean_rider_registeration.getUserfirstname());
				            
				            json.put("LastName",bean_rider_registeration.getUserlastname());
				            System.err.println("lastname--"+bean_rider_registeration.getUserlastname());
				            
				            json.put("Email", bean_rider_registeration.getEmail());
				            System.err.println("email--"+bean_rider_registeration.getEmail());
				           
				            json.put("Password", bean_rider_registeration.getPassword());
				            System.err.println("pass--"+bean_rider_registeration.getPassword());
				            
				            json.put("PhoneNumber", bean_rider_registeration.getContactno());
				            System.err.println("ph no---"+bean_rider_registeration.getContactno());
				            json.put("Image", str_base64);
				            System.err.println("images--"+str_base64);
				           // json.put("apartmentno", rider_registration.getApartmentno());
				        //    json.put("Address", bean_rider_registration.getAddress());
				           // json.put("city", rider_registration.getCity());
				          //  json.put("state", rider_registration.getState());
				         // json.put("zip", rider_registration.getZip());
				            
				        //   json.put("CreditCard", bean_rider_registration.getCreditno());
				           // json.put("expdate", rider_registration.getCard_expdate());
				            //json.put("zipcode", rider_registration.getZipcode());
				         //   json.put("PromoCode", bean_rider_registration.getPromo()); 
				           
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
				       jsonResult = obj.getString("result");
				       jsonMessage = obj.getString("message");
				      Log.i("tag:", "Result: "+jsonResult);
				      Log.i("tag:", "Message :"+jsonMessage);
				      
				     /* String ListPromoCodeInfo=	obj.getString("ListPromoCodeInfo");
						Log.i("tag:", "ListPromoCodeInfo: "+ListPromoCodeInfo);
						
						JSONArray jsonarray=obj.getJSONArray("ListPromoCodeInfo");
					
						for(int i=0;i<jsonarray.length();i++){
							
						JSONObject obj2=jsonarray.getJSONObject(i);
									
						promocode=	obj2.getString("promocode");
						Log.i("tag:", "promocode: "+promocode);
						
						String dateused=	obj2.getString("dateused");
						Log.i("tag:", "dateused: "+dateused);
						
						String datecreated=	obj2.getString("datecreated");
						Log.i("tag:", "datecreated: "+datecreated);
						
						Utility.arrayListPromo().add(promocode);
						
						 }
					
						HashSet hs = new HashSet();
						hs.addAll(Utility.arrayListPromo());
						Utility.arrayListPromo().clear();
						Utility.arrayListPromo().addAll(hs);*/
						
						riderid=	obj.getString("userid");
						System.err.println(riderid);
					
						/*	String prefvehicletype=	obj.getString("prefvehicletype");
						Log.i("tag:", "prefvehicletype: "+String.valueOf(prefvehicletype));
						
						
						
						if(prefvehicletype.equalsIgnoreCase("") || prefvehicletype.equalsIgnoreCase(null))
						{
							prefvehicletype="1";
						}
						
						String fname=	obj.getString("firstname");
						Log.i("tag:", "fname: "+fname);
						
						String lname=	obj.getString("lastname");
						Log.i("tag:", "lname: "+lname);
						
						String pass=obj.getString("password");
						Log.i("tag:", "password: "+pass);
						
						String phone=	obj.getString("phone");
						Log.i("tag:", "phone: "+phone);
						
						String country=	obj.getString("country");
						Log.i("tag:", "country: "+country);
						
						String apt=obj.getString("apt");
						Log.i("tag:", "apt: "+apt);
						
						String state=obj.getString("state");
						Log.i("tag:", "state: "+state);
						
						String city=obj.getString("city");
						Log.i("tag:", "city: "+city);
						
						String zipcode=obj.getString("zip");
						Log.i("tag:", "zipcode: "+zipcode);
						
						String address=obj.getString("address");
						Log.i("tag:", "address: "+address);
						
						String profilepicdata=obj.getString("profilepicdata");
						Log.i("tag:", "profilepicdata: "+profilepicdata);
						
						String profilepiclocation=obj.getString("profilepiclocation");
						Log.i("tag:", "profilepiclocation: "+profilepiclocation);
						
						String driveractive=obj.getString("driveractive");
						Log.i("tag:", "driveractive: "+driveractive);
						
						driverid=obj.getString("driverid");
						Log.i("tag:", "driverid: "+driverid);
						
						String favoritedriver=obj.getString("favoritedriver");
						Log.i("tag:", "favoritedriver: "+favoritedriver);
						
						String paymentstatus=obj.getString("payment_status");
						Log.i("tag:", "favoritedriver: "+paymentstatus);
						
						String handicap=obj.getString("handicap");
						Log.i("tag:", "handicap: "+handicap);
						
						String activetripid=obj.getString("active_tripid");
						Log.i("tag:", "active_tripid: "+activetripid);
						
						String animal=obj.getString("animal");
						Log.i("tag:", "animal: "+animal);
						
						String specialneedsnotes=obj.getString("specialneedsnotes");
						Log.i("tag:", "specialneedsnotes: "+specialneedsnotes);
						
						String flightno=obj.getString("flightnumber");
						Log.i("tag:", "flightno: "+flightno);*/
						
						Editor editor =prefs.edit();
//						editor.putString("useremail", edittext_username.getText().toString());
						
						editor.putString("userid", riderid);
						editor.commit();
						
				/*		if(!(prefvehicletype==null))
						{
							int_value=Integer.parseInt(prefvehicletype);	
						}
						
						editor.putInt("vehicletype", int_value);
						editor.putString("userfirstname", fname);
						editor.putString("userlastname", lname);
						editor.putString("phone", phone);
						editor.putString("password", pass);
						editor.putString("apt", apt);
						editor.putString("country", country);
						editor.putString("state", state);
						editor.putString("city", city);
						editor.putString("zip", zipcode);
						editor.putString("address", address);
						editor.putString("completeaddress", address+","+city+","+state+","+country);
						editor.putString("profilepic", profilepicdata);
						editor.putString("picurl", profilepiclocation);
						editor.putString("driveractive",driveractive );
						editor.putString("driverid",driverid );
						editor.putString("favoritedriver",favoritedriver );
						editor.putString("paymentstatus",paymentstatus );
						editor.putString("handicap",handicap );
						editor.putString("activetripid",activetripid );
						editor.putString("animal",animal);
						editor.putString("specialneed",specialneedsnotes);
						editor.putString("flightno",flightno);
						editor.commit();
						
						Log.i("tag", "Address get: "+prefs.getString("completeaddress", null));*/
						Log.i("tag:", "Result: "+jsonResult);
						Log.i("tag:", "Message :"+jsonMessage);
				      }
				        catch(Exception e){
				         System.out.println(e);
				         Log.d("tag", "Error :"+e);
				       
				         }
					return result;
				      }
				  private class HttpAsyncTask extends AsyncTask<String, Void, String> { // AsyncTask  for registration web services
				        @Override
				        protected String doInBackground(String... urls) {

				        	bean_rider_registration = new Bean_RiderRegisteration();
				        	
				        	bean_rider_registration.setUserfirstname(ed_userfistname.getText().toString());
				        	System.err.println("userfirstname--"+ed_userfistname.getText().toString());
				        	
				        	bean_rider_registration.setUserlastname(ed_userlastname.getText().toString());
				        	System.err.println("userlastname--"+ed_userlastname.getText().toString());
				        	
				        	bean_rider_registration.setEmail(ed_email.getText().toString());
				        	System.err.println("email--"+ed_email.getText().toString());
				        	
				        	bean_rider_registration.setPassword(ed_password.getText().toString());
				        	System.err.println("pass--"+ed_password.getText().toString());
				        	
				        	bean_rider_registration.setContactno(zipcode+ed_contactno.getText().toString());
				        	System.err.println("contactno--"+zipcode+ed_contactno.getText().toString());
				        
				        	     	//System.err.println("data send=="+bean_rider_registration);
				            return POST(urls[0],bean_rider_registration);
				        }
				        @Override
					    protected void onPreExecute() {  // onPretExecute displays the results of the AsyncTask.
					        progress = ProgressDialog.show(Rider_Register.this, "", "Please wait...");
					    }
				      
				        @Override
				        protected void onPostExecute(String result) {   // onPostExecute displays the results of the AsyncTask.
				            //Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
				        	 progress.dismiss();
				        	 if(jsonResult!=null)
				        	 {
						        	if(jsonResult.equals("0"))
						        	{
						        		//Utility.alertMessage(Rider_Register.this,jsonMessage);
						        		new httpRegisterTask().execute();
		//				        		Intent i=new Intent(Rider_Register.this,MapView_Activity.class);
		//				        		startActivity(i);
						        		}
						        	else
						        	{
						        		Utility.alertMessage(Rider_Register.this,jsonMessage);
						        	}
							    	  ed_userfistname.setText("");
							    	  ed_userlastname.setText("");
							    	 // ed_email.setText("");
							    	 // ed_password.setText("");
							    	  ed_confirm_password.setText("");
							    	  ed_contactno.setText("");
						       }
				        }
				    }
				  
				  
				  
 public void onBackPressed() {
				Intent i=new Intent(Rider_Register.this,Login_Activity.class);
				finish();
				startActivity(i);
					}
				  
				  
				  
 /******************* Register class ***********************/
			class httpRegisterTask extends AsyncTask<Void, Void, Void> {
							
					  	String url_register = "http://appba.riderapid.com/newreg/?riderid="+prefs.getString("userid", null);
					  //	String paymenturl = "http://appba.riderapid.com/process/?tripid=1234rec_id=" + str_last3digit+"riderid=77tip_amount=7suggested_fare=" +"70"; 
					  				@Override
					  				protected void onPreExecute() {
					  				super.onPreExecute();
					  				// Showing progress dialog
					  				progressbar1 = new ProgressDialog(Rider_Register.this);
					  				progressbar1.setMessage("Please wait...");
					  				progressbar1.setCancelable(false);
					  				progressbar1.show();
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
					  		
					  			progressbar1.dismiss();
					  			if(register_jsonResult!=null)
					  			{
						  			if(register_jsonResult.equals("0"))
							  		{
								       Intent i=new Intent(Rider_Register.this,Login_Activity.class);
								       i.putExtra("username",ed_email.getText().toString());
								       i.putExtra("password",ed_password.getText().toString());
								       finish();
									   startActivity(i);
						  			
							  			}
							  		else
							  		{
							  			Utility.alertMessage(Rider_Register.this, register_jsonMessage);
							  			}
						  			}
					  			}
					  		
					/******************* register function ***********************/
					  		public void parsing() throws JSONException {
					  		try {
					  				HttpParams httpParameters = new BasicHttpParams();
					  				int timeoutConnection = 30000;
					  				HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
					  				HttpGet httpGet = new HttpGet(url_register);
									
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
									register_jsonResult=obj.getString("result");
									register_jsonMessage=obj.getString("message");
										
							        System.err.println("register_jsonResult="+register_jsonResult);
							        System.err.println("register_jsonMessage message="+register_jsonMessage);
							        
									}catch(Exception e){
									e.printStackTrace();
									
								}
					  		}}
					
					public void deviceRegister()
					{
						try {
					        PackageInfo info = getPackageManager().getPackageInfo("com.rapidride", PackageManager.GET_SIGNATURES);
					        for (Signature signature : info.signatures) {
					            MessageDigest md = MessageDigest.getInstance("SHA");
					            md.update(signature.toByteArray());
					            Log.d("Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
					            }
					    } catch (NameNotFoundException e) {

					    	e.printStackTrace();
					    } catch (NoSuchAlgorithmException e) {
					    	e.printStackTrace();

					    }
						TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
				        uuid = tManager.getDeviceId(); 
				        
						registerReceiver(mHandleMessageReceiver,new IntentFilter(Utility.DISPLAY_MESSAGE_ACTION));
						regId = GCMRegistrar.getRegistrationId(this);
				        Editor ed=prefs.edit();
				      //  ed.putString("regid", regId);
				        ed.putString("uuid", uuid);
				        ed.commit();
				        System.out.println("Resister id="+regId);
				        
				        GCMRegistrar.register(this, Utility.SENDER_ID);
				        Intent i1=new Intent(Rider_Register.this,MapView_Activity.class);
				        finish();
				    	startActivity(i1);
				        //new RegisterTask().execute();
					    if (regId.equals("")) {
					        // Automatically registers application on startup.
					    	// GCMRegistrar.register(this, Utility.SENDER_ID);
					        System.err.println("enter in if");
					    } else {
					    	//  pDialog.dismiss();
					        	System.err.println("enter in else");
				            // Device is already registered on GCM, check server.
				            if (GCMRegistrar.isRegisteredOnServer(this)) {
				                // Skips registration.
				               Log.e("already registered", "already reg");
				              
//				               Intent i=new Intent(Splash_Activity.this,Login_Activity.class);/************go to login class******************/
//				               startActivity(i);
				             //  pBar.setVisibility(View.GONE);
				               Intent i=new Intent(Rider_Register.this,MapView_Activity.class);
				               finish();
				               startActivity(i);
				 			   
				            }
				            
				          
				            else {
				                // Try to register again, but not in the UI thread.
				                // It's also necessary to cancel the thread onDestroy(),
				                // hence the use of AsyncTask instead of a raw thread.
				              //  final Context context = this;
				                
				               new RegisterTask().execute();
				                		
//				                		{
//				                	protected void onPreExecute() {
////				            			super.onPreExecute();
////				            			// Showing progress dialog
//				            	//	pBar.setVisibility(View.VISIBLE);
//				                	}
//				                    @Override
//				                    protected Void doInBackground(Void... params) {
//				                    	
//				                    	System.err.println("doInBackground");
//				                        boolean registered =
//				                        	//	 String role, final String driverId,final String riderId,final String regId,final String deviceId,final String TokenId)
//				                               // com.rapidride.util.ServerUtilities.register(context, uuid, regId,);
//				                        com.rapidride.util.ServerUtilities.register(context, "driver", "20", "71",uuid, regId);
//				                        // At this point all attempts to register with the app
//				                        // server failed, so we need to unregister the device
//				                        // from GCM - the app will try to register again when
//				                        // it is restarted. Note that GCM will send an
//				                        // unregistered callback upon completion, but
//				                        // GCMIntentService.onUnregistered() will ignore it.
//				                        if (!registered) {
//				                            GCMRegistrar.unregister(context);
//				                        }
//				                        return null;
//				                    }
				//
//				                    @Override
//				                    protected void onPostExecute(Void result) {
//				                    	pBar.setVisibility(View.GONE);
//				                        mRegisterTask = null;
//				                      Intent i=new Intent(Splash_Activity.this,Login_Activity.class); /************go to class******************/
//				          			  System.err.println("splash");
//				                        startActivity(i);
//				                    }
				//
//				                };
//				                mRegisterTask.execute(null, null, null);
//				            }
				           
				       }
					}
					}
					
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.

					
					class RegisterTask extends AsyncTask<Void, Void, Void>
					//final Context context = Login_Activity.this;
						{
					protected void onPreExecute() {
//						super.onPreExecute();
//						// Showing progress dialog
//					pBar.setVisibility(View.VISIBLE);
					}
				    @Override
				    protected Void doInBackground(Void... params) {
				    	
				    	System.err.println("doIn Background"+"driver"+" driverid="+driverid+" riderid="+riderid+"  uuid="+uuid+"regId="+regId);
				        boolean registered =
				        	//	 String role, final String driverId,final String riderId,final String regId,final String deviceId,final String TokenId)
				               // com.rapidride.util.ServerUtilities.register(context, uuid, regId,);
				        		
				        com.rapidride.util.ServerUtilities.register(Rider_Register.this, "rider", driverid, riderid,uuid, regId);
				        // At this point all attempts to register with the app
				        // server failed, so we need to unregister the device
				        // from GCM - the app will try to register again when
				        // it is restarted. Note that GCM will send an
				        // unregistered callback upon completion, but
				        // GCMIntentService.onUnregistered() will ignore it.
				        if (!registered) {
				            GCMRegistrar.unregister(Rider_Register.this);
				        }
				        return null;
				    }

				    @Override
				    protected void onPostExecute(Void result) {
				    //	pBar.setVisibility(View.GONE);
				        mRegisterTask = null;
				       // pDialog.dismiss();
				        Intent i=new Intent(Rider_Register.this,MapView_Activity.class);
				        finish();
						startActivity(i);
				    }
						}
					private void checkNotNull(Object reference, String name) {
					    if (reference == null) {
					        throw new NullPointerException(
					                getString(R.string.error_config, name));
					    }
					}

					private final BroadcastReceiver mHandleMessageReceiver =
					        new BroadcastReceiver() {
					    @Override
					    public void onReceive(Context context, Intent intent) {
					    	Log.e("on receive","hakshdsahkhd");
//					        String newMessage = intent.getExtras().getString("my custom message");
//					        mDisplay.append(newMessage + "\n");
					    }
					};

					protected void onDestroy() {
						try{
					        if (mRegisterTask != null) {
					            mRegisterTask.cancel(true);
					        }
					        unregisterReceiver(mHandleMessageReceiver);
					        GCMRegistrar.onDestroy(this);
						}catch(Exception e){
							e.printStackTrace();
						}
					    super.onDestroy();
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
}