package com.equiworx.tutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.equiworx.model.Parent;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.TutorHelperParser;
import com.equiworx.util.Util;


public class ParentListActivity extends Activity  implements AsyncResponseForTutorHelper{
	
	private ListView listView;
	private ArrayList<Parent> parentList;
	private ParentAdapter adapter;
	private TutorHelperParser parser;
	private Parent parent;
	private SharedPreferences tutorPrefs;
	private String tutorId;
	private TextView tv_title,tv_sort,textView_cancel;
	private EditText autocomp_txtview;
	private ImageView back;
	private LinearLayout filter_parentlist, sort_layout;
	private AlertDialog levelDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		setContentView(R.layout.activity_parentlist);
	
		initializeLayout();
		fetchlParentList();
		gotoNext();
		searchParent();
		setOnClick();
}


	

	private void gotoNext() {
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				parent = parentList.get(arg2);
				Intent i=new Intent(ParentListActivity.this,ParentDetailActivity.class);
				Editor ed=tutorPrefs.edit();
				ed.putString("p_name", parent.getName());
				ed.putString("p_contactno", parent.getContactNumber());
				ed.putString("p_email", parent.getEmail());
				ed.putString("p_noofstudent", parent.getStudentCount());
				ed.putString("p_balance", parent.getOutstandingBalance());
				ed.putString("p_address", parent.getAddress());
				ed.putString("p_notes", parent.getNotes());
				ed.putString("p_tutorId", tutorId);
				ed.putString("p_parentId", parent.getParentId());
				ed.putString("p_lessons", parent.getLessonCount());
				ed.putString("p_gender", parent.getGender());
				ed.commit();
				startActivity(i);
			}
		});
	}
	private void fetchlParentList() {
		if (Util.isNetworkAvailable(ParentListActivity.this)){
	
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("last_updated_date", ""));
			nameValuePairs.add(new BasicNameValuePair("tutor_id", tutorId));
			Log.e("connection", nameValuePairs.toString());
			AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(ParentListActivity.this, "fetch-parent", nameValuePairs, true, "Please wait...");
			mLogin.delegate = (AsyncResponseForTutorHelper) ParentListActivity.this;
			mLogin.execute();
		}else {
			Util.alertMessage(ParentListActivity.this,
					"Please check your internet connection");
		}
		
	}
	public void searchParent()
	{
	    
		autocomp_txtview.addTextChangedListener(new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			
		//	adapter.getFilter().filter(s.toString());
			String text = autocomp_txtview.getText().toString().toLowerCase();
			adapter.filter(text);
			}
		public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
		public void afterTextChanged(Editable s) {
	
			}
		});
}

	private void initializeLayout() {
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		tutorId = tutorPrefs.getString("tutorID", "0");
		back=(ImageView)findViewById(R.id.back);
		autocomp_txtview=(EditText)findViewById(R.id.editText_search);
		tv_sort=(TextView) findViewById(R.id.textView_sort);
		tv_sort.setVisibility(View.GONE);
		tv_title=(TextView)findViewById(R.id.title);
		textView_cancel=(TextView)findViewById(R.id.textView_cancel);
		textView_cancel.setVisibility(View.GONE);
		tv_title.setText("Student Management");
		listView=(ListView)findViewById(R.id.listview);
		parser=new TutorHelperParser(ParentListActivity.this);
		filter_parentlist = (LinearLayout)findViewById(R.id.filter_parentlist);
		sort_layout = (LinearLayout)findViewById(R.id.sort_layout);
		
	}
	private void setOnClick() {
		back.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				
			}
		});	
		textView_cancel.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent i=new Intent(ParentListActivity.this,ParentListActivity.class);
			startActivity(i);
			finish();
			textView_cancel.setVisibility(View.GONE);
				
			}
		});
		sort_layout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Strings to Show In Dialog with Radio Buttons
				final CharSequence[] items = { " By Name ",
						" UpComing Lessions ", " OutStanding Balance " };

				// Creating and Building the Dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(ParentListActivity.this);
				builder.setTitle("Select The Difficulty Level");
				builder.setSingleChoiceItems(items, -1,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {

								switch (item) {
								case 0:
									ArrayList<Parent> sortarArrayList=new ArrayList<Parent>();

									 Collections.sort(parentList, new Comparator<Parent>() {
									 public int compare(Parent v1, Parent v2) {
									 return  v1.getName().compareTo(v2.getName());
									 }
									 });

									Iterator<Parent> iterator = parentList .iterator();
									while (iterator.hasNext()) {
										sortarArrayList.add(iterator.next());
									}
									parentList.clear();
									parentList.addAll(sortarArrayList);
						

									listView.setAdapter(null);
									adapter = new ParentAdapter(ParentListActivity.this,parentList);
									listView.invalidateViews();

									listView.setAdapter(adapter);
									tv_sort.setVisibility(View.VISIBLE);
									tv_sort.setText("Sorted By Name");
									textView_cancel.setVisibility(View.VISIBLE);
									break;
								case 1:
									ArrayList<Parent> sortarArrayList1=new ArrayList<Parent>();
						
									 Collections.sort(parentList, new Comparator<Parent>() {
										 public int compare(Parent v1, Parent v2) {
										 return v1.getLessonCount().compareTo(v2.getLessonCount());
										 }
										 });

										Iterator<Parent> iterator1 = parentList.iterator();
										while (iterator1.hasNext()) {
											sortarArrayList1.add(iterator1.next());
										}
										parentList.clear();
										parentList.addAll(sortarArrayList1);
								
									listView.setAdapter(null);
									adapter = new ParentAdapter(ParentListActivity.this,parentList);
									listView.invalidateViews();

									listView.setAdapter(adapter);
									tv_sort.setVisibility(View.VISIBLE);
									tv_sort.setText("Sorted By UpComing Lessions");
									textView_cancel.setVisibility(View.VISIBLE);
									break;
								case 2:
									ArrayList<Parent> sortArrayList_balance=new ArrayList<Parent>();
									 Collections.sort(parentList, new Comparator<Parent>() {
										 public int compare(Parent v1, Parent v2) {
										 return	v1.getOutstandingBalance().trim().compareTo(v2.getOutstandingBalance().trim());
										 }
										 });

										Iterator<Parent> iterator_balance = parentList.iterator();
										while (iterator_balance.hasNext()) {
											
											sortArrayList_balance.add(iterator_balance.next());
										}
										parentList.clear();
										parentList.addAll(sortArrayList_balance);
									
									listView.setAdapter(null);
									adapter = new ParentAdapter(ParentListActivity.this,parentList);
									listView.invalidateViews();
									listView.setAdapter(adapter);
									tv_sort.setVisibility(View.VISIBLE);
									tv_sort.setText("Sorted By OutStanding Balance");
									textView_cancel.setVisibility(View.VISIBLE);
									break;
								case 3:
									// Your code when 4th option seletced
									break;

								}
								levelDialog.dismiss();
							}
						});
				levelDialog = builder.create();
				levelDialog.show();

			}
		});

		filter_parentlist.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				// Strings to Show In Dialog with Radio Buttons
				final CharSequence[] items = { "Active Student this month ","By OutStanding Balance" };

				// Creating and Building the Dialog
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ParentListActivity.this);
				builder.setTitle("Select The Difficulty Level");
				builder.setSingleChoiceItems(items, -1,
				new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {

								switch (item) {
								case 0:
									SortedSet<Parent> personss11 = new TreeSet<Parent>(
											new Comparator<Parent>() {
												@Override
												public int compare(Parent arg0,Parent arg1) {
													return arg0.getStudentCount().compareTo(arg1.getStudentCount());
												}
											});

									Iterator<Parent> iterator11 = parentList.iterator();
									while (iterator11.hasNext()) {
										personss11.add(iterator11.next());
									}
									parentList.clear();
									parentList.addAll(personss11);

									listView.setAdapter(null);
									adapter = new ParentAdapter(ParentListActivity.this,parentList);
									listView.invalidateViews();
									listView.setAdapter(adapter);
									tv_sort.setVisibility(View.VISIBLE);
									tv_sort.setText("Fiter By OutStanding Balance");
									textView_cancel.setVisibility(View.VISIBLE);
									break;
								case 1:
									SortedSet<Parent> personss3 = new TreeSet<Parent>(
											new Comparator<Parent>() {
												@Override
												public int compare(Parent arg0,
														Parent arg1) {
													return arg0.getOutstandingBalance().compareTo(arg1.getOutstandingBalance());
												}
											});

									Iterator<Parent> iterator3 = parentList.iterator();
									while (iterator3.hasNext()) {
										personss3.add(iterator3.next());
									}
									
									parentList.clear();
									parentList.addAll(personss3);
									listView.setAdapter(null);
									adapter = new ParentAdapter(ParentListActivity.this,parentList);
									listView.invalidateViews();
									listView.setAdapter(adapter);
									tv_sort.setVisibility(View.VISIBLE);
									tv_sort.setText("Fiter By Active Student this month");
									textView_cancel.setVisibility(View.VISIBLE);
									break;

								}
								levelDialog.dismiss();
							}
						});
				levelDialog = builder.create();
				levelDialog.show();

			}
		});
		
	}


	public class ParentAdapter extends BaseAdapter 
	{			
		private Context context;
		private TextView parentName, studentCount, lessonCount, outstandingBalance,parentid;
		private ImageView call,email;
		private Parent parent;
		private List<Parent> parentList=null;
		private ArrayList<Parent> arraylist;
	   // public ArrayList<Parent> mDisplayedValues;
	  
		public ParentAdapter(Context ctx, List<Parent> parentList)
		{
			context = ctx;
			this.parentList = parentList;
			this.arraylist = new ArrayList<Parent>();
			this.arraylist.addAll(parentList);
		}

		public int getCount() {
			// TODO Auto-generated method stub
			return parentList.size();
		}

		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return parentList.get(position);
		}
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		public View getView(final int position, View convertView, ViewGroup group) {
			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			if(convertView == null){
			    convertView = inflater.inflate(R.layout.parentlist_row, group, false);
			}

			parent = parentList.get(position);
			
			
			parentName = (TextView)convertView.findViewById(R.id.name);
			studentCount = (TextView)convertView.findViewById(R.id.studentno);
			lessonCount = (TextView)convertView.findViewById(R.id.lessonCount);
			outstandingBalance = (TextView)convertView.findViewById(R.id.balance);
			call = (ImageView)convertView.findViewById(R.id.imageView_call);
			email = (ImageView)convertView.findViewById(R.id.imageView_email);
			parentid=(TextView)convertView.findViewById(R.id.parentid);
			parentid.setVisibility(View.GONE);
			parentName.setText(parent.getName());
		    studentCount.setText(": "+parent.getStudentCount());
		    lessonCount.setText(": "+parent.getLessonCount());
		    outstandingBalance.setText(": $"+parent.getOutstandingBalance());
		    parentid.setText(parent.getParentId());
		    call.setOnClickListener(new View.OnClickListener() {
				@Override
			public void onClick(View arg0) {
					String uri = "tel:" + parent.getContactNumber();
					 Intent intent = new Intent(Intent.ACTION_CALL);
					 intent.setData(Uri.parse(uri));
					 startActivity(intent);
				}
			});
		    email.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
					Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				            "mailto",parent.getEmail().trim(), null));
					startActivity(Intent.createChooser(emailIntent, "Send email..."));
				}
			});
			
			
			return convertView;
		}
		

		// Filter function
		public void filter(String charText) {
			charText = charText.toLowerCase();
			parentList.clear();
			if (charText.length() == 0) {
				parentList.addAll(arraylist);
			} else {
				for (Parent wp : arraylist) {
					if (wp.getName().toLowerCase() //search by parent name
							.contains(charText))
					{
						parentList.add(wp);
					}
					else if(wp.getParentId().toLowerCase() //search by parent id
							.contains(charText))
					{
						parentList.add(wp);
					}
				}
			}
			notifyDataSetChanged();
		}
	}
	@Override
	public void processFinish(String output, String methodName) {
		
		if(methodName.equals("fetch-parent")){
			parentList=new ArrayList<Parent>();
			parentList = parser.getParentlist(output);
			
		
			adapter = new ParentAdapter(ParentListActivity.this,parentList);
			listView.setAdapter(adapter);
			
			}
	}}
