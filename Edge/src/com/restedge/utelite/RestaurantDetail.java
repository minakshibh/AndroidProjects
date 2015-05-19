package com.restedge.utelite;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.MadsAdView.MadsInlineAdView;
import com.adgoji.mraid.adview.AdViewCore;
import com.adgoji.mraid.adview.AdViewCore.OnAdDownload;
import com.restedge.utelite.R;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.restedge.event.EventListing;
import com.restedge.util.ConnectionDetector;
import com.restedge.util.Place;
import com.restedge.util.RestaurantDatabase;
import com.restedge.util.SAXXMLParser;
import com.restedge.util.User;


public class RestaurantDetail extends Activity {	
	
	// Declaration of layout views.........
	SharedPreferences userPreference;
	TextView restaurantInfoText, userRating, addressText,headerName, name, time, comment, dialogMessage, dialogTitle, okButton, cancelButton; 
	Button postComment, readMore,readMoreComment,showEvent;
	ImageButton backButton;
	RatingBar appRateingBar;
	EditText commentValue;	
	ImageView restaurantImage, mapIcon, sharePost;
	LinearLayout commentLayout;
	int sdk = android.os.Build.VERSION.SDK_INT;
	 boolean isOpened = false;
	//boolean isInternetConnected=false;
	boolean isPostCommentclick=false;
	boolean isPostCommentclickShow=false;
	boolean isGetComment=true;
	boolean isFacbookClick=false;
	boolean isRatingClick=false;
	boolean isClick=false;
	int cityId;
	Intent placeIntent;
	Place place;
	
	com.restedge.util.ImageLoaderRestaurant	imageLoader;
	String webUrl, methodName, address, fbPostResult;
	
	List<User> getComments;	
	public static List<User>  newList=null;
	double showRating;
	
	ConnectionDetector connectionDetector;
	
    private static final int DLG_EXAMPLE1 = 0;    
    private static final int TEXT_ID = 0;      
    //vicky 235404913284838
    //rahul sir 554439251295770
    // Your Facebook APP ID
    private static String APP_ID = "554439251295770"; // Replace your App ID here  
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";

    SharedPreferences pref;
 
	private Facebook facebook;
	AsyncFacebookRunner mAsyncRunner = null;	
	LayoutInflater inflater ; 
	Dialog settingsDialog;	
	String description, commentSuccess;
	ProgressDialog dialog;
	String receiptId,EmailSubject;
	SharedPreferences mypref;
	int placeId,counter=0;
//	MadsInlineAdView adView;
  //Called first when the android screen is created.	
	@SuppressLint({ "NewApi", "CommitPrefEdits" })
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);			
		setContentView(R.layout.restaurantdetail);		
		
		// initialize the variable of check internet connection class 
		connectionDetector=new ConnectionDetector(this);
		//isInternetConnected=connectionDetector.isConnectingToInternet();
		
		pref=getSharedPreferences("adpref", MODE_PRIVATE);
		counter=pref.getInt("AdCounter", 0);
		
		
		
		
		/*adView = (MadsInlineAdView)findViewById(R.id.mads_interstitial_adview);*/
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		if(counter==5)
		{
			SharedPreferences.Editor edit=pref.edit();
			edit.putInt("AdCounter", 0);
			edit.commit();
			//adView.setVisibility(View.VISIBLE);
			
		}
		else
		{
			counter++;
			SharedPreferences.Editor edit=pref.edit();
			edit.putInt("AdCounter", counter);
			edit.commit();
			//adView.setVisibility(View.GONE);
		}
/*		adView.setAdExpandListener(new AdExpandListener() {
			
			@Override
			public void onExpand() {
				// For the demo use case we just ad it to the main content view 
				// which means we overwrite the default behaviour of showing it 
				// inline 
				ViewGroup adParent = (ViewGroup)adView.getParent();
				if ( adParent != null ) {
					adParent.removeView(adView);
				} 
				((ViewGroup)findViewById(Window.ID_ANDROID_CONTENT)).addView(adView);
				
			}
			
			@Override
			public void onClose() {
				// TODO Auto-generated method stub
				
			}
		});*/
/*		adView.setOnAdDownload(new OnAdDownload() {
			
			@Override
			public void noad(AdViewCore arg0) {
				// TODO Auto-generated method stub
				Log.e("MAID","no ads ");
				adView.setVisibility(View.GONE);
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
		});*/
//		MadsDemoUtil.setupUI(adView, this);
//		adView.useCloseButton(true);
		
		
		
		setListnerToRootView();
		
		mypref=getSharedPreferences("mypref", MODE_PRIVATE);
		
		receiptId=mypref.getString("SupportEmail", "");
		EmailSubject=getResources().getString(R.string.EmailSubject);
		
		
		
		
		placeIntent=getIntent();
		placeId=placeIntent.getIntExtra("PlaceId", 0);
		cityId=placeIntent.getIntExtra("cityId", 0);
		
		System.out.println(placeId+" "+cityId);
		RestaurantDatabase db=new RestaurantDatabase(this);
		 place=db.getPlace(placeId);
	
		
		//get the intent (Values for the Place/Restaurant) from the Restaurant Listing screen
