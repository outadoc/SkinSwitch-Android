/*
 * SkinSwitch - SkinManagerConnectionHandler
 * Copyright (C) 2014-2014  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch.network;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import fr.outadev.skinswitch.GallerySkin;

/**
 * Handles the requests to the Skin Manager API.
 *
 * @author outadoc
 */
public class SkinManagerConnectionHandler extends ConnectionHandler {

	public static final String BASE_URL = "http://skin.outadoc.fr/json/";

	public SkinManagerConnectionHandler(Context context) {
		super(context);
	}

	/**
	 * Gets the n latest skins.
	 *
	 * @param count the max number of skins to fetch.
	 * @param start the index of the first skin to fetch.
	 * @return an array containing the latest skins.
	 */
	public List<GallerySkin> fetchLatestSkins(int count, int start) throws HttpRequest.HttpRequestException {
		return fetchSkinsFromAPI("method=getLatestSkins&max=" + count + "&start=" + start);
	}

	/**
	 * Gets n random skins.
	 *
	 * @param count the max number of skins to retrieve.
	 * @return an array containing the random skins.
	 */
	public List<GallerySkin> fetchRandomSkins(int count) throws HttpRequest.HttpRequestException {
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
	public List<GallerySkin> fetchSkinByName(String criteria, int count, int start) throws HttpRequest.HttpRequestException {
		try {
			return fetchSkinsFromAPI("method=searchSkinByName&max=" + count + "&start=" + start + "&match="
					+ URLEncoder.encode(criteria, "UTF-8"));
		} catch(UnsupportedEncodingException e) {
			return null;
		}
	}

	public List<GallerySkin> fetchAllSkins(int count, int start) throws HttpRequest.HttpRequestException {
		return fetchSkinsFromAPI("method=searchSkinByName&max=" + count + "&start=" + start + "&match=.");
	}

	public Bitmap fetchSkinBitmap(int id) {
		byte[] response = HttpRequest.get(BASE_URL + "?method=getSkin&id=" + id).userAgent(getUserAgent()).trustAllHosts()
				.useCaches(true).bytes();
		return BitmapFactory.decodeByteArray(response, 0, response.length);
	}

	/**
	 * Gets a list of skins from the API.
	 *
	 * @param parameters the GET parameters that will be given to the API.
	 * @return an array of skins returned by the API.
	 */
	private List<GallerySkin> fetchSkinsFromAPI(String parameters) throws HttpRequest.HttpRequestException {
		List<GallerySkin> skinsList = new ArrayList<GallerySkin>();
		String response = HttpRequest.get(BASE_URL + "?" + parameters).userAgent(getUserAgent()).trustAllHosts().body();

		if(response != null) {
			try {
				JSONArray resultArray = new JSONArray(response);
				Log.i(ConnectionHandler.TAG, "successfully got response from skin manager (" + resultArray.length() + " items)");

				for(int i = 0; i < resultArray.length(); i++) {
					JSONObject currSkinObj = resultArray.getJSONObject(i);
					GallerySkin skin = new GallerySkin(currSkinObj.getInt("id"), currSkinObj.getString("title"),
							currSkinObj.getString("description"), currSkinObj.getString("owner_username"));

					if(currSkinObj.has("model")) {
						skin.setModelString(currSkinObj.getString("model"));
					}

					skin.setSkinManagerId(currSkinObj.getInt("id"));
					skinsList.add(skin);
				}

				return skinsList;
			} catch(JSONException e) {
				Log.e(ConnectionHandler.TAG, "error parsing " + parameters);
				throw new HttpRequest.HttpRequestException(new IOException());
			}

		} else {
			Log.e(ConnectionHandler.TAG, "error fetching " + parameters);
			throw new HttpRequest.HttpRequestException(new IOException());
		}
	}

	public enum EndPoint {
		LATEST_SKINS, RANDOM_SKINS, SEARCH_SKINS, ALL_SKINS
	}

}
