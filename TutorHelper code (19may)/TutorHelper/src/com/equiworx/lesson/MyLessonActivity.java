package com.equiworx.lesson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.MyLesson;
import com.equiworx.model.StudentList;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class MyLessonActivity extends Activity implements AsyncResponseForTutorHelper{

	private ArrayList<MyLesson> arraylist_mylesson=new ArrayList<MyLesson>();
	public static ArrayList<StudentList> arraylist_studentlist=new ArrayList<StudentList>();
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
 	private DateFormat timeformatter = new SimpleDateFormat("HH:mm:ss");  
 	
	private MyLessonAdapter adapter;
	private ListView listView;
	private TutorHelperParser parser;
	private TextView tv_title;
	private ImageView back;
	private MyLesson mylesson;
	
	private String str_trigger="";
	private String str_parentid="";
	private String str_tuterid="",SelectedDate,str_startdate;
	private int datecheck=0;
	
	private SharedPreferences tutorPrefs;
	
	
	
	public  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_mylesson);
		
		intializelayout();
		
		setOnClickListners();
		getLessonDetail();
}

	private void getLessonDetail() {
		SelectedDate=getIntent().getStringExtra("date");
	
		if(SelectedDate!=null)
		{
			webLessonDetails();
			tv_title.setText("Lesson Details");
			}
		else
		{
			fetchlMyLessonList();
			tv_title.setText("My Lessons");
			}
	}

	private void intializelayout() {
		back=(ImageView)findViewById(R.id.back);
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tv_title=(TextView)findViewById(R.id.title);
		
		listView=(ListView)findViewById(R.id.listView_mylesson);
		parser=new TutorHelperParser(MyLessonActivity.this);
		
	}

	private void setOnClickListners() {
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					// TODO Auto-generated method stub
					finish();
				}
			});
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			
				mylesson= arraylist_mylesson.get(position);
				Intent intent=new Intent(MyLessonActivity.this,LessonDetailsActivity.class);
				intent.putExtra("tname", mylesson.getTutor_name());
				intent.putExtra("day", mylesson.getDays());	
				intent.putExtra("description", mylesson.getLessonDescription());
				intent.putExtra("stime", mylesson.getStartTime());
				intent.putExtra("etime", mylesson.getEndTime());
				intent.putExtra("duration", mylesson.getDuration());
				intent.putExtra("sdate", mylesson.getLessonDate());
				intent.putExtra("edate", mylesson.getLessonenddate());
				intent.putExtra("lid", mylesson.getLessonId());
				intent.putExtra("tid", mylesson.getLesson_tutor_id());
				intent.putExtra("recuring", mylesson.getIsRecurring());
			
				
				arraylist_studentlist.clear();
				
				System.err.println("students="+mylesson.getArray_studentlist().size());//setArray_studentlist
				for(int i=0;i<mylesson.getArray_studentlist().size();i++)
				{
					StudentList studentlist=new StudentList();
					studentlist.setName(mylesson.getArray_studentlist().get(i).getName());
					studentlist.setAddress(mylesson.getArray_studentlist().get(i).getAddress());
					studentlist.setContactInfo(mylesson.getArray_studentlist().get(i).getContactInfo());
					studentlist.setEmail(mylesson.getArray_studentlist().get(i).getEmail());
					studentlist.setNotes(mylesson.getArray_studentlist().get(i).getNotes());
					
					arraylist_studentlist.add(studentlist);
					}
				
				startActivity(intent);
					
				}
			});
	}
	public void webLessonDetails()
	{
		if (Util.isNetworkAvailable(MyLessonActivity.this)){
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("lesson_date", SelectedDate));
			nameValuePairs.add(new BasicNameValuePair("tutor_id",tutorPrefs.getString("tutorID", "")));
			//nameValuePairs.add(new BasicNameValuePair("parent_id",tutorPrefs.getString("tutorpass", "")));
			
			Log.e("get lesson detail", nameValuePairs.toString());///get-lesson-detail.php
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(MyLessonActivity.this, "get-lesson-detail", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) MyLessonActivity.this;
			mLogin.execute();
		}else {
				Util.alertMessage(MyLessonActivity.this,"Please check your internet connection");
				}
		}
	private void fetchlMyLessonList() {
		if (Util.isNetworkAvailable(MyLessonActivity.this)){
			/*ParentId/TutorId
			Trigger -- Parent/Tutor*///tutor_helper/fetch-parent.php//

			if(tutorPrefs.getString("mode", "").equals("parent"))
			{
				str_trigger="Parent";
				str_parentid=tutorPrefs.getString("parentID", "");
				str_tuterid="";//tutorPrefs.getString("tutorID","");
				}
			else if(tutorPrefs.getString("mode", "").equals("tutor"))
			{
				str_trigger="Tutor";
				str_parentid="";//tutorPrefs.getString("parentID", "");
				str_tuterid=tutorPrefs.getString("tutorID","");
			}
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("parent_id", str_parentid));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", str_tuterid));
			nameValuePairs.add(new BasicNameValuePair("trigger", str_trigger));
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			Log.e("My lesson", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(MyLessonActivity.this, "fetch-my-lessons", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) MyLessonActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(MyLessonActivity.this,"Please check your internet connection");
		}
		
	}
	
	public class MyLessonAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_name, tv_email,tv_note,tv_contactno;
	//CheckBox checkbox;
		//private ImageView call,email;
		private MyLesson myLesson;
		
		public MyLessonAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return arraylist_mylesson.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arraylist_mylesson.get(position);
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.lesson_row, parent, false);
			}

			myLesson = arraylist_mylesson.get(position);
	
			TextView approve= (TextView)convertView.findViewById(R.id.imageView_connect);
			approve.setVisibility(View.GONE);
			TextView reject= (TextView)convertView.findViewById(R.id.imageView_reject);
			reject.setVisibility(View.GONE);
			
			
		TextView	name = (TextView)convertView.findViewById(R.id.name);
		TextView	description = (TextView)convertView.findViewById(R.id.description);
		TextView	fees = (TextView)convertView.findViewById(R.id.fees);
		TextView	sun = (TextView)convertView.findViewById(R.id.sun);
		TextView	mon = (TextView)convertView.findViewById(R.id.mon);
		TextView	tue = (TextView)convertView.findViewById(R.id.tue);
		TextView	wed = (TextView)convertView.findViewById(R.id.wed);
		TextView	thur = (TextView)convertView.findViewById(R.id.thur);
		TextView	fri = (TextView)convertView.findViewById(R.id.fri);
		TextView	sat = (TextView)convertView.findViewById(R.id.sat);
		//TextView	stime = (TextView)convertView.findViewById(R.id.stime);
		TextView	etime = (TextView)convertView.findViewById(R.id.etime);

