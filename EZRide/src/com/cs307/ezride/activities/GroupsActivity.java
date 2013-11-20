package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.*;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class GroupsActivity extends Activity {
	private ArrayList<String> mGroupNamesArray;
	private ArrayAdapter<String> mGroupNamesArrayAdapter;
	private ListView mGroupsList;
	private UserDataSource userdatasource = null;
	private GroupDataSource groupdatasource = null;
	private User user = null;
	private Group[] groups = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		userdatasource = new UserDataSource(this);
		groupdatasource = new GroupDataSource(this);
		groupdatasource.open();
		user = userdatasource.getUser();
		
		if (user != null) {
			refreshGroups();
		} else {
			Toast.makeText(getBaseContext(), "You're not logged in. Log in to view your groups.", Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		if (groupdatasource != null)
			groupdatasource.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.groups, menu);
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
		case R.id.groups_action_add:
			onAddButtonClick();
			return false;
		case R.id.groups_action_join:
			onJoinButtonClick();
			return false;
		case R.id.groups_action_refresh:
			onRefreshButtonClick();
			return false;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void onAddButtonClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		builder.setTitle("Add Group");
		builder.setMessage("Enter a name for the group:");
		builder.setView(input);
		
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("EZRIDE_ON_CLICK", "clicked: " + input.getText().toString());
				RequestParams params = new RequestParams();
				params.put("username", user.getUsername());
				params.put("password", user.getPassword());
				params.put("groupname", input.getText().toString());
				createGroup(params);
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void onJoinButtonClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		builder.setTitle("Join Group");
		builder.setMessage("Enter the name of the group:");
		builder.setView(input);
		
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("EZRIDE_ON_CLICK", "clicked: " + input.getText().toString());
				RequestParams params = new RequestParams();
				params.put("username", user.getUsername());
				params.put("password", user.getPassword());
				params.put("groupname", input.getText().toString());
				joinGroup(params);
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	private void onRefreshButtonClick() {
		refreshGroups();
	}
	
	private void createGroup(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidcreategroup.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				if (response.contains("success")) {
					refreshGroups();
				} else {
					Toast.makeText(getBaseContext(), response, Toast.LENGTH_LONG).show();
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				Toast.makeText(getBaseContext(), "Server error. Please try again.", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void joinGroup(RequestParams params) {
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidjoingroup.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				
				if (response.contains("success")) {
					refreshGroups();
				} else {
					Toast.makeText(getBaseContext(), "Failed to join group. Please try again.", Toast.LENGTH_LONG).show();
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.d("EZRIDE_SERVER_RESULT", response);
				Toast.makeText(getBaseContext(), "Server error. Please try again.", Toast.LENGTH_LONG).show();
			}
		});
	}
	
	private void refreshGroups() {
		RequestParams params = new RequestParams();
		params.put("username", user.getUsername());
		params.put("password", user.getPassword());
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidgetusergroups.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("GroupsActivity.refreshGroups().response.succeed", response);
				
				groupdatasource.recreate();
				int numgroups = Integer.parseInt(response.substring(10, response.indexOf("\n")));
				int groupidindex = response.indexOf("groupid");
				for (int i = 0;i < numgroups;i++) {
					int g_id = Integer.parseInt(response.substring(groupidindex + 8, response.indexOf("\n", groupidindex)));
					String g_name = response.substring(response.indexOf("name", groupidindex) + 5, response.indexOf("\n", response.indexOf("name", groupidindex)));
					String g_datecreated = response.substring(response.indexOf("datecreated", groupidindex) + 12, response.indexOf("\n", response.indexOf("datecreated", groupidindex)));
					groupidindex = (response.indexOf("\n", response.indexOf("datecreated", groupidindex)) + 1);
					Log.d("GroupsActivity.refreshGroups()", "id=" + g_id + "\nname=" + g_name + "\ndatecreated=" + g_datecreated + "\ngroupidindex=" + groupidindex);
					
					if (groupdatasource.createGroup(g_id, g_name, g_datecreated) == null) {
						Toast.makeText(getBaseContext(), "Refresh failed. Please try again.", Toast.LENGTH_LONG).show();
						break;
					}
				}
				
				groups = groupdatasource.getGroups();
				mGroupNamesArray = new ArrayList<String>();
				
				for (int i = 0;i < groups.length;i++) {
					Log.d("GroupsActivity.refreshGroups().groupid", Integer.toString(groups[i].getId()));
					mGroupNamesArray.add(groups[i].getName());
				}
				
				mGroupNamesArrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.groups_item_simple, mGroupNamesArray);
				mGroupsList = (ListView)findViewById(R.id.groups_activity_listview);
				mGroupsList.setAdapter(mGroupNamesArrayAdapter);
				
				mGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Toast.makeText(getBaseContext(), "You've selected " + mGroupNamesArray.get(position), Toast.LENGTH_LONG).show();
					}
				});
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.d("GroupsActivity.refreshGroups().response.fail", response);
			}
		});
	}
}
