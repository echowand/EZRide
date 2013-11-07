package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class EZRideLoginActivity extends Activity {
	private String mUsername, mPassword, mRealName, mEmail, mPhoneNum, mAddress, mBio;
	private int mId;
	public static Context context = null;
	private UserDataSource userdatasource = null;
	private GroupDataSource groupdatasource = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezride_login);
		context = this.getBaseContext();
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		userdatasource = new UserDataSource(this);
		groupdatasource = new GroupDataSource(this);
		userdatasource.open();
		groupdatasource.open();
		
		Intent intent = getIntent();
		mUsername = intent.getStringExtra(EZRideLoginSignupActivity.USERNAME_MESSAGE);
		mPassword = intent.getStringExtra(EZRideLoginSignupActivity.PASSWORD_MESSAGE);
		
		RequestParams params = new RequestParams();
		params.put("username", mUsername);
		params.put("password", mPassword);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidezlogin.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				if (response.contains("wrong password") || response.contains("fail to find user")) {
					Toast.makeText(EZRideLoginActivity.context, "Log in error. Please try again.", Toast.LENGTH_LONG).show();
					finish();
				} else {
					mId = Integer.parseInt(response.substring(3, response.indexOf("\n")));
					mRealName = response.substring(response.indexOf("name") + 5, response.indexOf("\n", response.indexOf("name")));
					mEmail = response.substring(response.indexOf("email") + 6, response.indexOf("\n", response.indexOf("email")));
					mPhoneNum = response.substring(response.indexOf("phonenumber") + 12, response.indexOf("\n", response.indexOf("phonenumber")));
					mAddress = response.substring(response.indexOf("address") + 8, response.indexOf("\n", response.indexOf("address")));
					mBio = response.substring(response.indexOf("profile") + 8, response.indexOf("\n", response.indexOf("profile")));
					Log.d("EZRIDE_SERVER_RESULT", mId + "\n" + mRealName + "\n" + mEmail + "\n" + mPhoneNum + "\n" + mAddress + "\n" + mBio);
					
					User retuser = userdatasource.createUser(mId, mUsername, mPassword, mRealName, mEmail, mPhoneNum, mAddress, mBio);
					
					if (retuser != null) {
						Intent intent = new Intent(EZRideLoginActivity.context, ProfileActivity.class);
						startActivity(intent);
						finish();
					} else {
						Toast.makeText(getBaseContext(), "Log in error. Please try again.", Toast.LENGTH_LONG).show();
						finish();
					}
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				
			}
		});
		
		client.post("http://ezride-weiqing.rhcloud.com/androidgetusergroups.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				int numgroups = Integer.parseInt(response.substring(10, response.indexOf("\n")));
				int groupidindex = response.indexOf("groupid");
				for (int i = 0;i < numgroups;i++) {
					int g_id = Integer.parseInt(response.substring(groupidindex + 8, response.indexOf("\n", groupidindex)));
					String g_name = response.substring(response.indexOf("name", groupidindex) + 5, response.indexOf("\n", response.indexOf("name", groupidindex)));
					String g_datecreated = response.substring(response.indexOf("datecreated", groupidindex) + 12, response.indexOf("\n", response.indexOf("datecreated", groupidindex)));
					groupidindex = (response.indexOf("\n", response.indexOf("datecreated", groupidindex)) + 1);
					
					if (groupdatasource.createGroup(g_id, g_name, g_datecreated) == null) {
						Toast.makeText(getBaseContext(), "Refresh failed. Please try again.", Toast.LENGTH_LONG).show();
						break;
					}
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
			}
		});
	}
	
	@Override
	protected void onDestroy() {
		if (userdatasource != null)
			userdatasource.close();
		if (groupdatasource != null)
			groupdatasource.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ezride_login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
