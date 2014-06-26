package fr.outadev.skinswitch.network;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

public class MojangLoginManager {

	private static final String BASE_URL = "https://minecraft.net";

	public MojangLoginManager() {
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public void loginWithCredentials(String username, String password) throws InvalidMojangCredentialsException,
	        ChallengeRequirementException {

		Map<String, String> data = new HashMap<String, String>();

		data.put("username", username);
		data.put("password", password);
		data.put("remember", "false");

		String body = HttpRequest.post(BASE_URL + "/login").followRedirects(true).form(data).body();
		
		if(body.isEmpty() || body.indexOf("<h1>Login</h1>") != -1) {
			Log.e("SkinSwitch", "could not log in as " + username);
			throw new InvalidMojangCredentialsException();
		} else if(body.indexOf("<h1>Confirm your identity</h1>") != -1) {
			Log.e("SkinSwitch", "challenge required for " + username);
			throw new ChallengeRequirementException(new MojangLoginChallenge(body));
		}
		
		Log.i("SkinSwitch", "logged in as " + username);
	}

	public void validateChallenge(MojangLoginChallenge challenge, String answer) throws InvalidMojangChallengeAnswerException {
		Map<String, String> data = new HashMap<String, String>();

		data.put("answer", answer);
		data.put("questionId", challenge.getId());
		data.put("authenticityToken", challenge.getAuthToken());

		HttpRequest challengeRequest = HttpRequest.post(BASE_URL + "/challenge").followRedirects(false).form(data);

		System.out.println(challengeRequest.body());
	}

	public void uploadSkinToMojang(File skin) {
		HttpRequest skinRequest = HttpRequest.post(BASE_URL + "/profile/skin").followRedirects(false).contentType("image/png");
		skinRequest.part("skin", skin);
	}

}
