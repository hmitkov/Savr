package com.proxiad.savr.fragment;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.model.OpenGraphAction;
import com.facebook.widget.ProfilePictureViewCyrcle;
import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.view.FBSave;

public class FacebookProfileFragment extends FacebookFragment {
	private static final String FACEBOOK_GRAPH_OBJECT = "me/appsavr:save";
	private ProfilePictureViewCyrcle profilePic;
	private ImageView coverphoto;
	// private ImageView ivProfilePic;
	private TextView tvNumberOfFriends, tvNumberOfSaves;
	protected AsyncTask<String, Void, Void> task = null;
	private Bitmap coverPhotoBtm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		profilePic = (ProfilePictureViewCyrcle) view
				.findViewById(R.id.profilepic);
		// profilePic.setPresetSize(ProfilePictureView.NORMAL);
		// ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
		tvNumberOfFriends = (TextView) view
				.findViewById(R.id.tvNumberOfFriends);
		tvNumberOfSaves = (TextView) view.findViewById(R.id.tvNumberOfSaves);
		coverphoto = (ImageView) view.findViewById(R.id.coverImage);
		/*
		 * tvNumberOfSaves.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { // handleActionSave();
		 * 
		 * } });
		 */
		return view;
	}

	// protected boolean isLoaded() {
	// return isProfileLoaded();
	// }

	protected int getViewResourceId() {
		return R.layout.fragment_facebook_profile;
	}

	protected void startTask() {
		makeMeRequest();
	}

	private void makeMeRequest() {
		final Session session = Session.getActiveSession();
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (session == Session.getActiveSession()) {
							if (user != null) {
								// if (progressDialog == null) {
								// progressDialog =
								// ProgressDialog.show(getActivity(),
								// "",
								// getResources().getString(R.string.msg_loading_in_progress),
								// true);
								// }
								profilePic.setProfileId(user.getId());
								if (task == null) {
									task = new LoadFBDataTask();
									task.execute((user.getId()));
								}

							}
						}
						// if (response.getError() != null) {
						// handleError(response.getError());
						// }
					}
				});
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(getActivity(), "",
					getResources().getString(R.string.msg_loading_in_progress),
					true);
		}
		request.executeAsync();
	}

	private class LoadFBDataTask extends AsyncTask<String, Void, Void> {
		int numberOfFriends = 0;
		int numberOfSaves = 0;

		public LoadFBDataTask() {
		}

		protected Void doInBackground(String... params) {
			try {
				// This is a suitable place to check if a facebook user exists
				// on the savr back-end server. If not, then creat it and store
				// it on local storage. The place is suitable, because here we
				// have the facebook id and we are in an async task.
				// It is more correct to perform this operation in
				// onSessionStateChanged, after user login. But anyway we have
				// to load the token and save it only the first time.

				String myFbId = params[0];

				handleFacebookToken(myFbId);

				Session session = Session.getActiveSession();
				initNumberOfFriends(session);
				JSONArray arr = requestSaveGraphObjects(session);
				String URL = "https://graph.facebook.com/" + myFbId
						+ "?fields=cover&access_token="
						+ session.getAccessToken();
				String finalCoverPhoto = null;
				try {

					HttpClient hc = new DefaultHttpClient();
					HttpGet get = new HttpGet(URL);
					HttpResponse rp = hc.execute(get);

					if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						String result = EntityUtils.toString(rp.getEntity());

						JSONObject JODetails = new JSONObject(result);

						if (JODetails.has("cover")) {
							String getInitialCover = JODetails
									.getString("cover");

							if (getInitialCover.equals("null")) {
								finalCoverPhoto = null;
							} else {
								JSONObject JOCover = JODetails
										.optJSONObject("cover");

								if (JOCover.has("source")) {
									finalCoverPhoto = JOCover
											.getString("source");
								} else {
									finalCoverPhoto = null;
								}
							}
						} else {
							finalCoverPhoto = null;
						}
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

				System.err.println("finalCoverPhoto-->" + finalCoverPhoto);

				if (finalCoverPhoto != null) {
					coverPhotoBtm = ServerHelper.downloadImage(finalCoverPhoto);
					System.err.println("coverPhotoBtm-->" + coverPhotoBtm);

					// coverphoto
					// .setImageBitmap(coverPhotoBtm);
				}

				numberOfSaves = arr.length();
				List<String[]> saveIdAndDateList = MyJsonParser
						.getFBSaveIdAndDateList(arr);
				String jsonResponse;
				for (String[] idAndDate : saveIdAndDateList) {
					System.err.println("idAndDate[0]-->" + idAndDate[0]);
					jsonResponse = ServerHelper.getSaveById(idAndDate[0]);
					Save save = MyJsonParser.saveFromJson(jsonResponse);
					Date fbDate = null;
					try {
						fbDate = ActivityHelper.parseDate(idAndDate[1]);
					} catch (Exception e) {
						fbDate = null;
					}
					Bitmap profilePicBmt = profilePic.getImageBitmapOriginal();
					fbSaveList.add(new FBSave(save, myFbId, null, 
							ActivityHelper.loadAndStoreBMP(
									FacebookProfileFragment.this.getActivity(),
									save), fbDate, null)
							);
				}
				Collections.sort(fbSaveList);
			} catch (Exception e) {
				// setSocialLoaded(false);
				Log.e(Constants.TAG_LOG, e.getMessage());
			}
			return null;
		}

		JSONArray requestSaveGraphObjects(Session sesison) throws Exception {
			Request request = new Request(Session.getActiveSession(),
					FACEBOOK_GRAPH_OBJECT, null, HttpMethod.GET);
			Response response = request.executeAndWait();
			GraphObject go = response.getGraphObject();
			JSONObject jso = go.getInnerJSONObject();
			JSONArray arr = jso.getJSONArray("data");
			return arr;
		}

		void initNumberOfFriends(Session session) {
			List<String> friendsInstalledIds = requestMyAppFacebookFriends(session);
			numberOfFriends = friendsInstalledIds.size();
		}

		protected void onPreExecute() {
			emptyList();
		}

		@Override
		protected void onPostExecute(Void result) {
			System.err.println("coverphoto-->" + coverphoto);

			if (coverPhotoBtm != null) {
				coverphoto.setImageDrawable(new BitmapDrawable(ActivityHelper
						.fastblur(coverPhotoBtm, 5)));
			}
			super.onPostExecute(result);
			tvNumberOfFriends.setText(String.valueOf(numberOfFriends));
			tvNumberOfSaves.setText(String.valueOf(numberOfSaves));
			adapter.notifyDataSetChanged();
			if (progressDialog != null) {
				progressDialog.hide();
				progressDialog = null;
			}
			task = null;
			HomeActivity.shouldUpdate = false;
		}
	}

	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String POST_ACTION_PATH = "me/appsavr:save";
	// private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
	// private static final Uri M_FACEBOOK_URL =
	// Uri.parse("http://m.facebook.com");
	private boolean pendingAnnounce;

	void tokenUpdated() {
		if (pendingAnnounce) {
			handleActionSave();
		}
	}

	public void handleActionSave() {
		pendingAnnounce = false;
		Session session = Session.getActiveSession();
		session.removeCallback(callback);
		session.addCallback(callback);
		if (session == null || !session.isOpened()) {
			return;
		}

		List<String> permissions = session.getPermissions();
		if (!permissions.containsAll(PERMISSIONS)) {
			pendingAnnounce = true;
			requestPublishPermissions(session);
			return;
		}

		// Show a progress dialog because sometimes the requests can take a
		// while.
		progressDialog = ProgressDialog.show(this.getActivity(), "",
				"Waiting...", true);

		// Run this in a background thread since some of the populate methods
		// may take
		// a non-trivial amount of time.
		AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {

			@Override
			protected Response doInBackground(Void... voids) {
				SaveAction saveAction = GraphObject.Factory
						.create(SaveAction.class);
				SaveGraphObject save = GraphObject.Factory
						.create(SaveGraphObject.class);
				String url = "http://www.savr.fr/v/article.php?id="
						+ HomeActivity.getSave().getId_article();
				save.setUrl(url);
				saveAction.setArticle(save);
				Request request = new Request(Session.getActiveSession(),
						POST_ACTION_PATH, null, HttpMethod.POST);
				request.setGraphObject(saveAction);
				Response s = request.executeAndWait();
				return s;
			}

			@Override
			protected void onPostExecute(Response response) {
				onPostActionResponse(response);
			}
		};

		task.execute();
	}

	private void requestPublishPermissions(Session session) {
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
					this.getActivity(), PERMISSIONS)
					// demonstrate how to set an audience for the publish
					// permissions,
					// if none are set, this defaults to FRIENDS
					.setDefaultAudience(SessionDefaultAudience.FRIENDS)
					.setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	private void onPostActionResponse(Response response) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (this.getActivity() == null) {
			// if the user removes the app from the website, then a request will
			// have caused the session to close (since the token is no longer
			// valid),
			// which means the splash fragment will be shown rather than this
			// one,
			// causing activity to be null. If the activity is null, then we
			// cannot
			// show any dialogs, so we return.
			return;
		}

		PostResponse postResponse = response
				.getGraphObjectAs(PostResponse.class);

		if (postResponse != null && postResponse.getId() != null) {
			String dialogBody = String.format(
					this.getActivity().getString(R.string.result_dialog_text),
					postResponse.getId());
			new AlertDialog.Builder(this.getActivity())
					.setPositiveButton(R.string.result_dialog_button_text, null)
					.setTitle(R.string.result_dialog_title)
					.setMessage(dialogBody).show();
			// init(null);
		} else {
			handleError(response.getError());
		}
	}

	private interface SaveGraphObject extends GraphObject {
		public String getUrl();

		public void setUrl(String url);

		public String getId();

		public void setId(String id);
	}

	/**
	 * Interface representing the Eat action.
	 */
	private interface SaveAction extends OpenGraphAction {
		public SaveGraphObject getArticle();

		public void setArticle(SaveGraphObject save);
	}

	private interface PostResponse extends GraphObject {
		String getId();
	}
}
