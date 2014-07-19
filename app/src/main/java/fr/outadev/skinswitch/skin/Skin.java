package fr.outadev.skinswitch.skin;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.activities.ILoadingActivity;
import fr.outadev.skinswitch.activities.MojangLoginActivity;
import fr.outadev.skinswitch.network.MojangConnectionHandler;
import fr.outadev.skinswitch.network.login.ChallengeRequirementException;
import fr.outadev.skinswitch.network.login.InvalidMojangCredentialsException;
import fr.outadev.skinswitch.skin.SkinRenderer.Side;
import fr.outadev.skinswitch.user.UsersManager;

/**
 * Represents a stored skin, as it is in the database.
 *
 * @author outadoc
 */
public class Skin implements Serializable {

	private int id;
	private String name;
	private String description;
	private Date creationDate;
	private String source;

	/**
	 * Creates a new skin.
	 *
	 * @param id           the skin ID.
	 * @param name         the name of the skin.
	 * @param description  the description of the skin.
	 * @param creationDate the date of creation of the skin.
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
	 * @param name         the name of the skin.
	 * @param description  the description of the skin.
	 * @param creationDate the date of creation of the skin.
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

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
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
	 * @param context required to get the files directory of the app.
	 * @return the absolute path of the raw skin image (doesn't have to actually
	 * exist).
	 */
	protected String getRawSkinPath(Context context) {
		return context.getFilesDir() + "/" + "raw_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the head preview image.
	 *
	 * @param context required to get the files directory of the app.
	 * @return the absolute path of the head image (doesn't have to actually
	 * exist).
	 */
	protected String getSkinHeadPath(Context context) {
		return context.getCacheDir() + "/" + "head_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the front skin preview image.
	 *
	 * @param context required to get the files directory of the app.
	 * @return the absolute path of the front skin image (doesn't have to
	 * actually exist).
	 */
	protected String getFrontSkinPreviewPath(Context context) {
		return context.getCacheDir() + "/" + "preview_front_" + id + ".png";
	}

	/**
	 * Gets the path on the filesystem of the back skin preview image.
	 *
	 * @param context required to get the files directory of the app.
	 * @return the absolute path of the back skin image (doesn't have to
	 * actually exist).
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
	 * @param path    the path of the bitmap to decode.
	 * @param context
	 * @return the decoded bitmap.
	 * @throws FileNotFoundException if no file could be found at that path.
	 */
	protected Bitmap readBitmapFromFileSystem(String path, Context context) throws FileNotFoundException {
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
	 * @param bitmap the bitmap to write.
	 * @param path   the path at which the bitmap will be written.
	 * @throws IOException if an error occurred.
	 */
	protected void writeBitmapToFileSystem(Bitmap bitmap, String path) throws IOException {
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
	 * @throws FileNotFoundException if the raw skin wasn't set yet.
	 */
	public Bitmap getRawSkinBitmap(Context context) throws FileNotFoundException {
		return readBitmapFromFileSystem(getRawSkinPath(context), context);
	}

	/**
	 * Gets a bitmap of the skin head image.
	 *
	 * @param context
	 * @return a bitmap containing the skin head.
	 * @throws FileNotFoundException if the raw skin wasn't set yet.
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
			}

			return bmpHead;
		}
	}

	/**
	 * Gets a bitmap of the front skin preview image.
	 *
	 * @param context
	 * @return a bitmap of the front skin preview.
	 * @throws FileNotFoundException if the raw skin wasn't set yet.
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
			}

			return bmpPrev;
		}
	}

	/**
	 * Gets a bitmap of the back skin preview image.
	 *
	 * @param context
	 * @return a bitmap of the back skin preview.
	 * @throws FileNotFoundException if the raw skin wasn't set yet.
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
	 * @param bitmap  the bitmap to write.
	 * @throws IOException if an error occured when writing.
	 */
	public void saveRawSkinBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getRawSkinPath(context));
	}

	/**
	 * Writes a skin head bitmap to the filesystem.
	 *
	 * @param context
	 * @param bitmap  the bitmap to write.
	 * @throws IOException if an error occured when writing.
	 */
	public void saveSkinHeadBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getSkinHeadPath(context));
	}

