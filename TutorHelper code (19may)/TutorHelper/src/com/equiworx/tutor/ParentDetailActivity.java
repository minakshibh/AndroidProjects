package com.equiworx.tutor;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.StudentList;
import com.equiworx.student.AddStudent;
import com.equiworx.student.EditStudentActivity;
import com.equiworx.student.StudentDetailActivity;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class ParentDetailActivity extends Activity implements AsyncResponseForTutorHelper{
		
	 private SharedPreferences tutorPrefs;
	 private ImageView add;
	 private TextView txt_contactno,txt_email,txt_noofstudent,txt_balance,txt_address,txt_parentid;
	 private ArrayList<StudentList> arraylist_student=new ArrayList<StudentList>();
	 private StudentList studentList;
	 private StudentAdapter adapter;
	 private ListView listView;
	 private String parentId, tutorId;
	 private EditText txtNotes;
	 private ImageView imgview_back;
	 private TextView tv_title;
	 private TutorHelperParser parser;
	 private LinearLayout layout_history;
	 
	 
	 protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_parentdetails);
		
		initializelayout();
		fetchStudentList();
		onclickListenser();
}

	private void onclickListenser() {
		imgview_back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
				finish();
				
			}
		});
		layout_history.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intetn=new Intent(ParentDetailActivity.this,HistoryParentsActivity.class);
				startActivity(intetn);
			}
		});
		txt_contactno.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					String uri = "tel:" + txt_contactno.getText().toString().trim();
					 Intent intent = new Intent(Intent.ACTION_CALL);
					 intent.setData(Uri.parse(uri));
					 startActivity(intent);
				}
			});
		txt_email.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				            "mailto",txt_email.getText().toString().trim(), null));
					startActivity(Intent.createChooser(emailIntent, "Send email..."));
				}
			});
		add.setOnClickListener(new View.OnClickListener() {
		public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ParentDetailActivity.this,AddStudent.class);
				intent.putExtra("detailadd", "value");
		/*		intent.putExtra("parentID", tutorPrefs.getString("p_parentId", "0"));
				intent.putExtra("pname", tutorPrefs.getString("p_name", "0"));
				intent.putExtra("pemail", tutorPrefs.getString("p_email", "0"));
				intent.putExtra("pcontact", tutorPrefs.getString("p_contactno", "0"));
				intent.putExtra("paddress", tutorPrefs.getString("p_address", "0"));
				intent.putExtra("pgender", tutorPrefs.getString("p_gender", "0"));*/
				startActivity(intent);
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
			studentList = arraylist_student.get(position);
			Intent i=new Intent(ParentDetailActivity.this,StudentDetailActivity.class);
			i.putExtra("name", studentList.getName());
			i.putExtra("email", studentList.getEmail());	
			i.putExtra("address", studentList.getAddress());
			i.putExtra("phone", studentList.getContactInfo());
			i.putExtra("fees", studentList.getFees());
			i.putExtra("notes", studentList.getNotes());
			i.putExtra("gender", studentList.getGender());
			i.putExtra("parentid", tutorPrefs.getString("p_name", "0"));
			i.putExtra("studentid", studentList.getStudentId());
			
			startActivity(i);
				
			}
		});
	}

	private void fetchStudentList() {
		if (Util.isNetworkAvailable(ParentDetailActivity.this)){
			/*ParentId/TutorId
			Trigger -- Parent/Tutor*///tutor_helper/fetch-parent.php//
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			nameValuePairs.add(new BasicNameValuePair("parent_id", parentId));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", tutorId));
			Log.e("students list", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ParentDetailActivity.this, "fetch-student", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) ParentDetailActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(ParentDetailActivity.this,
					"Please check your internet connection");
		}
		
	}
	private void initializelayout() {
		
		studentList=new StudentList();
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText(tutorPrefs.getString("p_name", "0"));
		imgview_back=(ImageView)findViewById(R.id.back);
		parser=new TutorHelperParser(ParentDetailActivity.this);
		listView=(ListView)findViewById(R.id.listView_student);
		txt_contactno=(TextView)findViewById(R.id.contactno);
		txt_email=(TextView)findViewById(R.id.email);
		txt_noofstudent=(TextView)findViewById(R.id.studentno);
		txt_balance=(TextView)findViewById(R.id.balance);
		txt_address=(TextView)findViewById(R.id.Address);
		txt_parentid=(TextView)findViewById(R.id.parentid);
		txtNotes = (EditText)findViewById(R.id.editText_notes);
		layout_history=(LinearLayout)findViewById(R.id.layout_history);
		add=(ImageView)findViewById(R.id.add);
		String str=""+tutorPrefs.getString("p_contactno", "0");
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		txt_contactno.setText(content);
	
		
		String str_email=""+tutorPrefs.getString("p_email", "0");
		SpannableString content1 = new SpannableString(str_email);
		content1.setSpan(new UnderlineSpan(), 0, str_email.length(), 0);
		txt_email.setText(content1);
	/*
		ed.putString("p_name", parent.getName());
		ed.putString("p_contactno", parent.getContactNumber());
		ed.putString("p_email", parent.getEmail());
		ed.putString("p_noofstudent", parent.getStudentCount());
		ed.putString("p_balance", parent.getOutstandingBalance());
		ed.putString("p_address", parent.getAddress());
		ed.putString("p_notes", parent.getNotes());
		ed.putString("p_tutorId", tutorId);
		ed.putString("p_parentId", parent.getParentId());
		ed.putString("p_lessons", parent.getLessonCount());*/
		
		txt_noofstudent.setText(": "+tutorPrefs.getString("p_noofstudent", "0"));
		txt_balance.setText(":$"+tutorPrefs.getString("p_balance", "0"));
		txt_address.setText(":"+tutorPrefs.getString("p_address", "0"));
		parentId =  tutorPrefs.getString("p_parentId", "0");
		tutorId  = tutorPrefs.getString("tutorID", "0");
		txt_parentid.setText("Parent Id : "+parentId);
		
		txtNotes.setText(getIntent().getStringExtra("notes"));
	}
	public class StudentAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_name, tv_email,tv_note,tv_contactno;
		private ImageView img_edit;
		private StudentList studentlist;
		
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
			    convertView = inflater.inflate(R.layout.studentlist_row, parent, false);
			}

			studentlist = arraylist_student.get(position);
			TextView	lbl_notes =(TextView)convertView.findViewById(R.id.lbl_notes);
			lbl_notes.setVisibility(View.VISIBLE);
			TextView	lbl_address =(TextView)convertView.findViewById(R.id.lbl_address);
			lbl_address.setVisibility(View.GONE);
			
			tv_name = (TextView)convertView.findViewById(R.id.studentname);
			tv_email = (TextView)convertView.findViewById(R.id.Email);
			tv_note = (TextView)convertView.findViewById(R.id.Notes);
			tv_contactno = (TextView)convertView.findViewById(R.id.contactno);
			img_edit= (ImageView)convertView.findViewById(R.id.editImg);
			tv_contactno.setText(": "+studentlist.getContactInfo());
			
			tv_name.setText(studentlist.getName());
			tv_email.setText(": "+studentlist.getEmail());
			tv_note.setText(": "+studentlist.getNotes());
		
			//System.err.println("adpter name="+studentlist.getName());
			img_edit.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				studentlist = arraylist_student.get(position);
				Intent i=new Intent(ParentDetailActivity.this,EditStudentActivity.class);
				i.putExtra("trigger", "Tutor");
				i.putExtra("name", studentlist.getName());
				i.putExtra("email", studentlist.getEmail());
				i.putExtra("contactno", studentlist.getContactInfo());
				i.putExtra("fees", studentlist.getFees());
				i.putExtra("notes", studentlist.getNotes());
				i.putExtra("gender", studentlist.getGender());
				i.putExtra("parentid", studentlist.getParentId());
				i.putExtra("studentid", studentlist.getStudentId());					
				startActivity(i);
					
				}
			});
			return convertView;
		}
	}
	public void processFinish(String output, String methodName) {
	
		Log.e(methodName,output);
		
		if(methodName.equals("fetch-student")){
		
			arraylist_student = parser.getStudentlist(output);
			
			adapter = new StudentAdapter(ParentDetailActivity.this);
			listView.setAdapter(adapter);
		}
		
		
	}
}