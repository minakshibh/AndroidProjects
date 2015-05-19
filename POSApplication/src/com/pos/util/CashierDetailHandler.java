package com.pos.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class CashierDetailHandler extends DefaultHandler
{
	private ArrayList<Cashier> userDetailList;
    private StringBuffer tempVal;
    private Cashier tempUserDetail;
    Map<String,String> map;
    
    public CashierDetailHandler() {
    	userDetailList = new ArrayList<Cashier>();
        map=new HashMap<String, String>();
    }
    public ArrayList<Cashier> getUserDetailList() {
        return userDetailList;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
    	
         if (qName.equalsIgnoreCase("user") ) {
            
            tempUserDetail = new Cashier();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("username"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("timestamp"))
     	{    		
     		tempVal=new StringBuffer();	
     	} 
     	else if (qName.equalsIgnoreCase("password")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("userId")) {
        	 tempVal=new StringBuffer();
         } 
         else if (qName.equalsIgnoreCase("IsDeleted")) {
        	 tempVal=new StringBuffer();
         } 
         else if (qName.equalsIgnoreCase("userRoleId")) {
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
    	 else if(qName.equalsIgnoreCase("timestamp"))
      	{    		
    		 map.put("timestamp", tempVal.toString());  
      	} 
    	else if(qName.equalsIgnoreCase("user")) {
            // add it to the list
            userDetailList.add(tempUserDetail);
        } 
    	else if (qName.equalsIgnoreCase("userId")) 
    	{
            tempUserDetail.setId(Integer.parseInt(tempVal.toString()));
        } 
    	else if (qName.equalsIgnoreCase("username")) 
    	{
            tempUserDetail.setUsername(tempVal.toString());
            
        }
    	else if (qName.equalsIgnoreCase("password")) {
            tempUserDetail.setPassword(tempVal.toString());
        } 
    	else if (qName.equalsIgnoreCase("IsDeleted")) 
    	{
    		tempUserDetail.setDeleted(Boolean.parseBoolean(tempVal.toString()));
        }
    	else if(qName.equalsIgnoreCase("userRoleId"))
    	{
    		tempUserDetail.setRollId(Integer.parseInt(tempVal.toString().trim()));
    		
    	}
    	 
    }
}
