package com.cs307.ezride.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupTable {
	public static final String TABLE_NAME = "groups";
	public static final String COLUMN_ID = "groupid";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_DATECREATED = "datecreated";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_NAME + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT, "
			+ COLUMN_DESCRIPTION + " TEXT, "
			+ COLUMN_DATECREATED + " TEXT"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.d(GroupTable.class.getName(), TABLE_NAME + " table created.");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.d(GroupTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
