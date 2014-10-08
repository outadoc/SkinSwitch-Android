/*
 * SkinSwitch - MojangAccountSkin
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

package fr.outadev.skinswitch.skin;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * This type of skin is linked to a Minecraft player's UUID.
 * It will be downloaded as a copy of the player's skin, and will be refreshed in the same way.
 */
public class MojangAccountSkin extends BasicSkin {

	private String uuid;

	public static final String MOJANG_API_URL = "https://api.mojang.com/users/profiles/minecraft/";
	public static final String SESSION_API_URL = "https://sessionserver.mojang.com/session/minecraft/profile/";

	public MojangAccountSkin(int id, String name, String description, Date creationDate, String uuid) {
		super(id, name, description, creationDate);
		setUuid(uuid);
	}

	public MojangAccountSkin(String name, String description, Date creationDate, String uuid) {
		super(name, description, creationDate);
		setUuid(uuid);
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public void downloadSkinFromSource(Context context) throws HttpRequest.HttpRequestException, IOException {
		if(getUuid() == null) {
			return;
		}

		//we have to request a file on Mojang's server if we wanna retrieve the player's skin from his UUID.
		String body = HttpRequest.get(SESSION_API_URL + getUuid()).useCaches(false).body();

		if(body == null) {
			throw new HttpRequest.HttpRequestException(new IOException("Response to get the skin URL was empty."));
		}

		//we're expecting a JSON document that looks like this:
		/*
			{
			    ...
			    "properties": [
			        {
			            "name": "textures",
			            "value": "<base64 string>"
			        }
			    ]
			}
		 */

		try {
			//trying to reach the properties.value string
			JSONObject obj = new JSONObject(body);
			JSONArray properties = obj.getJSONArray("properties");

			if(properties != null) {
				for(int i = 0; i < properties.length(); i++) {
					if(properties.getJSONObject(i).getString("name").equals("textures")) {
						//once that string is reached, we have to decode it: it's in base64
						String base64info = properties.getJSONObject(i).getString("value");
						JSONObject textureInfo = new JSONObject(new String(Base64.decode(base64info, Base64.DEFAULT)));

						//the decoded string is also a JSON document, so we parse that. should look like this:
						/*
							{
							    ...
							    "textures": {
							        "SKIN": {
							            "url": "<player skin URL>"
							        },
							        ...
							    }
							}
						 */

						//we want to retrieve the textures.SKIN.url string. that's the skin's URL. /FINALLY/.
						String url = textureInfo.getJSONObject("textures").getJSONObject("SKIN").getString("url");

						if(url != null) {
							//download the skin from the provided URL
							byte[] response = HttpRequest.get(url).useCaches(true).bytes();

							if(response == null) {
								throw new HttpRequest.HttpRequestException(new IOException("Couldn't download " + this));
							}

							//decode the bitmap and store it
							Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length);

							if(bmp != null) {
								saveRawSkinBitmap(context, bmp);
								bmp.recycle();
							}
						}
					}
				}
			}
		} catch(JSONException e) {
			throw new HttpRequest.HttpRequestException(new IOException("The response from Mojang was invalid. Woops."));
		}
	}

	/**
	 * Checks if the skin's source is a valid skin.
	 * Also sets the skin's linked player UUID if it's valid.
	 *
	 * @param username the username to check the validity of
	 * @return true if it's valid, false if it's not
	 */
	@Override
	public boolean validateSource(String username) {
		if(username != null && !username.isEmpty()) {
			String body = HttpRequest.get(MOJANG_API_URL + username).useCaches(true).body();

			if(body != null) {
				try {
					JSONObject obj = new JSONObject(body);
					setUuid(obj.getString("id"));

					return true;
				} catch(JSONException e) {
					return false;
				}
			}
		}

		return false;
	}

}
