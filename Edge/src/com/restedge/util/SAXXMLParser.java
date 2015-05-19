package com.restedge.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

public class SAXXMLParser {

	public static Map<String, String> map=null;
	public static List<City> parse(InputStream is) {
       List<City> cities = null;
       
        try {
        	
//        	SAXParserFactory spf = SAXParserFactory.newInstance();
//            try {
//            	 CityXMLHandler saxHandler = new CityXMLHandler();
//                //get a new instance of parser
//                SAXParser sp = spf.newSAXParser();            
//                //parse the file and also register this class for call backs
//                sp.parse("http://112.196.24.205:111/utilivewebaccess.asmx/CityListing",new CityXMLHandler());
//                cities = saxHandler.getEmployees();
//                map = saxHandler.getOtherData();
//            } catch (SAXException se) {
//                se.printStackTrace();
//            } catch (ParserConfigurationException pce) {
//                pce.printStackTrace();
//            } catch (IOException ie) {
//                ie.printStackTrace();
//            }
        	
        	
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            CityXMLHandler saxHandler = new CityXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
            // get the `Employee list`
            cities = saxHandler.getEmployees();
         map = saxHandler.getOtherData();
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
        }
 
        // return Employee list
        return cities;
    }
	public static Map<String, String> parse1(InputStream is)
	{
		Map<String, String> map=null;
		try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            CityXMLHandler saxHandler = new CityXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            xmlReader.parse(new InputSource(is));
            // get the `Employee list`
            map = saxHandler.getOtherData();
 
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
        }
		return map;
		
	}
	public static  Map<String,List<Place>> parsePlaces(InputStream is) {
		 Map<String,List<Place>> placesRestaurant = null;
	        try {
	            // create a XMLReader from SAXParser
	            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
	                    .getXMLReader();
	            // create a SAXXMLHandler
	            PlaceXMLHandler saxHandler = new PlaceXMLHandler();
	            // store handler in XMLReader
	            xmlReader.setContentHandler(saxHandler);
	            // the process starts
	            InputSource ins=new InputSource(is);
	            ins.setEncoding("UTF-8");
	            xmlReader.parse(ins);
	            
	            // get the `Employee list`
	            placesRestaurant = saxHandler.getRestaurantPlaces();
	          
	            map=saxHandler.getOtherData();
	 
	        } catch (Exception ex) {
	            Log.d("XML", "SAXXMLParser: restaurant failed"+ex.getMessage());
	        }
	 
	        // return Employee list
	        return placesRestaurant;
	    }
	
	public static List<Place> parseNigtClub(InputStream is) {
	       List<Place> placesNightClub = null;
	        try {
	            // create a XMLReader from SAXParser
	            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
	                    .getXMLReader();
	            // create a SAXXMLHandler
	            PlaceXMLHandler saxHandler = new PlaceXMLHandler();
	            // store handler in XMLReader
	            xmlReader.setContentHandler(saxHandler);
	            // the process starts
	            InputSource ins=new InputSource(is);
	            ins.setEncoding("UTF-8");
	            xmlReader.parse(ins);
	            // get the `Employee list`
	            placesNightClub = saxHandler.getNightClubsPlaces();
	 
	        } catch (Exception ex) {
	            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
	        }
	 
	        // return Employee list
	        return placesNightClub;
	    }
	public static Map<String, String> parseRestaurantChecks(InputStream is)
	{
		Map<String, String> map=null;
		try {
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            PlaceXMLHandler saxHandler = new PlaceXMLHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
            InputSource ins=new InputSource(is);
            ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
            // get the `Employee list`
            map = saxHandler.getOtherData();
 
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
        }
		return map;
		
	}
	public static List<User> parseComment(InputStream is) {
        List<User> comments = null;
      try {
          // create a XMLReader from SAXParser
          XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
          // create a SAXXMLHandler
          CommentXMLHandler saxHandler = new CommentXMLHandler();
          // store handler in XMLReader
          xmlReader.setContentHandler(saxHandler);
          // the process starts
          xmlReader.parse(new InputSource(is));
          // get the `Employee list`
          //cities = saxHandler.getEmployees();
          comments = saxHandler.getComments();

      } catch (Exception ex) {
          Log.d("XML", "SAXXMLParser: parse() failed");
      }

      // return Employee list
      return comments;
  }
	public static ArrayList<Event> getEvents(InputStream is)
	{
		
		ArrayList<Event> eventList=null;
		try
		{
		 XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                 .getXMLReader();
		 EventXMLHandler saxHandler = new EventXMLHandler();
		 xmlReader.setContentHandler(saxHandler);
         // the process starts
         xmlReader.parse(new InputSource(is));
         
        eventList= saxHandler.getEvents();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return eventList;
		
	}
	public static Map<String, String> getEventsOtherData(InputStream is)
	{
		
		Map<String, String> map=null;
		try
		{
		 XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                 .getXMLReader();
		 EventXMLHandler saxHandler = new EventXMLHandler();
		 xmlReader.setContentHandler(saxHandler);
         // the process starts
         xmlReader.parse(new InputSource(is));
         
        map= saxHandler.getOtherData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	public static ArrayList<AddEventBean> getAddEventPlaces(InputStream is)
	{
		
		ArrayList<AddEventBean> eventList=null;
		try
		{
		 XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                 .getXMLReader();
		 AddEventPlaceHandler saxHandler = new AddEventPlaceHandler();
		 xmlReader.setContentHandler(saxHandler);
         // the process starts
         xmlReader.parse(new InputSource(is));
         
        eventList= saxHandler.getPlaces();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return eventList;
		
	}
	public static Map<String, String> getAddEventPlacesOtherData(InputStream is)
	{
		
		Map<String, String> map=null;
		try
		{
		 XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                 .getXMLReader();
		 AddEventPlaceHandler saxHandler = new AddEventPlaceHandler();
		 xmlReader.setContentHandler(saxHandler);
         // the process starts
         xmlReader.parse(new InputSource(is));
         
        map= saxHandler.getOtherData();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return map;
		
	}
	
}
