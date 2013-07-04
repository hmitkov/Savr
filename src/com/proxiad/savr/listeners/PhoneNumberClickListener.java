package com.proxiad.savr.listeners;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.proxiad.savr.R;

public class PhoneNumberClickListener implements OnClickListener {
	Context thisContext;
	String callNo;

	@Override
	public void onClick(View v) {
		try {
			thisContext = v.getContext();
			callNo = ((TextView) v).getText().toString().replaceAll(" ", "").trim();
			// .dismissProgressDialog();

			TextView textView = new TextView(v.getContext());
			textView.setGravity(Gravity.CENTER);
			textView.setText(thisContext.getResources().getString(R.string.dialog_call));
			textView.setPadding(10, 10, 10, 10);
			textView.setTextColor(Color.RED);
			textView.setTextSize(18);
			Typeface myriadPro = Typeface.createFromAsset(thisContext.getAssets(),
					"MuseoSlab-700.ttf");
			textView.setTypeface(myriadPro);

			AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
			dialog.setTitle("");
			dialog.setView(textView);

			dialog.setPositiveButton(thisContext.getResources().getString(R.string.dialog_yes),
					new android.content.DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							thisContext.startActivity(new Intent(
									Intent.ACTION_CALL, Uri.parse("tel:"
											+ callNo)));

						}
					});
			dialog.setNegativeButton(thisContext.getResources().getString(R.string.dialog_no),
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			dialog.show();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
