package com.zira.profile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
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
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.twentyfourseven.zira.LoginActivity;
import com.twentyfourseven.zira.R;

import com.zira.async_tasks.AsyncResponseForZira;
import com.zira.async_tasks.AsyncTaskForZira;
import com.zira.modal.ProfileInfoModel;
import com.zira.util.ImageLoader;
import com.zira.util.SingleTon;
import com.zira.util.Util;
import com.zira.util.ZiraParser;
//import android.provider.DocumentsContract;


public class EditBaseProfile extends Activity implements AsyncResponseForZira{

	private String registerRider = "RegisterRider";
	private String GetProfile="GetProfiles";
	
	
	private ImageView imgProfile;
	private TextView text_name;
	private EditText txtfirstName,txtLastName,txtEmail,txtPassword,txtMobileNumber;
	private Button btnDone;
	private Bitmap bitmap;
	private String encodedImage="";
//	static String str_base64="";
	private Uri selectedImageUri;
	private ProfileInfoModel mProfileInfoModel;
	private ImageLoader imageLoader;
	private SharedPreferences prefs;
	private Editor editor;
	private String mCurrentPhotoPath;
	private ZiraParser ziraParser;
	private Bitmap bitmap_getimage;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {	
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.editprofile_activity);	

		prefs = getSharedPreferences("Zira", MODE_PRIVATE);
		editor = prefs.edit();
		intailizeVariable();
		clickListner();

	}
	public void intailizeVariable() {
		ziraParser = new ZiraParser();
		mProfileInfoModel=SingleTon.getInstance().getProfileInfoModel();
		imgProfile=(ImageView)findViewById(R.id.image_profilePic);
		text_name=(TextView)findViewById(R.id.textView_name);
		txtfirstName=(EditText)findViewById(R.id.editText_firstname);
		txtLastName=(EditText)findViewById(R.id.editText_lastname);
		txtEmail=(EditText)findViewById(R.id.editText_email);
		txtPassword=(EditText)findViewById(R.id.editText_password);
		txtMobileNumber=(EditText)findViewById(R.id.editText_phone);
		btnDone=(Button)findViewById(R.id.button_done);

		text_name.setText(mProfileInfoModel.getFirstname()+" "+mProfileInfoModel.getLastname());
		txtfirstName.setText(mProfileInfoModel.getFirstname());
		txtLastName.setText(mProfileInfoModel.getLastname());
		txtEmail.setText(mProfileInfoModel.getEmail());
		txtPassword.setText(mProfileInfoModel.getPassword());
		txtMobileNumber.setText(mProfileInfoModel.getMobile());
		imageLoader = new ImageLoader(EditBaseProfile.this);
    	imageLoader.DisplayImage(mProfileInfoModel.getImage(), imgProfile);
    	//bitmap_getimage=imageLoader.getBitmapFromURL(mProfileInfoModel.getImage());
    	//DownloadFullFromUrl(mProfileInfoModel.getImage());
    	//convertImagesIntoBase64(bitmap_getimage);
    	if(mProfileInfoModel.getImage()==null)
    	{}
    	else
    	{
    		if(mProfileInfoModel.getImage().equals(""))
    		{}
    		else
    		{
	    	new LoadProfileImageAndConvertingToBase64()
			.execute(mProfileInfoModel.getImage());
    		}
    	}
	}
	public void clickListner()
	{

		imgProfile.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectImage();
			}
		});
		btnDone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.hideKeyboard(EditBaseProfile.this);
				emptyFieldCheck();		
			}
		});
	}
	protected void emptyFieldCheck() {

		if (txtEmail.getText().toString().trim().equals("")) {

			Util.alertMessage(EditBaseProfile.this, "Please Enter Email.");			
		}
		else if (txtPassword.getText().toString().trim().equals("")) {

			Util.alertMessage(EditBaseProfile.this, "Please Enter Password.");
		} 
		else if (txtMobileNumber.getText().toString().trim().equals("")) {

			Util.alertMessage(EditBaseProfile.this,	"Please Enter Mobile Number.");
		}
		else if (txtfirstName.getText().toString().trim().equals("")) {

			Util.alertMessage(EditBaseProfile.this, "Please Enter First Name.");
		} 
		else if (txtLastName.getText().toString().trim().equals("")) {

			Util.alertMessage(EditBaseProfile.this, "Please Enter Last Name.");
		}
		else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString().trim()).matches()
				&& !TextUtils.isEmpty(txtEmail.getText().toString().trim())) {
			Util.alertMessage(EditBaseProfile.this, "Invalid Email");

		} else {
			
			if(Util.isNetworkAvailable(EditBaseProfile.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("userid", prefs.getString("riderid", null)));
			nameValuePairs.add(new BasicNameValuePair("email", txtEmail.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("firstname",txtfirstName.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("lastname", txtLastName.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("password", txtPassword.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("userimage",encodedImage));//"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"));//encodedImage));
			nameValuePairs.add(new BasicNameValuePair("phonenumber", txtMobileNumber.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("creditcardnumber", mProfileInfoModel.getCreditcardnumber()));
			nameValuePairs.add(new BasicNameValuePair("creditcardexpiry",mProfileInfoModel.getCreditcardexpiry()));
			nameValuePairs.add(new BasicNameValuePair("cvv",mProfileInfoModel.getCreditcardcvv()));

			//Log.e("prodile", msg)
			AsyncTaskForZira mFetchVehicleTask = new AsyncTaskForZira(EditBaseProfile.this, registerRider,nameValuePairs, true, "Please wait...");
			mFetchVehicleTask.delegate = (AsyncResponseForZira)EditBaseProfile.this;
			mFetchVehicleTask.execute();
			}
			else
			{
				Util.alertMessage(EditBaseProfile.this, "Please check your internet connection");
				}
		}

	}
	
	protected void selectImage() {

		final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		AlertDialog.Builder builder = new AlertDialog.Builder(EditBaseProfile.this);
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
				Log.d("CameraDemo", "Pic saved");
				bitmap = (Bitmap) data.getExtras().get("data"); 
				imgProfile.setImageBitmap(bitmap);
				convertImagesIntoBase64(bitmap);

			} 
			else if (requestCode == 2) {
				encodedImage="";
				selectedImageUri = data.getData();
				String selectedImagePath = getPath(EditBaseProfile.this,selectedImageUri);
				Log.e("tag",""+selectedImagePath);

				Bitmap bm=BitmapFactory.decodeFile(selectedImagePath);
				Bitmap bm2=ExifUtils.rotateBitmap(selectedImagePath, bm);
				imgProfile.setImageBitmap(bm2);				
				convertImagesIntoBase64(bm2);
			}*/	 
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
			
			encodedImage = Util.showImage(imagePath, imgProfile);
			Log.d("encodedImage=",encodedImage);
		}
	}		

	private void convertImagesIntoBase64(Bitmap bitmap) {
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream(); 
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
		byte[] ba = bao.toByteArray();
		encodedImage = Base64.encodeToString(ba, Base64.DEFAULT);
		Log.e("tag","base64:"+encodedImage); 
	}
	@Override
	public void processFinish(String output, String methodName) {
		System.err.println("profilerider=="+output);
		JSONObject obj;
		if(methodName.equals(registerRider))
		{
		try {
			obj = new JSONObject(output);
			String jsonMessage=obj.getString("message");
			String jsonresult=obj.getString("result");
			if(jsonresult.equals("0"))
/*//			Toast.makeText(EditBaseProfile.this, jsonMessage, 1).show();			
			mProfileInfoModel.setFirstname(txtfirstName.getText().toString());
			mProfileInfoModel.setLastname(txtLastName.getText().toString());			
			mProfileInfoModel.setPassword(txtPassword.getText().toString());
			mProfileInfoModel.setMobile(txtMobileNumber.getText().toString());			
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			text_name.setText(mProfileInfoModel.getFirstname()+" "+mProfileInfoModel.getLastname());*/
			{
				getProfileInfo();
			}
			
		} catch (JSONException e) {
			
			e.printStackTrace();
		}
		}
		if(methodName.equals(GetProfile))
		{
			mProfileInfoModel = ziraParser.profileInfo(output);
			SingleTon.getInstance().setProfileInfoModel(mProfileInfoModel);
			finish();
			Intent i=new Intent(EditBaseProfile.this,GetProfile.class);
			startActivity(i);
		}

	}
	
	
	private void getProfileInfo() {
		if(Util.isNetworkAvailable(EditBaseProfile.this))
		{
		ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(1);
		nameValuePair.add(new BasicNameValuePair("UserId", prefs.getString("riderid", "")));
		AsyncTaskForZira mWebPageTask = new AsyncTaskForZira(EditBaseProfile.this, GetProfile,nameValuePair, true, "Please wait...");
		mWebPageTask.delegate = (AsyncResponseForZira) EditBaseProfile.this;mWebPageTask.execute();
		
		}else
		{
			Util.alertMessage(EditBaseProfile.this, "Please check your internet connection");
			}
		}

	private class LoadProfileImageAndConvertingToBase64 extends
	AsyncTask<String, Void, Bitmap> {
		protected Bitmap doInBackground(String... urls) {
	String urldisplay = urls[0];
	Bitmap mIcon11 = null;
	try {
		InputStream in = new java.net.URL(urldisplay).openStream();
		mIcon11 = BitmapFactory.decodeStream(in);
	} catch (Exception e) {
		Log.e("Error", e.getMessage());
		e.printStackTrace();
	}
	return mIcon11;
}

protected void onPostExecute(Bitmap result) {
	try{
	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	result.compress(Bitmap.CompressFormat.JPEG, 60,
			byteArrayOutputStream);
	byte[] byteArray = byteArrayOutputStream.toByteArray();
	encodedImage= Base64.encodeToString(byteArray, Base64.DEFAULT);
	System.err.println("encodedImage="+encodedImage);
	}
	catch(Exception e)
	{
		
	}
}
}
	//this getPath function get the path of from media provider,download provider, external storage provider etc.
//	@SuppressLint("NewApi")
	
//	commented by Minakshi
//	@TargetApi(Build.VERSION_CODES.KITKAT)
	/*public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2+1;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] {
						split[1]
				};

				return getDataColumn(context, contentUri, selection, selectionArgs);
			}
		}
		// MediaStore (and general)
		else 
		if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}
*/
	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 *
	 * @param context The context.
	 * @param uri The Uri to query.
	 * @param selection (Optional) Filter used in the query.
	 * @param selectionArgs (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	/*public static String getDataColumn(Context context, Uri uri, String selection,
			String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = {
				column
		};

		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}


	*//**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 *//*
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	*//**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 *//*
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	*//**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 *//*
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	*//**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 *//*
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}*/
}
