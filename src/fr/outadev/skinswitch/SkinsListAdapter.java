package fr.outadev.skinswitch;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import fr.outadev.skinswitch.SkinImageUtils.Side;

public class SkinsListAdapter extends ArrayAdapter<Skin> {

	public SkinsListAdapter(Activity activity, int resource, ArrayList<Skin> objects) {
		super(activity, resource, objects);
		this.activity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View cell = inflater.inflate(R.layout.skin_icon, parent, false);

		ImageView skinView = (ImageView) cell.findViewById(R.id.img_skin_preview);

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inScaled = false;

		Bitmap btmp = BitmapFactory.decodeResource(activity.getResources(), R.drawable.test_skin_outadoc, opt);
		skinView.setImageBitmap(SkinImageUtils.getCroppedHead(SkinImageUtils.getSkinPreview(btmp, Side.FRONT, 19)));
		btmp.recycle();

		return cell;
	}

	private final Activity activity;

}
