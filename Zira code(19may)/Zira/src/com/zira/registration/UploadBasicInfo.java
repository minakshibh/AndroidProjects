package com.zira.registration;


import java.io.File;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.twentyfourseven.zira.R;
import com.zira.util.Util;

public class UploadBasicInfo extends Activity{
	ImageView rider_img,btn_cancel;
	ImageView btn_next;
	EditText edit_fisrtname,edit_LastName;
	Bitmap bitmap;
	String encodedImage="", mCurrentPhotoPath;
	SharedPreferences reg_prefs;
	static String str_base64="";
	private Uri selectedImageUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.rider_register_two);
		intializevariables();
		clickListner();

		reg_prefs=getSharedPreferences("reg_Zira", MODE_PRIVATE);

		/*encodedImage = prefs.getString("userimage", "");
		edit_fisrtname.setText(prefs.getString("firstname", ""));
		edit_LastName.setText(prefs.getString("lastname", ""));

		if(!encodedImage.equals("")){
			byte[] decodedString = Base64.decode(encodedImage.trim(),Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(
                    decodedString, 0, decodedString.length);

            //setting the decodedByte to ImageView
            rider_img.setImageBitmap(decodedByte);
		}*/
	}

	private  void intializevariables()
	{
		edit_fisrtname=(EditText)findViewById(R.id.editText_riderFirstName);
		edit_LastName=(EditText)findViewById(R.id.editText_riderLastName);
		rider_img=(ImageView)findViewById(R.id.imageView_rider_uploadImage);
		btn_next=(ImageView)findViewById(R.id.button_rider_Next_registwo);
		btn_cancel=(ImageView)findViewById(R.id.button_rider_back_registwo);
	}

	private void clickListner()
	{
		rider_img.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		btn_next.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Util.hideKeyboard(UploadBasicInfo.this);
				String gettingFirstName=edit_fisrtname.getText().toString();
				String gettingSecondName=edit_LastName.getText().toString();
				if(gettingFirstName.equals("") )
				{
					Util.alertMessage(UploadBasicInfo.this, "Please enter first name.");
				}
				else if( gettingSecondName.equals("")){
					
					Util.alertMessage(UploadBasicInfo.this, "Please enter last name.");
				}
				else if(encodedImage.equals(""))
				{
					Util.alertMessage(UploadBasicInfo.this, "Please upload profile image.");
				}
					
				else
				{
					Editor e=reg_prefs.edit();
					e.putString("userimage", encodedImage);
					e.putString("firstname", edit_fisrtname.getText().toString());
					e.putString("lastname", edit_LastName.getText().toString());
					e.commit();

					Intent i=new Intent(UploadBasicInfo.this,AddCreditInfo.class);
					startActivity(i);
				}
			}
		});
		btn_cancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				finish();
			}
		});
	}
	protected void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(UploadBasicInfo.this);
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
			/*if (requestCode == 1) {
				encodedImage="";

				encodedImage = Util.showImage(mCurrentPhotoPath, rider_img);	        

			} else if (requestCode == 2) {
				encodedImage="";
				selectedImageUri = data.getData();
				Log.e("got data","from gallery");

				Cursor cursor = getContentResolver().query(selectedImageUri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
				cursor.moveToFirst();

				//Link to the image
				final String imageFilePath = cursor.getString(0);
				cursor.close();

				encodedImage = Util.showImage(imageFilePath, rider_img);	
			}	 */
			
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
			
			encodedImage = Util.showImage(imagePath, rider_img);
			
			System.err.println("encodedImage=="+encodedImage);
		}
	}



}