//			
		name.setText(myLesson.getTutor_name()+" has send you lesson request for ");
		name.setVisibility(View.GONE);
		description.setText("Description:"+myLesson.getLessonDescription());
		description.setMaxLines(1);
		//fees.setText("Fees : "+myLesson.getFees());
		fees.setVisibility(View.GONE);
		//ArrayList<String> days=new ArrayList<String>();
		String getdays=myLesson.getDays();
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
		
		//stime.setText(""+myLesson.getStartTime() +" - ");
		etime.setText(myLesson.getStartTime() +" - "+myLesson.getEndTime());
		
		TextView time=(TextView)convertView.findViewById(R.id.time);
		time.setText("No. of students :"+myLesson.getStudentno()+"Students");
		
		Button cancel=(Button)convertView.findViewById(R.id.cancel);
		if(tutorPrefs.getString("mode", "").equalsIgnoreCase("parent"))
		{
			
			
			
			
		    	  
			//dateCompare(mylesson.getLessonDate());   	//date compare  
		    	  
		  
		    	  
			
			
			if(datecheck==1)
			{
				cancel.setVisibility(View.VISIBLE);
				}
			else if(datecheck==2)
			{
				cancel.setVisibility(View.VISIBLE);
			}
			else
			{
				cancel.setVisibility(View.GONE);
			}
		}
		else
		{
			cancel.setVisibility(View.GONE);
		}
	
		
			return convertView;
		}
	}
	public void processFinish(String output, String methodName) {
	if(methodName.equals("fetch-my-lessons")){
		
		arraylist_mylesson = parser.getMyLesson(output);
		
		adapter = new MyLessonAdapter(MyLessonActivity.this);
		listView.setAdapter(adapter);
	}
	else if(methodName.equals("get-lesson-detail"))
	{
		arraylist_mylesson = parser.getMyLesson(output);
		adapter = new MyLessonAdapter(MyLessonActivity.this);
		listView.setAdapter(adapter);
		
			}
	}
	
	
	public void dateCompare(String str_date)
	{
	 Date date = null;
		try {
			date = (Date)formatter.parse(str_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	
		formatter = new SimpleDateFormat("yyyy-MM-dd");
 	str_startdate = formatter.format(date);

	
	//check date condition 
		
           		
		  try{
			  Calendar c = Calendar.getInstance();
			  System.out.println("Current time => " + c.getTime());
			  String currentdate = formatter.format(c.getTime());
		      Date seldate = formatter.parse(str_startdate);
		      Date curdate = formatter.parse(currentdate);
		      System.err.println("cuurent date="+curdate);
		     System.err.println("selected date="+seldate);
		     if (curdate.before(seldate))
		      {
		          
		        datecheck=1;
		        System.out.println("date=="+datecheck);   
		      		}
		     else if(curdate.equals(seldate))
		     { 
		    	datecheck=2;
		    	 System.out.println("date==="+datecheck);
		     }
		     else
		     {
		    	//Util.alertMessage(AddLessonActivity.this, "Please select Greater date from current date");  
		    	   datecheck=0;
		    	   System.out.println("date==="+datecheck);
		     	}

		    }catch (ParseException e1){
		        e1.printStackTrace();
		    	}
			}
	}