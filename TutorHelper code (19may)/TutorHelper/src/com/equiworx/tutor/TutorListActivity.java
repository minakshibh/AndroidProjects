package com.equiworx.tutor;

import java.util.ArrayList;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.model.Tutor;
import com.equiworx.model.TutorList;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;

public class TutorListActivity extends Activity implements AsyncResponseForTutorHelper {
	private ListView listView;
	private SharedPreferences tutorPrefs;
	private ArrayList<Tutor> tutorList;
	public static ArrayList<String> array_studentIDs=new ArrayList<String>();
	private ParentAdapter adapter;
	private Tutor tutor;
	private TutorList tutorlistmodel;
	private TutorHelperParser parser;
	private ImageView back;
	//private Button btn_addlist;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_tutorlist);
		
		
		intializelayout();
		fetchTuterList();
		setOnClickListners();

	}

	private void setOnClickListners() {
		back.setOnClickListener(new OnClickListener() {
		public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				array_studentIDs.clear();
				tutor = tutorList.get(position);
				Intent intent=new Intent(TutorListActivity.this,TutorDetailActivity.class);
				intent.putExtra("name", tutor.getName());
				intent.putExtra("email", tutor.getEmail());	
				intent.putExtra("address", tutor.getAddress());
				intent.putExtra("phone", tutor.getAltContactNumber());
				intent.putExtra("notes", tutor.getNotes());
				intent.putExtra("tutorid", tutor.getTutorId());
				System.err.println("students="+tutor.getStudent().size());
				for(int i=0;i<tutor.getStudent().size();i++)
				{
					System.err.println("student id="+tutor.getStudent().get(i).getStudentid());
					array_studentIDs.add(tutor.getStudent().get(i).getStudentid());
					}	
				
				startActivity(intent);
					
				}
			});
		/*btn_addlist.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i=new Intent(TutorListActivity.this,AddLesson.class);
				startActivity(i);
				
			}
		});*/
	}

	private void fetchTuterList() {
		if (Util.isNetworkAvailable(TutorListActivity.this)) {
		
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			nameValuePairs.add(new BasicNameValuePair("parent_id", tutorPrefs.getString("parentID", "")));

			Log.e("tutor list", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(
					TutorListActivity.this, "fetch-tutor", nameValuePairs,
					true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) TutorListActivity.this;
			mLogin.execute();
		} else {
			Util.alertMessage(TutorListActivity.this,"Please check your internet connection");
		}

	}

	private void intializelayout() {
		tutor=new Tutor();
		tutorlistmodel=new TutorList();
		parser = new TutorHelperParser(TutorListActivity.this);
		
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		listView = (ListView) findViewById(R.id.listview);
		back=(ImageView)findViewById(R.id.back);
	//	btn_addlist=(Button)findViewById(R.id.button_addlesson);
	}

	@Override
	public void processFinish(String output, String methodName) {
		Log.e("tutorlist", output);
		if (methodName.equals("fetch-tutor")) {
			tutorList = new ArrayList<Tutor>();
			tutorList = parser.getTuterList(output);
			
			adapter = new ParentAdapter(TutorListActivity.this);

			listView.setAdapter(adapter);
				
		}

	}

	public class ParentAdapter extends BaseAdapter {
		private Context context;
		private TextView tutor_id, email_id, notes_text,
		tutor_name;
		private CheckBox checkbox;
		//private ImageView call, email;
		private Tutor tutor;
		//private String checking;
		//private RelativeLayout mainlayout;

		public ParentAdapter(Context ctx) {
			context = ctx;
		}

		@Override
		public void notifyDataSetChanged() {
			// do your sorting here

			super.notifyDataSetChanged();
		}

		public void addItem(String string) {
			// TODO Auto-generated method stub

		}

		public int getCount() {
			// TODO Auto-generated method stub
			return tutorList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return tutorList.get(position);
		}

		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup group) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.single_tutorlistview_cell, group,
						false);
			}

			tutor = tutorList.get(position);
		
			tutor_id = (TextView) convertView.findViewById(R.id.tutor_id);
			email_id = (TextView) convertView.findViewById(R.id.email_id);
			notes_text = (TextView) convertView.findViewById(R.id.notes_text);
			tutor_name = (TextView) convertView.findViewById(R.id.tutor_name);
			checkbox = (CheckBox) convertView.findViewById(R.id.checkBox);
			checkbox.setVisibility(View.GONE);
			//email = (ImageView) convertView.findViewById(R.id.imageView_email);

			tutor_id.setText(tutor.getTutorId());
			email_id.setText(": " + tutor.getEmail());
			notes_text.setText(": " + tutor.getContactNumber());
			tutor_name.setText("" + tutor.getName());
			
			
			/*checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						//i++;
						tutor = tutorList.get(position);
						String tutorid=tutor.getTutorId();
						System.err.println("iddd=="+tutorid);
						
						addtutorList.add(tutorid);
						}
					else
					{
						try{
							tutor = tutorList.get(position);
							String tutorid=tutorList.get(position).getTutorId();
							for(int i=0;i<addtutorList.size();i++)
							{
								//System.out.println("Array list="+AddLesson.add.get(i));
								if(tutorid.equals(addtutorList.get(i)))
								{
									addtutorList.remove(i);
									}
								}
							
							//arraylist_addstu.remove(studentid);
							}
							catch(Exception e)
							{
								e.printStackTrace();
								}
						tutor = tutorList.get(position);
						String tutorid=tutor.getTutorId();
						System.err.println("old id="+tutorid);
						
						//AddLesson.str_students_list.replace(studentid, "");
						//System.err.println("total="+AddLesson.str_students_list);
					}
					 String temp="arraylist="+addtutorList.toString();
					 System.err.println(temp);
					
				}
			});*/
			return convertView;
		}
	}

}
