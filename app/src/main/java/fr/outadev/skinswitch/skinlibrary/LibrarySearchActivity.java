/*
 * SkinSwitch - LibrarySearchActivity
 * Copyright (C) 2014-2014  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch.skinlibrary;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

/**
 * Activity handling the library search.
 * Displays the search results for a query.
 *
 * @author outadoc
 */
public class LibrarySearchActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
