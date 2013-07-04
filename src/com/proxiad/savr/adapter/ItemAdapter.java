package com.proxiad.savr.adapter;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.app.TabActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.FileUtil;
import com.proxiad.savr.listeners.HeartClickListener;
import com.proxiad.savr.listeners.LinkClickListener;
import com.proxiad.savr.listeners.PhoneNumberClickListener;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;

public class ItemAdapter extends SimpleAdapter {
	String googleAddress;
	public ItemAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {

		super(context, data, resource, from, to);
	}

	@Override
	public void setViewImage(ImageView v, String value) {
		if (value != null && value.trim().startsWith(Constants.FIELD_HEART)) {
			if (value.equals(Constants.FIELD_HEART_PRESSED)) {
				v.setImageResource(R.drawable.heartpressed);
			} else {
				v.setOnClickListener(new HeartClickListener());
			}
		} else if (value != null
				&& value.trim().startsWith(Constants.FIELD_STAR_EMPTY)) {
			v.setImageResource(R.drawable.greystar);

		} else if (value != null
				&& value.trim().startsWith(Constants.FIELD_STAR_MID)) {
			v.setImageResource(R.drawable.midstar);

		} else if (value != null
				&& value.trim().startsWith(Constants.FIELD_STAR_RED)) {
			v.setImageResource(R.drawable.redstar);

		} else if (value != null
				&& value.trim().startsWith(Constants.FIELD_ITEM_PLUS_REMOVE)) {
			v.setVisibility(View.GONE);

		} else if (value != null
				&& value.trim().startsWith(Constants.FIELD_ITEM_PLUS_PRESENT)) {
			v.setImageResource(R.drawable.plus_button);

		} else if (value != null
				&& value.startsWith(Constants.SERVER_GOOGLE_MAP)) {
			System.err.println("MAP KEY PREENTER");
			googleAddress = value;
			super.setViewImage(v, value);
			v.setOnClickListener(new OnClickListener() {           
				
				  @Override
				  public void onClick(View v) 
				  {
					  System.err.println("MAP KEY ENTER");
					  ListActivity listActivity = (ListActivity)v.getContext();
					  HomeActivity tabActivity =  (HomeActivity)listActivity.getParent();
					  tabActivity.setFlagMap(Constants.STRING_MAP_POINTER);
					  tabActivity.getTabHost().setCurrentTab(1);
					  
				      //Toast.makeText(this, "Hello World", Toast.LENGTH_LONG).show();
				  }    
				});
			new DownloadImageTask(v, null).execute(value);

		}else if (value != null && !value.trim().equals("")) {
			if (!value.startsWith(Constants.SERVER_GOOGLE_MAP)) {
				value = Constants.SERVER_HOSTNAME + value;
				Bitmap bmp = BitmapFactory.decodeFile(FileUtil
						.getSaveImageFile(v.getContext(),
								HomeActivity.getSave()).getPath());
				if (bmp == null) {
					new DownloadImageTask(v, HomeActivity.getSave())
							.execute(value);
				} else {
					if (value.startsWith(Constants.FIELD_ITEM_IMAGE)) {
						setImage(v, bmp);
					} else {
						// setImage(v, ActivityHelper.fastblur(bmp,5));
						setImage(v, bmp);
					}
				}
			}/* else {

				super.setViewImage(v, value);
				new DownloadImageTask(v, null).execute(value);
			}*/
			return;
		} else {
			v.setVisibility(View.GONE);
		}
	}

	@Override
	public void setViewText(TextView v, String text) {

		if (text == null || text.trim().equals("")) {
			v.setVisibility(View.GONE);
		} else if (text != null
				&& (text.trim().toLowerCase().startsWith(Constants.FIELD_HTTP) || text
						.trim().toLowerCase().startsWith(Constants.FIELD_WWW))) {
			if (text.trim().toLowerCase().startsWith(Constants.FIELD_WWW)) {
				text = Constants.FIELD_HTTP_LEADER + text;
			}
			v.setOnClickListener(new LinkClickListener());
			super.setViewText(v, text);

		} else if (text!=null && isNumeric(text.replaceAll(" ", "").trim())) {
			v.setOnClickListener(new PhoneNumberClickListener());
			super.setViewText(v, text);
		} else {
			super.setViewText(v, text);
		}
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;
		Save save;

		public DownloadImageTask(ImageView bmImage, Save save) {
			this.bmImage = bmImage;
			this.save = save;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				if (save != null) {
					mIcon11 = ServerHelper.downloadSaveImageAndStore(
							bmImage.getContext(), urldisplay, save);
				} else {
					InputStream in = new java.net.URL(urldisplay).openStream();
					mIcon11 = BitmapFactory.decodeStream(in);
				}
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			setImage(bmImage, result);

		}
	}

	void setImage(ImageView imageView, Bitmap bmp) {
		if (bmp != null) {
			DisplayMetrics metrics = imageView.getContext().getResources()
					.getDisplayMetrics();
			imageView.setImageBitmap(bmp);
			imageView.setMinimumWidth(metrics.widthPixels);
			imageView.setMaxWidth(metrics.widthPixels);
			imageView.setMinimumHeight(ActivityHelper.dpFromPx(60,
					(Activity) imageView.getContext()));
		}
	}
}
