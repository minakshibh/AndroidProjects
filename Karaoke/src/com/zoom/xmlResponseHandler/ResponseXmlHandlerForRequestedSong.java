package com.zoom.xmlResponseHandler;


import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ResponseXmlHandlerForRequestedSong extends DefaultHandler {
 
   
    private StringBuffer tempVal;  
    String map;
   
    public ResponseXmlHandlerForRequestedSong() {
    
      
    }
    public String getOtherData()
    {
		return map;
    }
  
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
    
    	
    	if (qName.equalsIgnoreCase("Success")) {
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
    	
    	
    	 if(qName.equalsIgnoreCase("Success")) {
     		map= tempVal.toString();    
     		
     	}
    	 
    	
    }
    
}