package com.cs307.ezride;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class EZRideLoginActivity extends Activity {
	private String mUsername, mPassword, mRealName, mEmail, mPhoneNum, mAddress, mBio;
	public List<NameValuePair> nameValPair = new ArrayList<NameValuePair>();
	public static Context context = null;
	private DBHelper DB = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezride_login);
		context = this.getBaseContext();
		// Show the Up button in the action bar.
		//getActionBar().setDisplayHomeAsUpEnabled(true);
		
		DB = new DBHelper(getBaseContext());
		Intent intent = getIntent();
		mUsername = intent.getStringExtra(EZRideLoginSignupActivity.USERNAME_MESSAGE);
		mPassword = intent.getStringExtra(EZRideLoginSignupActivity.PASSWORD_MESSAGE);
		
		nameValPair.add(new BasicNameValuePair("username", mUsername));
		nameValPair.add(new BasicNameValuePair("password", mPassword));
		
		new PostTask().execute("http://ezride-weiqing.rhcloud.com/androidezlogin.php");
	}
	
	@Override
	protected void onDestroy() {
		if (DB != null)
			DB.close();
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
	
	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			String result = null;

			HttpPost httpPost = new HttpPost(params[0]);
			try {
				httpPost.setEntity(new UrlEncodedFormEntity(nameValPair));
				HttpResponse response = httpClient.execute(httpPost);
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuilder sb = new StringBuilder();
				String line = null;
				
				while ((line = reader.readLine()) != null)
					sb.append(line + "\n");
				result = sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (result == null)
				Log.d("EZRIDE_SERVER_RESULT", "result was empty");
			else
				Log.d("EZRIDE_SERVER_RESULT", result);
			
			if (result.compareTo("success\n") == 0) {
				Log.d("EZRIDE_LOGIN_USERINFO", "trying to get userinfo");
				String userinfo = null;
				
				httpPost = new HttpPost("http://ezride-weiqing.rhcloud.com/androidgetuserinfo.php");
				List<NameValuePair> nameVal = new ArrayList<NameValuePair>();
				nameVal.add(new BasicNameValuePair("username", mUsername));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameVal));
					HttpResponse response = httpClient.execute(httpPost);
					BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					StringBuilder sb = new StringBuilder();
					String line = null;
					
					while ((line = reader.readLine()) != null)
						sb.append(line + "\n");
					userinfo = sb.toString();
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("EZRIDE_LOGIN_USERINFO", "fail to get userinfo");
				}
				
				Log.d("EZRIDE_LOGIN_USERINFO", userinfo);
				mRealName = userinfo.substring(5, userinfo.indexOf("\n"));
				mEmail = userinfo.substring(userinfo.indexOf("email") + 6, userinfo.indexOf("\n", userinfo.indexOf("email")));
				mPhoneNum = userinfo.substring(userinfo.indexOf("phonenumber") + 12, userinfo.indexOf("\n", userinfo.indexOf("phonenumber")));
				mAddress = userinfo.substring(userinfo.indexOf("address") + 8, userinfo.indexOf("\n", userinfo.indexOf("address")));
				mBio = userinfo.substring(userinfo.indexOf("profile") + 8, userinfo.indexOf("\n", userinfo.indexOf("profile")));
				Log.d("EZRIDE_LOGIN_USERREALNAME", mRealName);
				Log.d("EZRIDE_LOGIN_USEREMAIL", mEmail);
				Log.d("EZRIDE_LOGIN_USERPHONE", mPhoneNum);
				Log.d("EZRIDE_LOGIN_USERADDRESS", mAddress);
				Log.d("EZRIDE_LOGIN_USERBIO", mBio);
				
				
				SQLiteDatabase db = DB.getWritableDatabase();
				ContentValues values = new ContentValues();
				values.put(DBHelper.KEY_ID, 0);
				values.put(DBHelper.KEY_UNAME, mUsername);
				values.put(DBHelper.KEY_PASS, mPassword);
				values.put(DBHelper.KEY_NAME, mRealName);
				values.put(DBHelper.KEY_EMAIL, mEmail);
				values.put(DBHelper.KEY_PHONE, mPhoneNum);
				values.put(DBHelper.KEY_ADDRESS, mAddress);
				values.put(DBHelper.KEY_BIO, mBio);
				
				long status = -1;
				try {
					status = db.insert(DBHelper.DATABASE_TABLE_NAMES[0], null, values);
					//Toast.makeText(EZRideLoginActivity.context, "Logged in successfully", Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				db.close();
				if (status != -1) {
					Intent intent = new Intent(EZRideLoginActivity.context, ProfileActivity.class);
					startActivity(intent);
					finish();
				} else {
					Toast.makeText(EZRideLoginActivity.context, "Log in error. Please try again.", Toast.LENGTH_LONG).show();
					finish();
				}
			} else {
				Toast.makeText(EZRideLoginActivity.context, result, Toast.LENGTH_LONG).show();
				finish();
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				Log.d("EZRIDE_SERVER_RESULT2", "result was empty");
			else
				Log.d("EZRIDE_SERVER_RESULT2", result);
		}
	}

}
