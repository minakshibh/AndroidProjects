package com.equiworx.util;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import com.equiworx.model.Connection;
import com.equiworx.model.Lesson;
import com.equiworx.model.LessonDetails;
import com.equiworx.model.MyLesson;
import com.equiworx.model.Parent;
import com.equiworx.model.StudentIdFee;
import com.equiworx.model.StudentList;
import com.equiworx.model.StudentRequest;
import com.equiworx.model.Tutor;
import com.equiworx.model.TutorLesson;

public class TutorHelperParser {

	private Context context;
	
	public TutorHelperParser(Context ctx){
		this.context = ctx;
	}
	
	public Tutor parseTutorDetails(String response){
	
		Tutor tutor = new Tutor();
		String result, message;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
		
		
			if(result.equals("0")){
				tutor.setTutorId(jsonChildNode.getString("tutor_id").toString());
				tutor.setName(jsonChildNode.getString("name").toString());
				tutor.setEmail(jsonChildNode.getString("email").toString());
				tutor.setContactNumber(jsonChildNode.getString("contact_number").toString());
				tutor.setAltContactNumber(jsonChildNode.getString("alt_c_number").toString());
				tutor.setAddress(jsonChildNode.getString("address").toString());
				tutor.setGender(jsonChildNode.getString("gender").toString());
				
			}else{
				tutor = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tutor;
	}
	public Tutor parseTutorEdit(String response){
		
		Tutor tutor = new Tutor();
		String result, message;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
		
		
			if(result.equals("0")){
				tutor.setTutorId(jsonChildNode.getString("tutor_pin").toString());
				tutor.setName(jsonChildNode.getString("name").toString());
				tutor.setEmail(jsonChildNode.getString("email").toString());
				tutor.setContactNumber(jsonChildNode.getString("contact_number").toString());
				tutor.setAltContactNumber(jsonChildNode.getString("alt_c_number").toString());
				tutor.setAddress(jsonChildNode.getString("address").toString());
				tutor.setGender(jsonChildNode.getString("gender").toString());
				
			}else{
				tutor = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return tutor;
	}
	public Parent parseParentDetails(String response){
		
		Parent parent = new Parent();
		String result, message;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				parent.setParentId(jsonChildNode.getString("parent_id").toString());
				parent.setName(jsonChildNode.getString("name").toString());
				parent.setEmail(jsonChildNode.getString("email").toString());
				parent.setContactNumber(jsonChildNode.getString("contact_number").toString());
				parent.setAltContactNumber(jsonChildNode.getString("alt_c_number").toString());
				parent.setAddress(jsonChildNode.getString("address").toString());
				parent.setCredits(jsonChildNode.getString("credits").toString());
				parent.setGender(jsonChildNode.getString("gender").toString());
				parent.setPin(jsonChildNode.getString("pin").toString());
			}else{
				parent = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parent;
	}
	
public Parent parseParentEdit(String response){
		
		Parent parent = new Parent();
		String result, message;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				parent.setParentId(jsonChildNode.getString("parent_pin").toString());
				parent.setName(jsonChildNode.getString("name").toString());
				parent.setEmail(jsonChildNode.getString("email").toString());
				parent.setContactNumber(jsonChildNode.getString("contact_number").toString());
				parent.setAltContactNumber(jsonChildNode.getString("alt_c_number").toString());
				parent.setAddress(jsonChildNode.getString("address").toString());
				parent.setCredits(jsonChildNode.getString("credits").toString());
				parent.setGender(jsonChildNode.getString("gender").toString());
				//parent.setPin(jsonChildNode.getString("pin").toString());
			}else{
				parent = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parent;
	}
	
	public String parseResponse(String response){
		
		
		String result, message = "";
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				return message;
			}else{
				showAlert(message);
				message = "failure";
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return message;
	}
	public Parent getParentInfo(String response){
		
		Parent parent = new Parent();
		String result, message;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			
			if(result.equals("0")){
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("parent_info"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					parent.setParentId(jsonObj.getString("parent_id").toString());
					parent.setName(jsonObj.getString("name").toString());
					parent.setEmail(jsonObj.getString("email").toString());
					parent.setContactNumber(jsonObj.getString("contact_number").toString());
					parent.setAltContactNumber(jsonObj.getString("alt_c_number").toString());
					parent.setAddress(jsonObj.getString("address").toString());
					
		        }
			}else{
				parent = null;
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parent;
	}
	
	public ArrayList<Parent> getParents(String response){
		String result, message, lastUpdated;
		ArrayList<Parent> parentList = new ArrayList<Parent>();
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			if(result.equals("0")){
				lastUpdated = jsonChildNode.getString("greatest_last_updated").toString();
				/*
				Editor editor = appPrefs.edit();
				editor.putString("incidentTimeStamp", lastUpdated);
				editor.commit();
				*/
				
				Parent temp = new Parent();
	             
				temp.setParentId("0");
				temp.setName("Select existing parent");
				
	            parentList.add(temp);
	            
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("parent_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
		            Parent parent = new Parent();
		            JSONObject jsonObj = jsonArray.getJSONObject(i);
		             
		            parent.setParentId(jsonObj.optString("parent_id").toString());
		            parent.setName(jsonObj.optString("name").toString());
		            parent.setEmail(jsonObj.optString("email").toString());
		            parent.setContactNumber(jsonObj.optString("contact_number").toString());
		            parent.setAltContactNumber(jsonObj.optString("alt_c_number").toString());
		            parent.setAddress(jsonObj.optString("address").toString());
		            
		            parentList.add(parent);
		       }
			}else{
				parentList = new ArrayList<Parent>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parentList;
	}
	
	public void showAlert(String message){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Tutor Helper");
		alert.setMessage(message);
		alert.setPositiveButton("Okay", null);
		alert.show();
	}
	public ArrayList<Connection> getConnectionInfo(String response){
		
		ArrayList<Connection> connectionlist = new ArrayList<Connection>();
		String result, message;
	
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			
			if(result.equals("0")){
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("request_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					Connection connection=new Connection();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					connection.setTutorId(jsonObj.getString("tutor_id").toString());
					connection.setParentId(jsonObj.getString("parent_id").toString());
					connection.setRequestId(jsonObj.getString("request_id").toString());
					connection.setParentName(jsonObj.getString("tutor_name").toString());
					connection.setTutorName(jsonObj.getString("parent_name").toString());
					
					connectionlist.add(connection);
		        }
			}else{
				connectionlist = new ArrayList<Connection>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return connectionlist;
	}
	
	public ArrayList<StudentRequest> getStudentRequestInfo(String response){
		
		ArrayList<StudentRequest> arraylist = new ArrayList<StudentRequest>();
		String result, message;
	
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			message = jsonChildNode.getString("message").toString();
			
			
			if(result.equals("0")){
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("request_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					StudentRequest studentRequest=new StudentRequest();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					studentRequest.setRequestID(jsonObj.getString("ID").toString());
					studentRequest.setStudentId(jsonObj.getString("student_id").toString());
					studentRequest.setName(jsonObj.getString("student_name").toString());
					studentRequest.setEmail(jsonObj.getString("student_email").toString());
					studentRequest.setAddress(jsonObj.getString("student_address").toString());
					studentRequest.setContactInfo(jsonObj.getString("student_contact").toString());
					studentRequest.setGender(jsonObj.getString("student_gender").toString());
					studentRequest.setTutorId(jsonObj.getString("tutor_id").toString());
					studentRequest.setFees(jsonObj.getString("fee").toString());
					studentRequest.setTutorName(jsonObj.getString("tutor_name").toString());
					studentRequest.setTutorEmail(jsonObj.getString("tutor_email").toString());
					studentRequest.setTutorContact(jsonObj.getString("tutor_contact_number").toString());
					studentRequest.setParentName(jsonObj.getString("parent_name").toString());
					studentRequest.setParentEmail(jsonObj.getString("parent_email").toString());
					studentRequest.setParentContact(jsonObj.getString("parent_contact_info").toString());
					
					arraylist.add(studentRequest);
		        }
			}else{
				arraylist = new ArrayList<StudentRequest>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arraylist;
	}
	
	public ArrayList<Parent> getParentlist(String response){
		
		ArrayList<Parent> arraylist_parentList = new ArrayList<Parent>();
		String result, message,greatest_last_updated;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
			message = jsonChildNode.getString("message").toString();
			
			
			if(result.equals("0")){
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("parent_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					
				
					Parent parent=new Parent();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					parent.setParentId(jsonObj.getString("parent_id").toString());
					parent.setName(jsonObj.getString("name").toString());
					parent.setEmail(jsonObj.getString("email").toString());
					parent.setContactNumber(jsonObj.getString("contact_number").toString());
					parent.setAltContactNumber(jsonObj.getString("alt_c_number").toString());
					parent.setAddress(jsonObj.getString("address").toString());
					parent.setStudentCount(jsonObj.getString("no._of_students").toString());
					parent.setLessonCount(jsonObj.getString("no._of_lessons").toString());
					parent.setNotes(jsonObj.getString("notes").toString());
					parent.setOutstandingBalance(jsonObj.getString("outstanding_balance").toString());
					
					arraylist_parentList.add(parent);
		        }
			}else{
				arraylist_parentList = new ArrayList<Parent>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arraylist_parentList;
	}
public ArrayList<StudentList> getStudentlist(String response){
		
		ArrayList<StudentList> arraylist_studentList = new ArrayList<StudentList>();
		String result, message,greatest_last_updated;
		
		try {

			JSONObject jsonChildNode = new JSONObject(response);	
			
			result = jsonChildNode.getString("result").toString();
			greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
			message = jsonChildNode.getString("message").toString();
			
			
			if(result.equals("0")){
				JSONArray jsonArray = new JSONArray(jsonChildNode.getString("student_list"));
				
				for(int i=0; i < jsonArray.length(); i++)
		        {
					StudentList studentList=new StudentList();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					studentList.setStudentId(jsonObj.getString("student_id").toString());
					String name = jsonObj.getString("name").toString();
					//System.err.println("name="+name);
					studentList.setName(name);
					studentList.setEmail(jsonObj.getString("email").toString());
					studentList.setAddress(jsonObj.getString("address").toString());
					studentList.setContactInfo(jsonObj.getString("contact_info").toString());
					studentList.setGender(jsonObj.getString("gender").toString());
					studentList.setFees(jsonObj.getString("fee").toString());
					studentList.setParentId(jsonObj.getString("parent_id").toString());
					studentList.setIsActiveInMonth(jsonObj.getString("isActiveInMonth").toString());
					studentList.setNotes(jsonObj.getString("notes").toString());
					
					arraylist_studentList.add(studentList);
					
		        }
			}else{
				arraylist_studentList = new ArrayList<StudentList>();
				showAlert(message);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return arraylist_studentList;
	}
public ArrayList<StudentList> getStudentlistwithoutNote(String response){
	
	ArrayList<StudentList> arraylist_studentList = new ArrayList<StudentList>();
	String result, message,greatest_last_updated;
	
	try {

		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
		message = jsonChildNode.getString("message").toString();
		
		
		if(result.equals("0")){
			JSONArray jsonArray = new JSONArray(jsonChildNode.getString("student_list"));
			
			for(int i=0; i < jsonArray.length(); i++)
	        {
				StudentList studentList=new StudentList();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				studentList.setStudentId(jsonObj.getString("student_id").toString());
				String name = jsonObj.getString("name").toString();
				//System.err.println("name="+name);
				studentList.setName(name);
				studentList.setEmail(jsonObj.getString("email").toString());
				studentList.setAddress(jsonObj.getString("address").toString());
				studentList.setContactInfo(jsonObj.getString("contact_info").toString());
				studentList.setGender(jsonObj.getString("gender").toString());
				studentList.setFees(jsonObj.getString("fee").toString());
				studentList.setParentId(jsonObj.getString("parent_id").toString());
				studentList.setIsActiveInMonth(jsonObj.getString("isActiveInMonth").toString());
			
				arraylist_studentList.add(studentList);
				
	        }
		}else{
			arraylist_studentList = new ArrayList<StudentList>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_studentList;
}
public ArrayList<MyLesson> getMyLesson(String response){
	
	ArrayList<MyLesson> arraylist_mylesson = new ArrayList<MyLesson>();
	String result, message,greatest_last_updated;
	
	try {

		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		//greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
		message = jsonChildNode.getString("message").toString();

		
		if(result.equals("0")){
			//JSONObject lesson_data = new JSONObject(message);
		
			String str_lesson_data=jsonChildNode.getString("lesson_data");
			JSONArray jsonArray1 = new JSONArray(str_lesson_data);

			for(int j=0; j < jsonArray1.length(); j++)
			{
				MyLesson myLesson=new MyLesson();
				JSONObject jsonObj1 = jsonArray1.getJSONObject(j);
				JSONArray jsonArray = new JSONArray(jsonObj1.getString("student_info"));
				ArrayList<StudentList> arraylist = new ArrayList<StudentList>();
				
				for(int i=0; i < jsonArray.length(); i++)
				{
					StudentList studentList=new StudentList();
					JSONObject jsonObj = jsonArray.getJSONObject(i);
					
					studentList.setStudentId(jsonObj.getString("student_id").toString());
					String name=	jsonObj.getString("student_name").toString();
					System.err.println("name="+name);
					studentList.setName(name);
					studentList.setEmail(jsonObj.getString("student_email").toString());
					studentList.setAddress(jsonObj.getString("student_address").toString());
					studentList.setContactInfo(jsonObj.getString("student_contact_info").toString());
					arraylist.add(studentList);
					
				}
				myLesson.setArray_studentlist(arraylist);
				
				
				myLesson.setStudentno(""+arraylist.size());
			//	arraylist.clear();
				//myLesson.setTutorId(jsonObj1.getString("parent_id"));
				//myLesson.setParentName(jsonObj1.getString("parent_name"));
				//myLesson.setParentEmail(jsonObj1.getString("parent_email"));
				
			
				myLesson.setLessonId(jsonObj1.getString("lesson_id"));
				//myLesson.setGreatest_last_updated(jsonObj1.getString("greatest_last_updated"));
				myLesson.setLesson_tutor_id(jsonObj1.getString("lesson_tutor_id"));
				myLesson.setTutor_name(jsonObj1.getString("tutor_name"));
				myLesson.setLessonTopic(jsonObj1.getString("lesson_topic"));
				myLesson.setLessonDescription(jsonObj1.getString("lesson_description"));
				myLesson.setStartTime(jsonObj1.getString("lesson_start_time"));
				myLesson.setEndTime(jsonObj1.getString("lesson_end_time"));
				myLesson.setDuration(jsonObj1.getString("lesson_duration"));
				myLesson.setDays(jsonObj1.getString("lesson_days"));
				
				myLesson.setLessonDate(jsonObj1.getString("lesson_date"));
				//myLesson.setLessonenddate(jsonObj1.getString("end_date"));
				myLesson.setIsRecurring(jsonObj1.getString("lesson_is_recurring"));
				myLesson.setIsActive(jsonObj1.getString("lesson_is_active"));
			
			arraylist_mylesson.add(myLesson);
	        }
			
		}else{
			//arraylist_mylesson = new ArrayList<MyLesson>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_mylesson;
}

public ArrayList<Lesson> getLesson(String response){
	
	ArrayList<Lesson> arraylist_lesson = new ArrayList<Lesson>();
	String result, message,greatest_last_updated;
	
	try {

		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		//greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
		message = jsonChildNode.getString("message").toString();
		
		
		if(result.equals("0")){
			JSONArray jsonArray = new JSONArray(jsonChildNode.getString("lesson_request"));
			
			for(int i=0; i < jsonArray.length(); i++)
	        {
				Lesson lesson=new Lesson();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				lesson.setRequestId(jsonObj.getString("request_id").toString());
				String name=	jsonObj.getString("lesson_id").toString();
				System.err.println("name="+name);
				lesson.setLessonId(name);
				lesson.setTutorId(jsonObj.getString("tutor_id").toString());
				lesson.setParentId(jsonObj.getString("parent_id").toString());
				lesson.setStudentId(jsonObj.getString("student_id").toString());
				lesson.setLessonTopic(jsonObj.getString("lesson_topic").toString());
				lesson.setLessonDescription(jsonObj.getString("lesson_description").toString());
				lesson.setLessonDuration(jsonObj.getString("lesson_duration").toString());
				lesson.setLessonStartTime(jsonObj.getString("lesson_start_time").toString());
				lesson.setLessonEndTime(jsonObj.getString("lesson_end_time").toString());
				lesson.setLessonDate(jsonObj.getString("lesson_date").toString());
				lesson.setCreatedDate(jsonObj.getString("lesson_created_date").toString());
				lesson.setIsRecurring(jsonObj.getString("lesson_is_recurring").toString());
				lesson.setLessonDays(jsonObj.getString("lesson_days").toString());
				lesson.setRequestStatus(jsonObj.getString("request_status").toString());
				lesson.setFees(jsonObj.getString("lesson_fee").toString());
				
				lesson.setParentname(jsonObj.getString("parent_name").toString());
				lesson.setTutername(jsonObj.getString("tutor_name").toString());
				lesson.setStudentname(jsonObj.getString("student_name").toString());
			
				arraylist_lesson.add(lesson);
				
	        }
		}else{
			arraylist_lesson = new ArrayList<Lesson>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_lesson;
}



public ArrayList<LessonDetails> getTutordetails(String response){
	
	ArrayList<LessonDetails> arraylist_lessondetails = new ArrayList<LessonDetails>();
	String result, message;
	
	try {
		TutorLesson tutorLesson=new TutorLesson();
		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		tutorLesson.setResult(result);
		message = jsonChildNode.getString("message").toString();
		tutorLesson.setMessage(message);
		tutorLesson.setActive_students(jsonChildNode.getString("no of active students").toString());
		tutorLesson.setFee_due(jsonChildNode.getString("fee_due").toString());
		tutorLesson.setFee_collected(jsonChildNode.getString("fee_collected").toString());
		
		if(result.equals("0")){
			JSONArray jsonArray = new JSONArray(jsonChildNode.getString("lesson_list"));
			
			for(int i=0; i < jsonArray.length(); i++)
	        {
				LessonDetails lesson=new LessonDetails();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				lesson.setBlock_out_time_for_fullday(jsonObj.getString("block_out_time_for_fullday").toString());
				lesson.setBlock_out_time_for_halfday(jsonObj.getString("block_out_time_for_halfday").toString());
				lesson.setNo_of_lessons(jsonObj.getString("no._of_lessons").toString());
				lesson.setLesson_date(jsonObj.getString("lesson_date").toString());
			
				
			
				arraylist_lessondetails.add(lesson);
				
	        }
		}else{
			arraylist_lessondetails = new ArrayList<LessonDetails>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_lessondetails;
}

public ArrayList<Tutor> getTuterList(String response){
	
	ArrayList<Tutor> arraylist_tuterlist = new ArrayList<Tutor>();
	String result, message,greatest_last_updated;
	
	try {

		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		//greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
		message = jsonChildNode.getString("message").toString();
		
		
		if(result.equals("0")){
			JSONArray jsonArray = new JSONArray(jsonChildNode.getString("tutor_list"));
			
			for(int i=0; i < jsonArray.length(); i++)
	        {
				Tutor tutor=new Tutor();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				tutor.setTutorId(jsonObj.getString("tutor_id").toString());
				tutor.setName(jsonObj.getString("name").toString());
				
				tutor.setEmail(jsonObj.getString("email").toString());
				tutor.setContactNumber(jsonObj.getString("contact_number").toString());
				tutor.setAltContactNumber(jsonObj.getString("alt_c_number").toString());
				tutor.setAddress(jsonObj.getString("address").toString());
				String note=jsonObj.getString("notes").toString();
				tutor.setNotes(note);
				arraylist_tuterlist.add(tutor);
				
				JSONObject jsonChildNode1 =jsonArray.getJSONObject(i);	
				
				
				JSONArray jsonArray1 = new JSONArray(jsonChildNode1.getString("fee_list"));
				

				ArrayList<StudentIdFee> array_student=new ArrayList<StudentIdFee>();
				for(int j=0; j<jsonArray1.length(); j++)
		        {
					JSONObject jsonObj1 = jsonArray1.getJSONObject(j);
				
					StudentIdFee studentIdFee=new StudentIdFee();
					String id=jsonObj1.getString("student_id").toString();
					Log.e("id", id);
					studentIdFee.setStudentid(id);
					studentIdFee.setFee(jsonObj1.getString("fee").toString());
					
					array_student.add(studentIdFee);
		        }
				tutor.setStudent(array_student);
	        }
		}else{
			arraylist_tuterlist = new ArrayList<Tutor>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_tuterlist;
}





/*public ArrayList<LessonStudentParentDetails> getLessonDetails(String response){
	
	ArrayList<LessonStudentParentDetails> arraylist_tuterlist = new ArrayList<LessonStudentParentDetails>();
	String result, message,greatest_last_updated;
	
	try {

		JSONObject jsonChildNode = new JSONObject(response);	
		
		result = jsonChildNode.getString("result").toString();
		//greatest_last_updated = jsonChildNode.getString("greatest_last_updated").toString();
		message = jsonChildNode.getString("message").toString();
		
		
		if(result.equals("0")){
			JSONArray jsonArray = new JSONArray(jsonChildNode.getString("lesson_data"));
			
			for(int i=0; i<jsonArray.length(); i++)
	        {
				LessonStudentParentDetails tutor=new LessonStudentParentDetails();
				JSONObject jsonObj = jsonArray.getJSONObject(i);
				
				tutor.setLesson_ID(jsonObj.getString("lesson_ID").toString());
				tutor.setLesson_tutor_id(jsonObj.getString("lesson_tutor_id").toString());
				
				tutor.setLesson_topic(jsonObj.getString("lesson_topic").toString());
				tutor.setLesson_description(jsonObj.getString("lesson_description").toString());
				tutor.setLesson_start_time(jsonObj.getString("lesson_start_time").toString());
				tutor.setLesson_end_time(jsonObj.getString("lesson_end_time").toString());
				String duration=jsonObj.getString("lesson_duration").toString();
				tutor.setLesson_duration(duration);
				tutor.setLesson_days(jsonObj.getString("lesson_days").toString());
				tutor.setLesson_date(jsonObj.getString("lesson_date").toString());
				tutor.setEnd_date(jsonObj.getString("end_date").toString());
				tutor.setLesson_is_recurring(jsonObj.getString("lesson_is_recurring").toString());
				String note=jsonObj.getString("lesson_is_active").toString();
	
				arraylist_tuterlist.add(tutor);
				
				JSONObject jsonChildNode1 =jsonArray.getJSONObject(i);	
				
				
				JSONArray jsonArray1 = new JSONArray(jsonChildNode1.getString("student_info"));
				

				ArrayList<StudentParentDetails> array_student=new ArrayList<StudentParentDetails>();
				for(int j=0; j<jsonArray1.length(); j++)
		        {
					JSONObject jsonObj1 = jsonArray1.getJSONObject(j);
				
					StudentParentDetails studentIdFee=new StudentParentDetails();
					String id=jsonObj1.getString("student_id").toString();
					Log.e("id", id);
					studentIdFee.setStudent_id(id);
					studentIdFee.setStudent_name(jsonObj1.getString("student_name").toString());
					studentIdFee.setStudent_email(jsonObj1.getString("student_email").toString());
					studentIdFee.setParent_id(jsonObj1.getString("parent_id").toString());
					studentIdFee.setParent_name(jsonObj1.getString("parent_name").toString());
					studentIdFee.setParent_email(jsonObj1.getString("parent_email").toString());
					studentIdFee.setStudent_address(jsonObj1.getString("student_address").toString());
					studentIdFee.setStudent_contact_info(jsonObj1.getString("student_contact_info").toString());
					
					
					array_student.add(studentIdFee);
		        }
				tutor.setArray_StudentParent(array_student);
	        }
		}else{
			arraylist_tuterlist = new ArrayList<LessonStudentParentDetails>();
			showAlert(message);
		}
		
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return arraylist_tuterlist;
}
*/

}
