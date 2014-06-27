package fr.outadev.skinswitch;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import fr.outadev.skinswitch.skin.Skin;
import fr.outadev.skinswitch.skin.SkinRenderer;
import fr.outadev.skinswitch.skin.SkinRenderer.Side;

public class SkinsListAdapter extends ArrayAdapter<Skin> {

	public SkinsListAdapter(Context context, int resource, List<Skin> array) {
		super(context, resource, array);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if(convertView == null) {
			convertView =  inflater.inflate(R.layout.skin_icon, parent, false);
		}

		ImageView skinView = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		TextView skinTitle = (TextView) convertView.findViewById(R.id.lbl_skin_title);

		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inScaled = false;

		skinTitle.setText(getItem(position).getName());

		Bitmap btmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.test_skin_outadoc, opt);
		skinView.setImageBitmap(SkinRenderer.getCroppedHead(SkinRenderer.getSkinPreview(btmp, Side.FRONT, 19)));
		btmp.recycle();

		return convertView;
	}

}
