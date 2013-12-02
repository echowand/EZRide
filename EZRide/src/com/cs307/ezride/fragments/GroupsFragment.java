package com.cs307.ezride.fragments;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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
						saveGroupToServer();
						//mActivity.recreate();
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
	
	public boolean notifyFragmentOfGroupChange(int groupIndex) {
		Log.d(GroupsFragment.class.getName() + ".notifyFragmentOfGroupChange", Integer.toString(groupIndex));
		selectedGroup = mGroupDataSource.getGroup(groupIndex);
		EditText nameField = (EditText)mActivity.findViewById(R.id.fragment_groups_name_field);
		EditText descriptionField = (EditText)mActivity.findViewById(R.id.fragment_groups_description_field);
		
		nameField.setText(selectedGroup.getName());
		descriptionField.setText(selectedGroup.getDescription());
		return true;
	}
	
	private void saveGroupToServer() {
		EditText nameField = (EditText)mView.findViewById(R.id.fragment_groups_name_field);
		EditText descriptionField = (EditText)mView.findViewById(R.id.fragment_groups_description_field);
		
		final String name = nameField.getText().toString();
		final String description = descriptionField.getText().toString();
		
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
				Log.d("GroupsActivity.refreshGroups().response.succeed", response);
				
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
				Log.d("GroupsActivity.refreshGroups().response.fail", response);
				
				Toast.makeText(mActivity, "Failed to connect to server. Please try again later.", Toast.LENGTH_SHORT).show();
			}
		});
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

}
