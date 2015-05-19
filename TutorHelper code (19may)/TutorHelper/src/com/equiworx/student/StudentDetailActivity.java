package com.equiworx.student;

import com.equiworx.tutorhelper.R;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class StudentDetailActivity extends Activity{
	private ImageView back;
	private TextView name,email,fees,address,contact,notes,parentame,studentid,edit;
	private LinearLayout lay_call,lay_email;
	
	
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);	
			setContentView(R.layout.activity_studentdetail);
			initializelayout();
			fetchStudentDetails();
			onclickListenser();
}

	private void fetchStudentDetails() {
		name.setText(""+getIntent().getStringExtra("name"));
		try{
		String str=getIntent().getStringExtra("email");
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		email.setText(content);
		
		address.setText(""+getIntent().getStringExtra("address"));
		
		String str1=getIntent().getStringExtra("phone");
		SpannableString content1 = new SpannableString(str1);
		content1.setSpan(new UnderlineSpan(), 0, str1.length(), 0);
		contact.setText(content1);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		//contact.setText(""+getIntent().getStringExtra("phone"));
		fees.setText("$ "+getIntent().getStringExtra("fees"));
		notes.setText(getIntent().getStringExtra("notes"));
		parentame.setText(""+getIntent().getStringExtra("parentid"));
		studentid.setText("Student Id :"+getIntent().getStringExtra("studentid"));
		
	}

	private void onclickListenser() {
	back.setOnClickListener(new View.OnClickListener() {
	public void onClick(View v) {
		finish();
			
		}
	});
	lay_call.setOnClickListener(new View.OnClickListener() {
	public void onClick(View v) {
		String uri = "tel:" + contact.getText().toString().trim();
		 Intent intent = new Intent(Intent.ACTION_CALL);
		 intent.setData(Uri.parse(uri));
		 startActivity(intent);
			
		}
	});	
	lay_email.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
		            "mailto",email.getText().toString().trim(), null));
			startActivity(Intent.createChooser(emailIntent, "Send email..."));
				
			}
		});	
	edit.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
	
		Intent i=new Intent(StudentDetailActivity.this,EditStudentActivity.class);
		i.putExtra("name",getIntent().getStringExtra("name") );
		i.putExtra("email",getIntent().getStringExtra("email") );
		i.putExtra("contactno", getIntent().getStringExtra("phone"));
		i.putExtra("fees",getIntent().getStringExtra("fees") );
		i.putExtra("address",getIntent().getStringExtra("address") );
		i.putExtra("gender",getIntent().getStringExtra("gender") );
		i.putExtra("notes", getIntent().getStringExtra("notes"));
		i.putExtra("parentid", getIntent().getStringExtra("parentid"));
		i.putExtra("studentid", getIntent().getStringExtra("studentid"));
		i.putExtra("check", "Parent");
		startActivity(i);
		finish();
				
			}
		});
	}


	private void initializelayout() {
	
		back=(ImageView)findViewById(R.id.back);
		edit=(TextView)findViewById(R.id.edit);
		name=(TextView)findViewById(R.id.name);
		email=(TextView)findViewById(R.id.email);
		address=(TextView)findViewById(R.id.address);
		notes=(TextView)findViewById(R.id.notes);
		contact=(TextView)findViewById(R.id.contact);
		fees=(TextView)findViewById(R.id.fees);
		parentame=(TextView)findViewById(R.id.parentname);
		studentid=(TextView)findViewById(R.id.studentid);
		lay_call=(LinearLayout)findViewById(R.id.lay_contact);
		lay_email=(LinearLayout)findViewById(R.id.lay_email);
		
		
	}
}