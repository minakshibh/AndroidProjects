package com.karaoke.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.karaoke.util.Song.VideosInAlbum;

import android.content.Context;
import android.util.Log;

public class UnzipUtil {

	private String zipFile;
	 private String location;
	 private Song album;
	 Context mcontext;
	 int count;
	  int id;
	 public UnzipUtil(String zipFile, String location,Song album1,Context context)
	 {
	  this.zipFile = zipFile;
	  this.location = location;
	  album=album1;
	  mcontext=context;
	  dirChecker("");
	 
	  System.out.println("album id"+album.getAlbumId()+"  songs "+album.getSongs());
	  Log.e("Album url ", album.getSongTumbnailUrl());
	  
	  ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
	   id=db.checkAlbumTrackCode(album.getAlbumcode());
	  if(id==0)
	  {
	  
	  	ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
		datbase.addAlbums(album, null, currentDateTimeString);
		count=0;
	  }
	  else
	  {
		  ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
			String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
			
			datbase.updateAlbumbyTrackcode(album, currentDateTimeString, id);
			count=0;
	  }
	  }
	 public void unzip()
	 {
	  try
	  {
	   FileInputStream fin = new FileInputStream(zipFile);
	   ZipInputStream zin = new ZipInputStream(fin);
	   ZipEntry ze = null;
	   while ((ze = zin.getNextEntry()) != null)
	   {
	    Log.e("Decompress", "Unzipping " + ze.getName());

	    
	    if(ze.isDirectory())
	    {
	     dirChecker(ze.getName());
	    }
	    else
	    {
	    	
	    	String file=ze.getName().replace("/", "");
	    	album.setSongs(count);
	    	count++;
	    	
	    	
	    	
	    	if(id>0)
	    	{
	    		ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
	    		ArrayList<Song> songList=datbase.getAlbumSongs(id);
	    		String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
	    		int songId=songList.get(count).getSongId();
	    		
	    		String albumName=album.getAlbumName();
	    		VideosInAlbum video=album.getSubCategories().get(count);
	    		
	    		datbase.updateAlbumSongsbyTrackcode(video,albumName,currentDateTimeString, location + file, songId, id);
	    		
	    	}
	    	else
	    	{
		    	ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
				datbase.addSongs(album, location + file, currentDateTimeString,"Album");
	    	}
	    	
	    	Log.e("Path ", location +"j-------------j"+ ze.getName());
	    	
	    	
//	    	File file=new File(location );
//	    	file.mkdirs();
	    	
	     FileOutputStream fout = new FileOutputStream(location + file);     

	     byte[] buffer = new byte[8192];
	     int len;
	     while ((len = zin.read(buffer)) != -1)
	     {
	      fout.write(buffer, 0, len);
	     }
	     fout.close();

	     zin.closeEntry();

	    }
	    
	   }
	   zin.close();
	  }
	  catch(Exception e)
	  {
	   Log.e("Decompress", "unzip", e);
	  }
	  
	  

	 }

	 private void dirChecker(String dir)
	 {
	  File f = new File(location + dir);
	  if(!f.isDirectory())
	  {
	   f.mkdirs();
	  }
	 }
}
