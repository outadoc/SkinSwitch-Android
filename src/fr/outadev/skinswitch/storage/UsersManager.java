package fr.outadev.skinswitch.storage;

import simbio.se.sau.Encryption;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Stores and manages the user's credentials.
 * 
 * @author outadoc
 * 
 */
public class UsersManager {

	private final SharedPreferences prefs;
	private final Encryption encryption;

	public static final String USERNAME_PREFS_ID = "mojang:username";
	public static final String PASSWORD_PREFS_ID = "mojang:password";
	public static final String LOGGED_IN_PREFS_ID = "isloggedin";

	private static final String STORAGE_KEY = "thiskeyissofuckinglongIhopenobodywilleverfinditlulz1234";

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
		return encryption.decrypt(STORAGE_KEY, prefs.getString(USERNAME_PREFS_ID, ""));
	}

	/**
	 * Gets the decrypted password.
	 * 
	 * @return the password.
	 */
	private String getPassword() {
		return encryption.decrypt(STORAGE_KEY, prefs.getString(PASSWORD_PREFS_ID, ""));
	}

	/**
	 * Securely stores the user's credentials.
	 * 
	 * @param user
	 *            the user to store.
	 */
	public void saveUserCredentials(User user) {
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString(USERNAME_PREFS_ID, encryption.encrypt(STORAGE_KEY, user.getUsername()));
		editor.putString(PASSWORD_PREFS_ID, encryption.encrypt(STORAGE_KEY, user.getPassword()));

		editor.commit();
	}

	/**
	 * Set if the user successfully logged in to the website or not.
	 * 
	 * @param loggedIn
	 *            true if he/she logged in, false if he/she didn't.
	 */
	public void setLoggedInSuccessfully(boolean loggedIn) {
		SharedPreferences.Editor editor = prefs.edit();

		if(!doCredentialsExist()) {
			loggedIn = false;
		}

		editor.putBoolean(LOGGED_IN_PREFS_ID, loggedIn);
		editor.commit();
	}

	/**
	 * Did the user log in successfully already?
	 * 
	 * @return true if he/she did, else false.
	 */
	public boolean isLoggedInSuccessfully() {
		if(!doCredentialsExist()) {
			return false;
		}

		return prefs.getBoolean(LOGGED_IN_PREFS_ID, false);
	}

}
