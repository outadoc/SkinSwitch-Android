package fr.outadev.skinswitch.network.login;

/**
 * Thrown if the user has to answer a security question.
 * 
 * @author outadoc
 * 
 */
public class ChallengeRequirementException extends Exception {

	private static final long serialVersionUID = 1L;
	private final LoginChallenge challenge;

	public ChallengeRequirementException(LoginChallenge challenge) {
		this.challenge = challenge;
	}

	/**
	 * Gets the challenge the user will have to answer.
	 * 
	 * @return the challenge.
	 */
	public LoginChallenge getChallenge() {
		return challenge;
	}

}
