package fr.outadev.skinswitch.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.Skin;

/**
 * Created by outadoc on 06/07/14.
 */
public class DetailActivity extends Activity {

	private Skin skin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);

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

}
