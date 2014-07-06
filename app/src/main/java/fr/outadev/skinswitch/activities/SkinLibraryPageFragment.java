package fr.outadev.skinswitch.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.adapters.SkinLibraryListAdapter;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;

public class SkinLibraryPageFragment extends Fragment {

	public static final String ARG_ENDPOINT = "EndPoint"; //$NON-NLS-1$
	private EndPoint endPoint;
	private List<SkinLibrarySkin> skinsList;
	private SkinLibraryListAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		Bundle args = getArguments();
		endPoint = (EndPoint) args.get(ARG_ENDPOINT);

		View view = inflater.inflate(R.layout.fragment_skin_library_list, container, false);

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent, R.color.colorPrimary,
				R.color.colorAccent);
		swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

			@Override
			public void onRefresh() {
				loadSkinsFromNetwork();
			}

		});

		ListView listView = (ListView) view.findViewById(R.id.skins_library_list);
		skinsList = new ArrayList<SkinLibrarySkin>();
		adapter = new SkinLibraryListAdapter(getActivity(), android.R.layout.simple_list_item_1, skinsList);
		listView.setAdapter(adapter);

		loadSkinsFromNetwork();
		return view;
	}

	private void loadSkinsFromNetwork() {
		(new AsyncTask<Void, Void, List<SkinLibrarySkin>>() {

			@Override
			protected void onPreExecute() {
				swipeRefreshLayout.setRefreshing(true);
			}

			@Override
			protected List<SkinLibrarySkin> doInBackground(Void... params) {
				SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler();

				switch(endPoint) {
					case LATEST_SKINS:
						return handler.fetchLatestSkins();
					case RANDOM_SKINS:
						return handler.fetchRandomSkins();
					case SEARCH_SKINS:
						return handler.fetchSkinByName("outadoc");
					default:
						return null;
				}
			}

			@Override
			protected void onPostExecute(List<SkinLibrarySkin> result) {
				if(result != null) {
					skinsList.clear();
					skinsList.addAll(result);
					adapter.notifyDataSetChanged();
				}

				swipeRefreshLayout.setRefreshing(false);
			}

		}).execute();
	}

}
