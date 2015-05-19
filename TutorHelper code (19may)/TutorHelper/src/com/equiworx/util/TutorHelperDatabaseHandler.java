package com.equiworx.util;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.equiworx.model.StudentList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class TutorHelperDatabaseHandler extends SQLiteOpenHelper{
 
    //The Android's default system path of your application database.
    @SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.qrpatrol.activity/databases/";
 
    private static String DB_NAME = "TutorHelper_db.sqlite";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    private String TABLE_STUDENT_DETAILS = "StudentDetails";
   
	

	//field for StudentList table
	private String StudentId = "studentId";
	private String Name = "name";
	private String Email = "email";
	private String Address = "address";
	private String ContactInfo = "contactInfo";
	private String Gender = "gender";
	private String IsActiveInMonth = "isActiveInMonth";
	private String Fees = "fees";
	private String Notes = "notes";
	private String ParentId = "parentId";
	

		// last_updated
		
	
	
	SQLiteCursor cursor;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
	
    public TutorHelperDatabaseHandler(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
   
	@Override
	public void onCreate(SQLiteDatabase db)
	{
		
		
		String CREATE_STUDENT_TABLE = "CREATE TABLE if NOT Exists " + TABLE_STUDENT_DETAILS + "("
				+ StudentId + " TEXT," + Name + " TEXT," + Email + " TEXT,"
				+ Address + " TEXT,"+ ContactInfo + " TEXT,"+ Gender + " TEXT,"+ IsActiveInMonth + " TEXT,"+ Fees + " TEXT,"
				+ Notes + " TEXT ,"+ ParentId + " TEXT)";

		

		
	
		db.execSQL(CREATE_STUDENT_TABLE);

	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
 
	}
	
	public void deleteStudentDetails(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STUDENT_DETAILS, null, null);
		db.close();
	}

//edit schedule
	public void updateStudentDetails(ArrayList<StudentList> schedule) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<schedule.size(); i++){
			StudentList studentList = schedule.get(i);
			String selectQuery = "SELECT  * FROM "+TABLE_STUDENT_DETAILS+" where "+StudentId+"="+ studentList.getStudentId();  
	
			try{
				ContentValues values = new ContentValues();
				values.put(StudentId, studentList.getStudentId()); 
				values.put(Name, studentList.getName());
				values.put(Email, studentList.getEmail());
				values.put(Address, studentList.getAddress());
				values.put(ContactInfo, studentList.getContactInfo());
				values.put(Gender, studentList.getGender());
				values.put(IsActiveInMonth, studentList.getIsActiveInMonth());
				values.put(Fees, studentList.getFees());
				values.put(Notes, studentList.getNotes());
				values.put(ParentId, studentList.getParentId());
				
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					// updating row
					int a=db.update(TABLE_STUDENT_DETAILS, values, StudentId + " = ?",
							new String[] { String.valueOf(studentList.getStudentId()) });
				}else{
					db.insert(TABLE_STUDENT_DETAILS, null, values);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
    }
	

		//get student list
		public StudentList getStudentDetails(String trigger,String id) {
		
				StudentList studentList = new StudentList();
			
				String selectQuery = null;
				if(trigger.equals("all"))
				{
					selectQuery = "SELECT  * FROM "+TABLE_STUDENT_DETAILS;    
					}
				else
				{
					selectQuery = "SELECT  * FROM "+TABLE_STUDENT_DETAILS+" where "+StudentId+" = "+id;
					}
				SQLiteDatabase db = this.getReadableDatabase();
				
				try{
					cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
					if (cursor.moveToFirst()) {
						do {
							
									
							//StudentList studentList = new StudentList();
							studentList.setStudentId(cursor.getString(cursor.getColumnIndex(StudentId)));
							studentList.setName(cursor.getString(cursor.getColumnIndex(Name)));
							studentList.setEmail(cursor.getString(cursor.getColumnIndex(Email)));
							studentList.setAddress(cursor.getString(cursor.getColumnIndex(Address)));
							studentList.setContactInfo(cursor.getString(cursor.getColumnIndex(ContactInfo)));
							studentList.setGender(cursor.getString(cursor.getColumnIndex(Gender)));
							studentList.setIsActiveInMonth(cursor.getString(cursor.getColumnIndex(IsActiveInMonth)));
							
							studentList.setFees(cursor.getString(cursor.getColumnIndex(Fees)));
							studentList.setNotes(cursor.getString(cursor.getColumnIndex(Notes)));
							studentList.setParentId(cursor.getString(cursor.getColumnIndex(ParentId)));
						
												//array_studentlist.set(studentList);
						} while (cursor.moveToNext());
					}
					
					cursor.getWindow().clear();
					cursor.close();
					// close inserting data from database
					db.close();
					// return city list
					return studentList;
					
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (cursor != null)
					{
						cursor.getWindow().clear();
						cursor.close();
					}
					
					db.close();
					return studentList;
				}
			}

	/*	//edit logs
				public void updateLogs(ArrayList<LogList> arraylogs) {
					SQLiteDatabase db = this.getWritableDatabase();
					for(int i = 0; i<arraylogs.size(); i++){
						LogList logslist = arraylogs.get(i);
						String selectQuery = "SELECT  * FROM "+TABLE_LOGS+" where "+logid+"="+ logslist.getLogId();  
						
						try{
							
							ContentValues values = new ContentValues();
							values.put(CheckPoint_Id, logslist.getCheckPointId()); 
							values.put(logid, logslist.getLogId());
							values.put(passedbyofficerid, logslist.getPassedByOfficerId());
							values.put(passedbyofficername, logslist.getPassedByOfficerName());
							values.put(passedbyofficercontactdetail, logslist.getPassedByOfficerContactInfo());
							values.put(description, logslist.getDescription());
							values.put(notes, logslist.getNotes());
							values.put(observation, logslist.getObservation());
							values.put(shiftid, logslist.getShiftId());
							values.put(lastupdated, logslist.getLast_updated());
						
							
							cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
							if (cursor.moveToFirst()) {
								// updating row
								int a=db.update(TABLE_LOGS, values, logid + " = ?",
										new String[] { String.valueOf(logslist.getLogId()) });
							}else{
								db.insert(TABLE_LOGS, null, values);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
					}
					db.close();
			    }
				
		//delete logs
		public void deleteLogs(){
			SQLiteDatabase db = this.getWritableDatabase();
			db.delete(TABLE_LOGS, null, null);
			db.close();
		}
		
		//get logs
				public ArrayList<LogList> getLogs(String trigger) {
				
						ArrayList<LogList> array_logList = new ArrayList<LogList>();
					
						String selectQuery;
						if(trigger.equals("all"))
							selectQuery = "SELECT  * FROM "+TABLE_LOGS;              
						else
							selectQuery = "SELECT  * FROM "+TABLE_LOGS+" where "+isChecked+" = "+"'true'";
						
						SQLiteDatabase db = this.getReadableDatabase();
						
						try{
							cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
							if (cursor.moveToFirst()) {
								do {
									
											
									LogList logList = new LogList();
									logList.setCheckPointId(cursor.getString(cursor.getColumnIndex(CheckPoint_Id)));
									logList.setLogId(cursor.getString(cursor.getColumnIndex(logid)));
									logList.setPassedByOfficerId(cursor.getString(cursor.getColumnIndex(passedbyofficerid)));
									logList.setPassedByOfficerName(cursor.getString(cursor.getColumnIndex(passedbyofficername)));
									logList.setPassedByOfficerContactInfo(cursor.getString(cursor.getColumnIndex(passedbyofficercontactdetail)));
									logList.setDescription(cursor.getString(cursor.getColumnIndex(description)));
									logList.setNotes(cursor.getString(cursor.getColumnIndex(notes)));
									logList.setObservation(cursor.getString(cursor.getColumnIndex(observation)));
									logList.setShiftId(cursor.getString(cursor.getColumnIndex(shiftid)));
									logList.setLast_updated(cursor.getString(cursor.getColumnIndex(lastupdated)));							
								
									array_logList.add(logList);
								} while (cursor.moveToNext());
							}
							
							cursor.getWindow().clear();
							cursor.close();
							// close inserting data from database
							db.close();
							// return city list
							return array_logList;
							
						}
						catch (Exception e)
						{
							e.printStackTrace();
							if (cursor != null)
							{
								cursor.getWindow().clear();
								cursor.close();
							}
							
							db.close();
							return array_logList;
						}
					}
*/

/*	public void updateIncidents(ArrayList<Incident> incidents) {
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<incidents.size(); i++){
			Incident incident = incidents.get(i);
			String selectQuery = "SELECT  * FROM "+TABLE_INCIDENT+" where "+Incident_Id+"="+ incident.getId();  
			
			try{
				ContentValues values = new ContentValues();
				values.put(Incident_Id, incident.getId()); 
				values.put(Incident_Desc, incident.getDesc());
				values.put(Incident_isChecked, incident.isChecked().toString());
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					// updating row
					int a=db.update(TABLE_INCIDENT, values, Incident_Id + " = ?",
							new String[] { String.valueOf(incident.getId()) });
				}else{
					db.insert(TABLE_INCIDENT, null, values);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
    }*/
	
/*	public void markCheckPointAsChecked(String checkPointId, String reportedTime, SQLiteDatabase db) {
		SQLiteDatabase db = this.getWritableDatabase();
		
			try{
				ContentValues values = new ContentValues();
				values.put(CheckedTime, reportedTime); 
				values.put(isChecked, "true");
				
			
				int a=db.update(TABLE_SCHEDULE, values, CheckPoint_Id + " = ?",
							new String[] { String.valueOf(checkPointId) });
				
			}catch(Exception e){
				e.printStackTrace();
			}
//		db.close();
    }*/
	
/*	public void deleteOfficer(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_OFFICER, null, null);
		db.close();
	}
	
	public void deleteEvents(){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_EVENT, null, null);
		db.close();
	}*/
	
	/*public void saveOfficerDetails(Officer officer){
		SQLiteDatabase db = this.getWritableDatabase();
		try{
			ContentValues values = new ContentValues();
			values.put(officerId, officer.getOfficerId()); 
			values.put(firstName, officer.getFirstName()); 
			values.put(lastName, officer.getLastName()); 
			values.put(email, officer.getEmail()); 
			values.put(contactInfo, officer.getContactInfo()); 
			values.put(alt_info, officer.getAlt_info()); 
			values.put(per_address, officer.getPer_address()); 
			values.put(cur_address, officer.getCur_address()); 
			values.put(State, officer.getState()); 
			values.put(City, officer.getCity()); 
			values.put(Country, officer.getCountry()); 
			values.put(ZipCode, officer.getZipcode()); 
			values.put(DOJ, officer.getDOJ()); 
			values.put(shiftId, officer.getShiftId()); 
			
			db.insert(TABLE_OFFICER, null, values);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		db.close();
	}
	
	public Officer getOfficerDetails(){
		Officer officer = new Officer();
		
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_OFFICER;              
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					officer.setOfficerId(cursor.getString(cursor.getColumnIndex(officerId)));
					officer.setFirstName(cursor.getString(cursor.getColumnIndex(firstName))); 
					officer.setLastName(cursor.getString(cursor.getColumnIndex(lastName)));
					officer.setEmail(cursor.getString(cursor.getColumnIndex(email)));
					officer.setContactInfo(cursor.getString(cursor.getColumnIndex(contactInfo))); 
					officer.setAlt_info(cursor.getString(cursor.getColumnIndex(alt_info)));
					officer.setPer_address(cursor.getString(cursor.getColumnIndex(per_address)));
					officer.setCur_address(cursor.getString(cursor.getColumnIndex(cur_address)));
					officer.setState(cursor.getString(cursor.getColumnIndex(State)));
					officer.setCity(cursor.getString(cursor.getColumnIndex(City)));
					officer.setCountry(cursor.getString(cursor.getColumnIndex(Country)));
					officer.setZipcode(cursor.getString(cursor.getColumnIndex(ZipCode)));
					officer.setDOJ(cursor.getString(cursor.getColumnIndex(DOJ)));
					officer.setShiftId(cursor.getString(cursor.getColumnIndex(shiftId)));
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return officer;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return officer;
		}
	}
	public ArrayList<Incident> getAllIncidents() {
		ArrayList<Incident> incidentList = new ArrayList<Incident>();
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_INCIDENT;              
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Incident incident = new Incident();
					incident.setId(cursor.getString(cursor.getColumnIndex(Incident_Id)));
					incident.setDesc(cursor.getString(cursor.getColumnIndex(Incident_Desc)));
					incident.setIsChecked(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(Incident_isChecked))));
					
					incidentList.add(incident);
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return incidentList;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return incidentList;
		}
	}
	//edit amrik
	public ArrayList<CheckPoint> getSchedule(String trigger) {
	//public CheckPoint getSchedule(String trigger) {
		ArrayList<CheckPoint> schedule = new ArrayList<CheckPoint>();
		//CheckPoint schedule = new CheckPoint();
		// Select All Query
		String selectQuery;
		if(trigger.equals("all"))
			selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE;              
		else
			selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE+" where "+isChecked+" = "+"'true'";
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					
					CheckPoint checkPoint = new CheckPoint();
					checkPoint.setCheckPointId(cursor.getString(cursor.getColumnIndex(CheckPoint_Id)));
					checkPoint.setPrefferedTime(cursor.getString(cursor.getColumnIndex(PrefferedTime)));
					checkPoint.setPriority(cursor.getString(cursor.getColumnIndex(Priority)));
					checkPoint.setName(cursor.getString(cursor.getColumnIndex(CheckPointName)));
					checkPoint.setAddress(cursor.getString(cursor.getColumnIndex(Address)));
					checkPoint.setCity(cursor.getString(cursor.getColumnIndex(City)));
					checkPoint.setState(cursor.getString(cursor.getColumnIndex(State)));
					checkPoint.setCountry(cursor.getString(cursor.getColumnIndex(Country)));
					checkPoint.setZipcode(cursor.getString(cursor.getColumnIndex(ZipCode)));
					checkPoint.setContactInfo(cursor.getString(cursor.getColumnIndex(ContactInfo)));
					checkPoint.setAltContact(cursor.getString(cursor.getColumnIndex(AlternateContact)));
					checkPoint.setLatitude(cursor.getString(cursor.getColumnIndex(Latitude)));
					checkPoint.setLongitude(cursor.getString(cursor.getColumnIndex(Longitude)));
					checkPoint.setNotes(cursor.getString(cursor.getColumnIndex(Notes)));
					checkPoint.setOpenTimings(cursor.getString(cursor.getColumnIndex(OpenTimings)));
					checkPoint.setCloseTimings(cursor.getString(cursor.getColumnIndex(CloseTimings)));
					checkPoint.setIsChecked(cursor.getString(cursor.getColumnIndex(isChecked)));
					checkPoint.setCheckedTime(cursor.getString(cursor.getColumnIndex(CheckedTime)));
					
					schedule.add(checkPoint);
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return schedule;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return schedule;
		}
	}
	
	public CheckPoint getCheckPoint(String id, SQLiteDatabase db) {
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_SCHEDULE +" where "+CheckPoint_Id +"="+id;              
		SQLiteCursor cpCursor = null;
		CheckPoint checkPoint = new CheckPoint();
		
		try{
			cpCursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cpCursor.moveToFirst()) {
				do {
					
					
					checkPoint.setCheckPointId(cpCursor.getString(cpCursor.getColumnIndex(CheckPoint_Id)));
					checkPoint.setPrefferedTime(cpCursor.getString(cpCursor.getColumnIndex(PrefferedTime)));
					checkPoint.setPriority(cpCursor.getString(cpCursor.getColumnIndex(Priority)));
					checkPoint.setName(cpCursor.getString(cpCursor.getColumnIndex(CheckPointName)));
					checkPoint.setAddress(cpCursor.getString(cpCursor.getColumnIndex(Address)));
					checkPoint.setCity(cpCursor.getString(cpCursor.getColumnIndex(City)));
					checkPoint.setState(cpCursor.getString(cpCursor.getColumnIndex(State)));
					checkPoint.setCountry(cpCursor.getString(cpCursor.getColumnIndex(Country)));
					checkPoint.setZipcode(cpCursor.getString(cpCursor.getColumnIndex(ZipCode)));
					checkPoint.setContactInfo(cpCursor.getString(cpCursor.getColumnIndex(ContactInfo)));
					checkPoint.setAltContact(cpCursor.getString(cpCursor.getColumnIndex(AlternateContact)));
					checkPoint.setLatitude(cpCursor.getString(cpCursor.getColumnIndex(Latitude)));
					checkPoint.setLongitude(cpCursor.getString(cpCursor.getColumnIndex(Longitude)));
					checkPoint.setNotes(cpCursor.getString(cpCursor.getColumnIndex(Notes)));
					checkPoint.setOpenTimings(cpCursor.getString(cpCursor.getColumnIndex(OpenTimings)));
					checkPoint.setCloseTimings(cpCursor.getString(cpCursor.getColumnIndex(CloseTimings)));
				
				} while (cpCursor.moveToNext());
			}
			
			cpCursor.getWindow().clear();
			cpCursor.close();
			// close inserting data from database
			
			// return city list
			return checkPoint;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cpCursor != null)
			{
				cpCursor.getWindow().clear();
				cpCursor.close();
			}
			
			return checkPoint;
		}
	}
	
	public void saveEvents(ArrayList<Event> events, String patrolId){
		SQLiteDatabase db = this.getWritableDatabase();
		for(int i = 0; i<events.size(); i++){
			Event event = events.get(i);
			
			String selectQuery = "SELECT  * FROM "+TABLE_EVENT+" where "+eventId+"="+ event.getId();  
			try{
				ContentValues values = new ContentValues();
				values.put(patrolID, patrolId); 
				values.put(eventId, event.getId()); 
				values.put(eventName, event.getName()); 
				values.put(Latitude, event.getLatitude());
				values.put(Longitude, event.getLongitude());
				values.put(CheckPoint_Id, event.getCheckPoint().getCheckPointId());
				values.put(reportedTime, event.getReportedTime());
				values.put(Incident_Desc, event.getIncidentDesc());
				values.put(isSentViaEmail, event.getIsSentViaEmail());
				values.put(text, event.getText());
				values.put(imageUrl, event.getImageUrl());
				values.put(soundUrl,  event.getSoundUrl());
				
				cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
				if (cursor.moveToFirst()) {
					// updating row
					int a=db.update(TABLE_EVENT, values, eventId + " = ?",
							new String[] { String.valueOf(event.getId()) });
				}else{
					db.insert(TABLE_EVENT, null, values);
					
					if(event.getName().equalsIgnoreCase("scan"))
						markCheckPointAsChecked(event.getCheckPoint().getCheckPointId(), event.getReportedTime(), db);
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		db.close();
	}
	
	public ArrayList<Event> getEvents(String id) {
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_EVENT +" where "+patrolID +"="+id+" ORDER BY "+reportedTime +" DESC";              
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Event> eventList = new ArrayList<Event>();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			if (cursor.moveToFirst()) {
				do {
					Event event = new Event();
					event.setName(cursor.getString(cursor.getColumnIndex(eventName)));
					event.setLatitude(cursor.getString(cursor.getColumnIndex(Latitude)));
					event.setLongitude(cursor.getString(cursor.getColumnIndex(Longitude)));
					
					String checkPointID = cursor.getString(cursor.getColumnIndex(CheckPoint_Id));
					event.setCheckPoint(getCheckPoint(checkPointID, db));
					
					event.setReportedTime(cursor.getString(cursor.getColumnIndex(reportedTime)));
					event.setIncidentDesc(cursor.getString(cursor.getColumnIndex(Incident_Desc)));
					event.setIsSentViaEmail(Boolean.valueOf(cursor.getString(cursor.getColumnIndex(isSentViaEmail))));
					event.setText(cursor.getString(cursor.getColumnIndex(text)));
					event.setImageUrl(cursor.getString(cursor.getColumnIndex(imageUrl)));
					event.setSoundUrl(cursor.getString(cursor.getColumnIndex(soundUrl)));
					
					eventList.add(event);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return eventList;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return eventList;
		}
	}*/
	
	/*
	 public void createDataBase() throws IOException{
		 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	this.getReadableDatabase();
	 
	        	try {
	 
	    			copyDataBase();
	 
	    		} catch (IOException e) {
	    			e.printStackTrace();
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }
	 
	    *//**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     *//*
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		File f = new File(myPath);
				if (f.exists())
					return true;
				else
					return false;
	    		
	 
	    	}catch(SQLiteException e){
	 
	    		e.printStackTrace();
	    		return false;
	 
	    	}
	 
	    	
	 
//	    	return checkDB != null ? true : false;
	    }
	 
	    *//**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * *//*
	    private void copyDataBase() throws IOException{
	 
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 */
	    public void openDataBase() throws SQLException{
	 
	    	//Open the database
	        String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	 
	    }
	 
	    @Override
		public synchronized void close() 
	    {
	 
	    	    if(myDataBase != null)
	    		    myDataBase.close();
	 
	    	    super.close();
	 
		}
	 
}