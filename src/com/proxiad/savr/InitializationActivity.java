package com.proxiad.savr;

import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.proxiad.savr.async.AsyncOperation;
import com.proxiad.savr.async.AsyncOperation.AsyncOperationCallback;
import com.proxiad.savr.async.SendCodeOperation;
import com.proxiad.savr.common.AlertDialogManager;
import com.proxiad.savr.common.NetworkUtil;
import com.proxiad.savr.model.Save;
import com.proxiad.savr.persistence.PersistenceManager;
import com.proxiad.savr.persistence.SerializationManager;

public class InitializationActivity extends Activity {

	LinearLayout llProgress;
	FrameLayout flSendCode;
	TextView tvMessages, tvSavedCodeNum;
	Button btnSendOrSave;
	EditText etCode;
	static AsyncOperation<?, ?, ?> mOperation = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialization);
		etCode = (EditText) findViewById(R.id.etCode);
		etCode.clearFocus();
		btnSendOrSave = (Button) findViewById(R.id.btnSendOrSave);
		btnSendOrSave.setBackgroundResource(R.drawable.send_btn_bg);
		btnSendOrSave.setTextColor(Color.WHITE);
		Typeface font = Typeface.createFromAsset(getAssets(), "MuseoSlab-700.ttf");
		btnSendOrSave.setTypeface(font);
		flSendCode = (FrameLayout) findViewById(R.id.flSendCode);
		llProgress = (LinearLayout) findViewById(R.id.llProgress);
		tvMessages = (TextView) findViewById(R.id.tvMessages);
		
		TextView initialMessage = (TextView) findViewById(R.id.InitialMessage);
		initialMessage.setText(R.string.label_code_tx);
		initialMessage.setTypeface(font);
		SpannableString text = new SpannableString(initialMessage.getText());  
		text.setSpan(new ForegroundColorSpan(Color.RED), 10, 19, 0);  
		initialMessage.setText(text, BufferType.SPANNABLE);
		tvSavedCodeNum = (TextView) findViewById(R.id.tvSavedCodesNum);
		if (mOperation != null) {
			SendCodeCallback callback = new SendCodeCallback();
			mOperation.setmCallback(callback);
			callback.onPreExecute();
		}

		btnSendOrSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String code = etCode.getText().toString();
				if (!checkCode()) {
					return;
				}

				List<Save> saveWithThisCode = PersistenceManager.getSaveByCode(InitializationActivity.this, code);
				if (saveWithThisCode != null && saveWithThisCode.size() != 0) {
					HomeActivity homeActivity = ((HomeActivity) InitializationActivity.this.getParent());
					homeActivity.setSave(saveWithThisCode.get(0));
					homeActivity.refresh();
				} else {
					if (NetworkUtil.isNetworkConnectionAvailable(v.getContext())) {
						if (mOperation == null) {
							SendCodeCallback callback = new SendCodeCallback();
							mOperation = new SendCodeOperation(v.getContext(), callback, code);
							mOperation.doLongOperation();
						} else {
							tvMessages.setTextColor(Color.RED);
							tvMessages.setVisibility(View.VISIBLE);
							tvMessages.setText("Error! In progress");
						}
					} else {
						SerializationManager.saveCode(v.getContext(), code);
						new AlertDialogManager().showAlertDialog(InitializationActivity.this, getString(R.string.msg_no_connection), getString(R.string.msg_code_saved), true);
						initSavedCodeNum();

					}
				}
			}
		});
		initSavedCodeNum();
	}

	private boolean checkCode() {
		String code = etCode.getText().toString();
		if (code == null || code.length() != 6) {
			// etCode.setError(ActivityHelper.getErrorMessage(v.getContext(),
			// R.string.error_code_length));
			tvMessages.setVisibility(View.VISIBLE);
			tvMessages.setText(R.string.error_code_length);
			etCode.requestFocus();
			return false;
		}
		return true;
	}

	private void initSavedCodeNum() {
		Set<String> savedCodeLilst = SerializationManager.getSavedCodess(this);
		TextView initialMessage = (TextView) findViewById(R.id.InitialMessage);
		if (savedCodeLilst == null || savedCodeLilst.size() == 0) {
			tvSavedCodeNum.setVisibility(View.GONE);
			initialMessage.setVisibility(View.VISIBLE);
		} else {
			
			initialMessage.setVisibility(View.GONE);
			tvSavedCodeNum.setVisibility(View.VISIBLE);
			// tvSavedCodeNum.setText(Html.fromHtml(getString(R.string.msg_saved_codes,
			// savedCodeLilst.size())));
			tvSavedCodeNum.setText(Html.fromHtml("There are " + savedCodeLilst.size() + " pending codes. Connect to internet and click <font color='blue'><b><u>here</u></b></font> to send them."));
			tvSavedCodeNum.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mOperation == null) {
						SendCodeCallback callback = new SendCodeCallback();
						mOperation = new SendCodeOperation(v.getContext(), callback, null);
						mOperation.doLongOperationWithCheck();
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_initialization, menu);
		return true;
	}

	class SendCodeCallback implements AsyncOperationCallback {

		public SendCodeCallback() {
			super();

		}

		@Override
		public void onPreExecute() {
			tvMessages.setVisibility(View.GONE);
			flSendCode.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.GONE);
			llProgress.setVisibility(View.VISIBLE);

		}

		@Override
		public void onPostExecute() {
			mOperation = null;
		}

		@Override
		public void onProgress(int progress) {
		}

		@Override
		public void onCancel() {
			mOperation = null;
		}

		@Override
		public void onError(String msg) {
			// tvMessages.setVisibility(View.VISIBLE);
			// tvMessages.setText(msg);
			new AlertDialogManager().showAlertDialog(InitializationActivity.this, getString(R.string.msg_error), msg, false);
			flSendCode.setVisibility(View.VISIBLE);
			llProgress.setVisibility(View.GONE);
			btnSendOrSave.setVisibility(View.VISIBLE);
			initSavedCodeNum();
			mOperation = null;
		}
	}

}
