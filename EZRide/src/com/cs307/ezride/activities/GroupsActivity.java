package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.*;

import java.util.ArrayList;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;
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
	private ArrayList<String> mGroupNamesArray = null;
	private ArrayAdapter<String> mGroupNamesArrayAdapter = null;
	private ListView mGroupsList = null;
	private PullToRefreshLayout mPullToRefreshLayout = null;
	private UserDataSource mUserDataSource = null;
	private GroupDataSource mGroupDataSource = null;
	private User mUser = null;
	private Group[] mGroups = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mUserDataSource = new UserDataSource(this);
		mGroupDataSource = new GroupDataSource(this);
		mGroupDataSource.open();
		mUser = mUserDataSource.getUser();
		
		if (mUser != null) {
			refreshGroups();
			mPullToRefreshLayout = (PullToRefreshLayout)findViewById(R.id.activity_groups);
			ActionBarPullToRefresh.from(this)
						.allChildrenArePullable()
						.listener(new OnRefreshListener() {
							@Override
							public void onRefreshStarted(View view) {
								onRefreshPull();
							}
						})
						.setup(mPullToRefreshLayout);
		} else {
			Toast.makeText(getBaseContext(), "You're not logged in. Log in to view your groups.", Toast.LENGTH_LONG).show();
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
		}
	}
	
	@Override
	protected void onDestroy() {
		if (mGroupDataSource != null)
			mGroupDataSource.close();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_groups_menu, menu);
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
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
			return true;
		case R.id.groups_action_add:
			onAddButtonClick();
			return false;
		case R.id.groups_action_join:
			onJoinButtonClick();
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
				params.put(UserDataSource.PREF_GOOGLEID, mUser.getGoogleId());
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
				params.put(UserDataSource.PREF_GOOGLEID, mUser.getGoogleId());
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
	
	private void onRefreshPull() {
		refreshGroups();
		mPullToRefreshLayout.setRefreshComplete();
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
					if (response.contains("failed") && (response.contains("user") || response.contains("user2")))
						Toast.makeText(getBaseContext(), "Failed to verify user details.", Toast.LENGTH_LONG).show();
					else if ((response.contains("failed") && response.contains("insert")) || (response.contains("group already exists")))
						Toast.makeText(getBaseContext(), "Group name already exists. Please try another name.", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getBaseContext(), "Unknown server error.", Toast.LENGTH_LONG).show();
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
					if (response.contains("failed") && response.contains("user"))
						Toast.makeText(getBaseContext(), "Failed to verify user details.", Toast.LENGTH_LONG).show();
					else if (response.contains("failed") && response.contains("group"))
						Toast.makeText(getBaseContext(), "Group does not exist as entered. Perhaps try another variation?", Toast.LENGTH_LONG).show();
					else
						Toast.makeText(getBaseContext(), "Unknown server error.", Toast.LENGTH_LONG).show();
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
		params.put(UserDataSource.PREF_GOOGLEID, mUser.getGoogleId());
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidgetusergroups.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("GroupsActivity.refreshGroups().response.succeed", response);
				
				mGroupDataSource.recreate();
				int numgroups = Integer.parseInt(response.substring(10, response.indexOf("\n")));
				int groupidindex = response.indexOf("groupid");
				for (int i = 0;i < numgroups;i++) {
					int g_id = Integer.parseInt(response.substring(groupidindex + 8, response.indexOf("\n", groupidindex)));
					String g_name = response.substring(response.indexOf("name", groupidindex) + 5, response.indexOf("\n", response.indexOf("name", groupidindex)));
					String g_datecreated = response.substring(response.indexOf("datecreated", groupidindex) + 12, response.indexOf("\n", response.indexOf("datecreated", groupidindex)));
					groupidindex = (response.indexOf("\n", response.indexOf("datecreated", groupidindex)) + 1);
					Log.d("GroupsActivity.refreshGroups()", "id=" + g_id + "\nname=" + g_name + "\ndatecreated=" + g_datecreated + "\ngroupidindex=" + groupidindex);
					
					if (mGroupDataSource.addGroup(g_id, g_name, g_datecreated) == null) {
						Toast.makeText(getBaseContext(), "Refresh failed. Please try again.", Toast.LENGTH_LONG).show();
						break;
					}
				}
				
				mGroups = mGroupDataSource.getGroups();
				mGroupNamesArray = new ArrayList<String>();
				
				for (int i = 0;i < mGroups.length;i++) {
					Log.d("GroupsActivity.refreshGroups().groupid", Integer.toString(mGroups[i].getId()));
					mGroupNamesArray.add(mGroups[i].getName());
				}
				
				mGroupNamesArrayAdapter = new ArrayAdapter<String>(getBaseContext(), R.layout.activity_groups_item_simple, mGroupNamesArray);
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
