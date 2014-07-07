package fr.outadev.skinswitch.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.FileNotFoundException;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.Util;
import fr.outadev.skinswitch.network.MojangConnectionHandler;
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;
import fr.outadev.skinswitch.user.UsersManager;

/**
 * Created by outadoc on 06/07/14.
 */
public class DetailActivity extends Activity {

	private Skin skin;
	private ShareActionProvider shareActionProvider;
	private int animTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		skin = (Skin) getIntent().getSerializableExtra("skin");
		animTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

		setupSkinPreviews();
		setupText();
		setupButtons();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin_details, menu);

		MenuItem shareItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
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

		(new AsyncTask<Void, Void, Void>() {

			Bitmap bmp_front;
			Bitmap bmp_back;

			@Override
			protected Void doInBackground(Void... voids) {
				try {
					bmp_front = skin.getFrontSkinPreview(DetailActivity.this);
					bmp_back = skin.getBackSkinPreview(DetailActivity.this);
				} catch(FileNotFoundException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(Void void0) {
				if(bmp_front != null && bmp_back != null) {
					img_skin_preview_front.setImageBitmap(bmp_front);
					img_skin_preview_back.setImageBitmap(bmp_back);
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
	}

	private void setupText() {
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(skin.getName());

		TextView descriptionView = (TextView) findViewById(R.id.description);
		descriptionView.setText(skin.getDescription());
	}

	private void setupButtons() {
		ImageButton b_delete = (ImageButton) findViewById(R.id.b_delete);
		ImageButton b_wear = (ImageButton) findViewById(R.id.b_upload_skin);

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
				AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this);
				builder.setTitle("Wear " + skin.getName() + "?").setMessage("Do you really want to replace your current " +
						"Minecraft skin with " + skin.getName() + "?");

				builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {
						(new AsyncTask<Void, Void, Exception>() {

							@Override
							protected Exception doInBackground(Void... voids) {
								MojangConnectionHandler handler = new MojangConnectionHandler();
								UsersManager um = new UsersManager(DetailActivity.this);

								try {
									handler.loginWithCredentials(um.getUser());
									handler.uploadSkinToMojang(skin.getRawSkinFile(DetailActivity.this));
								} catch(Exception e) {
									return e;
								}

								return null;
							}

							@Override
							protected void onPostExecute(Exception e) {
								if(e != null) {
									e.printStackTrace();
								}
							}

						}).execute();
					}

				});

				builder.setNegativeButton(R.string.no, null);
				builder.create().show();
			}

		});
	}

	private Intent getDefaultIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + skin.getName() + "! " + skin.getSource() + " #SkinSwitch");
		sendIntent.setType("text/plain");
		return sendIntent;
	}
}
