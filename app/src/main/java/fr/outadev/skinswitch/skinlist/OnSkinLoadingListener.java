package fr.outadev.skinswitch.skinlist;

/**
 * Defines an activity that has a progess indicator.
 * Created by outadoc on 19/07/14.
 */
public interface OnSkinLoadingListener {

	/**
	 * Displays a loading state.
	 *
	 * @param loading true if loading, false if done.
	 */
	public void setLoading(boolean loading);

}
