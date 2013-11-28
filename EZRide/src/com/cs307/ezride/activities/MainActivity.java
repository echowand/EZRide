package com.cs307.ezride.activities;

import com.cs307.ezride.database.*;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {
	private DBHelper dbHelper = null;
	private SharedPreferences mPrefs = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dbHelper = new DBHelper(this);
		dbHelper.getReadableDatabase();
		dbHelper.close();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			Log.d(MainActivity.class.getName(), "Google Play services is available.");
			if (mPrefs.getBoolean("isLoggedIn", false) == false) {
				Editor mPrefsEditor = mPrefs.edit();
				mPrefsEditor.putBoolean("isLoggedIn", false);
				mPrefsEditor.commit();
				Intent intent = new Intent(this, GPlusLoginActivity.class);
				startActivity(intent);
				finish();
			} else {
				Intent intent = new Intent(this, MainFragmentActivity.class);
				startActivity(intent);
				finish();
			}
		} else {
			Log.d(MainActivity.class.getName(), "Google Play services is unavailable.");
			Toast.makeText(this, "You do not have Google Play Services installed. Please install, then try again.", Toast.LENGTH_LONG).show();
			finish();
		}
		
		//setContentView(R.layout.activity_main);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null)
			dbHelper.close();
	}

}
