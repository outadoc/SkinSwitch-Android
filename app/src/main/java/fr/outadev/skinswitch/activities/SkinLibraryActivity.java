package fr.outadev.skinswitch.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.SearchView;

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
		getMenuInflater().inflate(R.menu.search_in_library, menu);

		//set up search view
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
		searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		searchView.setIconifiedByDefault(true);

		return true;
	}

}
