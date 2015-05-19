package com.pos.util;

import java.math.BigDecimal;

public class Currency {
	public int imageId;
	public BigDecimal quantity;
	public double value;
	public CurrencyType type;
	public int currencyTypeId;
	public int currencyId;
	public String currencyName;
	
	public String currSaleValue,currReturnValue;
	public Currency(double value, int imageId, CurrencyType type){
		this.value = value;
		this.imageId = imageId;
		this.type = type;
		quantity = BigDecimal.valueOf(0);
	}
	public Currency(double value, int imageId, CurrencyType type,int id,int currType,String currName){
		this.value = value;
		this.imageId = imageId;
		this.type = type;
		this.currencyId =id;
		this.currencyTypeId =currType;
		this.currencyName =currName;
		quantity = BigDecimal.valueOf(0);
		
		
	}
	
}