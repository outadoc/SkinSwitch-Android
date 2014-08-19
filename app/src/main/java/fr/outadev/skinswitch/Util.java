/*
 * SkinSwitch - Util
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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Random useful methods.
 *
 * @author outadoc
 */
public abstract class Util {

	public static final String TAG = "SkinSwitch";

	public static void crossfade(final View oldView, final View newView, int animTime) {

		// Set the new view to 0% opacity but visible, so that it is visible
		// (but fully transparent) during the animation.
		newView.setAlpha(0f);
		newView.setVisibility(View.VISIBLE);

		// Animate the new view to 100% opacity, and clear any animation
		// listener set on the view.
		newView.animate()
				.alpha(1f)
				.setDuration(animTime)
				.setListener(null);

		// Animate the old view to 0% opacity. After the animation ends,
		// set its visibility to GONE as an optimization step (it won't
		// participate in layout passes, etc.)
		oldView.animate()
				.alpha(0f)
				.setDuration(animTime)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						oldView.setVisibility(View.GONE);
					}
				});
	}

}
