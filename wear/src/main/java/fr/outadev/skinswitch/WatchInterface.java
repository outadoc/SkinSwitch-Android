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
 * Used to control the interface of the app, and display certain elements after events.
 */
public interface WatchInterface {

	/**
	 * Displays the Google voice entry activity.
	 */
	public void displaySpeechRecognizer();

	/**
	 * Displays the name of the skin the user spelled.
	 *
	 * @param name the name that comes from the speech recogniser
	 */
	public void displayRequestedSkinName(String name);

	/**
	 * Displays the name of the skin that will be uploaded to Minecraft.net.
	 *
	 * @param name the name of the final skin
	 */
	public void displayInAppSkinName(String name);

	/**
	 * Displays a bitmap representing the skin on the screen.
	 *
	 * @param skin   the bitmap of the head of the skin
	 * @param skinId the id of the skin
	 */
	public void displaySkinBitmap(Bitmap skin, byte skinId);

}
