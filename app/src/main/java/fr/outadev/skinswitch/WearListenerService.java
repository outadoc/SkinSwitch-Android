package fr.outadev.skinswitch;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Arrays;

/**
 * Android Wear listener.
 */
public class WearListenerService extends WearableListenerService {

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		super.onMessageReceived(messageEvent);
		Log.d("SkinSwitch/Wear", "message received: " + messageEvent.getPath() + " / " + Arrays.toString(messageEvent.getData
				()));
	}

	@Override
	public void onPeerConnected(Node peer) {
		super.onPeerConnected(peer);
		Log.d("SkinSwitch/Wear", "connected to " + peer.getDisplayName());
	}

	@Override
	public void onPeerDisconnected(Node peer) {
		super.onPeerDisconnected(peer);
		Log.d("SkinSwitch/Wear", "disconnected from " + peer.getDisplayName());
	}
}
