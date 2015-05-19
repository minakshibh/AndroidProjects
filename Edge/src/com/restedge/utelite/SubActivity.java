package com.restedge.utelite;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.restedge.utelite.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.restedge.util.Place;
import com.restedge.util.User;




public class SubActivity extends FragmentActivity  implements LocationListener {
	
	// Declaration of layout views.........
	RelativeLayout mapLayout;
	ScrollView ScrollView;
	TextView readData, getDirections, headerName;
	ListView commentlist;
	ImageButton backButton;
	
	// Google Map
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private String provider;
    MarkerOptions marker;
    SharedPreferences pref;
    Intent intent;
    Place place;
    String receiptId,EmailSubject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);		
        
		setContentView(R.layout.subactivity);
		
		pref=getSharedPreferences("mypref", MODE_PRIVATE);
		// Restration of layout views.........
		mapLayout=(RelativeLayout)findViewById(R.id.maplayout);
		//readLayout=(RelativeLayout)findViewById(R.id.readmorelayout);
		ScrollView=(ScrollView)findViewById(R.id.scrollid);
		//readmorecommentlayout=(RelativeLayout)findViewById(R.id.readmorecommentlayout);
		receiptId=pref.getString("SupportEmail", "");
		EmailSubject=getResources().getString(R.string.EmailSubject);
		backButton=(ImageButton)findViewById(R.id.backbtn);
		headerName=(TextView)findViewById(R.id.headername);
		
		commentlist=(ListView)findViewById(R.id.commentlist);
		
		readData=(TextView)findViewById(R.id.readmore);
		
		getDirections=(TextView)findViewById(R.id.getdirections);	
		
		// making the string  ("Get Directions") clickable..........
		getDirections.setMovementMethod(LinkMovementMethod.getInstance());
		addClickablePart("Get Directions").setSpan(new ForegroundColorSpan(getResources().getColor(R.color.app_base)), 0,0,0);		
		getDirections.setText(addClickablePart("Get Directions"), BufferType.SPANNABLE);
		
		// Here getting the value of the trigger from the RestaurantDetail screen..........
		intent = getIntent();
		String trigger = intent.getStringExtra("value");
		//placeIntent=getIntent();		
		place=(Place)intent.getSerializableExtra("Place");
		headerName.setText(place.getName());
		
		System.out.println("RestaurantDetail"+place.getAddress());
		System.out.println("RestaurantDetail"+place.getName());
		
		if(trigger.equals("map")){//    trigger for google map............. 
			String address=intent.getStringExtra("address");
			mapLayout.setVisibility(View.VISIBLE);
			/*readLayout.setVisibility(View.GONE);
			readmorecommentlayout.setVisibility(View.GONE);*/
			ScrollView.setVisibility(View.GONE);
			commentlist.setVisibility(View.GONE);
			// Get the location manager
		    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		    // Define the criteria how to select the locatioin provider -> use
		    // default
		    Criteria criteria = new Criteria();
		    provider = locationManager.getBestProvider(criteria, false);
		    Location location = locationManager.getLastKnownLocation(provider);

		    // Initialize the location fields
		    if (location != null) {
		      System.out.println("Provider " + provider + " has been selected.");
		      onLocationChanged(location);
		    } else {
		    	 System.out.println("Provider " + provider + " has not been selected.");
		      //latituteField.setText("Location not available");
		      //longitudeField.setText("Location not available");
		    }
		    
		    
		    try {
		           // Loading map
		  		   	
		  		  SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
		  		  googleMap = mapFragment.getMap();
//		  		  googleMap.getUiSettings().setScrollGesturesEnabled(false);		  			
		  		   
		  		   googleMap.setMyLocationEnabled(true);
		  		   
		  		   	
			  		// latitude and longitude
			  		 double latitude = location.getLatitude();
			  		 double longitude = location.getLongitude();
		  		   
			  		/*  // create marker
			  		  marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("I am Here");	  		   
			  		  // adding marker
			  		  googleMap.addMarker(marker);*/
			  		 Log.e("loc details", "HHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH");
			  		 Log.e("loc details", "Latitude : "+place.getLatitude()+" , Longitude : "+place.getLongitude());
			  		 
			  		 marker = new MarkerOptions().position(new LatLng(place.getLatitude(), place.getLongitude())).title(place.getName());	  		   
//			  		 marker = new MarkerOptions().position(new LatLng(28.636086700000000000, 77.177992500000070000)).title("Given Postion");	  		   
			  		  // adding marker
			  		
			  		 googleMap.addMarker(marker);	
			  		
			  		 googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(place.getLatitude(), place.getLongitude()), 15));
		  		   	 googleMap.animateCamera(CameraUpdateFactory.zoomTo(14), 2000, null);
			  	     
			  			}catch (Exception e) {
				    	   //Toast.makeText(getApplicationContext(), "e.getMessage() ++ "+ e.getMessage(), Toast.LENGTH_SHORT).show();
				           e.printStackTrace();
				           }
			
				}else if(trigger.equals("ReadMore")){  //    trigger for read more information about resturant............. 
					//String infoString=intent.getStringExtra("rsetInfo");
					
					//mapLayout.setVisibility(View.GONE);
					/*readLayout.setVisibility(View.VISIBLE);
					readmorecommentlayout.setVisibility(View.GONE);*/
					ScrollView.setVisibility(View.VISIBLE);
					commentlist.setVisibility(View.GONE);
					
					String description=place.getDescription();
					  description = description.replaceAll("\\\\n", "\\\n");
					  description = description.replaceAll("\\\\r", "\\\r");
					  readData.setText(description);
					//readData.setText("<string nameAs per my understanding, this application is a Restaurant Guide which provides users with genuine information about various Restaurants, Bars, nightclubs etc. in Norway. The application will be based on static data structure i.e. predefined info of restaurants, bars, nightclubs which will be stored in SQLite database and application will access it so as to display info regarding the places of interest.   As this is a local guide of Restaurants, so the basic language of the app is Norwegian (where-by Text Files in Norwegian will be provided by you).  Basic layout of this application is based on iPod Music app i.e. the buttons (representing Restaurant, Bars, Nightclubs etc.) will have images over them along with the corresponding text.   Lazy loading of images (Multi-threading) will be used so as to reduce processing time while displaying info.   Below is the screen flow for this application:1.Splash Screen: This would be the intro screen for this application which would get displayed for 2- 3 seconds on application startup.2.	City Listing: This screen will list the cities where I have gathered information about the nightlife.3.	Genre Listing: This screen will list the genre of entertainment people can choose from. The genres are: Nattklubber (Nightclubs), Restauranter (Restaurants) & Bar/pub (It�s the same in English and Norwegian).3.1. All cities is to have the genre listing.4.	Entertainment Listing: This screen would show a listing of Restaurants, bars, nightclubs etc. as fetched from the SQLite database. Records will be fetched via Multi-threading. User would be able to see the corresponding detail by clicking on any restaurant of her/his interestDesign of this screen is based on iPod music app. Enclosed below is a screenshot of the expected design:5.	Restaurant Detail: This screen would show detailed information of any place of interest. Image of place will be shown along with its detailed information. Detailed info will be scrollable so that user can see the info completely.</string>");
				}else if(trigger.equals("ReadMoreComments")){  //    trigger for read mor comments............. 
					//User[] commentData=(User[])intent.getSerializableExtra("CommentsData");
					
					//User[] getCommentsArray = new User[getComments.size()];
					//mapLayout.setVisibility(View.GONE);
					/*readLayout.setVisibility(View.GONE);
					readmorecommentlayout.setVisibility(View.VISIBLE);*/	
					try{
					ScrollView.setVisibility(View.GONE);
					commentlist.setVisibility(View.VISIBLE);
					//Log.e("On subActivity", " On subActivity: "+RestaurantDetail.newList.size());
					CommentAdapte1r adapter = new CommentAdapte1r (this, R.layout.showcommentlayout, RestaurantDetail.newList);
					commentlist.setAdapter(adapter);
					}catch(Exception e){}
				}
		
		
		
		 //    listener for the views............. 
		 View.OnClickListener listener = new View.OnClickListener() {
	            public void onClick(View v) {
	              // it was the 1st button
	            	switch (v.getId()) {
						/*case R.id.getdirections:
							//boolean isCar = dockState == EXTRA_DOCK_STATE_CAR;
						//Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
						Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=an+merrute+bus"));
						startActivity(intent);
						
						break;	*/
						
						case R.id.backbtn:
							//Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
							/*Intent backintent = new Intent(SubActivity.this, RestaurantDetail.class);
							backintent.putExtra("Place",place);
							startActivity(backintent);*/
							finish();
							break;	
					}          
	            }
	          };
	          
	         // getDirections.setOnClickListener(listener);  
	          backButton.setOnClickListener(listener);

	}	
