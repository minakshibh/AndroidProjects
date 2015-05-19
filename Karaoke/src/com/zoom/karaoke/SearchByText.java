package com.zoom.karaoke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.karaoke.util.AvailableAlbumAdapter;
import com.karaoke.util.AvailableSongAdapter;
import com.karaoke.util.ConnectionDetector;
import com.karaoke.util.Song;
import com.zoom.dataController.SingleTon;
import com.zoom.karaoke.R;



public class SearchByText extends Activity {

	ImageButton btnBack;
	Button songs,albums;
	 ListView listView;
	 LinearLayout searchLayout;
	Context mcontext;
	ProgressDialog pDialog;
	public static ArrayList<Song> listsong=new ArrayList<Song>();
	public static ArrayList<Song> listalbums=new ArrayList<Song>();
	
	public static AvailableAlbumAdapter	adapteralbum;
	public static AvailableSongAdapter adapter;
	String method_name="GetVideos";
	ImageButton mysongs,searchBarBtn;
	SharedPreferences helper,pref;
	Editor edit;
	EditText editText;
	String method_search_name="Search";
	String searchUrl="";
	ConnectionDetector detector;
	int pagingSong=1,pagingAlbum=1;
	String songTag = "", albumTag = "";
	
	private int value=-1;
	LinearLayout headerLayout;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.search_by_text1);
		
		mcontext=this;
		btnBack=(ImageButton)findViewById(R.id.back);
		songs=(Button)findViewById(R.id.songs);
		albums=(Button)findViewById(R.id.albums);
		mysongs=(ImageButton)findViewById(R.id.mysongs);
		searchLayout=(LinearLayout)findViewById(R.id.searchlayout);
		
		searchBarBtn=(ImageButton)findViewById(R.id.searchBtn);
		editText=(EditText)findViewById(R.id.searchText);
		pref=mcontext.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		headerLayout=(LinearLayout)findViewById(R.id.headerlayout);
		headerLayout.setVisibility(View.GONE);
		listsong.clear();
		listalbums.clear();
		 edit=pref.edit();
		
		 detector=new ConnectionDetector(mcontext);
		btnBack.setOnClickListener(Listener);
		songs.setOnClickListener(Listener);
		albums.setOnClickListener(Listener);
		mysongs.setOnClickListener(Listener);
		searchBarBtn.setOnClickListener(Listener);
		
		
		listView=(ListView)findViewById(R.id.listSong);
		listView.setTag("Song");
		
		
		editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {        	
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//					hideKeyboard();
					String str = editText.getText().toString().trim();
					
					if(str.equalsIgnoreCase(""))
					{
						showDialog("Internet connection not available");
					}
					else
					{
						
						String tag=listView.getTag().toString();
						
						if(detector.isConnectingToInternet())
						{
							new SearchAvailableList().execute(tag,str);
							InputMethodManager imm = (InputMethodManager)getSystemService(
								      Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);	
						}
						else
						{
							showDialog("Internet connection not available");
						}
					}
		        	 return true;
		        }
				return false;
			} });
        
		
		
		helper=getSharedPreferences("helper", MODE_PRIVATE);
	
		searchUrl=getResources().getString(R.string.appurl)+"/"+method_search_name;
		
		
		detector=new ConnectionDetector(mcontext);
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				if(listView.getTag().toString().equalsIgnoreCase("Song"))
				{
					showToast("song");
					Song song=listsong.get(arg2);
					System.out.println("Song "+song);
					Intent in=new Intent(mcontext,AvaibleDetailActivity.class);
					helper.edit().putString("type", "Song").commit();
					in.putExtra("type", "song");
					in.putExtra("ob", song);
					in.putExtra("index", arg2);
					startActivity(in);
				}
				else
				{
//					showToast("albums");
					
					Song album=listalbums.get(arg2);
					Intent in=new Intent(mcontext,AvaibleDetailActivity.class);
					in.putExtra("type", "album");
					in.putExtra("ob", album);
					in.putExtra("index", arg2);
					Log.e("Sizessss", ""+album.getSubCategories().size());
					helper.edit().putString("type", "Album").commit();
					startActivity(in);
					
				}
				
			}
		});
		
		
	}
	public void onResume()
	{
		super.onResume();
		
		String str=pref.getString("song", null);
		try
		{
			if(str!=null)
			{
				int a=pref.getInt("songId", -1);
				if(a>-1)
				{
					
					System.out.println(AvailableSong.listsong.remove(a));
					adapter.notifyDataSetChanged();
					edit.putInt("songId", -1);
					edit.putString("song", null);
					edit.commit();
				}
				 a=pref.getInt("albumId", -1);
				 if(a>-1)
				 {
					 
					 	listalbums.remove(a);
						adapteralbum.notifyDataSetChanged();
						edit.putInt("albumId", -1);
					 edit.putString("song", null);
					 edit.commit();
					 
				 }
			}
		}
		catch(Exception e)
		{
			
		}
		
		
		
	}
	OnClickListener Listener=new OnClickListener() {
		
		@SuppressWarnings("deprecation")
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			int whitecolorcode=getResources().getColor(R.color.white);
			int blackcolorcode=getResources().getColor(R.color.black);
			switch (v.getId())
			{
				case R.id.back:
				
					Intent intent=new Intent(SearchByText.this,AvailableSong.class);
					startActivity(intent);
					finish();
					
					break;
				case R.id.songs:
					songs.setBackgroundResource(R.drawable.tw2_left);
					songs.setTextColor(blackcolorcode);
					
					albums.setBackgroundResource(R.drawable.tb2_right);
					albums.setTextColor(whitecolorcode);
					System.out.println(listsong.size());
					
					String song_search_text=editText.getText().toString();
					
					if(!song_search_text.equalsIgnoreCase("") && !song_search_text.equalsIgnoreCase(songTag))
					{	
						listsong.clear();
						songTag = song_search_text;						
						new SearchAvailableList().execute("Song",song_search_text);
					}else{
						adapter=new AvailableSongAdapter(mcontext, android.R.layout.simple_list_item_1, listsong,true);
						listView.setTag("Song");
						listView.setAdapter(adapter);
					}
					
					break;
				case R.id.albums:
					songs.setBackgroundResource(R.drawable.tb2_left);
					songs.setTextColor(whitecolorcode);
					
					albums.setBackgroundResource(R.drawable.tw2_right);
					albums.setTextColor(blackcolorcode);
					
					String album_search_text=editText.getText().toString();
					
					if(!album_search_text.equalsIgnoreCase("") && !album_search_text.equalsIgnoreCase(albumTag))
					{	
						listalbums.clear();
						albumTag = album_search_text;						
						new SearchAvailableList().execute("Album",album_search_text);
					}else{
						listView.setTag("Album");
						adapteralbum=new AvailableAlbumAdapter(mcontext, android.R.layout.simple_list_item_1, listalbums,true);
						listView.setAdapter(adapteralbum);
					}
					
					break;
				case R.id.mysongs:
					
					if(headerLayout.getVisibility()==View.VISIBLE)
						headerLayout.setVisibility(View.GONE);
					else
						headerLayout.setVisibility(View.VISIBLE);
					break;
				
				case R.id.searchBtn:
					if(detector.isConnectingToInternet())
					{
						if(value==1){							
							editText.setText("");
							searchBarBtn.setBackgroundResource(R.drawable.search_bar_icon_selector);
							value=-1;
							return;
						}
						
						String tag=listView.getTag().toString();
						
						String search_text=editText.getText().toString();
						
						if(!search_text.equalsIgnoreCase(""))
						{	
							if(tag.equalsIgnoreCase("Song"))
							{
								listsong.clear();
								songTag = search_text;
							}
							else
							{
								listalbums.clear();
								albumTag = search_text;
							}
							
							new SearchAvailableList().execute(tag,search_text);
						}
						else
						{
							showDialog("Please enter search term");
						}
					}
					else
					{
						showDialog("Internet connection not available");
						
					}
						
					break;
			}
			
			
		}
	};
	
	class SearchAvailableList extends AsyncTask<String, String, String>
	{
		ArrayList<Song> songlist=new ArrayList<Song>();
		ArrayList<Song> albumlist;
		String type;
		Map<String,String> map;
		int loadMoreSong=0;
		public void onPreExecute()
		{
			pDialog = new ProgressDialog(mcontext);
			pDialog.setMessage("Please wait...");
//	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
		
			try
			{
				
				type=params[0];
				
				Log.e("type ", type);
				InputStream is;
				
				TelephonyManager tManager = (TelephonyManager)mcontext.getSystemService(Context.TELEPHONY_SERVICE);
		        String uuid = tManager.getDeviceId();
				
		        
				
		        if(type.equalsIgnoreCase("Song"))
				{
					
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
			        HttpPost requestLogin = new HttpPost(searchUrl);
			         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			         nameValuePairs.add(new BasicNameValuePair("trigger","videos"));
			        
			         nameValuePairs.add(new BasicNameValuePair("paging",""+pagingSong));
			         
			         nameValuePairs.add(new BasicNameValuePair("user_email", SingleTon.getInstance().getUserEmailID()));
			         nameValuePairs.add(new BasicNameValuePair("searchParameter", params[1]));
			   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				     HttpResponse response = httpClient.execute(requestLogin);
				     HttpEntity entity = response.getEntity();
				     is= entity.getContent();
				     
				     
					 	String isString =    "<DocumentElement>" + convertStreamToString(is).split("<DocumentElement>")[1] ;
						Log.e("response",is.toString());
						
						InputStream stream = new ByteArrayInputStream(isString.getBytes());
				     
					songlist=com.karaoke.util.SAXXMLParser.parseSong(stream);
					map=com.karaoke.util.SAXXMLParser.map;
					loadMoreSong=Integer.parseInt(map.get("HaveRecords"));
					System.out.println("sizes songs "+songlist.size());
					listsong=songlist;
				}
				else
				{
					
					DefaultHttpClient httpClient = new DefaultHttpClient();
			        HttpPost requestLogin = new HttpPost(searchUrl);
			        
			       
			       
			         List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			         nameValuePairs.add(new BasicNameValuePair("trigger", "albums"));
			         nameValuePairs.add(new BasicNameValuePair("paging",""+pagingAlbum));
			         nameValuePairs.add(new BasicNameValuePair("user_email", SingleTon.getInstance().getUserEmailID()));
			         nameValuePairs.add(new BasicNameValuePair("searchParameter", params[1]));
			   	     requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				     HttpResponse response = httpClient.execute(requestLogin);
				     HttpEntity entity = response.getEntity();
				     is= entity.getContent();
				     
				 	String isString =    "<DocumentElement>" + convertStreamToString(is).split("<DocumentElement>")[1] ;
					Log.e("response",is.toString());
					
				
					
					InputStream stream = new ByteArrayInputStream(isString.getBytes());
				     
				     albumlist= com.karaoke.util.SAXXMLParser.parseAlbum(stream);
				     
				     map=com.karaoke.util.SAXXMLParser.map;
				     loadMoreSong=Integer.parseInt(map.get("HaveRecords"));
				    System.out.println("Song count"+ albumlist.get(0).getSongs());
				    
					 System.out.println("sizes albums "+albumlist.size());
					 
					 for(int i=0;i<albumlist.size();i++)
						 Log.e("Lenght", ""+albumlist.get(i).getAlbumId());
					listalbums=albumlist;
				}
				 
				
				
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		@Override
	    protected void onPostExecute(String str)
		{
	    	
	    	pDialog.dismiss();
	    	View footerView =null;	    
	    	value=1;
	    	searchBarBtn.setBackgroundResource(R.drawable.green_cross);
	    	if(listView.getFooterViewsCount()==0 && loadMoreSong>0)
	    	{
		    	 footerView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.footerhelp, null);
				listView.addFooterView(footerView);
	    	}
	    	
	    	if(type.equalsIgnoreCase("Song"))
	    	{
		    	 adapter=new AvailableSongAdapter(mcontext, android.R.layout.simple_list_item_1, listsong,true);
				listView.setTag("Song");
		    	 listView.setAdapter(adapter);
	    	}
	    	else
	    	{
	    		System.out.println("Album");
	    		adapteralbum=new AvailableAlbumAdapter(mcontext, android.R.layout.simple_list_item_1, listalbums,true);
	    		listView.setTag("Album");
	    		listView.setAdapter(adapteralbum);
	    		
	    	}
	    	
	    	if(loadMoreSong ==0)
	    	{
	    		if(footerView!=null)
	    			listView.removeFooterView(footerView);
	    	}
		}
	}
	public void onFooterClick(View v)
	{
		String tag=listView.getTag().toString();
		
		if(tag.equalsIgnoreCase("Song"))
		{
			pagingSong++;
			new SearchAvailableList().execute("Song");
		}
		else
		{
			pagingAlbum++;
			new SearchAvailableList().execute("Album");
		}
		
	}
	public String convertStreamToString(InputStream is) {
	    try {
	        return new java.util.Scanner(is).useDelimiter("\\A").next();
	    } catch (java.util.NoSuchElementException e) {
	        return "";
	    }
	}
	public void showToast(String str)
	{
		Toast.makeText(mcontext, str, 1000).show();
	}
	@SuppressWarnings("deprecation")
	public void showDialog(String str)
	{
		final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
		
		alertdialog.setTitle("Zoom Karaoke");
		alertdialog.setMessage(str);
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
			}
		});
		alertdialog.show();
		
	}
	
	public void onMySongClick(View v)
	{
		Intent in=new Intent(SearchByText.this,MySongs.class);
		startActivity(in);
		finish();
	}
	
	public void onTopSellerClick(View v)
	{
		SingleTon.getInstance().setFrom("onTopSellerClick");
		Intent mIntent=new Intent(SearchByText.this,AvailableSong.class);
		startActivity(mIntent);	
		finish();
	}

	public void onRequestSongClick(View v){
		
		Intent intent=new Intent(SearchByText.this,RequestSongActivity.class);
		startActivity(intent);
		finish();

	}

	public void onCreditsClick(View v)
	{
		Intent intent=new Intent(SearchByText.this,CreditActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void onFreeSongClick(View v)
	{

		SingleTon.getInstance().setFrom("onFreeSongClick");
		Intent mIntent=new Intent(SearchByText.this,AvailableSong.class);
		startActivity(mIntent);	
		finish();
		
		

	}
	
public void onBackPressed() {
		
		Intent intent=new Intent(SearchByText.this,AvailableSong.class);
		startActivity(intent);
		finish();
			
		
	}
}
