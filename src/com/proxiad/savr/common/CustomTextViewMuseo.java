package com.proxiad.savr.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewMuseo extends TextView {

	public CustomTextViewMuseo(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomTextViewMuseo(Context context) {
		super(context);
		init();
	}

	public CustomTextViewMuseo(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		Typeface myriadPro = Typeface.createFromAsset(getContext().getAssets(),
				"MuseoSlab-700.ttf");
		setTypeface(myriadPro);
		
	}
	public void setDefaultColor(){
		//light_blackD101101101
		setTextColor(Color.parseColor("#656565"));
		getText();
	}
	public String getString(){
		return getText().toString();
	}
}