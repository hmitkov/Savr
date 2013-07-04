package com.proxiad.savr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.proxiad.savr.common.Constants;
import com.proxiad.savr.social.TwitterManager;

public class TwitterLoginActivity extends Activity {

	WebView wvTwitterLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_twitter_login);
		String url = TwitterManager.getInstance().getRequestToken().getAuthenticationURL();
		wvTwitterLogin = (WebView) findViewById(R.id.wvTwitterLogin);
		wvTwitterLogin.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.startsWith(Constants.TWITTER_CALLBACK_URL)) {
					TwitterManager.getInstance().setUrlTwitterOautVerifier(url);
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
					return true;
				}
				return false;
			}
		});
		wvTwitterLogin.loadUrl(url);
	}

}
