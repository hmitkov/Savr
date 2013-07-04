package com.proxiad.savr.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.proxiad.savr.common.ActivityHelper;
import com.proxiad.savr.common.Constants;
import com.proxiad.savr.common.FileUtil;
import com.proxiad.savr.model.Save;

public class ServerHelper {
	public static String getSaveById(String id) throws Exception {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put(Constants.REQUEST_PARAMETER_ID, id);
		String jsonResponse = sendGetRequest(Constants.SERVER_PATH_GET_ARTICLE_BY_ID, params);
		return jsonResponse;
	}

	public static String sendCode(String code) throws Exception {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put(Constants.REQUEST_PARAMETER_CODE, code);
		String jsonResponse = sendGetRequest(Constants.SERVER_PATH_GET_ARTICLE, params);
		return jsonResponse;
	}

	public static String getUserToken(String id, String type) throws Exception {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_ID, id));
		nameValuePairs.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_TYPE, type));
		String jsonResponse = sendPostRequest(Constants.SERVER_PATH_CREE_USER_FB_TW, nameValuePairs);
		return jsonResponse;
	}

	public static String like(int id, String token) throws Exception {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_ID, String.valueOf(id)));
		nameValuePairs.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_TOKEN, token));
		String jsonResponse = sendPostRequest(Constants.SERVER_PATH_LIKES, nameValuePairs);
		return jsonResponse;
	}

	// public static String createUser(String userName, String password) throws
	// Exception {
	// List<NameValuePair> params = new ArrayList<NameValuePair>();
	// params.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_USERNAME,
	// userName));
	// params.add(new BasicNameValuePair(Constants.REQUEST_PARAMETER_PASSWORD,
	// password));
	// String jsonResponse = sendPostRequest(Constants.SERVER_PATH_GET_ARTICLE,
	// params);
	// return jsonResponse;
	// }

	private static String sendGetRequest(String path, Map<String, String> params) throws Exception {
		HttpResponse response = sendGetRequestToServer(path, params);
		String jsonResponse = getJsonFromResponse(response);
		return jsonResponse;
	}

	private static String sendPostRequest(String path, List<NameValuePair> nameValuePairs) throws Exception {
		HttpResponse response = sendPostRequestToServer(path, nameValuePairs);
		String jsonResponse = getJsonFromResponse(response);
		return jsonResponse;
	}

	private static String getJsonFromResponse(HttpResponse response) throws Exception {
		// Check if server response is valid
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != 200) {
			throw new IOException("Invalid response from server [" + status.toString() + "]");
		} else {

			// Pull content stream from response
			HttpEntity entity = response.getEntity();
			InputStream inputStream = entity.getContent();

			StringBuilder builder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line;

			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}

			// return result from buffered stream
			String dataAsString = builder.toString();
			return dataAsString;
		}
	}

	private static HttpResponse sendGetRequestToServer(String path, Map<String, String> params) throws Exception {
		HttpClient client = new DefaultHttpClient();
		String serverUrl = getServerURL(path);
		List<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();

		for (Map.Entry<String, String> param : params.entrySet()) {
			// get.getParams().setParameter(param.getKey(), param.getValue());
			nameValuePairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));

		}
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		serverUrl += "?" + paramString;
		HttpGet get = new HttpGet(serverUrl);
		HttpResponse response = client.execute(get);
		return response;
	}

	private static HttpResponse sendPostRequestToServer(String path, List<NameValuePair> nameValuePairs) throws Exception {
		HttpClient httpClient = new DefaultHttpClient();
		String serverUrl = getServerURL(path);
		HttpPost httpPost = new HttpPost(serverUrl);
		httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
		HttpResponse response = httpClient.execute(httpPost);
		return response;
	}

	private static String getServerURL(String path) {
		String serverHostname = Constants.SERVER_HOSTNAME;
		String url = serverHostname + path;
		return url;
	}

	public static void downloadSaveImage(Context context, Save save) throws Exception {
		File imageFile = FileUtil.getSaveImageFile(context, save);
		imageFile.createNewFile();
		downloadImage(Constants.SERVER_HOSTNAME + save.getPhoto(), imageFile);
	}

	public static File downloadImage(String urlStr, File emptyImageFile) throws Exception {
		URLConnection conn;
		InputStream is = null;
		FileOutputStream out = null;
		try {
			URL url = new URL(urlStr);
			/* Open a connection to that URL. */
			conn = url.openConnection();

			is = conn.getInputStream();
			out = new FileOutputStream(emptyImageFile);
			Bitmap bitmapImg = BitmapFactory.decodeStream(is);
			if (bitmapImg != null) {
				bitmapImg = ActivityHelper.getRoundedCornerBitmap(bitmapImg, Constants.ROUND_IMAGE_CORNERS);
			}
			bitmapImg.compress(Bitmap.CompressFormat.PNG, 90, out);
		} finally {
			if (is != null) {
				is.close();
			}
			if (out != null) {
				out.close();
			}
		}
		return emptyImageFile;
	}

	public static Bitmap downloadImage(String url) {
		Bitmap mIcon11 = null;
		try {
			InputStream in = new java.net.URL(url).openStream();
			mIcon11 = BitmapFactory.decodeStream(in);
		} catch (Exception e) {
			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return mIcon11;
	}

	public static Bitmap downloadSaveImageAndStore(Context context, String url, Save save) throws Exception {
		Bitmap image = downloadImage(url);
		saveImage(image, FileUtil.getSaveImageFile(context, save));
		return image;
	}

	public static void saveImage(Bitmap bmp, File file) throws Exception {
		file.createNewFile();
		FileOutputStream out = new FileOutputStream(file);
		bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
	}
}
