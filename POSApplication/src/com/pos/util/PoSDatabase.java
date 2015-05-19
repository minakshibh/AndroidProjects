package com.pos.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ksoap2.serialization.PropertyInfo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.gesture.Prediction;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;



public class PoSDatabase extends SQLiteOpenHelper {

	Context mcontext;
	
	@SuppressLint("NewApi")
//	public CursorWindow cursorWindow;
	SQLiteCursor cursor = null;
//	private static CursorWindow cursorWindow;
	
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "POS";


	private static final String TABLE_USERDETAIL = "UserDetail";
	
	
	private static final String TABLE_CURRENCY_DETAIL = "CurrencyDetail";
	private static final String TABLE_CURRENCY_SETTING = "CurrencySetting";
	
	private static final String TABLE_DENOMINATION_DETAIL = "DenominationDetail";
	
	private static final String TABLE_OPERATOR_DETAIL = "OperatorDetail";
	
	
	private static final String TABLE_PRODUCT_DETAIL = "ProductDetail";
	
	private static final String TABLE_TRANSACTION_DETAIL = "TransactionDetail";
	
	private static final String TABLE_BUY_PRODUCT_DETAIL = "BuyProductDetail";
	//field for cashier table
	private static final String user_Id = "uId";
	private static final String user_Name = "uName";
	private static final String user_Password = "Password";
	private static final String user_RollId = "uRollId";
	
	
	private static final String currency_id="currencyId";
	private static final String currency_Date="currency_date";

	private static final String Discount_Type="DiscountType";
	private static final String Discount_Amount="DiscountAmount";
	
	private static final String curr_Value="currValue";
	private static final String curr_Type="currType";
	private static final String curr_Image="currImage";
	private static final String curr_Name="currName";
	private static final String curr_Sale="currSale";
	private static final String curr_Return="currReturn";
	//field for product table
		private static final String product_Local_Id = "plId";
		private static final String product_Id = "pId";
		private static final String product_Name = "pName";
		private static final String product_ItemId = "pItemId";
		private static final String product_Tax = "pTax";
		private static final String product_Discount = "pDiscount";
		private static final String product_Price = "pPrice";
		private static final String product_Qunatity = "pQunatity";
		private static final String product_BarCode = "pBarcode";
		private static final String productMDiscount = "productMDiscount";
		private static final String productMDiscType = "productMDiscType";
	
		//field for transaction fields
		private static final String transaction_Local_Id = "transactionLocalId";
		private static final String transaction_Date = "transactionDate";
		private static final String transaction_Time = "transactionTime";
		private static final String store_Id = "storeId";
		private static final String pos_Id = "posId";
		private static final String transaction_Id = "transaction_Id";
		private static final String transaction_SubTotal = "transaction_subTotal";
		private static final String transaction_Total = "transaction_total";
		private static final String transaction_Total_Discount = "transaction_discount";
		private static final String transaction_Total_Tax = "transaction_Tax";
		private static final String transaction_payableAmt = "transaction_payableAmt";
		private static final String transaction_PaymentMethod = "payment_method";
		private static final String VoucherId = "VoucherId";
		private static final String VoucherName = "VoucherName";
		
		private static final String Query_Type = "query";
		//field for buy product fields
		
		
		
		
		
		private static final String curr_Qunatity="currQunatity";
		
		
		
