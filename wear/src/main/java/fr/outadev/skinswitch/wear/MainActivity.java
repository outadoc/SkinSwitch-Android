package fr.outadev.skinswitch.wear;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.io.InputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.outadev.skinswitch.R;

public class MainActivity extends Activity implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks {

	private static final int SPEECH_REQUEST_CODE = 0;
	private static final int CONFIRM_REQUEST_CODE = 1;

	private static final String TAG = "SkinSwitch/Wear";
	private static final long IMAGE_RESPONSE_TIMEOUT = 3000;
	GoogleApiClient mGoogleApiClient;

	private View confirmView;
	private View loadingView;

	private TextView lblLoading;
	private DelayedConfirmationView delayedConfirmationView;
	private TextView lblSkinName;
	private TextView lblSkinName2;
	private ImageView imgSkinHead;

	private Timer imageTimeoutTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				confirmView = stub.findViewById(R.id.view_confirmation);
				loadingView = stub.findViewById(R.id.view_loading);
				lblSkinName = (TextView) stub.findViewById(R.id.lbl_skin_name);
				lblSkinName2 = (TextView) stub.findViewById(R.id.lbl_skin_name_2);
				imgSkinHead = (ImageView) stub.findViewById(R.id.img_skin_head);
				lblLoading = (TextView) stub.findViewById(R.id.lbl_loading);
				delayedConfirmationView = (DelayedConfirmationView) stub.findViewById(R.id.view_confirm_delay);
			}

		});

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
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
			askForSkinHead(spokenText);

			imageTimeoutTimer = new Timer();
			imageTimeoutTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					displayErrorAndFinish(getResources().getString(R.string.error_skin_not_found));
				}

			}, IMAGE_RESPONSE_TIMEOUT);
		} else if(requestCode == SPEECH_REQUEST_CODE) {
			displayErrorAndFinish(getResources().getString(R.string.error_search_cancelled));
		} else if(requestCode == CONFIRM_REQUEST_CODE) {
			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Called when receiving data (ex: the skin).
	 *
	 * @param dataEvents
	 */
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		Log.d(TAG, "received data");
		imageTimeoutTimer.cancel();

		for(DataEvent event : dataEvents) {
			if(event.getType() == DataEvent.TYPE_CHANGED &&
					event.getDataItem().getUri().getPath().equals("/skinHead")) {
				final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				Asset profileAsset = dataMapItem.getDataMap().getAsset("image");
				final Bitmap bitmap = loadBitmapFromAsset(profileAsset);

				MainActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// Do something with the bitmap
						confirmView.setVisibility(View.GONE);

						lblSkinName2.setText(dataMapItem.getDataMap().getString("name"));
						imgSkinHead.setImageBitmap(bitmap);

						Util.crossfade(loadingView, confirmView, getResources().getInteger(android.R.integer
								.config_mediumAnimTime));

						delayedConfirmationView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {

							@Override
							public void onTimerFinished(View view) {
								wearSkinWithId(dataMapItem.getDataMap().getByte("skinId"));
								startActivityForResult(new Intent(MainActivity.this, SendConfirmationActivity.class),
										CONFIRM_REQUEST_CODE);
							}

							@Override
							public void onTimerSelected(View view) {
								delayedConfirmationView.setListener(null);
								finish();
							}

						});

						delayedConfirmationView.setTotalTimeMs(1500);
						delayedConfirmationView.start();

					}

				});
			}
		}

	}

	/**
	 * Called when we're connected to the phone.
	 *
	 * @param connectionHint
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(TAG, "onConnected: " + connectionHint);
		Wearable.DataApi.addListener(mGoogleApiClient, this);
		displaySpeechRecognizer();
	}

	/**
	 * Called when the connection gets interrupted.
	 *
	 * @param cause
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		Log.d(TAG, "onConnectionSuspended: " + cause);
	}

	/**
	 * Create an intent that can start the Speech Recognizer activity.
	 */
	private void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Start the activity, the intent will be populated with the speech text
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}

	private void sendMessageToCompanion(final String path, final byte[] payload) {
		if(mGoogleApiClient.isConnected()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();

					for(Node node : nodes.getNodes()) {
						MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(),
								path, payload).await();

						if(!result.getStatus().isSuccess()) {
							Log.e(TAG, "error");
						} else {
							Log.i(TAG, "success! sent to: " + node.getDisplayName());
						}
					}
				}

			}).start();

		} else {
			Log.e(TAG, "not connected");
		}
	}

	private void askForSkinHead(final String skinName) {
		lblSkinName.setText(skinName);
		sendMessageToCompanion("/getSkin", skinName.getBytes());
	}

	private void wearSkinWithId(byte id) {
		sendMessageToCompanion("/sendSkin", new byte[]{id});
	}

	public Bitmap loadBitmapFromAsset(Asset asset) {
		if(asset == null) {
			throw new IllegalArgumentException("Asset must be non-null");
		}

		// convert asset into a file descriptor and block until it's ready
		InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();

		if(assetInputStream == null) {
			Log.w(TAG, "Requested an unknown Asset.");
			return null;
		}

		// decode the stream into a bitmap
		return BitmapFactory.decodeStream(assetInputStream);
	}

	private void displayErrorAndFinish(final String error) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				lblLoading.setText(error);
				loadingView.setVisibility(View.VISIBLE);
				confirmView.setVisibility(View.GONE);
			}

		});

		(new Timer()).schedule(new TimerTask() {

			@Override
			public void run() {
				finish();
			}

		}, 1500);
	}

}
