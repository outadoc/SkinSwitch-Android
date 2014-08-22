/*
 * SkinSwitch - SkinsListAdapter
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

package fr.outadev.skinswitch.skinlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.BasicSkin;

/**
 * ArrayAdapter for displaying the main skins list.
 *
 * @author outadoc
 */
public class SkinsListAdapter extends ArrayAdapter<BasicSkin> {

	private SkinsListFragment frag;
	private Typeface minecraftiaFont;
	private boolean wasTutorialPlayed;

	private Animation expandAnim;
	private Animation loadingAnim;

	public SkinsListAdapter(Context context, SkinsListFragment frag, int resource, List<BasicSkin> array) {
		super(context, resource, array);
		this.frag = frag;

		minecraftiaFont = Typeface.createFromAsset(getContext().getAssets(), "Minecraftia.ttf");
		wasTutorialPlayed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("wasTutorialPlayed", false);

		expandAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_skin_rotation);
		loadingAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_skin_nod);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_icon, parent, false);
			((TextView) convertView.findViewById(R.id.lbl_skin_title)).setTypeface(minecraftiaFont);
		}

		final ImageView skinView = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final TextView skinTitle = (TextView) convertView.findViewById(R.id.lbl_skin_title);

		final BasicSkin skin = getItem(position);

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

		if(position == 0 && !wasTutorialPlayed) {

		}

		convertView.setOnTouchListener(new OnSkinHeadTouchListener(skin, skinView));
		return convertView;
	}

	/**
	 * Listener that listens to touch events on the skin icons.
	 * Will make them spin when the user touches them, initialize the upload after a certain time.
	 * If the user releases it quick enough, open the skin details.
	 *
	 * @author outadoc
	 */
	private class OnSkinHeadTouchListener implements View.OnTouchListener {

		private final BasicSkin skin;
		private final View skinView;

		private long touchTimestamp;
		private Timer timer;

		/**
		 * Creates a skin head listener.
		 *
		 * @param skin     the BasicSkin object associated with the view
		 * @param skinView the view (usually a skin head bitmap) the user will interact with
		 */
		public OnSkinHeadTouchListener(BasicSkin skin, View skinView) {
			this.skin = skin;
			this.skinView = skinView;
		}

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			switch(motionEvent.getAction()) {
				case MotionEvent.ACTION_DOWN:
					onTouchStart();
					break;
				case MotionEvent.ACTION_UP:
					onTouchEnd();
					break;
				case MotionEvent.ACTION_CANCEL:
					onTouchCancel();
					break;
			}

			return true;
		}

		private void onTouchStart() {
			//touching the skin head
			touchTimestamp = (new Date()).getTime();
			timer = new Timer();

			timer.schedule(new TimerTask() {

				public void run() {
					frag.getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							skin.initSkinUpload(getContext(), new OnSkinLoadingListener() {

								@Override
								public void setLoading(boolean loading) {
									if(loading) {
										skinView.startAnimation(loadingAnim);
									} else {
										skinView.clearAnimation();
									}
								}

							});
						}

					});
				}

			}, 1000);

			skinView.startAnimation(expandAnim);
		}

		private void onTouchEnd() {
			//releasing the skin head
			if((new Date()).getTime() - touchTimestamp < 300) {
				skinView.clearAnimation();

				Intent intent = new Intent(getContext(), DetailActivity.class);
				intent.putExtra("skin", skin);
				getContext().startActivity(intent);
			}

			if((new Date()).getTime() - touchTimestamp < 1000) {
				skinView.clearAnimation();
			}

			timer.cancel();
			touchTimestamp = 0;
		}

		private void onTouchCancel() {
			if(timer != null) {
				timer.cancel();
			}

			skinView.clearAnimation();
			touchTimestamp = 0;
		}

	}

}
