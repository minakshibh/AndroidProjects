package com.pos.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ProductDetailHandler extends DefaultHandler
{
	private ArrayList<ProductReal> productList;
    private StringBuffer tempVal;
    private ProductReal tempProduct;
    Map<String,String> map;
    
    public ProductDetailHandler() {
    	productList = new ArrayList<ProductReal>();
        map=new HashMap<String, String>();
    }
    public ArrayList<ProductReal> getProductList() {
        return productList;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    public void startElement(String uri, String localName, String qName,
            Attributes attributes)  {
      
    	try
    	{
         if (qName.equalsIgnoreCase("Product") ) {
            
        	 tempProduct = new ProductReal();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("ProductId"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("timestamp"))
     	{    		
     		tempVal=new StringBuffer();	
     	} 
     	else if (qName.equalsIgnoreCase("ProductName")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("Barcode")) {
        	 tempVal=new StringBuffer();
         } 
         else if (qName.equalsIgnoreCase("Quantity")) {
        	 tempVal=new StringBuffer();
         } 
         else if (qName.equalsIgnoreCase("ItemId")) {
        	 tempVal=new StringBuffer();
         } 
         else if (qName.equalsIgnoreCase("Price")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("Discount")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("Tax")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("IsDeleted")) {
        	 tempVal=new StringBuffer();
         }
    	}
    	catch(Exception e)
    	{
    		Log.e("Error in upper parsing", ""+e.getMessage());
    		e.printStackTrace();
    	}
    }
    public void characters(char[] ch, int start, int length)
            {
    	try{
    		tempVal.append(ch, start, length);
    	}catch(Exception e){
    		e.printStackTrace();
    		
    	}
    }
    public void endElement(String uri, String localName, String qName)
             {
    	
    	try
    	{
    	 if(qName.equalsIgnoreCase("Result"))
    	{
    		map.put("result", tempVal.toString());  
    		
    	}
    	 else if(qName.equalsIgnoreCase("Message"))
    	{    		
    		map.put("message", tempVal.toString());    		
    	}
    	 else if(qName.equalsIgnoreCase("timestamp"))
      	{    		
    		 map.put("timestamp", tempVal.toString());  
      	} 
    	else if(qName.equalsIgnoreCase("Product")) {
            // add it to the list
    		productList.add(tempProduct);
        } 
    	else if (qName.equalsIgnoreCase("ProductId")) 
    	{
            tempProduct.setPid(Integer.parseInt(tempVal.toString()));
        } 
    	else if (qName.equalsIgnoreCase("ProductName")) 
    	{
    		tempProduct.setProductName(tempVal.toString());
            
        }
    	else if (qName.equalsIgnoreCase("Quantity")) {
    		tempProduct.setTotalQuantity(Integer.parseInt(tempVal.toString()));
        } 
    	else if (qName.equalsIgnoreCase("Barcode")) {
    		tempProduct.setBarCode(tempVal.toString());
        } 
    	else if (qName.equalsIgnoreCase("Price")) {
    		tempProduct.setPrice(BigDecimal.valueOf(Double.valueOf(tempVal.toString())));
        } 
    	else if (qName.equalsIgnoreCase("Discount")) {
    		tempProduct.setDiscount(BigDecimal.valueOf(Double.valueOf(tempVal.toString())));
        } 
    	else if (qName.equalsIgnoreCase("Tax")) {
    		tempProduct.setTax(BigDecimal.valueOf(Double.valueOf(tempVal.toString())));
        }
    	else if (qName.equalsIgnoreCase("ItemId")) {
    		tempProduct.setItemId(tempVal.toString());
        }
    	else if (qName.equalsIgnoreCase("IsDeleted")) {
    		
    		tempProduct.setDeleted(Boolean.parseBoolean(tempVal.toString()));
    		System.out.println("kdfkjdfkj"+Boolean.parseBoolean(tempVal.toString()));
        } 
    	}
    	catch(Exception e)
    	{
    		Log.e("Error in set value", ""+ e.getMessage());
    		e.printStackTrace();
    	}
    }
    
}
