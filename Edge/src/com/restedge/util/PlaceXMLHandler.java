package com.restedge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class PlaceXMLHandler extends DefaultHandler {
 
    private List<Place> restaurantPlaces;
    private List<Place> nightclubPlaces;
    private StringBuffer tempVal;
    private Place tempPlace;
    Map<String,String> map;
    boolean flag=true;
//    StringBuilder buffer;
    Map<String,List<Place>> hashMap;
    
    public PlaceXMLHandler() {
        restaurantPlaces = new ArrayList<Place>();
        nightclubPlaces=new ArrayList<Place>();
        map=new HashMap<String, String>();
        hashMap=new HashMap<String, List<Place>>();
    }
 
    public  Map<String,List<Place>> getRestaurantPlaces() {
        
    	hashMap.put("Restaurants", restaurantPlaces);
//    	System.out.println(restaurantPlaces.size());
    	hashMap.put("NightClubs", nightclubPlaces);
//    	System.out.println(nightclubPlaces.size());
    	return hashMap;
    }
 
    public List<Place> getNightClubsPlaces()
    {
    	return nightclubPlaces;
    }
    
    public Map<String, String> getOtherData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes)  {

        if (qName.equalsIgnoreCase("Restaurant")) {
            // create a new instance of employee
            flag=true;
        	tempPlace = new Place();
            
        }else if (qName.equalsIgnoreCase("NightClub")) {
            // create a new instance of employee
           flag=false;
           tempPlace = new Place();
            
        }else if(qName.equalsIgnoreCase("LastUpdated"))
        	tempVal=new StringBuffer();
	    
        else if(qName.equalsIgnoreCase("Result"))
        	tempVal=new StringBuffer();
        
        else if(qName.equalsIgnoreCase("Message"))
    		tempVal=new StringBuffer();
    	    	
        else if (qName.equalsIgnoreCase("ID"))
    		tempVal=new StringBuffer();
    	
        else if (qName.equalsIgnoreCase("Name")) 
    		tempVal=new StringBuffer();
    	
        else if (qName.equalsIgnoreCase("SubCategory"))
    		tempVal=new StringBuffer();
    	
        else if (qName.equalsIgnoreCase("rating"))
    		tempVal=new StringBuffer();
    	
        else if (qName.equalsIgnoreCase("ImageUrl"))
    		tempVal=new StringBuffer();
    	
        else if (qName.equalsIgnoreCase("address"))
    	   tempVal=new StringBuffer();
       
        else if (qName.equalsIgnoreCase("longitude"))
    	   tempVal=new StringBuffer();
       
        else if (qName.equalsIgnoreCase("latitude"))
        	tempVal=new StringBuffer();
        
        else if (qName.equalsIgnoreCase("description"))
    	   tempVal=new StringBuffer();
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
    {
    	try
    	{
    	
	    	if(qName.equalsIgnoreCase("LastUpdated"))
	    	{
	    		map.put("LastUpdated", tempVal.toString());
	    	}else if(qName.equalsIgnoreCase("Result"))
	    	{
	    		map.put("Result", tempVal.toString());
	    	}else if(qName.equalsIgnoreCase("Message"))
	    	{
	    		map.put("Message", tempVal.toString());
	    	}else if (qName.equalsIgnoreCase("Restaurant")) {
	            // add it to the list
	        	flag=true;
	            restaurantPlaces.add(tempPlace);
	        }else if(qName.equalsIgnoreCase("NightClub"))
	        {
	        	flag=false;
	        	
	        	nightclubPlaces.add(tempPlace);
	        }else if (qName.equalsIgnoreCase("ID"))
	        {	        	
	        	tempPlace.setId(Integer.parseInt(tempVal.toString()));
	        	
	        }else if (qName.equalsIgnoreCase("Name")) 
	        {
	        	tempPlace.setName(tempVal.toString());		         
	        }else if (qName.equalsIgnoreCase("SubCategory"))
	        {
	        	tempPlace.setSubcategory(tempVal.toString());
	        }else  if (qName.equalsIgnoreCase("rating"))
	        {
	        	tempPlace.setRating(Double.parseDouble(tempVal.toString()));
	        }else if (qName.equalsIgnoreCase("ImageUrl"))
	        {
	        	tempPlace.setImageUrl(tempVal.toString());
	        }else  if (qName.equalsIgnoreCase("address"))
	        {
	        	tempPlace.setAddress(tempVal.toString());
	        }else if (qName.equalsIgnoreCase("longitude"))
	        {	        	
	        	tempPlace.setLongitude(Double.parseDouble(tempVal.toString()));	      
	        }else if (qName.equalsIgnoreCase("latitude"))
	        {
	        	tempPlace.setLatitude(Double.parseDouble(tempVal.toString()));
	        }else if (qName.equalsIgnoreCase("description"))
	        {
	        	tempPlace.setDescription(tempVal.toString());
	        }
	        else if(qName.equalsIgnoreCase("VisibilityFlag"))
	        {
	        	tempPlace.setVisibility_flag(Integer.parseInt(tempVal.toString()));
	        }
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}    	
    }
}

