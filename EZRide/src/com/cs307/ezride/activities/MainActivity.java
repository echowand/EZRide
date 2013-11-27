package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener, ConnectionCallbacks, OnConnectionFailedListener, OnAccessRevokedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private ProgressDialog mConnectionProgressDialog = null;
	private PlusClient mPlusClient = null;
	private ConnectionResult mConnectionResult = null;
	private DBHelper dbHelper = null;
	private SharedPreferences mPrefs = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.gplus_sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.gplus_logout_button).setVisibility(View.INVISIBLE);
		
		dbHelper = new DBHelper(this);
		dbHelper.getReadableDatabase();
		dbHelper.close();
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {
			Log.d(MainActivity.class.getName(), "Google Play services is available.");
			mPlusClient = new PlusClient.Builder(this, this, this)
						.setActions("http://schemas.google.com/AddActivity")
						.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
						.build();
			
			findViewById(R.id.gplus_sign_in_button).setOnClickListener(this);
			findViewById(R.id.gplus_logout_button).setOnClickListener(this);
			
			mConnectionProgressDialog = new ProgressDialog(this);
			mConnectionProgressDialog.setMessage("Signing in....");
		} else {
			Log.d(MainActivity.class.getName(), "Google Play services is unavailable.");
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null)
			dbHelper.close();
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
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mConnectionResult = null;
            mPlusClient.connect();
        } else {
        	mConnectionProgressDialog.dismiss();
        }
    }
	
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (mConnectionProgressDialog.isShowing()) {
			// The user clicked the sign-in button already. Start to resolve
			// connection errors. Wait until onConnected() to dismiss the
			// connection dialog.
			if (result.hasResolution()) {
				try {
					result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
				} catch (SendIntentException e) {
					mPlusClient.connect();
				}
			}
		}
		// Save the result and resolve the connection failure upon a user click.
		mConnectionResult = result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void onConnected(Bundle connectionHint) {
		mConnectionProgressDialog.dismiss();
		final String accountName = mPlusClient.getAccountName();
        Toast.makeText(this, accountName + " is connected.", Toast.LENGTH_LONG).show();
        
        findViewById(R.id.gplus_sign_in_button).setVisibility(View.INVISIBLE);
		findViewById(R.id.gplus_logout_button).setVisibility(View.VISIBLE);
        
        final Context context = this;
        AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				Bundle appActivities = new Bundle();
				appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "http://schemas.google.com/AddActivity");
				String scope = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/calendar";
				Log.d(MainActivity.class.getName(), scope);
				try {
					String token = GoogleAuthUtil.getToken(context, accountName, scope, appActivities);
					Log.d(MainActivity.class.getName(), token);
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
        
        RequestParams params = new RequestParams();
        params.put("google_id", mPlusClient.getCurrentPerson().getId());
        params.put("name", mPlusClient.getCurrentPerson().getDisplayName());
        params.put("avatarURL", mPlusClient.getCurrentPerson().getImage().getUrl());
        params.put("email", mPlusClient.getAccountName());
        params.put("profile", mPlusClient.getCurrentPerson().getAboutMe());
        
        AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidgpluslogin.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getBaseContext(), "Server error. Please try again later.", Toast.LENGTH_LONG).show();
			}
		});
	}

	@Override
	public void onDisconnected() {
		Log.d(MainActivity.class.getName(), "disconnected");
		Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onAccessRevoked(ConnectionResult status) {
		mPlusClient.connect();
		findViewById(R.id.gplus_sign_in_button).setVisibility(View.VISIBLE);
		findViewById(R.id.gplus_logout_button).setVisibility(View.INVISIBLE);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.gplus_sign_in_button:
			Log.d(MainActivity.class.getName(), "tapped sign in");
			if (!mPlusClient.isConnected()) {
	            if (mConnectionResult == null) {
	                mConnectionProgressDialog.show();
	            } else {
	                try {
	                    mConnectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
	                } catch (SendIntentException e) {
	                    // Try connecting again.
	                    mConnectionResult = null;
	                    mPlusClient.connect();
	                }
	            }
	        }
			break;
		case R.id.gplus_logout_button:
			Log.d(MainActivity.class.getName(), "tapped sign out");
			if (mPlusClient.isConnected()) {
				mPlusClient.clearDefaultAccount();
				mPlusClient.revokeAccessAndDisconnect(this);
			}
			break;
		default:
			break;
		}
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
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
	
	public void testMapActivity(View view) {
		Intent intent = new Intent(this, MapActivity.class);
		startActivity(intent);
	} */

}
