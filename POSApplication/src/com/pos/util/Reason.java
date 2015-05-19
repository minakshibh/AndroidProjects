package com.pos.util;

import android.os.Parcel;
import android.os.Parcelable;

public class Reason implements Parcelable{

	private String id, userCode, description, detailText;

	public Reason(){
		
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserCode() {
		return userCode;
	}

	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDetailText() {
		return detailText;
	}

	public void setDetailText(String detailText) {
		this.detailText = detailText;
	}

	 public static final Parcelable.Creator<Reason> CREATOR = new Creator<Reason>() {  
		 public Reason createFromParcel(Parcel source) {  
			 Reason mReason = new Reason();  
			 mReason.id = source.readString();  
			 mReason.userCode = source.readString();  
			 mReason.description = source.readString(); 
			 mReason.detailText = source.readString(); 
		     return mReason;  
		 }  
		 public Reason[] newArray(int size) {  
		     return new Reason[size];  
		 }  
		    };  
		       
		    public int describeContents() {  
		 return 0;  
		    }  
		    public void writeToParcel(Parcel parcel, int flags) {  
		 parcel.writeString(id);  
		 parcel.writeString(userCode);  
		 parcel.writeString(description);  
		 parcel.writeString(detailText);  
		    }  
}
