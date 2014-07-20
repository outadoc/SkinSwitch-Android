package fr.outadev.skinswitch.skinlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import fr.outadev.skinswitch.R;
import fr.outadev.skinswitch.skin.BasicSkin;

/**
 * ArrayAdapter for displaying the main skins list.
 *
 * @author outadoc
 */
public class SkinsListAdapter extends ArrayAdapter<BasicSkin> {

	private SkinsListFragment frag;

	public SkinsListAdapter(Context context, SkinsListFragment frag, int resource, List<BasicSkin> array) {
		super(context, resource, array);
		this.frag = frag;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.skin_icon, parent, false);
		}

		final ImageView skinView = (ImageView) convertView.findViewById(R.id.img_skin_preview);
		final TextView skinTitle = (TextView) convertView.findViewById(R.id.lbl_skin_title);

		final BasicSkin skin = getItem(position);

		skinTitle.setText(skin.getName());

		(new AsyncTask<Void, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				try {
					return skin.getSkinHeadBitmap(getContext());
				} catch(FileNotFoundException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if(result != null) {
					skinView.setImageBitmap(result);
				} else {
					skinView.setImageResource(R.drawable.broken_image);
				}
			}

		}).execute();

		convertView.setOnTouchListener(new View.OnTouchListener() {

			private long touchTimestamp;
			private Timer timer;

			private Animation expandAnim;

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					onTouchStart(skinView);
				} else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
					onTouchEnd(skinView);
				}

				return true;
			}

			private void onTouchStart(final View view) {
				//touching the skin head
				touchTimestamp = (new Date()).getTime();
				timer = new Timer();

				timer.schedule(new TimerTask() {

					public void run() {
						frag.getActivity().runOnUiThread(new Runnable() {

							@Override
							public void run() {
								skin.initSkinUpload(getContext());
							}

						});
					}

				}, 1000);

				expandAnim = AnimationUtils.loadAnimation(getContext(), R.anim.anim_skin_rotation);
				view.startAnimation(expandAnim);
			}

			private void onTouchEnd(View view) {
				//releasing the skin head
				if((new Date()).getTime() - touchTimestamp < 300) {
					view.clearAnimation();

					Intent intent = new Intent(getContext(), DetailActivity.class);
					intent.putExtra("skin", skin);
					getContext().startActivity(intent);
				}

				if((new Date()).getTime() - touchTimestamp < 1000) {
					view.clearAnimation();
				}

				timer.cancel();
				touchTimestamp = 0;
			}

		});

		return convertView;
	}

}
