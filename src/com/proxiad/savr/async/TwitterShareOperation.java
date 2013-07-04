package com.proxiad.savr.async;

import twitter4j.Twitter;
import android.content.Context;
import android.os.AsyncTask;

import com.proxiad.savr.R;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.model.Save;

public class TwitterShareOperation extends AsyncOperation<Void, Void, Void> {
	Twitter configuredTwitter;
	Save save;
	String messages;

	public TwitterShareOperation(Context context, AsyncOperationCallback callback, Twitter twitter, Save save) {
		super(context, callback);
		configuredTwitter = twitter;
		this.save = save;
	}

	@Override
	public void doLongOperation() {
		if (mTask == null) {
			mTask = new TwitterLoginTask();
			mTask.execute();
		} else {
			throw new IllegalStateException("Already in progress");
		}
	}

	private class TwitterLoginTask extends AsyncTask<Void, Void, Void> {

		public TwitterLoginTask() {
		}

		protected Void doInBackground(Void... params) {
			try {
				configuredTwitter.updateStatus(Constants.SERVER_HOSTNAME + Constants.ARTICLE_PATH + save.getId_article());
			} catch (Exception e) {
				messages = context.getString(R.string.msg_already_shared);
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (mCallback != null) {
				if (messages != null && messages.length() > 0) {
					mCallback.onError(messages);
				} else {
					// TwitterProfileFragment.profileLoaded = false;
					mCallback.onPostExecute();
				}
			}
		}
	}
}
