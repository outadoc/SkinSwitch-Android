package fr.outadev.skinswitch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

public class SkinsListFragment extends Fragment {

	private SkinsDatabase db;
	private GridView gridView;
	private SkinsListAdapter skinsAdapter;
	private List<Skin> skinsList;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.skin_list, container, false);
		setHasOptionsMenu(true);
		
		db = new SkinsDatabase(getActivity());
		gridView = (GridView) view.findViewById(R.id.grid_view);
		skinsList = new ArrayList<Skin>();
		skinsAdapter = new SkinsListAdapter(getActivity(), android.R.layout.simple_list_item_1, skinsList);
		gridView.setAdapter(skinsAdapter);
		
		refreshSkins();
		
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.main, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_login:
	        	Intent intent = new Intent(getActivity(), MojangLoginActivity.class);
			    startActivity(intent);
			    return true;
	        case R.id.action_add:
	        	SkinsDatabase db = new SkinsDatabase(getActivity());
	        	db.addSkin(new Skin(-1, "Test", "Hihihi description", new Date()));
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
