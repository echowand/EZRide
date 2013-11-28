package com.cs307.ezride.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class UserDataSource {
	private SharedPreferences mPrefs = null;
	private Editor mPrefsEditor = null;
	public static final String PREF_ID = "id";
	public static final String PREF_GOOGLEID = "google_id";
	public static final String PREF_REALNAME = "realname";
	public static final String PREF_EMAIL = "email";
	public static final String PREF_PHONE = "phone";
	public static final String PREF_ADDRESS = "address";
	public static final String PREF_BIO = "bio";
	public static final String PREF_AVATARURL = "avatarUrl";
	
	public UserDataSource(Context context) {
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		//mPrefsEditor = mPrefs.edit();
	}
	
	/**
	 * Creates/Updates a user in Shared Preferences. 
	 *
	 * @param	id				the id of the user from the server's database.
	 * @param	google_id		the Google id of the user.
	 * @param	name			the user's name.
	 * @param	email			the user's email address.
	 * @param	phone			the user's phone number.
	 * @param	address			the user's address
	 * @param	bio				the user's about me.
	 * @param	avatarUrl		the url to the user's avatar.
	 * @return					an User object containing the information of the user created. null if creation failed.
	 */
	public User addUser(int id, String google_id, String name, String email, String phone, String address, String bio, String avatarUrl) {
		if (mPrefs != null) {
			mPrefsEditor = mPrefs.edit();
			User user = new User();
			if (id != -1) {
				mPrefsEditor.putInt(PREF_ID, id);
				user.setId(id);
			}
			if (bio != null) {
				mPrefsEditor.putString(PREF_BIO, bio);
				user.setBio(bio);
			}
			if (google_id != null) {
				mPrefsEditor.putString(PREF_GOOGLEID, google_id);
				user.setGoogleId(google_id);
			}
			if (name != null) {
				mPrefsEditor.putString(PREF_REALNAME, name);
				user.setRealname(name);
			}
			if (email != null) {
				mPrefsEditor.putString(PREF_EMAIL, email);
				user.setEmail(email);
			}
			if (avatarUrl != null) {
				mPrefsEditor.putString(PREF_AVATARURL, avatarUrl);
				user.setAvatarUrl(avatarUrl);
			}
			if (address != null) {
				mPrefsEditor.putString(PREF_ADDRESS, address);
				user.setAddress(address);
			}
			if (phone != null) {
				mPrefsEditor.putString(PREF_PHONE, phone);
				user.setPhone(phone);
			}
			
			mPrefsEditor.putBoolean("isLoggedIn", true);
			
			if (!mPrefsEditor.commit()) {
				Log.w(UserDataSource.class.getName(), "Create user commit failed.");
				return null;
			} else {
				return user;
			} 
		}
		return null;
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
			mPrefsEditor.remove(PREF_GOOGLEID);
			mPrefsEditor.remove(PREF_REALNAME);
			mPrefsEditor.remove(PREF_EMAIL);
			mPrefsEditor.remove(PREF_PHONE);
			mPrefsEditor.remove(PREF_ADDRESS);
			mPrefsEditor.remove(PREF_BIO);
			mPrefsEditor.remove(PREF_AVATARURL);
			mPrefsEditor.putBoolean("isLoggedIn", false);
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
			user.setAvatarUrl(mPrefs.getString(PREF_AVATARURL, null));
			user.setBio(mPrefs.getString(PREF_BIO, null));
			user.setEmail(mPrefs.getString(PREF_EMAIL, null));
			user.setGoogleId(mPrefs.getString(PREF_GOOGLEID, null));
			user.setId(mPrefs.getInt(PREF_ID, -1));
			user.setPhone(mPrefs.getString(PREF_PHONE, null));
			user.setRealname(mPrefs.getString(PREF_REALNAME, null));
			if ((user.getId() == -1) || (user.getGoogleId() == null)) {
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
