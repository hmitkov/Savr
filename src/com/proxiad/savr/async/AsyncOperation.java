package com.proxiad.savr.async;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;

import com.proxiad.savr.R;
import com.proxiad.savr.common.NetworkUtil;

public abstract class AsyncOperation<A, B, C> {
	Context context;
	AsyncOperationCallback mCallback = null;
	AsyncTask<A, B, C> mTask = null;

	public AsyncOperation(Context context, AsyncOperationCallback callback) {
		super();
		this.context = context;
		mCallback = callback;
	}

	public void doLongOperationWithCheck() {
		if (!checkNetwork()) {
			if (mCallback != null) {
				mCallback.onError("No network");
			}
		} else {
			doLongOperation();
		}
	}

	public void setmCallback(AsyncOperationCallback mCallback) {
		this.mCallback = mCallback;
	}

	public abstract void doLongOperation();

	public interface AsyncOperationCallback {
		public void onPreExecute();

		public void onPostExecute();

		public void onProgress(int progress);

		public void onCancel();

		public void onError(String msg);
	}

	private boolean checkNetwork() {
		if (!NetworkUtil.isNetworkConnectionAvailable(context)) {
			// Network is not present. Open dialog to propose the user to change
			// the network settings.
			openDialog();
			return false;
		}
		return true;
	}

	private void openDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(R.string.msg_no_network);
		dialog.setMessage(R.string.msg_need_network);

		dialog.setPositiveButton(R.string.btn_change_settings, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface di, int i) {
				context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
			}
		});
		dialog.setNegativeButton(R.string.btn_close, null);
		dialog.show();
	}

}
