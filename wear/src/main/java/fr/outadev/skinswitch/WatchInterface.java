/*
 * SkinSwitch - WatchInterface
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

import android.graphics.Bitmap;

/**
 * Created by outadoc on 1/1/15.
 */
public interface WatchInterface {

	public void displaySpeechRecognizer();
	public void displayRequestedSkinName(String name);
	public void displayInAppSkinName(String name);
	public void displaySkinBitmap(Bitmap skin, byte skinId);

}
