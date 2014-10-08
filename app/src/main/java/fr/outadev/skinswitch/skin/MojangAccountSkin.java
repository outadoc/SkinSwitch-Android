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

import android.accounts.NetworkErrorException;
import android.content.Context;

import com.github.kevinsawicki.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;

/**
 * Created by outadoc on 08/10/14.
 */
public class MojangAccountSkin extends BasicSkin {

	private String uuid;

	public static final String BASE_API_URL = "https://api.mojang.com/users/profiles/minecraft/";

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
	public void downloadSkinFromSource(Context context) throws NetworkErrorException, IOException {

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
			String body = HttpRequest.get(BASE_API_URL + username)
					.trustAllHosts()
					.useCaches(true)
					.body();

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
