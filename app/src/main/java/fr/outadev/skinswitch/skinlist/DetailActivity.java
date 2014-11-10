/*
 * SkinSwitch - DetailActivity
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

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shamanland.fab.FloatingActionButton;

import java.io.FileNotFoundException;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.Utils;
import fr.outadev.skinswitch.skin.BasicSkin;
import fr.outadev.skinswitch.skin.CustomUriSkin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

/**
 * The skin detail activity.
 *
 * @author outadoc
 */
public class DetailActivity extends ActionBarActivity implements OnSkinLoadingListener {

	public static final String SHARED_SKIN_IMAGE = "skin_preview";

	private BasicSkin skin;
	private int animTime;

	private FloatingActionButton b_wear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		ViewCompat.setTransitionName(findViewById(R.id.skin_preview_front), SHARED_SKIN_IMAGE);

		skin = (BasicSkin) getIntent().getSerializableExtra("skin");
		animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

		b_wear = (FloatingActionButton) findViewById(R.id.b_upload_skin);

		Log.d(Utils.TAG, skin.toString());

		setupSkinPreviews();
		setupText();
		setupButtons();

		applySystemWindowsBottomInset();
	}

	@Override
	protected void onResume() {
		super.onResume();

		SkinsDatabase db = new SkinsDatabase(this);
		skin = db.getSkin(skin.getId());

		setLoading(false);
		setupText();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin_details, menu);
		MenuItem shareItem = menu.findItem(R.id.action_share);

		if(skin instanceof CustomUriSkin) {
			ShareActionProvider shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
			shareActionProvider.setShareIntent(getDefaultIntent());
			shareItem.setEnabled(true);
		} else {
			shareItem.setEnabled(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.action_refresh_skin: {
				new AlertDialog.Builder(this)
						.setTitle(getString(R.string.refresh_skin_title))
						.setMessage(getString(R.string.refresh_skin_message))
						.setNegativeButton(getString(R.string.no), null)
						.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								(new AsyncTask<Void, Void, Void>() {

									@Override
									protected Void doInBackground(Void... voids) {
										try {
											skin.downloadSkinFromSource(DetailActivity.this);
										} catch(Exception e) {
											e.printStackTrace();
											Toast.makeText(DetailActivity.this, getResources().getString(R.string
															.error_skin_refresh,
													e.getMessage()), Toast.LENGTH_LONG).show();
										}

										return null;
									}

									@Override
									protected void onPostExecute(Void aVoid) {
										setupSkinPreviews();
										Toast.makeText(DetailActivity.this, getResources().getString(R.string
														.success_skin_refresh),
												Toast.LENGTH_LONG).show();
									}

								}).execute();
							}

						}).create().show();

				return true;
			}
			case R.id.action_delete_skin: {
				AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
				builder.setTitle(getResources().getString(R.string.delete_skin_title, skin.getName())).setMessage(getResources()
						.getString(R.string.delete_skin_message, skin.getName()));

				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						(new AsyncTask<Void, Void, Void>() {

							@Override
							protected Void doInBackground(Void... voids) {
								SkinsDatabase db = new SkinsDatabase(DetailActivity.this);
								db.removeSkin(skin);
								skin.deleteAllSkinResFromFilesystem(DetailActivity.this);
								DetailActivity.this.finish();

								return null;
							}

						}).execute();
					}

				});

				builder.setNegativeButton(R.string.no, null);
				builder.create().show();

				return true;
			}
			case R.id.action_edit_skin: {
				Intent intent = new Intent(this, NewCustomSkinActivity.class);
				intent.putExtra("skin", skin);
				startActivity(intent);
				return true;
			}
			case android.R.id.home:
				this.finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creates and displays the skin previews in the containers.
	 */
	private void setupSkinPreviews() {
		final ImageView img_skin_preview_front = (ImageView) findViewById(R.id.skin_preview_front);
		final ImageView img_skin_preview_back = (ImageView) findViewById(R.id.skin_preview_back);

		(new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... voids) {
				try {
					return skin.getFrontSkinPreview(DetailActivity.this);
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Bitmap bmp) {
				if(bmp != null) {
					img_skin_preview_front.setImageBitmap(bmp);
					colorizeInterface(bmp);
				}

				(new AsyncTask<Void, Void, Bitmap>() {

					@Override
					protected Bitmap doInBackground(Void... voids) {
						try {
							return skin.getBackSkinPreview(DetailActivity.this);
						} catch(FileNotFoundException e) {
							e.printStackTrace();
						}

						return null;
					}

					@Override
					protected void onPostExecute(Bitmap bmp) {
						if(bmp != null) {
							img_skin_preview_back.setImageBitmap(bmp);
						}
					}

				}).execute();
			}

		}).execute();

		img_skin_preview_front.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(view.getAlpha() == 1.0) {
					Utils.crossfade(img_skin_preview_front, img_skin_preview_back, animTime);
				}
			}

		});

		img_skin_preview_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				if(view.getAlpha() == 1.0) {
					Utils.crossfade(img_skin_preview_back, img_skin_preview_front, animTime);
				}
			}

		});
	}

	/**
	 * Sets up the title and description.
	 */
	private void setupText() {
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(skin.getName());

		TextView descriptionView = (TextView) findViewById(R.id.description);
		descriptionView.setText(skin.getDescription());
	}

	/**
	 * Sets up the buttons (colour and actions).
	 */
	private void setupButtons() {
		b_wear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				skin.initSkinUpload(DetailActivity.this, DetailActivity.this);
			}

		});
	}

	@TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
	private void applySystemWindowsBottomInset() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
			View containerView = findViewById(R.id.container);
			containerView.setFitsSystemWindows(true);

			containerView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {

				@Override
				public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
					DisplayMetrics metrics = getResources().getDisplayMetrics();

					if(metrics.widthPixels < metrics.heightPixels) {
						view.setPadding(0, 0, 0, windowInsets.getSystemWindowInsetBottom());
					} else {
						view.setPadding(0, 0, windowInsets.getSystemWindowInsetRight(), 0);
					}

					return windowInsets.consumeSystemWindowInsets();
				}

			});
		}
	}

	private void colorizeInterface(Bitmap skin) {
		Palette palette = Palette.generate(skin);

		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setTextColor(palette.getVibrantColor(Color.BLACK));

		TextView descriptionView = (TextView) findViewById(R.id.description);
		descriptionView.setTextColor(palette.getLightVibrantColor(Color.BLACK));

		int rippleColor = palette.getVibrantColor(getResources().getColor(R.color.loading_bar_one));

		colorRipple(R.id.b_upload_skin, rippleColor);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private void colorRipple(int id, int tintColor) {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			FloatingActionButton buttonView = (FloatingActionButton) findViewById(id);

			RippleDrawable ripple = (RippleDrawable) buttonView.getBackground();
			GradientDrawable rippleBackground = (GradientDrawable) ripple.getDrawable(0);
			rippleBackground.setColor(getResources().getColor(R.color.colorPrimary));

			ripple.setColor(ColorStateList.valueOf(tintColor));
		}
	}

	private Intent getDefaultIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_message, skin.getName(),
				((CustomUriSkin) skin).getSource()));
		sendIntent.setType("text/plain");
		return sendIntent;
	}

	@Override
	public void setLoading(boolean loading) {

	}
}
