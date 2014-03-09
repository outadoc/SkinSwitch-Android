package fr.outadev.skinswitch;

import fr.outadev.skinswitch.network.NetworkHandler;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				NetworkHandler networkHandler = new NetworkHandler(MainActivity.this);
				return Boolean.valueOf(networkHandler.checkMojangCredentials("testUser", "testPassword"));
			}

			@Override
			protected void onPostExecute(Boolean result) {
				Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
			}

		}.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
