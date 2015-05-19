package com.zoom.karaoke;

import java.util.ArrayList;

import com.karaoke.util.PlayListAdapter;
import com.karaoke.util.PlayListSongAdapter;
import com.zoom.karaoke.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

public class PlayListSong extends Activity{

	ListView listView;
	ArrayList<String> albumslist=new ArrayList<String>();
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.playlistsong);
		
		listView=(ListView)findViewById(R.id.playlistsong);
		albumslist.add("Hello ");
		albumslist.add("Hi ");
		
		PlayListSongAdapter adapter=new PlayListSongAdapter(this, android.R.layout.simple_list_item_1, albumslist);
		listView.setAdapter(adapter);
		
		
		
	}
}
