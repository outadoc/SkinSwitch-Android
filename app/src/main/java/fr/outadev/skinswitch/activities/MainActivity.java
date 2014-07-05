package fr.outadev.skinswitch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.user.UsersManager;

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
