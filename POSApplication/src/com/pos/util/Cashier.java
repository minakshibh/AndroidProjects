package com.pos.util;

public class Cashier {

	int id,rollId;
	public int getRollId() {
		return rollId;
	}
	public void setRollId(int rollId) {
		this.rollId = rollId;
	}
	String username,password;
	boolean isDeleted=false;
	public int getId()
	{
		return id;
	}
	public void setId(int id) 
	{
		this.id = id;
	}
	public String getUsername()
	{
		return username;
	}
	public boolean isDeleted() {
		return isDeleted;
	}
	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public void setUsername(String username) 
	{
		this.username = username;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password) 
	{
		this.password = password;
	}
}
