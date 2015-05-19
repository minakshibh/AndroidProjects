package com.qrpatrol.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.qrpatrol.android.R;
import com.qrpatrol.modal.CheckPoint;
import com.qrpatrol.modal.Officer;
import com.qrpatrol.order.LogListActivity;
import com.qrpatrol.order.OrderListActivity;
import com.qrpatrol.patrol.PatrolActivity;
import com.qrpatrol.schedule.ScheduleListActivity;
import com.qrpatrol.util.QRPatrolDatabaseHandler;

public class DashboardActivity extends Activity implements LocationListener{

	private ImageView patrol, schedule, orders, logs;
	private Button checkOut;
	private LinearLayout settings, aboutUs;
	private TextView username;
	public static Officer officer;
	private CheckPoint checkpoint;
	private SharedPreferences appPrefs, patrolPrefs;
	public static double myLat=0, myLon=0;
	private LocationManager locationManager;
	private String provider;
	
	private Criteria criteria;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_dashboard);
		
		initializeLayout();
		setClickListners();
		initializeMap();
	}

	private void initializeLayout() {
		appPrefs = getSharedPreferences("qrpatrol_app_prefs", MODE_PRIVATE);
		patrolPrefs = getSharedPreferences("patrol_prefs", MODE_PRIVATE);
		
		patrol = (ImageView)findViewById(R.id.patrol);
		schedule = (ImageView)findViewById(R.id.schedule);
		orders = (ImageView)findViewById(R.id.orders);
		logs = (ImageView)findViewById(R.id.logs);
		
		checkOut = (Button)findViewById(R.id.checkOut);
		settings = (LinearLayout)findViewById(R.id.settings);
		aboutUs = (LinearLayout)findViewById(R.id.aboutUs);
		
		username = (TextView)findViewById(R.id.username);
		officer = (Officer)getIntent().getParcelableExtra("Officer");
		checkpoint = (CheckPoint)getIntent().getParcelableExtra("checkpoint");
		System.err.println("checkpoint"+checkpoint);
		username.setText("Hi "+officer.getFirstName()+" "+officer.getLastName()+"!!!");
	}
	
	private void setClickListners() {
		patrol.setOnClickListener(listener);
		schedule.setOnClickListener(listener);
		orders.setOnClickListener(listener);
		logs.setOnClickListener(listener);
		checkOut.setOnClickListener(listener);
		settings.setOnClickListener(listener);
		aboutUs.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == patrol){
				Intent intent = new Intent(DashboardActivity.this, PatrolActivity.class);
				startActivity(intent);
			}else if(v == schedule){
				Intent intent = new Intent(DashboardActivity.this, ScheduleListActivity.class);
				intent.putExtra("checkpoint", checkpoint);
				intent.putExtra("trigger", "all");
				startActivity(intent);
			}else if(v == orders){
				Intent intent = new Intent(DashboardActivity.this, OrderListActivity.class);
//				intent.putExtra("Officer", officer);
			//	intent.putExtra("checkpoint", checkpoint);
			//	intent.putExtra("trigger", "all");
				startActivity(intent);
			}else if(v == logs){
				Intent intent = new Intent(DashboardActivity.this, LogListActivity.class);
//				intent.putExtra("Officer", officer);
				//	intent.putExtra("checkpoint", checkpoint);
				//	intent.putExtra("trigger", "all");
					startActivity(intent);
				
			}else if(v == checkOut){
				QRPatrolDatabaseHandler dbHandler = new QRPatrolDatabaseHandler(DashboardActivity.this);
				
				// clear preferences
				Editor editor = appPrefs.edit();
				editor.putString("guardId", "");
				editor.putString("pin", "");
				editor.commit();
				
				editor = patrolPrefs.edit();
				editor.clear();
				editor.commit();
				
				//clear schedule table
				dbHandler.deleteSchedule();
				
				//clear event table
				dbHandler.deleteEvents();
				
				//clear officer table
				dbHandler.deleteOfficer();
				
				Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
				startActivity(intent);
				finish();
				
			}else if(v == settings){
				Intent intent = new Intent(DashboardActivity.this, SettingActivity.class);
				startActivity(intent);
				
			}else if(v == aboutUs){
				Intent intent = new Intent(DashboardActivity.this, AboutUsActivity.class);
				startActivity(intent);
				
			}
		}
	};
	
	private void getLastKnownLocation() {
		// TODO Auto-generated method stub
		// Getting Current Location
//		if(locflag == 0){
			Location location = locationManager.getLastKnownLocation(provider);	
			myLat = location.getLatitude();
			
			// Getting longitude of the current location
			myLon = location.getLongitude();
//		}
//		MarkerOptions sta_marker = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude()));
//		googleMap.addMarker(sta_marker);
	}

	@Override
	public void onLocationChanged(Location location) {
		try{
			// TODO Auto-generated method stub
			myLat = location.getLatitude();
		
			// Getting longitude of the current location
			myLon = location.getLongitude();
		}catch(Exception e){
			location = null;
			e.printStackTrace();
		}
		
	/*	if(locflag == 0 && location!=null){
			// Showing the current location in Google Map
			locflag = 1;
			locationManager.requestLocationUpdates(provider, 5000, 0, this);
		}*/
		
		// Zoom in the Google Map
//		googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	private void initializeMap() {
		
			try {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				criteria = new Criteria();
				provider = locationManager.getBestProvider(criteria, true);
				
				boolean enabledGPS = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);
				boolean enabledWiFi = locationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

				// Check if enabled and if not send user to the GSP settings
				if (!enabledGPS && !enabledWiFi) {
					Toast.makeText(DashboardActivity.this, "GPS signal not found",Toast.LENGTH_LONG).show();
					Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
					startActivity(intent);
				}
				else
				{
					locationManager.requestLocationUpdates(provider, 2000, 0, this);
					}
				} catch (Exception e) {
					e.printStackTrace();
					}
						
			
	}
}
