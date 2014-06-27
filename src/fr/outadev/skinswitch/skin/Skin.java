package fr.outadev.skinswitch.skin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getRawSkinFileName(Context context) {
		return "raw_" + id + ".png";
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

	private void saveBitmapToDisk(Bitmap bitmap, String filename, Context context) throws IOException {
		FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
		fos.close();
		System.out.println(filename);
	}

	public File getRawSkinFile(Context context) {
		return new File(context.getFilesDir() + "/" + getRawSkinFileName(context));
	}

	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		return getBitmapFromDisk(context.getFilesDir() + "/" + getRawSkinFileName(context), context);
	}

	public void saveRawSkinBitmap(Context context, Bitmap bitmap) throws IOException {
		saveBitmapToDisk(bitmap, getRawSkinFileName(context), context);
	}
}
