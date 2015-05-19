package com.pos.util;

import java.io.Serializable;

public class Transaction implements Serializable{

	int Transaction_Local_Id,currencyId;
	public int getCurrencyId() {
		return currencyId;
	}
	public void setCurrencyId(int currencyId) {
		this.currencyId = currencyId;
	}
	String date , time , employee_Id, store_Id , pos_Id,transaction_Id,subTotal,total,discount,tax;
	String paymentMethod,voucherId,voucherName,queryType, payableAmt;
	String mDiscountType,mDiscountAmount;
	
	public String getPayableAmt() {
		return payableAmt;
	}
	public void setPayableAmt(String payableAmt) {
		this.payableAmt = payableAmt;
	}
	public String getmDiscountType() {
		return mDiscountType;
	}
	public void setmDiscountType(String mDiscountType) {
		this.mDiscountType = mDiscountType;
	}
	public String getmDiscountAmount() {
		return mDiscountAmount;
	}
	public void setmDiscountAmount(String mDiscountAmount) {
		this.mDiscountAmount = mDiscountAmount;
	}
	public String getVoucherId() {
		return voucherId;
	}
	public void setVoucherId(String voucherId) {
		this.voucherId = voucherId;
	}
	public String getVoucherName() {
		return voucherName;
	}
	public void setVoucherName(String voucherName) {
		this.voucherName = voucherName;
	}
	public String getQueryType() {
		return queryType;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public int getTransaction_Local_Id() {
		return Transaction_Local_Id;
	}
	public void setTransaction_Local_Id(int transaction_Local_Id) {
		Transaction_Local_Id = transaction_Local_Id;
	}
	
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getEmployee_Id() {
		return employee_Id;
	}
	public void setEmployee_Id(String employee_Id) {
		this.employee_Id = employee_Id;
	}
	
	public String getStore_Id() {
		return store_Id;
	}
	public void setStore_Id(String store_Id) {
		this.store_Id = store_Id;
	}
	public String getPos_Id() {
		return pos_Id;
	}
	public void setPos_Id(String pos_Id) {
		this.pos_Id = pos_Id;
	}
	public String getTransaction_Id() {
		return transaction_Id;
	}
	public void setTransaction_Id(String transaction_Id) {
		this.transaction_Id = transaction_Id;
	}
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getTax() {
		return tax;
	}
	public void setTax(String tax) {
		this.tax = tax;
	}
	
}
