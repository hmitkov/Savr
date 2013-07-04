package com.proxiad.savr.fragment;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.adapter.TwitterSaveAdapter;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.social.TwitterManager;
import com.proxiad.savr.view.FBSave;

public class TwitterProfileFragment extends TwitterFragment {
	ImageView ivProfilePic;
	private TextView tvNumberOfFriends, tvNumberOfSaves;
	public static Bitmap profilePic = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		ivProfilePic = (ImageView) view.findViewById(R.id.profilepic);
		tvNumberOfFriends = (TextView) view.findViewById(R.id.tvNumberOfFriends);
		tvNumberOfSaves = (TextView) view.findViewById(R.id.tvNumberOfSaves);
		return view;
	}

	@Override
	protected int getViewResourceId() {
		return R.layout.fragment_twitter_profile;
	}

	@Override
	public void setAdapter() {
		lvFBSaves.setAdapter(new TwitterSaveAdapter(this.getActivity(), fbSaveList, profilePic));
	}

	@Override
	public void startTask() {
		new TwitterLoadTask().execute((Void) null);
	}

	private class TwitterLoadTask extends AsyncTask<Void, Void, Void> {
		int numberOfSaves = 0;
		long numberOfFollowers = 0;

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
				profilePic = TwitterManager.getInstance().getProfilePicture();
				numberOfFollowers = TwitterManager.getInstance().getFollowersCount();
				//TwitterManager.getInstance().get
				Map<String, Date> sharedSaves = TwitterManager.getInstance().getSharedSaveList();
				for (Map.Entry<String, Date> entry : sharedSaves.entrySet()) {
					try {
						String jsonResponse = ServerHelper.getSaveById(entry.getKey());
						Save save = MyJsonParser.saveFromJson(jsonResponse);
						fbSaveList.add(new FBSave(save, null, null, ActivityHelper.loadAndStoreBMP(TwitterProfileFragment.this.getActivity(), save), entry.getValue(), null));
						numberOfSaves++;
					} catch (Exception e) {
						e.printStackTrace();
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
			if (profilePic != null) {
				
				ivProfilePic.setImageBitmap(ActivityHelper.getCircleBitmap(profilePic));
			}
			tvNumberOfSaves.setText(String.valueOf(numberOfSaves));
			tvNumberOfFriends.setText(String.valueOf(numberOfFollowers));
			setAdapter();
			if (progressDialog != null) {
				progressDialog.hide();
				progressDialog = null;
			}
			HomeActivity.shouldUpdate = false;
		}
	}
}
