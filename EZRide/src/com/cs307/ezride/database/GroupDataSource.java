package com.cs307.ezride.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupDataSource {
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	private String[] allColumns = { GroupTable.COLUMN_ID,
			GroupTable.COLUMN_NAME,
			GroupTable.COLUMN_DATECREATED };
	
	public GroupDataSource(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		dbHelper.close();
	}
	
	/**
	 * Creates a group in the database. 
	 *
	 * @param	id			the id of the group from central database.
	 * @param	name		the group's actual name.
	 * @param	datecreated	the group's creation date and time.
	 */
	public Group createGroup(int id, String name, String datecreated) {
		ContentValues values = new ContentValues();
		values.put(GroupTable.COLUMN_ID, id);
		values.put(GroupTable.COLUMN_NAME, name);
		values.put(GroupTable.COLUMN_DATECREATED, datecreated);
		
		long insertId = database.insert(GroupTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(GroupTable.TABLE_NAME, allColumns, GroupTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		Group group = CursorToGroup(cursor);
		cursor.close();
		return group;
	}
	
	public int updateGroup(Group group) {
		ContentValues values = new ContentValues();
		values.put(GroupTable.COLUMN_NAME, group.getName());
		values.put(GroupTable.COLUMN_DATECREATED, group.getDateCreated());
		
		String whereClause = GroupTable.COLUMN_ID + "=?";
		String[] whereArgs = { Integer.toString(group.getId()) };
		return database.update(GroupTable.TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Deletes the group's info from the database. 
	 *
	 * @param	user	the group to be deleted.
	 */
	public void deleteGroup(Group group) {
		int id = group.getId();
		database.delete(GroupTable.TABLE_NAME, UserTable.COLUMN_ID + " = " + id, null);
		Log.w(UserTable.class.getName(), "Group deleted with id: " + id);
	}
	
	/**
	 * Gets the user's groups from the database. 
	 *
	 * @return      the User's groups as an array of Group objects.
	 */
	public Group[] getGroups() {
		Cursor cursor = null;
		try {
			cursor = database.query(GroupTable.TABLE_NAME, null, null, null, null, null, null);
		} catch (NullPointerException e) {
			Log.e("EZRIDE_DATABASE_ERROR", "Retrieving user failed.");
			return null;
		}
		cursor.moveToFirst();
		if (cursor.getCount() == 0)
			return null;
		else {
			Group[] group = new Group[cursor.getCount()];
			
			for (int i = 0;i < cursor.getCount();i++) {
				group[i] = CursorToGroup(cursor);
				if (cursor.moveToNext() == false)
					break;
			}
			
			return group;
		}
	}
	
	private Group CursorToGroup(Cursor cursor) {
		if (cursor == null)
			return null;
		cursor.moveToFirst();
		Group group = new Group();
		group.setId(cursor.getInt(cursor.getColumnIndex(GroupTable.COLUMN_ID)));
		group.setName(cursor.getString(cursor.getColumnIndex(GroupTable.COLUMN_NAME)));
		group.setDateCreated(cursor.getString(cursor.getColumnIndex(GroupTable.COLUMN_DATECREATED)));
		return group;
	}
}
