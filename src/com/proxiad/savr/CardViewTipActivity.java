package com.proxiad.savr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionDefaultAudience;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.OpenGraphAction;
import com.proxiad.savr.adapter.ItemAdapter;
import com.proxiad.savr.async.AsyncOperation;
import com.proxiad.savr.async.EmptyAsyncCallback;
import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.AlertDialogManager;
import com.proxiad.savr.common.ConnectionDetector;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;
import com.proxiad.savr.social.TwitterManager;

public class CardViewTipActivity extends ListActivity {
	protected UiLifecycleHelper uiHelper;
	protected Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(final Session session, final SessionState state, final Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};
	protected ProgressDialog progressDialog;
	LinearLayout llProgress;
	FrameLayout flSendCode;
	TextView tvMessages;
	Button btnSendOrSave;
	static AsyncOperation<?, ?, ?> mOperation = null;
	ArrayList<HashMap<String, String>> dataContainer;
	View btnShare = null;

	private void onSessionStateChange(final Session session, SessionState state, Exception exception) {
		if (session != null && session.isOpened()) {
			if (state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				tokenUpdated();
			}
		}
	}

	private String getFieldValue(String labelValue, Save newSave) {
		String fieldValue = new String();
		if (labelValue.equals(getString(R.string.prix))) {
			fieldValue = newSave.getPrix();
		} else if (labelValue.equals(getString(R.string.adresse))) {
			fieldValue = newSave.getAdresse();
		} else if (labelValue.equals(getString(R.string.artistes))) {
			fieldValue = newSave.getArtistes();
		} else if (labelValue.equals(getString(R.string.numero))) {
			fieldValue = newSave.getNumero();
		} else if (labelValue.equals(getString(R.string.date_debut))) {
			fieldValue = newSave.getDate_debut();
		} else if (labelValue.equals(getString(R.string.date_fin))) {
			fieldValue = newSave.getDate_fin();
		} else if (labelValue.equals(getString(R.string.horaires))) {
			fieldValue = newSave.getHoraires();
		} else if (labelValue.equals(getString(R.string.marque))) {
			fieldValue = newSave.getMarque();
		} else if (labelValue.equals(getString(R.string.acteurs))) {
			fieldValue = newSave.getActeurs();
		} else if (labelValue.equals(getString(R.string.label))) {
			fieldValue = newSave.getLabel();
		} else if (labelValue.equals(getString(R.string.groupe))) {
			fieldValue = newSave.getGroupe();
		} else if (labelValue.equals(getString(R.string.genre))) {
			fieldValue = newSave.getGenre();
		} else if (labelValue.equals(getString(R.string.realisateur))) {
			fieldValue = newSave.getRealisateur();
		} else if (labelValue.equals(getString(R.string.type_de_cuisine))) {
			fieldValue = newSave.getType_de_cuisine();
		} else if (labelValue.equals(getString(R.string.auteur))) {
			fieldValue = newSave.getAuteur();
		} else if (labelValue.equals(getString(R.string.edition))) {
			fieldValue = newSave.getEdition();
		}
		return fieldValue;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		dataContainer = new ArrayList<HashMap<String, String>>();
		setContentView(R.layout.item_list);
		if (savedInstanceState != null) {
			pendingAnnounce = savedInstanceState.getBoolean("pendingAnnounce", false);
		}
		//setLayout();
	}

