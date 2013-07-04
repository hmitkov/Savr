package com.proxiad.savr.fragment;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.proxiad.savr.R;
import com.proxiad.savr.adapter.TwitterSaveAdapter;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.social.TwitterManager;
import com.proxiad.savr.view.FBSave;

public class TwitterSocialFragment extends TwitterFragment {

	@Override
	public void setAdapter() {
		lvFBSaves.setAdapter(new TwitterSaveAdapter(this.getActivity(), fbSaveList, null));
	}

	@Override
	protected int getViewResourceId() {
		return R.layout.fragment_facebook_social;
	}

	@Override
	public void startTask() {
		new TwitterLoadTask().execute((Void) null);
	}

	private class TwitterLoadTask extends AsyncTask<Void, Void, Void> {
		public TwitterLoadTask() {
		}

		@Override
		protected void onPreExecute() {
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.msg_loading_in_progress), true);
			}
			for (int i = 0; i < fbSaveList.size(); i++) {
				fbSaveList.remove(i);
			}
		}

		protected Void doInBackground(Void... params) {
			try {
				long[] myFollowersId = TwitterManager.getInstance().getMyFollowersIds();
				for (long id : myFollowersId) {
					Map<String, Date> sharedSaves = TwitterManager.getInstance().getSharedSaveListForUser(id);
					for (Map.Entry<String, Date> entry : sharedSaves.entrySet()) {
						try {
							String jsonResponse = ServerHelper.getSaveById(entry.getKey());
							Save save = MyJsonParser.saveFromJson(jsonResponse);
							Bitmap followerProfileImage = TwitterManager.getInstance().getUserProfilePicture(id);
							String followerName = TwitterManager.getInstance().getUserName(id);
							fbSaveList.add(new FBSave(save, String.valueOf(id), followerName, ActivityHelper.loadAndStoreBMP(TwitterSocialFragment.this.getActivity(), save), entry.getValue(), followerProfileImage));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				Collections.sort(fbSaveList);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void params) {
			setAdapter();
			if (progressDialog != null) {
				progressDialog.hide();
				progressDialog = null;
			}
			// socialLoaded = true;
		}
	}
}