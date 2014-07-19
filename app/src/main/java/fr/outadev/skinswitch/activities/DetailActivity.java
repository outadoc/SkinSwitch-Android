package fr.outadev.skinswitch.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.FileNotFoundException;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.Util;
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

/**
 * Created by outadoc on 06/07/14.
 */
public class DetailActivity extends Activity implements ILoadingActivity {

	private Skin skin;
	private int animTime;

	private ImageButton b_delete;
	private ImageButton b_wear;

	private FrameLayout b_upload_skin_container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		skin = (Skin) getIntent().getSerializableExtra("skin");
		animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

		b_delete = (ImageButton) findViewById(R.id.b_delete);
		b_wear = (ImageButton) findViewById(R.id.b_upload_skin);

		b_upload_skin_container = (FrameLayout) findViewById(R.id.b_upload_skin_container);

		setupSkinPreviews();
		setupText();
		setupButtons();

		setOutlines();
		applySystemWindowsBottomInset(R.id.container);

		setLoading(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin_details, menu);

		MenuItem shareItem = menu.findItem(R.id.action_share);
		ShareActionProvider shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		shareActionProvider.setShareIntent(getDefaultIntent());

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				this.finish();
				return true;
		}

		return super.onOptionsItemSelected(item);
	}

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
				Util.crossfade(img_skin_preview_front, img_skin_preview_back, animTime);
			}

		});

		img_skin_preview_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Util.crossfade(img_skin_preview_back, img_skin_preview_front, animTime);
			}

		});
	}

	private void setupText() {
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(skin.getName());

		TextView descriptionView = (TextView) findViewById(R.id.description);
		descriptionView.setText(skin.getDescription());
	}

	private void setupButtons() {
		b_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
				builder.setTitle("Delete " + skin.getName() + "?").setMessage("Do you really want to delete this skin?");

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
			}

		});

		b_wear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				skin.initSkinUpload(DetailActivity.this);
			}

		});
	}

	private void setOutlines() {
		int size = getResources().getDimensionPixelSize(R.dimen.floating_button_size);

		Outline outline = new Outline();
		outline.setOval(0, 0, size, size);

		b_delete.setOutline(outline);
		b_upload_skin_container.setOutline(outline);
	}

	private void applySystemWindowsBottomInset(int container) {
		View containerView = findViewById(container);
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

	@SuppressWarnings("ConstantConditions")
	private void colorizeInterface(Bitmap skin) {
		Palette palette = Palette.generate(skin);

		if(palette.getVibrantColor() != null) {
			TextView titleView = (TextView) findViewById(R.id.title);
			titleView.setTextColor(palette.getVibrantColor().getRgb());
		}

		if(palette.getLightVibrantColor() != null) {
			TextView descriptionView = (TextView) findViewById(R.id.description);
			descriptionView.setTextColor(palette.getLightVibrantColor().getRgb());
		}

		colorRipple(R.id.b_delete, (palette.getVibrantColor() != null) ? palette.getVibrantColor().getRgb() : getResources()
				.getColor(R.color.loading_bar_one));
		colorRipple(R.id.b_upload_skin, (palette.getVibrantColor() != null) ? palette.getVibrantColor().getRgb() : getResources
				().getColor(R.color.loading_bar_one));
	}

	private void colorRipple(int id, int tintColor) {
		View buttonView = findViewById(id);

		RippleDrawable ripple = (RippleDrawable) buttonView.getBackground();
		GradientDrawable rippleBackground = (GradientDrawable) ripple.getDrawable(0);
		rippleBackground.setColor(getResources().getColor(R.color.colorPrimary));

		ripple.setColor(ColorStateList.valueOf(tintColor));
	}

	private Intent getDefaultIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + skin.getName() + "! " + skin.getSource() + " #SkinSwitch");
		sendIntent.setType("text/plain");
		return sendIntent;
	}

	@Override
	public void setLoading(boolean loading) {
		View bLoading = findViewById(R.id.b_loading);
		View bWear = findViewById(R.id.b_upload_skin);

		bLoading.setVisibility((loading) ? View.VISIBLE : View.GONE);
		bWear.setVisibility((loading) ? View.GONE : View.VISIBLE);
	}
}
