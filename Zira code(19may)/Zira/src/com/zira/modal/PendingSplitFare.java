package com.zira.modal;

import org.json.JSONObject;

public class PendingSplitFare {
	private String jsonResultsplit="";
	private String jsonMessage="";
	private JSONObject obj;
	private String pendingsplitfare="";
	private String amount="";
	private String tripid="";
	public String getJsonResultsplit() {
		return jsonResultsplit;
	}
	public void setJsonResultsplit(String jsonResultsplit) {
		this.jsonResultsplit = jsonResultsplit;
	}
	public String getJsonMessage() {
		return jsonMessage;
	}
	public void setJsonMessage(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}
	public JSONObject getObj() {
		return obj;
	}
	public void setObj(JSONObject obj) {
		this.obj = obj;
	}
	public String getPendingsplitfare() {
		return pendingsplitfare;
	}
	public void setPendingsplitfare(String pendingsplitfare) {
		this.pendingsplitfare = pendingsplitfare;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTripid() {
		return tripid;
	}
	public void setTripid(String tripid) {
		this.tripid = tripid;
	}
	
}
