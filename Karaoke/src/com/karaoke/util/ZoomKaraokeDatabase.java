package com.karaoke.util;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



public class ZoomKaraokeDatabase extends SQLiteOpenHelper {

	Context mcontext;
	
	@SuppressLint("NewApi")
//	public CursorWindow cursorWindow;
	SQLiteCursor cursor = null;
//	private static CursorWindow cursorWindow;
	
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "ZoomKaraoke";


	private static final String TABLE_SONG = "Songs";
	
	
	private static final String TABLE_ALBUM_DETAIL = "Albums";
	
	
	
	
	//field for restaurant table
	private static final String Album_Id = "Id";
	private static final String Album_Name = "Name";
	private static final String Album_ImageUrl = "Url";
	private static final String Album_Price = "Price";
	private static final String Album_SongNo = "SongNo";
	private static final String Album_Artist = "Artist_Name";
	private static final String Album_Song = "songs";
	private static final String Album_Sd_Url = "SdCard_Path";
	private static final String Album_Buy_Time = "Album_Buy_Time";
	//field for city table
	private static final String Song_Id = "sId";
	private static final String Song_Name = "Song_Name";
	private static final String Song_Image_Url = "Image_Url";
	private static final String Song_Price = "price";
	private static final String Song_Artist = "Artist_Name";
	private static final String Song_Album = "Album_Name";
	private static final String Song_Sd_Url = "Sd_Url";
	private static final String Song_Buy_Date = "song_buy_date";
	private static final String Type = "type";
	private static final String Total_Songs = "songs";
	
