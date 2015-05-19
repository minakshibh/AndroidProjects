package com.karaoke.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class SAXXMLParser {

	public static Map<String, String> map=null;
	public static ArrayList<Song> parseSong(InputStream is) {
		ArrayList<Song> songs = null;
       
        try {
        
        	
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
            SongXmlHandler saxHandler = new SongXmlHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
            // get the `Employee list`
            songs = saxHandler.getSongs();
            
         map = saxHandler.getOtherData();
            
         Log.e("Map ", ""+map);
        } catch (Exception ex) {
            Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
        }
 
        // return Employee list
        return songs;
    }
//	public static Map<String, String> parseSongData(InputStream is)
//	{
//		Map<String, String> map=null;
//		try {
//            // create a XMLReader from SAXParser
//            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
//                    .getXMLReader();
//            // create a SAXXMLHandler
//            SongXmlHandler saxHandler = new SongXmlHandler();
//            // store handler in XMLReader
//            xmlReader.setContentHandler(saxHandler);
//            // the process starts
//            InputSource ins= new InputSource(is);
//            ins.setEncoding("UTF-8");
//            xmlReader.parse(ins);
//            // get the `Employee list`
//            map = saxHandler.getOtherData();
// 
//        } catch (Exception ex) {
//            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
//        }
//		return map;
//		
//	}
	public static ArrayList<Song> parseAlbum(InputStream is) {
		ArrayList<Song> albums = null;
       
        try {
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
          
            AlbumXmlHandler saxHandler = new AlbumXmlHandler();
           
            xmlReader.setContentHandler(saxHandler);
           
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
           
            albums = saxHandler.getSongs();
         map = saxHandler.getOtherData();
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: Album failed"+ex.getMessage());
        }
 
        // return Employee list
        return albums;
    }
//	public static Map<String, String> parseAlbumData(InputStream is)
//	{
//		Map<String, String> map=null;
//		try {
//            // create a XMLReader from SAXParser
//            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
//                    .getXMLReader();
//            // create a SAXXMLHandler
//            AlbumXmlHandler saxHandler = new AlbumXmlHandler();
//            // store handler in XMLReader
//            xmlReader.setContentHandler(saxHandler);
//            // the process starts
//            xmlReader.parse(new InputSource(is));
//            // get the `Employee list`
//            map = saxHandler.getOtherData();
// 
//        } catch (Exception ex) {
//            Log.d("XML", "SAXXMLParser: other data failed"+ex.getMessage());
//        }
//		return map;
//		
//	}
	public static HashMap<String, String> parseTransation(InputStream is) {
		HashMap<String, String> map=null;
       
        try {
        
        	
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
           TransactionXMlHandler saxHandler = new TransactionXMlHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            // the process starts
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
            // get the `Employee list`
            map = saxHandler.getTransationData();
        
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
        }
 
        // return Employee list
        return map;
    }
	public static HashMap<String, String> parseCardValidation(InputStream is) {
		HashMap<String, String> map=null;
       
        try {
        
        	
            // create a XMLReader from SAXParser
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
            // create a SAXXMLHandler
           CardValidationHandler saxHandler = new CardValidationHandler();
            // store handler in XMLReader
            xmlReader.setContentHandler(saxHandler);
            
            // the process starts
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
           
            map = saxHandler.getCardValidationData();
        
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: parse() failed"+ex.getMessage());
        }
 
        // return Employee list
        return map;
    }
}
