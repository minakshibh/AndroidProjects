package com.zira.registration;


import java.io.File;
import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;
import com.twentyfourseven.zira.DriverModeActivity;
import com.twentyfourseven.zira.R;
import com.twentyfourseven.zira.VehicleSearchActivity;
import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.login.ForgotPassword;
import com.zira.modal.ProfileInfoModel;
import com.zira.profile.EditBaseProfile;
import com.zira.profile.GetProfile;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;
import com.zira.util.Util;
//import android.provider.DocumentsContract;
import com.zira.util.ZiraParser;


public class DocumentUploadActivity extends Activity implements OnClickListener,AsyncResponseForZira {

	private ImageLoader imageLoader;
	private ProfileInfoModel mProfileInfoModel;
	private ImageView vehicleImage,RCImage,licenceImage,medicalImage;
	private Button done;
	private Bitmap bitmap;
	private String trigger;	
	private String driverRegMethod ="DriverRegistration", uploadImageMethod = "UploadImage", mCurrentPhotoPath, encodedImage;
	private SharedPreferences prefs;
	Editor editor;
	SharedPreferences prefs3;
	private String GetProfile="GetProfiles";
	private ZiraParser ziraParser;
	private CheckBox checkbox_confirm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_document_upload);
		
		VehicleInformationActivity.regActivities.add(DocumentUploadActivity.this);
		
		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		
		initialiseVariable();
		initialiseListener();
	}

	private void initialiseListener() {

		vehicleImage.setOnClickListener(this);
		RCImage.setOnClickListener(this);
		licenceImage.setOnClickListener(this);
		medicalImage.setOnClickListener(this);
		done.setOnClickListener(this);
	}

	private void initialiseVariable() {
		
		ziraParser = new ZiraParser();
		//prefs3 = getSharedPreferences("Ziradata", MODE_PRIVATE);
		//editor=prefs3.edit();
		mProfileInfoModel=SingleTon.getInstance().getProfileInfoModel();
		vehicleImage=(ImageView)findViewById(R.id.vehicleImage);
		RCImage=(ImageView)findViewById(R.id.RCImage);
		licenceImage=(ImageView)findViewById(R.id.licenceImage);
		medicalImage=(ImageView)findViewById(R.id.medicalImage);
		done=(Button)findViewById(R.id.done);
		checkbox_confirm=(CheckBox)findViewById(R.id.checkBox_document);
		if(SingleTon.getInstance().isEdited()){
			
			imageLoader = new ImageLoader(DocumentUploadActivity.this);
			imageLoader.DisplayImage(mProfileInfoModel.getVechile_img_location(),vehicleImage);
	    	imageLoader.DisplayImage(mProfileInfoModel.getRc_img_location(),RCImage);
	    	imageLoader.DisplayImage(mProfileInfoModel.getLicense_img_location(),licenceImage);
	    	imageLoader.DisplayImage(mProfileInfoModel.getMedicalcertificate_img_location(),medicalImage);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.vehicleImage:
			trigger="vehicle";
			selectImage();
			break;

		case R.id.RCImage:
			trigger="rc";
			selectImage();

			break;

		case R.id.licenceImage:
			trigger="drivinglicense";
			selectImage();
			break;

		case R.id.medicalImage:
			trigger="medicalcertificate";
			selectImage();
			break;

		case R.id.done:
			if(checkbox_confirm.isChecked())
			{
				uploadDataToServer();
				}
			else
			{
				Util.alertMessage(DocumentUploadActivity.this, "Please select the Confirm message");
				}
			break;

		default:

			break;
		}
	}

	private void uploadDataToServer() {

		try {
			if(Util.isNetworkAvailable(DocumentUploadActivity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("riderid", prefs.getString("riderid", null)));
			nameValuePairs.add(new BasicNameValuePair("vehicle_make", SingleTon.getInstance().getVehicleMake()));
			nameValuePairs.add(new BasicNameValuePair("vehicle_model",  SingleTon.getInstance().getVehicleModel()));
			nameValuePairs.add(new BasicNameValuePair("vehicle_year",SingleTon.getInstance().getVehicleYear()));
			nameValuePairs.add(new BasicNameValuePair("licensePlateNumber", SingleTon.getInstance().getVehicleLicencePlateNumber()));
			nameValuePairs.add(new BasicNameValuePair("licensePlateCountry",SingleTon.getInstance().getVehicleCountryName()));
			nameValuePairs.add(new BasicNameValuePair("licensePlateState", SingleTon.getInstance().getVehicleStateName()));
			nameValuePairs.add(new BasicNameValuePair("address1",  SingleTon.getInstance().getBg_address1()));
			nameValuePairs.add(new BasicNameValuePair("address2",""));// SingleTon.getInstance().getBg_address2()));
			nameValuePairs.add(new BasicNameValuePair("city",SingleTon.getInstance().getBg_city()));
			nameValuePairs.add(new BasicNameValuePair("state", SingleTon.getInstance().getBg_drivingLicenseState()));
			nameValuePairs.add(new BasicNameValuePair("zipcode", SingleTon.getInstance().getBg_zipcode()));
			nameValuePairs.add(new BasicNameValuePair("drivingLicenseNumber", SingleTon.getInstance().getBg_drivingLicenseNumber()));
			nameValuePairs.add(new BasicNameValuePair("drivingLicenseState", SingleTon.getInstance().getBg_drivingLicenseState()));
			nameValuePairs.add(new BasicNameValuePair("drivingLicenseExpirationDate", SingleTon.getInstance().getBg_LicExoDate()));
			nameValuePairs.add(new BasicNameValuePair("dateofbirth", SingleTon.getInstance().getBgDOB()));
			nameValuePairs.add(new BasicNameValuePair("socialSecurityNumber", SingleTon.getInstance().getBg_socialSecNumber()));

			AsyncTaskForZira mRegister = new AsyncTaskForZira(DocumentUploadActivity.this, driverRegMethod,nameValuePairs, true, "Please wait...");
			mRegister.delegate = (AsyncResponseForZira) DocumentUploadActivity.this;
			mRegister.execute();

		}
		else
		{
			Util.alertMessage(DocumentUploadActivity.this, "Please check your internet connection");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}


	protected void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(DocumentUploadActivity.this);
		builder.setTitle("Add Photo!");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (options[item].equals("Take Photo"))
				{
					Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					// Ensure that there's a camera activity to handle the intent
					if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
						// Create the File where the photo should go
						File photoFile = null;
						try {
							mCurrentPhotoPath = Util.createImageFile();
							photoFile = new File(mCurrentPhotoPath);
						} catch (Exception ex) {
							// Error occurred while creating the File
							ex.printStackTrace();
						}
						// Continue only if the File was successfully created
						if (photoFile != null) {
							takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
									Uri.fromFile(photoFile));
							startActivityForResult(takePictureIntent, 1);
						}
					}
				}
				else if (options[item].equals("Choose from Gallery"))
				{
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
				}
				else if (options[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			String imagePath = "";

			if (requestCode == 1) {
				encodedImage="";
				imagePath = mCurrentPhotoPath;
			} else if (requestCode == 2) {
				encodedImage="";
				Uri selectedImageUri = data.getData();

				Cursor cursor = getContentResolver().query(selectedImageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
				cursor.moveToFirst();

				//Link to the image
				final String imageFilePath = cursor.getString(0);
				cursor.close();
				imagePath = imageFilePath;	
			}

			if(trigger.equals("vehicle")){
				encodedImage = Util.showImage(imagePath, vehicleImage);
			}
			else if(trigger.equals("rc")){
				encodedImage = Util.showImage(imagePath, RCImage);
			}
			else if(trigger.equals("drivinglicense")){
				encodedImage = Util.showImage(imagePath, licenceImage);
			}
			else if(trigger.equals("medicalcertificate")){
				encodedImage = Util.showImage(imagePath, medicalImage);
			}

			try {
				if(Util.isNetworkAvailable(DocumentUploadActivity.this)){
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("RiderId",  prefs.getString("riderid", null)));
				nameValuePairs.add(new BasicNameValuePair("Trigger", trigger));
				nameValuePairs.add(new BasicNameValuePair("Image",encodedImage ));
	
				
				Log.e("IMAGEUPLOAD", nameValuePairs.toString());
				AsyncTaskForZira mUploadImage = new AsyncTaskForZira(DocumentUploadActivity.this, uploadImageMethod,nameValuePairs,false,"");
				mUploadImage.delegate = (AsyncResponseForZira) DocumentUploadActivity.this;
				mUploadImage.execute();
				}
				else
				{
					Util.alertMessage(DocumentUploadActivity.this, "Please check your internet connection");
					}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}          	
		}
	}


	@Override
	public void processFinish(String output, String methodName) {
		// obj;
		//Log.e("doucment", output);
		if(methodName.equalsIgnoreCase(uploadImageMethod))
		{
			Log.e("doucment", output);
			/*try {
				//JSONObject	obj = new JSONObject(output);
			//	String jsonMessage=obj.getString("message");
				//String jsonMessage1=obj.getString("result");
			//	System.err.println("messgage=="+jsonMessage+"result="+jsonMessage1);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		}
		if(methodName.equalsIgnoreCase(driverRegMethod))
		{
			Log.e("driver update", output);
			try {
				JSONObject obj = new JSONObject(output);
				String jsonResult=obj.getString("result");
				String jsonMessage=obj.getString("message");
				
				if(jsonResult.equals("0"))
				{
					//Util.alertMessage(DocumentUploadActivity.this, jsonMessage);
					//editor.clear();
					//editor.commit();
					AlertDialog.Builder alert = new AlertDialog.Builder(DocumentUploadActivity.this);
					alert.setTitle("Zira24/7");
					alert.setMessage("Registration successful");
				
					alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							//System.err.println("messgage=="+jsonMessage+" "+jsonResult);
							//				Toast.makeText(DocumentUploadActivity.this, jsonMessage, 1).show();
											SingleTon.getInstance().setEdited(false);		
											mProfileInfoModel.setVechile_make(SingleTon.getInstance().getVehicleMake());
											mProfileInfoModel.setVechile_model(SingleTon.getInstance().getVehicleModel());
											mProfileInfoModel.setVechile_year(SingleTon.getInstance().getVehicleYear());
											mProfileInfoModel.setLicenseplatenumber(SingleTon.getInstance().getVehicleLicencePlateNumber());
											mProfileInfoModel.setLicenseplatecountry(SingleTon.getInstance().getVehicleCountryName());
											mProfileInfoModel.setLicenseplatestate(SingleTon.getInstance().getVehicleStateName());
											mProfileInfoModel.setAddress(SingleTon.getInstance().getBg_address1()+"");
											mProfileInfoModel.setAddress1(SingleTon.getInstance().getBg_address1());
											mProfileInfoModel.setAddress2("");//SingleTon.getInstance().getBg_address2());
											mProfileInfoModel.setCity(SingleTon.getInstance().getBg_city());
											mProfileInfoModel.setState(SingleTon.getInstance().getBg_drivingLicenseState());
											mProfileInfoModel.setZipcode(SingleTon.getInstance().getBg_zipcode());
											mProfileInfoModel.setDrivinglicensenumber(SingleTon.getInstance().getBg_drivingLicenseNumber());
											mProfileInfoModel.setDrivinglicensestate(SingleTon.getInstance().getBg_drivingLicenseState());
											mProfileInfoModel.setDrivinglicenseexpirationdate(SingleTon.getInstance().getBg_LicExoDate());
											mProfileInfoModel.setDateofbirth(SingleTon.getInstance().getBgDOB());
											mProfileInfoModel.setSocialsecuritynumber(SingleTon.getInstance().getBg_socialSecNumber());
											SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
											getProfileInfo();
											//finish();
						}
					});
					alert.show();
					
					
					/*if(prefs.getString("mode", "").equals("rider"))
					{
					Intent i=new Intent(DocumentUploadActivity.this,VehicleSearchActivity.class);
					finish();
					startActivity(i);
					}
					else if(prefs.getString("mode", "").equals("driver"))
					{
					//Intent i=new Intent(DocumentUploadActivity.this,DriverModeActivity.class);
					finish();
					//startActivity(i);
					}*/
					
					}
				else
				{
					Util.alertMessage(DocumentUploadActivity.this, jsonMessage);		
					}

			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		}
		if(methodName.equalsIgnoreCase(GetProfile))
		{
			Log.e("getprofile=", output);
			mProfileInfoModel = ziraParser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			Intent i=new Intent(DocumentUploadActivity.this,GetProfile.class);
			startActivity(i);
			finish();
		
		}
	}
	private void getProfileInfo() {
		if(Util.isNetworkAvailable(DocumentUploadActivity.this))
		{
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
		AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(DocumentUploadActivity.this, GetProfile,nameValuePair, true, "Please wait...");
		mWebPageTask.delegate = (AsyncResponseForZira) DocumentUploadActivity.this;mWebPageTask.execute();
		
		}else
		{
			Util.alertMessage(DocumentUploadActivity.this, "Please check your internet connection");
			}
		}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(DocumentUploadActivity.this,BackgroundCheckActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
	}
}