//		placeIntent=getIntent();		
//		place=(Place)placeIntent.getSerializableExtra("Place");		
		
		
		
		//Getting the SharedPreferences............
		userPreference = this.getSharedPreferences("UserPreference", MODE_PRIVATE);
		commentSuccess=null;
		
		//Created the Facebook api object.
        facebook = new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		
		
		// Restration of layout views.........
		restaurantInfoText = (TextView) findViewById(R.id.restinfo);
		addressText = (TextView) findViewById(R.id.addresstxt);
		headerName  = (TextView) findViewById(R.id.headername);		
		userRating  = (TextView) findViewById(R.id.rateit);
		
		readMore = (Button) findViewById(R.id.readmore);
		readMoreComment = (Button) findViewById(R.id.readmorecomment);
		postComment = (Button) findViewById(R.id.postcomments);
		backButton = (ImageButton) findViewById(R.id.backbtn);
		
		restaurantImage= (ImageView) findViewById(R.id.restaurantimage); 
		mapIcon= (ImageView) findViewById(R.id.mapIcon); 	
		sharePost= (ImageView) findViewById(R.id.shareonfb);
		
		commentValue = (EditText) findViewById(R.id.txtcomments);		
		appRateingBar= (RatingBar) findViewById(R.id.ratingBar);		
			
		// Create the custom dialog popup.
		 settingsDialog= new Dialog(this); 
		 settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE); 
		 LayoutInflater DialogInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 ViewGroup DialogParent=(ViewGroup) DialogInflater.inflate(R.layout.dialoglayout, null);
		 View dialoglayout = DialogInflater.inflate(R.layout.dialoglayout, DialogParent, false);
		 settingsDialog.setContentView(dialoglayout); 
		 okButton=(TextView)dialoglayout.findViewById(R.id.oklbtn);
		 dialogMessage=(TextView)dialoglayout.findViewById(R.id.dialogmessage);
		 cancelButton=(TextView)dialoglayout.findViewById(R.id.cancelbtn);	 
		
		 showEvent=(Button)findViewById(R.id.Showevents);
		
			showEvent.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					Intent in=new Intent(RestaurantDetail.this,EventListing.class);
					in.putExtra("ClassName", "Restaurant");
					in.putExtra("placeId", placeId);
					
					in.putExtra("cityId",cityId );
					 Log.e("Tag", placeId+" "+cityId);
					
					
					startActivity(in);
				}
			});
		 
		 
		 
		 
		 
		 
		
		// Setting values.........
		address=place.getAddress();
		showRating=place.getRating();
		appRateingBar.setRating((float)showRating);
		appRateingBar.setIsIndicator(true);
		//restaurantInfoText.setText(Html.fromHtml(place.getDescription()));//setText(place.getDescription());
		description=place.getDescription();
		description = description.replaceAll("\\\\n", "\\\n");
		description = description.replaceAll("\\\\r", "\\\r");
		restaurantInfoText.setText(description);
		addressText.setText(address);	
		headerName.setText(place.getName());
		
		if(!(place.getPlaceImage()==null)){
		restaurantImage.setImageBitmap(BitmapFactory.decodeByteArray(place.getPlaceImage() , 0, place.getPlaceImage() .length));
		}else{
			Log.e("place.getImageUrl()   ",place.getImageUrl());
			restaurantImage.setImageBitmap(getDecodeUrl(place.getImageUrl()));
			
		}
		
		
		
		
//		userRating.setOnClickListener(new OnClickListener() {
//			
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				showDialog();
//			}
//		});
		userRating.setMovementMethod(LinkMovementMethod.getInstance());
		addClickablePart("Give your rating","Give your rating").setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_base)), 0,0,0);		
		userRating.setText(addClickablePart("Give your rating", "Give your rating"), BufferType.SPANNABLE);
		
			
		// Added 3 comments dynamically on the screen............
		commentLayout=(LinearLayout) findViewById(R.id.commentsView);
		inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(connectionDetector.isConnectingToInternet()){		
				new RequestTask().execute(getResources().getString(R.string.app_url)+"/"+"GetComments",  ""+place.getId());	   
		}else{
			dialogMessage.setText("Error in internet connection No comments found");
			okButton.setVisibility(View.GONE);
			//okButton.setText("Continue");
			cancelButton.setText("Ok");
			settingsDialog.show();
		}
			/*for(int i = 0; i < 3; i++) {
				  
				  LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				  ViewGroup parent=(ViewGroup) inflater.inflate(R.layout.showcommentlayout, null);
				  View rowView = inflater.inflate(R.layout.showcommentlayout, parent, false);
			   
				   name = (TextView) rowView.findViewById(R.id.name);
				   time = (TextView) rowView.findViewById(R.id.time);
				   comment = (TextView) rowView.findViewById(R.id.comments);
			    
				  name.setText(getComments.get(i).getName());
				  time.setText(getComments.get(i).getTime());
				  comments.setText(getComments.get(i).getComment());
			    
				  commentLayout.addView(rowView);
			}	*/    
		
		
		
        
        // Click listner for the views (For Buttons and clickable images)
        View.OnClickListener listener = new View.OnClickListener() {
            @SuppressLint("NewApi")
			@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
			public void onClick(View v) {
              // it was the 1st button
            	switch (v.getId()) {
            	
				case  R.id.postcomments:
					
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						postComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.readinfo));
					} else {
						postComment.setBackground(getResources().getDrawable(R.drawable.readinfo));
					}
					
