package com.zira.profile;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twentyfourseven.zira.LoginActivity;
import com.twentyfourseven.zira.R;
import com.zira.modal.ProfileInfoModel;
import com.zira.registration.VehicleInformationActivity;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;


public class GetProfile extends Activity implements OnClickListener{
	
	private ImageView riderImg, driverImg;
	private Button btn_edit_rider, btn_edit_driver, btn_signOut;
	private TextView txt_ridername, txt_rideremail, txt_rider_mobilenumber,
			txt_vehicleCompanyName, txt_vehiclename, txtvehicleNumber,
			txt_zipCode, txt_lisenseNo, txt_socialSecurityNumber,textView_address;	
	private ProfileInfoModel mProfileInfoModel;
	private ImageLoader imageLoader;
	private RelativeLayout rel_vehicleinfo1;
	private LinearLayout lin_vehicleinfo2;
	SharedPreferences prefs;
	Editor e;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.new_get_profile);
		
		
		intailizeVariable();
		clickListner();	
		prefs=getSharedPreferences("Zira", MODE_PRIVATE);
		e=prefs.edit();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mProfileInfoModel = SingleTon.getInstance().getProfileInfoModel();
		txt_ridername.setText(mProfileInfoModel.getFirstname());
		txt_rideremail.setText(mProfileInfoModel.getEmail());
		txt_rider_mobilenumber.setText(mProfileInfoModel.getMobile());
		txt_vehicleCompanyName.setText(mProfileInfoModel.getVechile_make());
		txt_vehiclename.setText(mProfileInfoModel.getVechile_model());
		textView_address.setText(mProfileInfoModel.getAddress());
		txtvehicleNumber.setText(mProfileInfoModel.getLicenseplatenumber());
		txt_zipCode.setText("Zip Code:"+" "+mProfileInfoModel.getZipcode());			
		txt_socialSecurityNumber.setText("Social no:"+" "+mProfileInfoModel.getSocialsecuritynumber());
		txt_lisenseNo.setText("Licence no:"+" "+mProfileInfoModel.getDrivinglicensenumber());		
		imageLoader = new ImageLoader(GetProfile.this);
    	imageLoader.DisplayImage(mProfileInfoModel.getImage(),riderImg );
    	imageLoader.DisplayImage(mProfileInfoModel.getVechile_img_location(), driverImg);
    	rel_vehicleinfo1=(RelativeLayout)findViewById(R.id.RelativeLayout_Vehicleinfo1);
    	lin_vehicleinfo2=(LinearLayout)findViewById(R.id.LinearLayout_vehicleLayout);
    	if(mProfileInfoModel.getAddress()==null)
    	{}
    	else
    	{
    	if(mProfileInfoModel.getAddress().equals(""))
    	{
    		rel_vehicleinfo1.setVisibility(View.INVISIBLE);
    		lin_vehicleinfo2.setVisibility(View.INVISIBLE);
    		
    	}
    	}
	}
	
	public void intailizeVariable()	{		
	
		riderImg=(ImageView)findViewById(R.id.imageViewProfilepic);
		driverImg=(ImageView)findViewById(R.id.imageView_vehiclepic);
		btn_edit_rider=(Button)findViewById(R.id.button_profileedit);
		btn_edit_driver=(Button)findViewById(R.id.button_vehicle_edit);
		btn_signOut=(Button)findViewById(R.id.button_signout);
		txt_ridername=(TextView)findViewById(R.id.textView_name);
		txt_rideremail=(TextView)findViewById(R.id.textView_email);
		txt_rider_mobilenumber=(TextView)findViewById(R.id.textView_phoneno);
		txt_vehicleCompanyName=(TextView)findViewById(R.id.textView_vehiclename);
		txt_vehiclename=(TextView)findViewById(R.id.textView_model);
		txtvehicleNumber=(TextView)findViewById(R.id.textView_number);
		txt_zipCode=(TextView)findViewById(R.id.textView_vehiclezipcode);
		txt_lisenseNo=(TextView)findViewById(R.id.textView_Lisenceno);
		txt_socialSecurityNumber=(TextView)findViewById(R.id.textView_socialsecurityno);
		textView_address=(TextView)findViewById(R.id.textView_address);
	}		
	
	public void clickListner() {
		btn_edit_rider.setOnClickListener(this);
		btn_edit_driver.setOnClickListener(this);
		btn_signOut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
	
		switch (v.getId()) {

		case R.id.button_profileedit:
			
			Intent i=new Intent(GetProfile.this,EditBaseProfile.class);
			finish();
			startActivity(i);
		
			break;

		case R.id.button_vehicle_edit:
			
			Intent mIntent=new Intent(GetProfile.this,VehicleInformationActivity.class);	
			SingleTon.getInstance().setEdited(true);
			startActivity(mIntent);
		
			break;

		case R.id.button_signout:
			
			Intent intent = new Intent(GetProfile.this,LoginActivity.class);
			finish();
			//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			startActivity(intent);
			
			e.putString("FBAccessToken","");
			e.putLong("FBAccessExpires",0);		
			e.putString("Login", "");
			e.putString("Userid", "");
			e.putString("_Login", "");
			e.clear();
			e.commit();
		

		default:

			break;
		}
	
	}
	@Override
	public void onBackPressed() {
	finish();
	}
}
