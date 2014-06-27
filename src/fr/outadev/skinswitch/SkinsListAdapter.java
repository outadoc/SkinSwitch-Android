package fr.outadev.skinswitch;

import java.io.FileNotFoundException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.outadev.skinswitch.skin.Skin;

public class SkinsListAdapter extends ArrayAdapter<Skin> {

	public SkinsListAdapter(Context context, int resource, List<Skin> array) {
		super(context, resource, array);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_icon, parent, false);
		}

		final ImageView skinView = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		TextView skinTitle = (TextView) convertView.findViewById(R.id.lbl_skin_title);

		final Skin skin = getItem(position);
		skinTitle.setText(skin.getName());

		(new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					return skin.getSkinHeadBitmap(getContext());
				} catch(FileNotFoundException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if(result != null) {
					skinView.setImageBitmap(result);
				} else {
					skinView.setImageResource(R.drawable.broken_image);
				}
			}

		}).execute();

		return convertView;
	}

}
