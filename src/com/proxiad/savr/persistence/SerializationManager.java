package com.proxiad.savr.persistence;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.SerializationUtil;

public class SerializationManager {
	public static Set<String> getSavedCodess(Context context) {
		Set<String> savedCodes = null;
		try {
			savedCodes = (Set<String>) SerializationUtil.deserialize(context, Constants.CODE_FILE_NAME);
		} catch (Exception e) {
			savedCodes = new HashSet<String>();
		}
		return savedCodes;
	}

	public static void saveCode(Context context, String code) {
		Set<String> savedCodes = getSavedCodess(context);
		savedCodes.add(code);
		try {
			SerializationUtil.serialize(context, Constants.CODE_FILE_NAME, savedCodes);
		} catch (Exception e) {
			Log.e(Constants.TAG_LOG + "PersistenceManager.saveLocations", e.getMessage());
		}
	}

	public static void deleteCodes(Context context) {
		try {
			SerializationUtil.empty(context, Constants.CODE_FILE_NAME);
		} catch (Exception e) {
			Log.e(Constants.TAG_LOG + "SerializationManager.empty", e.getMessage());
		}
	}
}
