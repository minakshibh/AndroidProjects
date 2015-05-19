package com.equiworx.model;

public class StudentList {
String
	studentId,
	name, 
	email, 
	address, 
	contactInfo, 
	gender, 
	isActiveInMonth,
	fees,
	notes,
	parentId;

public String getNotes() {
	return notes;
}

public void setNotes(String notes) {
	this.notes = notes;
}

public String getStudentId() {
	return studentId;
}

public void setStudentId(String studentId) {
	this.studentId = studentId;
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

public String getAddress() {
	return address;
}

public void setAddress(String address) {
	this.address = address;
}

public String getContactInfo() {
	return contactInfo;
}

public void setContactInfo(String contactInfo) {
	this.contactInfo = contactInfo;
}

public String getGender() {
	return gender;
}

public void setGender(String gender) {
	this.gender = gender;
}

public String getIsActiveInMonth() {
	return isActiveInMonth;
}

public void setIsActiveInMonth(String isActiveInMonth) {
	this.isActiveInMonth = isActiveInMonth;
}

public String getFees() {
	return fees;
}

public void setFees(String fees) {
	this.fees = fees;
}

public String getParentId() {
	return parentId;
}

public void setParentId(String parentId) {
	this.parentId = parentId;
}


}
