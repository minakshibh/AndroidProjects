package com.restedge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class EventXMLHandler extends DefaultHandler{

	ArrayList<Event> events=null;
	private StringBuffer tempVal=new StringBuffer();
	private Event tempEvent;
	HashMap<String, String> map;
	 public EventXMLHandler() {
	    	events = new ArrayList<Event>();
	    	 map=new HashMap<String, String>();
	    }
	 public ArrayList<Event> getEvents() 
	 {
	        return events;
	 }
	 public Map<String, String> getOtherData()
	    {
			return map;
	    }
	 public void startElement(String uri, String localName, String qName,
	            Attributes attributes)  {
	        // reset
	        
	       //This is used to get lastupdatecities
	        if (qName.equalsIgnoreCase("event") ) {
	            // create a new instance of employee
	        	System.out.println("event");
	            tempEvent = new Event();
	        }
	        else if(qName.equalsIgnoreCase("Result"))
	     	{
	        	tempVal=new StringBuffer();	
	        	System.out.println("Result");
	     	}else if(qName.equalsIgnoreCase("Message"))
	     	{    		
	     		tempVal=new StringBuffer();	
	     		System.out.println("Message");
	     	}else if (qName.equalsIgnoreCase("Id")) {
	     		tempVal=new StringBuffer();
	     		System.out.println("Id");
	         } 
	     	else if (qName.equalsIgnoreCase("EventName")) {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("EventName");
	        	 
	         } else if (qName.equalsIgnoreCase("EventImage")) {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("EventImage");
	         }
	         else if (qName.equalsIgnoreCase("EventDesc")) {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("EventDesc");
	         }
	         else if(qName.equalsIgnoreCase("StartDate"))
	         {
	        	 tempVal=new StringBuffer();
	        	 
	        	 System.out.println("StartDate");
	         }
	         else if(qName.equalsIgnoreCase("EndDate"))
	         {
	        	 tempVal=new StringBuffer();
	        	 
	        	 System.out.println("EndDate");
	         }
	         else if(qName.equalsIgnoreCase("VenueId"))
	         {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("VenueId");
	        	 
	         }else if(qName.equalsIgnoreCase("VenueName"))
	         {
	        	 tempVal=new StringBuffer();
	        	 
	        	 System.out.println("VenueName");
	         }
	         else if(qName.equalsIgnoreCase("Latitude"))
	         {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("Latitude");
	         }
	         else if(qName.equalsIgnoreCase("Longitude"))
	         {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("Longitude");
	         }
	         else if(qName.equalsIgnoreCase("CityName"))
	         {
	        	 tempVal=new StringBuffer();
	        	 System.out.println("Longitude");
	         }
	    }
	 	public void characters(char[] ch, int start, int length)
	             {
	    	try{
	    		
	    		tempVal.append(ch, start, length);
	    		System.out.println(tempVal);
	    	}catch(Exception e){
	    		e.printStackTrace();
	    	}
	    }
	 	public void endElement(String uri, String localName, String qName)
	            {
	    	
	    	if(qName.equalsIgnoreCase("Result"))
	    	{
	    		map.put("Result", tempVal.toString()); 
	    		
	    		
	    		System.out.println(tempVal);
	    		
	    		
	    	}else if(qName.equalsIgnoreCase("Message"))
	    	{    		
	    		map.put("Message", tempVal.toString());   
	    		System.out.println(tempVal);
	    	}
	    	else if(qName.equalsIgnoreCase("event")) {
	            // add it to the list
	            events.add(tempEvent);
	            System.out.println(tempVal);
	        } else if (qName.equalsIgnoreCase("Id")) {
	        	tempEvent.setEventId(Integer.parseInt(tempVal.toString()));
	        	
	        	System.out.println(tempVal);
	        } else if (qName.equalsIgnoreCase("EventName")) {
	        	tempEvent.setEventName(tempVal.toString());
	        	System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("EventDesc")) {
	            tempEvent.setEventDesc(tempVal.toString());
	            System.out.println(tempVal);
	        } 
	        else if (qName.equalsIgnoreCase("EventImage")) {
	            tempEvent.setEventImageUrl(tempVal.toString());
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("StartDate")) {
	        	
	        	
	        		System.out.println("Tempval"+tempVal);
	        		String[] str= tempVal.toString().split(" ");
	        		
	        		
	        		String [] str1=str[1].split(":");
	        		
	            tempEvent.setStartDate(str[0]+" "+str1[0]+":"+str1[1]);
	        	
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("EndDate")) {
	            
	        	String[] str= tempVal.toString().split(" ");
        		String [] str1=str[1].split(":");
	        	
	        	tempEvent.setEndDate(str[0]+" "+str1[0]+":"+str1[1]);
	            
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("VenueId")) {
	            tempEvent.setVenueId(Integer.parseInt(tempVal.toString()));
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("VenueName")) {
	            tempEvent.setVenueName(tempVal.toString());
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("Latitude")) {
	            tempEvent.setLatitude(tempVal.toString());
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("Longitude")) {
	            tempEvent.setLongitude(tempVal.toString());
	            System.out.println(tempVal);
	        }
	        else if (qName.equalsIgnoreCase("CityName")) {
	            tempEvent.setCityName(tempVal.toString());
	            System.out.println(tempVal);
	        }
	    }
}
