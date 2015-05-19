package com.equiworx.lesson;


import java.util.ArrayList;
import java.util.HashSet;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.StudentList;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class StudentListActivity extends Activity implements AsyncResponseForTutorHelper{
	private  ArrayList<StudentList> arraylist_student=new ArrayList<StudentList>();
	public static ArrayList<Integer> arraylist_checkbox=new ArrayList<Integer>();
	private StudentAdapter adapter;
	private ListView listView;
	private  Button  btnselect;
	private String str_parentid="",str_tuterid="";
	private SharedPreferences tutorPrefs;
	private TextView tv_title,tv_cancel;
	 //HashMap<String , String>
	private TutorHelperParser parser;
	
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.activity_studentlist);
	

	intializelayout();
	fetchlStudentList();
	setOnClickListners();
	
	}
	
	private void intializelayout() {
		AddLessonActivity.arraylist_addstu.clear();
		arraylist_checkbox.clear();
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		parser=new TutorHelperParser(StudentListActivity.this);
		tv_title=(TextView)findViewById(R.id.title);
		tv_title.setText("Add Students");
		listView=(ListView)findViewById(R.id.listView);
		//AddLesson.str_students_list="";
		btnselect=(Button)findViewById(R.id.button_done);
		tv_cancel=(TextView)findViewById(R.id.textView_cancel);

	}

	private void setOnClickListners() {
		
	btnselect.setOnClickListener(new OnClickListener() {
	public void onClick(View v) {
		if(AddLessonActivity.arraylist_addstu.size()==0)
		{
			Util.alertMessage(StudentListActivity.this, "Please select Students");
			}
		else
		{
			finish();
			//arraylist_checkbox.clear();
			}
		}
	});
	tv_cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			AddLessonActivity.arraylist_addstu.clear();
			arraylist_checkbox.clear();
			finish();
			}
		});
		
	}

	private void fetchlStudentList() {
		
		if(tutorPrefs.getString("mode", "").equals("parent"))
		{
			str_parentid=tutorPrefs.getString("parentID", "");
			str_tuterid="-1";
			}
		else if(tutorPrefs.getString("mode", "").equals("tutor"))
		{
			str_parentid="-1";
			str_tuterid=tutorPrefs.getString("tutorID","");
			}
		if (Util.isNetworkAvailable(StudentListActivity.this)){

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			nameValuePairs.add(new BasicNameValuePair("parent_id", str_parentid));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", str_tuterid));
			Log.e("Student list", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(StudentListActivity.this, "fetch-student", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) StudentListActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(StudentListActivity.this,"Please check your internet connection");
		}
		
	}
	public class StudentAdapter extends BaseAdapter
	{			
		private Context context;
		private TextView tv_name, tv_studentid,tv_parentid;
		private CheckBox checkbox;
		//private ImageView call,email;
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
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.studentlist_row1, parent, false);
			}
		
			studentlist = arraylist_student.get(position);
			TextView edit= (TextView)convertView.findViewById(R.id.edit);
			edit.setVisibility(View.GONE);
		
			
			tv_name = (TextView)convertView.findViewById(R.id.studentname);
			tv_studentid = (TextView)convertView.findViewById(R.id.studentid);
			tv_parentid = (TextView)convertView.findViewById(R.id.parentid);
			//tv_contactno = (TextView)convertView.findViewById(R.id.contactno);
		
			//tv_contactno.setText(": "+studentlist.getContactInfo());
			
			tv_studentid.setText("Student Id : "+studentlist.getStudentId());
			tv_parentid.setText("Parent Id : "+studentlist.getParentId());
			tv_name.setText(""+studentlist.getName());
				
			checkbox=(CheckBox)convertView.findViewById(R.id.checkbox_student);
			checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						studentlist = arraylist_student.get(position);
						String studentid=studentlist.getStudentId();
						System.err.println("iddd=="+studentid);
						AddLessonActivity.arraylist_addstu.add(studentid);
						/*for(int i=1;i<AddLesson.arraylist_addstu.size();i++)
						{
							if(studentlist.getStudentId().equalsIgnoreCase(AddLesson.arraylist_addstu.get(i)))
							{
								
								AddLesson.arraylist_addstu.add(studentid);
							}
						}*/
						
						arraylist_checkbox.add(position);
						}
					else
					{
						try{
							studentlist = arraylist_student.get(position);
							String studentid=studentlist.getStudentId();
							for(int i=0;i<AddLessonActivity.arraylist_addstu.size();i++)
							{
								System.out.println("Array list="+AddLessonActivity.arraylist_addstu.get(i));
								if(studentid.equals(AddLessonActivity.arraylist_addstu.get(i)))
								{
									AddLessonActivity.arraylist_addstu.remove(i);
									arraylist_checkbox.remove(i);
									}
							}
							
						//arraylist_addstu.remove(studentid);
						}
						catch(Exception e)
						{
							e.printStackTrace();
							
						}
						studentlist = arraylist_student.get(position);
						String studentid=studentlist.getStudentId();
						System.err.println("old id="+studentid);
						
					}
					
					 ArrayList<String> unique = removeDuplicates(AddLessonActivity.arraylist_addstu);
						for (String element : unique) {
						    System.out.println(element);
						}
						 String temp="arraylist="+AddLessonActivity.arraylist_addstu.toString();
						 System.err.println(temp);
				}
			});
			System.err.println("size"+arraylist_checkbox.size());
			for(int i=0;i<arraylist_checkbox.size();i++)
			{
				if(arraylist_checkbox.get(i)==position)
				{
					checkbox.setChecked(true);
					}
				else
				{
					checkbox.setChecked(false);
					}
				}
			return convertView;
		}
	}
	
