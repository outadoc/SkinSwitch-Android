package fr.outadev.skinswitch.skin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class SkinsDatabase {

	private final DatabaseOpenHelper databaseOpenHelper;

	public SkinsDatabase(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context);
	}

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

	public void addSkin(Skin skin) throws SQLException {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put("name", skin.getName());
		values.put("description", skin.getDescription());
		values.put("timestamp", skin.getCreationDate().getTime());

		db.insertOrThrow("skins", null, values);
		db.close();
	}

	public void removeSkin(Skin skin) {
		removeSkin(skin.getId());
	}

	public void removeSkin(int id) {
		SQLiteDatabase db = databaseOpenHelper.getWritableDatabase();
		db.delete("skins", "id = ?", new String[] { Integer.valueOf(id).toString() });
		db.close();
	}
}
