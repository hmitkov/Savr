package com.proxiad.savr.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class NetworkUtil {
	public static boolean isNetworkConnectionAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null)
			return false;
		State network = info.getState();
		return (network == NetworkInfo.State.CONNECTED);
	}
}
