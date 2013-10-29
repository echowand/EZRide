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
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	public String mUsername, mPassword, mUserRealName, mUseremail, mUserphone, mUseraddress, mUserbio;
	public List<NameValuePair> nameValPair = new ArrayList<NameValuePair>();
	public static String serverResult = null;
	private DBHelper DB = null;
	public static Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		context = this.getBaseContext();
		
		DB = new DBHelper(getBaseContext());
		SQLiteDatabase db = DB.getReadableDatabase();
		Cursor cursor = null;
		
		try {
			cursor = db.query(DBHelper.DATABASE_TABLE_NAMES[0], null, null, null, null, null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (cursor != null)
			cursor.moveToFirst();
		
		mUsername = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_UNAME));
		mPassword = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PASS));
		mUserRealName = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_NAME));
		mUseremail = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_EMAIL));
		mUserphone = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_PHONE));
		mUseraddress = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_ADDRESS));
		mUserbio = cursor.getString(cursor.getColumnIndex(DBHelper.KEY_BIO));
		/*Log.d("EZRIDE_PROFILE_USERNAME", mUsername);
		Log.d("EZRIDE_PROFILE_PASSWORD", mPassword);
		Log.d("EZRIDE_PROFILE_REALNAME", mUserRealName);
		Log.d("EZRIDE_PROFILE_EMAIL", mUseremail);
		Log.d("EZRIDE_PROFILE_PHONE", mUserphone);
		Log.d("EZRIDE_PROFILE_ADDRESS", mUseraddress);
		Log.d("EZRIDE_PROFILE_BIO", mUserbio);*/
		
		EditText editUserName = (EditText)findViewById(R.id.profile_username);
		EditText editRealName = (EditText)findViewById(R.id.profile_name_field);
		EditText editEmail = (EditText)findViewById(R.id.profile_email_field);
		EditText editPhone = (EditText)findViewById(R.id.profile_phone_field);
		EditText editAddress = (EditText)findViewById(R.id.profile_address_field);
		EditText editBio = (EditText)findViewById(R.id.profile_bio_field);
		
		editUserName.setText(mUsername);
		editRealName.setText(mUserRealName);
		editEmail.setText(mUseremail);
		editPhone.setText(mUserphone);
		editAddress.setText(mUseraddress);
		editBio.setText(mUserbio);
		
		db.close();
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
		getMenuInflater().inflate(R.menu.profile, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.profile_action_save:
			return saveProfile();
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean saveProfile() {
		nameValPair.clear();
		nameValPair.add(new BasicNameValuePair("username", mUsername));
		nameValPair.add(new BasicNameValuePair("password", mPassword));
		nameValPair.add(new BasicNameValuePair("name", mUserRealName));
		nameValPair.add(new BasicNameValuePair("email", mUseremail));
		nameValPair.add(new BasicNameValuePair("phonenumber", mUserphone));
		nameValPair.add(new BasicNameValuePair("address", mUseraddress));
		nameValPair.add(new BasicNameValuePair("profile", mUserbio));
		
		new PostTask().execute("http://ezride-weiqing.rhcloud.com/androidupdateuserinfo.php?");
		
		return true;
	}
	
	public void changePasswordButton_onClick(View view) {
		/*LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.profile_password_popup, null, false), 200, 200, true);
		pw.setOutsideTouchable(true);
		pw.showAtLocation(this.findViewById(R.id.activity_profile), Gravity.CENTER, 0, 0);*/
	}
	
	public void submitButton_onClick(View view) {
		EditText uname = (EditText)findViewById(R.id.profile_username);
		mUsername = uname.getText().toString();
		
		nameValPair.clear();
		nameValPair.add(new BasicNameValuePair("username", mUsername));
		
		new PostTask().execute("http://ezride-weiqing.rhcloud.com/androidgetuserinfo.php?");
	}
	
	private class PostTask extends AsyncTask<String, Integer, String> {
		private String[] _params;
		@Override
		protected String doInBackground(String... params) {
			_params = params;
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
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				Log.d("EZRIDE_SERVER_RESULT2", "result was empty");
			else
				Log.d("EZRIDE_SERVER_RESULT2", result);
			
			if (_params[0].contains("androidgetuserinfo")) {
				mUserRealName = result.substring(5, result.indexOf("\n"));
				mUseremail = result.substring(result.indexOf("email") + 6, result.indexOf("\n", result.indexOf("email")));
				mUserphone = result.substring(result.indexOf("phonenumber") + 12, result.indexOf("\n", result.indexOf("phonenumber")));
				mUseraddress = result.substring(result.indexOf("address") + 8, result.indexOf("\n", result.indexOf("address")));
				mUserbio = result.substring(result.indexOf("profile") + 8, result.indexOf("\n", result.indexOf("profile")));
				
				EditText editUserName = (EditText)findViewById(R.id.profile_username);
				EditText editRealName = (EditText)findViewById(R.id.profile_name_field);
				EditText editEmail = (EditText)findViewById(R.id.profile_email_field);
				EditText editPhone = (EditText)findViewById(R.id.profile_phone_field);
				EditText editAddress = (EditText)findViewById(R.id.profile_address_field);
				EditText editBio = (EditText)findViewById(R.id.profile_bio_field);
				
				editUserName.setText(mUsername);
				editRealName.setText(mUserRealName);
				editEmail.setText(mUseremail);
				editPhone.setText(mUserphone);
				editAddress.setText(mUseraddress);
				editBio.setText(mUserbio);
			} else if (_params[0].contains("androidupdateuserinfo")) {
				if (result.contains("success")) {
					Toast.makeText(ProfileActivity.context, "Profile updated successfully", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ProfileActivity.context, "Profile update failed", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

}
