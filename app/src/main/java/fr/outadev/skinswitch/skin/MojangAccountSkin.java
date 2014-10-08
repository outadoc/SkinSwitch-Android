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

import java.io.IOException;
import java.util.Date;

/**
 * Created by outadoc on 08/10/14.
 */
public class MojangAccountSkin extends BasicSkin {

	private String uuid;

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

	@Override
	public boolean isValidSource(String username) {
		if(username != null && !username.isEmpty()) {
			int status = HttpRequest.get("https://api.mojang.com/users/profiles/minecraft/" + username)
					.trustAllHosts()
					.useCaches(true)
					.code();

			if(status == 200) {
				return true;
			}
		}

		return false;
	}

}
