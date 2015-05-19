package com.restedge.utelite;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.MadsAdView.MadsInlineAdView;
import com.restedge.utelite.R;
import com.restedge.util.ConnectionDetector;
import com.restedge.util.MadsDemoUtil;
import com.restedge.util.Place;
import com.restedge.util.RestaurantDatabase;
import com.restedge.util.SAXXMLParser;



public class RestaurantListing extends Activity{

	
	ListView placeListing;
	Map<String, String> map=new HashMap<String, String>();
	
	ArrayList<Place> restaurant;
	ArrayList<Place> nclubs;
	
	String placeListUrl;//="http://112.196.24.205:111/utilivewebaccess.asmx/PlaceListing";
	
	TextView txtCityName;
	ProgressDialog dialog;
	Button btnNightClub,btnRestaurant;
	ImageButton btnBack;
	Intent in;
	String method_name="PlaceListing";
	String cityid, cityName;
	InputStream is;
	String receiptId,EmailSubject;
	private SharedPreferences pref;
	 int LastUpdated;
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.restaurant_listing);
	
		System.gc();
		
		
		
	/*	MadsInlineAdView adView = (MadsInlineAdView)findViewById(R.id.mads_inline_adview);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		MadsDemoUtil.setupUI(adView, this);
		
		adView.setVisibility(View.GONE);*/
		
		
		String app_url=getResources().getString(R.string.app_url);
		placeListUrl=app_url+"/"+method_name;
		
		EmailSubject=getResources().getString(R.string.EmailSubject);
		
		pref = RestaurantListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
		txtCityName=(TextView)findViewById(R.id.txtHeader);
		
		receiptId=pref.getString("SupportEmail", null);
		//this arraylist used to store restaurants
		restaurant = new ArrayList<Place>();
		//this list used to store clubs
		nclubs = new ArrayList<Place>();
		
		btnNightClub=(Button)findViewById(R.id.btnnightclub);
		
		btnRestaurant=(Button)findViewById(R.id.btnrestaurant);
			
		
		btnNightClub.setVisibility(View.GONE);
		btnRestaurant.setVisibility(View.GONE);
		
		//This intent used to get city info on which clicked
		in=getIntent();
		cityid=in.getStringExtra("cityId");
		cityName = in.getStringExtra("cityName");
		
		txtCityName.setText(cityName);
		
		System.out.println(cityid+"  "+cityName);
		btnNightClub.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new GettingPlaces("NightClub").execute(); 
				
				btnNightClub.setBackgroundResource(R.drawable.tab_button_hover);
				btnRestaurant.setBackgroundResource(R.drawable.tab_button);
				//check  clubs are available or not
				if(nclubs.size()==0)
		    	{
		    		Toast.makeText(RestaurantListing.this, "No Night clubs found", 1000).show();
		    	}
				//setting the adpeter on placelist
				PlaceAdapter adapter=new PlaceAdapter(RestaurantListing.this, android.R.layout.simple_list_item_1, nclubs);
		    	placeListing.setAdapter(adapter);
			}
		});
		//click on restaurant button
		btnRestaurant.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new GettingPlaces("Restaurant").execute();
				btnNightClub.setBackgroundResource(R.drawable.tab_button);
				btnRestaurant.setBackgroundResource(R.drawable.tab_button_hover);
				//check restaurants are available or not
				if(restaurant.size()==0)
		    	{
		    		Toast.makeText(RestaurantListing.this, "No Retaurant found", 1000).show();
		    		
		    	}
				PlaceAdapter adapter=new PlaceAdapter(RestaurantListing.this, android.R.layout.simple_list_item_1, restaurant);
		    	placeListing.setAdapter(adapter);
			}
		});
		
		btnBack=(ImageButton)findViewById(R.id.btnBack);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {			
				finish();
			}
		});
		
		placeListing=(ListView)findViewById(R.id.placeListing);
		
		//click listener for placelisting items
		placeListing.setOnItemClickListener(new OnItemClickListener() {
			
			

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
			View v=arg1;
			
			TextView placeId=(TextView)v.findViewById(R.id.restaurantId);
			if(placeId.getText().toString().length()!=0)
			{
				int id=Integer.parseInt(placeId.getText().toString());
				
				RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
				Place place=db.getPlace(id);
				
				Intent intent=new Intent(RestaurantListing.this,RestaurantDetail.class);
				Log.e("Place id",""+ id);
				Log.e("City id", ""+cityid);
				
				intent.putExtra("PlaceId",id);
				intent.putExtra("cityId", Integer.parseInt(cityid));
				startActivity(intent);
			}
			
		//	startActivity(intent);
				
			}
		});
		
		//check wether internet is available   or not
		ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		//get places from server when internet available
		if(isInternetPresent)
		{
			System.out.println("hello get connection");
			new GettingPlaces("Restaurant").execute(); 
		}
		else
		{
			AlertDialog alertdialog=new AlertDialog.Builder(RestaurantListing.this).create();
    		System.out.println("check connection");
    		alertdialog.setTitle("Edge");
    		alertdialog.setMessage("Internet Not available");
    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
    			
    			public void onClick(DialogInterface dialog, int which) {
    				// TODO Auto-generated method stub
    				
    			}
    		});
    		alertdialog.show();
    		
    		
    		RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
	    	restaurant = new ArrayList<Place>();
	    	nclubs = new ArrayList<Place>();
	    	//Getting restaurants from database on basis of city
	    	ArrayList<Place> places=db.getTypePlaceList(("Restaurant"),cityid);
	    	try
	    	{
	    		System.out.println("restaurant list");
		    	
		    	System.out.println("size list"+places.size());
		    	//Sorting in order of places by buffet,night club,dining
		    	Collections.sort(places, new Comparator<Place>(){

			        public int compare(Place o1, Place o2) {
			            Place p1 = (Place) o1;
			            Place p2 = (Place) o2;
			           return p1.getSubcategory().compareToIgnoreCase(p2.getSubcategory());
			        }
			    });
		    
	    	}catch(Exception  e){
	    		e.printStackTrace();
	    	}
		    	String temp ="none";
		    //	Toast.makeText(RestaurantListing.this, ""+places.size(), 1000).show();
		    	for(int i =0; i<places.size(); i++){
		    		Log.e("Place "+i,places.get(i).getName()+"::: "+places.get(i).getId()+",,, "+places.get(i).getSubcategory());
		    		
		    		if(places.get(i).getSubcategory().equals(temp)){
		    			restaurant.add(places.get(i));
		    			Log.e("restaurant inserted",places.get(i).getName());
		    		}else{
		    			Place headerPlace = new Place();
		    			headerPlace.setName(places.get(i).getSubcategory());
		    			headerPlace.setId(-1);
		    			restaurant.add(headerPlace);
		    			
		    			restaurant.add(places.get(i));
		    			Log.e("restaurant inserted",places.get(i).getName());
		    			
		    			Log.e("header inserted",headerPlace.getName());
		    			
		    			temp = places.get(i).getSubcategory();
			    		Log.e("temp", temp);
		    		}
		    		
		    		
		    	}
		    	
		    	 db=new RestaurantDatabase(RestaurantListing.this);
	    	
	    		System.out.println("nightclub list");
	    		//Getting night clubs from database on bais of city
	    		ArrayList<Place> 	 place=db.getTypePlaceList(("NightClub"),cityid);
	    		//check whether night clubs are available or not
	    		
	    		//Sorting night clubs by buffet,night clubs
	    		Collections.sort(place, new Comparator<Place>(){

			        public int compare(Place o1, Place o2) {
			            Place p1 = (Place) o1;
			            Place p2 = (Place) o2;
			           return p1.getSubcategory().compareToIgnoreCase(p2.getSubcategory());
			        }
			    });
	    		
	    		temp ="none";
		    	
		    	for(int i =0; i<place.size(); i++){
		    		
		    		if(place.get(i).getSubcategory().equals(temp)){
		    			nclubs.add(place.get(i));
		    			Log.e("nclub inserted",place.get(i).getName());
		    		}else{
		    			Place headerPlace = new Place();
		    			headerPlace.setName(place.get(i).getSubcategory());
		    			headerPlace.setId(-1);
		    			nclubs.add(headerPlace);
		    			Log.e("nclub header inserted",headerPlace.getName());
		    			
		    			nclubs.add(place.get(i));
		    			Log.e("nclub inserted",place.get(i).getName());
		    			
		    			temp = place.get(i).getSubcategory();
		    		}
		    	}
		    	
		    	PlaceAdapter adapter=new PlaceAdapter(RestaurantListing.this, android.R.layout.simple_list_item_1, restaurant);
		    	placeListing.setAdapter(adapter);
	    	
    		
		}
	
    	
		}
		class PlaceAdapter extends ArrayAdapter<Place>
		{
			
			Context mcontext;
			ArrayList<Place> placeList;
			com.restedge.util.ImageLoaderRestaurant	imageLoader;
			String tempSubcategory="none";
			
			boolean flag=true;
			public PlaceAdapter(Context context,
					int textViewResourceId, ArrayList<Place> objects) {
				super(context, textViewResourceId, objects);
				// TODO Auto-generated constructor stub
			
				mcontext=context;
				placeList=objects;
				imageLoader=new com.restedge.util.ImageLoaderRestaurant(mcontext.getApplicationContext());
			
				
				System.out.println("hello constructor");
			}
			public View getView(int position,View convertView,ViewGroup parent)
			{
				
				if(convertView==null)
					convertView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.restaurant_row, null);
				
				ImageView placeImage=(ImageView)convertView.findViewById(R.id.restaurantImage);
				TextView placeName=(TextView)convertView.findViewById(R.id.restaurantName);
				TextView placeAddress=(TextView)convertView.findViewById(R.id.restaurantAddress);
				TextView placeId=(TextView)convertView.findViewById(R.id.restaurantId);
				TextView catHeader =(TextView)convertView.findViewById(R.id.catHeader);
				RelativeLayout rowLayout = (RelativeLayout)convertView.findViewById(R.id.rowLayout);
				
//				TextView txtSubcategory=(TextView)convertView.findViewById(R.id.subCategory);
				
				Place place=placeList.get(position);
				
				if(place.getId()==-1){
					catHeader.setVisibility(View.VISIBLE);
					rowLayout.setVisibility(View.GONE);
					catHeader.setText(place.getName());
				}else{
					catHeader.setVisibility(View.GONE);
					rowLayout.setVisibility(View.VISIBLE);
					
//				Log.e("track","position "+position+"place id "+place.getId()+"  "+place.getSubcategory()+" temp :: "+tempSubcategory);
				
				
				/*if(!place.getSubcategory().equalsIgnoreCase(tempSubcategory) && flag)
				{
					
					tempSubcategory=place.getSubcategory();
					txtSubcategory.setVisibility(View.VISIBLE);
					txtSubcategory.setText(place.getSubcategory());
				}
				
				if(position==placeList.size()-1)
				{
					System.out.println("size equall");
					
					
				}
				
				*/
					Log.e("place detected",place.getName()+" category: "+place.getSubcategory());
				
				
				placeImage.setTag(""+place.getId());
				placeName.setText(place.getName());
				
				String address=place.getAddress();
				address = address.replaceAll("\\\\n", "\\\n");
				address = address.replaceAll("\\\\r", "\\\r");
				
				
				placeAddress.setText(address);
				
				imageLoader.DisplayImage(""+place.getId(), RestaurantListing.this, placeImage);
				
				placeId.setText(""+place.getId());
				
				
				}
				
				return convertView;
			}
		}
		public class GettingPlaces extends AsyncTask<String, Void, String> 
		{
			String placetype;
			List<Place> placesRestaurant;
			List<Place> placesClubs;
			public GettingPlaces(String placetype) {				
				this.placetype=placetype;
			}
			
			int result;
			String message;
			@Override
		    protected void onPreExecute() {
		    	
		    	super.onPreExecute();
		    	dialog=new ProgressDialog(RestaurantListing.this);
		    	
		   
		    	dialog.setTitle("Edge");
		    	dialog.setMessage("Please wait");
		    	dialog.setCanceledOnTouchOutside(false);
		    	dialog.show();
		    	
		    }
			@Override
			protected String doInBackground(String... arg0) {
				// TODO Auto-generated method stub
//				SharedPreferences pref=RestaurantListing.this.getSharedPreferences("mypref", MODE_PRIVATE);
				int check= pref.getInt(cityName+"LastUpdate", -1);
				
				try
				{
					DefaultHttpClient httpClient = new DefaultHttpClient();
			        
			        HttpPost requestLogin = new HttpPost(placeListUrl);
			        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			        
			        System.out.println(check+"  "+cityid);
					nameValuePairs.add(new BasicNameValuePair("lastUpdatedId", ""+check));
					nameValuePairs.add(new BasicNameValuePair("cityId", cityid));
					requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        HttpResponse response = httpClient.execute(requestLogin);
			        HttpEntity entity = response.getEntity();
			        is = entity.getContent();  
					
			        //Getting parsing places
			        Map<String,List<Place>> places = SAXXMLParser.parsePlaces(is);
					//Getting other info like lastupdates
			        map=SAXXMLParser.map;
					
					 placesRestaurant=places.get("Restaurants");
					 System.out.println("Restarants "+placesRestaurant.size());
					 
					for(int i=0;i<placesRestaurant.size();i++)
						Log.e(placesRestaurant.get(i).getName(),"Lat="+ placesRestaurant.get(i).getLatitude()+"Long="+placesRestaurant.get(i).getLongitude());
					 
					 placesClubs=places.get("NightClubs");
					 System.out.println("NightClubs  "+placesClubs.size());
					 
					 
					 for(int i=0;i<placesClubs.size();i++)
							Log.d(placesClubs.get(i).getName(),"Lat="+ placesClubs.get(i).getLatitude()+"Long= "+ placesClubs.get(i).getLongitude());
						 
					 LastUpdated=Integer.parseInt(map.get("LastUpdated"));
						
					 result=Integer.parseInt(map.get("Result"));
					 message=map.get("Message");
					
					 System.out.println(LastUpdated+" "+result+" "+message);
					 
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				
			
				//check whether result is available or not
			if(result==0)
			{	
				//Check whether new data is available for insert or update
				if(LastUpdated>check)
				{
					try
					{						 
							System.out.println("size for restaurant"+placesRestaurant.size());
							
							for (int i=0;i<placesRestaurant.size();i++)
							{
							
								RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
								
								if(placesRestaurant.get(i).getVisibility_flag()==0)
								{
									Place place= db.getPlace(placesRestaurant.get(i).getId());
									if(place==null)
										continue;
									else
									{
										db.deletePlace(placesRestaurant.get(i).getId());
										continue;
									}
								}
								
								Place place= db.getPlace(placesRestaurant.get(i).getId());
								
								//check place available or not
								if(place==null)
								{
									 db=new RestaurantDatabase(RestaurantListing.this);
									placesRestaurant.get(i).setPlaceImage(null);
									placesRestaurant.get(i).setCategory("Restaurant");
									System.out.println("Url image  "+placesRestaurant.get(i).getImageUrl());
									db.addPlace(placesRestaurant.get(i),Integer.parseInt(cityid));
								}
								else
								{
									 db=new RestaurantDatabase(RestaurantListing.this);
									int placeid=placesRestaurant.get(i).getId();
									placesRestaurant.get(i).setPlaceImage(null);
									
									placesRestaurant.get(i).setCategory("Restaurant");
									db.updatePlace(placeid, placesRestaurant.get(i));
								}
								
							/*
							 * check record in database based on placeid
							 * if already exist{
							 * 		update query
							 * }else{
							 * 		insert query
							 * }					
							 * */
							
							
								/*boolean flag=false;
								
									Place place=placesRestaurant.get(i);
									int placeid=place.getId();
									
									RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
									ArrayList<Place> allplaces=db.getAllPlaces();
									
									for(int j=0;j<allplaces.size();j++)
									{
										Place p=allplaces.get(j);
										if( p.getId()==placeid)
										{
											flag=true;
											break;
										}
										else
											flag=false;
									}
									
									if(flag)
									{
										
										String url=place.getImageUrl();
										System.out.println(url);
										
									
										place.setPlaceImage(null);
										db.updatePlace(placeid, place);
									}
									else
									{
										String url=place.getImageUrl();
										
										
										place.setPlaceImage(null);
										
										
										db.addPlace(place);
									}*/
							
							}
							
							System.out.println("size for NightClub"+placesClubs.size());
							for (int i=0;i<placesClubs.size();i++)
							{
								
								
								RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
								
								if(placesClubs.get(i).getVisibility_flag()==0)
								{
									Place place= db.getPlace(placesClubs.get(i).getId());
									if(place==null)
										continue;
									else
									{
										db.deletePlace(placesClubs.get(i).getId());
										continue;
									}
								}
								
								
								
								
								Place place= db.getPlace(placesClubs.get(i).getId());
								if(place==null)
								{
									placesClubs.get(i).setPlaceImage(null);
									placesClubs.get(i).setCategory("NightClub");
									db.addPlace(placesClubs.get(i),Integer.parseInt(cityid));
								}
								else
								{
									int placeid=placesClubs.get(i).getId();
									placesClubs.get(i).setPlaceImage(null);
									placesClubs.get(i).setCategory("NightClub");
									db.updatePlace(placeid, placesClubs.get(i));
								}
								/*
								 * check record in database based on placeid
								 * if already exist{
								 * 		update query
								 * }else{
								 * 		insert query
								 * }					
								 * */
								
								/*boolean flag=false;
								
									Place place=placesNightClub.get(i);
									int placeid=place.getId();
									
									RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
									ArrayList<Place> allplaces=db.getAllPlaces();
									
									for(int j=0;j<allplaces.size();j++)
									{
										Place p=allplaces.get(j);
										if( p.getId()==placeid)
										{
											flag=true;
											break;
										}
										else
											flag=false;
									}
									
									if(flag)
									{
										
										String url=place.getImageUrl();
										System.out.println(url);
										
										
										place.setPlaceImage(null);
										db.updatePlace(placeid, place);
									}
									else
									{
										String url=place.getImageUrl();
										
										
										place.setPlaceImage(null);
										
										
										db.addPlace(place);
									}*/
							
							}
							//update shared preference for updation
							SharedPreferences.Editor edit=pref.edit();
							edit.putInt(cityName+"LastUpdate", LastUpdated);
							edit.commit();
							
					}
					catch(Exception e){
						System.out.println("Errro update"+e.getMessage());
					}
				}
				
				
				
			}
				
			
				return null;
			}
			@SuppressWarnings("deprecation")
			@Override
		    protected void onPostExecute(String str) {
		    	
		    	dialog.dismiss();
		    	if(result!=0)
		    	{
		    		AlertDialog alertdialog=new AlertDialog.Builder(RestaurantListing.this).create();
		    		System.out.println("check connection");
		    		alertdialog.setTitle("Edge");
		    		alertdialog.setMessage(message);
		    		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
		    			
		    			public void onClick(DialogInterface dialog, int which) {
		    				
		    			}
		    		});
		    		alertdialog.show();
		    	}
		    	RestaurantDatabase db=new RestaurantDatabase(RestaurantListing.this);
		    	restaurant = new ArrayList<Place>();
		    	nclubs = new ArrayList<Place>();
		    	//checking type of place
		    	if(placetype.equalsIgnoreCase("Restaurant"))
		    	{
		    		System.out.println("restaurant list");
			    	ArrayList<Place> places=db.getTypePlaceList(("Restaurant"),cityid);
			    	System.out.println("size list"+places.size());
			    	//sorting restaurant list by subcategory
			    	Collections.sort(places, new Comparator<Place>(){
	
				        public int compare(Place o1, Place o2) {
				            Place p1 = (Place) o1;
				            Place p2 = (Place) o2;
				           return p1.getSubcategory().compareToIgnoreCase(p2.getSubcategory());
				        }
				    });
			    	
			    	String temp ="none";
			    //	Toast.makeText(RestaurantListing.this, ""+places.size(), 1000).show();
			    	for(int i =0; i<places.size(); i++){
			    		Log.e("Place "+i,places.get(i).getName()+"::: "+places.get(i).getId()+",,, "+places.get(i).getSubcategory());
			    		
			    		if(places.get(i).getSubcategory().equals(temp)){
			    			restaurant.add(places.get(i));
			    			Log.e("restaurant inserted",places.get(i).getName());
			    		}else{
			    			Place headerPlace = new Place();
			    			headerPlace.setName(places.get(i).getSubcategory());
			    			headerPlace.setId(-1);
			    			restaurant.add(headerPlace);
			    			
			    			restaurant.add(places.get(i));
			    			Log.e("restaurant inserted",places.get(i).getName());
			    			
			    			Log.e("header inserted",headerPlace.getName());
			    			
			    			temp = places.get(i).getSubcategory();
				    		Log.e("temp", temp);
			    		}
			    		
			    		
			    	}
			    	if(restaurant.size()==0)
			    	{
			    		Toast.makeText(RestaurantListing.this, "No Retaurant found", 1000).show();
			    	}
			    	View footerView=RestaurantListing.this.getLayoutInflater().inflate(R.layout.footerhelp, null);
					//placeListing.addFooterView(footerView);
			    	
			    	PlaceAdapter adapter=new PlaceAdapter(RestaurantListing.this, android.R.layout.simple_list_item_1, restaurant);
			    	placeListing.setAdapter(adapter);
		    	}
		    	if(placetype.equalsIgnoreCase("Restaurant"))
		    	{
		    		System.out.println("nightclub list");
		    		//Getting the list of night clubs
		    		ArrayList<Place> places=db.getTypePlaceList(("NightClub"),cityid);
		    		//Sorting night clubs in subcategory
		    		Collections.sort(places, new Comparator<Place>(){

				        public int compare(Place o1, Place o2) {
				            Place p1 = (Place) o1;
				            Place p2 = (Place) o2;
				           return p1.getSubcategory().compareToIgnoreCase(p2.getSubcategory());
				        }
				    });
		    		
		    		String temp ="none";
			    	
			    	for(int i =0; i<places.size(); i++){
			    		
			    		if(places.get(i).getSubcategory().equals(temp)){
			    			nclubs.add(places.get(i));
			    			Log.e("nclub inserted",places.get(i).getName());
			    		}else{
			    			Place headerPlace = new Place();
			    			headerPlace.setName(places.get(i).getSubcategory());
			    			headerPlace.setId(-1);
			    			nclubs.add(headerPlace);
			    			Log.e("nclub header inserted",headerPlace.getName());
			    			
			    			nclubs.add(places.get(i));
			    			Log.e("nclub inserted",places.get(i).getName());
			    			
			    			temp = places.get(i).getSubcategory();
			    		}
			    	}
			    	
			    	
//			    	PlaceAdapter adapter=new PlaceAdapter(RestaurantListing.this, android.R.layout.simple_list_item_1, nclubs);
//			    	placeListing.setAdapter(adapter);
		    	}
		    	//start service in download images of places
				startService(new Intent(RestaurantListing.this, ImageDownloadRestaurantService.class));
				
		    }
			
		}
