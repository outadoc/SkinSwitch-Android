/*
 * SkinSwitch - ConnectionHandler
 * Copyright (C) 2014-2015  Baptiste Candellier
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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

/**
 * Handles HTTP requests with a custom user agent.
 *
 * @author outadoc
 */
public abstract class ConnectionHandler {

	public static final String TAG = "Mojang/Network";
	private String userAgent;

	public ConnectionHandler(Context context) {
		String appVersion = "x.x";

		try {
			if(context != null) {
				PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

				if(info != null) {
					appVersion = info.versionName;
				}
			}
		} catch(PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		userAgent = "SkinSwitch/<" + appVersion + "> Mobile (+http://dev.outadoc.fr/project/skinswitch.html) (Linux; <" + Build
				.VERSION.RELEASE + ">; " +
				"<" + Build.VERSION.INCREMENTAL + ">)";
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
}
