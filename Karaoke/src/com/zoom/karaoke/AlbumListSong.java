package com.zoom.karaoke;

import java.util.ArrayList;

import com.karaoke.util.Album;
import com.karaoke.util.AlbumListSongAdapter;
import com.karaoke.util.PlayList;
import com.karaoke.util.Song;
import com.karaoke.util.StaticVariables;
import com.karaoke.util.ZoomKaraokeDatabase;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.karaoke.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumListSong extends Activity{

	ListView listView;
	TextView albumName;
	Intent in;
	int Album_id;
	Context mcontext;
	ArrayList<Song> songList;
	ImageButton back;
	private ImageView btnAdd, btnEdit;
	private String trigger;
	public static PlayList playList;
	
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.albumlistsong);
		
		mcontext=this;
		
		listView=(ListView)findViewById(R.id.listSong);
		back=(ImageButton)findViewById(R.id.back);
		albumName=(TextView)findViewById(R.id.albumName);
		btnAdd = (ImageView)findViewById(R.id.btnAdd);
		btnEdit = (ImageView)findViewById(R.id.btnEdit);
		
//		StaticVariables.editButtonCheck=false;
	
		btnEdit.setOnClickListener(Listener);
		
		in=getIntent();
		trigger = in.getStringExtra("trigger");

		Log.e("trigger", ""+trigger);
		if(in.hasExtra("albumname"))
		{
			String albumname=in.getStringExtra("albumname");
			albumName.setText(albumname);
		}
		ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
		
		if(trigger==null){
			btnAdd.setVisibility(View.GONE);
			btnEdit.setVisibility(View.GONE);
			
			Album_id=in.getIntExtra("AlbumId", 0);		
			if(in.hasExtra("albumname"))
			{
				String albumname=in.getStringExtra("albumname");
				albumName.setText(albumname);
			}
			
			songList=db.getAlbumSongs(Album_id);
		}else if(trigger.equals("playlist")){	
			
			btnAdd.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					
					
					if(btnAdd.getTag()==null)
					{
						Intent intent = new Intent(AlbumListSong.this, AddSongsToPlaylist.class);
						intent.putExtra("playList", playList);
						startActivity(intent);
					}
					else
					{
						ZoomKaraokeDatabaseHandler database=new ZoomKaraokeDatabaseHandler(mcontext);
						
						btnAdd.setBackgroundResource(R.drawable.add_btn);
						btnAdd.setTag(null);
						btnEdit.setBackgroundResource(R.drawable.edit_icon);
						btnEdit.setVisibility(View.VISIBLE);
						songList=database.getPlayListSongs(playList.getId());
						AlbumListSongAdapter adapter=new AlbumListSongAdapter(mcontext, android.R.layout.simple_list_item_1, songList,"");
						listView.setAdapter(adapter);
					}
					
				}
			});
			
			playList = (PlayList)getIntent().getSerializableExtra("playList");
			albumName.setText(playList.getPlayListName());
			
			songList=db.getPlayListSongs(playList.getId());
		}
		
		AlbumListSongAdapter adapter=new AlbumListSongAdapter(mcontext, android.R.layout.simple_list_item_1, songList,"");
		listView.setAdapter(adapter);
		
		back.setOnClickListener(Listener);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(btnAdd.getTag()==null)
				{
					if(trigger!=null)
					{
					if( trigger.equalsIgnoreCase("playlist")){
						Intent in=new Intent(mcontext,PlayScreen.class);
						in.putExtra("PlayList", "PlayList");
						in.putExtra("ob", playList);
						in.putExtra("songno", arg2);
						in.putExtra("count", 1);
						
						startActivity(in);
					}}
					else{
						
						Log.e("Size  ", ""+songList.size());
						
						Intent in=new Intent(mcontext,PlayScreen.class);
						in.putExtra("Album", "Album");
						in.putExtra("AlbumId", songList.get(arg2).getAlbumId());
						in.putExtra("songno", arg2);
						in.putExtra("ob", songList.get(arg2));
						in.putExtra("count", songList.size());
						
						in.putExtra("songlist", songList);
						startActivity(in);
					
					}
				}
			}
		});
	}
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.back:
					finish();
					break;
				case R.id.btnEdit:
					
//					if(StaticVariables.editButtonCheck)
//						StaticVariables.editButtonCheck=false;
//					else
//						StaticVariables.editButtonCheck=true;
					
					
						
						
						btnEdit.setVisibility(View.GONE);
						
						btnAdd.setBackgroundResource(R.drawable.done_btn_selector);
						btnAdd.setTag("Done");
						AlbumListSongAdapter adapter=new AlbumListSongAdapter(mcontext, android.R.layout.simple_list_item_1, songList,"Edit");
						listView.setAdapter(adapter);
					
					break;
				
			}
		}
	};
	public void onResume()
	{
		super.onResume();
		try
		{
			ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
			songList=db.getPlayListSongs(playList.getId());
			AlbumListSongAdapter adapter=new AlbumListSongAdapter(mcontext, android.R.layout.simple_list_item_1, songList,"");
			listView.setAdapter(adapter);
		
		}
		catch(Exception e)
		{
			
		}
		
	}
}
