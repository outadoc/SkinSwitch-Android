package fr.outadev.skinswitch.storage;

/**
 * Represents a user, with his/her username and decrypted password.
 * 
 * @author outadoc
 * 
 */
public class User {

	private String username;
	private String password;

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
