package fr.outadev.skinswitch.skin;

import java.util.Date;

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

}
