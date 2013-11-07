package com.cs307.ezride.database;

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
	
	/**
	 * Creates a user in the database. 
	 *
	 * @param	id			the id of the User from central database.
	 * @param	username	the username of the User.
	 * @param	password	the password of the User.
	 * @param	realname	the User's actual name.
	 * @param	email		the User's email address.
	 * @param	phone		the User's phone number.
	 * @param	address		the User's address.
	 * @param	bio			the User's general information.
	 */
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
		
		long insertId = database.insert(UserTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(UserTable.TABLE_NAME, allColumns, UserTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		User user = CursorToUser(cursor);
		cursor.close();
		return user;
	}
	
	/**
	 * Updates the user's info in the database. 
	 *
	 * @param	user	the User to be deleted.
	 * @return			0 if the user was not successfully updated, >0 otherwise.
	 */
	public int updateUser(User user) {
		ContentValues values = new ContentValues();
		values.put(UserTable.COLUMN_USERNAME, user.getUsername());
		values.put(UserTable.COLUMN_PASSWORD, user.getPassword());
		values.put(UserTable.COLUMN_REALNAME, user.getRealname());
		values.put(UserTable.COLUMN_EMAIL, user.getEmail());
		values.put(UserTable.COLUMN_PHONE, user.getPhone());
		values.put(UserTable.COLUMN_ADDRESS, user.getAddress());
		values.put(UserTable.COLUMN_BIO, user.getBio());
		
		String whereClause = UserTable.COLUMN_ID + "=?";
		String[] whereArgs = { Integer.toString(user.getId()) };
		return database.update(UserTable.TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Deletes the user's info from the database. 
	 *
	 * @param	user	the User to be deleted.
	 */
	public void deleteUser(User user) {
		int id = user.getId();
		database.delete(UserTable.TABLE_NAME, UserTable.COLUMN_ID + " = " + id, null);
		Log.w(UserTable.class.getName(), "User deleted with id: " + id);
	}
	
	/**
	 * Gets the user's info from the database. 
	 *
	 * @return      the User's info as a User object.
	 */
	public User getUser() {
		Cursor cursor = null;
		try {
			cursor = database.query(UserTable.TABLE_NAME, null, null, null, null, null, null);
		} catch (NullPointerException e) {
			Log.e("EZRIDE_DATABASE_ERROR", "Retrieving user failed.");
			return null;
		}
		if (cursor.getCount() == 0)
			return null;
		else
			return CursorToUser(cursor);
	}
	
	private User CursorToUser(Cursor cursor) {
		if (cursor == null)
			return null;
		cursor.moveToFirst();
		User user = new User();
		user.setId(cursor.getInt(cursor.getColumnIndex(UserTable.COLUMN_ID)));
		user.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_USERNAME)));
		user.setPassword(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_PASSWORD)));
		user.setRealname(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_REALNAME)));
		user.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_EMAIL)));
		user.setPhone(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_PHONE)));
		user.setAddress(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_ADDRESS)));
		user.setBio(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_BIO)));
		return user;
	}
}
