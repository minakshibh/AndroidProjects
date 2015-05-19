package com.pos.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class TransProductHandler  extends DefaultHandler
{
	private ArrayList<ProductReal> returnProductList;
    private StringBuffer tempVal;
    private ProductReal tempProduct;
    Map<String,String> map;
    ProductReal product;
    InvoiceDetail invoiceDetail;
    boolean isPayWithDiscount = false;
    
    int flag = 0;
    
    public TransProductHandler() {
    	returnProductList = new ArrayList<ProductReal>();
        map=new HashMap<String, String>();
    }
    public ArrayList<ProductReal> getreturnProductList() {
        return returnProductList;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    public void startElement(String uri, String localName, String qName,
            Attributes attributes)  {
      
    	Log.e("start tag name :: ", qName);
    	tempVal=new StringBuffer();	
    	
    
    	try
    	{
    	
    	if (qName.equalsIgnoreCase("Product")) {
        	 flag = 1;
        	tempProduct = new ProductReal();
        	tempVal=new StringBuffer();	
         }
       
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public void characters(char[] ch, int start, int length) 
             {
    	
    	try
    	{	Log.e("temp Val :: ", "::::"+ch.toString()+",,"+start+",,,,"+length);
    		tempVal.append(ch, start, length);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    public void endElement(String uri, String localName, String qName)
            {
    	
    	if(tempVal == null || tempVal.equals("null"))
    		tempVal = new StringBuffer("0");
    	
    	Log.e("end tag name :: ", qName);
    	
    	try{
    		if(qName.equalsIgnoreCase("Result") && flag ==0){
        		map.put("result", tempVal.toString()); 
        	}else if(qName.equalsIgnoreCase("Message") && flag ==0){    		
        		map.put("message", tempVal.toString());    		
        	}else if(qName.equalsIgnoreCase("InvoiceId")  && flag ==0){
    			 map.put("InvoiceId", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("InvoiceDate")  && flag ==0){
    			 map.put("InvoiceDate", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("CustomerId")  && flag ==0) {
    			 map.put("CustomerId", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("TotalAmount")  && flag ==0){
    			 map.put("TotalAmount", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("Tax")  && flag ==0){
    			 map.put("Tax", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("Discount")  && flag ==0){
    			 map.put("Discount", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("GrandTotal")  && flag ==0){
    			 map.put("GrandTotal", tempVal.toString());  
    		 }else if(qName.equalsIgnoreCase("Retrun_DateTime")  && flag ==0){
    			 map.put("Retrun_DateTime", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("ReturnStoreID")  && flag ==0){
    			 map.put("ReturnStoreID", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("RetrunPosID")  && flag ==0){
    			 map.put("RetrunPosID", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("RetrunReason_ID")  && flag ==0){
    			 map.put("RetrunReason_ID", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("RetrunTotal")  && flag ==0){
    			 
    		 }else if(qName.equalsIgnoreCase("Transaction_ID")  && flag ==0){
    			 map.put("Transaction_ID", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("ReturnTotal")  && flag ==0){
    			 map.put("ReturnTotal", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("InvoiceStatus")  && flag ==0){
    			 map.put("InvoiceStatus", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("PaymentMethodGrid")  && flag ==0){
    			 map.put("PaymentMethod", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("Paid")  && flag ==0){
    			 map.put("Paid", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("CreatedBy")  && flag ==0){
    			 map.put("CreatedBy", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("CurrencyID")  && flag ==0){
    			 map.put("CurrencyID", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("DiscountType")  && flag ==0){
    			 map.put("MdiscountType", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("DiscountAmount")  && flag ==0){
    			 map.put("Mdiscount", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("MDiscountAmount")  && flag ==0){
    			 map.put("MDiscountAmount", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("MPDiscountAmount")  && flag ==0){
    			 map.put("MPDiscountAmount", tempVal.toString());
    		 }else if(qName.equalsIgnoreCase("DiscountTotal")  && flag ==0){
    			 map.put("DiscountTotal", tempVal.toString());
    		 }else if (qName.equalsIgnoreCase("Product")) 
    		 {
    			 tempProduct.setTotalPrice(tempProduct.getPrice().multiply(BigDecimal.valueOf(tempProduct.getSelectedQuatity())));
    			 tempProduct.setDiscount(tempProduct.getSingleItemDiscount().multiply(BigDecimal.valueOf(tempProduct.getSelectedQuatity())));
    			 tempProduct.setTax(tempProduct.getSingleItemTax().multiply(BigDecimal.valueOf(tempProduct.getSelectedQuatity())));
    			 if(tempProduct.getMDType() == -1 || tempProduct.getMDType() == 0)
    				 tempProduct.setMdiscount(BigDecimal.valueOf(0));
    			 
    			 if(tempProduct.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
    				 if(tempProduct.getMDType() ==PosConstants.MDAmount){
    					 BigDecimal temp = tempProduct.getTotalPrice().subtract(tempProduct.getMdiscount());
    					 temp.setScale(2);
    					 tempProduct.setDiscountedValue(temp);
    				 	 
    				 }
    				 else{ 
    					 BigDecimal temp = tempProduct.getMdiscount().multiply(tempProduct.getTotalPrice());
    					 temp = temp.divide(BigDecimal.valueOf(100), 2);
    					 tempProduct.setDiscountedValue(tempProduct.getTotalPrice().subtract(temp));
    				 }
    			 }else{
    				 BigDecimal temp = tempProduct.getSingleItemDiscount().multiply(BigDecimal.valueOf(tempProduct.getSelectedQuatity()));
    				 tempProduct.setDiscountedValue(tempProduct.getTotalPrice().subtract(temp));
    			 }
    			 
    			 tempProduct.getDiscountedValue().setScale(2);
    			 
    	    		returnProductList.add(tempProduct);
    	    		flag = 0;
   	         } else if (qName.equalsIgnoreCase("RecordId")){
   	        	tempProduct.setRecordId(Integer.parseInt(tempVal.toString()));
   	         }else if (qName.equalsIgnoreCase("ProductId")){
   	        	tempProduct.setPid(Integer.parseInt(tempVal.toString()));
   	         }else if (qName.equalsIgnoreCase("ProductName")){
   	        	tempProduct.setProductName(tempVal.toString());
   	         }else if (qName.equalsIgnoreCase("Type")){
   	        	 
   	         }else if (qName.equalsIgnoreCase("Quantity")){
   	        	tempProduct.setSelectedQuatity(Integer.parseInt(tempVal.toString()));
   	        	tempProduct.setInitialQuantity(Integer.parseInt(tempVal.toString()));
   	        	
   	         }else if (qName.equalsIgnoreCase("Barcode")){
   	        	tempProduct.setBarCode(tempVal.toString());
   	         }else if (qName.equalsIgnoreCase("ItemId")){
   	        	tempProduct.setItemId(tempVal.toString());
   	         }else if (qName.equalsIgnoreCase("Price")){
   	        	tempProduct.setPrice(BigDecimal.valueOf(Double.parseDouble(tempVal.toString())));
   	         }else if (qName.equalsIgnoreCase("Discount")){
   	        	tempProduct.setSingleItemDiscount(BigDecimal.valueOf(Double.parseDouble(tempVal.toString())));
   	         }else if (qName.equalsIgnoreCase("Tax")){
   	        	tempProduct.setSingleItemTax(BigDecimal.valueOf(Double.parseDouble(tempVal.toString())));
   	         }else if (qName.equalsIgnoreCase("retrurnquantity")){
   	        	tempProduct.setInitialReturnQuantity(Integer.parseInt(tempVal.toString()));
   	         }else if (qName.equalsIgnoreCase("RetrunReason_ID")){
   	        	tempProduct.setRetunResonId(Integer.parseInt(tempVal.toString()));
   	         }else if (qName.equalsIgnoreCase("InvoiceDetailID")){
   	        	tempProduct.setInvoiceDetailID((Integer.parseInt(tempVal.toString())));
   	         }else if (qName.equalsIgnoreCase("MaxValue")){
   	        	 
   	         }else if (qName.equalsIgnoreCase("TestQuantity")){
   	        	 
   	         }else if (qName.equalsIgnoreCase("ProductListValue")){
   	        	 
   	         }else if (qName.equalsIgnoreCase("MDiscount")){
   	        	tempProduct.setMdiscount(BigDecimal.valueOf(Double.parseDouble(tempVal.toString())));
   	         }else if (qName.equalsIgnoreCase("ManualDis")){
   	        	 
   	         }else if (qName.equalsIgnoreCase("DiscountType")){
   	        	 if(Integer.parseInt(tempVal.toString())==1)
   	        		 tempProduct.setMDType(PosConstants.MDPercentage);
   	        	 else if(Integer.parseInt(tempVal.toString())==2)
   	        		 tempProduct.setMDType(PosConstants.MDAmount);
   	        	 else{
   	        		tempProduct.setMDType(PosConstants.MDnoapplied);
   	        	 }
   	         }else if(qName.equalsIgnoreCase("IsPayWithDiscount")){
   	        	 isPayWithDiscount = Boolean.valueOf(tempVal.toString());
   	         }
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	
    }
	public boolean isPayWithDiscount() {
		return isPayWithDiscount;
	}
}