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