package fr.outadev.skinswitch;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.List;

import fr.outadev.skinswitch.skin.BasicSkin;
import fr.outadev.skinswitch.skin.SkinsDatabase;

/**
 * Android Wear listener.
 */
public class WearListenerService extends WearableListenerService {

	private static final String TAG = "SkinSwitch/Wear";

	@Override
	public void onMessageReceived(MessageEvent messageEvent) {
		super.onMessageReceived(messageEvent);
		Log.d(TAG, "message received: " + messageEvent.getPath() + " / " + new String(messageEvent.getData()));

		if(messageEvent.getPath().equals("getSkin")) {
			String skinName = new String(messageEvent.getData());
			SkinsDatabase db = new SkinsDatabase(this);
			List<BasicSkin> allSkins = db.getAllSkins();

			BasicSkin foundSkin = null;

			for(BasicSkin skin : allSkins) {
				if(skin.getName().contains(skinName)) {
					foundSkin = skin;
					break;
				}
			}

			if(foundSkin != null) {
				Log.d(TAG, "found a matching skin: " + foundSkin);
			}
		}
	}

	@Override
	public void onPeerConnected(Node peer) {
		super.onPeerConnected(peer);
		Log.d(TAG, "connected to " + peer.getDisplayName());
	}

	@Override
	public void onPeerDisconnected(Node peer) {
		super.onPeerDisconnected(peer);
		Log.d(TAG, "disconnected from " + peer.getDisplayName());
	}
}
