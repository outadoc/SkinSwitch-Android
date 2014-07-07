package fr.outadev.skinswitch.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.activities.DetailActivity;
import fr.outadev.skinswitch.activities.SkinsListFragment;
import fr.outadev.skinswitch.skin.Skin;

public class SkinsListAdapter extends ArrayAdapter<Skin> {

	private SkinsListFragment frag;

	public SkinsListAdapter(Context context, SkinsListFragment frag, int resource, List<Skin> array) {
		super(context, resource, array);
		this.frag = frag;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_icon, parent, false);
		}

		final ImageView skinView = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final TextView skinTitle = (TextView) convertView.findViewById(R.id.lbl_skin_title);

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

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				/*SkinsDatabase db = new SkinsDatabase(getContext());
				db.removeSkin(skin);
				frag.refreshSkins();*/
				Intent intent = new Intent(getContext(), DetailActivity.class);
				Bundle args = new Bundle();
				intent.putExtra("skin", skin);
				getContext().startActivity(intent);
			}

		});

		return convertView;
	}

}
