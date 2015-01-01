/*
 * SkinSwitch - MainActivity
 * Copyright (C) 2014-2015  Baptiste Candellier
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

package fr.outadev.skinswitch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.wearable.view.DelayedConfirmationView;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity implements WatchInterface {

	private static final int SPEECH_REQUEST_CODE = 0;
	private static final int CONFIRM_REQUEST_CODE = 1;
	private static final long IMAGE_RESPONSE_TIMEOUT = 3000;

	private View confirmView;
	private View loadingView;

	private TextView lblLoading;
	private DelayedConfirmationView delayedConfirmationView;
	private TextView lblSkinNameRequested;
	private TextView lblSkinNameInApp;
	private ImageView imgSkinHead;

	private Timer imageTimeoutTimer;

	private CompanionManager companionManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Create a connection manager
		companionManager = new CompanionManager(this, this);

		((WatchViewStub) findViewById(R.id.watch_view_stub))
				.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {

					@Override
					public void onLayoutInflated(WatchViewStub stub) {
						confirmView = stub.findViewById(R.id.view_confirmation);
						loadingView = stub.findViewById(R.id.view_loading);
						lblSkinNameRequested = (TextView) stub.findViewById(R.id.lbl_skin_name_requested);
						lblSkinNameInApp = (TextView) stub.findViewById(R.id.lbl_skin_name_inapp);
						imgSkinHead = (ImageView) stub.findViewById(R.id.img_skin_head);
						lblLoading = (TextView) stub.findViewById(R.id.lbl_loading);
						delayedConfirmationView = (DelayedConfirmationView) stub.findViewById(R.id.view_confirm_delay);
					}

				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
			List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);

			imageTimeoutTimer = new Timer();
			imageTimeoutTimer.schedule(new TimerTask() {

				@Override
				public void run() {
					displayErrorAndFinish(getResources().getString(R.string.error_skin_not_found));
				}

			}, IMAGE_RESPONSE_TIMEOUT);

			companionManager.askForSkinHead(spokenText);

		} else if(requestCode == SPEECH_REQUEST_CODE) {
			displayErrorAndFinish(getResources().getString(R.string.error_search_cancelled));
		} else if(requestCode == CONFIRM_REQUEST_CODE) {
			finish();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * Create an intent that can start the Speech Recognizer activity.
	 */
	@Override
	public void displaySpeechRecognizer() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

		// Start the activity, the intent will be populated with the speech text
		startActivityForResult(intent, SPEECH_REQUEST_CODE);
	}

	@Override
	public void displayRequestedSkinName(String name) {
		lblSkinNameRequested.setText(name);
	}

	@Override
	public void displayInAppSkinName(String name) {
		lblSkinNameInApp.setText(name);
	}

	@Override
	public void displaySkinBitmap(Bitmap skin, final byte skinId) {
		imageTimeoutTimer.cancel();

		imgSkinHead.setImageBitmap(skin);
		confirmView.setVisibility(View.GONE);

		Util.crossfade(loadingView, confirmView, getResources().getInteger(android.R.integer.config_mediumAnimTime));

		delayedConfirmationView.setListener(new DelayedConfirmationView.DelayedConfirmationListener() {

			@Override
			public void onTimerFinished(View view) {
				companionManager.wearSkinWithId(skinId);
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

	/**
	 * Displays an error screen, and quits.
	 *
	 * @param error the error string to display
	 */
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
