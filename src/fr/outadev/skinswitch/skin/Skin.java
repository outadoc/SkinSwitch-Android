package fr.outadev.skinswitch.skin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import fr.outadev.skinswitch.skin.SkinRenderer.Side;

/**
 * Represents a stored skin, as it is in the database.
 * 
 * @author outadoc
 * 
 */
public class Skin {

	private int id;
	private String name;
	private String description;
	private Date creationDate;

	/**
	 * Creates a new skin.
	 * 
	 * @param id
	 *            the skin ID.
	 * @param name
	 *            the name of the skin.
	 * @param description
	 *            the description of the skin.
	 * @param creationDate
	 *            the date of creation of the skin.
	 */
	public Skin(int id, String name, String description, Date creationDate) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
	}

	/**
	 * Similar to {@link #Skin(int, String, String, Date) Skin(int, String,
	 * String, Date)}, except the ID is automatically set to -1.
	 * 
	 * @param name
	 *            the name of the skin.
	 * @param description
	 *            the description of the skin.
	 * @param creationDate
	 *            the date of creation of the skin.
	 */
	public Skin(String name, String description, Date creationDate) {
		this(-1, name, description, creationDate);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	protected void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * 
	 * Path getters
	 * 
	 */

	/**
	 * Gets the path on the filesystem of the raw skin image (which is sent to
	 * minecraft.net).
	 * 
	 * @param context
	 *            required to get the files directory of the app.
	 * @return the absolute path of the raw skin image (doesn't have to actually
	 *         exist).
	 */
	protected String getRawSkinPath(Context context) {
		return context.getFilesDir() + "/" + "raw_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the head preview image.
	 * 
	 * @param context
	 *            required to get the files directory of the app.
	 * @return the absolute path of the head image (doesn't have to actually
	 *         exist).
	 */
	protected String getSkinHeadPath(Context context) {
		return context.getCacheDir() + "/" + "head_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the front skin preview image.
	 * 
	 * @param context
	 *            required to get the files directory of the app.
	 * @return the absolute path of the front skin image (doesn't have to
	 *         actually exist).
	 */
	protected String getFrontSkinPreviewPath(Context context) {
		return context.getCacheDir() + "/" + "preview_front_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the back skin preview image.
	 * 
	 * @param context
	 *            required to get the files directory of the app.
	 * @return the absolute path of the back skin image (doesn't have to
	 *         actually exist).
	 */
	protected String getBackSkinPreviewPath(Context context) {
		return context.getCacheDir() + "/" + "preview_back_" + id + ".png";
	}

	/**
	 * 
	 * Filesystem read/write methods
	 * 
	 */

	/**
	 * Reads a bitmap from the filesystem at the specified path.
	 * 
	 * @param path
	 *            the path of the bitmap to decode.
	 * @param context
	 * @return the decoded bitmap.
	 * @throws FileNotFoundException
	 *             if no file could be found at that path.
	 */
	private Bitmap readBitmapFromFileSystem(String path, Context context) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inScaled = false;

		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		if(bitmap == null) {
			throw new FileNotFoundException("Could not find skin file for ID #" + id + " (path: " + path + ")");
		}

		return bitmap;
	}

	/**
	 * Writes a bitmap to the filesystem at the specified path.
	 * 
	 * @param bitmap
	 *            the bitmap to write.
	 * @param path
	 *            the path at which the bitmap will be written.
	 * @throws IOException
	 *             if an error occurred.
	 */
	private void writeBitmapToFileSystem(Bitmap bitmap, String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
		fos.close();
	}

	/**
	 * 
	 * Bitmap getters
	 * 
	 */

	/**
	 * Gets the raw skin File object for this skin (can be sent to
	 * minecraft.net).
	 * 
	 * @param context
	 * @return a File object pointing to the skin's PNG file.
	 */
	public File getRawSkinFile(Context context) {
		return new File(getRawSkinPath(context));
	}

	/**
	 * Gets a bitmap of the raw skin image.
	 * 
	 * @param context
	 * @return a bitmap containing the raw skin.
	 * @throws FileNotFoundException
	 *             if the raw skin wasn't set yet.
	 */
	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		return readBitmapFromFileSystem(getRawSkinPath(context), context);
	}

	/**
	 * Gets a bitmap of the skin head image.
	 * 
	 * @param context
	 * @return a bitmap containing the skin head.
	 * @throws FileNotFoundException
	 *             if the raw skin wasn't set yet.
	 */
	public Bitmap getSkinHeadBitmap(Context context) throws FileNotFoundException {
		try {
			return readBitmapFromFileSystem(getSkinHeadPath(context), context);
		} catch(FileNotFoundException e) {
			Log.d("SkinSwitch", "creating head preview and cache for " + this);

			Bitmap bmpPrev = getFrontSkinPreview(context);
			Bitmap bmpHead = SkinRenderer.getCroppedHead(bmpPrev);

			bmpPrev.recycle();

			try {
				saveSkinHeadBitmap(context, bmpHead);
			} catch(IOException e1) {
				e1.printStackTrace();
			}

			return bmpHead;
		}
	}

	/**
	 * Gets a bitmap of the front skin preview image.
	 * 
	 * @param context
	 * @return a bitmap of the front skin preview.
	 * @throws FileNotFoundException
	 *             if the raw skin wasn't set yet.
	 */
	public Bitmap getFrontSkinPreview(Context context) throws FileNotFoundException {
		try {
			return readBitmapFromFileSystem(getFrontSkinPreviewPath(context), context);
		} catch(FileNotFoundException e) {
			Log.d("SkinSwitch", "creating front preview and cache for " + this);

			Bitmap bmpRaw = getRawSkinBitmap(context);
			Bitmap bmpPrev = SkinRenderer.getSkinPreview(bmpRaw, Side.FRONT, 19);

			bmpRaw.recycle();

			try {
				saveFrontSkinPreviewBitmap(context, bmpPrev);
			} catch(IOException e1) {
				e1.printStackTrace();
			}

			return bmpPrev;
		}
	}

	/**
	 * Gets a bitmap of the back skin preview image.
	 * 
	 * @param context
	 * @return a bitmap of the back skin preview.
	 * @throws FileNotFoundException
	 *             if the raw skin wasn't set yet.
	 */
	public Bitmap getBackSkinPreview(Context context) throws FileNotFoundException {
		try {
			return readBitmapFromFileSystem(getBackSkinPreviewPath(context), context);
		} catch(FileNotFoundException e) {
			Log.d("SkinSwitch", "creating back preview and cache for " + this);

			Bitmap bmpRaw = getRawSkinBitmap(context);
			Bitmap bmpPrev = SkinRenderer.getSkinPreview(bmpRaw, Side.BACK, 19);

			bmpRaw.recycle();

			try {
				saveBackSkinPreviewBitmap(context, bmpPrev);
			} catch(IOException e1) {
				e1.printStackTrace();
			}

			return bmpPrev;
		}
	}

	/**
	 * 
	 * Bitmap setters
	 * 
	 */

	/**
	 * Writes a raw skin bitmap to the filesystem.
	 * 
	 * @param context
	 * @param bitmap
	 *            the bitmap to write.
	 * @throws IOException
	 *             if an error occured when writing.
	 */
	public void saveRawSkinBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getRawSkinPath(context));
	}

	/**
	 * Writes a skin head bitmap to the filesystem.
	 * 
	 * @param context
	 * @param bitmap
	 *            the bitmap to write.
	 * @throws IOException
	 *             if an error occured when writing.
	 */
	public void saveSkinHeadBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getSkinHeadPath(context));
	}

	/**
	 * Writes a front skin preview bitmap to the filesystem.
	 * 
	 * @param context
	 * @param bitmap
	 *            the bitmap to write.
	 * @throws IOException
	 *             if an error occured when writing.
	 */
	public void saveFrontSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getFrontSkinPreviewPath(context));
	}

	/**
	 * Writes a back skin preview bitmap to the filesystem.
	 * 
	 * @param context
	 * @param bitmap
	 *            the bitmap to write.
	 * @throws IOException
	 *             if an error occured when writing.
	 */
	public void saveBackSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getBackSkinPreviewPath(context));
	}

	@Override
	public String toString() {
		return "Skin [id=" + id + ", name=" + name + ", description=" + description + ", creationDate=" + creationDate + "]";
	}

}
