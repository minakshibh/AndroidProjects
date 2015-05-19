package com.equiworx.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.view.Window;
import com.equiworx.lesson.LessonRequestActivity;
import com.equiworx.parent.ConnectionRequests;
import com.equiworx.tutorhelper.R;


public class NotificationParentActivity extends Activity{
	String message;
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lessonrequest);
	
	message=getIntent().getStringExtra("message");
	if(message.contains("request lesson"))
	{
		Intent i=new Intent(NotificationParentActivity.this,LessonRequestActivity.class);
		startActivity(i);
	//	nav have requested to give a lesson to your child(hi io)  on the topic . 
		//Kindly check in order to approve/reject the same.

		
	}
	else if(message.contains("Accepted the request for lesson"))
	{
		alertMessage(message);
		//Mr. test3 have Accepted the request for lesson on 


	}
	else if(message.contains("connection request"))
	{
		//amrik singh has sent you a connection request. Please check the mobile app in order to approve/reject the same.

		Intent i=new Intent(NotificationParentActivity.this,ConnectionRequests.class);
		startActivity(i);
	}
	else if(message.contains("connection request approved"))
	{
		alertMessage(message);
		//Your connection request has been approved by  zzzz. You can contact  on 1234568836

	}
	
	else if(message.contains("Student request"))
	{
		//Tutor Request to parent== A new student named happy have been added to your account by nav. Please check the mobile app in order to approve/reject the same.

	}
	else if(message.contains("Accepted Student request"))
	{
		//Your request for student creation for the student named hvc have been Approved by Mr. test3

	}
		
		
 
	
	/*1.lesson 
				A)Tutor
				Lesson Request Tutor to Parent==>>nav have requested to give a lesson to your child(hi io)  on the topic . Kindly check in order to approve/reject the same.
				Parent Accept lesson== Mr. test3 have Accepted the request for lesson on 
				
				B)parent
				Lesson parent side=no notification

				2.tutor connection request==
				Tutor Request==
				Parent Accept==Your connection request has been approved by  zzzz. You can contact  on 12345688

				3.Student Request==
				ecisting
				A new student named hcfhfj have been added to your account by nav. Please check the mobile app in order to approve/reject the same.
				Tutor Student Request==nav has sent you a connection request. Please check the mobile app in order to approve/reject the same.
     			Parent Accept Student Request==Your request for student creation for the student named hvc have been Approved by Mr. test3

				issue ------
				1.connection string in student request.
				2.not notification connection request with parentid=-1
				3.parent lesson request to tutor notification*/


				 

}
	
	public void alertMessage(String str)
	{
		
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle("Tutor Helper");
		alert.setMessage(str);
		alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface arg0, int arg1) {
				
				finish();
				}
			});	
	  alert.show();
	}
}