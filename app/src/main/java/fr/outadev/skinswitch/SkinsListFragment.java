/*
 * SkinSwitch - SkinsListFragment
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

package fr.outadev.skinswitch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.util.ArrayList;
import java.util.List;

import fr.outadev.skinswitch.network.UsersManager;
import fr.outadev.skinswitch.settings.SettingsActivity;

/**
 * Fragment that displays all the skins (used in the main screen).
 *
 * @author outadoc
 */
public class SkinsListFragment extends Fragment {

	private SkinsDatabase db;
	private UsersManager usersManager;

	private SkinsListAdapter skinsAdapter;
	private List<BasicSkin> skinsList;
	private AdView adView;
	private View noContentView;
	private GridView gridView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.skin_list, container, false);
		setHasOptionsMenu(true);

		db = new SkinsDatabase(getActivity());
		usersManager = new UsersManager(getActivity());

		adView = (AdView) view.findViewById(R.id.adView);
		noContentView = view.findViewById(R.id.view_no_content);

		gridView = (GridView) view.findViewById(R.id.grid_view);
		skinsList = new ArrayList<BasicSkin>();
		skinsAdapter = new SkinsListAdapter(getActivity(), this, android.R.layout.simple_list_item_1, skinsList);
		gridView.setAdapter(skinsAdapter);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		setupAds();
	}

	@Override
	public void onResume() {
		super.onResume();

		// when the activity is resuming, refresh
		refreshSkins();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		boolean isLoggedIn = false;

		if(usersManager != null) {
			isLoggedIn = usersManager.isLoggedInSuccessfully();
		}

		menu.findItem(R.id.action_login).setVisible(!isLoggedIn);
		menu.findItem(R.id.action_logout).setVisible(isLoggedIn);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch(item.getItemId()) {

			case R.id.action_logout: {
				// if we want to log out
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.dialog_logout_title).setMessage(getResources().getString(R.string
						.dialog_logout_message, usersManager.getUser().getUsername()));

				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						usersManager.setLoggedInSuccessfully(false);
						Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.logged_out),
								Toast.LENGTH_SHORT).show();
					}
				});

				builder.setNegativeButton(R.string.no, null);
				builder.create().show();
				return true;
			}
			case R.id.action_login: {
				// if we want to log in
				Intent intent = new Intent(getActivity(), MojangLoginActivity.class);
				startActivity(intent);
				return true;
			}
			case R.id.action_add: {

				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(R.string.add_skin_dialog_title).setItems(R.array.new_skin_choices,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								switch(which) {
									case 0: {
										Intent intent = new Intent(getActivity(), SkinGalleryActivity.class);
										startActivity(intent);
										break;
									}
									case 1: {
										Intent intent = new Intent(getActivity(), NewCustomSkinActivity.class);
										startActivity(intent);
										break;
									}
								}
							}
						}
				);

				builder.create().show();
				return true;
			}
			case R.id.action_settings: {
				//open the settings
				Intent intent = new Intent(getActivity(), SettingsActivity.class);
				startActivity(intent);
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refreshes the list of skins and the view.
	 */
	public void refreshSkins() {
		(new AsyncTask<Void, Void, List<BasicSkin>>() {

			@Override
			protected List<BasicSkin> doInBackground(Void... params) {
				return db.getAllSkins();
			}

			@Override
			protected void onPostExecute(List<BasicSkin> result) {
				skinsList.clear();

				// show the "no skins yet" view if necessary
				gridView.setVisibility((result.isEmpty()) ? View.GONE : View.VISIBLE);
				noContentView.setVisibility((result.isEmpty()) ? View.VISIBLE : View.GONE);

				skinsList.addAll(result);
				skinsAdapter.notifyDataSetChanged();

				Log.d(Utils.TAG, skinsList.size() + " skins loaded from database");
			}

		}).execute();
	}

	private void setupAds() {
		adView.setAdListener(new AdListener() {

			@Override
			public void onAdFailedToLoad(int errorCode) {
				adView.setVisibility(View.GONE);
				super.onAdFailedToLoad(errorCode);
			}

			@Override
			public void onAdLoaded() {
				adView.setVisibility(View.VISIBLE);
				super.onAdLoaded();
			}

		});

		if(getActivity().getResources().getBoolean(R.bool.enableAds)) {
			// if we want ads, check for availability and load them
			int hasGPS = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());

			if(hasGPS == ConnectionResult.SUCCESS) {
				AdRequest adRequest = new AdRequest.Builder()
						.addTestDevice("1176AD77C8CCAB0BE044FA12ACD473B0").build();
				adView.loadAd(adRequest);
			} else {
				adView.setVisibility(View.GONE);
			}
		} else {
			// if we don't want ads, remove the view from the layout
			adView.setVisibility(View.GONE);
		}
	}
}
