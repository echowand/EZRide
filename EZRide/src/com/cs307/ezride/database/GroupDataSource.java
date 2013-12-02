package com.cs307.ezride.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GroupDataSource {
	private SQLiteDatabase database = null;
	private DBHelper dbHelper = null;
	private String[] allColumns = { GroupTable.COLUMN_ID,
			GroupTable.COLUMN_NAME,
			GroupTable.COLUMN_DESCRIPTION,
			GroupTable.COLUMN_DATECREATED };
	
	private Context context = null;
	
	public GroupDataSource(Context context) {
		dbHelper = new DBHelper(context);
		this.context = context;
	}
	
	public void open() throws SQLException {
		if (dbHelper == null)
			dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() {
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}
	
	public void clear() {
		database.execSQL("DROP TABLE IF EXISTS " + GroupTable.TABLE_NAME);
	}
	
	public void recreate() {
		database.execSQL("DROP TABLE IF EXISTS " + GroupTable.TABLE_NAME);
		GroupTable.onCreate(database);
		Log.d(GroupDataSource.class.getName(), GroupTable.TABLE_NAME + " table recreated.");
	}
	
	/**
	 * Adds a group to the database, and creates a Group object of the data in the process. 
	 *
	 * @param	id				the id of the group from central database.
	 * @param	name			the group's actual name.
	 * @param	description		the group's description.
	 * @param	datecreated		the group's creation date and time.
	 * @return					the data of the group bundled into a Group object
	 */
	public Group addGroup(int id, String name, String description, String datecreated) {
		Log.d("GroupDataSource.createGroup", "id=" + id + "\nname=" + name + "\ndatecreated=" + datecreated);
		ContentValues values = new ContentValues();
		if (id < 0)
			return null;
		else
			values.put(GroupTable.COLUMN_ID, id);
		if (name == null)
			values.putNull(GroupTable.COLUMN_NAME);
		else
			values.put(GroupTable.COLUMN_NAME, name);
		if (description == null)
			values.putNull(GroupTable.COLUMN_DESCRIPTION);
		else
			values.put(GroupTable.COLUMN_DESCRIPTION, description);
		if (datecreated == null)
			values.putNull(GroupTable.COLUMN_DATECREATED);
		else
			values.put(GroupTable.COLUMN_DATECREATED, datecreated);
		
		long insertId = database.insert(GroupTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(GroupTable.TABLE_NAME, allColumns, GroupTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		Group group = CursorToGroup(cursor);
		cursor.close();
		return group;
	}
	
	/**
	 * Adds a group to the database, and creates a Group object of the data in the process. 
	 *
	 * @param	id				the id of the group from central database.
	 * @param	name			the group's actual name.
	 * @param	datecreated		the group's creation date and time.
	 * @return					null if failure, the original object on success
	 */
	public Group addGroup(Group group) {
		Log.d("GroupDataSource.createGroup", "id=" + group.getId() + "\nname=" + group.getName() + "\ndatecreated=" + group.getDateCreated());
		ContentValues values = new ContentValues();
		values.put(GroupTable.COLUMN_ID, group.getId());
		values.put(GroupTable.COLUMN_NAME, group.getName());
		values.put(GroupTable.COLUMN_DESCRIPTION, group.getDescription());
		values.put(GroupTable.COLUMN_DATECREATED, group.getDateCreated());
		
		long insertId = database.insert(GroupTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(GroupTable.TABLE_NAME, allColumns, GroupTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		
		if (cursor == null)
			return null;
		else
			return group;
	}
	
	/**
	 * Deletes a group from the database. 
	 *
	 * @param	id				the id of the group from central database.
	 * @return					the number of rows deleted. If more than 1 is returned, there is a problem with the database.
	 */
	public int deleteGroup(int id) {
		return database.delete(GroupTable.TABLE_NAME, GroupTable.COLUMN_ID + " = " + id, null);
	}
		
	/**
	 * Deletes a group from the database. 
	 *
	 * @param	group			the group you wish to delete.
	 * @return					the number of rows deleted. If more than 1 is returned, there is a problem with the database.
	 */
	public int deleteGroup(Group group) {
		return database.delete(GroupTable.TABLE_NAME, GroupTable.COLUMN_ID + " = " + group.getId(), null);
	}
	
	public int updateGroup(Group group) {
		ContentValues values = new ContentValues();
		values.put(GroupTable.COLUMN_NAME, group.getName());
		values.put(GroupTable.COLUMN_DESCRIPTION, group.getDescription());
		values.put(GroupTable.COLUMN_DATECREATED, group.getDateCreated());
		
		String whereClause = GroupTable.COLUMN_ID + "=?";
		String[] whereArgs = { Integer.toString(group.getId()) };
		return database.update(GroupTable.TABLE_NAME, values, whereClause, whereArgs);
	}
	
	/**
	 * Gets a group from the database. 
	 *
	 * @param	position	a value corresponding to the position of the group you'd like to return in the database.
	 * @return      		the group as a Group object, or null if an error.
	 */
	public Group getGroup(int position) {
		Cursor cursor = null;
		
		try {
			cursor = database.query(GroupTable.TABLE_NAME, null, null, null, null, null, null);
		} catch (Exception e) {
			Log.e("EZRIDE_DATABASE_ERROR", "Retrieving user failed.");
			e.printStackTrace();
			return null;
		}
		
		if (cursor != null) {
			cursor.moveToPosition(position);
			return CursorToGroup(cursor);
		}
		
		return null;
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
			Log.e("EZRIDE_DATABASE_ERROR", "Retrieving groups failed.");
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
	
	private static Group CursorToGroup(Cursor cursor) {
		if ((cursor == null)||(cursor.isAfterLast()))
			return null;
		Group group = new Group();
		group.setId(cursor.getInt(cursor.getColumnIndex(GroupTable.COLUMN_ID)));
		group.setName(cursor.getString(cursor.getColumnIndex(GroupTable.COLUMN_NAME)));
		group.setDescription(cursor.getString(cursor.getColumnIndex(GroupTable.COLUMN_DESCRIPTION)));
		group.setDateCreated(cursor.getString(cursor.getColumnIndex(GroupTable.COLUMN_DATECREATED)));
		return group;
	}
}
