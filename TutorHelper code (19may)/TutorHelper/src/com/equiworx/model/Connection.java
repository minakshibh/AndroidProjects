package com.equiworx.model;

import java.util.ArrayList;

public class Connection {
String result,
message,
tutorId,
parentId,
requestId,
tutorName,
parentName;
ArrayList<String> request_list;
public String getResult() {
	return result;
}
public void setResult(String result) {
	this.result = result;
}
public String getMessage() {
	return message;
}
public void setMessage(String message) {
	this.message = message;
}
public String getTutorId() {
	return tutorId;
}
public void setTutorId(String tutorId) {
	this.tutorId = tutorId;
}
public String getParentId() {
	return parentId;
}
public void setParentId(String parentId) {
	this.parentId = parentId;
}
public String getRequestId() {
	return requestId;
}
public void setRequestId(String requestId) {
	this.requestId = requestId;
}
public String getTutorName() {
	return tutorName;
}
public void setTutorName(String tutorName) {
	this.tutorName = tutorName;
}
public String getParentName() {
	return parentName;
}
public void setParentName(String parentName) {
	this.parentName = parentName;
}
public ArrayList<String> getRequest_list() {
	return request_list;
}
public void setRequest_list(ArrayList<String> request_list) {
	this.request_list = request_list;
}



}
