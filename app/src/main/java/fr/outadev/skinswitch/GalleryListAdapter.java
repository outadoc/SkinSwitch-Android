/*
 * SkinSwitch - GalleryListAdapter
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

import android.app.Activity;
import android.app.ProgressDialog;
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

import com.github.kevinsawicki.http.HttpRequest;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * ArrayAdapter for displaying the skins on a skin library page.
 *
 * @author outadoc
 */
public class GalleryListAdapter extends ArrayAdapter<GallerySkin> {

	private final int animTime;
	private Activity parentActivity;
	private ProgressDialog progDial;

	public GalleryListAdapter(Activity parent, int resource, List<GallerySkin> objects) {
		super(parent, resource, objects);
		this.parentActivity = parent;
		animTime = getContext().getResources().getInteger(android.R.integer.config_mediumAnimTime);

		progDial = new ProgressDialog(getContext());
		progDial.setMessage(getContext().getResources().getString(R.string.downloading_custom_skin));
		progDial.setIndeterminate(true);
		progDial.setCancelable(false);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_gallery_card, parent, false);
		}

		final GallerySkin skin = getItem(position);

		TextView txt_skin_name = (TextView) convertView.findViewById(R.id.lbl_skin_name);
		TextView txt_skin_description = (TextView) convertView.findViewById(R.id.lbl_skin_description);
		TextView txt_skin_author = (TextView) convertView.findViewById(R.id.lbl_skin_author);

		final ImageView img_skin_preview_front = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final ImageView img_skin_preview_back = (ImageView) convertView.findViewById(R.id.img_skin_preview_back);

		img_skin_preview_front.setAlpha(0.0F);

		img_skin_preview_front.setVisibility(View.VISIBLE);
		img_skin_preview_back.setVisibility(View.GONE);

		txt_skin_name.setText(skin.getName());
		txt_skin_description.setText((!skin.getDescription().isEmpty()) ? skin.getDescription() :
				getContext().getResources().getString(R.string.no_description_available));
		txt_skin_author.setText(getContext().getResources().getString(R.string.gallery_skin_author, skin.getOwner()));

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
					img_skin_preview_front.setAlpha(1.0F);

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
				Utils.crossfade(img_skin_preview_front, img_skin_preview_back, animTime);
			}

		});

		img_skin_preview_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Utils.crossfade(img_skin_preview_back, img_skin_preview_front, animTime);
			}

		});

		CardView cardView = (CardView) convertView.findViewById(R.id.card_view);
		cardView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				(new DownloadSkinAsyncTask()).execute(position);
			}

		});

		return convertView;
	}

	private class DownloadSkinAsyncTask extends AsyncTask<Integer, Void, Exception> {

		private int position;

		@Override
		protected Exception doInBackground(Integer... position) {
			this.position = position[0];

			GallerySkin skin = getItem(this.position);
			skin.setCreationDate(new Date());
			SkinsDatabase db = new SkinsDatabase(getContext());

			try {
				skin.toDownloadableSkin().downloadSkinFromSource(getContext());
			} catch(IOException ignored) {
			} catch(HttpRequest.HttpRequestException e) {
				e.printStackTrace();
				return e;
			}

			db.addSkin(skin);

			try {
				Thread.sleep(300);
			} catch(InterruptedException ignored) {
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			progDial.show();
		}

		@Override
		protected void onPostExecute(Exception e) {
			if(e != null) {
				Snackbar.with(parentActivity)
						.text(R.string.error_skin_download)
						.actionLabel(R.string.error_retry)
						.actionColorResource(R.color.colorAccent)
						.actionListener(new ActionClickListener() {

							@Override
							public void onActionClicked() {
								(new DownloadSkinAsyncTask()).execute(position);
							}

						})
						.show(parentActivity);
			}

			progDial.hide();
			parentActivity.finish();
		}

	}

}
