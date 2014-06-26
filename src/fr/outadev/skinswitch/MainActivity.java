package fr.outadev.skinswitch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import fr.outadev.skinswitch.storage.UsersManager;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		UsersManager usersManager = new UsersManager(this);
		
		if(!usersManager.userCreated()) {
		    Intent intent = new Intent(this, MojangLoginActivity.class);
		    startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_login:
	        	Intent intent = new Intent(this, MojangLoginActivity.class);
			    startActivity(intent);
			    return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

}
