package fr.outadev.skinswitch.skin;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

public class SkinLibrarySkin extends Skin {

	private String owner;

	public SkinLibrarySkin(int id, String name, String description, Date creationDate, String owner) {
		super(id, name, description, creationDate);
		this.owner = owner;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	@Override
	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler();
		Bitmap bmp = handler.fetchSkinBitmap(getId());

		if(bmp == null) {
			throw new FileNotFoundException("Couldn't fetch skin for id=" + getId());
		}

		return bmp;
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

}
