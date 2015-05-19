package com.pos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CurrencySettingHandler extends DefaultHandler
{
	private ArrayList<CurrencySetting> userDetailList;
    private StringBuffer tempVal;
    private CurrencySetting tempUserDetail;
    Map<String,String> map;
    
    public CurrencySettingHandler() {
    	userDetailList = new ArrayList<CurrencySetting>();
        map=new HashMap<String, String>();
    }
    public ArrayList<CurrencySetting> getCurrencySettingList() {
        return userDetailList;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
    	
         if (qName.equalsIgnoreCase("CurrencySettings") ) {
            
            tempUserDetail = new CurrencySetting();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("CurrencyID"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("CurrentSaleVal"))
     	{    		
     		tempVal=new StringBuffer();	
     	} 
     	else if (qName.equalsIgnoreCase("CurrentReturnVal")) {
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
    		map.put("result", tempVal.toString());  
    		
    	}
    	 else if(qName.equalsIgnoreCase("Message"))
    	{    		
    		map.put("message", tempVal.toString());    		
    	}
    	 
    	else if(qName.equalsIgnoreCase("CurrencySettings")) {
            // add it to the list
            userDetailList.add(tempUserDetail);
        } 
    	else if (qName.equalsIgnoreCase("CurrencyID")) 
    	{
            tempUserDetail.setCurrencyId(Integer.parseInt(tempVal.toString()));
        } 
    	else if (qName.equalsIgnoreCase("CurrentSaleVal")) 
    	{
            tempUserDetail.setCurrSaleValue(tempVal.toString());
            
        }
    	else if (qName.equalsIgnoreCase("CurrentReturnVal")) {
            tempUserDetail.setCurrReturnValue(tempVal.toString());
        } 
    	
    	 
    }
}

