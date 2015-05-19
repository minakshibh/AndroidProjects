package com.twentyfourseven.zira;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SeeDriverLocation extends Activity {
	GoogleMap googleMap;
	double longitude,latitude;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.driver_view_location);
		//getting driver longitude and latitude by GetDriver Location Webservice
		Bundle extras = getIntent().getExtras();
		try{
		if (extras != null) {
			longitude = Double.parseDouble(extras.getString("JsonDriverLongitude"));
			latitude = Double.parseDouble(extras.getString("JsonDriverLatitude"));
			
			Log.i("tag", "Longitude:"+longitude);
			Log.i("tag", "Latitude:"+latitude);
			}
		}
		catch(Exception e)
		{
				
		}

		try {
			// Loading map
			initilizeMap();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * function to load map. If map is not created it will create it for you
	 * */
	private void initilizeMap() {
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.driver_map)).getMap();
			googleMap.setMyLocationEnabled(true);
			googleMap.setTrafficEnabled(true);
			googleMap.getUiSettings().setRotateGesturesEnabled(true);
			googleMap.getUiSettings().setZoomControlsEnabled(false);
			CameraPosition cameraPosition = new CameraPosition.Builder()
					.target(new LatLng(latitude, longitude)).zoom(18).build();

			googleMap.animateCamera(CameraUpdateFactory
					.newCameraPosition(cameraPosition));

			placeMarker();
			// check if map is created successfully or not
			if (googleMap == null) {
				Toast.makeText(getApplicationContext(),
						"Sorry! unable to create maps", Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		initilizeMap();
	}

	// placing marker
	public void placeMarker() {
		// latitude and longitude pass here of driver
		// create marker
		MarkerOptions marker = new MarkerOptions().position(
				new LatLng(latitude, longitude)).title("");

		// Changing marker icon
		marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));

		// adding marker
		googleMap.addMarker(marker);
	}

}
