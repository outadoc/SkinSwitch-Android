package fr.outadev.skinswitch;

import java.io.IOException;
import java.util.Date;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

public class NewCustomSkinActivity extends Activity {

	private EditText txt_name;
	private EditText txt_description;
	private EditText txt_source;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_custom_skin);

		txt_name = (EditText) findViewById(R.id.txt_skin_name);
		txt_description = (EditText) findViewById(R.id.txt_skin_description);
		txt_source = (EditText) findViewById(R.id.txt_skin_source);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_add_custom_skin, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
			case R.id.item_next:
				attemptSkinAddition();
				return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}

	private void attemptSkinAddition() {
		View errorField = null;
		// create a new skin from the data that was entered
		final Skin skin = new Skin(txt_name.getText().toString(), txt_description.getText().toString(), new Date());

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
			final ProgressDialog progDial = new ProgressDialog(this);
			progDial.setMessage("Downloading skin...");
			progDial.setIndeterminate(true);
			progDial.setCancelable(false);

			(new AsyncTask<Void, Void, Boolean>() {

				@Override
				protected void onPreExecute() {
					progDial.show();
				}

				@Override
				protected Boolean doInBackground(Void... params) {
					return skin.isValidSource();
				}

				@Override
				protected void onPostExecute(Boolean isValidSkinUrl) {
					progDial.hide();

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
								Toast.makeText(NewCustomSkinActivity.this, "Skin added successfully!", Toast.LENGTH_LONG).show();
								NewCustomSkinActivity.this.finish();
							}

						}).execute();

					} else {
						txt_source.setError(getString(R.string.error_incorrect_url));
						txt_source.requestFocus();
					}
				}

			}).execute();
		}
	}

}
