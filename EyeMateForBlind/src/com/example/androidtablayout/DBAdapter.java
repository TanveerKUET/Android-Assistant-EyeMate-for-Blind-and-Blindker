package com.example.androidtablayout;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBAdapter {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_LAT = "lattitude";
	public static final String KEY_LONG = "longitude";
	public static final String KEY_PHONE = "phone";
	
	public static final String TAG = "DBAdapter";
	
	private static final String  DATABASE_NAME  = "blindperson";
	private static final String DATABASE_TABLE = "myinfo";
	
	
	private static final int  DATABASE_VERSION = 1;
	private static final String  DATABASE_CREATE ="create table myinfo (_id integer primary key autoincrement,"+ "name text not null,phone text not null);";	
    private final Context context;
    
    private DatabaseHelper DBHelper ;
    private SQLiteDatabase db;
    
    public DBAdapter(Context ctx){
 	   this.context = ctx;
 	   DBHelper = new DatabaseHelper(context);
 	   
    }
    private static class DatabaseHelper extends SQLiteOpenHelper{

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME , null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try{
				db.execSQL(DATABASE_CREATE);
				Log.e("DATABASE_TRY", "blindperson created successfully...");
				
			}catch(SQLException sqle){
				sqle.printStackTrace();
				Log.e("DATABASE_CATCH", "Failed to create DATABSE");
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int  oldVersion,  int  newVersion) {
			// TODO Auto-generated method stub
			Log.w(TAG,"Upgrading database from version " + oldVersion + "to"+newVersion+", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS myinfo");
			onCreate(db);
		}
    }
    
  //---opens the database---
    public DBAdapter open() throws SQLException{
    	 db = DBHelper.getWritableDatabase();
    	return this;
    }
    
  //---closes the database---​​​​public void close() 
    public void close(){
    	DBHelper.close();
    }
    
  //---insert a contact into the database---
    public long insertContact(String Name,String pnumber) {
    	ContentValues initialValues = new ContentValues();
    	initialValues.put(KEY_NAME,Name);
		initialValues.put(KEY_PHONE,pnumber);
    	return db.insert(DATABASE_TABLE, null, initialValues);
    }
    
  //---retrieves all the contacts---
    public  Cursor getAllContacts(){
		    return db.query(DATABASE_TABLE, new String[]{KEY_NAME,KEY_PHONE},null, null, null, null, null);
    }
    
  //delete all data
    public void deleteAll(){
    	try{
    		db.execSQL("delete FROM myinfo");
    	}catch (SQLException e) {
    		e.printStackTrace();
			Log.e("DELETE_EXCEPTION", "Failed to delete");
		}
		
    	
    }
}
