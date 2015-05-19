package com.zoom.karaoke;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;
import com.karaoke.util.Album;
import com.karaoke.util.ConnectionDetector;
import com.karaoke.util.MyAlbumsAdapter;
import com.karaoke.util.MySongAdapter;
import com.karaoke.util.PlayList;
import com.karaoke.util.Song;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.authentication.LoginActivity;
import com.zoom.karaoke.R;


public class MySongs extends Activity{

	
	static ListView listSong;
	Button songs,albums,playlist;
	ImageButton back;
	static Context mcontext;
	ArrayList<Album> albumlist=new ArrayList<Album>();
	static ArrayList<Song> songlist=new ArrayList<Song>();
	ArrayList<PlayList> playlists=new ArrayList<PlayList>();
	private ProgressDialog progress;
	ZoomKaraokeDatabaseHandler db;
	private ImageButton addBtn;

	String UserName;
	private static String APP_ID = "273872786121666";
	private static final String[] PERMISSIONS = new String[] {"publish_stream"};
	private static final String TOKEN = "access_token";
    private static final String EXPIRES = "expires_in";
    private static final String KEY = "facebook-credentials";
    
    AsyncFacebookRunner mAsyncRunner = null;	
    SharedPreferences pref,pref1;
	Facebook facebook;
	
	ImageButton inviteBtn;
	private boolean isCreating= false;
	ConnectionDetector detector;
	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mysong1);
		
		listSong = (ListView)findViewById(R.id.listSong);
		songs = (Button)findViewById(R.id.songs);
		albums = (Button)findViewById(R.id.albums);
		playlist = (Button)findViewById(R.id.playList);
		back = (ImageButton)findViewById(R.id.back);
		addBtn = (ImageButton)findViewById(R.id.addBtn);
		addBtn.setVisibility(View.GONE);
		
		inviteBtn=(ImageButton)findViewById(R.id.inviteFb);
		mcontext=this;
		
		db=new ZoomKaraokeDatabaseHandler(mcontext);
		songlist=db.getAllSong();
		
		
		
		
		albumlist=db.getAllAlbums();
		
		playlists = db.getAllPlaylists();
			
		
		detector=new ConnectionDetector(mcontext);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		facebook=new Facebook(APP_ID);
		
		pref1=getSharedPreferences("mypref", MODE_PRIVATE);
		mAsyncRunner=new AsyncFacebookRunner(facebook);
		Log.e("Valid ", ""+restoreCredentials(facebook));
	
		
//		Song song=availableSong.get(position);
//		tartist.setText(song.getArtistName());
//		
//		image.setTag(song.getSongTumbnailUrl());
//		imageLoader.DisplayImage(song.getSongTumbnailUrl(), (Activity)mcontext, image);
//		talbumName.setText(song.getAlbumName());
		
//		for(int i=0;i<albumlist.size();i++)
//		{
//			
//			Song song=new Song();
//			song.setArtistName(artistName)
//			
//		}
		System.out.println("songlist  "+songlist);
		System.out.println("album  "+albumlist.size());
		
