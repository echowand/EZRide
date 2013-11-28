package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.UserDataSource;
//import com.cs307.ezride.fragments.MapFragment;
import com.cs307.ezride.fragments.TestFragment;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainFragmentActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnAccessRevokedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private PlusClient mPlusClient = null;
	private SharedPreferences mPrefs = null;
	private UserDataSource mUserDataSource = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUserDataSource = new UserDataSource(this);
		
		mPlusClient = new PlusClient.Builder(this, this, this)
					.setActions("http://schemas.google.com/AddActivity")
					.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
					.build();
		
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.activity_main_fragment_placeholder, new TestFragment());
		ft.commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlusClient != null)
			mPlusClient.disconnect();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mPlusClient != null)
			mPlusClient.disconnect();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_fragment_menu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_map_view:
			MapViewButton_onClick();
			return false;
		case R.id.action_groups_view:
			GroupsViewButton_onClick();
			return false;
		case R.id.action_calendar_view:
			CalendarViewButton_onClick();
			return false;
            }
		return super.onOptionsItemSelected(item);
    }
	
	@Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mPlusClient.connect();
        }
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
					Editor prefEditor = mPrefs.edit();
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
			Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		mPlusClient.connect();
		finish();
	}
	
	private void MapViewButton_onClick() {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

	private void GroupsViewButton_onClick() {
        Intent intent = new Intent(this, GroupsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

	private void CalendarViewButton_onClick() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

}
