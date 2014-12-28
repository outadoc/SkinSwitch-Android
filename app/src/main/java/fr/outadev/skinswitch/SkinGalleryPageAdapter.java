/*
 * SkinSwitch - SkinGalleryPageAdapter
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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.outadev.skinswitch.network.SkinManagerConnectionHandler.EndPoint;

/**
 * Page adapter for displaying the different tabs of the skin library.
 *
 * @author outadoc
 */
public class SkinGalleryPageAdapter extends FragmentPagerAdapter {

	public static final int INDEX_RANDOM_SKINS = 0;
	public static final int INDEX_LATEST_SKINS = 1;
	public static final int INDEX_ALL_SKINS = 2;

	private Context context;

	public SkinGalleryPageAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int index) {
		SkinGalleryPageFragment frag = new SkinGalleryPageFragment();
		Bundle args = new Bundle();

		switch(index) {
			case INDEX_RANDOM_SKINS:
				args.putSerializable(SkinGalleryPageFragment.ARG_ENDPOINT, EndPoint.RANDOM_SKINS);
				break;
			case INDEX_LATEST_SKINS:
				args.putSerializable(SkinGalleryPageFragment.ARG_ENDPOINT, EndPoint.LATEST_SKINS);
				break;
			case INDEX_ALL_SKINS:
				args.putSerializable(SkinGalleryPageFragment.ARG_ENDPOINT, EndPoint.ALL_SKINS);
				break;
			default:
				return null;
		}

		frag.setArguments(args);
		return frag;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		int resId;

		switch(position) {
			case INDEX_RANDOM_SKINS:
				resId = R.string.gallery_section_random;
				break;
			case INDEX_LATEST_SKINS:
				resId = R.string.gallery_section_latest;
				break;
			case INDEX_ALL_SKINS:
				resId = R.string.gallery_section_all;
				break;
			default:
				return null;
		}

		return context.getResources().getString(resId).toUpperCase();
	}

}
