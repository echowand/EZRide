package com.cs307.ezride.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MessageDataSource {
	private SQLiteDatabase database = null;
	private DBHelper dbHelper = null;
	private String[] allColumns = { MessageTable.COLUMN_ID,
			MessageTable.COLUMN_CONTENT,
			MessageTable.COLUMN_POSTTIME,
			MessageTable.COLUMN_USERID,
			MessageTable.COLUMN_GROUPID };
	
	private Context context = null;
	
	public MessageDataSource(Context context) {
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
		database.execSQL("DROP TABLE IF EXISTS " + MessageTable.TABLE_NAME);
	}
	
	public void recreate() {
		database.execSQL("DROP TABLE IF EXISTS " + MessageTable.TABLE_NAME);
		MessageTable.onCreate(database);
		Log.d(MessageDataSource.class.getName(), MessageTable.TABLE_NAME + " table recreated.");
	}
	
	public Message addMessage(int id, String content, String posttime, int userid, int groupid) {
		Log.d(MessageDataSource.class.getName() + ".addMessage", "id=" + id + "\ncontent=" + content + "\nposttime=" + posttime + "\nuserid=" + userid + "\ngroupid=" + groupid);
		ContentValues values = new ContentValues();
		values.put(MessageTable.COLUMN_ID, id);
		values.put(MessageTable.COLUMN_CONTENT, content);
		values.put(MessageTable.COLUMN_POSTTIME, posttime);
		values.put(MessageTable.COLUMN_USERID, userid);
		values.put(MessageTable.COLUMN_GROUPID, groupid);
		
		database.insert(MessageTable.TABLE_NAME, null, values);
		Cursor cursor = database.query(MessageTable.TABLE_NAME, allColumns, MessageTable.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		
		Message m = CursorToMessage(cursor);
		cursor.close();
		
		return m;
	}
	
	public Message[] getMessages(int groupid) {
		Cursor cursor = null;
		try {
			String selection = "groupid = ?";
			String[] selectionArgs = new String[1];
			selectionArgs[0] = Integer.toString(groupid);
			cursor = database.query(MessageTable.TABLE_NAME, null, selection, selectionArgs, null, null, null);
		} catch (Exception e) {
			Log.e("EZRIDE_DATABASE_ERROR", "Retrieving messages failed.");
			return null;
		}
		cursor.moveToFirst();
		if (cursor.getCount() == 0)
			return null;
		else {
			Message[] messages = new Message[cursor.getCount()];
			
			for (int i = 0;i < cursor.getCount();i++){
				messages[i] = CursorToMessage(cursor);
				if (!cursor.moveToNext())
					break;
			}
			
			return messages;
		}
	}
	
	public static Message CursorToMessage(Cursor cursor) {
		if ((cursor == null)||(cursor.isAfterLast()))
			return null;
		Message message = new Message();
		message.setId(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_ID)));
		message.setContent(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_CONTENT)));
		message.setPostTime(cursor.getString(cursor.getColumnIndex(MessageTable.COLUMN_POSTTIME)));
		message.setUserId(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_USERID)));
		message.setGroupId(cursor.getInt(cursor.getColumnIndex(MessageTable.COLUMN_GROUPID)));
		return message;
	}
}