	public ZoomKaraokeDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		mcontext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try
		{
			
		String CREATE_CITY_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_SONG + "("
				+ Song_Id + " INTEGER PRIMARY KEY," + Song_Name + " TEXT," + Song_Image_Url + " TEXT,"
				+ Song_Buy_Date + " TEXT,"+ Song_Sd_Url + " TEXT,"+ Song_Album + " TEXT,"+ Song_Artist + " TEXT,"+ Type + " TEXT,"+ Total_Songs + " INTEGER ,"+ Song_Price + " TEXT" + ")";
		
		
		
//		String CREATE_ALBUM_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_ALBUM + "("
//				+ Album_Id + " INTEGER ," + Album_Name + " TEXT," + Album_ImageUrl + " TEXT,"
//				+ Album_Song + " TEXT,"+ Album_Buy_Time + " TEXT,"+ Album_Sd_Url + " TEXT,"+ Album_Artist + " TEXT,"+ Album_Price + " TEXT ," +Album_SongNo + " INTEGER " + ")";
//		
		
		String CREATE_ALBUM_DETAIL = "CREATE TABLE  if NOT Exists " + TABLE_ALBUM_DETAIL + "("
				+ Album_Id + " INTEGER ," + Album_Name + " TEXT," + Album_ImageUrl + " TEXT,"
				+ Song_Name + " TEXT,"+ Album_Buy_Time + " TEXT,"+ Album_Sd_Url + " TEXT,"+ Album_Artist + " TEXT,"+ Album_Price + " TEXT ," +Album_SongNo + " INTEGER " + ")";
		
		db.execSQL(CREATE_CITY_TABLE);
		db.execSQL(CREATE_ALBUM_DETAIL);
		
		
		
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	public void addsong(Song song,String sdurl,String date,String typestring) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		if(song.getSongId()==0)
			values.put(Song_Id, song.getAlbumId());
		else
			values.put(Song_Id, song.getSongId());
		
		values.put(Song_Name, song.getSongName()); // song Name
		values.put(Song_Image_Url, song.getSongTumbnailUrl()); // song Image
		values.put(Song_Price,""+ song.getSongPrice());	//song Imageurl
		values.put(Song_Album,song.getAlbumName());	//song Imageurl
		values.put(Song_Artist,song.getArtistName());	//song 
		values.put(Type,typestring);
		values.put(Song_Sd_Url,sdurl);	//song Imageurl
		values.put(Song_Buy_Date, date);
		values.put(Total_Songs, song.getSongs());
		db.insert(TABLE_SONG, null, values);  // Inserting Row
		db.close(); // Closing database connection
	}
	public void addAlbumDetail(Song album,String sdurl,String date,String name) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Album_Id, album.getAlbumId());	//song Id
		values.put(Album_Name, album.getAlbumName()); // song Name
		
		values.put(Album_ImageUrl, album.getSongTumbnailUrl()); // song Image
		values.put(Album_Price,""+ album.getSongPrice());	//song Imageurl
		values.put(Album_Sd_Url,sdurl);	//song Imageurl
		values.put(Album_Artist,album.getArtistName());	//song 
		values.put(Album_Buy_Time, date);
		values.put(Song_Name, name);
		values.put(Album_SongNo,album.getSongs());
		long a=db.insert(TABLE_ALBUM_DETAIL, null, values);  // Inserting Row
		System.out.println("insert album "+a);
		db.close(); // Closing database connection
	}
	
	public ArrayList<Song> getAllSong() {
		ArrayList<Song> songList = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT  * FROM Songs";              

		SQLiteDatabase db = this.getReadableDatabase();
	
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try{
			
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			System.out.println("Size "+cursor.getCount());
			
			
			
			if (cursor.moveToFirst()) {
				do {
					
					
					if(cursor.getString(cursor.getColumnIndex(Type)).equalsIgnoreCase("Song"))
					{	
						System.out.println("id "+cursor.getInt(cursor.getColumnIndex(Song_Id)));
						Song song = new Song();
						song.setSongId(cursor.getInt(cursor.getColumnIndex(Song_Id)));  //city id
						song.setAlbumName(cursor.getString(1));					//city name
						song.setArtistName(cursor.getString(cursor.getColumnIndex(Song_Artist)));					//city iamge url
						song.setSongTumbnailUrl(cursor.getString(cursor.getColumnIndex(Song_Image_Url)));
						song.setSDcardPath(cursor.getString(cursor.getColumnIndex(Song_Sd_Url)));//city image
						song.setSongName(cursor.getString(cursor.getColumnIndex(Song_Name)));
						// Adding city to list
						System.out.println("Song list "+songList.size());
						songList.add(song);
					}
					else
					{
						int id=cursor.getInt(cursor.getColumnIndex(Song_Id));
						System.out.println("Id "+id);
						Cursor	cursor1 = (SQLiteCursor) db.query(TABLE_ALBUM_DETAIL, new String[] { 
								Album_Id,Album_Name,Album_Artist,Album_ImageUrl,Song_Name, Album_Sd_Url }, Album_Id + "=?",
								new String[] { String.valueOf(id) }, null, null, null, null);  
						
						if(cursor1.moveToFirst())
						{
							do
							{
								System.out.println("countererer "+cursor1.getCount());
								Song song = new Song();
								
								song.setSongId(cursor1.getInt(0));  //city id
								song.setAlbumName(cursor1.getString(1));					//city name
								song.setArtistName(cursor1.getString(cursor1.getColumnIndex(Album_Artist)));					//city iamge url
								song.setSongTumbnailUrl(cursor1.getString(cursor1.getColumnIndex(Album_ImageUrl)));
								song.setSDcardPath(cursor1.getString(cursor1.getColumnIndex(Album_Sd_Url)));//city image
								
								
								song.setSongName(cursor1.getString(cursor1.getColumnIndex(Song_Name)));
								
								songList.add(song);
							}
							while(cursor1.moveToNext());
						}
					}
				
				
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return songList;
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (cursor != null)
			{
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return songList;
		}
		

	}
	public ArrayList<Album> getAllAlbums() {
		ArrayList<Album> albumList = new ArrayList<Album>();
		// Select All Query
		String selectQuery = "SELECT * FROM Albums";

		SQLiteDatabase db = this.getReadableDatabase();
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		try{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Album album = new Album();
					System.out.println(cursor.getInt(cursor.getColumnIndex(Album_Id)));
					album.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id))); //id
					album.setAlbumName(cursor.getString(cursor.getColumnIndex(Album_Name))); //
					album.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(Album_ImageUrl)));
					album.setArtistName(cursor.getString(cursor.getColumnIndex(Album_Artist)));
					album.setAlbumsdPath(cursor.getString(cursor.getColumnIndex(Album_Sd_Url)));
					album.setSongs(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Album_Song))));
					album.setSongNo(cursor.getInt(cursor.getColumnIndex(Album_SongNo)));
					System.out.println(" "+album.getArtistName());
					// Adding city to list
					albumList.add(album);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			cursor.close();
			
			// close inserting data from database
			db.close();
			// return city list
			return albumList;
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return albumList;
		}

	}
	public ArrayList<Song> getAllAlbumsUnique(String str) {
		ArrayList<Song> albumList = new ArrayList<Song>();
		// Select All Query
		String selectQuery = "SELECT *  FROM Songs where type='Album' ";

		SQLiteDatabase db = this.getReadableDatabase();
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		try{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Song album = new Song();
					System.out.println("abum id "+cursor.getInt(cursor.getColumnIndex(Song_Id)));
					album.setAlbumId(cursor.getInt(cursor.getColumnIndex(Song_Id))); //id
					album.setAlbumName(cursor.getString(cursor.getColumnIndex(Song_Album))); //
					album.setSongTumbnailUrl(cursor.getString(cursor.getColumnIndex(Song_Image_Url)));
					album.setArtistName(cursor.getString(cursor.getColumnIndex(Song_Artist)));
					album.setSDcardPath(cursor.getString(cursor.getColumnIndex(Song_Sd_Url)));
//					album.setSongs(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Album_Song))));
					album.setSongs(cursor.getInt(cursor.getColumnIndex(Total_Songs)));
					System.out.println(" "+album.getArtistName());
					// Adding city to list
					albumList.add(album);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			cursor.close();
			
			// close inserting data from database
			db.close();
			// return city list
			return albumList;
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return albumList;
		}

	}
	public ArrayList<Album> getAlbumSongs(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Album> albumList = new ArrayList<Album>();
		cursor = (SQLiteCursor) db.query(TABLE_ALBUM_DETAIL, new String[] { 
				Album_Id,Album_Name,Album_Artist, Album_Sd_Url }, Album_Id + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			do {
				
				Album album = new Album();
				
				album.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id))); //id
				album.setAlbumName(cursor.getString(cursor.getColumnIndex(Album_Name))); //
				album.setArtistName(cursor.getString(cursor.getColumnIndex(Album_Artist)));
				album.setAlbumsdPath(cursor.getString(cursor.getColumnIndex(Album_Sd_Url)));
				
				
				albumList.add(album);
			} while (cursor.moveToNext());
		}
		cursor.getWindow().clear();
		cursor.close();
		
		// close inserting data from database
		db.close();
		// return city list
		return albumList;
		
		
		
	}
