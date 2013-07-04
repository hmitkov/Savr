package com.proxiad.savr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.proxiad.savr.application.Application;
import com.proxiad.savr.common.AlertDialogManager;
import com.proxiad.savr.common.ConnectionDetector;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.fragment.TwitterFragment;
import com.proxiad.savr.fragment.TwitterLoginFragment;
import com.proxiad.savr.fragment.TwitterLoginFragment.TwitterLoginCallback;
import com.proxiad.savr.jsno.MyJsonParser;
import com.proxiad.savr.server.ServerHelper;
import com.proxiad.savr.social.TwitterManager;

public abstract class SavrSocialActivity extends FragmentActivity implements TwitterLoginCallback {
	public static final int MIAN = 0;
	public static final int SETTINGS = 1;
	public static final int TWITTER_LOGIN = 2;
	public static final int TWITTER = 3;
	private static final int FRAGMENT_COUNT = TWITTER + 1;

	private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
	private MenuItem settings;
	private boolean isResumed = false;
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private ConnectionDetector cd;
	AlertDialogManager alert = new AlertDialogManager();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(getLayoutResource());

		FragmentManager fm = getSupportFragmentManager();
		fragments[MIAN] = fm.findFragmentById(R.id.mainFragment);
		fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);
		fragments[TWITTER_LOGIN] = fm.findFragmentById(R.id.twitterLoginFragment);
		fragments[TWITTER] = fm.findFragmentById(R.id.twitterFragment);

		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			transaction.hide(fragments[i]);
		}
		transaction.commit();

		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(this, "Internet Connection Error", "Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		ImageView btnSetting = (ImageView) getParent().findViewById(R.id.btn_settings);
		btnSetting.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// openOptionsMenu();
				showPopup(v);
			}
		});
	}

	protected abstract int getLayoutResource();

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		isResumed = true;
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		isResumed = false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		Session session = Session.getActiveSession();

		if (session != null && session.isOpened()) {
			// if the session is already open, try to show the selection
			// fragment
			showFragment(new int[] { MIAN }, false);
		} else if (TwitterManager.getInstance().isLoggedInAlready()) {
			showFragment(new int[] { TWITTER }, false);
		} else {

			if (!cd.isConnectingToInternet()) {

			} else {
				showFragment(new int[] { SETTINGS, TWITTER_LOGIN }, false);
			}
		}
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// // only add the menu when the selection fragment is showing
	// if (fragments[MIAN].isVisible() || fragments[TWITTER].isVisible()) {
	// if (menu.size() == 0) {
	// settings = menu.add(getString(R.string.btn_menu_logout));
	// }
	// return true;
	// } else {
	// menu.clear();
	// settings = null;
	// }
	// return false;
	// }

	public void showPopup(View v) {
		if (fragments[MIAN].isVisible() || fragments[TWITTER].isVisible()) {
			PopupMenu popup = new PopupMenu(this, v);
			settings = popup.getMenu().add(getString(R.string.btn_menu_logout));
			settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					return onOptionsItemSelected(item);
				}
			});
			popup.show();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.equals(settings)) {
			try {
				if (fragments[MIAN].isVisible()) {
					showFragment(new int[] { SETTINGS }, true);
				} else if (fragments[TWITTER].isVisible()) {
					showFragment(new int[] { TWITTER_LOGIN }, false);
				}
				return true;
			} catch (Exception e) {
				System.err.println("onOptionItemSelected" + e.getMessage());
			}
		}
		return false;
	}

	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (isResumed) {
			FragmentManager manager = getSupportFragmentManager();
			int backStackSize = manager.getBackStackEntryCount();
			for (int i = 0; i < backStackSize; i++) {
				manager.popBackStack();
			}
			// check for the OPENED state instead of session.isOpened() since
			// for the
			// OPENED_TOKEN_UPDATED state, the selection fragment should already
			// be showing.
			if (state.equals(SessionState.OPENED)) {
				showFragment(new int[] { MIAN }, false);
			} else if (state.isClosed()) {
				showFragment(new int[] { SETTINGS, TWITTER_LOGIN }, false);
			}
		}
	}

	public void showFragment(int[] fragmentIndexes, boolean addToBackStack) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		for (int i = 0; i < fragments.length; i++) {
			if (isAmongFragmentIndexed(i, fragmentIndexes)) {
				transaction.show(fragments[i]);
			} else {
				transaction.hide(fragments[i]);
			}
		}
		if (addToBackStack) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	private boolean isAmongFragmentIndexed(int index, int[] fragmentIndexes) {
		boolean result = false;
		for (int i = 0; i < fragmentIndexes.length; i++) {
			if (index == fragmentIndexes[i]) {
				result = true;
				break;
			}
		}
		return result;
	}

	// TwitterLoginActivity is started for result from TwitterLoginFragment.
	// The result comes to this mehtod.
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TwitterLoginFragment.REQUEST_TOKEN_LOGIN && resultCode == RESULT_OK) {
			new AccessTokenTask().execute((Void) null);
		}
	}

	private class AccessTokenTask extends AsyncTask<Void, Void, Void> {
		boolean success = false;

		public AccessTokenTask() {
		}

		protected Void doInBackground(Void... urls) {
			try {
				TwitterManager.getInstance().storeTwitterLoginStatus();
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				String token = Application.getInstance().getTokenFacebook();
				long id = TwitterManager.getInstance().getUserId();
				boolean set = Application.getInstance().setTwitterIdIfNecessary(id);
				// If there is no token yet, or the id is new
				if (token.length() == 0 || set) {
					String jsonResponse = ServerHelper.getUserToken(String.valueOf(id), Constants.REQUEST_PARAMETER_TYPE_TW);
					token = MyJsonParser.readToken(jsonResponse);
					Application.getInstance().setTokenTwitter(token);
				}

			} catch (Exception e) {

			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (success) {
				FragmentManager manager = getSupportFragmentManager();
				int backStackSize = manager.getBackStackEntryCount();
				for (int i = 0; i < backStackSize; i++) {
					manager.popBackStack();
				}
				((TwitterFragment) fragments[TWITTER]).startTask();
				showFragment(new int[] { TWITTER }, false);
				TwitterLoginFragment tlf = (TwitterLoginFragment) fragments[TWITTER_LOGIN];
				tlf.initButtonVisibility();
			}
			// Upon successful login to twitter, store the twitter id, if it is
			// not stored already.

		}
	}

	@Override
	public void onLoginAuthorisedUser() {
		showFragment(new int[] { TWITTER }, false);
		TwitterLoginFragment tlf = (TwitterLoginFragment) fragments[TWITTER_LOGIN];
		tlf.initButtonVisibility();
	}

	@Override
	public void onLogoutFromTwitter() {
		TwitterLoginFragment tlf = (TwitterLoginFragment) fragments[TWITTER_LOGIN];
		tlf.initButtonVisibility();
		showFragment(new int[] { SETTINGS, TWITTER_LOGIN }, false);
	}

}
