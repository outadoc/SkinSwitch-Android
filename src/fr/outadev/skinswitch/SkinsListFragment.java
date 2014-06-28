package fr.outadev.skinswitch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

		refreshSkins();

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
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch(item.getItemId()) {

			case R.id.action_logout:
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
			case R.id.action_login:
				// if we want to log in
				Intent intent = new Intent(getActivity(), MojangLoginActivity.class);
				startActivity(intent);
				return true;
			case R.id.action_add:
				// that's just testing stuff to add skins to the database easily
				SkinsDatabase db = new SkinsDatabase(getActivity());
				Skin newSkin = new Skin(-1, "Test", "Hihihi description", new Date());
				db.addSkin(newSkin);

				// create a fake skin
				try {
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inScaled = false;
					Bitmap btmp = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.test_skin_etho, opt);

					newSkin.saveRawSkinBitmap(getActivity(), btmp);
				} catch(IOException e) {
					e.printStackTrace();
				}

				refreshSkins();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

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
