package fr.outadev.skinswitch.network;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.outadev.skinswitch.network.login.ChallengeRequirementException;
import fr.outadev.skinswitch.network.login.InvalidMojangChallengeAnswerException;
import fr.outadev.skinswitch.network.login.InvalidMojangCredentialsException;
import fr.outadev.skinswitch.network.login.LoginChallenge;
import fr.outadev.skinswitch.user.User;

/**
 * Manages the connection to minecraft.net. Allows you to sign in successfully,
 * and send a skin.
 *
 * @author outadoc
 */
public class MojangConnectionHandler {

	private static final String BASE_URL = "https://minecraft.net";

	public MojangConnectionHandler() {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	/**
	 * Login with this user's credentials to the website.
	 *
	 * @param user the username and password that will be used to login.
	 * @throws InvalidMojangCredentialsException if the username or password isn't right.
	 * @throws ChallengeRequirementException     if the user has to complete a challenge in order to log in
	 *                                           from this device.
	 */
	public void loginWithCredentials(User user) throws InvalidMojangCredentialsException, ChallengeRequirementException {

		Map<String, String> data = new HashMap<String, String>();

		data.put("username", user.getUsername());
		data.put("password", user.getPassword());
		data.put("remember", "false");

		String body = HttpRequest.post(BASE_URL + "/login").followRedirects(true).form(data).body();

		if(body.isEmpty() || body.indexOf("<h1>Login</h1>") != -1) {
			Log.e("SkinSwitch", "could not log in as " + user.getUsername());
			throw new InvalidMojangCredentialsException();
		} else if(body.indexOf("<h1>Confirm your identity</h1>") != -1) {
			Log.e("SkinSwitch", "challenge required for " + user.getUsername());
			throw new ChallengeRequirementException(new LoginChallenge(body));
		}

		Log.i("SkinSwitch", "logged in as " + user.getUsername());
	}

	/**
	 * Send a completed challenge to the website for validation.
	 *
	 * @param challenge the challenge that was answered.
	 * @param answer    the answer.
	 * @throws InvalidMojangChallengeAnswerException if the answer wasn't the right one.
	 */
	public void validateChallenge(LoginChallenge challenge, String answer) throws InvalidMojangChallengeAnswerException {
		Map<String, String> data = new HashMap<String, String>();

		data.put("answer", answer);
		data.put("questionId", challenge.getId());
		data.put("authenticityToken", challenge.getAuthToken());

		String body = HttpRequest.post(BASE_URL + "/challenge").followRedirects(false).form(data).body();

		if(body.equals("Security challenge passed.")) {
			return;
		}

		String error = null;

		try {
			JSONObject errorObject = (JSONObject) new JSONTokener(body).nextValue();

			if(errorObject != null && errorObject.getString("error") != null) {
				error = errorObject.getString("error").replaceAll("\\<.*?>", "");
			} else {
				error = body;
			}
		} catch(Exception e) {
			error = body;
		}

		if(error != null) {
			throw new InvalidMojangChallengeAnswerException(error);
		}
	}

	/**
	 * Uploads a skin to the website.
	 *
	 * @param skin the skin to send.
	 * @throws SkinUploadException if the upload failed.
	 */
	public void uploadSkinToMojang(File skin) throws SkinUploadException {
		// first off, we need an authenticity token to upload the skin ;-;
		String profileBody = HttpRequest.get(BASE_URL + "/profile").body();
		String authToken = null;

		// parse the request and get the auth token
		Pattern patternAuthToken = Pattern.compile("<input type=\"hidden\" name=\"authenticityToken\" value=\"([0-9a-f]*)\">");
		Matcher matcher = patternAuthToken.matcher(profileBody);

		if(matcher.find()) {
			authToken = matcher.group(1);
		}

		// once we have that, send the actual skin
		HttpRequest skinRequest = HttpRequest.post(BASE_URL + "/profile/skin")
				.followRedirects(true)
				.part("authenticityToken", authToken)
				.part("skin", skin.getName(), "image/png", skin);

		String body = skinRequest.body();

		//check for upload errors
		if(body != null) {
			Pattern errorPattern = Pattern.compile("<span class=\"error\">(.+)</span>");
			matcher = errorPattern.matcher(body);

			if(matcher.find()) {
				String error = matcher.group(1);
				throw new SkinUploadException(error);
			}
		}

	}
}