	/**
	 * Writes a front skin preview bitmap to the filesystem.
	 *
	 * @param context
	 * @param bitmap  the bitmap to write.
	 * @throws IOException if an error occured when writing.
	 */
	public void saveFrontSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getFrontSkinPreviewPath(context));
	}

	/**
	 * Writes a back skin preview bitmap to the filesystem.
	 *
	 * @param context
	 * @param bitmap  the bitmap to write.
	 * @throws IOException if an error occured when writing.
	 */
	public void saveBackSkinPreviewBitmap(Context context, Bitmap bitmap) throws IOException {
		writeBitmapToFileSystem(bitmap, getBackSkinPreviewPath(context));
	}

	/**
	 * Downloads and stores this skin's raw skin on the filesystem.
	 * Source must be specified via {@link #setSource(String).
	 *
	 * @param context
	 * @throws NetworkErrorException if the skin couldn't be downloaded.
	 * @throws IOException           if the skin couldn't be saved.
	 */
	public void downloadSkinFromSource(Context context) throws NetworkErrorException, IOException {
		if(getSource() == null) {
			throw new NetworkErrorException("No source was set for " + this);
		}

		byte[] response = HttpRequest.get(getSource()).trustAllHosts().useCaches(true).bytes();
		if(response == null) {
			throw new NetworkErrorException("Couldn't download " + this);
		}

		Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length);

		if(bmp != null) {
			saveRawSkinBitmap(context, bmp);
			bmp.recycle();
		}
	}

	/**
	 * Checks if the skin's source is a valid skin.
	 *
	 * @return true if it's valid, false if it's not
	 */
	public boolean isValidSource() {
		if(source == null) {
			return false;
		}

		byte[] response = HttpRequest.get(source).trustAllHosts().useCaches(true).bytes();

		if(response != null) {
			Bitmap bmp = BitmapFactory.decodeByteArray(response, 0, response.length);

			if(bmp != null) {
				bmp.recycle();
				return true;
			}
		}

		return false;
	}

	private boolean deleteFile(String path) {
		File file = new File(path);
		return file.delete();
	}

	public void deleteAllSkinResFromFilesystem(Context context) {
		deleteFile(getSkinHeadPath(context));
		deleteFile(getBackSkinPreviewPath(context));
		deleteFile(getFrontSkinPreviewPath(context));
		deleteFile(getRawSkinPath(context));

		Log.i("SkinSwitch", "deleted all local res files for " + this);
	}

	public void initSkinUpload(final Context activity) {
		UsersManager usersManager = new UsersManager(activity);

		//if the user isn't logged in, pop up the login window
		if(!usersManager.isLoggedInSuccessfully()) {
			Intent intent = new Intent(activity, MojangLoginActivity.class);
			activity.startActivity(intent);
			return;
		}

		//else, ask for a confirmation
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Wear " + getName() + "?").setMessage("Do you really want to replace your current " +
				"Minecraft skin with " + getName() + "?");

		builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {
				(new AsyncTask<Void, Void, Exception>() {

					@Override
					protected void onPreExecute() {
						((ILoadingActivity) activity).setLoading(true);
					}

					@Override
					protected Exception doInBackground(Void... voids) {
						MojangConnectionHandler handler = new MojangConnectionHandler();
						UsersManager um = new UsersManager(activity);

						try {
							handler.loginWithCredentials(um.getUser());
							handler.uploadSkinToMojang(getRawSkinFile(activity));
						} catch(Exception e) {
							return e;
						}

						return null;
					}

					@Override
					protected void onPostExecute(Exception e) {
						if(e != null) {
							//display the error if any
							if(e.getMessage() != null && !e.getMessage().isEmpty()) {
								Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
							}

							//if the user needs to fill in a challenge
							if(e instanceof ChallengeRequirementException) {
								Intent intent = new Intent(activity, MojangLoginActivity.class);
								intent.putExtra("step", MojangLoginActivity.Step.CHALLENGE);
								activity.startActivity(intent);
							} else if(e instanceof InvalidMojangCredentialsException) {
								//if the user needs to relog in
								Intent intent = new Intent(activity, MojangLoginActivity.class);
								activity.startActivity(intent);
							}

						} else {
							Toast.makeText(activity, "Skin uploaded successfully!",
									Toast.LENGTH_SHORT).show();
						}

						((ILoadingActivity) activity).setLoading(false);
					}

				}).execute();
			}

		});

		builder.setNegativeButton(R.string.no, null);
		builder.create().show();
	}

	@Override
	public String toString() {
		String str = "Skin [id=" + id + ", name=" + name + ", description=" + description + ", creationDate=" + creationDate
				+ "]";

		if(source != null) {
			str += ", source=" + source + "]";
		} else {
			str += "]";
		}

		return str;
	}

}
