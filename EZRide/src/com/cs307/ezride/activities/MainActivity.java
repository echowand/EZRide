package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;

public class MainActivity extends Activity {
	private DBHelper dbHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dbHelper = new DBHelper(this);
		dbHelper.getReadableDatabase();
		dbHelper.close();
	}
	
	@Override
	protected void onDestroy() {
		if (dbHelper != null)
			dbHelper.close();
		super.onDestroy();
	}

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	
	public void ezrideLogin(View view) {
		Intent intent = new Intent(this, EZRideLoginSignupActivity.class);
		startActivity(intent);
	}
	
	public void gplusLogin(View view) {
		Intent intent = new Intent(this, ProfileActivity.class);
		startActivity(intent);
	}
	
	public void fbLogin(View view) {
		Intent intent = new Intent(this, GroupsActivity.class);
		startActivity(intent);
	}
	
	public void testCalendarActivity(View view) {
		Intent intent = new Intent(this, CalendarActivity.class);
		startActivity(intent);
	}

}
