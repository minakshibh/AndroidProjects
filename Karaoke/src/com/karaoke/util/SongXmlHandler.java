package com.karaoke.util;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;



public class SongXmlHandler  extends DefaultHandler {
 
    private ArrayList<Song> songs;
    private StringBuffer tempVal;
    private Song tempSong;
    Map<String,String> map;
    
    public SongXmlHandler() {
        songs = new ArrayList<Song>();
        map=new HashMap<String, String>();
    }
 
    public ArrayList<Song> getSongs() {
        return songs;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
         if (qName.equalsIgnoreCase("video") ) {
            
            tempSong = new Song();
        }else if(qName.equalsIgnoreCase("Result"))
     	{
        	tempVal=new StringBuffer();	
     	}else if(qName.equalsIgnoreCase("Message"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("HaveRecords"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if (qName.equalsIgnoreCase("VideoId")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("VideoUrl")) {
        	 tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("VideoName")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("VideoPrice")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("ThumbnailUrl")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("ArtistName")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("AlbumName")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("SampleVideoUrl")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("TrackCode")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("Duration")) {
        	 tempVal=new StringBuffer();
         }else if (qName.equalsIgnoreCase("ItemStatus")) {
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
    	else if(qName.equalsIgnoreCase("HaveRecords"))
    	{    		
    		map.put("HaveRecords", tempVal.toString());    		
    	}
    	else if(qName.equalsIgnoreCase("video")) {
            // add it to the list
            songs.add(tempSong);
        } else if (qName.equalsIgnoreCase("VideoId")) {
            tempSong.setSongId(Integer.parseInt(tempVal.toString()));
        } else if (qName.equalsIgnoreCase("VideoName")) {
            tempSong.setSongName(tempVal.toString());
        } else if (qName.equalsIgnoreCase("VideoUrl")) {
            tempSong.setSongUrl(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("VideoPrice")) {
            tempSong.setSongPrice(Float.parseFloat(tempVal.toString()));
        }
        else if (qName.equalsIgnoreCase("ThumbnailUrl")) 
        {
            tempSong.setSongTumbnailUrl(tempVal.toString());
        }
        else if (qName.equalsIgnoreCase("ArtistName")) 
        {
            tempSong.setArtistName(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("AlbumName")) 
        {
            tempSong.setAlbumName(tempVal.toString());
        }
        else if (qName.equalsIgnoreCase("SampleVideoUrl")) 
        {
            tempSong.setSampleVideoUrl(tempVal.toString());
        } 
        else if(qName.equalsIgnoreCase("TrackCode"))
        {
        	tempSong.setAlbumcode(tempVal.toString());
        }
        else if(qName.equalsIgnoreCase("Duration"))
        {
        	tempSong.setDuration(tempVal.toString());
        }else if (qName.equalsIgnoreCase("ItemStatus")) {
       	 	if(tempVal.toString().equals("0"))
       	 		tempSong.setPurchased(false);
       	 	else
       	 		tempSong.setPurchased(true);
       	}
    }
}