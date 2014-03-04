package fr.outadev.skinswitch;

import java.util.ArrayList;

import fr.outadev.skinswitch.SkinImageUtils.Side;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class SkinsListAdapter extends ArrayAdapter<Skin> {

	public SkinsListAdapter(Activity activity, int resource, ArrayList<Skin> objects) {
		super((Context) activity, resource, objects);
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

		return cell;
	}

	private Activity activity;

}
