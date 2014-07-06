package fr.outadev.skinswitch.adapters;

import android.accounts.NetworkErrorException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.SkinLibrarySkin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

public class SkinLibraryListAdapter extends ArrayAdapter<SkinLibrarySkin> {

	private Activity parentActivity;
	private final int animTime;

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

		TextView txt_skin_name = (TextView) convertView.findViewById(R.id.lbl_skin_name);
		TextView txt_skin_description = (TextView) convertView.findViewById(R.id.lbl_skin_description);
		TextView txt_skin_author = (TextView) convertView.findViewById(R.id.lbl_skin_author);

		final ImageView img_skin_preview_front = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final ImageView img_skin_preview_back = (ImageView) convertView.findViewById(R.id.img_skin_preview_back);

		img_skin_preview_front.setVisibility(View.VISIBLE);
		img_skin_preview_front.setAlpha(1F);

		img_skin_preview_back.setVisibility(View.GONE);

		txt_skin_name.setText(getItem(position).getName());
		txt_skin_description.setText((getItem(position).getDescription().length() != 0) ? getItem(position).getDescription() :
				getContext().getResources().getString(R.string.no_description_available));
		txt_skin_author.setText("Author: " + getItem(position).getOwner());

		//loading images
		img_skin_preview_front.setImageResource(R.drawable.char_front);
		img_skin_preview_back.setImageResource(R.drawable.char_back);

		(new AsyncTask<Void, Void, Bitmap>() {

			// get front preview

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
							if(bitmap != null) {
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
				crossfade(img_skin_preview_front, img_skin_preview_back);
			}

		});

		img_skin_preview_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				crossfade(img_skin_preview_back, img_skin_preview_front);
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
						} catch(NetworkErrorException e) {
							e.printStackTrace();
						} catch(IOException e) {
							e.printStackTrace();
						}

						try {
							Thread.sleep(300);
						} catch(InterruptedException e) {

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

	private void crossfade(final View oldView, final View newView) {

		// Set the new view to 0% opacity but visible, so that it is visible
		// (but fully transparent) during the animation.
		newView.setAlpha(0f);
		newView.setVisibility(View.VISIBLE);

		// Animate the new view to 100% opacity, and clear any animation
		// listener set on the view.
		newView.animate()
				.alpha(1f)
				.setDuration(animTime)
				.setListener(null);

		// Animate the old view to 0% opacity. After the animation ends,
		// set its visibility to GONE as an optimization step (it won't
		// participate in layout passes, etc.)
		oldView.animate()
				.alpha(0f)
				.setDuration(animTime)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						oldView.setVisibility(View.GONE);
					}
				});
	}

}
