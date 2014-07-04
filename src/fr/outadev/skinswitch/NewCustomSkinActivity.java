package fr.outadev.skinswitch;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class NewCustomSkinActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_add_custom_skin);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add_custom_skin, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
