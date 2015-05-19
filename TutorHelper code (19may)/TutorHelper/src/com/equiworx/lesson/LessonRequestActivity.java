package com.equiworx.lesson;


import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.Lesson;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

@SuppressLint("ResourceAsColor")
public class LessonRequestActivity extends Activity implements AsyncResponseForTutorHelper{
	private ArrayList<Lesson> arraylist_lesson=new ArrayList<Lesson>();
	private LessonAdapter adapter;
	private ListView listView;
	private TextView tv_title;
	private ImageView back;
	
	private String str_trigger="",str_parentid="",str_tutorid="";
	private TutorHelperParser parser;
	private SharedPreferences tutorPrefs;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_lessonrequest);
		
		initailiselayout();
		onClickListeners();
		fetchlLessonList();
	}

	private void onClickListeners() {
		// TODO Auto-generated method stub
		back.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void initailiselayout() {
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Lesson Requests");
		listView=(ListView)findViewById(R.id.listView_lesson);
		back=(ImageView)findViewById(R.id.back);
		parser=new TutorHelperParser(LessonRequestActivity.this);
		
	}
	private void fetchlLessonList() {
		if (Util.isNetworkAvailable(LessonRequestActivity.this)){
			/*ParentId/TutorId
			Trigger -- Parent/Tutor*///t//fetch-lessons-request.php
			
				if(tutorPrefs.getString("mode", "").equals("parent"))
				{
					str_trigger="Parent";
					str_parentid=tutorPrefs.getString("parentID", "");
					str_tutorid="";
					}
				else if(tutorPrefs.getString("mode", "").equals("tutor"))
				{
					str_trigger="Tutor";
					str_parentid="";
					str_tutorid=tutorPrefs.getString("tutorID","");
					
					}
			
			
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		
			nameValuePairs.add(new BasicNameValuePair("parent_id", str_parentid));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", str_tutorid));
			nameValuePairs.add(new BasicNameValuePair("trigger", str_trigger));
			nameValuePairs.add(new BasicNameValuePair("type", "ForMe"));
			Log.e("lesson", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(LessonRequestActivity.this, "fetch-lessons-request", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) LessonRequestActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(LessonRequestActivity.this,"Please check your internet connection");
		}
		
	}

	public class LessonAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_topic, ParentId,tv_note,tv_contactno;
	//	CheckBox checkbox;
		//private ImageView call,email;
		private Lesson lesson;
		
		public LessonAdapter(Context ctx)
		{
			context = ctx;
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return arraylist_lesson.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return arraylist_lesson.get(position);
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

			lesson = arraylist_lesson.get(position);
	
			TextView approve= (TextView)convertView.findViewById(R.id.imageView_connect);
			TextView reject= (TextView)convertView.findViewById(R.id.imageView_reject);
			
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
		name.setText(lesson.getTutername()+" has send you lesson request for "+lesson.getStudentname());
		description.setText("Description:"+lesson.getLessonDescription());
		fees.setText("Fees : $"+lesson.getFees());
		
		//ArrayList<String> days=new ArrayList<String>();
		String getdays=lesson.getLessonDays();
		System.err.println("day="+getdays);
		/*try{
		getdays.split(",");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/
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
		
		//stime.setText(""+lesson.getLessonStartTime()+" - ");
		etime.setText(lesson.getLessonStartTime() +" - "+lesson.getLessonEndTime());
		
		TextView time=(TextView)convertView.findViewById(R.id.time);
		time.setVisibility(View.GONE);

		
		
			approve.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("request_id", lesson.getRequestId()));
					nameValuePairs.add(new BasicNameValuePair("trigger", str_trigger));
					nameValuePairs.add(new BasicNameValuePair("response", "Accepted"));
					Log.e("approve", nameValuePairs.toString());
					AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(LessonRequestActivity.this, "accept-lesson-request", nameValuePairs, true, "Please wait...");
					mLogin.delegate = (AsyncResponseForTutorHelper) LessonRequestActivity.this;
					mLogin.execute();
					
				}
			});
			reject.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("request_id", lesson.getRequestId()));
					nameValuePairs.add(new BasicNameValuePair("trigger", str_trigger));
					nameValuePairs.add(new BasicNameValuePair("response", "Rejected"));
					Log.e("approve", nameValuePairs.toString());
					AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(LessonRequestActivity.this, "accept-lesson-request", nameValuePairs, true, "Please wait...");
					mLogin.delegate = (AsyncResponseForTutorHelper) LessonRequestActivity.this;
					mLogin.execute();
					}
			});
			
			return convertView;
		}
	}
	public void processFinish(String output, String methodName) {
		Log.e("fetch lesson=", output);
		if(methodName.equals("fetch-lessons-request")){
			
			arraylist_lesson = parser.getLesson(output);
			if(arraylist_lesson.size()==0)
			{
				AlertDialog.Builder alert = new AlertDialog.Builder(LessonRequestActivity.this);
				alert.setTitle("Tutor Helper");
				alert.setMessage("no lesson requests");
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
			adapter = new LessonAdapter(LessonRequestActivity.this);
			listView.setAdapter(adapter);
			}
		}
		//accept-lesson-request
		else if(methodName.equals("accept-lesson-request")){
			
			Log.e("lesson request", output);
			
			fetchlLessonList();
		}
	}
}
