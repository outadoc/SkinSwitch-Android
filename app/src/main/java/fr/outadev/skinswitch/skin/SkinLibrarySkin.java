package fr.outadev.skinswitch.skin;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

public class SkinLibrarySkin extends BasicSkin {

	private String owner;
	private int skinManagerId;

	public SkinLibrarySkin(int id, String name, String description, String owner) {
		super(id, name, description, null);
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getSkinManagerId() {
		return skinManagerId;
	}

	public void setSkinManagerId(int skinManagerId) {
		this.skinManagerId = skinManagerId;
	}

	@Override
	protected Bitmap readBitmapFromFileSystem(String path, Context context) throws FileNotFoundException {
		Log.d("SkinSwitch", "didn't load cache for " + this + "because it is a " + this.getClass().getName());
		throw new FileNotFoundException();
	}

	@Override
	protected void writeBitmapToFileSystem(Bitmap bitmap, String path) throws IOException {
		Log.d("SkinSwitch", "didn't save cache for " + this + "because it is a " + this.getClass().getName());
		throw new FileNotFoundException();
	}

	@Override
	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler();
		Bitmap bmp = handler.fetchSkinBitmap(skinManagerId);

		if(bmp == null) {
			throw new FileNotFoundException("Couldn't fetch skin for id=" + getId());
		}

		return bmp;
	}

	public BasicSkin toSkin() {
		BasicSkin skin = new BasicSkin(getId(), getName(), getDescription(), getCreationDate());
		skin.setSource(getSource());
		return skin;
	}

}
