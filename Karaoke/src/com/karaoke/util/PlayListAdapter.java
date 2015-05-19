package com.karaoke.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.zoom.karaoke.R;

public class PlayListAdapter extends ArrayAdapter<String>
{

	Context mcontext;
	ArrayList<String> availableSong;
	LayoutInflater inflator;
	public PlayListAdapter(Context context, int resource, ArrayList<String> objects) 
	{
		super(context, resource, objects);
		
		availableSong=objects;
		mcontext=context;
		inflator=((Activity)mcontext).getLayoutInflater();
		
	}
	public View getView(int position,View convertView,ViewGroup parent)
	{
		
		if(convertView==null)
			convertView=inflator.inflate(R.layout.customplaylist, null);
		
		
		
		
		return convertView;
	}
}