package com.zoom.karaoke;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.util.Xml.Encoding;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import com.karaoke.util.AvailableAlbumAdapter;
import com.karaoke.util.AvailableSongAdapter;
import com.karaoke.util.ConnectionDetector;
import com.karaoke.util.Song;
import com.karaoke.util.Song.VideosInAlbum;
import com.zoom.authentication.LoginActivity;
import com.zoom.dataController.SingleTon;

public class AvailableSong extends Activity {

	ImageButton btnBack;
	Button songs,albums;
	ListView listView;
	LinearLayout searchLayout;
	Context mcontext;
	ProgressDialog pDialog;
	LinearLayout headerLayout;
	public static ArrayList<Song> listsong=new ArrayList<Song>();
	public static ArrayList<Song> listalbums=new ArrayList<Song>();
	String url;
	public static AvailableAlbumAdapter	adapteralbum;
	public static AvailableSongAdapter adapter;
	String method_name="NewRelease";
	ImageButton mysongs,searchMainBtn;
	SharedPreferences helper,pref;
	Editor edit;
	String tag = "";
	String user_email = "";

	View footerView =null;
	boolean paidtype=true;
	String method_search_name="Search";
	String searchUrl="";
	ConnectionDetector detector;
	int pagingSong=1,pagingAlbum=1;
	AsyncTask<Void, Void, Void> mRegisterTask;
	String uuid ;
	TelephonyManager tManager;
	TextView freeSong;
	int loadMoreSong=0;
	int loadMoreAlbum =0,loadmore=0;
	int paging = 0;
	private TextView freesong;
	private LinearLayout mysonglinear;
	public static Activity delegate;
	private int VALUE=-1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		delegate=this;
		checkNotNull(CommonUtilities.SERVER_URL, "SERVER_URL");
		checkNotNull(CommonUtilities.SENDER_ID, "SENDER_ID");



		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);
		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);
		getUserNameFromSharedPreference();
		setContentView(R.layout.availablesong1);

		freeSong=(TextView)findViewById(R.id.freesong);
		paidtype=true;
		headerLayout=(LinearLayout)findViewById(R.id.headerlayout);

		tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		uuid = tManager.getDeviceId();

		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.zoom.karaoke", 
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("Your Tag", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();

		}

		registerReceiver(mHandleMessageReceiver,
				new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);

		System.out.println("Resister id "+regId);

		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				Log.e("already registered", "already reg");
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						boolean registered =
								ServerUtilities.register(context, uuid, regId);
						// At this point all attempts to register with the app
						// server failed, so we need to unregister the device
						// from GCM - the app will try to register again when
						// it is restarted. Note that GCM will send an
						// unregistered callback upon completion, but
						// GCMIntentService.onUnregistered() will ignore it.
						if (!registered) {
							GCMRegistrar.unregister(context);
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						mRegisterTask = null;
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}



		mcontext=this;
		btnBack=(ImageButton)findViewById(R.id.back);
		songs=(Button)findViewById(R.id.songs);
		albums=(Button)findViewById(R.id.albums);
		mysongs=(ImageButton)findViewById(R.id.mysongs);
		searchLayout=(LinearLayout)findViewById(R.id.searchlayout);
		searchMainBtn=(ImageButton)findViewById(R.id.searchMainBtn);
		mysonglinear=(LinearLayout)findViewById(R.id.mysonglinear);

		/*pref=mcontext.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);

		 edit=pref.edit();
		edit.putString("user_email", user_email);
		edit.commit();*/

		btnBack.setOnClickListener(Listener);
		songs.setOnClickListener(Listener);
		albums.setOnClickListener(Listener);
		mysongs.setOnClickListener(Listener);

		searchMainBtn.setOnClickListener(Listener);

		listView=(ListView)findViewById(R.id.listSong);	

		if(!listsong.isEmpty())
			listsong.clear();
		if(!listalbums.isEmpty())
			listalbums.clear();

		helper=getSharedPreferences("helper", MODE_PRIVATE);
		url=getResources().getString(R.string.appurl)+"/"+method_name;
		searchUrl=getResources().getString(R.string.appurl)+"/"+method_search_name;


		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {


				if(paidtype)
				{
					if(listView.getTag().toString().equalsIgnoreCase("Song"))
					{

						Song song=listsong.get(arg2);
						System.out.println("Song "+song);
						Intent in=new Intent(AvailableSong.this,AvaibleDetailActivity.class);
						helper.edit().putString("type", "Song").commit();
						in.putExtra("type", "song");
						in.putExtra("ob", song);
						in.putExtra("index", arg2);
						startActivity(in);
						finish();
					}
					else
					{
						Song album=listalbums.get(arg2);
						Intent in=new Intent(AvailableSong.this,AvaibleDetailActivity.class);
						in.putExtra("type", "album");
						in.putExtra("ob", album);
						in.putExtra("index", arg2);
						Log.e("Sizessss", ""+album.getSubCategories().size());
						ArrayList<VideosInAlbum> l=album.getSubCategories();
						for(int i=0;i<l.size();i++)
							Log.e("Code ", ""+l.get(i).getVideoTrackCode());
						helper.edit().putString("type", "Album").commit();
						startActivity(in);
						finish();

					}
				}
				else
				{
					if(listView.getTag().toString().equalsIgnoreCase("Song"))
					{

						Song song=listsong.get(arg2);
						System.out.println("Song "+song);
						Intent in=new Intent(AvailableSong.this,AvaibleDetailActivity.class);
						helper.edit().putString("type", "Song").commit();
						in.putExtra("type", "song");
						in.putExtra("ob", song);
						in.putExtra("index", arg2);
						in.putExtra("Flag", false);
						startActivity(in);
						finish();
					}
					else
					{


						Song album=listalbums.get(arg2);
						Intent in=new Intent(AvailableSong.this,AvaibleDetailActivity.class);
						in.putExtra("type", "album");
						in.putExtra("ob", album);
						in.putExtra("index", arg2);
						in.putExtra("Flag", false);
						Log.e("Sizessss", ""+album.getSubCategories().size());
						ArrayList<VideosInAlbum> l=album.getSubCategories();
						for(int i=0;i<l.size();i++)
							Log.e("Code ", ""+l.get(i).getVideoTrackCode());
						helper.edit().putString("type", "Album").commit();
						startActivity(in);
						finish();

					}

				}

			}
		});

		detector=new ConnectionDetector(mcontext);
		if(detector.isConnectingToInternet())
		{		
			listView.setTag("Song");
			tag = "Song";

			try {
				if(SingleTon.getInstance().getFrom().equals("onFreeSongClick")){
					
					SingleTon.getInstance().setFrom("");
					getFreeSongs();					
					return;					
				}

				if(SingleTon.getInstance().getFrom().equals("albumAdapter")){	
					
					SingleTon.getInstance().setFrom("");
					setDataRegardingAlbum();				
					return;

				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			//listView.setTag("Song");
			//tag = "Song";
			//tag = "Song";
			new GetAvailabelList().execute("Paid",String.valueOf(paging));
		}
		else
		{
			showConnectionDialog();

		}



	}
	private void getUserNameFromSharedPreference() {

		pref=AvailableSong.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		user_email=pref.getString("user_email", null);

	}
	@SuppressWarnings("deprecation")
	public void showConnectionDialog()
	{
		final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
		System.out.println("check connection");
		alertdialog.setTitle("Zoom Karaoke");
		alertdialog.setMessage("Internet connection not available");
		alertdialog.setButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				alertdialog.dismiss();
			}
		});
		alertdialog.show();

	}
	public void onResume()
	{
		super.onResume();

		headerLayout.setVisibility(View.GONE);
		String str=pref.getString("song", null);
		if(str!=null)
		{
			int a=pref.getInt("songId", -1);
			if(a>-1 && listsong.size()>0)
			{

				System.out.println(AvailableSong.listsong.remove(a));
				adapter.notifyDataSetChanged();
				edit.putInt("songId", -1);
				edit.putString("song", null);
				edit.commit();
			}
			a=pref.getInt("albumId", -1);
			if(a>-1 && listalbums.size()>0)
			{

				listalbums.remove(a);
				adapteralbum.notifyDataSetChanged();
				edit.putInt("albumId", -1);
				edit.putString("song", null);
				edit.commit();

			}
		}
	}
	OnClickListener Listener=new OnClickListener() {

		@SuppressWarnings("deprecation")
		public void onClick(View v) {

			int whitecolorcode=getResources().getColor(R.color.white);
			int blackcolorcode=getResources().getColor(R.color.black);
			switch (v.getId())
			{
			case R.id.back:

				headerLayout.setVisibility(View.GONE);
				freeSong.setVisibility(View.VISIBLE);
				if(paidtype)
				{
					final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
					System.out.println("check connection");
					alertdialog.setTitle("Zoom Karaoke");
					alertdialog.setMessage("Do you want to close the app?");
					alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							alertdialog.dismiss();
							finish();
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
					btnBack.setBackgroundResource(R.drawable.exit_btn);
					mysonglinear.setVisibility(View.VISIBLE);
					songs.setText("Latest Songs");
					albums.setText("Latest Albums");
					mysongs.setTag(null);
					mysongs.setBackgroundResource(R.drawable.menu_icon);


					if(detector.isConnectingToInternet())
					{
						tag = listView.getTag().toString();
						paidtype=true;
						listsong.clear();
						listalbums.clear();
						new GetAvailabelList().execute("Paid",String.valueOf(paging));
					}
					else
					{
						showConnectionDialog();
					}

				}
				break;
			case R.id.songs:
				songs.setBackgroundResource(R.drawable.tw2_left);
				songs.setTextColor(blackcolorcode);

				albums.setBackgroundResource(R.drawable.tb2_right);
				albums.setTextColor(whitecolorcode);
				System.out.println(listsong.size());

				headerLayout.setVisibility(View.GONE);
				if(paidtype)
				{
					if(listsong.size()>0)
					{

						//							
						adapter=new AvailableSongAdapter(mcontext, android.R.layout.simple_list_item_1, listsong,paidtype);
						listView.setTag("Song");
						listView.setAdapter(adapter);



					}
					else
					{
						if(detector.isConnectingToInternet())
						{
							tag = "Song";
							new GetAvailabelList().execute("Paid",String.valueOf(paging));
						}
						else
						{

							showConnectionDialog();
						}
					}
				}
				else
				{
					if(listsong.size()>0)
					{


						//							
						adapter=new AvailableSongAdapter(mcontext, android.R.layout.simple_list_item_1, listsong,paidtype);
						listView.setTag("Song");
						listView.setAdapter(adapter);



					}
					else
					{

						if(detector.isConnectingToInternet())
						{
							tag = "Song";
							new GetAvailabelList().execute("Free",String.valueOf(paging));
						}
						else
						{
							showConnectionDialog();
						}
					}
				}
				break;
			case R.id.albums:

				setDataRegardingAlbum();

				break;
			case R.id.mysongs:


				if(mysongs.getTag()!=null)
				{
					freesong=(TextView)findViewById(R.id.freesong);
					freesong.setVisibility(View.GONE);
					if(headerLayout.getVisibility()==View.VISIBLE)
						headerLayout.setVisibility(View.GONE);
					else
						headerLayout.setVisibility(View.VISIBLE);
				}
				else
				{
					if(headerLayout.getVisibility()==View.VISIBLE)
						headerLayout.setVisibility(View.GONE);
					else
						headerLayout.setVisibility(View.VISIBLE);

					Log.e("",""+mysongs.getTag());
				}	

				//					Intent in=new Intent(AvailableSong.this,MySongs.class);
				//					startActivity(in);
				break;
			case R.id.searchMainBtn:

				//					Intent intent=new Intent(mcontext,SearchByText.class);
				//					startActivity(intent);
				break;


			}


		}


	};
	class GetAvailabelList extends AsyncTask<String, String, String>
	{
		ArrayList<Song> songlist=new ArrayList<Song>();
		ArrayList<Song> albumlist;
		Map<String,String> map;

		public void onPreExecute()
		{
			pDialog = new ProgressDialog(mcontext);

			if(tag.equalsIgnoreCase("Song"))
			{
				pDialog.setMessage("Loading available songs"+"\n"+" Please wait...");
			}else{
				pDialog.setMessage("Loading albums"+"\n"+" Please wait...");
			}
			//	        pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			try
			{

				InputStream is;
				loadmore =0;
				//				listsong.clear();
				if(tag.equalsIgnoreCase("Song"))
				{

					loadMoreSong=0;

					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost requestLogin = new HttpPost(url);
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("Trigger", "videos"));		
					nameValuePairs.add(new BasicNameValuePair("user_email", user_email));
					nameValuePairs.add(new BasicNameValuePair("type", params[0]));
					nameValuePairs.add(new BasicNameValuePair("paging", params[1]));
					requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(requestLogin);
					HttpEntity entity = response.getEntity();
					is= entity.getContent();

					Log.e("response : ",is.toString());

					songlist=com.karaoke.util.SAXXMLParser.parseSong(is);
					map=com.karaoke.util.SAXXMLParser.map;
					loadMoreSong=Integer.parseInt(map.get("HaveRecords"));
					loadmore = Integer.parseInt(map.get("HaveRecords"));
					System.out.println("sizes songs "+songlist.size());
					listsong.addAll(songlist);
				}
				else
				{
					//					listalbums.clear();

					InputStream mInputStream;
					loadMoreAlbum = 0;
					DefaultHttpClient httpClient = new DefaultHttpClient();
					HttpPost requestLogin = new HttpPost(url);

					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("Trigger", "albums"));				
					nameValuePairs.add(new BasicNameValuePair("user_email", user_email));
					nameValuePairs.add(new BasicNameValuePair("type", params[0]));
					nameValuePairs.add(new BasicNameValuePair("paging", params[1]));
					requestLogin.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = httpClient.execute(requestLogin);
					HttpEntity entity = response.getEntity();
					mInputStream= entity.getContent();
					String isString =  "<DocumentElement>" + convertStreamToString(mInputStream).split("<DocumentElement>")[1] ;
					Log.e("response",mInputStream.toString());

					InputStream stream = new ByteArrayInputStream(isString.getBytes());
					albumlist= com.karaoke.util.SAXXMLParser.parseAlbum(stream);
					//albumlist= com.karaoke.util.SAXXMLParser.parseAlbum(stream);

					map=com.karaoke.util.SAXXMLParser.map;
					loadMoreAlbum=Integer.parseInt(map.get("HaveRecords"));

					loadmore =Integer.parseInt(map.get("HaveRecords"));
					//					 for(int i=0;i<albumlist.size();i++)
					//					 {
					//						 Log.e("Lengh", ""+albumlist.get(i).getSongs());
					//						 ArrayList<VideosInAlbum> son=albumlist.get(i).getSubCategories();
					//						 for(int j=0;j<son.size();j++)
					//							 Log.e("Track code", ""+son.get(j).getVideoTrackCode());
					//						 
					//					 }
					listalbums.addAll(albumlist);
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


			if(listView.getFooterViewsCount()==0 )
			{
				System.out.println("Footer layout ");
				footerView=((Activity)mcontext).getLayoutInflater().inflate(R.layout.footerhelp, null);
				listView.addFooterView(footerView);
			}


			if(tag.equalsIgnoreCase("Song"))
			{
				adapter=new AvailableSongAdapter(mcontext, android.R.layout.simple_list_item_1, listsong,paidtype);
				listView.setTag("Song");
				listView.setAdapter(adapter);
			}
			else
			{
				System.out.println("Album");
				adapteralbum=new AvailableAlbumAdapter(mcontext, android.R.layout.simple_list_item_1, listalbums,paidtype);
				listView.setTag("Album");
				listView.setAdapter(adapteralbum);

			}
			System.out.println("loadMoreSong "+loadMoreSong);
			if(loadmore ==0)
			{
				if(footerView !=null)
				{
					listView.removeFooterView(footerView);
					footerView =null;
				}

			}

			try {
				/*if(SingleTon.getInstance().getFrom().equals("onFreeSongClick")){	
					
					SingleTon.getInstance().setFrom("");
					getFreeSongs();			

				}*/
				if(SingleTon.getInstance().getFrom().equals("onTopSellerClick")){	
					btnBack.setBackgroundResource(R.drawable.back_btn_selector);
					paidtype=false;
					mysonglinear.setVisibility(View.GONE);
					headerLayout.setVisibility(View.GONE);
					SingleTon.getInstance().setFrom("");					
				}
				/*if(SingleTon.getInstance().getFrom().equals("albumAdapter")){					
					setDataRegardingAlbum();
					SingleTon.getInstance().setFrom("");

				}*/
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	public void onFooterClick(View v)
	{
		String tag=listView.getTag().toString();

		if(paidtype)
		{
			if(tag.equalsIgnoreCase("Song"))
			{
				pagingSong++;
				tag = "Song";
				new GetAvailabelList().execute("Paid",String.valueOf(pagingSong));
			}
			else
			{
				pagingAlbum++;
				tag = "Album";
				new GetAvailabelList().execute("Paid",String.valueOf(pagingAlbum));
			}
		}
		else
		{
			if(tag.equalsIgnoreCase("Song"))
			{
				pagingSong++;
				tag = "Song";
				new GetAvailabelList().execute("Free",String.valueOf(pagingSong));
			}
			else
			{
				pagingAlbum++;
				tag = "Album";
				new GetAvailabelList().execute("Free",String.valueOf(pagingAlbum));
			}


		}

	}

	protected void setDataRegardingAlbum() {

		songs.setBackgroundResource(R.drawable.tb2_left);
		songs.setTextColor(getResources().getColor(R.color.white));

		headerLayout.setVisibility(View.GONE);
		albums.setBackgroundResource(R.drawable.tw2_right);
		albums.setTextColor(getResources().getColor(R.color.black));
		listView.setTag("Album");
		if(paidtype)
		{
			if(listalbums.size()>0)
			{



				adapteralbum=new AvailableAlbumAdapter(mcontext, android.R.layout.simple_list_item_1, listalbums,paidtype);

				listView.setAdapter(adapteralbum);




			}
			else
			{

				if(detector.isConnectingToInternet()){
					tag = "Album";
					new GetAvailabelList().execute("Paid",String.valueOf(paging));
				}
				else
				{
					showConnectionDialog();
				}
			}
		}
		else
		{
			if(listalbums.size()>0)
			{



				adapteralbum=new AvailableAlbumAdapter(mcontext, android.R.layout.simple_list_item_1, listalbums,paidtype);

				listView.setAdapter(adapteralbum);





			}
			else
			{

				if(detector.isConnectingToInternet()){
					tag = "Album";
					new GetAvailabelList().execute("Free",String.valueOf(paging));
				}
				else
				{
					showConnectionDialog();
				}
			}

		}



	}
	String convertStreamToString(java.io.InputStream is) {
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

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(
					getString(R.string.error_config, name));
		}
	}

	private final BroadcastReceiver mHandleMessageReceiver =
			new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.e("on receive","hakshdsahkhd");
			//            String newMessage = intent.getExtras().getString("my custom message");
			//            mDisplay.append(newMessage + "\n");
		}
	};

	@Override
	protected void onDestroy() {
		try{
			if (mRegisterTask != null) {
				mRegisterTask.cancel(true);
			}
			unregisterReceiver(mHandleMessageReceiver);
			GCMRegistrar.onDestroy(this);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
	}
	public void onMySongClick(View v)
	{
		Intent in=new Intent(AvailableSong.this,MySongs.class);
		startActivity(in);
		finish();
	}
	public void onSearchClick(View v)
	{
		Intent intent=new Intent(mcontext,SearchByText.class);
		startActivity(intent);
		finish();
	}

	public void onTopSellerClick(View v)
	{
		if (VALUE == 1) {
			headerLayout.setVisibility(View.GONE);
			SingleTon.getInstance().setFrom("onTopSellerClick");
			listView.setTag("Song");
			tag = "Song";
			VALUE=0;
			if(!listsong.isEmpty())
				listsong.clear();
			if(!listalbums.isEmpty())
				listalbums.clear();
			new GetAvailabelList().execute("Paid", String.valueOf(paging));

		}
		else {
			btnBack.setBackgroundResource(R.drawable.back_btn_selector);
			paidtype=false;
			mysonglinear.setVisibility(View.GONE);
			headerLayout.setVisibility(View.GONE);
		}
		
	}

	public void onRequestSongClick(View v){

		Intent intent=new Intent(AvailableSong.this,RequestSongActivity.class);
		startActivity(intent);
		finish();

	}

	public void onCreditsClick(View v)
	{
		Intent intent=new Intent(mcontext,CreditActivity.class);
		startActivity(intent);
		finish();
	}
	public void onLogOutClick(View v)
	{

		pref=AvailableSong.this.getSharedPreferences("MyPref", android.content.Context.MODE_PRIVATE);
		edit=pref.edit();
		edit.putString("user_email", "");
		edit.commit();

		Intent intent=new Intent(AvailableSong.this,LoginActivity.class);
		startActivity(intent);
		finish();


	}
	public void onFreeSongClick(View v)
	{

		getFreeSongs();


	}
	public  void getFreeSongs() {
		VALUE=1;
		headerLayout.setVisibility(View.GONE);
		mysonglinear.setVisibility(View.VISIBLE);
		listsong.clear();
		listalbums.clear();
		Log.e("Tag ", listView.getTag().toString());
		freeSong.setVisibility(View.GONE);
		songs.setText("Free Songs");
		albums.setText("Free Albums");
		mysongs.setTag("mysong");
		//mysongs.setBackgroundResource(R.drawable.my_song_selector);

		adapter.notifyDataSetChanged();
		if(adapteralbum !=null)
			adapteralbum.notifyDataSetChanged();

		btnBack.setBackgroundResource(R.drawable.back_btn_selector);
		Log.e("Tag", listView.getTag().toString());
		//Toast.makeText(AvailableSong.this, listView.getTag().toString(), 0).show();
		paidtype=false;
		new GetAvailabelList().execute("Free",String.valueOf(paging));
	}


	@Override
	public void onBackPressed() {

		headerLayout.setVisibility(View.GONE);
		freeSong.setVisibility(View.VISIBLE);
		if(paidtype)
		{
			final AlertDialog alertdialog=new AlertDialog.Builder(mcontext).create();
			System.out.println("check connection");
			alertdialog.setTitle("Zoom Karaoke");
			alertdialog.setMessage("Do you want to close the app?");
			alertdialog.setButton2("Yes", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					alertdialog.dismiss();
					finish();
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
			btnBack.setBackgroundResource(R.drawable.exit_btn);
			mysonglinear.setVisibility(View.VISIBLE);
			songs.setText("Latest Songs");
			albums.setText("Latest Albums");
			mysongs.setTag(null);
			mysongs.setBackgroundResource(R.drawable.menu_icon);


			if(detector.isConnectingToInternet())
			{
				tag = listView.getTag().toString();
				paidtype=true;
				listsong.clear();
				listalbums.clear();
				new GetAvailabelList().execute("Paid",String.valueOf(paging));
			}
			else
			{
				showConnectionDialog();
			}

		}
	}
}
