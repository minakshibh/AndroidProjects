package com.equiworx.parent;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.StudentRequest;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class StudentRequestActivity extends Activity implements AsyncResponseForTutorHelper{

	private ArrayList<StudentRequest> arraylist_student;
	private TutorHelperParser parser;
	private ListView listview;
	private StudentAdapter adapter;
	private ImageView back;
	
	
	private String parentId;
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_studentrequest);
		
		fetchlStudentRequest();
		back=(ImageView)findViewById(R.id.back);
		back.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	private void fetchlStudentRequest() {
		
		parser=new TutorHelperParser(StudentRequestActivity.this);
		listview=(ListView)findViewById(R.id.list_request);
		
		parentId = getIntent().getStringExtra("parentID");
		
		if (Util.isNetworkAvailable(StudentRequestActivity.this)){
			/*ParentId/TutorId
			Trigger -- Parent/Tutor*///fetch-student-request.php
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("parent_id", parentId));
			//nameValuePairs.add(new BasicNameValuePair("trigger", "Parent"));
			Log.e("student request", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(StudentRequestActivity.this, "fetch-student-request", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) StudentRequestActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(StudentRequestActivity.this,
					"Please check your internet connection");
		}
	}
	public class StudentAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_name, tv_id;
		private Button iv_connect,iv_reject;
		private StudentRequest studentRequest;
		
		public StudentAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return arraylist_student.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arraylist_student.get(position);
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.connection_row, parent, false);
			}

			studentRequest = arraylist_student.get(position);
			
			tv_name = (TextView)convertView.findViewById(R.id.name);
			tv_id = (TextView)convertView.findViewById(R.id.id);
			iv_connect = (Button)convertView.findViewById(R.id.imageView_connect);
			iv_reject = (Button)convertView.findViewById(R.id.imageView_reject);
			iv_connect.setText("Approve");
			
			tv_id.setText("Student ID : "+studentRequest.getStudentId());
			tv_name.setText(studentRequest.getTutorName()+" has added a student named "+studentRequest.getName()+" under your account. Below are the details: \n\nFees:    $"+studentRequest.getFees()
					+"\nEmail:    "+studentRequest.getEmail()+"\nContact Info:    "+studentRequest.getContactInfo());
			iv_connect.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (Util.isNetworkAvailable(StudentRequestActivity.this)){
					/*ParentId/TutorId approve-student.php
					Trigger -- Parent/Tutor*/
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("request_id", studentRequest.getRequestID()));
					nameValuePairs.add(new BasicNameValuePair("status", "Approved"));
					Log.e("approve", nameValuePairs.toString());
					AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(StudentRequestActivity.this, "approve-student", nameValuePairs, true, "Please wait...");
					mLogin.delegate = (AsyncResponseForTutorHelper) StudentRequestActivity.this;
					mLogin.execute();
				}else {
					Util.alertMessage(StudentRequestActivity.this,
							"Please check your internet connection");
				}	
					
				}
			});
			iv_reject.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if (Util.isNetworkAvailable(StudentRequestActivity.this)){
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("request_id", studentRequest.getRequestID()));
					nameValuePairs.add(new BasicNameValuePair("status", "Rejected"));
					Log.e("approve", nameValuePairs.toString());
					AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(StudentRequestActivity.this, "approve-student", nameValuePairs, true, "Please wait...");
					mLogin.delegate = (AsyncResponseForTutorHelper) StudentRequestActivity.this;
					mLogin.execute();
				}else {
					Util.alertMessage(StudentRequestActivity.this,
							"Please check your internet connection");
				}	
						
					}
				});
			return convertView;
		}
		
		
	}
	@Override
	public void processFinish(String output, String methodName) {
		Log.e("student=", output);
		if(methodName.equals("fetch-student-request")){
			arraylist_student=new ArrayList<StudentRequest>();
			arraylist_student = parser.getStudentRequestInfo(output);
			if(arraylist_student.size()==0)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(StudentRequestActivity.this);
				alert.setTitle("Tutor Helper");
				alert.setMessage("no student requests");
				alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
						finish();
					}
				});	
				alert.show();
			}
			else
			{
			adapter = new StudentAdapter(StudentRequestActivity.this);
			listview.setAdapter(adapter);
		}}
		else if(methodName.equals("approve-student")){
			fetchlStudentRequest();
		}
		
	}
}
