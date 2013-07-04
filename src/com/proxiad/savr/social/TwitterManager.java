package com.proxiad.savr.social;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import twitter4j.IDs;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;

import com.proxiad.savr.HomeActivity;
import com.proxiad.savr.application.Application;
import com.proxiad.savr.async.AsyncOperation.AsyncOperationCallback;
import com.proxiad.savr.async.TwitterShareOperation;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.server.ServerHelper;

public class TwitterManager {
	private static TwitterManager instance;

	public static TwitterManager getInstance() {
		if (instance == null) {
			instance = new TwitterManager();
		}
		return instance;
	}

	public static void resetInstance() {
		instance = null;
	}

	private SharedPreferences mSharedPreferences;
	public Twitter twitter;
	private RequestToken requestToken;

	private TwitterManager() {
		mSharedPreferences = Application.getInstance().getSharedPreferences(Constants.PREF_NAME, 0);
		ConfigurationBuilder builder = new ConfigurationBuilder();
		builder.setOAuthConsumerKey(Constants.TWITTER_CONSUMER_KEY);
		builder.setOAuthConsumerSecret(Constants.TWITTER_CONSUMER_SECRET);
		Configuration configuration = builder.build();
		TwitterFactory factory = new TwitterFactory(configuration);
		twitter = factory.getInstance();

		if (isLoggedInAlready()) {
			String token = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_TOKEN, null);
			String secret = mSharedPreferences.getString(Constants.PREF_KEY_OAUTH_SECRET, null);
			AccessToken at = new AccessToken(token, secret);
			twitter.setOAuthAccessToken(at);
		}
	}

	public boolean isLoggedInAlready() {
		return mSharedPreferences.getBoolean(Constants.PREF_KEY_TWITTER_LOGIN, false);
	}

	public void share(AsyncOperationCallback callback) {
		new TwitterShareOperation(Application.getInstance().getApplicationContext(), callback, twitter, HomeActivity.getSave()).doLongOperation();
	}

	public Bitmap getProfilePicture() throws Exception {
		return getUserProfilePicture(twitter.getId());
	}

	public Bitmap getUserProfilePicture(long id) throws Exception {
		User user = twitter.showUser(id);
		String url = user.getProfileImageURL();
		Bitmap bmp = ServerHelper.downloadImage(url);
		return bmp;
	}
	public String getUserName(long id) throws Exception {
		User user = twitter.showUser(id);
		return user.getName();
	}
	public Map<String, Date> getSharedSaveList() throws Exception {
		Map<String, Date> sharedSaves = new HashMap<String, Date>();
		List<twitter4j.Status> statuses = twitter.getUserTimeline(new Paging(1, 200));
		for (twitter4j.Status status : statuses) {
			String id = getSaveIdFromStatus(status);
			if (id != null && id.length() > 0) {
				Date date = status.getCreatedAt();
				sharedSaves.put(id, date);
			}
		}
		return sharedSaves;
	}

	private String getSaveIdFromStatus(Status status) {
		for (URLEntity urlEntity : status.getURLEntities()) {
			if (urlEntity.getExpandedURL().contains(Constants.SERVER_HOSTNAME + Constants.ARTICLE_PATH)) {
				String id = (urlEntity.getExpandedURL().substring(urlEntity.getExpandedURL().length() - 3, urlEntity.getExpandedURL().length()));
				return id;
			}
		}
		return null;
	}

	public Map<String, Date> getSharedSaveListForUser(long id) throws Exception {
		Map<String, Date> sharedSaves = new HashMap<String, Date>();
		List<twitter4j.Status> statuses = twitter.getUserTimeline(id, new Paging(1, 200));
		for (twitter4j.Status status : statuses) {
			String saveId = getSaveIdFromStatus(status);
			if (saveId != null) {
				Date date = status.getCreatedAt();
				sharedSaves.put(saveId, date);
			}
		}
		return sharedSaves;
	}

	public long[] getMyFollowersIds() throws Exception {
		IDs ids = twitter.getFollowersIDs(-1);
		return ids.getIDs();
	}

	public long getFollowersCount() throws Exception {
		return getMyFollowersIds().length;
	}

	public void requestLogin() throws Exception {
		try {
			requestToken = twitter.getOAuthRequestToken(Constants.TWITTER_CALLBACK_URL);
		} catch (Exception e) {
			AccessToken at = twitter.getOAuthAccessToken();
			storeAccessToken(at);
		}
	}

	public void logoutFromTwitter() {
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(Constants.PREF_KEY_OAUTH_TOKEN);
		e.remove(Constants.PREF_KEY_OAUTH_SECRET);
		e.remove(Constants.PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

	public RequestToken getRequestToken() {
		return requestToken;
	}

	public void setRequestToken() throws Exception {
		this.requestToken = twitter.getOAuthRequestToken();
	}

	String urlTwitterOautVerifier = null;

	public void setUrlTwitterOautVerifier(String urlTwitterOautVerifier) {
		this.urlTwitterOautVerifier = urlTwitterOautVerifier;
	}

	public void storeTwitterLoginStatus() throws Exception {
		if (urlTwitterOautVerifier != null && urlTwitterOautVerifier.startsWith(Constants.TWITTER_CALLBACK_URL)) {
			Uri uri = Uri.parse(urlTwitterOautVerifier);
			// The url, returned by Twitter login has oaut_verifier, which we
			// need to get access token.
			String verifier = uri.getQueryParameter(Constants.URL_TWITTER_OAUTH_VERIFIER);
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
			storeAccessToken(accessToken);
		} else {
			throw new IllegalStateException("urlTwitterOautVerifier=" + urlTwitterOautVerifier);
		}

	}

	void storeAccessToken(AccessToken accessToken) {
		Editor e = mSharedPreferences.edit();

		// After getting access token, access token secret
		// store them in application preferences
		e.putString(Constants.PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
		e.putString(Constants.PREF_KEY_OAUTH_SECRET, accessToken.getTokenSecret());
		// Store login status - true
		e.putBoolean(Constants.PREF_KEY_TWITTER_LOGIN, true);
		e.commit(); // save changes
	}

	public long getUserId() throws TwitterException {
		return twitter.getId();
	}

}
