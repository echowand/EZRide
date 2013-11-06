package com.cs307.ezride;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups);
		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mGroupNamesArray = new ArrayList<String>();
		mGroupNamesArray.add("Group A");
		mGroupNamesArray.add("Group B");
		mGroupNamesArray.add("Group C");
		
		mGroupNamesArrayAdapter = new ArrayAdapter<String>(this, R.layout.groups_item_simple, mGroupNamesArray);
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
	protected void onDestroy() {
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
			return onAddButtonClick();
		case R.id.groups_action_join:
			return onJoinButtonClick();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private boolean onAddButtonClick() {
		AlertDialog.Builder popup = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		popup.setTitle("Add Group");
		popup.setMessage("Enter a name for the group:");
		popup.setView(input);
		
		popup.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		popup.show();
		
		return true;
	}
	
	private boolean onJoinButtonClick() {
		AlertDialog.Builder popup = new AlertDialog.Builder(this);
		final EditText input = new EditText(this);
		
		popup.setTitle("Join Group");
		popup.setMessage("Enter the name of the group:");
		popup.setView(input);
		
		popup.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
		popup.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		popup.show();
		
		return true;
	}

}
