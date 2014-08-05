package fr.outadev.skinswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.outadev.skinswitch.network.login.MojangLoginActivity;
import fr.outadev.skinswitch.user.UsersManager;

/**
 * Main SkinSwitch activity. Displays the main skins list in a fragment.
 *
 * @author outadoc
 */
public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(savedInstanceState == null || !savedInstanceState.getBoolean("hasShownWindow", false)) {
			UsersManager usersManager = new UsersManager(this);

			if(!usersManager.isLoggedInSuccessfully()) {
				Intent intent = new Intent(this, MojangLoginActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("hasShownWindow", true);
	}

}
