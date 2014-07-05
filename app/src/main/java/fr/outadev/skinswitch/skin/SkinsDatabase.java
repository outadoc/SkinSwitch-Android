package fr.outadev.skinswitch.skin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Manages the skins in the database.
 * 
 * @author outadoc
 * 
 */
public class SkinsDatabase {

	private final DatabaseOpenHelper databaseOpenHelper;

	public SkinsDatabase(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context);
	}

	/**
	 * Gets a skin with the specified id.
	 * 
	 * @param id
	 *            the id of the skin.
	 * @return the skin, if it exists; else, null.
	 */
	public Skin getSkin(int id) {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		Cursor cur = db.query("skins", new String[] { "name", "description", "timestamp" }, "id = ?", new String[] { Integer
		        .valueOf(id).toString() }, null, null, "name");

		if(cur.moveToFirst()) {
			Skin skin = new Skin(id, cur.getString(0), cur.getString(1), new Date(cur.getLong(2)));
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
	public List<Skin> getAllSkins() {
		SQLiteDatabase db = databaseOpenHelper.getReadableDatabase();
		Cursor cur = db.query("skins", new String[] { "id", "name", "description", "timestamp" }, null, null, null, null, "name");

		List<Skin> skins = new ArrayList<Skin>();

		while(cur.moveToNext()) {
			skins.add(new Skin(cur.getInt(0), cur.getString(1), cur.getString(2), new Date(cur.getLong(3))));
		}

		cur.close();
		db.close();
		return skins;
	}

	/**
	 * Adds a skin to the database.
	 * 
	 * @param skin
	 *            the skin to add.
	 * @throws SQLException
	 *             if it couldn't be added.
	 */
	public void addSkin(Skin skin) throws SQLException {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("name", skin.getName());
		values.put("description", skin.getDescription());
		values.put("timestamp", skin.getCreationDate().getTime());

		db.insertOrThrow("skins", null, values);
		db.close();
		
		skin.setId(getLastInsertedId());
	}

	/**
	 * Removes a skin from the database.
	 * 
	 * @param skin
	 *            the skin to remove.
	 */
	public void removeSkin(Skin skin) {
		removeSkin(skin.getId());
	}

	/**
	 * Removes a skin from the database.
	 * 
	 * @param id
	 *            the ID of the skin to remove.
	 */
	public void removeSkin(int id) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		db.delete("skins", "id = ?", new String[] { Integer.valueOf(id).toString() });
		db.close();
	}
	
	/**
	 * Returns the identifier of the last inserted skin.
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
