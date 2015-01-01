/*
 * SkinSwitch - SkinsListAdapter
 * Copyright (C) 2014-2015  Baptiste Candellier
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

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * ArrayAdapter for displaying the main skins list.
 *
 * @author outadoc
 */
public class SkinsListAdapter extends ArrayAdapter<BasicSkin> {

	private static final long PRESS_ANIMATION_DURATION = 1000;
	private static final long RESET_ANIMATION_DURATION = 500;

	private final Activity activity;
	private SkinsListFragment frag;
	private Typeface minecraftiaFont;
	private boolean wasTutorialPlayed;

	public SkinsListAdapter(Activity activity, SkinsListFragment frag, int resource, List<BasicSkin> array) {
		super(activity, resource, array);
		this.frag = frag;
		this.activity = activity;

		minecraftiaFont = Typeface.createFromAsset(getContext().getAssets(), "Minecraftia.ttf");
		wasTutorialPlayed = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("wasTutorialPlayed", false);
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

		//if that's the first skin our user has ever added to the app,
		// show him the upload animation to hint him
		if(position == 0 && !wasTutorialPlayed) {
			skinView.animate()
					.setDuration(PRESS_ANIMATION_DURATION)
					.rotation(-180.0F)
					.scaleX(1.5F).scaleY(1.5F)
					.setListener(new Animator.AnimatorListener() {

						@Override
						public void onAnimationEnd(Animator animation) {
							skinView.animate()
									.setDuration(RESET_ANIMATION_DURATION)
									.rotation(0.0F)
									.scaleX(1.0F).scaleY(1.0F);
						}

						public void onAnimationStart(Animator animation) {
						}

						public void onAnimationCancel(Animator animation) {
						}

						public void onAnimationRepeat(Animator animation) {
						}

					});

			//reset the tutorial boolean so it doesn't show anymore after that
			wasTutorialPlayed = true;
			PreferenceManager.getDefaultSharedPreferences(getContext())
					.edit()
					.putBoolean("wasTutorialPlayed", true)
					.commit();
		}

		convertView.setOnTouchListener(new OnSkinHeadTouchListener(skin, skinView));
		return convertView;
	}

	public Activity getActivity() {
		return activity;
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

		private boolean isLoading;

		/**
		 * Creates a skin head listener.
		 *
		 * @param skin     the BasicSkin object associated with the view
		 * @param skinView the view (usually a skin head bitmap) the user will interact with
		 */
		public OnSkinHeadTouchListener(BasicSkin skin, View skinView) {
			this.skin = skin;
			this.skinView = skinView;
			this.isLoading = false;
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

			if(isLoading) {
				return;
			}

			timer.schedule(new TimerTask() {

				public void run() {
					frag.getActivity().runOnUiThread(new Runnable() {

						@Override
						public void run() {
							cancelAnimationAndGoBackToWork();

							skin.initSkinUpload(getActivity(), new OnSkinLoadingListener() {

								@Override
								public void setLoading(boolean loading) {
									if(loading) {
										isLoading = true;

										skinView.animate()
												.setInterpolator(new AccelerateInterpolator(0.6F))
												.setDuration(60000)
												.rotationBy(360 * 71);
									} else {
										isLoading = false;
										cancelAnimationAndGoBackToWork();
									}
								}

							});
						}

					});
				}

			}, PRESS_ANIMATION_DURATION);

			skinView.animate()
					.setDuration(PRESS_ANIMATION_DURATION)
					.rotation(-180.0F)
					.scaleX(1.5F).scaleY(1.5F);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				skinView.animate()
						.setDuration(PRESS_ANIMATION_DURATION)
						.translationZ(5.0F);
			}
		}

		private void onTouchEnd() {
			//releasing the skin head
			if((new Date()).getTime() - touchTimestamp < 300) {
				cancelAnimationAndGoBackToWork();

				ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
						getActivity(), skinView.findViewById(R.id.img_skin_preview), DetailActivity.SHARED_SKIN_IMAGE);
				Intent intent = new Intent(getContext(), DetailActivity.class);
				intent.putExtra("skin", skin);
				ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
			}

			if((new Date()).getTime() - touchTimestamp < PRESS_ANIMATION_DURATION) {
				if(!isLoading) {
					cancelAnimationAndGoBackToWork();
				}
			}

			timer.cancel();
			touchTimestamp = 0;
		}

		private void onTouchCancel() {
			if(timer != null) {
				timer.cancel();
			}

			if(!isLoading) {
				cancelAnimationAndGoBackToWork();
			}
			touchTimestamp = 0;
		}

		private void cancelAnimationAndGoBackToWork() {
			skinView.animate().cancel();
			skinView.animate()
					.setDuration(RESET_ANIMATION_DURATION)
					.rotation(0.0F)
					.scaleX(1.0F).scaleY(1.0F);

			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				skinView.animate()
						.setDuration(RESET_ANIMATION_DURATION)
						.translationZ(2.0F);
			}
		}

	}

}
