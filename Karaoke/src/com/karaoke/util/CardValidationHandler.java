package com.karaoke.util;

import java.util.HashMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class CardValidationHandler extends DefaultHandler {
 
   
    private StringBuffer tempVal;
  
    HashMap<String,String> map;
    
    public CardValidationHandler() {
       
        map=new HashMap<String, String>();
    }
 
   
 
    public HashMap<String, String> getCardValidationData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {
      
    	try
    	{
        if(qName.equalsIgnoreCase("Status"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("StatusDetail"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("VPSTxId"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("SecurityKey"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("TxAuthNo"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("AVSCV2"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("AddressResult"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("PostCodeResult"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("CV2Result"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("SecureStatus"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("FraudResponse"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("ExpiryDate"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("BankAuthCode"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("DeclineCode"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
    	}catch(Exception e)
    	{
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
    	 if(qName.equalsIgnoreCase("Status"))
    	{
    		 Log.e("Status", tempVal.toString());
    		map.put("Status", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("StatusDetail"))
    	{    		
    		map.put("StatusDetail", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("VPSTxId"))
    	{    		
    		map.put("VPSTxId", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("SecurityKey"))
    	{    		
    		map.put("SecurityKey", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("TxAuthNo"))
    	{    		
    		map.put("TxAuthNo", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("AVSCV2"))
    	{    		
    		map.put("AVSCV2", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("AddressResult"))
    	{    		
    		map.put("AddressResult", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("PostCodeResult"))
    	{    		
    		map.put("PostCodeResult", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("CV2Result"))
    	{    		
    		map.put("CV2Result", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("SecureStatus"))
    	{    		
    		map.put("SecureStatus", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("FraudResponse"))
    	{    		
    		map.put("FraudResponse", tempVal.toString());    		
    	}else if(qName.equalsIgnoreCase("ExpiryDate"))
    	{    		
    		map.put("ExpiryDate", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("BankAuthCode"))
    	{    		
    		map.put("BankAuthCode", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("DeclineCode"))
    	{    		
    		map.put("DeclineCode", tempVal.toString());    		
    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
}