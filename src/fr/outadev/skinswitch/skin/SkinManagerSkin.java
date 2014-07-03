package fr.outadev.skinswitch.skin;

import java.io.FileNotFoundException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

public class SkinManagerSkin extends Skin {

	private String owner;

	public SkinManagerSkin(int id, String name, String description, Date creationDate, String owner) {
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

}
