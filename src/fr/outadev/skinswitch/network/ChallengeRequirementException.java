package fr.outadev.skinswitch.network;

public class ChallengeRequirementException extends Exception {

    private static final long serialVersionUID = 1L;
    private final LoginChallenge challenge;
    
	public ChallengeRequirementException(LoginChallenge challenge) {
		this.challenge = challenge;
    }
	
	public LoginChallenge getChallenge() {
		return challenge;
	}

}
