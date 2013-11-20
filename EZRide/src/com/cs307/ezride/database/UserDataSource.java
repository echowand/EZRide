package com.cs307.ezride.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class UserDataSource {
	private SharedPreferences mPrefs = null;
	private Editor mPrefsEditor = null;
	public static final String PREF_ID = "userid";
	public static final String PREF_USERNAME = "username";
	public static final String PREF_PASSWORD = "password";
	public static final String PREF_REALNAME = "realname";
	public static final String PREF_EMAIL = "email";
	public static final String PREF_PHONE = "phone";
	public static final String PREF_ADDRESS = "address";
	public static final String PREF_BIO = "bio";
	
	public UserDataSource(Context context) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		//mPrefsEditor = mPrefs.edit();
	}
	
	/**
	 * Creates a user in Shared Preferences. 
	 *
	 * @param	id			the id of the User from central database.
	 * @param	username	the username of the User.
	 * @param	password	the password of the User.
	 * @param	realname	the User's actual name.
	 * @param	email		the User's email address.
	 * @param	phone		the User's phone number.
	 * @param	address		the User's address.
	 * @param	bio			the User's general information.
	 * @return				an User object containing the information of the user created. null if creation failed.
	 */
	public User createUser(int id, String username, String password, String realname, String email,
			String phone, String address, String bio) {
		if (mPrefs != null) {
			mPrefsEditor = mPrefs.edit();
			mPrefsEditor.putInt(PREF_ID, id).putString(PREF_USERNAME, username).putString(PREF_PASSWORD, password).putString(PREF_REALNAME, realname);
			mPrefsEditor.putString(PREF_EMAIL, email).putString(PREF_PHONE, phone).putString(PREF_ADDRESS, address).putString(PREF_BIO, bio);
			
			if (!mPrefsEditor.commit()) {
				Log.w(UserDataSource.class.getName(), "Create user commit failed.");
				return null;
			} else {
				User user = new User();
				user.setId(id);
				user.setAddress(address);
				user.setBio(bio);
				user.setEmail(email);
				user.setPassword(password);
				user.setPhone(phone);
				user.setRealname(realname);
				user.setUsername(username);
				return user;
			}
		} else {
			Log.w(UserDataSource.class.getName(), "SharedPreferences variable was not initialized somehow.");
			return null;
		}
	}
	
	/**
	 * Updates the user's info in Shared Preferences. 
	 *
	 * @param	user	the User to be updated.
	 * @return			true if the user was successfully updated, false otherwise.
	 */
	public boolean updateUser(User user) {
		if ((user != null)&&(mPrefs != null)) {
			mPrefsEditor = mPrefs.edit();
			mPrefsEditor.putInt(PREF_ID, user.getId()).putString(PREF_USERNAME, user.getUsername()).putString(PREF_PASSWORD, user.getPassword()).putString(PREF_REALNAME, user.getRealname());
			mPrefsEditor.putString(PREF_EMAIL, user.getEmail()).putString(PREF_PHONE, user.getPhone()).putString(PREF_ADDRESS, user.getAddress()).putString(PREF_BIO, user.getBio());
			if (mPrefsEditor.commit())
				return true;
			else {
				Log.w(UserDataSource.class.getName(), "Update user commit failed.");
				return false;
			}
		} else {
			Log.w(UserDataSource.class.getName(), "Either user was null or SharedPreferences variable was not initialized somehow.");
			return false;
		}
	}
	
	/**
	 * Deletes the user's info from Shared Preferences.
	 * 
	 * @return			true if the user was successfully deleted, false otherwise.
	 */
	public boolean deleteUser() {
		if (mPrefs != null) {
			mPrefsEditor = mPrefs.edit();
			mPrefsEditor.remove(PREF_ID);
			mPrefsEditor.remove(PREF_USERNAME);
			mPrefsEditor.remove(PREF_PASSWORD);
			mPrefsEditor.remove(PREF_REALNAME);
			mPrefsEditor.remove(PREF_EMAIL);
			mPrefsEditor.remove(PREF_PHONE);
			mPrefsEditor.remove(PREF_ADDRESS);
			mPrefsEditor.remove(PREF_BIO);
			if (mPrefsEditor.commit())
				return true;
			else {
				Log.w(UserDataSource.class.getName(), "Delete user commit failed.");
				return false;
			}
		} else {
			Log.w(UserDataSource.class.getName(), "SharedPreferences variable was not initialized somehow.");
			return false;
		}
	}
	
	/**
	 * Gets the user's info from the database. 
	 *
	 * @return      the User's info as a User object.
	 */
	public User getUser() {
		if (mPrefs != null) {
			User user = new User();
			user.setAddress(mPrefs.getString(PREF_ADDRESS, null));
			user.setBio(mPrefs.getString(PREF_BIO, null));
			user.setEmail(mPrefs.getString(PREF_EMAIL, null));
			user.setId(mPrefs.getInt(PREF_ID, -1));
			user.setPassword(mPrefs.getString(PREF_PASSWORD, null));
			user.setPhone(mPrefs.getString(PREF_PHONE, null));
			user.setRealname(mPrefs.getString(PREF_REALNAME, null));
			user.setUsername(mPrefs.getString(PREF_USERNAME, null));
			if (user.getId() == -1) {
				Log.w(UserDataSource.class.getName(), "Create user commit failed.");
				return null;
			} else {
				return user;
			}
		} else {
			Log.w(UserDataSource.class.getName(), "SharedPreferences variable was not initialized somehow.");
			return null;
		}
	}
}
