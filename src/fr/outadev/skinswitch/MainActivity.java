package fr.outadev.skinswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import fr.outadev.skinswitch.storage.UsersManager;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		UsersManager usersManager = new UsersManager(this);

		if(!usersManager.isLoggedInSuccessfully()) {
			Intent intent = new Intent(this, MojangLoginActivity.class);
			startActivity(intent);
		}
	}

}
