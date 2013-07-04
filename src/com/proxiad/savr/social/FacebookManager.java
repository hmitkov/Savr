package com.proxiad.savr.social;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;

public class FacebookManager {
	private static FacebookManager instance;

	public static FacebookManager getInstance(Activity activity) {
		if (instance == null) {
			instance = new FacebookManager(activity);
		}
		return instance;
	}

	public static void resetInstance() {
		instance = null;
	}

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String POST_ACTION_PATH = "me/appsavr:save";
	private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");

	Activity activity;
	private ProgressDialog progressDialog;

	public FacebookManager(Activity activity) {
		this.activity = activity;
		Session session = Session.getActiveSession();
		if (session == null) {
			session = new Session(activity);
			Session.setActiveSession(session);
		}

		if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			session.openForRead(new Session.OpenRequest(activity).setCallback(callback));
		}
	}

	private boolean pendingAnnounce;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				tokenUpdated();
			} else {
				// makeMeRequest(session);
			}
		}
	}

	private void tokenUpdated() {
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
		progressDialog = ProgressDialog.show(activity, "", "Waiting...", true);

		// Run this in a background thread since some of the populate methods
		// may take
		// a non-trivial amount of time.
		AsyncTask<Void, Void, Response> task = new AsyncTask<Void, Void, Response>() {

			@Override
			protected Response doInBackground(Void... voids) {
				SaveAction saveAction = GraphObject.Factory.create(SaveAction.class);
				SaveGraphObject save = GraphObject.Factory.create(SaveGraphObject.class);
				String url = "http://www.savr.fr/v/article.php?id=" + HomeActivity.getSave().getId_article();
				save.setUrl(url);
				saveAction.setArticle(save);
				Request request = new Request(Session.getActiveSession(), POST_ACTION_PATH, null, HttpMethod.POST);
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
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(activity, PERMISSIONS)
			// demonstrate how to set an audience for the publish permissions,
			// if none are set, this defaults to FRIENDS
					.setDefaultAudience(SessionDefaultAudience.FRIENDS).setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
		}
	}

	private void onPostActionResponse(Response response) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		if (activity == null) {
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

		PostResponse postResponse = response.getGraphObjectAs(PostResponse.class);

		if (postResponse != null && postResponse.getId() != null) {
			String dialogBody = String.format(activity.getString(R.string.result_dialog_text), postResponse.getId());
			new AlertDialog.Builder(activity).setPositiveButton(R.string.result_dialog_button_text, null).setTitle(R.string.result_dialog_title).setMessage(dialogBody).show();
			// init(null);
		} else {
			handleError(response.getError());
		}
	}

	private void handleError(FacebookRequestError error) {
		DialogInterface.OnClickListener listener = null;
		String dialogBody = null;

		if (error == null) {
			dialogBody = "aaa";
		} else {
			switch (error.getCategory()) {
			case AUTHENTICATION_RETRY:
				// tell the user what happened by getting the message id, and
				// retry the operation later
				String userAction = (error.shouldNotifyUser()) ? "" : activity.getString(error.getUserActionMessageId());
				dialogBody = activity.getString(R.string.error_authentication_retry, userAction);
				listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						Intent intent = new Intent(Intent.ACTION_VIEW, M_FACEBOOK_URL);
						activity.startActivity(intent);
					}
				};
				break;

			case AUTHENTICATION_REOPEN_SESSION:
				// close the session and reopen it.
				dialogBody = activity.getString(R.string.error_authentication_reopen);
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
				dialogBody = activity.getString(R.string.error_permission);
				listener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						pendingAnnounce = true;
						requestPublishPermissions(Session.getActiveSession());
					}
				};
				break;

			case SERVER:
			case THROTTLING:
				// this is usually temporary, don't clear the fields, and
				// ask the user to try again
				dialogBody = activity.getString(R.string.error_server);
				break;

			case BAD_REQUEST:
				// this is likely a coding error, ask the user to file a bug
				dialogBody = activity.getString(R.string.error_bad_request, error.getErrorMessage());
				break;

			case OTHER:
			case CLIENT:
			default:
				// an unknown issue occurred, this could be a code error, or
				// a server side issue, log the issue, and either ask the
				// user to retry, or file a bug
				dialogBody = activity.getString(R.string.error_unknown, error.getErrorMessage());
				break;
			}
		}

		new AlertDialog.Builder(activity).setPositiveButton(R.string.error_dialog_button_text, listener).setTitle(R.string.error_dialog_title).setMessage(dialogBody).show();
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
