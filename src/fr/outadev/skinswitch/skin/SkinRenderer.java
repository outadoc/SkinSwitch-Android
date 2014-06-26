package fr.outadev.skinswitch.skin;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;

public class SkinRenderer {

	public static Bitmap getCroppedHead(Bitmap skin) {
		return Bitmap.createBitmap(skin, (int) (skin.getWidth() / 4), 0, (int) (skin.getWidth() / 2),
		        (int) (skin.getWidth() / 2 - 1));
	}

	public static Bitmap getSkinPreview(Bitmap skin) {
		return getSkinPreview(skin, Side.FRONT);
	}

	public static Bitmap getSkinPreview(Bitmap skin, Side side) {
		return getSkinPreview(skin, side, 6);
	}

	public static Bitmap getSkinPreview(Bitmap skin, Side side, int zoom) {
		Bitmap head, chest, arm_right, arm_left, leg_right, leg_left, armor_head, armor_chest, armor_arm_right, armor_arm_left, armor_leg_right, armor_leg_left;

		if(side == null || side == Side.FRONT) {
			// if we want a preview of the front of the skin or if nothing is
			// specified
			// get body parts, one at a time
			head = Bitmap.createBitmap(skin, 8, 8, 8, 8);

			chest = Bitmap.createBitmap(skin, 20, 20, 8, 12);

			// if there's a specific skin for left arm, use it. else, flip the
			// right arm's skin and use it instead.
			arm_right = Bitmap.createBitmap(skin, 44, 20, 4, 12);

			arm_left = (!isNewSkinFormat(skin) || areAllPixelsOfSameColor(Bitmap.createBitmap(skin, 36, 52, 4, 12))) ? flipImage(arm_right)
			        : Bitmap.createBitmap(skin, 36, 52, 4, 12);

			// if there's a specific skin for left leg, use it. else, flip the
			// right leg's skin and use it instead.
			leg_right = Bitmap.createBitmap(skin, 4, 20, 4, 12);
			leg_left = (!isNewSkinFormat(skin) || areAllPixelsOfSameColor(Bitmap.createBitmap(skin, 20, 52, 4, 12))) ? flipImage(leg_right)
			        : Bitmap.createBitmap(skin, 20, 52, 4, 12);

			// it's armor time!
			armor_head = Bitmap.createBitmap(skin, 40, 8, 8, 8);

			if(isNewSkinFormat(skin)) {
				armor_chest = Bitmap.createBitmap(skin, 20, 36, 8, 12);

				armor_arm_right = Bitmap.createBitmap(skin, 44, 36, 4, 12);
				armor_arm_left = Bitmap.createBitmap(skin, 52, 52, 4, 12);

				armor_leg_right = Bitmap.createBitmap(skin, 4, 36, 4, 12);
				armor_leg_left = Bitmap.createBitmap(skin, 4, 52, 4, 12);
			} else {
				armor_chest = armor_arm_right = armor_arm_left = armor_leg_right = armor_leg_left = Bitmap.createBitmap(1, 1,
				        skin.getConfig());
			}
		} else {
			// if we want a preview of the back of the skin
			head = Bitmap.createBitmap(skin, 24, 8, 8, 8);

			chest = Bitmap.createBitmap(skin, 32, 20, 8, 12);

			arm_right = Bitmap.createBitmap(skin, 52, 20, 4, 12);
			arm_left = (!isNewSkinFormat(skin) || areAllPixelsOfSameColor(Bitmap.createBitmap(skin, 44, 52, 4, 12))) ? flipImage(arm_right)
			        : Bitmap.createBitmap(skin, 44, 52, 4, 12);

			leg_right = Bitmap.createBitmap(skin, 12, 20, 4, 12);
			leg_left = (!isNewSkinFormat(skin) || areAllPixelsOfSameColor(Bitmap.createBitmap(skin, 28, 52, 4, 12))) ? flipImage(leg_right)
			        : Bitmap.createBitmap(skin, 28, 52, 4, 12);

			// it's armor time!
			armor_head = Bitmap.createBitmap(skin, 56, 8, 8, 8);

			if(isNewSkinFormat(skin)) {
				armor_chest = Bitmap.createBitmap(skin, 32, 36, 8, 12);

				armor_arm_right = Bitmap.createBitmap(skin, 52, 36, 4, 12);
				armor_arm_left = Bitmap.createBitmap(skin, 60, 52, 4, 12);

				armor_leg_right = Bitmap.createBitmap(skin, 12, 36, 4, 12);
				armor_leg_left = Bitmap.createBitmap(skin, 12, 52, 4, 12);
			} else {
				armor_chest = armor_arm_right = armor_arm_left = armor_leg_right = armor_leg_left = Bitmap.createBitmap(1, 1,
				        skin.getConfig());
			}
		}

		Bitmap dest = Bitmap.createBitmap(16, 40, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(dest);

		// at this point we most likely saturated the memory anyway, so.
		// MOAR. BITMAPS.
		Bitmap final_head = overlayArmor(head, armor_head);
		Bitmap final_chest = overlayArmor(chest, armor_chest);
		Bitmap final_arm_left = overlayArmor(arm_left, armor_arm_left);
		Bitmap final_arm_right = overlayArmor(arm_right, armor_arm_right);
		Bitmap final_leg_left = overlayArmor(leg_left, armor_leg_left);
		Bitmap final_leg_right = overlayArmor(leg_right, armor_leg_right);

		// we got everything, just stick the parts where they belong on the
		// preview
		canvas.drawBitmap(final_head, getSrcRect(final_head), getDestRect(final_head, 4, 0), null);
		canvas.drawBitmap(final_chest, getSrcRect(final_chest), getDestRect(final_chest, 4, 8), null);
		canvas.drawBitmap(final_arm_left, getSrcRect(final_arm_left), getDestRect(final_arm_left, 0, 8), null);
		canvas.drawBitmap(final_arm_right, getSrcRect(final_arm_right), getDestRect(final_arm_right, 12, 8), null);
		canvas.drawBitmap(final_leg_left, getSrcRect(final_leg_left), getDestRect(final_leg_left, 4, 20), null);
		canvas.drawBitmap(final_leg_right, getSrcRect(final_leg_right), getDestRect(final_leg_right, 8, 20), null);

		return resizeImage(dest, zoom);
	}

	private static boolean isNewSkinFormat(Bitmap skin) {
		return(skin.getHeight() == skin.getWidth() && skin.getWidth() == 64);
	}

	private static Rect getSrcRect(Bitmap img) {
		return new Rect(0, 0, img.getWidth(), img.getHeight());
	}

	private static Rect getDestRect(Bitmap img, int x, int y) {
		return new Rect(x, y, x + img.getWidth(), y + img.getHeight());
	}

	private static boolean areAllPixelsOfSameColor(Bitmap image) {
		// remember the color of the first pixel
		int firstPixColor = image.getPixel(0, 0);

		for(int i = 0; i < image.getHeight(); i++) {
			for(int j = 0; j < image.getWidth(); j++) {
				if(image.getPixel(j, i) != firstPixColor) return false;
			}
		}

		// if all pixels are the same color, this should be true
		return true;
	}

	private static Bitmap overlayArmor(Bitmap head, Bitmap armor) {
		Bitmap headCopy = head.copy(head.getConfig(), true);

		if(!areAllPixelsOfSameColor(armor)) {
			for(int i = 0; i < head.getHeight(); i++) {
				for(int j = 0; j < head.getWidth(); j++) {
					if(Color.alpha(armor.getPixel(j, i)) == 255) {
						headCopy.setPixel(j, i, armor.getPixel(j, i));
					}
				}
			}
		}

		return headCopy;
	}

	private static Bitmap flipImage(Bitmap image) {
		Matrix matrix = new Matrix();
		matrix.preScale(-1.0f, 1.0f);
		return Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
	}

	private static Bitmap resizeImage(Bitmap image, int zoom) {
		return Bitmap.createScaledBitmap(image, image.getWidth() * zoom, image.getHeight() * zoom, false);
	}

	public enum Side {
		FRONT, BACK
	}

}
