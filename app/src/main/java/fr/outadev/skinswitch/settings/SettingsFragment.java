/*
 * SkinSwitch - SettingsFragment
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

package fr.outadev.skinswitch.settings;

import android.annotation.SuppressLint;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import fr.outadev.skinswitch.R;

/**
 * Fragment that inflates the settings.
 *
 * @author outadoc
 */
public class SettingsFragment extends PreferenceFragment {

	@SuppressLint("Override")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.prefs);
	}

	@Override
	public void onResume() {
		super.onResume();

		try {
			PackageInfo info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			findPreference("pref_about").setSummary(getResources().getString(R.string.about_skinswitch_sum,
					getResources().getString(R.string.app_name), info.versionName));
		} catch(PackageManager.NameNotFoundException e1) {
			e1.printStackTrace();
		}
	}

}