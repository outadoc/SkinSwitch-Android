package fr.outadev.skinswitch.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.outadev.skinswitch.activities.SkinLibraryPageFragment;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;

public class SkinLibraryPageAdapter extends FragmentPagerAdapter {

	public static final int INDEX_RANDOM_SKINS = 0;
	public static final int INDEX_LATEST_SKINS = 1;
	public static final int INDEX_SEARCH_SKINS = 2;

	public SkinLibraryPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		SkinLibraryPageFragment frag = new SkinLibraryPageFragment();
		Bundle args = new Bundle();

		switch(index) {
			case INDEX_RANDOM_SKINS:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.RANDOM_SKINS);
				break;
			case INDEX_LATEST_SKINS:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.LATEST_SKINS);
				break;
			case INDEX_SEARCH_SKINS:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.SEARCH_SKINS);
				break;
			default:
				return null;
		}

		frag.setArguments(args);
		return frag;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case INDEX_RANDOM_SKINS:
				return "RANDOM";
			case INDEX_LATEST_SKINS:
				return "LATEST";
			case INDEX_SEARCH_SKINS:
				return "SEARCH";
			default:
				return null;
		}
	}

}
