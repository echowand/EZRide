package com.cs307.ezride.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class EventDataSource {
	private SQLiteDatabase database = null;
	private DBHelper dbHelper = null;
	private String[] allColumns = { EventTable.COLUMN_ID,
			EventTable.COLUMN_GOOGLEID,
			EventTable.COLUMN_TITLE,
			EventTable.COLUMN_DETAILS,
			EventTable.COLUMN_START,
			EventTable.COLUMN_END };
	
	private Context context = null;
	
	public EventDataSource(Context context) {
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
		database.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE_NAME);
	}
	
	public void recreate() {
		database.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE_NAME);
		EventTable.onCreate(database);
		Log.d(EventDataSource.class.getName(), EventTable.TABLE_NAME + " table recreated.");
	}
	
	/**
	 * Adds an event to the database, and creates an Event object of the data in the process. 
	 *
	 * @param	id			the id of the event from central database.
	 * @param	title		the event's title.
	 * @param	details		the description of the event
	 * @param	start		the start time of the event
	 * @param	end			the end time of the event
	 * @return				the data of the event bundled into an Event object
	 */
	public Event addEvent(int id, int google_id, String title, String details, String start, String end) {
		Log.d(EventDataSource.class.getName() + ".createEvent", "id=" + id + "\ngoogle_id=" + google_id + "\ntitle=" + title + "\ndetails=" + details + "\nstart=" + start + "\nend=" + end);
		ContentValues values = new ContentValues();
		values.put(EventTable.COLUMN_ID, id);
		values.put(EventTable.COLUMN_GOOGLEID, google_id);
		values.put(EventTable.COLUMN_TITLE, title);
		values.put(EventTable.COLUMN_DETAILS, details);
		values.put(EventTable.COLUMN_START, start);
		values.put(EventTable.COLUMN_END, end);
		
		database.insert(EventTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(EventTable.TABLE_NAME, allColumns, EventTable.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		
		Event event = CursorToEvent(cursor);
		cursor.close();
		return event;
	}
	
	/**
	 * Adds an event to the database from an Event object. 
	 *
	 * @param	event		the data of an event bundled into an Event object
	 * @return				null if failure, the original object on success
	 */
	public Event addEvent(Event event) {
		Log.d(EventDataSource.class.getName() + ".createEvent", "id=" + event.getId() + "\ngoogle_id=" + event.getGoogleId() + "\ntitle=" + event.getTitle() + "\ndetails=" + event.getDetails() + "\nstart=" + event.getStart() + "\nend=" + event.getEnd());
		ContentValues values = new ContentValues();
		values.put(EventTable.COLUMN_ID, event.getId());
		values.put(EventTable.COLUMN_GOOGLEID, event.getGoogleId());
		values.put(EventTable.COLUMN_TITLE, event.getTitle());
		values.put(EventTable.COLUMN_DETAILS, event.getDetails());
		values.put(EventTable.COLUMN_START, event.getStart());
		values.put(EventTable.COLUMN_END, event.getEnd());
		
		long insertId = database.insert(EventTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(EventTable.TABLE_NAME, allColumns, EventTable.COLUMN_ID + " = " + insertId, null, null, null, null);
		
		if (cursor == null)
			return null;
		else
			return event;
	}
	
	/**
	 * Deletes an event in the database.
	 * 
	 * @param	id			the id of the event you wish to delete.
	 * @return				the number of rows deleted. If more than 1 is returned, there is a problem with the database.
	 */
	public int deleteEvent(int id) {
		return database.delete(EventTable.TABLE_NAME, EventTable.COLUMN_ID + " = " + id, null);
	}
	
	/**
	 * Deletes an event in the database
	 * 
	 * @param	event		the event you wish to delete.
	 * @return				the number of rows deleted. If more than 1 is returned, there may be a problem with the database.
	 */
	public int deleteEvent(Event event) {
		return database.delete(EventTable.TABLE_NAME, EventTable.COLUMN_ID + " = " + event.getId(), null);
	}
	
	/**
	 * Deletes several events in the database
	 * 
	 * @param	list		the list of events you wish to delete.
	 * @return				false if everything was deleted successfully. true if at least one couldn't be deleted (possibly an invalid event).
	 */
	public boolean deleteMultipleEvents(ArrayList<Event> list) {
		boolean returnval = false;
		
		for(Event event : list) {
			if (database.delete(EventTable.TABLE_NAME, EventTable.COLUMN_ID + " = " + event.getId(), null) == 0)
				returnval = true;
		}
		
		return returnval;
	}
	
	/**
	 * 
	 * @param	event		the event you wish to update.
	 * @return				the number of rows affected. If more than 1 is returned, there may be a problem with the database.
	 */
	public int updateEvent(Event event) {
		ContentValues values = new ContentValues();
		values.put(EventTable.COLUMN_TITLE, event.getTitle());
		values.put(EventTable.COLUMN_GOOGLEID, event.getGoogleId());
		values.put(EventTable.COLUMN_DETAILS, event.getDetails());
		values.put(EventTable.COLUMN_START, event.getStart());
		values.put(EventTable.COLUMN_END, event.getEnd());
		
		String whereClause = EventTable.COLUMN_ID + "=?";
		String[] whereArgs = { Integer.toString(event.getId()) };
		return database.update(EventTable.TABLE_NAME, values, whereClause, whereArgs);
	}
	
	private Event CursorToEvent(Cursor cursor) {
		if ((cursor == null)||(cursor.isAfterLast()))
			return null;
		Event event = new Event();
		event.setId(cursor.getInt(cursor.getColumnIndex(EventTable.COLUMN_ID)));
		event.setGoogleId(cursor.getInt(cursor.getColumnIndex(EventTable.COLUMN_GOOGLEID)));
		event.setTitle(cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_TITLE)));
		event.setDetails(cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_DETAILS)));
		event.setStart(cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_START)));
		event.setEnd(cursor.getString(cursor.getColumnIndex(EventTable.COLUMN_END)));
		return event;
	}
}
