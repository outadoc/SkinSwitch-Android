package fr.outadev.skinswitch.network.skinmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.outadev.skinswitch.skin.SkinLibrarySkin;

/**
 * Handles the requests to the Skin Manager API.
 *
 * @author outadoc
 */
public class SkinManagerConnectionHandler {

	public static final String BASE_URL = "https://skin.outadoc.fr/json/";

	/**
	 * Gets the n latest skins.
	 *
	 * @param count the max number of skins to fetch.
	 * @param start the index of the first skin to fetch.
	 * @return an array containing the latest skins.
	 */
	public List<SkinLibrarySkin> fetchLatestSkins(int count, int start) throws HttpRequest.HttpRequestException {
		return fetchSkinsFromAPI("method=getLastestSkins&max=" + count + "&start=" + start);
	}

	/**
	 * Gets n random skins.
	 *
	 * @param count the max number of skins to retrieve.
	 * @return an array containing the random skins.
	 */
	public List<SkinLibrarySkin> fetchRandomSkins(int count) throws HttpRequest.HttpRequestException {
		return fetchSkinsFromAPI("method=getRandomSkins&max=" + count);
	}

	/**
	 * Gets a list of skins that match the criteria.
	 *
	 * @param criteria the search criteria.
	 * @param count    the max number of skins to fetch.
	 * @param start    the index of the first skin to fetch.
	 * @return an array of skins matching the criteria.
	 */
	public List<SkinLibrarySkin> fetchSkinByName(String criteria, int count, int start) throws HttpRequest.HttpRequestException {
		try {
			return fetchSkinsFromAPI("method=searchSkinByName&max=" + count + "&start=" + start + "&match="
					+ URLEncoder.encode(criteria, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			return null;
		}
	}

	public List<SkinLibrarySkin> fetchAllSkins(int count, int start) throws HttpRequest.HttpRequestException {
		return fetchSkinsFromAPI("method=searchSkinByName&max=" + count + "&start=" + start + "&match=.");
	}

	public Bitmap fetchSkinBitmap(int id) {
		byte[] response = HttpRequest.get(BASE_URL + "?method=getSkin&id=" + id).trustAllHosts().useCaches(true).bytes();
		return BitmapFactory.decodeByteArray(response, 0, response.length);
	}

	/**
	 * Gets a list of skins from the API.
	 *
	 * @param parameters the GET parameters that will be given to the API.
	 * @return an array of skins returned by the API.
	 */
	private List<SkinLibrarySkin> fetchSkinsFromAPI(String parameters) throws HttpRequest.HttpRequestException {
		List<SkinLibrarySkin> skinsList = new ArrayList<SkinLibrarySkin>();
		String response = HttpRequest.get(BASE_URL + "?" + parameters).trustAllHosts().body();

		if(response != null) {
			try {
				JSONArray resultArray = new JSONArray(response);

				for(int i = 0; i < resultArray.length(); i++) {
					JSONObject currSkinObj = resultArray.getJSONObject(i);
					SkinLibrarySkin skin = new SkinLibrarySkin(currSkinObj.getInt("id"), currSkinObj.getString("title"),
							currSkinObj.getString("description"), null, currSkinObj.getString("owner_username"));
					skin.setSource(BASE_URL + "?method=getSkin&id=" + currSkinObj.getInt("id"));
					skin.setSkinManagerId(currSkinObj.getInt("id"));
					skinsList.add(skin);
				}

				return skinsList;
			} catch(JSONException e) {
				throw new HttpRequest.HttpRequestException(new IOException());
			}

		} else {
			throw new HttpRequest.HttpRequestException(new IOException());
		}
	}

	public enum EndPoint {
		LATEST_SKINS, RANDOM_SKINS, SEARCH_SKINS, ALL_SKINS
	}

}
