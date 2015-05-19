package com.restedge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class CityXMLHandler extends DefaultHandler {
 
    private List<City> cities;
    private StringBuffer tempVal;
    private City tempCity;
    Map<String,String> map;
    
    public CityXMLHandler() {
        cities = new ArrayList<City>();
        map=new HashMap<String, String>();
    }
 
    public List<City> getEmployees() {
        return cities;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
        // reset
        
       //This is used to get lastupdatecities
        if (qName.equalsIgnoreCase("LastUpdatedCities")) {
        	tempVal=new StringBuffer();
        }else if (qName.equalsIgnoreCase("City") ) {
            // create a new instance of employee
            tempCity = new City();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}else if (qName.equalsIgnoreCase("CityId")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("CityName")) {
        	 tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("ImageUrl")) {
        	 tempVal=new StringBuffer();
         }
         else if(qName.equalsIgnoreCase("SupportEmail"))
         {
        	 tempVal=new StringBuffer();
         }
         else if(qName.equalsIgnoreCase("VisibilityFlag"))
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
    	
    	if(qName.equalsIgnoreCase("LastUpdatedCities"))
    	{
    		map.put("LastUpdatedCities", tempVal.toString());    	
    	}else if(qName.equalsIgnoreCase("Result"))
    	{
    		map.put("Result", tempVal.toString());    		
    	}else if(qName.equalsIgnoreCase("Message"))
    	{    		
    		map.put("Message", tempVal.toString());    		
    	}else if(qName.equalsIgnoreCase("SupportEmail"))
    	{
    		map.put("SupportEmail", tempVal.toString());
    	}
    	else if(qName.equalsIgnoreCase("City")) {
            // add it to the list
            cities.add(tempCity);
        } else if (qName.equalsIgnoreCase("CityId")) {
            tempCity.setCityId(Integer.parseInt(tempVal.toString()));
        } else if (qName.equalsIgnoreCase("CityName")) {
            tempCity.setCityName(tempVal.toString());
        } else if (qName.equalsIgnoreCase("ImageUrl")) {
            tempCity.setCityUrl(tempVal.toString());
        } 
        else if(qName.equalsIgnoreCase("VisibilityFlag"))
        {
        	tempCity.setVisibility_flag(Integer.parseInt(tempVal.toString()));
        }
    }
}
