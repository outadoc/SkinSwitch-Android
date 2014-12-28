/*
 * SkinSwitch - MainActivity
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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import fr.outadev.skinswitch.network.UsersManager;

/**
 * Main SkinSwitch activity. Displays the main skins list in a fragment.
 *
 * @author outadoc
 */
public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if(savedInstanceState == null || !savedInstanceState.getBoolean("hasShownWindow", false)) {
			UsersManager usersManager = new UsersManager(this);

			if(!usersManager.isLoggedInSuccessfully()) {
				Intent intent = new Intent(this, MojangLoginActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("hasShownWindow", true);
	}

}
