package fr.outadev.skinswitch;

import android.content.Context;

public class SkinsDatabase {

	public SkinsDatabase(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context);
	}

	DatabaseOpenHelper databaseOpenHelper;
}
