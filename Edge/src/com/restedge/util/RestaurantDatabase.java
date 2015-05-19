package com.restedge.util;




import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("NewApi")
public class RestaurantDatabase extends SQLiteOpenHelper {

	Context mcontext;
	
	@SuppressLint("NewApi")
//	public CursorWindow cursorWindow;
	SQLiteCursor cursor = null;
//	private static CursorWindow cursorWindow;
	
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Uteliv";

	// City table name
	private static final String TABLE_City = "City";
	
	//Resaurant table
	private static final String TABLE_PLACE = "Place";
	
	
	//SupportEmail
	private static final String TABLE_SupportEmail = "SupportEmail";
	//SupportEmail table fields
	private static final String Email_Id = "eId";
	private static final String Email_Address = "email_address";
	
	
	//field for restaurant table
	private static final String Place_Id = "Id";
	private static final String Place_Name = "Name";
	private static final String Place_ImageUrl = "Url";
	private static final String Place_Image = "Image";
	private static final String Place_Address = "Address";
	private static final String Place_Description = "Description";
	private static final String Place_Latitude = "Latitude";
	private static final String Place_Longitude = "Longitude";
	private static final String Place_category = "Category";
	private static final String Place_subcategory = "SubCategory";
	private static final String Place_rating = "Rating";
	
	
	//field for city table
	private static final String City_Id = "cId";
	private static final String City_Name = "Name";
	private static final String City_Url = "Url";
	private static final String City_Image = "Image";
	
	
	
