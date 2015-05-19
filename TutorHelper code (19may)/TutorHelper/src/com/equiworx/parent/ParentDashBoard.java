package com.equiworx.parent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.lesson.LessonRequestActivity;
import com.equiworx.lesson.MyLessonActivity;
import com.equiworx.student.AddStudent;
import com.equiworx.student.MyStudentList;
import com.equiworx.tutor.TutorListActivity;
import com.equiworx.tutorhelper.HomeAcitivity;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.Util;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

/**
 * Customize the weekday gridview
 */

public class ParentDashBoard extends FragmentActivity 	implements AsyncResponseForTutorHelper{
	
	private CaldroidFragment caldroidFragment;
	private Calendar calendar;
	private Date date33;
	private TextView tv_title;
	private Date selectdate1,selectdate2,selectdate3,selectdate4;
	private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	private String parentId;
	private ImageView menuIcon,back;
	private LinearLayout menuLayout,calendarlayout;
	private SharedPreferences tutorPrefs;
	private RelativeLayout parentdashboard;
	private LinearLayout newStudentLayout;
	private TextView connectionRequests, studentRequests,myStudent,lessonRequests,myLesson,myProfile,tuterList, logout,studentmerge,parentmerge;
	
	
	private String str_date;
	//SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy");
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.parent_dashboard);
		
		initializeLayout();
		setClickListeners();
		dateOnClick();
	}
	private void setClickListeners() {
		// TODO Auto-generated method stub
		
		newStudentLayout.setOnClickListener(listener);
		//newLessonLayout.setOnClickListener(listener);
		connectionRequests.setOnClickListener(listener);
		studentRequests.setOnClickListener(listener);
		lessonRequests.setOnClickListener(listener);
		myLesson.setOnClickListener(listener);
		menuIcon.setOnClickListener(listener);
		myProfile.setOnClickListener(listener);
		tuterList.setOnClickListener(listener);
		myStudent.setOnClickListener(listener);
		
		studentmerge.setOnClickListener(listener);
		parentmerge.setOnClickListener(listener);
		logout.setOnClickListener(listener);
		
		parentdashboard.setOnTouchListener(new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
				menuLayoutGone();
				return false;
			}
		});
	}

	private void dateOnClick() {
		
		if (caldroidFragment != null)
		{
			final CaldroidListener listener = new CaldroidListener()
			  {
				@Override
				public void onSelectDate(Date date, View view) {
		        		System.err.println("single click date--->"+date);
		 	
		        		String str_date1 = formatter.format(date);
		        		
		        		AlertDialog.Builder alert = new AlertDialog.Builder(ParentDashBoard.this);
		        		alert.setTitle("Select date");
		        		alert.setMessage(""+str_date1);
		        		alert.setPositiveButton("OK", null);
		        		alert.show();

					}
				public void onLongClickDate(Date date, View view)
					{
						System.err.println("long click date--->"+date);
						//caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_blue,date);
						//caldroidFragment.refreshView();	
					}
		
			 };
					
			caldroidFragment.setCaldroidListener(listener);
		}
		
	}
	private void initializeLayout() {
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("DashBoard");
		back= (ImageView)findViewById(R.id.back);
		back.setVisibility(View.GONE);
		parentdashboard=(RelativeLayout)findViewById(R.id.parentdashboard);
		newStudentLayout = (LinearLayout)findViewById(R.id.addStudentlayout);
		
		calendarlayout=(LinearLayout)findViewById(R.id.calendar1);
		//newLessonLayout = (LinearLayout)findViewById(R.id.addLessonLayout);
		connectionRequests = (TextView)findViewById(R.id.connectionRequests);
		studentRequests = (TextView)findViewById(R.id.studentRequests);
		lessonRequests=(TextView)findViewById(R.id.lessonRequests);
		//student=(TextView)findViewById(R.id.student);
		myLesson=(TextView)findViewById(R.id.myLesson);
		menuIcon = (ImageView)findViewById(R.id.menuIcon);
		menuLayout = (LinearLayout)findViewById(R.id.menuLayout);
		logout=(TextView)findViewById(R.id.logOut);
	
		myStudent=(TextView)findViewById(R.id.mystu);
		myProfile = (TextView)findViewById(R.id.myProfile);
		tuterList = (TextView)findViewById(R.id.tuterList);
		
		parentmerge = (TextView)findViewById(R.id.Parentmerge);
		studentmerge = (TextView)findViewById(R.id.Studentmerge);
		//tuterList.setVisibility(View.GONE);
		//myProfile.setVisibility(View.GONE);
		
		
		SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		parentId = tutorPrefs.getString("parentID", "0");
		
		Bundle args = new Bundle();
		calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();
        caldroidFragment = new CaldroidFragment(ParentDashBoard.this);
        Calendar currentdate = Calendar.getInstance();
		str_date = formatter.format(currentdate.getTime());
		try {
    		  date33=formatter.parse(str_date);
    		 
  			} 
    	  catch (ParseException e) 
			{
    		  e.printStackTrace();
				}	
 
		calendar.setTime(date33);
        args.putInt(CaldroidFragment.MONTH, calendar.get(Calendar.MONTH));
		args.putInt(CaldroidFragment.YEAR, calendar.get(Calendar.YEAR));
		args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
		args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);
		caldroidFragment.setArguments(args);
		FragmentTransaction t = getSupportFragmentManager().beginTransaction();
		t.replace(R.id.calendar1, caldroidFragment);
		t.commit();
	//	selectdates();
	
	}
	public void dateselected()
	{
		if (Util.isNetworkAvailable(ParentDashBoard.this)){
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tutor_pin", tutorPrefs.getString("tutorID", "")));
			nameValuePairs.add(new BasicNameValuePair("password",tutorPrefs.getString("tutorpass", "")));
			
			Log.e("date select", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ParentDashBoard.this, "login", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) ParentDashBoard.this;
			mLogin.execute();
		}else {
			Util.alertMessage(ParentDashBoard.this,"Please check your internet connection");
		}
		
		
	
		}
	
