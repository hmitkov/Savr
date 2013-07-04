package com.proxiad.savr.async;

import com.proxiad.savr.async.AsyncOperation.AsyncOperationCallback;

public class EmptyAsyncCallback implements AsyncOperationCallback {
	public void onPreExecute() {
	};

	public void onPostExecute() {
	};

	public void onProgress(int progress) {
	};

	public void onCancel() {
	};

	public void onError(String msg) {
	};
}
