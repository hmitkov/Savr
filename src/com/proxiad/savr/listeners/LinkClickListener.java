package com.proxiad.savr.listeners;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
public class LinkClickListener implements OnClickListener{
	@Override
	public void onClick(View v) {
		Uri uri = Uri.parse(((TextView) v).getText().toString());
		v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, uri));
		
	}
}
