package fr.outadev.skinswitch;

import java.io.FileNotFoundException;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;
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

		(new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler();

				System.out.println(handler.fetchLatestSkins());
				System.out.println(handler.fetchRandomSkins());
				try {
	                System.out.println(handler.fetchSkinByName("outadoc").get(0).getSkinHeadBitmap(MainActivity.this));
                } catch(FileNotFoundException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
				System.out.println(handler.fetchSkinBitmap(10));

				return null;
			}

		}).execute();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("hasShownWindow", true);
	}

}
