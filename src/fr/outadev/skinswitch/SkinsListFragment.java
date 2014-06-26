package fr.outadev.skinswitch;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

public class SkinsListFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.skin_list, container, false);
		
		SkinsDatabase db = new SkinsDatabase(getActivity());
		List<Skin> array = db.getAllSkins();

		GridView gridview = (GridView) view.findViewById(R.id.grid_view);
		gridview.setAdapter(new SkinsListAdapter(getActivity(), android.R.layout.simple_list_item_1, array));

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
