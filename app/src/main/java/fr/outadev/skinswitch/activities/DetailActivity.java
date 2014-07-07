package fr.outadev.skinswitch.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.io.FileNotFoundException;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.Skin;

/**
 * Created by outadoc on 06/07/14.
 */
public class DetailActivity extends Activity {

	private Skin skin;
	private ShareActionProvider shareActionProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		skin = (Skin) getIntent().getSerializableExtra("skin");

		setupSkinPreview();
		setupText();
	}

	private void setupSkinPreview() {
		ImageView img_skin = (ImageView) findViewById(R.id.skin_preview_front);

		try {
			img_skin.setImageBitmap(skin.getFrontSkinPreview(this));
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setupText() {
		TextView titleView = (TextView) findViewById(R.id.title);
		titleView.setText(skin.getName());

		TextView descriptionView = (TextView) findViewById(R.id.description);
		descriptionView.setText(skin.getDescription());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.skin_details, menu);

		MenuItem shareItem = menu.findItem(R.id.action_share);
		shareActionProvider = (ShareActionProvider) shareItem.getActionProvider();
		shareActionProvider.setShareIntent(getDefaultIntent());

		return true;
	}

	private Intent getDefaultIntent() {
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + skin.getName() + "! " + skin.getSource() + " #SkinSwitch");
		sendIntent.setType("text/plain");
		return sendIntent;
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
}