	String table_name="City";
	String city_table="create table if not exists City(integer Id,cityName text,cityUrl text,cityImage BLOB)";
	public RestaurantDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		mcontext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try
		{
			//query for create city table 
		String CREATE_CITY_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_City + "("
				+ City_Id + " INTEGER PRIMARY KEY," + City_Name + " TEXT," + City_Url + " TEXT,"
				+ City_Image + " BLOB" + ")";
		//create city table
		db.execSQL(CREATE_CITY_TABLE);
		
		//query for place table
		String CREATE_PLACE_TABLE="CREATE TABLE   if NOT Exists " + TABLE_PLACE + "("
				+ Place_Id + " INTEGER PRIMARY KEY,"+ Place_category +" TEXT," + Place_subcategory +" TEXT,"+Place_rating +" DOUBLE,"+ Place_Name + " TEXT," + Place_Address + " TEXT,"
				+ Place_ImageUrl + " TEXT," +Place_Description+" TExt," +Place_Latitude+" Text,"+Place_Longitude+" Text,"
				+Place_Image+" BLOB,"+City_Id+" Integer)";
		
		//create table for place
		db.execSQL(CREATE_PLACE_TABLE);
		
		
//		String CREATE_EmailTable = "CREATE TABLE  if NOT Exists " + TABLE_SupportEmail + "("
//				+ Email_Id + " INTEGER PRIMARY KEY," + Email_Address + " TEXT " + ")";
//		db.execSQL(CREATE_EmailTable);
		
		
		}
		catch(Exception e){
			System.out.println("create table "+e.getMessage());
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	public void addCity(City city) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(City_Id, city.getCityId());	//City Id
		values.put(City_Name, city.getCityName()); // City Name
		values.put(City_Image, city.getCityimage()); // City Image
		values.put(City_Url, city.getCityUrl());	//City Imageurl
		
		db.insert(TABLE_City, null, values);  // Inserting Row
		db.close(); // Closing database connection
	}
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
	//
	public Place getPlace(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try
		{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
			cursor = (SQLiteCursor) db.query(TABLE_PLACE, new String[] { 
					Place_Id,Place_subcategory,Place_rating,Place_category,Place_Name,Place_Address,Place_ImageUrl ,Place_Description,Place_Latitude,Place_Longitude,Place_Image }, Place_Id + "=?",
					new String[] { String.valueOf(id) }, null, null, null, null);
			if (cursor != null)
				cursor.moveToFirst();
	
			
			Place place=new Place();
			place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
			place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory)));
			place.setRating(cursor.getDouble(cursor.getColumnIndex(Place_rating)));
			place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
			place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
			place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
			place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
			place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
			place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
			place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
			place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
			
			cursor.getWindow().clear();
			cursor.close();
			db.close();
			// return place
			return place;
		}
		catch(Exception e)
		{
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return null;
		}

	}
	//Get city by cityid
	public City getCity(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try
		{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
			cursor = (SQLiteCursor) db.query(TABLE_City, new String[] { 
					City_Id,City_Name, City_Url,City_Image }, City_Id + "=?",
					new String[] { String.valueOf(id) }, null, null, null, null);
			
			if (cursor != null)
				cursor.moveToFirst();
			
			City city=new City();
			city.setCityId(cursor.getInt(cursor.getColumnIndex(City_Id))); 			//id
			city.setCityName(cursor.getString(cursor.getColumnIndex(City_Name)));	//cityname
			city.setCityUrl(cursor.getString(cursor.getColumnIndex(City_Url)));		//cityurl
			city.setCityimage(cursor.getBlob(3));									//city image
			
			cursor.getWindow().clear();
			cursor.close();  		//cursor closed
			
			db.close();				//database closed
			return city;			//return city;
			
		}
		catch(Exception e)
		{
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return null;
		}
		
	}
	public ArrayList<City> getAllCities() {
		ArrayList<City> cityList = new ArrayList<City>();
		// Select All Query
		String selectQuery = "SELECT  * FROM City";              

		SQLiteDatabase db = this.getReadableDatabase();
	
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
//			cursor.getWindow().clear();
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					City city = new City();
					city.setCityId(Integer.parseInt(cursor.getString(0)));  //city id
					city.setCityName(cursor.getString(1));					//city name
					city.setCityUrl(cursor.getString(2));					//city iamge url
					city.setCityimage(cursor.getBlob(3));					//city image
					// Adding city to list
					cityList.add(city);
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return cityList;
			
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			
			db.close();
			return null;
		}
		

	}
	public ArrayList<Place> getAllPlaces() {
		ArrayList<Place> placeList = new ArrayList<Place>();
		// Select All Query
		String selectQuery = "SELECT * FROM Place";

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
					Place place = new Place();
					
					place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
					place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory))); //
					place.setRating(cursor.getDouble(cursor.getColumnIndex(Place_rating)));
					place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
					place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
					place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
					place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
					place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
					place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
					place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
					place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
					
					
					// Adding city to list
					placeList.add(place);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			cursor.close();
			
			// close inserting data from database
			db.close();
			// return city list
			return placeList;
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return null;
		}

	}
	public int updatePlace(int placeId,Place place) {
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(Place_subcategory, place.getSubcategory());
		values.put(Place_rating, place.getRating());
		values.put(Place_category, place.getCategory());
		values.put(Place_Name, place.getName());
		values.put(Place_Address, place.getAddress());
		values.put(Place_ImageUrl, place.getImageUrl());
		values.put(Place_Description, place.getDescription());
		values.put(Place_Latitude, place.getLatitude());
		values.put(Place_Longitude, place.getLongitude());
		values.put(Place_Image, place.getPlaceImage());
		// updating row
		int a=db.update(TABLE_PLACE, values, Place_Id + " = ?",
				new String[] { String.valueOf(placeId) });
	
		
		db.close();
		return a;
		
	}
	// Updating single city
	public int updateCity(int cityId,City city) {
			SQLiteDatabase db = this.getWritableDatabase();

			ContentValues values = new ContentValues();
			
			values.put(City_Name, city.getCityName());
			values.put(City_Url, city.getCityUrl());
			values.put(City_Image, city.getCityimage());

			
			// updating row
			int a=db.update(TABLE_City, values, City_Id + " = ?",
					new String[] { String.valueOf(cityId) });
		
			db.close();
			return a;
			
		}
	//wether restaurant and NightClub
	
	
	public ArrayList<Place>  getTypePlaceList(String placetype,String cityid)
	{
		
		ArrayList<Place> restaurantList = new ArrayList<Place>();
		SQLiteDatabase db = this.getReadableDatabase();
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();
		*/
		try{
			/*cursorWindow = new CursorWindow("CursorWindow");
			cursorWindow.clear();
			cursor.setWindow(cursorWindow);*/
			
			// Select All Query
			cursor = (SQLiteCursor) db.query(TABLE_PLACE, new String[] { 
					Place_Id,Place_subcategory,Place_rating,Place_category,Place_Name,Place_Address,Place_ImageUrl ,Place_Description,Place_Latitude,Place_Longitude,Place_Image }, 
					Place_category + "=?" +" AND "+ City_Id + "=?",
			
					new String[] { String.valueOf(placetype),String.valueOf(cityid) }, null, null, null, null);
			
			
				
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Place place=new Place();
					place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
					place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory)));
					place.setRating(cursor.getInt(cursor.getColumnIndex(Place_rating)));
					place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
					place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
					place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
					place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
					place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
					place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
					place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
					place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
					
					
					// Adding city to list
					restaurantList.add(place);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			
			// return city list
			return restaurantList;
		}catch(Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			e.printStackTrace();
			db.close();
			return null;
		}
		
	}
	public ArrayList<Place>  getNightClubsOnlyList()
	{
		
		ArrayList<Place> nightclubList = new ArrayList<Place>();
		// Select All Query
		String selectQuery = "SELECT * FROM Place where Category=NightClub";

		SQLiteDatabase db = this.getReadableDatabase();
//		Cursor cursor =null;
		
		try{
			cursor = (SQLiteCursor)db.rawQuery(selectQuery, null);
			// looping through all rows and adding to list
			if (cursor.moveToFirst()) {
				do {
					Place place=new Place();
					place.setId(cursor.getInt(cursor.getColumnIndex(Place_Id))); //id
					place.setSubcategory(cursor.getString(cursor.getColumnIndex(Place_subcategory)));
					place.setRating(cursor.getInt(cursor.getColumnIndex(Place_rating)));
					place.setCategory(cursor.getString(cursor.getColumnIndex(Place_category)));
					place.setName(cursor.getString(cursor.getColumnIndex(Place_Name)));
					place.setAddress(cursor.getString(cursor.getColumnIndex(Place_Address)));
					place.setImageUrl(cursor.getString(cursor.getColumnIndex(Place_ImageUrl)));
					place.setDescription(cursor.getString(cursor.getColumnIndex(Place_Description)));
					place.setLatitude(cursor.getDouble(cursor.getColumnIndex(Place_Latitude)));
					place.setLongitude(cursor.getDouble(cursor.getColumnIndex(Place_Longitude)));
					place.setPlaceImage(cursor.getBlob(cursor.getColumnIndex(Place_Image)));
					
					// Adding city to list
					nightclubList.add(place);
				} while (cursor.moveToNext());
			}
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return nightclubList;
		}catch (Exception e) {
			if (cursor != null){
				cursor.getWindow().clear();
				cursor.close();
			}
			db.close();
			return null;
		}
		
	}
	public int updateRating(int placeid,double rate)
	{
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		values.put(Place_rating, rate);
		

		
		// updating row
		int a=db.update(TABLE_PLACE, values, Place_Id + " = ?",
				new String[] { String.valueOf(placeid) });
	
		db.close();
		return a;
	}

	
}
