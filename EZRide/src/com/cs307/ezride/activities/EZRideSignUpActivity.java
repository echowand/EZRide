package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.loopj.android.http.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class EZRideSignUpActivity extends Activity {
	private String mUsername, mPassword, mEmail, mBio;
	public static Context context = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezride_sign_up);
		context = this.getBaseContext();
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		mUsername = intent.getStringExtra(EZRideLoginSignupActivity.USERNAME_MESSAGE);
		mPassword = intent.getStringExtra(EZRideLoginSignupActivity.PASSWORD_MESSAGE);
	}
	
	/*
	 * OnClick action for register button
	 */
	public void ezrideRegisterButton_onClick(View view){
		EditText email = (EditText)findViewById(R.id.ezride_enter_email);
		EditText bio = (EditText)findViewById(R.id.ezride_enter_bio);
		mEmail = email.getText().toString();
		mBio = bio.getText().toString();
		
		RequestParams params = new RequestParams();
		params.put("username", mUsername);
		params.put("password", mPassword);
		params.put("email", mEmail);
		params.put("profile", mBio);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidezregister.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				if (response.contains("New user added")) {
					Intent intent = new Intent(EZRideSignUpActivity.context, EZRideLoginActivity.class);
					intent.putExtra(EZRideLoginSignupActivity.USERNAME_MESSAGE, mUsername);
					intent.putExtra(EZRideLoginSignupActivity.PASSWORD_MESSAGE, mPassword);
					startActivity(intent);
					finish();
				} else if (response.contains("query")) {
					Log.d("EZRIDE_SIGNUP", "query failed");
					Toast.makeText(getBaseContext(), "Username already exists", Toast.LENGTH_LONG).show();
					finish();
				} else {
					Log.d("EZRIDE_SIGNUP", "register failed");
					Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
					finish();
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				Toast.makeText(getBaseContext(), "Registration failed", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ezride_sign_up, menu);
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
