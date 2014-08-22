/*
 * SkinSwitch - NewCustomSkinActivity
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

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Date;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.BasicSkin;
import fr.outadev.skinswitch.skin.InvalidSkinSizeException;
import fr.outadev.skinswitch.skin.SkinsDatabase;

/**
 * Activity for creating a new custom skin (from an URL or username).
 *
 * @author outadoc
 */
public class NewCustomSkinActivity extends Activity {

	private EditText txt_name;
	private EditText txt_description;
	private EditText txt_source;

	private BasicSkin editSkin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_custom_skin);

		if(getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}

		editSkin = (BasicSkin) getIntent().getSerializableExtra("skin");

		txt_name = (EditText) findViewById(R.id.txt_skin_name);
		txt_description = (EditText) findViewById(R.id.txt_skin_description);
		txt_source = (EditText) findViewById(R.id.txt_skin_source);

		if(editSkin != null) {
			setTitle(getResources().getString(R.string.title_activity_edit_skin));

			txt_source.setVisibility(View.GONE);
			findViewById(R.id.lbl_skin_source).setVisibility(View.GONE);

			txt_name.setText(editSkin.getName());
			txt_description.setText(editSkin.getDescription());
			txt_source.setText(editSkin.getSource());
		}

		// handle URIs like skinswitch://?name=foo&desc=bar&url=foobar
		Uri queryUri = getIntent().getData();

		if(queryUri != null) {
			String name = queryUri.getQueryParameter("name");
			String desc = queryUri.getQueryParameter("desc");
			String url = queryUri.getQueryParameter("url");

			if(name != null && desc != null && url != null) {
				txt_name.setText(name);
				txt_description.setText(desc);
				txt_source.setText(url);
			}
		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_next:
				validateAndParseUserInput();
				return true;
			case android.R.id.home:
				this.finish();
				return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_custom_skin, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Validates the user input, displays error messages if it's incorrect, and
	 * tries to add the skin if the input has been validated successfully.
	 */
	private void validateAndParseUserInput() {
		View errorField = null;
		// create a new skin from the data that was entered
		final BasicSkin skin = new BasicSkin(txt_name.getText().toString(), txt_description.getText().toString(), new Date());

		// check skin name
		if(TextUtils.isEmpty(skin.getName())) {
			txt_name.setError(getString(R.string.error_field_required));
			errorField = txt_name;
		} else {
			txt_name.setError(null);
		}

		// check source
		if(TextUtils.isEmpty(txt_source.getText().toString())) {
			txt_source.setError(getString(R.string.error_field_required));
			errorField = txt_source;
		} else if(!txt_source.getText().toString().matches("(https?:\\/\\/.+)|([a-zA-Z0-9_\\-]+)")) {
			txt_source.setError(getString(R.string.error_incorrect_url));
			errorField = txt_source;
		} else {
			txt_source.setError(null);
		}

		// parse the source
		if(!txt_source.getText().toString().matches("https?:\\/\\/.+")) {
			skin.setSource("http://s3.amazonaws.com/MinecraftSkins/" + txt_source.getText().toString() + ".png");
		} else {
			skin.setSource(txt_source.getText().toString());
		}

		if(errorField != null) {
			errorField.requestFocus();
		} else {
			if(editSkin != null) {
				updateSkin();
			} else {
				downloadAndAddSkin(skin);
			}
		}
	}

	/**
	 * Downloads a skin, adds it to the database, and saves it to the
	 * filesystem.
	 *
	 * @param skin the skin to download.
	 */
	private void downloadAndAddSkin(final BasicSkin skin) {
		final ProgressDialog progDial = new ProgressDialog(this);
		progDial.setMessage(getResources().getString(R.string.downloading_custom_skin));
		progDial.setIndeterminate(true);
		progDial.setCancelable(false);

		(new AsyncTask<Void, Void, Boolean>() {

			@Override
			protected void onPreExecute() {
				progDial.show();
			}

			@Override
			protected Boolean doInBackground(Void... params) {
				try {
					return skin.isValidSource();
				} catch(final InvalidSkinSizeException e) {
					NewCustomSkinActivity.this.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(NewCustomSkinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}

					});

					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean isValidSkinUrl) {
				if(isValidSkinUrl) {
					(new AsyncTask<Void, Void, Void>() {

						@Override
						protected Void doInBackground(Void... params) {
							SkinsDatabase db = new SkinsDatabase(NewCustomSkinActivity.this);
							db.addSkin(skin);

							try {
								skin.downloadSkinFromSource(NewCustomSkinActivity.this);
							} catch(NetworkErrorException e) {
								e.printStackTrace();
							} catch(IOException e) {
								e.printStackTrace();
							}

							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							progDial.hide();
							progDial.dismiss();

							Toast.makeText(NewCustomSkinActivity.this, getResources().getString(R.string.success_skin_added),
									Toast.LENGTH_LONG).show();
							NewCustomSkinActivity.this.finish();
						}

					}).execute();

				} else {
					progDial.hide();
					progDial.dismiss();

					txt_source.setError(getString(R.string.error_incorrect_url));
					txt_source.requestFocus();
				}
			}

		}).execute();
	}

	private void updateSkin() {
		editSkin.setName(txt_name.getText().toString());
		editSkin.setDescription(txt_description.getText().toString());

		SkinsDatabase db = new SkinsDatabase(this);
		db.updateSkin(editSkin);
		Toast.makeText(NewCustomSkinActivity.this, getResources().getString(R.string.success_skin_updated),
				Toast.LENGTH_LONG).show();
		finish();
	}

}
