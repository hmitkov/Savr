package com.proxiad.savr.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.adapter.FacebookSaveAdapter;
import com.proxiad.savr.adapter.SaveAdapter;
import com.proxiad.savr.application.Application;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.NetworkUtil;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.view.FBSave;

public abstract class FacebookFragment extends Fragment {
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	protected ListView lvSocialSaves;
	protected SaveAdapter adapter;
	protected List<FBSave> fbSaveList = new ArrayList<FBSave>();
	protected ProgressDialog progressDialog;

	protected UiLifecycleHelper uiHelper;
	protected Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened() && (fbSaveList.size() == 0 || HomeActivity.shouldUpdate) && NetworkUtil.isNetworkConnectionAvailable(getActivity())) {
			startTask();
		}
	}

	protected abstract void startTask();

	protected abstract int getViewResourceId();

	// protected abstract boolean isLoaded();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final HomeActivity homeActivity = (HomeActivity) getActivity().getParent();
		View view = inflater.inflate(getViewResourceId(), container, false);
		lvSocialSaves = (ListView) view.findViewById(R.id.lvSocialSaves);
		fbSaveList = new ArrayList<FBSave>();
		adapter = new FacebookSaveAdapter(getActivity(), fbSaveList);
		lvSocialSaves.setAdapter(adapter);
		lvSocialSaves.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Save save = fbSaveList.get((int) id).getSave();
				HomeActivity.setSave(save);
				homeActivity.refresh();
			}
		});
		return view;
	}

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				tokenUpdated();
			}
		} else if (state == SessionState.CLOSED) {
			// setProfileLoaded(false);
			// setSocialLoaded(false);
		}
	}

	void tokenUpdated() {

	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if (requestCode == REAUTH_ACTIVITY_CODE) {
		// uiHelper.onActivityResult(requestCode, resultCode, data);
		// }
	}

	protected void handleError(FacebookRequestError error) {
		DialogInterface.OnClickListener listener = null;
		String dialogBody = null;

		if (error == null) {
			dialogBody = getString(R.string.error_dialog_default_text);
		} else {
			switch (error.getCategory()) {
			case AUTHENTICATION_RETRY:
				// tell the user what happened by getting the message id, and
				// retry the operation later
				String userAction = (error.shouldNotifyUser()) ? "" : getString(error.getUserActionMessageId());
				dialogBody = getString(R.string.error_authentication_retry, userAction);
				listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
						startActivity(intent);
					}
				};
				break;

			case AUTHENTICATION_REOPEN_SESSION:
				// close the session and reopen it.
				dialogBody = getString(R.string.error_authentication_reopen);
				listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Session session = Session.getActiveSession();
						if (session != null && !session.isClosed()) {
							session.closeAndClearTokenInformation();
						}
					}
				};
				break;

			case PERMISSION:
				// request the publish permission
				dialogBody = getString(R.string.error_permission);
				listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						requestPublishPermissions(Session.getActiveSession());
					}
				};
				break;

			case SERVER:
			case THROTTLING:
				// this is usually temporary, don't clear the fields, and
				// ask the user to try again
				dialogBody = getString(R.string.error_server);
				break;

			case BAD_REQUEST:
				// this is likely a coding error, ask the user to file a bug
				dialogBody = getString(R.string.error_bad_request, error.getErrorMessage());
				break;

			case OTHER:
			case CLIENT:
			default:
				// an unknown issue occurred, this could be a code error, or
				// a server side issue, log the issue, and either ask the
				// user to retry, or file a bug
				dialogBody = getString(R.string.error_unknown, error.getErrorMessage());
				break;
			}
		}

		new AlertDialog.Builder(getActivity()).setPositiveButton(R.string.error_dialog_button_text, listener).setTitle(R.string.error_dialog_title).setMessage(dialogBody).show();
	}

	private void requestPublishPermissions(Session session) {
		if (session != null) {
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
			// demonstrate how to set an audience for the publish permissions,
			// if none are set, this defaults to FRIENDS
					.setDefaultAudience(SessionDefaultAudience.FRIENDS).setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	//
	// // Not used in the moment
	// private class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
	// ImageView bmImage;
	// String id;
	//
	// public DownloadImageTask(ImageView bmImage, String id) {
	// this.bmImage = bmImage;
	// this.id = id;
	// }
	//
	// protected Bitmap doInBackground(Void... params) {
	// String urldisplay = "http://graph.facebook.com/" + id +
	// "/picture?type=large";
	// Bitmap mIcon11 = null;
	// try {
	// InputStream in = new java.net.URL(urldisplay).openStream();
	// mIcon11 = BitmapFactory.decodeStream(in);
	// } catch (Exception e) {
	// Log.e("Error", e.getMessage());
	// e.printStackTrace();
	// }
	// return mIcon11;
	// }
	//
	// protected void onPostExecute(Bitmap result) {
	// Display display = getActivity().getWindowManager().getDefaultDisplay();
	// bmImage.setImageBitmap(result);
	// bmImage.setMinimumHeight(220);
	// bmImage.setMaxHeight(220);
	// bmImage.setMinimumWidth(display.getWidth());
	// bmImage.setMaxWidth(display.getWidth());
	// bmImage.setScaleType(ScaleType.CENTER_CROP);
	// }
	// }

	protected List<String> requestMyAppFacebookFriends(Session session) {
		List<String> result = new ArrayList<String>();
		Request friendsRequest = createRequest(session);
		Response response = friendsRequest.executeAndWait();
		List<GraphUser> friends = getResults(response);
		for (GraphUser gUser : friends) {
			Object installed = gUser.getProperty("installed");
			if (installed != null && (Boolean) installed) {
				result.add(gUser.getId());
			}
		}
		return result;
	}

	private List<GraphUser> getResults(Response response) {
		List<GraphUser> result = new ArrayList<GraphUser>();
		GraphMultiResult multiResult = response.getGraphObjectAs(GraphMultiResult.class);
		if (multiResult != null) {
			GraphObjectList<GraphObject> data = multiResult.getData();
			result = data.castToListOf(GraphUser.class);
		}
		return result;
	}

	private Request createRequest(Session session) {
		Request request = Request.newGraphPathRequest(session, "me/friends", null);

		Set<String> fields = new HashSet<String>();
		String[] requiredFields = new String[] { "id", "name", "picture", "installed" };
		fields.addAll(Arrays.asList(requiredFields));

		Bundle parameters = request.getParameters();
		parameters.putString("fields", TextUtils.join(",", fields));
		request.setParameters(parameters);

		return request;
	}

	protected void emptyList() {
		for (int i = 0; i < fbSaveList.size(); i++) {
			fbSaveList.remove(i);
		}
	}

	void handleFacebookToken(String id) {
		// In case of exception, do nothing.
		try {
			String token = Application.getInstance().getTokenFacebook();
			boolean set = Application.getInstance().setFacebookIdIfNecessary(id);
			// If there is no token yet, or the id is new
			if (token.length() == 0 || set) {
				String jsonResponse = ServerHelper.getUserToken(id, Constants.REQUEST_PARAMETER_TYPE_FB);
				token = MyJsonParser.readToken(jsonResponse);
				Application.getInstance().setTokenFacebook(token);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
