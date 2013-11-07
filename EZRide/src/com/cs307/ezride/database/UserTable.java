package com.cs307.ezride.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserTable {
	public static final String TABLE_USER = "user";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_USERNAME = "username";
	public static final String COLUMN_PASSWORD = "password";
	public static final String COLUMN_REALNAME = "realname";
	public static final String COLUMN_EMAIL = "email";
	public static final String COLUMN_PHONE = "phone";
	public static final String COLUMN_ADDRESS = "address";
	public static final String COLUMN_BIO = "bio";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_USER + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_USERNAME + " TEXT, "
			+ COLUMN_PASSWORD + " TEXT, "
			+ COLUMN_REALNAME + " TEXT, "
			+ COLUMN_EMAIL + " TEXT, "
			+ COLUMN_PHONE + " TEXT, "
			+ COLUMN_ADDRESS + " TEXT, "
			+ COLUMN_BIO + " TEXT"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.w(UserTable.class.getName(), TABLE_USER + " table created.");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(UserTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
		onCreate(db);
	}
}
