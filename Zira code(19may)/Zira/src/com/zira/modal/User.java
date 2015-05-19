package com.zira.modal;

import java.util.ArrayList;

public class User {
	
	private String userid, userEmail, result, message;
	private Boolean isdrivermodeactivated;
	private ArrayList<PromoCode> promoCodeList;
	private ArrayList<CreditCard> cardList;
	
	
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
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public Boolean getIsdrivermodeactivated() {
		return isdrivermodeactivated;
	}
	public void setIsdrivermodeactivated(Boolean isdrivermodeactivated) {
		this.isdrivermodeactivated = isdrivermodeactivated;
	}
	public ArrayList<PromoCode> getPromoCodeList() {
		return promoCodeList;
	}
	public void setPromoCodeList(ArrayList<PromoCode> promoCodeList) {
		this.promoCodeList = promoCodeList;
	}
	public ArrayList<CreditCard> getCardList() {
		return cardList;
	}
	public void setCardList(ArrayList<CreditCard> cardList) {
		this.cardList = cardList;
	}
	
}



