package com.falke.training_map.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 2;
	// Database Name
	public static final String DATABASE_NAME = "ROUTE_SERVICE2";
	// tasks table name
	private static final String TABLE_NAME_R = "route2";
	private static final String TABLE_NAME_P = "point2";
	// tasks Table Columns names
	private static final String KEY_RID = "route_id";
	private static final String KEY_RBEZ = "route_bez";
	private static final String KEY_PID = "point_id";
	private static final String KEY_LAT = "point_lat";
	private static final String KEY_LNG = "point_lng";
	private static final String KEY_PRID = "pRoute_id";

	public static int COUNT = 0;
	public static int PRID = 1;

	private SQLiteDatabase dbase;
	 
	public DbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		dbase=db;
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_R + " ( "
				+ KEY_RID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_RBEZ + " TEXT )";
				db.execSQL(sql);

		sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_P + " ( "
				+ KEY_PID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_LAT + " TEXT, "
				+ KEY_LNG + " TEXT, " + KEY_PRID + " INTEGER )";
				db.execSQL(sql);
		//db.close();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_R);
		// Create tables again
		onCreate(db);
	}
	
	public void open() {
	    dbase = this.getWritableDatabase();
	}
	
	public void openR() {
	    dbase = this.getReadableDatabase();
	}
	
	public void Dbclose() {
	    this.close();
	}
	
	public long insert_R(String bez) {
		
		dbase = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_RBEZ, bez);

		return dbase.insert(TABLE_NAME_R, null, values);
		
	}

	public long insert_P(String lat, String lng, int route_id) {

		dbase = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_LAT, lat);
		values.put(KEY_LNG, lng);
		values.put(KEY_PRID, route_id);

		return dbase.insert(TABLE_NAME_P, null, values);

	}
	
	public void delDb(String d){
		dbase = this.getWritableDatabase();
		if(d.equalsIgnoreCase("P")){
			dbase.execSQL("delete from "+ TABLE_NAME_P);
		}else if(d.equalsIgnoreCase("R")){
			dbase.execSQL("delete from "+ TABLE_NAME_R);
		}
	}
	
	
	public int count_P(){
	//	String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME_P +" WHERE " + KEY_RID + " LIKE '" + DATE + "'" ;
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME_P +" WHERE " + KEY_PRID + " LIKE " + PRID;

		dbase = this.getReadableDatabase();
		Cursor mCount = dbase.rawQuery(selectQuery, null);
		mCount.moveToFirst();
		int count= mCount.getInt(0); 
		mCount.close();

		return count; 
	}

	public int count_R(){
		//	String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME_P +" WHERE " + KEY_RID + " LIKE '" + DATE + "'" ;
		String selectQuery = "SELECT COUNT(*) FROM " + TABLE_NAME_R ;

		dbase = this.getReadableDatabase();
		Cursor mCount = dbase.rawQuery(selectQuery, null);
		mCount.moveToFirst();
		int count= mCount.getInt(0);
		mCount.close();

		return count;
	}

	/*
	public long deletRow(int rowId){
		
		dbase = this.getWritableDatabase();
		
		String where = KEY_ID+"=?"; // The where clause to identify which columns to delete.
		String[] value = { ""+rowId+"" }; // The value for the where clause.
		
		return dbase.delete(TABLE_TAGE, where, value);
	}
	*/
	

	public List<Route> getRoute(){
		List<Route> einList = new ArrayList<Route>();
		
		
	//	String selectQuery = KEY_DATE + " BETWEEN " + FOR7D + " AND " + NOW;
//		String selectQuery = KEY_SDATE + " LIKE '" + DATE + "'";
		String selectQuery = KEY_RBEZ + " LIKE '" + "%" + "'";
		dbase=this.getReadableDatabase();
//		Cursor cursor = dbase.rawQuery(selectQuery, null);
		
		
		Cursor cursor = dbase.query(TABLE_NAME_R, null, selectQuery, null, null ,null, null);
		// looping through all rows and adding to list
		
		if (cursor.moveToFirst()) {
			do {

				Route ein = new Route();
				ein.setID(cursor.getInt(0));
				ein.setBEZ(cursor.getString(1));

				einList.add(ein);
			} while (cursor.moveToNext());
		}
		// return ein list
		return einList;
	}

	public List<Point> getPoint(){
		List<Point> einList = new ArrayList<Point>();


		//	String selectQuery = KEY_DATE + " BETWEEN " + FOR7D + " AND " + NOW;
//		String selectQuery = KEY_SDATE + " LIKE '" + DATE + "'";
		String selectQuery = "true";
		dbase=this.getReadableDatabase();
//		Cursor cursor = dbase.rawQuery(selectQuery, null);


		Cursor cursor = dbase.query(TABLE_NAME_P, null, null, null, null ,null, null);
		// looping through all rows and adding to list

		if (cursor.moveToFirst()) {
			do {

				Point ein = new Point();
				ein.setID(cursor.getInt(0));
				ein.setLAT(cursor.getString(1));
				ein.setLNG(cursor.getString(2));
				ein.setROUTE_ID(cursor.getInt(3));

				einList.add(ein);
			} while (cursor.moveToNext());
		}
		// return ein list
		return einList;
	}
	
	
	
	
	public List<Route> getAll_R() {
		List<Route> einList = new ArrayList<Route>();
		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_TAGE +" WHERE " + KEY_SDATE + " LIKE '" + DATE  + "' AND "  + KEY_TRAINING + " LIKE '" + TRAINING + "' AND " + KEY_BOOTSKLASSE + " LIKE '" + BOOTSKLASSE + "' AND " + KEY_BOOT + " LIKE '" + BOOT + "'" ;
//		String selectQuery = "SELECT * FROM " + TABLE_TAGE +" WHERE " + KEY_SDATE + " LIKE '" + DATE + "'";
		
		//String selectQuery = KEY_SDATE + " LIKE '" + DATE + "'";
		
		dbase=this.getReadableDatabase();
//		Cursor cursor = dbase.rawQuery(selectQuery, null);
		
		
		Cursor cursor = dbase.query(TABLE_NAME_R, null, null, null, null ,null, null);
		// looping through all rows and adding to list
		
		if (cursor.moveToFirst()) {
			do {

				Route ein = new Route();
				ein.setID(cursor.getInt(0));
				ein.setBEZ(cursor.getString(1));
				
				einList.add(ein);
			} while (cursor.moveToNext());
		}
		// return ein list
		return einList;
	}


	public List<Point> getAll_P() {
		List<Point> einList = new ArrayList<Point>();
		// Select All Query
//		String selectQuery = "SELECT * FROM " + TABLE_TAGE +" WHERE " + KEY_SDATE + " LIKE '" + DATE  + "' AND "  + KEY_TRAINING + " LIKE '" + TRAINING + "' AND " + KEY_BOOTSKLASSE + " LIKE '" + BOOTSKLASSE + "' AND " + KEY_BOOT + " LIKE '" + BOOT + "'" ;
//		String selectQuery = "SELECT * FROM " + TABLE_TAGE +" WHERE " + KEY_SDATE + " LIKE '" + DATE + "'";

		String selectQuery = "SELECT * FROM " + TABLE_NAME_P +" WHERE " + KEY_PRID + " LIKE " + PRID;

		dbase=this.getReadableDatabase();
		Cursor cursor = dbase.rawQuery(selectQuery, null);


	//	Cursor cursor = dbase.query(TABLE_NAME_P, null, null, null, null ,null, null);
		// looping through all rows and adding to list

		if (cursor.moveToFirst()) {
			do {

				Point ein = new Point();
				ein.setID(cursor.getInt(0));
				ein.setLAT(cursor.getString(1));
				ein.setLNG(cursor.getString(2));
				ein.setROUTE_ID(cursor.getInt(3));

				einList.add(ein);
			} while (cursor.moveToNext());
		}
		// return ein list
		return einList;
	}


	public int rowcount() {
		int row=0;
		String selectQuery = "SELECT  * FROM " + TABLE_NAME_R;
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		row=cursor.getCount();
		return row;
	}

	public static void addCount(int count){

		COUNT = count;

	}

	public static void addPrid(int prid){

		PRID = prid;

	}
