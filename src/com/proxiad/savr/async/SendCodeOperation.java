package com.proxiad.savr.async;

import java.util.Set;

import org.json.JSONException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.InitializationActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;
import com.proxiad.savr.persistence.SerializationManager;
import com.proxiad.savr.server.ServerHelper;

public class SendCodeOperation extends AsyncOperation<String, Void, Void> {
	private String messages = "";
	private String code = null;
	Save save;

	public SendCodeOperation(Context context, AsyncOperationCallback callback, String code) {
		super(context, callback);
		this.code = code;
	}

	@Override
	public void doLongOperation() {
		if (mTask == null) {
			mTask = new SendCodeTask();
			mTask.execute(code);
		} else {
			throw new IllegalStateException("Already in progress");
		}
	}

	public class SendCodeTask extends AsyncTask<String, Void, Void> {
		boolean result = false;

		@Override
		protected void onPreExecute() {
			if (mCallback != null) {
				mCallback.onPreExecute();
			}
		};

		@Override
		protected Void doInBackground(String... params) {
			Set<String> savedCodes = SerializationManager.getSavedCodess(context);
			code = params[0];
			if (code != null) {
				savedCodes.add(code);
			}
			String jsonResponse = null;
			for (String code : savedCodes) {
				try {
					jsonResponse = ServerHelper.sendCode(code);
					save = MyJsonParser.saveFromJson(jsonResponse);

					// Some save do not have image. In the json, the property
					// "photo" is empty. In this case, downloadSaveImage will
					// throw an exception, but we will catch it and continue.
					try {
						ServerHelper.downloadSaveImage(context, save);
					} catch (Exception e) {
						Log.e(Constants.TAG_LOG, "No photo");
					}
					PersistenceManager.insertSave(context, save);
				} catch (JSONException e) {
					messages = messages + e.getMessage() + "\n";
				}

				catch (Exception e) {
					String message = context.getString(R.string.error_cannot_validate) + " " + code + ".\n";
					messages = messages + message;
					Log.e(Constants.TAG_LOG + this.getClass(), "doInBackground: " + message);
				}
			}
			SerializationManager.deleteCodes(context);

			return null;
		}

		@Override
		protected void onPostExecute(Void v) {
			if (mCallback != null) {
				if (messages.length() > 1) {
					mCallback.onError(messages);
				} else {
					mCallback.onPostExecute();
					InitializationActivity initializationActivity = ((InitializationActivity) context);
					HomeActivity homeActivity = (HomeActivity) initializationActivity.getParent();
					homeActivity.setSave(save);
					homeActivity.refresh();
				}
			}
		}

		@Override
		protected void onCancelled() {

		}
	}
}
