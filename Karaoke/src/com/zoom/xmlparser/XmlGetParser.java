package com.zoom.xmlparser;


import java.io.InputStream;
import javax.xml.parsers.SAXParserFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.zoom.xmlResponseHandler.ResponseXmlHandler;

import android.util.Log;

public class XmlGetParser {

	public static String makeHttpRequest(String url){
		
		InputStream inputStream = null;
		String result = "";
		try {

			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();

			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();			
		
			// convert inputstream to string
			if(inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}

		return result;
	}
	
	private static String convertInputStreamToString(InputStream inputStream) {	

		String Result="";
       
        try {
        	
        	// create a XMLReader from SAXParser
        	XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
        	// create a SAXXMLHandler
        	ResponseXmlHandler saxHandler = new ResponseXmlHandler();        	
        	// store handler in XMLReader
        	xmlReader.setContentHandler(saxHandler);
        	// the process starts
        	InputSource ins= new InputSource(inputStream);
        	ins.setEncoding("UTF-8");
        	xmlReader.parse(ins);
        	// get the `Employee list`
        	Result = saxHandler.getOtherData();          
     
            
         
        } catch (Exception ex) {
            Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
        }
 
        // return Employee list
        return Result;
	}

}