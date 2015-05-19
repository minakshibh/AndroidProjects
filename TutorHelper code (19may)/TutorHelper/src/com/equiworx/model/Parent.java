package com.equiworx.model;

public class Parent {

	private String parentId, name, email, contactNumber, altContactNumber, address, credits, gender, pin, studentCount, lessonCount, outstandingBalance, notes;

	public Parent(String parentId, String name,String address,String gender)
	{
		 super();
		    this.parentId = parentId;
		    this.name = name;
		    this.address = address;
		    this.gender = gender;
	}
	public Parent()
	{
		
	}
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
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

	public String getCredits() {
		return credits;
	}

	public void setCredits(String credits) {
		this.credits = credits;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}
	
	
	public String getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(String studentCount) {
		this.studentCount = studentCount;
	}
public String getLessonCount() {
		return lessonCount;
	}

	public void setLessonCount(String lessonCount) {
		this.lessonCount = lessonCount;
	}

	public String getOutstandingBalance() {
		return outstandingBalance;
	}
	public String string() {
		return outstandingBalance;
	}

	public void setOutstandingBalance(String outstandingBalance) {
		this.outstandingBalance = outstandingBalance;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return name;
	}

}
