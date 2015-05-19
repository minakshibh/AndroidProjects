package com.equiworx.lesson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.equiworx.model.StudentList;
import com.equiworx.tutorhelper.R;

public class LessonDetailsActivity extends Activity {
	private TextView tv_title,tv_name,tv_description,tv_stime,tv_duration,tv_sdate,tv_edate,tv_recuring;
	private TextView 	sun ,mon ,tue ,wed ,thur,fri ,sat ;
	private ImageView back;
	//private StudentAdapter adapter;
	//private ListView listView;
	private LinearLayout student_row,main_layout;
	private StudentList studentlist;
	public  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lesson_details);
		
		intializelayout();
		setOnClickListners();
		getLessonDetail();
		getStudentsLayout();
}
	private void intializelayout() {
		studentlist=new StudentList();
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Lesson Details");
		back=(ImageView)findViewById(R.id.back);
		student_row=(LinearLayout)findViewById(R.id.student_row);
		main_layout=(LinearLayout)findViewById(R.id.main_layout);
		tv_name=(TextView)findViewById(R.id.textView1);
		tv_description=(TextView)findViewById(R.id.textView2);
		tv_stime=(TextView)findViewById(R.id.textView3);
		//tv_etime=(TextView)findViewById(R.id.textView4);
		tv_duration=(TextView)findViewById(R.id.textView5);
		
		tv_sdate=(TextView)findViewById(R.id.textView6);
		tv_edate=(TextView)findViewById(R.id.textView7);
		tv_recuring=(TextView)findViewById(R.id.textView8);
		//listView=(ListView)findViewById(R.id.listView);
	
		sun = (TextView)findViewById(R.id.sun);
		mon = (TextView)findViewById(R.id.mon);
		tue = (TextView)findViewById(R.id.tue);
		wed = (TextView)findViewById(R.id.wed);
		thur = (TextView)findViewById(R.id.thur);
		fri = (TextView)findViewById(R.id.fri);
		sat = (TextView)findViewById(R.id.sat);
			
	}
	private void getStudentsLayout() {
		//adapter = new StudentAdapter(LessonDetailsActivity.this);
		//listView.setAdapter(adapter);
		
		
		for(int i=0;i<MyLessonActivity.arraylist_studentlist.size();i++)
		{
			studentlist = MyLessonActivity.arraylist_studentlist.get(i);
			
		
			if(student_row == null){
				View convertView = getLayoutInflater().inflate(R.layout.studentlist_row, main_layout, false);
				TextView	lbl_notes =(TextView)convertView.findViewById(R.id.lbl_notes);
				lbl_notes.setVisibility(View.VISIBLE);
				lbl_notes.setText("Address");
				TextView	lbl_address =(TextView)convertView.findViewById(R.id.lbl_address);
				lbl_address.setVisibility(View.GONE);
				
				TextView tv_name = (TextView)convertView.findViewById(R.id.studentname);
				TextView tv_email = (TextView)convertView.findViewById(R.id.Email);
				TextView tv_note = (TextView)convertView.findViewById(R.id.Notes);
				TextView tv_contactno = (TextView)convertView.findViewById(R.id.contactno);
				ImageView img_view=(ImageView)convertView.findViewById(R.id.editImg);
				img_view.setVisibility(View.GONE);
				tv_contactno.setText(": "+studentlist.getContactInfo());
				
				tv_name.setText(studentlist.getName());
				tv_email.setText(": "+studentlist.getEmail());
				tv_note.setText(": "+studentlist.getAddress());
			
				main_layout.addView(convertView);
			
			}
			
		}
		//addlessonview();
	}

	private void setOnClickListners() {
		back.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			finish();
			}
		});
		//listView.setc);
		
	}

	private void getLessonDetail() {
		
		tv_name.setText(""+ getIntent().getStringExtra("tname"));
		tv_description.setText(""+getIntent().getStringExtra("description"));
		tv_stime.setText(""+getIntent().getStringExtra("stime")+" - "+getIntent().getStringExtra("etime"));
		tv_duration.setText(getIntent().getStringExtra("duration"));
		tv_sdate.setText(getIntent().getStringExtra("sdate"));
		tv_edate.setText(getIntent().getStringExtra("edate"));
		tv_recuring.setText(getIntent().getStringExtra("recuring"));
	
		getIntent().getStringExtra("tid");
		getIntent().getStringExtra("lid");
		
		
		String getdays=	getIntent().getStringExtra("day");	
		System.err.println("day="+getdays);
	
		if(getdays.contains("Sunday"))
		{
			sun.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			sun.setTextColor(getResources().getColor(R.color.gray));
		}
		if(getdays.contains("Monday"))
		{
			mon.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			mon.setTextColor(getResources().getColor(R.color.gray));
		}
		if(getdays.contains("Tuesday"))
		{
			tue.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			tue.setTextColor(getResources().getColor(R.color.gray));
			}
		if(getdays.contains("Wednesday"))
		{
			wed.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			wed.setTextColor(getResources().getColor(R.color.gray));
		}
		if(getdays.contains("Thursday"))
		{
			thur.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			thur.setTextColor(getResources().getColor(R.color.gray));
		}
		if(getdays.contains("Saturday"))
		{
			sat.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			sat.setTextColor(getResources().getColor(R.color.gray));
		}
		if(getdays.contains("Friday"))
		{
			fri.setTextColor(getResources().getColor(R.color.appBlue));
			}
		else
		{
			fri.setTextColor(getResources().getColor(R.color.gray));
		}
	}

	 //parentLayout.addView(iv,myLayoutParams);
	
	public class StudentAdapter 
	{			
		/*private Context context;
		private TextView tv_name, tv_email,tv_note,tv_contactno;
		private ImageView img_edit;
		private StudentList studentlist;
		
		public StudentAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return MyLessonActivity.arraylist_studentlist.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return MyLessonActivity.arraylist_studentlist.get(position);
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

			studentlist = MyLessonActivity.arraylist_studentlist.get(position);
			TextView	lbl_notes =(TextView)convertView.findViewById(R.id.lbl_notes);
			lbl_notes.setVisibility(View.VISIBLE);
			TextView	lbl_address =(TextView)convertView.findViewById(R.id.lbl_address);
			lbl_address.setVisibility(View.GONE);
			
			tv_name = (TextView)convertView.findViewById(R.id.studentname);
			tv_email = (TextView)convertView.findViewById(R.id.Email);
			tv_note = (TextView)convertView.findViewById(R.id.Notes);
			tv_contactno = (TextView)convertView.findViewById(R.id.contactno);
			img_edit= (ImageView)convertView.findViewById(R.id.editImg);
			img_edit.setVisibility(View.GONE);
			tv_contactno.setText(": "+studentlist.getContactInfo());
			
			tv_name.setText(studentlist.getName());
			tv_email.setText(": "+studentlist.getEmail());
			tv_note.setText(": "+studentlist.getNotes());
		
		
			return convertView;
		}*/
	}
}