/*
	public long insertup(int id, long dates, String sdates,  int training1, int time1, int distance1, int bootsklasse1, int boot1
				,  int training2, int time2, int distance2, int bootsklasse2, int boot2
				,  int training3, int time3, int distance3, int bootsklasse3, int boot3
				,  int training4, int time4, int distance4, int bootsklasse4, int boot4
				,  int training5, int time5, int distance5, int bootsklasse5, int boot5, String notizen) {
		
			// Create content values that contains the name of the column you want to update and the value you want to assign to it 
			ContentValues cv = new ContentValues();
			cv.put(KEY_DATE, dates);
			cv.put(KEY_SDATE, sdates);
			
			cv.put(KEY_TRAINING1, training1);
			cv.put(KEY_TIME1, time1);
			cv.put(KEY_DISTANCE1, distance1);
			cv.put(KEY_BOOTSKLASSE1, bootsklasse1);
			cv.put(KEY_BOOT1, boot1);
			
			cv.put(KEY_TRAINING2, training2);
			cv.put(KEY_TIME2, time2);
			cv.put(KEY_DISTANCE2, distance2);
			cv.put(KEY_BOOTSKLASSE2, bootsklasse2);
			cv.put(KEY_BOOT2, boot2);
			
			cv.put(KEY_TRAINING3, training3);
			cv.put(KEY_TIME3, time3);
			cv.put(KEY_DISTANCE3, distance3);
			cv.put(KEY_BOOTSKLASSE3, bootsklasse3);
			cv.put(KEY_BOOT3, boot3);
			
			cv.put(KEY_TRAINING4, training4);
			cv.put(KEY_TIME4, time4);
			cv.put(KEY_DISTANCE4, distance4);
			cv.put(KEY_BOOTSKLASSE4, bootsklasse4);
			cv.put(KEY_BOOT4, boot4);
			
			cv.put(KEY_TRAINING5, training5);
			cv.put(KEY_TIME5, time5);
			cv.put(KEY_DISTANCE5, distance5);
			cv.put(KEY_BOOTSKLASSE5, bootsklasse5);
			cv.put(KEY_BOOT5, boot5);
			
			cv.put(KEY_NOTIZEN, notizen);

			String where = KEY_ID+"=?"; // The where clause to identify which columns to update.
			String[] value = { ""+id+"" }; // The value for the where clause.
			

			// Update the database (all columns in TABLE_NAME where my_column has a value of 2 will be changed to 5)
		return	dbase.update(TABLE_TAGE, cv, where, value);
	}

	*/
}
