package com.karaoke.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karaoke.util.AvailableSongAdapter.AsyncForPurchasingSong;
import com.karaoke.util.AvailableSongAdapter.DownloadFileFromURL;
import com.zoom.dataController.SingleTon;
import com.zoom.karaoke.AlbumListSong;
import com.zoom.karaoke.AvailableSong;
import com.zoom.karaoke.CreditActivity;
import com.zoom.karaoke.PaymentActivity;
import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import com.zoom.xmlResponseHandler.ResponseXmlHandler;

public class AvailableAlbumAdapter extends ArrayAdapter<Song>
{

	Context mcontext;
	ProgressDialog pDialog;
	ArrayList<Song> availableAlbum;
	LayoutInflater inflator;
	boolean type;
	Song album;
	SharedPreferences pref;
	Editor edit;
	String transationUrl;
	ImageLoaderAvailableSongTumbnail imageLoader;
	String albumsDirectory="/sdcard/ZoomKaraoke/Albums/";
	private String purchasingUrl;
	private String AlbumName,AlbumUrl;
	public AvailableAlbumAdapter(Context context, int resource, ArrayList<Song> objects,Boolean type) 
	{
		super(context, resource, objects);
		
		availableAlbum=objects;
		mcontext=context;
		this.type=type;
		
		pref=mcontext.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		
		 edit=pref.edit();
		
		
		transationUrl=mcontext.getResources().getString(R.string.appurl)+"/InsertTransaction";
		inflator=((Activity)mcontext).getLayoutInflater();
		imageLoader=new ImageLoaderAvailableSongTumbnail(mcontext.getApplicationContext());
	}
	public View getView(final int position,View convertView,ViewGroup parent)
	{	
			if(convertView==null)
				convertView=inflator.inflate(R.layout.availablealbumscustom, null);
			
			
			TextView artistName=(TextView)convertView.findViewById(R.id.artistName);
			final TextView albumName=(TextView)convertView.findViewById(R.id.albumName);
			TextView albumId=(TextView)convertView.findViewById(R.id.albumId);
			TextView buynow=(TextView)convertView.findViewById(R.id.buyNow);
			TextView price=(TextView)convertView.findViewById(R.id.price);
			TextView songCount=(TextView)convertView.findViewById(R.id.songcount);
			ImageView img=(ImageView)convertView.findViewById(R.id.image);
			TextView txtSongLabel=(TextView)convertView.findViewById(R.id.songLabel);
			price.setVisibility(View.GONE);
			
			
//			RelativeLayout llayout=(RelativeLayout)convertView.findViewById(R.id.llayout);
			
//			View view=View.inflate(mcontext, R.layout.availablesongcustom, null);
			
			
			final Song album1=availableAlbum.get(position);
			artistName.setText(availableAlbum.get(position).getArtistName());
			albumName.setText(availableAlbum.get(position).getAlbumName());
			albumId.setText(""+availableAlbum.get(position).getAlbumId());		
			
			//price.setText("� "+album1.getSongPrice());
			int count = availableAlbum.get(position).getSongs();
			
			
			float priceofsong=album1.getSongPrice();
			
			Log.e("price", ""+priceofsong);
			if(priceofsong==0.0 || album1.isPurchased)
				type = false;
			else
				type = true;
			
			if(count == 1)
				songCount.setText(""+availableAlbum.get(position).getSongs()+ " Song"+" ("+(availableAlbum.get(position).getSongs()+" "+"Credits)"));
			else
				songCount.setText(""+availableAlbum.get(position).getSongs()+ " Songs"+" ("+(availableAlbum.get(position).getSongs()+" "+"Credits)"));
			
			
			
			if(album1.getSongPrice()==0.0 || album1.isPurchased)
			{
				price.setVisibility(View.GONE);

				buynow.setBackgroundResource(R.drawable.download_btn);
				
				RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)buynow.getLayoutParams();
				layoutParams.addRule(RelativeLayout.CENTER_VERTICAL); //ALIGN_PARENT_RIGHT / LEFT etc.
				buynow.setLayoutParams(layoutParams);
				
			}			
			
			
			img.setTag(album1.getSongTumbnailUrl());
			imageLoader.DisplayImage(album1.getSongTumbnailUrl(),(Activity) mcontext, img);
			buynow.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					// TODO Auto-generated method stub
					album=album1;
					
