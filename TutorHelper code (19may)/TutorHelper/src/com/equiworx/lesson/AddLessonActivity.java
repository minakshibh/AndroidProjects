package com.equiworx.lesson;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import com.equiworx.asynctasks.AsyncResponseForTutorHelper;
import com.equiworx.asynctasks.AsyncTaskForTutorHelper;
import com.equiworx.tutorhelper.R;
import com.equiworx.util.Util;

public class AddLessonActivity extends Activity implements AsyncResponseForTutorHelper{

	private Button btn_done;
	private CheckBox checkbox_sun,checkbox_mon,checkbox_tus,checkbox_wed,checkbox_thur,checkbox_sat,checkbox_fri,checkbox_all;
	private EditText ed_description;
	private ImageView back;
	private RadioGroup  radiogroup;
	private RadioButton   rbtn_yes;
	private RadioButton   rbtn_lno;
	private TextView btn_editst,btn_editet,ed_lessondate,ed_Lessonenddate;
	private TextView txt_title,txt_students,addStudent;
	
	private String str_starttime="",str_endtime="",str_day="",str_tutorid="";
	private String str_sun="",str_mon="",str_tus="",str_wed="",str_thur="",str_sat="",str_fri="",str_all="";
	private String str_isrec="0";
	private String str_duration="",str_startdate;
	public  String str_students_list="";
	private int mHour, mMinute,mSec, mYear, mMonth, mDay; 
	private int  sYear, sMonth, sDay; 
	public static ArrayList<String> arraylist_addstu=new ArrayList<String>();
	private String first_enddate,str_reqsender="";
 	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
 	private DateFormat timeformatter = new SimpleDateFormat("HH:mm:ss");  
	private SharedPreferences tutorPrefs;
	private int datecheck=0;
	private int timecheck=0;
	
	
	public  void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_addlesson);
		
		initialiselayout();
		onClickListeners();
		
	}

	@Override
	protected void onResume() {
	
		txt_students=(TextView)findViewById(R.id.textView_students);
		txt_students.setText(""+arraylist_addstu.size()+" Students");
		super.onResume();
	}

	private void initialiselayout() {
		
		back=(ImageView)findViewById(R.id.back);
		arraylist_addstu.clear();
		tutorPrefs = getSharedPreferences("tutor_prefs", MODE_PRIVATE);
		txt_title=(TextView)findViewById(R.id.title);
		txt_title.setText("Add Lesson");
		//ed_topic=(EditText)findViewById(R.id.topic);
		ed_description=(EditText)findViewById(R.id.desription);
		//ed_starttime=(EditText)findViewById(R.id.startdate);
		//ed_endtime=(EditText)findViewById(R.id.enddate);
		//txt_day=(TextView)findViewById(R.id.day);
		ed_lessondate=(TextView)findViewById(R.id.ed_Lessondate);
		//txt_liststudent=(TextView)findViewById(R.id.liststudents);
		String Student="0";
		Log.i("tag", "Student LIST::::::::::" +arraylist_addstu.size());
		for(int i=0;i<arraylist_addstu.size();i++)
			{
				if(i==0){
					Student=arraylist_addstu.get(i);
				}
				else if(i>0)
				{
					Student=Student+","+arraylist_addstu.get(i);
				}
				
				
			}
		//txt_liststudent.setText(arraylist_addstu.size() +" Students");
		btn_done=(Button)findViewById(R.id.done);
		addStudent=(TextView)findViewById(R.id.addstudents);
		String str="Select Students:";
		SpannableString content = new SpannableString(str);
		content.setSpan(new UnderlineSpan(), 0, str.length(), 0);
		addStudent.setText(content);
		
		btn_editst=(TextView)findViewById(R.id.starttime_edit);
		btn_editet=(TextView)findViewById(R.id.endtime_edit);
		
		
//check box
		checkbox_all=(CheckBox)findViewById(R.id.checkBox_all);
		checkbox_sun=(CheckBox)findViewById(R.id.checkBox_sun);
		checkbox_mon=(CheckBox)findViewById(R.id.checkBox_mon);
		checkbox_tus=(CheckBox)findViewById(R.id.checkBox_tue);
		checkbox_wed=(CheckBox)findViewById(R.id.checkBox_wed);
		checkbox_thur=(CheckBox)findViewById(R.id.checkBox_thur);
		checkbox_fri=(CheckBox)findViewById(R.id.checkBox_fri);
		checkbox_sat=(CheckBox)findViewById(R.id.checkBox_sat);

		ed_Lessonenddate=(TextView)findViewById(R.id.ed_Lessonenddate);
//menu list
		//rightmenulayout=(LinearLayout)findViewById(R.id.llayout);
	//	rightmenu=(ImageView)findViewById(R.id.rightmenu);
	//	btn_lessonlist=(Button)findViewById(R.id.btn_lessonlist);
	//	btn_mylesson=(Button)findViewById(R.id.btn_mylesson);
//radio list
		radiogroup = (RadioGroup) findViewById(R.id.radio_isrec);
		rbtn_yes=(RadioButton)findViewById(R.id.radioButton_yes);
		rbtn_lno=(RadioButton)findViewById(R.id.radioButton_no);	
	}
	private void onClickListeners() {
		getCurrentTime();
		back.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
     	
//for start time.
     	
     	 String stime=mHour + ":" + mMinute+":"+mSec;
     	
    	 Date date = null;
			try {
				date = (Date)timeformatter.parse(stime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			timeformatter = new SimpleDateFormat("HH:mm:ss");
			str_starttime = timeformatter.format(date);
	    	btn_editst.setText(str_starttime);
	  //  btn_editst.setText(mHour + ":" + mMinute+":"+mSec);
		btn_editst.setOnClickListener(new View.OnClickListener() {
		public void onClick(View v) {
		
            // Launch Time Picker Dialog
	            TimePickerDialog tpd = new TimePickerDialog(AddLessonActivity.this,
	                    new TimePickerDialog.OnTimeSetListener() {
	                    public void onTimeSet(TimePicker view, int hourOfDay,
	                                int minute) {
	                        	 String stime=hourOfDay + ":" + minute+":"+mSec;
	                        	 mHour=hourOfDay;
	                         	mMinute=minute;
	                        	 Date date = null;
									try {
										date = (Date)timeformatter.parse(stime);
									} catch (ParseException e) {
										e.printStackTrace();
									}
									
									timeformatter = new SimpleDateFormat("HH:mm:ss");
									str_starttime = timeformatter.format(date);
			        		    	 btn_editst.setText(str_starttime);
			        		    	 btn_editet.setText("select end time"); 
			        		    	 //check time  	 
			        		    	  try{
	                        			  Calendar c = Calendar.getInstance();
	                        			  System.out.println("Current time => " + c.getTime());
 			
	                        			  String currentdate = timeformatter.format(c.getTime());
	                        			  
	 			        			     Date seldate = timeformatter.parse(str_starttime);
	 			        			     Date curdate = timeformatter.parse(currentdate);

	 			        			     if (curdate.before(seldate))
	 			        			      {
	 			        			         System.out.println("date2 is Greater than my date1");    
	 			        			      
	 			        			      }
	 			        			     else if(curdate.equals(seldate))
	 			        			     {
	 			        			    	// Util.alertMessage(AddLesson.this, "Select date Greater than start lesson date");  
	 			        			    	// ed_Lessonenddate.setText("select lesson end date"); 
	 			        			     }
	 			        			     else
	 			        			     {
	 			        			    	if(datecheck==1)
	 			        			    	{
	 			        			    		
	 			        			    	}
	 			        			    	else{
	 			        			    	 Util.alertMessage(AddLessonActivity.this, "Please select Greater time from current time");  
	 			        			    	btn_editst.setText("select start time"); 
	 			        			    	}
	 			        			     	}

	 			        			    }catch (ParseException e1){
	 			        			        e1.printStackTrace();
	 			        			    }
	                        } 
	                    }, mHour, mMinute, false);
	            tpd.show();	
	
			}
		});
	
	
		
		
		//for end time
		getCurrentTime();
		 String str_etime=(mHour+1) + ":" + (mMinute)+":"+mSec;
	     	
    	 Date dateendt = null;
			try {
				dateendt = (Date)timeformatter.parse(str_etime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			timeformatter = new SimpleDateFormat("HH:mm:ss");
			str_endtime = timeformatter.format(dateendt);
	    	
		btn_editet.setText(str_endtime);
		btn_editet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
            TimePickerDialog tpd = new TimePickerDialog(AddLessonActivity.this, new TimePickerDialog.OnTimeSetListener() {
             public void onTimeSet(TimePicker view, int hourOfDay,int minute) {
                	// Display Selected time in textbox
                	String stime=hourOfDay + ":" + minute+":"+mSec;
                	mHour=hourOfDay;
                	mMinute=minute;
                	Date date = null;
                	try {
                		date = (Date)timeformatter.parse(stime);
                	} catch (ParseException e) {
                		e.printStackTrace();
                	}

                	timeformatter = new SimpleDateFormat("HH:mm:ss");
                	str_endtime = timeformatter.format(date);
                	btn_editet.setText(""+str_endtime);

                	//check time  	 
                	try{
                		Calendar c = Calendar.getInstance();
                		System.out.println("Current time => " + c.getTime());

                		//  String currentdate = timeformatter.format(c.getTime());

                		Date seldate = timeformatter.parse(str_endtime);
                		Date curdate = timeformatter.parse(str_starttime);

                		if (curdate.before(seldate))
                		{
                			System.out.println("date2 is Greater than my date1");    

                			}
                		else if(curdate.equals(seldate))
                		{
                			Util.alertMessage(AddLessonActivity.this, "Please select Greater time from lesson start time");  
                			btn_editet.setText("select end time"); 
                			}
                		else
                		{
                			Util.alertMessage(AddLessonActivity.this, "Please select Greater time from lesson start time");  
                			btn_editet.setText("select end time"); 
                			}

                	}catch (ParseException e1){
                		e1.printStackTrace();
                	}

                } 
            }, mHour, mMinute, false);
            tpd.show();	
						
				}
			});
	
		//end time end
		//check box
		checkbox_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
				{
					checkbox_sun.setChecked(true);
					checkbox_mon.setChecked(true);
					checkbox_tus.setChecked(true);
					checkbox_wed.setChecked(true);
					checkbox_thur.setChecked(true);
					checkbox_fri.setChecked(true);
					checkbox_sat.setChecked(true);
					}
				else
				{
					checkbox_sun.setChecked(false);
					checkbox_mon.setChecked(false);
					checkbox_tus.setChecked(false);
					checkbox_wed.setChecked(false);
					checkbox_thur.setChecked(false);
					checkbox_fri.setChecked(false);
					checkbox_sat.setChecked(false);
					}
				
			}
		});
		checkbox_sun.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_sun="Sunday,";
						}
					else
					{
						str_sun="";
						}
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_mon.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_mon="Monday,";
						}
					else
					{
						str_mon="";
						}
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_tus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_tus="Tuesday,";
						}
					else
					{
						str_tus="";
						}	
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_wed.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_wed="Wednesday,";
						}
					else
					{
						str_wed="";
						}
					str_day=str_sun+str_mon+str_tus+str_wed+str_thur+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_thur.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_thur="Thursday,";
						}
					else
					{
						str_thur="";
						}
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_fri.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_fri="Friday,";
						}
					else
					{
						str_fri="";
						}
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		checkbox_sat.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked)
					{
						str_sat="Saturday,";
						}
					else
					{
						str_sat="";
						}
					str_day=str_sun+str_mon+str_tus+str_thur+str_wed+str_fri+str_sat;
					System.err.println("total="+str_day);
				}
			});
		//check box end
		

		// start date
		getCurretDate();
		
		String sdate=mYear + "-" + (mMonth+1) + "-" + mDay;
		 Date date1 = null;
			try {
				date1 = (Date)formatter.parse(sdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			 formatter = new SimpleDateFormat("yyyy-MM-dd");
			 str_startdate = formatter.format(date1);
	    	 ed_lessondate.setText(str_startdate);
	
		ed_lessondate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
      DatePickerDialog dpd = new DatePickerDialog(AddLessonActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year,
               int monthOfYear, int dayOfMonth) {
     	      long currentdateintoInt= System.currentTimeMillis();
     	      String sdate=year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
     	      mYear=year;
     	      mMonth=monthOfYear;
     	      mDay=dayOfMonth;
                	 Date date = null;
					try {
						date = (Date)formatter.parse(sdate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					datecheck=0;
					formatter = new SimpleDateFormat("yyyy-MM-dd");
    		    	str_startdate = formatter.format(date);
                	ed_lessondate.setText(""+str_startdate);
                	ed_Lessonenddate.setText("select end date"); 
                	
                	//check date condition 
                		
                             		
                		  try{
                			  Calendar c = Calendar.getInstance();
                			  System.out.println("Current time => " + c.getTime());
                			  String currentdate = formatter.format(c.getTime());
                		      Date seldate = formatter.parse(str_startdate);
	        			      Date curdate = formatter.parse(currentdate);
	        			     if (curdate.before(seldate))
	        			      {
	        			         System.out.println("date2 is Greater than my date1");    
	        			         datecheck=1;
	        			      		}
	        			     else if(curdate.equals(seldate))
	        			     { 
	        			    	//  datecheck=1;
	        			     }
	        			     else
	        			     {
	        			    	Util.alertMessage(AddLessonActivity.this, "Please select Greater date from current date");  
	        			    	ed_lessondate.setText("select start date"); 
	        			     	}

	        			    }catch (ParseException e1){
	        			        e1.printStackTrace();
	        			    	}
                			 
	                        }
	                    }, mYear, mMonth, mDay);
	            dpd.show();
			}
		});
	
		// end date
		getNextSixMonthDate();
		String edate=sYear + "-" + (sMonth+1) + "-" + sDay;
		 Date dateend = null;
			try {
				dateend = (Date)formatter.parse(edate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			 formatter = new SimpleDateFormat("yyyy-MM-dd");
			 first_enddate = formatter.format(dateend);
	    
		
		ed_Lessonenddate.setText(first_enddate);
		ed_Lessonenddate.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
			         
		            // Launch Date Picker Dialog
		            DatePickerDialog dpd = new DatePickerDialog(AddLessonActivity.this, new DatePickerDialog.OnDateSetListener() {
                   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                  	String sdate=year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                  		mYear=year;
                  		mMonth=monthOfYear;
                    	mDay=dayOfMonth;    				
        		    	Date date = null;
						try {
							date = (Date)formatter.parse(sdate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						 formatter = new SimpleDateFormat("yyyy-MM-dd");
        		    	 String  s = formatter.format(date);
        		    	 ed_Lessonenddate.setText(""+s);
        		    	 
        		    	 //check date
        		  try{
        			     Date seldate = formatter.parse(s);
        			     Date curdate = formatter.parse(str_startdate);

        			     if (curdate.before(seldate))
        			      {
        			         System.out.println("date2 is Greater than my date1");    
        			      	}
        			     else if(curdate.equals(seldate))
        			     {
        			    	 Util.alertMessage(AddLessonActivity.this, "Please select Greater date from lesson start date");  
        			    	 ed_Lessonenddate.setText("select end date"); 
        			     	}
        			     else
        			     {
        			    	 Util.alertMessage(AddLessonActivity.this, "Please select Greater date from lesson start date");  
        			    	 ed_Lessonenddate.setText("select end date"); 
        			     	}

        			    }catch (ParseException e1){
        			        e1.printStackTrace();
        			    }
                    }
                }, mYear, mMonth, mDay);
		            dpd.show();
			}
		});
				radiogroup.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() 
	        {
	            public void onCheckedChanged(RadioGroup group, int checkedId) {
	                switch(checkedId){
	                    case R.id.radioButton_yes:
	                        // do operations specific to this selection
	                    	str_isrec="1";
	                    	System.err.println("str="+str_isrec);
	                    break;

	                    case R.id.radioButton_no:
	                    	str_isrec="0";
	                    	System.err.println("str="+str_isrec);
	                    break;
	                }}
	        });
				
		addStudent.setOnClickListener(new OnClickListener() {
		public void onClick(View v) {
			Intent i=new Intent(AddLessonActivity.this,StudentListActivity.class);
			i.putExtra("parentID", getIntent().getStringExtra("parentID"));
			i.putExtra("tuterID", getIntent().getStringExtra("tuterID"));
			startActivity(i);
				
			}
		});
		/*Button lesson=(Button)findViewById(R.id.lessonlist);
		lesson.setOnClickListener(new OnClickListener() {
		
			public void onClick(View v) {
				
				Intent i=new Intent(AddLesson.this,LessonRequestActivity.class);
				startActivity(i);
			}
		});
		Button lesson1=(Button)findViewById(R.id.mylist);
		lesson1.setOnClickListener(new OnClickListener() {
		
			public void onClick(View v) {
				
				Intent i=new Intent(AddLesson.this,MyLessonActivity.class);
				startActivity(i);
			}
		});*/
		
	
		/*rightmenu.setVisibility(View.VISIBLE);
		rightmenulayout.setVisibility(View.GONE);
		rightmenu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				View child = getLayoutInflater().inflate(R.layout.menu_addlesson, null, false);
				child.setVisibility(View.VISIBLE);
					if(rightmenulayout.getVisibility()==View.GONE)
					{
						rightmenulayout.setVisibility(View.VISIBLE);
						System.err.println("VISIBLE click");
						
					}
					else if(rightmenulayout.getVisibility()==View.VISIBLE)
					{
						rightmenulayout.setVisibility(View.GONE);
						System.err.println("GONE click");
					}
				}
			});*/

