package fr.outadev.skinswitch.network;

import java.io.File;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

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

		String location = HttpRequest.post(BASE_URL + "/login").followRedirects(false).form(data).location();

		if(location.indexOf("/login") != -1) {
			throw new InvalidMojangCredentialsException();
		} else if(location.indexOf("/challenge") != -1) {
			String challengeContent = HttpRequest.post(BASE_URL + "/challenge").followRedirects(false).body();
			throw new ChallengeRequirementException(new MojangLoginChallenge(challengeContent));
		}
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
