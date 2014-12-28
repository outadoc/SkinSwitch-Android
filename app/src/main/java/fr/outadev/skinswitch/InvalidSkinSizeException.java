/*
 * SkinSwitch - InvalidSkinSizeException
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

package fr.outadev.skinswitch;

/**
 * An exception thrown when the skin is not the right size.
 *
 * @author outadoc
 */
public class InvalidSkinSizeException extends Exception {

	private final int width;
	private final int height;

	/**
	 * Creates a new InvalidSkinSizeException.
	 *
	 * @param width  the width of the invalid skin
	 * @param height the height of the invalid skin
	 */
	public InvalidSkinSizeException(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public String getMessage() {
		return "Invalid size for skin, should be 64x32 or 64x64 (is " + width + "x" + height + ")";
	}
}
