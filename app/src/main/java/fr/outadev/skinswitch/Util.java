package fr.outadev.skinswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

/**
 * Created by outadoc on 07/07/14.
 */
public abstract class Util {

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