//					showDialog();
					
					if(connectionDetector.isConnectingToInternet()){
								isPostCommentclick=true;
								isFacbookClick=false;
								isRatingClick=false;
								isClick=true;
								methodName="SaveComment";
							try{
							if(userPreference.getString("LoginResult","").equals("Login Successful")){
								
								   String str=commentValue.getText().toString();	
								   Log.e("Restaurant Detail : ", "User commets : "+str);
								   commentValue.setText("");
								   
								   if(!str.trim().equals("")){
								   webUrl = getResources().getString(R.string.app_url)+"/"+methodName;
								   new RequestTask().execute(webUrl,  userPreference.getString("UserId",""), ""+place.getId(), str);	
								  // Toast.makeText(getApplicationContext(), "Coment value : "+str, Toast.LENGTH_LONG).show();
								   }else{
									   
									    dialogMessage.setText("Please enter comment value");
										okButton.setVisibility(View.GONE);
										cancelButton.setText("Ok");
										settingsDialog.show();
								   }
								   
								}else{
									dialogMessage.setText("Please Login first");
									okButton.setVisibility(View.VISIBLE);
									cancelButton.setVisibility(View.VISIBLE);
									okButton.setText("Continue");
									cancelButton.setText("Cancel");
									settingsDialog.show();
								}}catch(Exception e){e.printStackTrace();/*Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();*/}
					}else{
						dialogMessage.setText("Error in internet connection");
						okButton.setVisibility(View.GONE);
						//okButton.setText("Continue");
						cancelButton.setText("Ok");
						settingsDialog.show();
					}		
										
					break;							
					
				case  R.id.mapIcon:	
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						mapIcon.setBackgroundDrawable(getResources().getDrawable(R.drawable.maphover));
					} else {
						mapIcon.setBackground(getResources().getDrawable(R.drawable.maphover));
					}
					
//					showDialog();
					
					if(connectionDetector.isConnectingToInternet()){					
						
						Intent intent=new Intent(RestaurantDetail.this, SubActivity.class);
						intent.putExtra("value", "map");
						intent.putExtra("Place",place);
						startActivity(intent);						
						
					}else{
						dialogMessage.setText("Error in internet connection");
						okButton.setVisibility(View.GONE);
						//okButton.setText("Continue");
						cancelButton.setText("Ok");
						settingsDialog.show();
					}	
					
		            break;
		            	
				case  R.id.backbtn:	
					
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						backButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.back_button_selector));
					} else {
						backButton.setBackground(getResources().getDrawable(R.drawable.back_button_selector));
					}
					
				    finish();		
				    
	            	break;
	            	
				case  R.id.readmore:
										
					if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
						readMore.setBackgroundDrawable(getResources().getDrawable(R.drawable.readinfo));
					} else {
						readMore.setBackground(getResources().getDrawable(R.drawable.readinfo));
					}
					
					Intent intent=new Intent(RestaurantDetail.this, SubActivity.class);
					intent.putExtra("value", "ReadMore");
					intent.putExtra("Place",place);
					startActivity(intent);
					
					
	            	break;
	            	
				case  R.id.readmorecomment:	
//					showDialog();
					if(connectionDetector.isConnectingToInternet()){
						try{
						
							if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
								readMoreComment.setBackgroundDrawable(getResources().getDrawable(R.drawable.outer));
							} else {
								readMoreComment.setBackground(getResources().getDrawable(R.drawable.outer));
							}
							
							/* Log.e("Commet List", "Commet List"+getComments.size());
							User[] getCommentsArray = new User[getComments.size()];
				            //copy your List of Strings into the Array ,and then pass it in your intent
							for(int i=0; i<getComments.size(); i++){
								getCommentsArray[i]=getComments.get(i);
							}*/
							  newList=getComments;
				            // ....
							Intent commentintent = new Intent(RestaurantDetail.this, SubActivity.class);
				            commentintent.putExtra("value", "ReadMoreComments");
				            commentintent.putExtra("Place",place);
				            commentintent.putExtra("CommentsData", "hgjjjjhgh");
				            //commentintent.putParcelableArrayListExtra("CommentsData", );
				            //Log.e("Commet Array", "Commet Array"+getCommentsArray.length);
				            startActivityForResult(commentintent, 0); 
						}catch(Exception e){}
		            
					}else{
						newList=getComments;
						if(newList.size()!=0){
							Intent commentintent = new Intent(RestaurantDetail.this, SubActivity.class);
				            commentintent.putExtra("value", "ReadMoreComments");
				            commentintent.putExtra("Place",place);
				            commentintent.putExtra("CommentsData", "hgjjjjhgh");
				            //commentintent.putParcelableArrayListExtra("CommentsData", );
				            //Log.e("Commet Array", "Commet Array"+getCommentsArray.length);
				            startActivityForResult(commentintent, 0);
						}else{
						dialogMessage.setText("Error in internet connection, no comments found");
						okButton.setVisibility(View.GONE);
						//okButton.setText("Continue");
						cancelButton.setText("Ok");
						settingsDialog.show();
						}
					}
