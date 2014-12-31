/*
 * SkinSwitch - CompanionManager
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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by outadoc on 12/31/14.
 */
public class CompanionManager implements DataApi.DataListener, GoogleApiClient.ConnectionCallbacks {

	private GoogleApiClient googleApiClient;

	private Activity activity;
	private WatchInterface watchInterface;

	public CompanionManager(Activity activity, WatchInterface watchInterface) {
		this.activity = activity;
		this.watchInterface = watchInterface;

		googleApiClient = new GoogleApiClient.Builder(activity)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

					@Override
					public void onConnectionFailed(ConnectionResult result) {
						Log.d(MainActivity.TAG, "onConnectionFailed: " + result);
					}

				})
				.addApi(Wearable.API)
				.build();

		googleApiClient.connect();
	}

	/**
	 * Called when we're connected to the phone.
	 *
	 * @param connectionHint
	 */
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(MainActivity.TAG, "onConnected: " + connectionHint);
		Wearable.DataApi.addListener(googleApiClient, this);
		watchInterface.displaySpeechRecognizer();
	}

	/**
	 * Called when the connection gets interrupted.
	 *
	 * @param cause
	 */
	@Override
	public void onConnectionSuspended(int cause) {
		Log.d(MainActivity.TAG, "onConnectionSuspended: " + cause);
	}

	/**
	 * Called when receiving data (ex: the skin).
	 *
	 * @param dataEvents
	 */
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		Log.d(MainActivity.TAG, "received data");

		for(DataEvent event : dataEvents) {
			if(event.getType() == DataEvent.TYPE_CHANGED &&
					event.getDataItem().getUri().getPath().equals("/skinHead")) {
				final DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				final Bitmap bitmap = Util.loadBitmapFromAsset(dataMapItem.getDataMap().getAsset("image"), googleApiClient);

				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						watchInterface.displayInAppSkinName(dataMapItem.getDataMap().getString("name"));
						watchInterface.displaySkinBitmap(bitmap, dataMapItem.getDataMap().getByte("skinId"));
					}

				});
			}
		}

	}

	public void sendMessageToCompanion(final String path, final byte[] payload) {
		if(googleApiClient.isConnected()) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();

					for(Node node : nodes.getNodes()) {
						MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(googleApiClient, node.getId(),
								path, payload).await();

						if(!result.getStatus().isSuccess()) {
							Log.e(MainActivity.TAG, "error");
						} else {
							Log.i(MainActivity.TAG, "success! sent to: " + node.getDisplayName());
						}
					}
				}

			}).start();

		} else {
			Log.e(MainActivity.TAG, "not connected");
		}
	}

	public void askForSkinHead(String skinName) {
		watchInterface.displayRequestedSkinName(skinName);
		sendMessageToCompanion("/getSkin", skinName.getBytes());
	}

	public void wearSkinWithId(byte id) {
		sendMessageToCompanion("/sendSkin", new byte[]{id});
	}

}
