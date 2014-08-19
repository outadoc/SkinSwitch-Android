/*
 * SkinSwitch - SkinsDatabase
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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Manages the skins in the database.
 *
 * @author outadoc
 */
public class SkinsDatabase {

	private final DatabaseOpenHelper databaseOpenHelper;

	public SkinsDatabase(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context);
	}

	/**
	 * Gets a skin with the specified id.
	 *
	 * @param id the id of the skin.
	 * @return the skin, if it exists; else, null.
	 */
	public BasicSkin getSkin(int id) {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		Cursor cur = db.query("skins", new String[]{"name", "description", "timestamp", "source"}, "id = ?", new String[]{Integer
				.valueOf(id).toString()}, null, null, "name");

		if(cur.moveToFirst()) {
			BasicSkin skin = new BasicSkin(id, cur.getString(0), cur.getString(1), new Date(cur.getLong(2)));
			skin.setSource(cur.getString(3));
			cur.close();
			db.close();
			return skin;
		} else {
			db.close();
			return null;
		}
	}

	/**
	 * Gets all the skins in the database.
	 *
	 * @return a List of skins contained in the database.
	 */
	public List<BasicSkin> getAllSkins() {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		Cursor cur = db.query("skins", new String[]{"id", "name", "description", "timestamp", "source"}, null, null, null, null,
				"UPPER(name)");

		List<BasicSkin> skins = new ArrayList<BasicSkin>();

		while(cur.moveToNext()) {
			BasicSkin tmp = new BasicSkin(cur.getInt(0), cur.getString(1), cur.getString(2), new Date(cur.getLong(3)));
			tmp.setSource(cur.getString(4));
			skins.add(tmp);
		}

		cur.close();
		db.close();
		return skins;
	}

	/**
	 * Adds a skin to the database.
	 *
	 * @param skin the skin to add.
	 * @throws SQLException if it couldn't be added.
	 */
	public void addSkin(BasicSkin skin) throws SQLException {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("name", skin.getName());
		values.put("description", skin.getDescription());
		values.put("timestamp", skin.getCreationDate().getTime());
		values.put("source", skin.getSource());

		db.insertOrThrow("skins", null, values);
		db.close();

		skin.setId(getLastInsertedId());
	}

	/**
	 * Removes a skin from the database.
	 *
	 * @param skin the skin to remove.
	 */
	public void removeSkin(BasicSkin skin) {
		removeSkin(skin.getId());
	}

	/**
	 * Removes a skin from the database.
	 *
	 * @param id the ID of the skin to remove.
	 */
	public void removeSkin(int id) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		db.delete("skins", "id = ?", new String[]{Integer.valueOf(id).toString()});
		db.close();
	}

	/**
	 * Updates a skin in the database.
	 *
	 * @param skin the skin to edit, with its updated information.
	 */
	public void updateSkin(BasicSkin skin) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put("name", skin.getName());
		cv.put("description", skin.getDescription());

		db.update("skins", cv, "id = ?", new String[]{Integer.valueOf(skin.getId()).toString()});
		db.close();
	}

	/**
	 * Returns the identifier of the last inserted skin.
	 *
	 * @return -1 if there are no skins in the database, or the last inserted id.
	 */
	public int getLastInsertedId() {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		Cursor cur = db.rawQuery("SELECT id FROM skins ORDER BY id DESC LIMIT 1", null);

		int id = -1;

		//if there are entries in the database
		if(cur.moveToFirst()) {
			id = cur.getInt(0);
		}

		cur.close();
		db.close();

		return id;
	}
}
