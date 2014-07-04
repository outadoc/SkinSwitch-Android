package fr.outadev.skinswitch;

import java.util.ArrayList;
import java.util.List;

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
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;
import fr.outadev.skinswitch.ui.SkinsListAdapter;
import fr.outadev.skinswitch.user.UsersManager;

public class SkinsListFragment extends Fragment {

	private SkinsDatabase db;
	private UsersManager usersManager;

	private GridView gridView;
	private SkinsListAdapter skinsAdapter;
	private List<Skin> skinsList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.skin_list, container, false);
		setHasOptionsMenu(true);

		db = new SkinsDatabase(getActivity());
		usersManager = new UsersManager(getActivity());

		gridView = (GridView) view.findViewById(R.id.grid_view);
		skinsList = new ArrayList<Skin>();
		skinsAdapter = new SkinsListAdapter(getActivity(), this, android.R.layout.simple_list_item_1, skinsList);
		gridView.setAdapter(skinsAdapter);

		return view;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);

		boolean isLoggedIn = usersManager.isLoggedInSuccessfully();

		menu.findItem(R.id.action_login).setVisible(!isLoggedIn);
		menu.findItem(R.id.action_logout).setVisible(isLoggedIn);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}

	@Override
	public void onResume() {
		super.onResume();

		// when the activity is resuming, refresh
		refreshSkins();
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
						Toast.makeText(getActivity(), "Logged out.", Toast.LENGTH_SHORT).show();
					}
				});

				builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

					}
				});

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
										break;
									}
									case 1: {
										Intent intent = new Intent(getActivity(), NewCustomSkinActivity.class);
										startActivity(intent);
										break;
									}
								}
							}
				        });

				builder.create().show();
				return true;
			}
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refreshes the list of skins and the view.
	 */
	public void refreshSkins() {
		(new AsyncTask<Void, Void, List<Skin>>() {

			@Override
			protected void onPreExecute() {
				skinsList.clear();
			}

			@Override
			protected List<Skin> doInBackground(Void... params) {
				return db.getAllSkins();
			}

			@Override
			protected void onPostExecute(List<Skin> result) {
				skinsList.addAll(result);
				skinsAdapter.notifyDataSetChanged();
			}

		}).execute();
	}
}
