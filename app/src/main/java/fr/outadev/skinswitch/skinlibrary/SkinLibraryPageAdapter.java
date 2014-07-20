package fr.outadev.skinswitch.skinlibrary;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;

/**
 * Page adapter for displaying the different tabs of the skin library.
 *
 * @author outadoc
 */
public class SkinLibraryPageAdapter extends FragmentPagerAdapter {

	public static final int INDEX_RANDOM_SKINS = 0;
	public static final int INDEX_LATEST_SKINS = 1;
	public static final int INDEX_ALL_SKINS = 2;

	private Context context;

	public SkinLibraryPageAdapter(FragmentManager fm, Context context) {
		super(fm);
		this.context = context;
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
			case INDEX_ALL_SKINS:
				args.putSerializable(SkinLibraryPageFragment.ARG_ENDPOINT, EndPoint.ALL_SKINS);
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
				resId = R.string.library_section_random;
				break;
			case INDEX_LATEST_SKINS:
				resId = R.string.library_section_latest;
				break;
			case INDEX_ALL_SKINS:
				resId = R.string.library_section_all;
				break;
			default:
				return null;
		}

		return context.getResources().getString(resId).toUpperCase();
	}

}