/*	btn_lessonlist.setOnClickListener(new OnClickListener() {

		public void onClick(View v) {
			System.err.println("click");
			Intent i=new Intent(AddLesson.this,LessonRequestActivity.class);
			startActivity(i);
			
		}
	});

	btn_mylesson.setOnClickListener(new OnClickListener() {

		public void onClick(View v) {
			System.err.println("click");
			Intent i=new Intent(AddLesson.this,MyLessonActivity.class);
			startActivity(i);
			
		}
	});*/
	btn_done.setOnClickListener(new View.OnClickListener() {
	public void onClick(View v) {
		
		String check = emptyFieldCheck();
		
		for(int i=0;i<arraylist_addstu.size();i++)
		{
			if(i==0)
			{
				str_students_list=arraylist_addstu.get(i);
			}
			else if(i>0)
			{
				str_students_list=str_students_list+","+arraylist_addstu.get(i);
			}
		}
	
		
		if(check.equals("success"))
		{
			timecheck();
			if(datecheck==1)
			{
				Add_Lesson();
				}
			else
				{
					if(timecheck==1)
					{
						Add_Lesson();
						}
					else{
						Util.alertMessage(AddLessonActivity.this, "Please select Greater time from current time"); 
						}
							
					}
			
		}else
			Util.alertMessage(AddLessonActivity.this, check);
			}
	});
		
	}
	public void Add_Lesson()
	{
		if (Util.isNetworkAvailable(AddLessonActivity.this)){
			
			if(tutorPrefs.getString("mode", "").equalsIgnoreCase("parent"))
			{
				str_reqsender="Parent";
				str_tutorid=getIntent().getStringExtra("tutorid");
			}
			else if(tutorPrefs.getString("mode", "").equalsIgnoreCase("tutor"))
			{
				str_reqsender="Tutor";
				str_tutorid= tutorPrefs.getString("tutorID","");
				}
		
		findDuration();
		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("tutor_id",str_tutorid));
		nameValuePairs.add(new BasicNameValuePair("topic", ""));
		nameValuePairs.add(new BasicNameValuePair("description", ed_description.getText().toString()));
		nameValuePairs.add(new BasicNameValuePair("start_time", btn_editst.getText().toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("end_time", btn_editet.getText().toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("days",str_day));
		nameValuePairs.add(new BasicNameValuePair("lesson_date", ed_lessondate.getText().toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("end_date", ed_Lessonenddate.getText().toString().trim()));
		nameValuePairs.add(new BasicNameValuePair("duration",  ""+str_duration));
		nameValuePairs.add(new BasicNameValuePair("is_rec", str_isrec));
		nameValuePairs.add(new BasicNameValuePair("student_list", str_students_list));
		nameValuePairs.add(new BasicNameValuePair("req_sender", str_reqsender));
		
		Log.e("add lesson=", nameValuePairs.toString());
		
		AsyncTaskForTutorHelper mLogin = new AsyncTaskForTutorHelper(AddLessonActivity.this, "create-lesson", nameValuePairs, true, "Please wait...");
		mLogin.delegate = (AsyncResponseForTutorHelper) AddLessonActivity.this;
		mLogin.execute();
	}
		else {
		Util.alertMessage(AddLessonActivity.this,
				"Please check your internet connection");
	}
	}
	private void getCurrentTime() {
		 final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSec=c.get(Calendar.SECOND);
     		
	}
/*	private void getnextTime() {
		 final Calendar c = Calendar.getInstance();
       nHour = c.get(Calendar.HOUR_OF_DAY);
       nMinute = c.get(Calendar.MINUTE);
       nSec=c.get(Calendar.SECOND);
    		
	}*/
	private void getCurretDate() {
		  final Calendar c = Calendar.getInstance();
          mYear = c.get(Calendar.YEAR);
          mMonth = c.get(Calendar.MONTH);
          mDay = c.get(Calendar.DAY_OF_MONTH);
          
 	}

	private void getNextSixMonthDate() {
	  Calendar cal = Calendar.getInstance(); //Get the Calendar instance
		  cal.add(Calendar.MONTH,6);//Three months from now
		  cal.getTime();
        sYear = cal.get(Calendar.YEAR);
        sMonth = cal.get(Calendar.MONTH);
        sDay = cal.get(Calendar.DAY_OF_MONTH);
        
	}



	

	protected String emptyFieldCheck() {
		 if(ed_description.getText().toString().trim().equals("")){
			return "Please enter description";
		}else if(btn_editst.getText().toString().trim().equals("select start time")){
			return "Please select start time";
		}else if(btn_editet.getText().toString().trim().equals("select end time")){
			return "Please select end time";
	
		}else if(ed_lessondate.getText().toString().trim().equals("select start date")){
			return "Please select start lesson date";
		}else if(arraylist_addstu.size()==0){
			return "Please add student";
		}else if(ed_Lessonenddate.getText().toString().trim().equals("select end date")){
			return "Please select lesson end date";
		}else if(str_day.equals("")){
			return "Please select day";
		}else{
			return "success";
		}
	}

	public void findDuration()
	{
		int stime=Integer.parseInt(btn_editst.getText().toString().trim().replace(":", ""));
		  
		int etime=	Integer.parseInt(btn_editet.getText().toString().trim().replace(":", ""));
		  int total=etime-stime;
		  String str_dur=""+total;
		  if(str_dur.length()==3)
		  {
			 	 Date date = null;
				try {
					DateFormat dtimeformatter = new SimpleDateFormat("mss");  
					date = (Date)dtimeformatter.parse(str_dur);
					
					timeformatter = new SimpleDateFormat("HH:mm:ss");
					str_duration = timeformatter.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		  }
		  else  if(str_dur.length()==4)
		  {
			 	 Date date = null;
				try {
					DateFormat dtimeformatter = new SimpleDateFormat("mmss");  
					date = (Date)dtimeformatter.parse(str_dur);
					
					timeformatter = new SimpleDateFormat("HH:mm:ss");
					str_duration = timeformatter.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
		  }
		 else  if(str_dur.length()==5)
				  {
					 	 Date date = null;
						try {
							DateFormat dtimeformatter = new SimpleDateFormat("hmmss");  
							date = (Date)dtimeformatter.parse(str_dur);
							
							timeformatter = new SimpleDateFormat("HH:mm:ss");
							str_duration = timeformatter.format(date);
						} catch (ParseException e) {
							e.printStackTrace();
						}
		  }
		 else  if(str_dur.length()==6)
		  {
			 	 Date date = null;
				try {
					DateFormat dtimeformatter = new SimpleDateFormat("hhmmss");  
					date = (Date)dtimeformatter.parse(str_dur);
					
					timeformatter = new SimpleDateFormat("HH:mm:ss");
					str_duration = timeformatter.format(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}}
		}
	public void processFinish(String output, String methodName) {
		if(methodName.equals("create-lesson"))
		{
			try {
				//TutorLesson tutorLesson=new TutorLesson();
				JSONObject jsonChildNode = new JSONObject(output);	
				String result =jsonChildNode.getString("result");
				String message=jsonChildNode.getString("message");
				if(result.equals("0"))
				{	finish();	
					}
				else
				{
					Util.alertMessage(AddLessonActivity.this, message);
					}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
	}
public void timecheck()
{
	  try{
		  Calendar c = Calendar.getInstance();
		  System.out.println("Current time => " + c.getTime());

		  String currentdate = timeformatter.format(c.getTime());
		  
		     Date seldate = timeformatter.parse(str_starttime);
		     Date curdate = timeformatter.parse(currentdate);

		     if (curdate.before(seldate))
		      {
		         System.out.println("date2 is Greater than my date1"); 
		         System.err.println("1");
		         timecheck=1;
		        
		      }
		     else if(curdate.equals(seldate))
		     {
		    	// Util.alertMessage(AddLesson.this, "Select date Greater than start lesson date");  
		    	// ed_Lessonenddate.setText("select lesson end date"); 
		    	 timecheck=1;
		       
		    	 System.err.println("=");
		     }
		     else
		     {
		    	//Util.alertMessage(AddLesson.this, "Please select Greater time from current time"); 
		    	
		    	//btn_editst.setText("select start time"); 
		    	 System.err.println("3");
		    	 timecheck=0;
		     	}

		    }catch (ParseException e1){
		        e1.printStackTrace();
		    }	
}
}