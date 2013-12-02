package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.EventDataSource;
import com.cs307.ezride.database.GroupDataSource;
import com.cs307.ezride.database.UserDataSource;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class SettingsActivity extends Activity implements ConnectionCallbacks,
						OnConnectionFailedListener, OnAccessRevokedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	
	private PlusClient mPlusClient = null;
	private UserDataSource mUserDataSource = null;
	private GroupDataSource mGroupDataSource = null;
	private EventDataSource mEventDataSource = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mUserDataSource = new UserDataSource(this);
		mGroupDataSource = new GroupDataSource(this);
		mGroupDataSource.open();
		mEventDataSource = new EventDataSource(this);
		mEventDataSource.open();
		
		mPlusClient = new PlusClient.Builder(this, this, this)
					.setActions("http://schemas.google.com/AddActivity")
					.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
					.build();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("SettingsActivity", "onStart");
		if (mPlusClient != null) {
			if (!mPlusClient.isConnected()) {
				mPlusClient.connect();
			}
		} else {
			mPlusClient = new PlusClient.Builder(this, this, this)
						.setActions("http://schemas.google.com/AddActivity")
						.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
						.build();
			mPlusClient.connect();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("SettingsActivity", "onResume");
		if (mPlusClient != null) {
			if (!mPlusClient.isConnected()) {
				mPlusClient.connect();
			}
		} else {
			mPlusClient = new PlusClient.Builder(this, this, this)
						.setActions("http://schemas.google.com/AddActivity")
						.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
						.build();
			mPlusClient.connect();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("SettingsActivity", "onPause");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("SettingsActivity", "onStop");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("SettingsActivity", "onDestroy");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
				mPlusClient = null;
			}
		}
		if (mGroupDataSource != null)
			mGroupDataSource.close();
		if (mEventDataSource != null)
			mEventDataSource.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		this.finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mPlusClient.connect();
        }
    }

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		//mPlusClient.connect();
		//finish();
		Intent intent = new Intent(this, GPlusLoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onConnected(Bundle connectionHint) {
		final Context context = this;
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				Bundle appActivities = new Bundle();
				appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "http://schemas.google.com/AddActivity");
				String scope = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/calendar";
				
				try {
					String token = GoogleAuthUtil.getToken(context, mPlusClient.getAccountName(), scope, appActivities);
					Log.d(MainFragmentActivity.class.getName(), token);
					Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(context).edit();
					prefEditor.putString("access_token", token);
					prefEditor.commit();
				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLVE_ERR);
				} catch (GoogleAuthException e) {
					return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.execute((Void)null);
	}

	@Override
	public void onDisconnected() {
		Log.d(MainFragmentActivity.class.getName(), "disconnected");
		Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
	}
	
	public void LogoutButton_onClick(View view) {
		Log.d(MainFragmentActivity.class.getName(), "tapped sign out");
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.revokeAccessAndDisconnect(this);
			mUserDataSource.deleteUser();
			mGroupDataSource.clear();
			mEventDataSource.clear();
			Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
		}
	}

}
