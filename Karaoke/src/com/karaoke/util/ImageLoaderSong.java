package com.karaoke.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.zoom.karaoke.R;
import com.zoom.karaoke.SplashScreen;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;



public class ImageLoaderSong {

	

	    //the simplest in-memory cache implementation. This should be replaced with something like SoftReference or BitmapOptions.inPurgeable(since 1.6)
	private Map<String, Bitmap> cache=Collections.synchronizedMap(
            new LinkedHashMap<String, Bitmap>(20,1.5f,true));

	   

	    Context mcontext;
	    private File cacheDir;

	    long limit=1000000;
	    public void setLimit(long new_limit){
	        limit=new_limit;
	        
	        Log.i("Memort", "MemoryCache will use up to "+limit/1024./1024.+"MB");
	    }
	    public ImageLoaderSong(Context context){
	        //Make the background thead low priority. This way it will not affect the UI performance
	        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);

	        setLimit(Runtime.getRuntime().maxMemory()/4);
	        mcontext=context;
	      
	       
	    }

	    final int stub_id=R.drawable.stub;
	    public void DisplayImage(String id, Activity activity, ImageView imageView)
	    {
	    	System.out.println(id);
	        if(cache.containsKey(id))
	        {
	        	System.out.println("If");
	            imageView.setImageBitmap(cache.get(id));
	        }
	        else
	        {
	        	System.out.println("else");
	            queuePhoto(id, activity, imageView);
	            imageView.setImageResource(R.drawable.stub);
	        }    
	    }

	    private void queuePhoto(String url, Activity activity, ImageView imageView)
	    {
	        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
	      photosQueue.Clean(imageView);
	        PhotoToLoad p=new PhotoToLoad(url, imageView);
	        synchronized(photosQueue.photosToLoad){
	            photosQueue.photosToLoad.push(p);
	            photosQueue.photosToLoad.notifyAll();
	        }

	        //start thread if it's not started yet
	        if(photoLoaderThread.getState()==Thread.State.NEW)
	            photoLoaderThread.start();
	    }

	    private Bitmap getBitmap(String id) 
	    {
	       
//	    	
	    	Bitmap bitmap = null;
	    	
	    	    try
	    	    {
			    	if(cache.containsKey(id))
			    		return cache.get(id);
	    	
	    	ZoomKaraokeDatabaseHandler handler=new ZoomKaraokeDatabaseHandler(mcontext);
	    	
	    	
	    	 int sid=Integer.parseInt(id);
	    	Song song=handler.getSongById(sid);
	    	
	    	byte image[]=song.getSongImage();
	    	 handler=new ZoomKaraokeDatabaseHandler(mcontext);
	    	 
	    	if(image==null)
	    	{
	    		byte[] imagebyte=null;
	    		 imagebyte=getDecodeUrl(song.getSongTumbnailUrl());
	    		song.setSongImage(imagebyte);
	    		
	    		
	    		handler.updateSongImage(song.getSongId(), song);
	    		bitmap=decodeFile(imagebyte);
	    		return bitmap;
	    	}
	    	else
	    	{
	    		bitmap=decodeFile(image);
	    		 return bitmap;
	    	}
	    	    }
	    	    catch (Throwable ex){
	    	           ex.printStackTrace();
	    	         /*  if(ex instanceof OutOfMemoryError)
	    	               cache.clear();*/
	    	           return null;
	    	        }
				
	    }
	    public byte[] getDecodeUrl(String url)
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
	       
	         int sampleSize = calculateInSampleSize(o,SplashScreen.deviceWidth, SplashScreen.deviceHeight);
	         
	         System.out.println("scale city"+sampleSize);
	         o.inSampleSize = sampleSize;
	         o.inJustDecodeBounds = false;
	        
		      
	         
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
	    //calculate scale factor of image
	    public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {		
			    final int height = options.outHeight;
			    final int width = options.outWidth;
			    int inSampleSize = 1;
			
			    if (height > reqHeight || width > reqWidth) {
			
			        final int heightRatio = Math.round((float) height / (float) reqHeight);
			        final int widthRatio = Math.round((float) width / (float) reqWidth);
			
			        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
			    }
			
			    return inSampleSize;
		}
	    
	    //decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFile(byte[] image){
	        try {
	            //decode image size
	        	BitmapFactory.Options o = new BitmapFactory.Options();
	            o.inJustDecodeBounds = true;
	            Bitmap bitmap=BitmapFactory.decodeByteArray(image, 0, image.length, o);

	            //Find the correct scale value. It should be the power of 2.
	            final int REQUIRED_SIZE=70;
	            int width_tmp=o.outWidth, height_tmp=o.outHeight;
	            System.out.println(o.outWidth);
	            int scale=1;
	            while(true){
	                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
	                    break;
	                width_tmp/=2;
	                height_tmp/=2;
	                scale*=2;
	            }

	            //decode with inSampleSize
	            
	            o.inSampleSize=scale;
	            o.inJustDecodeBounds=false;
	    	
	    	
	    	
	    	 bitmap=BitmapFactory.decodeByteArray(image, 0, image.length, o);
	            return bitmap;
	        } catch (Exception e) {
	        	
	        	return null;
	        
	        }
	       
	    }

	    //Task for the queue
	    private class PhotoToLoad
	    {
	        public String id;
	        public ImageView imageView;
	        public PhotoToLoad(String u, ImageView i){
	            id=u; 
	            imageView=i;
	        }
	    }

	    PhotosQueue photosQueue=new PhotosQueue();

	    public void stopThread()
	    {
	        photoLoaderThread.interrupt();
	    }

	    //stores list of photos to download
	    class PhotosQueue
	    {
	        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();

	        //removes all instances of this ImageView
	        public void Clean(ImageView image)
	        {
	            for(int j=0 ;j<photosToLoad.size();){
	                if(photosToLoad.get(j).imageView==image)
	                    photosToLoad.remove(j);
	                else
	                    ++j;
	            }
	        }
	    }

	    class PhotosLoader extends Thread {
	        public void run() {
	            try {
	                while(true)
	                {
	                    //thread waits until there are any images to load in the queue
	                    if(photosQueue.photosToLoad.size()==0)
	                        synchronized(photosQueue.photosToLoad){
	                            photosQueue.photosToLoad.wait();
	                        }
	                    if(photosQueue.photosToLoad.size()!=0)
	                    {
	                        PhotoToLoad photoToLoad;
	                        synchronized(photosQueue.photosToLoad){
	                            photoToLoad=photosQueue.photosToLoad.pop();
	                        }
	                        Bitmap bmp=getBitmap(photoToLoad.id);
	                        if(bmp!=null)
	                        {
	                        cache.put(photoToLoad.id, bmp);
	                        Object tag=photoToLoad.imageView.getTag();
	                        if(tag!=null && ((String)tag).equals(photoToLoad.id)){
	                        	System.out.println("tag"+tag);
	                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView);
	                            Activity a=(Activity)photoToLoad.imageView.getContext();
	                            a.runOnUiThread(bd);
	                        }
	                        }
	                    }
	                    if(Thread.interrupted())
	                        break;
	                }
	            } catch (InterruptedException e) {
	                //allow thread to exit
	            }
	        }
	    }

	    PhotosLoader photoLoaderThread=new PhotosLoader();

	    //Used to display bitmap in the UI thread
	    class BitmapDisplayer implements Runnable
	    {
	        Bitmap bitmap;
	        ImageView imageView;
	        public BitmapDisplayer(Bitmap b, ImageView i){bitmap=b;imageView=i;}
	        public void run()
	        {
	            if(bitmap!=null)
	                imageView.setImageBitmap(bitmap);
	            else
	                imageView.setImageResource(stub_id);
	        }
	    }

	}