//		@Override
//	    public boolean onCreateOptionsMenu(Menu menu)
//	    {
//	        MenuInflater menuInflater = getMenuInflater();
//	        menuInflater.inflate(R.menu.main, menu);
//	        return true;
//	    }
//		public boolean onOptionsItemSelected(MenuItem item){
//		    try{
//		        switch(item.getItemId()){
//		            case R.id.helpmenu:
//		            	Intent in=new Intent(RestaurantListing.this,HelperActivity.class);
//		            	in.putExtra("receiptId", receiptId);
//		            	
//		            	startActivity(in);
////		            	Intent intent=new Intent(Intent.ACTION_SEND);
////		            	String[] recipients={receiptId};
////		            	intent.putExtra(Intent.EXTRA_EMAIL, recipients);
////		            	intent.putExtra(Intent.EXTRA_SUBJECT,EmailSubject);
////		            	intent.putExtra(Intent.EXTRA_TEXT,"");
////		            	
////		            	intent.setType("plain/text");
////		            	startActivity(Intent.createChooser(intent, "Contact Uteliv via"));
//		    }
//		    }catch(Exception e){
//		        Log.i("Sleep Recorder", e.toString());
//		    }
//		    return true;
//		}
		public void onFooterClick(View v)
		{
			Intent in=new Intent(RestaurantListing.this,HelperActivity.class);
	    	in.putExtra("receiptId", receiptId);
	    	
	    	startActivity(in);
			
		}
		
}
