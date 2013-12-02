package com.cs307.ezride.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageTable {
	public static final String TABLE_NAME = "messages";
	public static final String COLUMN_ID = "messageid";
	public static final String COLUMN_CONTENT = "content";
	public static final String COLUMN_POSTTIME = "posttime";
	public static final String COLUMN_USERID = "userid";
	public static final String COLUMN_GROUPID = "groupid";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_NAME + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_CONTENT + " TEXT, "
			+ COLUMN_POSTTIME + " TEXT, "
			+ COLUMN_USERID + " INTEGER, "
			+ COLUMN_GROUPID + "INTEGER"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.d(MessageTable.class.getName(), TABLE_NAME + " table created.");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(EventTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
