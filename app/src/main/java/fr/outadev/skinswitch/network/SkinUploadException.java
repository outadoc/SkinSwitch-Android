package fr.outadev.skinswitch.network;

/**
 * Defines an exception that happened during the upload of a skin.
 * Created by outadoc on 19/07/14.
 */
public class SkinUploadException extends Exception {

	private static final long serialVersionUID = 1L;

	public SkinUploadException(String error) {
		super(error);
	}

}