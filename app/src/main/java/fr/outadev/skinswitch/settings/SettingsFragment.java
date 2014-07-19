package fr.outadev.skinswitch.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import fr.outadev.skinswitch.R;

/**
 * Created by outadoc on 19/07/14.
 */
public class SettingsFragment extends PreferenceFragment {
	@SuppressLint("Override")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.prefs);
	}
}