//					
	            	break;
	            	
	            case  R.id.shareonfb:	
	            	if(connectionDetector.isConnectingToInternet()){
	            	if (! facebook.isSessionValid()) {
	            		getProfileInformation();
	        			loginAndPostToWall();
	        			
	        		}
	        		else {
	        			isPostCommentclick=false;
	        			isRatingClick=false;
	        			isFacbookClick=true;
	        			isClick=true;
	        			getProfileInformation();
	        			//new RequestTask().execute(loginRegisterUrl,  userPreference.getString("UserId",""), ""+place.getId(),"");	
	        			//postToUsersWall(Facebook fb, String message, String linkurl, String caption, String des, String restName, String imageurl)
	        			new RequestTask().execute(userPreference.getString("FbUser",""), place.getImageUrl(), "", description, place.getName(), place.getImageUrl());	
	        			//fbPostResult=postToUsersWall(facebook, userPreference.getString("FbUser",""), place.getImageUrl(), "", description, place.getName(), place.getImageUrl());
						  
	        			/*
	        			//postToFrendsWall(userPreference.getString("FbUser",""));
	        			postToUsersWall(facebook, userPreference.getString("FbUser",""), place.getImageUrl(), "", description, place.getName(), place.getImageUrl());
        			   	testing();
	        			 */	        		
	        			}
	            	}else{
						
						dialogMessage.setText("Error in internet connection, Please check");
						okButton.setVisibility(View.GONE);
						//okButton.setText("Continue");
						cancelButton.setText("Ok");
						settingsDialog.show();
	            	}
					
					
        	       break;   
        	       
	            case  R.id.oklbtn:
					okListener();
					break;	
					
				case  R.id.cancelbtn:
					cancelListener();	
					break;	
				case R.id.Showevents:
					Intent in=new Intent(RestaurantDetail.this,EventListing.class);
					in.putExtra("ClassName", "Restaurant");
					in.putExtra("placeId", placeId);
					in.putExtra("cityId",cityId );
					break;
				}         
            }
          };
          
          
          // Set the click listener on the view's clik here..
         mapIcon.setOnClickListener(listener);        
         postComment.setOnClickListener(listener); 
         backButton.setOnClickListener(listener); 
         readMore.setOnClickListener(listener); 
         readMoreComment.setOnClickListener(listener); 	 
         sharePost.setOnClickListener(listener);
         okButton.setOnClickListener(listener);          	   
         cancelButton.setOnClickListener(listener);	
         
	}
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog) { 
        switch (id) {
            case DLG_EXAMPLE1:                
                break;                      
        }
    }
	
	
	public void cancelListener(){
		settingsDialog.dismiss();		
	}
	
    public void okListener(){		
    	settingsDialog.dismiss();
    	Intent loginIntent=new Intent(RestaurantDetail.this, LoginRegisterScreen.class);
    	loginIntent.putExtra("Name", place.getName());
		startActivity(loginIntent);
	}
	
	  @Override
	    protected Dialog onCreateDialog(int id) {
	 
	        switch (id) {
	            case DLG_EXAMPLE1: 
	               return getRatingDialog();
	                
	            default:
	                return null;
	        }
	    }
	
	
	@Override
    protected void onResume() {
        super.onResume();
     
    }  
	
	/*public void share(View button){
		if (! facebook.isSessionValid()) {
			loginAndPostToWall();
			getProfileInformation();
		}
		else {
			getProfileInformation();
			postToFrendsWall(userPreference.getString("FbUser",""));
		}
	}*/

	public void loginAndPostToWall(){
		 facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}	
	
	// Make the Strings ("Read More", "Get Directions", "Read More Comments") like a link (url) and clickable.....
	public SpannableStringBuilder addClickablePart(String str, final String type) {
	    SpannableStringBuilder ssb = new SpannableStringBuilder(str);
	    Log.e("Exceptionfsdfdsfdesgdsf", " length : "+str.length()+" ,  : "+str.indexOf("G"));
		
	    try{    
	        Matcher	m= Pattern.compile("Give your rating").matcher(str);
	        
	        while(m.find()){		    	
		    	// final String clickString  = m.group(0); // (714) 321-2620			    	
		         ssb.setSpan(new ClickableSpan() {	
		        	  @SuppressWarnings("deprecation")
					@Override
		            public void onClick(View widget) {
		        		  if(connectionDetector.isConnectingToInternet()){
		              	 if(type.equals("Give your rating")){	
							try{
								if(userPreference.getString("LoginResult", "").equals("Login Successful")){
									showDialog(DLG_EXAMPLE1);
									isFacbookClick=false;
									isPostCommentclick=false;
									isClick=true;
									isRatingClick=true;
								}else{
									dialogMessage.setText("Please Login first");
									okButton.setVisibility(View.VISIBLE);
									cancelButton.setVisibility(View.VISIBLE);
									okButton.setText("Continue");
									cancelButton.setText("Cancel");
									settingsDialog.show();
								}}catch(Exception e){e.printStackTrace();/*Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();*/}
		              	 }
		        		  }else{
							dialogMessage.setText("Error in internet connection");
							okButton.setVisibility(View.GONE);
							//okButton.setText("Continue");
							cancelButton.setText("Ok");
							settingsDialog.show();
							//Toast.makeText(getApplicationContext(), "Please check internet connection first..", Toast.LENGTH_LONG).show();
						}		
		        		  	
		            }
		        	 
		        }, m.start(), m.end(), 0);
		    }
		   }catch(Exception e){Log.e("Exception",  e.getMessage());	    
	    	Log.e("Exception", e.getMessage());	    }
	        ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#9B0C08")), 0 ,16, 0);
	        return ssb;	    
	 }	
	
	// Give the user Rating PopUp.............
	 private Dialog getRatingDialog() {
	   	 
	        AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        builder.setTitle("Give Rating...");	        
	        LinearLayout layout = new LinearLayout(this);
	        layout.setId(TEXT_ID);
	        layout.setOrientation(LinearLayout.VERTICAL);	     
	        
	        LayoutInflater ratingInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup ratingParent=(ViewGroup) ratingInflater.inflate(R.layout.ratingpopup, null);
			View ratingView = ratingInflater.inflate(R.layout.ratingpopup, ratingParent, false);
	        final RatingBar ratingbar=(RatingBar)ratingView.findViewById(R.id.ratingBarpopup);
	        
	        layout.addView(ratingView);	       
	        builder.setView(layout);        
	 
	        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        	 public void onClick(DialogInterface dialog, int whichButton) {
	            	isPostCommentclick=false;	     
	            	isFacbookClick=false;
	            	isRatingClick=true;
	            	isClick=true;
	            	methodName="RatePlace";
	            	float myrating = ratingbar.getRating();
	            	//ratingBar.setRating(value);
	              //  Toast.makeText(getApplicationContext(), "Rating : "+myrating, Toast.LENGTH_LONG).show(); 
	              //  loginRegisterUrl = getResources().getString(R.string.app_url)+"/"+methodName;
	            	webUrl=getResources().getString(R.string.app_url)+"/"+"RatePlace";
	                new RequestTask().execute(webUrl,  userPreference.getString("UserId",""), ""+place.getId(), ""+myrating); 					
	                return;
	            }
	        });
	 
	        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	 
	            public void onClick(DialogInterface dialog, int which) {
	                return;
	                
	            }
	        });
	 
	        return builder.create();
	        } 	
	 
	
	// Class for background task's like get data frome the web services 
    class RequestTask extends AsyncTask<String, String, String>{
    	
    	@Override
	    protected void onPreExecute() {	    	
	    	super.onPreExecute();	 
	    	
	    	 // Progress bar..........
			 dialog=new ProgressDialog(RestaurantDetail.this);
			 dialog.setTitle("Edge");
		     dialog.setMessage("Please wait");
		     dialog.setCanceledOnTouchOutside(false);	
	    	 dialog.show();
	    	
	    }
    	
		    @Override
		    protected String doInBackground(String... uri) {  
		    	 
		    	if(isClick){
		    		  String content = null;
		    		
		    		 try {	        
				            
				            if(isPostCommentclick){
				            	isPostCommentclickShow=true;
				            	DefaultHttpClient httpClient = new DefaultHttpClient();	            
					            HttpPost request = new HttpPost(uri[0]);
					            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);				           
					          
				            	    nameValuePairs.add(new BasicNameValuePair("userId", uri[1]));
						            nameValuePairs.add(new BasicNameValuePair("placeId", uri[2]));
						            nameValuePairs.add(new BasicNameValuePair("comment", uri[3]));				            
						            request.setEntity(new UrlEncodedFormEntity(nameValuePairs));				            
						            HttpResponse response = httpClient.execute(request);				            
						            HttpEntity entity = response.getEntity();
						            content = EntityUtils.toString(entity);
						            Log.e("Post Comment", "Comment : "+content);  
						            //////////////////////////////////////////////////
						            InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
					    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
					    	        DocumentBuilder db = dbf.newDocumentBuilder();
					    	     
					    	        Document doc = db.parse(is);
					    	        
					    	        Element root = doc.getDocumentElement();
					    	        NodeList resultItem = root.getElementsByTagName("Result");
					    	        commentSuccess = resultItem.item(0).getFirstChild().getNodeValue();
					    	        
						            //////////////////////////////////////////////////
						            
						            InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));						            
						            getComments = SAXXMLParser.parseComment(stream);
						            newList=getComments;
						            return content;
				            
				            }else if (isRatingClick) {
						            	String[] strArray = new String[3];
						            	DefaultHttpClient httpClient = new DefaultHttpClient();	            
							            HttpPost request = new HttpPost(uri[0]);
							            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);				           
							            Log.e("my Rating", "Rating : "+uri[3]);
						            	nameValuePairs.add(new BasicNameValuePair("userId", uri[1]));
								        nameValuePairs.add(new BasicNameValuePair("placeId", uri[2]));
						            	nameValuePairs.add(new BasicNameValuePair("myRatings", uri[3]));						            	
						            	request.setEntity(new UrlEncodedFormEntity(nameValuePairs));							            
							            HttpResponse response = httpClient.execute(request);							            
							            HttpEntity entity = response.getEntity();
							            content = EntityUtils.toString(entity);
							            Log.e("Rating on web", "Rating : "+content);					         
							            
							            try{
							    			InputStream is = new ByteArrayInputStream(content.getBytes("UTF-8"));
							    			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
							    	        DocumentBuilder db = dbf.newDocumentBuilder();
							    	     
							    	        Document doc = db.parse(is);
							    	        
							    	        Element root = doc.getDocumentElement();
							    	        NodeList resultItem = root.getElementsByTagName("Result");
							    	        String result = resultItem.item(0).getFirstChild().getNodeValue();
							    	        
							    	        strArray[0] = result;
							    	        
							    	        if(result.equals("0"))
							    	        {
							    	        	Log.e("Login","successful");
							    	        	NodeList messageItem = root.getElementsByTagName("Message");
							    	        	NodeList ratingItem = root.getElementsByTagName("rating");
							    		        String message = messageItem.item(0).getFirstChild().getNodeValue();
							    		        String rating = ratingItem.item(0).getFirstChild().getNodeValue();
							    		        strArray[1] = message;	
//							    		        showRating=Long.parseLong(rating);
							    		        showRating=Double.parseDouble(rating);
							    		        RestaurantDatabase data=new RestaurantDatabase(RestaurantDetail.this);
							    		       int rateinfo= data.updateRating(placeId, showRating);
							    		       Log.e("Successful update", ""+rateinfo);
							    		        Log.e("Rating ","successful , "+message +"Rating from web` : "+showRating);
							    	        }else{
							    	        	Log.e("Rating ","failed");
							    	        	NodeList messageItem = root.getElementsByTagName("Message");
							    		        String message = messageItem.item(0).getFirstChild().getNodeValue();
							    		        strArray[1] = message;		       
							    		        Log.e("Error message",message);
							    	        }	
							    		}catch(Exception e)
							    		{
							    			Log.e("In post comment error", content);
							    			e.printStackTrace();
							    		}				            
						            }else if (isFacbookClick){	
						            	/*Looper.prepare();	*/
						            	/*
						            	runOnUiThread(new Runnable() {
											    public void run() {	*/										
											//stuff that updates ui
											    	//fbPostResult=postToUsersWall(facebook, userPreference.getString("FbUser",""), place.getImageUrl(), "", description, place.getName(), place.getImageUrl());
						            	//fbPostResult=postToUsersWall(facebook, userPreference.getString("FbUser",""), place.getImageUrl(), "", description, place.getName(), place.getImageUrl());			
						            	Log.e("", "TestTTTTTTTTTTTTTTTTTTTTT:   " +uri[0]+"    :::::   "+userPreference.getString("FbUser",""));
						            	fbPostResult=postToUsersWall(facebook, uri[0], uri[1], uri[2], uri[3], uri[4],uri[5]);			
						            	Log.e("FaceBoookkkkkkkkk", ":::"+fbPostResult)	;	
											 /*  }
											});*/
						            				            	
						            }	
				            
				            
						            return content;
				        } catch (Exception e) {
				            e.printStackTrace();
				            return  null;
				        }  
				    	
		    	}else{

		    		 try {	        	
				            DefaultHttpClient httpClient = new DefaultHttpClient();	            
				            HttpPost requestLogin = new HttpPost(uri[0]);
				            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				            nameValuePairs.add(new BasicNameValuePair("PlaceId", uri[1]));		           
				            
				            requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				            
				            HttpResponse response = httpClient.execute(requestLogin);
				            
				            HttpEntity entity = response.getEntity();
				            String content = EntityUtils.toString(entity);			            
				            Log.e("Get Comment", "Comment : "+content);			            
				            InputStream stream = new ByteArrayInputStream(content.getBytes("UTF-8"));				            
				            getComments = SAXXMLParser.parseComment(stream);
				          //  Looper.prepare();                 
				            return content;
				        } catch (Exception e) {
				            e.printStackTrace();
				            return  null;
				        }  
		        
		    	}
		    }

		    @SuppressLint("InlinedApi")
			@Override
		    protected void onPostExecute(String result) {
		        super.onPostExecute(result);
		        //Do anything with response..
		        dialog.dismiss();
		        if(isRatingClick){
		        	Log.e("In   isRatingClick=true; ", "  isRatingClick=true;");
		        	
		        	appRateingBar.setRating((float)showRating);
		        	
		           }else if(isFacbookClick){		      
		        	   
		        		Log.e("In isFaceBook ", "  isFaceBook=true;");		        		
		        	    dialogMessage.setText(fbPostResult);
						okButton.setVisibility(View.GONE);
						cancelButton.setVisibility(View.VISIBLE);
						//okButton.setText("Continue");
						cancelButton.setText("Cancel");
						settingsDialog.show();
						/*Toast.makeText(getApplicationContext(), fbPostResult, Toast.LENGTH_LONG).show();
						Log.e("In isFaceBook ", "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");*/
		        	   
			           }else{
			        	   Log.e(" commentLayout.removeAllViews(); ", "   commentLayout.removeAllViews();");
		        	               commentLayout.removeAllViews();
		        	               
						           for(int i = 0; i < 3; i++) {						        	
									  ViewGroup parent=(ViewGroup) inflater.inflate(R.layout.showcommentlayout, null);
									  View rowView = inflater.inflate(R.layout.showcommentlayout, parent, false);				   
									   name = (TextView) rowView.findViewById(R.id.name);
									   time = (TextView) rowView.findViewById(R.id.time);
									   comment = (TextView) rowView.findViewById(R.id.comments);	
									  if(i<3){
										  try{
										  name.setText(getComments.get(i).getName());
										  time.setText(getComments.get(i).getTime());
										  comment.setText(getComments.get(i).getComment());				    
									    
									   // set some proper LayoutParams for the inflated View which is going to be added
									      // to the ll LinearLayout
									      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
									      // set some margins to distance the rows 
									      lp.topMargin = 2; 
									      lp.bottomMargin = 1; 					      
									      commentLayout.addView(rowView, lp);	
									      
									      Log.e("RRRRRRRRRRRR", "vvvv  "+isPostCommentclickShow+" , commentSuccess.equals(Success), "+commentSuccess.equals("Success"));
									      if(isPostCommentclickShow && commentSuccess.equals("0") ){
											  isPostCommentclickShow=false;
											  dialogMessage.setText("Comment posted Successfully");
											  okButton.setVisibility(View.GONE);
												//okButton.setText("Continue");
												cancelButton.setText("Ok");
												settingsDialog.show();
								            }
									      
									     
									      
										  }catch(Exception e){}
										   }else{
											   break;
										 }						  
									 
					   }
		        }	      
		    }
		}
    
 // Save FaceBook Credentials once user login.
    public boolean saveCredentials(Facebook facebook) {
    	Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}

 // Restore FaceBook Credentials of user login.
	public boolean restoreCredentials(Facebook facebook) {
    	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
    	return facebook.isSessionValid();
	}
    
    class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	getProfileInformation();
	    	//if (messageToPost != null){
			//postToWall(messageToPost);
	    	//	postToFrendsWall(userPreference.getString("FbUser",""));
	    		//postToUsersWall(fb, message, linkurl, caption, des, restName, imageurl)
	    	//postToUsersWall(facebook, userPreference.getString("FbUser",""), place.getImageUrl(), "", place.getDescription(), place.getName(), place.getImageUrl());
    		
		//}
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed!");
	        finish();
	    }
	    
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed!");
	        finish();
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled!");
	        finish();
	    }
	}

    private void showToast(String message){
    	  //Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    	    dialogMessage.setText(message);
    	    okButton.setVisibility(View.GONE);   
    	    cancelButton.setText("Ok");
    	    settingsDialog.show();
    	 }
	
	
	// Get the Profile information of the login user.
	public void getProfileInformation() {	

		mAsyncRunner.request("me", new RequestListener() {

            public void onComplete(String response, Object state) {
                Log.d("Profile", response);
                String json = response;
                try {
                    JSONObject profile = new JSONObject(json);                  
                    // getting name of the user
                    final String Username = profile.getString("name");
                    
                    Editor editor = userPreference.edit();
					 editor.putString("FbUser", Username);
					 editor.commit();                  

                    runOnUiThread(new Runnable() {

                        public void run() {
                           // Toast.makeText(getApplicationContext(), "Name: " + name + "\nEmail: " + email +"\nId: " + id, Toast.LENGTH_LONG).show();

                        }

                    });
                    
                  

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            public void onIOException(IOException e, Object state) {
            }

            public void onFileNotFoundException(FileNotFoundException e,
                    Object state) {
            }

            public void onMalformedURLException(MalformedURLException e,
                    Object state) {
            }

            public void onFacebookError(FacebookError e, Object state) {
            }
        });
		
		//return  fbUser;
    }	
	
	// Post message on the other's/given fb link 
	 protected String postToUsersWall(Facebook fb, String message, String linkurl, String caption, String des, String restName, String imageurl){
		 	try {	
		    	
		    	 if (restoreCredentials(fb)) {
		           
		    		   Bundle params = new Bundle();
			           params.putString("message", "User is "+ message);
			            //params.putString("link", "http://media-cdn.tripadvisor.com/media/photo-s/04/6c/90/34/1877.jpg"); 
			           // params.putString("link", imageurl);
			            params.putString("link", linkurl);
			            params.putString("caption", "              ");
			            params.putString("description", des);
			            params.putString("name", restName);
			           // params.putString("picture", imageurl);
			            params.putByteArray("picture", place.getPlaceImage());
			    
			            String response = facebook.request("me/feed", params, "POST");
		                Log.d("Tests....Value: ",response);
		                
		                JSONObject json;
	                    try {
	                        json = Util.parseJson(response);
	                        if(!json.isNull("id"))
	                        {
	                        	
	                           // Log.i("Facebook", "Informatinon share on fb");
	                        	    /*dialogMessage.setText("Informatinon share on fb");
									okButton.setVisibility(View.GONE);
									cancelButton.setText("Ok");
									settingsDialog.show();*/
									
									return "Informatinon share on fb";
	                        }
	                        else
	                        {
	                            //Log.e("Facebook","Error: " + response);
	                        	    
	                        	    /*dialogMessage.setText("Error: " + response);
									okButton.setVisibility(View.GONE);
									cancelButton.setText("Ok");
									settingsDialog.show();*/
									
									return ""+response;
	                        }
	                    } catch (FacebookError e) {
	                        Log.e("Facebook","Error: " + e.getMessage());	                        
	                        return  e.getMessage();
	                    }	                
		           
		                /*if (response == null || response.equals("") ||   response.equals("false")) {
		                  Log.v("Error", "Blank response");
		                 }*/
	                    
		              } else {		           
		            Log.d("Share DAta", "sessionNOTValid, relogin");
		        	
		            facebook.authorize(this, PERMISSIONS, new LoginDialogListener());
		            return "sessionNOTValid, relogin";
		        }
		    	 
		    }catch(Exception e){
		        e.printStackTrace();
		        
		        return e.getMessage();
		    }
		 	
		
			
		}
		
	
	 
	
	 public Bitmap getDecodeUrl(String url)
		{
	    	try
			{
			 Bitmap bitmap=null;
	         URL imageUrl = new URL(url);
	         System.out.println(imageUrl);
	         HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	         conn.setConnectTimeout(30000);
	         conn.setReadTimeout(30000);
	         conn.setInstanceFollowRedirects(true);
	         InputStream is=conn.getInputStream();
	         System.out.println(is);
//	         
			
	         BitmapFactory.Options o = new BitmapFactory.Options();
	         o.inJustDecodeBounds = true;
	       

	       
	         int sampleSize = calculateInSampleSize(o, CityListing.deviceWidth, CityListing.deviceHeight);
	         
	         System.out.println("scale city"+sampleSize);
	         o.inSampleSize = sampleSize;
	         o.inJustDecodeBounds = false;
	        
		      
	         
		       bitmap=BitmapFactory.decodeStream(is,null,o);
		      return bitmap;
			
			}
			catch(Exception e)
			{
				System.out.println("Error "+e);
				if(e!=null)
					System.out.println(e.getMessage());
				return null;
			}
			
		}	 
	 
 	 public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {		
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;
			
			    if (height > reqHeight || width > reqWidth) {
			
			        final int heightRatio = Math.round((float) height / (float) reqHeight);
			        final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			    }
			
			    return inSampleSize;
		}
// 	@Override
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
//	            	Intent in=new Intent(RestaurantDetail.this,HelperActivity.class);
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
	public void onFooterClick(View v)
	{
		Intent in=new Intent(RestaurantDetail.this,HelperActivity.class);
    	in.putExtra("receiptId", receiptId);
    	
    	startActivity(in);
		
	}
	public void setListnerToRootView(){
	    final View activityRootView = getWindow().getDecorView().findViewById(R.id.rdlayout); 
	    activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
	        public void onGlobalLayout() {

	            int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();
	            if (heightDiff > 100 ) { // 99% of the time the height diff will be due to a keyboard.
	               
	                
	            	// adView.setVisibility(View.GONE);
	                
	                if(isOpened == false){
	                    //Do two things, make the view top visible and the editText smaller
	                	 
	                }
	                isOpened = true;
	            }else if(isOpened == true){
	                
	            	//adView.setVisibility(View.VISIBLE);
	               
	                isOpened = false;
	            }
	         }
	    });
	}
	
//	@SuppressWarnings("deprecation")
//	public void showDialog()
//	{
//		AlertDialog.Builder builder=new AlertDialog.Builder(this);
//		
//		
//		AlertDialog dialog=builder.create();
//		dialog.setTitle("Uteliv");
//		dialog.setMessage("Buy paid version of Uteliv to enable these options and to remove Ads");
//		dialog.setButton2("Ok", new DialogInterface.OnClickListener() {
//			
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//				Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.uteliv.activity"));
//				startActivity(intent);
//				
//			}
//		});
//		dialog.setButton("Cancel", new DialogInterface.OnClickListener() {
//			
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				
//				
//				dialog.dismiss();
//			}
//		});
//		dialog.show();
//		
//		
//	}
}
   