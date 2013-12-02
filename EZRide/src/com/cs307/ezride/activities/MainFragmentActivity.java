package com.cs307.ezride.activities;

import java.util.ArrayList;
import java.util.List;

import com.cs307.ezride.R;
import com.cs307.ezride.database.*;
import com.cs307.ezride.fragments.*;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.PlusClient;
import com.google.android.gms.plus.PlusClient.OnAccessRevokedListener;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainFragmentActivity extends FragmentActivity implements ConnectionCallbacks,
				OnConnectionFailedListener, OnAccessRevokedListener, ActionBar.OnNavigationListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private static final String STATE_SELECTED_DRAWER_ITEM = "selected_drawer_item";
	
	private PlusClient mPlusClient = null;
	private SharedPreferences mPrefs = null;
	private UserDataSource mUserDataSource = null;
	private DrawerLayout mDrawerLayout = null;
	private ActionBarDrawerToggle mDrawerToggle = null;
	private ListView mDrawerList = null;
	private String[] mDrawerItems = null;
	private CharSequence mDrawerTitle = null;
	private CharSequence mTitle = null;
	private GroupDataSource mGroupDataSource = null;
	private EventDataSource mEventDataSource = null;
	private MessageDataSource mMessageDataSource = null;
	
	private TestFragment mTestFragment = null;
	private MapFragment mMapFragment = null;
	private CalendarFragment mCalendarFragment = null;
	private GroupsFragment mGroupsFragment = null;
	
	private int currentSelectedFragment = -1;
	
	private String[] groupNames = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment);
		
		Log.d("MainFragmentActivity", "onCreate");
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUserDataSource = new UserDataSource(this);
		mGroupDataSource = new GroupDataSource(this);
		mGroupDataSource.open();
		mEventDataSource = new EventDataSource(this);
		mEventDataSource.open();
		mEventDataSource.recreate();
		mMessageDataSource = new MessageDataSource(this);
		mMessageDataSource.open();
		mTitle = mDrawerTitle = getTitle();
		
		final ActionBar actionBar = getActionBar();
		// We actually want to show the title.
		// Helps guide the user through navigation a bit better.
		//actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		mPlusClient = new PlusClient.Builder(this, this, this)
					.setActions("http://schemas.google.com/AddActivity")
					.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
					.build();
		
		mDrawerItems = getResources().getStringArray(R.array.activity_main_fragment_drawerlist_array);
		mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_main_fragment);
		mDrawerList = (ListView)findViewById(R.id.activity_main_fragment_drawerlist);
		
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.activity_main_fragment_drawer_list_item, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.activity_main_fragment_drawer_open,
				R.string.activity_main_fragment_drawer_close
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		Group[] groups = mGroupDataSource.getGroups();
		List<String> groupList = new ArrayList<String>();
		for (Group group : groups) {
			groupList.add(group.getName());
		}
		groupNames = groupList.toArray(new String[groupList.size()]);
		actionBar.setListNavigationCallbacks(new ArrayAdapter<String>(actionBar.getThemedContext(),
								android.R.layout.simple_list_item_1,
								android.R.id.text1, groupNames), this);
		
		if (savedInstanceState == null)
			selectItem(3);
		else if (savedInstanceState.containsKey(STATE_SELECTED_DRAWER_ITEM))
			selectItem(savedInstanceState.getInt(STATE_SELECTED_DRAWER_ITEM));
		else
			selectItem(0);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		Log.d("MainFragmentActivity", "onPostCreate");
		mDrawerToggle.syncState();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("MainFragmentActivity", "onStart");
		if (mPlusClient != null) {
			if (!mPlusClient.isConnected()) {
				mPlusClient.connect();
			}
		} else {
			mPlusClient = new PlusClient.Builder(this, this, this)
						.setActions("http://schemas.google.com/AddActivity")
						.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
						.build();
			mPlusClient.connect();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		Log.d("MainFragmentActivity", "onResume");
		if (mPlusClient != null) {
			if (!mPlusClient.isConnected()) {
				mPlusClient.connect();
			}
		} else {
			mPlusClient = new PlusClient.Builder(this, this, this)
						.setActions("http://schemas.google.com/AddActivity")
						.setScopes(Scopes.PLUS_LOGIN, "https://www.googleapis.com/auth/calendar")
						.build();
			mPlusClient.connect();
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Log.d("MainFragmentActivity", "onPause");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		Log.d("MainFragmentActivity", "onStop");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("MainFragmentActivity", "onDestroy");
		if (mPlusClient != null) {
			if (mPlusClient.isConnected()) {
				mPlusClient.disconnect();
				mPlusClient = null;
			}
		}
		if (mGroupDataSource != null)
			mGroupDataSource.close();
		if (mEventDataSource != null)
			mEventDataSource.close();
		if (mMessageDataSource != null) {
			mMessageDataSource.close();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(STATE_SELECTED_DRAWER_ITEM, currentSelectedFragment);
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar().getSelectedNavigationIndex());
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM))
			getActionBar().setSelectedNavigationItem(savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		if (savedInstanceState.containsKey(STATE_SELECTED_DRAWER_ITEM))
			selectItem(savedInstanceState.getInt(STATE_SELECTED_DRAWER_ITEM));
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main_fragment_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;
		
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.activity_main_fragment_action_settings:
			mDrawerLayout.closeDrawer(mDrawerList);
			intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			return super.onOptionsItemSelected(item);
		case R.id.activity_main_fragment_action_about:
			mDrawerLayout.closeDrawer(mDrawerList);
			intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
			return super.onOptionsItemSelected(item);
		default:
			mDrawerLayout.closeDrawer(mDrawerList);
			return super.onOptionsItemSelected(item);
        }
    }
	
	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		Log.d(MainFragmentActivity.class.getName() + ".onNavigationItemSelected", "positionM = " + Integer.toString(position));
		
		if (currentSelectedFragment == 0) {
			if (mTestFragment != null) {
				mTestFragment.notifyFragmentOfGroupChange(position);
			}
		} else if (currentSelectedFragment == 1) {
			if (mMapFragment != null) {
				mMapFragment.notifyFragmentOfGroupChange(position);
			}
		} else if (currentSelectedFragment == 2) {
			if (mCalendarFragment != null) {
				mCalendarFragment.notifyFragmentOfGroupChange(position);
			}
		} else if (currentSelectedFragment == 3) {
			if (mGroupsFragment != null) {
				mGroupsFragment.notifyFragmentOfGroupChange(position);
			}
		} else {
			Toast.makeText(this, "Wat. This shouldn't have happened.", Toast.LENGTH_SHORT).show();
		}
		
		return true;
	}
	
	@Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        if (requestCode == REQUEST_CODE_RESOLVE_ERR && responseCode == RESULT_OK) {
            mPlusClient.connect();
        }
    }

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (result.hasResolution()) {
			try {
				result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);
			} catch (SendIntentException e) {
				mPlusClient.connect();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onConnected(Bundle connectionHint) {
		final Context context = this;
		AsyncTask task = new AsyncTask() {
			@Override
			protected Object doInBackground(Object... params) {
				Bundle appActivities = new Bundle();
				appActivities.putString(GoogleAuthUtil.KEY_REQUEST_VISIBLE_ACTIVITIES, "http://schemas.google.com/AddActivity");
				String scope = "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/calendar";
				
				try {
					String token = GoogleAuthUtil.getToken(context, mPlusClient.getAccountName(), scope, appActivities);
					Log.d(MainFragmentActivity.class.getName(), token);
					Editor prefEditor = mPrefs.edit();
					prefEditor.putString("access_token", token);
					prefEditor.commit();
				} catch (UserRecoverableAuthException e) {
					startActivityForResult(e.getIntent(), REQUEST_CODE_RESOLVE_ERR);
				} catch (GoogleAuthException e) {
					return null;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.execute((Void)null);
	}

	@Override
	public void onDisconnected() {
		Log.d(MainFragmentActivity.class.getName(), "disconnected");
		Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	public void LogoutButton_onClick(View view) {
		Log.d(MainFragmentActivity.class.getName(), "tapped sign out");
		if (mPlusClient.isConnected()) {
			mPlusClient.clearDefaultAccount();
			mPlusClient.revokeAccessAndDisconnect(this);
			mUserDataSource.deleteUser();
			mGroupDataSource.clear();
			mEventDataSource.clear();
			Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		mPlusClient.connect();
		finish();
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
	
	private void selectItem(int position) {
		Log.d(MainFragmentActivity.class.getName() + ".selectItem()", "positionD = " + Integer.toString(position));
		currentSelectedFragment = position;
		
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (position == 0) {
			if (mTestFragment == null)
				mTestFragment = new TestFragment();
			ft.replace(R.id.activity_main_fragment_placeholder, mTestFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
		} else if (position == 1) {
			if (mMapFragment == null)
				mMapFragment = new MapFragment();
			ft.replace(R.id.activity_main_fragment_placeholder, mMapFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
		} else if (position == 2) {
			if (mCalendarFragment == null)
				mCalendarFragment = new CalendarFragment();
			ft.replace(R.id.activity_main_fragment_placeholder, mCalendarFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
		} else if (position == 3) {
			if (mGroupsFragment == null)
				mGroupsFragment = new GroupsFragment();
			ft.replace(R.id.activity_main_fragment_placeholder, mGroupsFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
		} else {
			Toast.makeText(this, "Wat. This shouldn't have happened.", Toast.LENGTH_SHORT).show();
		}
		
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
}
