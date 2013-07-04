package com.proxiad.savr.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.proxiad.savr.common.Constants;

public class Application extends android.app.Application {
	private static Application instance;
	long twitterId = 0;
	String facebookId = "";
	String tokenTwitter = "";
	String tokenFacebook = "";

	public Application() {
		super();
	};

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		SharedPreferences sharedPreferences = getPref();
		twitterId = sharedPreferences.getLong(Constants.PREF_KEY_TWITTER_ID, 0);
		facebookId = sharedPreferences.getString(Constants.PREF_KEY_FACEBOOK_ID, "");
		tokenTwitter = sharedPreferences.getString(Constants.TOKEN_TWITTER, "");
		tokenFacebook = sharedPreferences.getString(Constants.TOKEN_FACEBOOK, "");
	}

	public static Application getInstance() {
		return instance;
	}

	public long getTwitterId() {
		return twitterId;
	}

	public boolean setTwitterIdIfNecessary(long twitterId) {
		if (this.twitterId != twitterId) {
			this.twitterId = twitterId;
			SharedPreferences settings = getPref();
			Editor editor = settings.edit();
			editor.putLong(Constants.PREF_KEY_TWITTER_ID, twitterId);
			editor.apply();
			return true;
		}
		return false;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public boolean setFacebookIdIfNecessary(String facebookId) {
		if (!this.facebookId.equals(facebookId)) {
			this.facebookId = facebookId;
			SharedPreferences settings = getPref();
			Editor editor = settings.edit();
			editor.putString(Constants.PREF_KEY_FACEBOOK_ID, facebookId);
			editor.apply();
			return true;
		}
		return false;
	}

	private SharedPreferences getPref() {
		return getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
	}

	public String getTokenTwitter() {
		return tokenTwitter;
	}

	public void setTokenTwitter(String tokenTwitter) {
		this.tokenTwitter = tokenTwitter;
		SharedPreferences settings = getPref();
		Editor editor = settings.edit();
		editor.putString(Constants.PREF_KEY_TWITTER_TOKEN, tokenTwitter);
		editor.apply();
	}

	public String getTokenFacebook() {
		return tokenFacebook;
	}

	public void setTokenFacebook(String tokenFacebook) {
		this.tokenFacebook = tokenFacebook;
		SharedPreferences settings = getPref();
		Editor editor = settings.edit();
		editor.putString(Constants.PREF_KEY_FACEBOOK_TOKEN, tokenFacebook);
		editor.apply();
	}

}
