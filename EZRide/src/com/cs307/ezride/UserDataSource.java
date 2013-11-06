package com.cs307.ezride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserDataSource {
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = { UserTable.COLUMN_ID,
			UserTable.COLUMN_USERNAME,
			UserTable.COLUMN_PASSWORD,
			UserTable.COLUMN_REALNAME,
			UserTable.COLUMN_EMAIL,
			UserTable.COLUMN_PHONE,
			UserTable.COLUMN_ADDRESS,
			UserTable.COLUMN_BIO };
	
	public UserDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public User createUser(int id, String username, String password, String realname, String email,
			String phone, String address, String bio) {
		ContentValues values = new ContentValues();
		values.put(UserTable.COLUMN_ID, id);
		values.put(UserTable.COLUMN_USERNAME, username);
		values.put(UserTable.COLUMN_PASSWORD, password);
		values.put(UserTable.COLUMN_REALNAME, realname);
		values.put(UserTable.COLUMN_EMAIL, email);
		values.put(UserTable.COLUMN_PHONE, phone);
		values.put(UserTable.COLUMN_ADDRESS, address);
		values.put(UserTable.COLUMN_BIO, bio);
		
		long insertId = database.insert(UserTable.TABLE_USER, null, values);
		Cursor cursor = database.query(UserTable.TABLE_USER, allColumns, UserTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		User user = CursorToUser(cursor);
		cursor.close();
		return user;
	}
	
	public void deleteUser(User user) {
		int id = user.getId();
		database.delete(UserTable.TABLE_USER, UserTable.COLUMN_ID + " = " + id, null);
		Log.w(UserTable.class.getName(), "User deleted with id: " + id);
	}
	
	private User CursorToUser(Cursor cursor) {
		User user = new User();
		user.setId(cursor.getInt(0));
		user.setUsername(cursor.getString(1));
		user.setPassword(cursor.getString(2));
		user.setRealname(cursor.getString(3));
		user.setEmail(cursor.getString(4));
		user.setPhone(cursor.getString(5));
		user.setAddress(cursor.getString(6));
		user.setBio(cursor.getString(7));
		return user;
	}
}
