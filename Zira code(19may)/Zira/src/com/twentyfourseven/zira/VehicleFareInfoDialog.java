package com.twentyfourseven.zira;

import java.text.DecimalFormat;
import com.zira.modal.MainVehicleModel;
import com.zira.util.SingleTon;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;

public class VehicleFareInfoDialog extends Activity {

	private TextView etaTextView,minFareTextView,maxSizeTextView,baseFareTextView,minuteTextView;
	private TextView milesTextView; 
	public static TextView cancelChargesTextView;
	private MainVehicleModel vehicleModel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.vehicle_fare_info_dialog);
		
		initialiseVariable();
		try {
			initialiseListener();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String convertStringToFloat(String string) {
		
		String convertedFloat = null;
		float getValue=Float.valueOf(string);		
		convertedFloat=String.valueOf(getValue);	
		return convertedFloat;
	}
	private void initialiseListener() {	
		
		try{

		if(VehicleSearchActivity.str_vehicleType.equalsIgnoreCase("ZiraE")){		
			
			etaTextView.setText("-");
			
			Log.e("mileprice=", convertStringToFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice()));
			Log.e("capacity=", vehicleModel.getZoneInfoList().get(0).getVehicleCapacity());
			Log.e("baseprice=",vehicleModel.getZoneInfoList().get(0).getBasePrice());
			Log.e("minprice=", vehicleModel.getZoneInfoList().get(0).getMinprice());
			//Log.e("CANCELLATION ziraE=", vehicleModel.getCancellationFee());
			
			String minfare=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice()));
			minFareTextView.setText("$ "+minfare);
			maxSizeTextView.setText(vehicleModel.getZoneInfoList().get(0).getVehicleCapacity()+" PPL");
			
			String baseprice=new DecimalFormat("##.##").format(Float.parseFloat((vehicleModel.getZoneInfoList().get(0).getBasePrice())));
			baseFareTextView.setText("$ "+baseprice+" BASE FARE");
			
			String minprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMinprice()));
			minuteTextView.setText("$ "+minprice+"/MIN");
			
			String mileprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getMilesPrice()));
			milesTextView.setText("$ "+mileprice+"/Miles");
			
			String CANCELLATION=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(0).getCancellationcharges()));
			cancelChargesTextView.setText("CANCELLATION CHARGES : "+ "$ "+CANCELLATION);
		
			Log.i("Minprice()))"+vehicleModel.getZoneInfoList().get(0).getMinprice(),vehicleModel.getZoneInfoList().get(1).getVehicleCapacity());
		}
		else if(VehicleSearchActivity.str_vehicleType.equalsIgnoreCase("ZiraPlus")){	
			
			etaTextView.setText("-");
			Log.e("mileprice=", convertStringToFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice()));
			Log.e("capacity=", vehicleModel.getZoneInfoList().get(1).getVehicleCapacity());
			Log.e("baseprice=",vehicleModel.getZoneInfoList().get(1).getBasePrice());
			Log.e("minprice=", vehicleModel.getZoneInfoList().get(1).getMinprice());
			//Log.e("CANCELLATION zira plus=", vehicleModel.getCancellationFee());
			
			String minfare=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice()));
			minFareTextView.setText("$ "+minfare);
			maxSizeTextView.setText(vehicleModel.getZoneInfoList().get(1).getVehicleCapacity()+" PPL");
			
			String baseprice=new DecimalFormat("##.##").format(Float.parseFloat((vehicleModel.getZoneInfoList().get(1).getBasePrice())));
			baseFareTextView.setText("$ "+baseprice+" BASE FARE");
			
			String minprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMinprice()));
			minuteTextView.setText("$ "+minprice+"/MIN");
			
			String mileprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getMilesPrice()));
			milesTextView.setText("$ "+mileprice+"/Miles");
			
			String CANCELLATION=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(1).getCancellationcharges()));
			cancelChargesTextView.setText("CANCELLATION CHARGES : "+ "$ "+CANCELLATION);
		}
		else if(VehicleSearchActivity.str_vehicleType.equalsIgnoreCase("ZiraTaxi")){		
			etaTextView.setText("-");
			
			Log.e("mileprice=", convertStringToFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice()));
			Log.e("capacity=", vehicleModel.getZoneInfoList().get(2).getVehicleCapacity());
			Log.e("baseprice=",vehicleModel.getZoneInfoList().get(2).getBasePrice());
			Log.e("minprice=", vehicleModel.getZoneInfoList().get(2).getMinprice());
			//Log.e("CANCELLATION zirataxi=", vehicleModel.getCancellationFee());
			
			String minfare=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice()));
			minFareTextView.setText("$ "+minfare);
			maxSizeTextView.setText(vehicleModel.getZoneInfoList().get(2).getVehicleCapacity()+" PPL");
			
			String baseprice=new DecimalFormat("##.##").format(Float.parseFloat((vehicleModel.getZoneInfoList().get(2).getBasePrice())));
			baseFareTextView.setText("$ "+baseprice+" BASE FARE");
			
			String minprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMinprice()));
			minuteTextView.setText("$ "+minprice+"/MIN");
			
			String mileprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getMilesPrice()));
			milesTextView.setText("$ "+mileprice+"/Miles");
			
			String CANCELLATION=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(2).getCancellationcharges()));
			cancelChargesTextView.setText("CANCELLATION CHARGES : "+"$ "+ CANCELLATION);
		}
		
		else if(VehicleSearchActivity.str_vehicleType.equalsIgnoreCase("ZiraLux")){		
			etaTextView.setText("-");
			
			Log.e("mileprice=", convertStringToFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice()));
			Log.e("capacity=", vehicleModel.getZoneInfoList().get(3).getVehicleCapacity());
			Log.e("baseprice=",vehicleModel.getZoneInfoList().get(3).getBasePrice());
			Log.e("minprice=", vehicleModel.getZoneInfoList().get(3).getMinprice());
			//Log.e("CANCELLATION ziralux=", vehicleModel.getCancellationFee());
			
			String minfare=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice()));
			minFareTextView.setText("$ "+minfare);
			maxSizeTextView.setText(vehicleModel.getZoneInfoList().get(3).getVehicleCapacity()+" PPL");
			
			String baseprice=new DecimalFormat("##.##").format(Float.parseFloat((vehicleModel.getZoneInfoList().get(3).getBasePrice())));
			baseFareTextView.setText("$ "+baseprice+" BASE FARE");
			
			String minprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMinprice()));
			minuteTextView.setText("$ "+minprice+"/MIN");
			
			String mileprice=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getMilesPrice()));
			milesTextView.setText("$ "+mileprice+"/Miles");
			
			String CANCELLATION=new DecimalFormat("##.##").format(Float.parseFloat(vehicleModel.getZoneInfoList().get(3).getCancellationcharges()));
			cancelChargesTextView.setText("CANCELLATION CHARGES : "+"$ "+ CANCELLATION);
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void initialiseVariable() {
		
		vehicleModel=SingleTon.getInstance().getMainVehicleModel();
		etaTextView=(TextView)findViewById(R.id.etaTextView);
		minFareTextView=(TextView)findViewById(R.id.minFareTextView);
		maxSizeTextView	=(TextView)findViewById(R.id.maxSizeTextView);	
		baseFareTextView=(TextView)findViewById(R.id.baseFareTextView);
		minuteTextView=(TextView)findViewById(R.id.minuteTextView);
		milesTextView=(TextView)findViewById(R.id.milesTextView);
		cancelChargesTextView=(TextView)findViewById(R.id.cancelChargesTextView); 
		
	}

	
}
