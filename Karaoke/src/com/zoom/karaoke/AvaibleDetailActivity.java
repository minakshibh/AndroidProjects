package com.zoom.karaoke;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.karaoke.util.ImageLoaderAvailableSongTumbnail;
import com.karaoke.util.Song;

import com.karaoke.util.AvailableSongAdapter.AsyncForPurchasingSong;
import com.karaoke.util.Song.VideosInAlbum;
import com.karaoke.util.UnzipUtil;
import com.karaoke.util.ZoomKaraokeDatabaseHandler;
import com.zoom.dataController.SingleTon;
import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import com.zoom.xmlResponseHandler.ResponseXmlHandler;


public class AvaibleDetailActivity extends Activity {

	private String AlbumID,NoOFSongs;
	private String AAFileURl,AAAlbumName;
	private String purchasingAlbumUrl;
	Intent in1;
	Song song;
	Song album;
	ImageView image;
	TextView txtTitle,albumName,artistName,id,songname,price,zoomlogo;
	ImageButton buyBtn,back;
	private String ASongUrl,AalbumName,purchasingUrl;
	

	ImageLoaderAvailableSongTumbnail imageLoader;
	int index;
	ProgressDialog pDialog;
	String file_url;
	Context mcontext;
	int downloadId;
	
	boolean flag=true;
	String transationUrl;
	LayoutInflater inflator;
	String albumsDirectory;
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@SuppressLint("NewApi")
	public void onCreate(Bundle b)
	{
		super.onCreate(b);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.detailsong);
		in1=getIntent();
		
		image=(ImageView)findViewById(R.id.image);
		back=(ImageButton)findViewById(R.id.back);
		txtTitle=(TextView)findViewById(R.id.title);
		albumName=(TextView)findViewById(R.id.albumName);
		artistName=(TextView)findViewById(R.id.artistName);
		id=(TextView)findViewById(R.id.id);
		price=(TextView)findViewById(R.id.price);
		zoomlogo=(TextView)findViewById(R.id.zoomLogo);
		
		
		inflator=getLayoutInflater();
		buyBtn=(ImageButton)findViewById(R.id.buyNow);
		songname=(TextView)findViewById(R.id.songName);
		mcontext=this;
		transationUrl=mcontext.getResources().getString(R.string.appurl)+"/InsertTransaction";
		
