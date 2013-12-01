package com.cs307.ezride.fragments;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GroupsFragment extends Fragment {
	private ArrayList<String> mGroupNamesArray = null;
	private ArrayAdapter<String> mGroupNamesArrayAdapter = null;
	private ListView mGroupsList = null;
	private PullToRefreshLayout mPullToRefreshLayout = null;
	private UserDataSource mUserDataSource = null;
	private GroupDataSource mGroupDataSource = null;
	private User mUser = null;
	private Group[] mGroups = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mGroupDataSource != null)
			mGroupDataSource.close();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_groups, container, false);
		mUserDataSource = new UserDataSource(getActivity().getBaseContext());
		mGroupDataSource = new GroupDataSource(getActivity().getBaseContext());
		mGroupDataSource.open();
		mUser = mUserDataSource.getUser();
		
		if (mUser != null) {
			refreshGroups();
			mPullToRefreshLayout = (PullToRefreshLayout)view;
			ActionBarPullToRefresh.from(getActivity())
							.allChildrenArePullable()
							.listener(new OnRefreshListener() {
								@Override
								public void onRefreshStarted(View view) {
									onRefreshPull();
								}
							})
							.setup(mPullToRefreshLayout);
		}
		return view;
	}
	
	private void onRefreshPull() {
		refreshGroups();
		mPullToRefreshLayout.setRefreshComplete();
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
					int g_id = -1;
					String g_name = null, g_datecreated = null, g_description = null;
					try {
						g_id = Integer.parseInt(response.substring(groupidindex + 8, response.indexOf("\n", groupidindex)));
					} catch(NumberFormatException e) {
						g_id = -1;
					}
					try {
						g_name = response.substring(response.indexOf("name", groupidindex) + 5, response.indexOf("\n", response.indexOf("name", groupidindex)));
					} catch (Exception e) {
						g_name = null;
					}
					try {
						g_description = response.substring(response.indexOf("description", groupidindex) + 12, response.indexOf("\n", response.indexOf("description", groupidindex)));
					} catch (Exception e) {
						g_description = null;
					}
					try {
						g_datecreated = response.substring(response.indexOf("datecreated", groupidindex) + 12, response.indexOf("\n", response.indexOf("datecreated", groupidindex)));
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
				
				mGroups = mGroupDataSource.getGroups();
				mGroupNamesArray = new ArrayList<String>();
				
				for (int i = 0;i < mGroups.length;i++) {
					Log.d("GroupsActivity.refreshGroups().groupid", Integer.toString(mGroups[i].getId()));
					mGroupNamesArray.add(mGroups[i].getName());
				}
				
				mGroupNamesArrayAdapter = new CustomArrayAdapter(getActivity(), R.layout.fragment_groups_item_simple, mGroupNamesArray);
				mGroupsList = (ListView)getView().findViewById(R.id.fragment_groups_listview);
				mGroupsList.setAdapter(mGroupNamesArrayAdapter);
				
				mGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						Toast.makeText(getActivity().getBaseContext(), "You've selected " + mGroupNamesArray.get(position), Toast.LENGTH_LONG).show();
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
	
	private class CustomArrayAdapter extends ArrayAdapter<String> {
		Context context = null;
		
		public CustomArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
		}
		
		private class ViewHolder {
			TextView txtGroupName;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			String rowItem = getItem(position);
			
			LayoutInflater i = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = i.inflate(R.layout.fragment_groups_item_simple, null);
				holder = new ViewHolder();
				holder.txtGroupName = (TextView)convertView.findViewById(R.id.fragment_groups_name_textview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder)convertView.getTag();
			}
			
			holder.txtGroupName.setText(rowItem);
			
			return convertView;
		}
	}
	
	public void notifyFragmentOfGroupChange(int groupIndex) {
		
	}

}
