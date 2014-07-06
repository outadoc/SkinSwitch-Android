package fr.outadev.skinswitch.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

public class LibrarySearchActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
			String query = getIntent().getStringExtra(SearchManager.QUERY);

			Bundle args = new Bundle();
			args.putString(SkinLibraryPageFragment.ARG_SEARCH_QUERY, query);
			args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, SkinManagerConnectionHandler.EndPoint.SEARCH_SKINS);

			Fragment searchFrag = new SkinLibraryPageFragment();
			searchFrag.setArguments(args);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(android.R.id.content, searchFrag).commit();
		}
	}
}
