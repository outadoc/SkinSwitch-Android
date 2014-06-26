package fr.outadev.skinswitch.network;

public class InvalidMojangChallengeAnswerException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public InvalidMojangChallengeAnswerException(String message) {
	    super(message);
    }

}
