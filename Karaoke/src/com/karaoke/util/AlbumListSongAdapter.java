package com.karaoke.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.DialogListener;
import com.karaoke.util.MySongAdapter.LoginDialogListener;

import com.zoom.karaoke.AlbumListSong;
import com.zoom.karaoke.R;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AlbumListSongAdapter extends ArrayAdapter<Song>{

	ArrayList<Song> albumList;
	ImageLoaderSong imageLoader;
	Context mcontext;
	ZoomKaraokeDatabaseHandler db;
	
	Facebook facebook;
	SharedPreferences pref;
	
	AsyncFacebookRunner mAsyncRunner;
	private static String APP_ID = "1427672070815095";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    String username;
	String songName;
	String songTumbUrl;
	ConnectionDetector detector;
	String string;
	
	int playlist_id;
	
	@SuppressWarnings("deprecation")
	public AlbumListSongAdapter(Context context, int textViewResourceId,
			ArrayList<Song> objects,String str) {
		super(context, textViewResourceId, objects);
		mcontext=context;
		albumList=objects;
		
		string=str;
		detector=new ConnectionDetector(mcontext);
		facebook=new Facebook(APP_ID);
		restoreCredentials(facebook);
		mAsyncRunner=new AsyncFacebookRunner(facebook);
		
		imageLoader=new ImageLoaderSong(mcontext.getApplicationContext());
		 db=new ZoomKaraokeDatabaseHandler(mcontext);
	}
	public View getView(final int position,View convertView,ViewGroup parent)
	{
		if(convertView==null)
			convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.availablesongcustom, null);
		
		/*TextView txtArtist=(TextView)convertView.findViewById(R.id.artistName);
		TextView txtSongName=(TextView)convertView.findViewById(R.id.songName);
		TextView txtbuy=(TextView)convertView.findViewById(R.id.buyNow);
		ImageView image=(ImageView)convertView.findViewById(R.id.image);
		txtbuy.setVisibility(View.GONE);
		
		final Song song=albumList.get(position);
		
		System.out.println(""+song.getArtistName());
		System.out.println(""+song.getAlbumName());
		txtArtist.setText(song.getArtistName());
	
		
		if(StaticVariables.editButtonCheck)
		{
			txtbuy.setBackgroundResource(R.drawable.remove_icon);
			txtbuy.setVisibility(View.VISIBLE);
		}
		else
		{
			txtbuy.setBackgroundResource(R.drawable.play_icon);
			txtbuy.setVisibility(View.VISIBLE);
		}
		String sdurl=song.getSDcardPath();
		System.out.println(sdurl);
		String fileName = sdurl.substring(sdurl.lastIndexOf('/') + 1);
		if(fileName.contains(".mp4"))
			fileName=fileName.replace(".mp4", "");
		image.setTag(""+song.getSongId());
		imageLoader.DisplayImage(""+song.getSongId(), (Activity)mcontext, image);
		txtSongName.setText(fileName);
		
		final int playlist_id=AlbumListSong.playList.getId();
		txtbuy.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				db.removeSongFromPlayList(playlist_id, song.getSongId());
				albumList.remove(position);
				
				AlbumListSongAdapter.this.notifyDataSetChanged();
			}
		});
		*/
		TextView txtArtist=(TextView)convertView.findViewById(R.id.artistName);
		TextView txtSongName=(TextView)convertView.findViewById(R.id.songName);
		TextView txtbuy = (TextView)convertView.findViewById(R.id.buyNow);
		ImageView image = (ImageView)convertView.findViewById(R.id.image);
		
		TextView trackCode=(TextView)convertView.findViewById(R.id.trackCode);
		txtbuy.setBackgroundResource(R.drawable.facebook_icon);
		
		RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)txtbuy.getLayoutParams();
		layoutParams.addRule(RelativeLayout.CENTER_VERTICAL); //ALIGN_PARENT_RIGHT / LEFT etc.
		txtbuy.setLayoutParams(layoutParams);
		
		final Song song=albumList.get(position);
		
		if(AlbumListSong.playList!=null)
		{
			 playlist_id=AlbumListSong.playList.getId();
		}
		System.out.println(""+song.getArtistName());
		System.out.println(""+song.getAlbumName());
		txtArtist.setText(song.getArtistName());
	
		trackCode.setText("Track code : "+song.getAlbumcode());
		
		songName=song.getSongName();
		songTumbUrl=song.getSongTumbnailUrl();
		
		
		String sdurl=song.getSDcardPath();
		System.out.println(sdurl);
		String fileName = sdurl.substring(sdurl.lastIndexOf('/') + 1);
		if(fileName.contains(".mp4"))
			fileName=fileName.replace(".mp4", "");
		image.setTag(""+song.getSongId());
		imageLoader.DisplayImage(""+song.getSongId(), (Activity)mcontext, image);
		
		
		
		txtSongName.setText(song.getSongName());
		
		if(string.equalsIgnoreCase("Edit"))
			txtbuy.setBackgroundResource(R.drawable.remove_icon);
		
		txtbuy.setOnClickListener(new OnClickListener() {
			
			@TargetApi(Build.VERSION_CODES.GINGERBREAD)
			@SuppressLint("NewApi")
			public void onClick(View v) {
				// TODO Auto-generated method stub
			
				if(string.equalsIgnoreCase("Edit"))
				{
					
					db.removeSongFromPlayList(playlist_id, song.getSongId());
					albumList.remove(position);
					
					AlbumListSongAdapter.this.notifyDataSetChanged();
					
					
				}
				else
				{
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