/*	private void selectdates()
	{
		String dateInString = "7-04-2015";
		String dateInString2 = "10-04-2015";
		String dateInString3 = "15-04-2015";
		String dateInString4 = "18-04-2015";
	 
		try {
	 
			selectdate1 = formatter.parse(dateInString);
			selectdate2 = formatter.parse(dateInString2);
			selectdate3 = formatter.parse(dateInString3);
			selectdate4 = formatter.parse(dateInString4);
			System.out.println(selectdate1);
			System.out.println(formatter.format(selectdate1));
	 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		

		caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_1_full,selectdate1);
		caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_2_half,selectdate2);
		caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_3,selectdate3);
		caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_4,selectdate4);
		caldroidFragment.refreshView();
	}*/
	public void menuLayoutGone()
	{
		if(menuLayout.getVisibility() == View.VISIBLE)
		{
			menuLayout.setVisibility(View.GONE);
			}	
	}
	private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			if(v == newStudentLayout){
				SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
				
				Editor editor = tutorPrefs.edit();
				editor.putString("name", "");
				editor.putString("email", "");
				editor.putString("contact", "");
				editor.putString("address", "");
				//editor.putString("parentID","0");
				editor.putString("gender","male");
				editor.commit();
				
				Intent intent = new Intent(ParentDashBoard.this, AddStudent.class);
				intent.putExtra("trigger", "parent");
				intent.putExtra("parentID", parentId);
				startActivity(intent);
				menuLayoutGone();
			/*}else if(v == newLessonLayout){
				Intent intent = new Intent(ParentDashBoard.this, AddLesson.class);
				//intent.putExtra("parentID", parentId);
				//intent.putExtra("trigger", "Parent");
				startActivity(intent);
				menuLayoutGone();*/
				
			}else if( v == connectionRequests){
				Intent intent = new Intent(ParentDashBoard.this, ConnectionRequests.class);
				intent.putExtra("parentID", parentId);
				intent.putExtra("trigger", "Parent");
				menuLayoutGone();
				
				startActivity(intent);
			}else if( v== studentRequests){
				Intent intent = new Intent(ParentDashBoard.this, StudentRequestActivity.class);
				intent.putExtra("parentID", parentId);
				startActivity(intent);
				menuLayoutGone();
			}else if(v == menuIcon){
				if(menuLayout.getVisibility() == View.GONE)
					menuLayout.setVisibility(View.VISIBLE);
				else
					menuLayout.setVisibility(View.GONE);
			}else if( v== lessonRequests){
				Intent intent = new Intent(ParentDashBoard.this, LessonRequestActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== myLesson){
				Intent intent = new Intent(ParentDashBoard.this, MyLessonActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}
			else if( v== myStudent){
				System.err.println("studentlist");
					Intent intent = new Intent(ParentDashBoard.this, MyStudentList.class);
					startActivity(intent);
					menuLayoutGone();
					
			}else if( v== myProfile){
				Intent intent = new Intent(ParentDashBoard.this, MyProfileActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== tuterList){
				Intent intent = new Intent(ParentDashBoard.this, TutorListActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== parentmerge){
				Intent intent = new Intent(ParentDashBoard.this, ParentMerge_Activity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== studentmerge){
				//Intent intent = new Intent(ParentDashBoard.this, TutorListActivity.class);
				//startActivity(intent);
				menuLayoutGone();
			}else if (v == logout){
				Editor editor = tutorPrefs.edit();
				editor.clear();
				editor.commit();
				Intent intent = new Intent(ParentDashBoard.this, HomeAcitivity.class);
				startActivity(intent);
				finish();
			}
		}
	};

	@Override
	public void processFinish(String output, String methodName) {
		// TODO Auto-generated method stub
		
	}
	}