package com.karaoke.util;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TransactionXMlHandler  extends DefaultHandler {
 
   
    private StringBuffer tempVal;
  
    HashMap<String,String> map;
    
    public TransactionXMlHandler() {
       
        map=new HashMap<String, String>();
    }
 
   
 
    public HashMap<String, String> getTransationData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
    	
        if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
    }
 
    
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    	try{
    		tempVal.append(ch, start, length);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
 
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    	
    	 if(qName.equalsIgnoreCase("Result"))
    	{
    		map.put("Result", tempVal.toString());    		
    	}else if(qName.equalsIgnoreCase("Message"))
    	{    		
    		map.put("Message", tempVal.toString());    		
    	}
    	
    }
}