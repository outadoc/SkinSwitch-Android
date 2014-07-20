package fr.outadev.skinswitch.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.widget.TextView;

import java.util.List;

import fr.outadev.skinswitch.R;

public class MainActivity extends Activity {

	private static final int SPEECH_REQUEST_CODE = 0;
	private TextView lblSkinName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				lblSkinName = (TextView) stub.findViewById(R.id.lbl_skin_name);
			}

		});

		displaySpeechRecognizer();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);

			lblSkinName.setText(spokenText);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Create an intent that can start the Speech Recognizer activity.
	 */
	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak the name of a skin");
		// Start the activity, the intent will be populated with the speech text
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}
}
