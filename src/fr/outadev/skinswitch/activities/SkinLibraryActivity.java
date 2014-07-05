package fr.outadev.skinswitch.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.adapters.SkinLibraryPageAdapter;

public class SkinLibraryActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_skin_library);

		SkinLibraryPageAdapter adapter = new SkinLibraryPageAdapter(getSupportFragmentManager());
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(adapter);
	}

}
