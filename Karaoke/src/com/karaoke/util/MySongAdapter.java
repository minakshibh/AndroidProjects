package com.karaoke.util;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.karaoke.util.AvailableSongAdapter.DownloadFileFromURL;
import com.zoom.karaoke.FacebookShare;
import com.zoom.karaoke.MySongs;
import com.zoom.karaoke.R;



public class MySongAdapter extends ArrayAdapter<Song>
{

	Context mcontext;
	ArrayList<Song> availableSong;
	LayoutInflater inflator;
	ImageLoaderSong imageLoader;
	
	Facebook facebook;
	SharedPreferences pref;
	
	AsyncFacebookRunner mAsyncRunner;
	private static String APP_ID = "1427672070815095";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    ListView listSong;
    String sdurl;
    String username;
	String songName;
	String songTumbUrl;
	ConnectionDetector detector;
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public MySongAdapter(Context context, int resource, ArrayList<Song> objects) 
	{
		super(context, resource, objects);
		
		listSong=listSong;
		availableSong=objects;
		mcontext=context;
		inflator=((Activity)mcontext).getLayoutInflater();
		
		detector=new ConnectionDetector(mcontext);
		facebook=new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner=new AsyncFacebookRunner(facebook);
		
		imageLoader=new ImageLoaderSong(mcontext.getApplicationContext());
	}
	public View getView(final int position,View convertView,ViewGroup parent)
	{
		
		if(convertView==null)
			convertView=inflator.inflate(R.layout.availablesongcustom, null);
		
		TextView artist=(TextView)convertView.findViewById(R.id.artistName);
		TextView talbumName=(TextView)convertView.findViewById(R.id.songName);
		ImageView image=(ImageView)convertView.findViewById(R.id.image);
		TextView txtbuy=(TextView)convertView.findViewById(R.id.buyNow);
		TextView trackCode=(TextView)convertView.findViewById(R.id.trackCode);
		ImageView delete =(ImageView)convertView.findViewById(R.id.deleteSong);
		delete.setVisibility(View.VISIBLE);
		
		txtbuy.setBackgroundResource(R.drawable.facebook_icon);
		final Song song=availableSong.get(position);
		artist.setText(song.getArtistName());
		
		RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)txtbuy.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL); //ALIGN_PARENT_RIGHT / LEFT etc.
		txtbuy.setLayoutParams(layoutParams);
		
		
		trackCode.setText("Track code : "+song.getAlbumcode());
		
		image.setTag(""+song.getSongId());
		imageLoader.DisplayImage(""+song.getSongId(), (Activity)mcontext, image);
		
		
		sdurl=song.getSDcardPath();
		
		String fileName = sdurl.substring(sdurl.lastIndexOf('/') + 1);
		if(fileName.contains(".mp4"))
			fileName=fileName.replace(".mp4", "");
		
		talbumName.setText(song.getSongName());
		
		songName=song.getSongName();
		
		
		delete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				
				final AlertDialog.Builder alertdialog=new AlertDialog.Builder(mcontext);
				
				alertdialog.setTitle("Zoom-Karaoke");
				alertdialog.setMessage("Are you sure you want to delete ?");
				alertdialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
					
						Toast.makeText(mcontext, String.valueOf(position), 1).show();			
						ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
						db.openDataBase();
						db.deleteSong(availableSong.get(position).getSongId());
						db.close();
						File file = new File(availableSong.get(position).getSDcardPath());
						boolean deleted = file.delete();
						Toast.makeText(mcontext, "Song deleted", 1).show();						
						availableSong.remove(position);						
						MySongAdapter.this.notifyDataSetChanged();
					}
				});
				alertdialog.setNegativeButton("No", null);
				alertdialog.show();
			}
		});
		
		txtbuy.setOnClickListener(new OnClickListener() {
			
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@SuppressLint("NewApi")
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				songTumbUrl=song.getSongTumbnailUrl();
				Log.e("Url ", songTumbUrl);
				if(detector.isConnectingToInternet())
				{
					if(!facebook.isSessionValid())
	        		{
	        		
	        			loginAndPostToWall();
	        		}
					else
					{
						loginAndPostToWall();
					}
				}
				else
				{
					showDialog();
				}
				
			}
		});
		
		
		return convertView;
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressWarnings("deprecation")
	public void loginAndPostToWall(){
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		 facebook.authorize((Activity)mcontext, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	class LoginDialogListener implements DialogListener {
	    public void onComplete(Bundle values) {
	    	saveCredentials(facebook);
	    	
	    	postToUsersWall( facebook,"Nice app", "Good app", "Child app", "", "");
	    	
	    }
	    public void onFacebookError(FacebookError error) {
	    	showToast("Authentication with Facebook failed!");
//	        finish();
	    }
	    
	    public void onError(DialogError error) {
	    	showToast("Authentication with Facebook failed!");
//	        finish();
	    }
	    public void onCancel() {
	    	showToast("Authentication with Facebook cancelled!");
//	        finish();
	    }
	}
	protected String postToUsersWall(Facebook fb, String message, String linkurl, String caption, String des, String restName){
	 	try {	
	    	
	    	 if (restoreCredentials(fb)) {
	           
	    		   Bundle params = new Bundle();
		           params.putString("message", "Hello Everybody");
//		           linkurl="http://i883.photobucket.com/albums/ac35/Juni01star/launcherlogo12345_zpsf8934089.png";
		            //params.putString("link", "http://media-cdn.tripadvisor.com/media/photo-s/04/6c/90/34/1877.jpg"); 
		           // params.putString("link", imageurl);
//		            params.putString("link", "https://play.google.com/store/apps/details?id=my.fishinggameapp&hl=en");
		            params.putString("caption", "it's Awesome. You can check it out in ZoomKaraoke app.");
		            params.putString("description"," I've been listening to this song: "+ songName);
		            params.putString("name", username);
		            
		            Log.e("Song url", songTumbUrl);
		            params.putString("picture", songTumbUrl);
//		            params.putByteArray("picture", image);
		    
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
	        	
	            facebook.authorize((Activity)mcontext, PERMISSIONS, new LoginDialogListener());
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
                    
                 
							 try {
								username = profile.getString("name");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						
                  
                 

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
	
	public boolean saveCredentials(Facebook facebook) 
	{
	   	Editor editor = mcontext.getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
	   	editor.putString(TOKEN, facebook.getAccessToken());
	   	editor.putLong(EXPIRES, facebook.getAccessExpires());
	   	return editor.commit();
	}
	// Restore FaceBook Credentials of user login.
		@SuppressWarnings("deprecation")
		public boolean restoreCredentials(Facebook facebook) {
	    	SharedPreferences sharedPreferences = mcontext.getSharedPreferences(KEY, Context.MODE_PRIVATE);
	    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
	    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
	    	return facebook.isSessionValid();
		}
	
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	@SuppressWarnings("deprecation")
	public void showDialog()
	{
		final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
		System.out.println("check connection");
		alertdialog.setTitle("Zoom-Karaoke");
		alertdialog.setMessage("Internet connection not available");
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
			}
		});
		alertdialog.show();
	}
}