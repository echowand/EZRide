package com.cs307.ezride;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
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
	private String serverResult;
	private List<NameValuePair> nameValPair = new ArrayList<NameValuePair>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ezride_sign_up);
		// Show the Up button in the action bar.
		setupActionBar();
		
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
		
		Toast.makeText(this, "Username: " + mUsername + "\nPassword: " + mPassword + "\nEmail: " + mEmail + "\nBio: " + mBio, Toast.LENGTH_SHORT).show();
		finish();
		
		nameValPair.add(new BasicNameValuePair("username", mUsername));
		nameValPair.add(new BasicNameValuePair("password", mPassword));
		nameValPair.add(new BasicNameValuePair("email", mEmail));
		nameValPair.add(new BasicNameValuePair("profile", mBio));
		
		new PostTask().execute("http://ezride-weiqing.rhcloud.com/register.php");
		Toast.makeText(this, serverResult, Toast.LENGTH_SHORT).show();
		finish();

		/*HttpURLConnection connection;
		OutputStreamWriter request = null;
		
		URL url = null;
		String response = null;
		String parameters = "username="+mUsername+"&password="+mPassword+"&email="+mEmail+"&profile="+mBio;
		
		try
		{
			url = new URL("ezride-weiqing.rhcloud.com/register.php");
			connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod("POST");    

            request = new OutputStreamWriter(connection.getOutputStream());
            request.write(parameters);
            request.flush();
            request.close();            
            String line = "";               
            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.                
            response = sb.toString();
            // You can perform UI operations here
            Toast.makeText(this,"Message from Server: \n"+ response, 0).show();             
            isr.close();
            reader.close();
			
		}
		catch (IOException e)
		{
			//err
		}*/
		
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
			Log.d("EZRIDE_SERVER_RESULT", result);
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			serverResult = result;
			//Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
		}
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