		image.setOnClickListener(Listener);
		buyBtn.setOnClickListener(Listener);
		back.setOnClickListener(Listener);
		imageLoader=new ImageLoaderAvailableSongTumbnail(this);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
		
		
		
		
		if(in1.hasExtra("type"))
		{
			String type=in1.getStringExtra("type");
			if(type.equalsIgnoreCase("song"))
			{
				song=(Song)in1.getSerializableExtra("ob");
				byte[] imagebyte=null;
				System.out.println(song);
				String imageUrl=song.getSongTumbnailUrl();
				imagebyte=getDecodeUrl(imageUrl);
				index=in1.getIntExtra("index", 0);
				txtTitle.setText("Song");
				if(imagebyte!=null)
				{
					Bitmap bitmap=BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
					image.setImageBitmap(bitmap);
				}
				albumName.setText(song.getArtistName());
				artistName.setText("TrackCode - "+song.getAlbumcode());
				file_url=song.getSongUrl();
				downloadId=song.getSongId();
				songname.setText(song.getAlbumcode());
				songname.setVisibility(View.VISIBLE);
				price.setText("");
				song.getSongUrl();
				song.getSongName();
				if(song.isPurchased()||song.getSongPrice()==0.0){
			
					flag=false;
					buyBtn.setBackgroundResource(R.drawable.download_btn_large);
				}
				else{
					flag=true;
					buyBtn.setBackgroundResource(R.drawable.buy_large);
				}
			}
			else
			{
				album=(Song)in1.getSerializableExtra("ob");
				txtTitle.setText("Album");
				byte[] imagebyte=null;
				String imageUrl=album.getSongTumbnailUrl();
				imagebyte=getDecodeUrl(imageUrl);
				index=in1.getIntExtra("index", 0);
				SingleTon.getInstance().setFrom("albumAdapter");
				if(imagebyte!=null)
				{
					Bitmap bitmap=BitmapFactory.decodeByteArray(imagebyte, 0, imagebyte.length);
					image.setImageBitmap(bitmap);
				}
				
				
				zoomlogo.setVisibility(View.GONE);
				
				albumName.setText(album.getAlbumName());
				artistName.setText(album.getArtistName());
				file_url=album.getSongUrl();
				downloadId=album.getAlbumId();
				
				AlbumID=String.valueOf(album.getAlbumId());
				NoOFSongs=String.valueOf(album.getSongs());
				
				price.setText(album.getSongs()+" "+"Songs"+" ("+album.getSongs()+" Credits)");
				
				if(album.isPurchased()||album.getSongPrice()==0.0){
					flag=false;
					buyBtn.setBackgroundResource(R.drawable.download_btn_large);
				}
				else{
					flag=true;
					buyBtn.setBackgroundResource(R.drawable.buy_large);
				}
				
				LinearLayout layout=(LinearLayout)findViewById(R.id.llayout);
				
				layout.setVisibility(View.VISIBLE);
				ArrayList<VideosInAlbum> listofVideos=new ArrayList<Song.VideosInAlbum>();
				listofVideos=album.getSubCategories();
				Log.e("Size ", "  "+ listofVideos.size());
				for(int i=0;i<listofVideos.size();i++)
				{
					
					final VideosInAlbum video=listofVideos.get(i);
					View view=inflator.inflate(R.layout.availablesongcustom, null);
					
					TextView txtArtist=(TextView)view.findViewById(R.id.artistName);
					TextView txtSongName=(TextView)view.findViewById(R.id.songName);
					TextView txtbuy=(TextView)view.findViewById(R.id.buyNow);
					ImageView image=(ImageView)view.findViewById(R.id.image);
					TextView trackCode=(TextView)view.findViewById(R.id.trackCode);
					TextView price=(TextView)view.findViewById(R.id.price);
					
					
					RelativeLayout.LayoutParams layoutParams =(RelativeLayout.LayoutParams)txtbuy.getLayoutParams();
					layoutParams.addRule(RelativeLayout.CENTER_VERTICAL); //ALIGN_PARENT_RIGHT / LEFT etc.
					txtbuy.setLayoutParams(layoutParams);
//					price.setVisibility(View.VISIBLE);
					
					trackCode.setText("Track code : "+video.getVideoTrackCode());
					
					
					txtSongName.setText(video.getVideoName());
				
					
					txtArtist.setText(video.getArtistName());
					txtbuy.setBackgroundResource(0);
					txtbuy.setTextColor(Color.WHITE);
					Log.e("Track code ", ""+video.getDuration());
					
					txtbuy.setText(video.getDuration());
					
					LinearLayout.LayoutParams	params =new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
					 params.setMargins(0, 0, 0, 0);
					 
					 view.setLayoutParams(params);
					layout.addView(view);
					
					if(i<listofVideos.size()-1)
					{
						LayoutParams lparams = new LayoutParams(
								   LayoutParams.FILL_PARENT, 1);
								TextView tv=new TextView(mcontext);
								tv.setLayoutParams(lparams);
								tv.setBackgroundColor(Color.WHITE);
								layout.addView(tv);
					}
				
					
					image.setBackgroundResource(R.drawable.play_song_icon);
					image.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							// TODO Auto-generated method stub
							
							Song songTemp=new Song();
							
							songTemp.setSongName(video.getVideoName());
							songTemp.setSDcardPath(video.getSampleVideoUrl());
							ArrayList<Song> songlist = new ArrayList<Song>();
							songTemp.setSDcardPath(video.getSampleVideoUrl());
							songlist.add(songTemp);
							Intent in=new Intent(mcontext,PlayScreen.class);
							
							in.putExtra("singlesong", "singlesong");
							in.putExtra("ob", songTemp);
							in.putExtra("count", 1);
							in.putExtra("songlist", songlist);
							
							mcontext.startActivity(in);
							
						}
					});
					
				}
				
				
			}
			
			
			
			/*if(in1.hasExtra("Flag"))
			{
				
				flag=in1.getBooleanExtra("Flag", true);
				price.setText("Free");
			
				buyBtn.setBackgroundResource(R.drawable.download_btn_large);
//				buyBtn.set
				
				
				
			}*/
			
		}
		
	}
	OnClickListener Listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId()) 
			{
				case R.id.buyNow:
					
					if(flag)
					{
						if(txtTitle.getText().toString().equalsIgnoreCase("Song"))
						{
							
	
							/*Intent intent=new Intent(mcontext,PaymentActivity.class);
							intent.putExtra("Type", "Song");
							intent.putExtra("Ob", song);
							intent.putExtra("position", index);
							mcontext.startActivity(intent);
							new DownloadSongFromURL().execute(file_url,songname.getText().toString());*/
							
							if(SingleTon.getInstance().getUserCredit().equals("0")){							
								
								final AlertDialog alertdialog=new AlertDialog.Builder(AvaibleDetailActivity.this).create();
								
								alertdialog.setTitle("Zoom Karaoke");
								alertdialog.setMessage("You don't have enough credits to buy this track.");
								alertdialog.setButton2("Buy Credits", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
									
									mcontext.startActivity(new Intent(AvaibleDetailActivity.this,CreditActivity.class));
									finish();	
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
							
							final AlertDialog alertdialog=new AlertDialog.Builder(AvaibleDetailActivity.this).create();
							
							alertdialog.setTitle("Zoom Karaoke");
							alertdialog.setMessage("Do you want to purchase this track?");
							alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
								
									
									System.out.println("hello");
									purchasingUrl="http://www.zoomkaraokeapp.co.uk/service.asmx/InsertVideoTransaction" 
											+"?user_email="+SingleTon.getInstance().getUserEmailID()
											+"&VideoID="+song.getSongId()
											+"&CreditUsed=1";						
									
									new AsyncForPurchasingSong().execute();					
									
									
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
						else
						{
							
							//Intent in=new Intent(mcontext,PaymentActivity.class);							
							//in.putExtra("Type", "Album");
							//in.putExtra("Ob",album);
							//mcontext.startActivity(in);
						    //new DownloadAlbumFromURL().execute(file_url,album.getAlbumName());
							
							if(SingleTon.getInstance().getUserCredit().equals("0")){							
								
								final AlertDialog alertdialog=new AlertDialog.Builder(AvaibleDetailActivity.this).create();
								
								alertdialog.setTitle("Zoom Karaoke");
								alertdialog.setMessage("You don't have enough credits to buy this album.");
								alertdialog.setButton2("Buy Credits", new DialogInterface.OnClickListener() {
									
									public void onClick(DialogInterface dialog, int which) {
									
									mcontext.startActivity(new Intent(AvaibleDetailActivity.this,CreditActivity.class));
									finish();
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
										finish();	
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
								
							
							final AlertDialog alertdialog=new AlertDialog.Builder(AvaibleDetailActivity.this).create();
							
							alertdialog.setTitle("Zoom Karaoke");
							alertdialog.setMessage("Do you want to purchase this album?");
							alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
								
									
									System.out.println("hello");
									purchasingAlbumUrl="http://www.zoomkaraokeapp.co.uk/service.asmx/InsertVideoTransaction" 
											+"?user_email="+SingleTon.getInstance().getUserEmailID()
											+"&VideoID="+AlbumID
											+"&CreditUsed="+NoOFSongs;						
									
									AAFileURl=file_url;
									AAAlbumName=album.getAlbumName();
									new AsyncForPurchasingAlbum().execute();					
									//new DownloadAlbumFromURL().execute(file_url,album.getAlbumName());
									
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
					}
					else
					{
						if(txtTitle.getText().toString().equalsIgnoreCase("Song"))
						{
							final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
							
							alertdialog.setTitle("Zoom Karaoke");
							alertdialog.setMessage("Do you want to download this song ?");
							alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
								
								public void onClick(DialogInterface dialog, int which) {
								
									ASongUrl=song.getSongUrl();
									AalbumName=song.getSongName();
									new DownloadFileFromURL().execute(song.getSongUrl(),albumName.getText().toString());
									
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
						else
						{
							final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
							
							alertdialog.setTitle("Zoom Karaoke");
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
//					new CheckTransation(downloadId,"123",file_url,songname.getText().toString()).execute();
					break;
				case R.id.back:
					Intent intent=new Intent(AvaibleDetailActivity.this,AvailableSong.class);
					startActivity(intent);
					finish();
					break;
				case R.id.image:
					if(txtTitle.getText().toString().equalsIgnoreCase("Song"))
					{
						ArrayList<Song> songlist = new ArrayList<Song>();
						songlist.add(song);
						Intent in=new Intent(mcontext,PlayScreen.class);
						song.setSDcardPath(song.getSampleVideoUrl());
						in.putExtra("singlesong", "singlesong");
						in.putExtra("ob", song);
						in.putExtra("count", 1);
						in.putExtra("songlist", songlist);
						
						mcontext.startActivity(in);
					}
					break;
				
				}
			
			
		}
	};
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
       
         int sampleSize = calculateInSampleSize(o, SplashScreen.deviceWidth, SplashScreen.deviceHeight);
         
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
	
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		boolean flag=true;
		 File outputFile;
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
			int count;
	        try {
	            URL url = new URL(f_url[0]);
	            
	            String filename=f_url[1];
	            
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
	            
	            File songDirectory = new File("/sdcard/ZoomKaraoke/Songs/");
	         // have the object build the directory structure, if needed.
	            songDirectory.mkdirs();
	         // create a File object for the output file
	         outputFile = new File(songDirectory, filename);
	         // now attach the OutputStream to the file object, instead of a String representation
//	         FileOutputStream fos = new FileOutputStream(outputFile);
	            
	            
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

	            // flushing output
	            output.flush();
	            
	            // closing streams
	            output.close();
	            input.close();
	            
	            
	            
	            
	            
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	flag=false;
	        }
	        
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
		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			
			ZoomKaraokeDatabaseHandler datbase=new ZoomKaraokeDatabaseHandler(mcontext);
			
			int id=datbase.checkSongTrackCode(song.getAlbumcode());
			if(id==0)
			{
				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
				ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
				db.openDataBase();
				db.addSongs(song, outputFile.getAbsolutePath(), currentDateTimeString,"Song");
				song.setSDcardPath(outputFile.getAbsolutePath());
			}
			else
			{
				String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
				ZoomKaraokeDatabaseHandler db=new ZoomKaraokeDatabaseHandler(mcontext);
//				db.openDataBase();
				song.setSDcardPath(outputFile.getAbsolutePath());
				db.updateSongbyTrackcode(song, currentDateTimeString,id);
				
			}
			
			final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
			
			alertdialog.setTitle("Download complete");
			alertdialog.setMessage("Do you want to play this song ?");
			alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
					Intent in=new Intent(mcontext,PlayScreen.class);
					
					Log.e("Path",song.getSDcardPath());
					ArrayList<Song> songlist = new ArrayList<Song>();
					songlist.add(song);
					
					in.putExtra("singlesong", "singlesong");
					in.putExtra("ob", song);
					in.putExtra("count", 1);
					in.putExtra("songlist", songlist);
					((Activity)mcontext).finish();
					mcontext.startActivity(in);
					
				}
			});
			alertdialog.setButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
					Intent intent=new Intent(AvaibleDetailActivity.this,AvailableSong.class);
					startActivity(intent);
					finish();
				}
			});
			alertdialog.show();
			
			
			
			
		}

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
	    
	    SingleTon.getInstance().setFrom("albumAdapter");
		alertdialog.setTitle("Download complete");
		alertdialog.setMessage("Do you want to open this album");
		alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
				
				ZoomKaraokeDatabaseHandler handle=new ZoomKaraokeDatabaseHandler(mcontext);
				int id=handle.getMaxAlbumId();
				Intent in=new Intent(mcontext,AlbumListSong.class);
				in.putExtra("AlbumId", id);
				((Activity)mcontext).finish();
				mcontext.startActivity(in);
				
				
				
			}
		});
		alertdialog.setButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
				SingleTon.getInstance().setFrom("albumAdapter");
				Intent mIntent=new Intent(AvaibleDetailActivity.this,AvailableSong.class);
				mcontext.startActivity(mIntent);
			}
		});
		alertdialog.show();
	  } 
 }
	
	public class AsyncForPurchasingSong extends AsyncTask<String, Void, String> {


		@Override
		protected void onPreExecute() {
			
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
			new DownloadFileFromURL().execute(song.getSongUrl(),song.getSongName());
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
	
	
	public class AsyncForPurchasingAlbum extends AsyncTask<String, Void, String> {


		@Override
		protected void onPreExecute() {
			
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

				HttpResponse httpResponse = httpclient.execute(new HttpGet(purchasingAlbumUrl));
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
			new DownloadAlbumFromURL().execute(file_url,album.getAlbumName());
			SingleTon.getInstance().setUserCredit(result);			
			String a=SingleTon.getInstance().getUserCredit();
			

		}
	}
	public void onBackPressed() {

		Intent intent=new Intent(AvaibleDetailActivity.this,AvailableSong.class);
		startActivity(intent);
		finish();


	}
	
}
