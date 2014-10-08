/*
 * SkinSwitch - CustomSkin
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

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.IOException;
import java.util.Date;

/**
 * Created by outadoc on 08/10/14.
 */
public class CustomSkin extends BasicSkin {

	private String source;

	public CustomSkin(int id, String name, String description, Date creationDate, String source) {
		super(id, name, description, creationDate);
		setSource(source);
	}

	public CustomSkin(String name, String description, Date creationDate, String source) {
		super(name, description, creationDate);
		setSource(source);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public void downloadSkinFromSource(Context context) throws NetworkErrorException, IOException {
		if(getSource() == null) {
			throw new NetworkErrorException("No source was set for " + this);
		}

		byte[] response = HttpRequest.get(getSource()).trustAllHosts().useCaches(true).bytes();
		if(response == null) {
			throw new NetworkErrorException("Couldn't download " + this);
		}

		Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length);

		if(bmp != null) {
			saveRawSkinBitmap(context, bmp);
			bmp.recycle();
		}
	}

	@Override
	public boolean isValidSource(String param) throws InvalidSkinSizeException {

		if(source == null) {
			return false;
		}

		byte[] response = HttpRequest.get(source).trustAllHosts().useCaches(true).bytes();

		if(response != null) {
			Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length);

			if(bmp != null) {
				if(bmp.getWidth() == 64 && (bmp.getHeight() == 32 || bmp.getHeight() == 64)) {
					bmp.recycle();
					return true;
				} else {
					//size is not 64x32 or 64x64, abort, abort!
					throw new InvalidSkinSizeException(bmp.getWidth(), bmp.getHeight());
				}
			}
		}

		return false;
	}
}
