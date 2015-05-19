package com.zoom.karaoke;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karaoke.util.ImageLoaderAvailableSongTumbnail;
import com.karaoke.util.PlayList;
import com.karaoke.util.Song;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.karaoke.R;

public class AddSongsToPlaylist extends Activity{

	private PlayList playList;
	private TextView headerName;
	private ImageButton btnBack;
	private ListView listView;
	ZoomKaraokeDatabaseHandler handler;
	ArrayList<Song> allSongList=new ArrayList<Song>();
	ArrayList<Song> songList=new ArrayList<Song>();
	Context mcontext;
	Intent in;
	int playlist_id;
	private ImageView addBtn,editBtn;
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.albumlistsong);
		
		mcontext=this;
		playList = (PlayList)getIntent().getSerializableExtra("playList");
		listView=(ListView)findViewById(R.id.listSong);
		
		
		
		
		headerName = (TextView)findViewById(R.id.albumName);
		btnBack = (ImageButton)findViewById(R.id.back);
		addBtn=(ImageView)findViewById(R.id.btnAdd);
		editBtn=(ImageView)findViewById(R.id.btnEdit);
		
		editBtn.setVisibility(View.GONE);
		addBtn.setBackgroundResource(R.drawable.done_btn_selector);
		addBtn.setOnClickListener(Listener);
		btnBack.setOnClickListener(Listener);
		headerName.setText(playList.getPlayListName());
		
		
		handler=new ZoomKaraokeDatabaseHandler(mcontext);
		
		allSongList=handler.getAllSong();
		
		
		songList=handler.getPlayListSongs(playList.getId());
		
		Log.e("allSongList ",""+ allSongList.size());
		
		Log.e("Length ",""+ songList.size());
		for(int i=0;i<allSongList.size();i++)
		{
			Song song=allSongList.get(i);
			
			for(int j=0;j<songList.size();j++)
			{
				Song song1=songList.get(j);
				
				Log.e("alList id  "+song.getSongId(), "song id  "+song1.getSongId());
				if(song.getSongId()==song1.getSongId())
				{
					allSongList.remove(i);
					if(allSongList.size()>0)
						i--;
					break;
				}
			}
			
		}
		
		
		SongAdapter adapter=new SongAdapter(this,android.R.layout.simple_list_item_1,allSongList);
		listView.setAdapter(adapter);
		
		
		
		
	}
	
	class SongAdapter extends ArrayAdapter<Song>
	{

		LayoutInflater inflator;
		ArrayList<Song> listSong;
		ImageLoaderAvailableSongTumbnail imageLoader;
		public SongAdapter(Context context, int resource, ArrayList<Song> objects) {
			super(context, resource, objects);
			inflator=((Activity)context).getLayoutInflater();
			listSong=objects;
			imageLoader=new ImageLoaderAvailableSongTumbnail(mcontext);
		}
		public View getView(final int position,View convertView,ViewGroup parent)
		{
			
				if(convertView==null)
					convertView=inflator.inflate(R.layout.availablesongcustom, null);
				
				TextView artistName=(TextView)convertView.findViewById(R.id.artistName);
				final TextView songName=(TextView)convertView.findViewById(R.id.songName);
				TextView songId=(TextView)convertView.findViewById(R.id.songId);
				final TextView buynow=(TextView)convertView.findViewById(R.id.buyNow);
				TextView price=(TextView)convertView.findViewById(R.id.price);
				TextView trackCode=(TextView)convertView.findViewById(R.id.trackCode);
				
				final CheckBox check=(CheckBox)convertView.findViewById(R.id.check);
				buynow.setVisibility(View.INVISIBLE);
				
				check.setVisibility(View.VISIBLE);
				
				check.setButtonDrawable(R.drawable.checkbox_unchecked);
				final Song song=listSong.get(position);
				ImageView image=(ImageView)convertView.findViewById(R.id.image);
				
				trackCode.setText("Track code : "+song.getAlbumcode());
				artistName.setText(song.getArtistName());
				songName.setText(song.getSongName());
				songId.setText(""+song.getSongId());
				
				image.setTag(song.getSongTumbnailUrl());
				
				
				imageLoader.DisplayImage(song.getSongTumbnailUrl(),(Activity) mcontext, image);
				
				check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						
						if(isChecked)
							check.setButtonDrawable(R.drawable.checkbox_checked);
						else
							check.setButtonDrawable(R.drawable.checkbox_unchecked);
						song.setSelected(isChecked);
						
					}
				});
				
				
			return convertView;
		}
		
	}
//	public void showToast(String str)
//	{
//		
//		Toast.makeText(mcontext, str, 1000).show();
//		
//	}
	OnClickListener Listener=new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.btnAdd:
					for(int i=0;i<allSongList.size();i++)
					{
						Song song=allSongList.get(i);
						if(song.isSelected())
						{
							handler=new ZoomKaraokeDatabaseHandler(mcontext);
							handler.addSongToPlayList(playList.getId(), song.getSongId());
							
							
						}
						
						
					}
					finish();
					
					break;
				case R.id.back:
					finish();
					break;
			
			}
		}
	};
}