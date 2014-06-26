package fr.outadev.skinswitch.network;

public class ChallengeRequirementException extends Exception {

    private static final long serialVersionUID = 1L;
    private final MojangLoginChallenge challenge;
    
	public ChallengeRequirementException(MojangLoginChallenge challenge) {
		this.challenge = challenge;
    }
	
	public MojangLoginChallenge getChallenge() {
		return challenge;
	}

}
