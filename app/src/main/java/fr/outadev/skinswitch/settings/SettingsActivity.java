package fr.outadev.skinswitch.settings;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

/**
 * Activity that handles the SkinSwitch settings.
 *
 * @author outadoc
 */
public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}

		return false;
	}
}