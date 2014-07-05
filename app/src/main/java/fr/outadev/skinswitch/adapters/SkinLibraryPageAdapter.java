package fr.outadev.skinswitch.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.outadev.skinswitch.activities.SkinLibraryPageFragment;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;

public class SkinLibraryPageAdapter extends FragmentPagerAdapter {

	public SkinLibraryPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int index) {
		SkinLibraryPageFragment frag = new SkinLibraryPageFragment();
		Bundle args = new Bundle();

		switch(index) {
			case 0:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.LATEST_SKINS);
				break;
			case 1:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.RANDOM_SKINS);
				break;
			case 2:
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
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch(position) {
			case 0:
				return "LATEST";
			case 1:
				return "RANDOM";
			case 2:
				return "SEARCH";
			default:
				return null;
		}
	}

}
