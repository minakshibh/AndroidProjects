package com.karaoke.util;

import java.util.ArrayList;

import com.zoom.karaoke.R;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class CardTypeSpinAdapter extends ArrayAdapter<String>
{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<String> values;

    public CardTypeSpinAdapter(Context context, int textViewResourceId,
    		ArrayList<String> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    public int getCount(){
       return values.size();
    }

    public String getItem(int position){
       return values.get(position);
    }

    public long getItemId(int position){
       return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        
    	 
    	 convertView=((Activity)context).getLayoutInflater().inflate(R.layout.paymentspinner, null);
    	 
    	TextView txt=(TextView)convertView.findViewById(R.id.name);
    	txt.setText(values.get(position));
        return convertView;
    }

   
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
    	
    	 convertView=((Activity)context).getLayoutInflater().inflate(R.layout.paymentspinner, null);
    	 TextView txt=(TextView)convertView.findViewById(R.id.name);
    	 txt.setText(values.get(position));
        return convertView;
    	
        

       
    }
}

