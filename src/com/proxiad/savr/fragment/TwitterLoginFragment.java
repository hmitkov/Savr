package com.proxiad.savr.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.proxiad.savr.R;
import com.proxiad.savr.TwitterLoginActivity;
import com.proxiad.savr.common.AlertDialogManager;
import com.proxiad.savr.social.TwitterManager;

public class TwitterLoginFragment extends Fragment {
	public static final String EXTRA_AUTHENTICATION_URL = "authentication_url";

	public static final int REQUEST_TOKEN_LOGIN = 2;

	Button btnLoginTwitter;
	Button btnLogoutTwitter;
	TwitterLoginCallback mCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_twitter_login, container, false);
		btnLoginTwitter = (Button) view.findViewById(R.id.btnLoginTwitter);
		btnLogoutTwitter = (Button) view.findViewById(R.id.btnLogoutTwitter);
		btnLoginTwitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loginToTwitter();
			}
		});

		btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				logoutFromTwitter();
			}
		});
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		initButtonVisibility();
	}

	public void initButtonVisibility() {
		if (TwitterManager.getInstance().isLoggedInAlready()) {
			btnLoginTwitter.setVisibility(View.GONE);
			btnLogoutTwitter.setVisibility(View.VISIBLE);
		} else {
			btnLoginTwitter.setVisibility(View.VISIBLE);
			btnLogoutTwitter.setVisibility(View.GONE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallback = (TwitterLoginCallback) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement TwitterLoginCallback");
		}
	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		if (TwitterManager.getInstance().isLoggedInAlready()) {
			mCallback.onLoginAuthorisedUser();
		} else {
			new TwitterLoginTask().execute((Void) null);

			// TwitterFragment.profileLoaded = false;
			// TwitterFragment.socialLoaded = false;
		}
	}

	private void logoutFromTwitter() {
		TwitterManager.getInstance().logoutFromTwitter();
		TwitterManager.resetInstance();
		mCallback.onLogoutFromTwitter();
	}

	private class TwitterLoginTask extends AsyncTask<Void, Void, Void> {
		boolean success = false;

		public TwitterLoginTask() {
		}

		protected Void doInBackground(Void... urls) {
			try {
				// This call may also store the access token. So in the
				// postExecute, check if this has happend
				TwitterManager.getInstance().requestLogin();
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
				TwitterManager.resetInstance();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (TwitterManager.getInstance().isLoggedInAlready()) {
				mCallback.onLoginAuthorisedUser();
			} else {
				if (success) {
					Intent intent = new Intent(getActivity(), TwitterLoginActivity.class);
					getActivity().startActivityForResult(intent, REQUEST_TOKEN_LOGIN);
				} else {
					new AlertDialogManager().showAlertDialog(TwitterLoginFragment.this.getActivity(), getString(R.string.msg_error),  getString(R.string.error_login), false);
				}
			}
		}
	}

	public interface TwitterLoginCallback {

		void onLoginAuthorisedUser();

		void onLogoutFromTwitter();
	}
}
