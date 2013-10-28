package com.cs307.ezride;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	public static final String KEY_ID = "id";
	public static final String KEY_UNAME = "username";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASS = "password";
	public static final String KEY_BIO = "bio";
	public static final String KEY_NAME = "name";
	public static final String KEY_PHONE = "phonenumber";
	public static final String KEY_ADDRESS = "address";
	
	DBHelper DB = null;
	private static final String DATABASE_NAME = "ezride.db";
	private static final int DATABASE_VERSION = 1;
	public static final String[] DATABASE_TABLE_NAMES = {"user"};
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		try {
			String create = null;
			for (int i = 0;i < DATABASE_TABLE_NAMES.length;i++) {
				if (DATABASE_TABLE_NAMES[i].equals("user")) {
					create = "CREATE TABLE " + DATABASE_TABLE_NAMES[i] + "(" +
							KEY_ID + " INTEGER PRIMARY KEY," +
							KEY_UNAME + " TEXT," +
							KEY_PASS + " TEXT," +
							KEY_NAME + " TEXT," +
							KEY_EMAIL + " TEXT," +
							KEY_PHONE + " TEXT," +
							KEY_BIO + " TEXT," +
							KEY_ADDRESS + " TEXT);";
				} else {
					create = null;
				}
				
				if (create == null) {
					throw new Exception("Unknown table name.");
				}
				
				db.execSQL(create);
				Log.d("DBHelper.onCreate", DATABASE_TABLE_NAMES[i] + " table created");
			}
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	public void open() {
		getWritableDatabase();
	}
	
	public Cursor rawQuery(String query, String[] args) {
		Cursor cursor = null;
		cursor = db.rawQuery(query, args);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}

}
