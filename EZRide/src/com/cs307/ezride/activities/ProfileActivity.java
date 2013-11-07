package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.User;
import com.cs307.ezride.database.UserDataSource;
import com.loopj.android.http.*;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ProfileActivity extends Activity {
	public String mUsername, mPassword, mUserRealName, mUseremail, mUserphone, mUseraddress, mUserbio;
	private UserDataSource datasource = null;
	private User user = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		datasource = new UserDataSource(this);
		datasource.open();
		user = datasource.getUser();
		
		//Log.d("EZRIDE_USER", user.toString());
		
		if (user != null) {
			mUsername = user.getUsername();
			mPassword = user.getPassword();
			mUserRealName = user.getRealname();
			mUseremail = user.getEmail();
			mUserphone = user.getPhone();
			mUseraddress = user.getAddress();
			mUserbio = user.getBio();
			
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
		}
	}
	
	@Override
	protected void onDestroy() {
		if (datasource != null)
			datasource.close();
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
			saveProfile();
			return false;
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void saveProfile() {
		RequestParams params = new RequestParams();
		
		EditText editRealName = (EditText)findViewById(R.id.profile_name_field);
		EditText editEmail = (EditText)findViewById(R.id.profile_email_field);
		EditText editPhone = (EditText)findViewById(R.id.profile_phone_field);
		EditText editAddress = (EditText)findViewById(R.id.profile_address_field);
		EditText editBio = (EditText)findViewById(R.id.profile_bio_field);
		
		params.put("username", mUsername);
		params.put("password", mPassword);
		params.put("name", editRealName.getText().toString());
		params.put("email", editEmail.getText().toString());
		params.put("phonenumber", editPhone.getText().toString());
		params.put("address", editAddress.getText().toString());
		params.put("profile", editBio.getText().toString());
		
		user.setRealname(editRealName.getText().toString());
		user.setEmail(editEmail.getText().toString());
		user.setPhone(editPhone.getText().toString());
		user.setAddress(editAddress.getText().toString());
		user.setBio(editBio.getText().toString());
		
		if (datasource.updateUser(user) > 0) {
			AsyncHttpClient client = new AsyncHttpClient();
			client.post("http://ezride-weiqing.rhcloud.com/androidupdateuserinfo.php", params, new AsyncHttpResponseHandler() {
				@Override
				public void onStart() {
					super.onStart();
				}
				
				@Override
				public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
					String response = new String(responseBody);
					Log.d("EZRIDE_SERVER_RESULT", response);
					
					if (response.contains("success")) {
						Toast.makeText(getBaseContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getBaseContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
					}
				}
				
				@Override
				public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
					Toast.makeText(getBaseContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			Toast.makeText(getBaseContext(), "Profile update failed", Toast.LENGTH_SHORT).show();
		}
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
		
		RequestParams params = new RequestParams();
		params.put("username", mUsername);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidgetuserinfo.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				if(response.contains("fail to find user")) {
					Toast.makeText(getBaseContext(), "User does not exist", Toast.LENGTH_LONG).show();
				} else {
					EditText editRealName = (EditText)findViewById(R.id.profile_name_field);
					EditText editEmail = (EditText)findViewById(R.id.profile_email_field);
					EditText editPhone = (EditText)findViewById(R.id.profile_phone_field);
					EditText editAddress = (EditText)findViewById(R.id.profile_address_field);
					EditText editBio = (EditText)findViewById(R.id.profile_bio_field);
				
					editRealName.setText(response.substring(5, response.indexOf("\n")));
					editEmail.setText(response.substring(response.indexOf("email") + 6, response.indexOf("\n", response.indexOf("email"))));
					editPhone.setText(response.substring(response.indexOf("phonenumber") + 12, response.indexOf("\n", response.indexOf("phonenumber"))));
					editAddress.setText(response.substring(response.indexOf("address") + 8, response.indexOf("\n", response.indexOf("address"))));
					editBio.setText(response.substring(response.indexOf("profile") + 8, response.indexOf("\n", response.indexOf("profile"))));
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Toast.makeText(getBaseContext(), "Refreshing failed. The response was: " + response, Toast.LENGTH_LONG).show();
			}
		});
	}
}
