package com.zoom.karaoke;

import java.lang.reflect.Field;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.karaoke.util.Album;
import com.karaoke.util.CcMediaController;
import com.karaoke.util.CustomVideoView;
import com.karaoke.util.PlayList;
import com.karaoke.util.Song;
import com.karaoke.util.StaticVariables;
import com.karaoke.util.ZoomKaraokeDatabase;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.karaoke.R;
public class PlayScreen extends Activity{

	//static MediaPlayer mp;
//	Button reload;
	Song song;
	Song album;
	String name;
	CcMediaController mc;
	String sdurl;
//	public static String type="Song";
	Context mcontext;
	RelativeLayout header;
	public static TextView songName;
	TextView txt,albumName;
	public static ArrayList<Song> listSong=new ArrayList<Song>();
	 int h ,w;
	 public static int index;
	 
	 public static int counter=0;
	 ImageButton back;
	 RelativeLayout pbackground;
	public static VideoView video=null;
	int count;
	private PlayList playList;
	
	@SuppressLint("NewApi")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playscreen);
		
		txt=(TextView)findViewById(R.id.zoom);
		back=(ImageButton)findViewById(R.id.back);
		songName=(TextView)findViewById(R.id.songName);
		albumName=(TextView)findViewById(R.id.albumName);
		header=(RelativeLayout)findViewById(R.id.header);
		mcontext=this;
		pbackground=(RelativeLayout)findViewById(R.id.playerbackground);
		
		Intent in=getIntent();
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        h = displaymetrics.heightPixels;
         w = displaymetrics.widthPixels;
		
		count = in.getIntExtra("count", 0);
		
		video=(VideoView)findViewById(R.id.video);
		if(in.hasExtra("Album"))
		{ 
			album=(Song)in.getSerializableExtra("ob");
			name=album.getAlbumName();
			albumName.setText(name);
			
			index=in.getIntExtra("songno", 0);
			
//			Log.e("sdpath", sdurl);
			
			counter=index;
//			listAbum=db.getAlbumSongs(album.getAlbumId());
			@SuppressWarnings("unchecked")
			ArrayList<Song> arrayList = (ArrayList<Song>)in.getSerializableExtra("songlist");
			listSong = arrayList;
			sdurl=listSong.get(index).getSDcardPath();
			
//			String fileName = sdurl.substring(sdurl.lastIndexOf('/') + 1);
			
			songName.setText(listSong.get(index).getSongName());
			
		}else if(in.hasExtra("PlayList")){
			playList = (PlayList)in.getSerializableExtra("ob");
			name = playList.getPlayListName();
			albumName.setText(name);
			ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
			index=in.getIntExtra("songno", 0);
			counter = index;
			
			@SuppressWarnings("unchecked")
			ArrayList<Song> arrayList = db.getPlayListSongs(playList.getId());
			listSong = arrayList;
			sdurl = listSong.get(index).getSDcardPath();
			
			Log.e("Name ",""+listSong.get(index).getSongName());
			
			songName.setText(listSong.get(index).getSongName());
		}else
		{
			song=(Song)in.getSerializableExtra("ob");
			name=song.getSongName();
			sdurl=song.getSDcardPath();
			
			index=in.getIntExtra("songno", 0);
			counter=index;
			@SuppressWarnings("unchecked")
			ArrayList<Song> arrayList = (ArrayList<Song>)in.getSerializableExtra("songlist");
			listSong = arrayList;
			
			songName.setText(name);			
		}
		
		StaticVariables.flag=false;
		
		
        
         Log.e("Sd Path", ""+sdurl);
		
		 mc = new CcMediaController(this);
		mc.setEnabled(true);
//		mc.hide();
		video.setVideoPath(sdurl);
		 
		mc.setAnchorView(video);

		
		
		
//		video.setZOrderMediaOverlay(true);
		video.setMediaController(mc);
//		video.requestFocus();
		
//		video.resume();
		video.start();
		video.seekTo(1);
		video.setOnCompletionListener(completeListener);
//		video.setOnErrorListener(new OnErrorListener() {
//			  public boolean onError(MediaPlayer mp, int what, int extra) {
//			     return true;
//			  }
//			});
		back.setOnClickListener(Listener);
		
	}
	
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
//	

	  
	  @SuppressLint("NewApi")
	@Override
	  public void onConfigurationChanged (Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);

	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
		  {

	    	txt.setVisibility(View.GONE);
	    	songName.setVisibility(View.GONE);
	    	System.out.println("Width "+w+"  height "+h);
	    	int left = video.getLeft();
            int top = video.getTop();
            int right = left + (w );
            int botton = top+h ;
            header.setVisibility(View.GONE);
            video.layout(left, top, right, botton);
			  System.out.println(video.getLeft()+" "+video.getTop()+" "+video.getBottom()+" "+video.getRight());
			  
		  }
		  else
		  {
			  pbackground.setBackgroundResource(R.drawable.player_bg);
			  txt.setVisibility(View.VISIBLE);
			  songName.setVisibility(View.VISIBLE);
			  int left = video.getLeft();
	            int top = video.getTop();
	            int right = left + (w /2);
	            int botton = top + (h/2 );
	            video.layout(left, top, right, botton);
	            header.setVisibility(View.VISIBLE);
	            System.out.println(video.getLeft()+" "+video.getTop()+" "+video.getBottom()+" "+video.getRight());
		  }
	  }
	  OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.back:
				finish();
				break;

			
			}
		}
	};
	OnCompletionListener completeListener=new OnCompletionListener() {
		
		public void onCompletion(MediaPlayer mp) {			

			video.seekTo(0);
			CcMediaController.pause.setBackgroundResource(R.drawable.play_button_selector);
			
			Log.e("index aaaaaaaaaaaaaa  "+counter+" "+index," List size "+listSong.size()); 
//
//			if(type.equalsIgnoreCase("Song"))
//			{
				if(counter<listSong.size()-1)
					counter++;
				else
					counter = 0;
				
				if(counter!=index || StaticVariables.flag){
					CcMediaController.pause.setBackgroundResource(R.drawable.pause_button_selector);
					Song song=listSong.get(counter);
					songName.setText(song.getSongName());
					String sdcardurl=song.getSDcardPath();
					video.setVideoPath(sdcardurl);
					video.requestFocus();
					video.resume();
					video.start();
				}else{
					counter--;
					
					if(counter<0)
						counter=0;
					index = counter;
					
					Log.e("index ","::: "+index);
					Log.e("counter", "::: "+counter);
				}
			/*}
			else
			{
				if(counter<listSong.size()-1)
					counter++;
				else
					counter = 0;
				
				if(counter!=index || StaticVariables.flag){
					CcMediaController.pause.setBackgroundResource(R.drawable.pause_button_selector);
					
					Song album=listSong.get(counter);
					String sdcardurl=album.getSDcardPath();
					String fileName = sdcardurl.substring(sdcardurl.lastIndexOf('/') + 1);
					
					songName.setText(fileName);
					
					video.setVideoPath(sdcardurl);
					
					video.requestFocus();
					video.resume();
					video.start();
				}else{
					counter--;
					index = counter;
					
					Log.e("index ","::: "+index);
					Log.e("counter", "::: "+counter);
				}
			}*/
		}
	};

	
	  
}
