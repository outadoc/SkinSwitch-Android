package fr.outadev.skinswitch.network.login;

/**
 * Thrown if the answer to a challenge was wrong.
 * 
 * @author outadoc
 * 
 */
public class InvalidMojangChallengeAnswerException extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidMojangChallengeAnswerException(String message) {
		super(message);
	}

}