	public PoSDatabase(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		
		mcontext=context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		try
		{
			
		String CREATE_USER_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_USERDETAIL + "("
				+ user_Id + " INTEGER ," + user_RollId +" INTEGER ,"+user_Name + " TEXT," + user_Password + " TEXT " + ")";
		
		
		String CREATE_CURRENCY_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_CURRENCY_DETAIL + "("+ currency_id + " INTEGER PRIMARY KEY," +  curr_Value + " TEXT ,"
		+  curr_Type + " INTEGER ,"+ curr_Image+ " TEXT ,"+ curr_Name+ " TEXT " +")";
		
		String CREATE_DENOMINATION_TABLE = "CREATE TABLE  if NOT Exists " + TABLE_DENOMINATION_DETAIL + "("+ user_Id + " INTEGER ," +  currency_id + " INTEGER ,"
				+  curr_Qunatity + " TEXT ,"+ currency_Date + " TEXT "+")";
				
		
		String CREATE_PRODUCT_TABLE="CREATE TABLE  if NOT Exists " + TABLE_PRODUCT_DETAIL + "("+product_Local_Id +"INTEGER PRIMARY KEY ,"
				+ product_Id + " INTEGER ," + product_Name + " TEXT," + product_BarCode + " TEXT ," +product_ItemId +" TEXT ,"
				+ product_Discount+" TEXT ,"+product_Price +" TEXT ,"+ product_Qunatity+" INTEGER ,"+ product_Tax +" TEXT "+")";
		
		
		String CREATE_TRANSACTION_TABLE="CREATE TABLE  if NOT Exists " + TABLE_TRANSACTION_DETAIL + "("+transaction_Local_Id +"INTEGER PRIMARY KEY ,"
				+ transaction_Date + " TEXT ," + transaction_Time + " TEXT," + user_Id + " INTEGER ," +store_Id +" TEXT ,"
				+ pos_Id +" TEXT ,"+transaction_Id +" TEXT ,"+ transaction_SubTotal+" TEXT ,"+ transaction_Total +" TEXT ,"+ transaction_Total_Discount+" TEXT ,"
				+ transaction_Total_Tax +" TEXT ," + transaction_PaymentMethod +" TEXT ,"+ VoucherId +" TEXT ," + VoucherName +" TEXT , "
				+  currency_id +" INTEGER ,"+ Discount_Type +" TEXT ,"+ Discount_Amount +" TEXT ,"+Query_Type +" TEXT ,"+ transaction_payableAmt +" TEXT"+")";
		
		
		
		String CREATE_BUYPRODUCT_TABLE="CREATE TABLE  if NOT Exists " + TABLE_BUY_PRODUCT_DETAIL + "("+transaction_Local_Id +"INTEGER  ,"
				+ product_Id + " TEXT ," + transaction_Id + " TEXT," + product_Qunatity + " INTEGER ," +product_Price +" TEXT ,"
				+ product_Discount +" TEXT ,"+product_Tax +" TEXT ," +product_Name +" TEXT ,"+product_BarCode +" TEXT ,"+product_ItemId +" TEXT ,"+productMDiscount+" TEXT ,"+ productMDiscType +" TEXT "+")";
		
		
		
		String CREATE_CURRENCY_SETTING ="CREATE TABLE  if NOT Exists " + TABLE_CURRENCY_SETTING + "("+currency_id +" INTEGER PRIMARY KEY ,"
				+ curr_Sale +" TEXT ,"+curr_Return +" TEXT " +")";
		
		
		String CREATE_OPERATOR_TABLE ="CREATE TABLE  if NOT Exists " + TABLE_OPERATOR_DETAIL + "("+user_Id +" INTEGER  KEY ,"
				+ currency_id +" INTEGER ,"+transaction_Total +" TEXT " +")";
		
		
		
		db.execSQL(CREATE_USER_TABLE);
	
		db.execSQL(CREATE_CURRENCY_TABLE);
		
		db.execSQL(CREATE_PRODUCT_TABLE);
		
		db.execSQL(CREATE_TRANSACTION_TABLE);
		
		db.execSQL(CREATE_BUYPRODUCT_TABLE);

		db.execSQL(CREATE_DENOMINATION_TABLE);
		
		db.execSQL(CREATE_CURRENCY_SETTING);
		db.execSQL(CREATE_OPERATOR_TABLE);
		
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	public void addUser(Cashier userdetail) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		
		values.put(user_Id, userdetail.getId()); // user Id
		values.put(user_Name, userdetail.getUsername().trim()); // user name
		values.put(user_Password,userdetail.getPassword().trim());	//user password
		values.put(user_RollId, userdetail.getRollId());
		
		long a=db.insert(TABLE_USERDETAIL, null, values);  // Inserting Row
		Log.e("insert", ""+a);
		db.close(); // Closing database connection
	}
	public void updateUser(Cashier userdetail,int id)
	{
		
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(user_Name, userdetail.getUsername().trim()); // user name
		values.put(user_Password,userdetail.getPassword().trim());	//user password
		values.put(user_RollId, userdetail.getRollId());
		long a=db.update(TABLE_USERDETAIL, values, user_Id + " =?",  new String[] {String.valueOf(id)});  // Inserting Row
		Log.e("update", ""+a);
		db.close(); // Closing database connection
		
		
		
		
	}
	public ArrayList<Cashier> getAllUsers() {
		ArrayList<Cashier> userList = new ArrayList<Cashier>();
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_USERDETAIL;              

		SQLiteDatabase db = this.getReadableDatabase();
	
		try{
			
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			System.out.println("Size "+cursor.getCount());
			
			if (cursor.moveToFirst()) {
				do {
						Cashier cashier = new Cashier();
						cashier.setId(cursor.getInt(cursor.getColumnIndex(user_Id)));  //user id
						cashier.setUsername(cursor.getString(cursor.getColumnIndex(user_Name)));	//user name
						cashier.setRollId(cursor.getInt(cursor.getColumnIndex(user_RollId)));
						cashier.setPassword(cursor.getString(cursor.getColumnIndex(user_Password)));					//city iamge url
						
						userList.add(cashier);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return userList;
			
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
			return userList;
		}
		

	}
	
	public  void addTransaction(int userId,int currencyId,String totalAmount)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues value =new ContentValues();
		value.put(user_Id, userId);
		value.put(currency_id, currencyId);
		value.put(transaction_Total, totalAmount);
		
		long a= db.insert(TABLE_OPERATOR_DETAIL, null, value);
		
		db.close();
		Log.e("Transaction complete", ""+a);
		
		
	}
	public  ArrayList<Transaction> getTransactionByUser(int userId)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		ArrayList<Transaction> list = new ArrayList<Transaction>();
		
		
		 SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_OPERATOR_DETAIL, new String[] { 
				user_Id,currency_id,transaction_Total}, user_Id + "=?" ,
				new String[] {String.valueOf(userId) }, null, null, null, null);
		
		 Transaction transaction=null;
		 if(cursor!=null)
		 {
			 if(cursor.moveToFirst())
			 {
				 do
				 {
				 	transaction=new Transaction();
				 	transaction.setEmployee_Id(String.valueOf(cursor.getInt(cursor.getColumnIndex(user_Id))));  //user id
					transaction.setCurrencyId(cursor.getInt(cursor.getColumnIndex(currency_id)));	//user name
					transaction.setTotal(cursor.getString(cursor.getColumnIndex(transaction_Total)));
							//city iamge url
					
					list.add(transaction);
				 }
				 while(cursor.moveToNext());
			 }
			 cursor.getWindow().clear();
				cursor.close(); 
		 }
		  		//cursor closed
			Log.e("Detailsql ", ""+list);
			db.close();
			return list;	
		
	}
	public  void deleteTransactionByUser(int userId)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
			
