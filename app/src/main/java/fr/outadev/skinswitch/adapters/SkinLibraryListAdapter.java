package fr.outadev.skinswitch.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;

public class SkinLibraryListAdapter extends ArrayAdapter<SkinLibrarySkin> {

	public SkinLibraryListAdapter(Context context, int resource, List<SkinLibrarySkin> objects) {
		super(context, resource, objects);

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_library_card, parent, false);
		}

		return convertView;
	}

}
