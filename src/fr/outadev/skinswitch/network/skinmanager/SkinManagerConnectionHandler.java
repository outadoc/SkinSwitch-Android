package fr.outadev.skinswitch.network.skinmanager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.github.kevinsawicki.http.HttpRequest;

import fr.outadev.skinswitch.skin.SkinManagerSkin;

public class SkinManagerConnectionHandler {

	private static final String BASE_URL = "http://skin.outadoc.fr/json/";

	public List<SkinManagerSkin> getLatestSkins() {
		return getLatestSkins(15, 0);
	}

	public List<SkinManagerSkin> getLatestSkins(int count, int start) {
		List<SkinManagerSkin> skinsList = new ArrayList<SkinManagerSkin>();
		String response = HttpRequest.get(BASE_URL + "?method=getLastestSkins&max=" + count + "&start=" + start).body();

		if(response != null) {
			try {
				JSONArray resultArray = new JSONArray(response);

				if(resultArray != null) {
					for(int i = 0; i < resultArray.length(); i++) {
						JSONObject currSkinObj = resultArray.getJSONObject(i);
						SkinManagerSkin skin = new SkinManagerSkin(currSkinObj.getInt("id"), currSkinObj.getString("title"),
						        currSkinObj.getString("description"), null, currSkinObj.getString("owner_username"));
						skinsList.add(skin);
					}

					return skinsList;
				}
			} catch(JSONException e) {
				return null;
			}

		}

		return null;
	}

}
