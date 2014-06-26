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
	 * Check if the user has already logged in.
	 * 
	 * @return true if his credentials exist, false if not.
	 */
	public boolean userCreated() {
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

}
