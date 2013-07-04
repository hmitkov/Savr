package com.proxiad.savr.jsno;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.proxiad.savr.model.Save;

public class MyJsonParser {

	public static Save saveFromJson(String json) throws JSONException {
		
		Save save = new Gson().fromJson(json, Save.class);
		if (save.getCategorie() == null) {
			// {"erreur":"Article Introuvable","code":"231049"}
			JsonObject errObj = new JsonParser().parse(json).getAsJsonObject();
			String err = errObj.get("erreur").getAsString();
			String code = errObj.get("code").getAsString();
			throw new JSONException(err + ". code: " + code);
		}
		return save;
	}

	public static List<String[]> getFBSaveIdAndDateList(JSONArray arr) throws Exception {
		List<String[]> saveIdAndDateList = new ArrayList<String[]>();
		for (int i = 0; i < arr.length(); i++) {
			String[] saveIdAndDate = new String[3];
			JSONObject appAndArticle = arr.getJSONObject(i);
			String date = appAndArticle.getString("publish_time");
			JSONObject data = appAndArticle.getJSONObject("data");
			JSONObject article = data.getJSONObject("article");
			String url = article.getString("url");
			String id = (url.substring(url.length() - 3, url.length()));
			JSONObject from = appAndArticle.getJSONObject("from");
			String publisher = from.getString("name");
			saveIdAndDate[0] = id;
			saveIdAndDate[1] = date;
			saveIdAndDate[2] = publisher;
			saveIdAndDateList.add(saveIdAndDate);
		}
		return saveIdAndDateList;
	}

	public static String readToken(String json) {
		JsonObject obj = new JsonParser().parse(json).getAsJsonObject();
		return obj.get("token").getAsString();
	}
}
