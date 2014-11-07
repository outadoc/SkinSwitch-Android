/*
 * SkinSwitch - UsersManager
 * Copyright (C) 2014-2014  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import fr.outadev.skinswitch.Utils;
import simbio.se.sau.Encryption;

/**
 * Stores and manages the user's credentials.
 *
 * @author outadoc
 */
public class UsersManager {

	public static final String USERNAME_PREFS_ID = "mojang:username";
	public static final String PASSWORD_PREFS_ID = "mojang:password";
	public static final String LOGGED_IN_PREFS_ID = "isloggedin";

	private static final String STORAGE_KEY =
			"etG4kM3fBG3WVsoRTWcFNFHnZiHaUO94pNR9MmWHXFUt6nNtrDtpybpH2knEcjgC8QsnzlqVG0qLyx9" +
					"GnRzWD2buQC3DZV1qIvVvbKae4xOWEtFFwqtp3zV2t3k0XYDWWhSn3EmHgScJjlJiUrPv35poDeNxaN" +
					"9Z3xJ46hYkPT2QmDfS2qFYcDJ0kH2DjDsyDDcw2w0bSGD1BFPW2y8JsUJCn3gKMiwTM0ksvyevqL2is0pRZkyUuCoVc9gn1wCh";

	private final SharedPreferences prefs;
	private final Encryption encryption;

	public UsersManager(Context context) {
		prefs = context.getSharedPreferences("Users", Context.MODE_PRIVATE);
		encryption = new Encryption();
	}

	/**
	 * Gets the User that was stored.
	 *
	 * @return the user.
	 */
	public User getUser() {
		return new User(getUsername(), getPassword());
	}

	/**
	 * Check if the credentials are set.
	 *
	 * @return true if his credentials exist, false if not.
	 */
	private boolean doCredentialsExist() {
		return prefs.contains(USERNAME_PREFS_ID) && prefs.contains(PASSWORD_PREFS_ID);
	}

	/**
	 * Gets the decrypted username.
	 *
	 * @return the username.
	 */
	private String getUsername() {
		return encryption.decrypt(getStorageKey(), prefs.getString(USERNAME_PREFS_ID, ""));
	}

	/**
	 * Gets the decrypted password.
	 *
	 * @return the password.
	 */
	private String getPassword() {
		return encryption.decrypt(getStorageKey(), prefs.getString(PASSWORD_PREFS_ID, ""));
	}

	/**
	 * Securely stores the user's credentials.
	 *
	 * @param user the user to store.
	 */
	public void saveUserCredentials(User user) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(USERNAME_PREFS_ID, encryption.encrypt(getStorageKey(), user.getUsername()));
		editor.putString(PASSWORD_PREFS_ID, encryption.encrypt(getStorageKey(), user.getPassword()));

		editor.apply();
		Log.i(Utils.TAG, "credentials saved successfully");
	}

	/**
	 * Did the user log in successfully already?
	 *
	 * @return true if he/she did, else false.
	 */
	public boolean isLoggedInSuccessfully() {
		if(!doCredentialsExist() || (doCredentialsExist() && (getUsername() == null || getPassword() == null))) {
			setLoggedInSuccessfully(false);
		}

		return doCredentialsExist() && prefs.getBoolean(LOGGED_IN_PREFS_ID, false);

	}

	/**
	 * Set if the user successfully logged in to the website or not.
	 *
	 * @param loggedIn true if he/she logged in, false if he/she didn't.
	 */
	public void setLoggedInSuccessfully(boolean loggedIn) {
		SharedPreferences.Editor editor = prefs.edit();

		if(!doCredentialsExist()) {
			loggedIn = false;
		}

		if(!loggedIn) {
			saveUserCredentials(new User(getUsername(), ""));
		}

		editor.putBoolean(LOGGED_IN_PREFS_ID, loggedIn);
		editor.apply();
	}

	public String getStorageKey() {
		return STORAGE_KEY + Settings.Secure.ANDROID_ID;
	}

}