	public void setLayout() {
		dataContainer = new ArrayList<HashMap<String, String>>();
		// HomeActivity homeActivity = (HomeActivity) getParent();
		Save newSave = HomeActivity.getSave();
		HashMap<String, String> dataMap = new HashMap<String, String>();

		String codeSave = newSave.getCode();
		List<Save> savesByCode = PersistenceManager.getSaveByCode(this, codeSave);
		System.err.println("savesByCode.size()>"+savesByCode.size());
		if(savesByCode.size()!=0){
			
			dataMap.put(Constants.FIELD_ITEM_PLUS, Constants.FIELD_ITEM_PLUS_REMOVE);
		}else{
			dataMap.put(Constants.FIELD_ITEM_PLUS, Constants.FIELD_ITEM_PLUS_PRESENT);
		}
		String stars = newSave.getNote();
		double starsRate = Double.parseDouble(stars.trim());

		if (starsRate == 0) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate < 1 && starsRate > 0) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_MID);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate == 1) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);

		} else if (starsRate < 2 && starsRate > 1) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_MID);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate == 2) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate < 3 && starsRate > 2) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_MID);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate == 3) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_EMPTY);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate < 4 && starsRate > 3) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_MID);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate == 4) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_EMPTY);
		} else if (starsRate < 5 && starsRate > 4) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_MID);
		} else if (starsRate == 5) {
			dataMap.put(Constants.FIELD_STAR_1, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_2, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_3, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_4, Constants.FIELD_STAR_RED);
			dataMap.put(Constants.FIELD_STAR_5, Constants.FIELD_STAR_RED);
		}
		for (int i = 0; i < newSave.getCles().length; i++) {
			String currentLabel = newSave.getCles()[i];// .replace("_", " ");
			String currentLabelFormatted = newSave.getCles()[i].replace("_", " ");
			if (i == 0) {
				dataMap.put(Constants.FIELD_FIRST_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_FIRST_VALUE, getFieldValue(currentLabel, newSave));
			} else if (i == 1) {
				dataMap.put(Constants.FIELD_SECOND_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_SECOND_VALUE, getFieldValue(currentLabel, newSave));
			} else if (i == 2) {
				dataMap.put(Constants.FIELD_THIRD_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_THIRD_VALUE, getFieldValue(currentLabel, newSave));
			} else if (i == 3) {
				dataMap.put(Constants.FIELD_FOURTH_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_FOURTH_VALUE, getFieldValue(currentLabel, newSave));
			} else if (i == 4) {
				dataMap.put(Constants.FIELD_FIFTH_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_FIFTH_VALUE, getFieldValue(currentLabel, newSave));
			} else if (i == 5) {
				dataMap.put(Constants.FIELD_SIXTH_LABEL, currentLabelFormatted);
				dataMap.put(Constants.FIELD_SIXTH_VALUE, getFieldValue(currentLabel, newSave));
			}

		}
		dataMap.put(Constants.FIELD_ARTICLE, newSave.getArticle());
		dataMap.put(Constants.FIELD_CODE, newSave.getCode());
		dataMap.put(Constants.FIELD_NAME, newSave.getNom());
		dataMap.put(Constants.FIELD_ITEM_IMAGE, newSave.getPhoto());
		String isLiked = String.valueOf(newSave.getIsLiked());
		dataMap.put(Constants.FIELD_LOCAL_LIKE, isLiked);
		if (newSave.getIsLiked()) {
			dataMap.put(Constants.FIELD_HEART, Constants.FIELD_HEART_PRESSED);
		} else {
			dataMap.put(Constants.FIELD_HEART, Constants.FIELD_HEART_NOT_PRESSED);
		}

		if (newSave.getGeocode() != null && !newSave.getGeocode().equals("")) {
			Display display = getWindowManager().getDefaultDisplay();
			int screenWidth = display.getWidth();
			// get 100 dp
			int mapHeight = ActivityHelper.pxFromDp(100, this);
			String rareGeoCode = newSave.getGeocode().trim().substring(1, newSave.getGeocode().length() - 1);
			rareGeoCode = rareGeoCode.replace(" ", "");
			String mapURL = Constants.SERVER_GOOGLE_MAP + rareGeoCode + Constants.SERVER_GOOGLE_MAP_ZOOM + Constants.SERVER_GOOGLE_MAP_SIZE + screenWidth + Constants.SERVER_GOOGLE_MAP_X + mapHeight + Constants.SERVER_GOOGLE_MAP_MARKER + rareGeoCode + Constants.SERVER_GOOGLE_MAP_SENSOR;
			dataMap.put(Constants.FIELD_MAP_IMAGE, mapURL);
		} else {
			dataMap.put(Constants.FIELD_MAP_IMAGE, "");
		}
		dataMap.put(Constants.FIELD_LIKES, newSave.getLikes() + "");

		dataContainer.add(dataMap);

		ListAdapter adapter = new ItemAdapter(this, dataContainer, R.layout.activity_card_view_tips, new String[] { Constants.FIELD_FIRST_LABEL, //
				Constants.FIELD_FIRST_VALUE, //
				Constants.FIELD_SECOND_LABEL, //
				Constants.FIELD_SECOND_VALUE, //
				Constants.FIELD_THIRD_LABEL, //
				Constants.FIELD_THIRD_VALUE,//
				Constants.FIELD_FOURTH_LABEL, //
				Constants.FIELD_FOURTH_VALUE, //
				Constants.FIELD_FIFTH_LABEL,//
				Constants.FIELD_FIFTH_VALUE, //
				Constants.FIELD_SIXTH_LABEL, //
				Constants.FIELD_SIXTH_VALUE, //
				Constants.FIELD_ARTICLE, //
				Constants.FIELD_CODE, //
				Constants.FIELD_NAME, //
				Constants.FIELD_ITEM_IMAGE,//
				Constants.FIELD_MAP_IMAGE,//
				Constants.FIELD_LIKES, //
				Constants.FIELD_HEART, //
				Constants.FIELD_STAR_1,//
				Constants.FIELD_STAR_2, //
				Constants.FIELD_STAR_3, //
				Constants.FIELD_STAR_4, //
				Constants.FIELD_STAR_5,
				Constants.FIELD_ITEM_PLUS},//
				new int[] { //
				R.id.fistLabel,//
						R.id.firstValue,//
						R.id.secondLabel,//
						R.id.secondValue,//
						R.id.thirdLabel, //
						R.id.thirdValue, //
						R.id.fourthLabel, //
						R.id.fourthValue, //
						R.id.fifthLabel, //
						R.id.fifthValue,//
						R.id.sixthLabel, //
						R.id.sixthValue, //
						R.id.article, //
						R.id.itemCode, //
						R.id.itemName, //
						R.id.itemImage,//
						R.id.map, //
						R.id.likes, //
						R.id.heart, //
						R.id.star1, //
						R.id.star2, //
						R.id.star3, //
						R.id.star4,//
						R.id.star5,
						R.id.plus});

		setListAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_initialization, menu);
		return true;
	}

	public void setSave(Save newSave) {
		HomeActivity.setSave(newSave);
		System.err.println("SET LAYOUT 1");
		setLayout();
	}

	public void btnPlusOnClick(View view) {
		try {
			List<Save> saveWithThisCode = PersistenceManager.getSaveByCode(this, HomeActivity.getSave().getCode());
			if (saveWithThisCode == null || saveWithThisCode.size() == 0) {
				PersistenceManager.insertSave(this, HomeActivity.getSave());
				((HomeActivity) getParent()).initSlideMenuItems();
				new AlertDialogManager().showAlertDialog(this, getString(R.string.msg_success), getString(R.string.msg_find_item, getString(HomeActivity.getSave().getCategorie().getStringResource())), true);
			} else {
				new AlertDialogManager().showAlertDialog(this, getString(R.string.msg_already_saved), getString(R.string.msg_find_item, getString(HomeActivity.getSave().getCategorie().getStringResource())), false);
			}
		} catch (Exception e) {
			System.err.println("btnPlusOnClick:" + e.getMessage());
		}
	}

	public void btnShareOnClick(View view) {
		btnShare = view;
		if (!new ConnectionDetector(this).isConnectingToInternet()) {
			new AlertDialogManager().showAlertDialog(this, getString(R.string.error_internet), getString(R.string.error_internet_connect), false);
			return;
		}
		Session session = Session.getActiveSession();
		if (TwitterManager.getInstance().isLoggedInAlready()) {
			TwitterManager.getInstance().share(new TwitterShareCallback());
		} else if (session != null) {
			handleActionSave();
			// FacebookFragment.setProfileLoaded(false);
		}

		else {
			new AlertDialogManager().showAlertDialog(CardViewTipActivity.this, getString(R.string.error_not_login), getString(R.string.msg_how_to_login), false);
		}
	}

	class TwitterShareCallback extends EmptyAsyncCallback {
		@Override
		public void onPostExecute() {
			HomeActivity.shouldUpdate = true;
			new AlertDialogManager().showAlertDialog(CardViewTipActivity.this, getString(R.string.msg_success), getString(R.string.msg_share_twitter), true);
		}

		@Override
		public void onError(String msg) {
			new AlertDialogManager().showAlertDialog(CardViewTipActivity.this, getString(R.string.msg_error), msg, false);
		}
	}

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String POST_ACTION_PATH = "me/appsavr:save";
	private static final String PENDING_ANNOUNCE_KEY = "pendingAnnounce";
	private static final Uri M_FACEBOOK_URL = Uri.parse("http://m.facebook.com");
	private boolean pendingAnnounce;

	void tokenUpdated() {
		if (pendingAnnounce) {
			handleActionSave();
		}
	}

	public void handleActionSave() {
		// btnShare.setVisibility(View.INVISIBLE);
		pendingAnnounce = false;
		Session session = Session.getActiveSession();
		// session.removeCallback(callback);
		// session.addCallback(callback);
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
		progressDialog = ProgressDialog.show(this, "", "Waiting...", true);

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
			Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS)
			// demonstrate how to set an audience for the publish permissions,
			// if none are set, this defaults to FRIENDS
					.setDefaultAudience(SessionDefaultAudience.FRIENDS).setRequestCode(REAUTH_ACTIVITY_CODE);
			session.requestNewPublishPermissions(newPermissionsRequest);
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

		new AlertDialog.Builder(this).setPositiveButton(R.string.error_dialog_button_text, listener).setTitle(R.string.error_dialog_title).setMessage(dialogBody).show();
	}

	
	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		setLayout();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("pendingAnnounce", pendingAnnounce);
		uiHelper.onSaveInstanceState(outState);
	}
	
	private void onPostActionResponse(Response response) {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		btnShare.setVisibility(View.VISIBLE);
		new AlertDialogManager().showAlertDialog(this, getString(R.string.msg_success), getString(R.string.msg_share_facebook), true);
		HomeActivity.shouldUpdate = true;
		if (this == null) {
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
			String dialogBody = String.format(getString(R.string.result_dialog_text), postResponse.getId());
			new AlertDialog.Builder(this).setPositiveButton(R.string.result_dialog_button_text, null).setTitle(R.string.result_dialog_title).setMessage(dialogBody).show();
			// init(null);
		} else {
			handleError(response.getError());
		}
	}
}
