package com.equiworx.model;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Tutor implements Parcelable{
	private String tutorId, name, email, contactNumber, altContactNumber, address, gender,notes,studentid,fee;

	ArrayList<StudentIdFee> student;
	public String getStudentid() {
		return studentid;
	}
	public void setStudentid(String studentid) {
		this.studentid = studentid;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public ArrayList<StudentIdFee> getStudent() {
		return student;
	}
	public void setStudent(ArrayList<StudentIdFee> student) {
		this.student = student;
	}
	public Tutor(){
		
	}
	 public Tutor(Parcel source) {
		 tutorId = source.readString();
		 name = source.readString();
		 email = source.readString();
		 contactNumber = source.readString();
		 altContactNumber = source.readString();
		 address = source.readString();
		 gender = source.readString();
    }
	 
	 
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getTutorId() {
		return tutorId;
	}

	public void setTutorId(String tutorId) {
		this.tutorId = tutorId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getAltContactNumber() {
		return altContactNumber;
	}

	public void setAltContactNumber(String altContactNumber) {
		this.altContactNumber = altContactNumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public static final Parcelable.Creator<Tutor> CREATOR
	  = new Parcelable.Creator<Tutor>() 
	{
	       public Tutor createFromParcel(Parcel in) 
	       {
	           return new Tutor(in);
	       }

	       public Tutor[] newArray (int size) 
	       {
	           return new Tutor[size];
	       }
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return this.hashCode();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {	 
		 
		dest.writeString(tutorId);
		dest.writeString(name);
		dest.writeString(email);
		dest.writeString(contactNumber);
		dest.writeString(altContactNumber);
		dest.writeString(address);
		dest.writeString(gender);
	}
	
	
	
}
