package fr.outadev.skinswitch.skin;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Handles the database creation/update.
 *
 * @author outadoc
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "skinswitch.db";

	private static final String SKINS_TABLE_CREATE = "CREATE TABLE skins (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
			"name VARCHAR(16) NOT NULL, description TEXT NOT NULL, timestamp VARCHAR(16) NOT NULL, source VARCHAR(256))";

	DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SKINS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
}