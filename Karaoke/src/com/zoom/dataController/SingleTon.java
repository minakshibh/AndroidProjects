package com.zoom.dataController;

import java.util.ArrayList;



public class SingleTon {
	
	private static SingleTon instance = null;
	
	
	private String userCredit,userEmailID;	
	private String from;
	private String adapter;
	
	
	protected SingleTon() {
		// Exists only to defeat instantiation.
	}
	public static SingleTon getInstance() {
		if(instance == null) {
			instance = new SingleTon();
		}
		return instance;
	}
	
	public String getUserCredit() {
		return userCredit;
	}
	public void setUserCredit(String userCredit) {
		this.userCredit = userCredit;
	}
	public String getUserEmailID() {
		return userEmailID;
	}
	public void setUserEmailID(String userEmailID) {
		this.userEmailID = userEmailID;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getAdapter() {
		return adapter;
	}
	public void setAdapter(String adapter) {
		this.adapter = adapter;
	}
	
	
	
}
