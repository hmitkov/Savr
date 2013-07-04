package com.proxiad.savr.listeners;

import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;

import com.proxiad.savr.CardViewTipActivity;
import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.R;
import com.proxiad.savr.application.Application;
import com.proxiad.savr.common.AlertDialogManager;
import com.proxiad.savr.common.ConnectionDetector;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;
import com.proxiad.savr.server.ServerHelper;

public class HeartClickListener implements OnClickListener {
	CardViewTipActivity activity;
	String token = null;

	@Override
	public void onClick(View v) {
		activity = (CardViewTipActivity) v.getContext();

		if (!HomeActivity.getSave().getIsLiked()) {
			if (!new ConnectionDetector(v.getContext()).isConnectingToInternet()) {
				new AlertDialogManager().showAlertDialog(v.getContext(), v.getContext().getString(R.string.error_internet), v.getContext().getString(R.string.error_internet_connect), false);
				return;
			}
			token = Application.getInstance().getTokenFacebook();
			if (token == null) {
				token = Application.getInstance().getTokenTwitter();
			}
			if (token == null) {
				new AlertDialogManager().showAlertDialog(v.getContext(), v.getContext().getString(R.string.error_not_login), v.getContext().getString(R.string.error_login_first), false);
				return;
			}
			new LikeTask().execute((Void) null);
		}
	}

	private class LikeTask extends AsyncTask<Void, Void, Void> {
		String message = null;

		protected Void doInBackground(Void... params) {
			try {
				ServerHelper.like(HomeActivity.getSave().getId_article(), token);
			} catch (Exception e) {
				message = e.getMessage();
			}
			return null;
		}

		protected void onPostExecute(Void result) {
			if (message == null) {
				Save updated = PersistenceManager.updateLikes(activity, HomeActivity.getSave());
				activity.setSave(updated);
				new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.msg_success), activity.getString(R.string.msg_liked), true);
			} else {
				new AlertDialogManager().showAlertDialog(activity, activity.getString(R.string.msg_error), message, false);
			}
		}
	}
}
