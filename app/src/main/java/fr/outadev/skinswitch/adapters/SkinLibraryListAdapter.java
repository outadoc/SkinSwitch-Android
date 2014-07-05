package fr.outadev.skinswitch.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;

public class SkinLibraryListAdapter extends ArrayAdapter<SkinLibrarySkin> {

	public SkinLibraryListAdapter(Context context, int resource, List<SkinLibrarySkin> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_library_card, parent, false);
		}

		TextView txt_skin_name = (TextView) convertView.findViewById(R.id.lbl_skin_name);
		TextView txt_skin_description = (TextView) convertView.findViewById(R.id.lbl_skin_description);
		TextView txt_skin_author = (TextView) convertView.findViewById(R.id.lbl_skin_author);

		final ImageView img_skin_preview = (ImageView) convertView.findViewById(R.id.img_skin_preview);

		txt_skin_name.setText(getItem(position).getName());
		txt_skin_description.setText((getItem(position).getDescription().length() != 0) ? getItem(position).getDescription() :
				getContext().getResources().getString(R.string.no_description_available));
		txt_skin_author.setText("Author: " + getItem(position).getOwner());

		img_skin_preview.setImageResource(R.drawable.char_front); //loading

		(new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... voids) {
				try {
					return getItem(position).getFrontSkinPreview(getContext());
				} catch(FileNotFoundException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				if(bitmap != null) {
					img_skin_preview.setImageBitmap(bitmap);
				}
			}
		}).execute();

		return convertView;
	}

}
