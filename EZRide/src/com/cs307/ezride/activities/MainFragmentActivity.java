package com.cs307.ezride.activities;

import com.cs307.ezride.R;
import com.cs307.ezride.database.UserDataSource;
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

public class MainFragmentActivity extends FragmentActivity implements ConnectionCallbacks, OnConnectionFailedListener, OnAccessRevokedListener {
	private static final int REQUEST_CODE_RESOLVE_ERR = 9000;
	private PlusClient mPlusClient = null;
	private SharedPreferences mPrefs = null;
	private UserDataSource mUserDataSource = null;
	private DrawerLayout mDrawerLayout = null;
	private ActionBarDrawerToggle mDrawerToggle = null;
	private ListView mDrawerList = null;
	private String[] mDrawerItems = null;
	private CharSequence mDrawerTitle = null;
	private CharSequence mTitle = null;
	private int prevPosition = -1;
	
	private TestFragment mTestFragment = null;
	private MapFragment mMapFragment = null;
	private GroupsFragment mGroupsFragment = null;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_fragment);
		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mUserDataSource = new UserDataSource(this);
		mTitle = mDrawerTitle = getTitle();
		
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
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
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
		
		if (savedInstanceState == null)
			selectItem(0);
		
		/*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.replace(R.id.activity_main_fragment_placeholder, new TestFragment());
		ft.commit();*/
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mPlusClient != null)
			mPlusClient.disconnect();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mPlusClient.connect();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		if (mPlusClient != null)
			mPlusClient.disconnect();
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
		boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_calendar_view).setVisible(!isDrawerOpen);
		menu.findItem(R.id.action_groups_view).setVisible(!isDrawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item))
			return true;
		
		switch (item.getItemId()) {
		case R.id.action_groups_view:
			GroupsViewButton_onClick();
			return false;
		case R.id.action_calendar_view:
			CalendarViewButton_onClick();
			return false;
		default:
			return super.onOptionsItemSelected(item);
        }
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
			Toast.makeText(this, "You have logged out of EZRide.", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onAccessRevoked(ConnectionResult arg0) {
		mPlusClient.connect();
		finish();
	}

	private void GroupsViewButton_onClick() {
        Intent intent = new Intent(this, GroupsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

	private void CalendarViewButton_onClick() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}
	
	private void selectItem(int position) {
		Log.d(MainFragmentActivity.class.getName() + ".selectItem()", "position = " + Integer.toString(position));
		
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
			
		} else if (position == 3) {
			if (mGroupsFragment == null)
				mGroupsFragment = new GroupsFragment();
			ft.replace(R.id.activity_main_fragment_placeholder, mGroupsFragment);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			//ft.addToBackStack(null);
			ft.commit();
		} else if (position == 4) {
			
		} else if (position == 5) {
			
		} else {
			
		}
		
		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
		prevPosition = position;
	}

}
