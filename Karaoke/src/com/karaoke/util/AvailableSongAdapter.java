package com.karaoke.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import android.os.AsyncTask;
import android.os.Build;
import android.sax.StartElementListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zoom.dataController.SingleTon;
import com.zoom.karaoke.AvailableSong;
import com.zoom.karaoke.CreditActivity;
import com.zoom.karaoke.PlayScreen;
import com.zoom.karaoke.R;
import com.zoom.xmlResponseHandler.CreditXmlHandler;
import com.zoom.xmlResponseHandler.ResponseXmlHandler;
import com.zoom.xmlparser.XmlGetParser;

public class AvailableSongAdapter extends ArrayAdapter<Song>
{

	Context mcontext;
	ProgressDialog pDialog;
	static ArrayList<Song> availableSong;
	LayoutInflater inflator;
	boolean type;
	Song song;
	String transationUrl;
	String videoSongID;
	private String purchasingUrl;
	
	private String AsongUrl,AsongName;
	TextView artistName;
	TextView songName;
	TextView songId;
	TextView buynow;
	TextView price;
	TextView trackCode;
	TextView txtsongprice;
	
	ImageLoaderAvailableSongTumbnail imageLoader;
	public AvailableSongAdapter(Context context, int resource, ArrayList<Song> objects,Boolean type) 
	{
		super(context, resource, objects);
		
		availableSong=objects;
		mcontext=context;
		this.type=type;
		System.out.println("Hello");
		
		
		inflator=((Activity)mcontext).getLayoutInflater();
		transationUrl=mcontext.getResources().getString(R.string.appurl)+"/InsertTransaction";
		imageLoader=new ImageLoaderAvailableSongTumbnail(mcontext.getApplicationContext());
	}
	public View getView(final int position,View convertView,ViewGroup parent)
	{
			if(convertView==null)
				convertView=inflator.inflate(R.layout.availablesongcustom, null);
			
			artistName=(TextView)convertView.findViewById(R.id.artistName);
			songName=(TextView)convertView.findViewById(R.id.songName);
			songId=(TextView)convertView.findViewById(R.id.songId);
			buynow=(TextView)convertView.findViewById(R.id.buyNow);
			price=(TextView)convertView.findViewById(R.id.price);
			trackCode=(TextView)convertView.findViewById(R.id.trackCode);
			txtsongprice=(TextView)convertView.findViewById(R.id.pricesong);
			
			price.setVisibility(View.VISIBLE);
			txtsongprice.setVisibility(View.GONE);
			ImageView image=(ImageView)convertView.findViewById(R.id.image);
			final Song song1=availableSong.get(position);
			artistName.setText(availableSong.get(position).getArtistName());
			songName.setText(availableSong.get(position).getSongName());
			songId.setText(""+availableSong.get(position).getSongId());
			final String file_url=availableSong.get(position).getSongUrl();
		
//			
			
			txtsongprice.setText("£ "+song1.getSongPrice());
			price.setText(song1.getDuration());
			
			
			Log.e("Track code ", song1.getAlbumcode());
			trackCode.setText("Track code : "+song1.getAlbumcode());
			
			

			float priceofsong=song1.getSongPrice();
			
			Log.e("price", ""+priceofsong);
			if(priceofsong==0.0 || song1.isPurchased)
				type=false;
			else
				type = true;
				
			
			txtsongprice.setVisibility(View.GONE);
			if(!type)
			{
				buynow.setBackgroundResource(R.drawable.download_btn);
				
			}else{
				buynow.setBackgroundResource(R.drawable.buy_now_selector);
			}
			
			
//			image.setTag(song1.getSongTumbnailUrl());
//			
//			
//			imageLoader.DisplayImage(song1.getSongTumbnailUrl(),(Activity) mcontext, image);
//			
			image.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					// TODO Auto-generated method stub
					song=availableSong.get(position);
					Log.e("Path",""+song.getSDcardPath());
					ArrayList<Song> songlist = new ArrayList<Song>();
					songlist.add(song);
					Intent in=new Intent(mcontext,PlayScreen.class);
					song.setSDcardPath(availableSong.get(position).getSampleVideoUrl());
					in.putExtra("singlesong", "singlesong");
					in.putExtra("ob", song);
					in.putExtra("count", 1);
					in.putExtra("songlist", songlist);
					
					mcontext.startActivity(in);
					
					
				}
			});
			
			buynow.setOnClickListener(new OnClickListener() {
				
				@SuppressWarnings("deprecation")
				public void onClick(View v) {
					// TODO Auto-generated method stub
					song=song1;
					
					if(!(song1.getSongPrice()==0.0 || song1.isPurchased)) {

						if(SingleTon.getInstance().getUserCredit().equals("0")){							
							
							final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
							
							alertdialog.setTitle("Zoom Karaoke");
							alertdialog.setMessage("You don't have enough credits to buy this track.");
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
						alertdialog.setMessage("Do you want to purchase this track?");
						alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
							
								videoSongID=String.valueOf(song.getSongId());
								System.out.println("hello");
								purchasingUrl="http://www.zoomkaraokeapp.co.uk/service.asmx/InsertVideoTransaction" 
										+"?user_email="+SingleTon.getInstance().getUserEmailID()
										+"&VideoID="+song.getSongId()
										+"&CreditUsed=1";
								
								AsongUrl=song.getSongUrl();
								AsongName=songName.getText().toString();
								new AsyncForPurchasingSong().execute();
								
								//new DownloadFileFromURL().execute(song.getSongUrl(),songName.getText().toString());
								
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
						
						final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
						
						alertdialog.setTitle("Zoom Karaoke");
						alertdialog.setMessage("Do you want to download this song ?");
						alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								videoSongID=String.valueOf(song.getSongId());
								new DownloadFileFromURL().execute(song.getSongUrl(),song.getSongName().toString());
								
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
	                publishProgress(""+(int)(total/1000));	             
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
				
					mcontext.startActivity(in);
					
				}
			});
			alertdialog.setButton("No", new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
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
			new DownloadFileFromURL().execute(AsongUrl,AsongName);
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
