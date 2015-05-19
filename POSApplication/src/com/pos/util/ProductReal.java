package com.pos.util;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductReal implements Serializable{

	int pid,timestamp,totalQuantity,selectedQuatity;
	int Quantity = 0,retunResonId,InvoiceDetailID,RecordId,returnQunatity,MDType=PosConstants.MDnoapplied, initialReturnQuantity;
	
	BigDecimal Mdiscount=new BigDecimal(0);
	BigDecimal discountedValue;

	String productName,barCode,itemId;
	
	BigDecimal convertedPrice;
	BigDecimal price,discount,tax,singleItemDiscount,singleItemTax, initialDiscount, initialTax, initialMDiscount, initialTotal;
	
	String transactionId="";
	BigDecimal totalPrice;

	boolean isDeleted;
	public BigDecimal getDiscountedValue() {
		return discountedValue;
	}
	public void setDiscountedValue(BigDecimal discountedValue) {
		this.discountedValue = discountedValue;
	}
	
	public BigDecimal getConvertedPrice() {
		return convertedPrice;
	}
	public void setConvertedPrice(BigDecimal convertedPrice) {
		this.convertedPrice = convertedPrice;
	}
	public BigDecimal getInitialDiscount() {
		return initialDiscount;
	}
	public void setInitialDiscount(BigDecimal initialDiscount) {
		this.initialDiscount = initialDiscount;
	}
	public BigDecimal getInitialTax() {
		return initialTax;
	}
	public void setInitialTax(BigDecimal initialTax) {
		this.initialTax = initialTax;
	}
	public BigDecimal getInitialMDiscount() {
		return initialMDiscount;
	}
	public void setInitialMDiscount(BigDecimal initialMDiscount) {
		this.initialMDiscount = initialMDiscount;
	}
	public BigDecimal getInitialTotal() {
		return initialTotal;
	}
	public void setInitialTotal(BigDecimal initialTotal) {
		this.initialTotal = initialTotal;
	}
	public int getInitialReturnQuantity() {
		return initialReturnQuantity;
	}
	public void setInitialReturnQuantity(int initialReturnQuantity) {
		this.initialReturnQuantity = initialReturnQuantity;
	}

	public BigDecimal getMdiscount() {
		return Mdiscount;
	}
	public void setMdiscount(BigDecimal mdiscount) {
		Mdiscount = mdiscount;
	}
	public int getMDType() {
		return MDType;
	}
	public void setMDType(int mDType) {
		MDType = mDType;
	}
	public int getReturnQunatity() {
		return returnQunatity;
	}
	public void setReturnQunatity(int returnQunatity) {
		this.returnQunatity = returnQunatity;
	}
	public int getRecordId() {
		return RecordId;
	}
	public void setRecordId(int recordId) {
		RecordId = recordId;
	}
	public int getInvoiceDetailID() {
		return InvoiceDetailID;
	}
	public void setInvoiceDetailID(int invoiceDetailID) {
		InvoiceDetailID = invoiceDetailID;
	}
	public int getRetunResonId() {
		return retunResonId;
	}
	public void setRetunResonId(int retunResonId) {
		this.retunResonId = retunResonId;
	}

	
	
public ProductReal(){		
		
	}
public ProductReal(int pid, int timestamp, int qunatity, String productName,
		String barCode, String itemId, BigDecimal price, BigDecimal discount,
		BigDecimal tax) {
	super();
	this.pid = pid;
	this.timestamp = timestamp;
	this.selectedQuatity = qunatity;
	this.productName = productName;
	this.barCode = barCode;
	this.itemId = itemId;
	this.price = price;
	this.discount = discount;
	this.tax = tax;
}

public  ProductReal(ProductReal product) {
	super();
	this.pid = product.getPid();
	this.timestamp = product.getTimestamp();
	this.selectedQuatity = product.getSelectedQuatity();
	this.productName = product.getProductName();
	this.barCode = product.getBarCode();
	this.itemId = product.getItemId();
	this.price = product.getPrice();
	this.discount = product.getDiscount();
	this.tax = product.getTax();
	this.Quantity = product.getInitialQuantity();
	this.MDType = product.getMDType();
	this.Mdiscount = product.getMdiscount();
	this.discountedValue = product.getDiscountedValue();
	this.singleItemDiscount = product.getSingleItemDiscount();
	this.singleItemTax = product.getSingleItemTax();
	this.totalPrice = product.getTotalPrice();
	this.InvoiceDetailID = product.getInvoiceDetailID();
	this.initialDiscount = product.getInitialDiscount();
	this.initialMDiscount = product.getInitialMDiscount();
	this.initialTax = product.getInitialTax();
	this.initialTotal = product.getInitialTotal();
}
	public int getInitialQuantity() {
	return Quantity;
}
public void setInitialQuantity(int initialQuantity) {
	this.Quantity = initialQuantity;
}
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	
	public boolean isDeleted() {
		return isDeleted;
	}

	public int getSelectedQuatity() {
		return selectedQuatity;
	}

	public void setSelectedQuatity(int selectedQuatity) {
		this.selectedQuatity = selectedQuatity;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public BigDecimal getSingleItemDiscount() {
		return singleItemDiscount;
	}
	public void setSingleItemDiscount(BigDecimal singleItemDiscount) {
		this.singleItemDiscount = singleItemDiscount;
	}
	public BigDecimal getSingleItemTax() {
		return singleItemTax;
	}
	public void setSingleItemTax(BigDecimal singleItemTax) {
		this.singleItemTax = singleItemTax;
	}

	
	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	
	
	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getBarCode() {
		return barCode;
	}

	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getTax() {
		return tax;
	}

	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}
	
}
