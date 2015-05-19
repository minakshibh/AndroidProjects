package com.restedge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;

public class AddEventPlaceHandler extends DefaultHandler {
 
    private ArrayList<AddEventBean> places;
    private StringBuffer tempVal;
    private AddEventBean tempplace;
    Map<String,String> map;
    
    public AddEventPlaceHandler() {
        places = new ArrayList<AddEventBean>();
        map=new HashMap<String, String>();
    }
 
    public ArrayList<AddEventBean> getPlaces() {
        return places;
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
        if (qName.equalsIgnoreCase("place") ) {
            // create a new instance of employee
            tempplace = new AddEventBean();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}else if (qName.equalsIgnoreCase("cityId")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("PlaceName")) {
        	 tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("Place_id")) {
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
    	else if(qName.equalsIgnoreCase("place")) {
            // add it to the list
            places.add(tempplace);
        } else if (qName.equalsIgnoreCase("cityId")) {
        	Log.e("city id",""+ Integer.parseInt(tempVal.toString()));
        	tempplace.setCityId(Integer.parseInt(tempVal.toString()));
        } else if (qName.equalsIgnoreCase("PlaceName")) {
        	tempplace.setPlaceName(tempVal.toString());
        } else if (qName.equalsIgnoreCase("Place_id")) {
        	tempplace.setPlaceId(Integer.parseInt(tempVal.toString()));
        } 
        
    }
}