		db.delete(TABLE_OPERATOR_DETAIL,null , null);
		
			db.close();
	
	}
	
	public int maxUserId()
	{
		SQLiteDatabase db=this.getReadableDatabase();
		final SQLiteStatement stmt = db
	            .compileStatement("SELECT MAX(uId) FROM Cashier");
		
		int a = 0;
		a=(int) stmt.simpleQueryForLong();
		
		db.close();
		return a;
	}
	public Cashier checkUserExist(String name)
	{
		SQLiteDatabase db=this.getReadableDatabase();
		 SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_USERDETAIL, new String[] { 
				user_Id,user_Name,user_Password,user_RollId}, user_Name + "=?" ,
				new String[] {name }, null, null, null, null);
		
		 Cashier detail=null;
		 if(cursor!=null)
		 {
			 if(cursor.moveToFirst())
			 {
				 detail=new Cashier();
				 detail.setId(cursor.getInt(cursor.getColumnIndex(user_Id)));  //user id
					detail.setUsername(cursor.getString(cursor.getColumnIndex(user_Name)));	//user name
					detail.setRollId(cursor.getInt(cursor.getColumnIndex(user_RollId)));
					detail.setPassword(cursor.getString(cursor.getColumnIndex(user_Password)));					//city iamge url
					
				
					db.close();
					cursor.close();
					return detail;
				 
			 }
			 cursor.getWindow().clear();
				cursor.close(); 
		 }
		  		//cursor closed
			Log.e("Detailsql ", ""+detail);
			db.close();
			return detail;	
		
	}
	@SuppressLint("SimpleDateFormat")
	public void addFloatCash(Currency curr)
	{
		
		SQLiteDatabase db=this.getWritableDatabase();
		Calendar c=Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		
			ContentValues value=new ContentValues();
			value.put(currency_id, curr.currencyId);
			value.put(curr_Value, curr.value);
			value.put(curr_Type, curr.currencyTypeId);
			value.put(curr_Image, "Image");
			value.put(curr_Name, curr.currencyName);
			
			long a=db.insert(TABLE_CURRENCY_DETAIL, null, value);  // Inserting Row
			Log.e("insert", ""+a);
			db.close(); // Closing database connection
	}
	@SuppressLint("SimpleDateFormat")
	public void addDenominatationDetail(int userId,int currId,String quantity)
	{
		
		SQLiteDatabase db=this.getWritableDatabase();
		Calendar c=Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		
			ContentValues value=new ContentValues();
			value.put(user_Id, user_Id);
			value.put(currency_id, currId);
			value.put(curr_Qunatity, quantity);
			
			value.put(currency_Date, formattedDate);
			
			long a=db.insert(TABLE_DENOMINATION_DETAIL, null, value);  // Inserting Row
			Log.e("insert", ""+a);
			db.close(); // Closing database connection
	}
	
	
	public void addProduct(ProductReal product) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		
		
		values.put(product_Id,product.getPid()); // user Id
		values.put(product_Name, product.getProductName()); // user name
		values.put(product_BarCode,product.getBarCode());	//user password
		
		values.put(product_Discount,product.getDiscount().toString());
		
		values.put(product_ItemId,product.getItemId());
		values.put(product_Tax,product.getTax().toString());
		
		values.put(product_Qunatity, product.getTotalQuantity());
		values.put(product_Price,product.getPrice().toString());
		
		
		long a=db.insert(TABLE_PRODUCT_DETAIL, null, values);  // Inserting Row
		Log.e("insert", ""+a);
		db.close(); // Closing database connection
	}
	public ArrayList<ProductReal> getAllProducts() {
		ArrayList<ProductReal> productList = new ArrayList<ProductReal>();
		// Select All Query
		String selectQuery = "SELECT  * FROM ProductDetail";              

		SQLiteDatabase db = this.getReadableDatabase();
	
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try{
			
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			System.out.println("Size "+cursor.getCount());
			
			if (cursor.moveToFirst()) {
				do {
						ProductReal product = new ProductReal();
						product.setPid(cursor.getInt(cursor.getColumnIndex(product_Id)));
						product.setBarCode(cursor.getString(cursor.getColumnIndex(product_BarCode)));
						product.setDiscount(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Discount)))));
						
						product.setItemId(cursor.getString(cursor.getColumnIndex(product_ItemId)));
						product.setPrice(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Price)))));
						product.setSingleItemDiscount(product.getDiscount());
						
						product.setProductName(cursor.getString(cursor.getColumnIndex(product_Name)));
						product.setTotalQuantity(cursor.getInt(cursor.getColumnIndex(product_Qunatity)));
						product.setTax(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Tax)))));
						product.setSingleItemTax(product.getTax());
						product.setMdiscount(BigDecimal.valueOf(0));
						product.setMDType(PosConstants.MDnoapplied);
						productList.add(product);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return productList;
			
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
			return productList;
		}
		

	}
	public ProductReal getProductByBarCode(String barcode) {
		
		SQLiteDatabase db = this.getReadableDatabase();
	
		ProductReal product =null;
		SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_PRODUCT_DETAIL, new String[] { 
				product_Id,product_BarCode, product_Discount,product_ItemId ,product_Price,product_Name,product_Qunatity,product_Tax}, product_BarCode + "=?",
				new String[] { barcode }, null, null, null, null);
		
		try{
			
			if (cursor.moveToFirst()) {
				do {
					
						product=new ProductReal();
						product.setPid(cursor.getInt(cursor.getColumnIndex(product_Id)));
						product.setBarCode(cursor.getString(cursor.getColumnIndex(product_BarCode)));
						product.setDiscount(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Discount)))));
						
						product.setItemId(cursor.getString(cursor.getColumnIndex(product_ItemId)));
						product.setPrice(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Price)))));
						
						product.setProductName(cursor.getString(cursor.getColumnIndex(product_Name)));
						product.setTotalQuantity(cursor.getInt(cursor.getColumnIndex(product_Qunatity)));
						product.setTax(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Tax)))));
						
						product.setSingleItemDiscount(product.getDiscount());
						product.setSingleItemTax(product.getTax());
						product.setMdiscount(BigDecimal.valueOf(0));
						product.setMDType(PosConstants.MDnoapplied);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return product;
			
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
			return null;
		}
		

	}
	public ProductReal getProductByProductId(String id) {
		
		
		SQLiteDatabase db = this.getReadableDatabase();
	
		ProductReal product = new ProductReal();
		SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_PRODUCT_DETAIL, new String[] { 
				product_Id,product_BarCode, product_Discount,product_ItemId ,product_Price,product_Name,product_Qunatity,product_Tax}, product_Id + "=?",
				new String[] { id }, null, null, null, null);
		
		try{
			
			
			if (cursor.moveToFirst()) {
				do {
						
						product.setPid(cursor.getInt(cursor.getColumnIndex(product_Id)));
						product.setBarCode(cursor.getString(cursor.getColumnIndex(product_BarCode)));
						product.setDiscount(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Discount)))));
						
						product.setItemId(cursor.getString(cursor.getColumnIndex(product_ItemId)));
						product.setPrice(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Price)))));
						
						product.setProductName(cursor.getString(cursor.getColumnIndex(product_Name)));
						product.setTotalQuantity(cursor.getInt(cursor.getColumnIndex(product_Qunatity)));
						product.setTax(BigDecimal.valueOf(Double.valueOf(cursor.getString(cursor.getColumnIndex(product_Tax)))));
						

						product.setSingleItemDiscount(product.getDiscount());
						product.setSingleItemTax(product.getTax());
						product.setMdiscount(BigDecimal.valueOf(0));
						product.setMDType(PosConstants.MDnoapplied);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return product;
			
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
			return null;
		}
		

	}
	public void deleteProducts()
	{
		SQLiteDatabase db=this.getWritableDatabase();
		
		db.delete(TABLE_PRODUCT_DETAIL,null , null);
		
		db.close();
	}
	public void deleteCashier(int id)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		
		db.delete(TABLE_USERDETAIL,user_Id + " =?" , new String[] {String.valueOf(id)});
		
		db.close();
	}
	
	public void saveTransaction(Transaction transaction)
	{
        /*p1.setName("UserId");
        p1.setValue(employee_id);

        p2.setName("transactionId");
        p2.setValue(String.valueOf(transactionId));

        p3.setName("Paid");
        p3.setValue(null);

        p4.setName("PosId");
        p4.setValue(posId);

        p5.setName("invoiceStatus");
        p5.setValue("true");

        p6.setName("VoucherNumber");						//Chnage done here
        p6.setValue(voucherId);

        p9.setName("CurrencyId");						//Chnage done here
        p9.setValue(currencyId);

        p7.setName("CustomerId");
        p7.setValue(null);

        p8.setName("PayM");											//Chnages done here
        p8.setValue(paymentType);

        p10.setName("DiscountAmount");											//Chnages done here
        p10.setValue(discountAmount);

        p11.setName("DiscountType");											//Chnages done here
        p11.setValue(discountType);

        p12.setName("Discount");											//Chnages done here
        p12.setValue(totalDiscount);

        p13.setName("Tax");											//Chnages done here
        p13.setValue(totalTax);

        p14.setName("FinalTotal");											//Chnages done here
        p14.setValue(finalTotal);
        
        p15.setName("TotalAmount");											//Chnages done here
        p15.setValue(subTotalAmt);*/
        
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		
		values.put(transaction_Date,transaction.getDate()); // user Id
//		values.put(transaction_Time, transaction.getTime()); // user name
		values.put(user_Id,transaction.getEmployee_Id());	//user password
		
		values.put(store_Id,transaction.getStore_Id());
		values.put(transaction_payableAmt,transaction.getPayableAmt());
		
		values.put(pos_Id,transaction.getPos_Id());
		
		values.put(transaction_Id,transaction.getTransaction_Id());
		
		values.put(transaction_SubTotal, transaction.getSubTotal());
		values.put(transaction_Total,transaction.getTotal());
		
		values.put(transaction_Total_Discount,transaction.getDiscount());
		values.put(transaction_Total_Tax,transaction.getTax());
		values.put(transaction_PaymentMethod, transaction.getPaymentMethod());
		
		values.put(VoucherId, transaction.getVoucherId());
		values.put(VoucherName, transaction.getVoucherId());
		values.put(Query_Type, "Transaction");
		
		values.put(currency_id, transaction.getCurrencyId());
		values.put(Discount_Type, transaction.getmDiscountType());
		values.put(Discount_Amount, transaction.getmDiscountAmount());
		
		long a=db.insert(TABLE_TRANSACTION_DETAIL, null, values);  // Inserting Row
		Log.e("insert transaction", ""+a);
		db.close(); // Closing database connection
		
	}
	public void updateProductQunatity(int productId,int quntity)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(product_Qunatity, quntity); // 
		
		int a= db.update(TABLE_PRODUCT_DETAIL, values, product_Id + " =?",  new String[] {String.valueOf(productId)});
		
		db.close();
		Log.e("update Quntity ", ""+a);
	}
	public void buyProductDetail(ProductReal product,String transactionId)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		
		/*
		 product1.addProperty("ProductId", product.getPid());
         product1.addProperty("ProductName",product.getProductName());
         product1.addProperty("Type", 1);
         product1.addProperty("Quantity", product.getSelectedQuatity());
         product1.addProperty("Barcode",product.getBarCode());
         product1.addProperty("ItemId",product.getItemId());
         product1.addProperty("Price",product.getPrice().toString());
         product1.addProperty("CurrencyPrice","1");
         if(product.getMdiscount().compareTo(BigDecimal.valueOf(0))>0){
         	product1.addProperty("Discount",product.getMdiscount().toString());
         }else
         	product1.addProperty("Discount",product.getSingleItemDiscount().toString());
         product1.addProperty("Tax",product.getSingleItemTax().toString());
         product1.addProperty("retrurnquantity", 0);
         product1.addProperty("RetrunReason_ID", 0);
     	 product1.addProperty("InvoiceDetailID", 0);
     	
     	System.out.println(String.valueOf(product.getMdiscount())+" "+product.getMDType());
     	
     	product1.addProperty("MDiscount", String.valueOf(product.getMdiscount()));
     	
     	product1.addProperty("DiscountType", product.getMDType());
     	product1.addProperty("ManualDis", null);
     	*/
		
		values.put(transaction_Id, transactionId); // user Id
		values.put(product_Id, product.getPid()); // user name
		values.put(product_Qunatity,product.getSelectedQuatity());	//user password
		
		values.put(product_Price,product.getPrice().toString());
		
		if(product.getSingleItemDiscount()!=null)
			values.put(product_Discount,product.getSingleItemDiscount().toString());
		
		if(product.getSingleItemTax()!=null)
			values.put(product_Tax,product.getSingleItemTax().toString());
		
		values.put(product_BarCode, product.getBarCode());
		values.put(product_Name, product.getProductName());
		values.put(product_ItemId, product.getItemId());
		values.put(productMDiscount, product.getMdiscount().toString());
		values.put(productMDiscType, product.getMDType());
		
		long a=db.insert(TABLE_BUY_PRODUCT_DETAIL, null, values);  // Inserting Row
		Log.e("buy product insert ", ""+a);
		db.close(); // Closing database connection
	}
	
	public void addCurrencySetting(CurrencySetting currSetting)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(currency_id, currSetting.getCurrencyId());
		values.put(curr_Sale, currSetting.getCurrSaleValue());
		values.put(curr_Return, currSetting.getCurrReturnValue());
		
		
		long a=db.insert(TABLE_CURRENCY_SETTING, null, values);  // Inserting Row
		Log.e("currency insert ", ""+a);
		db.close(); // Closing database connection
		
		
	}
	public CurrencySetting getCurrencySetting(int currId)
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		CurrencySetting currSetting =null;
		SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_CURRENCY_SETTING, new String[] { 
				currency_id,curr_Sale, curr_Return }, currency_id + "=?",
				new String[] { String.valueOf(currId) }, null, null, null, null);
		
		try{
			
			
			
			if (cursor.moveToFirst()) {
				do {
					currSetting =new CurrencySetting();
						currSetting.setCurrencyId(cursor.getInt(cursor.getColumnIndex(currency_id)));
						currSetting.setCurrSaleValue(cursor.getString(cursor.getColumnIndex(curr_Sale)));
						currSetting.setCurrReturnValue((cursor.getString(cursor.getColumnIndex(curr_Return))));
						
						
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return currSetting;
			
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
			return null;
		}
		
	}
	public ArrayList<CurrencySetting> getAllCurrencySetting()
	{
		
		SQLiteDatabase db = this.getReadableDatabase();
		ArrayList<CurrencySetting> currList =new ArrayList<CurrencySetting>();
		
		CurrencySetting currSetting =null;
		
		String query = "Select * from "+TABLE_CURRENCY_SETTING;
		SQLiteCursor cursor = (SQLiteCursor) db.rawQuery(query, null);
		
		try{
			
			if (cursor.moveToFirst()) {
				do {
					currSetting =new CurrencySetting();
						currSetting.setCurrencyId(cursor.getInt(cursor.getColumnIndex(currency_id)));
						currSetting.setCurrSaleValue(cursor.getString(cursor.getColumnIndex(curr_Sale)));
						currSetting.setCurrReturnValue((cursor.getString(cursor.getColumnIndex(curr_Return))));
						
						currList.add(currSetting);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return currList;
			
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
			return currList;
		}
		
	}
	public void updateCurrencySetting(CurrencySetting currSetting,int currId)
	{
		SQLiteDatabase db = this.getReadableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(curr_Sale, currSetting.getCurrSaleValue()); // 
		values.put(curr_Return, currSetting.getCurrReturnValue()); //
		int a= db.update(TABLE_CURRENCY_SETTING, values, currency_id + " =?",  new String[] {String.valueOf(currId)});
		
		db.close();
		Log.e("update currency ", ""+a);
	}
	public void deleteProduct(int id)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		
		db.delete(TABLE_PRODUCT_DETAIL,product_Id + " =?" , new String[] {String.valueOf(id)});
		
		db.close();
	}
	public ArrayList<Transaction>  getTransactions()
	{
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		// Select All Query
		String selectQuery = "SELECT  * FROM "+TABLE_TRANSACTION_DETAIL;              

		SQLiteDatabase db = this.getReadableDatabase();
	
		/*if (cursor != null && !(cursor.isClosed()))
			cursor.close();*/
		
		try{
			
			cursor = (SQLiteCursor) db.rawQuery(selectQuery, null);
			System.out.println("Size "+cursor.getCount());
			
			if (cursor.moveToFirst()) {
				do {
					 
						Transaction transaction = new Transaction();
						
						transaction.setDate(cursor.getString(cursor.getColumnIndex(transaction_Date)));
						transaction.setEmployee_Id(cursor.getString(cursor.getColumnIndex(user_Id)));
						transaction.setStore_Id(cursor.getString(cursor.getColumnIndex(store_Id)));
						transaction.setPos_Id(cursor.getString(cursor.getColumnIndex(pos_Id)));
						transaction.setTransaction_Id(String.valueOf(cursor.getInt(cursor.getColumnIndex(transaction_Id))));
						transaction.setSubTotal(cursor.getString(cursor.getColumnIndex(transaction_SubTotal)));
						transaction.setTotal(cursor.getString(cursor.getColumnIndex(transaction_Total)));
						transaction.setDiscount(cursor.getString(cursor.getColumnIndex(transaction_Total_Discount)));
						transaction.setTax(cursor.getString(cursor.getColumnIndex(transaction_Total_Tax)));
						transaction.setPaymentMethod(cursor.getString(cursor.getColumnIndex(transaction_PaymentMethod)));
						transaction.setVoucherId(cursor.getString(cursor.getColumnIndex(VoucherId)));
						transaction.setVoucherName(cursor.getString(cursor.getColumnIndex(VoucherName)));
						transaction.setQueryType(cursor.getString(cursor.getColumnIndex(Query_Type)));
						transaction.setCurrencyId(cursor.getInt(cursor.getColumnIndex(currency_id)));
						transaction.setmDiscountType(cursor.getString(cursor.getColumnIndex(Discount_Type)));
						transaction.setmDiscountAmount(cursor.getString(cursor.getColumnIndex(Discount_Amount)));
						transaction.setPayableAmt(cursor.getString(cursor.getColumnIndex(transaction_payableAmt)));
						
						transactionList.add(transaction);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return transactionList;
			
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
			return transactionList;
		}
		
	}
	public ArrayList<ProductReal> getBuyProducts(String transId)
	{
		ArrayList<ProductReal> productList = new ArrayList<ProductReal>();
		// Select All Query
		
		SQLiteDatabase db = this.getReadableDatabase();
		
		SQLiteCursor cursor = (SQLiteCursor) db.query(TABLE_BUY_PRODUCT_DETAIL, new String[] { 
				product_Id,product_Discount, product_Qunatity,product_Price,product_Tax,product_Name, product_BarCode,product_ItemId, productMDiscount, productMDiscType}, transaction_Id + "=?",
				new String[] { transId }, null, null, null, null);
		

		
		
		try{
			
			
			if (cursor.moveToFirst()) {
				do {
						//user password);
					    ProductReal product = new ProductReal();
						product.setPid(cursor.getInt(cursor.getColumnIndex(product_Id)));
						product.setDiscount(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(product_Discount)))));
						product.setPrice(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(product_Price)))));
						product.setSelectedQuatity(cursor.getInt(cursor.getColumnIndex(product_Qunatity)));
						product.setTax(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(product_Tax)))));
						product.setSingleItemDiscount(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(product_Discount)))));
						product.setSingleItemTax(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(product_Tax)))));
						product.setBarCode(cursor.getString(cursor.getColumnIndex(product_BarCode)));
						product.setProductName(cursor.getString(cursor.getColumnIndex(product_Name)));
						product.setItemId(cursor.getString(cursor.getColumnIndex(product_ItemId)));
						product.setMdiscount(BigDecimal.valueOf(Double.parseDouble(cursor.getString(cursor.getColumnIndex(productMDiscount)))));
						product.setMDType(cursor.getInt(cursor.getColumnIndex(productMDiscType)));
						productList.add(product);
					
				} while (cursor.moveToNext());
			}
			
			cursor.getWindow().clear();
			cursor.close();
			// close inserting data from database
			db.close();
			// return city list
			return productList;
			
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
			return productList;
		}
		
		
		
	}
	public void deleteTransaction(String id)
	{
		SQLiteDatabase db=this.getWritableDatabase();
		
		Log.e("Trnasaction deleted", ""+db.delete(TABLE_TRANSACTION_DETAIL,transaction_Id + " =?" , new String[] {id}));
		Log.e("But product deleted", ""+db.delete(TABLE_BUY_PRODUCT_DETAIL,transaction_Id + " =?" , new String[] {id}));
		
		
		db.close();
	}
	
}
