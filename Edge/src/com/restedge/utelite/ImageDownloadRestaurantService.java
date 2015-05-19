package com.restedge.utelite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


import com.restedge.util.City;
import com.restedge.util.Place;
import com.restedge.util.RestaurantDatabase;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;

public class ImageDownloadRestaurantService extends Service{

	RestaurantDatabase db;
	 ArrayList<Place> places;
	  
	  
	    
	     String downloadUrl;
	     public static boolean serviceState=false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onLowMemory() 
	{
		System.gc();
	}
	@Override
    public void onCreate() {
        serviceState=true;
      
        
        db=new RestaurantDatabase(this);
        
        System.out.println("service method");
       places=db.getAllPlaces();
        
       
       
        for(int i=0;i<places.size();i++)
        {
     	   
     	   Place place=places.get(i);
     	  
     	   if(place.getPlaceImage()==null)
     	   {
     		   
     		   System.out.println("Place Id"+place.getId());
     		   UpdateMyResults update=new UpdateMyResults(place);
         	   update.start();
     		
     	   }
     	   
        }
      }
	//getting bytes from server by url
	public byte[] getBitmap(String url)
	{
		
		try
		{
		 Bitmap bitmap=null;
         URL imageUrl = new URL(url);
         System.out.println(imageUrl);
         HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
         conn.setConnectTimeout(30000);
         conn.setReadTimeout(30000);
         conn.setInstanceFollowRedirects(true);
         InputStream is=conn.getInputStream();
        
//         
        
        
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) > -1 ) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        // Open new InputStreams using the recorded bytes
        // Can be repeated as many times as you wish
        InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
        InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
        
        
		
         BitmapFactory.Options o = new BitmapFactory.Options();
         o.inJustDecodeBounds = true;
       

         BitmapFactory.decodeStream(is1,null,o);
//        
//        System.out.println(o.outWidth);
//         //The new size we want to scale to
//         final int REQUIRED_SIZE=70;
//
//         //Find the correct scale value. It should be the power of 2.
//        int scale;
         //Decode with inSampleSize
       
	         int sampleSize = calculateInSampleSize(o, 200, 200);
	        
	         
	         o.inSampleSize=sampleSize;
	         o.inJustDecodeBounds = false;
	        
		      
//	         final int REQUIRED_SIZE=200;
//	         int width_tmp=o.outWidth, height_tmp=o.outHeight;
//	         int scale=1;
//	         while(true){
//	             if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
//	                 break;
//	             width_tmp/=2;
//	             height_tmp/=2;
//	             scale*=2;
//	         }
	         
	         System.out.println("scale retsurant service"+sampleSize);
	         //decode with inSampleSize
//	         BitmapFactory.Options o2 = new BitmapFactory.Options();
//	         o2.inSampleSize=scale;
	         
	         
	         
		       bitmap=BitmapFactory.decodeStream(is2,null,o);
		       System.out.println(bitmap);
		      is1.close();
		  		is2.close();
		     ByteArrayOutputStream stream = new ByteArrayOutputStream();
			 bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			 
			byte imageInByte[] = stream.toByteArray();
			
			return imageInByte;
		
		}
		catch(Exception e)
		{
			System.out.println("Error "+e);
			if(e!=null)
				System.out.println(e.getMessage());
			return null;
		}
		
		
	}
	 public static int calculateInSampleSize(
			 BitmapFactory.Options options, int reqWidth, int reqHeight) {		
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;
		
		    if (height > reqHeight || width > reqWidth) {

		        final int halfHeight = height / 2;
		        final int halfWidth = width / 2;

		        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
		        // height and width larger than the requested height and width.
		        while ((halfHeight / inSampleSize) > reqHeight
		                && (halfWidth / inSampleSize) > reqWidth) {
		            inSampleSize *= 2;
		        }
		    }

		
		    return inSampleSize;
		}
	 //This thread is used update place in database
	class UpdateMyResults extends Thread
	{

		Place place;
		boolean stopthread=false;
		UpdateMyResults(Place place)
		{
			this.place=place;
			 db=new RestaurantDatabase(ImageDownloadRestaurantService.this);
		}
		public void run() {
			
			 while (!stopthread){
				 
				 try
				 {
	               byte[] image= getBitmap(place.getImageUrl());
				  if(image==null)
					  stopthread=false;
				 System.out.println("byte stream length"+image.length);
				  place.setPlaceImage(image);
				  
				 int a= db.updatePlace(place.getId(), place);
				 if(a>0)
				 {
				 stopthread=true;
				 db.close();
				 }
				 }
				 catch(Exception e){
					 System.out.println(e);
					 stopthread=true;
					 db.close();
				 }
				  
	           }//
			
			
		}
		
	}
	
}
