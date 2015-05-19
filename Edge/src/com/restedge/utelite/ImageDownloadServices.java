package com.restedge.utelite;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.restedge.util.City;
import com.restedge.util.RestaurantDatabase;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;

public class ImageDownloadServices extends Service{

    SharedPreferences preferences;

    ArrayList<City> cities;
  
   RestaurantDatabase db;
    
     String downloadUrl;
    
     // Handler that receives messages from the thread
    


   @Override
       public void onCreate() {
        
           db=new RestaurantDatabase(this);
           
           System.out.println("service method");
          cities=db.getAllCities();
           
           for(int i=0;i<cities.size();i++)
           {
        	   
        	   City city=cities.get(i);
        	   
        	   
        	  
        	   if(city.getCityimage()==null)
        	   {
        		   
        		   UpdateMyResults update=new UpdateMyResults(city);
            	   update.start();
        		  
        	   }
        	   
           }
        

       }

   //This thread is update database by city
   private class UpdateMyResults extends Thread{

       City city;
      
       boolean stopThread=false;
       public UpdateMyResults(City city) {
		// TODO Auto-generated constructor stub
    	   
    	   this.city=city;
    	   db=new RestaurantDatabase(ImageDownloadServices.this);
	}
	@Override
       public void run() {
           while (!stopThread){
        	   try
        	   {
               byte[] image= getBitmap(city.getCityUrl());
			  
			
			  city.setCityimage(image);
			  
			  
			 int a= db.updateCity(city.getCityId(), city);
			  
			 if(a>0)
			 {
			  stopThread=true;
			  db.close();
			 }
        	   }catch(Exception e){
        		   System.out.println(e);
        		   db.close();
        		   stopThread=true;
        	   }
			  
           }//end while
       }//end run
   }//

   

   

    @Override
     public IBinder onBind(Intent intent) {
         // We don't provide binding, so return null
         return null;
     }
    //this method is used byte from server by url
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
         System.out.println(is);
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

        
         
         
         int sampleSize = calculateInSampleSize(o, 200, 200);
//      
        

         // Decode bitmap with inSampleSize set
         o.inJustDecodeBounds = false;
         o.inSampleSize=sampleSize;
         
	      System.out.println("city service scale factor  "+sampleSize);
         
	       bitmap=BitmapFactory.decodeStream(is2,null,o);
	       System.out.println("city service "+ bitmap);
	      is1.close();
	  		is2.close();
	  		is.close();
	      ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		 
		
		 
		 
		byte imageInByte[] = stream.toByteArray();
		
		
		return imageInByte;
		
		}
		catch(Exception e)
		{
			System.out.println("Error "+e);
			
			return null;
		}
		
		
	}
	//calculate scale factor
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
 
}