package fr.outadev.skinswitch.wear;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

import fr.outadev.skinswitch.R;

public class MainActivity extends Activity {

	private static final int SPEECH_REQUEST_CODE = 0;
	private static final String TAG = "SkinSwitch/Wear";
	GoogleApiClient mGoogleApiClient;
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

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

					@Override
					public void onConnected(Bundle connectionHint) {
						Log.d(TAG, "onConnected: " + connectionHint);
						displaySpeechRecognizer();
					}

					@Override
					public void onConnectionSuspended(int cause) {
						Log.d(TAG, "onConnectionSuspended: " + cause);
					}

				})
				.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

					@Override
					public void onConnectionFailed(ConnectionResult result) {
						Log.d(TAG, "onConnectionFailed: " + result);
					}

				})
				.addApi(Wearable.API)
				.build();

		mGoogleApiClient.connect();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);
			sendSkinRequest(spokenText);
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

	private void sendSkinRequest(final String skinName) {
		lblSkinName.setText(skinName);

		if(mGoogleApiClient.isConnected()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

					for(Node node : nodes.getNodes()) {
						MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
								"getSkin", skinName.getBytes()).await();

						if(!result.getStatus().isSuccess()) {
							Log.e(TAG, "error");
						} else {
							Log.i(TAG, "success!! sent to: " + node.getDisplayName());
						}
					}
				}

			}).start();

		} else {
			Log.e(TAG, "not connected");
		}
	}
}
