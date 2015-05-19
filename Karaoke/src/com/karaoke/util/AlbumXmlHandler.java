package com.karaoke.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.karaoke.util.Song.VideosInAlbum;

public class AlbumXmlHandler extends DefaultHandler {
 
    private ArrayList<Song> albums;
    private StringBuffer tempVal;
    private Song tempAlbum;
    private VideosInAlbum tempVideoInAlbum;
    Map<String,String> map;
    private ArrayList<VideosInAlbum> videosinAlbums;
    private int itemStatus = -1;
    
    public AlbumXmlHandler() {
    	albums = new ArrayList<Song>();
        map=new HashMap<String, String>();
    }
 
    public ArrayList<Song> getSongs() {
        return albums;
    }
 
    public Map<String, String> getOtherData()
    {
		return map;
    }
    // Event Handlers
    public void startElement(String uri, String localName, String qName,
            Attributes attributes) throws SAXException {
      
    	try{
    	
         if (qName.equalsIgnoreCase("Album") ) {
            
            tempAlbum = new Song();
            videosinAlbums=new ArrayList<Song.VideosInAlbum>();
        }
         else if(qName.equalsIgnoreCase("Result")) {
        	tempVal=new StringBuffer();	
     	}
         else if(qName.equalsIgnoreCase("Message")) {    		
     		tempVal=new StringBuffer();	
     	}
     	else if(qName.equalsIgnoreCase("HaveRecords"))
     	{    		
     		tempVal=new StringBuffer();	
     	}
     	else if (qName.equalsIgnoreCase("AlbumID")) {
     		tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("AlbumUrl")) {
        	 tempVal=new StringBuffer();
         } else if (qName.equalsIgnoreCase("AlbumName")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("AlbumPrice")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("AlbumThumbnailUrl")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("AlbumArtistName")) {
        	 tempVal=new StringBuffer();
         }
         else if (qName.equalsIgnoreCase("NoOfSongs")) {
        	 tempVal=new StringBuffer();
         }
         else if(qName.equalsIgnoreCase("UploadDate"))
         {
        	 tempVal=new StringBuffer();
         }
         else if(qName.equalsIgnoreCase("AlbumCode"))
         {
        	 tempVal=new StringBuffer();
         }
      else if(qName.equalsIgnoreCase("Video"))
         {
        	 tempVal=new StringBuffer();
        	  tempVideoInAlbum=new  Song().new VideosInAlbum();
         }
         else if(qName.equalsIgnoreCase("VideoID"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("TrackCode"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("VideoUrl"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("SampleVideoUrl"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("VideoName"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("ThumbnailUrl"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("ArtistName"))
         {
        	 tempVal=new StringBuffer();
        	
         }
         else if(qName.equalsIgnoreCase("Duration"))
         {
        	 tempVal=new StringBuffer();
        	
         }else if (qName.equalsIgnoreCase("ItemStatus")) {
        	 tempVal=new StringBuffer();
         }
    	}catch(Exception e){
    		Log.e("in start element", "in start element");
    		e.printStackTrace();
    	}
    }
 
    
    public void characters(char[] ch, int start, int length)
            throws SAXException {
    	try{
    		tempVal.append(ch, start, length);
    	}catch(Exception e){
    		Log.e("in character", "tempVAl null");
    		e.printStackTrace();
    	}
    }
 
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
    	try{
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
    	else if(qName.equalsIgnoreCase("Album")) {
            // add it to the list
    		tempAlbum.setSubCategories(videosinAlbums);
            albums.add(tempAlbum);
            
            
        } else if (qName.equalsIgnoreCase("AlbumID"))
        {
        	tempAlbum.setAlbumId(Integer.parseInt(tempVal.toString()));
        } 
        else if (qName.equalsIgnoreCase("AlbumName")) 
        {
        	tempAlbum.setAlbumName(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("AlbumUrl"))
        {
        	tempAlbum.setSongUrl(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("AlbumPrice")) 
        {
        	tempAlbum.setSongPrice(Float.parseFloat(tempVal.toString()));
        }
        else if (qName.equalsIgnoreCase("AlbumThumbnailUrl")) 
        {
        	tempAlbum.setSongTumbnailUrl(tempVal.toString());
        }
        else if (qName.equalsIgnoreCase("AlbumArtistName")) 
        {
        	tempAlbum.setArtistName(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("NoOfSongs")) 
        {
        	tempAlbum.setSongs(Integer.parseInt(tempVal.toString()));
        } 
        else if (qName.equalsIgnoreCase("UploadDate")) 
        {
        	tempAlbum.setUploadDate(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("AlbumCode")) 
        {
        	tempAlbum.setAlbumcode(tempVal.toString());
        }
//    	 Video start here
       else if (qName.equalsIgnoreCase("Video")) 
        {
    	    
    		Log.e("in purchase", ""+itemStatus);
        	videosinAlbums.add(tempVideoInAlbum);
        	
        	//tempAlbum.setPurchased(true);
        	
        	  if(itemStatus == 0)
        		  tempAlbum.setPurchased(false);
       	   else if(itemStatus == 1)
       		tempAlbum.setPurchased(true);
       	   else{
       		   
       	   } 
        }
        else if (qName.equalsIgnoreCase("VideoID")) 
        {
        	tempVideoInAlbum.setVideoId(Integer.parseInt(tempVal.toString()));
        }
        else if (qName.equalsIgnoreCase("TrackCode")) 
        {
        	tempVideoInAlbum.setVideoTrackCode(tempVal.toString());
        }
        else if (qName.equalsIgnoreCase("VideoUrl")) 
        {
        	tempVideoInAlbum.setVideoUrl(tempVal.toString());
        }
        else if (qName.equalsIgnoreCase("SampleVideoUrl")) 
        {
        	tempVideoInAlbum.setSampleVideoUrl(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("VideoName")) 
        {
        	tempVideoInAlbum.setVideoName(tempVal.toString());
        } 
        else if (qName.equalsIgnoreCase("ThumbnailUrl")) 
        {
        	tempVideoInAlbum.setThumbnailUrl(tempVal.toString());
        }  
        else if (qName.equalsIgnoreCase("ArtistName")) 
        {
        	tempVideoInAlbum.setArtistName(tempVal.toString());
        }  
        else if (qName.equalsIgnoreCase("Duration")) 
        {
        	tempVideoInAlbum.setDuration(tempVal.toString());
        }else if (qName.equalsIgnoreCase("ItemStatus")) {
       	 	if(tempVal.toString().equals("0"))
       	 		itemStatus = 0;
       	 	else
       	 		itemStatus = 1;
       	}  
    	 
    	}catch(Exception e){
    		Log.e("in end element", "in end element");
    		e.printStackTrace();
    	}
    }
}