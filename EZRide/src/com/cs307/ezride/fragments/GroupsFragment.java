package com.cs307.ezride.fragments;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GroupsFragment extends Fragment {
	private View mView = null;
	private Activity mActivity = null;
	private UserDataSource mUserDataSource = null;
	private GroupDataSource mGroupDataSource = null;
	private User mUser = null;
	private Group selectedGroup = null;
	private Object mLockObject = new Object();
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.mActivity = activity;
	}
	
	/*@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (mUserDataSource == null)
			mUserDataSource = new UserDataSource(mActivity);
		if (mGroupDataSource == null) {
			mGroupDataSource = new GroupDataSource(mActivity);
			mGroupDataSource.open();
		}
	}*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_groups, container, false);
		
		if (mUserDataSource == null)
			mUserDataSource = new UserDataSource(mActivity);
		if (mGroupDataSource == null) {
			mGroupDataSource = new GroupDataSource(mActivity);
			mGroupDataSource.open();
		}
		
		if ((mUserDataSource != null)&&(mGroupDataSource != null)) {
			mUser = mUserDataSource.getUser();
			if (mUser != null) {
				ActionBar ab = mActivity.getActionBar();
				int selectedPosition = ab.getSelectedNavigationIndex();
				
				selectedGroup = mGroupDataSource.getGroup(selectedPosition);
				EditText nameField = (EditText)mView.findViewById(R.id.fragment_groups_name_field);
				EditText descriptionField = (EditText)mView.findViewById(R.id.fragment_groups_description_field);
				
				nameField.setText(selectedGroup.getName());
				descriptionField.setText(selectedGroup.getDescription());
				
				Button saveButton = (Button)mView.findViewById(R.id.fragment_groups_button_save);
				saveButton.setOnClickListener(new OnClickListener() {
					//@SuppressWarnings({ "rawtypes", "unchecked" })
					@SuppressWarnings({ "unchecked", "rawtypes" })
					@Override
					public void onClick(View v) {
						if (saveGroupToServer()) {
							AsyncTask task = new AsyncTask() {
								@Override
								protected String[] doInBackground(Object... params) {
									synchronized (mLockObject) {
										try {
											mLockObject.wait();
										} catch (InterruptedException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										return null;
									}
								}
								
								@Override
								protected void onPostExecute(Object result) {
									mActivity.recreate();
								}
							};
							task.execute((Void)null);
						}
					}
				});
				
				Button cancelButton = (Button)mView.findViewById(R.id.fragment_groups_button_cancel);
				cancelButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(),0);
						EditText nameField = (EditText)mActivity.findViewById(R.id.fragment_groups_name_field);
						EditText descriptionField = (EditText)mActivity.findViewById(R.id.fragment_groups_description_field);
						
						nameField.setText(selectedGroup.getName());
						descriptionField.setText(selectedGroup.getDescription());
					}
				});
				
				Button leaveButton = (Button)mView.findViewById(R.id.fragment_groups_button_exit);
				leaveButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						AsyncHttpClient client = new AsyncHttpClient();
						RequestParams params = new RequestParams();
						params.add("google_id", mUserDataSource.getUser().getGoogleId());
						params.add("groupid", Integer.toString(selectedGroup.getId()));
						params.add("userid", Integer.toString(mUserDataSource.getUser().getId()));
						client.post("http://ezride-weiqing.rhcloud.com/androidexitgroup.php", params, new AsyncHttpResponseHandler() {
							@Override
							public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
								String response = new String(responseBody);
								Log.d("GroupsFragment.response.succeed", response);
								
								if (response.contains("success")) {
									mGroupDataSource.deleteGroup(selectedGroup);
									mActivity.recreate();
								} else {
									Toast.makeText(mActivity, "Failed to leave group. Please try again.", Toast.LENGTH_SHORT).show();
								}
							}
							
							@Override
							public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
								String response = new String(responseBody);
								if (response != null) {
									Log.d("GroupsFragment.response.fail", response);
									Toast.makeText(mActivity, "Failed to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
				});
			}
		}
		return mView;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		if (mUserDataSource == null)
			mUserDataSource = new UserDataSource(mActivity);
		if (mGroupDataSource == null) {
			mGroupDataSource = new GroupDataSource(mActivity);
			mGroupDataSource.open();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mUserDataSource == null)
			mUserDataSource = new UserDataSource(mActivity);
		if (mGroupDataSource == null) {
			mGroupDataSource = new GroupDataSource(mActivity);
			mGroupDataSource.open();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mGroupDataSource != null) {
			mGroupDataSource.close();
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		if (mGroupDataSource != null) {
			mGroupDataSource.close();
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (mGroupDataSource != null) {
			mGroupDataSource.close();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGroupDataSource != null) {
			mGroupDataSource.close();
			mGroupDataSource = null;
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_groups_menu, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.fragment_groups_action_join:
			onJoinButtonClick();
			return true;
		case R.id.fragment_groups_action_add:
			onAddButtonClick();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public boolean notifyFragmentOfGroupChange(int groupIndex) {
		Log.d(GroupsFragment.class.getName() + ".notifyFragmentOfGroupChange", Integer.toString(groupIndex));
		selectedGroup = mGroupDataSource.getGroup(groupIndex);
		EditText nameField = (EditText)mActivity.findViewById(R.id.fragment_groups_name_field);
		EditText descriptionField = (EditText)mActivity.findViewById(R.id.fragment_groups_description_field);
		
		nameField.setText(selectedGroup.getName());
		descriptionField.setText(selectedGroup.getDescription());
		return true;
	}
	
	private boolean saveGroupToServer() {
		EditText nameField = (EditText)mView.findViewById(R.id.fragment_groups_name_field);
		EditText descriptionField = (EditText)mView.findViewById(R.id.fragment_groups_description_field);
		
		if (nameField.getText().toString() == "") {
			return false;
		}
		
		String a = null;
		if (descriptionField.getText().toString() != "") {
			a = descriptionField.getText().toString();
		}
		
		final String name = nameField.getText().toString();
		final String description = a;
		
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("google_id", mUserDataSource.getUser().getGoogleId());
		params.add("groupid", Integer.toString(selectedGroup.getId()));
		params.add("name", name);
		params.add("description", description);
		client.post("http://ezride-weiqing.rhcloud.com/androidupdategroupinfo.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("GroupsFragment.response.succeed", response);
				
				if (response.contains("success")) {
					if (saveGroupToLocalDB()) {
						Toast.makeText(mActivity, "Saved successfully.", Toast.LENGTH_SHORT).show();
						synchronized (mLockObject) {
							mLockObject.notify();
						}
					} else {
						Toast.makeText(mActivity, "Failed to save.", Toast.LENGTH_SHORT).show();
						synchronized (mLockObject) {
							mLockObject.notify();
						}
					}
				} else {
					Toast.makeText(mActivity, "Failed to save to server. Please try again.", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				if (response != null) {
					Log.d("GroupsFragment.response.fail", response);
					Toast.makeText(mActivity, "Failed to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		return true;
	}
	
	private boolean saveGroupToLocalDB() {
		EditText nameField = (EditText)mView.findViewById(R.id.fragment_groups_name_field);
		EditText descriptionField = (EditText)mView.findViewById(R.id.fragment_groups_description_field);
		
		String name = nameField.getText().toString();
		String description = descriptionField.getText().toString();
		
		selectedGroup.setName(name);
		selectedGroup.setDescription(description);
		
		if (mGroupDataSource.updateGroup(selectedGroup) == 0)
			return false;
		else
			return true;
	}
	
	private void onAddButtonClick() {
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		final View textEntryView = inflater.inflate(R.layout.fragment_groups_dialog_create_group, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		final EditText name_field = (EditText)textEntryView.findViewById(R.id.fragment_groups_dialog_create_group_name_field);
		final EditText description_field = (EditText)textEntryView.findViewById(R.id.fragment_groups_dialog_create_group_description_field);
		
		builder.setTitle("Add Group");
		builder.setMessage("Enter a name and description for the group:");
		builder.setView(textEntryView);
		
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//Log.d("EZRIDE_ON_CLICK", "clicked: " + input.getText().toString());
				RequestParams params = new RequestParams();
				params.put("google_id", mUser.getGoogleId());
				params.put("groupname", name_field.getText().toString());
				params.put("description", description_field.getText().toString());
				
				AsyncHttpClient client = new AsyncHttpClient();
				client.post("http://ezride-weiqing.rhcloud.com/androidcreategroup.php", params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
						String response = new String(responseBody);
						Log.d("EZRIDE_SERVER_RESULT", response);
						
						if (response.contains("success")) {
							refreshGroups();
						} else {
							Toast.makeText(mActivity, "Failed to create group. Please try again.", Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
						String response = new String(responseBody);
						Log.d("EZRIDE_SERVER_RESULT", response);
						Toast.makeText(mActivity, "Server error. Please try again.", Toast.LENGTH_SHORT).show();
					}
				});
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
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		final EditText input = new EditText(mActivity);
		
		builder.setTitle("Join Group");
		builder.setMessage("Enter the name of the group:");
		builder.setView(input);
		
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Log.d("EZRIDE_ON_CLICK", "clicked: " + input.getText().toString());
				RequestParams params = new RequestParams();
				params.put("google_id", mUser.getGoogleId());
				params.put("groupname", input.getText().toString());
				
				AsyncHttpClient client = new AsyncHttpClient();
				client.post("http://ezride-weiqing.rhcloud.com/androidjoingroup.php", params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
						String response = new String(responseBody);
						Log.d("EZRIDE_SERVER_RESULT", response);
						
						if (response.contains("success")) {
							refreshGroups();
						} else {
							Toast.makeText(mActivity, "Failed to join group. Please try again.", Toast.LENGTH_SHORT).show();
						}
					}
					
					@Override
					public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
						String response = new String(responseBody);
						Log.d("EZRIDE_SERVER_RESULT", response);
						Toast.makeText(mActivity, "Server error. Please try again.", Toast.LENGTH_SHORT).show();
					}
				});
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
	
	private void refreshGroups() {
		RequestParams params = new RequestParams();
		params.put(UserDataSource.PREF_GOOGLEID, mUser.getGoogleId());
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post("http://ezride-weiqing.rhcloud.com/androidgetusergroups.php", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, org.apache.http.Header[] headers, byte[] responseBody) {
				String response = new String(responseBody);
				Log.d("GroupsFragment.response.succeed", response);
				
				mGroupDataSource.recreate();
				int numgroups = Integer.parseInt(response.substring(10, response.indexOf("\n")));
				int groupidindex = response.indexOf("groupid");
				for (int i = 0;i < numgroups;i++) {
					int g_id = -1;
					String g_name = null, g_datecreated = null, g_description = null;
					try {
						g_id = Integer.parseInt(response.substring(groupidindex + 8, response.indexOf("\n", groupidindex)));
						if (g_id < 0) {
							Toast.makeText(mActivity, "Refresh failed.", Toast.LENGTH_SHORT).show();
							return;
						}
					} catch(NumberFormatException e) {
						g_id = -1;
					}
					try {
						g_name = response.substring(response.indexOf("name", groupidindex) + 5, response.indexOf("\n", response.indexOf("name", groupidindex)));
						if (g_name.equalsIgnoreCase("")) {
							g_name = null;
						}
					} catch (Exception e) {
						g_name = null;
					}
					try {
						g_description = response.substring(response.indexOf("description", groupidindex) + 12, response.indexOf("\n", response.indexOf("description", groupidindex)));
						if (g_description.equalsIgnoreCase("")) {
							g_description = null;
						}
					} catch (Exception e) {
						g_description = null;
					}
					try {
						g_datecreated = response.substring(response.indexOf("datecreated", groupidindex) + 12, response.indexOf("\n", response.indexOf("datecreated", groupidindex)));
						if (g_datecreated.equalsIgnoreCase("")) {
							g_datecreated = null;
						}
					} catch (Exception e) {
						g_datecreated = null;
					}
					groupidindex = (response.indexOf("\n", response.indexOf("datecreated", groupidindex)) + 1);
					Log.d("GroupsActivity.refreshGroups()", "id=" + g_id + "\nname=" + g_name + "\ndatecreated=" + g_datecreated + "\ngroupidindex=" + groupidindex);
					
					
					if (mGroupDataSource.addGroup(g_id, g_name, g_description, g_datecreated) == null) {
						Toast.makeText(getActivity().getBaseContext(), "Refresh failed. Please try again.", Toast.LENGTH_LONG).show();
						break;
					}
				}
				
				mActivity.recreate();
			}
			
			@Override
			public void onFailure(int statusCode, org.apache.http.Header[] headers, byte[] responseBody, Throwable error) {
				String response = new String(responseBody);
				Log.d("GroupsFragment.response.fail", response);
			}
		});
	}

}
