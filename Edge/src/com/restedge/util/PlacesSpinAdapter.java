package com.restedge.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.restedge.utelite.R;

public class PlacesSpinAdapter extends ArrayAdapter<Place>
{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<Place> values;

    
    public PlacesSpinAdapter(Context context, int textViewResourceId,
    		ArrayList<Place> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
        
       
        
    }

    public int getCount(){
       return values.size();
    }

    public Place getItem(int position){
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
       
    	 View v=((Activity)context).getLayoutInflater().inflate(R.layout.customspinnerlayout, null);
    	 
    	 TextView txtId=(TextView)v.findViewById(R.id.id);
    	 TextView txtName=(TextView)v.findViewById(R.id.name);
    	 
    	 txtId.setText(""+values.get(position).getId());
	   	 txtName.setText(values.get(position).getName());
    	 
    	 return v;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView,
            ViewGroup parent) {
    	View v=((Activity)context).getLayoutInflater().inflate(R.layout.customspinnerlayout, null);
   	 
	   	 TextView txtId=(TextView)v.findViewById(R.id.id);
	   	 TextView txtName=(TextView)v.findViewById(R.id.name);
	   	 
	   	 txtId.setText(""+values.get(position).getId());
	   	 txtName.setText(values.get(position).getName());
        return v;
    }
}
