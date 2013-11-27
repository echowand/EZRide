package com.cs307.ezride.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EventTable {
	public static final String TABLE_NAME = "events";
	public static final String COLUMN_ID = "eventid";
	public static final String COLUMN_GOOGLEID = "google_id";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_DETAILS = "details";
	public static final String COLUMN_START = "start";
	public static final String COLUMN_END = "end";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_NAME + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_GOOGLEID + " INTEGER, "
			+ COLUMN_TITLE + " TEXT, "
			+ COLUMN_DETAILS + " TEXT, "
			+ COLUMN_START + " TEXT, "
			+ COLUMN_END + "TEXT"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.d(EventTable.class.getName(), TABLE_NAME + " table created.");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(EventTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
