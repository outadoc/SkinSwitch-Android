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

	protected String getRawSkinPath(Context context) {
		return context.getFilesDir() + "/" + "raw_" + id + ".png";
	}

	protected String getSkinHeadPath(Context context) {
		return context.getCacheDir() + "/" + "head_" + id + ".png";
	}

	protected String getFrontSkinPreviewPath(Context context) {
		return context.getCacheDir() + "/" + "preview_front_" + id + ".png";
	}

	protected String getBackSkinPreviewPath(Context context) {
		return context.getCacheDir() + "/" + "preview_back_" + id + ".png";
	}

	private Bitmap getBitmapFromDisk(String path, Context context) throws FileNotFoundException {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inScaled = false;
		
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);

		if(bitmap == null) {
			throw new FileNotFoundException("Could not find skin file for ID #" + id + " (path: " + path + ")");
		}

		return bitmap;
	}

	private void saveBitmapToDisk(Bitmap bitmap, String path) throws IOException {
		FileOutputStream fos = new FileOutputStream(path);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
		fos.close();
	}

	public File getRawSkinFile(Context context) {
		return new File(getRawSkinPath(context));
	}

	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		return getBitmapFromDisk(getRawSkinPath(context), context);
	}

	public void saveRawSkinBitmap(Context context, Bitmap bitmap) throws IOException {
		saveBitmapToDisk(bitmap, getRawSkinPath(context));
	}

	public void saveSkinHeadBitmap(Context context, Bitmap bitmap) throws IOException {
		saveBitmapToDisk(bitmap, getSkinHeadPath(context));
	}

	public void saveFrontSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		saveBitmapToDisk(bitmap, getFrontSkinPreviewPath(context));
	}

	public void saveBackSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		saveBitmapToDisk(bitmap, getBackSkinPreviewPath(context));
	}

	public Bitmap getSkinHeadBitmap(Context context) throws FileNotFoundException {
		try {
			return getBitmapFromDisk(getSkinHeadPath(context), context);
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

	public Bitmap getFrontSkinPreview(Context context) throws FileNotFoundException {
		try {
			return getBitmapFromDisk(getFrontSkinPreviewPath(context), context);
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

	public Bitmap getBackSkinPreview(Context context) throws FileNotFoundException {
		try {
			return getBitmapFromDisk(getBackSkinPreviewPath(context), context);
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

	@Override
    public String toString() {
	    return "Skin [id=" + id + ", name=" + name + ", description=" + description + ", creationDate=" + creationDate + "]";
    }
	
}
