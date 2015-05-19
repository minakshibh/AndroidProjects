package com.karaoke.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoom.karaoke.R;

public class MyAlbumsAdapter extends ArrayAdapter<Album>
{

	Context mcontext;
	ArrayList<Album> availableAlbum;
	LayoutInflater inflator;
	ImageLoaderAlbum imageLoader;
	public MyAlbumsAdapter(Context context, int resource, ArrayList<Album> objects) 
	{
		super(context, resource, objects);
		
		availableAlbum=objects;
		mcontext=context;
		inflator=((Activity)mcontext).getLayoutInflater();
		
		imageLoader=new ImageLoaderAlbum(mcontext.getApplicationContext());
	}
	public View getView(int position,View convertView,ViewGroup parent)
	{
		
		if(convertView==null)
			convertView=inflator.inflate(R.layout.availablealbumscustom, null);
		
		TextView albumName=(TextView)convertView.findViewById(R.id.albumName);
		TextView artistName=(TextView)convertView.findViewById(R.id.artistName);
		TextView songs=(TextView)convertView.findViewById(R.id.songcount);
		TextView txtSongNo=(TextView)convertView.findViewById(R.id.songNo);
		ImageView image=(ImageView)convertView.findViewById(R.id.image);
		TextView buynow=(TextView)convertView.findViewById(R.id.buyNow);
		buynow.setVisibility(View.GONE);
		
		Album album=availableAlbum.get(position);
		
		System.out.println("Album url "+album.getThumbnailUrl());
		
		
		image.setTag(""+album.getAlbumId());
		imageLoader.DisplayImage(""+album.getAlbumId(), (Activity)mcontext, image);
		albumName.setText(album.getAlbumName());
		artistName.setText(album.getArtistName());
		
		if(album.getSongs()==1)
			songs.setText(""+album.getSongs()+" Song");
		else
			songs.setText(""+album.getSongs()+" Songs");
		
		
		return convertView;
	}
}