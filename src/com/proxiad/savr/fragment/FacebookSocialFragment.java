package com.proxiad.savr.fragment;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.view.FBSave;

public class FacebookSocialFragment extends FacebookFragment {
	// protected boolean isLoaded() {
	// return isSocialLoaded();
	// }
	protected AsyncTask<Void, Void, Void> task = null;

	protected int getViewResourceId() {
		return R.layout.fragment_facebook_social;
	}

	protected void startTask() {
		if (task == null) {
			task = new LoadFBDataTask();
			task.execute();
		}
	}

	private class LoadFBDataTask extends AsyncTask<Void, Void, Void> {
		public LoadFBDataTask() {
		}

		protected Void doInBackground(Void... params) {
			Session session = Session.getActiveSession();
			List<String> friendsInstalledIdList = requestMyAppFacebookFriends(session);
			for (String friendId : friendsInstalledIdList) {
				try {
					Request request = new Request(Session.getActiveSession(), friendId + "/appsavr:save", null, HttpMethod.GET);
					Response response = request.executeAndWait();
					GraphObject go = response.getGraphObject();
					JSONObject jso = go.getInnerJSONObject();
					JSONArray arr = jso.getJSONArray("data");
					List<String[]> saveIdAndDateList = MyJsonParser.getFBSaveIdAndDateList(arr);

					String jsonResponse;
					for (String[] idAndDate : saveIdAndDateList) {
						jsonResponse = ServerHelper.getSaveById(idAndDate[0]);
						Save save = MyJsonParser.saveFromJson(jsonResponse);
						Date fbDate = null;
						try {
							fbDate = ActivityHelper.parseDate(idAndDate[1]);
						} catch (Exception e) {
							fbDate = null;
						}
						String publisher = idAndDate[2];
						fbSaveList.add(new FBSave(save, friendId, publisher, ActivityHelper.loadAndStoreBMP(FacebookSocialFragment.this.getActivity(), save), fbDate, null));
					}
					// For some of the iterations there is an exception. But for
					// the rest, it is ok. This means that all the items will go
					// in fbSaveList, except those for which there is an
					// exception.
					// The logic is: if it is success for at least one, then it
					// is success. This way exceptions due to no network will
					// not be
					// success, because there won't be success for any.
					// setSocialLoaded(true);
					
					Collections.sort(fbSaveList);
				} catch (Exception e) {
					Log.e(Constants.TAG_LOG, e.getMessage());
				}
			}

			return null;
		}

		protected void onPreExecute() {
			emptyList();
			if (progressDialog == null) {
				progressDialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.msg_loading_in_progress), true);
			}
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			adapter.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.dismiss();
				progressDialog = null;
			}
			task = null;
		}
	}
}
