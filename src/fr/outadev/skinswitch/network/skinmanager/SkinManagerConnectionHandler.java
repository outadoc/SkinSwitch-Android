package fr.outadev.skinswitch.network.skinmanager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.kevinsawicki.http.HttpRequest;

import fr.outadev.skinswitch.skin.SkinManagerSkin;

/**
 * Handles the requests to the Skin Manager API.
 * 
 * @author outadoc
 *
 */
public class SkinManagerConnectionHandler {

	private static final String BASE_URL = "https://skin.outadoc.fr/json/";

	/**
	 * Gets the latest skins.
	 * 
	 * @return an array containing the latest skins.
	 */
	public List<SkinManagerSkin> fetchLatestSkins() {
		return fetchLatestSkins(15, 0);
	}

	/**
	 * Gets the n latest skins.
	 * 
	 * @param count
	 *            the max number of skins to fetch.
	 * @param start
	 *            the index of the first skin to fetch.
	 * @return an array containing the latest skins.
	 */
	public List<SkinManagerSkin> fetchLatestSkins(int count, int start) {
		return fetchSkinsFromAPI("method=getLastestSkins&max=" + count + "&start=" + start);
	}

	/**
	 * Gets random skins.
	 * 
	 * @return an array containing random skins.
	 */
	public List<SkinManagerSkin> fetchRandomSkins() {
		return fetchRandomSkins(15);
	}

	/**
	 * Gets n random skins.
	 * 
	 * @param count
	 *            the max number of skins to retrieve.
	 * @return an array containing the random skins.
	 */
	public List<SkinManagerSkin> fetchRandomSkins(int count) {
		return fetchSkinsFromAPI("method=getRandomSkins&max=" + count);
	}

	/**
	 * Gets a list of skins that match the criteria.
	 * 
	 * @param criteria
	 *            the search criteria.
	 * @return an array of skins matching the criteria.
	 */
	public List<SkinManagerSkin> fetchSkinByName(String criteria) {
		return fetchSkinByName(criteria, 15, 0);
	}

	/**
	 * Gets a list of skins that match the criteria.
	 * 
	 * @param criteria
	 *            the search criteria.
	 * @param count
	 *            the max number of skins to fetch.
	 * @param start
	 *            the index of the first skin to fetch.
	 * @return an array of skins matching the criteria.
	 */
	public List<SkinManagerSkin> fetchSkinByName(String criteria, int count, int start) {
		try {
			return fetchSkinsFromAPI("method=searchSkinByName&max=" + count + "&start=" + start + "&match="
			        + URLEncoder.encode(criteria, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			return null;
		}
	}

	public Bitmap fetchSkinBitmap(int id) {
		byte[] response = HttpRequest.get(BASE_URL + "?method=getSkin&id=" + id).trustAllHosts().useCaches(true).bytes();
		return BitmapFactory.decodeByteArray(response, 0, response.length);
	}

	/**
	 * Gets a list of skins from the API.
	 * 
	 * @param parameters
	 *            the GET parameters that will be given to the API.
	 * @return an array of skins returned by the API.
	 */
	private List<SkinManagerSkin> fetchSkinsFromAPI(String parameters) {
		List<SkinManagerSkin> skinsList = new ArrayList<SkinManagerSkin>();
		String response = HttpRequest.get(BASE_URL + "?" + parameters).trustAllHosts().body();

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