//		for(int i=0;i<albumlist.size();i++)
//		{
//			Album album=albumlist.get(i);
//			System.out.println(album.getThumbnailUrl()+" "+album.getAlbumId()+" "+album.getAlbumsdPath()+" "+album.getSongs());
//		}
		
		songs.setOnClickListener(Listener);   
		albums.setOnClickListener(Listener);
		playlist.setOnClickListener(Listener);
		back.setOnClickListener(Listener);
		addBtn.setOnClickListener(Listener);
		inviteBtn.setOnClickListener(Listener);
		MySongAdapter adapter=new MySongAdapter(this, android.R.layout.simple_list_item_1, songlist);
		listSong.setTag("Song");
		listSong.setAdapter(adapter);
		
		listSong.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(listSong.getTag().toString().equalsIgnoreCase("Song"))
				{
					showToast("Song");
					System.out.println(songlist.get(arg2));
					System.out.println(""+songlist.get(arg2).getSDcardPath());
					Intent in=new Intent(MySongs.this,PlayScreen.class);
					in.putExtra("songno", arg2);
					in.putExtra("ob", songlist.get(arg2));
					in.putExtra("count", songlist.size());
					in.putExtra("songlist", songlist);
					
					startActivity(in);
				}
				else if(listSong.getTag().toString().equalsIgnoreCase("Album"))
				{
					Intent in=new Intent(MySongs.this,AlbumListSong.class);
					in.putExtra("Album", "Album");
					in.putExtra("AlbumId", albumlist.get(arg2).getAlbumId());
					in.putExtra("albumname", albumlist.get(arg2).getAlbumName());
					in.putExtra("ob", albumlist.get(arg2));
					in.putExtra("songlist", albumlist);					
					startActivity(in);
				}
				else if(listSong.getTag().toString().equalsIgnoreCase("PlayList"))
				{
					Intent intent = new Intent(MySongs.this, AlbumListSong.class);
					intent.putExtra("trigger", "playlist");					
					intent.putExtra("playList", playlists.get(arg2));
					startActivity(intent);
				}
			}
		});
	}
	
	OnClickListener Listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int whitecolorcode=getResources().getColor(R.color.white);
			int blackcolorcode=getResources().getColor(R.color.black);
			
			switch (v.getId()) 
			{
			
			case R.id.songs:
				addBtn.setVisibility(View.GONE);
				songs.setBackgroundResource(R.drawable.tw3_left);
				songs.setTextColor(blackcolorcode);
				
				albums.setBackgroundResource(R.drawable.tb3_center);
				albums.setTextColor(whitecolorcode);
				
				playlist.setBackgroundResource(R.drawable.tb3_right);
				playlist.setTextColor(whitecolorcode);
				
				
				setAdapterForSongs();
				listSong.setTag("Song");
			
				
				
				break;
			case R.id.albums:
				addBtn.setVisibility(View.GONE);
				songs.setBackgroundResource(R.drawable.tb3_left);
				songs.setTextColor(whitecolorcode);
				
				albums.setBackgroundResource(R.drawable.tw3_center);
				albums.setTextColor(blackcolorcode);
				
				playlist.setBackgroundResource(R.drawable.tb3_right);
				playlist.setTextColor(whitecolorcode);
				
				MyAlbumsAdapter adapter1=new MyAlbumsAdapter(mcontext, android.R.layout.simple_list_item_1, albumlist);
				
				listSong.setAdapter(adapter1);
				listSong.setTag("Album");
				
				break;
			
			case R.id.playList:
				addBtn.setVisibility(View.VISIBLE);
				songs.setBackgroundResource(R.drawable.tb3_left);
				songs.setTextColor(whitecolorcode);
				
				albums.setBackgroundResource(R.drawable.tb3_center);
				albums.setTextColor(whitecolorcode);
				
				playlist.setBackgroundResource(R.drawable.tw3_right);
				playlist.setTextColor(blackcolorcode);
				
				listSong.setTag("PlayList");
				
//				ArrayList<PlayList> playlist = new ArrayList<PlayList>();
				
				MyPlayListAdapter adapter2=new MyPlayListAdapter(mcontext, android.R.layout.simple_list_item_1, playlists);
				listSong.setAdapter(adapter2);
				
				break;
			case R.id.back:
				Intent intent=new Intent(MySongs.this,AvailableSong.class);
				startActivity(intent);
				finish();
				
				break;
			
			case R.id.inviteFb:
				
				Log.e("", ""+facebook.isSessionValid());
				if(detector.isConnectingToInternet())
				{
					if(!facebook.isSessionValid())
	        		{
	        		
						loginAndPostToWall();
	        		}
					else
					{
						
						sendRequestDialog();
					}
				}
				else
				{
					showDialog("Internet connection not available");
				}
				
				break;
			case R.id.addBtn:
				final Dialog alert = new Dialog(MySongs.this);
				alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
				
				alert.setContentView(R.layout.create_playlist_dialog);
				
				final EditText txtPlaylistName = (EditText)alert.findViewById(R.id.txtPlaylistName);
				Button btnOk = (Button)alert.findViewById(R.id.btnOk);
				Button btnCancel = (Button)alert.findViewById(R.id.btnCancel);
				
				alert.show();
				
				btnOk.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if(txtPlaylistName.getText().toString().trim().equals("")){
							Toast.makeText(MySongs.this, "Please enter playlist name", Toast.LENGTH_LONG).show();
						}else{
							alert.dismiss();
							isCreating = true;
							
							new createPlaylist().execute(txtPlaylistName.getText().toString().trim(), "add");
							
							Toast.makeText(MySongs.this, "Playlist created successfully.", Toast.LENGTH_LONG).show();
						}
					}
				});
				
				btnCancel.setOnClickListener(new View.OnClickListener() {
					
					public void onClick(View v) {
					alert.dismiss();
					}
				});
			}
		}
	};
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	
	public static void setAdapterForSongs() {
		MySongAdapter adapter=new MySongAdapter(mcontext, android.R.layout.simple_list_item_1, songlist);
		
		listSong.setAdapter(adapter);
	}

	protected void setCustomAdapter() {
		// TODO Auto-generated method stub
		
	}

	public class createPlaylist extends AsyncTask<String, String, String>{
	   
		
		@Override
	    protected String doInBackground(String... str) {
			ZoomKaraokeDatabaseHandler database=new ZoomKaraokeDatabaseHandler(MySongs.this);
			database.openDataBase();
			
			if(isCreating){
				database.createPlayList(str[0]);
			}else{
				database.removePlaylist(str[0]);
			}
			
	    	return "success";
	    	
	    }
	    
	    @Override
	    protected void onPreExecute() {
	    	if(isCreating)
	        	progress = ProgressDialog.show(MySongs.this, "Creating Playlist", "Please wait...");
	    	else
	    		progress = ProgressDialog.show(MySongs.this, "Deleting Playlist", "Please wait...");
	    }
	    
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);

	        playlists = db.getAllPlaylists();
	        MyPlayListAdapter adapter2=new MyPlayListAdapter(mcontext, android.R.layout.simple_list_item_1, playlists);
			listSong.setAdapter(adapter2);
	        progress.dismiss();
	    }
	}
	
	public class MyPlayListAdapter extends ArrayAdapter<PlayList>
	{

		Context mcontext;
		ArrayList<PlayList> playList;
		LayoutInflater inflator;
		private TextView playListName;
		private ImageView deletePlayList;
		
		public MyPlayListAdapter(Context context, int resource, ArrayList<PlayList> playList2) 
		{
			super(context, resource, playList2);
			
			this.playList=playList2;
			mcontext=context;
			inflator=((Activity)mcontext).getLayoutInflater();
		}
		public View getView(int position,View convertView,ViewGroup parent)
		{
			final int pos = position;
			
			if(convertView==null)
				convertView=inflator.inflate(R.layout.playlistsongcustom, null);
			
			playListName = (TextView)convertView.findViewById(R.id.playListName);
			deletePlayList = (ImageView)convertView.findViewById(R.id.delete);
			
			playListName.setText(playList.get(position).getPlayListName());
			
			deletePlayList.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View v) {
					AlertDialog.Builder alert = new AlertDialog.Builder(mcontext);
					alert.setTitle("Delete this playlist : "+playList.get(pos).getPlayListName());
					alert.setMessage("Are you sure?");
					alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							isCreating =false;
							new createPlaylist().execute(String.valueOf(playList.get(pos).getId()), "remove");
						}
					});
					alert.setNegativeButton("Cancel", null);
					alert.show();
				}
			});
			return convertView;
		}
	}
	@SuppressWarnings("deprecation")
	public void loginAndPostToWall(){
		 facebook.authorize(this, PERMISSIONS, Facebook.FORCE_DIALOG_AUTH, new LoginDialogListener());
	}
	public boolean saveCredentials(Facebook facebook) {
    	Editor editor = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE).edit();
    	editor.putString(TOKEN, facebook.getAccessToken());
    	editor.putLong(EXPIRES, facebook.getAccessExpires());
    	return editor.commit();
	}
	// Restore FaceBook Credentials of user login.
		@SuppressWarnings("deprecation")
		public boolean restoreCredentials(Facebook facebook) {
	    	SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(KEY, Context.MODE_PRIVATE);
	    	facebook.setAccessToken(sharedPreferences.getString(TOKEN, null));
	    	facebook.setAccessExpires(sharedPreferences.getLong(EXPIRES, 0));
	    	facebook.getSession();
	    	return facebook.isSessionValid();
		}
	    
	    class LoginDialogListener implements DialogListener {
		    public void onComplete(Bundle values) {
		    	saveCredentials(facebook);
		    	
		    	
		    	sendRequestDialog();
		    	
		    	
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
	
	@SuppressWarnings("deprecation")
	public String getProfileInformation() 
	{	
		 
		mAsyncRunner.request("me", new RequestListener() {

            public void onComplete(String response, Object state) {
                Log.d("Profile", response);
                String json = response;
                try {
                    JSONObject profile = new JSONObject(json);                  
                    // getting name of the user
                     UserName = profile.getString("name");
                    Log.e("UserName ", UserName);
                     
                  

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
		return  UserName;
		
    }
	private void sendRequestDialog() {
	    Bundle params = new Bundle();
	    params.putString("message", "Learn how to make your Android apps social");
	    params.putString("title", "Send a Request");
	    params.putString("app_id",APP_ID);
//	    params.putString("action_type", "send");
//	    params.putString("data", "https://play.google.com/store/apps/details?id=my.signatureapp");
//	    params.putString("object_id", "611373128891349");     //page id 611373128891349
	    @SuppressWarnings("deprecation")
		WebDialog requestsDialog = 
		(
	        new WebDialog.RequestsDialogBuilder(mcontext,
	            facebook.getSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener() {

	            	
					public void onComplete(Bundle values,
							com.facebook.FacebookException error) {
						if(error==null)
							showDialog("Invitation is succesfully send");
						else
						{
							showDialog(error.getMessage());
						}
					}

	            })
	            .build();
	    requestsDialog.show();
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
	
	public void onBackPressed() {
		
		Intent intent=new Intent(MySongs.this,AvailableSong.class);
		startActivity(intent);
		finish();
			
		
	}

}