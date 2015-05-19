package com.pos.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class SAXXMLParser {

	public static Map<String, String> map=null;
	public static boolean isPayWithDiscount;
     
	public static ArrayList<Cashier> parseUserDetail(InputStream is) {
		
		ArrayList<Cashier> userlist = null;
        try {
        	
            
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
          
            
            CashierDetailHandler saxHandler=new CashierDetailHandler();
            xmlReader.setContentHandler(saxHandler);
           
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
           
            userlist = saxHandler.getUserDetailList();
          map = saxHandler.getOtherData();
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: cashier failed"+ex.getMessage());
        }
 
        // return user list
        return userlist;
    }
	public static ArrayList<ProductReal> parseProductDetail(InputStream is) {
		
		ArrayList<ProductReal> productlist = null;
        try {
        	
            
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
          
            
            ProductDetailHandler saxHandler=new ProductDetailHandler();
            xmlReader.setContentHandler(saxHandler);
           
           InputSource ins= new InputSource(is);
           ins.setEncoding("UTF-8");
            xmlReader.parse(ins);
           
            Log.e("sdf", "sfsfsfsf");
            productlist = saxHandler.getProductList();
            map = saxHandler.getOtherData();
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: product failed"+ex.getMessage());
        }
 
        // return user list
        return productlist;
    }
	public static ArrayList<ProductReal> parseReturnProduct(InputStream is) {
		
		ArrayList<ProductReal> productlist = null;
        try {
        	
            
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
          
            
            TransProductHandler saxHandler=new TransProductHandler();
            
            xmlReader.setContentHandler(saxHandler);
           
           InputSource ins= new InputSource(is);
           Log.e("sdf", "sfsfsfsf"+ins);
           ins.setEncoding("UTF-8");
           Log.e("sdf", "sfsfsfsf"+ins);
            xmlReader.parse(ins);
           
            Log.e("sdf", "sfsfsfsf"+ins);
            productlist = saxHandler.getreturnProductList();
            map = saxHandler.getOtherData();
            isPayWithDiscount = saxHandler.isPayWithDiscount();
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: product  failed"+ex.getMessage());
        }
 
        return productlist;
    }
	public static ArrayList<CurrencySetting> parseCurrencySetting(InputStream is) {
		
		ArrayList<CurrencySetting> currencySettingList = null;
        try {
        	
            
            XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser()
                    .getXMLReader();
          
            
            CurrencySettingHandler saxHandler=new CurrencySettingHandler();
            
            xmlReader.setContentHandler(saxHandler);
           
           InputSource ins= new InputSource(is);
           Log.e("sdf", "sfsfsfsf"+ins);
           ins.setEncoding("UTF-8");
           Log.e("sdf", "sfsfsfsf"+ins);
            xmlReader.parse(ins);
           
            Log.e("sdf", "sfsfsfsf"+ins);
            currencySettingList = saxHandler.getCurrencySettingList();
            map = saxHandler.getOtherData();
            
        } catch (Exception ex) {
            Log.d("XML", "SAXXMLParser: currency failed"+ex.getMessage());
        }
 
        // return user list
        return currencySettingList;
    }
	
	
}
