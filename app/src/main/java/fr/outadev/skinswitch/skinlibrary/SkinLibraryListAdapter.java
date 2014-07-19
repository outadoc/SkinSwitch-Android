package fr.outadev.skinswitch.skinlibrary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.Util;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

public class SkinLibraryListAdapter extends ArrayAdapter<SkinLibrarySkin> {

	private final int animTime;
	private Activity parentActivity;

	public SkinLibraryListAdapter(Activity parent, int resource, List<SkinLibrarySkin> objects) {
		super(parent, resource, objects);
		this.parentActivity = parent;
		animTime = getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_library_card, parent, false);
		}

		final SkinLibrarySkin skin = getItem(position);

		TextView txt_skin_name = (TextView) convertView.findViewById(R.id.lbl_skin_name);
		TextView txt_skin_description = (TextView) convertView.findViewById(R.id.lbl_skin_description);
		TextView txt_skin_author = (TextView) convertView.findViewById(R.id.lbl_skin_author);

		final ImageView img_skin_preview_front = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final ImageView img_skin_preview_back = (ImageView) convertView.findViewById(R.id.img_skin_preview_back);

		img_skin_preview_front.setVisibility(View.VISIBLE);
		img_skin_preview_front.setAlpha(1F);

		img_skin_preview_back.setVisibility(View.GONE);

		txt_skin_name.setText(skin.getName());
		txt_skin_description.setText((!skin.getDescription().isEmpty()) ? skin.getDescription() :
				getContext().getResources().getString(R.string.no_description_available));
		txt_skin_author.setText("Author: " + skin.getOwner());

		//loading images
		img_skin_preview_front.setImageResource(R.drawable.char_front);
		img_skin_preview_back.setImageResource(R.drawable.char_back);

		(new AsyncTask<Void, Void, Bitmap>() {

			// get front preview

			private Object tag;

			@Override
			protected void onPreExecute() {
				tag = skin.getSkinManagerId();

				img_skin_preview_front.setTag(skin.getSkinManagerId());
				img_skin_preview_back.setTag(skin.getSkinManagerId());
			}

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
				if(bitmap != null && tag.equals(img_skin_preview_front.getTag())) {
					img_skin_preview_front.setImageBitmap(bitmap);

					(new AsyncTask<Void, Void, Bitmap>() {

						// get back preview

						@Override
						protected Bitmap doInBackground(Void... voids) {
							try {
								return getItem(position).getBackSkinPreview(getContext());
							} catch(FileNotFoundException e) {
								return null;
							}
						}

						@Override
						protected void onPostExecute(Bitmap bitmap) {
							if(bitmap != null && tag.equals(img_skin_preview_back.getTag())) {
								img_skin_preview_back.setImageBitmap(bitmap);
							}
						}
					}).execute();
				}
			}
		}).execute();

		img_skin_preview_front.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Util.crossfade(img_skin_preview_front, img_skin_preview_back, animTime);
			}

		});

		img_skin_preview_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Util.crossfade(img_skin_preview_back, img_skin_preview_front, animTime);
			}

		});

		CardView cardView = (CardView) convertView.findViewById(R.id.card_view);
		cardView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				(new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... voids) {
						SkinLibrarySkin skin = getItem(position);
						skin.setCreationDate(new Date());
						SkinsDatabase db = new SkinsDatabase(getContext());
						db.addSkin(skin);

						try {
							skin.toSkin().downloadSkinFromSource(getContext());
						} catch(Exception e) {
							e.printStackTrace();
							Toast.makeText(getContext(), "Couldn't download the skin: " + e.getMessage(),
									Toast.LENGTH_LONG).show();
							cancel(true);
						}

						try {
							Thread.sleep(300);
						} catch(InterruptedException e) {
							//well, yup
						}

						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						parentActivity.finish();
					}

				}).execute();

			}

		});

		return convertView;
	}

}
