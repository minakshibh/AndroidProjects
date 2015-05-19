package com.zoom.karaoke;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import com.karaoke.util.ConnectionDetector;
import com.zoom.karaoke.R;

public class FacebookShare extends Activity {

	Button btnLogin,btnShare,btnFriendList;
	ConnectionDetector connectionDetector;
	Facebook facebook;
	SharedPreferences pref;
	TextView txtuser;
	ArrayList<String> friendlist=new ArrayList<String>();
	AsyncFacebookRunner mAsyncRunner;
	private static String APP_ID = "273872786121666";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    String username;
    Context mcontext;
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albumlistsong);
		
		
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		mcontext=this;
		connectionDetector=new ConnectionDetector(this);
		facebook=new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner=new AsyncFacebookRunner(facebook);
		
		
		pref=getSharedPreferences("pref", MODE_PRIVATE);
		
	            		if(!facebook.isSessionValid())
	            		{
	            		
	            			loginAndPostToWall();
	            		}
	            		else
	            			getProfileInformation();
	        		
			
				
	
		
		
	}
	public void loginAndPostToWall(){
		 facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	
	    	postToUsersWall( facebook,"Nice app", "Good app", "Child app", "", "");
	    	
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
	protected String postToUsersWall(Facebook fb, String message, String linkurl, String caption, String des, String restName){
	 	try {	
	    	
	    	 if (restoreCredentials(fb)) {
	           
	    		   Bundle params = new Bundle();
		           params.putString("message", "Aqua Logic");
		           linkurl="http://i883.photobucket.com/albums/ac35/Juni01star/launcherlogo12345_zpsf8934089.png";
		            //params.putString("link", "http://media-cdn.tripadvisor.com/media/photo-s/04/6c/90/34/1877.jpg"); 
		           // params.putString("link", imageurl);
		            params.putString("link", "https://play.google.com/store/apps/details?id=my.fishinggameapp&hl=en");
		            params.putString("caption", "              ");
		            params.putString("description", "Description");
		            params.putString("name", username);
		            params.putString("picture", linkurl);
		         //   params.putByteArray("picture", image);
		    
		            String response = facebook.request("me/feed", params, "POST");
	                Log.d("Tests....Value: ",response);
	                
	                JSONObject json;
                    try {
                        json = Util.parseJson(response);
                        if(!json.isNull("id"))
                        {
                        	showToast("Your status has been updated succesfully");
                            Log.i("Facebook", "Informatinon share on fb");
                        	    
								
								return "Informatinon share on fb";
                        }
                        else
                        {
                            
								
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
	@SuppressWarnings("deprecation")
	public String getProfileInformation() 
	{	
		 
		mAsyncRunner.request("me", new RequestListener() {

            public void onComplete(String response, Object state) {
                Log.d("Profile", response);
                String json = response;
                try {
                    final JSONObject profile = new JSONObject(json);                  
                    // getting name of the user
                     
                    runOnUiThread(new Runnable() {
						
						public void run() {
							// TODO Auto-generated method stub
							 try {
								username = profile.getString("name");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 txtuser.setVisibility(View.VISIBLE);
							 btnFriendList.setVisibility(View.VISIBLE);
							 btnShare.setVisibility(View.VISIBLE);
							 
			                    txtuser.setText("Welcome "+username);
			                     System.out.println("Username "+username);
			                     btnLogin.setVisibility(View.INVISIBLE);
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
		return  username;
		
    }
	
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
	
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	private void sendRequestDialog() 
	{
	    Bundle params = new Bundle();
	    
	  
	   
	   String target_url="https://apps.facebook.com/nviteriendemo/?fb_source=notification&/?request_ids=[100001983802796]"+
	    	    "&ref=notif&fb_source=notification"+
	    	    "&app_request_type=user_to_user";
	   params.putString("title", "invite friends");
       params.putString("message", "come join us");
//	   String url="https://play.google.com/store/apps/details?id=my.fishinggameapp";
	    params.putString("message", "top.location.href='http://www.facebook.com/123456852karaoke?sk=273872786121666'");
//	    params.putString("name", "An example parameter");
//	    params.putString("link", url);
//	    params.putString("action_type", "send");
//	    params.putString("to", "100001983802796");
//	   params.putString("object_id", "%7B%22app_id%22%3A193048140809145%2C%22type%22%3A%22%5C%22object%5C%22%22%2C%22url%22%3A%22%5C%22http%3A%5C%2F%5C%2Fsamples.ogp.me%5C%2F226075010839791%5C%22%22%2C%22title%22%3A%22%5C%22Sample+Object%5C%22%22%2C%22image%22%3A%22%5C%22https%3A%5C%2F%5C%2Fs-static.ak.fbcdn.net%5C%2Fimages%5C%2Fdevsite%5C%2Fattachment_blank.png%5C%22%22%2C%22description%22%3A%22%5C%22%5C%22%22%7D");
	    params.putString("data", "join my app");
	    
	    
	    @SuppressWarnings("deprecation")
		WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(mcontext,
	            facebook.getSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {

	            	
					public void onComplete(Bundle values,
							com.facebook.FacebookException error) {
						Log.e("Date ", ""+facebook.getSession().getExpirationDate());
						// TODO Auto-generated method stub
						Log.e("Bundle ", ""+values);
						Log.e("Error ",""+ error);
					}

	            })
	            .build();
	    requestsDialog.show();
	}
}

