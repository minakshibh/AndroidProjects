package com.karaoke.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.karaoke.util.Song.VideosInAlbum;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class ZoomKaraokeDatabaseHandler extends SQLiteOpenHelper{
 
    //The Android's default system path of your application database.
    @SuppressLint("SdCardPath")
	private static String DB_PATH = "/data/data/com.zoom.karaoke/databases/";
 
    private static String DB_NAME = "ZoomKaraoke_db.sqlite";
 
    private SQLiteDatabase myDataBase; 
 
    private final Context myContext;
    
    private static final String TABLE_SONGS = "Songs";
    
	private static final String TABLE_ALBUMS = "Albums";
	
	private static final String TABLE_PLAYLIST = "PlayList";
	
	private static final String TABLE_PLAYLIST_DATA = "PlaylistData";
	
	//field for album table
	private static final String Album_Id = "albumId";
	private static final String Album_Name = "albumName";
	private static final String Album_Thumbnail = "thumbnail";
	private static final String Album_Songs = "numberOfSongs";
	private static final String Album_Artist = "artistName";
	private static final String Album_Buy_Date = "albumBuyDate";
	private static final String ALBUM_Image = "albumImage";
	private static final String ALBUM_Server_Url = "serverUrl";
	private static final String ALBUM_Code = "AlbumCode";
	//field for song table
	private static final String Song_Id = "songId";
	private static final String Song_Name = "songName";
	private static final String Song_Thumbnail = "thumbnail";
	private static final String Song_Artist = "artistName";
	private static final String Song_SdCard_Path = "localUrl";
	private static final String Song_Buy_Date = "songBuyDate";
	private static final String Song_Server_Url = "serverUrl";
	private static final String Song_Image = "songImage";
	private static final String Song_Track_Code = "Trackcode";
	private static final String Song_Duration= "Duration";
	//fields for playlist table
	private String playListName = "playlistName";
		
	//fields for playlistData table
	private String playListId = "playlistId";
	private String playListVideoId = "videoId";
	
	SQLiteCursor cursor;
    
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
	
    public ZoomKaraokeDatabaseHandler(Context context) {
 
    	super(context, DB_NAME, null, 1);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
   
	@Override
	public void onCreate(SQLiteDatabase db)
	{
//		String CREATE_CITY_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_SONGS + "("
//				+ Song_Id + " INTEGER PRIMARY KEY," + Song_Name + " TEXT," + Song_Thumbnail + " TEXT,"
//				+ Song_Buy_Date + " TEXT,"+ Song_SdCard_Path + " TEXT,"+ Song_Album + " TEXT,"+ Song_Artist + " TEXT,"+ Type + " TEXT,"+ Total_Songs + " INTEGER ,"+ Song_Price + " TEXT" + ")";
//		
//		
//		
////		String CREATE_ALBUM_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_ALBUM + "("
////				+ Album_Id + " INTEGER ," + Album_Name + " TEXT," + Album_ImageUrl + " TEXT,"
////				+ Album_Song + " TEXT,"+ Album_Buy_Time + " TEXT,"+ Album_Sd_Url + " TEXT,"+ Album_Artist + " TEXT,"+ Album_Price + " TEXT ," +Album_SongNo + " INTEGER " + ")";
////		
//		
//		String CREATE_ALBUM_DETAIL = "CREATE TABLE  if NOT Exists " + TABLE_ALBUM_DETAIL + "("
//				+ Album_Id + " INTEGER ," + Album_Name + " TEXT," + Album_ImageUrl + " TEXT,"
//				+ Song_Name + " TEXT,"+ Album_Buy_Time + " TEXT,"+ Album_Sd_Url + " TEXT,"+ Album_Artist + " TEXT,"+ Album_Price + " TEXT ," +Album_SongNo + " INTEGER " + ")";
//		
//		db.execSQL(CREATE_CITY_TABLE);
//		db.execSQL(CREATE_ALBUM_DETAIL);
		
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
 
	}
	
	public void deleteSong(int id){
		
		SQLiteDatabase db = this.getWritableDatabase();
		
			db.delete(TABLE_SONGS, Song_Id +"="+id , null);			
			
		
	}
	
	public void addSongs(Song song,String sdcardPath,String date,String type) {
		SQLiteDatabase db = this.getWritableDatabase();

		if(type.equalsIgnoreCase("Song"))
		{
			ContentValues values = new ContentValues();
			
			values.put(Song_Track_Code, song.getAlbumcode());
			values.put(Song_Duration, song.getDuration());
			values.put(Song_Name, song.getSongName()); 				// song Name
			values.put(Song_Thumbnail, song.getSongTumbnailUrl()); 	// song Image
			values.put(Album_Name,song.getAlbumName());				//song Imageurl
			values.put(Song_Artist,song.getArtistName());			//song 
			values.put(Song_SdCard_Path,sdcardPath);							//song Imageurl
			values.put(Song_Buy_Date, date);
			
			values.put(Song_Server_Url, song.getSongUrl());
			values.put(Song_Image, song.getSongImage());
			values.put(Album_Id, song.getAlbumId());
			db.insert(TABLE_SONGS, null, values);  // Inserting Row
		
		
//		db.close(); // Closing database connection
		}
		else
		{
			
			
			ContentValues values = new ContentValues();
			final SQLiteStatement stmt = db
		            .compileStatement("SELECT MAX(albumId) FROM Albums");

		    
			int a = 0;
			a=(int) stmt.simpleQueryForLong();
			
			int soungcount=song.getSongs();
			
			Log.e("soungcount", ""+soungcount);
			Log.e("SubCategories",""+ song.getSubCategories().size());
			
			VideosInAlbum video=song.getSubCategories().get(soungcount);
			
			values.put(Song_Track_Code, video.getVideoTrackCode());
			values.put(Song_Duration,	video.getDuration());
			values.put(Song_Name, video.getVideoName()); 				// song Name
			values.put(Song_Thumbnail, video.getThumbnailUrl()); 	// song Image
			values.put(Album_Name,song.getAlbumName());				//song Imageurl
			values.put(Song_Artist,video.getArtistName());			//song 
			values.put(Song_SdCard_Path,sdcardPath);							//song Imageurl
			values.put(Song_Buy_Date, date);
			values.put(Song_Server_Url, video.getVideoUrl());
			values.put(Song_Image, song.getSongImage());
			values.put(Album_Id, a);
			db.insert(TABLE_SONGS, null, values);  // Inserting Row
			db.close(); // Closing database connection
		}
	}
	public int checkSongTrackCode(String str)
	{
		
		SQLiteDatabase db=getReadableDatabase();
		String sqlQuery="SELECT DISTINCT Trackcode FROM Songs";
		
		SQLiteCursor subCursor = (SQLiteCursor) db.query(TABLE_SONGS, new String[] { 
				Song_Id }, Song_Track_Code + "=?",
				new String[] {str }, null, null, null, null);
		
		
		
		
		
		Log.e("Track cursor", " "+subCursor.moveToFirst());
		if(subCursor.moveToFirst())
		{
			int id=subCursor.getInt(0);
			db.close();
			subCursor.close();
			return id;
		}
		else
		{
			db.close();
			subCursor.close();
			return 0;
		}
		
	}
	public int checkAlbumTrackCode(String str)
	{
		
		SQLiteDatabase db=getReadableDatabase();
		
		
		SQLiteCursor subCursor = (SQLiteCursor) db.query(TABLE_ALBUMS, new String[] { 
				Album_Id }, ALBUM_Code + "=?",
				new String[] {str }, null, null, null, null);
		
		
		
		Log.e("Track cursor", " "+subCursor.moveToFirst());
		if(subCursor.moveToFirst())
		{
			int id=subCursor.getInt(0);
			db.close();
			subCursor.close();
			return id;
		}
		else
		{
			db.close();
			subCursor.close();
			return 0;
		}
		
	}
	public int updateAlbumbyTrackcode(Song album,String date,int  songId)
	{
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(Album_Name, album.getAlbumName()); 
		values.put(Album_Thumbnail, album.getSongTumbnailUrl());
		values.put(Album_Songs, album.getSongs());
		values.put(Album_Artist, album.getArtistName());
		values.put(Album_Buy_Date, date);
		values.put(ALBUM_Image, album.getSongImage());
		values.put(ALBUM_Server_Url, album.getSongUrl());
		values.put(ALBUM_Code, album.getAlbumcode());
		// updating row
		int a=db.update(TABLE_ALBUMS, values, Album_Id + " = ?",
				new String[] { String.valueOf(songId) });
	
		db.close();
		
		Log.e("Update ", ""+a);
		return a;
		
		
	}
	
	public int updateAlbumSongsbyTrackcode(VideosInAlbum video,String albumName,String date,String sdpath,int  songId,int albumId)
	{
		
		
		
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		
		
		ContentValues values = new ContentValues();
		
		
		
		values.put(Song_Track_Code, video.getVideoTrackCode());
		values.put(Song_Duration,	video.getDuration());
		values.put(Song_Name, video.getVideoName()); 				// song Name
		values.put(Song_Thumbnail, video.getThumbnailUrl()); 	// song Image
		values.put(Album_Name,albumName);				//song Imageurl
		values.put(Song_Artist,video.getArtistName());			//song 
		values.put(Song_SdCard_Path,sdpath);							//song Imageurl
		values.put(Song_Buy_Date, date);
		values.put(Song_Server_Url, video.getVideoUrl());
		
		values.put(Album_Id, albumId);
		// updating row
		int a=db.update(TABLE_SONGS, values, Song_Id+ " = ?",
				new String[] { String.valueOf(songId) });
	
		db.close();
		
		Log.e("Update ", ""+a);
		return a;
		
		
	}
	public int updateSongbyTrackcode(Song song,String date,int  songId)
	{
		
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(Song_Track_Code, song.getAlbumcode());
		values.put(Song_Duration, song.getDuration());
		values.put(Song_Name, song.getSongName()); 				// song Name
		values.put(Song_Thumbnail, song.getSongTumbnailUrl()); 	// song Image
		values.put(Album_Name,song.getAlbumName());				//song Imageurl
		values.put(Song_Artist,song.getArtistName());			//song 
		values.put(Song_SdCard_Path,song.getSDcardPath());							//song Imageurl
		values.put(Song_Buy_Date, date);
		
		values.put(Song_Server_Url, song.getSongUrl());
		values.put(Song_Image, song.getSongImage());
		values.put(Album_Id, song.getAlbumId());

		// updating row
		int a=db.update(TABLE_SONGS, values, Song_Id + " = ?",
				new String[] { String.valueOf(songId) });
	
		db.close();
		
		Log.e("Update ", ""+a);
		return a;
		
		
	}
	public void addAlbums(Song album,String sdcardPath,String date) {
		SQLiteDatabase db = this.getWritableDatabase();

		
		ContentValues values = new ContentValues();
		values.put(Album_Name, album.getAlbumName()); 
		values.put(Album_Thumbnail, album.getSongTumbnailUrl());
		values.put(Album_Songs, album.getSongs());
		values.put(Album_Artist, album.getArtistName());
		values.put(Album_Buy_Date, date);
		values.put(ALBUM_Image, album.getSongImage());
		values.put(ALBUM_Server_Url, album.getSongUrl());
		values.put(ALBUM_Code, album.getAlbumcode());
		db.insert(TABLE_ALBUMS, null, values);  // Inserting Row
		db.close(); // Closing database conn
		
	}
	
	public void createPlayList(String playlistName) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(playListName, playlistName); 
		
		db.insert(TABLE_PLAYLIST, null, values);  // Inserting Row
		db.close(); // Closing database conn
	}
	
	public void removePlaylist(String playlistId){
		SQLiteDatabase db = this.getWritableDatabase();
	    
		db.delete(TABLE_PLAYLIST, "id = ?",
	            new String[] {playlistId});
	    
	    db.close();
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
					
						System.out.println("id "+cursor.getInt(cursor.getColumnIndex(Song_Id)));
						Song song = new Song();
						song.setSongId(cursor.getInt(cursor.getColumnIndex(Song_Id)));  
						song.setAlbumName(cursor.getString(cursor.getColumnIndex(Album_Name)));					//city name
						song.setArtistName(cursor.getString(cursor.getColumnIndex(Song_Artist)));					//city iamge url
						song.setSongTumbnailUrl(cursor.getString(cursor.getColumnIndex(Song_Thumbnail)));
						song.setSDcardPath(cursor.getString(cursor.getColumnIndex(Song_SdCard_Path)));//city image
						song.setSongName(cursor.getString(cursor.getColumnIndex(Song_Name)));
						song.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id)));
						song.setAlbumcode(cursor.getString(cursor.getColumnIndex(Song_Track_Code)));
						// Adding city to list
						System.out.println("Song list "+songList.size());
						songList.add(song);
					
				
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
					album.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(Album_Thumbnail)));
					album.setArtistName(cursor.getString(cursor.getColumnIndex(Album_Artist)));
					album.setAlbumImage(cursor.getBlob(cursor.getColumnIndex(ALBUM_Image)));
					album.setSongs(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Album_Songs))));
					
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
	
	public ArrayList<PlayList> getAllPlaylists() {
		ArrayList<PlayList> playLists = new ArrayList<PlayList>();
		
		// Select all records from PlayList table
		String selectQuery = "SELECT * FROM "+TABLE_PLAYLIST+" order by id desc";

		SQLiteDatabase db = this.getReadableDatabase();
		
		try{
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					PlayList playlist = new PlayList();
					playlist.setId(cursor.getInt(cursor.getColumnIndex("id")));
					playlist.setPlayListName(cursor.getString(cursor.getColumnIndex(playListName)));
					
					playLists.add(playlist);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			
			// close cursor and database.
			cursor.close();
			db.close();
			
			return playLists; // return a list of existing playlists
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return playLists;
		}

	}
	public void addSongToPlayList(int playlist_id,int song_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(playListId, playlist_id); 
		values.put(playListVideoId, song_id); 
		db.insert(TABLE_PLAYLIST_DATA, null, values);  // Inserting Row
		db.close(); // Closing database conn
		
	}
	public void removeSongFromPlayList(int playlist_id,int song_id)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		int a=db.delete(TABLE_PLAYLIST_DATA, playListId +"=? AND "+ playListVideoId +"=? ", new String[] { String.valueOf(playlist_id), String.valueOf(song_id)});
		
		Log.e("Row delete", ""+a);
	}
	
	public ArrayList<Song> getAlbumSongs(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Song> albumList = new ArrayList<Song>();
		cursor = (SQLiteCursor) db.query(TABLE_SONGS, new String[] { 
				Song_Id,Album_Id,Album_Name,Album_Artist,Song_SdCard_Path,Song_Thumbnail,Song_Image,Song_Track_Code,Song_Name }, Album_Id + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		
		if (cursor.moveToFirst()) {
			do {
				
				Song song = new Song();
				song.setSongId(cursor.getInt(cursor.getColumnIndex(Song_Id)));
				song.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id))); //id
				song.setAlbumName(cursor.getString(cursor.getColumnIndex(Album_Name))); //
				song.setArtistName(cursor.getString(cursor.getColumnIndex(Album_Artist)));
				song.setSDcardPath(cursor.getString(cursor.getColumnIndex(Song_SdCard_Path)));
				song.setSongTumbnailUrl(cursor.getString(cursor.getColumnIndex(Song_Thumbnail)));
				song.setSongImage(cursor.getBlob(cursor.getColumnIndex(Song_Image)));
				song.setAlbumcode(cursor.getString(cursor.getColumnIndex(Song_Track_Code)));
				song.setSongName(cursor.getString(cursor.getColumnIndex(Song_Name)));
				albumList.add(song);
			} while (cursor.moveToNext());
		}
		cursor.getWindow().clear();
		cursor.close();
		
		// close inserting data from database
		db.close();
		// return city list
		return albumList;
	}
	
	public ArrayList<Song> getPlayListSongs(int id)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<Song> songList = new ArrayList<Song>();
		
		cursor = (SQLiteCursor) db.query(TABLE_PLAYLIST_DATA, new String[] { 
				playListVideoId}, playListId + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		
		
		if (cursor.moveToFirst()) {
			do {
				int songId = cursor.getInt(cursor.getColumnIndex(playListVideoId));
				SQLiteCursor subCursor = (SQLiteCursor) db.query(TABLE_SONGS, new String[] { 
						Song_Id,Album_Id,Song_Name,Album_Name,Album_Artist,Song_SdCard_Path,Song_Thumbnail,Song_Image,Song_Track_Code }, Song_Id + "=?",
						new String[] { String.valueOf(songId) }, null, null, null, null);
				
				if (subCursor.moveToFirst()) {
					do {
						
						Song song = new Song();
						song.setSongId(subCursor.getInt(subCursor.getColumnIndex(Song_Id)));
						song.setAlbumId(subCursor.getInt(subCursor.getColumnIndex(Album_Id))); //id
						song.setAlbumName(subCursor.getString(subCursor.getColumnIndex(Album_Name))); //
						song.setArtistName(subCursor.getString(subCursor.getColumnIndex(Album_Artist)));
						song.setSDcardPath(subCursor.getString(subCursor.getColumnIndex(Song_SdCard_Path)));
						song.setSongTumbnailUrl(subCursor.getString(subCursor.getColumnIndex(Song_Thumbnail)));
						song.setSongImage(subCursor.getBlob(subCursor.getColumnIndex(Song_Image)));
						song.setAlbumcode(subCursor.getString(subCursor.getColumnIndex(Song_Track_Code)));
						song.setSongName(subCursor.getString(subCursor.getColumnIndex(Song_Name)));
						songList.add(song);
					} while (subCursor.moveToNext());
				}
				subCursor.getWindow().clear();
				subCursor.close();
				
			} while (cursor.moveToNext());
		}
		cursor.getWindow().clear();
		cursor.close();
		
		// close inserting data from database
		db.close();
		// return city list
		return songList;
	}
	
	public Song getSongById(int id) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		cursor = (SQLiteCursor) db.query(TABLE_SONGS, new String[] { 
				Song_Id,Song_Name,Song_Thumbnail,Song_SdCard_Path,Song_Image,Album_Id}, Song_Id + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);            

		Song song =  new Song(); ;
		try{
			
			
			if (cursor.moveToFirst()) {
				do {
					
						System.out.println("id "+cursor.getInt(cursor.getColumnIndex(Song_Id)));
						
						song.setSongId(cursor.getInt(cursor.getColumnIndex(Song_Id)));  
						song.setSongName(cursor.getString(cursor.getColumnIndex(Song_Name)));					//city name
											
						song.setSongTumbnailUrl(cursor.getString(cursor.getColumnIndex(Song_Thumbnail)));
						song.setSDcardPath(cursor.getString(cursor.getColumnIndex(Song_SdCard_Path)));//city image
						song.setSongImage(cursor.getBlob(cursor.getColumnIndex(Song_Image)));
						song.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id)));
						// Adding city to list
					
				
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return song;
			
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
			return song;
		}
		

	}
	public int getMaxAlbumId()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		final SQLiteStatement stmt = db
	            .compileStatement("SELECT MAX(albumId) FROM Albums");

	    
		int a = 0;
		a=(int) stmt.simpleQueryForLong();
		db.close();  //optional
		return a;
	}
	public Album getAlbumById(int id) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		cursor = (SQLiteCursor) db.query(TABLE_ALBUMS, new String[] { 
				Album_Id,Album_Thumbnail,ALBUM_Image}, Album_Id + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);            

		Album album =  new Album(); ;
		try{
			
			
			if (cursor.moveToFirst()) {
				do {
					
						
						album.setAlbumId(cursor.getInt(cursor.getColumnIndex(Album_Id)));  
											//city name
											
						album.setThumbnailUrl(cursor.getString(cursor.getColumnIndex(Album_Thumbnail)));
						
						album.setAlbumImage(cursor.getBlob(cursor.getColumnIndex(ALBUM_Image)));
						
						// Adding city to list
						
				
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return album;
			
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
			return album;
		}
		

	}
	public int updateSongImage(int songId,Song song) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(Song_Image, song.getSongImage());

		// updating row
		int a=db.update(TABLE_SONGS, values, Song_Id + " = ?",
				new String[] { String.valueOf(songId) });
	
		db.close();
		return a;
		
	}
	public int updateAlbumImage(int albumId,Album album) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(ALBUM_Image, album.getAlbumImage());

		// updating row
		int a=db.update(TABLE_ALBUMS, values, Album_Id + " = ?",
				new String[] { String.valueOf(albumId) });
	
		db.close();
		return a;
		
	}
	
	 public void createDataBase() throws IOException{
		 
	    	boolean dbExist = checkDataBase();
	 
	    	if(dbExist){
	    		//do nothing - database already exist
	    	}else{
	 
	    		//By calling this method and empty database will be created into the default system path
	               //of your application so we are gonna be able to overwrite that database with our database.
	        	this.getReadableDatabase();
	 
	        	try {
	 
	    			copyDataBase();
	 
	    		} catch (IOException e) {
	 
	        		throw new Error("Error copying database");
	 
	        	}
	    	}
	 
	    }
	 
	    /**
	     * Check if the database already exist to avoid re-copying the file each time you open the application.
	     * @return true if it exists, false if it doesn't
	     */
	    private boolean checkDataBase(){
	 
	    	SQLiteDatabase checkDB = null;
	 
	    	try{
	    		String myPath = DB_PATH + DB_NAME;
	    		File f = new File(myPath);
				if (f.exists())
					return true;
				else
					return false;
	    		
	 
	    	}catch(SQLiteException e){
	 
	    		e.printStackTrace();
	    		return false;
	 
	    	}
	 
	    	
	 
//	    	return checkDB != null ? true : false;
	    }
	 
	    /**
	     * Copies your database from your local assets-folder to the just created empty database in the
	     * system folder, from where it can be accessed and handled.
	     * This is done by transfering bytestream.
	     * */
	    private void copyDataBase() throws IOException{
	 
	    	//Open your local db as the input stream
	    	InputStream myInput = myContext.getAssets().open(DB_NAME);
	 
	    	// Path to the just created empty db
	    	String outFileName = DB_PATH + DB_NAME;
	 
	    	//Open the empty db as the output stream
	    	OutputStream myOutput = new FileOutputStream(outFileName);
	 
	    	//transfer bytes from the inputfile to the outputfile
	    	byte[] buffer = new byte[1024];
	    	int length;
	    	while ((length = myInput.read(buffer))>0){
	    		myOutput.write(buffer, 0, length);
	    	}
	 
	    	//Close the streams
	    	myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
	 
	    }
	 
	    public void openDataBase() throws SQLException{
	 
	    	//Open the database
	        String myPath = DB_PATH + DB_NAME;
	    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	 
	    }
	 
	    @Override
		public synchronized void close() 
	    {
	 
	    	    if(myDataBase != null)
	    		    myDataBase.close();
	 
	    	    super.close();
	 
		}
	 
}