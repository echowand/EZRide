package com.cs307.ezride;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
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
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

public class ProfileActivity extends Activity {
	private String mUsername, mUseremail, mUserphone, mUseraddress, mUserbio;
	private List<NameValuePair> nameValPair = new ArrayList<NameValuePair>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
		getActionBar().setDisplayHomeAsUpEnabled(true);
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
		
		nameValPair.add(new BasicNameValuePair("username", uname.toString()));
		
		new PostTask().execute("http://ezride-weiqing.rhcloud.com/getuserinfo.php?");
	}
	
	private class PostTask extends AsyncTask<String, Integer, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpClient httpClient = new DefaultHttpClient();
			URI uri = null;
			String result = null;
			try {
				uri = new URI(params[0]);
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (uri == null) {
				return "f-uri";
			}
			HttpPost httpPost = new HttpPost(uri);
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
				//Log.d("Login", "CPE");
			}
			if (result == null)
				Log.d("EZRIDE_SERVER_RESULT", "result was empty");
			else
				Log.d("EZRIDE_SERVER_RESULT", result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			//serverResult = result;
			//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		}
	}

}
