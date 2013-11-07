package com.cs307.ezride.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupTable {
	public static final String TABLE_GROUP = "group";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DATECREATED = "datecreated";
	
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_GROUP + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY, "
			+ COLUMN_NAME + " TEXT, "
			+ COLUMN_DATECREATED + " TEXT"
			+ ");";
	
	public static void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
		Log.w(UserTable.class.getName(), TABLE_GROUP + " table created.");
	}
	
	public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(UserTable.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUP);
		onCreate(db);
	}
}