/*	
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view = null;

	    if (convertView == null) {
	        LayoutInflater inflator = context.getLayoutInflater();
	        view = inflator.inflate(R.layout.subtag, null);
	        final ViewHolder viewHolder = new ViewHolder();
	        viewHolder.text = (TextView) view.findViewById(R.id.subtag_textCatagory);
	        viewHolder.check = (CheckBox) view.findViewById(R.id.subtag_checkCatagory);

	        viewHolder.text.setOnClickListener(this);
	        viewHolder.check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

	                    public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
	                        TagBojo element = (TagBojo) viewHolder.check.getTag();
	                        element.setSelected(buttonView.isChecked());

	                        System.out.println(element.getListName());
	                });
	        view.setTag(viewHolder);
	        viewHolder.check.setTag(list.get(position));
	    } else {
	        view = convertView;
	        ((ViewHolder) view.getTag()).check.setTag(list.get(position));
	    }
	    ViewHolder holder = (ViewHolder) view.getTag();
	    holder.text.setText(list.get(position).getListName());
	    holder.check.setChecked(list.get(position).getSelected());
	    return view;
	}*/
	public void processFinish(String output, String methodName) {
	if(methodName.equals("fetch-student")){
		
		if(tutorPrefs.getString("mode", "").equals("parent"))
		{
			arraylist_student = parser.getStudentlistwithoutNote(output);
			}
		else if(tutorPrefs.getString("mode", "").equals("tutor"))
		{
			arraylist_student = parser.getStudentlist(output);
			
			}
		adapter = new StudentAdapter(StudentListActivity.this);
		listView.setAdapter(adapter);
	}
	}
	
	static ArrayList<String> removeDuplicates(ArrayList<String> list) {

		// Store unique items in result.
		AddLessonActivity.arraylist_addstu = new ArrayList<String>();

		// Record encountered Strings in HashSet.
		HashSet<String> set = new HashSet<String>();

		// Loop over argument list.
		for (String item : list) {

		    // If String is not in set, add it to the list and the set.
		    if (!set.contains(item)) {
		    	AddLessonActivity.arraylist_addstu.add(item);
			set.add(item);
		    }
		}
		return AddLessonActivity.arraylist_addstu;
	    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		arraylist_checkbox.clear();
	}
}
