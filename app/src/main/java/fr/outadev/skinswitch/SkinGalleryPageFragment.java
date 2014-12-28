/*
 * SkinSwitch - SkinGalleryPageFragment
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

import java.util.ArrayList;
import java.util.List;

import fr.outadev.skinswitch.network.SkinManagerConnectionHandler;
import fr.outadev.skinswitch.network.SkinManagerConnectionHandler.EndPoint;

/**
 * Fragment containing a skin library tab.
 * Displays a list of skins downloaded from the API, depending on the requested end point.
 *
 * @author outadoc
 */
public class SkinGalleryPageFragment extends Fragment {

	public static final String ARG_ENDPOINT = "EndPoint";
	public static final String ARG_SEARCH_QUERY = "SearchQuery";
	private static final int MAX_SKINS_LOAD_COUNT = 15;

	private EndPoint endPoint;
	private String searchQuery;
	private List<SkinGallerySkin> skinsList;
	private SkinGalleryListAdapter adapter;
	private SwipeRefreshLayout swipeRefreshLayout;

	private ProgressBar progressBar;

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

		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
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
		skinsList = new ArrayList<SkinGallerySkin>();
		adapter = new SkinGalleryListAdapter(getActivity(), android.R.layout.simple_list_item_1, skinsList);
		listView.setAdapter(adapter);

		listView.setOnScrollListener(new EndlessScrollListener(getResources().getInteger(R.integer
				.endless_scroll_visible_threshold)) {

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

	/**
	 * Downloads a list of skins from the API and displays them in the list.
	 *
	 * @param append determines whether the downloaded skins should be appended to the list or replace it.
	 */
	private void loadSkinsFromNetwork(final boolean append) {
		(new AsyncTask<Void, Void, List<SkinGallerySkin>>() {

			@Override
			protected void onPreExecute() {
				swipeRefreshLayout.setRefreshing(true);
			}

			@Override
			protected List<SkinGallerySkin> doInBackground(Void... params) {
				SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler(getActivity());

				try {
					switch(endPoint) {
						case LATEST_SKINS:
							return handler.fetchLatestSkins(MAX_SKINS_LOAD_COUNT, skinsList.size());
						case RANDOM_SKINS:
							return handler.fetchRandomSkins(MAX_SKINS_LOAD_COUNT);
						case SEARCH_SKINS:
							return handler.fetchSkinByName(searchQuery, MAX_SKINS_LOAD_COUNT, skinsList.size());
						case ALL_SKINS:
							return handler.fetchAllSkins(MAX_SKINS_LOAD_COUNT, skinsList.size());
					}
				} catch(HttpRequest.HttpRequestException e) {
					e.printStackTrace();
					return null;
				}

				return null;
			}

			@Override
			protected void onPostExecute(List<SkinGallerySkin> result) {
				progressBar.setVisibility(View.GONE);
				swipeRefreshLayout.setRefreshing(false);

				if(result != null) {
					if(!append) {
						skinsList.clear();
					}

					skinsList.addAll(result);
					adapter.notifyDataSetChanged();
				} else {
					if(isAdded()) {
						Toast.makeText(getActivity(), getResources().getString(R.string.gallery_connection_error),
								Toast.LENGTH_LONG).show();
					}
				}
			}

		}).execute();
	}

}