//	public int updatePlace(int placeId,Place place) {
//		
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		values.put(Place_subcategory, place.getSubcategory());
//		values.put(Place_rating, place.getRating());
//		values.put(Place_category, place.getCategory());
//		values.put(Place_Name, place.getName());
//		values.put(Place_Address, place.getAddress());
//		values.put(Place_ImageUrl, place.getImageUrl());
//		values.put(Place_Description, place.getDescription());
//		values.put(Place_Latitude, place.getLatitude());
//		values.put(Place_Longitude, place.getLongitude());
//		values.put(Place_Image, place.getPlaceImage());
//		// updating row
//		int a=db.update(TABLE_PLACE, values, Place_Id + " = ?",
//				new String[] { String.valueOf(placeId) });
//	
//		
//		db.close();
//		return a;
//		
//	}
//	// Updating single city
//	public int updateCity(int cityId,City city) {
//			SQLiteDatabase db = this.getWritableDatabase();
//
//			ContentValues values = new ContentValues();
//			
//			values.put(City_Name, city.getCityName());
//			values.put(City_Url, city.getCityUrl());
//			values.put(City_Image, city.getCityimage());
//
//			
//			// updating row
//			int a=db.update(TABLE_City, values, City_Id + " = ?",
//					new String[] { String.valueOf(cityId) });
//		
//			db.close();
//			return a;
//			
//		}
//	//wether restaurant and NightClub
//	
//	
//	public ArrayList<Place>  getTypePlaceList(String placetype,String cityid)
//	{
//		
//		ArrayList<Place> restaurantList = new ArrayList<Place>();
//		SQLiteDatabase db = this.getReadableDatabase();
//		/*if (cursor != null && !(cursor.isClosed()))
//			cursor.close();
//		*/
//		try{
//			/*cursorWindow = new CursorWindow("CursorWindow");
//			cursorWindow.clear();
//			cursor.setWindow(cursorWindow);*/
//			
//			// Select All Query
//			cursor = (SQLiteCursor) db.query(TABLE_PLACE, new String[] { 
//					Place_Id,Place_subcategory,Place_rating,Place_category,Place_Name,Place_Address,Place_ImageUrl ,Place_Description,Place_Latitude,Place_Longitude,Place_Image }, 
//					Place_category + "=?" +" AND "+ City_Id + "=?",
//			
//					new String[] { String.valueOf(placetype),String.valueOf(cityid) }, null, null, null, null);
//			
//			
//				
//			// looping through all rows and adding to list
//			if (cursor.moveToFirst()) {
//				do {
//					Place place=new Place();
//					place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
//					place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory)));
//					place.setRating(cursor.getInt(cursor.getColumnIndex(Place_rating)));
//					place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
//					place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
//					place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
//					place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
//					place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
//					place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
//					place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
//					place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
//					
//					
//					// Adding city to list
//					restaurantList.add(place);
//				} while (cursor.moveToNext());
//			}
//			cursor.getWindow().clear();
//			cursor.close();
//			// close inserting data from database
//			db.close();
//			
//			// return city list
//			return restaurantList;
//		}catch(Exception e) {
//			if (cursor != null){
//				cursor.getWindow().clear();
//				cursor.close();
//			}
//			e.printStackTrace();
//			db.close();
//			return null;
//		}
//		
//	}
//	public ArrayList<Place>  getNightClubsOnlyList()
//	{
//		
//		ArrayList<Place> nightclubList = new ArrayList<Place>();
//		// Select All Query
//		String selectQuery = "SELECT * FROM Place where Category=NightClub";
//
//		SQLiteDatabase db = this.getReadableDatabase();
////		Cursor cursor =null;
//		
//		try{
//			cursor = (SQLiteCursor)db.rawQuery(selectQuery, null);
//			// looping through all rows and adding to list
//			if (cursor.moveToFirst()) {
//				do {
//					Place place=new Place();
//					place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
//					place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory)));
//					place.setRating(cursor.getInt(cursor.getColumnIndex(Place_rating)));
//					place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
//					place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
//					place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
//					place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
//					place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
//					place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
//					place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
//					place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
//					
//					// Adding city to list
//					nightclubList.add(place);
//				} while (cursor.moveToNext());
//			}
//			cursor.getWindow().clear();
//			cursor.close();
//			// close inserting data from database
//			db.close();
//			// return city list
//			return nightclubList;
//		}catch (Exception e) {
//			if (cursor != null){
//				cursor.getWindow().clear();
//				cursor.close();
//			}
//			db.close();
//			return null;
//		}
//		
//	}
//	public int updateRating(int placeid,double rate)
//	{
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues values = new ContentValues();
//		
//		values.put(Place_rating, rate);
//		
//
//		
//		// updating row
//		int a=db.update(TABLE_PLACE, values, Place_Id + " = ?",
//				new String[] { String.valueOf(placeid) });
//	
//		db.close();
//		return a;
//	}
	/*
	public void deleteCity(int cid) {
		SQLiteDatabase db = this.getWritableDatabase();

		
		db.delete(TABLE_City,  City_Id + "=?", new String[] { String.valueOf(cid) }) ;
		
		
		db.close(); // Closing database connection
	}
	
	public void deletePlace(int id) {
		SQLiteDatabase db = this.getWritableDatabase();

		db.delete(TABLE_PLACE,  Place_Id + "=?", new String[] { String.valueOf(id) }) ;
		
		
		db.close(); // Closing database connection
	}
	
	
	
	public void addEmail(SupportEmail supportemail) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Email_Id, supportemail.getEmail_id());	//Email Id
		values.put(City_Name, supportemail.getEmail_address()); // Email Address Name
		
		
		db.insert(TABLE_SupportEmail, null, values);  // Inserting Row
		db.close(); // Closing database connection
	}
	public SupportEmail getEmail() {
		SQLiteDatabase db = this.getReadableDatabase();

		cursor = (SQLiteCursor) db.query(TABLE_SupportEmail, new String[] { 
				Email_Id,Email_Address}, City_Id + "=?",
				new String[] { String.valueOf(1) }, null, null, null, null);
		
		if (cursor != null)
			cursor.moveToFirst();
		
		SupportEmail email=new SupportEmail();
		email.setEmail_id(cursor.getInt(cursor.getColumnIndex(Email_Id))); 			//id
		email.setEmail_address(cursor.getString(cursor.getColumnIndex(Email_Address)));	//cityname
									
		
		cursor.getWindow().clear();
		cursor.close();  		//cursor closed
		
		db.close();				//database closed
		return email;
		
	}
	
	
	//adding place in database and city id 
	public void addPlace(Place place,int city_id) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Place_Id, place.getId());           //place id
		values.put(Place_subcategory, place.getSubcategory());   //place subcategory
		values.put(Place_rating, place.getRating());			//place rating
		values.put(Place_category,place.getCategory() );		//place category
		values.put(Place_Name, place.getName());				 //  Name
		values.put(Place_Address, place.getAddress()); 			// Place address
		values.put(Place_ImageUrl, place.getImageUrl());		//place image url
		values.put(Place_Description, place.getDescription());	 //place description
		values.put(Place_Latitude, place.getLatitude());		 //place latitude 
		values.put(Place_Longitude, place.getLongitude());		 //place longitude
		values.put(Place_Image, place.getPlaceImage());			 //place image
		values.put(City_Id, city_id);							//city id for to which this place added
		db.insert(TABLE_PLACE, null, values);					//insert place
		db.close(); // Closing database connection
	}
	/*
	
	
	//Get city by cityid
//	public City getCity(int id) {
//		SQLiteDatabase db = this.getReadableDatabase();
//		/*if (cursor != null && !(cursor.isClosed()))
//			cursor.close();*/
//		
//		try
//		{
//			/*cursorWindow = new CursorWindow("CursorWindow");
//			cursorWindow.clear();
//			cursor.setWindow(cursorWindow);*/
//			cursor = (SQLiteCursor) db.query(TABLE_City, new String[] { 
//					City_Id,City_Name, City_Url,City_Image }, City_Id + "=?",
//					new String[] { String.valueOf(id) }, null, null, null, null);
//			
//			if (cursor != null)
//				cursor.moveToFirst();
//			
//			City city=new City();
//			city.setCityId(cursor.getInt(cursor.getColumnIndex(City_Id))); 			//id
//			city.setCityName(cursor.getString(cursor.getColumnIndex(City_Name)));	//cityname
//			city.setCityUrl(cursor.getString(cursor.getColumnIndex(City_Url)));		//cityurl
//			city.setCityimage(cursor.getBlob(3));									//city image
//			
//			cursor.getWindow().clear();
//			cursor.close();  		//cursor closed
//			
//			db.close();				//database closed
//			return city;			//return city;
//			
//		}
//		catch(Exception e)
//		{
//			if (cursor != null){
//				cursor.getWindow().clear();
//				cursor.close();
//			}
//			db.close();
//			return null;
//		}
//		
//	}
	
}