					if(!(album1.isPurchased||album1.getSongPrice()==0))
					{
							if(SingleTon.getInstance().getUserCredit().equals("0")){							
							
							final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
							
							alertdialog.setTitle("Zoom Karaoke");
							alertdialog.setMessage("You don't have enough credits to buy this album.");
							alertdialog.setButton2("Buy Credits", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
								
								mcontext.startActivity(new Intent(mcontext,CreditActivity.class));
								AvailableSong.delegate.finish();	
								}
							});
							alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									alertdialog.dismiss();
									
								}
							});
							alertdialog.show();
							
						}
						else {
							
							if(Integer.valueOf(SingleTon.getInstance().getUserCredit())<(album.getSongs())){							
								
								final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
								
								alertdialog.setTitle("Zoom Karaoke");
								alertdialog.setMessage("You don't have enough credits to buy this album.");
								alertdialog.setButton2("Buy Credits", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
									
									mcontext.startActivity(new Intent(mcontext,CreditActivity.class));
									AvailableSong.delegate.finish();
									}
								});
								alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										alertdialog.dismiss();
										
									}
								});
								alertdialog.show();
							}
							
							else {
						
						final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
						
						alertdialog.setTitle("Zoom Karaoke");
						alertdialog.setMessage("Do you want to purchase this album?");
						alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
							
							
								System.out.println("hello");
								purchasingUrl="http://www.zoomkaraokeapp.co.uk/service.asmx/InsertVideoTransaction" 
										+"?user_email="+SingleTon.getInstance().getUserEmailID()
										+"&VideoID="+availableAlbum.get(position).getAlbumId()
										+"&CreditUsed="+availableAlbum.get(position).getSongs();	
								
								AlbumName=album.getAlbumName();
								AlbumUrl=album.getSongUrl();							
								new AsyncForPurchasingSong().execute();								
								//new DownloadAlbumFromURL().execute(album.getSongUrl(),album.getAlbumName());
								
							}
						});
						alertdialog.setButton("No", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertdialog.dismiss();
								
							}
						});
						alertdialog.show();
						}
					}
					}
					else
					{
						
						
						final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
						
						alertdialog.setTitle("Zoom karaoke");
						alertdialog.setMessage("Do you want to download this Album ?");
						alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								new DownloadAlbumFromURL().execute(album.getSongUrl(),album.getAlbumName());
								
							}
						});
						alertdialog.setButton("No", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								alertdialog.dismiss();
								
							}
						});
						alertdialog.show();
						
						
					}

					
				}
			});
			
		return convertView;
	}
	class DownloadAlbumFromURL extends AsyncTask<String, String, String> {

		File songDirectory;
		File albumDir;
		  File outputFile;
		  String result;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(mcontext);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Downloading file in background thread
		 * */
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@SuppressLint("SdCardPath")
		@Override
		protected String doInBackground(String... f_url) {
			int count = 0;
	        try {
	            URL url = new URL(f_url[0]);
	            
	            String filename=f_url[1];
	            
	            Log.e("url ", f_url[0]);
	            Log.e("name  ", f_url[1]);
	            
	            String extension = MimeTypeMap.getFileExtensionFromUrl(f_url[0]);
	            filename=filename+"."+extension;
	            System.out.println(extension);
	            URLConnection conection = url.openConnection();
	            conection.connect();
	            // getting file length
	           
	            int lenghtOfFile = conection.getContentLength();
	            int max=(lenghtOfFile/1000);
	        	pDialog.setMax(max);
	        	 pDialog.setProgressNumberFormat("%1d/%2d kB");
	            // input stream to read file - with 8k buffer
	            InputStream input = new BufferedInputStream(url.openStream(), 8192);
	            
	           songDirectory = new File("/sdcard/ZoomKaraoke/ZipFolder/");
	         // have the object build the directory structure, if needed.
	            songDirectory.mkdirs();
	           albumsDirectory="/sdcard/ZoomKaraoke/Albums/";
	            
	          
	           
	         // create a File object for the output file
	            outputFile = new File(songDirectory, filename);
	         // now attach the OutputStream to the file object, instead of a String representation
//	         FileOutputStream fos = new FileOutputStream(outputFile);
	            
//	         ZipInputStream zin = new ZipInputStream(input);
	            // Output stream to write file
	            OutputStream output = new FileOutputStream(outputFile);

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
//	                publishProgress(""+(int)((total*100)/lenghtOfFile));
	                publishProgress(""+(int)(total/1000));
	                // writing data to file
	                output.write(data, 0, count);
	            }
	            
	           
	            output.flush();
	            // closing streams
	            output.close();
	            input.close();
	           
	            
	           
	        } catch (Exception e) {
	        	Log.e("Error", "Error ");
	        	e.printStackTrace();
	        	result="false";
	        }
	        result="true";
	        return null;
		}
		
		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
           
       }

		/**
		 * After completing background task
		 * Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			
			if(result.equalsIgnoreCase("true"))
			  {
			   try
			   {
				   unzip(outputFile.getAbsolutePath(),albumsDirectory,album);
//				   Decompress d = new Decompress(outputFile.getAbsolutePath(), songDirectory.getAbsolutePath()); 
//		           System.out.println("compressing");
//				   d.unzip(); 
			   } catch (Exception e)
			   {
//			    // TODO Auto-generated catch block
			    e.printStackTrace();
			   }
//			
//			
			}
		}

	}
	
	public void unzip(String str,String str1,Song album) throws IOException 
	{
	 pDialog = new ProgressDialog(mcontext);
	 pDialog.setMessage("Please Wait...");
	 pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	 pDialog.setCancelable(false);
	 pDialog.show();
	 String StorezipFileLocation=str;
	 String DirectoryName=str1;
	 new UnZipTask().execute(StorezipFileLocation, DirectoryName);
	}


	private class UnZipTask extends AsyncTask<String, Void, Boolean> 
	{
	 @SuppressWarnings("rawtypes")
	 @Override
	 protected Boolean doInBackground(String... params) 
	 {
//	  String filePath = params[0];
//	  String destinationPath = params[1];
//
//	  File archive = new File(filePath);
	  try 
	  {
//	   ZipFile zipfile = new ZipFile(archive);
//	   for (Enumeration e = zipfile.entries(); e.hasMoreElements();) 
//	   {
//	    ZipEntry entry = (ZipEntry) e.nextElement();
//	    unzipEntry(zipfile, entry, destinationPath);
//	   }


		  System.out.println("Album url "+params[1]);
		  
		  
		 
	   UnzipUtil d = new UnzipUtil(params[0], params[1],album,mcontext); 
	   d.unzip();

	  } 
	  catch (Exception e) 
	  {
	   return false;
	  }

	  return true;
	 }

	 @SuppressWarnings("deprecation")
	@SuppressLint("SdCardPath")
	@Override
	 protected void onPostExecute(Boolean result) 
	 {
	  pDialog.dismiss(); 
	  
	  
	  File file=new File("/sdcard/ZoomKaraoke/ZipFolder");
	  if (file.isDirectory()) {
	        String[] children = file.list();
	        for (int i = 0; i < children.length; i++) {
	            new File(file, children[i]).delete();
	        }
	        file.delete();
	    }
	  
	  
	  final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
		
		alertdialog.setTitle("Download complete");
		alertdialog.setMessage("Do you want to open this album");
		alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
				
				ZoomKaraokeDatabaseHandler handle=new ZoomKaraokeDatabaseHandler(mcontext);
//				int id=handle.getMaxAlbumId();
//				
				
				ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
				int   id=db.checkAlbumTrackCode(album.getAlbumcode());
				Intent in=new Intent(mcontext,AlbumListSong.class);
				in.putExtra("AlbumId", id);
				in.putExtra("albumname", album.getAlbumName());
				mcontext.startActivity(in);
				
				
				
			}
		});
		alertdialog.setButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
				SingleTon.getInstance().setFrom("albumAdapter");
				Intent mIntent=new Intent(mcontext,AvailableSong.class);
				mcontext.startActivity(mIntent);
			
			}
		});
		alertdialog.show();
	}
	  
	  
	  
	  
	  
	 }
	 
	public class AsyncForPurchasingSong extends AsyncTask<String, Void, String> {


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			pDialog = new ProgressDialog(mcontext);
			pDialog.setTitle("Loading");
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... urls) {	

			InputStream inputStream = null;
			String result = "";
			try {
				HttpClient httpclient = new DefaultHttpClient();

				HttpResponse httpResponse = httpclient.execute(new HttpGet(purchasingUrl));
				inputStream = httpResponse.getEntity().getContent();	
				if(inputStream != null)
					result = convertInputStreamToString(inputStream);
				else
					result = "Did not work!";			

			} catch (Exception e) {
				Log.d("InputStream", e.getLocalizedMessage());
			}

			return result;
		}

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
			pDialog.dismiss();
			new DownloadAlbumFromURL().execute(AlbumUrl,AlbumName);
			SingleTon.getInstance().setUserCredit(result);			
			String a=SingleTon.getInstance().getUserCredit();

		}
	}
	private String convertInputStreamToString(InputStream inputStream) {	

		String Result="";

		try {
			XMLReader xmlReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();		
			CreditXmlHandler saxHandler = new CreditXmlHandler(); 
			xmlReader.setContentHandler(saxHandler);
			InputSource ins= new InputSource(inputStream);
			ins.setEncoding("UTF-8");
			xmlReader.parse(ins);
			Result = saxHandler.getTotalCredits(); 

		} catch (Exception ex) {
			Log.e("XML", "SAXXMLParser:  failed"+ex.getMessage());
		}			      
		return Result;
	}
	
	}
	

