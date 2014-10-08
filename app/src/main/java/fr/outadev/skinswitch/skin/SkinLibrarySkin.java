/*
 * SkinSwitch - SkinLibrarySkin
 * Copyright (C) 2014-2014  Baptiste Candellier
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

package fr.outadev.skinswitch.skin;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

import fr.outadev.skinswitch.Utils;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler;

public class SkinLibrarySkin extends CustomUriSkin {

	private String owner;
	private int skinManagerId;

	public SkinLibrarySkin(int id, String name, String description, String owner) {
		super(id, name, description, null, SkinManagerConnectionHandler.BASE_URL + "?method=getSkin&id=" + id);
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
		Log.d(Utils.TAG, "didn't load cache for " + this + "because it is a " + this.getClass().getName());
		throw new FileNotFoundException();
	}

	@Override
	protected void writeBitmapToFileSystem(Bitmap bitmap, String path) throws IOException {
		Log.d(Utils.TAG, "didn't save cache for " + this + "because it is a " + this.getClass().getName());
		throw new FileNotFoundException();
	}

	@Override
	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		SkinManagerConnectionHandler handler = new SkinManagerConnectionHandler(context);
		Bitmap bmp = handler.fetchSkinBitmap(skinManagerId);

		if(bmp == null) {
			throw new FileNotFoundException("Couldn't fetch skin for id=" + getId());
		}

		return bmp;
	}

	public CustomUriSkin toDownloadableSkin() {
		return new CustomUriSkin(getId(), getName(), getDescription(), getCreationDate(), getSource());
	}

}
