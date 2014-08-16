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
