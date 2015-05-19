package com.zira.util;

import com.google.android.gms.maps.model.LatLng;
import com.zira.modal.MainVehicleModel;

import android.app.AlertDialog;
import android.content.Context;


public class VehicleUtil {


	public static void getVehicleList(Context ctx ,LatLng latLng,String riderID,String distance,String currentTime,String methodNmae) {
		

		MainVehicleModel modalInstance = new MainVehicleModel();
		final String result;
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			//	result = Util.getResponseFromUrl(methodName, nameValuePairs, activity);
				
				
				
			}
		}).start();
		
	
	}	



}
