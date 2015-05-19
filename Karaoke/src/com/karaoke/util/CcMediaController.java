package com.karaoke.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.Toast;

import com.zoom.karaoke.PlayScreen;
import com.zoom.karaoke.R;

public class CcMediaController extends MediaController {  
	   
    ImageButton mCCBtn;  
    Context mContext;  
    AlertDialog mLangDialog;  
    private Handler mhHandlerforward;
    int a=0;
    private Handler mhHandlerbackward;
    int saveCounter=-1;
    private boolean mDown;
    CcMediaController context;
   public static  MyButton backward,forward;
   public static Button reset,pause;
    public CcMediaController(Context context) {  
        super(new ContextThemeWrapper(context,R.style.MusicPlayer));  
        mContext = context;  
        
        mhHandlerforward =new  Handler();
        mhHandlerbackward=new Handler();
    }  
 
    @Override  
    public void setAnchorView(View view) {  
        super.setAnchorView(view);  
  
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(  
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  
        frameParams.gravity = Gravity.CENTER|Gravity.TOP;  
  
        View v = makeCCView();  
        addView(v, frameParams);  
  
    }  
    
    private View makeCCView() {  
    	View view=((Activity)mContext).getLayoutInflater().inflate(R.layout.controller, null);
  
    	
    	backward=(MyButton)view.findViewById(R.id.backward);
    	forward=(MyButton)view.findViewById(R.id.forward);
    	reset=(Button)view.findViewById(R.id.reload);
    	pause=(Button)view.findViewById(R.id.pause);
    	
    	backward.setOnClickListener(Listener);
    	forward.setOnClickListener(Listener);
    	reset.setOnClickListener(Listener);
    	pause.setOnClickListener(Listener);
    	
    	forward.setOnLongClickListener(LongListener);
    	backward.setOnLongClickListener(LongListener);
    	
    	context=this;
    	 forward.setSampleLongpress(null);
    	backward.setSampleLongpress(null);
    	
    	if(StaticVariables.flag)
		{
			reset.setBackgroundResource(R.drawable.reload_icon_h);
			
		}
		else
		{
			reset.setBackgroundResource(R.drawable.reload_icon);
			
		}
    	
    	
        return view;  
    }  
    OnClickListener Listener=new OnClickListener() {
		
		@SuppressLint("NewApi")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) 
			{
				case R.id.backward:
					
					
					Log.e("Position", ""+PlayScreen.video.getCurrentPosition());
					if(!((PlayScreen.video.getCurrentPosition()/1000)<4))
					{
						PlayScreen.video.seekTo(0);
					}
					else
					{
						String sdcardurl = null;
//						if(PlayScreen.type.equalsIgnoreCase("Song"))
//						{
							PlayScreen.counter--;
							if((PlayScreen.counter<0))
								PlayScreen.counter=PlayScreen.listSong.size()-1;
							Song song=PlayScreen.listSong.get(PlayScreen.counter);
							PlayScreen.songName.setText(song.getSongName());
							sdcardurl=song.getSDcardPath();
						
//						}
//						else
//						{
//							PlayScreen.counter--;
//							
//							if(PlayScreen.counter<0)
//								PlayScreen.counter=PlayScreen.listAbum.size()-1;
//							Song song=PlayScreen.listAbum.get(PlayScreen.counter);
//							sdcardurl=song.getSDcardPath();
//							String fileName = sdcardurl.substring(sdcardurl.lastIndexOf('/') + 1);
//							PlayScreen.songName.setText(fileName);
//							sdcardurl=song.getSDcardPath();
//						
//						}
						PlayScreen.video.setVideoPath(sdcardurl);
						PlayScreen.video.resume();
						PlayScreen.video.start();
						
						PlayScreen.video.seekTo(1);
						
					}
						
					
					
					break;
				case R.id.forward:
					String sdcardurl = null;
					
					Log.e("Play state first",""+ PlayScreen.video.isPlaying());
					Log.e("Hello", "Forwarddd ");
//					if(PlayScreen.type.equalsIgnoreCase("Song"))
//					{
						PlayScreen.counter++;
						Log.e((PlayScreen.listSong.size()-1)+" PlayScreen.counter  ",""+ PlayScreen.counter);
//						if(!(PlayScreen.counter<PlayScreen.listSong.size()))
//							PlayScreen.counter=0;
						
						if(PlayScreen.counter==PlayScreen.listSong.size())
							PlayScreen.counter=0;
						
						if(PlayScreen.counter==0)
						{
							Song song=PlayScreen.listSong.get(0);
							PlayScreen.songName.setText(song.getSongName());
							Log.e("sdcardurl", ""+song.getSDcardPath());
							sdcardurl=song.getSDcardPath();
						}
						else
						{
							Song song=PlayScreen.listSong.get(PlayScreen.counter);
							PlayScreen.songName.setText(song.getSongName());
							Log.e("sdcardurl", ""+song.getSDcardPath());
							sdcardurl=song.getSDcardPath();
						}
//					}
//					else
//					{
//						PlayScreen.counter++;
//						
//						if(!(PlayScreen.counter<PlayScreen.listAbum.size()-1))
//							PlayScreen.counter=0;
//						Song song=PlayScreen.listAbum.get(PlayScreen.counter);
//						sdcardurl=song.getSDcardPath();
//						String fileName = sdcardurl.substring(sdcardurl.lastIndexOf('/') + 1);
//						PlayScreen.songName.setText(fileName);
//						sdcardurl=song.getSDcardPath();
//					
//					}
					PlayScreen.video.setVideoPath(sdcardurl);
					PlayScreen.video.requestFocus();
					PlayScreen.video.resume();
					PlayScreen.video.start();
//					CcMediaController.this.hide();
//					CcMediaController.this.show(0);
					PlayScreen.video.seekTo(1);
					
					Log.e("Play state end",""+ PlayScreen.video.isPlaying());
					
					break;
				case R.id.pause:
					
					System.out.println("PlayScreen.video.canPause()"+PlayScreen.video.canPause());
					
					
					
					if(PlayScreen.video.canPause())
					{
						
						if(PlayScreen.video.isPlaying())
						{
							
//							PlayScreen.video.requestFocus();
							
							PlayScreen.video.pause();
							
						
						
//							a=PlayScreen.video.getCurrentPosition()/1000;
//							PlayScreen.video.refreshDrawableState();
							pause.setBackgroundResource(R.drawable.play_button_selector);
						}
						else
						{
							
//							final int topContainerId1 = getResources().getIdentifier("mediacontroller_progress", "id", "android");
//							final SeekBar seekbar = (SeekBar) CcMediaController.this.findViewById(topContainerId1);
							
//							PlayScreen.video.requestFocus();
//							PlayScreen.video.
							PlayScreen.video.start();
//							PlayScreen.video.
							CcMediaController.this.hide();
							CcMediaController.this.show(0);
							/*
							Log.e("current pos", ":::"+  PlayScreen.video.getCurrentPosition()
									);
							Log.e("current progress", ":::"+  seekbar.getMax()+",, "+ PlayScreen.video.getDuration()
									);
							seekbar.setMax(PlayScreen.video.getDuration());
							seekbar.setProgress(PlayScreen.video.getCurrentPosition());		*/
							
//							PlayScreen.video.refreshDrawableState();
							
							pause.setBackgroundResource(R.drawable.pause_button_selector);
						}
					}
					/*else
					{
						Log.e("index bbbbbbbbbbbbb  "+PlayScreen.counter," index "+PlayScreen.index); 
						
						Album album=PlayScreen.listAbum.get(PlayScreen.counter);
						
						PlayScreen.index = PlayScreen.counter;
						
						Log.e("changed valuessssss  "+PlayScreen.counter," index "+PlayScreen.index); 
						
						String sdcardurl=album.getAlbumsdPath();
						PlayScreen.video.setVideoPath(sdcardurl);
						PlayScreen.video.resume();
						PlayScreen.video.requestFocus();
						PlayScreen.video.start();
						
					}*/
					break;
				case R.id.reload:
					
					if(StaticVariables.flag)
					{
						reset.setBackgroundResource(R.drawable.reload_icon);
						StaticVariables.flag=false;
						
					}
					else
					{
						reset.setBackgroundResource(R.drawable.reload_icon_h);
						StaticVariables.flag=true;
					}
//					if(PlayScreen.type.equalsIgnoreCase("Song"))
//					{
//						
//						if(PlayScreen.counter<PlayScreen.listSong.size())
//						{
//							Song song=PlayScreen.listSong.get(PlayScreen.counter);
//							String sdcardurl=song.getSDcardPath();
//							PlayScreen.video.setVideoPath(sdcardurl);
//							PlayScreen.video.start();
//						}
//						else
//						{
//							Song song=PlayScreen.listSong.get(0);
//							String sdcardurl=song.getSDcardPath();
//							PlayScreen.video.setVideoPath(sdcardurl);
//							PlayScreen.video.start();
//							PlayScreen.counter=0;
//							
//						}
//					}
//					else
//					{
//						if(PlayScreen.counter<PlayScreen.listAbum.size())
//						{
//							Album album=PlayScreen.listAbum.get(PlayScreen.counter);
//							String sdcardurl=album.getAlbumsdPath();
//							PlayScreen.video.setVideoPath(sdcardurl);
//							PlayScreen.video.resume();
//							PlayScreen.video.requestFocus();
//							PlayScreen.video.start();
//						}
//						else
//						{
//							Album album=PlayScreen.listAbum.get(0);
//							String sdcardurl=album.getAlbumsdPath();
//							PlayScreen.video.setVideoPath(sdcardurl);
//							PlayScreen.video.resume();
//							PlayScreen.video.requestFocus();
//							PlayScreen.video.start();
//							
//							PlayScreen.counter=0;
//						}
//					}
//					PlayScreen.counter++;
					break;
	
				
			}
			
			
			
		}
	};
	OnLongClickListener LongListener=new OnLongClickListener() {
		
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) 
			{
				case R.id.backward:
					
					 backward.setSampleLongpress(context);
							System.out.println("Long Pressed");
							int time=PlayScreen.video.getCurrentPosition();
							time/=1000;
							time=time-10;
							time=time*1000;
							PlayScreen.video.seekTo(time);
							mDown=true;
							mhHandlerbackward.post(mRunnablebackward);
							 
					
					break;
				case R.id.forward:
					 forward.setSampleLongpress(context);
				    	
					mDown=true;
							int time1=PlayScreen.video.getCurrentPosition();
							time1/=1000;
							time1=time1+10;
							time1=time1*1000;
							PlayScreen.video.seekTo(time1);
							mhHandlerforward.post(mRunnable);
					
					break;
			
			}
			
			return true;
		}
	};
	final Runnable mRunnablebackward = new Runnable() {
        public void run() {
            if (mDown) {
//                Log.e("Tag ", "Tag");
                
                int time=PlayScreen.video.getCurrentPosition();
				time/=1000;
				time=time-10;
				time=time*1000;
				PlayScreen.video.seekTo(time);
                
                
                show(0);
                mhHandlerbackward.postDelayed(this, 200);
            }
            
        }
    };
	
	final Runnable mRunnable = new Runnable() {
        public void run() {
            if (mDown) {
//                Log.e("Tag ", "Tag");
                
                int time=PlayScreen.video.getCurrentPosition();
				time/=1000;
				time=time+10;
				time=time*1000;
				PlayScreen.video.seekTo(time);
                
                
                show(0);
                mhHandlerbackward.postDelayed(this, 200);
            }
            
        }
    };
	public void checkflag()
	{
		
		
	}
	public void showToast(String str)
	{
		Toast.makeText(mContext, str, 1000).show();
	}
	
	public void cancelLongpress() {        
		mDown=false;
		
//		forward.setSampleLongpress(null);
//		backward.setSampleLongpress(null);
		System.out.println("Remove");
		mhHandlerbackward.removeCallbacks(mRunnablebackward);
		mhHandlerforward.removeCallbacks(mRunnable);
	    } 
	 public void show(int i)
	 {
		super.show(0);
	 }
	 
}  
