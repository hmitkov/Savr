package com.proxiad.savr.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.proxiad.savr.server.ServerHelper;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
	ImageView imageView;
	String url;

	public DownloadImageTask(ImageView imageView, String url) {
		this.imageView = imageView;
		this.url = url;
	}

	protected Bitmap doInBackground(Void... params) {
		return ServerHelper.downloadImage(url);
	}

	protected void onPostExecute(Bitmap result) {
		if (result != null) {
			imageView.setImageBitmap(result);
		}
	}
}
