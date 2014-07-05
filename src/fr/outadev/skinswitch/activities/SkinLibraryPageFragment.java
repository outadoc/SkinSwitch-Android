package fr.outadev.skinswitch.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import fr.outadev.skinswitch.network.skinmanager.SkinManagerConnectionHandler.EndPoint;

public class SkinLibraryPageFragment extends Fragment {
	
    public static final String ARG_ENDPOINT = "EndPoint";
	private EndPoint endPoint;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
        Bundle args = getArguments();
        endPoint = (EndPoint) args.get(ARG_ENDPOINT);

		return null;
	}

}
