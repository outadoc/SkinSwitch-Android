package fr.outadev.skinswitch.network;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

import com.github.kevinsawicki.http.HttpRequest;

import android.content.Context;

public class NetworkHandler {

	public NetworkHandler(Context context) {
		this.context = context;

		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public boolean checkMojangCredentials(String username, String password) {

		Map<String, String> data = new HashMap<String, String>();
		
		data.put("username", username);
		data.put("password", password);
		data.put("remember", "false");
		
		String location = HttpRequest.post(BASE_URL + "/login").followRedirects(false)
				.form(data).location();
		
		return (location.indexOf("/login") == -1);
	}

	public void uploadSkinToMojang(int skinID) {

	}

	public Challenge getChallenge() {
		return null;
	}

	public void sendChallengeAnswer(String answer) {

	}

	private class Challenge {

		public Challenge(String id, String question) {
			this.id = id;
			this.question = question;
		}

		private String id;
		private String question;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getQuestion() {
			return question;
		}

		public void setQuestion(String question) {
			this.question = question;
		}

	}

	private static final int REQUEST_TIMEOUT = 30000;
	private static final int SOCKET_TIMEOUT = 10000;
	private static final String BASE_URL = "https://minecraft.net";

	private Context context;
}
