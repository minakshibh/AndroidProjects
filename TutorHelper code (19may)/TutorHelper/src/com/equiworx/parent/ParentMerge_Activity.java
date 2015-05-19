package com.equiworx.parent;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.StudentRequest;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class ParentMerge_Activity extends Activity implements AsyncResponseForTutorHelper{


	private TutorHelperParser parser;
	private EditText userId;
	private Button btn_merge;
	private ImageView back;
	private TextView title;
	
	private String parentId;
	private String password="";
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parent_merge);
		intilizeLayout();
		
		setonClick();
		
	}
	
	private void getOldParent() {

		if (Util.isNetworkAvailable(ParentMerge_Activity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("username", userId.getText().toString().trim()));
			nameValuePairs.add(new BasicNameValuePair("password",password.trim()));
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ParentMerge_Activity.this, "parent-login", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) ParentMerge_Activity.this;
			mLogin.execute();
		}
		else
		{
			Util.alertMessage(ParentMerge_Activity.this,"Please check your internet connection");
			}
		
	}
	
	private void mergeParent() {

		if (Util.isNetworkAvailable(ParentMerge_Activity.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			//nameValuePairs.add(new BasicNameValuePair("", userId.getText().toString().trim()));
			//nameValuePairs.add(new BasicNameValuePair("",password.trim()));
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ParentMerge_Activity.this, "parent-merge", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) ParentMerge_Activity.this;
			mLogin.execute();
		}
		else
		{
			Util.alertMessage(ParentMerge_Activity.this,"Please check your internet connection");
			}
		
	}

	private void intilizeLayout() {
		// TODO Auto-generated method stub
		title=(TextView)findViewById(R.id.title);
		title.setText("Parent Merge");
		back=(ImageView)findViewById(R.id.back);
		userId=(EditText)findViewById(R.id.userid);
		btn_merge=(Button)findViewById(R.id.btn_merge);
	}
	private void setonClick() {
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});	
		btn_merge.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			if(userId.getText().toString().trim().equals(""))
	    	{
	    		Util.alertMessage(ParentMerge_Activity.this, "Please enter userid");
	    	}
	    	else
	    	{
		    	
			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(ParentMerge_Activity.this);
			View promptsView = li.inflate(R.layout.dailog_layout, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ParentMerge_Activity.this);

			alertDialogBuilder.setTitle("Tutor Helper");
			//alertDialogBuilder.setMessage("Enter Password");
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
			
			// set dialog message
			alertDialogBuilder.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	password= userInput.getText().toString();
				    	if(password.equals(""))
				    	{
				    		Util.alertMessage(ParentMerge_Activity.this, "Please enter password");
				    		}
				    	else
				    	{
					    	getOldParent();
					    	}
				    }
				  })
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			
	    	}}
		});
	}
	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		if(methodName.equals("parent-login"))
		{
			try {

					JSONObject jsonChildNode = new JSONObject(output);	
					
					String result = jsonChildNode.getString("result").toString();
					String message = jsonChildNode.getString("message").toString();
					
				
				
					if(result.equals("0")){
						
						AlertDialog.Builder alert = new AlertDialog.Builder(ParentMerge_Activity.this);
						alert.setTitle("Tutor Helper");
						alert.setMessage("Are you sure you want to merge parent");
						alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
						mergeParent();
							
							}
						});	
						alert.setNegativeButton("No",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							
							}
						});	
						alert.show();
					}
					else
					{
						Util.alertMessage(ParentMerge_Activity.this, "Password not match with parentId");	
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				}
		else if(methodName.equals("parent-merge"))
		{
			String result = "123";
			String message="somthing wrong";
			JSONObject jsonChildNode;
			try {
				jsonChildNode = new JSONObject(output);
				result = jsonChildNode.getString("result").toString();
				message = jsonChildNode.getString("message").toString();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
			
		
			if(result.equals("0")){
				Util.alertMessage(ParentMerge_Activity.this, message);
				}
			else
			{
				Util.alertMessage(ParentMerge_Activity.this, message);	
				}
			}
		
	}
}