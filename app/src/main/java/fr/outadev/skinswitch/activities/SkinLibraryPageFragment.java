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
import fr.outadev.skinswitch.adapters.EndlessScrollListener;
import fr.outadev.skinswitch.adapters.SkinLibraryListAdapter;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;

public class SkinLibraryPageFragment extends Fragment {

	public static final String ARG_ENDPOINT = "EndPoint";
	public static final String ARG_SEARCH_QUERY = "SearchQuery";

	private EndPoint endPoint;
	private String searchQuery;

	private List<SkinLibrarySkin> skinsList;
	private SkinLibraryListAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;

	private static final int MAX_SKINS_LOAD_COUNT = 15;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		Bundle args = getArguments();

		if(args == null) {
			endPoint = EndPoint.RANDOM_SKINS;
			searchQuery = ".";
		} else {
			endPoint = (EndPoint) args.get(ARG_ENDPOINT);
			searchQuery = args.getString(ARG_SEARCH_QUERY);
		}

		View view = inflater.inflate(R.layout.fragment_skin_library_list, container, false);

		swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
		swipeRefreshLayout.setColorSchemeResources(R.color.loading_bar_one, R.color.loading_bar_two, R.color.loading_bar_one,
				R.color.loading_bar_two);
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

		listView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				loadSkinsFromNetwork(true);
			}

		});

		loadSkinsFromNetwork(false);
		return view;
	}

	private void loadSkinsFromNetwork() {
		loadSkinsFromNetwork(false);
	}

	private void loadSkinsFromNetwork(final boolean append) {
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
						return handler.fetchLatestSkins(MAX_SKINS_LOAD_COUNT, skinsList.size());
					case RANDOM_SKINS:
						return handler.fetchRandomSkins(MAX_SKINS_LOAD_COUNT);
					case SEARCH_SKINS:
						return handler.fetchSkinByName(searchQuery, MAX_SKINS_LOAD_COUNT, skinsList.size());
					default:
						return null;
				}
			}

			@Override
			protected void onPostExecute(List<SkinLibrarySkin> result) {
				System.out.println(result);

				if(result != null) {
					if(!append) {
						skinsList.clear();
					}

					skinsList.addAll(result);
					adapter.notifyDataSetChanged();
				}

				swipeRefreshLayout.setRefreshing(false);
			}

		}).execute();
	}

}
