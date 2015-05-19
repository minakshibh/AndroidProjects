package com.equiworx.tutor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
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
import com.equiworx.lesson.AddLessonActivity;
import com.equiworx.lesson.LessonRequestActivity;
import com.equiworx.lesson.MyLessonActivity;
import com.equiworx.model.LessonDetails;
import com.equiworx.model.MyLesson;
import com.equiworx.model.Tutor;
import com.equiworx.parent.MyProfileActivity;
import com.equiworx.parent.ParentDashBoard;
import com.equiworx.student.AddStudent;
import com.equiworx.tutorhelper.HomeAcitivity;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class TutorDashboard extends FragmentActivity implements AsyncResponseForTutorHelper{
	
	private CaldroidFragment caldroidFragment;
	private Calendar calendar;
	private Date date33,selectdate1,selectdate2,selectdate3,selectdate4,date;
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	private SharedPreferences tutorPrefs;
	private TutorHelperParser parser;
	private LessonDetails lessonDetails;
	private ArrayList<MyLesson> Lessondetail;
	
	private RelativeLayout tutordashboard;
	private ImageView menuIcon,back;
	private TextView tv_title,activestudents,feesdue,feesCollected;
	private TextView connectionRequests, studentRequests,lessonRequests,myLesson,myProfile,tuterList, logout,mystudent,studentmerge,parentmerge;
	private LinearLayout activeStudentsLayout, feesLayout, newStudentLayout, newLessonLayout;
	private LinearLayout menuLayout,calendar_layout,calInfoLayout;
	
	
	private ArrayList<LessonDetails> arraylist_lessondetails=new ArrayList<LessonDetails>();
	private String str_date;
	private String SelectedDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.layout_tutor_dashboard);
		
		initializeLayout();
		calenderInitialize();
		setClickListeners();
		dateOnClick();
		getTutorDetails();
		
	}
		
	private void initializeLayout() {
		parser=new TutorHelperParser(TutorDashboard.this);
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("DashBoard");
		lessonDetails=new LessonDetails();
		tutordashboard=(RelativeLayout)findViewById(R.id.tutordashboard);
		connectionRequests = (TextView)findViewById(R.id.connectionRequests);
		studentRequests = (TextView)findViewById(R.id.studentRequests);
		connectionRequests.setVisibility(View.GONE);
		studentRequests.setVisibility(View.GONE);
		mystudent=(TextView)findViewById(R.id.mystu);
		mystudent.setVisibility(View.GONE);
		activestudents=(TextView)findViewById(R.id.activestudents);
		feesdue=(TextView)findViewById(R.id.feesdue);
		feesCollected=(TextView)findViewById(R.id.feesCollected);
		calendar_layout=(LinearLayout)findViewById(R.id.calendar1);
		calInfoLayout=(LinearLayout)findViewById(R.id.calInfoLayout);
		//datedetails=(LinearLayout)findViewById(R.id.datedetails);
		parentmerge = (TextView)findViewById(R.id.Parentmerge);
		parentmerge.setVisibility(View.GONE);
		studentmerge = (TextView)findViewById(R.id.Studentmerge);
		studentmerge.setVisibility(View.GONE);
		studentmerge.setOnClickListener(listener);
		parentmerge.setOnClickListener(listener);
		lessonRequests=(TextView)findViewById(R.id.lessonRequests);
		myLesson=(TextView)findViewById(R.id.myLesson);
		myProfile = (TextView)findViewById(R.id.myProfile);
		tuterList = (TextView)findViewById(R.id.tuterList);
		tuterList.setVisibility(View.GONE);
		
		
		activeStudentsLayout = (LinearLayout)findViewById(R.id.activeStudentsLayout);
		feesLayout = (LinearLayout)findViewById(R.id.feesLayout);
		newStudentLayout = (LinearLayout)findViewById(R.id.addStudentlayout);
		newLessonLayout = (LinearLayout)findViewById(R.id.addLessonLayout);
		menuIcon = (ImageView)findViewById(R.id.menuIcon);
		back= (ImageView)findViewById(R.id.back);
		back.setVisibility(View.GONE);
		menuLayout = (LinearLayout)findViewById(R.id.menuLayout);
		logout = (TextView)findViewById(R.id.logOut);
		
		//tv_cancel=(TextView)findViewById(R.id.cancel);
		//selectdates();
	}
	
	private void calenderInitialize() {
		Bundle args = new Bundle();
		calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.clear();
        caldroidFragment = new CaldroidFragment(TutorDashboard.this);
        //caldroidFragment.getFragments().setBackgroundColor(Color.WHITE);
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
		
	}
	private void getTutorDetails() {
	
		if (Util.isNetworkAvailable(TutorDashboard.this)){
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("tutor_id", tutorPrefs.getString("tutorID", "")));
					
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(TutorDashboard.this, "getbasicdetail", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) TutorDashboard.this;
			mLogin.execute();
		}else {
			Util.alertMessage(TutorDashboard.this,"Please check your internet connection");
		}
		
	}
	private void setClickListeners() {
		
		activeStudentsLayout.setOnClickListener(listener);
		feesLayout.setOnClickListener(listener);
		newStudentLayout.setOnClickListener(listener);
		newLessonLayout.setOnClickListener(listener);
		lessonRequests.setOnClickListener(listener);
		myLesson.setOnClickListener(listener);
		myProfile.setOnClickListener(listener);
		tuterList.setOnClickListener(listener);
		menuIcon.setOnClickListener(listener);
		logout.setOnClickListener(listener);
		
		tutordashboard.setOnTouchListener(new View.OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
				menuLayoutGone();
				return false;
			}
		});
	/*	tv_cancel.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
			calendar_layout.setVisibility(View.VISIBLE);
			calInfoLayout.setVisibility(View.VISIBLE);
			datedetails.setVisibility(View.GONE);
				
			}
		});*/
	}

	private void dateOnClick() {
		
		if (caldroidFragment != null)
		{
			final CaldroidListener listener = new CaldroidListener()
			  {
				public void onSelectDate(Date date, View view) {
	        		System.err.println("single click date--->"+date);
	        		SelectedDate = formatter.format(date);
	        		Intent intent=new Intent(TutorDashboard.this,MyLessonActivity.class);
	        		intent.putExtra("date", SelectedDate);
	        		startActivity(intent);
		        		
		        		/*AlertDialog.Builder alert = new AlertDialog.Builder(TutorDashboard.this);
		        		alert.setTitle("Date Pressed");
		        		alert.setMessage(""+SelectedDate);
		        		alert.setPositiveButton("OK", null);
		        		alert.show();*/
		        	
		        		//calendar_layout.setVisibility(View.GONE);
		        		//calInfoLayout.setVisibility(View.GONE);
		        		//datedetails.setVisibility(View.VISIBLE);
		        		
		        		//getLessonDetails();
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


	
	/*private void selectdates()
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
	public void getLessonDetails()
	{
		if (Util.isNetworkAvailable(TutorDashboard.this)){
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("lesson_date", SelectedDate));
			nameValuePairs.add(new BasicNameValuePair("tutor_id",tutorPrefs.getString("tutorID", "")));
			//nameValuePairs.add(new BasicNameValuePair("parent_id",tutorPrefs.getString("tutorpass", "")));
			
			Log.e("get lesson detail", nameValuePairs.toString());///get-lesson-detail.php
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(TutorDashboard.this, "get-lesson-detail", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) TutorDashboard.this;
			mLogin.execute();
		}else {
			Util.alertMessage(TutorDashboard.this,"Please check your internet connection");
		}
		
		
	
		}
	private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			if(v == activeStudentsLayout){
				Intent i = new Intent(TutorDashboard.this, ParentListActivity.class);
				startActivity(i);
				menuLayoutGone();
			}else if(v == feesLayout){
				menuLayoutGone();
			}else if(v == newStudentLayout){
				SharedPreferences tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
				
				Editor editor = tutorPrefs.edit();
				editor.putString("name", "");
				editor.putString("email", "");
				editor.putString("contact", "");
				editor.putString("address", "");
				editor.putString("newparentID","0");
				editor.commit();
				
				Intent intent = new Intent(TutorDashboard.this, AddStudent.class);
				startActivity(intent);
				menuLayoutGone();
			}else if(v == newLessonLayout){
				Intent intent = new Intent(TutorDashboard.this, AddLessonActivity.class);
				//intent.putExtra("TutorID", tutorid);
				startActivity(intent);
				menuLayoutGone();
			}else if(v == menuIcon){
				if(menuLayout.getVisibility() == View.GONE)
					menuLayout.setVisibility(View.VISIBLE);
				else
					menuLayout.setVisibility(View.GONE);
			}else if( v== lessonRequests){
				Intent intent = new Intent(TutorDashboard.this, LessonRequestActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== myLesson){
				Intent intent = new Intent(TutorDashboard.this, MyLessonActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== myProfile){
				Intent intent = new Intent(TutorDashboard.this, MyProfileActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if( v== tuterList){
				Intent intent = new Intent(TutorDashboard.this, TutorListActivity.class);
				startActivity(intent);
				menuLayoutGone();
			}else if (v == logout){
				Editor editor = tutorPrefs.edit();
				editor.clear();
				editor.commit();
				Intent intent = new Intent(TutorDashboard.this, HomeAcitivity.class);
				startActivity(intent);
				finish();
			}
		}
	};
public void menuLayoutGone()
{
	if(menuLayout.getVisibility() == View.VISIBLE)
	{
		menuLayout.setVisibility(View.GONE);
		}	
}

	@Override
	public void processFinish(String output, String methodName) {
	if(methodName.equals("getbasicdetail"))
	{
		Log.e("gettutor==", output);
		
		try {
			//TutorLesson tutorLesson=new TutorLesson();
			JSONObject jsonChildNode = new JSONObject(output);	
			String str_activestudents=jsonChildNode.getString("no of active students").toString();
			if(str_activestudents.equals("null"))
			{
				activestudents.setText("0");
				}
			else
			{
				activestudents.setText(str_activestudents);
				}
		
			String str_feesdue=jsonChildNode.getString("fee_due").toString();
			if(str_feesdue.equals("null"))
			{
				feesdue.setText("$ 0");
				}
			else
			{
				feesdue.setText("$ "+str_feesdue);
				}
			String c_fees=jsonChildNode.getString("fee_collected").toString();
			if(c_fees.equals("null"))
			{
				feesCollected.setText("$"+"0");
				}
			else
			{
				feesCollected.setText("$"+c_fees);
				}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		arraylist_lessondetails = parser.getTutordetails(output);
		System.err.println(arraylist_lessondetails.size());
		for(int i=1;i<arraylist_lessondetails.size();i++)
		{
			lessonDetails=arraylist_lessondetails.get(i);
			String str_date=lessonDetails.getLesson_date();
			try {
				date = formatter.parse(str_date);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
			String No_of_lessons=lessonDetails.getNo_of_lessons();
			String fullday=lessonDetails.getBlock_out_time_for_fullday();
			String halfday=lessonDetails.getBlock_out_time_for_halfday();
			int lesson=Integer.parseInt(No_of_lessons);
			if(lesson==1)
			{
				if(fullday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_1_full,date);
					
					}
				else if(halfday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_1_half,date);
					}
				else
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_1,date);
					}
				
				}
			else if(lesson==2)
			{
				if(fullday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_2_full,date);
					}
				else if(halfday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_2_half,date);
					}
				else
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_2,date);
					}
				
				
			}
			else if(lesson==3)
			{
				if(fullday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_3_full,date);
					}
				else if(halfday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_3_half,date);
					}
				else
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_3,date);
					}
				
			}
			else if(lesson>=4)
			{
				if(fullday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_4_full,date);
					System.err.println(date);
					}
				else if(halfday.equalsIgnoreCase("true"))
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_4_half,date);
					System.err.println(date);
					}
				else
				{
					caldroidFragment.setBackgroundResourceForDate(R.drawable.circle_4,date);
					}
				
			}
		
		}
		caldroidFragment.refreshView();
	}
	else if(methodName.equalsIgnoreCase("get-lesson-detail"))
	{
		Lessondetail = new ArrayList<MyLesson>();
		Lessondetail = parser.getMyLesson(output);
	}
		
	}
}