//	@Override
//    public boolean onCreateOptionsMenu(Menu menu)
//    {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main, menu);
//        return true;
//    }
//	public boolean onOptionsItemSelected(MenuItem item){
//	    try{
//	        switch(item.getItemId()){
//	            case R.id.helpmenu:
//	            	Intent in=new Intent(SubActivity.this,HelperActivity.class);
//	            	in.putExtra("receiptId", receiptId);
//	            	
//	            	startActivity(in);
////	            	Intent intent=new Intent(Intent.ACTION_SEND);
////	            	String[] recipients={receiptId};
////	            	intent.putExtra(Intent.EXTRA_EMAIL, recipients);
////	            	intent.putExtra(Intent.EXTRA_SUBJECT,EmailSubject);
////	            	intent.putExtra(Intent.EXTRA_TEXT,"");
////	            	
////	            	intent.setType("plain/text");
////	            	startActivity(Intent.createChooser(intent, "Contact Uteliv via"));
//	    }
//	    }catch(Exception e){
//	        Log.i("Sleep Recorder", e.toString());
//	    }
//	    return true;
//	}
	public void onFooterClick(View v)
	{
		Intent in=new Intent(SubActivity.this,HelperActivity.class);
    	in.putExtra("receiptId", receiptId);
    	
    	startActivity(in);
		
	}
	@Override
    protected void onResume() {
        super.onResume();     
       // initilizeMap();
        try{
       locationManager.requestLocationUpdates(provider, 400, 1, this);
	}catch(Exception e){}
        
    }


	 public void onLocationChanged(Location location) {
	    int lat = (int) (location.getLatitude());
	    int lng = (int) (location.getLongitude());
	   // latituteField.setText(String.valueOf(lat));
	   // longitudeField.setText(String.valueOf(lng));
	  }

	  public void onStatusChanged(String provider, int status, Bundle extras) {
	    // TODO Auto-generated method stub

	  }

	  public void onProviderEnabled(String provider) {
	    Toast.makeText(this, "Enabled new provider " + provider,
	        Toast.LENGTH_SHORT).show();

	  }

	  public void onProviderDisabled(String provider) {
	    Toast.makeText(this, "Disabled provider " + provider,
	        Toast.LENGTH_SHORT).show();
	  }
	  
	  
	  // function make a string clickable or string behave like url
	  public SpannableStringBuilder addClickablePart(String str) {
		    SpannableStringBuilder ssb = new SpannableStringBuilder(str);
		    //ssb.setSpan(new ForegroundColorSpan(Color.GREEN), flag, flag, flag);	   
		    try{    	   	
		      // int i=0;
		        Matcher m = Pattern.compile("Get Directions").matcher(str);        	
		        
			    while(m.find()){		    	
			    	 final String clickString  = m.group(0); // (714) 321-2620	
			    	// myArray[i] = m.group(0);
			    	// Log.e("String",  matchstr[i] );
			         ssb.setSpan(new ClickableSpan() {	
			        	  @Override
			            public void onClick(View widget) {
			               // Toast.makeText(MyListActivity.this, clickString,  Toast.LENGTH_SHORT).show();
			        		  Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+place.getLatitude()+","+place.getLongitude()));
			        	        startActivity(intent);           		
//								Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=an+delhi"));
//								startActivity(intent);
			            	
			            }
			        }, m.start(), m.end(), 0);
			    }
			   }catch(Exception e){Log.e("Exception",  e.getMessage());	    
		    	Log.e("Exception", e.getMessage());	    }
		    ssb.setSpan(new ForegroundColorSpan(Color.parseColor("#9B0C08")), 0 ,14, 0);	    
		    return ssb;	    
		 }
}

// Custom Adapter for the list view......... 
class CommentAdapte1r extends ArrayAdapter<User> {

    private final Context context;
    private final List<User> data;
    private final int rowResourceId;

    public CommentAdapte1r(Context context, int textViewResourceId, List<User> objects) {

        super(context, textViewResourceId, objects);

        this.context = context;
        this.data = objects;
        this.rowResourceId = textViewResourceId;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(rowResourceId, parent, false);
      
        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView time = (TextView) rowView.findViewById(R.id.time);
        TextView comments = (TextView) rowView.findViewById(R.id.comments);

        	name.setText(RestaurantDetail.newList.get(position).getName());
    	    time.setText(RestaurantDetail.newList.get(position).getTime());
    		comments.setText(RestaurantDetail.newList.get(position).getComment()); 	
       /* name.setText(((Place) data).getName());
        time.setText(RestaurantDetail.newList.get(position).getTime());
		comments.setText(RestaurantDetail.newList.get(position).getComment());*/
    			  
        return rowView;

    }
    
    
}
