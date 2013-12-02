package com.cs307.ezride.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ezride.db";
	private static final int DATABASE_VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		GroupTable.onCreate(db);
		EventTable.onCreate(db);
		MessageTable.onCreate(db);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		GroupTable.onUpgrade(db, oldVersion, newVersion);
		EventTable.onUpgrade(db, oldVersion, newVersion);
		MessageTable.onUpgrade(db, oldVersion, newVersion);
	}

}
