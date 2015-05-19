package com.pos.util;

import java.math.BigDecimal;

public class InvoiceDetail {

	int InvoiceId,RetrunPosID,RetrunReason_ID,RetrunTotal;
	String InvoiceDate,Transaction_ID;
	BigDecimal TotalAmount,Tax,Discount,GrandTotal;
	
	
	boolean InvoiceStatus = false;
	
	
}
