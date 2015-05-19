package com.restedge.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class CommentXMLHandler extends DefaultHandler {
 
  /*  private List<City> cities;
    private String tempVal;
    private City tempCity;*/
	    //private List<City> cities;
	    private List<User> comments;
	    private StringBuffer tempVal;
	    private User tempComment;
        Map<String,String> map;
    
    public CommentXMLHandler() {
    	comments = new ArrayList<User>();
        map=new HashMap<String, String>();
    }
 
    public List<User> getComments() {
        return comments;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    	
    	
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,   Attributes attributes) throws SAXException {
        // reset
    	if (qName.equals("comment")) {
        	// create a new instance of employee
            tempComment = new User();
        }else if(qName.equalsIgnoreCase("Result"))
    	{
        	tempVal=new StringBuffer();
            
    	}else if(qName.equalsIgnoreCase("Message"))
    	{
    		tempVal=new StringBuffer();
    	}
    	else if (qName.equalsIgnoreCase("Username")) {
        	tempVal=new StringBuffer();
        }else if (qName.equals("Comment")) {
        	tempVal=new StringBuffer();
            
        }else if (qName.equalsIgnoreCase("Time")) {
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
    		
    	}else if (qName.equals("comment")) {
            // add it to the list
            comments.add(tempComment);
        } else if (qName.equalsIgnoreCase("Username")) {
            tempComment.setName(tempVal.toString());     //setCityId(Integer.parseInt(tempVal));
        } else if (qName.equals("Comment")) {
            tempComment.setComment(tempVal.toString());                  //setCityName(tempVal);
        } else if (qName.equalsIgnoreCase("Time")) {
            tempComment.setTime(tempVal.toString());                    //setCityUrl(tempVal);
        } 
    }   
}
