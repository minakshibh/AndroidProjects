package com.pos.util;

import java.io.ByteArrayInputStream;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.posapplication.R;

import android.content.Context;
import android.content.SharedPreferences;



public class ServiceConnector {

	private String url;
	private Context context;
	private SharedPreferences posPreferences;
	
	public ServiceConnector(Context context){
		this.context = context;
		url = context.getResources().getString(R.string.liveurl);
		posPreferences = context.getSharedPreferences("posPreference", android.content.Context.MODE_PRIVATE); 	
	}
	
	public ArrayList<VoucherType> getVoucherTypes(){
		String voucherTimeStamp = posPreferences.getString("voucherTypeTimestamp", "-1");
		 ArrayList<VoucherType> voucherTypes = new ArrayList<VoucherType>();
		 try {	           
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            
	            HttpPost requestLogin = new HttpPost(url+"/"+"GetVouchers");
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
	            nameValuePairs.add(new BasicNameValuePair("timestamp", voucherTimeStamp));
	            
	            requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            HttpResponse response = httpClient.execute(requestLogin);
	            
	            HttpEntity entity = response.getEntity();
	            String content = EntityUtils.toString(entity);
	            
	            voucherTypes = parseVoucherTypeResponse(content);
	            	     
	        } catch (Exception e) {
	            e.printStackTrace();
	            
	        }
		 return voucherTypes;
	}
	
	public ArrayList<Reason> getReasonList(){
		 ArrayList<Reason> reasonList = new ArrayList<Reason>();
		 
		 try {	           
	            DefaultHttpClient httpClient = new DefaultHttpClient();
	            
	            HttpPost requestLogin = new HttpPost(url+"/"+"GetReason");
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
	            
	            requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            HttpResponse response = httpClient.execute(requestLogin);
	            
	            HttpEntity entity = response.getEntity();
	            String content = EntityUtils.toString(entity);
	            
	            reasonList = parseReasonTypeResponse(content);
	            	     
	        } catch (Exception e) {
	            e.printStackTrace();
	            
	        }
		 return reasonList;
	}
	
	public ArrayList<Reason> parseReasonTypeResponse(String response){
		 ArrayList<Reason> reasonList = new ArrayList<Reason>();
		 
		try{
			InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(is);
	        Element root = doc.getDocumentElement();
	        NodeList resultItem = root.getElementsByTagName("Result");
	        String result = resultItem.item(0).getFirstChild().getNodeValue();
	        
	        if(result.equals("0")){		
	        	
				NodeList reasonNodeList = root.getElementsByTagName("ReasonList");
				for(int i = 0; i<reasonNodeList.item(0).getChildNodes().getLength(); i++){
					
					Node nNode = reasonNodeList.item(0).getChildNodes().item(i);
					
					 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						 Reason reason = new Reason();
			                Element eElement = (Element) nNode;
			                NodeList reasonNode = eElement.getChildNodes();
			                for(int x =0; x < reasonNode.getLength(); x++)
			                {
			                	if(reasonNode.item(x).getNodeName().equalsIgnoreCase("ID")){
			                		reason.setId(reasonNode.item(x).getFirstChild().getNodeValue());
			                	}else if(reasonNode.item(x).getNodeName().equalsIgnoreCase("UserCode")){
			                		reason.setUserCode(reasonNode.item(x).getFirstChild().getNodeValue());
			                	}
			                	else if(reasonNode.item(x).getNodeName().equalsIgnoreCase("Description")){
			                		reason.setDescription(reasonNode.item(x).getFirstChild().getNodeValue());
			                	}else if(reasonNode.item(x).getNodeName().equalsIgnoreCase("DetailText")){
			                		reason.setDetailText(reasonNode.item(x).getFirstChild().getNodeValue());
			                	}
			                }
			                reasonList.add(reason);
					 }
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return reasonList;
	}
	
	public ArrayList<VoucherType> parseVoucherTypeResponse(String response){
		 ArrayList<VoucherType> voucherTypes = new ArrayList<VoucherType>();
		 
		try{
			InputStream is = new ByteArrayInputStream(response.getBytes("UTF-8"));
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(is);
	        Element root = doc.getDocumentElement();
	        NodeList resultItem = root.getElementsByTagName("Result");
	        String result = resultItem.item(0).getFirstChild().getNodeValue();
	        
	        if(result.equals("0")){		
	        	
				NodeList voucherNodeList = root.getElementsByTagName("VoucherList");
				for(int i = 0; i<voucherNodeList.item(0).getChildNodes().getLength(); i++){
					
					Node nNode = voucherNodeList.item(0).getChildNodes().item(i);
					
					 if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						 VoucherType voucherType = new VoucherType();
			                Element eElement = (Element) nNode;
			                NodeList voucherList = eElement.getChildNodes();
			                for(int x =0; x < voucherList.getLength(); x++)
			                {
			                	if(voucherList.item(x).getNodeName().equalsIgnoreCase("ID")){
			                		voucherType.setId(Integer.parseInt(voucherList.item(x).getFirstChild().getNodeValue()));
			                	}else if(voucherList.item(x).getNodeName().equalsIgnoreCase("VoucherName")){
			                		voucherType.setName(voucherList.item(x).getFirstChild().getNodeValue());
			                	}
			                	else if(voucherList.item(x).getNodeName().equalsIgnoreCase("VoucherPrice")){
			                		voucherType.setPrice(BigDecimal.valueOf(Double.parseDouble(voucherList.item(x).getFirstChild().getNodeValue())));
			                	}
			                }
			                voucherTypes.add(voucherType);
					 }
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return voucherTypes;
	}
}
