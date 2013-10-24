package com.cs307.ezride;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.NavUtils;

public class EZRideLoginSignupActivity extends Activity {
	public final static String USERNAME_MESSAGE = "com.cs307.ezride.USERNAME_MESSAGE";
	public final static String PASSWORD_MESSAGE = "com.cs307.ezride.PASSWORD_MESSAGE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezride_login_signup);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ezride_login_signup, menu);
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
	
	public void ezrideSignUpButton_OnClick(View view) {
		Intent intent = new Intent(this, EZRideSignUpActivity.class);
		EditText username = (EditText)findViewById(R.id.ezride_enter_username);
		EditText password = (EditText)findViewById(R.id.ezride_enter_password);
		String message = username.getText().toString();
		intent.putExtra(USERNAME_MESSAGE, message);
		message = password.getText().toString();
		intent.putExtra(PASSWORD_MESSAGE, message);
		startActivity(intent);
	}
	
	public void ezrideLoginButton_OnClick(View view) {
		finish();
	}

}
