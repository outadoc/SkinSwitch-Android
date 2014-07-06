package fr.outadev.skinswitch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.adapters.SkinLibraryPageAdapter;

public class SkinLibraryActivity extends FragmentActivity {

	private ViewPager viewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin_library);

		SkinLibraryPageAdapter adapter = new SkinLibraryPageAdapter(getSupportFragmentManager());
		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_search_in_library, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_search:
				viewPager.setCurrentItem(SkinLibraryPageAdapter.INDEX_SEARCH_SKINS, true);
				return true;
			default:
				return false;
		}
	}
}
