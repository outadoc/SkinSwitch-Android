package fr.outadev.skinswitch.skinlist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.network.login.MojangLoginActivity;
import fr.outadev.skinswitch.settings.SettingsActivity;
import fr.outadev.skinswitch.skin.BasicSkin;
import fr.outadev.skinswitch.skin.SkinsDatabase;
import fr.outadev.skinswitch.skinlibrary.SkinLibraryActivity;
import fr.outadev.skinswitch.user.UsersManager;

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
				builder.setTitle(R.string.dialog_logout_title).setMessage(R.string.dialog_logout_message);

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
										Intent intent = new Intent(getActivity(), SkinLibraryActivity.class);
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
			protected void onPreExecute() {
				skinsList.clear();
			}

			@Override
			protected List<BasicSkin> doInBackground(Void... params) {
				return db.getAllSkins();
			}

			@Override
			protected void onPostExecute(List<BasicSkin> result) {
				// show the "no skins yet" view if necessary
				gridView.setVisibility((result.isEmpty()) ? View.GONE : View.VISIBLE);
				noContentView.setVisibility((result.isEmpty()) ? View.VISIBLE : View.GONE);

				skinsList.addAll(result);
				skinsAdapter.notifyDataSetChanged();
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
						.addTestDevice("29EBDB460C20FD273BADF028945C56E2")
						.addTestDevice("4A75A651AD45105DB97E1E0ECE162D0B").build();
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
